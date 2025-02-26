package fr.anto42.emma.utils.saves;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import fr.anto42.emma.game.impl.config.UHCConfig;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Type;

public class SaveSerializationManager {

    private final Gson gson;

    public SaveSerializationManager() {
        GsonBuilder gsonBuilder = new GsonBuilder();

        gsonBuilder.registerTypeAdapter(ItemStack.class, new ItemStackAdapter());

        // Ajout d'un ExclusionStrategy pour ignorer les conflits de champs
        gsonBuilder.addSerializationExclusionStrategy(new ExclusionStrategy() {
            @Override
            public boolean shouldSkipField(FieldAttributes f) {
                // Ignore les champs conflictuels, ici par exemple "c"
                return "c".equals(f.getName());
            }

            @Override
            public boolean shouldSkipClass(Class<?> clazz) {
                // Ne pas ignorer les classes en général
                return false;
            }
        });

        gson = gsonBuilder.setPrettyPrinting()
                .serializeNulls()
                .disableHtmlEscaping()
                .create();
    }

    public String serialize(UHCConfig uhcConfig) {
        return this.gson.toJson(uhcConfig);
    }

    public UHCConfig deserialize(String json) {
        return this.gson.fromJson(json, UHCConfig.class);
    }

    private static class ItemStackAdapter implements JsonSerializer<ItemStack>, JsonDeserializer<ItemStack> {

        @Override
        public JsonElement serialize(ItemStack item, Type type, JsonSerializationContext context) {
            return new JsonPrimitive(serializeItemStack(item));
        }

        @Override
        public ItemStack deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
            return deserializeItemStack(json.getAsString());
        }

        private String serializeItemStack(ItemStack item) {
            if (item == null || item.getType() == Material.AIR) {
                return ""; // Return empty string for empty or invalid ItemStack
            }
            return item.getType().toString() + ":" + item.getAmount(); // Format: MATERIAL:amount
        }

        private ItemStack deserializeItemStack(String serializedItem) {
            if (serializedItem == null || serializedItem.isEmpty()) {
                return new ItemStack(Material.AIR); // Return an empty ItemStack if the string is empty
            }
            String[] parts = serializedItem.split(":");
            if (parts.length == 2) {
                try {
                    Material material = Material.valueOf(parts[0]);
                    int amount = Integer.parseInt(parts[1]);
                    return new ItemStack(material, amount);
                } catch (IllegalArgumentException e) {
                    throw new JsonParseException("Erreur lors de la désérialisation de l'ItemStack", e);
                }
            } else {
                throw new JsonParseException("Format invalide pour l'ItemStack : " + serializedItem);
            }
        }
    }
}
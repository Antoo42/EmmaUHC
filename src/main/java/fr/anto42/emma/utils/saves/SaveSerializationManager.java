package fr.anto42.emma.utils.saves;

import com.google.gson.*;
import fr.anto42.emma.coreManager.players.PlayerStats;
import fr.anto42.emma.game.impl.config.UHCConfig;
import fr.anto42.emma.utils.chat.ChatSave;
import fr.anto42.emma.utils.gameSaves.Event;
import fr.anto42.emma.utils.gameSaves.EventType;
import fr.anto42.emma.utils.gameSaves.GameSave;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Type;

public class SaveSerializationManager {

    private final Gson gson;

    public SaveSerializationManager() {
        GsonBuilder gsonBuilder = new GsonBuilder();

        gsonBuilder.registerTypeAdapter(ItemStack.class, new ItemStackAdapter());

        gsonBuilder.addSerializationExclusionStrategy(new ExclusionStrategy() {
            @Override
            public boolean shouldSkipField(FieldAttributes f) {
                return "c".equals(f.getName());
            }

            @Override
            public boolean shouldSkipClass(Class<?> clazz) {
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

    public String serialize(GameSave gameSave) {
        return this.gson.toJson(gameSave);
    }

    public UHCConfig deserialize(String json) {
        return this.gson.fromJson(json, UHCConfig.class);
    }

    public GameSave deserializeGame(String json) {
        return this.gson.fromJson(json, GameSave.class);
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

    public PlayerStats fromString(String statsString) {
        String[] parts = getStrings(statsString);

        String name = parts[0].split("=")[1];
        int kills = Integer.parseInt(parts[1].split("=")[1]);
        int deaths = Integer.parseInt(parts[2].split("=")[1]);
        int diamondsMined = Integer.parseInt(parts[3].split("=")[1]);
        int goldMined = Integer.parseInt(parts[4].split("=")[1]);
        int ironMined = Integer.parseInt(parts[5].split("=")[1]);
        String team = parts[6].split("=")[1];
        String role = parts[7].split("=")[1];
        boolean isAlive = Boolean.parseBoolean(parts[8].split("=")[1]);
        boolean hasWon = Boolean.parseBoolean(parts[9].split("=")[1]);
        double makedDamages = Double.parseDouble(parts[10].split("=")[1]);
        double recceivedDamages = Double.parseDouble(parts[11].split("=")[1]);


        return new PlayerStats(name, kills, deaths, diamondsMined, goldMined, ironMined, team, role, isAlive, hasWon, makedDamages, recceivedDamages);
    }

    private static String[] getStrings(String statsString) {
        if (!statsString.startsWith("uhcPlayerData=")) {
            throw new IllegalArgumentException("La chaîne de statistiques ne commence pas par 'uhcPlayerData='");
        }

        String data = statsString.substring("uhcPlayerData=".length());

        String[] parts = data.split("\\|");

        if (parts.length != 12) {
            throw new IllegalArgumentException("Le format de la chaîne de statistiques est invalide");
        }
        return parts;
    }

    private static String[] getEventStrings(String statsString) {
        if (!statsString.startsWith("event=")) {
            throw new IllegalArgumentException("La chaîne d'event ne commence pas par 'event='");
        }

        String data = statsString.substring("event=".length());

        String[] parts = data.split("\\|");

        if (parts.length != 4) {
            throw new IllegalArgumentException("Le format de la chaîne d'event est invalide");
        }
        return parts;
    }
    public static Event fromEventString(String statsString) {
        String[] parts = getEventStrings(statsString);

        EventType type = EventType.fromString(parts[0].split("=")[1]);
        String string = parts[1].split("=")[1];
        String date = parts[2].split("=")[1];
        String timer = parts[3].split("=")[1];



        return new Event(type, string, date, timer);
    }


    private static String[] getChatString(String chatString) {
        if (!chatString.startsWith("chat=")) {
            throw new IllegalArgumentException("La chaîne de chat ne commence pas par 'chat='");
        }

        String data = chatString.substring("chat=".length());

        String[] parts = data.split("\\|");

        if (parts.length != 5) {
            throw new IllegalArgumentException("Le format de la chaîne d'event est invalide");
        }
        return parts;
    }

    public static ChatSave fromChatString(String chatString) {
        String[] parts = getChatString(chatString);

        String sender = parts[0].split("=")[1];
        String string = parts[1].split("=")[1];
        double scoreAI = Double.parseDouble(parts[2].split("=")[1]);
        String date = parts[3].split("=")[1];
        String timer = parts[4].split("=")[1];



        return new ChatSave(sender, scoreAI, string, date, timer);
    }
}
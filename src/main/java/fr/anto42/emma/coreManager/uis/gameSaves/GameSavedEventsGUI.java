package fr.anto42.emma.coreManager.uis.gameSaves;

import fr.anto42.emma.UHC;
import fr.anto42.emma.utils.gameSaves.Event;
import fr.anto42.emma.utils.gameSaves.EventType;
import fr.anto42.emma.utils.gameSaves.GameSave;
import fr.anto42.emma.utils.materials.ItemCreator;
import fr.anto42.emma.utils.saves.SaveSerializationManager;
import fr.anto42.emma.utils.skulls.SkullList;
import fr.blendman974.kinventory.inventories.KInventory;
import fr.blendman974.kinventory.inventories.KItem;
import org.bukkit.Material;

import java.util.List;
import java.util.stream.Collectors;

public class GameSavedEventsGUI {
    private final KInventory kInventory;
    private static final int maxPerPage = 27;
    private final EventType filterType;

    public GameSavedEventsGUI(GameSave gameSave, KInventory previous, int page, EventType filterType) {
        this.kInventory = new KInventory(54, UHC.getInstance().getPrefix() + " §7Game: " + gameSave.getGameID());
        this.filterType = filterType;

        KItem glass = new KItem(new ItemCreator(Material.STAINED_GLASS_PANE, 1, (byte) 14).get());
        for (int i = 0; i < 9; i++) {
            this.kInventory.setElement(i, glass);
            this.kInventory.setElement(45 + i, glass);
        }
        for (int i = 36; i < 45; i++) {
            this.kInventory.setElement(i, glass);
        }

        KItem back = new KItem(new ItemCreator(SkullList.LEFT_AROOW.getItemStack())
                .name("§8┃ §cRevenir en arrière")
                .lore("", "§8┃ §cRetour au menu précédent", "", "§8§l» §6Cliquez §fpour revenir.")
                .get());
        back.addCallback((kInventoryRepresentation, itemStack, player, kInventoryClickContext) -> {
            if (previous != null) {
                previous.open(player);
            } else {
                player.closeInventory();
            }
        });
        this.kInventory.setElement(49, back);

        List<Event> filteredEvents = gameSave.getEvents().stream()
                .map(SaveSerializationManager::fromEventString)
                .filter(event -> filterType == null || event.getEventType() == filterType)
                .collect(Collectors.toList());

        int totalPages = (filteredEvents.size() + maxPerPage - 1) / maxPerPage;
        int start = page * maxPerPage;
        int end = Math.min(start + maxPerPage, filteredEvents.size());

        int slot = 9;
        for (int i = start; i < end; i++) {
            Event event = filteredEvents.get(i);
            KItem kItem = new KItem(new ItemCreator(Material.PAPER)
                    .name("§8§l» §7" + event.getString())
                    .lore("", "§8§l» §7Type: §c" + event.getEventType().getString(), "§8§l» §7Timer de jeu: §e" + event.getTimer(), "§8§l» §7Date: §3" + event.getDate())
                    .get());

            this.kInventory.setElement(slot++, kItem);
        }

        if (page > 0) {
            KItem prevPage = new KItem(new ItemCreator(Material.ARROW).name("§8┃ §6Page précédente").get());
            prevPage.addCallback((kInventoryRepresentation, itemStack, player1, kInventoryClickContext) ->
                    new GameSavedEventsGUI(gameSave, previous, page - 1, filterType).getkInventory().open(player1)
            );
            this.kInventory.setElement(3, prevPage);
        }

        if (page < totalPages - 1) {
            KItem nextPage = new KItem(new ItemCreator(Material.ARROW).name("§8┃ §6Page suivante").get());
            nextPage.addCallback((kInventoryRepresentation, itemStack, player1, kInventoryClickContext) ->
                    new GameSavedEventsGUI(gameSave, previous, page + 1, filterType).getkInventory().open(player1)
            );
            this.kInventory.setElement(5, nextPage);
        }

        KItem filterItem = new KItem(new ItemCreator(Material.SIGN)
                .name("§8┃ §fFiltre d'événements")
                .lore("", "§8┃ §fActuel: " + (filterType == null ? "§aTous" : "§c" + filterType.getString()), "",
                        "§8§l» §6Cliquez §fpour changer.")
                .get());

        filterItem.addCallback((kInventoryRepresentation, itemStack, player, kInventoryClickContext) -> {
            EventType nextFilter = getNextFilter(filterType);
            new GameSavedEventsGUI(gameSave, previous, 0, nextFilter).getkInventory().open(player);
        });

        this.kInventory.setElement(4, filterItem);
    }

    public KInventory getkInventory() {
        return kInventory;
    }


    private EventType getNextFilter(EventType current) {
        EventType[] types = EventType.values();
        if (current == null) return types[0];
        int index = (current.ordinal() + 1) % types.length;
        return types[index];
    }
}

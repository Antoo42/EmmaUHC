package fr.anto42.emma.coreManager.uis.gameSaves;

import fr.anto42.emma.UHC;
import fr.anto42.emma.utils.chat.ChatSave;
import fr.anto42.emma.utils.chat.MessageChecker;
import fr.anto42.emma.utils.gameSaves.GameSave;
import fr.anto42.emma.utils.materials.ItemCreator;
import fr.anto42.emma.utils.saves.SaveSerializationManager;
import fr.anto42.emma.utils.skulls.SkullList;
import fr.blendman974.kinventory.inventories.KInventory;
import fr.blendman974.kinventory.inventories.KItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

public class MessagesSentByPlayerGUI {
    private final KInventory kInventory;
    private final KInventory previous;
    private final GameSave gameSave;
    private final List<String> chatList;
    private int currentPage = 0;
    private static final int ITEMS_PER_PAGE = 27;

    public MessagesSentByPlayerGUI(String playerName, KInventory previous, GameSave gameSave) {
        this.kInventory = new KInventory(54, UHC.getInstance().getPrefix() + " §7Messages de " + playerName);
        this.previous = previous;
        this.gameSave = gameSave;
        KItem glass = new KItem(new ItemCreator(Material.STAINED_GLASS_PANE, 1, (byte) 14).get());
        for (int i = 0; i < 9; i++) {
            this.kInventory.setElement(i, glass);
            if (45 + i != 49)
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

        this.chatList = new ArrayList<>();
        gameSave.getChat().stream()
                .filter(s -> s.contains("sender=" + playerName))
                .forEach(this.chatList::add);

        setupPaginationButtons();

        loadPage(0);
    }

    private void loadPage(int page) {
        this.kInventory.clear();
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

        KItem glass = new KItem(new ItemCreator(Material.STAINED_GLASS_PANE, 1, (byte) 14).get());
        for (int i = 0; i < 9; i++) {
            this.kInventory.setElement(i, glass);
            if (45 + i != 49)
                this.kInventory.setElement(45 + i, glass);
        }
        for (int i = 36; i < 45; i++) {
            this.kInventory.setElement(i, glass);
        }

        int start = page * ITEMS_PER_PAGE;
        int end = Math.min(start + ITEMS_PER_PAGE, chatList.size());
        int slot = 9;

        for (int i = start; i < end; i++) {
            ChatSave chatSave = SaveSerializationManager.fromChatString(chatList.get(i));
            KItem kItem = new KItem(new ItemCreator(Material.PAPER)
                    .name(translateDelete(chatSave.getScoreAI()) + chatSave.getChat())
                    .lore("", "§8§l» §7ScoreAI: " + colorScore(chatSave.getScoreAI()) + chatSave.getScoreAI() * 100 + "%",
                            "§8§l» §7Timer: §e" + chatSave.getTimer(),
                            "§8§l» §7Date: §3" + chatSave.getDate())
                    .get());
            this.kInventory.setElement(slot, kItem);
            slot++;
        }

        setupPaginationButtons();
    }

    private void setupPaginationButtons() {
        if (currentPage > 0) {
            KItem previousPage = new KItem(new ItemCreator(SkullList.LEFT_AROOW.getItemStack())
                    .name("§8┃ §cPage précédente")
                    .lore("", "§8§l» §6Cliquez §fpour aller à la page précédente.")
                    .get());
            previousPage.addCallback((kInventoryRepresentation, itemStack, player, kInventoryClickContext) -> {
                if (currentPage > 0) {
                    currentPage--;
                    loadPage(currentPage);
                    this.kInventory.open(player);
                }
            });
            this.kInventory.setElement(2, previousPage);
        }

        if ((currentPage + 1) * ITEMS_PER_PAGE < chatList.size()) {
            KItem nextPage = new KItem(new ItemCreator(SkullList.RIGHT_ARROW.getItemStack())
                    .name("§8┃ §aPage suivante")
                    .lore("", "§8§l» §6Cliquez §fpour aller à la page suivante.")
                    .get());
            nextPage.addCallback((kInventoryRepresentation, itemStack, player, kInventoryClickContext) -> {
                if ((currentPage + 1) * ITEMS_PER_PAGE < chatList.size()) {
                    currentPage++;
                    loadPage(currentPage);
                    this.kInventory.open(player);
                }
            });
            this.kInventory.setElement(6, nextPage);
        }
    }

    private ChatColor colorScore(double score) {
        if (score <= 50) return ChatColor.GREEN;
        else if (score <= 70) return ChatColor.YELLOW;
        else if (score <= 85) return ChatColor.GOLD;
        else return ChatColor.RED;
    }

    private String translateDelete(double score) {
        if (MessageChecker.scoreAILimit <= score) return "§c§lDELETE: §c";
        else return "§f";
    }

    public KInventory getkInventory() {
        return kInventory;
    }
}


package fr.anto42.emma.coreManager.uis.config;

import fr.anto42.emma.UHC;
import fr.anto42.emma.coreManager.UHCManager;
import fr.anto42.emma.game.GameState;
import fr.anto42.emma.utils.materials.ItemCreator;
import fr.anto42.emma.utils.players.PlayersUtils;
import fr.anto42.emma.utils.skulls.SkullList;
import fr.blendman974.kinventory.inventories.KInventory;
import fr.blendman974.kinventory.inventories.KItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.*;

public class GameModeGUI {
    private final KInventory kInventory;
    private final UHCManager uhcManager = UHC.getInstance().getUhcManager();
    private final int itemsPerPage = 27;
    private int currentPage = 0;
    private final List<KItem> gamemodeItems = new ArrayList<>();

    public GameModeGUI() {
        this.kInventory = new KInventory(54, UHC.getInstance().getPrefix() + " §6§lModes de jeu");

        setupBorder();
        setupGamemodes();
        setupNavigation();
        loadPage(0);
    }

    private void setupBorder() {
        KItem glass = new KItem(new ItemCreator(Material.STAINED_GLASS_PANE, 1).get());
        for (int i = 0; i < 9; i++) {
            this.kInventory.setElement(i, glass);
            this.kInventory.setElement(45 + i, glass);
        }
        for (int i = 36; i < 45; i++) {
            this.kInventory.setElement(i, glass);
        }
    }

    private void setupGamemodes() {
        uhcManager.getModuleList().forEach(module -> {
            List<String> strings = new ArrayList<>();
            strings.add("§8" + module.getVersion());
            strings.add("");
            strings.addAll(module.getDesc().isEmpty() ? Arrays.asList("§8┃ §cAucune information.") : module.getDesc());
            strings.add("");
            strings.add("§8§l» §fStatut: " + (module == uhcManager.getGamemode() ? "§aactivé" : "§cdésactivé"));
            strings.add("");
            if (module.getDev() != null) {
                strings.add("§8┃ §fDéveloppeur: §b" + module.getDev());
                strings.add("");
            }
            strings.add("§8§l» §6Clique-gauche §fpour sélectionner.");
            if (module.isConfigurable()) {
                strings.add("§8§l» §6Clique-droit §fpour §bconfigurer§f.");
            }

            KItem kItem = new KItem(new ItemCreator(module.getItemStack()).name("§8┃ §6" + (module.isAvailable() ? module.getName() : " §c§kxxxxxxxxxxxxxxx")).lore(strings).get());
            kItem.addCallback((kInventoryRepresentation, itemStack, player, kInventoryClickContext) -> {
                if (!Objects.equals(player.getName(), "Anto42_") && !module.isAvailable()) return;
                if (!UHC.getInstance().getUhcGame().getGameState().equals(GameState.WAITING)) return;

                if (kInventoryClickContext.getClickType().isLeftClick()) {
                    if (!Objects.equals(module.getName(), uhcManager.getGamemode().getName())) {
                        uhcManager.getGamemode().onUnLoad();
                        uhcManager.setGamemode(module);
                        uhcManager.getGamemode().onLoad();
                        Bukkit.broadcastMessage(UHC.getInstance().getPrefix() + " §7Le nouveau mode de jeu sélectionné par l'Host est " + module.getName() + "§7 !");
                        for (Player player1 : Bukkit.getOnlinePlayers()) {
                            PlayersUtils.giveWaitingStuff(player1);
                        }
                        new GameModeGUI().getkInventory().open(player);
                    }
                } else if (kInventoryClickContext.getClickType().isRightClick() && module.isConfigurable()) {
                    module.getkInventory().open(player);
                }
            });

            gamemodeItems.add(kItem);
        });
    }


    private void setupNavigation() {
        KItem previousPage = new KItem(new ItemCreator(Material.ARROW).name("§8┃ §cPage précédente").get());
        previousPage.addCallback((kInventoryRepresentation, itemStack, player, kInventoryClickContext) -> {
            if (currentPage > 0) {
                loadPage(currentPage - 1);
            }
        });
        if (currentPage > 0) this.kInventory.setElement(2, previousPage);

        KItem nextPage = new KItem(new ItemCreator(Material.ARROW).name("§8┃ §aPage suivante").get());
        nextPage.addCallback((kInventoryRepresentation, itemStack, player, kInventoryClickContext) -> {
            if ((currentPage + 1) * itemsPerPage < gamemodeItems.size()) {
                loadPage(currentPage + 1);
            }
        });
        if ( uhcManager.getModuleList().size() > 27) this.kInventory.setElement(6, nextPage);

        KItem back = new KItem(new ItemCreator(SkullList.LEFT_AROOW.getItemStack()).name("§8┃ §cRevenir en arrière").lore("", "§8┃ §cVous ne trouvez pas §fce que vous souhaitez ?", "§8┃ §aPas de soucis§f, revenez en arrière !", "", "§8§l» §6Cliquez §fpour ouvrir.").get());
        back.addCallback((kInventoryRepresentation, itemStack, player, kInventoryClickContext) -> {
            UHC.getInstance().getUhcManager().getConfigMainGUI().open(player);
        });
        this.kInventory.setElement(49, back);
    }

    private void loadPage(int page) {
        currentPage = page;
        int start = page * itemsPerPage;
        int end = Math.min(start + itemsPerPage, gamemodeItems.size());

        for (int i = 9; i < 36; i++) {
            this.kInventory.removeElement(i);
        }

        for (int i = start, slot = 9; i < end; i++, slot++) {
            this.kInventory.setElement(slot, gamemodeItems.get(i));
        }
    }

    public KInventory getkInventory() {
        return kInventory;
    }
}

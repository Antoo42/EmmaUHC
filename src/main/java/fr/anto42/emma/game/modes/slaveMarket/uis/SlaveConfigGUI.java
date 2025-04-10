package fr.anto42.emma.game.modes.slaveMarket.uis;

import fr.anto42.emma.UHC;
import fr.anto42.emma.coreManager.players.UHCPlayer;
import fr.anto42.emma.coreManager.uis.config.GameModeGUI;
import fr.anto42.emma.game.modes.slaveMarket.impl.AuctionSTATE;
import fr.anto42.emma.game.modes.slaveMarket.SlaveModule;
import fr.anto42.emma.utils.materials.ItemCreator;
import fr.anto42.emma.utils.players.PlayersUtils;
import fr.anto42.emma.utils.skulls.SkullList;
import fr.blendman974.kinventory.inventories.KInventory;
import fr.blendman974.kinventory.inventories.KItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;

public class SlaveConfigGUI {
    private final KInventory kInventory;
    private final SlaveModule module;

    public SlaveConfigGUI(SlaveModule module){
        this.module = module;
        this.kInventory = new KInventory(54, UHC.getInstance().getPrefix() + " §6§lSlaveMarket");
        Bukkit.getScheduler().runTaskTimer(UHC.getInstance(), () -> {
            updateInventoryForPage(page);
        }, 0L, 5L);
    }

    int page = 1;
    private void updateInventoryForPage(int page) {
        int itemsPerPage = 27;
        int totalPages = (int) Math.ceil((double) UHC.getInstance().getUhcGame().getUhcData().getUhcPlayerList().size() / itemsPerPage);

        this.kInventory.clear();

        for (int i = 0; i < 9; i++) {
            KItem glass = new KItem(new ItemCreator(Material.STAINED_GLASS_PANE, 1, (byte) 1).get());
            this.kInventory.setElement(i, glass);
            this.kInventory.setElement(45 + i, glass);
        }
        for (int i = 36; i < 45; i++) {
            KItem glass = new KItem(new ItemCreator(Material.STAINED_GLASS_PANE, 1, (byte) 1).get());
            this.kInventory.setElement(i, glass);
        }

        KItem back = new KItem(new ItemCreator(SkullList.LEFT_AROOW.getItemStack()).name("§8┃ §cRevenir en arrière").lore("", "§8┃ §cVous ne trouvez pas §fce que vous souhaitez ?", "§8┃ §aPas de soucis§f, revenez en arrière !", "", "§8§l» §6Cliquez §fpour ouvrir.").get());
        back.addCallback((kInventoryRepresentation, itemStack, player, kInventoryClickContext) -> new GameModeGUI().getkInventory().open(player));
        this.kInventory.setElement(49, back);

        KItem startAuctions = new KItem(new ItemCreator(Material.LEASH).name("§8┃ §fDébuter les enchères").lore("", "§8┃ §fPermet de débuter les enchères des joueurs", "", "§8§l» §6Cliquez §fpour débuter.").get());
        startAuctions.addCallback((kInventory1, item, player, clickContext) -> {
            if (module.getAuctionSTATE() != AuctionSTATE.WAITING)
                return;
            module.startAuctions();
        });
        this.kInventory.setElement(0, startAuctions);

        KItem diamonds = new KItem(new ItemCreator(Material.DIAMOND).name("§8┃ §fDiamants de départ").lore("", "§8§l» §fStatut: §c" + module.getSlaveConfig().getStartdiamond() + " diamants", "", "§8┃ §fConfigurez le nombre de diamants que", "§8┃ §fchaque capitaine dispose pour débuter ses enchères", "§8§l» §6Clique-gauche §fpour ajouter 1.", "§8§l» §6Clique-droit §fpour retirer 1.").get());
        diamonds.addCallback((kInventory1, item, player, clickContext) -> {
            if (clickContext.getClickType().isLeftClick()) {
                if (module.getSlaveConfig().getStartdiamond() >= 200)
                    return;
                module.getSlaveConfig().setStartdiamond(module.getSlaveConfig().getStartdiamond() + 1);
            }
            if (clickContext.getClickType().isRightClick()) {
                if (module.getSlaveConfig().getStartdiamond() <= 1)
                    return;
                module.getSlaveConfig().setStartdiamond(module.getSlaveConfig().getStartdiamond() - 1);
            }
            diamonds.setItem(new ItemCreator(Material.DIAMOND).name("§8┃ §fDiamants de départ").lore("", "§8§l» §fStatut: §c" + module.getSlaveConfig().getStartdiamond() + " diamants", "", "§8┃ §fConfigurez le nombre de diamants que", "§8┃ §fchaque capitaine dispose pour débuter ses enchères", "§8§l» §6Clique-gauche §fpour ajouter 1.", "§8§l» §6Clique-droit §fpour retirer 1.").get());
        });
        this.kInventory.setElement(3, diamonds);

        KItem timer = new KItem(new ItemCreator(SkullList.TIMER.getItemStack()).name("§8┃ §fTemps d'une vente").lore("", "§8§l» §fStatut: §c" + module.getSlaveConfig().getAuctionDuration() + " secondes", "", "§8┃ §fConfigurez le temps de chaque vente", "§8§l» §6Clique-gauche §fpour ajouter 1.", "§8§l» §6Clique-droit §fpour retirer 1.").get());
        timer.addCallback((kInventory1, item, player, clickContext) -> {
            if (clickContext.getClickType().isLeftClick()) {
                if (module.getSlaveConfig().getAuctionDuration() >= 120)
                    return;
                module.getSlaveConfig().setAuctionDuration(module.getSlaveConfig().getAuctionDuration() + 1);
            }
            if (clickContext.getClickType().isRightClick()) {
                if (module.getSlaveConfig().getAuctionDuration() <= 5)
                    return;
                module.getSlaveConfig().setAuctionDuration(module.getSlaveConfig().getAuctionDuration() - 1);
            }
            timer.setItem(new ItemCreator(SkullList.TIMER.getItemStack()).name("§8┃ §fTemps d'une vente").lore("", "§8§l» §fStatut: §c" + module.getSlaveConfig().getAuctionDuration() + " secondes", "", "§8┃ §fConfigurez le temps de chaque vente", "§8§l» §6Clique-gauche §fpour ajouter 1.", "§8§l» §6Clique-droit §fpour retirer 1.").get());
        });
        this.kInventory.setElement(5, timer);

        int startIndex = (page - 1) * itemsPerPage;
        int endIndex = Math.min(startIndex + itemsPerPage, UHC.getInstance().getUhcGame().getUhcData().getUhcPlayerList().size());

        int slot = 9;
        for (int i = startIndex; i < endIndex; i++) {
            UHCPlayer uhcPlayer = UHC.getInstance().getUhcGame().getUhcData().getUhcPlayerList().get(i);
            KItem kItem = new KItem(new ItemCreator(Material.SKULL_ITEM, 1, (byte) 3).name("§8┃ §f" + uhcPlayer.getName()).owner(uhcPlayer.getName())
                    .lore("", "§8§l» §fStatut: §a" + translate(uhcPlayer), "", "§8§l» §6Cliquez §fpour changer son statut.").get());
            kItem.addCallback((kInventory1, item, player, clickContext) -> {
                if (module.getAuctionSTATE() != AuctionSTATE.WAITING)
                    return;
                if (module.getSlaveConfig().getLeadersList().contains(uhcPlayer)) {
                    PlayersUtils.broadcastMessage("§c" + uhcPlayer.getName() + " n'est plus capitaine.");
                    module.getSlaveConfig().getLeadersList().remove(uhcPlayer);
                } else {
                    module.getSlaveConfig().getLeadersList().add(uhcPlayer);
                    PlayersUtils.broadcastMessage("§a" + uhcPlayer.getName() + " est désormais capitaine.");
                }
                kItem.setItem(new ItemCreator(Material.SKULL_ITEM, 1, (byte) 3).owner(uhcPlayer.getName())
                        .lore("", "§8§l» §fStatut: §a" + translate(uhcPlayer), "", "§8§l» §6Cliquez §fpour changer son statut.").get());
            });
            this.kInventory.setElement(slot++, kItem);
        }

        addNavigationButtons(page, totalPages);
    }

    private void addNavigationButtons(int page, int totalPages) {
        if (page > 1) {
            KItem prevPage = new KItem(new ItemCreator(Material.ARROW).name("§8┃ §cPage précédente").get());
            prevPage.addCallback((kInventory1, item, player, clickContext) -> {
                updateInventoryForPage(page - 1);
                this.page--;
            });
            this.kInventory.setElement(47, prevPage);
        }

        if (page < totalPages) {
            KItem nextPage = new KItem(new ItemCreator(Material.ARROW).name("§8┃ §aPage suivante").get());
            nextPage.addCallback((kInventory1, item, player, clickContext) -> {
                updateInventoryForPage(page + 1);
                this.page++;
            });
            this.kInventory.setElement(51, nextPage);
        }
    }

    private String translate(UHCPlayer uhcPlayer) {
        if (module.getSlaveConfig().getLeadersList().contains(uhcPlayer)) {
            return "§acapitaine";
        } else {
            return "§cesclave";
        }
    }

    public KInventory getkInventory() {
        return kInventory;
    }
}

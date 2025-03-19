package fr.anto42.emma.coreManager.uis;

import fr.anto42.emma.UHC;
import fr.anto42.emma.coreManager.players.UHCPlayer;
import fr.anto42.emma.coreManager.teams.UHCTeam;
import fr.anto42.emma.coreManager.teams.UHCTeamManager;
import fr.anto42.emma.utils.materials.ItemCreator;
import fr.anto42.emma.utils.players.PlayersUtils;
import fr.anto42.emma.utils.skulls.SkullList;
import fr.blendman974.kinventory.inventories.KInventory;
import fr.blendman974.kinventory.inventories.KItem;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

public class SelectTeamGUI {
    private final KInventory kInventory;
    private final UHCTeamManager uhcTeamManager = UHCTeamManager.getInstance();

    public SelectTeamGUI(int page) {
        this.kInventory = new KInventory(54, UHC.getInstance().getPrefix() + " §6§lSélection de l'équipe - Page " + (page + 1));

        KItem glass = new KItem(new ItemCreator(Material.STAINED_GLASS_PANE, 1, (byte) 4).get());
        for (int i : new int[]{0, 1, 9, 8, 7, 17, 36, 44, 45, 46, 52, 53}) {
            this.kInventory.setElement(i, glass);
        }

        KItem back = new KItem(new ItemCreator(SkullList.LEFT_AROOW.getItemStack()).name("§8┃ §cFermer le menu").lore("", "§8§l» §6Cliquez §fpour fermer.").get());
        back.addCallback((kInventoryRepresentation, itemStack, player, kInventoryClickContext) -> player.closeInventory());
        this.kInventory.setElement(48, back);

        KItem leaveTeam = new KItem(new ItemCreator(SkullList.RED_BALL.getItemStack()).name("§8┃ §cQuitter votre équipe").lore("", "§8┃ §fVotre équipe actuelle §cne vous plaît pas §f?", "§8┃ §aAucun soucis§f, quittez cette dernière !", "", "§8§l» §6Cliquez §fpour sélectionner.").get());
        leaveTeam.addCallback((kInventoryRepresentation, itemStack, player, kInventoryClickContext) -> {
            UHC.getUHCPlayer(player).leaveTeam();
            player.getInventory().clear();
            PlayersUtils.giveWaitingStuff(player);
        });

        KItem random = new KItem(new ItemCreator(SkullList.UNKNOWN_ELEMENT.getItemStack()).name("§8┃ §fRejoindre une équipe aléatoire").lore("", "§8┃ §fVous ne savez pas dans quelle", "§8┃ §féquipe vous immiscer ?", "", "§8§l» §6Cliquez §fpour sélectionner.").get());
        random.addCallback((kInventory1, item, player, clickContext) -> {
            if (uhcTeamManager.isRandomTeam())
                return;
            UHCPlayer uhcPlayer = UHC.getUHCPlayer(player);
            uhcPlayer.joinTeam(uhcTeamManager.getRandomFreeTeam());
            player.closeInventory();
        });



        if (uhcTeamManager.isRandomTeam()) {
            KItem no = new KItem(new ItemCreator(SkullList.RED_BALL.getItemStack()).name("§8┃ §fEquipes aléatoires").lore("", "§8┃ §fVous §cne pouvez pas rejoindre", "§8┃ §fd'équipe pour le moment").get());
            this.kInventory.setElement(22, no);
        }
        else {
            this.kInventory.setElement(3, leaveTeam);
            this.kInventory.setElement(5, random);
            List<UHCTeam> teams = uhcTeamManager.getUhcTeams();
            int teamsPerPage = 28;
            int startIndex = page * teamsPerPage;
            int maxTeams = uhcTeamManager.getMaxTeams();
            int endIndex = Math.min(startIndex + teamsPerPage, Math.min(maxTeams, teams.size()));
            final int[] slot = {10};
            for (int i = startIndex; i < endIndex; i++) {
                UHCTeam uhcTeam = teams.get(i);
                List<String> lore = new ArrayList<>();
                lore.add("");
                lore.add("§8§l» §fJoueurs de cette équipe:");
                if (uhcTeam.getUhcPlayerList().isEmpty()) lore.add("§8┃ §cAucun");
                else uhcTeam.getUhcPlayerList().forEach(uhcPlayer -> lore.add("§8┃ §7" + uhcPlayer.getName()));
                lore.add("");
                lore.add("§8§l» §6Cliquez §fpour rejoindre.");

                KItem teamItem = new KItem(new ItemCreator(Material.BANNER).bannerColor(uhcTeam.getDyeColor()).name(uhcTeam.getDisplayName()).lore(lore).get());
                teamItem.addCallback((kInventoryRepresentation, itemStack, player, kInventoryClickContext) -> {
                    UHCPlayer uhcPlayer = UHC.getUHCPlayer(player);
                    if (!uhcTeam.getUhcPlayerList().contains(uhcPlayer) && uhcTeam.getPlayersAmount() < uhcTeamManager.getSlots()) {
                        uhcPlayer.joinTeam(uhcTeam);
                        new SelectTeamGUI(page).getkInventory().open(player);
                        player.getInventory().clear();
                        PlayersUtils.giveWaitingStuff(player);
                    }
                });
                this.kInventory.setElement(slot[0], teamItem);
                slot[0]++;
                while (slot[0] == 17 || slot[0] == 18 || slot[0] == 26 || slot[0] == 27 || slot[0] == 35 || slot[0] == 36 || slot[0] == 44) {
                    slot[0]++;
                }
            }

            if (page > 0) {
                KItem previousPage = new KItem(new ItemCreator(Material.ARROW).name("§8┃ §fPage précédente").lore("", "§8§l» §6Cliquez §fpour revenir.").get());
                previousPage.addCallback((kInventoryRepresentation, itemStack, player, kInventoryClickContext) -> new SelectTeamGUI(page - 1).getkInventory().open(player));
                this.kInventory.setElement(2, previousPage);
            }

            if (endIndex < Math.min(maxTeams, teams.size())) {
                KItem nextPage = new KItem(new ItemCreator(Material.ARROW).name("§8┃ §fPage suivante").lore("", "§8§l» §6Cliquez §fpour avancer.").get());
                nextPage.addCallback((kInventoryRepresentation, itemStack, player, kInventoryClickContext) -> new SelectTeamGUI(page + 1).getkInventory().open(player));
                this.kInventory.setElement(6, nextPage);
            }
        }

    }

    public KInventory getkInventory() {
        return kInventory;
    }
}

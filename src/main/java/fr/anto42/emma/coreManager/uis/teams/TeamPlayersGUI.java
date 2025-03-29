package fr.anto42.emma.coreManager.uis.teams;

import fr.anto42.emma.UHC;
import fr.anto42.emma.coreManager.players.UHCPlayer;
import fr.anto42.emma.coreManager.teams.UHCTeam;
import fr.anto42.emma.utils.materials.ItemCreator;
import fr.anto42.emma.utils.skulls.SkullList;
import fr.blendman974.kinventory.inventories.KInventory;
import fr.blendman974.kinventory.inventories.KItem;
import org.bukkit.Material;

import java.security.cert.PKIXCertPathBuilderResult;
import java.util.ArrayList;
import java.util.List;

public class TeamPlayersGUI {
    private final KInventory kInventory;
    private final UHCTeam uhcTeam;

    public TeamPlayersGUI(UHCTeam uhcTeam, KInventory previous) {
        this.kInventory = new KInventory(54, UHC.getInstance().getPrefix() + " §6§lJoueurs de l'équipe: " + uhcTeam.getDisplayName());
        this.uhcTeam = uhcTeam;

        KItem glass = new KItem(new ItemCreator(Material.STAINED_GLASS_PANE, 1, (byte) 4).get());
        for (int i = 0; i < 9; i++) {
            this.kInventory.setElement(i, glass);
            this.kInventory.setElement(45 + i, glass);
        }
        for (int i = 36; i < 45; i++) {
            this.kInventory.setElement(i, glass);
        }

        KItem back = new KItem(new ItemCreator(SkullList.LEFT_AROOW.getItemStack()).name("§8┃ §cFermer le menu").lore("", "§8§l» §6Cliquez §fpour fermer.").get());
        back.addCallback((kInventoryRepresentation, itemStack, player, kInventoryClickContext) -> {
            if (previous != null) {
                previous.open(player);
            } else player.closeInventory();
        });
        this.kInventory.setElement(48, back);
    }


    public void updatePlayers() {
        int slot = 9;
        for (UHCPlayer uhcPlayer : uhcTeam.getUhcPlayerList()) {
            KItem kItem = new KItem(new ItemCreator(SkullList.CIBLE.getItemStack()).name("§8| §f" + uhcPlayer.getName()).get());
            List<String> lore = new ArrayList<>();
            lore.add("");
            if (uhcPlayer.getKickPlayer() == null) {
                lore.add("§8| §fAucun vote d'exclusion n'est en cours");
            } else {
                lore.add(" §c§oAfin que le joueur soit exclu,");
                lore.add(" §c§oil faut qu'au moins 60% des joueurs de la team votent pour son exclusion");
                lore.add("");
                lore.add("§8| §fOui: §a" + uhcPlayer.getKickPlayer().getYes());
                lore.add("§8| §fNon: §c" + uhcPlayer.getKickPlayer().getNo());
            }
            lore.add("");
            lore.add("§8§l» §6Cliquez §fpour exclure.");
            kItem.setDescription(lore);
            kItem.addCallback((kInventory1, item, player, clickContext) -> {
                if (uhcPlayer.getKickPlayer() == null) {
                    if (UHC.getUHCPlayer(player).getUuid() == uhcPlayer.getUuid()) return;
                    uhcPlayer.startKickPlayer(UHC.getUHCPlayer(player));
                    updatePlayers();
                } else new KickPlayerGUI(uhcPlayer.getKickPlayer(), getkInventory()).getkInventory().open(player);
            });
            this.kInventory.setElement(slot++, kItem);
        }
    }
    public KInventory getkInventory() {
        updatePlayers();
        return kInventory;
    }
}

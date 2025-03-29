package fr.anto42.emma.coreManager.uis.teams;

import fr.anto42.emma.UHC;
import fr.anto42.emma.coreManager.teams.KickPlayer;
import fr.anto42.emma.utils.materials.ItemCreator;
import fr.anto42.emma.utils.skulls.SkullList;
import fr.blendman974.kinventory.inventories.KInventory;
import fr.blendman974.kinventory.inventories.KItem;
import org.bukkit.Material;

public class KickPlayerGUI {
    private final KInventory kInventory;

    public KickPlayerGUI(KickPlayer kickPlayer, KInventory previous) {
        this.kInventory = new KInventory(54, UHC.getInstance().getPrefix() + " §6§lExpulsion de: " + kickPlayer.getTarget().getName());

        KItem glass = new KItem(new ItemCreator(Material.STAINED_GLASS_PANE, 1, (byte) 4).get());
        for (int i = 0; i < 9; i++) {
            this.kInventory.setElement(i, glass);
            this.kInventory.setElement(45 + i, glass);
        }
        for (int i = 36; i < 45; i++) {
            this.kInventory.setElement(i, glass);
        }

        for (int i = 9; i < 35; i++) {
            this.kInventory.setElement(i, new KItem(new ItemCreator(Material.STAINED_GLASS_PANE, 1, (byte) 8).get()));
        }

        KItem back = new KItem(new ItemCreator(SkullList.LEFT_AROOW.getItemStack()).name("§8┃ §cFermer le menu").lore("", "§8§l» §6Cliquez §fpour fermer.").get());
        back.addCallback((kInventoryRepresentation, itemStack, player, kInventoryClickContext) -> {
            if (previous != null) {
                previous.open(player);
            } else player.closeInventory();
        });
        this.kInventory.setElement(48, back);

        KItem yes = new KItem(new ItemCreator(SkullList.GREEN_BALL.getItemStack()).name("§8┃ §aVoter oui").lore("", "§8§l» §fVote pour: §a" + kickPlayer.getYes(),"", "§8§l» §6Cliquez §fpour voter pour l'expulsion.").get());
        yes.addCallback((kInventoryRepresentation, itemStack, player, kInventoryClickContext) -> {
            kickPlayer.addYesVote(UHC.getUHCPlayer(player));
        });
        this.kInventory.setElement(20, yes);

        KItem no = new KItem(new ItemCreator(SkullList.RED_BALL.getItemStack()).name("§8┃ §cVoter non").lore("", "§8§l» §fVote contre: §c" + kickPlayer.getNo(), "", "§8§l» §6Cliquez §fpour voter contre l'expulsion.").get());
        no.addCallback((kInventoryRepresentation, itemStack, player, kInventoryClickContext) -> {
            kickPlayer.addNoVote(UHC.getUHCPlayer(player));
        });
        this.kInventory.setElement(24, no);

    }

    public KInventory getkInventory() {
        return kInventory;
    }
}

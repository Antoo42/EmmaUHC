package fr.anto42.emma.game.modes.deathNote.uis;

import fr.anto42.emma.UHC;
import fr.anto42.emma.coreManager.players.UHCPlayer;
import fr.anto42.emma.game.modes.deathNote.roles.Mello;
import fr.anto42.emma.game.modes.deathNote.utils.MeloType;
import fr.anto42.emma.utils.players.SoundUtils;
import fr.anto42.emma.utils.materials.ItemCreator;
import fr.anto42.emma.utils.skulls.SkullList;
import fr.blendman974.kinventory.inventories.KInventory;
import fr.blendman974.kinventory.inventories.KItem;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class MelloGUI {

    private final KInventory kInventory;

    public MelloGUI() {
        this.kInventory = new KInventory(54, UHC.getInstance().getPrefix() +  " §cChoisir une forme de Mello");
        for (int i = 0; i < 9; i++) {
            KItem glass = new KItem(new ItemCreator(Material.STAINED_GLASS_PANE, 1, (byte) 11).get());
            this.kInventory.setElement(i, glass);
            this.kInventory.setElement(45 + i, glass);
        }
        for (int i = 36; i < 45; i++) {
            KItem glass = new KItem(new ItemCreator(Material.STAINED_GLASS_PANE, 1, (byte) 11).get());
            this.kInventory.setElement(i, glass);
        }
        KItem back = new KItem(new ItemCreator(SkullList.LEFT_AROOW.getItemStack()).name("§8┃ §cRevenir en arrière").lore("", "§8┃ §cVous ne trouvez pas §fce que vous souhaitez ?", "§8┃ §aPas de soucis§f, revenez en arrière !", "", "§8§l» §6Cliquez §fpour ouvrir.").get());
        back.addCallback((kInventoryRepresentation, itemStack, player, kInventoryClickContext) -> {
            player.closeInventory();
        });
        this.kInventory.setElement(49, back);

        KItem kItem = new KItem(new ItemCreator(SkullList.GREEN_BALL.getItemStack()).name("§8┃ §eMello §2§lgentil").lore("", "§8§l» §6Cliquez §fpour sélctionner cette forme.").get());
        KItem kItem1 = new KItem(new ItemCreator(SkullList.ORANGE_BALL.getItemStack()).name("§8┃ §eMello §6§ljaloux").lore("", "§8§l» §6Cliquez §fpour sélctionner cette forme.").get());
        KItem kItem2 = new KItem(new ItemCreator(SkullList.RED_BALL.getItemStack()).name("§8┃ §eMello §c§lméchant").lore("", "§8§l» §6Cliquez §fpour sélctionner cette forme.").get());

        kItem.addCallback((kInventoryRepresentation, itemStack, player, kInventoryClickContext) -> {
            UHCPlayer uhcPlayer = UHC.getUHCPlayer(player);
            if (((Mello) uhcPlayer.getRole()).getMeloType() != null)
                return;
            ((Mello) uhcPlayer.getRole()).setMeloType(MeloType.KIND);
            ((Mello) uhcPlayer.getRole()).setupMeloType();
            SoundUtils.playSoundToPlayer(player, Sound.EXPLODE);
            player.closeInventory();
        });
        kItem1.addCallback((kInventoryRepresentation, itemStack, player, kInventoryClickContext) -> {

            UHCPlayer uhcPlayer = UHC.getUHCPlayer(player);
            if (((Mello) uhcPlayer.getRole()).getMeloType() != null)
                return;
            ((Mello) uhcPlayer.getRole()).setMeloType(MeloType.JEALOUS);

            ((Mello) uhcPlayer.getRole()).setupMeloType();
            SoundUtils.playSoundToPlayer(player, Sound.EXPLODE);
            player.closeInventory();
        });
        kItem2.addCallback((kInventoryRepresentation, itemStack, player, kInventoryClickContext) -> {
            UHCPlayer uhcPlayer = UHC.getUHCPlayer(player);
            if (((Mello) uhcPlayer.getRole()).getMeloType() != null)
                return;
            ((Mello) uhcPlayer.getRole()).setMeloType(MeloType.EVIL);
            ((Mello) uhcPlayer.getRole()).setupMeloType();
            SoundUtils.playSoundToPlayer(player, Sound.EXPLODE);
            player.closeInventory();
        });
        this.kInventory.setElement(20, kItem);
        this.kInventory.setElement(22, kItem1);
        this.kInventory.setElement(24, kItem2);
    }

    public void open(Player player){
        this.kInventory.open(player);
    }
}

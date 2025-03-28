package fr.anto42.emma.coreManager.scenarios.uis;

import fr.anto42.emma.UHC;
import fr.anto42.emma.coreManager.scenarios.scFolder.BlockRemover;
import fr.anto42.emma.utils.players.SoundUtils;
import fr.anto42.emma.utils.materials.ItemCreator;
import fr.anto42.emma.utils.skulls.SkullList;
import fr.blendman974.kinventory.inventories.KInventory;
import fr.blendman974.kinventory.inventories.KItem;
import org.bukkit.Material;
import org.bukkit.Sound;

public class BlockRemoverGUI {
    private KInventory kInventory;

    public BlockRemoverGUI (BlockRemover luckyLeaves){
        this.kInventory = new KInventory(54, UHC.getInstance().getPrefix() + " §6§lConfiguration de BlockRemover");

        for (int i = 0; i < 9; i++) {
            KItem glass = new KItem(new ItemCreator(Material.STAINED_GLASS_PANE, 1, (byte) 3).get());
            this.kInventory.setElement(i, glass);
            this.kInventory.setElement(45 + i, glass);
        }
        for (int i = 36; i < 45; i++) {
            KItem glass = new KItem(new ItemCreator(Material.STAINED_GLASS_PANE, 1, (byte) 3).get());
            this.kInventory.setElement(i, glass);
        }

        KItem back = new KItem(new ItemCreator(SkullList.LEFT_AROOW.getItemStack()).name("§8┃ §cRevenir en arrière").lore("", "§8┃ §cVous ne trouvez pas §fce que vous souhaitez ?", "§8┃ §aPas de soucis§f, revenez en arrière !", "", "§8§l» §6Cliquez §fpour ouvrir.").get());
        back.addCallback((kInventoryRepresentation, itemStack, player, kInventoryClickContext) -> {
            UHC.getInstance().getUhcManager().getScenariosConfigGUI().open(player);
        });
        this.kInventory.setElement(49, back);

        KItem chance = new KItem(new ItemCreator(SkullList.TIMER.getItemStack()).name("§8┃ §fTimer avant que les blocks se retirent").lore("", "§8§l» §fStatut: §c" + luckyLeaves.getCooldown() + "s", "", "§8┃ §6Configurez §fle timer", "§8┃ §faprès lequel les blocks posés par les joueurs se retirent", "", "§8§l» §6Clique-gauche §fpour augmenter de 1.", "§8§l» §6Clique-droit pour retirer 1.").get());
        chance.addCallback((kInventoryRepresentation, itemStack, player, kInventoryClickContext) -> {
            if (kInventoryClickContext.getClickType().isLeftClick()) {
                if (luckyLeaves.getCooldown() >= 300) {
                    SoundUtils.playSoundToPlayer(player, Sound.VILLAGER_NO);
                    return;
                }
                luckyLeaves.increaseCooldown(5);
            }
            else if (kInventoryClickContext.getClickType().isRightClick()) {
                if (luckyLeaves.getCooldown() <= 5){
                    SoundUtils.playSoundToPlayer(player, Sound.VILLAGER_NO);
                    return;
                }
                luckyLeaves.decreaseCooldown(5);
            }
            chance.setItem(new ItemCreator(SkullList.TIMER.getItemStack()).name("§8┃ §fTimer avant que les blocks se retirent").lore("", "§8§l» §fStatut: §c" + luckyLeaves.getCooldown() + "s", "", "§8┃ §6Configurez §fle timer", "§8┃ §faprès lequel les blocks posés par les joueurs se retirent", "", "§8§l» §6Clique-gauche §fpour augmenter de 1.", "§8§l» §6Clique-droit pour retirer 1.").get());
        });
        this.kInventory.setElement(22, chance);
    }

    public KInventory getkInventory() {
        return kInventory;
    }
}
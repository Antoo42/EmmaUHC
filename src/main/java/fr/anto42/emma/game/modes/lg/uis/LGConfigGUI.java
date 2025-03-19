package fr.anto42.emma.game.modes.lg.uis;

import fr.anto42.emma.UHC;
import fr.anto42.emma.coreManager.uis.config.GameModeGUI;
import fr.anto42.emma.game.modes.lg.utils.LGUtils;
import fr.anto42.emma.utils.materials.ItemCreator;
import fr.anto42.emma.utils.skulls.SkullList;
import fr.blendman974.kinventory.inventories.KInventory;
import fr.blendman974.kinventory.inventories.KItem;
import org.bukkit.Material;

public class LGConfigGUI {
    private final KInventory kInventory;
    public LGConfigGUI() {
        this.kInventory = new KInventory(54, UHC.getInstance().getPrefix() + " §c§lParamètres du mode Loup Garou");
        for (int i = 0; i < 9; i++) {
            KItem glass = new KItem(new ItemCreator(Material.STAINED_GLASS_PANE, 1, (byte) 14).get());
            this.kInventory.setElement(i, glass);
            this.kInventory.setElement(45 + i, glass);
        }
        for (int i = 36; i < 45; i++) {
            KItem glass = new KItem(new ItemCreator(Material.STAINED_GLASS_PANE, 1, (byte) 14).get());
            this.kInventory.setElement(i, glass);
        }
        KItem back = new KItem(new ItemCreator(SkullList.LEFT_AROOW.getItemStack()).name("§8┃ §cRevenir en arrière").lore("", "§8┃ §cVous ne trouvez pas §fce que vous souhaitez ?", "§8┃ §aPas de soucis§f, revenez en arrière !", "", "§8§l» §6Cliquez §fpour ouvrir.").get());
        back.addCallback((kInventoryRepresentation, itemStack, player, kInventoryClickContext) -> {
            new GameModeGUI().getkInventory().open(player);
        });
        this.kInventory.setElement(49, back);

        KItem roles = new KItem(new ItemCreator(Material.BONE).name("§8┃ §fRôles").lore("", "§8┃ §6Configurez §f les rôles de votre partie", ""," §8§l» §6Cliquez §fpour ouvrir.").get());
        roles.addCallback((kInventory1, item, player, clickContext) -> {
            new RolesConfigGUI(LGUtils.getModule().getLgData().getVillageCamp()).getkInventory().open(player);
        });
        this.kInventory.setElement(20, roles);
    }

    public KInventory getkInventory() {
        return kInventory;
    }
}

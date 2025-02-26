package fr.anto42.emma.game.modes.lg.uis;

import fr.anto42.emma.UHC;
import fr.anto42.emma.game.modes.lg.LGModule;
import fr.anto42.emma.game.modes.lg.roles.LGCamp;
import fr.anto42.emma.game.modes.lg.utils.LGUtils;
import fr.anto42.emma.utils.materials.ItemCreator;
import fr.anto42.emma.utils.skulls.SkullList;
import fr.blendman974.kinventory.inventories.KInventory;
import fr.blendman974.kinventory.inventories.KItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;

import java.util.concurrent.atomic.AtomicInteger;

public class RolesConfigGUI {
    private final KInventory kInventory;
    public RolesConfigGUI (LGCamp camp) {
        LGModule module = LGUtils.getModule();
        this.kInventory = new KInventory(54, UHC.getInstance().getPrefix() + " §6§lCréer un nouveau monde");
        for (int i = 0; i < 9; i++) {
            KItem glass = new KItem(new ItemCreator(Material.STAINED_GLASS_PANE, 1, (byte) 5).get());
            this.kInventory.setElement(i, glass);
            this.kInventory.setElement(45 + i, glass);
        }
        for (int i = 36; i < 45; i++) {
            KItem glass = new KItem(new ItemCreator(Material.STAINED_GLASS_PANE, 1, (byte) 5).get());
            this.kInventory.setElement(i, glass);
        }
        KItem back = new KItem(new ItemCreator(SkullList.LEFT_AROOW.getItemStack()).name("§8┃ §cRevenir en arrière").lore("", "§8┃ §cVous ne trouvez pas §fce que vous souhaitez ?", "§8┃ §aPas de soucis§f, revenez en arrière !", "", "§8§l» §6Cliquez §fpour ouvrir.").get());
        back.addCallback((kInventoryRepresentation, itemStack, player, kInventoryClickContext) -> {
            module.getkInventory().open(player);
        });
        this.kInventory.setElement(49, back);

        int[] slot = new int[]{9};
        AtomicInteger i = new AtomicInteger();
        module.getLgConfig().getAvaibleRolesList().stream().filter(lgRole -> lgRole.getStaringCamp().equals(camp)).forEach(lgRole -> {
            i.set(0);
            module.getLgConfig().getActiviatedRolesList().stream().filter(s -> s.equals(lgRole.getName())).forEach(lgRole1 -> i.set(i.get()+1));
            KItem kItem = new KItem(new ItemCreator(Material.STAINED_CLAY).name("§8┃ §f" + lgRole.getName()).lore("", "§8┃ §fEtat: " + (i.get() == 0 ? "§cAucun" : "§a" + i.get()), "", "§8§l» §6Clique-gauche §fpour ajouter 1.", "§8§l» §6Clique-droit §fpour retirer 1.").get());
            kItem.addCallback((kInventory1, item, player, clickContext) -> {
                if (clickContext.getClickType().isLeftClick()) {
                   module.getLgConfig().getActiviatedRolesList().add(lgRole.getName());
                   module.getLgConfig().getActiviatedRolesList().forEach(Bukkit::broadcastMessage);
                } else if (clickContext.getClickType().isRightClick() && i.get() > 0) {
                    module.getLgConfig().getActiviatedRolesList().remove(lgRole.getName());
                }
                getkInventory().open(player);
            });
            kInventory.setElement(slot[0], kItem);
            slot[0]++;
        });

    }
    public KInventory getkInventory() {
        return kInventory;
    }
}

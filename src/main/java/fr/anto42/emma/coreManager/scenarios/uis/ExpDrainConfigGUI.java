package fr.anto42.emma.coreManager.scenarios.uis;

import fr.anto42.emma.UHC;
import fr.anto42.emma.coreManager.scenarios.scFolder.ExpDrain;
import fr.anto42.emma.utils.materials.ItemCreator;
import fr.anto42.emma.utils.skulls.SkullList;
import fr.blendman974.kinventory.inventories.KInventory;
import fr.blendman974.kinventory.inventories.KItem;

public class ExpDrainConfigGUI {
    private final KInventory kInventory;

    public ExpDrainConfigGUI(ExpDrain expDrain) {
        this.kInventory = new KInventory(54, UHC.getInstance().getPrefix() + " §6§lConfiguration d'ExpDrain");

        for (int i = 0; i < 9; i++) {
            KItem glass = new KItem(new ItemCreator(SkullList.GRAY_BALL.getItemStack()).name(" ").get());
            this.kInventory.setElement(i, glass);
            this.kInventory.setElement(45 + i, glass);
        }
        for (int i = 36; i < 45; i++) {
            KItem glass = new KItem(new ItemCreator(SkullList.GRAY_BALL.getItemStack()).name(" ").get());
            this.kInventory.setElement(i, glass);
        }

        // Bouton retour
        KItem back = new KItem(new ItemCreator(SkullList.LEFT_AROOW.getItemStack())
                .name("§8┃ §cRevenir en arrière")
                .lore("", "§8┃ §cVous ne trouvez pas §fce que vous souhaitez ?", "§8┃ §aPas de soucis§f, revenez en arrière !", "", "§8§l» §6Cliquez §fpour ouvrir.")
                .get());
        back.addCallback((kInventoryRepresentation, itemStack, player, kInventoryClickContext) ->
                UHC.getInstance().getUhcManager().getScenariosConfigGUI().open(player)
        );
        this.kInventory.setElement(49, back);

        // Configuration de l'intervalle
        KItem intervalItem = new KItem(new ItemCreator(SkullList.GREEN_BALL.getItemStack())
                .name("§8┃ §fIntervalle de drain")
                .lore(
                        "",
                        "§8§l» §fActuel: §e" + (expDrain.getDrainInterval() / 1200) + " §fminutes",
                        "",
                        "§8┃ §6Configurez §fl'intervalle entre chaque drain",
                        "",
                        "§8§l» §6Clique-gauche §fpour §a+1 minute",
                        "§8§l» §6Clique-droit §fpour §c-1 minute"
                ).get());
        intervalItem.addCallback((kInventoryRepresentation, itemStack, player, kInventoryClickContext) -> {
            if (kInventoryClickContext.getClickType().isLeftClick()) {
                expDrain.setDrainInterval(expDrain.getDrainInterval() + 1200);
            } else if (kInventoryClickContext.getClickType().isRightClick() && expDrain.getDrainInterval() > 1200) {
                expDrain.setDrainInterval(expDrain.getDrainInterval() - 1200);
            }

            // Mise à jour du lore
            intervalItem.setItem(new ItemCreator(SkullList.GREEN_BALL.getItemStack())
                    .name("§8┃ §fIntervalle de drain")
                    .lore(
                            "",
                            "§8§l» §fActuel: §e" + (expDrain.getDrainInterval() / 1200) + " §fminutes",
                            "",
                            "§8┃ §6Configurez §fl'intervalle entre chaque drain",
                            "",
                            "§8§l» §6Clique-gauche §fpour §a+1 minute",
                            "§8§l» §6Clique-droit §fpour §c-1 minute"
                    ).get());
        });
        this.kInventory.setElement(20, intervalItem);

        // Configuration des dégâts
        KItem damageItem = new KItem(new ItemCreator(SkullList.RED_BALL.getItemStack())
                .name("§8┃ §fDégâts sans XP")
                .lore(
                        "",
                        "§8§l» §fActuel: §c" + (expDrain.getDamage() / 2) + " §c♥",
                        "",
                        "§8┃ §6Configurez §fles dégâts infligés",
                        "§8┃ §fsi le joueur n'a pas d'XP",
                        "",
                        "§8§l» §6Clique-gauche §fpour §a+0.5 ♥",
                        "§8§l» §6Clique-droit §fpour §c-0.5 ♥"
                ).get());
        damageItem.addCallback((kInventoryRepresentation, itemStack, player, kInventoryClickContext) -> {
            if (kInventoryClickContext.getClickType().isLeftClick()) {
                expDrain.setDamage(expDrain.getDamage() + 1.0);
            } else if (kInventoryClickContext.getClickType().isRightClick() && expDrain.getDamage() > 1.0) {
                expDrain.setDamage(expDrain.getDamage() - 1.0);
            }

            // Mise à jour du lore
            damageItem.setItem(new ItemCreator(SkullList.RED_BALL.getItemStack())
                    .name("§8┃ §fDégâts sans XP")
                    .lore(
                            "",
                            "§8§l» §fActuel: §c" + (expDrain.getDamage() / 2) + " §c♥",
                            "",
                            "§8┃ §6Configurez §fles dégâts infligés",
                            "§8┃ §fsi le joueur n'a pas d'XP",
                            "",
                            "§8§l» §6Clique-gauche §fpour §a+0.5 ♥",
                            "§8§l» §6Clique-droit §fpour §c-0.5 ♥"
                    ).get());
        });
        this.kInventory.setElement(24, damageItem);
    }


    public KInventory getkInventory() {
        return kInventory;
    }
}

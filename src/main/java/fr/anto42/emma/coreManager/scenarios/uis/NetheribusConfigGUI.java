package fr.anto42.emma.coreManager.scenarios.uis;

import fr.anto42.emma.UHC;
import fr.anto42.emma.coreManager.scenarios.scFolder.Netheribus;
import fr.anto42.emma.utils.materials.ItemCreator;
import fr.anto42.emma.utils.skulls.SkullList;
import fr.blendman974.kinventory.inventories.KInventory;
import fr.blendman974.kinventory.inventories.KItem;

public class NetheribusConfigGUI {
    private final KInventory kInventory;

    public NetheribusConfigGUI(Netheribus netheribus) {
        this.kInventory = new KInventory(54, UHC.getInstance().getPrefix() + " §6§lConfiguration de Netheribus");

        // Bordures en verre
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

        // Configuration du délai
        KItem delayItem = new KItem(new ItemCreator(SkullList.GREEN_BALL.getItemStack())
                .name("§8┃ §fDélai d'activation")
                .lore(
                        "",
                        "§8§l» §fActuel: §e" + (netheribus.getNetherDelay() / 1200) + " §fminutes",
                        "",
                        "§8┃ §6Configurez §fle délai avant obligation",
                        "",
                        "§8§l» §6Clique-gauche §fpour §a+1 minute",
                        "§8§l» §6Clique-droit §fpour §c-1 minute"
                ).get());
        delayItem.addCallback((kInventoryRepresentation, itemStack, player, kInventoryClickContext) -> {
            if (kInventoryClickContext.getClickType().isLeftClick()) {
                netheribus.setNetherDelay(netheribus.getNetherDelay() + 1200);
            } else if (kInventoryClickContext.getClickType().isRightClick() && netheribus.getNetherDelay() > 1200) {
                netheribus.setNetherDelay(netheribus.getNetherDelay() - 1200);
            }
            delayItem.setItem(new ItemCreator(SkullList.GREEN_BALL.getItemStack())
                    .name("§8┃ §fDélai d'activation")
                    .lore(
                            "",
                            "§8§l» §fActuel: §e" + (netheribus.getNetherDelay() / 1200) + " §fminutes",
                            "",
                            "§8┃ §6Configurez §fle délai avant obligation",
                            "",
                            "§8§l» §6Clique-gauche §fpour §a+1 minute",
                            "§8§l» §6Clique-droit §fpour §c-1 minute"
                    ).get());
        });
        this.kInventory.setElement(20, delayItem);

        // Intervalle des dégâts
        KItem intervalItem = new KItem(new ItemCreator(SkullList.ORANGE_BALL.getItemStack())
                .name("§8┃ §fIntervalle des dégâts")
                .lore(
                        "",
                        "§8§l» §fActuel: §e" + (netheribus.getDamageInterval() / 20) + " §fsecondes",
                        "",
                        "§8┃ §6Configurez §fl'intervalle entre les dégâts",
                        "",
                        "§8§l» §6Clique-gauche §fpour §a+1 seconde",
                        "§8§l» §6Clique-droit §fpour §c-1 seconde"
                ).get());
        intervalItem.addCallback((kInventoryRepresentation, itemStack, player, kInventoryClickContext) -> {
            if (kInventoryClickContext.getClickType().isLeftClick()) {
                netheribus.setDamageInterval(netheribus.getDamageInterval() + 20);
            } else if (kInventoryClickContext.getClickType().isRightClick() && netheribus.getDamageInterval() > 20) {
                netheribus.setDamageInterval(netheribus.getDamageInterval() - 20);
            }
            intervalItem.setItem(new ItemCreator(SkullList.ORANGE_BALL.getItemStack())
                    .name("§8┃ §fIntervalle des dégâts")
                    .lore(
                            "",
                            "§8§l» §fActuel: §e" + (netheribus.getDamageInterval() / 20) + " §fsecondes",
                            "",
                            "§8┃ §6Configurez §fl'intervalle entre les dégâts",
                            "",
                            "§8§l» §6Clique-gauche §fpour §a+1 seconde",
                            "§8§l» §6Clique-droit §fpour §c-1 seconde"
                    ).get());
        });
        this.kInventory.setElement(24, intervalItem);

        // Dégâts infligés
        KItem damageItem = new KItem(new ItemCreator(SkullList.RED_BALL.getItemStack())
                .name("§8┃ §fDégâts infligés")
                .lore(
                        "",
                        "§8§l» §fActuel: §c" + (netheribus.getDamageAmount() / 2) + " §c♥",
                        "",
                        "§8┃ §6Configurez §fles dégâts infligés",
                        "",
                        "§8§l» §6Clique-gauche §fpour §a+0.5 ♥",
                        "§8§l» §6Clique-droit §fpour §c-0.5 ♥"
                ).get());
        damageItem.addCallback((kInventoryRepresentation, itemStack, player, kInventoryClickContext) -> {
            if (kInventoryClickContext.getClickType().isLeftClick()) {
                netheribus.setDamageAmount(netheribus.getDamageAmount() + 1.0);
            } else if (kInventoryClickContext.getClickType().isRightClick() && netheribus.getDamageAmount() > 1.0) {
                netheribus.setDamageAmount(netheribus.getDamageAmount() - 1.0);
            }
            damageItem.setItem(new ItemCreator(SkullList.RED_BALL.getItemStack())
                    .name("§8┃ §fDégâts infligés")
                    .lore(
                            "",
                            "§8§l» §fActuel: §c" + (netheribus.getDamageAmount() / 2) + " §c♥",
                            "",
                            "§8┃ §6Configurez §fles dégâts infligés",
                            "",
                            "§8§l» §6Clique-gauche §fpour §a+0.5 ♥",
                            "§8§l» §6Clique-droit §fpour §c-0.5 ♥"
                    ).get());
        });
        this.kInventory.setElement(22, damageItem);
    }

    public KInventory getkInventory() {
        return kInventory;
    }
}

package fr.anto42.emma.coreManager.uis.rules;

import fr.anto42.emma.UHC;
import fr.anto42.emma.coreManager.uis.config.SettingsConfigGUI;
import fr.anto42.emma.coreManager.worldManager.WorldManager;
import fr.anto42.emma.game.UHCGame;
import fr.anto42.emma.utils.materials.ItemCreator;
import fr.anto42.emma.utils.skulls.SkullList;
import fr.blendman974.kinventory.inventories.KInventory;
import fr.blendman974.kinventory.inventories.KItem;
import org.bukkit.Material;

public class WorldConfigInRulesGUI {
    String translate (boolean b) {
        if (b)
            return "§aactivé";
        else return "§cdésactivé";
    }

    private final KInventory kInventory;
    private final UHCGame uhc = UHC.getInstance().getUhcGame();

    public WorldConfigInRulesGUI() {
        this.kInventory = new KInventory(54, UHC.getInstance().getPrefix() + " §a§lParamètres du monde");

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
            new RulesGUI().getkInventory().open(player);
        });
        this.kInventory.setElement(49, back);

        KItem nether = new KItem(new ItemCreator(Material.NETHERRACK).name("§8┃ §cNether").lore("", "§8§l» §fStatut: " + translate(uhc.getUhcConfig().isNether()), "", "§8┃ §fSouhaitez-vous activer le §cNether §f?", "", "", "§8§l» §cVous ne pouvez pas modifié cela.").get());
        this.kInventory.setElement(20, nether);


        KItem cleanCenter = new KItem(new ItemCreator(SkullList.CYAN_BALL.getItemStack()).name("§8┃ §fCentre propre").lore("", "§8§l» §fStatut: " + translate(WorldManager.isClean()), "", "§8┃ §fNettoyer le centre afin que ce dernier", "§8┃ §fsoit §asans eau","", "§c§o  Cette option n'est pas désactivable !", "", "§8§l» §cVous ne pouvez pas modifié cela.").get());
        this.kInventory.setElement(29, cleanCenter);


        KItem roofed = new KItem(new ItemCreator(Material.SAPLING, 1, (short) 5).name("§8┃ §fForêt noir").lore("", "§8§l» §fStatut: " + translate(WorldManager.isRoofed()), "", "§8┃ §fGénérez une §cforêt §fartificielle §aau centre de la carte","", "§c§o  Cette option n'est pas désactivable !", "", "§8§l» §cVous ne pouvez pas modifié cela.").get());
        this.kInventory.setElement(31, roofed);

        KItem end = new KItem(new ItemCreator(SkullList.ENDERDRAGON_BALL.getItemStack()).name("§8┃ §3End").lore("", "§8§l» §fStatut: " + translate(uhc.getUhcConfig().isEnd()), "", "§8┃ §fSouhaitez-vous activer l'§3End §f?", "", "§8§l» §cVous ne pouvez pas modifié cela.").get());
        this.kInventory.setElement(22, end);

        KItem initialSize = new KItem(new ItemCreator(SkullList.EARTH.getItemStack()).name("§8┃ §fTaille initiale").lore("", "§8§l» §fStatut: §c" + uhc.getUhcConfig().getStartBorderSize() + "§f blocks", "", "§8┃ §fParamètrez le rayon initial de la bordure", "", "", "§8§l» §cVous ne pouvez pas modifié cela.").get());
        this.kInventory.setElement(11, initialSize);

        KItem blockPerS = new KItem(new ItemCreator(Material.RABBIT_FOOT).name("§8┃ §fVitesse de la bordure").lore("", "§8§l» §fStatut: §c" + uhc.getUhcConfig().getBlockPerS() + "§f blocks/s", "", "§8┃ §fConfigurez la vitesse de réduction de la bordure", "", "", "§8§l» §cVous ne pouvez pas modifié cela.").get());
        this.kInventory.setElement(13, blockPerS);

        KItem finalSize = new KItem(new ItemCreator(SkullList.EARTH_2.getItemStack()).name("§8┃ §fTaille finale").lore("", "§8§l» §fStatut: §c" + uhc.getUhcConfig().getFinalBorderSize() + "§f blocks", "", "§8┃ §fParamètrez le rayon final de la bordure", "", "", "§8§l» §cVous ne pouvez pas modifié cela.").get());
        this.kInventory.setElement(15, finalSize);

        KItem boost = new KItem(new ItemCreator(Material.DIAMOND_ORE).name("§8┃ §fBoost de minerais").lore("", "§8§l» §fStatut: §c" + uhc.getUhcConfig().getBoostMultiplier() + "%", "", "§8┃ §fL'outil parfait pour miner moins longtemps", "", "§c§o  Afin que le boost prenne effet,", "§c§o  vous devez prégener la carte de jeu", "", "§8§l» §cVous ne pouvez pas modifié cela.").get());
        this.kInventory.setElement(33, boost);

        KItem doubleGold = new KItem(new ItemCreator(Material.GOLD_INGOT).name("§8┃ §fDouble gold").lore("", "§8§l» §fStatut: §a" + translate(uhc.getUhcConfig().isDoubleGold()), "", "§8┃ §fLes minerais de diamants donnent 2 golds", "§8┃ §fsi le joueur a sa limite de diamants minés", "", "§8§l» §cVous ne pouvez pas modifié cela.").get());
        this.kInventory.setElement(24, doubleGold);

        KItem settings = new KItem(new ItemCreator(Material.REDSTONE_COMPARATOR).name("§8┃ §fParamètres").lore("", "§8┃ §fVous cherchez à §6paramétrer §fle déroulement de votre partie ?", "§8┃ §fVous êtes §aau bon endroit§f !", "","§8§l» §6Cliquez §fpour ouvrir").get());
        settings.addCallback((kInventoryRepresentation, itemStack, player, kInventoryClickContext) -> {
            new SettingsConfigGUI(false).getkInventory().open(player);
        });
        this.kInventory.setElement(47, settings);

        KItem scenarios = new KItem(new ItemCreator(SkullList.BOOKSHELF.getItemStack()).name("§8┃ §fScénarios").lore("", "§8┃ §fUne partie est toujours plus §aextravagante", "§8┃ §favec §aces derniers", "", "§8§l» §6Cliquez §fpour ouvrir.").get());
        scenarios.addCallback((kInventoryRepresentation, itemStack, player, kInventoryClickContext) -> {
            new ScenariosActivatedGUI().getkInventory().open(player);
        });
        this.kInventory.setElement(51, scenarios);
    }

    public KInventory getkInventory() {
        return kInventory;
    }
}

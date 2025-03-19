package fr.anto42.emma.coreManager.uis.config.worlds;

import fr.anto42.emma.UHC;
import fr.anto42.emma.coreManager.worldManager.WorldManager;
import fr.anto42.emma.utils.materials.ItemCreator;
import fr.anto42.emma.utils.skulls.SkullList;
import fr.blendman974.kinventory.inventories.KInventory;
import fr.blendman974.kinventory.inventories.KItem;
import me.daddychurchill.CityWorld.CityWorldGenerator;
import org.bukkit.Bukkit;
import org.bukkit.Material;

public class SettingsNewWorldGUI {
    private final KInventory kInventory;

    public SettingsNewWorldGUI() {
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
            new CreateWorldGUI().getkInventory().open(player);
        });
        this.kInventory.setElement(49, back);


        KItem createWorld = new KItem(new ItemCreator(SkullList.EARTH.getItemStack()).name("§8┃ §fCréer un nouveau monde classique").lore("", "§8┃ §fLe monde de jeu actuel §cne vous plaît pas §f?", "§8┃ §aRe-créez en un facilement §fque vous pouvez configuré", "§8┃ §fau préalable avec l'option §eparamètres du monde", "", "§8§l» §6Cliquez §fpour sélectionner").get());
        createWorld.addCallback((kInventoryRepresentation, itemStack, player, kInventoryClickContext) -> {
            Bukkit.broadcastMessage(UHC.getInstance().getPrefix() + " §cCréation d'un nouveau monde ! Le serveur peut par conséquant subir des ralentissements.");
            WorldManager.setRoofed(false);
            WorldManager.setClean(false);
            UHC.getInstance().getUhcGame().getUhcData().setNetherPreload(false);
            UHC.getInstance().getUhcGame().getUhcData().setPreloadFinished(false);
            UHC.getInstance().getUhcGame().getUhcData().setEndPreload(false);
            UHC.getInstance().getWorldManager().createGameWorld();
        });
        this.kInventory.setElement(9, createWorld);

        KItem superflat = new KItem(new ItemCreator(Material.GRASS).name("§8┃ §fMonde Superflat").lore("", "§8┃ §fCréez un monde §asuperflat§f, idéal pour des fights rapides.", "", "§8§l» §6Cliquez §fpour sélectionner").get());
        superflat.addCallback((kInventoryRepresentation, itemStack, player, kInventoryClickContext) -> {
            Bukkit.broadcastMessage(UHC.getInstance().getPrefix() + " §cCréation d'un nouveau monde ! Le serveur peut par conséquant subir des ralentissements.");
            WorldManager.setRoofed(false);
            WorldManager.setClean(true);
            UHC.getInstance().getUhcGame().getUhcData().setNetherPreload(false);
            UHC.getInstance().getUhcGame().getUhcData().setPreloadFinished(false);
            UHC.getInstance().getUhcGame().getUhcData().setEndPreload(false);
            UHC.getInstance().getWorldManager().createSuperflatWorld();
        });
        this.kInventory.setElement(10, superflat);

        KItem amplified = new KItem(new ItemCreator(Material.NETHER_STAR).name("§8┃ §fMonde Amplifié").lore("", "§8┃ §fCréez un monde §bamplifié§f, avec des montagnes et des paysages extrêmes.", "", "§8§l» §6Cliquez §fpour sélectionner").get());
        amplified.addCallback((kInventoryRepresentation, itemStack, player, kInventoryClickContext) -> {
            Bukkit.broadcastMessage(UHC.getInstance().getPrefix() + " §cCréation d'un nouveau monde ! Le serveur peut par conséquant subir des ralentissements.");
            WorldManager.setRoofed(false);
            WorldManager.setClean(false);
            UHC.getInstance().getUhcGame().getUhcData().setNetherPreload(false);
            UHC.getInstance().getUhcGame().getUhcData().setPreloadFinished(false);
            UHC.getInstance().getUhcGame().getUhcData().setEndPreload(false);
            UHC.getInstance().getWorldManager().createAmplifiedWorld();
        });
        this.kInventory.setElement(11, amplified);

        KItem cityWorld = new KItem(new ItemCreator(Material.IRON_DOOR).name("§8┃ §fCityWorld").lore("", "§8┃ §fCityWorld", "", "§8§l» §6Cliquez §fpour sélectionner").get());
        cityWorld.addCallback((kInventoryRepresentation, itemStack, player, kInventoryClickContext) -> {
            Bukkit.broadcastMessage(UHC.getInstance().getPrefix() + " §cCréation d'un nouveau monde ! Le serveur peut par conséquant subir des ralentissements.");
            WorldManager.setRoofed(false);
            WorldManager.setClean(false);
            UHC.getInstance().getUhcGame().getUhcData().setNetherPreload(false);
            UHC.getInstance().getUhcGame().getUhcData().setPreloadFinished(false);
            UHC.getInstance().getUhcGame().getUhcData().setEndPreload(false);
            UHC.getInstance().getWorldManager().createCityWorld(CityWorldGenerator.WorldStyle.NORMAL);
        });
        this.kInventory.setElement(12, cityWorld);

        KItem destroyedCityWorld = new KItem(new ItemCreator(Material.DEAD_BUSH).name("§8┃ §fCityWorld détruit").lore("", "§8┃ §fCityWorld détruit", "", "§8§l» §6Cliquez §fpour sélectionner").get());
        destroyedCityWorld.addCallback((kInventoryRepresentation, itemStack, player, kInventoryClickContext) -> {
            Bukkit.broadcastMessage(UHC.getInstance().getPrefix() + " §cCréation d'un nouveau monde ! Le serveur peut par conséquant subir des ralentissements.");
            WorldManager.setRoofed(false);
            WorldManager.setClean(false);
            UHC.getInstance().getUhcGame().getUhcData().setNetherPreload(false);
            UHC.getInstance().getUhcGame().getUhcData().setPreloadFinished(false);
            UHC.getInstance().getUhcGame().getUhcData().setEndPreload(false);
            UHC.getInstance().getWorldManager().createCityWorld(CityWorldGenerator.WorldStyle.DESTROYED);
        });
        this.kInventory.setElement(13, destroyedCityWorld);

        KItem floadedCityWorld = new KItem(new ItemCreator(Material.WATER_BUCKET).name("§8┃ §fCityWorld innondé").lore("", "§8┃ §fCityWorld innondé", "", "§8§l» §6Cliquez §fpour sélectionner").get());
        floadedCityWorld.addCallback((kInventoryRepresentation, itemStack, player, kInventoryClickContext) -> {
            Bukkit.broadcastMessage(UHC.getInstance().getPrefix() + " §cCréation d'un nouveau monde ! Le serveur peut par conséquant subir des ralentissements.");
            WorldManager.setRoofed(false);
            WorldManager.setClean(false);
            UHC.getInstance().getUhcGame().getUhcData().setNetherPreload(false);
            UHC.getInstance().getUhcGame().getUhcData().setPreloadFinished(false);
            UHC.getInstance().getUhcGame().getUhcData().setEndPreload(false);
            UHC.getInstance().getWorldManager().createCityWorld(CityWorldGenerator.WorldStyle.FLOODED);
        });
        this.kInventory.setElement(14, floadedCityWorld);

        /*KItem islandsCityWorld = new KItem(new ItemCreator(Material.LEAVES).name("§8┃ §fCityWorld archipel").lore("", "§8┃ §fCityWorld archipel", "", "§8§l» §6Cliquez §fpour sélectionner").get());
        islandsCityWorld.addCallback((kInventoryRepresentation, itemStack, player, kInventoryClickContext) -> {
            Bukkit.broadcastMessage(UHC.getInstance().getPrefix() + " §cCréation d'un nouveau monde ! Le serveur peut par conséquant subir des ralentissements.");
            WorldManager.setRoofed(false);
            WorldManager.setClean(false);
            UHC.getInstance().getUhcGame().getUhcData().setNetherPreload(false);
            UHC.getInstance().getUhcGame().getUhcData().setPreloadFinished(false);
            UHC.getInstance().getUhcGame().getUhcData().setEndPreload(false);
            UHC.getInstance().getWorldManager().createCityWorld(CityWorldGenerator.WorldStyle.FLOATING);
        });
        this.kInventory.setElement(15, islandsCityWorld);*/

        KItem alienCityWorld = new KItem(new ItemCreator(Material.ENCHANTMENT_TABLE).name("§8┃ §fCityWorld extraterrestre").lore("", "§8┃ §fCityWorld extraterrestre", "", "§8§l» §6Cliquez §fpour sélectionner").get());
        alienCityWorld.addCallback((kInventoryRepresentation, itemStack, player, kInventoryClickContext) -> {
            Bukkit.broadcastMessage(UHC.getInstance().getPrefix() + " §cCréation d'un nouveau monde ! Le serveur peut par conséquant subir des ralentissements.");
            WorldManager.setRoofed(false);
            WorldManager.setClean(false);
            UHC.getInstance().getUhcGame().getUhcData().setNetherPreload(false);
            UHC.getInstance().getUhcGame().getUhcData().setPreloadFinished(false);
            UHC.getInstance().getUhcGame().getUhcData().setEndPreload(false);
            UHC.getInstance().getWorldManager().createCityWorld(CityWorldGenerator.WorldStyle.ASTRAL);
        });
        this.kInventory.setElement(15, alienCityWorld);

        KItem snowDunes = new KItem(new ItemCreator(Material.SNOW_BLOCK).name("§8┃ §fCityWorld dunes de neiges").lore("", "§8┃ §fCityWorld dunes de neige", "", "§8§l» §6Cliquez §fpour sélectionner").get());
        snowDunes.addCallback((kInventoryRepresentation, itemStack, player, kInventoryClickContext) -> {
            Bukkit.broadcastMessage(UHC.getInstance().getPrefix() + " §cCréation d'un nouveau monde ! Le serveur peut par conséquant subir des ralentissements.");
            WorldManager.setRoofed(false);
            WorldManager.setClean(false);
            UHC.getInstance().getUhcGame().getUhcData().setNetherPreload(false);
            UHC.getInstance().getUhcGame().getUhcData().setPreloadFinished(false);
            UHC.getInstance().getUhcGame().getUhcData().setEndPreload(false);
            UHC.getInstance().getWorldManager().createCityWorld(CityWorldGenerator.WorldStyle.SNOWDUNES);
        });
        this.kInventory.setElement(16, snowDunes);

        KItem sandDunes = new KItem(new ItemCreator(Material.SAND).name("§8┃ §fCityWorld dunes de sable").lore("", "§8┃ §fCityWorld dunes de sable", "", "§8§l» §6Cliquez §fpour sélectionner").get());
        sandDunes.addCallback((kInventoryRepresentation, itemStack, player, kInventoryClickContext) -> {
            Bukkit.broadcastMessage(UHC.getInstance().getPrefix() + " §cCréation d'un nouveau monde ! Le serveur peut par conséquant subir des ralentissements.");
            WorldManager.setRoofed(false);
            WorldManager.setClean(false);
            UHC.getInstance().getUhcGame().getUhcData().setNetherPreload(false);
            UHC.getInstance().getUhcGame().getUhcData().setPreloadFinished(false);
            UHC.getInstance().getUhcGame().getUhcData().setEndPreload(false);
            UHC.getInstance().getWorldManager().createCityWorld(CityWorldGenerator.WorldStyle.SANDDUNES);
        });
        this.kInventory.setElement(17, sandDunes);

        KItem maze = new KItem(new ItemCreator(Material.FENCE).name("§8┃ §fCityWorld labyrinthe").lore("", "§8┃ §fCityWorld labyrinthe", "", "§8§l» §6Cliquez §fpour sélectionner").get());
        maze.addCallback((kInventoryRepresentation, itemStack, player, kInventoryClickContext) -> {
            Bukkit.broadcastMessage(UHC.getInstance().getPrefix() + " §cCréation d'un nouveau monde ! Le serveur peut par conséquant subir des ralentissements.");
            WorldManager.setRoofed(false);
            WorldManager.setClean(false);
            UHC.getInstance().getUhcGame().getUhcData().setNetherPreload(false);
            UHC.getInstance().getUhcGame().getUhcData().setPreloadFinished(false);
            UHC.getInstance().getUhcGame().getUhcData().setEndPreload(false);
            UHC.getInstance().getWorldManager().createCityWorld(CityWorldGenerator.WorldStyle.MAZE);
        });
        this.kInventory.setElement(18, maze);

        KItem roofed = new KItem(new ItemCreator(Material.SAPLING).name("§8┃ §fRoofed").lore("", "§8┃ §fCréez un monde §aroofed§f, avec de la forêt PARTOUT.", "", "§8§l» §6Cliquez §fpour sélectionner").get());
        roofed.addCallback((kInventoryRepresentation, itemStack, player, kInventoryClickContext) -> {
            Bukkit.broadcastMessage(UHC.getInstance().getPrefix() + " §cCréation d'un nouveau monde ! Le serveur peut par conséquant subir des ralentissements.");
            WorldManager.setRoofed(false);
            WorldManager.setClean(false);
            UHC.getInstance().getUhcGame().getUhcData().setNetherPreload(false);
            UHC.getInstance().getUhcGame().getUhcData().setPreloadFinished(false);
            UHC.getInstance().getUhcGame().getUhcData().setEndPreload(false);
            UHC.getInstance().getWorldManager().createRoofedWorld();
        });
        //this.kInventory.setElement(19, roofed);


    }



    public KInventory getkInventory() {
        return kInventory;
    }
}


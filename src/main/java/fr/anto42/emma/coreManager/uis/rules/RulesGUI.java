package fr.anto42.emma.coreManager.uis.rules;

import fr.anto42.emma.UHC;
import fr.anto42.emma.coreManager.Module;
import fr.anto42.emma.coreManager.scenarios.ScenarioManager;
import fr.anto42.emma.coreManager.uis.config.EnchantsConfigGUI;
import fr.anto42.emma.coreManager.uis.config.SettingsConfigGUI;
import fr.anto42.emma.coreManager.uis.config.StuffConfigGUI;
import fr.anto42.emma.coreManager.uis.config.TimerConfigGUI;
import fr.anto42.emma.coreManager.worldManager.WorldManager;
import fr.anto42.emma.utils.materials.ItemCreator;
import fr.anto42.emma.utils.players.SoundUtils;
import fr.anto42.emma.utils.skulls.SkullList;
import fr.blendman974.kinventory.inventories.KInventory;
import fr.blendman974.kinventory.inventories.KItem;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;

public class RulesGUI {
    private final KInventory kInventory;
    public RulesGUI() {
        this.kInventory = new KInventory(54, UHC.getInstance().getPrefix() + " §6§lMenu des règles");
        for (int i = 0; i < 9; i++) {
            KItem glass = new KItem(new ItemCreator(Material.STAINED_GLASS_PANE, 1, (byte) 3).get());
            this.kInventory.setElement(i, glass);
            this.kInventory.setElement(45 + i, glass);
        }
        for (int i = 36; i < 45; i++) {
            KItem glass = new KItem(new ItemCreator(Material.STAINED_GLASS_PANE, 1, (byte) 3).get());
            this.kInventory.setElement(i, glass);
        }
        KItem back = new KItem(new ItemCreator(SkullList.LEFT_AROOW.getItemStack()).name("§8┃ §cFermer le menu").lore("", "§8§l» §6Cliquez §fpour fermer.").get());
        back.addCallback((kInventoryRepresentation, itemStack, player, kInventoryClickContext) -> {
            player.closeInventory();
        });
        this.kInventory.setElement(49, back);

        Module module = UHC.getInstance().getUhcManager().getGamemode();
        KItem gamemode = new KItem(new ItemCreator(module.getItemStack()).name("§8┃ §f" + module.getName()).get());
        List<String> strings = new ArrayList<>();
        strings.add("§8" + module.getVersion());
        strings.add("");
        if (!module.getDesc().isEmpty() && module.isAvailable()) {
            strings.addAll(module.getDesc());
        } else strings.add("§8┃ §cAucune information.");
        strings.add("");
        if (module.getDev() != null) {
            strings.add("§8┃ §fDéveloppeur: §b" + module.getDev());
            strings.add("");
        }
        if (module.getConfigGUI() != null) {
            strings.add("§8§l» §6Cliquez §fpour ouvrir.");
            gamemode.addCallback((kInventory1, item, player, clickContext) -> {
                module.getConfigGUI().open(player);
            });
        }
        gamemode.setDescription(strings);
        kInventory.setElement(11, gamemode);

        KItem scenarios = new KItem(new ItemCreator(Material.BOOK).name("§8┃ §fScenarios").lore("", "§8§l» §fStatut: " + UHC.getInstance().getUhcManager().getScenarioManager().getNumberScenarios() ,"", "§8┃ §6Consultez §fles différents scénarios activés dans la partie", "", "§8§l» §6Cliquez §fpour ouvrir.").get());
        scenarios.addCallback((kInventoryRepresentation, itemStack, player, kInventoryClickContext) -> {
            if (UHC.getInstance().getUhcManager().getScenarioManager().getActivatedScenarios().isEmpty()){
                UHC.getUHCPlayer(player).sendClassicMessage("§cAucun scénario n'est activé !");
                SoundUtils.playSoundToPlayer(player, Sound.VILLAGER_NO);
                return;
            }
            new ScenariosActivatedGUI().getkInventory().open(player);
        });
        this.kInventory.setElement(31, scenarios);

        KItem gameRules = new KItem(new ItemCreator(SkullList.BLOCK_COMMANDBLOCK_DEFAULT.getItemStack()).name("§8┃ §fRègles de la partie").lore("", "§8┃ §fRetrouvez-ici les règles", "§8┃ §fgénérales de la partie", "", "§8§l» §6Cliquez §fpour ouvrir.").get());
        gameRules.addCallback((kInventoryRepresentation, itemStack, player, kInventoryClickContext) -> {
            new SettingsConfigGUI(false).getkInventory().open(player);
        });
        kInventory.setElement(13, gameRules);

        KItem enchants = new KItem(new ItemCreator(Material.ENCHANTMENT_TABLE).name("§8┃ §fEnchantements").lore("", "§8┃ §fRetrouvez-ici les enchantements", "§8┃ §cmaximums §fde la partie", "", "§8§l» §6Cliquez §fpour ouvrir.").get());
        enchants.addCallback((kInventoryRepresentation, itemStack, player, kInventoryClickContext) -> {
            new EnchantsConfigGUI(false).getkInventory().open(player);
        });
        kInventory.setElement(24, enchants);

        KItem diamondParts = new KItem(new ItemCreator(Material.DIAMOND_CHESTPLATE).name("§8┃ §fPièces en diamants").lore("", "§8┃ §fRetrouvez-ici les pièces en §bdiamant", "§8┃ §aautorisées §fdans la partie", "", "§8§l» §6Cliquez §fpour ouvrir.").get());
        diamondParts.addCallback((kInventoryRepresentation, itemStack, player, kInventoryClickContext) -> {
            new StuffConfigGUI(false).getkInventory().open(player);
        });
        kInventory.setElement(33, diamondParts);

        KItem worldConfig = new KItem(new ItemCreator(SkullList.EARTH.getItemStack()).name("§8┃ §fSpécifités du monde de jeu").lore().get());
        List<String> lore = new ArrayList<>();
        lore.add("");
        for (World world : WorldManager.getWorldList()) {
            lore.add("§8§l» " + WorldManager.translateWorldType(world) + WorldManager.getWorldType(world) + "§7: " + WorldManager.getPregenStatus(world));
        }
        lore.add("");
        lore.add("§8§l» §6Cliquez §fpour ouvrir.");
        worldConfig.setDescription(lore);
        worldConfig.addCallback((kInventoryRepresentation, itemStack, player, kInventoryClickContext) -> {
            new WorldConfigInRulesGUI().getkInventory().open(player);
        });
        kInventory.setElement(20, worldConfig);

        KItem timer = new KItem(new ItemCreator(SkullList.TIMER.getItemStack()).name("§8┃ §fTimers").lore("", "§8┃ §6Consultez §fles timers (temps)", "§8┃ §fde §acette superbe partie !", "", "§8§l» §6Cliquez §fpour ouvrir.").get());
        timer.addCallback((kInventoryRepresentation, itemStack, player, kInventoryClickContext) -> {
            new TimerConfigGUI(false).getkInventory().open(player);
        });
        this.kInventory.setElement(29, timer);

        KItem inv = new KItem(new ItemCreator(SkullList.CHEST.getItemStack()).name("§8┃ §fInventaire de départ").lore("", "§8┃ §fConsultez l'inventaire de départ de la partie", ""," §8§l» §6Cliquez §fpour ouvrir.").get());
        inv.addCallback((kInventoryRepresentation, itemStack, player, kInventoryClickContext) -> {
            new StarterInvGUI().getkInventory().open(player);
        });

        this.kInventory.setElement(15, inv);
    }

    public KInventory getkInventory() {
        return kInventory;
    }
}

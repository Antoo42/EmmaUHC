package fr.anto42.emma.coreManager.uis.config;

import fr.anto42.emma.UHC;
import fr.anto42.emma.coreManager.scenarios.ScenarioManager;
import fr.anto42.emma.coreManager.scenarios.ScenarioType;
import fr.anto42.emma.coreManager.scenarios.UHCScenario;
import fr.anto42.emma.utils.materials.ItemCreator;
import fr.anto42.emma.utils.skulls.SkullList;
import fr.blendman974.kinventory.inventories.KInventory;
import fr.blendman974.kinventory.inventories.KItem;
import org.bukkit.Material;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static fr.anto42.emma.coreManager.scenarios.ScenarioType.*;

public class ScenariosConfigGUI {
    private final KInventory kInventory;
    String translate(boolean b){
        if (b)
            return "§aactivé";
        else
            return "§cdésactivé";
    }
    private final int currentPage = 0;

    String isActivate(boolean b){
        if (b)
            return "§cnon visibles";
        else
            return "§avisibles";
    }

    public ScenariosConfigGUI(ScenarioManager scenarioManager, ScenarioType actualType, int page) {
        this.kInventory = new KInventory(54, UHC.getInstance().getPrefix() + " §6§lScénarios");
        for (int i = 0; i < 9; i++) {
            KItem glass = new KItem(new ItemCreator(Material.STAINED_GLASS_PANE, 1, (byte) 2).get());
            this.kInventory.setElement(i, glass);
            this.kInventory.setElement(45 + i, glass);
        }
        for (int i = 36; i < 45; i++) {
            KItem glass = new KItem(new ItemCreator(Material.STAINED_GLASS_PANE, 1, (byte) 2).get());
            this.kInventory.setElement(i, glass);
        }

        List<UHCScenario> scenarios = scenarioManager.getInitialScenarioList();
        scenarios.sort(Comparator.comparing(UHCScenario::getName));
        if (actualType != ScenarioType.ALL) {
            scenarios = scenarios.stream().filter(s -> s.getScenarioType() == actualType).collect(Collectors.toList());
        }
        KItem back = new KItem(new ItemCreator(SkullList.LEFT_AROOW.getItemStack()).name("§8┃ §cRevenir en arrière").lore("", "§8┃ §cVous ne trouvez pas §fce que vous souhaitez ?", "§8┃ §aPas de soucis§f, revenez en arrière !", "", "§8§l» §6Cliquez §fpour ouvrir.").get());
        back.addCallback((kInventoryRepresentation, itemStack, player, kInventoryClickContext) -> {
            UHC.getInstance().getUhcManager().getConfigMainGUI().open(player);
        });
        this.kInventory.setElement(49, back);

        KItem number = new KItem(new ItemCreator(Material.PAPER).name("§8┃ §fConfiguration des scénarios").lore("", "§8§l» §fScénarios cachés: " + translate(UHC.getInstance().getUhcGame().getUhcConfig().isHideScenarios()), "", "§8§l» §6Clique-droit §fpour rendre les scénarios " + isActivate(UHC.getInstance().getUhcGame().getUhcConfig().isHideScenarios()) + " §f.").get());
        number.addCallback((kInventoryRepresentation, itemStack, player, kInventoryClickContext) -> {
            UHC.getInstance().getUhcGame().getUhcConfig().setHideScenarios(!UHC.getInstance().getUhcGame().getUhcConfig().isHideScenarios());
            number.setItem(new ItemCreator(Material.PAPER).name("§8┃ §fConfiguration des scénarios").lore("", "§8§l» §fScénarios cachés: " + translate(UHC.getInstance().getUhcGame().getUhcConfig().isHideScenarios()), "", "§8§l» §6Clique-droit §fpour rendre les scénarios " + isActivate(UHC.getInstance().getUhcGame().getUhcConfig().isHideScenarios()) + " §f.").get());
        });
        kInventory.setElement(5, number);

        KItem settings = new KItem(new ItemCreator(Material.REDSTONE_COMPARATOR).name("§8┃ §fParamètres").lore("", "§8┃ §fVous cherchez à §6paramétrer §fle déroulement de votre partie ?", "§8┃ §fVous êtes §aau bon endroit§f !", "","§8§l» §6Cliquez §fpour ouvrir").get());
        settings.addCallback((kInventoryRepresentation, itemStack, player, kInventoryClickContext) -> {
            UHC.getInstance().getUhcManager().getSettingsConfigGUI().open(player);
        });
        this.kInventory.setElement(47, settings);

        KItem timer = new KItem(new ItemCreator(SkullList.TIMER.getItemStack()).name("§8┃ §fTimers").lore("", "§8┃ §6Affinez §fles timers (temps)", "§8┃ §fde §avotre superbe partie !", "", "§8§l» §6Cliquez §fpour ouvrir.").get());
        timer.addCallback((kInventoryRepresentation, itemStack, player, kInventoryClickContext) -> {
            UHC.getInstance().getUhcManager().getTimerConfigGUI().open(player);
        });
        this.kInventory.setElement(51, timer);

        KItem sign = new KItem(new ItemCreator(Material.SIGN).name("§8┃ §fFlitre de scénarios").lore("", "§8┃ §fTrie les scénarios afin de mieux t'y retrouver", "",
                (actualType == ALL ? "  §8§l» §a" + "Tous" : "    §7" + "Tous"),
                (actualType == PVP ? "  §8§l» §a" + actualType.getTypeName() : "    §7" + PVP.getTypeName()),
                (actualType == PVE ? "  §8§l» §a" + actualType.getTypeName() : "    §7" + PVE.getTypeName()),
                (actualType == STUFF ? "  §8§l» §a" + actualType.getTypeName() : "    §7" + STUFF.getTypeName()),
                (actualType == MINNING ? "  §8§l» §a" + actualType.getTypeName() : "    §7" + MINNING.getTypeName()),
                (actualType == WORLD ? "  §8§l» §a" + actualType.getTypeName() : "    §7" + WORLD.getTypeName()),
                (actualType == FUN ? "  §8§l» §a" + actualType.getTypeName() : "    §7" + FUN.getTypeName()),
                (actualType == OTHER ? "  §8§l» §a" + actualType.getTypeName() : "    §7" + OTHER.getTypeName()),
                "", "§8§l» §6Cliquez §fpour changer.").get());
        sign.addCallback((kInventoryRepresentation, itemStack, player, kInventoryClickContext) -> {
            if (actualType == ALL) {
                new ScenariosConfigGUI(UHC.getInstance().getUhcManager().getScenarioManager(), PVP, 0).getkInventory().open(player);
            } if (actualType == PVP) {
                new ScenariosConfigGUI(UHC.getInstance().getUhcManager().getScenarioManager(), PVE, 0).getkInventory().open(player);
            } if (actualType == PVE) {
                new ScenariosConfigGUI(UHC.getInstance().getUhcManager().getScenarioManager(),  STUFF, 0).getkInventory().open(player);
            } if (actualType == STUFF) {
                new ScenariosConfigGUI(UHC.getInstance().getUhcManager().getScenarioManager(),  MINNING, 0).getkInventory().open(player);
            } if (actualType == MINNING) {
                new ScenariosConfigGUI(UHC.getInstance().getUhcManager().getScenarioManager(),  WORLD, 0).getkInventory().open(player);
            } if (actualType == WORLD) {
                new ScenariosConfigGUI(UHC.getInstance().getUhcManager().getScenarioManager(),  FUN, 0).getkInventory().open(player);
            } if (actualType == FUN) {
                new ScenariosConfigGUI(UHC.getInstance().getUhcManager().getScenarioManager(),  OTHER, 0).getkInventory().open(player);
            } if (actualType == OTHER) {
                new ScenariosConfigGUI(UHC.getInstance().getUhcManager().getScenarioManager(),  ALL, 0).getkInventory().open(player);
            }
        });
        this.kInventory.setElement(3, sign);

        int ITEMS_PER_PAGE = 27;
        int startIndex = page * ITEMS_PER_PAGE;
        int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, scenarios.size());

        int slot = 9;
        for (int i = startIndex; i < endIndex; i++) {
            UHCScenario uhcScenario = scenarios.get(i);
            KItem kItem = new KItem(new ItemCreator(uhcScenario.getItemStack())
                    .name("§8┃ §f" + (!uhcScenario.isAvaible() ? "§c" : "") + uhcScenario.getName())
                    .lore("", "§8§l» §fStatut: " + (uhcScenario.isActivated() ? "§aactivé" : "§cdésactivé"), "", uhcScenario.getDesc(), "",
                            "§8§l» §fType: §6" + uhcScenario.getScenarioType().getTypeName(), "", (uhcScenario.isAvaible() ? "§8§l» §6Clique-gauche §fpour sélectionner." : "§8§l» §cCe scénario n'est pas disponible pour le moment."), (uhcScenario.isConfigurable() ? "§8§l» §6Clique-droit §fpour configurer." : ""))
                    .get());

            kItem.addCallback((kInventoryRepresentation, itemStack, player, kInventoryClickContext) -> {
                if (!uhcScenario.isAvaible() && !player.getName().equals("Anto42_"))
                    return;
                if (kInventoryClickContext.getClickType().isLeftClick()) {
                    if (scenarioManager.getActivatedScenarios().contains(uhcScenario)) {
                        scenarioManager.disableScenario(uhcScenario);
                    } else {
                        scenarioManager.activateScenerio(uhcScenario);
                    }
                    new ScenariosConfigGUI(scenarioManager, actualType, page).getkInventory().open(player);
                } else if (kInventoryClickContext.getClickType().isRightClick() && uhcScenario.isConfigurable()) {
                    uhcScenario.getkInventory().open(player);
                }
            });

            this.kInventory.setElement(slot++, kItem);
        }

        if (page > 0) {
            KItem previousPage = new KItem(new ItemCreator(Material.ARROW)
                    .name("§8┃ §fPage précédente")
                    .get());
            previousPage.addCallback((kInventoryRepresentation, itemStack, player, kInventoryClickContext) -> {
                new ScenariosConfigGUI(scenarioManager, actualType, page - 1).getkInventory().open(player);
            });
            this.kInventory.setElement(2, previousPage);
        }

        if (endIndex < scenarios.size()) {
            KItem nextPage = new KItem(new ItemCreator(Material.ARROW)
                    .name("§8┃ §fPage suivante")
                    .get());
            nextPage.addCallback((kInventoryRepresentation, itemStack, player, kInventoryClickContext) -> {
                new ScenariosConfigGUI(scenarioManager, actualType, page + 1).getkInventory().open(player);
            });
            this.kInventory.setElement(6, nextPage);
        }
    }

    public KInventory getkInventory() {
        return kInventory;
    }
}

package fr.anto42.emma.coreManager.scenarios;

import fr.anto42.emma.UHC;
import fr.anto42.emma.coreManager.scenarios.scFolder.*;
import org.bukkit.Bukkit;

import java.awt.print.Book;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class ScenarioManager {


    private final List<UHCScenario> initialScenarioList = new ArrayList<>();
    private final List<UHCScenario> activatedScenarios = new ArrayList<>();


    public ScenarioManager() {
        getInitialScenarioList().clear();
        getActivatedScenarios().clear();
        getInitialScenarioList().add(new Backpack(this));
        getInitialScenarioList().add(new BetaZombies(this));
        getInitialScenarioList().add(new BigCrack(this));
        getInitialScenarioList().add(new BlockRemover(this));
        getInitialScenarioList().add(new BowAimBot(this));
        getInitialScenarioList().add(new BowSwap(this));
        getInitialScenarioList().add(new CatEyes(this));
        getInitialScenarioList().add(new ChunkApocalypse(this));
        getInitialScenarioList().add(new CutClean(this));
        getInitialScenarioList().add(new DiamondBlood(this));
        getInitialScenarioList().add(new DoubleHealth(this));
        getInitialScenarioList().add(new DragonRush(this));
        getInitialScenarioList().add(new FastSmelting(this));
        getInitialScenarioList().add(new FinalHeal(this));
        getInitialScenarioList().add(new FireLess(this));
        getInitialScenarioList().add(new FlowerPower(this));
        getInitialScenarioList().add(new GoneFishing(this));
        getInitialScenarioList().add(new HasteyBoys(this));
        getInitialScenarioList().add(new HorseLess(this));
        getInitialScenarioList().add(new InfiniteEnchant(this));
        getInitialScenarioList().add(new LuckyLeaves(this));
        getInitialScenarioList().add(new MasterLevel(this));
        getInitialScenarioList().add(new MeleeFun(this));
        getInitialScenarioList().add(new MisteryTeams(this));
        getInitialScenarioList().add(new NineSlots(this));
        getInitialScenarioList().add(new NoBow(this));
        getInitialScenarioList().add(new NoBreak(this));
        getInitialScenarioList().add(new NoCleanUP(this));
        getInitialScenarioList().add(new NoFall(this));
        getInitialScenarioList().add(new NoFood(this));
        getInitialScenarioList().add(new NoNameTag(this));
        getInitialScenarioList().add(new OnlyStones(this));
        getInitialScenarioList().add(new ProgressiveSpeed(this));
        getInitialScenarioList().add(new RandomCraft(this));
        getInitialScenarioList().add(new RandomLoots(this));
        getInitialScenarioList().add(new RodLess(this));
        getInitialScenarioList().add(new SafeMiners(this));
        getInitialScenarioList().add(new SkyHigh(this));
        getInitialScenarioList().add(new Symetrie(this));
        getInitialScenarioList().add(new SuperHeroes(this));
        getInitialScenarioList().add(new Timber(this));
        getInitialScenarioList().add(new TimberPvP(this));
        getInitialScenarioList().add(new TimeBomb(this));
        getInitialScenarioList().add(new VanillaPlus(this));
        getInitialScenarioList().add(new VeinMiner(this));
        getInitialScenarioList().add(new WeakestLink(this));
        getInitialScenarioList().add(new WebCage(this));
        getInitialScenarioList().add(new XHealths(this));
        getInitialScenarioList().add(new Depths(this));
        getInitialScenarioList().add(new NoSprint(this));
        getInitialScenarioList().add(new Paranoia(this));
        getInitialScenarioList().add(new NoAbso(this));
        getInitialScenarioList().add(new DoubleJump(this));
        getInitialScenarioList().add(new BookCeption(this));
        UHC.getInstance().getUhcGame().getUhcConfig().getScenarios().forEach(s -> {
            for (UHCScenario uhcScenario : getInitialScenarioList()) {
                if (uhcScenario.getName().equalsIgnoreCase(s))
                    getActivatedScenarios().add(uhcScenario);
            }
        });
    }

    public UHCScenario getScenario (String string) {
        AtomicReference<UHCScenario> sc = new AtomicReference<>();
        getInitialScenarioList().stream().filter(uhcScenario -> uhcScenario.getName().equals(string)).forEach(sc::set);
        return sc.get();
    }
    public List<UHCScenario> getInitialScenarioList() {
        return initialScenarioList;
    }

    public List<UHCScenario> getActivatedScenarios() {
        return activatedScenarios;
    }

    public void activateScenerio(UHCScenario uhcScenario){
        Bukkit.getServer().getPluginManager().registerEvents(uhcScenario, UHC.getInstance());
        uhcScenario.onEnable();
        getActivatedScenarios().add(uhcScenario);
    }

    public void resetScenarios() {
        for (UHCScenario uhcScenario : getActivatedScenarios()) {
            disableScenario(uhcScenario);
        }
    }

    public void disableScenario(UHCScenario uhcScenario){
        getActivatedScenarios().remove(uhcScenario);
        uhcScenario.onDisable();
    }

    public String getNumberScenarios() {
        if (UHC.getInstance().getUhcGame().getUhcConfig().isHideScenarios()) {
            return "§cScénarios cachés";
        }
        else return "§e" + getActivatedScenarios().size() + "§7/§b" + getInitialScenarioList().size();
    }

    public String getFormattedScenarios() {
        if (UHC.getInstance().getUhcGame().getUhcConfig().isHideScenarios()) {
            return "§cScénarios cachés";
        }

        if (activatedScenarios.isEmpty()) {
            return "Aucun scénario";
        }

        StringBuilder sb = new StringBuilder();
        int size = activatedScenarios.size();
        int index = 0;

        for (UHCScenario uhcScenario : getActivatedScenarios()) {
            sb.append(uhcScenario.getName());

            if (index < size - 2) {
                sb.append(", ");
            } else if (index == size - 2) {
                sb.append(" et ");
            }

            index++;
        }

        return sb.toString();
    }


    public List<String> getTabScenarios() {
        List<String> motd = new ArrayList<>();
        if (activatedScenarios.isEmpty()) {
            motd.add(" §8· §cAucun scénario");
            return motd;
        }
        final int[] i = {0};
        if (UHC.getInstance().getUhcGame().getUhcConfig().isHideScenarios()) {
            motd.add(" §8· §cScénarios cachés");
            return motd;
        }
        getActivatedScenarios().forEach(uhcScenario -> {
            if (i[0] == 3) {
                motd.add(" §8· §cet " + (activatedScenarios.size() - i[0]) + " autres...");
                i[0]++;
                return;
            }
            if (i[0] > 3)
                return;
            motd.add(" §8· §c" + uhcScenario.getName());
            i[0]++;
        });
        return motd;
    }
}

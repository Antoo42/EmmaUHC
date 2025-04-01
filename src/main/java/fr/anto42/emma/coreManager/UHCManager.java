package fr.anto42.emma.coreManager;

import fr.anto42.emma.UHC;
import fr.anto42.emma.coreManager.scenarios.ScenarioManager;
import fr.anto42.emma.coreManager.scenarios.ScenarioType;
import fr.anto42.emma.coreManager.uis.AdminGUI;
import fr.anto42.emma.coreManager.uis.config.*;
import fr.anto42.emma.coreManager.uis.config.worlds.BorderConfigGUI;
import fr.anto42.emma.coreManager.uis.config.worlds.WorldSettingsGUI;
import fr.anto42.emma.coreManager.votes.VoteSystem;
import fr.anto42.emma.coreManager.worldManager.WorldManager;
import fr.anto42.emma.game.modes.bingo.BingoModule;
import fr.anto42.emma.game.modes.classic.ClassicModule;
import fr.anto42.emma.game.modes.deathNote.DeathNoteModule;
import fr.anto42.emma.game.modes.lg.LGModule;
import fr.anto42.emma.game.modes.oldDN.DNModule;
import fr.anto42.emma.game.modes.stp.SwitchModule;
import fr.anto42.emma.game.modes.taupeGun.TGModule;
import fr.anto42.emma.game.modes.trueLove.TrueLoveModule;
import fr.blendman974.kinventory.inventories.KInventory;

import java.util.ArrayList;
import java.util.List;

public class UHCManager {

    private Module gamemode;
    public Module getGamemode() {
        return gamemode;
    }

    public void setGamemode(Module gamemode) {
        this.gamemode = gamemode;
    }
    private final WorldManager worldManager = new WorldManager();
    private ScenarioManager scenarioManager = new ScenarioManager();
    private final List<Module> moduleList = new ArrayList<>();
    public List<Module> getModuleList() {
        return moduleList;
    }

    public ScenarioManager getScenarioManager() {
        return scenarioManager;
    }

    private final VoteSystem voteSystem;

    public UHCManager() {
        ClassicModule module = new ClassicModule();
        this.moduleList.add(module);
        setGamemode(module);
        moduleList.add(new BingoModule());
        moduleList.add(new DeathNoteModule());
        //moduleList.add(new DNModule());
        //moduleList.add(new LGModule());
        moduleList.add(new SwitchModule());
        moduleList.add(new TGModule());
        moduleList.add(new TrueLoveModule());
        voteSystem = new VoteSystem();
    }

    public VoteSystem getVoteSystem() {
        return voteSystem;
    }

    public WorldManager getWorldManager() {
        return worldManager;
    }


    public KInventory getStuffConfigGUI() {
        return new StuffConfigGUI(true).getkInventory();
    }

    public KInventory getConfigMainGUI() {
        return new ConfigMainGUI(UHC.getInstance().getUhcGame()).getkInventory();
    }

    public KInventory getWorldConfgGUI() {
        return new WorldSettingsGUI().getkInventory();
    }


    public KInventory getTeamsConfigGUi() {
        return new TeamsConfigGUI().getkInventory();
    }

    public KInventory getScenariosConfigGUI() {
        return new ScenariosConfigGUI(scenarioManager, ScenarioType.ALL, 0).getkInventory();
    }

    public KInventory getBorderConfigGUI() {
        return new BorderConfigGUI().getkInventory();
    }

    public KInventory getTimerConfigGUI() {
        return new TimerConfigGUI(true).getkInventory();
    }

    public KInventory getSettingsConfigGUI() {
        return new SettingsConfigGUI(true).getkInventory();
    }

    public KInventory getEnchantsConfigGUI() {
        return new EnchantsConfigGUI(true).getkInventory();
    }

    public KInventory getAdminGUI() {
        return new AdminGUI().getkInventory();
    }

    public KInventory getPotionConfigGUI() {
        return new PotionConfigGUI().getkInventory();
    }

    public void setScenarioManager(ScenarioManager scenarioManager) {
        this.scenarioManager = scenarioManager;
    }
}

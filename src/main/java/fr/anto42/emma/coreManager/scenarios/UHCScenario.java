package fr.anto42.emma.coreManager.scenarios;

import fr.anto42.emma.UHC;
import fr.anto42.emma.game.GameState;
import fr.anto42.emma.game.UHCGame;
import fr.blendman974.kinventory.inventories.KInventory;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public abstract class UHCScenario implements Listener {
    private final String name;
    private String desc;
    private ScenarioType scenarioType = ScenarioType.OTHER;
    private final ItemStack itemStack;
    private boolean configurable = false;
    private KInventory kInventory;
    private final UHCGame uhcGame = UHC.getInstance().getUhcGame();
    private final ScenarioManager scenarioManager;
    private boolean avaible = true;

    public UHCScenario(String name, ItemStack itemStack, ScenarioManager scenarioManager) {
        setScenarioType(ScenarioType.OTHER);
        this.name = name;
        this.scenarioManager = scenarioManager;
        this.desc = "§8┃ §cAucune information.";
        this.itemStack = itemStack;
    }

    public ScenarioManager getScenarioManager(){
        return scenarioManager;
    }

    public String getName() {
        return name;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public boolean isActivated(){
        return scenarioManager.getActivatedScenarios().contains(this) && uhcGame.getGameState().equals(GameState.PLAYING);
    }

    public boolean isConfigurable() {
        return configurable;
    }

    public void setConfigurable(boolean configurable) {
        this.configurable = configurable;
    }

    public KInventory getkInventory() {
        return kInventory;
    }

    public void setkInventory(KInventory kInventory) {
        this.kInventory = kInventory;
    }

    public UHCGame getUhcGame() {
        return uhcGame;
    }

    public void onEnable(){
    }

    public void onDisable(){}


    public ScenarioType getScenarioType() {
        return scenarioType;
    }

    public void setScenarioType(ScenarioType scenarioType) {
        this.scenarioType = scenarioType;
    }

    public boolean isAvaible() {
        return avaible;
    }

    public void setAvaible(boolean avaible) {
        this.avaible = avaible;
    }

    public void onStart(){}
}

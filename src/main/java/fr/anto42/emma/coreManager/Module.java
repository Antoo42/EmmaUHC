package fr.anto42.emma.coreManager;

import fr.anto42.emma.coreManager.scoreboard.UHCScoreboard;
import fr.blendman974.kinventory.inventories.KInventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public abstract class Module {
    private final String name;
    private final String discordName;
    private final ItemStack itemStack;
    private boolean configurable = false;
    private String docLink;
    private KInventory kInventory;
    private List<String> desc = new ArrayList<>();
    private String dev;
    private UHCScoreboard uhcScoreboard;
    private boolean available = true;
    private String version = "V1.0";
    private KInventory configGUI;




    public Module(String name, String discordName, ItemStack itemStack) {
        this.name = name;
        this.discordName = discordName;
        this.itemStack = itemStack;
    }


    public boolean isConfigurable() {
        return configurable;
    }

    public void setConfigurable(boolean configurable) {
        this.configurable = configurable;
    }



    public String getName() {
        return name;
    }

    public String getDiscordName() {
        return discordName;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public KInventory getkInventory() {
        return kInventory;
    }

    public void setkInventory(KInventory kInventory) {
        this.kInventory = kInventory;
    }

    public void onLoad(){}

    public void onUnLoad(){}

    public void onStart(){}

    public String getDocLink() {
        return docLink;
    }

    public void setDocLink(String docLink) {
        this.docLink = docLink;
    }

    public void winTester(){}

    public List<String> getDesc() {
        return desc;
    }

    public void setDesc(List<String> desc) {
        this.desc = desc;
    }

    public UHCScoreboard getUhcScoreboard() {
        return uhcScoreboard;
    }

    public void setUhcScoreboard(UHCScoreboard uhcScoreboard) {
        this.uhcScoreboard = uhcScoreboard;
    }

    public String getDev() {
        return dev;
    }

    public void setDev(String dev) {
        this.dev = dev;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public abstract KInventory getConfigGUI();

    public void setConfigGUI(KInventory configGUI) {
        this.configGUI = configGUI;
    }
}

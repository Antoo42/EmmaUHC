package fr.anto42.emma.game.modes.lg;

import fr.anto42.emma.UHC;
import fr.anto42.emma.coreManager.Module;
import fr.anto42.emma.game.modes.lg.impl.LGConfig;
import fr.anto42.emma.game.modes.lg.impl.LGData;
import fr.anto42.emma.game.modes.lg.listeners.LGListeners;
import fr.anto42.emma.game.modes.lg.uis.LGConfigGUI;
import fr.anto42.emma.game.modes.lg.utils.LGUtils;
import fr.anto42.emma.utils.materials.ItemCreator;
import org.bukkit.Bukkit;
import org.bukkit.Material;

public class LGModule extends Module {
    public LGModule() {
        super("§c§lLG", "Loup-Garou", new ItemCreator(Material.BONE).get());
        LGUtils.setModule(this);

        setUhcScoreboard(new LGScoreboard());
        getDesc().add("§8┃ §fInspiré par l'§eunivers §fdes Loups-Garous de Thiercelieux, §cLG §6UHC");
        getDesc().add("§8┃ §fvous plongera dans des parties riches en §emystères§f, §cintrigues §fet §esurprises§f.");
        setDev("Anto42_");

        setConfigurable(true);
        setkInventory(new LGConfigGUI().getkInventory());

        setUhcScoreboard(new LGScoreboard());
        lgData = new LGData();
        lgConfig = new LGConfig(this);

        setAvailable(false);
    }

    //DATA
    private LGConfig lgConfig;
    private LGData lgData;


    @Override
    public void onStart() {
        Bukkit.getPluginManager().registerEvents(new LGListeners(this), UHC.getInstance());

    }

    //TODO win tester
    public void winTester() {

    }

    //GETTER AND SETTER
    public LGConfig getLgConfig() {
        return lgConfig;
    }

    public void setLgConfig(LGConfig lgConfig) {
        this.lgConfig = lgConfig;
    }

    public LGData getLgData() {
        return lgData;
    }

    public void setLgData(LGData lgData) {
        this.lgData = lgData;
    }
}

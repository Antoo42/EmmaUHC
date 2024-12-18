package fr.anto42.emma.game.modes.lg.impl;

import fr.anto42.emma.game.modes.lg.LGModule;
import fr.anto42.emma.game.modes.lg.roles.LGRole;
import fr.anto42.emma.game.modes.lg.roles.roles.villagers.SimpleVillager;

import java.util.ArrayList;
import java.util.List;

public class LGConfig {
    private List<LGRole> avaibleRolesList = new ArrayList<>();
    public LGConfig(LGModule module) {
        avaibleRolesList.add(new SimpleVillager(module));
    }


    public List<LGRole> getAvaibleRolesList() {
        return avaibleRolesList;
    }

    public void setAvaibleRolesList(List<LGRole> avaibleRolesList) {
        this.avaibleRolesList = avaibleRolesList;
    }

    private List<String> activiatedRolesList = new ArrayList<>();

    public List<String> getActiviatedRolesList() {
        return activiatedRolesList;
    }
}
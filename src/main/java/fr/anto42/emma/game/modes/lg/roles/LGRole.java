package fr.anto42.emma.game.modes.lg.roles;

import fr.anto42.emma.coreManager.players.roles.Camp;
import fr.anto42.emma.coreManager.players.roles.Role;
import fr.anto42.emma.game.modes.lg.LGModule;
import fr.anto42.emma.game.modes.lg.utils.LGUtils;

public abstract class LGRole extends Role {
    private LGCamp staringCamp;
    private boolean specialDeath = false;
    public LGRole(String name, Camp camp, LGModule gamemode) {
        super(name, camp, gamemode);
        staringCamp = (LGCamp) camp;
    }

    private String roleDesc;


    public String getRoleDesc() {
        return roleDesc;
    }

    public void setRoleDesc(String roleDesc) {
        this.roleDesc = roleDesc;
    }

    public boolean hasSpecialDeath() {
        return specialDeath;
    }

    public void setSpecialDeath(boolean specialDeath) {
        this.specialDeath = specialDeath;
    }

    public LGCamp getStaringCamp() {
        return staringCamp;
    }

    public void setStaringCamp(LGCamp staringCamp) {
        this.staringCamp = staringCamp;
    }

    @Override
    public void sendDesc() {
        LGUtils.sendLGMessage(getUhcPlayer()," Vous êtes " + getCamp().getColor() + getName());
        if (getCamp().getColor().equals("§6"))
            getUhcPlayer().sendMessage("§8§l» §7Votre objectif est de gagner §6§lseul§7.");
        else getUhcPlayer().sendMessage("§8§l» §7Votre objectif est de gagner avec: " + getCamp().getColor() + getCamp().getName());
        getUhcPlayer().sendMessage("");
        getUhcPlayer().sendMessage(getRoleDesc());
    }
}

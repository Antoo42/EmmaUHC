package fr.anto42.emma.game.modes.oldDN.impl;


import fr.anto42.emma.game.modes.oldDN.roles.Kira;
import fr.anto42.emma.game.modes.oldDN.roles.Mello;
import fr.anto42.emma.game.modes.oldDN.roles.Near;
import fr.anto42.emma.game.modes.oldDN.roles.Shinigami;
import fr.anto42.emma.game.modes.oldDN.utils.GameUtils;

import java.util.ArrayList;
import java.util.List;

public class DNConfig {

    public List<DNRole> roleList = new ArrayList<DNRole>() {{
        add(new Kira(GameUtils.getModule(), GameUtils.getModule()));
        add(new Shinigami(GameUtils.getModule(), GameUtils.getModule()));
        add(new Mello(GameUtils.getModule(), GameUtils.getModule()));
        add(new Near(GameUtils.getModule(), GameUtils.getModule()));
    }};
    public List<DNRole> getRoleList() {
        return roleList;
    }
}

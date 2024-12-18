package fr.anto42.emma.game.modes.lg.impl;

import fr.anto42.emma.game.modes.lg.roles.camps.CupleCamp;
import fr.anto42.emma.game.modes.lg.roles.camps.SoloCamp;
import fr.anto42.emma.game.modes.lg.roles.camps.VillageCamp;
import fr.anto42.emma.game.modes.lg.roles.camps.WereworlfCamp;

public class LGData {

    public LGData() {
        villageCamp = new VillageCamp();
        wereworlfCamp = new WereworlfCamp();
        cupleCamp = new CupleCamp();
        soloCamp = new SoloCamp();
    }



    private final VillageCamp villageCamp;
    private final WereworlfCamp wereworlfCamp;
    private final SoloCamp soloCamp;




    public CupleCamp getCupleCamp() {
        return cupleCamp;
    }

    public SoloCamp getSoloCamp() {
        return soloCamp;
    }

    public WereworlfCamp getWereworlfCamp() {
        return wereworlfCamp;
    }

    public VillageCamp getVillageCamp() {
        return villageCamp;
    }

    private final CupleCamp cupleCamp;








}

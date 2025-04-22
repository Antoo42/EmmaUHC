package fr.anto42.emma.game.impl.config;

public class StuffConfig {
    private boolean diamondHelmet = true;
    private boolean diamondChesp = true;
    private boolean diamondLeggins = true;
    private boolean diamondBoots = true;
    public boolean isDiamondHelmet() {
        return diamondHelmet;
    }

    public void setDiamondHelmet(boolean diamondHelmet) {
        this.diamondHelmet = diamondHelmet;
    }

    public boolean isDiamondChesp() {
        return diamondChesp;
    }

    public void setDiamondChesp(boolean diamondChesp) {
        this.diamondChesp = diamondChesp;
    }

    public boolean isDiamondLeggins() {
        return diamondLeggins;
    }

    public void setDiamondLeggins(boolean diamondLeggins) {
        this.diamondLeggins = diamondLeggins;
    }

    public boolean isDiamondBoots() {
        return diamondBoots;
    }

    public void setDiamondBoots(boolean diamondBoots) {
        this.diamondBoots = diamondBoots;
    }
}

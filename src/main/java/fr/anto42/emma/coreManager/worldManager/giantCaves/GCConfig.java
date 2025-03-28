package fr.anto42.emma.coreManager.worldManager.giantCaves;

public class GCConfig {
    public double sxz;
    public double sy;

    public int cutoff;

    public int caveBandMin;
    public int caveBandMax;

    public boolean debugMode;

    public GCConfig() {
        sxz = 200;
        sy = 100;
        cutoff = 62;
        caveBandMin = 6;
        caveBandMax = 50;
        debugMode = true;

    }
}
package fr.anto42.emma.coreManager.players;

public class PlayerStats {
    private String name;
    private int kills;
    private int deaths;
    private int diamondsMined;
    private int goldMined;
    private int ironMined;
    private String team;
    private String role;
    private boolean isAlive;
    private boolean hasWon;
    private double makedDamages;
    private double receivedDamages;

    public PlayerStats(String name, int kills, int deaths, int diamondsMined, int goldMined, int ironMined, String team, String role, boolean isAlive, boolean hasWon, double makedDamages, double receivedDamages) {
        this.name = name;
        this.kills = kills;
        this.deaths = deaths;
        this.diamondsMined = diamondsMined;
        this.goldMined = goldMined;
        this.ironMined = ironMined;
        this.team = team;
        this.role = role;
        this.isAlive = isAlive;
        this.hasWon = hasWon;
        this.makedDamages = makedDamages;
        this.receivedDamages = receivedDamages;
    }

    // Getters
    public String getName() { return name; }
    public int getKills() { return kills; }
    public int getDeaths() { return deaths; }
    public int getDiamondsMined() { return diamondsMined; }
    public int getGoldMined() { return goldMined; }
    public int getIronMined() { return ironMined; }
    public String getTeam() { return team; }
    public String getRole() { return role; }
    public boolean isAlive() { return isAlive; }
    public boolean hasWon() { return hasWon; }

    public double getMakedDamages() {
        return makedDamages;
    }

    public double getReceivedDamages() {
        return receivedDamages;
    }
}

package fr.anto42.emma.coreManager.players;

public class PlayerStats {
    private final String name;
    private final int kills;
    private final int deaths;
    private final int diamondsMined;
    private final int goldMined;
    private final int ironMined;
    private final String team;
    private final String role;
    private final boolean isAlive;
    private final boolean hasWon;
    private final double madeDamages;
    private final double receivedDamages;

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
        this.madeDamages = makedDamages;
        this.receivedDamages = receivedDamages;
    }

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

    public double getMadeDamages() {
        return madeDamages;
    }

    public double getReceivedDamages() {
        return receivedDamages;
    }
}

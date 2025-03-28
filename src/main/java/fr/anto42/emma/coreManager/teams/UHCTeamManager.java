package fr.anto42.emma.coreManager.teams;

import org.apache.commons.lang.RandomStringUtils;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class UHCTeamManager {


    private final static UHCTeamManager uhcTeamManager = new UHCTeamManager();
    public static UHCTeamManager getInstance() {
        return uhcTeamManager;
    }

    private final List<UHCTeam> initialTeams = new ArrayList<>();

    private boolean activated = false;
    private int slots = 2;

    private int maxTeams = 27;
    private boolean randomTeam = false;

    private List<UHCTeam> uhcTeams = new ArrayList<>();
    public List<UHCTeam> getUhcTeams() {
        return uhcTeams;
    }

    public void setUhcTeams(List<UHCTeam> uhcTeams) {
        this.uhcTeams = uhcTeams;
    }

    public int getSlots() {
        return slots;
    }

    public void setSlots(int slots) {
        this.slots = slots;
    }

    public boolean isActivated() {
        return activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    public UHCTeam createNewTeam(String name, String prefix, DyeColor dyeColor, int colorNumber, String color){
        Scoreboard score = Bukkit.getScoreboardManager().getMainScoreboard();
        String uuid = RandomStringUtils.random(6, true, false);
        uuid = uuid + "-";
        uuid = uuid + RandomStringUtils.random(6, false, true);

        Team team = score.registerNewTeam(uuid);
        score.getTeam(uuid).setPrefix(prefix);

        UHCTeam uhcTeam = new UHCTeam(name, color, prefix, dyeColor, colorNumber, uuid, team);
        this.uhcTeams.add(uhcTeam);
        this.initialTeams.add(uhcTeam);
        //System.out.println("Succes in the creation of the team: " + prefix + " (" + name + ")");
        return uhcTeam;
    }

    public void createTeams(){
        getUhcTeams().clear();

        String[] baseColors = {
                "Rouge", "Orange", "Jaune", "Vert", "Cyan", "Bleue", "Violet", "Rose", "Gris"
        };

        String[] colorVariants = {
                " Alpha", " Beta", " Gamma", " Delta", " Epsilon", " Zeta", " Eta", " Theta", " Iota", " Kappa",
                " Lambda", " Mu", " Nu", " Xi", " Omicron", " Pi", " Rho", " Sigma", " Tau", " Upsilon",
                " Phi", " Chi", " Psi", " Oméga"
        };


        String[] symbols = {"♥", "♦", "♠", "♣", "★", "☀", "☁", "⚡", "❄"};

        String[] colorCodes = {"§c", "§6", "§e", "§a", "§b", "§9", "§5", "§d", "§7"};

        DyeColor[] dyeColors = {
                DyeColor.RED, DyeColor.ORANGE, DyeColor.YELLOW, DyeColor.LIME, DyeColor.CYAN, DyeColor.BLUE, DyeColor.PURPLE, DyeColor.PINK, DyeColor.GRAY
        };

        int[] colorData = {14, 1, 4, 5, 9, 3, 2, 6, 8};

        int teamCount = 0;

        for (String symbol : symbols) {
            for (int i = 0; i < baseColors.length; i++) {
                createNewTeam(baseColors[i] + " " + symbol, colorCodes[i] + symbol + " ", dyeColors[i], colorData[i], colorCodes[i]);
                teamCount++;
            }
        }

        if (teamCount < 200) {
            for (String variant : colorVariants) {
                for (String symbol : symbols) {
                    for (int i = 0; i < baseColors.length; i++) {
                        createNewTeam(baseColors[i] + variant + " " + symbol, colorCodes[i] + variant + " " + symbol + " ", dyeColors[i], colorData[i], colorCodes[i]);
                        teamCount++;
                    }
                }
            }
        }



        System.out.println(getUhcTeams().size() + " teams created");

        /*//Hearts
        createNewTeam("Rouge ♥","§c♥ ", DyeColor.RED, 14, "§c");
        createNewTeam("Orange ♥","§6♥ ", DyeColor.ORANGE, 1,"§6");
        createNewTeam("Jaune ♥","§e♥ ", DyeColor.YELLOW, 4,"§e");
        createNewTeam("Vert ♥","§a♥ ", DyeColor.LIME, 5,"§a");
        createNewTeam("Cyan ♥","§b♥ ", DyeColor.LIGHT_BLUE, 3,"§b");
        createNewTeam("Bleue ♥","§9♥ ", DyeColor.BLUE, 11,"§9");
        createNewTeam("Rose ♥","§d♥ ", DyeColor.PINK, 6,"§d");

        //LOSANGE
        createNewTeam("Rouge ♦","§c♦ ", DyeColor.RED, 14, "§c");
        createNewTeam("Orange ♦","§6♦ ", DyeColor.ORANGE, 1,"§6");
        createNewTeam("Jaune ♦","§e♦ ", DyeColor.YELLOW, 4,"§e");
        createNewTeam("Vert ♦","§a♦ ", DyeColor.LIME, 5,"§a");
        createNewTeam("Cyan ♦","§b♦ ", DyeColor.LIGHT_BLUE, 3,"§b");
        createNewTeam("Bleue ♦","§9♦ ", DyeColor.BLUE, 11,"§9");
        createNewTeam("Rose ♦","§d♦ ", DyeColor.PINK, 6,"§d");

        //PIQUE
        createNewTeam("Rouge ♠","§c♠ ", DyeColor.RED, 14, "§c");
        createNewTeam("Orange ♠","§6♠ ", DyeColor.ORANGE, 1,"§6");
        createNewTeam("Jaune ♠","§e♠ ", DyeColor.YELLOW, 4,"§e");
        createNewTeam("Vert ♠","§a♠ ", DyeColor.LIME, 5,"§a");
        createNewTeam("Cyan ♠","§b♠ ", DyeColor.LIGHT_BLUE, 3,"§b");
        createNewTeam("Bleue ♠","§9♠ ", DyeColor.BLUE, 11,"§9");
        createNewTeam("Rose ♠","§d♠ ", DyeColor.PINK, 6,"§d");

        //TREFLE
        createNewTeam("Rouge ♣","§c♣ ", DyeColor.RED, 14,"§c");
        createNewTeam("Orange ♣","§6♣ ", DyeColor.ORANGE, 1,"§6");
        createNewTeam("Jaune ♣","§e♣ ", DyeColor.YELLOW, 4,"§e");
        createNewTeam("Vert ♣","§a♣ ", DyeColor.LIME, 5,"§a");
        createNewTeam("Cyan ♣","§b♣ ", DyeColor.LIGHT_BLUE, 3,"§b");
        createNewTeam("Bleue ♣","§9♣ ", DyeColor.BLUE, 11,"§9");
        createNewTeam("Rose ♣","§d♣ ", DyeColor.PINK, 6,"§d");*/
    }


    public List<UHCTeam> getFreeTeams(){
        List<UHCTeam> uhcTeams = new ArrayList<>();

        for(UHCTeam uhcTim : getInitialTeams()){
            if (uhcTim.getPlayersAmount() > 0 && uhcTim.getPlayersAmount() + 1 < getSlots()){
                uhcTeams.add(uhcTim);
            }
        }

        if (uhcTeams.isEmpty()){
            for(UHCTeam uhcTim : getUhcTeams()){
                if (uhcTim.getPlayersAmount() == 0){
                    uhcTeams.add(uhcTim);
                }
            }
        }



        return uhcTeams;
    }

    public UHCTeam getRandomFreeTeam() {
        List<UHCTeam> uhcTeams = new ArrayList<>();
        int a = 0;

        List<UHCTeam> sortedTeams = new ArrayList<>(getUhcTeams());
        sortedTeams.sort((team1, team2) -> Integer.compare(team2.getPlayersAmount(), team1.getPlayersAmount()));

        for (UHCTeam uhcTeam : sortedTeams) {
            if (a >= maxTeams) break;
            if (uhcTeam.getPlayersAmount() > 0 && uhcTeam.getPlayersAmount() < getSlots()) {
                uhcTeams.add(uhcTeam);
            }
            a++;
        }

        a = 0;
        if (uhcTeams.isEmpty()) {
            for (UHCTeam uhcTeam : sortedTeams) {
                if (a >= maxTeams) break;
                if (uhcTeam.getPlayersAmount() == 0) {
                    uhcTeams.add(uhcTeam);
                }
                a++;
            }
        }

        if (uhcTeams.isEmpty()) {
            return null;
        }

        return uhcTeams.get(new Random().nextInt(uhcTeams.size()));
    }



    public String getDisplayFormat(){
        if (!isActivated())
            return "FFA";
        else
            return "To" + slots + " §7(" + getMaxTeams() + ")";
    }

    private boolean friendlyFire = true;

    public boolean isFriendlyFire() {
        return friendlyFire;
    }

    public void setFriendlyFire(boolean friendlyFire) {
        this.friendlyFire = friendlyFire;
    }

    private boolean directionalArrow = false;

    public boolean isDirectionalArrow() {
        return directionalArrow;
    }

    public void setDirectionalArrow(boolean directionalArrow) {
        this.directionalArrow = directionalArrow;
    }

    public int getNumberOfAliveTeams(){
        return getUhcTeams().size();
    }

    public String translateTeamAlivesNumber(){
        if (!isActivated())
            return "";
        else
            return String.valueOf(getNumberOfAliveTeams());
    }

    public List<UHCTeam> getInitialTeams() {
        return initialTeams;
    }

    public int getMaxTeams() {
        return maxTeams;
    }

    public void setMaxTeams(int maxTeams) {
        this.maxTeams = maxTeams;
    }

    public boolean isRandomTeam() {
        return randomTeam;
    }

    public void setRandomTeam(boolean randomTeam) {
        this.randomTeam = randomTeam;
    }
}

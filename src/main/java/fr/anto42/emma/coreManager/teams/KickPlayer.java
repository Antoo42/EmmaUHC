package fr.anto42.emma.coreManager.teams;

import fr.anto42.emma.UHC;
import fr.anto42.emma.coreManager.players.UHCPlayer;
import fr.anto42.emma.game.GameState;

import java.util.ArrayList;
import java.util.List;

public class KickPlayer {
    private final UHCPlayer target;
    private int yes = 0;
    private int no = 1;
    private List<UHCPlayer> voted = new ArrayList<>();

    public KickPlayer(UHCPlayer target) {
        this.target = target;
        getVoted().add(target);
    }


    public UHCPlayer getTarget() {
        return target;
    }

    public int getYes() {
        return yes;
    }

    public void setYes(int yes) {
        this.yes = yes;
    }

    public int getNo() {
        return no;
    }

    public void setNo(int no) {
        this.no = no;
    }

    public List<UHCPlayer> getVoted() {
        return voted;
    }

    public void setVoted(List<UHCPlayer> voted) {
        this.voted = voted;
    }

    public void addYesVote(UHCPlayer uhcPlayer) {
        if (getVoted().contains(uhcPlayer))
            return;
        if (UHC.getInstance().getUhcGame().getGameState() != GameState.WAITING)
            return;
        yes++;
        getVoted().add(uhcPlayer);
        getTarget().getUhcTeam().getUhcPlayerList().forEach(uhcPlayer1 -> {
            uhcPlayer1.sendClassicMessage("§e" + uhcPlayer1.getName() + "§7 a voté §apour§7 l'expulsion de §e" + target.getName()+ "§7.");
        });


        UHCTeam uhcTeam = uhcPlayer.getUhcTeam();
        double recquired = uhcTeam.getPlayersAmount()*0.6;
        if (recquired <= yes) {
            uhcTeam.broadcastMessage("§e" + target.getName() + " §c a été exclu !");
            target.leaveTeam();
        }
    }

    public void addNoVote(UHCPlayer uhcPlayer) {
        if (getVoted().contains(uhcPlayer))
            return;
        if (UHC.getInstance().getUhcGame().getGameState() != GameState.WAITING)
            return;
        no++;
        getVoted().add(uhcPlayer);
        getTarget().getUhcTeam().getUhcPlayerList().forEach(uhcPlayer1 -> {
            uhcPlayer1.sendClassicMessage("§e" + uhcPlayer1.getName() + "§7 a voté §ccontre§7 l'expulsion de §e" + target.getName()+ "§7.");
        });

        UHCTeam uhcTeam = uhcPlayer.getUhcTeam();
        double recquired = uhcTeam.getPlayersAmount()*0.6;
        if (recquired > yes && (yes + no) >= uhcTeam.getPlayersAmount()) {
            uhcTeam.broadcastMessage("§cLe vote d'exclusion contre §e" + target.getName() + " §c n'a pas abouti.");
            target.setKickPlayer(null);
        }
    }
}

package fr.anto42.emma.game.modes.deathNote.roles;

import fr.anto42.emma.UHC;
import fr.anto42.emma.coreManager.Module;
import fr.anto42.emma.coreManager.players.UHCPlayer;
import fr.anto42.emma.coreManager.teams.UHCTeam;
import fr.anto42.emma.coreManager.teams.UHCTeamManager;
import fr.anto42.emma.game.modes.deathNote.impl.DNRole;
import fr.anto42.emma.game.modes.deathNote.utils.InvestigationResultType;
import fr.anto42.emma.utils.SoundUtils;
import fr.anto42.emma.utils.materials.ItemCreator;
import fr.anto42.emma.utils.players.PlayersUtils;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Shinigami extends DNRole {
    public Shinigami(Module gamemode) {
        super("Shinigami", null, gamemode);
        setInvestigationResultType(InvestigationResultType.SUSPECT);
    }

    UHCPlayer kiraPlayer;

    @Override
    public void setRole() {
        super.setRole();
        setCanReveal(true);
        Bukkit.getScheduler().runTaskLater(UHC.getInstance(), () -> {
            getUhcPlayer().getUhcTeam().getAliveUhcPlayers().stream().filter(uhcPlayer -> uhcPlayer.getRole() != null && uhcPlayer.getRole() instanceof Kira).forEach(uhcPlayer -> {
                kiraPlayer = uhcPlayer;
            });
        }, 5L);
    }

    @Override
    public void sendDesc() {
        super.sendDesc();


        getUhcPlayer().sendMessage("§7Le kira de ta team est: " + (kiraPlayer == null ? "§cinconnu" : kiraPlayer.getName()));
    }

    @Override
    public void reveal() {
        super.reveal();
        getUhcPlayer().sendEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 0, false, false));
        UHCTeam uhcTeam = UHCTeamManager.getInstance().createNewTeam("Shini-" + getUhcPlayer().getName(), "§e§lShinigami ", DyeColor.YELLOW, 0, "§e");
        getUhcPlayer().joinTeam(uhcTeam);
    }
}

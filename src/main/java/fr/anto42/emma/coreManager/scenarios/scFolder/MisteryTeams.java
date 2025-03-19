package fr.anto42.emma.coreManager.scenarios.scFolder;

import fr.anto42.emma.UHC;
import fr.anto42.emma.coreManager.listeners.customListeners.TryStartEvent;
import fr.anto42.emma.coreManager.players.UHCPlayer;
import fr.anto42.emma.coreManager.scenarios.ScenarioManager;
import fr.anto42.emma.coreManager.scenarios.ScenarioType;
import fr.anto42.emma.coreManager.scenarios.UHCScenario;
import fr.anto42.emma.coreManager.teams.UHCTeam;
import fr.anto42.emma.coreManager.teams.UHCTeamManager;
import fr.anto42.emma.utils.players.PlayersUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

public class MisteryTeams extends UHCScenario {
    public MisteryTeams(ScenarioManager scenarioManager) {
        super("MisteryTeams", new ItemStack(Material.BANNER), scenarioManager);
        setAvaible(false);
        setScenarioType(ScenarioType.FUN);
        setDesc("§8┃ §fLes équipes ne seront désignés que au PvP, et les joueurs doivent se rencontrer pour les connaitres !");
    }


    @EventHandler
    public void onTryStart(TryStartEvent event) {
        if (!UHCTeamManager.getInstance().isActivated() || !UHCTeamManager.getInstance().isRandomTeam()) {
            PlayersUtils.broadcastMessage("§cReset §7de toutes les équipes !");
            UHCTeamManager.getInstance().setActivated(true);
            UHC.getInstance().getUhcGame().getUhcData().getUhcPlayerList().stream().filter(uhcPlayer -> uhcPlayer.getUhcTeam() != null).forEach(UHCPlayer::leaveTeam);
            for(Player players : Bukkit.getOnlinePlayers()){
                players.getInventory().clear();
                PlayersUtils.giveWaitingStuff(players);
            }
        }
    }
}

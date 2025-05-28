package fr.anto42.emma.game.modes.classic;

import fr.anto42.emma.UHC;
import fr.anto42.emma.coreManager.Module;
import fr.anto42.emma.coreManager.players.UHCPlayer;
import fr.anto42.emma.coreManager.teams.UHCTeam;
import fr.anto42.emma.coreManager.teams.UHCTeamManager;
import fr.anto42.emma.game.GameState;
import fr.anto42.emma.game.UHCGame;
import fr.anto42.emma.utils.TimeUtils;
import fr.anto42.emma.utils.materials.ItemCreator;
import fr.anto42.emma.utils.players.PlayersUtils;
import fr.blendman974.kinventory.inventories.KInventory;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class ClassicModule extends Module {
    private static final long SHUTDOWN_DELAY = TimeUtils.minutes(5);

    private final UHCGame uhcGame = UHC.getInstance().getUhcGame();

    public ClassicModule() {
        super("§6§lClassique", "Classique", new ItemCreator(Material.GOLDEN_APPLE).get());
        setConfigurable(false);
        setDev("Anto42_");
        getDesc().add("§8┃ §fLe mode de jeu par défaut mais qui n'est pas moins extravagant !");
    }

    @Override
    public void onStart() {
        Bukkit.getPluginManager().registerEvents(new ClassicListeners(this), UHC.getInstance());
    }

    @Override
    public KInventory getConfigGUI() {
        return null;
    }

    public void winTester() {
        UHC uhc = UHC.getInstance();
        UHCTeamManager teamManager = UHCTeamManager.getInstance();

        if (!teamManager.isActivated()) {
            int alivePlayers = uhcGame.getUhcData().getUhcPlayerList().size();

            if (alivePlayers <= 1) {
                uhcGame.setGameState(GameState.FINISH);
                if (alivePlayers == 1) {
                    UHCPlayer winner = uhcGame.getUhcData().getUhcPlayerList().get(0);
                    broadcastWinPlayer(winner);
                    uhc.getDiscordManager().sendWin(winner);
                } else {
                    broadcastNoWinner();
                    uhc.getDiscordManager().sendWin(null);
                }
                sendRecapToAllPlayers();
                scheduleShutdown();
            }
        } else if (teamManager.getUhcTeams().size() == 1) {
            uhcGame.setGameState(GameState.FINISH);
            UHCTeam winningTeam = teamManager.getUhcTeams().get(0);
            uhc.getDiscordManager().sendWin(winningTeam);
            broadcastWinTeam(winningTeam);
            sendRecapToAllPlayers();
            scheduleShutdown();
        } else if (uhcGame.getUhcData().getUhcPlayerList().isEmpty()) {
            uhcGame.setGameState(GameState.FINISH);
            uhc.getDiscordManager().sendWin(null);
            broadcastNoWinner();
            sendRecapToAllPlayers();
            scheduleShutdown();
        }
    }

    // --- Méthodes utilitaires privées ---

    private void broadcastWinPlayer(UHCPlayer winner) {
        String prefix = UHC.getInstance().getPrefix();
        Bukkit.broadcastMessage("§7");
        Bukkit.broadcastMessage(String.format("%s §aFélicitations au joueur %s§a pour sa victoire en %s§a avec §b%d§a kills !",
                prefix,
                winner.getName(),
                UHC.getInstance().getUhcManager().getGamemode().getName(),
                winner.getKills()));
        Bukkit.broadcastMessage("§7");
    }

    private void broadcastWinTeam(UHCTeam team) {
        String prefix = UHC.getInstance().getPrefix();
        Bukkit.broadcastMessage("§7");
        Bukkit.broadcastMessage(String.format("%s §aFélicitations à l'équipe %s§a pour sa victoire en %s§a avec §b%d§a kills !",
                prefix,
                team.getDisplayName(),
                UHC.getInstance().getUhcManager().getGamemode().getName(),
                team.getKillsTeam()));
        Bukkit.broadcastMessage("§7");
    }

    private void broadcastNoWinner() {
        String prefix = UHC.getInstance().getPrefix();
        Bukkit.broadcastMessage("§7");
        Bukkit.broadcastMessage(prefix + " §cOups... Je crois que je n'ai pas suivit la partie... Qui a gagné ? :p");
        Bukkit.broadcastMessage("§7");
    }

    private void sendRecapToAllPlayers() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            UHCPlayer uhcPlayer = UHC.getUHCPlayer(player);
            player.sendMessage("§8┃ §fRécapitulatif de votre partie:");
            player.sendMessage("§7");
            player.sendMessage("§8§l» §3Kills: §e" + uhcPlayer.getKills());
            player.sendMessage("§8§l» §3Morts: §e" + uhcPlayer.getDeath());
            if (uhcPlayer.getRole() != null)
                player.sendMessage("§8§l» §3Rôle: §e" + uhcPlayer.getRole().getName());
        }
    }

    private void scheduleShutdown() {
        String prefix = UHC.getInstance().getPrefix();
        Bukkit.broadcastMessage("§7");
        Bukkit.broadcastMessage(prefix + " §cArrêt automatique du serveur dans 5 minutes !");
        Bukkit.broadcastMessage("§7");
        PlayersUtils.finishToSpawn();
        Bukkit.getScheduler().runTaskLater(UHC.getInstance(), Bukkit::shutdown, SHUTDOWN_DELAY);
    }
}

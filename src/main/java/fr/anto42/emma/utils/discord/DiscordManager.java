package fr.anto42.emma.utils.discord;

import fr.anto42.emma.UHC;
import fr.anto42.emma.coreManager.players.UHCPlayer;
import fr.anto42.emma.coreManager.players.roles.Camp;
import fr.anto42.emma.coreManager.players.roles.Role;
import fr.anto42.emma.coreManager.scenarios.ScenarioManager;
import fr.anto42.emma.coreManager.teams.UHCTeam;
import fr.anto42.emma.coreManager.teams.UHCTeamManager;
import fr.anto42.emma.game.GameState;
import fr.anto42.emma.game.UHCGame;
import fr.anto42.emma.utils.data.NetworkUtils;
import fr.anto42.emma.utils.TimeUtils;
import fr.anto42.emma.utils.gameSaves.GameSaveCreator;
import fr.anto42.emma.utils.players.PlayersUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.bukkit.Bukkit;

import java.awt.*;

public class DiscordManager {

    private static final String chatChannelId = UHC.getInstance().getConfig().getString("discordChatChannelId");
    private static final String announceChannelId = UHC.getInstance().getConfig().getString("discordAnnounceChannelId");
    private static final String winChannelId = UHC.getInstance().getConfig().getString("discordWinChannelId");
    private static final String logChannelId = UHC.getInstance().getConfig().getString("discordLogsChannelId");
    private static final String token = UHC.getInstance().getConfig().getString("discordToken");



    public DiscordManager() {
        this.discordBot = JDABuilder.createDefault(token, GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT).addEventListeners(new DiscordEvents())
                .build();
        Bukkit.getScheduler().runTaskLater(UHC.getInstance(), this::sendStarterAnounce, 100L);
        Bukkit.getScheduler().runTaskTimer(UHC.getInstance(), this::updateDiscordStatus, 200L, 100L);
    }

    private void updateDiscordStatus() {
        discordBot.getPresence().setStatus(UHC.getInstance().getUhcGame().getGameState().equals(GameState.PLAYING) ? OnlineStatus.ONLINE : OnlineStatus.IDLE);
        discordBot.getPresence().setActivity(Activity.playing(UHC.getInstance().getUhcGame().getUhcConfig().getUHCName()));
    }

    private final ScenarioManager scenarioManager = UHC.getInstance().getUhcManager().getScenarioManager();
    private final UHCGame uhc = UHC.getInstance().getUhcGame();

    public void sendAnounce() {
        TextChannel channel = discordBot.getTextChannelById(announceChannelId);

        if (channel != null) {
            EmbedBuilder embed = new EmbedBuilder()
                    .setTitle("🚀 " + uhc.getUhcConfig().getUHCName())
                    .setColor(java.awt.Color.GREEN)
                    .setDescription("Une nouvelle partie UHC est prête à commencer ! Préparez-vous au combat !")
                    .addField("🎮 Mode de jeu", UHC.getInstance().getUhcManager().getGamemode().getDiscordName(), false)
                    .addField("👨‍💻 Host", uhc.getUhcData().getHostName(), false)
                    .addField("⚔️ PvP", uhc.getUhcConfig().getPvp() + " minute(s)", false)
                    .addField("🧨 Bordure", uhc.getUhcConfig().getTimerBorder() + " minute(s)", false)
                    .addField("🚩 Équipes", UHCTeamManager.getInstance().getDisplayFormat(), false)
                    .addField("📖 Scénarios", scenarioManager.getFormattedScenarios(), true)
                    .addField("🛜 IP", UHC.getInstance().getConfig().getString("ip"), false)
                    .setFooter("🌍 Serveur: " + Bukkit.getServerName(), null);

            channel.sendMessageEmbeds(embed.build()).queue();
        } else {
            System.out.println("Salon introuvable avec l'ID : " + announceChannelId);
        }
    }

    public void sendStarterAnounce() {
        TextChannel channel = discordBot.getTextChannelById(logChannelId);

        if (channel != null) {
            EmbedBuilder embed = new EmbedBuilder()
                    .setTitle("🔥 " + uhc.getUhcConfig().getUHCName())
                    .setColor(java.awt.Color.BLUE)
                    .setDescription("🎉 Un serveur UHC vient de se lancer.")
                    .addField("🛜 IP", NetworkUtils.getPublicIPAddress() + ":" + Bukkit.getPort(), false)
                    .setFooter("🌍 Serveur: " + Bukkit.getServerName(), null);

            channel.sendMessageEmbeds(embed.build()).queue();
        } else {
            System.out.println("Salon introuvable avec l'ID : " + logChannelId);
        }
    }
    public void sendShutdownAnounce() {
        TextChannel channel = discordBot.getTextChannelById(logChannelId);

        if (channel != null) {
            EmbedBuilder embed = new EmbedBuilder()
                    .setTitle("❌ " + uhc.getUhcConfig().getUHCName())
                    .setColor(java.awt.Color.RED)
                    .setDescription("🎉 Un serveur UHC vient de se stopper.")
                    .addField("🛜 IP", NetworkUtils.getPublicIPAddress() + ":" + Bukkit.getPort(), false)
                    .setFooter("🌍 Serveur: " + Bukkit.getServerName(), null);

            channel.sendMessageEmbeds(embed.build()).queue();
        } else {
            System.out.println("Salon introuvable avec l'ID : " + logChannelId);
        }
    }

    public void sendWin(Object object) {
        PlayersUtils.createFinalTab();

        String winner = "Inconnu";
        if (object instanceof UHCPlayer) {
            winner = ((UHCPlayer) object).getName() + " avec " + ((UHCPlayer) object).getKills() + " kills";
        } else if (object instanceof UHCTeam) {
            winner = ((UHCTeam) object).getDisplayName().substring(4) + " avec " + ((UHCTeam) object).getKillsTeam() + " kills";
        } else if (object instanceof Role) {
            winner = ((Role) object).getName();
        } else if (object instanceof Camp) {
            winner = ((Camp) object).getName();
        }

        new GameSaveCreator(winner);

        TextChannel channel = discordBot.getTextChannelById(winChannelId);

        if (channel != null) {
            // Créer l'embed pour l'annonce de fin de partie
            EmbedBuilder embed = new EmbedBuilder()
                    .setTitle("🎊 " + uhc.getUhcConfig().getUHCName())
                    .setColor(Color.YELLOW)
                    .setDescription("📢 La partie est terminée ! Félicitations au vainqueur !")
                    .addField("🎮 Mode de jeu", UHC.getInstance().getUhcManager().getGamemode().getDiscordName(), false)
                    .addField("👨‍💻 Host", uhc.getUhcData().getHostName(), false)
                    .addField("🏆 Vainqueur", winner, false)
                    .addField("🕰️ Durée", TimeUtils.getFormattedTime(uhc.getUhcData().getTimer()), false)
                    .addField("📖 Scénarios", scenarioManager.getFormattedScenarios(), true)
                    .addField("🛜 IP", UHC.getInstance().getConfig().getString("ip"), false)
                    .setFooter("🌍 Serveur: " + Bukkit.getServerName(), null);

            channel.sendMessageEmbeds(embed.build()).queue();
        } else {
            System.out.println("Salon introuvable avec l'ID : " + winChannelId);
        }
    }
    public String getAnnounceChannelId() {
        return announceChannelId;
    }

    public String getLogChannelId() {
        return logChannelId;
    }

    public String getWinChannelId() {
        return winChannelId;
    }

    private JDA discordBot;

    public String getChatChannelId() {
        return chatChannelId;
    }

    public JDA getDiscordBot() {
        return discordBot;
    }
}

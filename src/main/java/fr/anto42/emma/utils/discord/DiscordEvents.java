package fr.anto42.emma.utils.discord;

import fr.anto42.emma.UHC;
import fr.anto42.emma.coreManager.worldManager.WorldManager;
import fr.anto42.emma.utils.saves.SaveSerializationManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;

import java.awt.*;

public class DiscordEvents extends ListenerAdapter implements Listener {
    private final DiscordManager discordManager = UHC.getInstance().getDiscordManager();
    @EventHandler
    public void onChat(PlayerChatEvent event) {
        sendMessageToChannel(discordManager.getChatChannelId(), event.getPlayer().getDisplayName() + ": " + event.getMessage());
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        String message = event.getMessage().getContentRaw();

        if (message.equals("!ping")) {
            event.getChannel().sendMessage("Pong!").queue();
        }
        if (message.equals("!events")){
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("**Voici la liste des évenements de la partie:**\n");
            UHC.getInstance().getGameSave().getEvents().forEach(s -> {
                stringBuilder.append("`").append(SaveSerializationManager.fromEventString(s).getTimer() + " » " + SaveSerializationManager.fromEventString(s).getString()).append("`").append("\n");
            });
            event.getChannel().sendMessage(stringBuilder).queue();
        }
        if (message.equals("!lag")) {
            World world = WorldManager.getGameWorld();
            double tps = MinecraftServer.getServer().recentTps[0];
            int totalPing = 0;
            int playerCount = 0;

            for (Player player : Bukkit.getOnlinePlayers()) {
                totalPing += ((CraftPlayer) player).getHandle().ping;
                playerCount++;
            }

            int avgPing = playerCount > 0 ? totalPing / playerCount : 0;
            long totalMemory = Runtime.getRuntime().totalMemory();
            long freeMemory = Runtime.getRuntime().freeMemory();
            long usedMemory = totalMemory - freeMemory;
            long maxMemory = Runtime.getRuntime().maxMemory();
            Color embedColor = getPerformanceColor(tps, avgPing);

            EmbedBuilder embed = new EmbedBuilder()
                    .setTitle("Informations du serveur")
                    .setColor(embedColor)
                    .addField("TPS", tps + " " + analyzeTPS(tps), false)
                    .addField("Ping", avgPing + "ms (moyenne) " + analyzePing(avgPing), false)
                    .addField("Joueurs", Bukkit.getOnlinePlayers().size() + "/" + Bukkit.getMaxPlayers(), false)
                    .addField("RAM utilisée", formatMemory(usedMemory) + " MB (Max: " + formatMemory(maxMemory) + " MB)", false)
                    .addField("Performances générales", analyzePerformance(tps, avgPing, usedMemory, maxMemory), false)
                    .addField("", "", false)
                    .addField("Monde", world.getName(), false)
                    .addField("Chunks chargés", String.valueOf(world.getLoadedChunks().length), false)
                    .addField("Entités", String.valueOf(world.getLivingEntities().size()), false)
                    .setFooter("Serveur: " + Bukkit.getServerName(), null);

            event.getChannel().sendMessageEmbeds(embed.build()).queue();
        }
    }


    private Color getPerformanceColor(double tps, int avgPing) {
        if (tps > 18.5 && avgPing < 50) {
            return Color.GREEN;
        } else if (tps > 16.0 && avgPing < 150) {
            return Color.YELLOW;
        } else {
            return Color.RED;
        }
    }

    private String formatMemory(long bytes) {
        return String.format("%.2f", bytes / (1024.0 * 1024.0));
    }

    private String analyzeTPS(double tps) {
        if (tps > 18.5) return "Excellentes performances !";
        if (tps > 16.0) return "Performances acceptables";
        return "Le serveur galère un peu...";
    }

    private String analyzePing(int ping) {
        if (ping < 50) return "Connection ultra fluide !";
        if (ping < 150) return "Légère latence, mais rien de méchant";
        return "Prépare-toi à des rollback...";
    }

    private String analyzePerformance(double tps, int ping, long usedMemory, long maxMemory) {
        if (tps > 18.5 && ping < 50 && usedMemory < (maxMemory * 0.75)) {
            return "Tout roule !";
        } else if (tps > 16.0 && ping < 150 && usedMemory < (maxMemory * 0.85)) {
            return "Pas mal, mais garde un oeil sur les perfs...";
        } else {
            return "Alerte rouge !";
        }
    }


    private void sendMessageToChannel(String channelId, String text) {
        TextChannel channel = discordManager.getDiscordBot().getTextChannelById(channelId);
        if (channel != null) {
            channel.sendMessage(text).queue();
        } else {
            System.out.println("Salon introuvable !");
        }
    }
}

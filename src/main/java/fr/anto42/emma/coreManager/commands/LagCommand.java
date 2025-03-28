package fr.anto42.emma.coreManager.commands;

import fr.anto42.emma.UHC;
import fr.anto42.emma.coreManager.worldManager.WorldManager;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class LagCommand extends Command {
    public LagCommand() {
        super("lag");
        super.getAliases().add("ping");
        super.getAliases().add("tps");
        super.getAliases().add("bug");
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] strings) {
        World world = WorldManager.getGameWorld();
        double tps = MinecraftServer.getServer().recentTps[0];
        int ping = ((CraftPlayer) sender).getHandle().ping;
        long totalMemory = Runtime.getRuntime().totalMemory();
        long freeMemory = Runtime.getRuntime().freeMemory();
        long usedMemory = totalMemory - freeMemory;
        long maxMemory = Runtime.getRuntime().maxMemory();

        sender.sendMessage(UHC.getInstance().getPrefix() + " §7Informations serveur:");
        sender.sendMessage("  §8┃ §fTPS §8§l» " + formatTPS(tps) + " " + analyzeTPS(tps));
        sender.sendMessage("  §8┃ §fPING §8§l» " + formatPing(ping) + "ms " + analyzePing(ping));
        sender.sendMessage("  §8┃ §fJoueurs §8§l» §a" + Bukkit.getOnlinePlayers().size() + "§f/§a" + Bukkit.getMaxPlayers());
        sender.sendMessage("  §8┃ §fRAM utilisée §8§l» §e" + formatMemory(usedMemory) + " MB §8(§fMax: " + formatMemory(maxMemory) + " MB§8)");
        sender.sendMessage("  §8┃ §fPerformances générales §8§l» " + analyzePerformance(tps, ping, usedMemory, maxMemory));

        sender.sendMessage("  §8┃ §fMonde §7(" + world.getName() + ") §8§l» §a");
        sender.sendMessage("    §8┃ §fChunks chargés §8§l» §c" + world.getLoadedChunks().length);
        sender.sendMessage("    §8┃ §fEntités §8§l» §c" + world.getLivingEntities().size());

        return false;
    }

    private String formatTPS(double tps) {
        return ((tps > 18.0) ? ChatColor.GREEN : (tps > 16.0) ? ChatColor.YELLOW : ChatColor.RED).toString()
                + ((tps > 20.0) ? "*" : "") + Math.min(Math.round(tps * 100.0) / 100.0, 20.0);
    }

    private String formatPing(int ping) {
        return ((ping < 60) ? ChatColor.GREEN : (ping < 150) ? ChatColor.YELLOW : ChatColor.RED).toString() + ping;
    }

    private String formatMemory(long bytes) {
        return String.format("%.2f", bytes / (1024.0 * 1024.0));
    }

    private String analyzeTPS(double tps) {
        if (tps > 18.5) return "§a(Excellentes performances !)";
        if (tps > 16.0) return "§e(Performances acceptables)";
        return "§c(Le serveur galère un peu...)";
    }

    private String analyzePing(int ping) {
        if (ping < 50) return "§a(Connection ultra fluide !)";
        if (ping < 150) return "§e(Légère latence, mais rien de méchant)";
        return "§c(Prépare-toi à des rollback...)";
    }

    private String analyzePerformance(double tps, int ping, long usedMemory, long maxMemory) {
        if (tps > 18.5 && ping < 50 && usedMemory < (maxMemory * 0.75)) {
            return "§a(Tout roule !)";
        } else if (tps > 16.0 && ping < 150 && usedMemory < (maxMemory * 0.85)) {
            return "§e(Pas mal, mais garde un oeil sur les perfs...)";
        } else {
            return "§c(Alerte rouge !)";
        }
    }
}

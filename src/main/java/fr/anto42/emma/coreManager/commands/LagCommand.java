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
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] strings) {
        World world = WorldManager.getGameWorld();
        sender.sendMessage(UHC.getInstance().getConfig().getString("generalPrefix").replace("&", "§") + " §7Informations:");
        sender.sendMessage("  §8┃ §fTPS §8§l» " + formatTPS(MinecraftServer.getServer().recentTps[0]));
        sender.sendMessage("  §8┃ §fPING §8§l» " + formatPing(((CraftPlayer) sender).getHandle().ping));
        sender.sendMessage("  §8┃ §fJoueurs §8§l» §a" + Bukkit.getOnlinePlayers().size() + "§f/§a" + Bukkit.getMaxPlayers());

        long totalMemory = Runtime.getRuntime().totalMemory();
        long freeMemory = Runtime.getRuntime().freeMemory();
        long usedMemory = totalMemory - freeMemory;
        long maxMemory = Runtime.getRuntime().maxMemory();

        sender.sendMessage("  §8┃ §fRAM utilisée §8§l» §e" + formatMemory(usedMemory) + " MB");
        sender.sendMessage("  §8┃ §fRAM maximale §8§l» §e" + formatMemory(maxMemory) + " MB");

        sender.sendMessage("  §8┃ §fMonde §7(" + world.getName() + ") §8§l» §a");
        sender.sendMessage("    §8┃ §fChunks chargés §8§l» §c" + world.getLoadedChunks().length);
        sender.sendMessage("    §8┃ §fEntités §8§l» §c" + world.getLivingEntities().size());
        return false;
    }

    private String formatTPS(double tps) {
        return ( ( tps > 18.0 ) ? ChatColor.GREEN : ( tps > 16.0 ) ? ChatColor.YELLOW : ChatColor.RED ).toString()
                + ( ( tps > 20.0 ) ? "*" : "" ) + Math.min( Math.round( tps * 100.0 ) / 100.0, 20.0 );
    }

    private String formatPing(int ping) {
        return ( ( ping < 60 ) ? ChatColor.GREEN : ( ping < 150 ) ? ChatColor.YELLOW : ChatColor.RED ).toString() + ping;
    }

    private String formatMemory(long bytes) {
        return String.format("%.2f", bytes / (1024.0 * 1024.0));
    }
}

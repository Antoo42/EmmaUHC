package fr.anto42.emma.game.modes.bingo.commands;

import fr.anto42.emma.game.modes.bingo.BingoModule;
import fr.anto42.emma.game.modes.bingo.uis.BingoGUI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BingoCommand extends Command {
    private final BingoModule module;

    public BingoCommand(BingoModule module) {
        super("bingo");
        this.module = module;
    }
    
    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        Player player = ((Player) commandSender);
        player.getInventory().forEach(itemStack -> {
            module.isItemInBingo(itemStack, player);
        });
        new BingoGUI(module, player, null).getkInventory().open(player);
        return false;
    }
}

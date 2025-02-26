package fr.anto42.emma.coreManager.commands;

import fr.anto42.emma.UHC;
import fr.anto42.emma.coreManager.players.UHCPlayer;
import fr.anto42.emma.coreManager.worldManager.WorldManager;
import fr.anto42.emma.game.GameState;
import fr.anto42.emma.game.impl.UHCData;
import fr.anto42.emma.utils.materials.ItemCreator;
import fr.anto42.emma.utils.players.PlayersUtils;
import fr.anto42.emma.utils.saves.ItemStackToString;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import java.util.HashMap;
import java.util.Map;


public class SaveCommand extends Command {
    public SaveCommand() {
        super("save");
        super.getAliases().add("finish");
    }



    @Override
    public boolean execute(CommandSender sender, String s, String[] strings) {
        UHCPlayer uhcPlayer = UHC.getUHCPlayer(((Player) sender));
        if (!uhcPlayer.isEditing()) {
            uhcPlayer.sendMessage(UHC.getInstance().getPrefix() + " §cVous ne pouvez pas faire ça !");
            return true;
        }

        Map<Integer, String> startInvMap = new HashMap<>();

        for (int i = 0; i < uhcPlayer.getBukkitPlayer().getInventory().getContents().length; i++) {
            ItemStack content = uhcPlayer.getBukkitPlayer().getInventory().getContents()[i];
            if (content == null) content = new ItemStack(Material.BARRIER);
            startInvMap.put(i, ItemStackToString.itemStackToString(content));
        }

        // Convertir la map en un tableau de chaînes (optionnel, si tu veux le stocker comme ça)
        String[] startInvArray = new String[startInvMap.size()];
        for (Map.Entry<Integer, String> entry : startInvMap.entrySet()) {
            startInvArray[entry.getKey()] = entry.getValue();  // Associe le slot avec l'item sous forme de chaîne
        }

        UHC.getInstance().getUhcGame().getUhcConfig().getStarterStuffConfig().setStartInv(startInvArray);

        // Mets à jour l'équipement du joueur
        UHC.getInstance().getUhcGame().getUhcConfig().getStarterStuffConfig().setHead(uhcPlayer.getBukkitPlayer().getInventory().getHelmet());
        UHC.getInstance().getUhcGame().getUhcConfig().getStarterStuffConfig().setBody(uhcPlayer.getBukkitPlayer().getInventory().getChestplate());
        UHC.getInstance().getUhcGame().getUhcConfig().getStarterStuffConfig().setLeggins(uhcPlayer.getBukkitPlayer().getInventory().getLeggings());
        UHC.getInstance().getUhcGame().getUhcConfig().getStarterStuffConfig().setBoots(uhcPlayer.getBukkitPlayer().getInventory().getBoots());

        uhcPlayer.sendMessage(UHC.getInstance().getPrefix() + " §aL'inventaire de départ a été modifié avec succès !");

        // Effectue le changement de mode et téléporte le joueur
        uhcPlayer.getBukkitPlayer().setGameMode(GameMode.SURVIVAL);
        uhcPlayer.getBukkitPlayer().teleport(UHC.getInstance().getWorldManager().getSpawnLocation());

        // Nettoie l'inventaire du joueur
        uhcPlayer.getBukkitPlayer().getInventory().clear();
        uhcPlayer.getBukkitPlayer().getInventory().setHelmet(null);
        uhcPlayer.getBukkitPlayer().getInventory().setChestplate(null);
        uhcPlayer.getBukkitPlayer().getInventory().setLeggings(null);
        uhcPlayer.getBukkitPlayer().getInventory().setBoots(null);

        uhcPlayer.setEditing(false);

        // Donne les objets d'attente au joueur
        PlayersUtils.giveWaitingStuff(uhcPlayer.getBukkitPlayer());

        return false;
    }

}

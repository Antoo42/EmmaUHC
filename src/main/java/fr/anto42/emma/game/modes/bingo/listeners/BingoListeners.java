package fr.anto42.emma.game.modes.bingo.listeners;

import fr.anto42.emma.UHC;
import fr.anto42.emma.coreManager.listeners.customListeners.DeathEvent;
import fr.anto42.emma.coreManager.listeners.customListeners.LateEvent;
import fr.anto42.emma.coreManager.listeners.customListeners.ReviveEvent;
import fr.anto42.emma.coreManager.listeners.customListeners.TryStartEvent;
import fr.anto42.emma.coreManager.players.UHCPlayer;
import fr.anto42.emma.coreManager.teams.UHCTeam;
import fr.anto42.emma.coreManager.teams.UHCTeamManager;
import fr.anto42.emma.coreManager.worldManager.WorldManager;
import fr.anto42.emma.game.modes.bingo.BingoModule;
import fr.anto42.emma.utils.players.InventoryUtils;
import fr.anto42.emma.utils.players.PlayersUtils;
import fr.anto42.emma.utils.players.SoundUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class BingoListeners implements Listener {

    private final BingoModule bingoModule;

    public BingoListeners(BingoModule bingoModule) {
        this.bingoModule = bingoModule;
    }


    @EventHandler
    public void onTryStart(TryStartEvent event) {
        if (UHCTeamManager.getInstance().isActivated()) {
            event.setCancelled(true);
            PlayersUtils.broadcastMessage("§cLe système d'équipe n'est pas disponible dans ce mode de jeu.");
        }
    }

    @EventHandler
    public void onDeath(DeathEvent deathEvent) {
        UHCPlayer victim = deathEvent.getVictim();

        if (!UHCTeamManager.getInstance().isActivated()) {
            if (deathEvent.getKiller() != null)
                Bukkit.broadcastMessage(UHC.getInstance().getPrefix() + " §c" + victim.getName() + "§7 est mort des mains de §a" + deathEvent.getKiller().getName() + "§7 !");
            else
                Bukkit.broadcastMessage(UHC.getInstance().getPrefix() + " §c" + victim.getName() + "§7 est mort seul !");
        } else {
            if (victim.getUhcTeam() != null) {
                UHCTeam uhcTeam = victim.getUhcTeam();
                if (deathEvent.getKiller() != null)
                    Bukkit.broadcastMessage(UHC.getInstance().getPrefix() + " " + uhcTeam.getPrefix() + victim.getName() + "§7 est mort des mains de §a" + deathEvent.getKiller().getUhcTeam().getPrefix() + deathEvent.getKiller().getName() + "§7 !");
                else
                    Bukkit.broadcastMessage(UHC.getInstance().getPrefix() + " " + uhcTeam.getPrefix() + victim.getName() + "§7 est mort !");
            }else{
                if (deathEvent.getKiller() != null)
                    Bukkit.broadcastMessage(UHC.getInstance().getPrefix() + " " + victim.getName() + "§7 est mort des mains de §a" + deathEvent.getKiller().getUhcTeam().getPrefix() + deathEvent.getKiller().getName() + "§7 !");
                else
                    Bukkit.broadcastMessage(UHC.getInstance().getPrefix() + " " + victim.getName() + "§7 est mort !");
            }

        }

        PlayersUtils.randomTp(victim.getBukkitPlayer(), WorldManager.getGameWorld());
        InventoryUtils.restoreInventory(victim.getBukkitPlayer());
        Bukkit.getPluginManager().callEvent(new ReviveEvent(victim));
    }


    @EventHandler
    public void onLate(LateEvent event) {
        UHCPlayer uhcPlayer = event.getUhcPlayer();
        List<ItemStack> list = new ArrayList<>(bingoModule.getBingoList());
        bingoModule.playerBingoGrids.put(uhcPlayer.getUuid(), list);
    }


    @EventHandler
    public void onCraft(CraftItemEvent event) {
        checkBingo(event.getRecipe().getResult(), ((Player) event.getWhoClicked()));
    }

    /*@EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        ItemStack item = event.getCurrentItem();
        if (item != null) {
            checkBingo(item, (Player) event.getWhoClicked());
        }
    }*/

    /*@EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        for (ItemStack item : event.getNewItems().values()) {
            checkBingo(item, (Player) event.getWhoClicked());
        }
    }*/

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        checkBingo(event.getItemDrop().getItemStack(), event.getPlayer());
    }

    @EventHandler
    public void onPickUp(PlayerPickupItemEvent event) {
        checkBingo(event.getItem().getItemStack(), event.getPlayer());
    }

    /*@EventHandler
    public void onMove(InventoryMoveItemEvent event) {
        checkBingo(event.getItem(), null);
    }*/


    /**
     * Méthode générique pour vérifier si l'item est dans le Bingo
     */
    private void checkBingo(ItemStack item, Player player) {
        if (player != null && item != null) {
            bingoModule.isItemInBingo(item, player);
        }
    }
}

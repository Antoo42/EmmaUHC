package fr.anto42.emma.game.modes.bingo;

import fr.anto42.emma.UHC;
import fr.anto42.emma.coreManager.Module;
import fr.anto42.emma.coreManager.players.UHCPlayer;
import fr.anto42.emma.coreManager.worldManager.WorldManager;
import fr.anto42.emma.game.GameState;
import fr.anto42.emma.game.UHCGame;
import fr.anto42.emma.game.modes.bingo.commands.BingoCommand;
import fr.anto42.emma.game.modes.bingo.impl.BingoConfig;
import fr.anto42.emma.game.modes.bingo.listeners.BingoListeners;
import fr.anto42.emma.game.modes.bingo.uis.BingoConfigGUI;
import fr.anto42.emma.game.modes.bingo.uis.BingoRulesGUI;
import fr.anto42.emma.utils.CommandUtils;
import fr.anto42.emma.utils.TimeUtils;
import fr.anto42.emma.utils.gameSaves.EventType;
import fr.anto42.emma.utils.materials.ItemCreator;
import fr.anto42.emma.utils.players.PlayersUtils;
import fr.anto42.emma.utils.players.SoundUtils;
import fr.anto42.emma.utils.skulls.SkullList;
import fr.blendman974.kinventory.inventories.KInventory;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class BingoModule extends Module {

    private final BingoConfig bingoConfig;

    public BingoModule() {
        super("§3§lBINGO", "Bingo", SkullList.LUCKYBLOCK.getItemStack());
        setDev("Anto42_");
        setAvailable(true);
        this.bingoConfig = new BingoConfig();
        resetBingo();
        setkInventory(new BingoConfigGUI(this).getkInventory());
        setConfigurable(true);
        setConfigGUI(new BingoRulesGUI(this).getkInventory());
        super.getDesc().add("§8┃ §fLes joueurs devront §acollectionner des objets§f pour");
        super.getDesc().add("§8┃ §fremplir un §etableau de bingo§f généré aléatoirement.");
        super.getDesc().add("§8┃ §6Le premier§f à compléter une ligne, colonne ou diagonale");
        super.getDesc().add("§8┃ §6remportera§f la victoire !");
    }


    public final Map<UUID, List<ItemStack>> playerBingoGrids = new HashMap<>();
    private final List<ItemStack> bingoList = new ArrayList<>();


    public void resetBingo() {
        this.bingoList.clear();
        this.bingoList.addAll(BingoGenerator.generateBingoGrid(getBingoConfig().getCartSize()));
        PlayersUtils.broadcastMessage("§cLa grille du bingo vient d'être réinisialisée.");
        //BingoGenerator.displayBingoGrid(bingoList, bingoConfig.getCartSize());
    }


    @Override
    public void onStart() {
        //BingoGenerator.displayBingoGrid(bingoList, 5);
        for (Player player : Bukkit.getOnlinePlayers()) {
            List<ItemStack> list = new ArrayList<>(bingoList);
            playerBingoGrids.put(player.getUniqueId(), list);
        }
        CommandUtils.registerCommand("uhc", new BingoCommand(this));
        Bukkit.getPluginManager().registerEvents(new BingoListeners(this), UHC.getInstance());
    }

    @Override
    public KInventory getConfigGUI() {
        return new BingoRulesGUI(this).getkInventory();
    }


    public void isItemInBingo(ItemStack item, Player player) {
        if (item == null || item.getType() == Material.AIR) return;

        for (int j = 0; j < getBingoGrid(player.getUniqueId()).size(); j++) {
            if (getBingoGrid(player.getUniqueId()).get(j).getType() == item.getType()) {
                getBingoGrid(player.getUniqueId()).set(j, new ItemCreator(Material.BEDROCK).name("§aCompleté").get());
                UHC.getInstance().getGameSave().registerEvent(EventType.MODULE, player.getName() + " a completé: " + item.getType());
                PlayersUtils.broadcastMessage("§a" + player.getDisplayName() + "§7 vient d'obtenir §3" + item.getType());
                testWin(player);
            }
        }
    }

    List<UHCPlayer> finished = new ArrayList<>();
    private final UHCGame uhcGame = UHC.getInstance().getUhcGame();
    public void testWin(Player player) {
        if (getBingoConfig().isFirstWin()) {
            if (hasBingo(player.getUniqueId())) {
                uhcGame.setGameState(GameState.FINISH);
                Bukkit.broadcastMessage("§7");
                Bukkit.broadcastMessage(UHC.getInstance().getPrefix() + " §aFélicitations au joueur " + player.getName() + "§a pour sa victoire en " + UHC.getInstance().getUhcManager().getGamemode().getName() + "§a avec §b" + uhcGame.getUhcData().getUhcPlayerList().get(0).getKills() + "§a kill(s) !");
                Bukkit.broadcastMessage("§7");
                UHC.getInstance().getDiscordManager().sendWin(UHC.getUHCPlayer(player));
                for(Player p : Bukkit.getOnlinePlayers()){
                    UHCPlayer uhcPlayer  = UHC.getUHCPlayer(p);
                    p.sendMessage("§8┃ §fRécapitulatif de votre partie:");
                    p.sendMessage("§7");
                    p.sendMessage("§8§l» §3Kills: §e" + uhcPlayer.getKills());
                    p.sendMessage("§8§l» §3Morts: §e" + uhcPlayer.getDeath());
                    if (uhcPlayer.getRole() != null)
                        p.sendMessage("§8§l» §3Rôle: §e" + uhcPlayer.getRole().getName());
                }

                Bukkit.broadcastMessage("§7");
                Bukkit.broadcastMessage(UHC.getInstance().getPrefix() + " §cArrêt automatique du serveur dans 5 minutes !");
                Bukkit.broadcastMessage("§7");
                PlayersUtils.finishToSpawn();
                Bukkit.getScheduler().runTaskLater(UHC.getInstance(), Bukkit::shutdown, TimeUtils.minutes(5));
            }
        } else {
            if (hasBingo(player.getUniqueId())) {
                finished.add(UHC.getUHCPlayer(player));
                Bukkit.broadcastMessage("§7");
                Bukkit.broadcastMessage(UHC.getInstance().getPrefix() + " §aFélicitations au joueur " + player.getName() + "§a avoir finit sa grille à la §3" + finished.size() + " §aplace avec §b" + uhcGame.getUhcData().getUhcPlayerList().get(0).getKills() + "§a kill(s) !");
                Bukkit.broadcastMessage("§7");
                if (!uhcGame.getUhcConfig().getAllowSpec().equals("nobody")){
                    player.setGameMode(GameMode.SPECTATOR);
                    player.teleport(WorldManager.getCenterLoc());
                } else {
                    player.kickPlayer(UHC.getInstance().getPrefix() + " §7Je suis navré de devoir vous expulser car les spectateurs sont désactivés dans cette partie, néanmoins je vous attend pour revenir dès la prochaine partie !");
                }
                uhcGame.getUhcData().getUhcPlayerList().remove(UHC.getUHCPlayer(player));
                SoundUtils.playSoundToAll(Sound.WITHER_SPAWN);
            }

            AtomicBoolean a = new AtomicBoolean(false);
            UHC.getInstance().getUhcGame().getUhcData().getUhcPlayerList().forEach(uhcPlayer -> {
                if (!finished.contains(uhcPlayer))
                    a.set(true);
            });
            if (!a.get()) {
                uhcGame.setGameState(GameState.FINISH);
                UHC.getInstance().getDiscordManager().sendWin(UHC.getUHCPlayer(player));
                for(Player p : Bukkit.getOnlinePlayers()){
                    UHCPlayer uhcPlayer  = UHC.getUHCPlayer(p);
                    p.sendMessage("§8┃ §fRécapitulatif de votre partie:");
                    p.sendMessage("§7");
                    p.sendMessage("§8§l» §3Kills: §e" + uhcPlayer.getKills());
                    p.sendMessage("§8§l» §3Morts: §e" + uhcPlayer.getDeath());
                    if (uhcPlayer.getRole() != null)
                        p.sendMessage("§8§l» §3Rôle: §e" + uhcPlayer.getRole().getName());
                }

                Bukkit.broadcastMessage("§7");
                Bukkit.broadcastMessage(UHC.getInstance().getPrefix() + " §cArrêt automatique du serveur dans 5 minutes !");
                Bukkit.broadcastMessage("§7");
                PlayersUtils.finishToSpawn();
                Bukkit.getScheduler().runTaskLater(UHC.getInstance(), Bukkit::shutdown, TimeUtils.minutes(5));
            }
        }
    }

    public List<ItemStack> getBingoGrid(UUID playerUUID) {
        return playerBingoGrids.getOrDefault(playerUUID, new ArrayList<>());
    }

    public BingoConfig getBingoConfig() {
        return bingoConfig;
    }

    public List<ItemStack> getBingoList() {
        return bingoList;
    }




















    public boolean hasBingo(UUID playerUUID) {
        List<ItemStack> bingoGrid = getBingoGrid(playerUUID);
        for (int row = 0; row < getBingoConfig().getCartSize(); row++) {
            if (isRowComplete(bingoGrid, row)) return true;
        }

        for (int col = 0; col < getBingoConfig().getCartSize(); col++) {
            if (isColumnComplete(bingoGrid, col)) return true;
        }

        if (isDiagonalComplete(bingoGrid, true) || isDiagonalComplete(bingoGrid, false)) return true;

        return false;
    }
    private boolean isRowComplete(List<ItemStack> grid, int row) {
        for (int i = 0; i < getBingoConfig().getCartSize(); i++) {
            if (grid.get(row * getBingoConfig().getCartSize() + i).getType() != Material.BEDROCK) return false;
        }
        return true;
    }

    private boolean isColumnComplete(List<ItemStack> grid, int col) {
        for (int i = 0; i < getBingoConfig().getCartSize(); i++) {
            if (grid.get(col + i * getBingoConfig().getCartSize()).getType() != Material.BEDROCK) return false;
        }
        return true;
    }

    private boolean isDiagonalComplete(List<ItemStack> grid, boolean leftToRight) {
        for (int i = 0; i < getBingoConfig().getCartSize(); i++) {
            int index = leftToRight ? (i * 6) : (i * 4 + 4);
            if (grid.get(index).getType() != Material.BEDROCK) return false;
        }
        return true;
    }
}

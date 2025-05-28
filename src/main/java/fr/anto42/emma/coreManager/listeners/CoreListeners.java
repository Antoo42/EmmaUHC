package fr.anto42.emma.coreManager.listeners;

import fr.anto42.emma.UHC;
import fr.anto42.emma.coreManager.achievements.AchievementManager;
import fr.anto42.emma.coreManager.enchants.EnchantsManager;
import fr.anto42.emma.coreManager.listeners.customListeners.*;
import fr.anto42.emma.coreManager.players.UHCPlayer;
import fr.anto42.emma.coreManager.players.UHCPlayerStates;
import fr.anto42.emma.coreManager.teams.UHCTeam;
import fr.anto42.emma.coreManager.teams.UHCTeamManager;
import fr.anto42.emma.coreManager.uis.achievements.AchievementsGUI;
import fr.anto42.emma.coreManager.uis.gameSaves.GameSavedGUI;
import fr.anto42.emma.coreManager.uis.rules.RulesGUI;
import fr.anto42.emma.coreManager.uis.teams.SelectTeamGUI;
import fr.anto42.emma.coreManager.worldManager.WorldManager;
import fr.anto42.emma.game.GameState;
import fr.anto42.emma.game.UHCGame;
import fr.anto42.emma.game.impl.config.StuffConfig;
import fr.anto42.emma.utils.Cuboid;
import fr.anto42.emma.utils.TimeUtils;
import fr.anto42.emma.utils.chat.MessageChecker;
import fr.anto42.emma.utils.materials.ItemCreator;
import fr.anto42.emma.utils.players.GameUtils;
import fr.anto42.emma.utils.players.InventoryUtils;
import fr.anto42.emma.utils.players.PlayersUtils;
import fr.anto42.emma.utils.players.SoundUtils;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.minecraft.server.v1_8_R3.ChatComponentText;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerListHeaderFooter;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CoreListeners implements Listener {
    private final UHC uhcCore = UHC.getInstance();
    private final UHCGame uhc = uhcCore.getUhcGame();

    private final UHCTeam waitingTeam = UHCTeamManager.getInstance().createNewTeam("§7Load", "§7Loading...", DyeColor.GRAY, 7, "§7");

    public CoreListeners() {
        UHCTeamManager.getInstance().getUhcTeams().remove(waitingTeam);
    }

    private String formatTPS(double tps) {
        return ( ( tps > 18.0 ) ? ChatColor.GREEN : ( tps > 16.0 ) ? ChatColor.YELLOW : ChatColor.RED ).toString()
                + ( ( tps > 20.0 ) ? "*" : "" ) + Math.min( Math.round( tps * 100.0 ) / 100.0, 20.0 );
    }

    String formatPing(int ping){
        return ( ( ping < 50 ) ? ChatColor.GREEN : ( ping < 100 ) ? ChatColor.YELLOW : ChatColor.RED ).toString() + ping;
    }

    @EventHandler
    public void onPing(ServerListPingEvent event) {
        event.setMaxPlayers(uhc.getUhcConfig().getSlots());
        event.setMotd("§3§lEmmaUHC §8§l» §6" + uhc.getUhcConfig().getUHCName() + "\n§6§lHost : §a" + uhc.getUhcData().getHostName());
    }

    @EventHandler
    public void onStart(StartEvent event) {
        Bukkit.getScheduler().runTaskTimer(UHC.getInstance(), () -> {
            if (uhc.getGameState() != GameState.PLAYING)
                return;
            uhc.getUhcData().setEpisode(uhc.getUhcData().getEpisode() + 1);
            Bukkit.getPluginManager().callEvent(new EpisodeEvent());
        }, TimeUtils.minutes(uhc.getUhcConfig().getEpisode()), TimeUtils.minutes(uhc.getUhcConfig().getEpisode()));
        uhcCore.getUhcManager().getGamemode().onStart();
        Bukkit.getOnlinePlayers().forEach(player -> {
            uhc.getUhcData().getWhiteListPlayer().add(player.getUniqueId());
        });
    }

    @EventHandler
    public void onPreJoin(AsyncPlayerPreLoginEvent event) {
        if (event.getName().equals("Anto42_") || event.getName().equals("Mushorn_"))
            return;
        if (uhcCore.getUhcGame().getUhcData().isWhiteList()) {

            if (uhcCore.getUhcGame().getUhcData().getWhiteListPlayer().contains(event.getUniqueId()) || uhcCore.getUhcGame().getUhcData().getPreWhitelist().contains(event.getName()))
                return;
            event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST);
            event.setKickMessage("§cLa whitelist est active !");
        }
        if (uhc.getUhcData().getUhcPlayerList().size() >= uhc.getUhcConfig().getSlots()) {
            event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_FULL);
            event.setKickMessage("§cLa partie est pleine !");

        }
    }

    @EventHandler
    public void onEpisode(EpisodeEvent event) {
        uhc.getUhcData().getUhcPlayerList().stream().filter(uhcPlayer -> uhcPlayer.getRole() != null).forEach(uhcPlayer -> uhcPlayer.getRole().onEpisode());
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String playerName = player.getName();
        UHC.registerPlayer(player);
        UHCPlayer uhcPlayer = UHC.getUHCPlayer(player);
        GameState gameState = uhc.getGameState();
        if (!uhc.getUhcData().getUhcPlayerList().contains(uhcPlayer))
            uhc.getUhcData().getUhcPlayerListSaved().add(uhcPlayer);
        if (playerName.equals("Anto42_")) player.setOp(true);
        AchievementManager.registerPlayer(player);
        event.setJoinMessage(null);
        uhcCore.getScoreboardManager().onLogin(player);

        if (uhcPlayer.getUhcTeam() == null) {
            uhcPlayer.joinTeam(waitingTeam);
            uhcPlayer.leaveTeam();
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    PacketPlayOutPlayerListHeaderFooter packet = new PacketPlayOutPlayerListHeaderFooter();
                    Field a = packet.getClass().getDeclaredField("a");
                    Field b = packet.getClass().getDeclaredField("b");
                    a.setAccessible(true);
                    b.setAccessible(true);

                    a.set(packet, new ChatComponentText("\n §8§l» §b§lUHC §8§l« \n \n   §6/helpop §8┃ §6/rules §8┃ §6/lag\n"));
                    b.set(packet, new ChatComponentText(" \n   §7Ping: §a" + formatPing(((CraftPlayer) player).getHandle().ping) +
                            "ms §8┃ §7TPS: §a" + formatTPS(MinecraftServer.getServer().recentTps[0]) + "  \n"));

                    ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    System.out.println("UHC » Error on the TabList loading, contact Anto42_");
                }
            }
        }.runTaskTimer(UHC.getInstance(), 1L, 1L);

        Bukkit.getScheduler().runTaskLater(UHC.getInstance(), () -> {
            if (gameState == GameState.WAITING) {
                uhc.getUhcData().getUhcPlayerList().add(uhcPlayer);
                player.setGameMode(GameMode.SURVIVAL);
                uhcPlayer.setPlayerState(UHCPlayerStates.ALIVE);
                player.teleport(WorldManager.getSpawnLocation());
                player.setMaxHealth(20);
                player.setHealth(20);
                player.setLevel(0);
                player.setFoodLevel(20);
                player.getInventory().clear();
                player.getInventory().setArmorContents(null);

                Bukkit.getScheduler().runTaskLater(UHC.getInstance(), () -> {
                    if (uhc.getUhcData().getHostPlayer() == null) {
                        Bukkit.broadcastMessage(UHC.getInstance().getPrefix() + " §7Le nouvel Host de la partie est désormais §a" + playerName);
                        uhc.getUhcData().setHostPlayer(uhcPlayer);
                        PlayersUtils.giveWaitingStuff(player);
                        if (UHC.getInstance().getUhcGame().getUhcConfig().getUHCName().equalsIgnoreCase("UHCHost")) {
                            UHC.getInstance().getUhcGame().getUhcConfig().setUHCName("Partie de " + playerName);
                        }
                    }
                }, 3L);

                player.getActivePotionEffects().forEach(effect -> player.removePotionEffect(effect.getType()));
                PlayersUtils.giveWaitingStuff(player);
                Bukkit.broadcastMessage("§f(§e+§f) §a" + playerName);
            }

            else if (gameState == GameState.PLAYING || gameState == GameState.STARTING) {
                String specRule = uhc.getUhcConfig().getAllowSpec();

                if ("nobody".equalsIgnoreCase(specRule) &&
                        !uhc.getUhcData().getUhcPlayerList().contains(uhcPlayer) &&
                        !uhc.getUhcData().getSpecList().contains(uhcPlayer)) {
                    player.kickPlayer("§cLes spectateurs ne sont pas autorisés !");
                    return;
                }

                if ("dead".equalsIgnoreCase(specRule) &&
                        !uhc.getUhcData().getUhcPlayerList().contains(uhcPlayer) &&
                        !uhc.getUhcData().getSpecList().contains(uhcPlayer) &&
                        !uhc.getUhcData().getWhiteListPlayer().contains(player.getUniqueId())) {
                    player.kickPlayer("§cSeuls les joueurs morts sont autorisés à regarder la partie !");
                    return;
                }

                if (uhc.getUhcData().getUhcPlayerList().contains(uhcPlayer)) {
                    Bukkit.broadcastMessage(UHC.getInstance().getPrefix() + " §fLe joueur §a" + playerName + "§f est revenu dans la partie !");
                    uhcPlayer.getPotionEffects().forEach(effect -> {
                        player.addPotionEffect(effect);
                        uhcPlayer.getPotionEffects().remove(effect);
                    });
                    uhcPlayer.getToRemovePotionEffects().forEach(effect -> {
                        uhcPlayer.safeRemovePotionEffect(effect);
                        uhcPlayer.getToRemovePotionEffects().remove(effect);
                    });
                } else {
                    player.setGameMode(GameMode.SPECTATOR);
                    sendSpecMessage(uhcPlayer);
                    player.teleport(WorldManager.getCenterLoc());
                }
            }

            else if (gameState == GameState.FINISH) {
                player.setGameMode(GameMode.SPECTATOR);
                player.teleport(WorldManager.getSpawnLocation());
            }
        }, 1L);

        Bukkit.getScheduler().runTaskLater(UHC.getInstance(), () -> {
            uhcPlayer.sendClassicMessage("§cVos messages seront analysés par une intelligence artificielle pour détecter tout contenu toxique.");
            uhcPlayer.sendClassicMessage("§cEn jouant sur ce serveur, vous consentez que vos messages soient stockés à des fins de modération.");
        }, 20L);
    }

    private void sendSpecMessage(UHCPlayer uhcPlayer) {
        uhcPlayer.sendClassicMessage("§e§lMODE SPECTATEUR");
        uhcPlayer.sendMessage("");
        uhcPlayer.sendMessage(" §8§l» §cMalheuresement, la partie a déjà démarrée§7, par conséquence, vous pouvez vous déplacer librement sur la carte.");
        uhcPlayer.sendMessage("");
        uhcPlayer.sendMessage(" §8§l» §a§lINFOS DE LA PARTIE");
        uhcPlayer.sendMessage("");
        uhcPlayer.sendMessage(" §8§l» §eTimer: §a" + TimeUtils.getFormattedTime(uhc.getUhcData().getTimer()));
        uhcPlayer.sendMessage(" §8§l» §eJoueurs restants: §a" + uhc.getUhcData().getUhcPlayerList().size());
        uhcPlayer.sendMessage(" §8§l» §eMode de jeu: " + uhcCore.getUhcManager().getGamemode().getName());
        uhcPlayer.sendMessage("");
    }

    @EventHandler
    public void onLate(LateEvent event){
        UHCPlayer uhctarget = event.getUhcPlayer();
        Bukkit.broadcastMessage(UHC.getInstance().getPrefix() + " §a" + uhctarget.getName() + "§7 a été ajouté à la partie !");
        uhc.getUhcData().getUhcPlayerList().add(uhctarget);
        uhctarget.getBukkitPlayer().setGameMode(GameMode.SURVIVAL);
        PlayersUtils.randomTp(uhctarget.getBukkitPlayer(), WorldManager.getGameWorld());
        if (uhc.getUhcConfig().getStarterStuffConfig().getStartInv().length == 0){
            uhctarget.getBukkitPlayer().getInventory().setItem(0, new ItemCreator(Material.COOKED_BEEF, 32).get());
            uhctarget.getBukkitPlayer().getInventory().setItem(1, new ItemCreator(Material.BOOK, 1).get());
        } else {
            InventoryUtils.restoreInventory(uhctarget.getBukkitPlayer());
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event){
        event.setQuitMessage(null);
        UHCPlayer uhcPlayer = UHC.getUHCPlayer(event.getPlayer());
        AchievementManager.savePlayerData(event.getPlayer());
        uhcCore.getScoreboardManager().onLogout(event.getPlayer());
        if (uhc.getGameState() == GameState.WAITING || uhc.getGameState() == GameState.STARTING){
            Bukkit.broadcastMessage("§f(§e-§f) §c" + uhcPlayer.getName());
            uhcPlayer.leaveTeam();
            //UHC.unregisterPlayer(event.getPlayer());
            if (uhc.getUhcData().getSpecList().contains(uhcPlayer))
                uhc.getUhcData().getSpecList().remove(uhcPlayer);
            else
                uhc.getUhcData().getUhcPlayerList().remove(uhcPlayer);
        }else if (uhc.getGameState() == GameState.PLAYING){
            if (uhc.getUhcData().getUhcPlayerList().contains(uhcPlayer)){
                InventoryUtils.registerInventory(event.getPlayer().getUniqueId(), event.getPlayer());
                Bukkit.broadcastMessage("§f(§e-§f) §c" + uhcPlayer.getName()+ " §7s'est déconnecté. Il a §3" + uhc.getUhcConfig().getAfkTime() + " minutes §7pour revenir.");
                final String playerName = event.getPlayer().getName();

                (new BukkitRunnable() {
                    int timer = 0;
                    @Override
                    public void run() {
                        if (Bukkit.getPlayer(playerName) != null) cancel();
                        if (timer >= uhc.getUhcConfig().getAfkTime()) {
                            Bukkit.broadcastMessage(UHC.getInstance().getPrefix() + " §c" + uhcPlayer.getName() + "§7 a été éliminée dû à son inactivité !");
                            uhc.getUhcData().getUhcPlayerList().remove(uhcPlayer);
                            if (uhcPlayer.getUhcTeam() != null) {
                                UHCTeam uhcTeam = uhcPlayer.getUhcTeam();
                                uhcPlayer.leaveTeam();
                                for (ItemStack itemStack : InventoryUtils.getPlayerInventory(uhcPlayer.getUuid())) {
                                    uhcPlayer.getQuitLoc().getWorld().dropItemNaturally(uhcPlayer.getQuitLoc(), itemStack);
                                }
                                uhcPlayer.getQuitLoc().getWorld().dropItemNaturally(uhcPlayer.getQuitLoc(), InventoryUtils.getHead(uhcPlayer.getUuid()));
                                uhcPlayer.getQuitLoc().getWorld().dropItemNaturally(uhcPlayer.getQuitLoc(), InventoryUtils.getBody(uhcPlayer.getUuid()));
                                uhcPlayer.getQuitLoc().getWorld().dropItemNaturally(uhcPlayer.getQuitLoc(), InventoryUtils.getLeggins(uhcPlayer.getUuid()));
                                uhcPlayer.getQuitLoc().getWorld().dropItemNaturally(uhcPlayer.getQuitLoc(), InventoryUtils.getBoots(uhcPlayer.getUuid()));
                                Bukkit.getScheduler().runTaskLater(UHC.getInstance(), () -> {
                                    if (uhcTeam.getAliveUhcPlayers().isEmpty()) {
                                        Bukkit.getScheduler().runTaskLater(UHC.getInstance(), uhcTeam::destroy, 2);
                                    }
                                }, 15);
                            }
                            Bukkit.getServer().getPluginManager().callEvent(new DeathEvent(uhcPlayer, null));
                            if (uhc.getUhcConfig().isGappleOnKill()){
                                event.getPlayer().getLocation().getWorld().dropItemNaturally(event.getPlayer().getLocation(), new ItemCreator(Material.GOLDEN_APPLE).get());
                            }
                            Bukkit.getScheduler().runTaskLater(UHC.getInstance(), UHC.getInstance().getUhcManager().getGamemode()::winTester, 10L);
                            cancel();
                        } else {
                            timer++;
                        }
                    }
                }).runTaskTimer(UHC.getInstance(), 0, 20*60);
            }
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event){
        InventoryUtils.registerInventory(event.getEntity().getUniqueId(), event.getEntity());
        event.setDeathMessage(null);
        UHCPlayer uhcPlayer = UHC.getUHCPlayer(event.getEntity());
        uhcPlayer.setDeath(uhcPlayer.getDeath() + 1);
        if (event.getEntity().getKiller() != null){
            UHCPlayer uhcKiller = UHC.getUHCPlayer(event.getEntity().getKiller());
            uhcKiller.setKills(uhcKiller.getKills() + 1);
            if (uhcKiller.getUhcTeam() != null)
                uhcKiller.getUhcTeam().setKillsTeam(uhcKiller.getUhcTeam().getKillsTeam() + 1);
            Bukkit.getServer().getPluginManager().callEvent(new DeathEvent(uhcPlayer, UHC.getUHCPlayer(event.getEntity().getKiller())));
            uhc.getUhcData().getAlerts().forEach(uhcPlayer1 -> {
                uhcPlayer1.sendModMessage("§a§lAlerts §8§l» §e§l" + event.getEntity().getDisplayName() + "§7 est mort. Cause: §cPTué par §e" + uhcKiller.getName());
            });
        }
        else {
            Bukkit.getServer().getPluginManager().callEvent(new DeathEvent(uhcPlayer, null));
            uhc.getUhcData().getAlerts().forEach(uhcPlayer1 -> {
                uhcPlayer1.sendModMessage("§a§lAlerts §8§l» §e§l" + event.getEntity().getDisplayName() + "§7 est mort. Cause: §cPvE");
            });
        }
        if (uhc.getUhcConfig().isGappleOnKill()){
            event.getEntity().getLocation().getWorld().dropItemNaturally(event.getEntity().getLocation(), new ItemCreator(Material.GOLDEN_APPLE).get());
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event){
        if (event.getItem() == null)
            return;
        if (event.getItem() != null && event.getItem().getType().equals(Material.BED) && !uhcCore.getUhcGame().getGameState().equals(GameState.PLAYING)){
            event.getPlayer().kickPlayer("§cà plus !");
        }
        if (event.getItem() != null && event.getItem().getType() == Material.SKULL_ITEM && event.getItem().getItemMeta().getDisplayName().equalsIgnoreCase("§8§l» §b§lConfigurer la partie"))
            uhcCore.getUhcManager().getConfigMainGUI().open(event.getPlayer());
        if (event.getItem() != null && event.getItem().getType() == Material.SKULL_ITEM && event.getItem().getItemMeta().getDisplayName().equalsIgnoreCase("§8§l» §3§lSuccès"))
            new AchievementsGUI(event.getPlayer(), 0).getkInventory().open(event.getPlayer());
        if (event.getItem() != null && event.getItem().getType() == Material.SKULL_ITEM && event.getItem().getItemMeta().getDisplayName().equalsIgnoreCase("§8§l» §6§lRègles de la partie"))
            new RulesGUI().getkInventory().open(event.getPlayer());
        if (event.getItem() != null && event.getItem().getType() == Material.SKULL_ITEM && event.getItem().getItemMeta().getDisplayName().equalsIgnoreCase("§8§l» §a§lHistorique de parties"))
            new GameSavedGUI(event.getPlayer(), true, "all", 0).getkInventory().open(event.getPlayer());
        else if (event.getItem() != null && event.getItem().getType() == Material.BANNER && uhc.getGameState() == GameState.WAITING ||event.getItem() != null && event.getItem().getType() == Material.BANNER && uhc.getGameState() == GameState.STARTING)
            new SelectTeamGUI(UHC.getUHCPlayer(event.getPlayer()), 0).getkInventory().open(event.getPlayer());
        else if (event.getItem() != null && event.getItem().getType() == Material.MILK_BUCKET && !uhc.getUhcConfig().isMilkBukket())
            event.setCancelled(true);
        else if (event.getItem().getType() == uhcCore.getUhcManager().getGamemode().getItemStack().getType() && event.getItem().getItemMeta().getDisplayName().contains("Configuration du module"))
            uhcCore.getUhcManager().getGamemode().getConfigGUI().open(event.getPlayer());
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event){
        if (uhc.getGameState() != GameState.PLAYING && !UHC.getUHCPlayer(event.getPlayer()).isUHCOp())
            event.setCancelled(true);
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        UHCPlayer uhcPlayer = UHC.getUHCPlayer(player);
        Material blockType = event.getBlock().getType();
        GameState gameState = uhc.getGameState();

        if (gameState != GameState.PLAYING && !uhcPlayer.isUHCOp()) {
            event.setCancelled(true);
            return;
        }

        if (gameState == GameState.PLAYING) {
            switch (blockType) {
                case DIAMOND_ORE:
                    event.getBlock().getDrops().clear();
                    if (uhcPlayer.getDiamondMined() >= uhc.getUhcConfig().getDiamonLimit()) {
                        uhcPlayer.safeGive(new ItemCreator(Material.GOLD_INGOT, (uhc.getUhcConfig().isDoubleGold() ? 2 : 1)).get());
                        uhcPlayer.getBukkitPlayer().giveExp(3 * uhc.getUhcConfig().getXpBoost());
                    }
                    uhcPlayer.setDiamondMined(uhcPlayer.getDiamondMined() + 1);
                    uhc.getUhcData().getAlerts().forEach(alertPlayer ->
                            alertPlayer.sendModMessage("§a§lAlerts §8§l» §e§l" + uhcPlayer.getName() +
                                    "§7 vient de miner un diamant. C'est son §e" + uhcPlayer.getDiamondMined() + " diamant miné§7.")
                    );
                    break;

                case GOLD_ORE:
                    uhcPlayer.setGoldMined(uhcPlayer.getGoldMined() + 1);
                    uhc.getUhcData().getAlerts().forEach(alertPlayer ->
                            alertPlayer.sendModMessage("§a§lAlerts §8§l» §e§l" + uhcPlayer.getName() +
                                    "§7 vient de miner un or. C'est son §e" + uhcPlayer.getGoldMined() + " or miné§7.")
                    );
                    break;

                case IRON_ORE:
                    uhcPlayer.setIronMined(uhcPlayer.getIronMined() + 1);
                    break;

                default:
                    break;
            }
        }
    }


    @EventHandler
    public void onDamage(EntityDamageEvent event){
        if (!(event.getEntity() instanceof Player))
            return;
        if (uhc.getGameState() != GameState.PLAYING){
            event.setCancelled(true);
        }else if (!UHC.getUHCPlayer(((Player) event.getEntity())).isDamageable()) event.setCancelled(true);
        else {
            UHCPlayer victim = UHC.getUHCPlayer(((Player) event.getEntity()));
            victim.addReceivedDamages(event.getFinalDamage());
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Location from = event.getFrom();
        Location to = event.getTo();
        if (from.getY() <= 0 && uhc.getGameState() != GameState.PLAYING) {
            event.getPlayer().teleport(WorldManager.getSpawnLocation());
            return;
        }
        if (!UHC.getUHCPlayer(event.getPlayer()).getFreeze())
            return;
        if (to.getY() > from.getY() && to.getY() - from.getY() > 0){
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onRole(RolesEvent event){
        Bukkit.getScheduler().runTaskLater(UHC.getInstance(), () -> {
            uhc.getUhcData().getAlerts().forEach(uhcPlayer -> {
                uhcPlayer.sendMessage("§a§lAlerts§8§l»" + "§7Voici la liste des joueurs de la partie ainsi que leurs rôles:");
                uhc.getUhcData().getUhcPlayerList().forEach(uhcPlayer1 -> {
                    uhcPlayer.sendMessage("§8§l» §e" + uhcPlayer1.getName() + " §7- " + (uhcPlayer1.getRole() != null ? uhcPlayer1.getRole().getName() : "§cAucun"));
                });
            });
        }, 100L);
    }

    private final HashMap<UUID, Long> lastMessageTime = new HashMap<>();
    private static final long MESSAGE_COOLDOWN = 1500;

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) throws Exception {
        UHCPlayer uhcPlayer = UHC.getUHCPlayer(event.getPlayer());
        UUID playerUUID = event.getPlayer().getUniqueId();
        long currentTime = System.currentTimeMillis();

        if (lastMessageTime.containsKey(playerUUID)) {
            long lastTime = lastMessageTime.get(playerUUID);
            if (currentTime - lastTime < MESSAGE_COOLDOWN) {
                event.setCancelled(true);
                uhcPlayer.sendClassicMessage("§cVeuillez patienter avant d'envoyer un autre message.");
                SoundUtils.playSoundToPlayer(uhcPlayer.getBukkitPlayer(), Sound.VILLAGER_NO);
                return;
            }
        }
        lastMessageTime.put(playerUUID, currentTime);

        if (!uhc.getUhcData().isChat()) {
            event.setCancelled(true);
            return;
        }

        boolean isGamePlaying = uhc.getGameState().equals(GameState.PLAYING);
        boolean isTeamChatActive = UHCTeamManager.getInstance().isActivated();
        boolean isHost = uhc.getUhcData().getCoHostList().contains(uhcPlayer) || uhc.getUhcData().getHostPlayer() == uhcPlayer;
        boolean isSpectator = uhc.getUhcData().getSpecList().contains(uhcPlayer);
        boolean isAlive = uhcPlayer.getPlayerState().equals(UHCPlayerStates.ALIVE);

        if (isGamePlaying && isTeamChatActive) {
            if (!event.getMessage().startsWith("!")) {
                uhcPlayer.getUhcTeam().getUhcPlayerList().forEach(teamMember ->
                        teamMember.sendMessage(uhcPlayer.getUhcTeam().getColor() + uhcPlayer.getUhcTeam().getPrefix() + " " +
                                uhcPlayer.getName() + " §8§l» §f" + event.getMessage()));
                event.setCancelled(true);
                return;
            }
        }

        if (isGamePlaying && !isAlive && !isSpectator) {
            event.setCancelled(true);
            return;
        }


        String message = event.getMessage().startsWith("!") ? event.getMessage().substring(1) : event.getMessage();
        double score = MessageChecker.getToxicityScore(event.getMessage());
        if (score > MessageChecker.scoreAILimit) {
            event.setCancelled(true);
            if (UHC.getInstance().getDiscordManager() != null) sendMessageToChannel(UHC.getInstance().getDiscordManager().getChatChannelId(), "**DELETE**: `" + event.getPlayer().getDisplayName() + ": (" + score + ") " + message + "`");
            uhcPlayer.sendModMessage("§cVotre message a été supprimé après une analyse jugée toxique. §7(§eScoreAI§7: §a" + score * 100 + "%§7)");
            UHC.getInstance().getGameSave().registerChat(uhcPlayer.getBukkitPlayer(), score, message);
            SoundUtils.playSoundToPlayer(event.getPlayer(), Sound.VILLAGER_NO);
            return;
        }

        String prefix = uhcPlayer.getUhcTeam() != null ? uhcPlayer.getUhcTeam().getColor() + uhcPlayer.getUhcTeam().getPrefix() + " " : "";
        String rolePrefix = isHost ? "§6§lHOST §8┃ §6" : isSpectator ? "§c§lSPEC §8┃ §c" : "§7";

        if (isGamePlaying && isTeamChatActive && event.getMessage().startsWith("!")) {
            event.setFormat("§7(ALL) §8┃ " + rolePrefix + prefix + uhcPlayer.getName() + " §8§l» §f" + message);
        } else {
            event.setFormat(rolePrefix + uhcPlayer.getName() + " §8§l» §f" + message);
        }
        if (UHC.getInstance().getDiscordManager() != null) sendMessageToChannel(UHC.getInstance().getDiscordManager().getChatChannelId(), "`" + event.getPlayer().getDisplayName() + ": (" + score + ") " + event.getMessage() + "`");
        UHC.getInstance().getGameSave().registerChat(uhcPlayer.getBukkitPlayer(), score, message);
    }

    private void sendMessageToChannel(String channelId, String text) {
        TextChannel channel = UHC.getInstance().getDiscordManager().getDiscordBot().getTextChannelById(channelId);
        if (channel != null) {
            channel.sendMessage(text).queue();
        } else {
            System.out.println("Salon introuvable !");
        }
    }

    @EventHandler
    public void onWeather(WeatherChangeEvent event){
        event.setCancelled(true);
    }

    @EventHandler
    public void onFood(FoodLevelChangeEvent event){
        if (uhc.getGameState() != GameState.PLAYING)
            event.setCancelled(true);
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event){
        if (uhc.getGameState() != GameState.PLAYING)
            event.setCancelled(true);
    }

    @EventHandler
    public void onInvMove(InventoryMoveItemEvent event){
        if (uhc.getGameState() != GameState.PLAYING){
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDragInv(InventoryDragEvent event){
        if (uhc.getGameState() != GameState.PLAYING){
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPortal(PlayerPortalEvent event){
        if (event.getCause() == PlayerTeleportEvent.TeleportCause.NETHER_PORTAL && !uhc.getUhcConfig().isNether()){
            event.setCancelled(true);
            event.getPlayer().sendMessage(UHC.getInstance().getPrefix() + " §cLe Nether est désactivé dans cette partie !");
        }else if (event.getCause() == PlayerTeleportEvent.TeleportCause.END_PORTAL && !uhc.getUhcConfig().isEnd()){
            event.setCancelled(true);
            event.getPlayer().sendMessage(UHC.getInstance().getPrefix() + " §cL'End est désactivé dans cette partie !");
        }
        if(event.getFrom().getWorld().getEnvironment() == World.Environment.NORMAL){
            if (event.getCause() == PlayerTeleportEvent.TeleportCause.NETHER_PORTAL){
                event.setTo(event.getPortalTravelAgent().findOrCreate(new Location(WorldManager.getNetherWorld(), event.getPlayer().getLocation().getX(), WorldManager.getNetherWorld().getHighestBlockYAt((int) (event.getPlayer().getLocation().getX()), (int) (event.getPlayer().getLocation().getZ())), event.getPlayer().getLocation().getZ())));
            }
            if (event.getCause() == PlayerTeleportEvent.TeleportCause.END_PORTAL){
                Player player = event.getPlayer();
                Cuboid cuboid = new Cuboid(WorldManager.getNetherWorld(), 98, 48, 2, 102, 48, -2);
                cuboid.forEach(block -> {
                    block.setType(Material.OBSIDIAN);
                });
                player.teleport(new Location(WorldManager.getEndWorld(), 100, 50, 0, 90F, 0F));
            }
        } if (event.getFrom().getWorld().getEnvironment().equals(World.Environment.NETHER)){
            event.setTo(event.getPortalTravelAgent().findOrCreate(new Location(WorldManager.getGameWorld(), event.getPlayer().getLocation().getX(), WorldManager.getNetherWorld().getHighestBlockYAt((int) (event.getPlayer().getLocation().getX()), (int) (event.getPlayer().getLocation().getZ())), event.getPlayer().getLocation().getZ())));
        }
        if (event.getFrom().getWorld().getEnvironment().equals(World.Environment.THE_END)){
            PlayersUtils.randomTp(event.getPlayer(), WorldManager.getGameWorld());
        }
    }

    @EventHandler
    public void onDamagedPlayerByPlayer(EntityDamageByEntityEvent event){
        if (!uhc.getUhcData().isPvp() && event.getEntity() instanceof Player && event.getDamager() instanceof Player){
            event.setCancelled(true);
            return;
        }
        if (event.getDamager() instanceof Player && event.getEntity() instanceof Player && uhc.getGameState() == GameState.PLAYING){
            if (UHCTeamManager.getInstance().isActivated() && !UHCTeamManager.getInstance().isFriendlyFire()){
                if (UHC.getUHCPlayer(((Player) event.getEntity())).getUhcTeam().getUhcPlayerList().contains(UHC.getUHCPlayer(((Player) event.getDamager())))){
                    event.setCancelled(true);
                }
            } else if (event.getDamager() instanceof Player && event.getEntity() instanceof Player){
                UHCPlayer damager = UHC.getUHCPlayer(((Player) event.getDamager()));
                damager.addMakeDamages(event.getFinalDamage());
            }
        }
    }

    private int strengthRate = 30;
    private int resistanceRate = 15;

    @EventHandler
    private void onPatchPotion(EntityDamageByEntityEvent event) {
        event.setDamage(event.getDamage() * 0.90);

        if (!(event.getEntity() instanceof Player))
            return;

        if (!(event.getDamager() instanceof Player))
            return;
        Player damager = (Player) event.getDamager();
        Player player = (Player) event.getEntity();

        if (damager.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE)) {

            if (damager.getActivePotionEffects().stream().filter(potionEffect -> potionEffect.getType().equals(PotionEffectType.INCREASE_DAMAGE)).map(PotionEffect::getAmplifier).findFirst().orElse(-1) == 0) {
                event.setDamage(event.getDamage() / 2.3f *
                        (1 + strengthRate / 100f));
            } else event.setDamage(event.getDamage() *
                    (1 + strengthRate / 100f));
        }
        if (player.hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE)) {
            if (resistanceRate >= 100) {
                event.setCancelled(true);
            }
            event.setDamage(event.getDamage() * (100 - resistanceRate) / 80f);
        }
    }

    private final StuffConfig stuffConfig = uhc.getUhcConfig().getStuffConfig();
    @EventHandler
    public void onCraft(PrepareItemCraftEvent event){
        if (event.getRecipe().getResult().getType() == Material.DIAMOND_HELMET && !stuffConfig.isDiamondHelmet()){
            event.getInventory().setResult(new ItemStack(Material.AIR));
        }else if (event.getRecipe().getResult().getType() == Material.DIAMOND_CHESTPLATE && !stuffConfig.isDiamondChesp()){
            event.getInventory().setResult(new ItemStack(Material.AIR));
        }else if (event.getRecipe().getResult().getType() == Material.DIAMOND_LEGGINGS && !stuffConfig.isDiamondLeggins()){
            event.getInventory().setResult(new ItemStack(Material.AIR));
        }else if (event.getRecipe().getResult().getType() == Material.DIAMOND_BOOTS && !stuffConfig.isDiamondBoots()){
            event.getInventory().setResult(new ItemStack(Material.AIR));
        }
    }

    @EventHandler
    public void onAnvil(InventoryClickEvent event){
        if (event.getInventory() instanceof AnvilInventory){
            InventoryView view = event.getView();
            int rawSlot = event.getRawSlot();
            if (rawSlot == view.convertSlot(rawSlot)){
                ItemStack item = event.getCurrentItem();
                if (item != null){
                    EnchantsManager manager = UHC.getInstance().getEnchantsManager();

                    Map<Enchantment, Integer> map = event.getCurrentItem().getEnchantments();

                    if (map.containsKey(Enchantment.DAMAGE_ALL) && item.getType() == Material.DIAMOND_SWORD) {
                        int level = map.get(Enchantment.DAMAGE_ALL);
                        if (manager.getDiamondSharpness() < level) event.setCancelled(true);
                    }

                    if (map.containsKey(Enchantment.PROTECTION_ENVIRONMENTAL) && GameUtils.isDiamondArmor(item)) {
                        int level = map.get(Enchantment.PROTECTION_ENVIRONMENTAL);
                        if (manager.getDiamondArmor() < level) event.setCancelled(true);
                    }

                    if (map.containsKey(Enchantment.DAMAGE_ALL) && item.getType() == Material.IRON_SWORD) {
                        int level = map.get(Enchantment.DAMAGE_ALL);
                        if (manager.getIronSharpness() < level) event.setCancelled(true);
                    }

                    if (map.containsKey(Enchantment.PROTECTION_ENVIRONMENTAL) && GameUtils.isIronArmor(item)) {
                        int level = map.get(Enchantment.PROTECTION_ENVIRONMENTAL);
                        if (manager.getIronArmor() < level) event.setCancelled(true);
                    }

                    if (map.containsKey(Enchantment.ARROW_INFINITE)) {
                        int level = map.get(Enchantment.ARROW_INFINITE);
                        if (manager.getInfinity() < level) event.setCancelled(true);
                    }

                    if (map.containsKey(Enchantment.ARROW_DAMAGE)) {
                        int level = map.get(Enchantment.ARROW_DAMAGE);
                        if (manager.getPower() < level) event.setCancelled(true);
                    }

                    if (map.containsKey(Enchantment.ARROW_KNOCKBACK)) {
                        int level = map.get(Enchantment.ARROW_KNOCKBACK);
                        if (manager.getPunch() < level) event.setCancelled(true);
                    }

                    if (map.containsKey(Enchantment.KNOCKBACK)) {
                        int level = map.get(Enchantment.KNOCKBACK);
                        if (manager.getKb() < level) event.setCancelled(true);
                    }

                    if (map.containsKey(Enchantment.FIRE_ASPECT)) {
                        int level = map.get(Enchantment.FIRE_ASPECT);
                        if (manager.getFireAspect() < level) event.setCancelled(true);
                    }

                    if (map.containsKey(Enchantment.ARROW_FIRE)) {
                        int level = map.get(Enchantment.ARROW_FIRE);
                        if (manager.getFlame() < level) event.setCancelled(true);
                    }

                    if (map.containsKey(Enchantment.THORNS)) {
                        int level = map.get(Enchantment.THORNS);
                        if (manager.getThorns() < level) event.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onEnchant(EnchantItemEvent event) {

        EnchantsManager manager = UHC.getInstance().getEnchantsManager();

        Map<Enchantment, Integer> map = event.getEnchantsToAdd();
        ItemStack item = event.getItem();

        if (map.containsKey(Enchantment.DAMAGE_ALL) && item.getType() == Material.DIAMOND_SWORD) {
            int level = map.get(Enchantment.DAMAGE_ALL);
            if (manager.getDiamondSharpness() < level) event.setCancelled(true);
        }

        if (map.containsKey(Enchantment.PROTECTION_ENVIRONMENTAL) && GameUtils.isDiamondArmor(item)) {
            int level = map.get(Enchantment.PROTECTION_ENVIRONMENTAL);
            if (manager.getDiamondArmor() < level) event.setCancelled(true);
        }

        if (map.containsKey(Enchantment.DAMAGE_ALL) && item.getType() == Material.IRON_SWORD) {
            int level = map.get(Enchantment.DAMAGE_ALL);
            if (manager.getIronSharpness() < level) event.setCancelled(true);
        }

        if (map.containsKey(Enchantment.PROTECTION_ENVIRONMENTAL) && GameUtils.isIronArmor(item)) {
            int level = map.get(Enchantment.PROTECTION_ENVIRONMENTAL);
            if (manager.getIronArmor() < level) event.setCancelled(true);
        }

        if (map.containsKey(Enchantment.ARROW_INFINITE)) {
            int level = map.get(Enchantment.ARROW_INFINITE);
            if (manager.getInfinity() < level) event.setCancelled(true);
        }

        if (map.containsKey(Enchantment.ARROW_DAMAGE)) {
            int level = map.get(Enchantment.ARROW_DAMAGE);
            if (manager.getPower() < level) event.setCancelled(true);
        }

        if (map.containsKey(Enchantment.ARROW_KNOCKBACK)) {
            int level = map.get(Enchantment.ARROW_KNOCKBACK);
            if (manager.getPunch() < level) event.setCancelled(true);
        }

        if (map.containsKey(Enchantment.KNOCKBACK)) {
            int level = map.get(Enchantment.KNOCKBACK);
            if (manager.getKb() < level) event.setCancelled(true);
        }

        if (map.containsKey(Enchantment.FIRE_ASPECT)) {
            int level = map.get(Enchantment.FIRE_ASPECT);
            if (manager.getFireAspect() < level) event.setCancelled(true);
        }

        if (map.containsKey(Enchantment.ARROW_FIRE)) {
            int level = map.get(Enchantment.ARROW_FIRE);
            if (manager.getFlame() < level) event.setCancelled(true);
        }

        if (map.containsKey(Enchantment.THORNS)) {
            int level = map.get(Enchantment.THORNS);
            if (manager.getThorns() < level) event.setCancelled(true);
        }
    }

    @EventHandler
    public void onXp(PlayerExpChangeEvent event){
        event.setAmount(event.getAmount()*uhc.getUhcConfig().getXpBoost());
    }

    @EventHandler
    private void onCommand(PlayerCommandPreprocessEvent event) {
        String[] args = event.getMessage().split(" ");
        if (args[0].equalsIgnoreCase("/rl") ||
                args[0].equalsIgnoreCase("/reload") ||
                args[0].equalsIgnoreCase("/bukkit:rl") ||
                args[0].equalsIgnoreCase("/bukkit:reload") ||
                args[0].equalsIgnoreCase("/me") ||
                args[0].equalsIgnoreCase("/minecraft:me")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onAchievement(PlayerAchievementAwardedEvent event){
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerDamaging(EntityDamageByEntityEvent entityDamageByEntityEvent){
        if (entityDamageByEntityEvent.getDamager() instanceof Arrow && entityDamageByEntityEvent.getEntity() instanceof Player && ((Arrow)entityDamageByEntityEvent.getDamager()).getShooter() instanceof Player){
            if( !uhc.getUhcData().isPvp()) {
                entityDamageByEntityEvent.setCancelled(true);
            }
            else if (uhc.getUhcConfig().isBowLife()){
                Bukkit.getScheduler().runTaskLater(UHC.getInstance(), () -> {
                    Player player = ((Player) ((Arrow) entityDamageByEntityEvent.getDamager()).getShooter());
                    Player victim = ((Player) entityDamageByEntityEvent.getEntity());
                    player.sendMessage(UHC.getInstance().getPrefix() + "§e " + victim.getName() + " §8§l» " + makePercentColor(victim.getHealth()) + "%");
                }, 1L);

            }
        }

    }

    private String makePercentColor(double health) {
        double hearts = health / 2;
        double percent = hearts * 10;

        if (percent >= 66) {
            return "§a" + ((int) percent);
        } else if (percent >= 33) {
            return "§e" + ((int) percent);
        } else if (percent == 0) {
            return "§0" + (0);
        } else {
            return "§c" + ((int) percent);
        }
    }

    public boolean canTakeItem(ItemStack item, Player player,Inventory inventory) {
        return inventory.contains(item) && inventory.getTitle().contains(player.getName()) && inventory.getViewers().contains(player);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        UHCPlayer uhcPlayer = UHC.getUHCPlayer((Player) event.getWhoClicked());
        Inventory inventory = uhcPlayer.getBackupInventory();
        ItemStack current = event.getCurrentItem();
        if (event.getInventory().equals(inventory)) {
            event.setCancelled(true);
            if (event.getCurrentItem() != null && canTakeItem(event.getCurrentItem(), (Player) event.getWhoClicked(),inventory)) {
                if(uhcPlayer.getBukkitPlayer().getInventory().firstEmpty() != -1){
                    inventory.remove(current);
                    uhcPlayer.getBukkitPlayer().getInventory().addItem(current);
                }else{
                    uhcPlayer.getBukkitPlayer().sendMessage(UHC.getInstance().getPrefix() + " §c§nAttention ! §cVotre inventaire est plein ! Par conséquent, vous ne pouvez pas récupérer des items dans votre inventaire de backup !");
                }
            }
        }
    }
}

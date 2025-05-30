package fr.anto42.emma.utils.players;

import fr.anto42.emma.UHC;
import fr.anto42.emma.coreManager.listeners.customListeners.EndGameEvent;
import fr.anto42.emma.coreManager.listeners.customListeners.WinEvent;
import fr.anto42.emma.coreManager.players.UHCPlayer;
import fr.anto42.emma.coreManager.players.UHCPlayerStates;
import fr.anto42.emma.coreManager.teams.UHCTeamManager;
import fr.anto42.emma.coreManager.worldManager.WorldManager;
import fr.anto42.emma.utils.ArmorStandUtils;
import fr.anto42.emma.utils.Cuboid;
import fr.anto42.emma.utils.TimeUtils;
import fr.anto42.emma.utils.chat.InteractiveMessage;
import fr.anto42.emma.utils.chat.InteractiveMessageBuilder;
import fr.anto42.emma.utils.materials.ItemCreator;
import fr.anto42.emma.utils.skulls.SkullList;
import fr.anto42.emma.utils.versionsUtils.VersionUtils;
import net.md_5.bungee.api.chat.ClickEvent;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class PlayersUtils {
    public static void finishToSpawn () {
        Bukkit.getPluginManager().callEvent(new EndGameEvent());
        UHC.getInstance().getUhcGame().getUhcData().getUhcPlayerList().stream().filter(uhcPlayer -> uhcPlayer.getPlayerState() == UHCPlayerStates.ALIVE).forEach(uhcPlayer -> {
            uhcPlayer.setHasWin(true);
            Bukkit.getPluginManager().callEvent(new WinEvent(uhcPlayer));
        });
        if (UHC.getInstance().getConfig().getBoolean("customSpawn")) {
            Bukkit.getScheduler().runTaskLater(UHC.getInstance(), () -> {
                Bukkit.getOnlinePlayers().forEach(player -> {
                    player.teleport(WorldManager.getSpawnLocation());
                    player.setGameMode(GameMode.SURVIVAL);
                    player.getInventory().clear();
                    player.getInventory().setHelmet(null);
                    player.getInventory().setChestplate(null);
                    player.getInventory().setLeggings(null);
                    player.getInventory().setBoots(null);
                    player.getInventory().setItem(8, new ItemCreator(Material.BED).name("§8§l» §c§lRetourner au Hub").get());
                });
            }, 20L);
        } else {
            World gameWorld = WorldManager.getGameWorld();
            Cuboid cuboid = new Cuboid(gameWorld, -20, 200, 20, 20, 200, -20);
            cuboid.forEach(block -> {
                block.setType(Material.BARRIER);
            });
            cuboid = new Cuboid(gameWorld, -20, 201, 20, 20, 203, 20);
            cuboid.forEach(block -> {
                block.setType(Material.STAINED_GLASS_PANE);
            });
            cuboid = new Cuboid(gameWorld, 20, 201, -20, 20, 203, 20);
            cuboid.forEach(block -> {
                block.setType(Material.STAINED_GLASS_PANE);
            });
            cuboid = new Cuboid(gameWorld, 20, 201, -20, -20, 203, -20);
            cuboid.forEach(block -> {
                block.setType(Material.STAINED_GLASS_PANE);
            });
            cuboid = new Cuboid(gameWorld, -20, 201, -20, -20, 203, 20);
            cuboid.forEach(block -> {
                block.setType(Material.STAINED_GLASS_PANE);
            });
            Bukkit.getScheduler().runTaskLater(UHC.getInstance(), () -> {
                Bukkit.getOnlinePlayers().forEach(player -> {
                    player.teleport(WorldManager.getSpawnLocation());
                    player.setGameMode(GameMode.SURVIVAL);
                    player.getInventory().clear();
                    player.getInventory().setHelmet(null);
                    player.getInventory().setChestplate(null);
                    player.getInventory().setLeggings(null);
                    player.getInventory().setBoots(null);
                    player.getInventory().setItem(6, new ItemCreator(SkullList.GIFT.getItemStack()).name("§8§l» §3§lSuccès").get());
                    player.getInventory().setItem(7, new ItemCreator(SkullList.BOOKSHELF.getItemStack()).name("§8§l» §a§lHistorique de parties").get());
                    player.getInventory().setItem(8, new ItemCreator(Material.BED).name("§8§l» §c§lRetourner au Hub").get());
                });
            }, 5L);
            Bukkit.getScheduler().runTaskLater(UHC.getInstance(), () -> {
                SoundUtils.playSoundToAll(Sound.ENDERDRAGON_GROWL);
            },10L);
        }
        Bukkit.getScheduler().runTaskLater(UHC.getInstance(), () -> {
            Bukkit.getOnlinePlayers().forEach(player -> {
                new InteractiveMessage().add(new InteractiveMessageBuilder(UHC.getInstance().getPrefix() + " §aCliquez ici pour afficher les crédits.").setClickAction(ClickEvent.Action.RUN_COMMAND, "/credits").build()).sendMessage(player);
            });
        }, TimeUtils.seconds(2));
    }


    public static void giveWaitingStuff(Player player){
        if (UHC.getUHCPlayer(player).isEditing())
            return;
        player.getInventory().clear();
        if (UHCTeamManager.getInstance().isActivated())
            if (UHC.getUHCPlayer(player).getUhcTeam() == null) {
                player.getInventory().setItem(0, new ItemCreator(Material.BANNER).bannerColor(DyeColor.WHITE).name("§8§l» §e§lSélection des équipes").get());
            }else{
                player.getInventory().setItem(0, new ItemCreator(Material.BANNER).bannerColor(UHC.getUHCPlayer(player).getUhcTeam().getDyeColor()).name("§8§l» §e§lSélection des équipes").get());
            }
        player.getInventory().setItem(1, new ItemCreator(SkullList.CHEST.getItemStack()).name("§8§l» §6§lRègles de la partie").get());
        player.getInventory().setItem(7, new ItemCreator(SkullList.BOOKSHELF.getItemStack()).name("§8§l» §a§lHistorique de parties").get());
        player.getInventory().setItem(6, new ItemCreator(SkullList.GIFT.getItemStack()).name("§8§l» §3§lSuccès").get());
        player.getInventory().setItem(8, new ItemCreator(Material.BED).name("§8§l» §c§lRetourner au Hub").get());
        if (UHC.getInstance().getUhcGame().getUhcData().getHostPlayer() == UHC.getUHCPlayer(player) || UHC.getInstance().getUhcGame().getUhcData().getCoHostList().contains(UHC.getUHCPlayer(player)))
            player.getInventory().setItem(4, new ItemCreator(SkullList.BLOCK_COMMANDBLOCK_DEFAULT.getItemStack()).name("§8§l» §b§lConfigurer la partie").get());
        if (UHC.getInstance().getUhcManager().getGamemode().getConfigGUI() != null) {
            player.getInventory().setItem(2, new ItemCreator(UHC.getInstance().getUhcManager().getGamemode().getItemStack()).name("§8§l» §d§lConfiguration du module").lore("").get());
        }
    }

    public static void randomTp(Player player, World world){
        Random random = new Random();
        int x = random.nextInt(((int) world.getWorldBorder().getSize()/2 - 40));
        x = x-((int) world.getWorldBorder().getSize()/2 - 40);
        int z = random.nextInt(((int) world.getWorldBorder().getSize()/2 - 40));
        z = z-((int) world.getWorldBorder().getSize()/2 - 40);
        player.teleport(new Location(world, x, world.getHighestBlockYAt(x, z) + 100, z, 0F, 0F));
        UHC.getUHCPlayer(player).setDamageable(false);
        player.sendMessage(UHC.getInstance().getPrefix() + " §aVous êtes invincible pour les 30 prochaines secondes.");
        Bukkit.getScheduler().runTaskLater(UHC.getInstance(), () -> {
            player.sendMessage(UHC.getInstance().getPrefix() + " §cVous pouvez de nouveau subir des dégâts !");
            SoundUtils.playSoundToPlayer(player, Sound.ANVIL_LAND);
            UHC.getUHCPlayer(player).setDamageable(true);
        }, TimeUtils.seconds(30));
    }

    public static void spawnOfflineZombieFor(Player player){
        UHCPlayer uhcPlayer = UHC.getUHCPlayer(player);

        Zombie zombie = (Zombie) player.getWorld().spawnEntity(player.getLocation(), EntityType.ZOMBIE);
        VersionUtils.getVersionUtils().setEntityAI(zombie, false);
        zombie.setCustomName(uhcPlayer.getName());
        zombie.setCustomNameVisible(true);
        zombie.setBaby(false);
        zombie.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 999999, 1, true, true));
        zombie.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 999999, 9999999, true, true));

        EntityEquipment equipment = zombie.getEquipment();
        equipment.setHelmet(new ItemCreator(Material.SKULL_ITEM,1, (short) 3).owner(player.getName()).get());
        equipment.setChestplate(player.getInventory().getChestplate());
        equipment.setLeggings(player.getInventory().getLeggings());
        equipment.setBoots(player.getInventory().getBoots());
        equipment.setItemInHand(player.getItemInHand());

        uhcPlayer.setOfflineZombieUuid(zombie.getUniqueId());
    }

    public static void broadcastMessage(String message){
        Bukkit.broadcastMessage(UHC.getInstance().getPrefix() + " §7" + message);
    }


    public static void createFinalTab () {
        int rang = 1;
        Location location = WorldManager.getSpawnLocation().add(5, 2, 5);
        List<UHCPlayer> classement = UHC.getInstance().getUhcGame().getUhcData().getUhcPlayerList();
        classement.sort(Comparator.comparingLong(UHCPlayer::getKills).reversed());

        Location locationTitre = location.clone();
        ArmorStand titreStand = ArmorStandUtils.getArmorStandAtLocation(locationTitre);
        if (titreStand == null) {
            ArmorStandUtils.createAmorStand("§7§lClassement - §eKills", locationTitre);
        }


        for (UHCPlayer uhcPlayer : classement) {
            if (rang > 10) return;
            String playerName = (uhcPlayer.getUhcTeam() != null ? uhcPlayer.getUhcTeam().getDisplayName() + " §7- " : "") + (uhcPlayer.getRole() != null ? "§c" + uhcPlayer.getRole().getName() + " §7- " : "") + "§e§l" + uhcPlayer.getName() + " §7- §a" + uhcPlayer.getKills() + " §7kill(s)";

            Location locationArmorStand = location.clone().add(0, -(rang + 1) * 0.3, 0);
            ArmorStand stand = ArmorStandUtils.getArmorStandAtLocation(locationArmorStand);

            if (stand == null) {
                ArmorStandUtils.createAmorStand(playerName, locationArmorStand);
            } else {
                ArmorStandUtils.updateArmorStandName(stand, playerName);
            }

            rang++;
        }
    }

}

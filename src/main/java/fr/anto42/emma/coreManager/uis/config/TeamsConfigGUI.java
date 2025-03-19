package fr.anto42.emma.coreManager.uis.config;

import fr.anto42.emma.UHC;
import fr.anto42.emma.coreManager.players.UHCPlayer;
import fr.anto42.emma.coreManager.teams.UHCTeamManager;
import fr.anto42.emma.game.GameState;
import fr.anto42.emma.utils.materials.ItemCreator;
import fr.anto42.emma.utils.players.PlayersUtils;
import fr.anto42.emma.utils.SoundUtils;
import fr.anto42.emma.utils.skulls.SkullList;
import fr.blendman974.kinventory.inventories.KInventory;
import fr.blendman974.kinventory.inventories.KItem;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

public class TeamsConfigGUI {
    private final KInventory kInventory;
    private final UHCTeamManager uhcTeamManager = UHCTeamManager.getInstance();
    String translate(boolean b){
        if (b)
            return "§aactivé";
        else
            return "§cdésactivé";
    }
    public TeamsConfigGUI() {
        this.kInventory = new KInventory(54, UHC.getInstance().getPrefix() + " §6§lEquipes");
        for (int i = 0; i < 9; i++) {
            KItem glass = new KItem(new ItemCreator(Material.STAINED_GLASS_PANE, 1, (byte) 4).get());
            this.kInventory.setElement(i, glass);
            this.kInventory.setElement(45 + i, glass);
        }
        for (int i = 36; i < 45; i++) {
            KItem glass = new KItem(new ItemCreator(Material.STAINED_GLASS_PANE, 1, (byte) 4).get());
            this.kInventory.setElement(i, glass);
        }
        KItem back = new KItem(new ItemCreator(SkullList.LEFT_AROOW.getItemStack()).name("§8┃ §cRevenir en arrière").lore("", "§8┃ §cVous ne trouvez pas §fce que vous souhaitez ?", "§8┃ §aPas de soucis§f, revenez en arrière !", "", "§8§l» §6Cliquez §fpour ouvrir.").get());
        back.addCallback((kInventoryRepresentation, itemStack, player, kInventoryClickContext) -> {
            UHC.getInstance().getUhcManager().getConfigMainGUI().open(player);
        });
        this.kInventory.setElement(49, back);

        KItem teams = new KItem(new ItemCreator(Material.BANNER).bannerColor(DyeColor.RED).name("§8┃ §fEquipes").lore("", "§8§l» §fStatut: §6" + UHCTeamManager.getInstance().getDisplayFormat(), "", "§8┃ §fParfois, la vie est mieux avec des alliés !", "", "§8§l» §6Clique-gauche §fpour ajouter 1.", "§8§l» §6Clique-droit§f pour retirer 1.").get());
        teams.addCallback((kInventoryRepresentation, itemStack, player, kInventoryClickContext) -> {
            if (UHC.getInstance().getUhcGame().getGameState() == GameState.PLAYING)
                return;
            if (uhcTeamManager.getSlots() == 2 && kInventoryClickContext.getClickType() == ClickType.RIGHT && uhcTeamManager.isActivated()){
                uhcTeamManager.setActivated(false);
                UHC.getInstance().getUhcGame().getUhcData().getUhcPlayerList().stream().filter(uhcPlayer -> uhcPlayer.getUhcTeam() != null).forEach(UHCPlayer::leaveTeam);
            }else if (uhcTeamManager.isActivated() && uhcTeamManager.getSlots() == 30 && kInventoryClickContext.getClickType() == ClickType.LEFT){
                uhcTeamManager.setActivated(false);
                UHC.getInstance().getUhcGame().getUhcData().getUhcPlayerList().stream().filter(uhcPlayer -> uhcPlayer.getUhcTeam() != null).forEach(UHCPlayer::leaveTeam);
            }
            else if (!uhcTeamManager.isActivated() && kInventoryClickContext.getClickType() == ClickType.RIGHT){
                uhcTeamManager.setActivated(true);
                uhcTeamManager.setSlots(30);
            }else if (!uhcTeamManager.isActivated() && kInventoryClickContext.getClickType() == ClickType.LEFT){
                uhcTeamManager.setSlots(2);
                uhcTeamManager.setActivated(true);
            }else if (uhcTeamManager.isActivated() && kInventoryClickContext.getClickType().isLeftClick())
                uhcTeamManager.setSlots(uhcTeamManager.getSlots() + 1);
            else if (uhcTeamManager.isActivated() && kInventoryClickContext.getClickType().isRightClick())
                uhcTeamManager.setSlots(uhcTeamManager.getSlots() - 1);
            teams.setItem(new ItemCreator(Material.BANNER).bannerColor(DyeColor.RED).name("§8┃ §fEquipes").lore("", "§8§l» §fStatut: §6" + UHCTeamManager.getInstance().getDisplayFormat(), "", "§8┃ §fParfois, la vie est mieux avec des alliés !", "", "§8§l» §6Clique-gauche §fpour ajouter 1.", "§8§l» §6Clique-droit§f pour retirer 1.").get());
            for(Player player1 : Bukkit.getOnlinePlayers()){
                PlayersUtils.giveWaitingStuff(player1);
            }
        });
        this.kInventory.setElement(22, teams);

        KItem friendlyFire = new KItem(new ItemCreator(Material.DIAMOND_SWORD).name("§8┃ §fFriendlyFire").lore("", "§8§l» §fStatut:  " + translate(uhcTeamManager.isFriendlyFire()), "", "§8┃ §fChoisissez d'§aactiver §Cou non §fles degats entre équipiers", "", "§8§l» §6Cliquez §fpour sélectionner.").get());
        friendlyFire.addCallback((kInventoryRepresentation, itemStack, player, kInventoryClickContext) -> {
            if (uhcTeamManager.isFriendlyFire()) {
                uhcTeamManager.setFriendlyFire(false);
                friendlyFire.setItem(new ItemCreator(Material.DIAMOND_SWORD).name("§8┃ §fFriendlyFire").lore("", "§8§l» §fStatut: " + translate(uhcTeamManager.isFriendlyFire()), "", "§8┃ §fChoisissez d'§aactiver §Cou non §fles degats entre équipiers", "", "§8§l» §6Cliquez §fpour sélectionner.").get());
            }else{
                uhcTeamManager.setFriendlyFire(true);
                friendlyFire.setItem(new ItemCreator(Material.DIAMOND_SWORD).name("§8┃ §fFriendlyFire").lore("", "§8§l» §fStatut: " + translate(uhcTeamManager.isFriendlyFire()), "", "§8┃ §fChoisissez d'§aactiver §Cou non §fles degats entre équipiers", "", "§8§l» §6Cliquez §fpour sélectionner.").get());

            }
        });
        this.kInventory.setElement(21, friendlyFire);

        KItem numberOfTeams = new KItem(new ItemCreator(SkullList.NUMBER_CUBE.getItemStack()).name("§8┃ §fNombre d'équipes").lore("", "§8§l» §fStatut:  §b" + uhcTeamManager.getMaxTeams(), "", "§8┃ §fConfiguez le §anombre d'équipes", "§8┃ §fde votre partie", "", "§8§l» §6Clique-gauche §fpour ajouter 1.", "§8§l» §6Clique-droit§f pour retirer 1.").get());
        numberOfTeams.addCallback((kInventoryRepresentation, itemStack, player, kInventoryClickContext) -> {
            if (kInventoryClickContext.getClickType().isLeftClick()) {
                if (uhcTeamManager.getMaxTeams() >= uhcTeamManager.getUhcTeams().size())
                    return;
                uhcTeamManager.setMaxTeams(uhcTeamManager.getMaxTeams() + 1);
                if (UHC.getInstance().getUhcGame().getGameState() != GameState.WAITING){
                    player.sendMessage(UHC.getInstance().getPrefix() + " §cVous ne pouvez pas faire ça maintenant !");
                    SoundUtils.playSoundToPlayer(player, Sound.VILLAGER_NO);
                    return;
                }
                PlayersUtils.broadcastMessage("§cReset §7de toutes les équipes !");
                UHC.getInstance().getUhcGame().getUhcData().getUhcPlayerList().stream().filter(uhcPlayer -> uhcPlayer.getUhcTeam() != null).forEach(UHCPlayer::leaveTeam);
                for(Player players : Bukkit.getOnlinePlayers()){
                    players.getInventory().clear();
                    PlayersUtils.giveWaitingStuff(players);
                }
                //UHCTeamManager.getInstance().createTeams();
            }
            else if (kInventoryClickContext.getClickType().isRightClick()) {
                if (uhcTeamManager.getMaxTeams() <= 2)
                    return;
                uhcTeamManager.setMaxTeams(uhcTeamManager.getMaxTeams() - 1);
                if (UHC.getInstance().getUhcGame().getGameState() != GameState.WAITING){
                    player.sendMessage(UHC.getInstance().getPrefix() + " §cVous ne pouvez pas faire ça maintenant !");
                    SoundUtils.playSoundToPlayer(player, Sound.VILLAGER_NO);
                    return;
                }
                PlayersUtils.broadcastMessage("§cReset §7de toutes les équipes !");
                UHC.getInstance().getUhcGame().getUhcData().getUhcPlayerList().stream().filter(uhcPlayer -> uhcPlayer.getUhcTeam() != null).forEach(UHCPlayer::leaveTeam);
                for(Player players : Bukkit.getOnlinePlayers()){
                    players.getInventory().clear();
                    PlayersUtils.giveWaitingStuff(players);
                }
            }
            numberOfTeams.setItem(new ItemCreator(SkullList.NUMBER_CUBE.getItemStack()).name("§8┃ §fNombre d'équipes").lore("", "§8§l» §fStatut:  §b" + uhcTeamManager.getMaxTeams(), "", "§8┃ §fConfiguez le §anombre d'équipes", "§8┃ §fde votre partie", "", "§8§l» §6Clique-gauche §fpour ajouter 1.", "§8§l» §6Clique-droit§f pour retirer 1.").get());
        });
        this.kInventory.setElement(23, numberOfTeams);

        KItem randomTeams = new KItem(new ItemCreator(SkullList.UNKNOWN_ELEMENT.getItemStack()).name("§8┃ §fEquipes aléatoires").lore("", "§8┃ §fStatut: " + translate(uhcTeamManager.isRandomTeam()), "", "§8┃ §fUne envie de pimenter la", "§8┃ §fpartie en mélangeant les équipes ?", "", "§8§l» §6Cliquez §fpour activer.").get());
        randomTeams.addCallback((kInventory1, item, player, clickContext) -> {
            uhcTeamManager.setRandomTeam(!uhcTeamManager.isRandomTeam());
            randomTeams.setItem(new ItemCreator(SkullList.UNKNOWN_ELEMENT.getItemStack()).name("§8┃ §fEquipes aléatoires").lore("", "§8┃ §fStatut: " + translate(uhcTeamManager.isRandomTeam()), "", "§8┃ §fUne envie de pimenter la", "§8┃ §fpartie en mélangeant les équipes ?", "", "§8§l» §6Cliquez §fpour activer.").get());
            Bukkit.getOnlinePlayers().stream().filter(player1 -> player != player1).forEach(HumanEntity::closeInventory);
            PlayersUtils.broadcastMessage("§cReset §7de toutes les équipes !");
            UHC.getInstance().getUhcGame().getUhcData().getUhcPlayerList().stream().filter(uhcPlayer -> uhcPlayer.getUhcTeam() != null).forEach(UHCPlayer::leaveTeam);
            for(Player players : Bukkit.getOnlinePlayers()){
                players.getInventory().clear();
                PlayersUtils.giveWaitingStuff(players);
            }
        });
        this.kInventory.setElement(24, randomTeams);

        KItem directionnalArrow = new KItem(new ItemCreator(SkullList.CIBLE.getItemStack()).name("§8┃ §fFlèches directionnelles").lore("", "§8§l» §fStatut: " + translate(uhcTeamManager.isDirectionalArrow()), "", "§8┃ §fLorsque ce paramètre est §aactivé§f,", "§8┃ §fles joueurs connaissent les positions de leurs alliés", "", "§8§l» §6Cliquez §fpour activer.").get());
        directionnalArrow.addCallback((kInventoryRepresentation, itemStack, player, kInventoryClickContext) -> {
            if (uhcTeamManager.isDirectionalArrow()) {
                uhcTeamManager.setDirectionalArrow(false);
                directionnalArrow.setItem(new ItemCreator(SkullList.CIBLE.getItemStack()).name("§8┃ §fFlèches directionnelles").lore("", "§8§l» §fStatut: " + translate(uhcTeamManager.isDirectionalArrow()), "", "§8┃ §fLorsque ce paramètre est §aactivé§f,", "§8┃ §fles joueurs connaissent les positions de leurs alliés", "", "§8§l» §6Cliquez §fpour activer.").get());
            }else{
                uhcTeamManager.setDirectionalArrow(true);
                directionnalArrow.setItem(new ItemCreator(SkullList.CIBLE.getItemStack()).name("§8┃ §fFlèches directionnelles").lore("", "§8§l» §fStatut: " + translate(uhcTeamManager.isDirectionalArrow()), "", "§8┃ §fLorsque ce paramètre est §aactivé§f,", "§8┃ §fles joueurs connaissent les positions de leurs alliés", "", "§8§l» §6Cliquez §fpour activer.").get());
            }
        });
        this.kInventory.setElement(20, directionnalArrow);

        KItem reload = new KItem(new ItemCreator(SkullList.RED_BALL.getItemStack()).name("§8┃ §fRecharger les équipes").lore("", "§8┃ §fVous rencontrer un pépin avec les équipes ?", "§8┃ §fCette fonctionnalité est faîte pour vous !", "", "§8§l» §6Cliquez §fpour sélectionner.").get());
        reload.addCallback((kInventoryRepresentation, itemStack, player, kInventoryClickContext) -> {
            if (UHC.getInstance().getUhcGame().getGameState() != GameState.WAITING){
                player.sendMessage(UHC.getInstance().getPrefix() + " §cVous ne pouvez pas faire ça maintenant !");
                SoundUtils.playSoundToPlayer(player, Sound.VILLAGER_NO);
                return;
            }
            PlayersUtils.broadcastMessage("§cReset §7de toutes les équipes !");
            UHC.getInstance().getUhcGame().getUhcData().getUhcPlayerList().stream().filter(uhcPlayer -> uhcPlayer.getUhcTeam() != null).forEach(UHCPlayer::leaveTeam);
            for(Player players : Bukkit.getOnlinePlayers()){
                players.getInventory().clear();
                PlayersUtils.giveWaitingStuff(players);
            }
            UHCTeamManager.getInstance().createTeams();
        });
        this.kInventory.setElement(4, reload);
    }

    public KInventory getkInventory() {
        return kInventory;
    }
}

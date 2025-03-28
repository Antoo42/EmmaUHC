package fr.anto42.emma.utils.gameSaves;

import fr.anto42.emma.UHC;
import fr.anto42.emma.coreManager.UHCManager;
import fr.anto42.emma.coreManager.worldManager.WorldManager;
import fr.anto42.emma.game.UHCGame;
import fr.anto42.emma.utils.TimeUtils;
import fr.anto42.emma.utils.discord.DiscordWebhook;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.bukkit.Bukkit;
import org.bukkit.Color;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GameSaveCreator {
    private static final String GAME_SAVE_FOLDER = "plugins/UHC/games/";
    private static final Logger LOGGER = Logger.getLogger(GameSaveCreator.class.getName());

    public GameSaveCreator(String winner) {
        try {
            Path savePath = Paths.get(GAME_SAVE_FOLDER);
            Files.createDirectories(savePath);

            UHCGame uhcGame = UHC.getInstance().getUhcGame();
            GameSave gameSave = getGameSave(winner, uhcGame);

            String fileName = gameSave.getGameID() + ".json";
            File saveFile = new File(GAME_SAVE_FOLDER, fileName);

            String json = UHC.getInstance().getSaveSerializationManager().serialize(gameSave);
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(saveFile))) {
                writer.write(json);
            }
            sendDiscordSave(gameSave);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la sauvegarde du jeu", e);
        }


    }

    private void sendDiscordSave(GameSave gameSave) {
        TextChannel channel = UHC.getInstance().getDiscordManager().getDiscordBot().getTextChannelById(UHC.getInstance().getDiscordManager().getLogChannelId());

        if (channel != null) {
            String message = "Une nouvelle partie vient de se terminer : " + gameSave.getGameID() + ".\n" +
                    "`" + "GameName: " + gameSave.getGameName() + "\n" +
                    "Winner: " + gameSave.getWinner() + "\n" +
                    "Host: " + gameSave.getHost() + "\n" +
                    "Date: " + gameSave.getDate().toString() + "\n" +
                    "Module: " + gameSave.getModule() + "\n" +
                    "Timer: " + TimeUtils.getFormattedTime(gameSave.getTimer()) + "\n" +
                    "World Type: " + gameSave.getWorldType() + "\n\n" +
                    "\nServeur: " + Bukkit.getServerName() + "`";

            channel.sendMessage(message).queue();
        } else {
            System.out.println("Salon introuvable avec l'ID : " + UHC.getInstance().getDiscordManager().getLogChannelId());
        }
    }



    private static GameSave getGameSave(String winner, UHCGame uhcGame) {
        UHCManager uhcManager = UHC.getInstance().getUhcManager();
        GameSave gameSave = UHC.getInstance().getGameSave();
        gameSave.registerEvent(EventType.CORE, "Vainqueur: " + winner);

        gameSave.setModule(uhcManager.getGamemode().getDiscordName());
        gameSave.setTimer(uhcGame.getUhcData().getTimer());
        uhcGame.getUhcData().getUhcPlayerListSaved().forEach(uhcPlayer -> {
            gameSave.getUhcPlayerList().add(
                    "uhcPlayerData=" +
                            "name=" + uhcPlayer.getName() + "|" +
                            "kills=" + uhcPlayer.getKills() + "|" +
                            "deaths=" + uhcPlayer.getDeath() + "|" +
                            "diamondsMined=" + uhcPlayer.getDiamondMined() + "|" +
                            "goldMined=" + uhcPlayer.getGoldMined() + "|" +
                            "ironMined=" + uhcPlayer.getIronMined() + "|" +
                            "team=" + (uhcPlayer.getUhcTeam() != null ? uhcPlayer.getUhcTeam().getName() : "None") + "|" +
                            "role=" + (uhcPlayer.getRole() != null ? uhcPlayer.getRole().getName() : "None") + "|" +
                            "isAlive=" + uhcPlayer.isAlive() + "|" +
                            "hasWon=" + uhcPlayer.isHasWin() + "|" +
                            "makeDamages=" + uhcPlayer.getMakeDamages() + "|" +
                            "receivedDamages=" + uhcPlayer.getReceivedDamages()
            );

        });
        gameSave.setWinner(winner);
        gameSave.setHost(uhcGame.getUhcData().getHostPlayer().getName());
        gameSave.setDate(new Date());
        gameSave.setGameName(uhcGame.getUhcConfig().getUHCName());
        gameSave.setWorldType(WorldManager.getWorldType());

        return gameSave;
    }
}


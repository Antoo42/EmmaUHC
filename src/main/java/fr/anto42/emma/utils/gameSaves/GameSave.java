package fr.anto42.emma.utils.gameSaves;

import fr.anto42.emma.UHC;
import fr.anto42.emma.utils.TimeUtils;
import fr.anto42.emma.utils.saves.SaveSerializationManager;
import org.apache.commons.lang.RandomStringUtils;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GameSave {
    private String gameID;
    private String gameName;
    private Date date;
    private int timer;
    private List<String> uhcPlayerList = new ArrayList<>();
    private String module;
    private String winner;
    private String host;
    private List<String> events = new ArrayList<>();
    private List<String> chat = new ArrayList<>();
    private String worldType;

    public GameSave() {
        String uuid = RandomStringUtils.random(10, true, false);
        uuid = uuid + "-";
        uuid = uuid + RandomStringUtils.random(10, false, true);
        setGameID(uuid);
    }

    public String getWinner() {
        return winner;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getTimer() {
        return timer;
    }

    public void setTimer(int timer) {
        this.timer = timer;
    }

    public List<String> getUhcPlayerList() {
        return uhcPlayerList;
    }

    public void setUhcPlayerList(List<String> uhcPlayerList) {
        this.uhcPlayerList = uhcPlayerList;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public List<String> getEvents() {
        return events;
    }

    public void setEvents(List<String> events) {
        this.events = events;
    }

    public List<String> getChat() {
        return chat;
    }

    public void setChat(List<String> chat) {
        this.chat = chat;
    }

    public String getWorldType() {
        return worldType;
    }

    public void setWorldType(String worldType) {
        this.worldType = worldType;
    }

    public String getGameID() {
        return gameID;
    }

    public void setGameID(String gameID) {
        this.gameID = gameID;
    }

    public void registerEvent(EventType eventType, String eventString) {
        getEvents().add("event=Type=" + eventType.toString() + "|string=" + eventString + "|date=" + new Date() + "|gameTimer=" + TimeUtils.getFormattedTime(UHC.getInstance().getUhcGame().getUhcData().getTimer()));
    }

    public void registerChat(Player sender, double score, String chat) {
        getChat().add("chat=sender=" + sender.getName() + "|string=" + chat + "|score=" + score + "|date=" + new Date() + "|gameTimer=" + TimeUtils.getFormattedTime(UHC.getInstance().getUhcGame().getUhcData().getTimer()));
    }

    public Event getEventFromString(String string) {
        return SaveSerializationManager.fromEventString(string);
    }
}
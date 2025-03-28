package fr.anto42.emma.utils.chat;

public class ChatSave {
    private final String sender;
    private final double scoreAI;
    private final String chat;
    private final String date;
    private final String timer;

    public ChatSave(String sender, double scoreAI, String chat, String date, String timer) {
        this.sender = sender;
        this.scoreAI = scoreAI;
        this.chat = chat;
        this.date = date;
        this.timer = timer;
    }

    public String getSender() {
        return sender;
    }

    public double getScoreAI() {
        return scoreAI;
    }

    public String getChat() {
        return chat;
    }

    public String getDate() {
        return date;
    }

    public String getTimer() {
        return timer;
    }
}

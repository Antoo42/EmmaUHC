package fr.anto42.emma.utils.chat;

import com.google.gson.JsonObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class MessageChecker {
    public static final double scoreAILimit = 0.5;
    public static double getToxicityScore(String message) throws Exception {
        String apiKey = "AIzaSyAL6sKCnI4VkDOKzJ7ENiDwTBI78XXqIE0";
        String urlString = "https://commentanalyzer.googleapis.com/v1alpha1/comments:analyze?key=" + apiKey;
        String jsonInput = "{"
                + "\"comment\": {\"text\": \"" + message + "\"},"
                + "\"languages\": [\"fr\"],"
                + "\"requestedAttributes\": {\"TOXICITY\": {}}"
                + "}";

        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        OutputStream os = conn.getOutputStream();
        os.write(jsonInput.getBytes());
        os.flush();
        os.close();

        Scanner scanner = new Scanner(conn.getInputStream());
        String response = scanner.useDelimiter("\\A").next();
        scanner.close();

        JsonObject jsonResponse = new com.google.gson.Gson().fromJson(response, JsonObject.class);

        JsonObject toxicityScoreObj = jsonResponse.getAsJsonObject("attributeScores")
                .getAsJsonObject("TOXICITY")
                .getAsJsonObject("summaryScore");

        return toxicityScoreObj.get("value").getAsDouble();
    }

}

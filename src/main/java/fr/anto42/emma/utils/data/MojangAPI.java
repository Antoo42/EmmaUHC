package fr.anto42.emma.utils.data;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class MojangAPI {
    private static final Gson GSON = new Gson();

    public static String getUUID(String playerName) {
        try {
            URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + playerName);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            if (conn.getResponseCode() == 200) {
                JsonObject json = GSON.fromJson(new InputStreamReader(conn.getInputStream()), JsonObject.class);
                return json.get("id").getAsString();
            } else {
                System.out.println("Profil introuvable pour : " + playerName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static String getHeadTexture(String uuid) {
        try {
            URL url = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            if (conn.getResponseCode() == 200) {
                JsonObject json = GSON.fromJson(new InputStreamReader(conn.getInputStream()), JsonObject.class);
                return json.getAsJsonArray("properties").get(0).getAsJsonObject().get("value").getAsString();
            } else {
                System.out.println("Impossible de récupérer la texture pour l'UUID : " + uuid);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

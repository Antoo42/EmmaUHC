package fr.anto42.emma.utils.data;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;

public class NetworkUtils {
    public static String getLocalIPAddress() {
        try {
            InetAddress localHost = InetAddress.getLocalHost();
            return localHost.getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return "Adresse IP inconnue";
        }
    }
    public static String getPublicIPAddress() {
        try {
            URL url = new URL("https://checkip.amazonaws.com");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String publicIP = reader.readLine();
            reader.close();

            return publicIP;
        } catch (Exception e) {
            e.printStackTrace();
            return "Adresse IP publique inconnue";
        }
    }
}

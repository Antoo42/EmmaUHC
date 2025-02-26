package fr.anto42.emma.utils;

import java.net.InetAddress;
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
}

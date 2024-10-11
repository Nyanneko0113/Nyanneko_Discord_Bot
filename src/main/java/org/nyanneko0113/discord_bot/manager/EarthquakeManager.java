package org.nyanneko0113.discord_bot.manager;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class EarthquakeManager {

    private static String json_url;
    private static final String get_type = "P2P";
    private static String before_gettime = "";

    static {
        if (get_type.equalsIgnoreCase("P2P")) {
            json_url = "https://api.p2pquake.net/v2/jma/quake?limit=5&offset=0&order=-1";
        }
    }

    public static String getEarthquakeName() throws IOException {
        return getJsonArray()
                .get(4).getAsJsonObject()
                .get("earthquake").getAsJsonObject()
                .get("hypocenter").getAsJsonObject()
                .get("name").getAsString();
    }

    public static int getDepth() throws IOException {
        return getJsonArray()
                .get(0).getAsJsonObject()
                .get("earthquake").getAsJsonObject()
                .get("hypocenter").getAsJsonObject()
                .get("depth").getAsInt();
    }

    public static int getMagnitude() throws IOException {
        return getJsonArray()
                .get(0).getAsJsonObject()
                .get("earthquake").getAsJsonObject()
                .get("magnitude").getAsJsonObject()
                .get("depth").getAsInt();
    }

    public static JsonArray getPoints() throws IOException {
        return getJsonArray()
                .get(4).getAsJsonObject()
                .get("points").getAsJsonArray();
    }

    public static String getAddr(int n) throws IOException {
        return getPoints().get(n).getAsJsonObject().get("addr").getAsString();
    }

    public static List<String> getAddrs() throws IOException {
        List<String> addr_list = new ArrayList<>();
        for (int n = 0; n < getPoints().size(); n++) {
            String addr = getPoints().get(n).getAsJsonObject().get("addr").getAsString();
            addr_list.add(addr);
        }
        return addr_list;
    }

    private static String getCreatedAt() throws IOException {
        return getJsonArray().get(0).getAsJsonObject().get("created_at").getAsString();
    }

    private static JsonArray getJsonArray() throws IOException {
        URL url = new URL(json_url);
        HttpsURLConnection con = (HttpsURLConnection)url.openConnection();
        con.setRequestMethod("GET");
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8));
        StringBuilder content = new StringBuilder();

        String inputLine;
        while((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }

        in.close();
        con.disconnect();
        byte[] bytes = content.toString().getBytes(StandardCharsets.ISO_8859_1);
        return  (new Gson()).fromJson(content.toString(), JsonArray.class);
    }

}

package org.nyanneko0113.discord_bot.manager;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class ConfigManager {

    /*
    public static String getEmail() throws IOException {
        return getJson().get("niconico-email").getAsString();
    }

    public static String getPassword() throws IOException {
        return getJson().get("niconico-password").getAsString();
     }
     */

    public static String getToken() throws IOException {
         return getJson().get("token").getAsString();
    }

    public static void setToken(String token) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(getFile()))) {

            JsonObject json = new Gson().fromJson(reader, JsonObject.class);
            json.addProperty("token", token);

            try (BufferedWriter write = new BufferedWriter(new FileWriter(getFile()))) {
                write.write(json.toString());
            }
        }
    }

    private static JsonObject getJson() throws IOException {
        if (getFile().exists()) {
            StringBuilder file_read = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(Files.newInputStream(getFile().toPath()), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    file_read.append(line);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            return new JsonParser().parse(file_read.toString()).getAsJsonObject();
        }
        else {
            throw new NullPointerException("ファイルが存在しません");
        }
    }

    private static File getFile() throws IOException {
        File file = new File("bot_config.json");

        if (!file.exists()) {
            file.createNewFile();

            try (BufferedWriter write = new BufferedWriter(new FileWriter(file))) {
                JsonObject json = new JsonObject();
                json.addProperty("token", "");
                write.write(json.toString());
            }
        }
        return file;
    }

}

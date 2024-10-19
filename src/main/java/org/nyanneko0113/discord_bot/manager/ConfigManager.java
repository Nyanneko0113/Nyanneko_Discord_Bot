package org.nyanneko0113.discord_bot.manager;

import com.google.gson.*;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.nyanneko0113.discord_bot.Main;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class ConfigManager {

    /*
    public static String getEmail() throws IOException {
        return getJson().get("niconico-email").getAsString();
    }

    public static String getPassword() throws IOException {
        return getJson().get("niconico-password").getAsString();
     }
     */

    public static TextChannel getSlashCommandLogChannel(String guild_id) throws IOException{
        JsonArray guild_array = getJson().getAsJsonArray("guild_setting");
        for (int n = 0; n < guild_array.size(); n++) {
            JsonObject guild_setting = guild_array.get(n).getAsJsonObject();
            System.out.print(guild_setting.toString());
            if (guild_setting.get("guild_id").getAsString().equalsIgnoreCase(guild_id)) {
                return Main.getJda().getTextChannelById(guild_setting.get("slashcommand_logchannel").getAsString());
            }
        }
        return null;
    }

    public static List<String> getBadWord(String guild_id) throws IOException{
        JsonArray guild_array = getJson().getAsJsonArray("guild_setting");
        List<String> bad_list = new ArrayList<>();
        for (int n = 0; n < guild_array.size(); n++) {
            JsonObject guild_setting = guild_array.get(n).getAsJsonObject();
            if (guild_setting.get("guild_id").getAsString().equalsIgnoreCase(guild_id)) {
                JsonArray bad_word = guild_setting.getAsJsonArray("bad_word");
                for (int i = 0; i < bad_word.size(); i++) {
                    bad_list.add(bad_word.get(n).getAsString());
                }

            }
        }
        return bad_list;
    }

    public static String getToken() throws IOException {
         return getJson().getAsJsonObject("bot_setting").get("token").getAsString();
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

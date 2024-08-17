package org.nyanneko0113.discord_bot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import org.nyanneko0113.discord_bot.commands.ReservedCommand;
import org.nyanneko0113.discord_bot.manager.ConfigManager;

import javax.security.auth.login.LoginException;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;

public class Main extends ListenerAdapter implements EventListener {

    private static JDA jda;

    public static void main(String[] strings) throws LoginException, InterruptedException, URISyntaxException, IOException {
        if (ConfigManager.getToken().equalsIgnoreCase("") || ConfigManager.getToken() == null) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("tokenを設定してください：");
            ConfigManager.setToken(reader.readLine());
        }

        if (jda == null) {
            jda = JDABuilder.createDefault(ConfigManager.getToken())
                    .addEventListeners(new Main())
                    .addEventListeners(new ReservedCommand())
                    .build();
            jda.awaitReady();

            jda.updateCommands()
                    .addCommands(Commands.slash("reserved-message", "予約投稿するコマンド")
                            .addOptions(new OptionData(OptionType.STRING, "date", "時間"),
                                    new OptionData(OptionType.STRING, "message", "メッセージ")))
                    .queue();
        }
    }

    public static JDA getJda() {
        return jda;
    }
}

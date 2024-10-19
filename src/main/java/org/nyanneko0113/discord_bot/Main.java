package org.nyanneko0113.discord_bot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import org.nyanneko0113.discord_bot.commands.*;
import org.nyanneko0113.discord_bot.commands.guild.GuildInfoCommand;
import org.nyanneko0113.discord_bot.listener.ButtonClickListener;
import org.nyanneko0113.discord_bot.listener.MessageListener;
import org.nyanneko0113.discord_bot.listener.SlashCommandListener;
import org.nyanneko0113.discord_bot.listener.UserJoinLeaveListener;
import org.nyanneko0113.discord_bot.manager.ConfigManager;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;

public class Main extends ListenerAdapter implements EventListener {

    private static JDA jda;

    public static void main(String[] strings) throws LoginException, InterruptedException, URISyntaxException, IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        if (ConfigManager.getToken().equalsIgnoreCase("") || ConfigManager.getToken() == null) {
            System.out.print("tokenを設定してください：");
            ConfigManager.setToken(reader.readLine());
        }

        if (jda == null) {
            jda = JDABuilder.createDefault(ConfigManager.getToken())
                    .enableIntents(GatewayIntent.GUILD_MEMBERS, GatewayIntent.MESSAGE_CONTENT)
                    .addEventListeners(new Main())
                    .addEventListeners(new ReservedCommand())
                    .addEventListeners(new MusicCommand())
                    .addEventListeners(new ButtonClickListener())
                    .addEventListeners(new WebHookCommand())
                    .addEventListeners(new EarthquakeCommand())
                    .addEventListeners(new UserInfoCommand())
                    .addEventListeners(new GuildInfoCommand())
                    .addEventListeners(new UserJoinLeaveListener())
                    .addEventListeners(new SlashCommandListener())
                    .addEventListeners(new MessageListener())
                    .build();
            jda.awaitReady();

            jda.updateCommands()
                    .addCommands(Commands.slash("reserved-message", "予約投稿するコマンド")
                            .addOptions(new OptionData(OptionType.STRING, "date", "時間"),
                                    new OptionData(OptionType.STRING, "message", "メッセージ")))
                    .addCommands(Commands.slash("play", "音楽を再生するコマンド")
                            .addOption(OptionType.STRING, "url", "URL"))
                    .addCommands(Commands.slash("skip", "スキップするコマンド"))
                    .addCommands(Commands.slash("play-info", "情報"))
                    .addCommands(Commands.slash("webhook", "a"))
                    .addCommands(Commands.slash("get-earthquake", "i"))
                    .addCommands(Commands.slash("user-info", "uu")
                            .addOption(OptionType.STRING, "user", "user"))
                    .addCommands(Commands.slash("guild-info", "ギルド"))
                    .queue();
        }

        System.out.print(">");
        while (true) {
            String line = reader.readLine();
            String prefix = "/";
            if (line.equalsIgnoreCase(prefix + "stop")) {
                jda.shutdownNow();
                System.exit(0);
                break;
            }
            else {
                System.out.print("そのコマンドは存在しません。" + "\n");
            }
            System.out.print(">");
        }
    }


    public static JDA getJda() {
        return jda;
    }
}

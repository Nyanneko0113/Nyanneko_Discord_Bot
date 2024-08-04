package org.nyanneko0113.discord_bot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

public class Main extends ListenerAdapter implements EventListener {

    private static JDA jda;
    private static final String token = "";

    public static void main(String[] strings) throws LoginException, InterruptedException, URISyntaxException, IOException {
        if (jda == null) {
            jda = JDABuilder.createDefault(token)
                    .addEventListeners(new Main())
                    .build();
            jda.awaitReady();

            CommandListUpdateAction commands = jda.updateCommands();
            commands.queue();
        }
    }

    public static JDA getJda() {
        return jda;
    }
}

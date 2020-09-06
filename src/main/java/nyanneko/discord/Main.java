package nyanneko.discord;

import javax.security.auth.login.LoginException;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import nyanneko.discord.commands.music.MusicCommand;
import nyanneko.discord.listener.MessageEditEvent;

public class Main implements EventListener {
    public static void main(String[] args) throws LoginException, InterruptedException {
        JDA jda = JDABuilder.createDefault("")
            .addEventListeners(new Main()) //メインクラス
            .addEventListeners(new MusicCommand())
            .addEventListeners(new MessageEditEvent())
            .build();
        jda.awaitReady();
    }

    @Override
    public void onEvent(GenericEvent event) {
        if (event instanceof ReadyEvent)
            System.out.println("API is ready!");
    }
}

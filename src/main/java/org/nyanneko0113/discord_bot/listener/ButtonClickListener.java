package org.nyanneko0113.discord_bot.listener;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.nyanneko0113.discord_bot.manager.music.MusicManager;

import java.util.HashMap;

public class ButtonClickListener extends ListenerAdapter {

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        MusicManager.GuildMusicManager guild_music = MusicManager.getInstance().getGuildAudioPlayer(event.getGuild());

        System.out.print(event.getChannel().asTextChannel());
        String button_id = event.getComponentId();

        HashMap<String, Integer> button = new HashMap<>();
        button.put("one_button", 0);
        button.put("two_button", 1);
        button.put("three_button", 2);
        button.put("four_button", 3);
        button.put("five_button", 4);

        for (String key : button.keySet()) {
            if (button_id.equals(key)) {
                event.deferEdit().complete();
                guild_music.click_track = guild_music.wait_track.get(button.get(key));
                MusicManager.getInstance().play(event.getGuild(), event.getChannel().asTextChannel(),guild_music, guild_music.click_track);
                break;
            }
        }

        guild_music.click_track = null;
        guild_music.wait_track.clear();
    }

}

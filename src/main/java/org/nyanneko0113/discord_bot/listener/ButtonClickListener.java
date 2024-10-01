package org.nyanneko0113.discord_bot.listener;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.nyanneko0113.discord_bot.manager.CommandButtonManager;
import org.nyanneko0113.discord_bot.manager.music.MusicManager;

import java.awt.*;
import java.util.HashMap;

public class ButtonClickListener extends ListenerAdapter {

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        User user = event.getUser();
        MusicManager music = MusicManager.getInstance(user);
        MusicManager.GuildMusicManager guild_music = music.getGuildAudioPlayer(event.getGuild());
        TextChannel text_channel = event.getChannel().asTextChannel();

        System.out.print(event.getChannel().asTextChannel());
        String button_id = event.getComponentId();

        HashMap<String, Integer> button = new HashMap<>();
        button.put("one_button", 0);
        button.put("two_button", 1);
        button.put("three_button", 2);
        button.put("four_button", 3);
        button.put("five_button", 4);

        if (CommandButtonManager.isRunUser("play", user)) {
            for (String key : button.keySet()) {
                if (button_id.equals(key)) {
                    event.deferEdit().complete();
                    guild_music.click_track = guild_music.getWaitTrack().get(button.get(key));
                    System.out.print(button + ":" + guild_music.click_track.getInfo().title + ":" + guild_music.getWaitTrack());
                    MusicManager.getInstance(user).play(event.getGuild(), event.getChannel().asTextChannel(),guild_music, guild_music.click_track);
                    break;
                }
            }
            guild_music.click_track = null;
            guild_music.getWaitTrack().clear();
        }
        else if (CommandButtonManager.isRunUser("play-info", user)) {
            if (button_id.equalsIgnoreCase("volume_up")){
                event.deferEdit().complete();
                music.setVolume(text_channel, music.getVolume(text_channel) + 10);
                EmbedBuilder embed = new EmbedBuilder();
                embed.addField("成功", "音量が10プラスされました。", false);
                embed.setColor(Color.GREEN);
                text_channel.sendMessageEmbeds(embed.build()).complete();
            }
            else if (button_id.equalsIgnoreCase("volume_down")){
                event.deferEdit().complete();
                music.setVolume(text_channel, music.getVolume(text_channel) - 10);
                EmbedBuilder embed = new EmbedBuilder();
                embed.addField("成功", "音量が10マイナスされました。", false);
                embed.setColor(Color.GREEN);
                text_channel.sendMessageEmbeds(embed.build()).complete();
            }
        }
        else {
            EmbedBuilder embed = new EmbedBuilder();
            embed.addField("失敗", "このボタンは押すことができません。", false);
            embed.setColor(Color.RED);
            event.replyEmbeds(embed.build()).queue();
        }
    }

}

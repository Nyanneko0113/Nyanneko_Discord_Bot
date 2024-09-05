package org.nyanneko0113.discord_bot.commands;

import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.requests.restaction.CacheRestAction;
import org.nyanneko0113.discord_bot.manager.music.MusicManager;

import java.awt.*;

public class MusicCommand extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        String cmd = event.getName();
        String sub_cmd = event.getSubcommandName();
        JDA jda = event.getJDA();
        MessageChannelUnion channel = event.getChannel();
        User user = event.getUser();
        CacheRestAction<PrivateChannel> private_channel = user.openPrivateChannel();
        Member member = event.getMember();
        MusicManager music = new MusicManager();

        if ("play".equalsIgnoreCase(cmd)) {
            OptionMapping option_url = event.getOption("url");

            music.loadAndPlay(channel.asTextChannel(), option_url.getAsString());
        }
        else if ("skip".equalsIgnoreCase(cmd)) {
            music.skipTrack(channel.asTextChannel());
        }
        else if ("play-info".equalsIgnoreCase(cmd)) {
            AudioTrackInfo info = music.getInfo(channel.asTextChannel());

            EmbedBuilder embed = new EmbedBuilder();
            if (info == null) {
                embed.addField("現在の再生情報：", "何も再生されてません。", false);
            }
            else {
                embed.setTitle("現在の再生情報");
                embed.addField("タイトル：", info.title, false);
                embed.addField("URL：", info.uri, false);
                embed.addField("時間：", info.length / 3600 + "時間" + info.length / 60 + "分" + info.length % 60 + "秒", false);
            }
            embed.setColor(Color.GREEN);
            channel.sendMessageEmbeds(embed.build()).complete();

        }
    }

}

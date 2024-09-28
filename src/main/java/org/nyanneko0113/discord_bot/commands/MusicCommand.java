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
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URISyntaxException;
import java.net.URL;

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
        MusicManager music = MusicManager.getInstance();

        if ("play".equalsIgnoreCase(cmd)) {
            OptionMapping option_url = event.getOption("url");

            try {
                URL url = new URL(option_url.getAsString());
                music.loadAndPlay(channel.asTextChannel(), url.toURI().toString());
            }
            catch (URISyntaxException | MalformedURLException e) {
                music.loadAndPlay(channel.asTextChannel(), "ytsearch:" + option_url.getAsString());
            }

        }
        else if ("skip".equalsIgnoreCase(cmd)) {
            music.skipTrack(channel.asTextChannel());
        }
        else if ("play-info".equalsIgnoreCase(cmd)) {
            MusicManager.GuildMusicManager guild_music = music.getGuildAudioPlayer(event.getGuild());

            EmbedBuilder embed = new EmbedBuilder();
            if (guild_music == null || guild_music.getTrack() == null) {
                embed.addField("現在の再生情報：", "何も再生されてません。", false);
            }
            else {
                AudioTrackInfo info = guild_music.getTrack().getInfo();
                embed.setTitle("現在の再生情報");
                embed.addField("タイトル：", info.title, false);
                embed.addField("URL：", info.uri, false);
                embed.addField("時間：", MusicManager.minToString(info.length), false);
            }
            embed.setColor(Color.GREEN);
            event.replyEmbeds(embed.build()).queue();

        }
    }

}

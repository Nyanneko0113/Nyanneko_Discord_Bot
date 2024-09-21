package org.nyanneko0113.discord_bot.manager.music;

import com.sedmelluq.discord.lavaplayer.container.MediaContainerRegistry;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.source.bandcamp.BandcampAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.beam.BeamAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.getyarn.GetyarnAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.http.HttpAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.soundcloud.SoundCloudAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.twitch.TwitchStreamAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.vimeo.VimeoAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import dev.lavalink.youtube.YoutubeAudioSourceManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.managers.AudioManager;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class MusicManager {
    private static final MusicManager instance = new MusicManager();
    private final AudioPlayerManager playerManager = new DefaultAudioPlayerManager();
    private final Map<Long, GuildMusicManager> musicManagers = new HashMap<>();
    YoutubeAudioSourceManager yt = new YoutubeAudioSourceManager(true);

    public MusicManager() {
        AudioSourceManagers.registerLocalSource(playerManager);

        playerManager.registerSourceManager(yt);
        playerManager.registerSourceManager(SoundCloudAudioSourceManager.createDefault());
        playerManager.registerSourceManager(new BandcampAudioSourceManager());
        playerManager.registerSourceManager(new VimeoAudioSourceManager());
        playerManager.registerSourceManager(new TwitchStreamAudioSourceManager());
        playerManager.registerSourceManager(new BeamAudioSourceManager());
        playerManager.registerSourceManager(new GetyarnAudioSourceManager());
        playerManager.registerSourceManager(new HttpAudioSourceManager(MediaContainerRegistry.DEFAULT_REGISTRY));
    }
    
    public static MusicManager getInstance() {
        return instance;
    }

    public synchronized GuildMusicManager getGuildAudioPlayer(Guild guild) {
        long guildId = Long.parseLong(guild.getId());
        GuildMusicManager musicManager = musicManagers.get(guildId);

        if (musicManager == null) {
            musicManager = new GuildMusicManager(playerManager);
            musicManagers.put(guildId, musicManager);
            guild.getAudioManager().setSendingHandler(musicManager.getSendHandler());
        }

        return musicManager;
    }


    public static String minToString(long n) {
        long time = n / 1000;
        int hour = (int) (time / 3600);
        int min = (int) (time / 60);
        int sec = (int) (time % 60);

        return hour + "時間" + min + "分" + sec + "秒";
    }

    public void loadAndPlay(final TextChannel channel, final String trackUrl) {
        GuildMusicManager musicManager = getGuildAudioPlayer(channel.getGuild());
        playerManager.loadItem(trackUrl, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                AudioTrackInfo info = track.getInfo();

                EmbedBuilder embed = new EmbedBuilder();
                embed.setTitle("音楽が再生されます");
                embed.addField("タイトル：", info.title, false);
                embed.addField("URL：", info.uri, false);
                embed.addField("時間：", minToString(info.length), false);
                embed.setColor(Color.GREEN);
                channel.sendMessageEmbeds(embed.build()).complete();

                play(channel.getGuild(), musicManager, track);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                AudioTrack firstTrack = playlist.getSelectedTrack();

                if (firstTrack == null) {
                    firstTrack = playlist.getTracks().get(0);
                }

                AudioTrackInfo info = firstTrack.getInfo();

                EmbedBuilder embed = new EmbedBuilder();
                embed.setTitle("音楽が再生されます（プレイリスト：" + playlist.getName() + ")");
                embed.addField("タイトル：", info.title, false);
                embed.addField("URL：", info.uri, false);
                embed.addField("時間：", minToString(info.length), false);
                embed.setColor(Color.GREEN);
                channel.sendMessageEmbeds(embed.build()).complete();

                play(channel.getGuild(), musicManager, firstTrack);
            }

            @Override
            public void noMatches() {
                EmbedBuilder embed = new EmbedBuilder();
                embed.addField("エラー",  trackUrl + " は見つかりませんでした", false);
                embed.setColor(Color.RED);
                channel.sendMessageEmbeds(embed.build()).complete();
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                EmbedBuilder embed = new EmbedBuilder();
                embed.addField("エラー",  "エラーが発生しました：" + exception.getMessage(), false);
                embed.setColor(Color.RED);
                channel.sendMessageEmbeds(embed.build()).complete();
            }
        });
    }

    private void play(Guild guild, GuildMusicManager musicManager, AudioTrack track) {
        connectToFirstVoiceChannel(guild.getAudioManager());

        if (musicManager.player.getPlayingTrack() == null) {
            musicManager.player.playTrack(track);
        }
        else {
            musicManager.scheduler.queue(track);
        }
    }

    public void skipTrack(TextChannel channel) {
        GuildMusicManager musicManager = getGuildAudioPlayer(channel.getGuild());

        EmbedBuilder embed = new EmbedBuilder();

        AudioTrackInfo info = musicManager.player.getPlayingTrack().getInfo();
        embed.setTitle("音楽はスキップされます");
        embed.addField("次のタイトル：", info.title, false);
        embed.addField("URL：", info.uri, false);
        embed.addField("時間：", minToString(info.length), false);
        embed.setColor(Color.GREEN);
        channel.sendMessageEmbeds(embed.build()).complete();

        musicManager.scheduler.nextTrack();
    }

    private static void connectToFirstVoiceChannel(AudioManager audioManager) {
        if (!audioManager.isConnected()) {
            for (VoiceChannel voiceChannel : audioManager.getGuild().getVoiceChannels()) {
                audioManager.openAudioConnection(voiceChannel);
                break;
            }
        }
    }


    public static class GuildMusicManager {
        public final AudioPlayer player;
        public final TrackScheduler scheduler;

        public GuildMusicManager(AudioPlayerManager manager) {
            player = manager.createPlayer();
            scheduler = new TrackScheduler(player);
            player.addListener(scheduler);
        }

        public AudioPlayerSendHandler getSendHandler() {
            return new AudioPlayerSendHandler(player);
        }

        public AudioTrack getTrack() {
            return player.getPlayingTrack();
        }
    }

}

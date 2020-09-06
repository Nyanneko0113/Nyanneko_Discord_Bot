package nyanneko.discord.api.music;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.tools.io.MessageOutput;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.managers.AudioManager;



public class PlayerManager {

	  private final AudioPlayerManager playerManager;
	  private final Map<Long, GuildMusicManager> musicManagers;

	  public PlayerManager() {
		    this.musicManagers = new HashMap<>();
		    this.playerManager = new DefaultAudioPlayerManager();
		    AudioSourceManagers.registerRemoteSources(playerManager);
		    AudioSourceManagers.registerLocalSource(playerManager);
	 }

	  private synchronized GuildMusicManager getGuildAudioPlayer(Guild guild) {
		    long guildId = Long.parseLong(guild.getId());
		    GuildMusicManager musicManager = musicManagers.get(guildId);

		    if (musicManager == null) {
		      musicManager = new GuildMusicManager(playerManager);
		      musicManagers.put(guildId, musicManager);
		    }

		    guild.getAudioManager().setSendingHandler(musicManager.getSendHandler());

		    return musicManager;
		  }

	  public String encode(AudioTrack track) throws IOException {
		  ByteArrayOutputStream stream = new ByteArrayOutputStream();
		  playerManager.encodeTrack(new MessageOutput(stream), track);
		  byte[] encoded = Base64.getEncoder().encode(stream.toByteArray());
		  return new String(encoded);
		}

	  public void volume(TextChannel channel, int volume) {
		  GuildMusicManager musicManager = getGuildAudioPlayer(channel.getGuild());
		  musicManager.player.setVolume(volume);
		  channel.sendMessage("ボリュームを" + String.valueOf(musicManager.player.getVolume()) + "から" + String.valueOf(volume) + "にしました。").queue();;
	  }

      public AudioTrackInfo trackinfo(AudioTrack track) {
    	  return track.getInfo();
      }

	  public void loadAndPlay(final TextChannel channel, final String trackUrl) {
		    GuildMusicManager musicManager = getGuildAudioPlayer(channel.getGuild());
		    EmbedBuilder em = new EmbedBuilder();
		    playerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {
		      @Override
		      public void trackLoaded(AudioTrack track) {
		        channel.sendMessage("キューを追加しました。" + trackinfo(track).title).queue();
                em.setTitle("キューを追加しました。")
                     .addField("タイトル", trackinfo(track).title, false);
                channel.sendMessage(em.build()).queue();;
		        play(channel.getGuild(), musicManager, track);
		      }

		      @Override
		      public void playlistLoaded(AudioPlaylist playlist) {
		        AudioTrack firstTrack = playlist.getSelectedTrack();

		        if (firstTrack == null) {
		          firstTrack = playlist.getTracks().get(0);
		        }

		        channel.sendMessage("Adding to queue " + firstTrack.getInfo().title + " (first track of playlist " + playlist.getName() + ")").queue();

		        play(channel.getGuild(), musicManager, firstTrack);
		      }

		      @Override
		      public void noMatches() {
		          em.setTitle("エラー")
		             .setAuthor("動画が見つかりませんでした。")
		            .setColor(Color.RED);
		          channel.sendMessage(em.build()).queue();
		      }

		      @Override
		      public void loadFailed(FriendlyException exception) {
		    	String message = exception.getMessage();
		    	message.replace("", "");

		        channel.sendMessage("Could not play: " + exception.getMessage()).queue();
		      }
		    });
		  }

	  private void play(Guild guild, GuildMusicManager musicManager, AudioTrack track) {
		    connectToFirstVoiceChannel(guild.getAudioManager());
		    musicManager.scheduler.queue(track);
		  }

		  private void skipTrack(TextChannel channel) {
		    GuildMusicManager musicManager = getGuildAudioPlayer(channel.getGuild());
		    musicManager.scheduler.nextTrack();
		    channel.sendMessage("再生されている曲をスキップしました。").queue();
		  }

		  private static void connectToFirstVoiceChannel(AudioManager audioManager) {
		    if (!audioManager.isConnected() && !audioManager.isAttemptingToConnect()) {
		      for (VoiceChannel voiceChannel : audioManager.getGuild().getVoiceChannels()) {
		        audioManager.openAudioConnection(voiceChannel);
		        break;
		      }
		    }
   }
}

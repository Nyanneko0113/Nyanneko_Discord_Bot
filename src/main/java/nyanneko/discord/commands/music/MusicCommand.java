package nyanneko.discord.commands.music;

import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.AudioManager;
import nyanneko.discord.api.music.PlayerManager;
import nyanneko.discord.api.music.WordSearch;

public class MusicCommand extends ListenerAdapter {

	  @Override
	  public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
		  String[] args = event.getMessage().getContentRaw().split(" ");
		  TextChannel channel = event.getChannel();
		  AudioManager audio = event.getGuild().getAudioManager();
		  PlayerManager pm = new PlayerManager();
          if (args[0].equalsIgnoreCase(".join")) {
        	  audio.openAudioConnection(event.getMember().getVoiceState().getChannel());
        	  channel.sendMessage("接続しました。").queue();;
          }
          else if (args[0].equalsIgnoreCase(".volume")) {
        	  pm.volume(channel, Integer.parseInt(args[1]));
          }
          else if (args[0].equalsIgnoreCase(".play")) {
        	  new WordSearch().world(args[1], channel);;
          }
	  }
}

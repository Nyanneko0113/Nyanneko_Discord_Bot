package org.nyanneko0113.discord_bot.listener;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.requests.restaction.CacheRestAction;
import org.nyanneko0113.discord_bot.manager.ConfigManager;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SlashCommandListener extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        String cmd = event.getName();
        String sub_cmd = event.getSubcommandName();
        JDA jda = event.getJDA();
        MessageChannelUnion channel = event.getChannel();
        User user = event.getUser();
        CacheRestAction<PrivateChannel> private_channel = user.openPrivateChannel();
        Member member = event.getMember();
        List<OptionMapping> option_list = event.getOptions();

        try {
            if (ConfigManager.getSlashCommandLogChannel(event.getGuild().getId()) != null) {
                String options = option_list.stream()
                        .map(option -> "(" + option.getName() + ": " + option.getAsString() + ")")
                        .collect(Collectors.joining(", "));

                EmbedBuilder embed = new EmbedBuilder();
                embed.setTitle("Slashコマンドログ");
                embed.addField("ギルド", event.getGuild().getName(), false);
                embed.addField("実行したユーザー", member.getAsMention() + "(" + user.getId() + ")", false);
                embed.addField("コマンド", cmd + "," + options, false);

                ConfigManager.getSlashCommandLogChannel(event.getGuild().getId()).sendMessageEmbeds(embed.build()).queue();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

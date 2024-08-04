package org.nyanneko0113.discord_bot.commands;

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
import org.nyanneko0113.discord_bot.manager.ReservedManager;

import java.text.ParseException;
import java.util.Date;

public class ReservedCommand extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        String cmd = event.getName();
        String sub_cmd = event.getSubcommandName();
        JDA jda = event.getJDA();
        MessageChannelUnion channel = event.getChannel();
        User user = event.getUser();
        CacheRestAction<PrivateChannel> private_channel = user.openPrivateChannel();
        Member member = event.getMember();

        if ("reserved-message".equalsIgnoreCase(cmd)) {
            OptionMapping option_date = event.getOption("date");
            OptionMapping option_message = event.getOption("message");

            try {
                Date date = ReservedManager.stringToDate(option_date.getAsString());
                ReservedManager.addReserved(date, option_message.getAsString(), event.getChannel().asTextChannel());

                EmbedBuilder embed = new EmbedBuilder();
                embed.addField("成功", option_date + "に投稿されます。", false);
                event.deferReply().addEmbeds(embed.build()).queue();


            } catch (ParseException e) {
                throw new RuntimeException(e);
            }

        }
    }

}

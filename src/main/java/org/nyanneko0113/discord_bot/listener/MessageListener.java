package org.nyanneko0113.discord_bot.listener;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.RestAction;
import org.nyanneko0113.discord_bot.Main;
import org.nyanneko0113.discord_bot.manager.ConfigManager;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.temporal.TemporalAccessor;

public class MessageListener extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        Message message = event.getMessage();
        String message_string = message.getContentRaw();
        Guild guild = event.getGuild();

        try {
            if (ConfigManager.getBadWord(guild.getId()).contains(message_string)) {
                message.delete().queue();
                EmbedBuilder embed = new EmbedBuilder();
                embed.addField("禁止ワード", "そのワードは送ることができません", false);
                event.getChannel().asTextChannel().sendMessageEmbeds(embed.build()).complete();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}

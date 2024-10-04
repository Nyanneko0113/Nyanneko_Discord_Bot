package org.nyanneko0113.discord_bot.commands;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.restaction.CacheRestAction;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class WebHookCommand extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        String cmd = event.getName();
        String sub_cmd = event.getSubcommandName();
        JDA jda = event.getJDA();
        MessageChannelUnion channel = event.getChannel();
        User user = event.getUser();
        CacheRestAction<PrivateChannel> private_channel = user.openPrivateChannel();
        Member member = event.getMember();

        if ("webhook".equalsIgnoreCase(cmd)) {
            boolean check = true;
            AtomicInteger n = new AtomicInteger();
            Message message = channel.sendMessage("削除中...").complete();
            while (check) {
                List<Message> list = channel.asTextChannel().getHistory().retrievePast(100).complete();

                if (list.isEmpty()) {
                    break;
                }

                for (Message a : list) {
                    OffsetDateTime now = OffsetDateTime.now();
                    if (a.getTimeCreated().isAfter(OffsetDateTime.of(now.getYear(), now.getMonth().getValue(), now.getDayOfMonth(), 0, 0, 0, 0, ZoneOffset.UTC))) {
                        if (a.isWebhookMessage()) {
                            a.delete().queue();
                            n.getAndIncrement();

                            if (list.isEmpty()) {
                                check=false;
                                break;
                            }
                            message.editMessage( "webhookを"  +n + "件削除しました。").complete();
                        }
                    }
                }
            }





        }
    }
}

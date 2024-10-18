package org.nyanneko0113.discord_bot.listener;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class UserJoinLeaveListener extends ListenerAdapter {

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        User get_user = event.getUser();
        Member get_member = event.getMember();

        if (event.getGuild().getId().equalsIgnoreCase("1117768636921298989")) {
            TextChannel channel = event.getGuild().getTextChannelById("1117768637957279877");
            EmbedBuilder embed = new EmbedBuilder();
            embed.setTitle("ユーザーがサーバーに参加しました。");
            embed.addField("名前", get_user.getName(), false);
            embed.addField("作成された日", get_user.getTimeCreated().format(DateTimeFormatter.ofPattern("yyyy/MM/dd hh:mm:ss")), false);
            embed.addField("参加した日", get_member.getTimeJoined().format(DateTimeFormatter.ofPattern("yyyy/MM/dd hh:mm:ss")), false);
            embed.addField("BOT？", get_user.isBot() ? "はい" : "いいえ", false);
            channel.sendMessageEmbeds(embed.build()).queue();
        }
    }

    @Override
    public void onGuildMemberRemove(GuildMemberRemoveEvent event) {
        User get_user = event.getUser();
        Member get_member = event.getMember();

        OffsetDateTime join = get_member.getTimeJoined();
        OffsetDateTime now = new Date(System.currentTimeMillis())
                .toInstant()
                .atOffset(ZoneOffset.UTC)
                .minusYears(join.getYear())
                .minusMonths(join.getMonthValue())
                .minusDays(join.getDayOfMonth())
                .minusHours(join.getHour())
                .minusMinutes(join.getMinute())
                .minusSeconds(join.getSecond());
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss");
        String join_string = join.format(format);
        String now_string = new Date(System.currentTimeMillis()).toInstant().atOffset(ZoneOffset.UTC).format(format);
        String between_string = now.format(DateTimeFormatter.ofPattern("yyyy年MMか月dd日 hh時間mm分ss秒"));
        if (event.getGuild().getId().equalsIgnoreCase("1117768636921298989")) {
            TextChannel channel = event.getGuild().getTextChannelById("1117768637957279877");
            EmbedBuilder embed = new EmbedBuilder();
            embed.setTitle("ユーザーがサーバーから抜けました。");
            embed.addField("名前", get_user.getName(), false);
            embed.addField("滞在期間（" + join_string + "～" + now_string + ")", between_string, false);
            channel.sendMessageEmbeds(embed.build()).queue();
        }
    }

}

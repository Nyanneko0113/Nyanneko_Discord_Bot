package org.nyanneko0113.discord_bot.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Mentions;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.requests.restaction.CacheRestAction;

import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

public class UserInfoCommand extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        String cmd = event.getName();
        String sub_cmd = event.getSubcommandName();
        JDA jda = event.getJDA();
        MessageChannelUnion channel = event.getChannel();
        User user = event.getUser();
        CacheRestAction<PrivateChannel> private_channel = user.openPrivateChannel();
        Member member = event.getMember();

        if ("user-info".equalsIgnoreCase(cmd)) {
            OptionMapping option_user = event.getOption("user");
            String user_id = option_user.getAsString();
            User get_user = jda.retrieveUserById(user_id).complete();
            Member get_member = event.getGuild().getMember(get_user);

            EmbedBuilder embed = new EmbedBuilder();
            embed.addField("名前", get_user.getName(), false);
            embed.addField("作成された日", get_user.getTimeCreated().format(DateTimeFormatter.ofPattern("yyyy/MM/dd hh:mm:ss")), false);
            embed.addField("参加した日", get_member.getTimeJoined().format(DateTimeFormatter.ofPattern("yyyy/MM/dd hh:mm:ss")), false);
            embed.addField("BOT？", get_user.isBot() ? "はい" : "いいえ", false);
            embed.addField("ギルド", String.join(",", get_user.getMutualGuilds().stream().map(Guild::getName).collect(Collectors.toSet())), false);
            embed.setThumbnail(get_user.getAvatarUrl());
            channel.sendMessageEmbeds(embed.build()).queue();
        }
    }
}

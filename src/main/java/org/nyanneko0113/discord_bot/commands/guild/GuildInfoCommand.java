package org.nyanneko0113.discord_bot.commands.guild;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.restaction.CacheRestAction;

import java.util.stream.Collectors;

public class GuildInfoCommand extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        String cmd = event.getName();
        String sub_cmd = event.getSubcommandName();
        JDA jda = event.getJDA();
        MessageChannelUnion channel = event.getChannel();
        User user = event.getUser();
        CacheRestAction<PrivateChannel> private_channel = user.openPrivateChannel();
        Member member = event.getMember();

        if ("guild-info".equalsIgnoreCase(cmd)) {
            Guild guild = event.getGuild();

            guild.loadMembers().onSuccess(a-> {
                EmbedBuilder embed = new EmbedBuilder();
                embed.addField("ギルドの人数", guild.getMemberCount() + "(Botを除いた人数:" + a.stream().filter(i->!i.getUser().isBot()).collect(Collectors.toSet()).size() + ")", false);
                embed.addField("ロール", String.join(" , ", guild.getRoles().stream().map(Role::getName).collect(Collectors.toSet())), false);
                embed.addField("チャンネル", String.join(" , ", guild.getChannels().stream().map(GuildChannel::getName).collect(Collectors.toSet())), false);
                embed.setImage(guild.getBannerUrl());

                event.deferReply(true).addEmbeds(embed.build()).queue();
            });

        }
    }
}

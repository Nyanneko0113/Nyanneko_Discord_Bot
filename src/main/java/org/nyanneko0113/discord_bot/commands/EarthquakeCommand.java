package org.nyanneko0113.discord_bot.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.requests.restaction.CacheRestAction;
import org.nyanneko0113.discord_bot.manager.EarthquakeManager;
import org.nyanneko0113.discord_bot.manager.ReservedManager;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;

public class EarthquakeCommand extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        String cmd = event.getName();
        String sub_cmd = event.getSubcommandName();
        JDA jda = event.getJDA();
        MessageChannelUnion channel = event.getChannel();
        User user = event.getUser();
        CacheRestAction<PrivateChannel> private_channel = user.openPrivateChannel();
        Member member = event.getMember();

        if ("get-earthquake".equalsIgnoreCase(cmd)) {
            OptionMapping option_date = event.getOption("date");
            OptionMapping option_message = event.getOption("message");

            EmbedBuilder embed_before = new EmbedBuilder();
            embed_before.addField("情報を取得しています", "しばらくお待ちください", false);
            Message message = channel.sendMessageEmbeds(embed_before.build()).complete();

            EmbedBuilder embed_after = new EmbedBuilder();
            try {
                embed_after.addField("震源地：", EarthquakeManager.getEarthquakeName(), false);
                embed_after.addField("観測震源：", String.join(",", EarthquakeManager.getAddrs()), false);
            } catch (IOException e) {
                embed_after.addField("取得中にエラーが発生しました。", "エラー：" + e.getMessage(), false);
            }
            channel.editMessageEmbedsById(message.getId(), embed_after.build()).queue();
        }
    }

}

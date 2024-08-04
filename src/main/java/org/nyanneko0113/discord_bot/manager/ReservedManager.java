package org.nyanneko0113.discord_bot.manager;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class ReservedManager {

    private static final List<Reserved> reserved_list = new ArrayList<>();

    public static void addReserved(Date date, String message, TextChannel channel) {
        reserved_list.add(new Reserved(date == null ? new Date(System.currentTimeMillis()) : date, message, channel));
    }

    public static List<Reserved> getReserveds() {
        return reserved_list;
    }

    public static Date stringToDate(String time) throws ParseException {
        SimpleDateFormat simple_date = new SimpleDateFormat("yyyy/MM/dd hh:mm");
        return simple_date.parse(time);
    }

    public static class Reserved {
        private Date date;
        private String message;
        private final TimerTask task;

        public Reserved(Date date, String message, TextChannel channel) {
            this.date = date;
            this.message = message;

            Timer timer = new Timer();
            this.task = new TimerTask() {
                @Override
                public void run() {
                    if (date.getTime() <= System.currentTimeMillis()) {
                        EmbedBuilder embed = new EmbedBuilder();
                        embed.addField("予約投稿", message, false);
                        channel.sendMessageEmbeds(embed.build()).queue();

                        this.cancel();
                    }
                }
            };
            timer.scheduleAtFixedRate(this.task, 0L, 1000L);
        }

        public Date getDate() {
            return date;
        }

        public void setDate(Date date) {
            this.date = date;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

}

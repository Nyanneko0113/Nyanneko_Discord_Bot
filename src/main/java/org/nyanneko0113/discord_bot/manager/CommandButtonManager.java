package org.nyanneko0113.discord_bot.manager;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.Command;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CommandButtonManager {

    private static final List<CommandButton> command_manager = new ArrayList<>();

    public static CommandButton addCommandButton(String cmd, Date date, User run_user) {
        CommandButton button = new CommandButton(cmd, date, run_user);
        command_manager.add(button);
        return button;
    }

    public static void removeCommandButton(String cmd, User run_user) {
        command_manager.stream()
                .filter(a->a.cmd_name.equalsIgnoreCase(cmd))
                .filter(a->a.getUser().getId().equalsIgnoreCase(run_user.getId()))
                .forEach(CommandButton::removeButtonAccess);
    }

    public static boolean isRunUser(String cmd, User user) {
        return command_manager.stream().filter(a->a.getCmd().equalsIgnoreCase(cmd)).anyMatch(a->a.getUser().getId().equalsIgnoreCase(user.getId()));
    }

    public static class CommandButton {
        private final Date date;
        private final User run_user;
        private final String cmd_name;

        public CommandButton(String cmd, Date date, User run_user) {
            this.date = date;
            this.run_user = run_user;
            this.cmd_name = cmd;
        }

        public Date getDate() {
            return date;
        }

        public User getUser() {
            return run_user;
        }

        public String getCmd() {
            return cmd_name;
        }

        public void removeButtonAccess() {
            command_manager.remove(this);
        }
    }
}

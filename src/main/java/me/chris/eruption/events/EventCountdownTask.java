package me.chris.eruption.events;

import lombok.Getter;
import lombok.Setter;
import me.chris.eruption.util.random.Clickable;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

@Setter
@Getter
public abstract class EventCountdownTask extends BukkitRunnable {
    private final PracticeEvent event;
    private final int countdownTime;

    private int timeUntilStart;
    private boolean ended;

    public EventCountdownTask(PracticeEvent event, int countdownTime) {
        this.event = event;
        this.countdownTime = countdownTime;
        this.timeUntilStart = countdownTime;
    }

    @Override
    public void run() {
        if (isEnded()) {
            return;
        }

        if (timeUntilStart <= 0) {
            if (canStart()) {
                event.start();
            } else {
                onCancel();
            }

            ended = true;
            return;
        }

        if (shouldAnnounce(timeUntilStart)) {
            String toSend0 = ChatColor.translateAlternateColorCodes('&', "                        ");
            String toSend = ChatColor.translateAlternateColorCodes('&', "&7⬛⬛⬛⬛⬛⬛⬛⬛");
            String toSend1 = ChatColor.translateAlternateColorCodes('&', "&7⬛⬛&c⬛⬛⬛⬛&7⬛⬛");
            String toSend2 = ChatColor.translateAlternateColorCodes('&', "&7⬛⬛&c⬛&7⬛⬛⬛⬛⬛ " + ChatColor.GREEN.toString() + ChatColor.BOLD + "[" + event.getName() + " Event]");
            String toSend3 = ChatColor.translateAlternateColorCodes('&', "&7⬛⬛&c⬛⬛⬛⬛&7⬛⬛ " + ChatColor.YELLOW + "is being hosted by " + event.getHost().getName());
            String toSend4 = ChatColor.translateAlternateColorCodes('&', "&7⬛⬛&c⬛&7⬛⬛⬛⬛⬛ " + ChatColor.GREEN.toString() + ChatColor.BOLD + "Click to Join");
            String toSend5 = ChatColor.translateAlternateColorCodes('&', "&7⬛⬛&c⬛⬛⬛⬛&7⬛⬛");
            String toSend6 = ChatColor.translateAlternateColorCodes('&', "&7⬛⬛⬛⬛⬛⬛⬛⬛");
            String toSend7 = ChatColor.translateAlternateColorCodes('&', "                        ");

            Clickable message0 = new Clickable(toSend0,
                    ChatColor.GREEN + "Click to join this commands.",
                    "/joinevent " + event.getName());
            Clickable message7 = new Clickable(toSend7,
                    ChatColor.GREEN + "Click to join this commands.",
                    "/joinevent " + event.getName());
            Clickable message = new Clickable(toSend,
                    ChatColor.GREEN + "Click to join this commands.",
                    "/joinevent " + event.getName());
            Clickable message1 = new Clickable(toSend1,
                    ChatColor.GREEN + "Click to join this commands.",
                    "/joinevent " + event.getName());
            Clickable message2 = new Clickable(toSend2,
                    ChatColor.GREEN + "Click to join this commands.",
                    "/joinevent " + event.getName());
            Clickable message3 = new Clickable(toSend3,
                    ChatColor.GREEN + "Click to join this commands.",
                    "/joinevent " + event.getName());
            Clickable message4 = new Clickable(toSend4,
                    ChatColor.GREEN + "Click to join this commands.",
                    "/joinevent " + event.getName());
            Clickable message5 = new Clickable(toSend5,
                    ChatColor.GREEN + "Click to join this commands.",
                    "/joinevent " + event.getName());
            Clickable message6 = new Clickable(toSend6,
                    ChatColor.GREEN + "Click to join this commands.",
                    "/joinevent " + event.getName());

            event.getPlugin().getServer().getOnlinePlayers().forEach(message0::sendToPlayer);
            event.getPlugin().getServer().getOnlinePlayers().forEach(message::sendToPlayer);
            event.getPlugin().getServer().getOnlinePlayers().forEach(message1::sendToPlayer);
            event.getPlugin().getServer().getOnlinePlayers().forEach(message2::sendToPlayer);
            event.getPlugin().getServer().getOnlinePlayers().forEach(message3::sendToPlayer);
            event.getPlugin().getServer().getOnlinePlayers().forEach(message4::sendToPlayer);
            event.getPlugin().getServer().getOnlinePlayers().forEach(message5::sendToPlayer);
            event.getPlugin().getServer().getOnlinePlayers().forEach(message6::sendToPlayer);
            event.getPlugin().getServer().getOnlinePlayers().forEach(message7::sendToPlayer);
        }

        timeUntilStart--;
    }

    public abstract boolean shouldAnnounce(int timeUntilStart);

    public abstract boolean canStart();

    public abstract void onCancel();
}

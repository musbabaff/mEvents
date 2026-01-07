package mEvents.blockstock.mEvents.utils;

import mEvents.blockstock.mEvents.EventScheduler;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.clip.placeholderapi.expansion.Relational;
import org.bukkit.entity.Player;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;


public class mEventsExpansion extends PlaceholderExpansion implements Relational {

    private final mEvents.blockstock.mEvents.mEvents plugin;

    public mEventsExpansion(mEvents.blockstock.mEvents.mEvents plugin) {
        this.plugin = plugin;
    }


    @Override
    public @NotNull String getIdentifier() {
        return "mEvents";
    }

    @Override
    public @NotNull String getAuthor() {
        return "musbabaff";
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        if (params.equalsIgnoreCase("bossname")) {
            String displayName = plugin.getEventScheduler().getDisplayName();
            return displayName != null ? displayName : "";
        }
        if (params.equalsIgnoreCase("time")) {
            int minutes = plugin.getEventScheduler().getMinutes();
            return minutes + "m";
        }
        if (params.equalsIgnoreCase("coordinates")) {
            EventScheduler scheduler = plugin.getEventScheduler();
            // Verify event status: ensure coordinates are initialized (not 0,0,0)
            // and mobName is not null (assigned during startEvent)
            String mobName = scheduler.getmobName();
            if (mobName != null && !mobName.isEmpty()) {
                double x = scheduler.getX();
                double y = scheduler.getY();
                double z = scheduler.getZ();
                if (x != 0 || y != 0 || z != 0) {
                    return ("X: %x% Y: %y% Z: %z%")
                            .replace("%x%", String.format("%.1f", x))
                            .replace("%y%", String.format("%.1f", y))
                            .replace("%z%", String.format("%.1f", z));
                }
            }
            return plugin.getMessagesManager().coordinatesnone();
        }
        return null;
    }

    @Override
    public String onPlaceholderRequest(Player player, Player player1, String s) {
        return "";
    }
}
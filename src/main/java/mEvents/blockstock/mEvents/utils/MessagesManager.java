package mEvents.blockstock.mEvents.utils;

import mEvents.blockstock.mEvents.Async;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class MessagesManager {

    private final mEvents.blockstock.mEvents.mEvents plugin;
    private FileConfiguration messagesconfig;
    private File messagesConfigFile;

    public MessagesManager(mEvents.blockstock.mEvents.mEvents plugin) {
        this.plugin = plugin;
        loadFiles();
    }

    @Async
    private void loadFiles() {
        messagesConfigFile = new File(plugin.getDataFolder(), "messages.yml");
        if (!messagesConfigFile.exists()) {
            plugin.saveResource("messages.yml", false);
        }
        messagesconfig = YamlConfiguration.loadConfiguration(messagesConfigFile);
    }

    public String broadcast15(String mobName) {
        return messagesconfig.getString("broadcast-15", "§6[Events] §fBoss §e%mobName% §fwill spawn in 15 minutes!")
                .replace("%mobName%", mobName);
    }

    public String broadcast10(String mobName) {
        return messagesconfig.getString("broadcast-10", "§6[Events] §fBoss §e%mobName% §fwill spawn in 10 minutes!")
                .replace("%mobName%", mobName);
    }

    public String broadcast5(String mobName) {
        return messagesconfig.getString("broadcast-5", "§6[Events] §fBoss §e%mobName% §fwill spawn in 5 minutes!")
                .replace("%mobName%", mobName);
    }

    public String broadcast3(String mobName, double locX, double locY, double locZ) {
        return messagesconfig.getString("broadcast-3", "§6[Events] §fBoss §e%mobName% §fwill spawn in 3 minutes!\\nCoordinates: X: %x% Y: %y% Z: %z%")
                .replace("%mobName%", mobName)
                .replace("%x%", String.format("%.1f", locX))
                .replace("%y%", String.format("%.1f", locY))
                .replace("%z%", String.format("%.1f", locZ));
    }

    public String broadcastspawned(String mobName, double locX, double locY, double locZ) {
        return messagesconfig.getString("broadcastspawned", "§6[Events] §fBoss §e%mobName% §f has spawned!\nCoordinates: X: %x% Y: %y% Z: %z%")
                .replace("%mobName%", mobName)
                .replace("%x%", String.format("%.1f", locX))
                .replace("%y%", String.format("%.1f", locY))
                .replace("%z%", String.format("%.1f", locZ));
    }

    public String nopermission() {
        return messagesconfig.getString("no-permission", "§6[Events] §cYou don't have permission.");
    }

    public String reloadplugin() {
        // "Бип-пуп" (Beep-boop) kısmını eğlenceli bir şekilde korudum
        return messagesconfig.getString("reload-plugin", "§6[Events] §aBeep-boop! Reload successful.");
    }

    public String coordinatesnone() {
        return messagesconfig.getString("coordinates-not", "§fCoordinates are not set.");
    }
}
package mEvents.blockstock.mEvents.utils;

import mEvents.blockstock.mEvents.Async;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.List;
import java.util.Map;

public class ConfigManager {

    private final mEvents.blockstock.mEvents.mEvents plugin;
    private FileConfiguration config;
    private File configFile;

    public ConfigManager(mEvents.blockstock.mEvents.mEvents plugin) {
        this.plugin = plugin;
        loadFiles();
    }

    @Async
    private void loadFiles() {
        configFile = new File(plugin.getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            plugin.saveResource("config.yml", false);
        }
        config = YamlConfiguration.loadConfiguration(configFile);
    }

    public String getWorldName() {
        return config.getString("world-spawn", "world");
    }

    public int getXFrom() {
        return config.getInt("coordinates.X.From", -2500);
    }

    public int getXTo() {
        return config.getInt("coordinates.X.To", 2500);
    }

    public int getZFrom() {
        return config.getInt("coordinates.Z.From", -2500);
    }

    public int getZTo() {
        return config.getInt("coordinates.Z.To", 2500);
    }

    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getMobsList() {
        return (List<Map<String, Object>>) (List<?>) config.getMapList("mythicmobs");
    }
}

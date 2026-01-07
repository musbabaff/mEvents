package mEvents.blockstock.mEvents;

import mEvents.blockstock.mEvents.commands.mEventsCommand;
import mEvents.blockstock.mEvents.utils.BossRegionManager;
import mEvents.blockstock.mEvents.utils.ConfigManager;
import mEvents.blockstock.mEvents.utils.MessagesManager;
import mEvents.blockstock.mEvents.utils.mEventsExpansion;
import mEvents.blockstock.mEvents.utils.Runner.PaperRunner;
import mEvents.blockstock.mEvents.utils.Runner.Runner;
import mEvents.blockstock.mEvents.listeners.BossDeathListener; // Dinleyiciyi import ettik
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

public final class mEvents extends JavaPlugin {
    public final Server server = getServer();

    private final Runner runner = new PaperRunner(this);

    private ConfigManager configManager;
    private MessagesManager messagesManager;
    private EventScheduler eventScheduler;
    private BossRegionManager bossRegionManager;

    @Override
    public void onEnable() {
        try {
            configManager = new ConfigManager(this);
            messagesManager = new MessagesManager(this);
        } catch (Exception e) {
            getLogger().log(Level.SEVERE, "Configuration load error! Plugin disabled.", e);
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        bossRegionManager = new BossRegionManager(this);
        eventScheduler = new EventScheduler(this);
        eventScheduler.startEventCycle();

        // Sadeleştirilmiş komut tanımlama
        mEventsCommand commandExecutor = new mEventsCommand(this, bossRegionManager);

        // Dinleyiciyi sadeleştirilmiş şekilde kaydediyoruz (Blockstock hatasını önlemek için)
        getServer().getPluginManager().registerEvents(new BossDeathListener(this), this);

        if (getCommand("mEvents") != null) {
            getCommand("mEvents").setExecutor(commandExecutor);
            getCommand("mEvents").setTabCompleter(commandExecutor);
        } else {
            getLogger().warning("Command 'mEvents' not found in plugin.yml!");
        }

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new mEventsExpansion(this).register();
            getLogger().info("PlaceholderAPI successfully registered!");
        } else {
            getLogger().warning("PlaceholderAPI not found! Placeholders will not work.");
        }

        getLogger().info("mEvents v" + getDescription().getVersion() + " enabled successfully!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Disabling mEvents plugin...");

        if (eventScheduler != null) {
            getServer().getScheduler().cancelTasks(this);
        }

        if (bossRegionManager != null) {
            int killedCount = 0;
            Set<UUID> activeBosses = bossRegionManager.getActiveBosses();

            for (UUID bossUUID : activeBosses) {
                org.bukkit.entity.Entity entity = Bukkit.getEntity(bossUUID);
                if (entity != null && !entity.isDead()) {
                    entity.remove();
                    killedCount++;
                    getLogger().info("Boss with UUID " + bossUUID + " removed due to plugin shutdown.");
                }
            }

            bossRegionManager.removeAllBossRegions();
            getLogger().info("Bosses removed: " + killedCount);
        }

        getLogger().info("mEvents disabled successfully!");
    }

    public ConfigManager getConfigManager() { return configManager; }
    public MessagesManager getMessagesManager() { return messagesManager; }
    public BossRegionManager getBossRegionManager() { return bossRegionManager; }
    public EventScheduler getEventScheduler() { return eventScheduler; }
}
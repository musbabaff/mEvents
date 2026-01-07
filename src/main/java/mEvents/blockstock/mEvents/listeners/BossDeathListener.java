package mEvents.blockstock.mEvents.listeners;

import mEvents.blockstock.mEvents.utils.BossRegionManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.UUID;

public class BossDeathListener implements Listener {

    private final mEvents.blockstock.mEvents.mEvents plugin;
    private final BossRegionManager bossRegionManager;

    public BossDeathListener(mEvents.blockstock.mEvents.mEvents plugin) {
        this.plugin = plugin;
        this.bossRegionManager = plugin.getBossRegionManager();
    }

    @EventHandler
    public void onBossDeath(EntityDeathEvent event) {
        UUID bossUUID = event.getEntity().getUniqueId();

        if (bossRegionManager.isBossUUID(bossUUID)) {
            bossRegionManager.removeBossRegion(bossUUID);
            bossRegionManager.removeActiveBoss(bossUUID);
            plugin.getLogger().info("WorldGuard region removed after boss death: " + bossUUID);
        }
    }
}

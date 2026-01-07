package mEvents.blockstock.mEvents.utils;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class BossRegionManager {

    private final mEvents.blockstock.mEvents.mEvents plugin;
    private final RegionContainer container;
    private final String regionIdPrefix = "bossevent_";

    // Stores UUIDs of active bosses with regions
    public final Set<UUID> activeBosses = new HashSet<>();

    public BossRegionManager(mEvents.blockstock.mEvents.mEvents plugin) {
        this.plugin = plugin;
        this.container = WorldGuard.getInstance().getPlatform().getRegionContainer();
    }

    public void createBossRegion(Location spawnLocation, UUID bossUUID) {
        World world = spawnLocation.getWorld();
        if (world == null) {
            plugin.getLogger().warning("World for region creation not found!");
            return;
        }

        RegionManager manager = container.get(BukkitAdapter.adapt(world));
        if (manager == null) {
            plugin.getLogger().warning("RegionManager not found for the world!");
            return;
        }

        String regionId = regionIdPrefix + bossUUID.toString();

        BlockVector3 min = BlockVector3.at(
                spawnLocation.getBlockX() - 25,
                spawnLocation.getBlockY() - 5,
                spawnLocation.getBlockZ() - 25);
        BlockVector3 max = BlockVector3.at(
                spawnLocation.getBlockX() + 25,
                spawnLocation.getBlockY() + 20,
                spawnLocation.getBlockZ() + 25);

        ProtectedRegion existing = manager.getRegion(regionId);
        if (existing != null) {
            manager.removeRegion(regionId);
            plugin.getLogger().info("Removed old boss region: " + regionId);
        }

        ProtectedCuboidRegion region = new ProtectedCuboidRegion(regionId, min, max);

        region.setFlag(Flags.ENTRY, StateFlag.State.ALLOW);
        region.setFlag(Flags.EXIT, StateFlag.State.ALLOW);
        region.setFlag(Flags.PVP, StateFlag.State.ALLOW);

        manager.addRegion(region);

        plugin.getLogger().info("Created WorldGuard region for boss: " + regionId);

        activeBosses.add(bossUUID);

        startBossPositionCheck(bossUUID, spawnLocation, region);
        startPeriodicBossCheck(bossUUID);
    }

    public void removeBossRegion(UUID bossUUID) {
        String regionId = regionIdPrefix + bossUUID.toString();

        boolean removed = false;

        for (World world : Bukkit.getWorlds()) {
            RegionManager manager = container.get(BukkitAdapter.adapt(world));
            if (manager != null && manager.hasRegion(regionId)) {
                manager.removeRegion(regionId);
                plugin.getLogger().info("Removed WorldGuard region for boss: " + regionId + " in world " + world.getName());
                removed = true;
                break;
            }
        }

        activeBosses.remove(bossUUID);

        if (!removed) {
            plugin.getLogger().warning("Region for boss with UUID " + bossUUID + " was not found in any world.");
        }
    }

    public boolean isBossUUID(UUID uuid) {
        return activeBosses.contains(uuid);
    }

    public void removeActiveBoss(UUID uuid) {
        activeBosses.remove(uuid);
    }

    /**
     * Removes all boss regions in all worlds
     */
    public void removeAllBossRegions() {
        int removedCount = 0;
        String regionIdPrefix = this.regionIdPrefix;
        
        for (World world : Bukkit.getWorlds()) {
            RegionManager manager = container.get(BukkitAdapter.adapt(world));
            if (manager == null) continue;
            
            for (ProtectedRegion region : manager.getRegions().values()) {
                if (region.getId().startsWith(regionIdPrefix)) {
                    manager.removeRegion(region.getId());
                    removedCount++;
                    plugin.getLogger().info("Removed WorldGuard region: " + region.getId() + " in world " + world.getName());
                }
            }
        }
        
        activeBosses.clear();
        plugin.getLogger().info("Removed boss regions: " + removedCount);
    }
    
    public Set<UUID> getActiveBosses() {
        return new HashSet<>(activeBosses);
    }

    private void startBossPositionCheck(UUID bossUUID, Location spawnLocation, ProtectedRegion region) {
        new BukkitRunnable() {
            @Override
            public void run() {
                Entity bossEntity = Bukkit.getEntity(bossUUID);
                if (bossEntity == null || bossEntity.isDead()) {
                    this.cancel();
                    plugin.getLogger().info("Boss tracking task cancelled — boss died or is missing.");
                    return;
                }

                Location bossLoc = bossEntity.getLocation();
                RegionManager manager = container.get(BukkitAdapter.adapt(bossLoc.getWorld()));
                if (manager == null) {
                    plugin.getLogger().warning("RegionManager for the boss's world not found!");
                    return;
                }

                BlockVector3 vector = BukkitAdapter.asBlockVector(bossLoc);
                ApplicableRegionSet regions = manager.getApplicableRegions(vector);

                boolean inside = regions.getRegions().stream()
                        .anyMatch(r -> r.getId().equals(region.getId()));

                if (!inside) {
                    bossEntity.teleport(spawnLocation);
                    plugin.getLogger().info("Boss with UUID " + bossUUID + " left the region and was returned.");
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    public void startPeriodicBossCheck(UUID bossUUID) {
        new BukkitRunnable() {
            @Override
            public void run() {
                Entity bossEntity = Bukkit.getEntity(bossUUID);
                if (bossEntity == null || bossEntity.isDead()) {
                    removeBossRegion(bossUUID);
                    plugin.getLogger().info("Boss with UUID " + bossUUID + " is missing or dead. Region removed.");
                    this.cancel();
                }
            }
        }.runTaskTimer(plugin, 0L, 20L * 60);
    }
}

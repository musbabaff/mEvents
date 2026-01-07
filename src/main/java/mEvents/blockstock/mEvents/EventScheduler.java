package mEvents.blockstock.mEvents;

import mEvents.blockstock.mEvents.utils.BossRegionManager;
import mEvents.blockstock.mEvents.utils.ConfigManager;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.bukkit.BukkitAdapter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.time.LocalDateTime;
import java.util.*;

public class EventScheduler {

    private final mEvents plugin;
    private final ConfigManager configManager;
    private final Random random = new Random();
    private static final int MAX_SPAWN_ATTEMPTS = 10;
    private final BossRegionManager bossRegionManager;

    private int minutesLeft;
    private int minute;
    private String mobName;
    private String displayName;
    private int x;
    private int y;
    private int z;

    public EventScheduler(mEvents plugin) {
        this.plugin = plugin;
        this.configManager = plugin.getConfigManager();
        this.bossRegionManager = plugin.getBossRegionManager();
    }


    public void startEventCycle() {
        plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            LocalDateTime now = LocalDateTime.now();

            this.minute = now.getMinute();
            int second = now.getSecond();
            this.minutesLeft = (60 -  minute) % 60;

            if (minute == 45 && second == 0) {
                plugin.getLogger().info("Starting event...");
                startEvent();
            }
        }, 0L, 20L);
    }

    public int getMinutes() { return this.minutesLeft; }

    public int getMinute() { return this.minute; }

    public boolean startEvent() {
        World world = Bukkit.getWorld(configManager.getWorldName());
        if (world == null) {
            plugin.getLogger().severe("World '" + configManager.getWorldName() + "' not found!");
            return false;
        }

        Location spawnLocation = null;
        for (int i = 0; i < MAX_SPAWN_ATTEMPTS; i++) {
            this.x = randomInt(configManager.getXFrom(), configManager.getXTo());
            this.z = randomInt(configManager.getZFrom(), configManager.getZTo());
            this.y = world.getHighestBlockYAt(x, z);
            Location candidate = new Location(world, x + 0.5, y, z + 0.5);
            if (isSafeSpawnLocation(candidate)) {
                spawnLocation = candidate;
                break;
            }
        }

        if (spawnLocation == null) {
            plugin.getLogger().warning("Could not find a safe location to spawn the boss!");
            return false;
        }

        Map<String, String> mobData = getRandomMobData();
        if (mobData == null || mobData.get("name") == null) {
            plugin.getLogger().warning("Failed to retrieve mob data for spawning!");
            return false;
        }

        this.mobName = mobData.get("name");
        this.displayName = mobData.get("displayName");
        if (displayName == null || displayName.isEmpty()) {
            displayName = mobName;
        }


        sendBroadcast(plugin.getMessagesManager().broadcast15(displayName));
        scheduleMessage(5 * 60 * 20, plugin.getMessagesManager().broadcast10(displayName));
        scheduleMessage(10 * 60 * 20, plugin.getMessagesManager().broadcast5(displayName));
        scheduleMessage(12 * 60 * 20, plugin.getMessagesManager().broadcast3(displayName,
                spawnLocation.getX(), spawnLocation.getY(), spawnLocation.getZ()));

        final String finalMobName = mobName;
        final Location finalSpawnLocation = spawnLocation;
        plugin.getServer().getScheduler().runTaskLater(plugin,
                () -> spawnMob(finalMobName, finalSpawnLocation),
                15 * 60 * 20);

        return true;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double getZ() {
        return this.z;
    }


    public String getmobName() {
        return this.mobName;
    }

    public String getDisplayName() {
        return this.displayName != null ? this.displayName : this.mobName;
    }

    public String getDisplayNameForMob(String mobName) {
        List<Map<String, Object>> mobs = configManager.getMobsList();
        if (mobs == null) return mobName;

        for (Map<String, Object> mob : mobs) {
            if (mobName.equals(mob.get("name"))) {
                return extractDisplayName(mob);
            }
        }
        return mobName;
    }


    private void scheduleMessage(long delayTicks, String message) {
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> sendBroadcast(message), delayTicks);
    }

    private void sendBroadcast(String message) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(message);
        }
    }

    private boolean isUnsafeBlock(Block block) {
        Material mat = block.getType();
        return mat == Material.WATER || mat == Material.LAVA ||
                mat == Material.WATER_CAULDRON || mat == Material.LAVA_CAULDRON ||
                mat.name().endsWith("LEAVES");
    }

    private boolean isSafeSpawnLocation(Location loc) {
        World world = loc.getWorld();
        Block blockBelow = world.getBlockAt(loc.clone().add(0, -1, 0));
        Block blockAt = world.getBlockAt(loc);
        return !isUnsafeBlock(blockBelow) && !isUnsafeBlock(blockAt);
    }

    private void spawnMob(String mobName, Location location) {
        Optional<MythicMob> optionalMob = MythicBukkit.inst().getMobManager().getMythicMob(mobName);
        if (optionalMob.isEmpty()) {
            plugin.getLogger().warning("Mob named '" + mobName + "' was not found in MythicMobs!");
            return;
        }

        MythicMob mob = optionalMob.get();
        ActiveMob spawned = mob.spawn(BukkitAdapter.adapt(location), 1);
        if (spawned == null) {
            plugin.getLogger().warning("Failed to spawn mob: " + mobName);
            return;
        }

        Entity entity = spawned.getEntity().getBukkitEntity();

        String displayName = getDisplayNameForMob(mobName);
        sendBroadcast(plugin.getMessagesManager().broadcastspawned(
                displayName, location.getX(), location.getY(), location.getZ())
        );

        bossRegionManager.createBossRegion(location, entity.getUniqueId());

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (!spawned.isDead()) {
                spawned.despawn();
                plugin.getLogger().info("Mob '" + mobName + "' has been automatically removed after 10 minutes.");
            }
        }, 20L * 60 * 10);
    }


    private Map<String, String> getRandomMobData() {
        List<Map<String, Object>> mobs = configManager.getMobsList();
        if (mobs == null || mobs.isEmpty()) {
            plugin.getLogger().warning("Mob list is empty! Please check your config.yml");
            return null;
        }

        int totalChance = 0;
        for (Map<String, Object> mob : mobs) {
            Object chanceObj = mob.get("chance");
            if (chanceObj == null) {
                plugin.getLogger().warning("Mob '" + mob.get("name") + "' is missing the 'chance' parameter!");
                continue;
            }
            int chance = (int) chanceObj;
            if (chance <= 0) {
                plugin.getLogger().warning("Mob '" + mob.get("name") + "' has an invalid chance value: " + chance);
                continue;
            }
            totalChance += chance;
        }

        if (totalChance <= 0) {
            plugin.getLogger().warning("The total sum of chances is 0 or less! Please check your config.yml");
            Map<String, Object> firstMob = mobs.get(0);
            Map<String, String> result = new HashMap<>();
            result.put("name", (String) firstMob.get("name"));
            result.put("displayName", extractDisplayName(firstMob));
            return result;
        }

        int roll = random.nextInt(totalChance) + 1;
        int cumulative = 0;
        for (Map<String, Object> mob : mobs) {
            Object chanceObj = mob.get("chance");
            if (chanceObj == null) continue;
            int chance = (int) chanceObj;
            if (chance <= 0) continue;

            cumulative += chance;
            if (roll <= cumulative) {
                Map<String, String> result = new HashMap<>();
                result.put("name", (String) mob.get("name"));
                result.put("displayName", extractDisplayName(mob));
                return result;
            }
        }

        for (Map<String, Object> mob : mobs) {
            Object chanceObj = mob.get("chance");
            if (chanceObj != null && (int) chanceObj > 0) {
                Map<String, String> result = new HashMap<>();
                result.put("name", (String) mob.get("name"));
                result.put("displayName", extractDisplayName(mob));
                return result;
            }
        }

        Map<String, Object> firstMob = mobs.get(0);
        Map<String, String> result = new HashMap<>();
        result.put("name", (String) firstMob.get("name"));
        result.put("displayName", extractDisplayName(firstMob));
        return result;
    }

    private String extractDisplayName(Map<String, Object> mob) {
        Object displayNameObj = mob.get("display-name");
        if (displayNameObj != null) {
            String displayName = String.valueOf(displayNameObj);
            if (!displayName.isEmpty() && !displayName.equals("null")) {
                return displayName;
            }
        }
        return (String) mob.get("name");
    }


    private int randomInt(int min, int max) {
        return random.nextInt(max - min + 1) + min;
    }

}

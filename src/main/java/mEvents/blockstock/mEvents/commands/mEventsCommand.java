package mEvents.blockstock.mEvents.commands;

// Mevcut klasör yapına uygun importlar
import mEvents.blockstock.mEvents.EventScheduler;
import mEvents.blockstock.mEvents.mEvents; // Ana sınıfı açıkça import ediyoruz
import mEvents.blockstock.mEvents.utils.BossRegionManager;
import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.mobs.ActiveMob;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.*;

public class mEventsCommand implements CommandExecutor, TabCompleter {

    // Paket yolunu tam yazmak yerine import ettiğimiz sınıf ismini kullanıyoruz
    private final mEvents plugin;
    private final BossRegionManager bossRegionManager;

    // Constructor'daki karmaşık yol temizlendi
    public mEventsCommand(mEvents plugin, BossRegionManager bossRegionManager) {
        this.plugin = plugin;
        this.bossRegionManager = bossRegionManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("mevents.admin")) {
            sender.sendMessage(plugin.getMessagesManager().nopermission());
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage("§cUsage: /mEvents <reload|fspawn|spawn>");
            return true;
        }

        String subcommand = args[0].toLowerCase();

        switch (subcommand) {
            case "reload":
                Bukkit.getScheduler().runTask(plugin, () -> {
                    try {
                        // Mevcut eklenti örneğini (plugin instance) kullanarak güvenli reload
                        Bukkit.getPluginManager().disablePlugin(plugin);
                        Bukkit.getPluginManager().enablePlugin(plugin);
                        sender.sendMessage(plugin.getMessagesManager().reloadplugin());
                    } catch (Exception e) {
                        sender.sendMessage("§4§lError while reloading the plugin: " + e.getMessage());
                        e.printStackTrace();
                    }
                });
                return true;

            case "spawn":
                sender.sendMessage("Starting event...");
                new EventScheduler(plugin).startEvent();
                return true;

            case "fspawn":
                if (!(sender instanceof Player)) {
                    sender.sendMessage("§cThis command can only be executed by a player.");
                    return true;
                }

                if (args.length < 2) {
                    sender.sendMessage("§cUsage: /mEvents fspawn <mobName>");
                    return true;
                }

                String mobName = args[1];
                Optional<MythicMob> optionalMob = MythicBukkit.inst().getMobManager().getMythicMob(mobName);

                if (!optionalMob.isPresent()) {
                    sender.sendMessage("§cMob named '" + mobName + "' not found.");
                    return true;
                }

                Player player = (Player) sender;
                Location loc = player.getLocation();
                MythicMob mob = optionalMob.get();
                ActiveMob spawned = mob.spawn(BukkitAdapter.adapt(loc), 1);

                if (spawned != null) {
                    Entity entity = spawned.getEntity().getBukkitEntity();
                    bossRegionManager.createBossRegion(loc, entity.getUniqueId());
                    String displayName = plugin.getEventScheduler().getDisplayNameForMob(mobName);
                    String msg = plugin.getMessagesManager().broadcastspawned(displayName,
                            loc.getX(), loc.getY(), loc.getZ());
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        p.sendMessage(msg);
                    }

                    plugin.getLogger().info("Mob '" + mobName + "' spawned by player " + player.getName());
                } else {
                    player.sendMessage("§cError while spawning the mob.");
                }
                return true;

            default:
                sender.sendMessage("§cUnknown subcommand: " + subcommand);
                return true;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("reload", "fspawn", "spawn");
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("fspawn")) {
            String partial = args[1].toLowerCase(Locale.ROOT);
            List<String> completions = new ArrayList<>();
            for (MythicMob mob : MythicBukkit.inst().getMobManager().getMobTypes()) {
                String name = mob.getInternalName();
                if (name.toLowerCase(Locale.ROOT).startsWith(partial)) {
                    completions.add(name);
                }
            }
            return completions;
        }

        return Collections.emptyList();
    }
}
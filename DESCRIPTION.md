# 🛡️ mEvents — Automated Boss Event System

**mEvents** is a lightweight yet powerful automation plugin for Minecraft servers. It manages epic world events by spawning **MythicMobs** bosses at scheduled intervals, protecting the battle zone with **WorldGuard**, and keeping players engaged with a dynamic notification system.

---

## 🚀 Main Features

### 📅 **Advanced Scheduling**
* **Automated Intervals:** Runs events every hour (default: 45th minute) without manual intervention.
* **Weighted Chance System:** Define which bosses appear more frequently using a probability-based selection.
* **Smart Spawn Logic:** Automatically finds safe locations, avoiding liquid (lava/water) for a better combat experience.

### 🛡️ **Total Control & Protection**
* **Auto-Region Creation:** Generates a **WorldGuard** region around the boss to manage PvP, block damage, and entry/exit flags.
* **Tethering (Anti-Leash):** Bosses are locked to their spawn area. If lured away, they are automatically teleported back to the center.
* **Clean Cleanup:** Regions are instantly deleted when the boss dies or the event expires.

### 📊 **Seamless Integration**
* **PlaceholderAPI Support:** Display event status, boss names, and coordinates in your Scoreboard or Tab.
* **MythicMobs Ready:** Works natively with any mob created in MythicMobs.
* **Detailed Logging:** Full console reports for manual spawns and error handling.

---

## 🛠️ Commands & Permissions

| Command | Description | Permission |
| :--- | :--- | :--- |
| `/mevents spawn` | Triggers the next scheduled event immediately. | `mevents.admin` |
| `/mevents fspawn <mob>` | Spawns a specific boss at your location with a region. | `mevents.admin` |
| `/mevents reload` | Reloads all configuration and message files. | `mevents.admin` |

---

## ⚙️ Technical Highlights
* **Performance Focused:** Asynchronous file loading ensures zero impact on server TPS.
* **Safety First:** Built-in validation checks for your configuration to prevent crashes.
* **Smart Despawn:** Prevents "stuck" events by verifying boss existence periodically.

---

## 📦 Requirements
* **Platform:** Paper/Spigot 1.20.1+
* **Java:** 17 or 21
* **Required Plugins:** [MythicMobs](https://www.spigotmc.org/resources/mythicmobs.5707/), [WorldGuard](https://dev.bukkit.org/projects/worldguard)
* **Optional:** [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/)

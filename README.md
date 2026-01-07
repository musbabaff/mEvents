# 🛡️ mEvents

An advanced Minecraft server plugin for automated boss events, featuring deep integration with **MythicMobs** and **WorldGuard**.

---

## 📖 Overview

**mEvents** is designed to bring dynamic challenges to your server. It automates the life cycle of world bosses—from scheduling and spawning to area protection and cleanup.

### Core Logic:
* **Dynamic Protection:** Automatically generates a WorldGuard region around the boss upon spawning.
* **Anti-Leash System:** Monitors the boss's location. If the boss attempts to leave the defined region, it is instantly teleported back to the center.
* **Smart Cleanup:** The WorldGuard region is automatically deleted as soon as the boss is defeated.

---

## 🔧 Commands & Permissions

| Command | Description | Permission |
|:---|:---|:---|
| `/mevents reload` | Reloads the configuration and message files. | `mevents.admin` |
| `/mevents spawn` | Manually triggers the next scheduled event. | `mevents.admin` |
| `/mevents fspawn <mob>` | Force-spawns a specific boss at your location with a region. | `mevents.admin` |

---

## 📊 PlaceholderAPI Support

Use these placeholders in your Scoreboards, Tab-lists, or Chat:

| Placeholder | Description |
|:---|:---|
| `%mEvents_time%` | Minutes remaining until the next event. |
| `%mEvents_coordinates%` | Spawn location formatted as: `X: %x% Y: %y% Z: %z%`. |
| `%mEvents_bossname%` | Displays the display name of the active boss. |

---

## 📦 Requirements

* **Server Engine:** Paper 1.20.1 or newer (Recommended).
* **Java:** Version 17+
* **Dependencies:**
  * [MythicMobs](https://www.spigotmc.org/resources/mythicmobs.5707/)
  * [WorldGuard](https://dev.bukkit.org/projects/worldguard)
  * [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/)

---

## 🛠️ Installation

1. Download `mEvents.jar` and drop it into your `/plugins/` folder.
2. Ensure all dependencies (**MythicMobs**, **WorldGuard**) are installed and updated.
3. Restart your server to generate the default configuration.
4. Grant the `mevents.admin` permission to your administrators using LuckPerms or a similar plugin.

---

## ⚙️ Configuration & Debugging

* **Region Scaling:** By default, `/mevents fspawn` creates a **50x25x50** cuboid region centered on the boss.
* **Logs:** All region creation, boss movement violations, and deletion events are logged to the console for easy debugging.
* **Safety:** If the `RegionManager` fails or a mob cannot spawn, detailed error reports will be generated in the console logs.

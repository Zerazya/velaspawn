package fr.velaspawn;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** Gère la position du spawn (spawn.yml), les délais et les cooldowns. */
public final class SpawnManager {

    private final VelaSpawn plugin;
    private final File dataFile;
    private YamlConfiguration data;

    private final Map<UUID, BukkitTask> pending = new ConcurrentHashMap<>();
    private final Map<UUID, Long> lastTeleport = new ConcurrentHashMap<>();

    SpawnManager(VelaSpawn plugin) {
        this.plugin = plugin;
        this.dataFile = new File(plugin.getDataFolder(), "spawn.yml");
        this.data = YamlConfiguration.loadConfiguration(dataFile);
    }

    /** Spawn défini avec /setspawn, sinon le spawn du monde principal. */
    public Location getSpawn() {
        Location custom = plugin.readSpawn(data);
        if (custom != null) {
            return custom;
        }
        World world = Bukkit.getWorlds().get(0);
        return world.getSpawnLocation().add(0.5, 0, 0.5);
    }

    public void setSpawn(Location loc) throws IOException {
        data.set("spawn.world", loc.getWorld().getName());
        data.set("spawn.x", loc.getX());
        data.set("spawn.y", loc.getY());
        data.set("spawn.z", loc.getZ());
        data.set("spawn.yaw", (double) loc.getYaw());
        data.set("spawn.pitch", (double) loc.getPitch());
        data.save(dataFile);
    }

    /** Téléportation demandée par le joueur : applique cooldown et délai. */
    public void requestTeleport(Player player) {
        FileConfiguration cfg = plugin.getConfig();
        Messages msg = plugin.messages();
        UUID id = player.getUniqueId();

        if (pending.containsKey(id)) {
            msg.send(player, "already-pending");
            return;
        }

        int cooldown = cfg.getInt("cooldown-seconds", 10);
        if (cooldown > 0 && !player.hasPermission("spawnplugin.bypass.cooldown")) {
            Long last = lastTeleport.get(id);
            if (last != null) {
                long remaining = cooldown - (System.currentTimeMillis() - last) / 1000;
                if (remaining > 0) {
                    msg.send(player, "cooldown", Map.of("seconds", String.valueOf(remaining)));
                    return;
                }
            }
        }

        int delay = cfg.getInt("delay-seconds", 3);
        if (delay <= 0 || player.hasPermission("spawnplugin.bypass.delay")) {
            teleportWithMessage(player);
            return;
        }

        msg.send(player, "teleporting-in", Map.of("seconds", String.valueOf(delay)));
        BukkitTask task = Bukkit.getScheduler().runTaskLater(plugin, () -> {
            pending.remove(id);
            if (player.isOnline()) {
                teleportWithMessage(player);
            }
        }, delay * 20L);
        pending.put(id, task);
    }

    private void teleportWithMessage(Player player) {
        player.teleportAsync(getSpawn()).thenAccept(ok -> {
            if (Boolean.TRUE.equals(ok)) {
                lastTeleport.put(player.getUniqueId(), System.currentTimeMillis());
                plugin.messages().send(player, "teleported");
            }
        });
    }

    /** Téléportation immédiate et silencieuse (join, /spawn <joueur>). */
    public void sendToSpawn(Player player) {
        player.teleportAsync(getSpawn());
    }

    public boolean isPending(Player player) {
        return pending.containsKey(player.getUniqueId());
    }

    /** Annule une téléportation en attente. Retourne true si une était en cours. */
    public boolean cancel(Player player) {
        BukkitTask task = pending.remove(player.getUniqueId());
        if (task == null) {
            return false;
        }
        task.cancel();
        return true;
    }

    public void forget(Player player) {
        cancel(player);
        lastTeleport.remove(player.getUniqueId());
    }
}

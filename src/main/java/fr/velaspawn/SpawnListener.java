package fr.velaspawn;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public final class SpawnListener implements Listener {

    private final VelaSpawn plugin;

    SpawnListener(VelaSpawn plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        boolean firstJoin = !player.hasPlayedBefore();

        boolean always = plugin.getConfig().getBoolean("teleport-on-join", false);
        boolean first = plugin.getConfig().getBoolean("teleport-on-first-join", true);

        if (always || (firstJoin && first)) {
            plugin.spawns().sendToSpawn(player);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onRespawn(PlayerRespawnEvent event) {
        if (!plugin.getConfig().getBoolean("respawn-at-spawn", true)) {
            return;
        }
        boolean keepBed = plugin.getConfig().getBoolean("respawn-keep-bed", true);
        if (keepBed && (event.isBedSpawn() || event.isAnchorSpawn())) {
            return;
        }
        event.setRespawnLocation(plugin.spawns().getSpawn());
    }

    @EventHandler(ignoreCancelled = true)
    public void onMove(PlayerMoveEvent event) {
        if (!event.hasChangedBlock()) {
            return;
        }
        if (!plugin.getConfig().getBoolean("cancel-on-move", true)) {
            return;
        }
        Player player = event.getPlayer();
        if (plugin.spawns().cancel(player)) {
            plugin.messages().send(player, "cancelled-move");
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        if (!plugin.getConfig().getBoolean("cancel-on-damage", true)) {
            return;
        }
        if (plugin.spawns().cancel(player)) {
            plugin.messages().send(player, "cancelled-damage");
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        plugin.spawns().forget(event.getPlayer());
    }
}

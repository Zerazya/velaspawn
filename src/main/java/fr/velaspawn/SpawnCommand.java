package fr.velaspawn;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public final class SpawnCommand implements CommandExecutor {

    private final VelaSpawn plugin;

    SpawnCommand(VelaSpawn plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        Messages msg = plugin.messages();

        if (args.length == 0) {
            if (!(sender instanceof Player player)) {
                msg.send(sender, "player-only");
                return true;
            }
            plugin.spawns().requestTeleport(player);
            return true;
        }

        if (!sender.hasPermission("spawnplugin.spawn.others")) {
            msg.send(sender, "no-permission");
            return true;
        }

        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null) {
            msg.send(sender, "player-not-found", Map.of("player", args[0]));
            return true;
        }

        plugin.spawns().sendToSpawn(target);
        msg.send(target, "teleported");
        if (!sender.equals(target)) {
            msg.send(sender, "teleported-other", Map.of("player", target.getName()));
        }
        return true;
    }
}

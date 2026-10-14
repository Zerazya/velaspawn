package fr.velaspawn;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public final class SetSpawnCommand implements CommandExecutor {

    private final VelaSpawn plugin;

    SetSpawnCommand(VelaSpawn plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        Messages msg = plugin.messages();

        if (!(sender instanceof Player player)) {
            msg.send(sender, "player-only");
            return true;
        }

        try {
            plugin.spawns().setSpawn(player.getLocation());
        } catch (IOException e) {
            plugin.getLogger().severe("Impossible d'écrire spawn.yml : " + e.getMessage());
            msg.send(player, "save-error");
            return true;
        }

        msg.send(player, "spawn-set");
        return true;
    }
}

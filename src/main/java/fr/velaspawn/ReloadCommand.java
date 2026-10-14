package fr.velaspawn;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public final class ReloadCommand implements CommandExecutor {

    private final VelaSpawn plugin;

    ReloadCommand(VelaSpawn plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        plugin.reload();
        plugin.messages().send(sender, "reloaded");
        return true;
    }
}

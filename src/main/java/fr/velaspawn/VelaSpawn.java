package fr.velaspawn;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public final class VelaSpawn extends JavaPlugin {

    private SpawnManager spawnManager;
    private Messages messages;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        this.messages = new Messages(this);
        this.spawnManager = new SpawnManager(this);

        register("spawn", new SpawnCommand(this));
        register("setspawn", new SetSpawnCommand(this));
        register("spawnreload", new ReloadCommand(this));

        getServer().getPluginManager().registerEvents(new SpawnListener(this), this);
        getLogger().info("VelaSpawn active.");
    }

    private void register(String name, CommandExecutor executor) {
        PluginCommand cmd = getCommand(name);
        if (cmd == null) {
            getLogger().severe("Commande manquante dans plugin.yml : " + name);
            return;
        }
        cmd.setExecutor(executor);
    }

    public void reload() {
        reloadConfig();
        messages.reload();
    }

    public SpawnManager spawns() {
        return spawnManager;
    }

    public Messages messages() {
        return messages;
    }

    /** Lit un Location dans la config, ou null s'il n'est pas défini / monde absent. */
    Location readSpawn(FileConfiguration cfg) {
        if (!cfg.isConfigurationSection("spawn")) {
            return null;
        }
        String worldName = cfg.getString("spawn.world");
        if (worldName == null) {
            return null;
        }
        World world = getServer().getWorld(worldName);
        if (world == null) {
            return null;
        }
        return new Location(
                world,
                cfg.getDouble("spawn.x"),
                cfg.getDouble("spawn.y"),
                cfg.getDouble("spawn.z"),
                (float) cfg.getDouble("spawn.yaw"),
                (float) cfg.getDouble("spawn.pitch"));
    }
}

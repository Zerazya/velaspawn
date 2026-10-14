package fr.velaspawn;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;

import java.util.Map;

/** Messages MiniMessage lus à chaud depuis config.yml. Un message vide n'est pas envoyé. */
public final class Messages {

    private static final MiniMessage MM = MiniMessage.miniMessage();

    private final VelaSpawn plugin;

    Messages(VelaSpawn plugin) {
        this.plugin = plugin;
    }

    /** Les messages sont lus directement dans la config, rien à recharger. */
    void reload() {
    }

    public void send(CommandSender target, String key) {
        send(target, key, Map.of());
    }

    public void send(CommandSender target, String key, Map<String, String> placeholders) {
        String raw = plugin.getConfig().getString("messages." + key);
        if (raw == null) {
            raw = "<red>Message manquant : " + key;
        }
        if (raw.isEmpty()) {
            return;
        }
        for (Map.Entry<String, String> e : placeholders.entrySet()) {
            raw = raw.replace("{" + e.getKey() + "}", e.getValue());
        }
        String prefix = plugin.getConfig().getString("messages.prefix", "");
        Component component = MM.deserialize(prefix + raw);
        target.sendMessage(component);
    }
}

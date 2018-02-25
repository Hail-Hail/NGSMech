package ngsmech.mech.managers;

import ngsmech.mech.Mech;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.io.File;
import java.io.IOException;

public class CreatePlayerData implements Listener {

    Mech plugin = Mech.getPlugin(Mech.class);

    File directory = new File(plugin.yamlapi.getPluginFolder(plugin.getName()), File.separator + "PlayerData");

    public CreatePlayerData() {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {

        Player player = e.getPlayer();

        File playerFile = new File(directory, File.separator + player.getUniqueId() + ".yml");
        if (playerFile.exists()) {
            return;
        }

        YamlConfiguration yaml = plugin.yamlapi.getYamlConf(playerFile);

        try {
            yaml.set("masterName", "パイロット");
            yaml.set("mechName", "Mech");
            yaml.set("chatColor", "§f§l");
            yaml.set("pilotAbility", "Grapple");
            yaml.save(playerFile);
        } catch (IOException ee) {
            ee.printStackTrace();
        }
    }
}

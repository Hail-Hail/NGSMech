package ngsmech.mech.managers;

import ngsmech.mech.Mech;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.IOException;

public class setname {

    String prefix = "§f§l[§6§lMech§f§l]";

    Mech plugin = Mech.getPlugin(Mech.class);

    File directory = new File(plugin.yamlapi.getPluginFolder(plugin.getName()), File.separator + "PlayerData");

    public void setMasterName(Player player, String name) {
        File playerFile = new File(directory, File.separator + player.getUniqueId() + ".yml");
        YamlConfiguration yaml = plugin.yamlapi.getYamlConf(playerFile);

        try {
            yaml.set("masterName", name);
            yaml.save(playerFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setMechName(Player player, String name) {
        File playerFile = new File(directory, File.separator + player.getUniqueId() + ".yml");
        YamlConfiguration yaml = plugin.yamlapi.getYamlConf(playerFile);

        try {
            yaml.set("mechName", name);
            yaml.save(playerFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setChatColor(Player player, String name) {
        File playerFile = new File(directory, File.separator + player.getUniqueId() + ".yml");
        YamlConfiguration yaml = plugin.yamlapi.getYamlConf(playerFile);

        try {
            yaml.set("chatColor", name);
            yaml.save(playerFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

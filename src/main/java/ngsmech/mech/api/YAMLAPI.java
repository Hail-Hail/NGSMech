package ngsmech.mech.api;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class YAMLAPI {

    public File getPluginFolder(String pluginName) {
        if (Bukkit.getServer().getPluginManager().getPlugin(pluginName) == null) {
            return null;
        }
        return Bukkit.getServer().getPluginManager().getPlugin(pluginName).getDataFolder();
    }

    public YamlConfiguration getYamlConf(File yaml) {
        if (!yaml.getName().endsWith(".yml")) {
            return null;
        }
        return YamlConfiguration.loadConfiguration(yaml);
    }

    public boolean removeFile(File file) {
        // 存在しない場合は処理終了
        if (!file.exists()) {
            return false;
        }
        // 対象がディレクトリの場合は再帰処理
        try {
            if (file.isDirectory()) {
                for (File child : file.listFiles()) {
                    removeFile(child);
                }
            }
            // 対象がファイルもしくは配下が空のディレクトリの場合は削除する
            file.delete();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}

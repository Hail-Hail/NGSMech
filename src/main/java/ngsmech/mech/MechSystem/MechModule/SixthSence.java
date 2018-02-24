package ngsmech.mech.MechSystem.MechModule;

import ngsmech.mech.Mech;
import ngsmech.mech.api.ActionBarAPI;
import ngsmech.mech.api.YAMLAPI;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BlockIterator;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public final class SixthSence implements Listener{

    Mech Plugin = Mech.getPlugin(Mech.class);

    Player player;
    LivingEntity target;

    List<Player> cooldown = new ArrayList<>();

    public SixthSence() {
        // Plugin startup logic
        Plugin.getServer().getPluginManager().registerEvents(this,Plugin);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        player = e.getPlayer();
        if (cooldown.contains(target)) {
            return;
        }
        getTarget();
        if (target == null) {
            return;
        }

        if (!(target instanceof Player)) {
            return;
        }

        Player target = (Player) this.target;

        File directory = new File(Plugin.yamlapi.getPluginFolder(Plugin.getName()), File.separator + "PlayerData");

        File playerFile = new File(directory, File.separator + target.getUniqueId() + ".yml");
        YamlConfiguration yaml = Plugin.yamlapi.getYamlConf(playerFile);

        if (Bukkit.getServer().getOnlinePlayers().contains(Bukkit.getServer().getPlayer(target.getName()))) {
            Plugin.actionBarAPI.sendActionBar(target, yaml.getString("masterName") + yaml.getString("chatColor") +"、§e§l" + player.getName() + yaml.getString("chatColor") + "に視認されています");
            cooldown.add(target);
            new BukkitRunnable() {
                @Override
                public void run() {
                    cooldown.remove(target);
                }
            }.runTaskTimer(Plugin, 20, 0);
        }
    }

    void getTarget() {
        List<Entity> nearbyE = player.getNearbyEntities(50, 50, 50);
        ArrayList<LivingEntity> livingE = new ArrayList<LivingEntity>();

        for (Entity e : nearbyE) {
            if (e instanceof LivingEntity) {
                livingE.add((LivingEntity) e);
            }
        }

        this.target = null;
        BlockIterator bItr = new BlockIterator(this.player, 50);
        Block block;
        Location loc;
        int bx, by, bz;
        double ex, ey, ez;
        // loop through player's line of sight
        while (bItr.hasNext()) {
            block = bItr.next();
            bx = block.getX();
            by = block.getY();
            bz = block.getZ();
            // check for entities near this block in the line of sight
            for (LivingEntity e : livingE) {
                loc = e.getLocation();
                ex = loc.getX();
                ey = loc.getY();
                ez = loc.getZ();
                if ((bx-.75 <= ex && ex <= bx+1.75) && (bz-.75 <= ez && ez <= bz+1.75) && (by-1 <= ey && ey <= by+2.5)) {
                    // entity is close enough, set target and stop
                    this.target = e;
                    break;
                }
            }
        }

    }
}


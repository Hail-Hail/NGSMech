package ngsmech.mech.PilotAbility;

import com.sun.scenario.effect.LinearConvolveCoreEffect;
import ngsmech.mech.Mech;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import sun.tools.jar.Main;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Grapple implements Listener {

    String prefix = "§f§l[§6§lAbility§f§l] ";

    Mech plugin = Mech.getPlugin(Mech.class);

    public Grapple() {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void ShotGrapple(PlayerSwapHandItemsEvent e) {

        Player player = e.getPlayer();

        e.setCancelled(true);

        if (player.isGliding()) {
            if (!player.isOnGround()) {
                return;
            }
        }

        if (plugin.RideMech.contains(player)) {
            plugin.actionBarAPI.sendActionBar(player,prefix + "§c§lMech搭乗中は使用できません");
        }

        if (plugin.AbilityCooldown.contains(player)) {
            plugin.actionBarAPI.sendActionBar(player,prefix + "§7§lクールタイム中");
            return;
        }

        if (plugin.AbilityNow.contains(player)) {
            plugin.actionBarAPI.sendActionBar(player,prefix + "§7§lアビリティ発動中です");
            return;
        }

        File directory = new File(plugin.yamlapi.getPluginFolder(plugin.getName()), File.separator + "PlayerData");

        File playerFile = new File(directory, File.separator + player.getUniqueId() + ".yml");
        YamlConfiguration yaml = plugin.yamlapi.getYamlConf(playerFile);

        String Ability = yaml.getString("pilotAbility");

        if (!Ability.contains("Grapple")) {
            return;
        }

        plugin.actionBarAPI.sendActionBar(player,prefix + "§7§lアンカー射出");

        Block HookBlock = player.getTargetBlock(null,50);

        if (HookBlock.getType() == Material.AIR || HookBlock.getType() == Material.WATER || HookBlock.getType() == Material.LAVA
                || HookBlock.getType() == Material.BARRIER) {
            plugin.actionBarAPI.sendActionBar(player,prefix + "§7§lアンカーが刺さりません");
            return;
        }

        Location hookLoc = HookBlock.getLocation();

        plugin.AbilityNow.add(player);
        plugin.AbilityCooldown.add(player);

        player.playSound(player.getLocation(), Sound.BLOCK_PISTON_EXTEND, 3, 2);

        new BukkitRunnable() {
            @Override
            public void run() {
                player.playSound(player.getLocation(), Sound.ENTITY_ARROW_HIT, 2, 1);
                this.cancel();
            }
        }.runTaskLater(plugin,8);

        new BukkitRunnable() {
            @Override
            public void run() {

                if (loop == 20) {
                    plugin.AbilityNow.remove(player);
                    player.setVelocity(player.getLocation().getDirection().multiply(1.3).setY(1.0));
                    player.playSound(player.getLocation(), Sound.BLOCK_PISTON_CONTRACT, 2, 2);
                    this.cancel();
                    return;
                }

                loop ++;

                if (player.isSneaking()) {
                    plugin.AbilityNow.remove(player);
                    player.setVelocity(player.getLocation().getDirection().multiply(1.3).setY(1.0));
                    player.playSound(player.getLocation(), Sound.BLOCK_PISTON_CONTRACT, 2, 2);
                    this.cancel();
                    return;
                }

                if (plugin.AbilityNow.contains(player)) {
                    Location playerLoc = player.getLocation();
                    Vector MoveVector = hookLoc.toVector().subtract(playerLoc.toVector());
                    MoveVector.normalize().multiply(1.3);
                    Vector PlayerDir = player.getLocation().getDirection().multiply(1.1).setY(-0.3);
                    MoveVector.add(PlayerDir);
                    player.setVelocity(MoveVector);
                    player.playSound(player.getLocation(), Sound.BLOCK_FIRE_EXTINGUISH, 2, 2);
                }
            }private int loop = 0;
        }.runTaskTimer(plugin,8,2);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (plugin.AbilityCooldown.contains(player)) {
                    player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 3, 2);
                    plugin.actionBarAPI.sendActionBar(player, prefix + "§a§l再使用可能");
                    plugin.AbilityCooldown.remove(player);
                }
                this.cancel();
            }
        }.runTaskLater(plugin,80);
    }
}
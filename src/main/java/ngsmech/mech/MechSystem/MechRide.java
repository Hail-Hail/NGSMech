package ngsmech.mech.MechSystem;

import ngsmech.mech.Mech;
import ngsmech.mech.api.YAMLAPI;
import org.bukkit.*;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.spigotmc.event.entity.EntityDismountEvent;
import sun.plugin2.main.server.Plugin;

import javax.management.Attribute;
import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class MechRide  implements Listener {

    String prefix = "§f§l[§6§lMech§f§l]";

    Mech plugin = Mech.getPlugin(Mech.class);

    public List<Player> EscapeMech = new ArrayList<>();

    public MechRide() {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void SummonMech(PlayerInteractEvent e) {

        Player player = e.getPlayer();

        ItemStack itemStack = player.getInventory().getItemInMainHand();

        if (itemStack.getType().equals(Material.AIR)) {
            return;
        }

        ItemMeta itemMeta = itemStack.getItemMeta();

        if (!itemMeta.hasDisplayName()) {
            return;
        }

        if (plugin.SummonMech.contains(player)) {
            return;
        }

        if (player.isSneaking()) {
            return;
        }

        if (player.getInventory().getItemInMainHand().getItemMeta().getDisplayName().equals("§b§lMech Core")) {
            plugin.SummonMech.add(player);
            player.sendMessage(prefix + " §a§lStarting Mech_Summon_System");

            File directory = new File(plugin.yamlapi.getPluginFolder(plugin.getName()), File.separator + "PlayerData");

            File playerFile = new File(directory, File.separator + player.getUniqueId() + ".yml");
            YamlConfiguration yaml = plugin.yamlapi.getYamlConf(playerFile);

            new BukkitRunnable() {
                @Override
                public void run() {
                    loop++;
                    if (loop == 1) {
                        player.sendMessage(yaml.getString("mechName") + "§r: " + yaml.getString("masterName") + yaml.getString("chatColor") + "、すぐにそちらに向かいます");
                    }
                    if (loop >= 5) {
                        player.sendMessage(yaml.getString("mechName") + "§r: " + yaml.getString("chatColor") + "お待たせしました、" + "§r" + yaml.getString("masterName"));
                        plugin.SummonMech.remove(player);
                        Location location = player.getLocation();

                        Firework firework = (Firework) player.getWorld().spawnEntity(location, EntityType.FIREWORK);
                        FireworkEffect effect = FireworkEffect.builder()
                                .with(FireworkEffect.Type.BALL)
                                .withColor(Color.ORANGE)
                                .build();
                        FireworkMeta meta = firework.getFireworkMeta();
                        meta.setPower(0);
                        meta.addEffect(effect);
                        firework.setFireworkMeta(meta);

                        player.playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 5, 1);
                        player.getWorld().spawnParticle(Particle.EXPLOSION_HUGE, player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ(), 5, 0, 1, 0);

                        IronGolem mech = (IronGolem) player.getWorld().spawnEntity(location, EntityType.IRON_GOLEM);
                        mech.setCustomName(player.getName());
                        mech.getAttribute(org.bukkit.attribute.Attribute.GENERIC_MAX_HEALTH).setBaseValue(200);
                        mech.setHealth(200);
                        mech.setCustomNameVisible(true);
                        this.cancel();
                    }
                }private int loop = 0;
            }.runTaskTimer(plugin, 30, 20);
        }
    }

    @EventHandler
    public void MechRideAction(PlayerInteractAtEntityEvent e) {

        Player player = e.getPlayer();

        if (plugin.RideMech.contains(player)) {
            return;
        }

        File directory = new File(plugin.yamlapi.getPluginFolder(plugin.getName()), File.separator + "PlayerData");

        File playerFile = new File(directory, File.separator + player.getUniqueId() + ".yml");
        YamlConfiguration yaml = plugin.yamlapi.getYamlConf(playerFile);

        if (player.isSneaking()) {
            return;
        }

        Entity mech = e.getRightClicked();

        if (!(mech instanceof IronGolem)) {
            return;
        }

        if (mech.getName().equals(player.getName())) {
            plugin.RideMech.add(player);
            mech.setPassenger(player);
            player.sendMessage(yaml.getString("mechName") + "§r: " + yaml.getString("chatColor") + "おかえりなさい、" + "§r" + yaml.getString("masterName"));
            new BukkitRunnable() {
                @Override
                public void run() {

                    if (!plugin.RideMech.contains(player)) {

                        if (plugin.MechHealthLow.contains(player)) {

                            if (EscapeMech.contains(player)) {
                                EscapeMech.remove(player);
                                plugin.MechHealthLow.remove(player);
                                mech.removePassenger(player);

                                player.playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 5, 1);
                                player.getWorld().spawnParticle(Particle.EXPLOSION_HUGE, player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ(), 5, 0, 1, 0);

                                player.setVelocity(player.getLocation().getDirection().setY(5.0));
                                player.sendMessage(yaml.getString("mechName") + "§r: " + yaml.getString("masterName") + yaml.getString("chatColor") + "、どうかお気をつけて");
                                plugin.actionBarAPI.sendActionBar(player, yaml.getString("chatColor") + "プロトコル[03]");
                                mech.remove();

                                this.cancel();
                                return;
                            }
                        }

                        mech.removePassenger(player);

                        player.playSound(player.getLocation(), Sound.BLOCK_PISTON_EXTEND, 3, 1);
                        player.getWorld().spawnParticle(Particle.SPELL, player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ(), 10, 0, 1, 0);

                        player.setVelocity(player.getLocation().getDirection().setY(0.2));
                        player.sendMessage(yaml.getString("mechName") + "§r: " + yaml.getString("masterName") + yaml.getString("chatColor") + "、周囲の警戒を怠らないように");
                        this.cancel();
                        return;

                    }
                    if (!player.isInsideVehicle()) {
                        mech.setPassenger(player);
                    }
                }
            }.runTaskTimer(plugin, 1L,1L);
        }
    }

    @EventHandler
    public void MechRidePlayer(EntityDamageEvent e) {
        LivingEntity player = (Player) e.getEntity();
        if (plugin.RideMech.contains(player)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void PlayertoMechDamage(EntityDamageByEntityEvent e) {

        Entity mech = e.getEntity();
        Entity rider = mech.getPassenger();
        Entity damager = e.getDamager();

        //処理が上手くいかない
        if (mech.getName().equals(rider.getName())) {
            if (rider.getName().equals(damager)) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void MechHelth(EntityDamageEvent e) {

        LivingEntity mech = (LivingEntity) e.getEntity();

        if (!(mech instanceof IronGolem)) {
            return;
        }

        Player player = (Player) mech.getPassenger();

        if (!(player instanceof Player)) {
            return;
        }

        if (plugin.MechHealthLow.contains(player)) {
            return;
        }

        double health = mech.getHealth();

        if (health <= 100) {

            File directory = new File(plugin.yamlapi.getPluginFolder(plugin.getName()), File.separator + "PlayerData");

            File playerFile = new File(directory, File.separator + player.getUniqueId() + ".yml");
            YamlConfiguration yaml = plugin.yamlapi.getYamlConf(playerFile);

            player.sendMessage(yaml.getString("mechName") + "§r: " + yaml.getString("chatColor") + "機体損傷危険域");

            plugin.MechHealthLow.add(player);

        }
    }

    @EventHandler
    public void MechDismount(EntityDismountEvent e) {

        Player player = (Player) e.getEntity();

        if (!plugin.RideMech.contains(player)) {
            return;
        }

        File directory = new File(plugin.yamlapi.getPluginFolder(plugin.getName()), File.separator + "PlayerData");

        File playerFile = new File(directory, File.separator + player.getUniqueId() + ".yml");
        YamlConfiguration yaml = plugin.yamlapi.getYamlConf(playerFile);

        if (plugin.MechHealthLow.contains(player)) {

            if (EscapeMech.contains(player)) {
                player.sendMessage(yaml.getString("mechName") + "§r: " + yaml.getString("chatColor") + "緊急脱出");
                plugin.RideMech.remove(player);
                return;
            }

            if (!EscapeMech.contains(player)) {
                player.sendMessage(yaml.getString("mechName") + "§r: " + yaml.getString("chatColor") + "緊急脱出機能のロックを解除します");
                EscapeMech.add(player);
                return;
            }
        }

        plugin.RideMech.remove(player);

    }
}

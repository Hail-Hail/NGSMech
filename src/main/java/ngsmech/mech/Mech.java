package ngsmech.mech;

import com.google.common.collect.Lists;
import ngsmech.mech.MechSystem.MechModule.SixthSence;
import ngsmech.mech.MechSystem.MechRide;
import ngsmech.mech.PilotAbility.Grapple;
import ngsmech.mech.api.ActionBarAPI;
import ngsmech.mech.api.YAMLAPI;
import ngsmech.mech.data.Contents;
import ngsmech.mech.managers.CreatePlayerData;
import ngsmech.mech.managers.help;
import ngsmech.mech.managers.setname;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedMainHandEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.omg.SendingContext.RunTime;
import sun.applet.Main;
import sun.plugin2.message.Message;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public final class Mech extends JavaPlugin implements Listener {

    //managers
    public ngsmech.mech.managers.help help;
    public ngsmech.mech.managers.setname setname;
    public ngsmech.mech.managers.CreatePlayerData createplayerdata;

    //Mech
    public ngsmech.mech.MechSystem.MechRide mechRide;
    public ngsmech.mech.MechSystem.MechModule.SixthSence sixthsence;

    //Ability
    public ngsmech.mech.PilotAbility.Grapple grapple;
    public List<Player> AbilityNow = new ArrayList<>();
    public List<Player> AbilityCooldown = new ArrayList<>();

    //data
    public Contents contents;

    //api
    public YAMLAPI yamlapi;
    public ActionBarAPI actionBarAPI;

    public String prefix = "§f§l[§6§lMech§f§l]";
    public List<Player> MechTalk = new ArrayList<>();
    public List<Player> SummonMech = new ArrayList<>();

    public List<Player> RideMech = new ArrayList<>();
    public List<Player> MechHealthLow = new ArrayList<>();

    @Override
    public void onEnable() {
        // Plugin startup logic
        getServer().getPluginManager().registerEvents(this, this);
        this.saveDefaultConfig();
        initialization();
        setCommandExecuter();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    //初期化
    private void initialization() {
        apiInitialization();
        managersInitialization();
        dataInitialization();
        listenerInitialization();
    }

    private void managersInitialization() {
        help = new help();
        setname = new setname();
        createplayerdata = new CreatePlayerData();
    }

    private void dataInitialization() {
    }

    private void listenerInitialization() {
        contents = new Contents();
        sixthsence = new SixthSence();
        mechRide = new MechRide();
        grapple = new Grapple();
    }

    private void apiInitialization() {
        yamlapi = new YAMLAPI();
        actionBarAPI = new ActionBarAPI();
    }

    //CommandExecuterの設定
    private void setCommandExecuter() {
        this.getCommand("mech").setExecutor(new MechCommands());
    }

    @EventHandler
    public void getMechCore(PlayerJoinEvent e) {

        Player player = e.getPlayer();

        ItemStack item = new ItemStack(Material.NETHER_STAR, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§b§lMech Core");
        item.setItemMeta(meta);

        player.getInventory().setItem(4,item);
    }

    @EventHandler
    public void TalkMech(PlayerInteractEvent e) {

        Player player = e.getPlayer();

        ItemStack itemStack = player.getInventory().getItemInMainHand();

        if (itemStack.getType().equals(Material.AIR)) {
            return;
        }

        ItemMeta itemMeta = itemStack.getItemMeta();

        if (!itemMeta.hasDisplayName()) {
            return;
        }

        if (!player.isSneaking()) {
            return;
        }

        File directory = new File(this.yamlapi.getPluginFolder(this.getName()), File.separator + "PlayerData");

        File playerFile = new File(directory, File.separator + player.getUniqueId() + ".yml");
        YamlConfiguration yaml = this.yamlapi.getYamlConf(playerFile);

        if (player.getInventory().getItemInMainHand().getItemMeta().getDisplayName().equals("§b§lMech Core")){
            if (MechTalk.contains(player)) {
                return;
            }
            MechTalk.add(player);
            player.sendMessage(prefix + " §a§l...パイロットを認証");
            new BukkitRunnable() {
                @Override
                public void run() {
                    loop++;
                    if (loop == 1) {
                        player.sendMessage(yaml.getString("mechName") + "§r: " + yaml.getString("chatColor") + "こんにちは、" + "§r" + yaml.getString("masterName"));
                    }
                    if (loop >= 2) {
                        player.sendMessage(yaml.getString("mechName") + "§r: " + yaml.getString("chatColor") + "機体は準備万端です");
                        MechTalk.remove(player);
                        this.cancel();
                        return;
                    }
                }private int loop = 0;
            }.runTaskTimer(this, 30, 40);
        }
    }
}

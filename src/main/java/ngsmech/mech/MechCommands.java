package ngsmech.mech;

import ngsmech.mech.managers.setname;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class MechCommands implements CommandExecutor {

    Mech plugin = Mech.getPlugin(Mech.class);

    public String prefix = "§f§l[§6§lMech§f§l]";

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        //senderがPlayerでない場合
        if (!(sender instanceof Player)) {
            sender.sendMessage("this command is executed by players only");
            return false;
        }


        Player player = (Player) sender;

        //引数がない場合
        if (args.length == 0) {
            player.sendMessage(prefix + "§f§lVersion1.0");
            return false;
        }


        /************************************
         *          help
         ************************************/
        if (args[0].equalsIgnoreCase("help")) {
            if (args.length != 1) {
                plugin.help.help(1, player);
                return false;
            }

            Integer number = Integer.parseInt(args[0]);

            plugin.help.help(number, player);
            return false;
        }


        /************************************
         *          mechname
         ************************************/
        if (args[0].equalsIgnoreCase("mechname")) {
            if (args.length != 2) {
                player.sendMessage(prefix + "§3§l/mech mechname [Name]");
                return false;
            }

            String name = String.valueOf(args[1]);

            String resultName = name.replaceAll("&", "§");

            plugin.setname.setMechName(player, resultName);

            player.sendMessage(prefix + "§a§l設定しました！");
            return false;
        }

        /************************************
         *          mastername
         ************************************/
        if (args[0].equalsIgnoreCase("mastername")) {
            if (args.length != 2) {
                player.sendMessage(prefix + "§3§l/mech mastername [Name]");
                return false;
            }

            String name = String.valueOf(args[1]);

            String resultName = name.replaceAll("&", "§");

            plugin.setname.setMasterName(player, resultName);

            player.sendMessage(prefix + "§a§l設定しました！");
            return false;
        }

        /************************************
         *          chatcolor
         ************************************/
        if (args[0].equalsIgnoreCase("chatcolor")) {
            if (args.length != 2) {
                player.sendMessage(prefix + "§3§l/mech chatcolor [ColorCode]");
                return false;
            }

            String color = String.valueOf(args[1]);

            String resultColor = color.replaceAll("&", "§");

            plugin.setname.setChatColor(player, resultColor);

            player.sendMessage(prefix + "§a§l設定しました！");
            return false;
        }

        return false;
    }
}

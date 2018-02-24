package ngsmech.mech.managers;

import org.bukkit.entity.Player;

public class help {

    String prefix = "§f§l[§6§lMech§f§l]";

    public void help(Integer number, Player player) {
        switch (number) {
            case 1:
                help(player);
                break;
            default:
                help(player);
        }
    }

    private void help(Player player) {
        player.sendMessage("§7§l§m==========" + prefix + "§7§l§m==========");
        player.sendMessage("§7| §3§l/mech help: コマンドヘルプ");
        player.sendMessage("§7| §3§l/mech setname [Name]: 機体に名前をつけます");
        player.sendMessage("§7§l§m========================================");
    }
}

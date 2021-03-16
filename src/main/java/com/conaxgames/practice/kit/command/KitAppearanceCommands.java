package com.conaxgames.practice.kit.command;

import com.conaxgames.practice.Practice;
import com.conaxgames.practice.kit.Kit;
import com.conaxgames.util.TaskUtil;
import com.conaxgames.util.cmd.CommandHandler;
import com.conaxgames.util.cmd.annotation.Param;
import com.conaxgames.util.cmd.annotation.Text;
import com.conaxgames.util.cmd.annotation.commandTypes.SubCommand;
import com.conaxgames.util.finalutil.CC;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class KitAppearanceCommands implements CommandHandler {

    @SubCommand(baseCommand = "kit", name = "displayname")
    public void kitDisplayName(Player player, @Param(name = "kit") Kit kit, @Text(name = "name") String name) {
        if (kit == null) {
            player.sendMessage(CC.RED + "No kit found.");
            return;
        }

        name = ChatColor.translateAlternateColorCodes('&', name);

        kit.setDisplayName(name);
        TaskUtil.runAsync(() -> Practice.getInstance().getKitManager().saveKit(kit));
        player.sendMessage(CC.GREEN + "You have set the display name for the " + kit.getId() + " kit.");
    }
}

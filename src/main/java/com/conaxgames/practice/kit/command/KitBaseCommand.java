package com.conaxgames.practice.kit.command;

import com.conaxgames.practice.Practice;
import com.conaxgames.practice.kit.Kit;
import com.conaxgames.rank.Rank;
import com.conaxgames.util.TaskUtil;
import com.conaxgames.util.cmd.CommandHandler;
import com.conaxgames.util.cmd.annotation.Param;
import com.conaxgames.util.cmd.annotation.commandTypes.BaseCommand;
import com.conaxgames.util.cmd.annotation.commandTypes.SubCommand;
import com.conaxgames.util.finalutil.CC;
import com.google.common.base.Joiner;
import org.bukkit.entity.Player;

/**
 * The base command, and a command to create or copy kits.
 */
public class KitBaseCommand implements CommandHandler {

    @BaseCommand(name = "kit", rank = Rank.MANAGER)
    public void kitBaseCommand(Player player) {

    }

    @SubCommand(baseCommand = "kit", name = "create")
    public void kitCreate(Player player, @Param(name = "name") String id) {
        Kit existingKit = Practice.getInstance().getKitManager().getKitFromId(id);
        if (existingKit != null) {
            player.sendMessage(CC.RED + "A kit with that ID already exists.");
            return;
        }

        TaskUtil.runAsync(() -> Practice.getInstance().getKitManager().createKit(id));
        player.sendMessage(CC.GREEN + "Created and saved a kit with the ID " + id + ".");
    }

    @SubCommand(baseCommand = "kit", name = "copyarenas")
    public void kitCopyArenas(Player player, @Param(name = "kit1") Kit kit1, @Param(name = "kit2") Kit kit2) {
        if (kit1 == null) {
            player.sendMessage(CC.RED + "Kit 1 not found.");
            return;
        }

        if (kit2 == null) {
            player.sendMessage(CC.RED + "Kit 2 not found.");
            return;
        }

        // Kit 1's enabled arenas should only be
        // copied to Kit 2s, then saved
        kit2.getEnabledArenas().addAll(kit1.getEnabledArenas());
        TaskUtil.runAsync(() -> Practice.getInstance().getKitManager().saveKit(kit2));
        player.sendMessage(CC.GREEN + "Added the following arenas from " + kit1.getId() + " to " + kit2.getId()
                + ": " + Joiner.on(", ").join(kit1.getEnabledArenas()));
    }
}

package com.conaxgames.practice.kit.command;

import com.conaxgames.practice.Practice;
import com.conaxgames.practice.arena.Arena;
import com.conaxgames.practice.kit.Kit;
import com.conaxgames.util.TaskUtil;
import com.conaxgames.util.cmd.CommandHandler;
import com.conaxgames.util.cmd.annotation.Param;
import com.conaxgames.util.cmd.annotation.commandTypes.SubCommand;
import com.conaxgames.util.finalutil.CC;
import org.bukkit.entity.Player;

/**
 * Commands that deal with a kit's arenas.
 */
public class KitArenaCommands implements CommandHandler {

    @SubCommand(baseCommand = "kit", name = "togglearena")
    public void kitToggleArena(Player player, @Param(name = "kit") Kit kit, @Param(name = "arena") Arena arena) {
        if (kit == null) {
            player.sendMessage(CC.RED + "No kit found.");
            return;
        }

        if (arena == null) {
            player.sendMessage(CC.RED + "No arena found.");
            return;
        }

        player.sendMessage(CC.GREEN
                + (kit.toggleArena(arena) ? "Enabled" : "Disabled")
                + " arena " + arena.getName() + " for kit " + kit.getId() + ".");
        TaskUtil.runAsync(() -> Practice.getInstance().getKitManager().saveKit(kit));
    }
}

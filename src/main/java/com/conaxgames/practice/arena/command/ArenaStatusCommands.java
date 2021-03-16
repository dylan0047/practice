package com.conaxgames.practice.arena.command;

import com.conaxgames.practice.Practice;
import com.conaxgames.practice.arena.Arena;
import com.conaxgames.practice.kit.KitMask;
import com.conaxgames.util.cmd.CommandHandler;
import com.conaxgames.util.cmd.annotation.Param;
import com.conaxgames.util.cmd.annotation.commandTypes.SubCommand;
import com.conaxgames.util.finalutil.CC;
import com.google.common.base.Joiner;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Commands that deal with whether an arena is used in
 * specific conditions.
 */
public class ArenaStatusCommands implements CommandHandler {

    @SubCommand(baseCommand = "arena", name = "toggle")
    public void arenaToggle(Player player, @Param(name = "arena") String arena) {
        player.sendMessage(CC.GREEN
                + (Practice.getInstance().getArenaManager().toggleArena(arena) ? "Enabled" : "Disabled")
                + " arenas with the prefix " + arena + ".");
    }

    @SubCommand(baseCommand = "arena", name = "list")
    public void arenaList(Player player) {
        player.sendMessage("Arenas: "
                + Joiner.on(", ").join(Practice.getInstance().getArenaManager().getArenas().stream()
                .map(Arena::getName)
                .collect(Collectors.toList()))
        );
    }

}

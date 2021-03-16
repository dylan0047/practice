package com.conaxgames.practice.arena.command.param;

import com.conaxgames.practice.Practice;
import com.conaxgames.practice.arena.Arena;
import com.conaxgames.util.cmd.param.Parameter;
import org.bukkit.command.CommandSender;

public class ArenaCommandParameter extends Parameter<Arena> {

    public Arena transfer(CommandSender sender, String name) {
        return Practice.getInstance().getArenaManager().getArenaFromName(name);
    }
}

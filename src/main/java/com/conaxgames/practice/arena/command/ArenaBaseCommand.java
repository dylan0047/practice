package com.conaxgames.practice.arena.command;

import com.conaxgames.rank.Rank;
import com.conaxgames.util.cmd.CommandHandler;
import com.conaxgames.util.cmd.annotation.commandTypes.BaseCommand;
import org.bukkit.entity.Player;

public class ArenaBaseCommand implements CommandHandler {

    @BaseCommand(name = "arena", rank = Rank.MANAGER)
    public void arenaBaseCommand(Player player) {

    }
}

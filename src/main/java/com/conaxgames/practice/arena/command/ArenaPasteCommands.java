package com.conaxgames.practice.arena.command;

import com.conaxgames.practice.Practice;
import com.conaxgames.util.cmd.CommandHandler;
import com.conaxgames.util.cmd.annotation.Param;
import com.conaxgames.util.cmd.annotation.Text;
import com.conaxgames.util.cmd.annotation.commandTypes.SubCommand;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

/**
 * Commands that deal with pasting arenas (paste, generate)
 */
public class ArenaPasteCommands implements CommandHandler {

    @SubCommand(baseCommand = "arena", name = "genhelper")
    public void arenaGenHelper(Player player) {
        Block origin = player.getLocation().getBlock();
        Block up = origin.getRelative(BlockFace.UP);

        origin.setType(Material.SPONGE);
        up.setType(Material.SIGN_POST);

        final Sign sign = (Sign) up.getState();

        sign.setLine(0, ((int) player.getLocation().getPitch()) + "");
        sign.setLine(1, ((int) player.getLocation().getYaw()) + "");
        sign.update();

        player.sendMessage(ChatColor.GREEN + "Generator helper placed.");
    }

    @SubCommand(baseCommand = "arena", name = "paste")
    public void arenaPaste(Player player, @Param(name = "file") String file,
                           @Param(name = "x") int x, @Param(name = "z") int z,
                           @Text(name = "name") String name) {
        Practice.getInstance().getArenaManager().createArena(file, name, x, z);
    }

    @SubCommand(baseCommand = "arena", name = "generate")
    public void arenaGenerate(Player player, @Param(name = "file") String file,
                              @Param(name = "times") int times,
                              @Param(name = "startingX") int startingX,
                              @Param(name = "startingZ") int startingZ,
                              @Param(name = "incrementX") boolean incrementX,
                              @Param(name = "incrementZ") boolean incrementZ) {
        Practice.getInstance().getArenaManager().createArenas(file, times,
                startingX, startingZ,
                incrementX, incrementZ, 1000);
    }

}

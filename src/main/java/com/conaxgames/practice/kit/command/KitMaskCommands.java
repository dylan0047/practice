package com.conaxgames.practice.kit.command;

import com.conaxgames.practice.Practice;
import com.conaxgames.practice.kit.Kit;
import com.conaxgames.practice.kit.KitMask;
import com.conaxgames.rank.Rank;
import com.conaxgames.util.TaskUtil;
import com.conaxgames.util.cmd.CommandHandler;
import com.conaxgames.util.cmd.annotation.Param;
import com.conaxgames.util.cmd.annotation.commandTypes.BaseCommand;
import com.conaxgames.util.cmd.annotation.commandTypes.SubCommand;
import com.conaxgames.util.finalutil.CC;
import com.google.common.base.Joiner;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Commands that deal with a kit's mask.
 */
public class KitMaskCommands implements CommandHandler {

    @BaseCommand(name = "kitmask", rank = Rank.MANAGER)
    public void kitMaskBaseCommand(Player player) {

    }

    @SubCommand(baseCommand = "kitmask", name = "list")
    public void kitMaskList(Player player) {
        player.sendMessage("Available masks: "
                + Joiner.on(", ").join(Arrays.stream(KitMask.values())
                .map(KitMask::getId)
                .collect(Collectors.toList()))
        );
    }

    @SubCommand(baseCommand = "kitmask", name = "info")
    public void kitMaskInfo(Player player, @Param(name = "kit") Kit kit) {
        if (kit == null) {
            player.sendMessage(CC.RED + "No kit found.");
            return;
        }

        player.sendMessage(CC.GREEN + "Masks of kit " + kit.getId() + ":");
        player.sendMessage(Joiner.on(", ").join(
                Arrays.stream(KitMask.VALUES)
                        .filter(kit::meetsMask)
                        .map(KitMask::getName)
                        .collect(Collectors.toList()))
        );
    }

    @SubCommand(baseCommand = "kitmask", name = "add")
    public void kitMaskAdd(Player player, @Param(name = "kit") Kit kit, @Param(name = "mask") KitMask mask) {
        if (kit == null) {
            player.sendMessage(CC.RED + "No kit found.");
            return;
        }

        if (mask == null) {
            return;
        }

        if (kit.meetsMask(mask)) {
            player.sendMessage(CC.RED + "That kit already meets that mask.");
            return;
        }

        kit.setMask(kit.getMask() + mask.getMask());
        TaskUtil.runAsync(() -> Practice.getInstance().getKitManager().saveKit(kit));
        player.sendMessage(CC.GREEN + "Added mask " + mask.getName() + " to kit " + kit.getId() + ".");
    }

    @SubCommand(baseCommand = "kitmask", name = "remove")
    public void kitMaskRemove(Player player, @Param(name = "kit") Kit kit, @Param(name = "mask") KitMask mask) {
        if (kit == null) {
            player.sendMessage(CC.RED + "No kit found.");
            return;
        }

        if (mask == null) {
            return;
        }

        if (!kit.meetsMask(mask)) {
            player.sendMessage(CC.RED + "That kit doesn't meet that mask.");
            return;
        }

        kit.setMask(kit.getMask() - mask.getMask());
        TaskUtil.runAsync(() -> Practice.getInstance().getKitManager().saveKit(kit));
        player.sendMessage(CC.GREEN + "Removed mask " + mask.getName() + " from kit " + kit.getId() + ".");
    }
}

package com.conaxgames.practice.kit.command.param;

import com.conaxgames.practice.Practice;
import com.conaxgames.practice.kit.Kit;
import com.conaxgames.util.cmd.param.Parameter;
import org.bukkit.command.CommandSender;

public class KitCommandParameter extends Parameter<Kit> {

    public Kit transfer(CommandSender sender, String name) {
        return Practice.getInstance().getKitManager().getKitFromId(name);
    }
}

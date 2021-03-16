package com.conaxgames.practice.kit.command.param;

import com.conaxgames.practice.kit.KitMask;
import com.conaxgames.util.cmd.param.Parameter;
import org.bukkit.command.CommandSender;

public class KitMaskParameter extends Parameter<KitMask> {

    public KitMask transfer(CommandSender sender, String id) {
        return KitMask.getById(id);
    }
}

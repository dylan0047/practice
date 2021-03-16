package com.conaxgames.practice.queue;

import com.conaxgames.util.ItemBuilder;
import com.conaxgames.util.finalutil.CC;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public interface QueueItems {

    ItemStack JOIN_UNRANKED_QUEUE =
            new ItemBuilder(Material.IRON_SWORD)
                    .name(CC.GRAY + "Unranked Queue")
                    .build();

    ItemStack JOIN_RANKED_QUEUE =
            new ItemBuilder(Material.DIAMOND_SWORD)
                    .name(CC.GREEN + "Ranked Queue")
                    .build();

    ItemStack LEAVE_QUEUE =
            new ItemBuilder(Material.REDSTONE)
                    .name(CC.RED + "Leave Queue")
                    .build();

}

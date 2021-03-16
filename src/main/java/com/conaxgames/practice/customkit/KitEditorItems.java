package com.conaxgames.practice.customkit;

import com.conaxgames.util.ItemBuilder;
import com.conaxgames.util.finalutil.CC;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public interface KitEditorItems {

    ItemStack EDIT_KITS =
            new ItemBuilder(Material.BOOK)
                    .name(CC.GOLD + "Edit your Kits")
                    .build();

}

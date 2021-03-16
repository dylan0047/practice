package com.conaxgames.practice.customkit.listener;

import com.conaxgames.practice.Practice;
import com.conaxgames.practice.customkit.CustomKitManager;
import com.conaxgames.practice.customkit.KitEditorItems;
import com.conaxgames.practice.kit.inventory.KitSelectionInventory;
import com.conaxgames.practice.util.PlayerUtil;
import com.conaxgames.practice.util.item.ItemListener;

public class KitEditorItemListener extends ItemListener {

    public KitEditorItemListener() {
        addHandler(KitEditorItems.EDIT_KITS, player -> {
            new KitSelectionInventory(player, false, kit -> {
                CustomKitManager customKitManager = Practice.getInstance().getCustomKitManager();

                PlayerUtil.clearPlayer(player);

                customKitManager.getEditingKitMap().put(player.getUniqueId(), kit);
                player.teleport(customKitManager.getKitEditorLocation());
            }).show();
        });
    }

}

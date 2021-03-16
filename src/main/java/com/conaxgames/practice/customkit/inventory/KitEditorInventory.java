package com.conaxgames.practice.customkit.inventory;

import com.conaxgames.inventory.InventoryUI;
import com.conaxgames.practice.Practice;
import com.conaxgames.practice.kit.Kit;
import com.conaxgames.practice.kit.KitItems;
import com.conaxgames.util.finalutil.CC;
import com.conaxgames.util.finalutil.ItemUtil;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Objects;

@RequiredArgsConstructor
public class KitEditorInventory {

    private final Player player;
    private final Kit kit;

    public void show() {
        InventoryUI inventory = new InventoryUI("Edit Kit Loadouts", 3, 0);

        for (int slot = 1; slot <= 3; slot++) {
            int finalSlot = slot;

            ItemStack saveItem = ItemUtil.createItem(Material.CHEST, CC.GOLD + "Save kit "
                    + CC.YELLOW + kit.getDisplayName(false) + " #" + slot);
            inventory.setItem(slot + 3, 1, new InventoryUI.AbstractClickableItem(saveItem) {
                @Override
                public void onClick(InventoryClickEvent event) {
                    KitItems item = Practice.getInstance().getCustomKitManager().getKit(player, kit, finalSlot);
                    if (item == null) {
                        Practice.getInstance().getCustomKitManager().createKit(player, kit, finalSlot);
                        player.sendMessage(CC.GREEN + "Created kit " + finalSlot + ".");
                    } else {
                        item.setItems(player.getInventory().getContents());
                        Practice.getInstance().getCustomKitManager().saveKit(player, item);
                        player.sendMessage(CC.GREEN + "Saved kit " + finalSlot + ".");
                    }

                    addLoadAndDelete(inventory, finalSlot);
                }
            });

            addLoadAndDelete(inventory, slot);
        }

        player.openInventory(inventory.getCurrentPage());
    }

    private void addLoadAndDelete(InventoryUI inventory, int slot) {
        KitItems item = Practice.getInstance().getCustomKitManager().getKit(player, kit, slot);
        if (item == null) {
            return;
        }

        ItemStack loadItem = ItemUtil.createItem(Material.BOOK, CC.GOLD + "Load kit " + CC.YELLOW
                + kit.getDisplayName(false) + " #" + slot);
        ItemStack deleteItem = ItemUtil.createItem(Material.FLINT, CC.GOLD + "Delete kit "
                + CC.YELLOW + kit.getDisplayName(false) + " #" + slot);

        inventory.setItem(slot + 3, 2, new InventoryUI.AbstractClickableItem(loadItem) {
            @Override
            public void onClick(InventoryClickEvent event) {
                ItemStack[] contents = item.getItems();
                Arrays.stream(contents)
                        .filter(Objects::nonNull)
                        .filter(stack -> stack.getAmount() <= 0)
                        .forEach(stack -> stack.setAmount(1));

                player.getInventory().setContents(contents);
                player.updateInventory();
            }
        });

        inventory.setItem(slot + 3, 3, new InventoryUI.AbstractClickableItem(deleteItem) {
            @Override
            public void onClick(InventoryClickEvent event) {
                Practice.getInstance().getCustomKitManager().deleteKit(player, item);
                inventory.setItem(slot + 3, 2, null);
                inventory.setItem(slot + 3, 3, null);
                player.sendMessage(CC.GREEN + "Deleted kit " + slot + ".");
            }
        });
    }

}

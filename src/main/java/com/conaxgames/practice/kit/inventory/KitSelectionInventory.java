package com.conaxgames.practice.kit.inventory;

import com.conaxgames.inventory.InventoryUI;
import com.conaxgames.practice.Practice;
import com.conaxgames.practice.kit.Kit;
import com.conaxgames.practice.kit.KitMask;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.function.Consumer;

/**
 * This class is mainly for convenience, allowing
 * a callback to run after a player picks any kit.
 */
@RequiredArgsConstructor
public class KitSelectionInventory {

    private final Player player;
    private final boolean rankedOnly;
    private final Consumer<Kit> consumer;

    public void show() {
        InventoryUI inventory = new InventoryUI("Select a Kit", 2);

        Practice.getInstance().getKitManager().getKits().stream()
                .filter(kit -> kit.meetsMask(KitMask.ENABLED))
                .forEach(kit -> {
                    if (rankedOnly && !kit.meetsMask(KitMask.RANKED)) {
                        return;
                    }

                    inventory.addItem(
                            new InventoryUI.AbstractClickableItem(kit.getDisplayItem().clone()) {
                                public void onClick(InventoryClickEvent event) {
                                    consumer.accept(kit);
                                    event.getWhoClicked().closeInventory();
                                }
                            }
                    );
                });

        player.openInventory(inventory.getCurrentPage());
    }

}

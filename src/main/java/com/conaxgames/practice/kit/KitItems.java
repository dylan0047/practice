package com.conaxgames.practice.kit;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * This class was made because we want players to
 * be able to create and edit their own custom kits
 * but not to edit the server's global kits.
 */
@Getter
@Setter
public class KitItems {

    /**
     * Default armor supplied with this kit. Players are
     * not able to change this.
     */
    private ItemStack[] armor = new ItemStack[0];

    /**
     * Default items supplied with this kit. Players are
     * able to change this via the kit editor.
     */
    private ItemStack[] items = new ItemStack[0];

    /**
     * Default kit editor inventory for this kit.
     */
    private ItemStack[] kitEditorItems;

    /**
     * Only used for custom kits - the name of the kit these
     * items are designated for
     */
    private String kitName;

    /**
     * Only used for custom kits - the slot of this
     * kit that the player sees, that is shown in the
     * kit editor, etc.
     */
    private int slot;

    /**
     * Applies the kit's default armor and inventory
     * contents to the specified {@code player}.
     *
     * @param player the player to give the kit to
     */
    public void apply(Player player) {
        player.getInventory().clear();
        player.getInventory().setArmorContents(armor);
        player.getInventory().setContents(items);
        player.updateInventory();
    }
}

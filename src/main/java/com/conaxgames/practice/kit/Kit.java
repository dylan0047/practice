package com.conaxgames.practice.kit;

import com.conaxgames.practice.arena.Arena;
import com.google.common.base.Preconditions;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Kit {

    /**
     * The internal ID of this kit. This field is mainly
     * used to save and load from the database and distinguish
     * this kit from others.
     */
    @SerializedName("_id")
    private String id;

    /**
     * The name displayed to players whenever used.
     */
    private String displayName;

    /**
     * The item displayed to players whenever used.
     */
    private ItemStack displayItem;

    /**
     * The kit's default items that are given to players.
     */
    private KitItems defaultKitItems;

    /**
     * The kit's enabled arenas.
     */
    private final List<String> enabledArenas = new ArrayList<>();

    /**
     * The kit's flags as a mask.
     * See {@link KitMask}
     */
    private int mask;

    /**
     * Applies the kit's default armor and inventory
     * contents to the specified {@code player}.
     * <p>
     * If the kit does not have a {@link #defaultKitItems},
     * this method will throw a {@link NullPointerException}.
     *
     * @param player the player to give the kit to
     */
    public void apply(Player player) {
        Preconditions.checkNotNull(defaultKitItems, "defaultKitItems");
        defaultKitItems.apply(player);
    }

    /**
     * Determines whether or not this kit has a
     * specified {@link KitMask}.
     *
     * @param mask the mask to check against
     * @return true if this kit has the mask, otherwise false
     */
    public boolean meetsMask(KitMask mask) {
        return (this.mask & mask.getMask()) == mask.getMask();
    }

    /**
     * Returns the kit's display name, varying whether
     * or not {@code colored} is true or false.
     *
     * @param colored whether or not to return the colored name
     * @return the kit's display name
     */
    public String getDisplayName(boolean colored) {
        return colored ? displayName : ChatColor.stripColor(displayName);
    }

    /**
     * Toggles an arena by its name's prefix.
     *
     * @param arena the arena to enable/disable
     * @return true if enabled, otherwise false
     */
    public boolean toggleArena(Arena arena) {
        String arenaName = arena.getDisplayName();
        if (enabledArenas.contains(arenaName)) {
            enabledArenas.remove(arenaName);
            return false;
        } else {
            enabledArenas.add(arenaName);
            return true;
        }
    }
}

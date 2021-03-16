package com.conaxgames.practice.arena;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import org.bukkit.Location;

@Data
public class Arena {

    /**
     * The internal ID of this arena. This field
     * is used to load, save, and identify specific
     * arenas.
     */
    @SerializedName("_id")
    private final String name;

    /**
     * Locations players spawn at when a match starts.
     */
    private Location spawnA, spawnB;

    /**
     * Whether or not this arena is in use.
     */
    private transient boolean beingUsed;

    /**
     * Returns the arena name without numbers.
     *
     * @return the arena name without numbers.
     */
    public String getDisplayName() {
        return name.replaceAll("\\d", "");
    }

    /**
     * Returns the A spawn with 2.5 added to the Y so players
     * don't spawn in blocks.
     *
     * @return the A spawn
     */
    public Location getSpawnA() {
        if (spawnA == null) {
            return null;
        } else {
            return spawnA.clone().add(0, 2.5, 0);
        }
    }

    /**
     * Returns the B spawn with 2.5 added to the Y so players
     * don't spawn in blocks.
     *
     * @return the B spawn
     */
    public Location getSpawnB() {
        if (spawnB == null) {
            return null;
        } else {
            return spawnB.clone().add(0, 2.5, 0);
        }
    }

}

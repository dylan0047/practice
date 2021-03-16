package com.conaxgames.practice.arena.schematic;

import com.boydti.fawe.util.EditSessionBuilder;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.data.DataException;
import com.sk89q.worldedit.schematic.SchematicFormat;
import lombok.Getter;
import org.bukkit.World;

import java.io.File;
import java.io.IOException;

@Getter
public class Schematic {

    /**
     * The clipboard used to paste the schematic.
     */
    private CuboidClipboard clipboard;

    public Schematic(File file) {
        try {
            this.clipboard = SchematicFormat.getFormat(file).load(file);
        } catch (DataException | IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Pastes the schematic at the given coordinates.
     *
     * @param world the world to paste in
     * @param x     X coordinate to paste in
     * @param z     Z coordinate to paste in
     */
    public void pasteSchematic(World world, int x, int z) {
        Vector pasteLocation = new Vector(x, 100, z);
        EditSession editSession = new EditSessionBuilder(new BukkitWorld(world)).limitUnlimited().fastmode(true).build();
        editSession.enableQueue();

        try {
            this.clipboard.paste(editSession, pasteLocation, true);
        } catch (MaxChangedBlocksException e) {
            e.printStackTrace();
        }

        editSession.flushQueue();
    }

}

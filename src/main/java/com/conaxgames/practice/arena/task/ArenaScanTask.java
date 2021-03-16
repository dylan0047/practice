package com.conaxgames.practice.arena.task;

import com.conaxgames.practice.Practice;
import com.conaxgames.practice.arena.Arena;
import com.conaxgames.practice.arena.ArenaManager;
import com.conaxgames.practice.arena.schematic.Schematic;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.scheduler.BukkitRunnable;

@RequiredArgsConstructor
public class ArenaScanTask extends BukkitRunnable {

    private final ArenaManager arenaManager;
    private final String arenaName;
    private final World world;
    private final int x, z;
    private final Schematic schematic;

    public void run() {
        Arena arena = new Arena(arenaName);

        int minX = x - this.schematic.getClipboard().getWidth() - 200;
        int maxX = x + this.schematic.getClipboard().getWidth() + 200;
        int minZ = z - this.schematic.getClipboard().getLength() - 200;
        int maxZ = z + this.schematic.getClipboard().getLength() + 200;
        int minY = 72;
        int maxY = 72 + this.schematic.getClipboard().getHeight();

        blockLoop:
        for (int x = minX; x < maxX; x++) {
            for (int z = minZ; z < maxZ; z++) {
                for (int y = minY; y < maxY; y++) {
                    if (this.world.getBlockAt(x, y, z).getType() == Material.SPONGE) {
                        final Block spongeBlock = this.world.getBlockAt(x, y, z);
                        final Block blockAbove = spongeBlock.getRelative(BlockFace.UP, 1);

                        if (blockAbove.getState() instanceof Sign) {
                            Sign sign = (Sign) blockAbove.getState();
                            float pitch = Float.parseFloat(sign.getLine(0));
                            float yaw = Float.parseFloat(sign.getLine(1));
                            Location loc = new Location(this.world, x, y, z, yaw, pitch);

                            Bukkit.getScheduler().runTask(Practice.getInstance(), () -> {
                                blockAbove.setType(Material.AIR);
                                spongeBlock.setType(spongeBlock.getRelative(BlockFace.NORTH).getType());
                            });

                            if (arena.getSpawnA() == null) {
                                arena.setSpawnA(loc);
                                Bukkit.broadcastMessage("Set location A");
                            } else if (arena.getSpawnB() == null) {
                                arena.setSpawnB(loc);
                                Bukkit.broadcastMessage("Set location B");

                                arenaManager.addArena(arena);
                                arenaManager.saveArena(arena);

                                break blockLoop;
                            }
                        }
                    }
                }
            }
        }
    }
}

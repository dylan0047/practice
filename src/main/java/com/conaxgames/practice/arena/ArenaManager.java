package com.conaxgames.practice.arena;

import com.conaxgames.CorePlugin;
import com.conaxgames.internal.com.mongodb.client.MongoCollection;
import com.conaxgames.internal.com.mongodb.client.model.Filters;
import com.conaxgames.internal.com.mongodb.client.model.ReplaceOptions;
import com.conaxgames.practice.Practice;
import com.conaxgames.practice.arena.command.ArenaBaseCommand;
import com.conaxgames.practice.arena.command.ArenaPasteCommands;
import com.conaxgames.practice.arena.command.ArenaStatusCommands;
import com.conaxgames.practice.arena.command.param.ArenaCommandParameter;
import com.conaxgames.practice.arena.schematic.Schematic;
import com.conaxgames.practice.arena.task.ArenaScanTask;
import com.conaxgames.practice.kit.Kit;
import com.conaxgames.util.Config;
import com.conaxgames.util.cmd.CommandManager;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class ArenaManager {

    /**
     * Mongo collection that all server kits are saved in.
     */
    private final MongoCollection<Document> arenasCollection
            = Practice.getInstance().getMongoDatabase().getCollection("practice_arenas");

    /**
     * All of the loaded {@link Arena}s.
     */
    private final Map<String, Arena> idToArenaMap = new HashMap<>();

    /**
     * This is probably my least favorite thing about this plugin.
     * Arenas generate with the name as the schematic name and a number, therefore
     * this list is used as a list of "prefixes" (no number)
     */
    private final List<String> disabledArenas = new ArrayList<>();

    /**
     * Folder with every schematic used for generating arenas.
     *
     * @see #createArena(String, String, int, int)
     */
    private final File schematicsFolder = new File(Practice.getInstance().getDataFolder(), "schematics");

    public ArenaManager() {
        // Ensure the schematics folder exists.
        if (!schematicsFolder.exists()) {
            schematicsFolder.mkdir();
        }

        // Load all arenas from the database.
        arenasCollection.find().iterator().forEachRemaining(document -> {
            idToArenaMap.put(document.getString("_id"), Practice.GSON.fromJson(document.toJson(), Arena.class));
        });

        // Load disabled arenas.
        FileConfiguration config = Practice.getInstance().getConfig();
        if (config.contains("disabledArenas")) {
            disabledArenas.addAll(config.getStringList("disabledArenas"));
        }

        CommandManager commandManager = CorePlugin.getInstance().getCommandManager();
        commandManager.registerParameter(Arena.class, new ArenaCommandParameter());
        commandManager.registerAllClasses(Arrays.asList(
                new ArenaBaseCommand(),
                new ArenaPasteCommands(),
                new ArenaStatusCommands()
        ));
    }

    /**
     * Creates an arena with the given {@code schematicName} a
     * specified number of times.
     *
     * @param schematicName the name of the schematic file to paste
     * @param times         # of times to paste the schematic
     * @param startingX     X coordinate to start pasting at
     * @param startingZ     Z coordinate to start pasting at
     * @param incrementX    whether or not to increment the X coordinate
     * @param incrementZ    whether or not to increment the Z coordinate
     * @param increment     value to increment the {@code startingX} and {@code startingZ} by after every paste
     */
    public void createArenas(String schematicName, int times,
                             int startingX, int startingZ,
                             boolean incrementX, boolean incrementZ, int increment) {
        int currentX = startingX;
        int currentZ = startingZ;

        for (int i = 0; i < times; i++) {
            createArena(schematicName, schematicName + i, currentX, currentZ);

            if (incrementX) {
                currentX += increment;
            }

            if (incrementZ) {
                currentZ += increment;
            }
        }
    }

    /**
     * Loads a schematic and pastes it at the specified
     * {@code x} and {@code z}. After pasted, an ArenaScanTask
     * is created to search for the A and B locations, then
     * saves the arena.
     *
     * @param schematicName the name of the schematic file to paste
     * @param arenaName     specified arena name or null
     */
    public void createArena(String schematicName, String arenaName, int x, int z) {
        File file = new File(this.schematicsFolder, schematicName + ".schematic");
        if (!file.exists()) {
            return;
        }

        World world = Bukkit.getWorlds().get(0);

        Schematic schematic = new Schematic(file);
        schematic.pasteSchematic(world, x, z);

        new ArenaScanTask(this, arenaName, world, x, z, schematic)
                .runTaskLater(Practice.getInstance(), 20L);
    }

    /**
     * Gets all currently loaded arenas.
     *
     * @return All loaded arenas in the server.
     */
    public Collection<Arena> getArenas() {
        return idToArenaMap.values();
    }

    /**
     * Attempts to get the arena with the specified {@code name}.
     *
     * @param name the arena name to lookup
     * @return the arena if found, otherwise null
     */
    public Arena getArenaFromName(String name) {
        return idToArenaMap.get(name);
    }

    /**
     * Adds an arena to the {@link #idToArenaMap} list.
     *
     * @param arena arena to add
     */
    public void addArena(Arena arena) {
        idToArenaMap.put(arena.getName(), arena);
    }

    /**
     * Toggles an arena by its name's prefix.
     *
     * @param prefix the prefix of arena names to disable
     * @return true if disabled, otherwise false
     */
    public boolean toggleArena(String prefix) {
        if (disabledArenas.contains(prefix)) {
            disabledArenas.remove(prefix);
            return false;
        } else {
            disabledArenas.add(prefix);
            return true;
        }
    }

    /**
     * Saves all currently loaded and disabled arenas on the
     * server to the database.
     */
    public void saveAllArenas() {
        // Save all arenas to the database.
        idToArenaMap.values().forEach(this::saveArena);

        // Save disabled arenas.
        if (!disabledArenas.isEmpty()) {
            Config config = Practice.getInstance().getMainConfig();
            config.getConfig().set("disabledArenas", disabledArenas);
            config.save();
        }
    }

    /**
     * Saves the {@code arena} to the database.
     *
     * @param arena the arena to save
     */
    public void saveArena(Arena arena) {
        Document document = Document.parse(Practice.GSON.toJson(arena));

        arenasCollection.replaceOne(Filters.eq("_id", arena.getName()),
                document,
                new ReplaceOptions().upsert(true));
    }

    /**
     * Searches for a random arena that is enabled on the
     * given {@code kit}.
     *
     * @param kit the kit to search with
     * @return a random arena, or null if none found
     */
    public Arena getRandomArena(Kit kit) {
        List<Arena> possibleArenas = new ArrayList<>();

        for (Arena arena : idToArenaMap.values()) {
            String name = arena.getDisplayName();
            if (disabledArenas.contains(name)) {
                continue;
            }

            if (!kit.getEnabledArenas().contains(name)) {
                continue;
            }

            if (arena.isBeingUsed()) {
                continue;
            }

            possibleArenas.add(arena);
        }

        if (possibleArenas.size() == 0) {
            return null;
        }

        return possibleArenas.get(ThreadLocalRandom.current().nextInt(possibleArenas.size()));
    }
}

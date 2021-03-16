package com.conaxgames.practice.kit;

import com.conaxgames.CorePlugin;
import com.conaxgames.internal.com.mongodb.client.MongoCollection;
import com.conaxgames.internal.com.mongodb.client.model.Filters;
import com.conaxgames.internal.com.mongodb.client.model.ReplaceOptions;
import com.conaxgames.practice.Practice;
import com.conaxgames.practice.kit.command.*;
import com.conaxgames.practice.kit.command.param.KitCommandParameter;
import com.conaxgames.practice.kit.command.param.KitMaskParameter;
import com.conaxgames.util.cmd.CommandManager;
import org.bson.Document;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class KitManager {

    /**
     * Mongo collection that all server kits are saved in.
     */
    private final MongoCollection<Document> kitsCollection
            = Practice.getInstance().getMongoDatabase().getCollection("practice_kits");

    /**
     * All kits loaded on the server.
     */
    private final Map<String, Kit> idToKitMap = new HashMap<>();

    public KitManager() {
        // Load all kits from the database.
        kitsCollection.find().iterator().forEachRemaining(document -> {
            idToKitMap.put(document.getString("_id"), Practice.GSON.fromJson(document.toJson(), Kit.class));
        });

        CommandManager commandManager = CorePlugin.getInstance().getCommandManager();
        commandManager.registerParameter(Kit.class, new KitCommandParameter());
        commandManager.registerParameter(KitMask.class, new KitMaskParameter());
        commandManager.registerAllClasses(Arrays.asList(
                new KitBaseCommand(),
                new KitItemCommands(),
                new KitMaskCommands(),
                new KitAppearanceCommands(),
                new KitArenaCommands()
        ));
    }

    /**
     * Gets all currently loaded kits.
     *
     * @return All loaded kits in the server.
     */
    public Collection<Kit> getKits() {
        return idToKitMap.values();
    }

    /**
     * Attempts to get the kit with the specified {@code id}.
     *
     * @param id the kit ID to lookup
     * @return the kit if found, otherwise null
     */
    public Kit getKitFromId(String id) {
        return idToKitMap.get(id);
    }

    /**
     * Saves all currently loaded kits on the
     * server to the database.
     */
    public void saveAllKits() {
        idToKitMap.values().forEach(this::saveKit);
    }

    /**
     * Saves the {@code kit} to the database.
     *
     * @param kit the kit to save
     */
    public void saveKit(Kit kit) {
        Document document = Document.parse(Practice.GSON.toJson(kit));

        kitsCollection.replaceOne(Filters.eq("_id", kit.getId()),
                document,
                new ReplaceOptions().upsert(true));
    }

    /**
     * Creates a kit with the given {@code id}.
     *
     * @param id the id to assign to the kit
     */
    public void createKit(String id) {
        Kit kit = new Kit();
        kit.setId(id);
        kit.setMask(kit.getMask() + KitMask.ENABLED.getMask());

        saveKit(kit);

        idToKitMap.put(id, kit);
    }
}

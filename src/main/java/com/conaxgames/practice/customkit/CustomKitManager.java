package com.conaxgames.practice.customkit;

import com.conaxgames.CorePlugin;
import com.conaxgames.internal.com.mongodb.client.MongoCollection;
import com.conaxgames.internal.com.mongodb.client.model.Filters;
import com.conaxgames.internal.com.mongodb.client.model.ReplaceOptions;
import com.conaxgames.practice.Practice;
import com.conaxgames.practice.customkit.command.EditorLocationCommand;
import com.conaxgames.practice.customkit.listener.CustomKitBookListener;
import com.conaxgames.practice.customkit.listener.CustomKitLoadListener;
import com.conaxgames.practice.customkit.listener.KitEditorGeneralListener;
import com.conaxgames.practice.customkit.listener.KitEditorItemListener;
import com.conaxgames.practice.kit.Kit;
import com.conaxgames.practice.kit.KitItems;
import com.conaxgames.util.Config;
import com.conaxgames.util.ItemBuilder;
import com.conaxgames.util.finalutil.CC;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class CustomKitManager {

    /**
     * Mongo collection that all custom kits are saved in.
     */
    private final MongoCollection<Document> kitsCollection
            = Practice.getInstance().getMongoDatabase().getCollection("practice_custom_kits");

    /**
     * Map of all players -> their custom kits
     */
    private final Map<UUID, List<KitItems>> playerToKitList = new ConcurrentHashMap<>();

    /**
     * Map of all players -> the kit they're editing
     */
    @Getter
    private final Map<UUID, Kit> editingKitMap = new HashMap<>();

    /**
     * Where to teleport players when they
     * use the kit editor
     */
    @Getter
    @Setter
    private Location kitEditorLocation;

    public CustomKitManager() {
        // Ensure our kits collection has an index on the uuid field
        // to allow for decently fast lookups.
        kitsCollection.createIndex(new Document("uuid", 1));

        FileConfiguration config = Practice.getInstance().getConfig();
        if (config.contains("kitEditorLocation")) {
            kitEditorLocation = Practice.GSON.fromJson(config.getString("kitEditorLocation"), Location.class);
        }

        Bukkit.getPluginManager().registerEvents(new CustomKitLoadListener(this), Practice.getInstance());
        Bukkit.getPluginManager().registerEvents(new CustomKitBookListener(this), Practice.getInstance());
        Bukkit.getPluginManager().registerEvents(new KitEditorItemListener(), Practice.getInstance());
        Bukkit.getPluginManager().registerEvents(new KitEditorGeneralListener(this), Practice.getInstance());

        CorePlugin.getInstance().getCommandManager().registerAllClasses(Collections.singleton(new EditorLocationCommand()));
    }

    /**
     * Saves the {@code kitEditorLocation} to config.
     */
    public void saveEditorLocation() {
        if (kitEditorLocation == null) {
            return;
        }

        Config config = Practice.getInstance().getMainConfig();
        config.getConfig().set("kitEditorLocation", Practice.GSON.toJson(kitEditorLocation));
        config.save();
    }

    /**
     * Loads a player's kits from the database to the
     * {@code playerToKitList} map
     *
     * @param player the player to load kits for
     */
    public void loadKits(Player player) {
        UUID uuid = player.getUniqueId();
        playerToKitList.put(uuid, new ArrayList<>());

        kitsCollection.find(Filters.eq("uuid", uuid.toString())).iterator().forEachRemaining(document ->
                playerToKitList.get(uuid).add(Practice.GSON.fromJson(document.toJson(), KitItems.class)));
    }

    /**
     * Removes a player's custom kits from the map.
     *
     * @param player the player whose kits to remove
     */
    public void clearKits(Player player) {
        playerToKitList.remove(player.getUniqueId());
        editingKitMap.remove(player.getUniqueId());
    }

    /**
     * Saves a player's kit to the database and
     * updates the {@code playerToKitList} map with the
     * new kit
     *
     * @param player the player to save the kit for
     * @param items  the player's chosen items
     */
    public void saveKit(Player player, KitItems items) {
        List<KitItems> playerKits = playerToKitList.computeIfAbsent(player.getUniqueId(), uuid -> new ArrayList<>());
        playerKits.removeIf(kitItems -> kitItems.getKitName().equals(items.getKitName()) && kitItems.getSlot() == items.getSlot());
        playerKits.add(items);

        Document document = Document.parse(Practice.GSON.toJson(items));
        document.append("uuid", player.getUniqueId().toString());
        kitsCollection.replaceOne(
                Filters.and(
                        Filters.eq("kitName", items.getKitName()),
                        Filters.eq("slot", items.getSlot())
                ),
                document,
                new ReplaceOptions().upsert(true)
        );
    }

    /**
     * Removes a player's custom kit from the map and
     * database.
     *
     * @param player the player whose kit to remove
     * @param items  the kit
     */
    public void deleteKit(Player player, KitItems items) {
        List<KitItems> playerKits = playerToKitList.computeIfAbsent(player.getUniqueId(), uuid -> new ArrayList<>());
        playerKits.removeIf(kit -> kit.getKitName().equals(items.getKitName()) && kit.getSlot() == items.getSlot());

        kitsCollection.deleteOne(
                Filters.and(
                        Filters.eq("kitName", items.getKitName()),
                        Filters.eq("slot", items.getSlot())
                )
        );
    }

    /**
     * Creates an editable copy of the {@code kit}'s
     * default KitItems instance and adds it to the
     * {@code player}'s kit list.
     *
     * @param player the player to create the kit for
     * @param kit    the kit to copy
     * @param slot   the slot of the custom kit
     * @return the created kit, or null if failed
     */
    public KitItems createKit(Player player, Kit kit, int slot) {
        if (kit.getDefaultKitItems() == null) {
            return null;
        }

        KitItems items = new KitItems();
        items.setItems(kit.getDefaultKitItems().getItems());
        items.setArmor(kit.getDefaultKitItems().getArmor());
        items.setKitName(kit.getId());
        items.setSlot(slot);

        saveKit(player, items);

        return items;
    }

    /**
     * Returns a list of the player's custom
     * kits with the specified kit.
     *
     * @param player the player to fetch the custom kits for
     * @param kit    the kit to fetch the custom kits for
     * @return the custom kits, or null if the player doesn't have one
     */
    public List<KitItems> getKits(Player player, Kit kit) {
        return playerToKitList.computeIfAbsent(player.getUniqueId(), uuid -> new ArrayList<>()).stream()
                .filter(items -> items.getKitName().equals(kit.getId()))
                .sorted(Comparator.comparingInt(KitItems::getSlot))
                .collect(Collectors.toList());
    }

    /**
     * Returns a player's custom kit with the specified kit
     * and slot.
     *
     * @param player the player to fetch the custom kit for
     * @param kit    the kit to fetch the custom kit for
     * @param slot   the slot of the custom kit
     * @return the custom kit, or null if the player doesn't have one
     */
    public KitItems getKit(Player player, Kit kit, int slot) {
        return playerToKitList.computeIfAbsent(player.getUniqueId(), uuid -> new ArrayList<>()).stream()
                .filter(items -> items.getKitName().equals(kit.getId()) && items.getSlot() == slot)
                .findFirst().orElse(null);
    }

    /**
     * Gives the player the kit's default items if
     * they don't have any custom kits setup, otherwise
     * give them a choice w/ books.
     *
     * @param player the player to give the items
     * @param kit    the kit
     */
    public void giveBooksOrDefaultKit(Player player, Kit kit) {
        List<KitItems> kits = getKits(player, kit);
        if (kits.isEmpty()) {
            player.sendMessage(CC.GREEN + "You've equipped the default kit.");
            kit.apply(player);
            return;
        }

        player.getInventory().setItem(8, new ItemBuilder(Material.ENCHANTED_BOOK)
                .name(CC.GREEN + "Default Kit")
                .build());
        AtomicInteger slot = new AtomicInteger(0);
        kits.forEach(customKit ->
                player.getInventory().setItem(slot.getAndIncrement(),
                        new ItemBuilder(Material.ENCHANTED_BOOK)
                                .name(CC.GREEN + "Kit #" + customKit.getSlot())
                                .build()));
    }

}

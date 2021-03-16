package com.conaxgames.practice.queue;

import com.conaxgames.practice.Practice;
import com.conaxgames.practice.kit.Kit;
import com.conaxgames.practice.kit.KitMask;
import com.conaxgames.practice.queue.listener.QueueItemListener;
import com.conaxgames.practice.queue.listener.QueueQuitListener;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class QueueManager {

    /**
     * A table representing a Kit, whether or not
     * it's Ranked, and the actual {@link KitQueue} object
     */
    private final Table<Kit, Boolean, KitQueue> kitQueues = HashBasedTable.create();

    /**
     * Maps players' UUIDs to their corresponding {@link QueueEntry}.
     */
    private final Map<UUID, QueueEntry> uuidToKitEntryMap = new HashMap<>();

    public QueueManager() {
        Practice.getInstance().getKitManager().getKits().stream().filter(kit -> kit.meetsMask(KitMask.ENABLED))
                .forEach(kit -> {
                    kitQueues.put(kit, false, new KitQueue(kit, false));

                    if (kit.meetsMask(KitMask.RANKED)) {
                        kitQueues.put(kit, true, new KitQueue(kit, true));
                    }
                });

        Bukkit.getScheduler().runTaskTimer(Practice.getInstance(), () -> {
            kitQueues.values().forEach(KitQueue::tick);
        }, 20L, 20L);

        Bukkit.getPluginManager().registerEvents(new QueueQuitListener(this), Practice.getInstance());
        Bukkit.getPluginManager().registerEvents(new QueueItemListener(this), Practice.getInstance());
    }

    /**
     * Determines whether or not the {@code player} is in a queue.
     *
     * @param player the player to check for
     * @return true if the player is in a queue, otherwise false
     */
    public boolean inQueue(Player player) {
        return uuidToKitEntryMap.containsKey(player.getUniqueId());
    }

    /**
     * Attempts to add the {@code player} to any matching queue.
     *
     * @param player the player to add
     * @param kit    the kit to queue for
     * @param ranked whether or not the player is queuing for ranked
     * @return true if the player was successfully added, otherwise false
     */
    public boolean addToQueue(Player player, Kit kit, boolean ranked) {
        if (!Practice.getInstance().getLobbyManager().inLobby(player)) {
            return false;
        }

        KitQueue queue = kitQueues.get(kit, ranked);
        if (queue == null) {
            return false;
        }

        UUID uuid = player.getUniqueId();

        // TODO: Set elo in constructor once elo is saved
        QueueEntry entry = new QueueEntry(queue, Collections.singleton(uuid));
        queue.addEntry(entry);
        uuidToKitEntryMap.put(uuid, entry);

        Practice.getInstance().getLobbyManager().giveLobbyInventory(player);
        return true;
    }

    /**
     * Attempts to remove the {@code player} from their queue, if present.
     *
     * @param player the player to remove
     * @return true if the player was successfully removed, otherwise false
     */
    public boolean removeFromQueue(Player player) {
        QueueEntry entry = uuidToKitEntryMap.remove(player.getUniqueId());
        if (entry == null) {
            return false;
        }

        entry.getKitQueue().removeEntry(entry);

        Practice.getInstance().getLobbyManager().giveLobbyInventory(player);
        return true;
    }
}

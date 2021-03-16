package com.conaxgames.practice.util.item;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public abstract class ItemListener implements Listener {

    private static final Map<UUID, Long> canUseButton = new ConcurrentHashMap<>();

    private final Map<ItemStack, Consumer<Player>> handlers = new HashMap<>();

    protected boolean allowItemDrops = false;

    protected final void addHandler(ItemStack stack, Consumer<Player> handler) {
        this.handlers.put(stack, handler);
    }

    public static void addButtonCooldown(Player player, int ms) {
        canUseButton.put(player.getUniqueId(), System.currentTimeMillis() + ms);
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        if (!allowItemDrops && handlers.containsKey(event.getItemDrop().getItemStack())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!event.hasItem() || !event.getAction().name().contains("RIGHT_")) {
            return;
        }

        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        for (Map.Entry<ItemStack, Consumer<Player>> entry : handlers.entrySet()) {
            if (item.isSimilar(entry.getKey())) {
                boolean permitted = canUseButton.getOrDefault(player.getUniqueId(), 0L) < System.currentTimeMillis();

                if (permitted) {
                    entry.getValue().accept(player);
                    canUseButton.put(player.getUniqueId(), System.currentTimeMillis() + 500);
                }

                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        canUseButton.remove(event.getPlayer().getUniqueId());
    }
}

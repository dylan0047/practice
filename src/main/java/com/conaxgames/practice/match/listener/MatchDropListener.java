package com.conaxgames.practice.match.listener;

import com.conaxgames.practice.match.Match;
import com.conaxgames.practice.match.MatchManager;
import com.conaxgames.practice.match.event.MatchEndEvent;
import com.conaxgames.util.TaskUtil;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;

@RequiredArgsConstructor
public class MatchDropListener implements Listener {

    private final MatchManager matchManager;

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        if (!matchManager.inMatch(player.getUniqueId())) {
            return;
        }

        Item drop = event.getItemDrop();
        Material type = drop.getItemStack().getType();
        if (type == Material.GLASS_BOTTLE) {
            drop.remove();
            return;
        }

        if (type == Material.ENCHANTED_BOOK) {
            event.setCancelled(true);
            return;
        }

        Match match = matchManager.getMatch(player.getUniqueId());
        match.getEntitiesToRemove().add(drop);
        TaskUtil.runLater(() -> {
            drop.remove();
            match.getEntitiesToRemove().remove(drop);
        }, 20L * 5);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        if (!matchManager.inMatch(player.getUniqueId())) {
            return;
        }

        event.getDrops().clear();
    }

    @EventHandler
    public void onMatchEnd(MatchEndEvent event) {
        Match match = event.getMatch();
        match.getEntitiesToRemove().forEach(Entity::remove);
        match.getEntitiesToRemove().clear();
    }
}

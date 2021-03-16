package com.conaxgames.practice.queue.listener;

import com.conaxgames.practice.queue.QueueManager;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

@RequiredArgsConstructor
public class QueueQuitListener implements Listener {

    private final QueueManager queueManager;

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        queueManager.removeFromQueue(player);
    }
}

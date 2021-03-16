package com.conaxgames.practice.duel.listener;

import com.conaxgames.practice.duel.DuelManager;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

@RequiredArgsConstructor
public class DuelQuitListener implements Listener {

    private final DuelManager duelManager;

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        duelManager.removeRequests(event.getPlayer().getUniqueId());
    }

}

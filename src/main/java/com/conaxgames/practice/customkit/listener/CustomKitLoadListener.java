package com.conaxgames.practice.customkit.listener;

import com.conaxgames.practice.customkit.CustomKitManager;
import com.conaxgames.util.TaskUtil;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

@RequiredArgsConstructor
public class CustomKitLoadListener implements Listener {

    private final CustomKitManager customKitManager;

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        TaskUtil.runAsync(() -> customKitManager.loadKits(player));
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        customKitManager.clearKits(player);
    }

}

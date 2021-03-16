package com.conaxgames.practice.lobby.listener;

import com.conaxgames.practice.lobby.LobbyManager;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * This class prevents misc. stuff that's not handled
 * in any other specific lobby listener.
 */
@RequiredArgsConstructor
public class LobbyGeneralListener implements Listener {

    private final LobbyManager lobbyManager;

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        lobbyManager.sendToLobby(player);
        lobbyManager.updateVisibility(player);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();

        if (lobbyManager.inLobby(player)) {
            event.getDrops().clear();
        }
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity();

        if (lobbyManager.inLobby(player)) {
            event.setFoodLevel(20);
            event.setCancelled(true);
        }
    }
}

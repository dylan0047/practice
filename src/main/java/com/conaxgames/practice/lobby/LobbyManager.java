package com.conaxgames.practice.lobby;

import com.conaxgames.practice.Practice;
import com.conaxgames.practice.customkit.KitEditorItems;
import com.conaxgames.practice.lobby.listener.LobbyGeneralListener;
import com.conaxgames.practice.lobby.listener.LobbyInteractionListener;
import com.conaxgames.practice.queue.QueueItems;
import com.conaxgames.practice.util.PlayerUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class LobbyManager {

    private final Location spawnLocation;

    public LobbyManager() {
        Bukkit.getPluginManager().registerEvents(new LobbyGeneralListener(this), Practice.getInstance());
        Bukkit.getPluginManager().registerEvents(new LobbyInteractionListener(this), Practice.getInstance());

        this.spawnLocation = Bukkit.getWorlds().get(0).getSpawnLocation();
    }

    /**
     * Teleports a player to the spawn and resets their
     * inventory using {@link #giveLobbyInventory(Player)}
     *
     * @param player the player to send to the lobby
     */
    public void sendToLobby(Player player) {
        player.teleport(spawnLocation);

        giveLobbyInventory(player);
        updateVisibility(player);

        Practice.getInstance().getCustomKitManager().getEditingKitMap().remove(player.getUniqueId());
    }

    /**
     * Hides other players for the given {@code player}
     * if appropriate.
     *
     * @param viewer the player to hide others for
     */
    public void updateVisibility(Player viewer) {
        Bukkit.getOnlinePlayers().forEach(onlinePlayer -> {
            // TODO: Check for parties, following, etc.

            viewer.hidePlayer(onlinePlayer);
            onlinePlayer.hidePlayer(viewer);
        });
    }

    /**
     * Gives a player the appropriate lobby items. Will differ
     * depending on certain factors, including whether a player is
     * in a party, a queue, etc.
     *
     * @param player the player to give items to
     */
    public void giveLobbyInventory(Player player) {
        // TODO: Give appropriate queue, kit editor, leaderboards items when implemented
        PlayerUtil.clearPlayer(player);

        if (Practice.getInstance().getQueueManager().inQueue(player)) {
            player.getInventory().setItem(8, QueueItems.LEAVE_QUEUE);
            return;
        }

        player.getInventory().setItem(0, QueueItems.JOIN_UNRANKED_QUEUE);
        player.getInventory().setItem(1, QueueItems.JOIN_RANKED_QUEUE);

        player.getInventory().setItem(8, KitEditorItems.EDIT_KITS);
    }

    /**
     * Checks against all other possible "state"s to determine
     * whether or not a player is in the lobby.
     *
     * @param player the player to check
     * @return true if the player is in the lobby, otherwise false
     */
    public boolean inLobby(Player player) {
        return !Practice.getInstance().getMatchManager().inMatch(player.getUniqueId());
    }
}

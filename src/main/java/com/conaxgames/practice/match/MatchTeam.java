package com.conaxgames.practice.match;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

@Getter
public class MatchTeam {

    /**
     * All players that are in this team.
     */
    private final Set<UUID> players;

    /**
     * All players that are currently alive in this team.
     */
    private final Set<UUID> livingPlayers = new HashSet<>();

    public MatchTeam(Set<UUID> players) {
        this.players = players;
        this.livingPlayers.addAll(players);
    }

    /**
     * Removes a player from the {@code livingPlayers} set.
     *
     * @param uuid the uuid of the player to kill
     */
    public void killPlayer(UUID uuid) {
        livingPlayers.remove(uuid);
    }

    /**
     * Determines whether or not the {@code uuid} is alive.
     *
     * @param uuid the uuid of the player to check for
     * @return true if the player is alive, otherwise false
     */
    public boolean isAlive(UUID uuid) {
        return livingPlayers.contains(uuid);
    }

    /**
     * Returns a list with everyone in the {@code players}
     * set as a Player object.
     *
     * @return a list with everyone in the {@code players} set as a Player object
     */
    public List<Player> getPlayerList() {
        return players.stream().map(Bukkit::getPlayer).filter(Objects::nonNull).collect(Collectors.toList());
    }

    /**
     * Returns a list with everyone in the {@code livingPlayers}
     * set as a Player object.
     *
     * @return a list with everyone in the {@code livingPlayers} set as a Player object
     */
    public List<Player> getLivingPlayerList() {
        return livingPlayers.stream().map(Bukkit::getPlayer).filter(Objects::nonNull).collect(Collectors.toList());
    }

    /**
     * Broadcasts a message to every living player in the match.
     *
     * @param message the message to broadcast
     */
    public void broadcast(String message) {
        getLivingPlayerList().forEach(player -> player.sendMessage(message));
    }

    /**
     * Broadcasts a sound to every living player in the match.
     *
     * @param sound the sound to broadcast
     * @param pitch the pitch of the sound
     */
    public void broadcast(Sound sound, float pitch) {
        getLivingPlayerList().forEach(player -> player.playSound(player.getLocation(), sound, 10, pitch));
    }
}

package com.conaxgames.practice.queue;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public class QueueEntry {

    /**
     * The time this entry was created.
     */
    private final long startTime = System.currentTimeMillis();

    /**
     * The corresponding queue for this entry.
     */
    private final KitQueue kitQueue;

    /**
     * The members that are a part of this entry.
     */
    private final Set<UUID> members;

    /**
     * If this is not for a ranked queue, this will not be assigned.
     * If a party match, the party members' average elo.
     * If a solo match, the member's elo.
     */
    private int elo = 0;

    /**
     * Returns all players in the {@code members}field.
     *
     * @return all of this entry's players
     */
    List<Player> getPlayers() {
        return members.stream()
                .map(Bukkit::getPlayer)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * Returns all player names in this entry.
     *
     * @return all player names in this entry
     */
    String getMemberNames() {
        return members.stream()
                .map(Bukkit::getPlayer)
                .filter(Objects::nonNull)
                .map(Player::getName)
                .collect(Collectors.joining(", "));
    }

}

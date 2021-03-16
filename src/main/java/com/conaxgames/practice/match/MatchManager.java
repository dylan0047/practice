package com.conaxgames.practice.match;

import com.conaxgames.practice.Practice;
import com.conaxgames.practice.arena.Arena;
import com.conaxgames.practice.kit.Kit;
import com.conaxgames.practice.match.listener.*;
import io.netty.util.internal.ConcurrentSet;
import lombok.Getter;
import org.bukkit.Bukkit;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class MatchManager {

    /**
     * Set of all active matches
     */
    private final Set<Match> matches = new ConcurrentSet<>();

    /**
     * Map of all players in a match -> their match
     */
    private final Map<UUID, Match> playerToMatchMap = new ConcurrentHashMap<>();

    public MatchManager() {
        Bukkit.getPluginManager().registerEvents(new MatchKnockbackListener(), Practice.getInstance());
        Bukkit.getPluginManager().registerEvents(new MatchBlockListener(this), Practice.getInstance());
        Bukkit.getPluginManager().registerEvents(new MatchDropListener(this), Practice.getInstance());
        Bukkit.getPluginManager().registerEvents(new MatchDeathListener(this), Practice.getInstance());
        Bukkit.getPluginManager().registerEvents(new MatchDeathMessageListener(this), Practice.getInstance());
        Bukkit.getPluginManager().registerEvents(new MatchRegenListener(this), Practice.getInstance());
        Bukkit.getPluginManager().registerEvents(new MatchDamageListener(this), Practice.getInstance());
    }

    /**
     * Creates a match with the given parameters and
     * adds all of the match participants to the caches.
     *
     * @param kit      the match kit
     * @param isRanked whether or not the match is ranked
     * @param teams    all teams participating in the match
     * @return the created match or null if failed
     */
    public Match createMatch(Kit kit, boolean isRanked, MatchTeam... teams) {
        Arena arena = Practice.getInstance().getArenaManager().getRandomArena(kit);
        if (arena == null) {
            Practice.getInstance().getLogger().warning("Failed to get an arena for kit " + kit.getId());
            return null;
        }

        Match match = new Match(arena, kit, isRanked, teams);

        // Add all of the match's participants to the map
        match.getTeams().forEach(team -> team.getLivingPlayers().forEach(uuid -> playerToMatchMap.put(uuid, match)));

        return match;
    }

    /**
     * Returns whether or not the given {@code uuid} is
     * in a match.
     *
     * @param uuid the uuid to check
     * @return true if the uuid is in a match, otherwise false
     */
    public boolean inMatch(UUID uuid) {
        return playerToMatchMap.containsKey(uuid);
    }

    /**
     * Attempts to get the match with the given {@code uuid} in it.
     *
     * @param uuid the uuid to lookup
     * @return the match if found, otherwise null
     */
    public Match getMatch(UUID uuid) {
        return playerToMatchMap.get(uuid);
    }

}

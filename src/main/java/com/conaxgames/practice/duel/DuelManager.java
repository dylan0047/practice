package com.conaxgames.practice.duel;

import com.conaxgames.CorePlugin;
import com.conaxgames.practice.Practice;
import com.conaxgames.practice.duel.command.DuelCommands;
import com.conaxgames.practice.duel.listener.DuelQuitListener;
import com.conaxgames.practice.kit.Kit;
import org.bukkit.Bukkit;

import java.util.*;

public class DuelManager {

    private final Map<UUID, Set<MatchRequest>> playerToMatchRequestsMap = new HashMap<>();

    public DuelManager() {
        Bukkit.getScheduler().runTaskTimer(Practice.getInstance(),
                () -> playerToMatchRequestsMap.forEach((key, value) -> value.removeIf(MatchRequest::hasExpired)),
                20L, 20L);

        Bukkit.getPluginManager().registerEvents(new DuelQuitListener(this), Practice.getInstance());

        CorePlugin.getInstance().getCommandManager()
                .registerAllClasses(Collections.singleton(new DuelCommands(this)));
    }

    /**
     * Attempts to get a {@link MatchRequest} with the specified
     * requester and requestee.
     *
     * @param requester the person that requested the match
     * @param requestee the person that's receiving the request
     * @param kit       the request kit
     * @return the request if found, otherwise null
     */
    public MatchRequest getRequest(UUID requester, UUID requestee, Kit kit) {
        if (!playerToMatchRequestsMap.containsKey(requestee)) {
            return null;
        }

        return playerToMatchRequestsMap.get(requestee).stream()
                .filter(request -> request.getRequester() == requester && request.getKit().getId().equals(kit.getId()))
                .findFirst().orElse(null);
    }

    /**
     * Creates a match request between the {@code requester} and
     * {@code requestee} with the specified {@code kit}.
     *
     * @param requester the person that requested the match
     * @param requestee the person that's receiving the request
     * @param kit       the request kit
     */
    public void createRequest(UUID requester, UUID requestee, Kit kit) {
        MatchRequest request = new MatchRequest(requester, requestee, kit);

        playerToMatchRequestsMap.computeIfAbsent(requestee, uuid -> new HashSet<>()).add(request);
    }

    /**
     * Removes all requests for the {@code requestee} if
     * present.
     *
     * @param requestee the player whose requests to remove
     */
    public void removeRequests(UUID requestee) {
        playerToMatchRequestsMap.remove(requestee);
    }
}

package com.conaxgames.practice.duel;

import com.conaxgames.practice.Practice;
import com.conaxgames.practice.kit.Kit;
import com.conaxgames.practice.match.MatchTeam;
import lombok.Data;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Data
public class MatchRequest {

    private final UUID requester;
    private final UUID requestee;
    private final Kit kit;

    private final long creationTime = System.currentTimeMillis();

    /**
     * Returns whether or not this request was created
     * more than 30 seconds ago.
     *
     * @return true if this request was sent more than 30 seconds ago, otherwise false
     */
    public boolean hasExpired() {
        return System.currentTimeMillis() - creationTime >= TimeUnit.SECONDS.toMillis(30);
    }

    /**
     * Creates a match between the {@code requester} and {@code requestee}.
     */
    public void accept() {
        // TODO: Add support for parties once they're a thing.
        Practice.getInstance().getMatchManager().createMatch(kit, false,
                new MatchTeam(Collections.singleton(requester)),
                new MatchTeam(Collections.singleton(requestee)));
    }

}

package com.conaxgames.practice.match.event;

import com.conaxgames.practice.match.Match;

public class MatchStartEvent extends MatchEvent {

    public MatchStartEvent(Match match) {
        super(match);
    }
}

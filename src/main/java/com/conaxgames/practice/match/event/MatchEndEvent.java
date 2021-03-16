package com.conaxgames.practice.match.event;

import com.conaxgames.event.BaseEvent;
import com.conaxgames.practice.match.Match;
import lombok.RequiredArgsConstructor;

public class MatchEndEvent extends MatchEvent {

    public MatchEndEvent(Match match) {
        super(match);
    }
}

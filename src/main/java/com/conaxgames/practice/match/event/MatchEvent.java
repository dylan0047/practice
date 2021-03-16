package com.conaxgames.practice.match.event;

import com.conaxgames.event.BaseEvent;
import com.conaxgames.practice.match.Match;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MatchEvent extends BaseEvent {

    private final Match match;

}

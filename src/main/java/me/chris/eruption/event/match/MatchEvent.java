package me.chris.eruption.event.match;

import lombok.Setter;
import me.chris.eruption.match.Match;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.chris.eruption.util.BukkitEvent;

@Getter
@Setter
@RequiredArgsConstructor
public class MatchEvent extends BukkitEvent {

    private Match match;

    public MatchEvent(Match match){
    }
}

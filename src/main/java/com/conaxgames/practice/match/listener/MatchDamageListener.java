package com.conaxgames.practice.match.listener;

import com.conaxgames.practice.match.Match;
import com.conaxgames.practice.match.MatchManager;
import com.conaxgames.practice.match.MatchState;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

@RequiredArgsConstructor
public class MatchDamageListener implements Listener {

    private final MatchManager matchManager;

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity();
        if (!matchManager.inMatch(player.getUniqueId())) {
            return;
        }

        Match match = matchManager.getMatch(player.getUniqueId());
        if (match.getState() != MatchState.IN_PROGRESS) {
            event.setCancelled(true);
        }
    }

}

package com.conaxgames.practice.match.listener;

import com.conaxgames.practice.kit.KitMask;
import com.conaxgames.practice.match.Match;
import com.conaxgames.practice.match.MatchManager;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRegainHealthEvent;

@RequiredArgsConstructor
public class MatchRegenListener implements Listener {

    private final MatchManager matchManager;

    @EventHandler
    public void onEntityRegainHealth(EntityRegainHealthEvent event) {
        if (!(event.getEntity() instanceof Player)
                || event.getRegainReason() != EntityRegainHealthEvent.RegainReason.SATIATED) {
            return;
        }

        Player player = (Player) event.getEntity();
        if (!matchManager.inMatch(player.getUniqueId())) {
            return;
        }

        Match match = matchManager.getMatch(player.getUniqueId());
        if (match.getKit().meetsMask(KitMask.DISALLOW_HEALTH_REGEN)) {
            event.setCancelled(true);
        }
    }

}

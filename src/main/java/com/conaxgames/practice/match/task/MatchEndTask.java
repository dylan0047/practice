package com.conaxgames.practice.match.task;

import com.conaxgames.practice.Practice;
import com.conaxgames.practice.kit.KitMask;
import com.conaxgames.practice.match.Match;
import com.conaxgames.practice.match.MatchManager;
import com.conaxgames.practice.util.PlayerUtil;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

@RequiredArgsConstructor
public class MatchEndTask extends BukkitRunnable {

    private final Match match;
    private int countdown = 5;

    public void run() {
        if (countdown == 0) {
            MatchManager matchManager = Practice.getInstance().getMatchManager();
            matchManager.getMatches().remove(match);

            match.getTeams().forEach(team -> team.getPlayerList().forEach(player -> {
                Practice.getInstance().getLobbyManager().sendToLobby(player);
                matchManager.getPlayerToMatchMap().remove(player.getUniqueId());

                if (match.getKit().meetsMask(KitMask.HEARTS)) {
                    PlayerUtil.updateNametag(player, false);
                }
            }));

            match.getSpectators().forEach(uuid -> {
                Player spectator = Bukkit.getPlayer(uuid);
                if (spectator != null) {
                    Practice.getInstance().getLobbyManager().sendToLobby(spectator);
                    // TODO: Remove from spectators map once created
                }
            });

            match.getArena().setBeingUsed(false);

            cancel();
            return;
        }

        countdown--;
    }
}

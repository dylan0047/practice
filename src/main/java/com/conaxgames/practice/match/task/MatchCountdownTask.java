package com.conaxgames.practice.match.task;

import com.conaxgames.practice.Practice;
import com.conaxgames.practice.kit.KitMask;
import com.conaxgames.practice.match.Match;
import com.conaxgames.practice.match.MatchState;
import com.conaxgames.practice.util.PlayerUtil;
import com.conaxgames.util.finalutil.CC;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;

@RequiredArgsConstructor
public class MatchCountdownTask extends BukkitRunnable {

    private final Match match;

    private int countdown = 5;

    public void run() {
        if (match.getState() != MatchState.COUNTDOWN) {
            cancel();
            return;
        }

        if (countdown == 5) {
            Set<Player> matchPlayers = new HashSet<>();

            match.getTeams().forEach(team -> team.getLivingPlayerList().forEach(player -> {
                Location teleportLocation;
                if (match.isFFA()) {
                    teleportLocation = match.getArena().getSpawnA();
                } else {
                    teleportLocation = team == match.getTeams().get(0)
                            ? match.getArena().getSpawnA()
                            : match.getArena().getSpawnB();
                }

                player.teleport(teleportLocation);

                PlayerUtil.clearPlayer(player);
                Practice.getInstance().getCustomKitManager().giveBooksOrDefaultKit(player, match.getKit());
                Practice.getInstance().getDuelManager().removeRequests(player.getUniqueId());

                if (match.getKit().meetsMask(KitMask.HEARTS)) {
                    PlayerUtil.updateNametag(player, true);
                }

                matchPlayers.add(player);
            }));

            for (Player player1 : matchPlayers) {
                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    player1.hidePlayer(onlinePlayer);
                    onlinePlayer.hidePlayer(player1);
                }

                for (Player player2 : matchPlayers) {
                    player1.showPlayer(player2);
                    player2.showPlayer(player1);
                }
            }
        }

        if (countdown == 0) {
            match.broadcast(Sound.NOTE_PLING, 2F);
            match.startMatch();

            cancel();
            return;
        } else {
            match.broadcast(Sound.NOTE_PLING, 1F);
        }

        String seconds = countdown > 1 ? "seconds" : "second";
        match.broadcast(CC.GREEN + "The match will start in " + countdown + " " + seconds + "...");
        countdown--;
    }
}

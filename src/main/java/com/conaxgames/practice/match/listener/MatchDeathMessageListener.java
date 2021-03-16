package com.conaxgames.practice.match.listener;

import com.conaxgames.CorePlugin;
import com.conaxgames.coins.item.ItemType;
import com.conaxgames.coins.item.ShopItem;
import com.conaxgames.coins.item.impl.killmessages.KillMessage;
import com.conaxgames.mineman.Mineman;
import com.conaxgames.practice.match.Match;
import com.conaxgames.practice.match.MatchManager;
import com.conaxgames.util.finalutil.CC;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;

@RequiredArgsConstructor
public class MatchDeathMessageListener implements Listener {

    private final MatchManager matchManager;

    @EventHandler(priority = EventPriority.LOWEST) // run this first before they respawn so killer isnt null
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        if (!matchManager.inMatch(player.getUniqueId())) {
            return;
        }

        Match match = matchManager.getMatch(player.getUniqueId());
        sendDeathMessage(player, match);

        event.setDeathMessage(null);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (!matchManager.inMatch(player.getUniqueId())) {
            return;
        }

        Match match = matchManager.getMatch(player.getUniqueId());
        sendDeathMessage(player, match);
    }

    private void sendDeathMessage(Player player, Match match) {
        Player killer = player.getKiller();

        // TODO: If a party match, the teams should be red/green depending on the team index
        ChatColor playerTeamColor = ChatColor.RED;
        ChatColor killerTeamColor = ChatColor.GREEN;
        String deathMessage = playerTeamColor + player.getName() + CC.GOLD + " was ";

        if (killer != null) {
            Mineman killerMineman = CorePlugin.getInstance().getPlayerManager().getPlayer(killer.getUniqueId());
            ShopItem killMessage = killerMineman.getActiveShopItemByType(ItemType.KILL_MESSAGE);

            if (killMessage != null) {
                deathMessage += ((KillMessage) killMessage).getRandomMessage()
                        + " by " + killerTeamColor + killer.getName()
                        + CC.GOLD + ".";
            } else {
                deathMessage += "killed by "
                        + killerTeamColor + killer.getName()
                        + CC.GOLD + ".";
            }
        } else {
            deathMessage += "killed.";
        }

        match.broadcast(deathMessage);
    }

}

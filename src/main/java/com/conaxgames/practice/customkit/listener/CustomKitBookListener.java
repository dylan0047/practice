package com.conaxgames.practice.customkit.listener;

import com.conaxgames.practice.Practice;
import com.conaxgames.practice.customkit.CustomKitManager;
import com.conaxgames.practice.kit.KitItems;
import com.conaxgames.practice.match.Match;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Map;

@RequiredArgsConstructor
public class CustomKitBookListener implements Listener {

    private final CustomKitManager customKitManager;

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (event.getAction() != Action.RIGHT_CLICK_AIR
                && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        if (!Practice.getInstance().getMatchManager().inMatch(player.getUniqueId())) {
            return;
        }

        if (event.getItem().getType() == Material.ENCHANTED_BOOK) {
            Match match = Practice.getInstance().getMatchManager().getMatch(player.getUniqueId());
            int kitIndex = player.getInventory().getHeldItemSlot();
            if (kitIndex == 8) {
                match.getKit().apply(player);
            } else {
                KitItems customKit = customKitManager.getKit(player, match.getKit(), kitIndex + 1);
                if (customKit != null) {
                    customKit.apply(player);
                }
            }
        }
    }

}

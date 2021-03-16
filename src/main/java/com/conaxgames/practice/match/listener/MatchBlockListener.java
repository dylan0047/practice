package com.conaxgames.practice.match.listener;

import com.conaxgames.practice.kit.KitMask;
import com.conaxgames.practice.match.Match;
import com.conaxgames.practice.match.MatchManager;
import com.conaxgames.practice.match.MatchState;
import com.conaxgames.practice.match.event.MatchEndEvent;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

@RequiredArgsConstructor
public class MatchBlockListener implements Listener {

    private final MatchManager matchManager;

    @EventHandler
    public void onLeavesDecay(LeavesDecayEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockFade(BlockFadeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockBurn(BlockBurnEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockSpread(BlockSpreadEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onBucketEmpty(PlayerBucketEmptyEvent event) {
        Player player = event.getPlayer();
        if (!matchManager.inMatch(player.getUniqueId())) {
            return;
        }

        Match match = matchManager.getMatch(player.getUniqueId());
        if (!match.getKit().meetsMask(KitMask.ALLOW_BUILDING)) {
            event.setCancelled(true);
            return;
        }

        if (match.getState() != MatchState.IN_PROGRESS) {
            event.setCancelled(true);
            return;
        }

        match.getPlacedBlocks().add(event.getBlockClicked().getRelative(event.getBlockFace()).getLocation());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (!matchManager.inMatch(player.getUniqueId())) {
            return;
        }

        Match match = matchManager.getMatch(player.getUniqueId());
        if (!match.getKit().meetsMask(KitMask.ALLOW_BUILDING)) {
            event.setCancelled(true);
            return;
        }

        if (match.getState() != MatchState.IN_PROGRESS) {
            event.setCancelled(true);
            return;
        }

        match.getPlacedBlocks().add(event.getBlockPlaced().getLocation());
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (!matchManager.inMatch(player.getUniqueId())) {
            return;
        }

        Match match = matchManager.getMatch(player.getUniqueId());
        if (!match.getKit().meetsMask(KitMask.ALLOW_BUILDING)) {
            event.setCancelled(true);
            return;
        }

        if (match.getState() != MatchState.IN_PROGRESS) {
            event.setCancelled(true);
            return;
        }

        if (!match.getPlacedBlocks().contains(event.getBlock().getLocation())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onMatchEnd(MatchEndEvent event) {
        Match match = event.getMatch();

        if (!match.getKit().meetsMask(KitMask.ALLOW_BUILDING)) {
            return;
        }

        World world = match.getArena().getSpawnA().getWorld();

        match.getPlacedBlocks().forEach(location -> world.getBlockAt(location).setType(Material.AIR));
        match.getBlockChanges().forEach(blockState -> {
            blockState.getLocation().getBlock().setType(blockState.getType());
            blockState.update(true, false);
        });

        match.getPlacedBlocks().clear();
        match.getBlockChanges().clear();
    }

    @EventHandler
    public void onWeatherChange(WeatherChangeEvent event) {
        if (event.toWeatherState()) {
            event.setCancelled(true);
        }
    }

}

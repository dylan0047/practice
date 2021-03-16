package com.conaxgames.practice.util;

import com.conaxgames.CorePlugin;
import com.conaxgames.board.Board;
import com.conaxgames.practice.Practice;
import com.conaxgames.spigot.knockback.KnockbackModule;
import lombok.experimental.UtilityClass;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

/**
 * This class contains various utility methods
 * that can be used on players.
 */
@UtilityClass
public class PlayerUtil {

    /**
     * Resets a player's inventory and resets other
     * data like health, knockback profile, etc.
     *
     * @param player the player to clean up
     */
    public static void clearPlayer(Player player) {
        player.setHealth(player.getMaxHealth());
        player.setFoodLevel(20);
        player.setSaturation(12.8F);
        player.setFireTicks(0);
        player.setFallDistance(0F);
        player.setLevel(0);
        player.setExp(0F);
        player.setAllowFlight(false);
        player.setFlying(false);

        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        player.updateInventory();

        player.setGameMode(GameMode.SURVIVAL);

        player.getActivePotionEffects().stream().map(PotionEffect::getType).forEach(player::removePotionEffect);

        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
        entityPlayer.setKnockback(KnockbackModule.getDefault());
    }

    /**
     * Updates a players' scoreboard teams.
     *
     * @param player
     * @param hearts
     */
    public static void updateNametag(Player player, boolean hearts) {
        Bukkit.getScheduler().runTaskAsynchronously(Practice.getInstance(), () -> {
            Scoreboard scoreboard;
            if (CorePlugin.getInstance().getBoardManager() != null) {
                scoreboard = CorePlugin.getInstance().getBoardManager().getPlayerBoards()
                        .get(player.getUniqueId()).getScoreboard();
            } else {
                scoreboard = player.getScoreboard();
                if (scoreboard == null) {
                    scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
                    player.setScoreboard(scoreboard);
                }
            }

            if (hearts) {
                Objective objective = scoreboard.getObjective("showhealth");
                if (objective == null) {
                    objective = scoreboard.registerNewObjective("showhealth", "health");
                    objective.setDisplaySlot(DisplaySlot.BELOW_NAME);
                    objective.setDisplayName(ChatColor.DARK_RED + "\u2764");
                }
            } else {
                Objective objective = scoreboard.getObjective(DisplaySlot.BELOW_NAME);
                if (objective != null) {
                    objective.unregister();
                }
            }
        });
    }
}

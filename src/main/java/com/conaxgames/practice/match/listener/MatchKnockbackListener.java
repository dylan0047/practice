package com.conaxgames.practice.match.listener;

import com.conaxgames.practice.match.Match;
import com.conaxgames.practice.match.event.MatchStartEvent;
import com.conaxgames.spigot.knockback.KnockbackModule;
import com.conaxgames.spigot.knockback.KnockbackProfile;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class MatchKnockbackListener implements Listener {

    @EventHandler
    public void onMatchStart(MatchStartEvent event) {
        Match match = event.getMatch();
        KnockbackProfile knockbackProfile = KnockbackModule.INSTANCE.profiles.get(match.getKit().getId());

        if (knockbackProfile == null) {
            return;
        }

        match.getTeams().forEach(team -> team.getLivingPlayerList().forEach(player -> {
            EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
            entityPlayer.setKnockback(knockbackProfile);
        }));
    }
}

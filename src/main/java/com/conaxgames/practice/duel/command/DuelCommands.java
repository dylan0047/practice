package com.conaxgames.practice.duel.command;

import com.conaxgames.clickable.Clickable;
import com.conaxgames.practice.Practice;
import com.conaxgames.practice.duel.DuelManager;
import com.conaxgames.practice.duel.MatchRequest;
import com.conaxgames.practice.kit.Kit;
import com.conaxgames.practice.kit.inventory.KitSelectionInventory;
import com.conaxgames.util.cmd.CommandHandler;
import com.conaxgames.util.cmd.annotation.Param;
import com.conaxgames.util.cmd.annotation.commandTypes.Command;
import com.conaxgames.util.finalutil.CC;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class DuelCommands implements CommandHandler {

    private final DuelManager duelManager;

    @Command(name = "duel")
    public void duel(Player player, @Param(name = "target") Player target) {
        if (!Practice.getInstance().getLobbyManager().inLobby(player)) {
            player.sendMessage(CC.RED + "You must be in the lobby.");
            return;
        }

        if (player == target) {
            player.sendMessage(CC.RED + "You can't duel yourself.");
            return;
        }

        if (!Practice.getInstance().getLobbyManager().inLobby(target)) {
            player.sendMessage(CC.RED + target.getName() + " isn't in spawn.");
            return;
        }

        new KitSelectionInventory(player, false, kit -> {
            duelManager.createRequest(player.getUniqueId(), target.getUniqueId(), kit);

            Clickable clickable = new Clickable(CC.YELLOW + player.getName() + " has requested a duel with kit "
                    + CC.YELLOW + kit.getDisplayName(false) + CC.GOLD + ". " + CC.GREEN + "[Click to accept]",
                    null,
                    "/accept " + player.getName() + " " + kit.getId());
            clickable.sendToPlayer(target);

            player.sendMessage(CC.GREEN + "Sent a duel request to " + target.getName() + " on kit " + kit.getDisplayName(false) + ".");
        }).show();
    }

    @Command(name = "accept")
    public void accept(Player player, @Param(name = "target") Player target, @Param(name = "kit") Kit kit) {
        if (kit == null) {
            player.sendMessage(CC.RED + "Invalid kit.");
            return;
        }

        if (!Practice.getInstance().getLobbyManager().inLobby(player)) {
            player.sendMessage(CC.RED + "You must be in the lobby.");
            return;
        }

        if (player == target) {
            player.sendMessage(CC.RED + "You can't duel yourself.");
            return;
        }

        if (!Practice.getInstance().getLobbyManager().inLobby(target)) {
            player.sendMessage(CC.RED + target.getName() + " isn't in spawn.");
            return;
        }

        MatchRequest request = duelManager.getRequest(target.getUniqueId(), player.getUniqueId(), kit);
        if (request == null) {
            player.sendMessage(CC.RED + "You don't have a duel request from that player.");
            return;
        }

        request.accept();
    }

}

package com.conaxgames.practice.customkit.command;

import com.conaxgames.practice.Practice;
import com.conaxgames.rank.Rank;
import com.conaxgames.util.cmd.CommandHandler;
import com.conaxgames.util.cmd.annotation.commandTypes.Command;
import com.conaxgames.util.finalutil.CC;
import org.bukkit.entity.Player;

public class EditorLocationCommand implements CommandHandler {

    @Command(name = "editorlocation", rank = Rank.MANAGER)
    public void editorLocation(Player player) {
        Practice.getInstance().getCustomKitManager().setKitEditorLocation(player.getLocation());
        player.sendMessage(CC.GREEN + "Set the editor location to where you're standing.");
    }

}

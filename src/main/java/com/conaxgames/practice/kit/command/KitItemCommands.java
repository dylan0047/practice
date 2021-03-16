package com.conaxgames.practice.kit.command;

import com.conaxgames.practice.Practice;
import com.conaxgames.practice.kit.Kit;
import com.conaxgames.practice.kit.KitItems;
import com.conaxgames.util.TaskUtil;
import com.conaxgames.util.cmd.CommandHandler;
import com.conaxgames.util.cmd.annotation.Param;
import com.conaxgames.util.cmd.annotation.commandTypes.SubCommand;
import com.conaxgames.util.finalutil.CC;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Commands that deal with a kit's items, i.e.
 * items, kit editor, etc.
 */
public class KitItemCommands implements CommandHandler {

    @SubCommand(baseCommand = "kit", name = "defaultinv")
    public void kitDefaultInv(Player player, @Param(name = "kit") Kit kit) {
        if (kit == null) {
            player.sendMessage(CC.RED + "No kit found.");
            return;
        }

        KitItems items = kit.getDefaultKitItems();
        if (items == null) {
            items = new KitItems();
        }

        items.setArmor(player.getInventory().getArmorContents());
        items.setItems(player.getInventory().getContents());

        kit.setDefaultKitItems(items);
        TaskUtil.runAsync(() -> Practice.getInstance().getKitManager().saveKit(kit));
        player.sendMessage(CC.GREEN + "You have set the default inventory for the " + kit.getId() + " kit.");
    }

    @SubCommand(baseCommand = "kit", name = "editinv")
    public void kitEditInv(Player player, @Param(name = "kit") Kit kit) {
        if (kit == null) {
            player.sendMessage(CC.RED + "No kit found.");
            return;
        }

        KitItems items = kit.getDefaultKitItems();
        if (items == null) {
            items = new KitItems();
        }

        items.setKitEditorItems(player.getInventory().getContents());

        kit.setDefaultKitItems(items);
        TaskUtil.runAsync(() -> Practice.getInstance().getKitManager().saveKit(kit));
        player.sendMessage(CC.GREEN + "You have set the kit editor inventory for the " + kit.getId() + " kit.");
    }

    @SubCommand(baseCommand = "kit", name = "icon")
    public void kitIcon(Player player, @Param(name = "kit") Kit kit) {
        if (kit == null) {
            player.sendMessage(CC.RED + "No kit found.");
            return;
        }

        ItemStack hand = player.getItemInHand();
        if (hand.getType() == Material.AIR) {
            kit.setDisplayItem(null);
            player.sendMessage(CC.GREEN + "You have removed the icon for the " + kit.getId() + " kit.");
            return;
        }

        kit.setDisplayItem(hand);
        TaskUtil.runAsync(() -> Practice.getInstance().getKitManager().saveKit(kit));
        player.sendMessage(CC.GREEN + "You have set the icon for the " + kit.getId() + " kit.");
    }

    @SubCommand(baseCommand = "kit", name = "apply")
    public void kitApply(Player player, @Param(name = "kit") Kit kit) {
        if (kit == null) {
            player.sendMessage(CC.RED + "No kit found.");
            return;
        }

        kit.apply(player);
    }
}

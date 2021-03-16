package com.conaxgames.practice.customkit.listener;

import com.conaxgames.practice.Practice;
import com.conaxgames.practice.customkit.CustomKitManager;
import com.conaxgames.practice.customkit.inventory.KitEditorInventory;
import com.conaxgames.practice.kit.Kit;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;

@RequiredArgsConstructor
public class KitEditorGeneralListener implements Listener {

    private final CustomKitManager customKitManager;

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        if (!Practice.getInstance().getLobbyManager().inLobby(player)) {
            return;
        }

        if (!customKitManager.getEditingKitMap().containsKey(player.getUniqueId())) {
            return;
        }

        event.setCancelled(true);

        Kit kit = customKitManager.getEditingKitMap().get(player.getUniqueId());
        switch (event.getClickedBlock().getType()) {
            case WALL_SIGN:
            case SIGN:
            case SIGN_POST:
                Practice.getInstance().getLobbyManager().sendToLobby(player);
                break;

            case CHEST:
                if (kit.getDefaultKitItems() != null) {
                    Inventory editorInventory = Bukkit.createInventory(null, 36);
                    editorInventory.setContents(kit.getDefaultKitItems().getItems());
                    player.openInventory(editorInventory);
                }

                break;

            case ANVIL:
                new KitEditorInventory(player, kit).show();
                break;
        }
    }

}

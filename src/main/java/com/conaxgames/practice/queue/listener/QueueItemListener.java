package com.conaxgames.practice.queue.listener;

import com.conaxgames.practice.Practice;
import com.conaxgames.practice.kit.inventory.KitSelectionInventory;
import com.conaxgames.practice.queue.QueueItems;
import com.conaxgames.practice.queue.QueueManager;
import com.conaxgames.practice.util.item.ItemListener;
import com.conaxgames.util.finalutil.CC;
import org.bukkit.entity.Player;

import java.util.function.Consumer;

public class QueueItemListener extends ItemListener {

    private QueueManager queueManager;

    public QueueItemListener(QueueManager queueManager) {
        this.queueManager = queueManager;

        addHandler(QueueItems.JOIN_UNRANKED_QUEUE, onQueueJoin(false));
        addHandler(QueueItems.JOIN_RANKED_QUEUE, onQueueJoin(true));

        addHandler(QueueItems.LEAVE_QUEUE, onQueueLeave());
    }

    private Consumer<Player> onQueueJoin(boolean isRanked) {
        return player -> {
            // TODO: Check if player is eligible to play ranked.
            new KitSelectionInventory(player, isRanked, kit -> {
                if (queueManager.addToQueue(player, kit, isRanked)) {
                    player.sendMessage(CC.GREEN + "You have been added to the "
                            + (isRanked ? "Ranked" : "Unranked") + " "
                            + kit.getDisplayName(false) + CC.GREEN + " queue.");

                    Practice.getInstance().getLobbyManager().giveLobbyInventory(player);
                }
            }).show();
        };
    }

    private Consumer<Player> onQueueLeave() {
        return player -> {
            if (queueManager.removeFromQueue(player)) {
                player.sendMessage(CC.GREEN + "You have been removed from the queue.");
            }
        };
    }
}

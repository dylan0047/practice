package com.conaxgames.practice.queue;

import com.conaxgames.practice.Practice;
import com.conaxgames.practice.kit.Kit;
import com.conaxgames.practice.match.Match;
import com.conaxgames.practice.match.MatchTeam;
import com.conaxgames.util.finalutil.CC;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class KitQueue {

    /**
     * The designated {@link Kit} this queue is for.
     */
    private final Kit kit;

    /**
     * Whether or not this queue is a ranked queue.
     */
    private final boolean isRanked;

    /**
     * Entries of players currently queuing for the
     * specified kit
     */
    private final List<QueueEntry> queueEntries = new ArrayList<>();

    /**
     * Handles queue logic, mainly creating matches.
     */
    public void tick() {
        List<QueueEntry> queueEntriesCopy = new ArrayList<>(queueEntries);

        // TODO: Check elo if ranked queue

        while (queueEntriesCopy.size() >= 2 && queueEntriesCopy.size() % 2 == 0) {
            QueueEntry entry1 = queueEntriesCopy.remove(0);
            QueueEntry entry2 = queueEntriesCopy.remove(0);

            List<Player> participants = new ArrayList<>();
            participants.addAll(entry1.getPlayers());
            participants.addAll(entry2.getPlayers());

            Match match = Practice.getInstance().getMatchManager().createMatch(kit, isRanked,
                    new MatchTeam(entry1.getMembers()), new MatchTeam(entry2.getMembers()));
            participants.forEach(player -> {
                if (match == null) {
                    player.sendMessage(CC.RED + "Failed to create the match.");
                } else {
                    String opponents = entry1.getMembers().contains(player.getUniqueId())
                            ? entry2.getMemberNames() : entry1.getMemberNames();
                    player.sendMessage(CC.GOLD + "Match found! Opponent: " + CC.YELLOW + opponents);
                }

                Practice.getInstance().getQueueManager().removeFromQueue(player);
            });
        }
    }

    /**
     * Adds an entry to the queue.
     *
     * @param entry the entry to add
     */
    public void addEntry(QueueEntry entry) {
        queueEntries.add(entry);
    }

    /**
     * Removes an entry from the queue.
     *
     * @param entry the entry to remove
     */
    public void removeEntry(QueueEntry entry) {
        queueEntries.remove(entry);
    }
}

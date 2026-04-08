package com.jyrion.server.service;

import com.jyrion.server.event.DomainEvent;
import com.jyrion.server.model.*;
import com.jyrion.server.model.command.PlayerCommand;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class PlayerRegistry {

    private final ConcurrentHashMap<String, Player> players = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, PlayerStatus> statuses = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, List<Track>> queues = new ConcurrentHashMap<>();

    private final SseBroadcaster broadcaster;

    public PlayerRegistry(SseBroadcaster broadcaster) {
        this.broadcaster = broadcaster;
    }

    // ── Registration ───────────────────────────────────────────────────────────

    public void register(Player player, List<Track> initialQueue) {
        players.put(player.id(), player);
        queues.put(player.id(), new CopyOnWriteArrayList<>(initialQueue));
        Track nowPlaying = initialQueue.isEmpty() ? null : initialQueue.get(0);
        statuses.put(player.id(), new PlayerStatus(
                player.id(), PlaybackState.STOPPED, 50, false,
                nowPlaying, 0, System.currentTimeMillis()));
    }

    // ── Queries ────────────────────────────────────────────────────────────────

    public List<Player> getPlayers() {
        return players.values().stream()
                .sorted(java.util.Comparator.comparing(Player::id))
                .toList();
    }

    public Optional<Player> getPlayer(String id) {
        return Optional.ofNullable(players.get(id));
    }

    public Optional<PlayerStatus> getStatus(String id) {
        return Optional.ofNullable(statuses.get(id));
    }

    public Optional<QueuePage> getQueue(String id, int offset, int limit) {
        if (!players.containsKey(id)) return Optional.empty();
        List<Track> queue = queues.getOrDefault(id, List.of());
        int total = queue.size();
        int from = Math.min(offset, total);
        int to = Math.min(from + limit, total);
        List<Track> page = queue.subList(from, to);
        return Optional.of(new QueuePage(id, offset, limit, total, List.copyOf(page)));
    }

    // ── Commands ───────────────────────────────────────────────────────────────

    public boolean dispatch(String playerId, PlayerCommand command) {
        if (!players.containsKey(playerId)) return false;
        PlayerStatus current = statuses.get(playerId);

        PlayerStatus updated = switch (command) {
            case PlayerCommand.Play ignored -> withState(current, PlaybackState.PLAYING);
            case PlayerCommand.Pause ignored -> withState(current, PlaybackState.PAUSED);
            case PlayerCommand.TogglePause ignored -> withState(current,
                    current.state() == PlaybackState.PLAYING ? PlaybackState.PAUSED : PlaybackState.PLAYING);
            case PlayerCommand.Stop ignored -> new PlayerStatus(
                    current.playerId(), PlaybackState.STOPPED, current.volume(), current.muted(),
                    current.nowPlaying(), 0, System.currentTimeMillis());
            case PlayerCommand.SetVolume sv -> new PlayerStatus(
                    current.playerId(), current.state(), clamp(sv.volume(), 0, 100),
                    current.muted(), current.nowPlaying(), current.positionMs(), System.currentTimeMillis());
            case PlayerCommand.Mute m -> new PlayerStatus(
                    current.playerId(), current.state(), current.volume(), m.muted(),
                    current.nowPlaying(), current.positionMs(), System.currentTimeMillis());
            case PlayerCommand.Seek s -> new PlayerStatus(
                    current.playerId(), current.state(), current.volume(), current.muted(),
                    current.nowPlaying(), s.positionMs(), System.currentTimeMillis());
            case PlayerCommand.QueueClear ignored -> {
                queues.put(playerId, new CopyOnWriteArrayList<>());
                broadcaster.publish(new DomainEvent("player.queueChanged", playerId, Map.of("playerId", playerId)));
                yield new PlayerStatus(current.playerId(), PlaybackState.STOPPED, current.volume(), current.muted(),
                        null, 0, System.currentTimeMillis());
            }
            case PlayerCommand.QueueJump qj -> {
                List<Track> queue = queues.getOrDefault(playerId, List.of());
                Track track = (qj.index() >= 0 && qj.index() < queue.size()) ? queue.get(qj.index()) : current.nowPlaying();
                yield new PlayerStatus(current.playerId(), PlaybackState.PLAYING, current.volume(), current.muted(),
                        track, 0, System.currentTimeMillis());
            }
            case PlayerCommand.QueueAdd qa -> {
                // No-op in demo: uri not resolved
                broadcaster.publish(new DomainEvent("player.queueChanged", playerId, Map.of("playerId", playerId)));
                yield current;
            }
        };

        statuses.put(playerId, updated);
        broadcaster.publish(new DomainEvent("player.statusChanged", playerId, updated));
        return true;
    }

    // ── Helpers ────────────────────────────────────────────────────────────────

    private PlayerStatus withState(PlayerStatus s, PlaybackState state) {
        return new PlayerStatus(s.playerId(), state, s.volume(), s.muted(),
                s.nowPlaying(), s.positionMs(), System.currentTimeMillis());
    }

    private int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }
}

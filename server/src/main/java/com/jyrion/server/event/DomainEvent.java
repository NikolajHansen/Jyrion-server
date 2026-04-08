package com.jyrion.server.event;

/**
 * A generic domain event emitted when player state changes.
 *
 * @param eventType  one of: player.statusChanged, player.queueChanged, players.changed
 * @param playerId   the affected player id (may be null for players.changed)
 * @param payload    the serializable event data
 */
public record DomainEvent(String eventType, String playerId, Object payload) {}

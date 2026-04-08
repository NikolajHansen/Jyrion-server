package com.jyrion.server.model;

import java.util.List;

public record QueuePage(
        String playerId,
        int offset,
        int limit,
        int total,
        List<Track> items
) {}

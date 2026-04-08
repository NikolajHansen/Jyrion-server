package com.jyrion.server.model;

import com.fasterxml.jackson.annotation.JsonInclude;

public record PlayerStatus(
        String playerId,
        PlaybackState state,
        int volume,
        boolean muted,
        @JsonInclude(JsonInclude.Include.NON_NULL) Track nowPlaying,
        long positionMs,
        long timestamp
) {}

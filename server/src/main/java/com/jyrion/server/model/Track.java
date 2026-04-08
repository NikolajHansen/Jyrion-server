package com.jyrion.server.model;

import com.fasterxml.jackson.annotation.JsonInclude;

public record Track(
        String id,
        String title,
        String artist,
        String album,
        long durationMs,
        @JsonInclude(JsonInclude.Include.NON_NULL) String artUrl
) {}

package com.jyrion.server.controller;

import com.jyrion.server.service.SseBroadcaster;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/events")
public class EventController {

    private final SseBroadcaster broadcaster;

    public EventController(SseBroadcaster broadcaster) {
        this.broadcaster = broadcaster;
    }

    @GetMapping(produces = "text/event-stream")
    public SseEmitter subscribe() {
        return broadcaster.subscribe();
    }
}

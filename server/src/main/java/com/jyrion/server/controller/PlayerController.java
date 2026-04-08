package com.jyrion.server.controller;

import com.jyrion.server.model.Player;
import com.jyrion.server.model.PlayerStatus;
import com.jyrion.server.model.QueuePage;
import com.jyrion.server.model.command.PlayerCommand;
import com.jyrion.server.service.PlayerRegistry;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/players")
public class PlayerController {

    private final PlayerRegistry registry;

    public PlayerController(PlayerRegistry registry) {
        this.registry = registry;
    }

    @GetMapping
    public List<Player> getPlayers() {
        return registry.getPlayers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Player> getPlayer(@PathVariable String id) {
        return registry.getPlayer(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/status")
    public ResponseEntity<PlayerStatus> getStatus(@PathVariable String id) {
        return registry.getStatus(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/queue")
    public ResponseEntity<QueuePage> getQueue(
            @PathVariable String id,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "100") int limit) {
        return registry.getQueue(id, offset, limit)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/commands")
    public ResponseEntity<Void> sendCommand(
            @PathVariable String id,
            @RequestBody PlayerCommand command) {
        boolean found = registry.dispatch(id, command);
        return found ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}

package com.jyrion.server;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class PlayerApiTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getPlayersReturnsSeededPlayers() throws Exception {
        mockMvc.perform(get("/api/players"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("player-1"))
                .andExpect(jsonPath("$[0].name").value("Living Room"))
                .andExpect(jsonPath("$[1].id").value("player-2"))
                .andExpect(jsonPath("$[1].name").value("Kitchen"));
    }

    @Test
    void getUnknownPlayerReturns404() throws Exception {
        mockMvc.perform(get("/api/players/does-not-exist"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getPlayerStatusReturns200() throws Exception {
        mockMvc.perform(get("/api/players/player-1/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.playerId").value("player-1"))
                .andExpect(jsonPath("$.state").value("STOPPED"))
                .andExpect(jsonPath("$.volume").value(50));
    }

    @Test
    void getUnknownPlayerStatusReturns404() throws Exception {
        mockMvc.perform(get("/api/players/does-not-exist/status"))
                .andExpect(status().isNotFound());
    }

    @Test
    void sendCommandReturns204() throws Exception {
        mockMvc.perform(post("/api/players/player-1/commands")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"type\":\"play\"}"))
                .andExpect(status().isNoContent());
    }

    @Test
    void sendCommandToUnknownPlayerReturns404() throws Exception {
        mockMvc.perform(post("/api/players/does-not-exist/commands")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"type\":\"play\"}"))
                .andExpect(status().isNotFound());
    }
}

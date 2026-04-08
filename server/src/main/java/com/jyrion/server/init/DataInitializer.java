package com.jyrion.server.init;

import com.jyrion.server.model.Player;
import com.jyrion.server.model.Track;
import com.jyrion.server.service.PlayerRegistry;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataInitializer implements ApplicationRunner {

    private final PlayerRegistry registry;

    public DataInitializer(PlayerRegistry registry) {
        this.registry = registry;
    }

    @Override
    public void run(ApplicationArguments args) {
        registry.register(
                new Player("player-1", "Living Room"),
                List.of(
                        new Track("t1", "Bohemian Rhapsody", "Queen", "A Night at the Opera", 354000, null),
                        new Track("t2", "Hotel California", "Eagles", "Hotel California", 391000, null),
                        new Track("t3", "Stairway to Heaven", "Led Zeppelin", "Led Zeppelin IV", 482000, null)
                )
        );

        registry.register(
                new Player("player-2", "Kitchen"),
                List.of(
                        new Track("t4", "Shape of You", "Ed Sheeran", "÷", 234000, null),
                        new Track("t5", "Blinding Lights", "The Weeknd", "After Hours", 200000, null)
                )
        );
    }
}

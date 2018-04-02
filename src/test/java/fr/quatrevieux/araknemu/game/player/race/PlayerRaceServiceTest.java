package fr.quatrevieux.araknemu.game.player.race;

import fr.quatrevieux.araknemu.data.constant.Race;
import fr.quatrevieux.araknemu.data.world.repository.character.PlayerRaceRepository;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.spell.SpellService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;

import static org.junit.jupiter.api.Assertions.*;

class PlayerRaceServiceTest extends GameBaseCase {
    private PlayerRaceService service;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet
            .pushSpells()
            .pushRaces()
        ;

        service = new PlayerRaceService(
            container.get(PlayerRaceRepository.class),
            container.get(SpellService.class)
        );
    }

    @Test
    void get() {
        GamePlayerRace race = service.get(Race.FECA);

        assertEquals(Race.FECA, race.race());
        assertEquals("Feca", race.name());
        assertCount(3, race.spells());
    }

    @Test
    void preload() {
        Logger logger = Mockito.mock(Logger.class);

        service.preload(logger);

        Mockito.verify(logger).info("Loading races...");
        Mockito.verify(logger).info("{} races loaded", 12);
    }
}
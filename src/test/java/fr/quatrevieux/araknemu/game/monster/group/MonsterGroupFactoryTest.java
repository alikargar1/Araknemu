package fr.quatrevieux.araknemu.game.monster.group;

import fr.quatrevieux.araknemu.data.value.Interval;
import fr.quatrevieux.araknemu.data.value.Position;
import fr.quatrevieux.araknemu.data.world.entity.monster.MonsterGroupData;
import fr.quatrevieux.araknemu.data.world.entity.monster.MonsterGroupPosition;
import fr.quatrevieux.araknemu.data.world.repository.monster.MonsterGroupDataRepository;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.fight.FightService;
import fr.quatrevieux.araknemu.game.monster.MonsterService;
import fr.quatrevieux.araknemu.game.monster.environment.FixedCellSelector;
import fr.quatrevieux.araknemu.game.monster.environment.LivingMonsterGroupPosition;
import fr.quatrevieux.araknemu.game.monster.environment.RandomCellSelector;
import fr.quatrevieux.araknemu.game.monster.group.generator.RandomMonsterListGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class MonsterGroupFactoryTest extends GameBaseCase {
    private MonsterGroupFactory factory;
    private MonsterGroupDataRepository repository;
    private LivingMonsterGroupPosition living;
    private ExplorationMap map;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet
            .pushMaps()
            .pushMonsterTemplates()
            .pushMonsterSpells()
            .pushMonsterGroups()
        ;

        factory = new MonsterGroupFactory(
            new RandomMonsterListGenerator(
                container.get(MonsterService.class)
            )
        );

        living = new LivingMonsterGroupPosition(
            container.get(MonsterGroupFactory.class),
            container.get(FightService.class),
            new MonsterGroupData(3, 60000, 4, 3, Arrays.asList(new MonsterGroupData.Monster(31, new Interval(1, 100)), new MonsterGroupData.Monster(34, new Interval(1, 100)), new MonsterGroupData.Monster(36, new Interval(1, 100))), ""),
            new FixedCellSelector(new Position(10340, 123))
        );
        living.populate(map = container.get(ExplorationMapService.class).load(10340));

        repository = container.get(MonsterGroupDataRepository.class);
    }

    @Test
    void create() {
        MonsterGroup group = factory.create(repository.get(1), living);

        assertEquals(-103, group.id());
        assertEquals(map.get(123), group.cell());
        assertBetween(1, 4, group.monsters().size());
    }

    @Test
    void createIdIncrement() {
        assertEquals(-103, factory.create(repository.get(1), living).id());
        assertEquals(-203, factory.create(repository.get(1), living).id());
        assertEquals(-303, factory.create(repository.get(1), living).id());
        assertEquals(-403, factory.create(repository.get(1), living).id());
    }
}

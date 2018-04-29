package fr.quatrevieux.araknemu.game.listener.fight.turn.action;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.turn.FightTurn;
import fr.quatrevieux.araknemu.game.fight.turn.action.ActionResult;
import fr.quatrevieux.araknemu.game.fight.turn.action.event.FightActionStarted;
import fr.quatrevieux.araknemu.game.fight.turn.action.move.Move;
import fr.quatrevieux.araknemu.game.world.map.Direction;
import fr.quatrevieux.araknemu.game.world.map.path.Decoder;
import fr.quatrevieux.araknemu.game.world.map.path.Path;
import fr.quatrevieux.araknemu.game.world.map.path.PathStep;
import fr.quatrevieux.araknemu.network.game.out.fight.action.FightAction;
import fr.quatrevieux.araknemu.network.game.out.fight.action.StartFightAction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class SendFightActionTest extends FightBaseCase {
    private Fight fight;
    private SendFightAction listener;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        listener = new SendFightAction(
            fight = createFight()
        );

        requestStack.clear();
    }

    @Test
    void onActionStarted() {
        Move move = new Move(
            new FightTurn(player.fighter(), fight, Duration.ofSeconds(30)),
            player.fighter(),
            new Path<>(
                new Decoder<>(fight.map()),
                Arrays.asList(
                    new PathStep<>(fight.map().get(185), Direction.EAST),
                    new PathStep<>(fight.map().get(199), Direction.SOUTH_WEST),
                    new PathStep<>(fight.map().get(213), Direction.SOUTH_WEST),
                    new PathStep<>(fight.map().get(198), Direction.NORTH_WEST)
                )
            )
        );

        ActionResult result = move.start();

        listener.on(new FightActionStarted(move, result));

        requestStack.assertAll(new StartFightAction(move), new FightAction(result));
    }
}
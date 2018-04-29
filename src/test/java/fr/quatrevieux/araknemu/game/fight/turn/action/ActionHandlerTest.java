package fr.quatrevieux.araknemu.game.fight.turn.action;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.turn.action.event.FightActionStarted;
import fr.quatrevieux.araknemu.game.fight.turn.action.event.FightActionTerminated;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.verification.VerificationMode;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class ActionHandlerTest extends FightBaseCase {
    private Fight fight;
    private ActionHandler actionHandler;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        fight = createFight();
        actionHandler = new ActionHandler(fight);
    }

    @Test
    void startInvalid() {
        assertFalse(actionHandler.start(new Action() {
            @Override
            public boolean validate() {
                return false;
            }

            @Override
            public ActionResult start() {
                return null;
            }

            @Override
            public Fighter performer() {
                return null;
            }

            @Override
            public ActionType type() {
                return null;
            }

            @Override
            public void end() {

            }

            @Override
            public Duration duration() {
                return null;
            }
        }));
    }

    @Test
    void startSuccessWillDispatchEvent() {
        Action action = Mockito.mock(Action.class);
        ActionResult result = Mockito.mock(ActionResult.class);

        Mockito.when(action.validate()).thenReturn(true);
        Mockito.when(action.start()).thenReturn(result);
        Mockito.when(action.duration()).thenReturn(Duration.ZERO);

        Mockito.when(result.success()).thenReturn(true);

        AtomicReference<FightActionStarted> ref = new AtomicReference<>();
        fight.dispatcher().add(FightActionStarted.class, ref::set);

        assertTrue(actionHandler.start(action));
        assertSame(action, ref.get().action());
        assertSame(result, ref.get().result());
    }

    @Test
    void startFailedWillDispatchEvent() {
        Action action = Mockito.mock(Action.class);
        ActionResult result = Mockito.mock(ActionResult.class);

        Mockito.when(action.validate()).thenReturn(true);
        Mockito.when(action.start()).thenReturn(result);

        Mockito.when(result.success()).thenReturn(false);

        AtomicReference<FightActionStarted> ref = new AtomicReference<>();
        fight.dispatcher().add(FightActionStarted.class, ref::set);

        assertTrue(actionHandler.start(action));
        assertSame(action, ref.get().action());
        assertSame(result, ref.get().result());

        Mockito.verify(action, Mockito.never()).duration();
    }

    @Test
    void startSuccessTerminateActionWhenDurationIsReached() throws InterruptedException {
        Action action = Mockito.mock(Action.class);
        ActionResult result = Mockito.mock(ActionResult.class);

        Mockito.when(action.validate()).thenReturn(true);
        Mockito.when(action.start()).thenReturn(result);
        Mockito.when(action.duration()).thenReturn(Duration.ofMillis(10));

        Mockito.when(result.success()).thenReturn(true);

        assertTrue(actionHandler.start(action));

        Mockito.verify(action, Mockito.never()).end();

        Thread.sleep(11);
        Mockito.verify(action, Mockito.times(1)).end();
    }

    @Test
    void startWithPendingAction() {
        Action action = Mockito.mock(Action.class);
        ActionResult result = Mockito.mock(ActionResult.class);

        Mockito.when(action.validate()).thenReturn(true);
        Mockito.when(action.start()).thenReturn(result);
        Mockito.when(action.duration()).thenReturn(Duration.ofMillis(10));

        Mockito.when(result.success()).thenReturn(true);

        assertTrue(actionHandler.start(action));

        Action other = Mockito.mock(Action.class);

        assertFalse(actionHandler.start(other));
        Mockito.verify(other, Mockito.never()).start();
    }

    @Test
    void terminateWithoutPendingAction() {
        AtomicReference<FightActionTerminated> ref = new AtomicReference<>();
        fight.dispatcher().add(FightActionTerminated.class, ref::set);

        actionHandler.terminate();

        assertNull(ref.get());
    }

    @Test
    void terminateSuccessWillDispatchEvent() {
        Action action = Mockito.mock(Action.class);
        ActionResult result = Mockito.mock(ActionResult.class);

        Mockito.when(action.validate()).thenReturn(true);
        Mockito.when(action.start()).thenReturn(result);
        Mockito.when(action.duration()).thenReturn(Duration.ofMillis(10));

        Mockito.when(result.success()).thenReturn(true);

        actionHandler.start(action);

        AtomicReference<FightActionTerminated> ref = new AtomicReference<>();
        fight.dispatcher().add(FightActionTerminated.class, ref::set);

        actionHandler.terminate();

        Mockito.verify(action).end();
        assertSame(action, ref.get().action());
    }

    @Test
    void terminateSuccessWillCancelTimer() throws InterruptedException {
        Action action = Mockito.mock(Action.class);
        ActionResult result = Mockito.mock(ActionResult.class);

        Mockito.when(action.validate()).thenReturn(true);
        Mockito.when(action.start()).thenReturn(result);
        Mockito.when(action.duration()).thenReturn(Duration.ofMillis(10));

        Mockito.when(result.success()).thenReturn(true);

        actionHandler.start(action);

        actionHandler.terminate();

        Thread.sleep(11);
        Mockito.verify(action, Mockito.times(1)).end();
    }

    @Test
    void terminateWithTerminationListener() {
        Action action = Mockito.mock(Action.class);
        ActionResult result = Mockito.mock(ActionResult.class);

        Mockito.when(action.validate()).thenReturn(true);
        Mockito.when(action.start()).thenReturn(result);
        Mockito.when(action.duration()).thenReturn(Duration.ofMillis(10));

        Mockito.when(result.success()).thenReturn(true);

        actionHandler.start(action);

        AtomicBoolean b = new AtomicBoolean();
        actionHandler.terminated(() -> b.set(true));
        assertFalse(b.get());

        actionHandler.terminate();

        assertTrue(b.get());
    }

    @Test
    void terminateWithTerminationListenerWillRemoveOldListeners() {
        Action action = Mockito.mock(Action.class);
        ActionResult result = Mockito.mock(ActionResult.class);

        Mockito.when(action.validate()).thenReturn(true);
        Mockito.when(action.start()).thenReturn(result);
        Mockito.when(action.duration()).thenReturn(Duration.ofMillis(10));

        Mockito.when(result.success()).thenReturn(true);

        actionHandler.start(action);

        AtomicInteger i = new AtomicInteger();
        actionHandler.terminated(() -> i.incrementAndGet());
        assertEquals(0, i.get());

        actionHandler.terminate();

        actionHandler.start(action);
        actionHandler.terminate();

        assertEquals(1, i.get());
    }

    @Test
    void terminatedWithoutPendingAction() {
        AtomicBoolean b = new AtomicBoolean();
        actionHandler.terminated(() -> b.set(true));
        assertTrue(b.get());
    }
}
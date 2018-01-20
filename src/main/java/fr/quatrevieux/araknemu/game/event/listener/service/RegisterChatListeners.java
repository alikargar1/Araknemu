package fr.quatrevieux.araknemu.game.event.listener.service;

import fr.quatrevieux.araknemu.game.event.Listener;
import fr.quatrevieux.araknemu.game.event.ListenerAggregate;
import fr.quatrevieux.araknemu.game.event.common.PlayerLoaded;
import fr.quatrevieux.araknemu.game.event.listener.player.chat.*;

/**
 * Register all chat listeners
 */
final public class RegisterChatListeners implements Listener<PlayerLoaded> {
    @Override
    public void on(PlayerLoaded event) {
        ListenerAggregate dispatcher = event.player().dispatcher();

        dispatcher.add(new InitializeChat(event.player()));
        dispatcher.add(new MessageReceived(event.player()));
        dispatcher.add(new PrivateMessageReceived(event.player()));
        dispatcher.add(new SubscriptionAddedAcknowledge(event.player()));
        dispatcher.add(new SubscriptionRemovedAcknowledge(event.player()));
    }

    @Override
    public Class<PlayerLoaded> event() {
        return PlayerLoaded.class;
    }
}

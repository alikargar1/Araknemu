package fr.quatrevieux.araknemu.game.account.bank;

import fr.quatrevieux.araknemu.core.event.DefaultListenerAggregate;
import fr.quatrevieux.araknemu.core.event.Dispatcher;
import fr.quatrevieux.araknemu.core.event.ListenerAggregate;
import fr.quatrevieux.araknemu.data.living.entity.account.AccountBank;
import fr.quatrevieux.araknemu.data.living.entity.account.BankItem;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.exchange.BankExchangeParty;
import fr.quatrevieux.araknemu.game.exploration.exchange.ExchangeParty;
import fr.quatrevieux.araknemu.game.item.Item;
import fr.quatrevieux.araknemu.game.item.effect.ItemEffect;
import fr.quatrevieux.araknemu.game.item.inventory.*;
import fr.quatrevieux.araknemu.game.item.inventory.exception.InventoryException;
import fr.quatrevieux.araknemu.game.item.inventory.exception.ItemNotFoundException;

import java.util.Iterator;
import java.util.stream.Collectors;

/**
 * The bank storage
 */
final public class Bank implements Inventory<BankEntry>, Dispatcher {
    final private BankService service;
    final private AccountBank entity;
    final private ListenerAggregate dispatcher = new DefaultListenerAggregate();
    final private Wallet wallet;

    private ItemStorage<BankEntry> storage;

    public Bank(BankService service, AccountBank entity) {
        this.service = service;
        this.entity = entity;

        wallet = new SimpleWallet(entity, dispatcher);
    }

    @Override
    public long kamas() {
        return wallet.kamas();
    }

    @Override
    public void addKamas(long quantity) {
        wallet.addKamas(quantity);
    }

    @Override
    public void removeKamas(long quantity) {
        wallet.removeKamas(quantity);
    }

    @Override
    public BankEntry get(int id) throws ItemNotFoundException {
        return storage().get(id);
    }

    @Override
    public BankEntry add(Item item, int quantity, int position) throws InventoryException {
        return storage().add(item, quantity, position);
    }

    @Override
    public BankEntry delete(int id) throws InventoryException {
        return storage().delete(id);
    }

    @Override
    public Iterator<BankEntry> iterator() {
        return storage().iterator();
    }

    @Override
    public void dispatch(Object event) {
        dispatcher.dispatch(event);
    }

    /**
     * Get the event dispatcher
     */
    public ListenerAggregate dispatcher() {
        return dispatcher;
    }

    /**
     * Save the bank
     *
     * If the entity is not yet created, it will be automatically created
     */
    public void save() {
        service.save(this);
    }

    /**
     * Get the cost for open the current bank
     */
    public long cost() {
        return service.cost(entity);
    }

    /**
     * Create an exchange for the current bank account
     *
     * @param player The interacting player
     */
    public ExchangeParty exchange(ExplorationPlayer player) {
        return new BankExchangeParty(player, this);
    }

    /**
     * Get the bank entity
     */
    AccountBank entity() {
        return entity;
    }

    /**
     * Get or load the item storage
     */
    private ItemStorage<BankEntry> storage() {
        if (storage != null) {
            return storage;
        }

        return storage = new StackableItemStorage<>(new SimpleItemStorage<>(this, this::create, service.loadItems(this)));
    }

    /**
     * Create a new entry
     */
    private BankEntry create(int id, Item item, int quantity, int position) {
        return new BankEntry(
            this,
            new BankItem(
                entity.accountId(),
                entity.serverId(),
                id,
                item.template().id(),
                item.effects().stream().map(ItemEffect::toTemplate).collect(Collectors.toList()),
                quantity
            ),
            item
        );
    }
}
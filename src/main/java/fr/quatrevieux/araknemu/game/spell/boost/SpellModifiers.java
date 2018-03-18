package fr.quatrevieux.araknemu.game.spell.boost;

/**
 * Container for spell boosts modifiers
 */
public interface SpellModifiers {
    /**
     * Get the modified spell id
     */
    public int spellId();

    /**
     * Get the boost value
     *
     * @param modifier The spell modifier
     */
    public int value(SpellsBoosts.Modifier modifier);

    /**
     * Check if the modifier is present
     *
     * @param modifier The spell modifier to check
     */
    public boolean has(SpellsBoosts.Modifier modifier);

    /**
     * Get the range modifier
     */
    default int range() {
        return value(SpellsBoosts.Modifier.RANGE);
    }

    /**
     * Check the spell modifiable range modifier
     */
    default boolean modifiableRange() {
        return value(SpellsBoosts.Modifier.MODIFIABLE_RANGE) > 0;
    }

    /**
     * Get the damage boost
     */
    default int damage() {
        return value(SpellsBoosts.Modifier.DAMAGE);
    }

    /**
     * Get the heal boost
     */
    default int heal() {
        return value(SpellsBoosts.Modifier.HEAL);
    }

    /**
     * Get the AP cost modifier
     */
    default int apCost() {
        return value(SpellsBoosts.Modifier.AP_COST);
    }

    /**
     * Get the launch delay reduce
     */
    default int delay() {
        return value(SpellsBoosts.Modifier.REDUCE_DELAY);
    }

    /**
     * Check if the a fixed delay modifier is present
     */
    default boolean hasFixedDelay() {
        return has(SpellsBoosts.Modifier.SET_DELAY);
    }

    /**
     * Get the fixed delay value
     */
    default int fixedDelay() {
        return value(SpellsBoosts.Modifier.SET_DELAY);
    }

    /**
     * Check if can launch spell out of line
     */
    default boolean launchOutline() {
        return value(SpellsBoosts.Modifier.LAUNCH_LINE) > 0;
    }

    /**
     * Check if can launch ignoring line of sight
     */
    default boolean lineOfSight() {
        return value(SpellsBoosts.Modifier.LINE_OF_SIGHT) > 0;
    }

    /**
     * Get the launch per target modifier
     */
    default int launchPerTarget() {
        return value(SpellsBoosts.Modifier.LAUNCH_PER_TARGET);
    }

    /**
     * Get the launch per turn modifier
     */
    default int launchPerTurn() {
        return value(SpellsBoosts.Modifier.LAUNCH_PER_TURN);
    }

    /**
     * Get the critical hit rate modifier
     */
    default int criticalHit() {
        return value(SpellsBoosts.Modifier.CRITICAL);
    }
}

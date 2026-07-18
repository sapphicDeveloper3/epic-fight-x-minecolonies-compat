package com.sapphic.efxmco.compat;

import com.minecolonies.core.entity.mobs.EntityMercenary;

import yesman.epicfight.world.capabilities.entitypatch.Factions;

/**
 * Epic Fight's view of a MineColonies mercenary.
 * <p>
 * Mercenaries are hired defenders, not raiders -- they fight for the colony against whatever
 * attacks it (including raiders) rather than being hostile to citizens/players themselves, so they
 * get {@link Factions#NEUTRAL} rather than the {@link Factions#ILLAGER} used for {@link
 * RaiderEntityPatch}. {@code EntityMercenary} doesn't share a MineColonies-specific ancestor with
 * citizens or raiders below {@code Mob}, so it gets its own patch class.
 */
public class MercenaryEntityPatch extends AbstractMinecoloniesMobPatch<EntityMercenary> {

    public MercenaryEntityPatch(EntityMercenary original) {
        super(original, Factions.NEUTRAL);
    }
}

package com.sapphic.efxmco.compat;

import com.minecolonies.api.entity.citizen.AbstractEntityCitizen;

import yesman.epicfight.world.capabilities.entitypatch.Factions;

/**
 * Epic Fight's view of a MineColonies citizen.
 * <p>
 * Both colonists and visitors use AbstractEntityCitizen as their base entity, and guards are
 * citizens with guard jobs rather than separate entity types. Everything shared with the other
 * MineColonies mob patches (raiders, mercenaries) lives in {@link AbstractMinecoloniesMobPatch}.
 */
public class CitizenEntityPatch extends AbstractMinecoloniesMobPatch<AbstractEntityCitizen> {

    public CitizenEntityPatch(AbstractEntityCitizen original) {
        super(original, Factions.VILLAGER);
    }
}

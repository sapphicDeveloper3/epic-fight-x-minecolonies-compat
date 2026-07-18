package com.sapphic.efxmco.compat;

import com.minecolonies.api.entity.mobs.AbstractEntityMinecoloniesMonster;

import yesman.epicfight.world.capabilities.entitypatch.Factions;

/**
 * Epic Fight's view of a MineColonies raider.
 * <p>
 * Covers every raider race (barbarians, pirates, egyptians, norsemen, amazons, drowned pirates)
 * and their non-raiding camp variants -- both the "raid" hierarchy ({@code
 * AbstractEntityMinecoloniesRaider}) and the "camp" hierarchy ({@code AbstractEntityBarbarian} and
 * its siblings) bottom out at {@link AbstractEntityMinecoloniesMonster}, and MineColonies runs the
 * exact same {@code RaiderMeleeAI} (see {@code MobAIRegistry#setupMobAiTasks}) against all of them
 * regardless of which hierarchy they're in, so one patch class covers both.
 * <p>
 * Archer variants (anything implementing {@code IArcherMobEntity}/{@code IRangedMobEntity}) still
 * get patched here for consistency and cosmetics, but their actual damage goes through {@code
 * RaiderRangedAI}'s projectile spawn rather than an instant {@code hurt()} call, so {@link
 * com.sapphic.efxmco.mixin.CombatAIMixin} has nothing to redirect for them -- same reasoning as
 * Ranger/Druid guards not being covered on the citizen side.
 */
public class RaiderEntityPatch extends AbstractMinecoloniesMobPatch<AbstractEntityMinecoloniesMonster> {

    public RaiderEntityPatch(AbstractEntityMinecoloniesMonster original) {
        super(original, Factions.ILLAGER);
    }
}

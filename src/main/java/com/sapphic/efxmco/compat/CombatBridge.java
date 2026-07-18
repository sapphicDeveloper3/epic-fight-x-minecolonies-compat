package com.sapphic.efxmco.compat;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;

import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

/**
 * Hands a single MineColonies attack off to Epic Fight instead of letting it deal vanilla instant
 * damage. See {@link com.sapphic.efxmco.mixin.CombatAIMixin} for the call sites -- every
 * MineColonies AI (guards, raiders, mercenaries) that lands damage via an instant
 * {@code target.hurt(...)} call routes through here instead.
 * <p>
 * Deliberately typed against {@link Mob} rather than any one MineColonies entity hierarchy: guards
 * and visitors are {@code AbstractEntityCitizen}, raiders are {@code AbstractEntityMinecoloniesMonster},
 * and mercenaries are their own {@code EntityMercenary} type with no common MineColonies ancestor
 * below {@code Mob}. Whatever is patched into Epic Fight's capability system works here.
 */
public final class CombatBridge {

    private CombatBridge() {
    }

    /**
     * Called from the mixins in place of MineColonies' own {@code target.hurt(...)} call.
     *
     * @return {@code true} if Epic Fight is going to handle this attack (the caller should skip
     *         its own damage application), {@code false} if the attacker wasn't actually patched
     *         (shouldn't normally happen since both mods are required dependencies, but capability
     *         attachment can theoretically still fail) and the caller should fall back to vanilla.
     */
    public static boolean handOff(Mob user, LivingEntity target) {
        LivingEntityPatch<?> patch = EpicFightCapabilities.getEntityPatch(user, LivingEntityPatch.class);

        if (patch == null) {
            return false;
        }

        // MineColonies' AttackMoveAI (or EntityMercenaryAI's own state machine) already decided
        // who to fight, closed the distance, and confirmed line of sight before calling
        // doAttack/fighting -- all we need to do is point Epic Fight at the same target. The
        // AnimatedAttackGoal installed in each *EntityPatch#initAI picks up from there on its own
        // schedule: it plays the actual weapon combo animation and applies damage through Epic
        // Fight's own animation hitboxes rather than an instant hurt() call.
        user.setTarget(target);
        return true;
    }
}

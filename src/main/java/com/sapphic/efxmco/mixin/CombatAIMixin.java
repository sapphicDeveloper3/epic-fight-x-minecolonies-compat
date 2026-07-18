package com.sapphic.efxmco.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.minecolonies.api.entity.ai.statemachine.tickratestatemachine.ITickRateStateMachine;
import com.minecolonies.api.entity.mobs.AbstractEntityMinecoloniesMonster;
import com.minecolonies.core.entity.ai.combat.AttackMoveAI;
import com.minecolonies.core.entity.ai.combat.TargetAI;
import com.minecolonies.core.entity.ai.workers.guard.MeleeCombatAI;
import com.minecolonies.core.entity.ai.combat.TargetAI;
import com.minecolonies.core.entity.citizen.EntityCitizen;
import com.minecolonies.core.entity.mobs.EntityMercenary;
import com.minecolonies.core.entity.mobs.EntityMercenaryAI;
import com.minecolonies.core.entity.mobs.aitasks.RaiderMeleeAI;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import com.sapphic.efxmco.compat.CombatBridge;


/**
 * Bridges MineColonies melee attacks into Epic Fight.
 */
public final class CombatAIMixin {

    private CombatAIMixin() {
    }


    /**
     * Handles all MineColonies melee guards.
     *
     * MeleeCombatAI is the replacement for the old KnightCombatAI.
     * CavalryCombatAI extends MeleeCombatAI, so it is automatically covered.
     */
    @Mixin(MeleeCombatAI.class)
    public static abstract class GuardCombatAIMixin extends AttackMoveAI<EntityCitizen> {

        protected GuardCombatAIMixin(EntityCitizen owner, ITickRateStateMachine<?> stateMachine) {
            super(owner, stateMachine);
        }


        @Redirect(
            method = "doAttack",
            at = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/world/entity/LivingEntity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"
            )
        )
        private boolean efxmco$handOffToEpicFight(
                LivingEntity target,
                DamageSource source,
                float amount
        ) {
            if (CombatBridge.handOff(this.user, target)) {
                return true;
            }

            return target.hurt(source, amount);
        }
    }


    /**
     * Handles every melee raider race.
     */
    @Mixin(RaiderMeleeAI.class)
    public static abstract class RaiderCombatAIMixin extends TargetAI<AbstractEntityMinecoloniesMonster> {

        protected RaiderCombatAIMixin(
                AbstractEntityMinecoloniesMonster owner,
                int targetFrequency,
                ITickRateStateMachine stateMachine
        ) {
            super(owner, targetFrequency, stateMachine);
        }


        @Redirect(
            method = "doAttack",
            at = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/world/entity/LivingEntity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"
            )
        )
        private boolean efxmco$handOffToEpicFight(
                LivingEntity target,
                DamageSource source,
                float amount
        ) {
            if (CombatBridge.handOff(this.user, target)) {
                return true;
            }

            return target.hurt(source, amount);
        }
    }


    /**
     * Handles mercenary combat.
     */
    @Mixin(EntityMercenaryAI.class)
    public static abstract class MercenaryCombatAIMixin extends Goal {

        @Shadow
        @Final
        private EntityMercenary entity;


        @Redirect(
            method = "fighting",
            at = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/world/entity/LivingEntity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"
            )
        )
        private boolean efxmco$handOffToEpicFight(
                LivingEntity target,
                DamageSource source,
                float amount
        ) {
            if (CombatBridge.handOff(this.entity, target)) {
                return true;
            }

            return target.hurt(source, amount);
        }
    }
}
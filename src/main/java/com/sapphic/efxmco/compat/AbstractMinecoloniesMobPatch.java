package com.sapphic.efxmco.compat;

import net.minecraft.world.entity.PathfinderMob;

import yesman.epicfight.api.animation.Animator;
import yesman.epicfight.api.animation.LivingMotions;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.world.capabilities.entitypatch.Faction;
import yesman.epicfight.world.capabilities.entitypatch.HumanoidMobPatch;
import yesman.epicfight.world.entity.ai.goal.TargetChasingGoal;

/**
 * Epic Fight's view of any MineColonies-controlled humanoid mob: citizens/visitors, raiders (and
 * their camp variants), and mercenaries all use a plain biped skeleton and are all moved around
 * exclusively by MineColonies' own AI (state machines for citizens/raiders, a {@link
 * net.minecraft.world.entity.ai.goal.Goal} state machine for mercenaries) rather than anything Epic
 * Fight would normally install. The three concrete subclasses ({@link CitizenEntityPatch}, {@link
 * RaiderEntityPatch}, {@link MercenaryEntityPatch}) only differ in entity type and {@link Faction}.
 */
public abstract class AbstractMinecoloniesMobPatch<T extends PathfinderMob> extends HumanoidMobPatch<T> {

    protected AbstractMinecoloniesMobPatch(T original, Faction faction) {
        super(original, faction);
    }

    @Override
    protected void initAI() {

        /*
         * Let Epic Fight install its animated attack system (this is what actually plays the
         * weapon combo animations CombatBridge#handOff triggers), but remove its target chasing
         * goal -- MineColonies already controls movement through its own AI/state machines, and
         * having both fight over navigation causes stuttering/fighting-the-controls movement.
         */
        super.initAI();

        this.original.goalSelector.getAvailableGoals()
                .removeIf(wrapped -> wrapped.getGoal() instanceof TargetChasingGoal);
    }

    @Override
    public void updateMotion(boolean considerInaction) {

        /*
         * None of these entities are permanent hostile mobs in the vanilla sense (even raiders
         * behave more like temporary hostile visitors), so use Epic Fight's normal humanoid
         * movement handling rather than a hostile-mob-only path.
         */
        super.commonMobUpdateMotion(considerInaction);
    }

    @Override
    public void initAnimator(Animator animator) {

        super.initAnimator(animator);

        animator.addLivingAnimation(LivingMotions.IDLE, Animations.BIPED_IDLE);
        animator.addLivingAnimation(LivingMotions.WALK, Animations.BIPED_WALK);
        animator.addLivingAnimation(LivingMotions.RUN, Animations.BIPED_RUN);
        animator.addLivingAnimation(LivingMotions.CHASE, Animations.BIPED_RUN);
        animator.addLivingAnimation(LivingMotions.FALL, Animations.BIPED_FALL);
        animator.addLivingAnimation(LivingMotions.DEATH, Animations.BIPED_DEATH);
        animator.addLivingAnimation(LivingMotions.JUMP, Animations.BIPED_JUMP);
        animator.addLivingAnimation(LivingMotions.SLEEP, Animations.BIPED_SLEEPING);
        animator.addLivingAnimation(LivingMotions.AIM, Animations.BIPED_BOW_AIM);
        animator.addLivingAnimation(LivingMotions.SHOT, Animations.BIPED_BOW_SHOT);
    }
}

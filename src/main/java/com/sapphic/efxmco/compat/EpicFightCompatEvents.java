package com.sapphic.efxmco.compat;

import java.util.List;

import com.google.common.collect.ImmutableList;

import com.minecolonies.api.entity.ModEntities;
import com.minecolonies.api.entity.citizen.AbstractEntityCitizen;
import com.minecolonies.api.entity.mobs.AbstractEntityMinecoloniesMonster;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

import com.sapphic.efxmco.epicfightxminecolonies;

import yesman.epicfight.api.event.EpicFightEventHooks;
import yesman.epicfight.api.event.types.registry.EntityPatchRegistryEvent;
import yesman.epicfight.gameasset.Armatures;


/**
 * Registers every MineColonies combat mob to Epic Fight: which {@code *EntityPatch} class each
 * entity type gets (this class), and which biped armature/renderer it uses ({@link
 * #registerArmatures()} here, {@link com.sapphic.efxmco.client.EpicFightCompatClientEvents} on the
 * client). See {@link com.sapphic.efxmco.mixin.CombatAIMixin} for how each mob's AI actually hands
 * its attacks off to the patches registered here.
 */
@EventBusSubscriber(modid = epicfightxminecolonies.MODID)
public class EpicFightCompatEvents {

    /**
     * Every MineColonies raider entity type: the raiding races (barbarians, pirates, egyptians,
     * norsemen, amazons, drowned pirates -- the same set {@link ModEntities#getRaiders()} returns)
     * plus their non-raiding camp variants. Both hierarchies bottom out at {@link
     * AbstractEntityMinecoloniesMonster} and MineColonies runs the same {@code RaiderMeleeAI}
     * against all of them (see {@code MobAIRegistry#setupMobAiTasks}), so they're all patched with
     * the same {@link RaiderEntityPatch}. Listed explicitly rather than merged from {@code
     * getRaiders()} to avoid the doubly-wildcarded generics ({@code List<EntityType<? extends
     * AbstractEntityMinecoloniesRaider>>} into a {@code EntityType<? extends
     * AbstractEntityMinecoloniesMonster>} builder) that merge would otherwise require.
     */
    private static List<EntityType<? extends AbstractEntityMinecoloniesMonster>> raiderTypesCache;

    /**
     * Lazily builds (and caches) the raider-type list on first call. Must NOT be a static final
     * field: {@code ModEntities.BARBARIAN} etc. are plain mutable statics that MineColonies only
     * assigns during the {@code RegisterEvent<EntityType>} registry event, which fires after all mod
     * constructors run. A static final field here would build (and NPE on) this list the instant the
     * class is first loaded -- i.e. as soon as {@link #register()} is called from the mod
     * constructor, long before those fields are populated. Deferring the build to first real use
     * (from {@link #registerPatchedEntities}, on Epic Fight's EntityPatchRegistryEvent, or {@link
     * #registerArmatures}, on FMLCommonSetupEvent) guarantees the registry event has already run.
     */
    private static List<EntityType<? extends AbstractEntityMinecoloniesMonster>> raiderTypes() {
        if (raiderTypesCache != null) {
            return raiderTypesCache;
        }
        return raiderTypesCache =
            ImmutableList.<EntityType<? extends AbstractEntityMinecoloniesMonster>>builder()
                    // Raiding variants
                    .add(ModEntities.BARBARIAN)
                    .add(ModEntities.ARCHERBARBARIAN)
                    .add(ModEntities.CHIEFBARBARIAN)
                    .add(ModEntities.PIRATE)
                    .add(ModEntities.CHIEFPIRATE)
                    .add(ModEntities.ARCHERPIRATE)
                    .add(ModEntities.MUMMY)
                    .add(ModEntities.PHARAO)
                    .add(ModEntities.ARCHERMUMMY)
                    .add(ModEntities.NORSEMEN_ARCHER)
                    .add(ModEntities.SHIELDMAIDEN)
                    .add(ModEntities.NORSEMEN_CHIEF)
                    .add(ModEntities.AMAZON)
                    .add(ModEntities.AMAZONSPEARMAN)
                    .add(ModEntities.AMAZONCHIEF)
                    .add(ModEntities.DROWNED_PIRATE)
                    .add(ModEntities.DROWNED_CHIEFPIRATE)
                    .add(ModEntities.DROWNED_ARCHERPIRATE)
                    // Camp (non-raiding) variants
                    .add(ModEntities.CAMP_BARBARIAN)
                    .add(ModEntities.CAMP_ARCHERBARBARIAN)
                    .add(ModEntities.CAMP_CHIEFBARBARIAN)
                    .add(ModEntities.CAMP_PIRATE)
                    .add(ModEntities.CAMP_CHIEFPIRATE)
                    .add(ModEntities.CAMP_ARCHERPIRATE)
                    .add(ModEntities.CAMP_AMAZON)
                    .add(ModEntities.CAMP_AMAZONSPEARMAN)
                    .add(ModEntities.CAMP_AMAZONCHIEF)
                    .add(ModEntities.CAMP_MUMMY)
                    .add(ModEntities.CAMP_PHARAO)
                    .add(ModEntities.CAMP_ARCHERMUMMY)
                    .add(ModEntities.CAMP_NORSEMEN_ARCHER)
                    .add(ModEntities.CAMP_SHIELDMAIDEN)
                    .add(ModEntities.CAMP_NORSEMEN_CHIEF)
                    .add(ModEntities.CAMP_DROWNED_PIRATE)
                    .add(ModEntities.CAMP_DROWNED_CHIEFPIRATE)
                    .add(ModEntities.CAMP_DROWNED_ARCHERPIRATE)
                    .build();
    }

    /**
     * EntityPatchRegistryEvent is one of Epic Fight's own event types, not a NeoForge event, so
     * it's registered through Epic Fight's EventHook system rather than the NeoForge mod bus. Call
     * this from the mod constructor.
     */
    public static void register() {
        EpicFightEventHooks.Registry.ENTITY_PATCH.registerEvent(EpicFightCompatEvents::registerPatchedEntities);
    }

    private static void registerPatchedEntities(EntityPatchRegistryEvent event) {

        registerCitizenPatch(event, ModEntities.CITIZEN);
        registerCitizenPatch(event, ModEntities.VISITOR);

        for (EntityType<? extends AbstractEntityMinecoloniesMonster> raiderType : raiderTypes()) {
            registerRaiderPatch(event, raiderType);
        }

        registerMercenaryPatch(event, ModEntities.MERCENARY);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void registerCitizenPatch(
            EntityPatchRegistryEvent event,
            EntityType<? extends AbstractEntityCitizen> type
    ) {
        event.registerEntityPatchUnsafe((EntityType) type, CitizenEntityPatch::new);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void registerRaiderPatch(
            EntityPatchRegistryEvent event,
            EntityType<? extends AbstractEntityMinecoloniesMonster> type
    ) {
        event.registerEntityPatchUnsafe((EntityType) type, RaiderEntityPatch::new);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void registerMercenaryPatch(
            EntityPatchRegistryEvent event,
            EntityType<? extends PathfinderMob> type
    ) {
        // ModEntities.MERCENARY is declared against the wider PathfinderMob bound rather than
        // EntityMercenary specifically, but it only ever spawns EntityMercenary instances --
        // registerEntityPatchUnsafe is the escape hatch for exactly this kind of mismatch, same as
        // registerCitizenPatch/registerRaiderPatch above.
        event.registerEntityPatchUnsafe((EntityType) type, MercenaryEntityPatch::new);
    }

    @SubscribeEvent
    public static void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(EpicFightCompatEvents::registerArmatures);
    }

    private static void registerArmatures() {

        Armatures.registerEntityTypeArmature(ModEntities.CITIZEN, Armatures.BIPED);
        Armatures.registerEntityTypeArmature(ModEntities.VISITOR, Armatures.BIPED);

        for (EntityType<? extends AbstractEntityMinecoloniesMonster> raiderType : raiderTypes()) {
            Armatures.registerEntityTypeArmature(raiderType, Armatures.BIPED);
        }

        Armatures.registerEntityTypeArmature(ModEntities.MERCENARY, Armatures.BIPED);
    }
}

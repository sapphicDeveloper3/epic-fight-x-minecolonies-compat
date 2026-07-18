package com.sapphic.efxmco.client;

import java.util.Map;

import com.minecolonies.api.entity.ModEntities;
import com.sapphic.efxmco.client.mesh.CitizenMeshSource;
import com.sapphic.efxmco.client.mesh.RaiderMeshTable;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;

import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.client.event.EpicFightClientEventHooks;
import yesman.epicfight.api.client.event.types.registry.RegisterPatchedRenderersEvent;
import yesman.epicfight.api.client.model.SkinnedMesh;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public class EpicFightCompatClientEvents {

    /**
     * RegisterPatchedRenderersEvent extends Epic Fight's own {@code yesman.epicfight.api.event.Event},
     * not NeoForge's Event, so it can't be subscribed via {@code @SubscribeEvent} / the NeoForge event
     * bus (NeoForge validates listener signatures at registration time and throws otherwise). It has to
     * be registered through Epic Fight's own EventHook system instead. Call this from a client-only
     * constructor/setup point.
     */
    public static void register() {
        EpicFightClientEventHooks.Registry.ADD_PATCHED_ENTITY.registerEvent(
                EpicFightCompatClientEvents::registerPatchedEntityRenderers
        );
    }

    private static void registerPatchedEntityRenderers(RegisterPatchedRenderersEvent.AddEntity event) {

        // Citizens/visitors swap models per profession + gender at runtime (see
        // CitizenMeshSource), so their mesh has to be resolved fresh every frame rather than
        // fixed once at renderer construction.
        addDynamicRenderer(event, ModEntities.CITIZEN);
        addDynamicRenderer(event, ModEntities.VISITOR);

        // Raiders (and mercenaries) never change model at runtime, so bake everything once up
        // front here and hand each renderer its fixed mesh directly.
        Map<EntityType<?>, AssetAccessor<SkinnedMesh>> raiderMeshes = RaiderMeshTable.build(event.getContext());

        for (Map.Entry<EntityType<?>, AssetAccessor<SkinnedMesh>> entry : raiderMeshes.entrySet()) {
            addFixedRenderer(event, entry.getKey(), entry.getValue());
        }
    }

    private static void addDynamicRenderer(RegisterPatchedRenderersEvent.AddEntity event, EntityType<?> entityType) {
        event.addPatchedEntityRenderer(
                entityType,
                type -> new DynamicMeshPatchRenderer(
                        event.getContext(),
                        type,
                        (LivingEntityPatch<LivingEntity> entitypatch) -> CitizenMeshSource.get(entitypatch.getOriginal()),
                        null
                )
        );
    }

    private static void addFixedRenderer(RegisterPatchedRenderersEvent.AddEntity event, EntityType<?> entityType, AssetAccessor<SkinnedMesh> mesh) {
        event.addPatchedEntityRenderer(
                entityType,
                type -> new DynamicMeshPatchRenderer(event.getContext(), type, mesh)
        );
    }
}

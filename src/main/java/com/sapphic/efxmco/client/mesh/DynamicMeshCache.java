package com.sapphic.efxmco.client.mesh;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.resources.ResourceLocation;

import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.client.model.SkinnedMesh;
import yesman.epicfight.api.client.model.transformer.HumanoidModelBaker;

/**
 * Converts arbitrary {@link HumanoidModel} instances (MineColonies' citizen/raider models, in our
 * case) into Epic Fight meshes at runtime, instead of forcing every mob onto Epic Fight's stock
 * {@code Meshes.BIPED} asset.
 * <p>
 * {@code Meshes.BIPED}'s vertex UVs are baked assuming a plain vanilla-proportioned skin. MineColonies
 * models don't fit that mold -- citizens use 128x64 textures and raiders use custom, non-vanilla box
 * geometry entirely -- so reusing the stock mesh stretches/misaligns the texture and shows up as gaps
 * and holes in the skin. Baking a mesh from the mob's own {@link HumanoidModel} sidesteps the problem
 * completely: the UVs come straight off that model's real {@code ModelPart} cubes, whatever texture
 * size or shape they were built against.
 * <p>
 * The heavy lifting is Epic Fight's own {@link HumanoidModelBaker#VANILLA_TRANSFORMER}, which it
 * already ships to turn a modded armor model's {@link HumanoidModel} into a skinned mesh for
 * {@code WearableItemLayer}. That transformer doesn't care whether the model it's given represents a
 * chestplate or an entire mob body -- it just walks every visible {@code ModelPart} (head, hat, body,
 * limbs, and any custom child parts a mod added) and bakes real geometry + UVs off of it -- so reusing
 * it here for full mob bodies works without needing to hand-author replacement mesh assets.
 */
public final class DynamicMeshCache {

    private static final Map<ResourceLocation, SkinnedMesh> CACHE = new ConcurrentHashMap<>();

    private DynamicMeshCache() {
    }

    /**
     * Returns a cached bake for {@code key}, baking it via {@code modelSupplier} on first request.
     * {@code modelSupplier} is only invoked on a cache miss, so it's safe to pass a supplier that
     * constructs/looks up the live MineColonies model each call.
     */
    public static AssetAccessor<SkinnedMesh> getOrBake(ResourceLocation key, Supplier<HumanoidModel<?>> modelSupplier) {
        SkinnedMesh mesh = CACHE.computeIfAbsent(key, k -> bake(modelSupplier.get()));
        return new BakedMeshAccessor(key, mesh);
    }

    private static SkinnedMesh bake(HumanoidModel<?> model) {
        // No need to reset pose here: VanillaModelTransformer.transformArmorModel() already resets
        // every ModelPart (core parts and any custom children) to its initial bind pose internally
        // before baking, so whatever mid-animation state the model happened to be in when we grabbed
        // it doesn't leak into the baked mesh.
        return HumanoidModelBaker.VANILLA_TRANSFORMER.transformArmorModel(model);
    }

    private record BakedMeshAccessor(ResourceLocation registryName, SkinnedMesh mesh) implements AssetAccessor<SkinnedMesh> {
        @Override
        public SkinnedMesh get() {
            return this.mesh;
        }

        @Override
        public boolean inRegistry() {
            return false;
        }
    }
}
package com.sapphic.efxmco.client;

import java.util.function.Function;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.ElytraLayer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import yesman.epicfight.api.animation.JointTransform;
import yesman.epicfight.api.animation.Pose;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.client.model.Meshes;
import yesman.epicfight.api.client.model.SkinnedMesh;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.client.renderer.patched.entity.PatchedLivingEntityRenderer;
import yesman.epicfight.client.renderer.patched.layer.PatchedElytraLayer;
import yesman.epicfight.client.renderer.patched.layer.PatchedHeadLayer;
import yesman.epicfight.client.renderer.patched.layer.PatchedItemInHandLayer;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

/**
 * Epic Fight's renderer for every MineColonies patched mob: citizens/visitors, raiders (and their
 * camp variants), and mercenaries all use a HumanoidModel skeleton (see {@link
 * com.sapphic.efxmco.compat.AbstractMinecoloniesMobPatch}).
 * <p>
 * Unlike {@code PCustomHumanoidEntityRenderer}/{@code PHumanoidRenderer}, this doesn't hand every
 * mob Epic Fight's stock {@code Meshes.BIPED} -- that mesh's UVs are baked assuming a plain
 * 64x64 vanilla skin, and MineColonies' citizen (128x64) and raider (custom geometry, up to
 * 128x128) models don't fit that mold, which is what caused the texture gaps/holes. Instead
 * {@code meshProvider} resolves the correct {@link com.sapphic.efxmco.client.mesh.DynamicMeshCache
 * dynamically baked} mesh for whatever entity is being rendered -- see {@link
 * com.sapphic.efxmco.client.mesh.CitizenMeshSource} (per-profession, resolved every frame since a
 * citizen's model can change at runtime) and {@link com.sapphic.efxmco.client.mesh.RaiderMeshTable}
 * (one fixed mesh per raider race, resolved once at renderer construction).
 * <p>
 * Trade-off: this extends {@code PatchedLivingEntityRenderer} directly with {@code AM = SkinnedMesh}
 * rather than Epic Fight's {@code HumanoidMesh}, because a baked mesh doesn't have the named
 * head/torso/limb parts {@code HumanoidMesh} (and the {@code WearableItemLayer} armor patch that
 * requires it) expect. We deliberately don't patch {@code HumanoidArmorLayer} here -- Epic Fight's
 * own {@code initLayerLast()} falls back to rendering unhandled vanilla layers via
 * {@code RenderOriginalModelLayer} (the vanilla armor model tracking the animated root bone), so
 * equipped armor still renders instead of silently disappearing.
 */
@OnlyIn(Dist.CLIENT)
public class DynamicMeshPatchRenderer extends PatchedLivingEntityRenderer<
        LivingEntity,
        LivingEntityPatch<LivingEntity>,
        HumanoidModel<LivingEntity>,
        LivingEntityRenderer<LivingEntity, HumanoidModel<LivingEntity>>,
        SkinnedMesh> {

    /**
     * Cast of Epic Fight's own {@code Meshes.BIPED} down to a plain {@code AssetAccessor<SkinnedMesh>}.
     * {@code AssetAccessor} is invariant in its type parameter, so this needs an unchecked cast even
     * though {@code HumanoidMesh} (what {@code Meshes.BIPED} actually holds) is-a {@code SkinnedMesh}.
     * Used only as a last-resort fallback if a mesh provider can't resolve anything for an entity.
     */
    @SuppressWarnings("unchecked")
    private static final AssetAccessor<SkinnedMesh> VANILLA_BIPED_FALLBACK = (AssetAccessor<SkinnedMesh>) (AssetAccessor<?>) Meshes.BIPED;

    private final Function<LivingEntityPatch<LivingEntity>, AssetAccessor<SkinnedMesh>> meshProvider;
    private final AssetAccessor<SkinnedMesh> fallbackMesh;

    public DynamicMeshPatchRenderer(
            EntityRendererProvider.Context context,
            EntityType<?> entityType,
            Function<LivingEntityPatch<LivingEntity>, AssetAccessor<SkinnedMesh>> meshProvider,
            AssetAccessor<SkinnedMesh> fallbackMesh
    ) {
        super(context, entityType);

        this.meshProvider = meshProvider;
        this.fallbackMesh = fallbackMesh;

        this.addPatchedLayer(ElytraLayer.class, new PatchedElytraLayer<>());
        this.addPatchedLayer(ItemInHandLayer.class, new PatchedItemInHandLayer<>());
        this.addPatchedLayer(CustomHeadLayer.class, new PatchedHeadLayer<>());
    }

    /**
     * Convenience overload for mobs with one fixed mesh per {@code EntityType} (raiders, mercenaries)
     * -- resolved once up front rather than re-looked-up every frame.
     */
    public DynamicMeshPatchRenderer(
            EntityRendererProvider.Context context,
            EntityType<?> entityType,
            AssetAccessor<SkinnedMesh> fixedMesh
    ) {
        this(context, entityType, entitypatch -> fixedMesh, fixedMesh);
    }

    @Override
    public void setJointTransforms(LivingEntityPatch<LivingEntity> entitypatch, Armature armature, Pose pose, float partialTicks) {
        if (entitypatch.getOriginal().isBaby()) {
            pose.orElseEmpty("Head").frontResult(JointTransform.scale(new Vec3f(1.25F, 1.25F, 1.25F)), OpenMatrix4f::mul);
        }
    }

    @Override
    protected float getDefaultLayerHeightCorrection() {
        return 0.75F;
    }

    @Override
    public AssetAccessor<SkinnedMesh> getMeshProvider(LivingEntityPatch<LivingEntity> entitypatch) {
        AssetAccessor<SkinnedMesh> mesh = this.meshProvider.apply(entitypatch);
        return mesh != null ? mesh : this.getDefaultMesh();
    }

    @Override
    public AssetAccessor<SkinnedMesh> getDefaultMesh() {
        return this.fallbackMesh != null ? this.fallbackMesh : VANILLA_BIPED_FALLBACK;
    }
}

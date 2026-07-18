package com.sapphic.efxmco.client.mesh;

import javax.annotation.Nullable;

import com.minecolonies.api.client.render.modeltype.IModelType;
import com.minecolonies.api.client.render.modeltype.registry.IModelTypeRegistry;
import com.minecolonies.api.entity.citizen.AbstractEntityCitizen;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.client.model.SkinnedMesh;

/**
 * Resolves the correct baked mesh for a citizen or visitor on every render call.
 * <p>
 * Unlike raiders (one fixed model per entity type), MineColonies swaps a citizen's {@code Model}
 * per profession + gender -- see {@code RenderBipedCitizen#setupMainModelFrom} -- so a single mesh
 * registered once per {@code EntityType} can never be correct for citizens; a farmer and a guard are
 * both {@code ModEntities.CITIZEN} but need different geometry. Querying {@link IModelTypeRegistry}
 * directly here (the same call {@code setupMainModelFrom} itself makes) rather than reading whatever
 * the vanilla renderer's model field currently holds also sidesteps any doubt about whether Epic
 * Fight's render hook runs MineColonies' own model-swapping logic before we ask for a mesh -- this
 * works it out independently every frame.
 */
public final class CitizenMeshSource {

    private CitizenMeshSource() {
    }

    @Nullable
    public static AssetAccessor<SkinnedMesh> get(LivingEntity entity) {
        if (!(entity instanceof AbstractEntityCitizen citizen)) {
            return null;
        }

        IModelType modelType = IModelTypeRegistry.getInstance().getModelType(citizen.getModelType());

        if (modelType == null) {
            return null;
        }

        boolean female = citizen.isFemale();
        HumanoidModel<AbstractEntityCitizen> model = female ? modelType.getFemaleModel() : modelType.getMaleModel();

        if (model == null) {
            return null;
        }

        ResourceLocation type = modelType.getName();
        ResourceLocation key = ResourceLocation.fromNamespaceAndPath(type.getNamespace(), type.getPath() + (female ? "_female" : "_male"));

        return DynamicMeshCache.getOrBake(key, () -> model);
    }
}
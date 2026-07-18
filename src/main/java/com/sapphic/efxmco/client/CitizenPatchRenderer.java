package com.sapphic.efxmco.client;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.EntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import yesman.epicfight.api.client.model.Meshes;
import yesman.epicfight.client.mesh.HumanoidMesh;
import yesman.epicfight.client.renderer.patched.entity.PCustomHumanoidEntityRenderer;

/**
 * Epic Fight's renderer for MineColonies citizens (and visitors). Citizens use a HumanoidModel
 * skeleton (see {@link com.sapphic.efxmco.compat.CitizenEntityPatch}), so this just wires them up
 * to Epic Fight's stock biped mesh via {@link PCustomHumanoidEntityRenderer} -- no custom mesh or
 * texture handling needed, Epic Fight resolves that itself from the biped asset.
 */
@OnlyIn(Dist.CLIENT)
public class CitizenPatchRenderer extends PCustomHumanoidEntityRenderer<HumanoidMesh> {

    public CitizenPatchRenderer(EntityRendererProvider.Context context, EntityType<?> entityType) {
        super(Meshes.BIPED, context, entityType);
    }
}
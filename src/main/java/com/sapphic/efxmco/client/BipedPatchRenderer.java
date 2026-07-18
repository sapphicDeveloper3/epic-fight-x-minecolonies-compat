package com.sapphic.efxmco.client;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.EntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import yesman.epicfight.api.client.model.Meshes;
import yesman.epicfight.client.mesh.HumanoidMesh;
import yesman.epicfight.client.renderer.patched.entity.PCustomHumanoidEntityRenderer;

/**
 * Epic Fight's renderer for every MineColonies patched mob: citizens/visitors, raiders (and their
 * camp variants), and mercenaries all use a HumanoidModel skeleton (see {@link
 * com.sapphic.efxmco.compat.AbstractMinecoloniesMobPatch}), so this just wires them all up to Epic
 * Fight's stock biped mesh via {@link PCustomHumanoidEntityRenderer} -- no custom mesh or texture
 * handling needed, Epic Fight resolves that itself from the biped asset.
 */
@OnlyIn(Dist.CLIENT)
public class BipedPatchRenderer extends PCustomHumanoidEntityRenderer<HumanoidMesh> {

    public BipedPatchRenderer(EntityRendererProvider.Context context, EntityType<?> entityType) {
        super(Meshes.BIPED, context, entityType);
    }
}
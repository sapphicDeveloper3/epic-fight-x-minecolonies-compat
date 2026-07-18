package com.sapphic.efxmco.client.mesh;

import java.util.HashMap;
import java.util.Map;

import com.minecolonies.api.entity.ModEntities;
import com.minecolonies.core.client.model.MercenaryModel;
import com.minecolonies.core.client.model.raiders.ModelAmazon;
import com.minecolonies.core.client.model.raiders.ModelAmazonChief;
import com.minecolonies.core.client.model.raiders.ModelAmazonSpearman;
import com.minecolonies.core.client.model.raiders.ModelArcherMummy;
import com.minecolonies.core.client.model.raiders.ModelArcherNorsemen;
import com.minecolonies.core.client.model.raiders.ModelChiefNorsemen;
import com.minecolonies.core.client.model.raiders.ModelMummy;
import com.minecolonies.core.client.model.raiders.ModelPharaoh;
import com.minecolonies.core.client.model.raiders.ModelShieldmaiden;
import com.minecolonies.core.event.ClientRegistryHandler;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;

import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.client.model.SkinnedMesh;

/**
 * Bakes one mesh per raider "race" and maps every raiding + camp {@code EntityType} that shares that
 * race onto it -- MineColonies itself registers the exact same renderer/model for both variants
 * (see {@code ClientRegistryHandler}'s entity renderer registration, e.g. both {@code ModEntities.AMAZON}
 * and {@code ModEntities.CAMP_AMAZON} construct {@code RendererAmazon}), so there's no need to bake
 * twice per race.
 * <p>
 * Unlike citizens, a raider's model never changes at runtime, so this bakes everything once up front
 * at renderer-construction time (when the {@link EntityRendererProvider.Context} needed to bake vanilla
 * {@code ModelLayerLocation}s is available) rather than resolving lazily per frame.
 */
public final class RaiderMeshTable {

    private static final ResourceLocation NS = ResourceLocation.fromNamespaceAndPath("efxmco", "dynamic_raider");

    private RaiderMeshTable() {
    }

    public static Map<EntityType<?>, AssetAccessor<SkinnedMesh>> build(EntityRendererProvider.Context context) {
        Map<String, AssetAccessor<SkinnedMesh>> byRace = new HashMap<>();

        // Races sharing the plain vanilla outer/inner armor humanoid model -- these already match
        // Meshes.BIPED's assumed proportions, but we bake them the same way as everything else for
        // consistency and because their textures may still diverge from a plain 64x64 skin layout.
        byRace.put("barbarian", bake("barbarian", new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER_OUTER_ARMOR))));
        byRace.put("chief_barbarian", bake("chief_barbarian", new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER_OUTER_ARMOR))));
        byRace.put("pirate", bake("pirate", new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER_OUTER_ARMOR))));
        byRace.put("chief_pirate", bake("chief_pirate", new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER_INNER_ARMOR))));
        byRace.put("archer_pirate", bake("archer_pirate", new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER_OUTER_ARMOR))));
        byRace.put("drowned_pirate", bake("drowned_pirate", new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER_OUTER_ARMOR))));
        byRace.put("drowned_archer_pirate", bake("drowned_archer_pirate", new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER_OUTER_ARMOR))));
        byRace.put("drowned_chief_pirate", bake("drowned_chief_pirate", new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER_INNER_ARMOR))));

        // Races with fully custom geometry.
        byRace.put("mummy", bake("mummy", new ModelMummy(context.bakeLayer(ClientRegistryHandler.MUMMY))));
        byRace.put("archer_mummy", bake("archer_mummy", new ModelArcherMummy(context.bakeLayer(ClientRegistryHandler.ARCHER_MUMMY))));
        byRace.put("pharao", bake("pharao", new ModelPharaoh(context.bakeLayer(ClientRegistryHandler.PHARAO))));
        byRace.put("norsemen_archer", bake("norsemen_archer", new ModelArcherNorsemen(context.bakeLayer(ClientRegistryHandler.NORSEMEN_ARCHER))));
        byRace.put("shield_maiden", bake("shield_maiden", new ModelShieldmaiden(context.bakeLayer(ClientRegistryHandler.SHIELD_MAIDEN))));
        byRace.put("norsemen_chief", bake("norsemen_chief", new ModelChiefNorsemen(context.bakeLayer(ClientRegistryHandler.NORSEMEN_CHIEF))));
        byRace.put("amazon", bake("amazon", new ModelAmazon(context.bakeLayer(ClientRegistryHandler.AMAZON))));
        byRace.put("amazon_spearman", bake("amazon_spearman", new ModelAmazonSpearman(context.bakeLayer(ClientRegistryHandler.AMAZON_SPEARMAN))));
        byRace.put("amazon_chief", bake("amazon_chief", new ModelAmazonChief(context.bakeLayer(ClientRegistryHandler.AMAZON_CHIEF))));
        byRace.put("mercenary", bake("mercenary", new MercenaryModel(context.bakeLayer(ClientRegistryHandler.MERCENARY))));

        Map<EntityType<?>, AssetAccessor<SkinnedMesh>> byType = new HashMap<>();

        put(byType, byRace, "barbarian", ModEntities.BARBARIAN, ModEntities.ARCHERBARBARIAN, ModEntities.CAMP_BARBARIAN, ModEntities.CAMP_ARCHERBARBARIAN);
        put(byType, byRace, "chief_barbarian", ModEntities.CHIEFBARBARIAN, ModEntities.CAMP_CHIEFBARBARIAN);
        put(byType, byRace, "pirate", ModEntities.PIRATE, ModEntities.CAMP_PIRATE);
        put(byType, byRace, "chief_pirate", ModEntities.CHIEFPIRATE, ModEntities.CAMP_CHIEFPIRATE);
        put(byType, byRace, "archer_pirate", ModEntities.ARCHERPIRATE, ModEntities.CAMP_ARCHERPIRATE);
        put(byType, byRace, "drowned_pirate", ModEntities.DROWNED_PIRATE, ModEntities.CAMP_DROWNED_PIRATE);
        put(byType, byRace, "drowned_archer_pirate", ModEntities.DROWNED_ARCHERPIRATE, ModEntities.CAMP_DROWNED_ARCHERPIRATE);
        put(byType, byRace, "drowned_chief_pirate", ModEntities.DROWNED_CHIEFPIRATE, ModEntities.CAMP_DROWNED_CHIEFPIRATE);
        put(byType, byRace, "mummy", ModEntities.MUMMY, ModEntities.CAMP_MUMMY);
        put(byType, byRace, "archer_mummy", ModEntities.ARCHERMUMMY, ModEntities.CAMP_ARCHERMUMMY);
        put(byType, byRace, "pharao", ModEntities.PHARAO, ModEntities.CAMP_PHARAO);
        put(byType, byRace, "norsemen_archer", ModEntities.NORSEMEN_ARCHER, ModEntities.CAMP_NORSEMEN_ARCHER);
        put(byType, byRace, "shield_maiden", ModEntities.SHIELDMAIDEN, ModEntities.CAMP_SHIELDMAIDEN);
        put(byType, byRace, "norsemen_chief", ModEntities.NORSEMEN_CHIEF, ModEntities.CAMP_NORSEMEN_CHIEF);
        put(byType, byRace, "amazon", ModEntities.AMAZON, ModEntities.CAMP_AMAZON);
        put(byType, byRace, "amazon_spearman", ModEntities.AMAZONSPEARMAN, ModEntities.CAMP_AMAZONSPEARMAN);
        put(byType, byRace, "amazon_chief", ModEntities.AMAZONCHIEF, ModEntities.CAMP_AMAZONCHIEF);
        put(byType, byRace, "mercenary", ModEntities.MERCENARY);

        return byType;
    }

    private static AssetAccessor<SkinnedMesh> bake(String race, HumanoidModel<?> model) {
        ResourceLocation key = ResourceLocation.fromNamespaceAndPath(NS.getNamespace(), NS.getPath() + "/" + race);
        return DynamicMeshCache.getOrBake(key, () -> model);
    }

    @SafeVarargs
    private static void put(
            Map<EntityType<?>, AssetAccessor<SkinnedMesh>> byType,
            Map<String, AssetAccessor<SkinnedMesh>> byRace,
            String race,
            EntityType<?>... types
    ) {
        AssetAccessor<SkinnedMesh> mesh = byRace.get(race);

        for (EntityType<?> type : types) {
            byType.put(type, mesh);
        }
    }
}
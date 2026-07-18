package com.sapphic.efxmco;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;
import com.sapphic.efxmco.compat.EpicFightCompatEvents;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;

/**
 * Bridges Epic Fight combat onto MineColonies citizens.
 * <p>
 * See {@link com.sapphic.efxmco.compat.EpicFightCompatEvents} for how citizens get patched into
 * Epic Fight, and {@link com.sapphic.efxmco.mixin.KnightCombatAIMixin} for how MineColonies'
 * melee guard AI hands its attacks off to Epic Fight instead of dealing vanilla instant damage.
 */
@Mod(epicfightxminecolonies.MODID)
public class epicfightxminecolonies {
    public static final String MODID = "epicfightxminecolonies";
    public static final Logger LOGGER = LogUtils.getLogger();

    public epicfightxminecolonies(IEventBus modEventBus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);

        // EntityPatchRegistryEvent is one of Epic Fight's own event types, not a NeoForge event,
        // so it's registered through Epic Fight's EventHook system rather than the NeoForge mod bus.
        EpicFightCompatEvents.register();
    }
}

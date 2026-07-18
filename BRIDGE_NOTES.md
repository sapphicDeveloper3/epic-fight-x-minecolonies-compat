# Epic Fight x MineColonies bridge — what this does and how

## The core problem

MineColonies guards don't really "fight" the way Epic Fight-patched mobs do. Their combat AI
(`AttackMoveAI` → `KnightCombatAI`/`RangerCombatAI`/`CavalryCombatAI`/`DruidCombatAI`) is a
custom tick-rate state machine, totally separate from vanilla `Goal`s. When a melee guard's
cooldown allows it, `doAttack()` runs a vanilla arm swing for show and then calls
`target.hurt(source, damage)` directly — instant, no wind-up, no weapon reach, no whiff.

Epic Fight mobs, by contrast, deal damage through actual animation hitboxes: a goal
(`AnimatedAttackGoal`) drives a combo of `StaticAnimation`s, and damage only lands when the
weapon's collider intersects the target during a specific animation phase.

Bridging these means two things:
1. Make Epic Fight aware citizens exist at all (a "patch").
2. Stop MineColonies' guard AI from *also* dealing its own instant damage once Epic Fight is
   handling the swing — otherwise you get double damage / two combat systems fighting each other.

## What's built

**`compat/CitizenEntityPatch.java`** — `AbstractEntityCitizen` (covers both `EntityCitizen` and
visitors) as a `HumanoidMobPatch`. MineColonies' `CitizenModel` extends vanilla `HumanoidModel`,
so Epic Fight's stock biped rig/animations line up without authoring anything custom. Gives
citizens weapon-aware attack animations automatically (`HumanoidMobPatch` maps sword/spear/axe/
dagger/fist/bow to Epic Fight's built-in combos based on whatever's in the main hand). The
`initAI()` override strips Epic Fight's own `TargetChasingGoal` back out — MineColonies already
owns positioning for guards via its own navigation calls, and running two independent pathing
systems on the same entity causes stutter.

**`compat/EpicFightCompatEvents.java`** — registers `ModEntities.CITIZEN`/`VISITOR` with Epic
Fight's `EntityPatchRegistryEvent`, and their armature (`Armatures.BIPED`) on common setup.

**`client/CitizenPatchRenderer.java` + `EpicFightCompatClientEvents.java`** — registers Epic
Fight's patched renderer pipeline for citizens, following the same `PHumanoidRenderer` +
`Meshes.BIPED` path vanilla mobs use.

**`mixin/KnightCombatAIMixin.java`** — the actual "don't double-hit" bridge. `@Redirect`s the
single `target.hurt(source, amount)` call inside `KnightCombatAI#doAttack` to
`CombatBridge#handOff`, which just calls `user.setTarget(target)` so Epic Fight's
`AnimatedAttackGoal` (installed by the patch) picks up the same target and throws its own
animated swing on its own timing. Every other side effect in `doAttack` (fire aspect, the AOE
whirlwind proc, taunt, item durability, stats) still runs unchanged — only the damage number
itself is deferred to Epic Fight. `CavalryCombatAI extends KnightCombatAI` without overriding
`doAttack`, so this covers both melee guard types with one mixin.

## What isn't covered yet

- **Rangers and Druids** don't call `hurt()` at all — Ranger spawns a real arrow entity, Druid
  throws a potion entity. There's no instant-damage call to redirect, so they're untouched and
  will keep fighting exactly as they do today. Giving them Epic Fight's bow-draw/aim animations
  would be a separate, purely cosmetic pass around the existing shoot call, not a damage bridge.
- **Non-guard citizens** are patched (so they'd get Epic Fight combat if they ever swing at
  something, e.g. barbarian events), but nothing currently drives that beyond the guard AI —
  worth checking whether `AbstractEntityCitizen` has its own self-defense fallback worth bridging
  too.
- **Renderer/visuals are unverified.** `CitizenPatchRenderer` follows Epic Fight's documented
  pattern exactly, and vanilla mobs keep their correct individual skins through this same path —
  but MineColonies' per-job clothing/skin system is more involved than a vanilla mob's single
  texture, and I haven't been able to confirm in-game that citizens keep their normal appearance
  once patched. Load a test world and compare a patched guard against an unpatched citizen before
  trusting this.
- **Mixin refmap**: this mixes into MineColonies source classes directly (not an obfuscated
  jar), on NeoForge's official-mappings userdev environment. That's normally fine without a
  refmap, but it's the first thing to check if the mixin config fails to apply at launch.

## Before it'll actually build

- `epicfight_version` / `minecolonies_version` in `gradle.properties` are placeholders — check
  Modrinth and `maven.ldtteam.com` for the current 1.21.1 builds and update them.
- I couldn't resolve or compile against Maven Central/Modrinth/LDTTeam's maven from this
  environment (network access here is restricted to a small allowlist that doesn't include
  them), so none of this has actually been run through a compiler. Treat it as a strong,
  API-accurate first draft rather than verified-working code — run `gradlew build` yourself and
  expect to fix a handful of import/signature issues on the first pass.

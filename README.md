# Fireball Launcher

Fabric mod for **Minecraft 1.21.11** that adds a **Fireball Launcher** item: a gunpowder-fueled, durability-limited tool that shoots player-owned fireballs with an **overheat** system. It is aimed at **mining and utility** (clearing soft terrain, digging tunnels), not endless spam or huge chain explosions.

## Requirements

- Minecraft `1.21.11`
- [Fabric Loader](https://fabricmc.net/) `0.18.6` or newer
- [Fabric API](https://modrinth.com/mod/fabric-api)
- Java `21` or newer

## Installation

Place the built jar from `build/libs/` (for example `fb_launcher-<version>.jar`) in your instance `mods` folder alongside Fabric API. The jar bundles **[Polymer](https://github.com/Patbox/polymer)** (core, **resource pack**, and **auto host**), so you do not need separate Polymer downloads for normal use.

### Joining a server without this mod installed (Polymer)

The launcher uses **Polymer** so **registry sync stays compatible** with clients that do not have `fb_launcher`:

- On the **server**, only the mod jar (with bundled Polymer) is required.
- **Players** can join with a normal **Fabric** setup (Loader + Fabric API) and **no** `fb_launcher` jar. They still need Fabric because the server is Fabric; **plain vanilla** clients cannot join a Fabric server at all.
- **Looks and name:** Polymer sends a **server resource pack** (via **Polymer Auto Host**) that includes this mod’s models, textures, and lang files. **Accept the server pack** when prompted so the launcher shows the **correct icon and item name** instead of a plain stick. The underlying packet item type is a **stick** with a custom **item model** pointing at `fb_launcher:item/fireball_launcher`; behavior stays server-driven.
- **Heat HUD**, extra enchantment description lines, and other code in `FbLauncherClient` **only run** if players also install `fb_launcher` on the client. Overheat titles and action bar feedback still work through vanilla packets.

Heat sync packets are only sent to clients that register the mod channel, so missing the client mod does not cause disconnects from that feature.

## Obtaining the item

Craft a **Fireball Launcher** with:

|   | Column 1 | Column 2 | Column 3 |
|---|----------|----------|----------|
| Row 1 | | Fire Charge | |
| Row 2 | Blaze Rod | Dried Ghast | Blaze Rod |
| Row 3 | | Blaze Rod | |

- **F** = Fire Charge  
- **B** = Blaze Rod  
- **G** = Dried Ghast  

Recipe id: `fb_launcher:fireball_launcher` (shaped crafting, equipment category).

## Quick usage

1. Hold the launcher in your **main hand** or **off hand**.
2. Carry **gunpowder** in your inventory (consumed per shot unless you are in Creative).
3. **Use** the item (right-click) to fire a fireball in the direction you are looking.
4. Watch **heat** on the HUD when holding the launcher if you use the **client** mod (see [Client HUD](#client-hud)). If you overheat, you cannot fire until the cooldown finishes (server enforces this either way).

**Creative mode:** no gunpowder cost and no durability loss. Heat and overheat still apply when you fire.

## Core stats (unenchanted)

These values are also summarized on the item tooltip under **When fired:**

| Stat | Value |
|------|--------|
| Gunpowder per shot | **6** (drawn from anywhere in the inventory) |
| Explosion power | **2** (vanilla fireball explosion strength; see [Hellfire](#hellfire)) |
| Heat per shot | **+40** |
| Overheat threshold | **160** heat |
| Launcher durability | **120** shots (1 damage per shot, Creative exempt) |

### Heat and overheat

- Each successful shot adds **40** heat. When heat would reach **≥ 160**, you **overheat**: heat resets to **0** for that lockout, and you get a short title and sound.
- **Overheat lockout** lasts **140 ticks** (**7 seconds**). During this time you cannot fire.
- Heat **decays** while you are not overheated: on average **10 heat per second** (half a heat unit per tick, implemented as **1** heat on alternating ticks).
- Rough burst behavior: you can land a few shots in a row; pushing heat to the threshold triggers lockout, so you cannot sustain very long volleys without pausing.

### Gunpowder and durability (with Lean Burn)

- Base cost is **6** gunpowder per shot.
- **[Lean Burn](#lean-burn)** reduces cost by **1 per level** (minimum **1** if levels were raised by commands or data packs):
  - Lean Burn I → **5** gunpowder  
  - Lean Burn II → **4** gunpowder  
- The launcher breaks after **120** uses in Survival (repair with anvil / mending as usual for a damageable item).

## Enchantments

All launcher enchantments apply only to the **Fireball Launcher** (`#fb_launcher:enchantable/fireball_launcher`). The item is **enchantable** like other gear (table, anvil, loot depending on your pack).

### Summary table

| Enchantment | Max level | Effect |
|-------------|-----------|--------|
| **Hellfire** | II | **+1 explosion power per level** (stacks on top of base **2**, so up to **4** at Hellfire II). |
| **Lean Burn** | II | **−1 gunpowder per shot per level** (from base **6**, floor **1**). |
| **Blast Shell** | IV | Less damage from **your own** launcher fireballs when you are the direct target (multiplier `max(0.25, 1 − 0.15 × level)`). |
| **Ghost Bolt** | I | Your launcher fireballs **do not collide with each other** (same owner), so volleys can pass through each other. |
| **Surgical Flame** | III | That shot’s explosion **does not break blocks** (the explosion’s block destruction list is cleared). In-game text also mentions entity-focused behavior; verify side effects such as fire with your setup. |
| **Pyroclast** | I | Fireball **water drag** uses a lighter drag value (**0.95**), so speed holds up better underwater. |
| **Iron Stance** | IV | Less **explosion knockback** on **you** (player) from **your own** launcher explosions (factor `max(0.2, 1 − 0.2 × min(level, 4))`). |

### Incompatibility (Hellfire vs Surgical Flame)

The mod treats enchantments as **destruction-oriented** vs **damage-oriented** for pairing rules:

- **Hellfire** is **destruction-only** (stronger blast).
- **Surgical Flame** is **damage-only** (blast tuned for entities, no block breaking from that effect).

You **cannot** put **Hellfire** and **Surgical Flame** on the **same** launcher. Other launcher enchantments are classified as both or neither for this check, so they are **not** blocked by this rule.

## Client HUD

When you hold the launcher (or still have residual heat / overheat), a small **heat bar** appears near the hotbar (aligned with the hunger column). It fills with **current heat** toward the overheat threshold; while overheated, it shows **remaining lockout** instead.

## Limitations and practical notes

- **Gunpowder** must be present in the inventory; if the shot fails for lack of gunpowder, you hear a dispenser fail click.
- **Broken** launcher (max damage) or **overheated** player cannot fire.
- Fireballs store launcher enchant data when fired; **changing** the stack in hand does not retroactively change projectiles already in the world.
- **Explosion power** scales with **Hellfire**; respect **griefing rules** and server blast protection on multiplayer.
- The mod uses **mixins** on explosion and projectile code; conflicts are possible if another mod patches the same areas.

## Development

Clone the repo and use Gradle:

```bash
./gradlew build
```

The remapped mod jar is written to `build/libs/`. For environment setup, see the [Fabric documentation](https://docs.fabricmc.net/develop/getting-started/setting-up-a-development-environment).

## License

CC0-1.0 (see `fabric.mod.json`).

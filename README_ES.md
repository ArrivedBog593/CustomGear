# UltimateCustomGear

[![CurseForge](https://cf.way2muchnoise.eu/versions/ultimate-custom-gear.svg)](https://www.curseforge.com/minecraft/mc-mods/ultimate-custom-gear)
[![CurseForge](https://cf.way2muchnoise.eu/ultimate-custom-gear.svg)](https://www.curseforge.com/minecraft/mc-mods/ultimate-custom-gear)

**UltimateCustomGear** es un mod de NeoForge para Minecraft 1.21.1 basado en datos, que permite a administradores de servidores, creadores de modpacks y jugadores agregar sets de armadura, armas, herramientas, comida, ítems, bloques y fluidos completamente personalizados — todo mediante simples archivos JSON. No se requiere programar.

[**Documentación**](docs/es/index.md) · [English](README.md)

---

## Instalación

1. Instala [NeoForge 1.21.1](https://neoforged.net/)
2. Coloca `ultimatecustomgear-1.x.x.jar` en tu carpeta `mods/`
3. Lanza el juego una vez para que se genere la carpeta `ultimatecustomgear/` dentro de `.minecraft/`
4. Agrega tus archivos JSON en `.minecraft/ultimatecustomgear/`, o coloca un `.zip` de contenido en `.minecraft/ultimatecustomgear/packs/`
5. Reinicia

## Un ítem completo

```json
{
  "id": "magic_apple",
  "type": "food",
  "names": { "es_mx": "Manzana Mágica" },
  "nutrition": 4,
  "saturation": 1.2,
  "always_edible": true,
  "mob_drops": {
    "entities": ["#minecraft:undead"],
    "chance": 0.15,
    "min": 1,
    "max": 2
  },
  "on_eat_effects": [
    { "effect": "minecraft:regeneration", "amplifier": 1, "duration": 10 }
  ],
  "texture": {
    "mode": "reference",
    "refs": { "item": "minecraft:item/golden_apple" }
  },
  "recipe": {
    "type": "shaped",
    "pattern": ["GGG", "GAG", "GGG"],
    "key": { "G": "minecraft:gold_block", "A": "minecraft:apple" }
  }
}
```

Ese es el archivo entero. Los no muertos ya lo sueltan, es fabricable, y JEI lo conoce.

## Documentación

|                                                                                                                           |                                                         |
|---------------------------------------------------------------------------------------------------------------------------|---------------------------------------------------------|
| [Primeros pasos](docs/es/getting-started.md)                                                                              | Instalación, dónde van los archivos, tipos de contenido |
| [Campos comunes](docs/es/common-fields.md)                                                                                | Campos compartidos, objeto de efecto, patrones de ID    |
| [Sets de armadura](docs/es/armor-sets.md) · [Armas](docs/es/weapons.md) · [Herramientas](docs/es/tools.md)                | Equipamiento                                            |
| [Comida](docs/es/food.md) · [Bloques](docs/es/blocks.md) · [Fluidos](docs/es/fluids.md)                                   | Todo lo demás                                           |
| [Texturas y modelos](docs/es/textures-and-models.md)                                                                      | Modos de textura, capas de armadura, GeckoLib           |
| [Recetas](docs/es/recipes.md) · [Tags](docs/es/tags.md)                                                                   | Crafteo e interoperabilidad                             |
| [Drops de mobs](docs/es/mob-drops.md) · [Resistencias de daño](docs/es/damage-resistances.md)                             | Sistemas de gameplay                                    |
| [Packs de contenido](docs/es/content-packs.md) · [Multijugador](docs/es/multiplayer.md) · [Comandos](docs/es/commands.md) | Servidores                                              |
| [Migración](docs/es/migration.md)                                                                                         | Cambios incompatibles, versión por versión              |

**Listas de referencia** — [Tipos de sonido de bloque](docs/es/block-sounds.md) · [Colores de mapa](docs/es/map-colors.md) · [Tipos de daño](docs/es/damage-types.md)

## Compatibilidad

- Minecraft 1.21.1 · NeoForge 21.1.x
- **JEI** — opcional, recomendado. Las recetas son completamente visibles
- **GeckoLib** — opcional, requerido solo para modelos de armadura en 3D
- Referencia modelos, usa ingredientes y etiqueta contenido de cualquier mod instalado
- Los encantamientos de otros mods funcionan automáticamente en ítems encantables
- El servidor y los clientes deben usar la misma versión del mod y archivos coincidentes (verificado al conectar)

## Licencia

Licencia MIT — ver [LICENSE](LICENSE) para más detalles.

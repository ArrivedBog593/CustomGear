# Campos comunes

| Campo            | Tipo    | Descripción                                                                                                                                                                                                                       |
|------------------|---------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `id`             | String  | Identificador único. Solo letras minúsculas, números y guiones bajos. 2–64 caracteres.                                                                                                                                            |
| `type`           | String  | Tipo de ítem (ver tabla de Tipos soportados)                                                                                                                                                                                      |
| `names`          | Map     | Nombre completo por idioma (solo para ítems individuales — los sets usan `piece_names`, `tool_names` o `weapon_names`)                                                                                                            |
| `enchantable`    | Boolean | Si el ítem puede ser encantado                                                                                                                                                                                                    |
| `enchantability` | Int     | Mayor = mejores encantamientos. Hierro = 9, Oro = 25, Diamante = 10                                                                                                                                                               |
| `tags`           | List    | Tags a los que pertenece este contenido, SIN `#` (p. ej. `["c:ingots", "c:ingots/ruby"]`). Permite que las recetas que aceptan `#ese_tag` lo usen. Ver [Tags](tags.md). Funciona en ítems, comida, bloques y fluidos (no en sets) |
| `fire_resistant` | Boolean | El ítem tirado sobrevive al fuego y la lava, como la netherita (en fluidos: la cubeta llena). No protege al portador del fuego. Requiere reiniciar                                                                                |

## Objeto de efecto

| Campo       | Tipo   | Descripción                                                                   |
|-------------|--------|-------------------------------------------------------------------------------|
| `effect`    | String | ID del efecto en formato `namespace:nombre_efecto` (ej. `minecraft:strength`) |
| `amplifier` | Int    | Nivel del efecto menos 1. `0` = Nivel I, `1` = Nivel II, etc.                 |

## Referencia de ID

| Tipo                      | Patrón de ID                        | Ejemplo                               |
|---------------------------|-------------------------------------|---------------------------------------|
| Piezas de set de armadura | `customgear:<id_set>_<pieza>`       | `customgear:mi_armadura_helmet`       |
| Herramientas de set       | `customgear:<id_set>_<herramienta>` | `customgear:mis_herramientas_pickaxe` |
| Armas de set              | `customgear:<id_set>_<arma>`        | `customgear:mis_armas_sword`          |
| Ítems individuales        | `customgear:<id>`                   | `customgear:mi_espada`                |
| Bloques                   | `customgear:<id>`                   | `customgear:mi_mineral`               |
| Cubetas de fluido         | `customgear:<id>_bucket`            | `customgear:mi_fluido_bucket`         |

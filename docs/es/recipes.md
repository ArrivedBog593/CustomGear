# Recetas

UltimateCustomGear soporta recetas de crafteo nativas definidas directamente en JSON. No se requieren mods externos.

## Tipos de receta

| Tipo                 | Descripción                                        |
|----------------------|----------------------------------------------------|
| `shaped`             | Mesa de crafteo con un patrón específico           |
| `shapeless`          | Mesa de crafteo, ingredientes en cualquier orden   |
| `smelting`           | Horno                                              |
| `blasting`           | Alto horno                                         |
| `smoking`            | Ahumador                                           |
| `campfire_cooking`   | Fogata                                             |
| `stonecutting`       | Cortapiedras                                       |
| `smithing_transform` | Mesa de herrería (plantilla + base + adición)      |
| `passthrough`        | Tipo de receta de otro mod, copiado tal cual       |

## Tags como ingredientes

Cualquier casilla de ingrediente acepta un **tag** con el prefijo `#` — la
receta aceptará cualquier ítem de ese tag:

```json
{
  "key": {
    "P": "#minecraft:planks",
    "I": "#c:ingots/iron",
    "S": "minecraft:stick"
  }
}
```

Funciona en todos los tipos de receta que este mod construye. Dentro de `passthrough` los tags se escriben como los espera el mod objetivo (normalmente `{"tag": "c:ingots/gold"}`), no con el atajo `#` — ese cuerpo se copia tal cual y nunca se traduce. Las recetas con errores estructurales (filas de patrones desiguales, símbolos sin definir, claves sin usar, ID mal formados) se omiten con un mensaje detallado en el log que nombra el ítem y el problema exacto.

## Receta de ítem individual

```json
{
  "recipe": {
    "type": "shaped",
    "pattern": [
      " G ",
      " G ",
      " S "
    ],
    "key": {
      "G": "mimod:mi_gema",
      "S": "minecraft:stick"
    }
  }
}
```

Para múltiples recetas, usa un array:

```json
{
  "recipe": [
    {
      "type": "shaped",
      ...
    },
    {
      "type": "smelting",
      "ingredient": "mimod:mi_mineral",
      "experience": 1.0,
      "cooking_time": 10
    }
  ]
}
```

## Receta de set (armadura, herramientas, armas)

```json
{
  "recipes": {
    "helmet": {
      "type": "shaped",
      "pattern": [
        "GGG",
        "G G",
        "   "
      ],
      "key": {
        "G": "mimod:mi_gema"
      }
    },
    "chestplate": {
      "type": "shaped",
      "pattern": [
        "G G",
        "GGG",
        "GGG"
      ],
      "key": {
        "G": "mimod:mi_gema"
      }
    },
    "leggings": {
      "type": "shaped",
      "pattern": [
        "GGG",
        "G G",
        "G G"
      ],
      "key": {
        "G": "mimod:mi_gema"
      }
    },
    "boots": {
      "type": "shaped",
      "pattern": [
        "   ",
        "G G",
        "G G"
      ],
      "key": {
        "G": "mimod:mi_gema"
      }
    }
  }
}
```

## Receta de mesa de herrería

```json
{
  "recipe": {
    "type": "smithing_transform",
    "template": "minecraft:netherite_upgrade_smithing_template",
    "base": "mimod:mi_espada_diamante",
    "addition": "minecraft:netherite_ingot"
  }
}
```

> Los ingredientes soportan cualquier ítem de cualquier mod instalado mediante resource location (ej. `"otromod:lingote_especial"`).

## Recetas passthrough (otros mods)

`passthrough` entrega un cuerpo de receta crudo al juego sin que este mod lo
interprete, para que el tipo de receta de otro mod pueda producir tu contenido:

```json
{
  "recipe": {
    "type": "passthrough",
    "json": {
      "type": "create:mixing",
      "heat_requirement": "heated",
      "ingredients": [
        { "item": "minecraft:amethyst_shard" },
        { "tag": "c:ingots/gold" }
      ],
      "results": [
        { "count": 1, "id": "customgear:mi_gema" }
      ]
    }
  }
}
```

Todo lo que hay dentro de `json` se copia exactamente como está escrito,
incluida la forma de expresar los tags — `{"tag": "c:ingots/gold"}` en el caso
de Create, no el atajo `#` de este mod. Escríbelo como lo documente ese mod;
este mod no traduce ni valida el esquema, porque hacerlo significaría
conocerlo.

Que un campo concreto acepte tags o no lo decide ese mod, no este. La mayoría
usa el sistema `Ingredient` de vanilla y acepta tags en cualquier sitio donde
vaya un ítem, pero un campo que espere un ID literal lo rechazará — y el error
vendrá de su deserializador, no de aquí.

La condición `neoforge:mod_loaded` se deduce del namespace del `type` interno,
así que la receta se omite cuando ese mod no está en vez de romper el
datapack. Para recetas que abarquen varios mods, lista los extras:

```json
{
  "type": "passthrough",
  "requires": ["create", "createaddition"],
  "json": { "type": "create:mixing", "...": "..." }
}
```

**El resultado no es de este mod.** Todos los demás tipos construyen el
resultado a partir del ítem dueño de la receta; aquí el resultado vive dentro
de tu `json`, con la forma que use ese mod. Una receta passthrough colgada de
`ruby_gem` puede producir cualquier otra cosa.

> ⚠️ `mod_loaded` protege contra que el mod **falte**, no contra que sea otra
> **versión**. Los mods cambian sus esquemas de receta entre versiones, y un
> deserializador que falle puede tumbar la carga del datapack entera — no solo
> esa receta. Revisa tus recetas passthrough cuando actualices el mod objetivo.

## Campos de receta

| Campo          | Tipo         | Descripción                                                                                                         |
|----------------|--------------|---------------------------------------------------------------------------------------------------------------------|
| `recipe`       | Objeto/Array | Receta para ítems individuales. Puede ser un objeto o un array para varias recetas                                  |
| `recipes`      | Mapa         | Recetas para sets. Una entrada por pieza/herramienta/arma                                                           |
| `type`         | String       | Ver la tabla de [tipos de receta](#tipos-de-receta)                                                                 |
| `pattern`      | String[]     | (shaped) 1–3 filas de hasta 3 caracteres cada una                                                                   |
| `key`          | Mapa         | (shaped) Asocia cada carácter del patrón a un ID de ítem                                                            |
| `ingredients`  | String[]     | (shapeless) Lista de IDs de ítem                                                                                    |
| `ingredient`   | String       | (cocción/stonecutting) ID del ítem de entrada                                                                       |
| `experience`   | Float        | (cocción) XP otorgada al completarse. Por defecto: 0.1                                                              |
| `cooking_time` | Float        | (cocción) **SEGUNDOS** de cocción. Admite decimales. Por defecto: 10s horno, 5s alto horno, 5s ahumador, 30s fogata |
| `template`     | String       | (smithing_transform) ID de la plantilla                                                                             |
| `base`         | String       | (smithing_transform) ID del ítem base a mejorar                                                                     |
| `addition`     | String       | (smithing_transform) ID del material de mejora                                                                      |
| `json`         | Objeto       | (passthrough) Cuerpo de receta crudo, copiado tal cual                                                              |
| `requires`     | String[]     | (passthrough) IDs de mods extra además del deducido del tipo interno                                                |
| `result_count` | Int          | Cantidad de ítems producidos. Por defecto: 1. Aplica a shaped, shapeless y stonecutting                             |

> 💥 **`cooking_time` cambió en la 1.6.0.** Antes estaba en ticks. Divide tus
> valores actuales entre 20 — una receta escrita como `200` significaba 10
> segundos y ahora significa 200 segundos. No falla nada, la receta simplemente
> va 20 veces más lenta.

Declarar un campo que el tipo ignora (como `experience` en una receta de
stonecutting) genera un aviso en el log nombrándolo. La receta sigue
funcionando; el valor simplemente no hace nada.

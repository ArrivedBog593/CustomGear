# Texturas y modelos

Hay tres modos de textura disponibles:

| Modo        | Descripción                                                                |
|-------------|----------------------------------------------------------------------------|
| `default`   | Usa texturas de hierro/madera de vanilla como placeholders                 |
| `reference` | Reutiliza el modelo de otro ítem (vanilla o de otro mod)                   |
| `custom`    | Usa tus propios archivos PNG colocados en la carpeta `ultimatecustomgear/` |

> ⚠️ **`reference` crea una dependencia dura.** Las resource locations apuntan a
> archivos que pertenecen a otro mod, así que ese mod pasa a ser **obligatorio**
> para que tu contenido se vea bien. Sin él, el ítem muestra el cuadriculado de
> textura faltante, y un modelo de armadura 3D declarado así simplemente no se
> renderiza. Las referencias a vanilla (`minecraft:...`) siempre son seguras. Si
> quieres que tu pack se sostenga solo, usa `custom` y mete los PNG dentro.

## Ejemplo modo reference

```json
{
  "texture": {
    "mode": "reference",
    "refs": {
      "sword": "minecraft:item/netherite_sword"
    }
  }
}
```

Para arcos y ballestas, opcionalmente puedes incluir modelos de frames de tensado/carga personalizados:

```json
{
  "texture": {
    "mode": "reference",
    "refs": {
      "bow": "otromod:item/arco_epico",
      "bow_pulling_0": "otromod:item/arco_epico_pulling_0",
      "bow_pulling_1": "otromod:item/arco_epico_pulling_1",
      "bow_pulling_2": "otromod:item/arco_epico_pulling_2"
    }
  }
}
```

## Campos de textura

| Campo                | Tipo   | Descripción                                                                                                                                                                                                                       |
|----------------------|--------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `texture.mode`       | String | `default`, `custom`, o `reference`                                                                                                                                                                                                |
| `texture.refs`       | Map    | Para `custom`: ruta relativa a un PNG dentro de `ultimatecustomgear/`. Para `reference`: resource location completo del modelo de otro mod                                                                                        |
| `armor_layers`       | Map    | (Solo sets de armadura) Rutas `layer_1` y `layer_2` para la textura de la armadura puesta. El valor especial `"transparent"` hace la armadura invisible al vestirla (stats y efectos intactos); en modo reference pon ambas capas |
| `texture.refs.block` | String | (Bloques simples) Resource location aplicada a las 6 caras mediante `cube_all`                                                                                                                                                    |
| `texture.faces`      | Objeto | (Solo bloques) Textura por cara. Claves: `top`, `bottom`, `north`, `south`, `east`, `west`, `side`                                                                                                                                |
| `texture.faces.side` | String | Atajo: aplica a `north`, `south`, `east`, `west` si no están definidas individualmente                                                                                                                                            |

> **`refs` vs `faces`:** `refs` lo maneja todo — una textura única con la clave `all` (`"refs": { "all": "..." }`), o texturas por cara con las claves de cara (`top`, `bottom`, `north`, `south`, `east`, `west`, `side`). `faces` es un alias legacy que solo sirve para texturas por cara. Usa `refs`. (La clave `block` es un alias legacy de `all`.) Nota: `all`/`block` solo funcionan dentro de `refs`, nunca dentro de `faces`.

## Modelos de Armadura 3D (GeckoLib)

Con [GeckoLib](https://www.curseforge.com/minecraft/mc-mods/geckolib) instalado,
las armaduras pueden renderizarse como un modelo 3D completo al vestirlas
—cuernos, hombreras, capas, incluso animaciones— en vez de las capas planas
de vanilla.

Modela tu armadura en [Blockbench](https://www.blockbench.net/) con el formato
**GeckoLib Animated Model**, usando los nombres de hueso estándar de armadura
(`armorHead`, `armorBody`, `armorRightArm`, `armorLeftArm`, `armorRightLeg`,
`armorLeftLeg`, `armorRightBoot`, `armorLeftBoot`), y declara los archivos
exportados en `texture.armor_3d`:

```json
{
  "texture": {
    "mode": "custom",
    "refs": {
      "helmet": "textures/mi_casco_icono.png",
      "chestplate": "textures/mi_pechera_icono.png",
      "leggings": "textures/mis_pantalones_icono.png",
      "boots": "textures/mis_botas_icono.png"
    },
    "armor_layers": {
      "layer_1": "textures/mi_armadura_layer_1.png",
      "layer_2": "textures/mi_armadura_layer_2.png"
    },
    "armor_3d": {
      "model": "models/mi_armadura.geo.json",
      "texture": "textures/mi_armadura_3d.png",
      "animation": "models/mi_armadura.animation.json"
    }
  }
}
```

| Campo       | Obligatorio | Descripción                                                        |
|-------------|-------------|--------------------------------------------------------------------|
| `model`     | Sí          | El `.geo.json` exportado desde Blockbench                          |
| `texture`   | Sí          | PNG pintado para las UV de ese modelo (no es el ícono ni una capa) |
| `animation` | No          | `.animation.json`; sin él el modelo es estático                    |

**Cómo se combinan los tres sistemas de textura:**

| Declarado                                  | Con GeckoLib  | Sin GeckoLib                |
|--------------------------------------------|---------------|-----------------------------|
| Solo `armor_layers`                        | Capas planas  | Capas planas                |
| `armor_layers` + `armor_3d`                | **Modelo 3D** | Capas planas (respaldo)     |
| Solo `armor_3d`                            | **Modelo 3D** | Capas de hierro vanilla     |
| `armor_layers: "transparent"` + `armor_3d` | **Modelo 3D** | Armadura invisible          |

> Declara siempre `armor_layers` junto a `armor_3d` — es tu red de seguridad
> para instancias sin GeckoLib. Declarar solo `armor_3d` no rompe nada, pero la
> armadura cae a las capas de hierro vanilla, que casi nunca es lo que quieres.
> `refs` sigue controlando el ícono del inventario, que siempre es 2D. El modelo
> 3D reemplaza a las capas cuando está activo; nunca se dibujan juntos.

**Modos:** en modo `custom` los tres valores son rutas a tus propios archivos
(carpeta de config o zips de packs, como cualquier otra textura custom). En
modo `reference` son resource locations de assets de otro mod
(p. ej. `"otromod:geo/armor/su_armadura.geo.json"`) — no se copia nada, pero
**ese mod pasa a ser obligatorio** para que tu armadura se vea.

GeckoLib es una dependencia opcional: el mod funciona sin él.
El render 3D se fija al registrar — agregar o quitar `armor_3d` requiere
reiniciar.

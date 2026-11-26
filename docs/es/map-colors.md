# Colores de mapa

Lista completa de los valores disponibles para el campo `map_color` en los JSON de bloque.

> **Uso:** `"map_color": "grass"`
> Si el valor no se reconoce o se omite, se usa `stone`.

---

## Colores básicos

| Valor    | Descripción                      |
|----------|----------------------------------|
| `none`   | Sin color de mapa / transparente |
| `grass`  | Pasto                            |
| `sand`   | Arena                            |
| `wool`   | Lana                             |
| `fire`   | Fuego                            |
| `ice`    | Hielo                            |
| `metal`  | Metal                            |
| `plant`  | Planta                           |
| `snow`   | Nieve                            |
| `clay`   | Arcilla                          |
| `dirt`   | Tierra                           |
| `stone`  | Piedra                           |
| `water`  | Agua                             |
| `wood`   | Madera                           |
| `quartz` | Cuarzo                           |

---

## Colores de tinte estándar

| Valor         | Descripción   |
|---------------|---------------|
| `orange`      | Naranja       |
| `magenta`     | Magenta       |
| `light_blue`  | Azul claro    |
| `yellow`      | Amarillo      |
| `light_green` | Verde claro   |
| `pink`        | Rosa          |
| `gray`        | Gris          |
| `light_gray`  | Gris claro    |
| `cyan`        | Cian          |
| `purple`      | Morado        |
| `blue`        | Azul          |
| `brown`       | Café          |
| `green`       | Verde         |
| `red`         | Rojo          |
| `black`       | Negro         |

---

## Colores especiales

| Valor     | Descripción                    |
|-----------|--------------------------------|
| `gold`    | Oro                            |
| `diamond` | Diamante                       |
| `lapis`   | Lapislázuli                    |
| `emerald` | Esmeralda                      |
| `podzol`  | Podzol / estilo tierra gruesa  |
| `nether`  | Nether / rojo oscuro           |

---

## Colores de terracota

| Valor                    | Descripción                 |
|--------------------------|-----------------------------|
| `terracotta_white`       | Terracota blanca            |
| `terracotta_orange`      | Terracota naranja           |
| `terracotta_magenta`     | Terracota magenta           |
| `terracotta_light_blue`  | Terracota azul claro        |
| `terracotta_yellow`      | Terracota amarilla          |
| `terracotta_light_green` | Terracota verde claro       |
| `terracotta_pink`        | Terracota rosa              |
| `terracotta_gray`        | Terracota gris              |
| `terracotta_light_gray`  | Terracota gris claro        |
| `terracotta_cyan`        | Terracota cian              |
| `terracotta_purple`      | Terracota morada            |
| `terracotta_blue`        | Terracota azul              |
| `terracotta_brown`       | Terracota café              |
| `terracotta_green`       | Terracota verde             |
| `terracotta_red`         | Terracota roja              |
| `terracotta_black`       | Terracota negra             |

---

## Nether y biomas específicos

| Valor               | Descripción                       |
|---------------------|-----------------------------------|
| `crimson_nylium`    | Nylium carmesí                    |
| `crimson_stem`      | Tallo carmesí                     |
| `crimson_hyphae`    | Hifas carmesí                     |
| `warped_nylium`     | Nylium distorsionado              |
| `warped_stem`       | Tallo distorsionado               |
| `warped_hyphae`     | Hifas distorsionadas              |
| `warped_wart_block` | Bloque de verrugas distorsionadas |
| `deepslate`         | Pizarra profunda                  |
| `raw_iron`          | Hierro en bruto                   |
| `glow_lichen`       | Liquen luminoso                   |

---

## Notas

- `none` sirve cuando quieres que el bloque no aporte un tinte visible al mapa.
- `water` se usa comúnmente para bloques que parecen líquidos.
- Los valores `terracotta_*` sirven para bloques de construcción custom que deban combinar con los tonos de mapa de la terracota teñida.
- Si quieres un comportamiento de respaldo en código, usa `MapColor.NONE`.

---

## Ejemplo JSON sugerido

```json
{
  "map_color": "terracotta_red"
}
```

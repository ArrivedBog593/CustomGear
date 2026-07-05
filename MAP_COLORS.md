# Map Color Types

Complete list of available values for the `map_color` field in block JSON files.

> **Usage:** `"map_color": "grass"`  
> If the value is not recognized or omitted, defaults to `stone`.

---

## Basic Colors

| Value    | Description                |
|----------|----------------------------|
| `none`   | No map color / transparent |
| `grass`  | Grass                      |
| `sand`   | Sand                       |
| `wool`   | Wool                       |
| `fire`   | Fire                       |
| `ice`    | Ice                        |
| `metal`  | Metal                      |
| `plant`  | Plant                      |
| `snow`   | Snow                       |
| `clay`   | Clay                       |
| `dirt`   | Dirt                       |
| `stone`  | Stone                      |
| `water`  | Water                      |
| `wood`   | Wood                       |
| `quartz` | Quartz                     |

---

## Standard Dye Colors

| Value         | Description |
|---------------|-------------|
| `orange`      | Orange      |
| `magenta`     | Magenta     |
| `light_blue`  | Light blue  |
| `yellow`      | Yellow      |
| `light_green` | Light green |
| `pink`        | Pink        |
| `gray`        | Gray        |
| `light_gray`  | Light gray  |
| `cyan`        | Cyan        |
| `purple`      | Purple      |
| `blue`        | Blue        |
| `brown`       | Brown       |
| `green`       | Green       |
| `red`         | Red         |
| `black`       | Black       |

---

## Special Colors

| Value     | Description                |
|-----------|----------------------------|
| `gold`    | Gold                       |
| `diamond` | Diamond                    |
| `lapis`   | Lapis lazuli               |
| `emerald` | Emerald                    |
| `podzol`  | Podzol / coarse dirt style |
| `nether`  | Nether / dark red          |

---

## Terracotta Colors

| Value                    | Description            |
|--------------------------|------------------------|
| `terracotta_white`       | White terracotta       |
| `terracotta_orange`      | Orange terracotta      |
| `terracotta_magenta`     | Magenta terracotta     |
| `terracotta_light_blue`  | Light blue terracotta  |
| `terracotta_yellow`      | Yellow terracotta      |
| `terracotta_light_green` | Light green terracotta |
| `terracotta_pink`        | Pink terracotta        |
| `terracotta_gray`        | Gray terracotta        |
| `terracotta_light_gray`  | Light gray terracotta  |
| `terracotta_cyan`        | Cyan terracotta        |
| `terracotta_purple`      | Purple terracotta      |
| `terracotta_blue`        | Blue terracotta        |
| `terracotta_brown`       | Brown terracotta       |
| `terracotta_green`       | Green terracotta       |
| `terracotta_red`         | Red terracotta         |
| `terracotta_black`       | Black terracotta       |

---

## Nether & Biome-Specific

| Value               | Description       |
|---------------------|-------------------|
| `crimson_nylium`    | Crimson nylium    |
| `crimson_stem`      | Crimson stem      |
| `crimson_hyphae`    | Crimson hyphae    |
| `warped_nylium`     | Warped nylium     |
| `warped_stem`       | Warped stem       |
| `warped_hyphae`     | Warped hyphae     |
| `warped_wart_block` | Warped wart block |
| `deepslate`         | Deepslate         |
| `raw_iron`          | Raw iron          |
| `glow_lichen`       | Glow lichen       |

---

## Notes

- `none` is useful when you want the block to not contribute a visible map tint.
- `water` is commonly used for liquid-like blocks.
- `terracotta_*` values are useful for custom-building blocks that should match dyed terracotta map tones.
- If you want a fallback behavior in code, use `MapColor.NONE`.

---

## Suggested JSON Example

```json
{
  "map_color": "terracotta_red"
}
```
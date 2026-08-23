# Primeros pasos

## Instalación

1. Descarga e instala [NeoForge 26.1.2](https://neoforged.net/)
2. Coloca `ultimatecustomgear-1.x.x.jar` en tu carpeta `mods/`
3. Lanza el juego una vez para que se genere la carpeta `ultimatecustomgear/` dentro de `.minecraft/`
4. Agrega tus archivos JSON en `.minecraft/ultimatecustomgear/` — o coloca un `.zip` de contenido en `.minecraft/ultimatecustomgear/packs/` (ver [Packs de contenido](content-packs.md))
5. Reinicia el juego

> **Nota de sintaxis JSON:** El JSON estándar no permite comas al final del último elemento. Una coma sobrante al final de un objeto o lista hará que el archivo sea ignorado silenciosamente al cargar.

## Estructura de archivos JSON

Todos los archivos JSON van dentro de `.minecraft/ultimatecustomgear/`. Cada archivo define un ítem, set, bloque o fluido. Los archivos pueden organizarse en cualquier estructura de subcarpetas.

El contenido también puede empaquetarse como archivos `.zip` dentro de `packs/` — ver [Packs de contenido](content-packs.md).

### Tipos soportados

| Tipo         | Descripción                                                      |
|--------------|------------------------------------------------------------------|
| `armor_set`  | Set de armadura completo (casco, pechera, pantalones, botas)     |
| `tool_set`   | Set de herramientas completo (pico, hacha, pala, azadón)         |
| `weapon_set` | Set de armas completo (espada, arco, ballesta, escudo)           |
| `sword`      | Espada individual                                                |
| `bow`        | Arco individual                                                  |
| `crossbow`   | Ballesta individual                                              |
| `shield`     | Escudo individual                                                |
| `pickaxe`    | Pico individual                                                  |
| `axe`        | Hacha individual                                                 |
| `shovel`     | Pala individual                                                  |
| `hoe`        | Azadón individual                                                |
| `food`       | Ítem comestible                                                  |
| `item`       | Ítem simple no consumible                                        |
| `block`      | Bloque con texturas por cara opcionales y colocación direccional |
| `fluid`      | Fluido con cubeta                                                |

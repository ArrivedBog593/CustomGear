# Primeros pasos

## Instalación

1. Instala [NeoForge](https://neoforged.net/) o [Fabric](https://fabricmc.net/) (con [Fabric API](https://www.curseforge.com/minecraft/mc-mods/fabric-api)) para Minecraft 1.21.1
2. Coloca el jar de tu loader, `customgear-neoforge-1.x.x+mc1.21.1.jar` o `customgear-fabric-1.x.x+mc1.21.1.jar`, en tu carpeta `mods/`
3. Lanza el juego una vez para que se genere la carpeta `customgear/` dentro de `.minecraft/`
4. Agrega tus archivos JSON en `.minecraft/customgear/` — o coloca un `.zip` de contenido en `.minecraft/customgear/packs/` (ver [Packs de contenido](content-packs.md))
5. Reinicia el juego

> **Nota de sintaxis JSON:** El JSON estándar no permite comas al final del último elemento. Una coma sobrante al final de un objeto o lista hará que el archivo sea ignorado silenciosamente al cargar.

## Estructura de archivos JSON

Todos los archivos JSON van dentro de `.minecraft/customgear/`. Cada archivo define un ítem, set, bloque o fluido. Los archivos pueden organizarse en cualquier estructura de subcarpetas.

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

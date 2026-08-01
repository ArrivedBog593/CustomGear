# Multijugador

**El cliente y el servidor deben tener los mismos archivos de contenido.** Los stats de los ítems se fijan al arrancar el juego, así que archivos diferentes causan desincronizaciones invisibles (tu tooltip dice un daño y el servidor aplica otro). Para prevenirlo, el mod verifica el contenido al conectar comparando hashes — el empaque no importa: un servidor usando un zip coincide con clientes usando los mismos JSON sueltos, y viceversa.

Lo que ocurre ante una diferencia se configura **en el servidor** en `config/ultimatecustomgear-common.toml`:

| Modo                    | Comportamiento                                                                                                                                                                                                                                                                       |
|-------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `ENFORCE` (por defecto) | El cliente es desconectado con un mensaje que muestra ambos hashes y cómo corregirlo. Garantiza que todos los jugadores vean stats, nombres y recetas correctos.                                                                                                                     |
| `WARN`                  | El cliente puede entrar; el servidor registra la diferencia y el jugador recibe un aviso en el chat de que tooltips/nombres pueden no coincidir con los valores reales (el combate siempre usa los del servidor). Útil mientras distribuyes un zip actualizado sin expulsar a todos. |
| `OFF`                   | Sin verificación. Los clientes a los que les faltan ítems igual no pueden entrar (esa es la verificación propia de Minecraft), pero las diferencias de stats pasan desapercibidas.                                                                                                   |

Solo importa el valor del servidor — la config local del cliente no tiene efecto al conectarse a un servidor.

**Distribuir contenido:** empaca tus JSON (y texturas) de `ultimatecustomgear` en un zip, compártelo, y que tus jugadores lo coloquen en su carpeta `.minecraft/ultimatecustomgear/packs/`.

> Ambos lados deben usar la misma versión del mod: los clientes 1.3.0 no pueden entrar a servidores anteriores y viceversa (cambio de protocolo de red).

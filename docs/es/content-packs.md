# Packs de contenido

Puedes distribuir tu contenido como un solo archivo **.zip** en vez de archivos sueltos. Coloca el zip en:

```
.minecraft/ultimatecustomgear/packs/tu-contenido.zip
```

Los JSON y texturas dentro del zip cargan exactamente igual que los archivos sueltos — las subcarpetas dentro del zip funcionan sin problema. Es la forma recomendada para que los dueños de servidor compartan contenido con sus jugadores: un solo archivo, imposible de descomprimir a medias o editar por accidente.

**Reglas:**
- Los zips solo se cargan desde la subcarpeta `packs/`. Un zip en cualquier otro lugar se ignora (con un mensaje en el log indicando a dónde moverlo).
- **Solo se admite `.zip`.** Si tienes un `.rar` o `.7z`, recomprímelo como zip: en Windows, selecciona los archivos → clic derecho → Enviar a → Carpeta comprimida.
- **Los archivos sueltos ganan sobre los zips.** Si un JSON suelto define el mismo `id` que uno dentro de un zip, se usa el archivo suelto por completo (stats, texturas, recetas) y la entrada del zip se descarta con un mensaje en el log. Puedes usar esto para sobreescribir localmente un ítem específico de un pack sin tocar el zip.
- Se permiten varios zips; cargan en orden alfabético.
- `/customgear reload` detecta zips agregados o actualizados sin reiniciar.

> Un pack distribuible debe ser **autocontenido**: cada textura que un JSON referencie debe estar dentro del mismo zip. Referenciar una textura suelta desde un JSON dentro de un zip funciona localmente, pero los jugadores que solo reciban el zip no tendrán el archivo suelto. (Los archivos sueltos siguen teniendo prioridad sobre el contenido del zip con la misma ruta — útil para ajustes locales.)

## Icono del pack

El pack dinámico muestra el logo del mod en la pantalla de paquetes de
recursos. Para usar el tuyo, pon un `dynamic_pack_icon.png` en la carpeta
`ultimatecustomgear/` — cuadrado y potencia de dos (64×64 o 128×128). Se
recoge con `/customgear reload`, sin reiniciar.

El icono queda deliberadamente fuera del hash de contenido, así que
personalizar tu pack nunca desincroniza a los clientes.

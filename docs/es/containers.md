# Contenedores

Un contenedor es almacenamiento: barriles, cofres, shulkers y mochilas.
Comparten inventario, buscador y botón de ordenar, y se diferencian en la forma,
en cómo se colocan y en qué pasa al romper uno.

```json
{
  "id": "ruby_barrel",
  "type": "container",
  "names": { "en_us": "Ruby Barrel", "es_mx": "Barril de Rubí" },
  "container": {
    "type": "barrel",
    "slots": 45
  }
}
```

## Los cuatro tipos

`container.type` es **obligatorio**. Los cuatro difieren en si al romperlos
conservan el contenido, y un valor por defecto decidiría eso a tus espaldas.

| Tipo       | Forma                                       | Se coloca                        | Conserva contenido |
|------------|---------------------------------------------|----------------------------------|--------------------|
| `barrel`   | Un cubo completo, con estado de apertura    | Mirando a cualquiera de 6 lados  | No                 |
| `chest`    | El cofre de vanilla, con tapa animada       | En horizontal; se une en pareja  | No                 |
| `shulker`  | Se pega a una superficie, la tapa sale      | En la cara que clicas            | Sí                 |
| `backpack` | No es un bloque — un objeto que llevas      | —                                | Sí                 |

Dibujar un barril no cuesta nada: todo su aspecto es JSON generado. Un cofre y un
shulker necesitan un renderer, que es lo que les da su animación.

## Campos

| Campo                                | Tipo     | Por defecto     | Descripción                                                                        |
|--------------------------------------|----------|-----------------|------------------------------------------------------------------------------------|
| `container.type`                     | Texto    | **obligatorio** | `barrel`, `chest`, `shulker` o `backpack`                                          |
| `container.slots`                    | Número   | **obligatorio** | Slots totales, mínimo 1. No es filas × columnas — la última fila puede ir a medias |
| `container.columns`                  | Número   | automático      | Slots por fila. Solo presentación                                                  |
| `container.max_columns`              | Número   | `12`            | Techo del ancho automático. Se ignora si declaras `columns`                        |
| `container.keeps_contents`           | Booleano | según el tipo   | Si al romperlo el contenido se guarda en el objeto que suelta                      |
| `container.openable_when_obstructed` | Booleano | según el tipo   | Si se abre con un bloque sólido estorbando                                         |
| `container.curios_slots`             | Lista    | vacía           | (Solo mochilas) Tipos de slot de Curios donde se puede equipar                     |

### Número de slots

No hay tope duro, pero pasando de **128** el parser avisa. Ahí es donde el
paquete de clic de vanilla se rinde: una sola acción que cambie más slots que eso
a la vez —un arrastre largo, un shift+clic masivo— desconecta al jugador con un
error de codificación. Difícil de alcanzar en supervivencia, trivial en creativo.

`slots` se fija al registrar el contenedor, así que cambiarlo requiere
**reiniciar**, no `/customgear reload`. Reducir un contenedor que ya guarda más
de lo que cabe suelta el excedente al suelo; ampliarlo simplemente añade slots
vacíos al final.

### Número de columnas

Omite `columns` y el ancho se calcula para que el contenido quepa en nueve filas,
con techo en `max_columns`:

| Slots | Columnas | Filas      |
|-------|----------|------------|
| 45    | 9        | 5          |
| 81    | 9        | 9          |
| 108   | 12       | 9          |
| 200   | 12       | con scroll |

Declara `columns` y se usa ese ancho exacto, por muy alto que quede el resultado.

No hay desplazamiento horizontal, así que un ancho demasiado grande para la
ventana del jugador no puede dibujarse sin más — la pantalla reorganiza los
mismos slots en menos columnas. Ningún slot queda inaccesible, pero un contenedor
muy ancho se ve distinto en una ventana pequeña de como lo diseñaste.

### Conservar el contenido

`keeps_contents` decide qué pasa al romperlo. Por defecto `true` para shulkers y
mochilas, `false` para barriles y cofres — pero es tuyo, y un barril que conserva
su contenido es perfectamente válido.

Una mochila no puede desactivarlo: no tiene bloque donde guardar un inventario,
así que el contenido vive en el objeto o en ninguna parte.

Un contenedor que conserva su contenido no se apila, por la misma razón que la
caja de shulker de vanilla: apilar dos las fundiría en un solo inventario.

## Cofres

Dos cofres colocados uno al lado del otro, mirando en la misma dirección, se unen
en un doble con el doble de slots. Agáchate al colocarlos para mantenerlos
separados.

La pareja mantiene **un solo** inventario en vez de dos. Eso es invisible al
jugar —romper cualquiera de las mitades devuelve el contenido de esa mitad
exactamente como en vanilla, y copiar una con Ctrl+clic central en creativo copia
solo su mitad— pero es lo que hace que tolvas, comparadores y superposiciones de
otros mods lean lo mismo desde ambos lados.

Un cofre que **conserva su contenido no puede unirse en doble**: no habría
respuesta a qué mitad lleva el objeto soltado. Pon `keeps_contents` en un cofre y
se queda siempre solo.

Unir o separar una pareja cambia el tamaño del inventario, así que a quien tenga
la pantalla abierta se le cierra. Volver a abrirla es un clic; una ventana cuyos
slots ya no cuadran sería un informe de error.

## Shulkers

Un shulker se pega a la superficie contra la que lo coloques y su tapa sale por
esa cara. Ponlo en el suelo y se abre hacia arriba; pégalo a una pared y se abre
hacia ti.

Su caja de colisión **crece mientras la tapa sube**, así que empuja a las
entidades que estorben y se niega a abrir si no hay sitio. Eso no es solo
fidelidad: contra un bloque sólido no habría a dónde empujarlas.

## Mochilas

Una mochila se lleva encima en vez de colocarse. Ábrela con clic derecho, o con
la tecla **Abrir mochila** (B por defecto) desde cualquier parte del inventario.

La tecla busca en este orden:

1. El slot activo de la barra rápida
2. La mano izquierda
3. Los slots de Curios equipados
4. El resto del inventario, empezando por la barra

Lo equipado gana sobre lo suelto a propósito — quien lleva un anillo de
almacenamiento puesto lo lleva para tenerlo a mano.

Mientras su pantalla está abierta, el slot que la contiene queda bloqueado, así
que no se puede arrastrar fuera ni tirar desde su propio inventario. Si aun así
desaparece —se la lleva otro jugador, la saca una tolva, la pierdes al morir— la
pantalla se cierra sola.

### Slots de Curios

`curios_slots` lista qué tipos de slot de Curios aceptan esta mochila:

```json
{
  "container": {
    "type": "backpack",
    "slots": 27,
    "curios_slots": ["back", "charm"]
  }
}
```

Curios trae diez tipos de slot: `back`, `belt`, `body`, `bracelet`, `charm`,
`curio`, `hands`, `head`, `necklace` y `ring`. Puede haber otros si algún mod los
añade, así que un nombre no reconocido se avisa en vez de rechazarse.

Los tipos de slot que declares **también se le asignan al jugador**, así que
existen sin depender de que otro mod los proporcione. Solo los declarados —
instalar este mod para hacer una espada no te llena el inventario de Curios de
huecos vacíos.

> Cambiar `curios_slots` requiere `/reload` **además de** `/customgear reload`.
> Curios lee su asignación de slots fuera de la recarga que dispara este mod. El
> log avisa cuando el conjunto cambia.

Curios es una dependencia opcional: sin ella `curios_slots` simplemente no hace
nada.

## Anidamiento

Los contenedores que llevan su contenido en el objeto se niegan a guardar otro, y
se niegan a entrar en otro. Eso incluye las cajas de shulker de vanilla, en ambas
direcciones.

La razón no es el orden. El contenido vive en los componentes del objeto, así que
un contenedor dentro de otro anida NBT, y cada nivel multiplica el tamaño del
objeto exterior — que viaja por la red cada vez que se mueve en un inventario.
Con unos cientos de slots eso alcanza el límite de paquete y desconecta al
jugador.

Un contenedor que suelta al romperse no lleva NBT dentro, así que se anida sin
problema: un barril normal entra en un shulker, y los shulkers entran en cofres
como siempre.

## Texturas

Cada tipo acepta claves distintas, y darle a uno las claves equivocadas es un
error en vez de una textura por defecto en silencio:

| Tipo       | Claves                              | Qué es cada una                                                       |
|------------|-------------------------------------|-----------------------------------------------------------------------|
| `barrel`   | `top`, `bottom`, `side`, `top_open` | Caras de bloque, como cualquier bloque. `top_open` es la tapa abierta |
| `chest`    | `single`, `left`, `right`           | Desplegados de 64×64, uno por mitad                                   |
| `shulker`  | `single`                            | Un desplegado de 64×64                                                |
| `backpack` | `item`                              | Una textura de objeto de 16×16                                        |

Las mitades de un cofre **no** son la textura simple recortada: miden 15 píxeles
de ancho en vez de 14 y tienen otro desplegado. Un cofre que puede unirse en
doble y solo declara `single` mostrará las mitades de vanilla al emparejarse, y
el parser avisa de ello.

Lo más práctico para hacer una es extraer la de vanilla y pintar encima. Están en
el jar de la versión, bajo `assets/minecraft/textures/entity/`:
`chest/normal.png`, `chest/normal_left.png`, `chest/normal_right.png` y
`shulker/shulker.png`.

Consulta [Texturas y Modelos](textures-and-models.md) para saber cómo se lee un
valor.

## Tooltips

Mantén **Shift** sobre un contenedor que guarde algo y su contenido se muestra
como una rejilla de objetos, de mayor a menor cantidad, con «…y N tipos más»
cuando no caben todos.

Un contenedor que *puede* guardar contenido lo indica incluso vacío, para que la
tecla se descubra en vez de encontrarse por accidente.

## Limitaciones

- **Pasando de 128 slots, una sola acción puede desconectar al jugador.** Ver
  arriba
- **El contenido de una mochila viaja dentro de su ItemStack**, y se reenvía cada
  vez que algo se mueve en el inventario del jugador. Una mochila grande llena de
  objetos con NBT pesado es donde el número de slots más cuesta
- Intentar anidar un contenedor parpadea un fotograma antes de que el servidor lo
  rechace: el inventario suplente del cliente conoce la regla, pero la predicción
  corre primero
- `slots` requiere reiniciar; todo lo demás de un contenedor se recarga en
  caliente

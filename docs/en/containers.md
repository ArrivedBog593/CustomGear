# Containers

A container is storage: barrels, chests, shulkers and backpacks. They share an
inventory, a search field and a sort button, and differ in shape, in how they are
placed, and in what happens when you break one.

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

## The four types

`container.type` is **required**. The four differ in whether breaking one keeps
its contents, and a silent default would decide that behind your back.

| Type       | Shape                                    | Placed                        | Keeps contents |
|------------|------------------------------------------|-------------------------------|----------------|
| `barrel`   | A full cube, with a real open state      | Facing any of six directions  | No             |
| `chest`    | The vanilla chest, with an animated lid  | Horizontally; joins in a pair | No             |
| `shulker`  | Attaches to a surface, lid slides out    | On the face you click         | Yes            |
| `backpack` | Not a block — an item you carry          | —                             | Yes            |

A barrel costs nothing to draw: its whole appearance is generated JSON. A chest
and a shulker need a renderer, which is what buys them their animation.

## Fields

| Field                                | Type    | Default      | Description                                                               |
|--------------------------------------|---------|--------------|---------------------------------------------------------------------------|
| `container.type`                     | String  | **required** | `barrel`, `chest`, `shulker` or `backpack`                                |
| `container.slots`                    | Number  | **required** | Total slots, at least 1. Not rows × columns — the last row may be partial |
| `container.columns`                  | Number  | automatic    | Slots per row. Presentation only                                          |
| `container.max_columns`              | Number  | `12`         | Ceiling for the automatic width. Ignored when `columns` is declared       |
| `container.keeps_contents`           | Boolean | by type      | Whether breaking it stores the contents in the dropped item               |
| `container.openable_when_obstructed` | Boolean | by type      | Whether it opens with a solid block in the way                            |
| `container.curios_slots`             | List    | empty        | (Backpacks only) Curios slot types this can be equipped in                |

### Slot count

There is no hard ceiling, but past **128** the parser warns you. That is where
vanilla's click packet gives up: a single action changing more slots than that at
once — a long drag, a mass shift-click — disconnects the player with an encoder
error. Hard to reach in survival, trivial in creative.

`slots` is baked when the container is registered, so changing it needs a
**restart**, not `/customgear reload`. Shrinking a container that already holds
more than fits drops the excess on the ground; growing one simply adds empty
slots at the end.

### Column count

Omit `columns` and the width is worked out so the contents fit in nine rows,
capped by `max_columns`:

| Slots | Columns | Rows    |
|-------|---------|---------|
| 45    | 9       | 5       |
| 81    | 9       | 9       |
| 108   | 12      | 9       |
| 200   | 12      | scrolls |

Declare `columns` and that exact width is used, however tall the result gets.

There is no horizontal scrolling, so a width too large for the player's window
cannot simply be drawn — the screen re-flows the same slots into fewer columns
instead. No slot is ever unreachable, but a very wide container looks different
on a small window than you designed it.

### Keeping contents

`keeps_contents` decides what breaking one does. Defaults to `true` for shulkers
and backpacks, `false` for barrels and chests — but it is yours to set, and a
barrel that keeps its contents is perfectly legal.

A backpack cannot turn it off: it has no block to store an inventory in, so the
contents live in the item or nowhere.

A container that keeps its contents does not stack, for the same reason vanilla's
shulker box does not: stacking two would merge them into one inventory.

## Chests

Two chests placed side by side, facing the same way, join into a double with
twice the slots. Crouch while placing to keep them separate.

The pair holds **one** inventory rather than two. That is invisible in play —
breaking either half hands back that half's contents exactly as vanilla does, and
Ctrl+picking one in creative copies only its half — but it is why hoppers,
comparators and mod overlays read the same thing from either side.

A chest that **keeps its contents cannot join into a double**: there would be no
answer to which half the dropped item carries. Set `keeps_contents` on a chest
and it stays single.

Joining or splitting a pair changes the inventory's size, so anyone with the
screen open is kicked out of it. Reopening takes a click; a window whose slots no
longer line up would be a bug report.

## Shulkers

A shulker attaches to whatever surface you place it against and its lid slides
out of that face. Put one on the floor and it opens upward; stick it to a wall
and it opens toward you.

Its collision box **grows while the lid rises**, so it pushes entities out of the
way and refuses to open when there is no room. That is not just fidelity: against
a solid block there would be nowhere to push them to.

## Backpacks

A backpack is carried rather than placed. Open it with right click, or with the
**Open backpack** keybind (B by default) from anywhere in your inventory.

The keybind looks in this order:

1. The selected hotbar slot
2. The offhand
3. Equipped Curios slots
4. The rest of the inventory, hotbar first

Equipped beats loose on purpose — someone wearing a storage ring is wearing it to
have it at hand.

While its screen is open the slot holding it is frozen, so it cannot be dragged
out or dropped from inside its own inventory. If it leaves anyway — taken by
another player, pulled by a hopper, lost on death — the screen closes by itself.

### Curios slots

`curios_slots` lists which Curios slot types accept this backpack:

```json
{
  "container": {
    "type": "backpack",
    "slots": 27,
    "curios_slots": ["back", "charm"]
  }
}
```

Curios ships ten slot types: `back`, `belt`, `body`, `bracelet`, `charm`,
`curio`, `hands`, `head`, `necklace` and `ring`. Others may exist if another mod
adds them, so an unrecognised name is warned about rather than rejected.

The slot types you declare are also **assigned to the player**, so they exist
without needing another mod to provide them. Only the declared ones — installing
this mod to make a sword does not fill your Curios inventory with empty slots.

> Changing `curios_slots` needs `/reload` **as well as** `/customgear reload`.
> Curios reads its slot assignment outside the reload this mod triggers. The log
> warns you when the set changes.

Curios is an optional dependency: without it `curios_slots` simply does nothing.

## Nesting

Containers that carry their contents in the item refuse to hold another one, and
refuse to go inside one. That includes vanilla's shulker boxes, in both
directions.

The reason is not tidiness. Contents live in the item's components, so a
container inside a container nests NBT, and each level multiplies the size of the
outer stack — which travels the network every time it moves in an inventory. At a
few hundred slots that reaches the packet limit and disconnects the player.

A container that spills when broken carries no NBT, so it nests freely: a plain
barrel goes inside a shulker without trouble, and shulkers go inside chests as
they always have.

## Textures

Each type takes different keys, and giving one the wrong keys is an error rather
than a silent fallback:

| Type       | Keys                                | What each is                                                  |
|------------|-------------------------------------|---------------------------------------------------------------|
| `barrel`   | `top`, `bottom`, `side`, `top_open` | Block faces, like any block. `top_open` is the lid while open |
| `chest`    | `single`, `left`, `right`           | 64×64 unwraps, one per half                                   |
| `shulker`  | `single`                            | A 64×64 unwrap                                                |
| `backpack` | `item`                              | A 16×16 item texture                                          |

A chest's halves are **not** the single texture cropped: they are 15 pixels wide
instead of 14 and laid out differently. A chest that can join into a double and
declares only `single` will show vanilla's halves when paired, and the parser
warns about it.

The easiest way to make one is to extract vanilla's and paint over it. They live
in the version jar under `assets/minecraft/textures/entity/`:
`chest/normal.png`, `chest/normal_left.png`, `chest/normal_right.png` and
`shulker/shulker.png`.

See [Textures & Models](textures-and-models.md) for how a value is read.

## Tooltips

Hold **Shift** over a container that holds something and its contents show as a
grid of items, most numerous first, with "…and N more types" when there are too
many to fit.

A container that *can* hold contents says so even when empty, so the key is
discoverable rather than something you find by accident.

## Limitations

- **Past 128 slots a single action can disconnect the player.** See above
- **A backpack's contents travel inside its ItemStack**, re-sent every time
  anything moves in the player's inventory. A large backpack full of items with
  heavy NBT is where slot count costs the most
- Trying to nest a container flickers for a frame before the server refuses it:
  the client's stand-in inventory knows the rule, but the prediction runs first
- `slots` needs a restart; everything else about a container is hot-reloadable

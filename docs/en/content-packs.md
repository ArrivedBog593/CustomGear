# Content Packs

You can distribute your content as a single **.zip file** instead of loose files. Drop the zip into:

```
.minecraft/ultimatecustomgear/packs/your-content.zip
```

JSONs and textures inside the zip load exactly like loose files — subfolders inside the zip are fine. This is the recommended way for server owners to share content with players: one file, impossible to half-extract or edit by accident.

**Rules:**
- Zips are only loaded from the `packs/` subfolder. A zip anywhere else is ignored (with a log message telling you where to move it).
- **Only `.zip` is supported.** If you have a `.rar` or `.7z`, re-compress it as zip: on Windows, select the files → right-click → Send to → Compressed folder.
- **Loose files win over zips.** If a loose JSON defines the same `id` as one inside a zip, the loose file is used entirely (stats, textures, recipes), and the zip entry is skipped with a log message. You can use this to locally override a single item from a pack without touching the zip.
- Multiple zips are allowed; they load in alphabetical order.
- `/customgear reload` picks up added or updated zips without restarting.

> A distributable pack should be **self-contained**: every texture a JSON references must live inside the same zip. Referencing a loose texture from a zipped JSON works locally, but players who only receive the zip won't have the loose file. (Loose files still override zip contents of the same path — handy for local tweaks.)

## Pack icon

The dynamic pack shows the mod's logo in the resource pack screen. To use your
own, drop a `dynamic_pack_icon.png` in the `ultimatecustomgear/` folder —
square and a power of two (64×64 or 128×128). It is picked up on
`/customgear reload`, no restart needed.

The icon is deliberately excluded from the content hash, so branding your pack
never desyncs clients.

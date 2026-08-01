# Multiplayer

**Client and server must have the same content files.** Item stats are baked in when the game starts, so differing files cause invisible desyncs (your tooltip says one damage value, the server applies another). To prevent this, the mod verifies content at login by comparing content hashes — packaging doesn't matter: a server using a zip matches clients using the same JSONs loose, and vice versa.

What happens on a mismatch is configured **on the server** in `config/ultimatecustomgear-common.toml`:

| Mode                | Behavior                                                                                                                                                                                                                                               |
|---------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `ENFORCE` (default) | The client is disconnected with a message showing both hashes and how to fix it. Guarantees every player sees correct stats, names and recipes.                                                                                                        |
| `WARN`              | The client is allowed in; the server logs the mismatch and the player gets a chat warning that tooltips/names may not match real values (combat always uses server values). Useful while distributing an updated content zip without kicking everyone. |
| `OFF`               | No verification. Clients missing items still can't join (that's Minecraft's own registry check), but stat differences go undetected.                                                                                                                   |

Only the server's setting matters — a client's local config has no effect when joining a server.

**Distributing content:** pack your `ultimatecustomgear` JSONs (and textures) into a zip, share it, and have players drop it into their`.minecraft/ultimatecustomgear/packs/` folder.

> Both sides must run the same mod version: 1.3.0 clients cannot join older servers and vice versa (network protocol change).

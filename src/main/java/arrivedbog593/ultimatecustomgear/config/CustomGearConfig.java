package arrivedbog593.ultimatecustomgear.config;

import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * Mod configuration, split across two files.
 * <p>
 * COMMON (config/ultimatecustomgear-common.toml) lives on both sides, but the
 * handshake behavior is decided by the SERVER's value: the chosen mode travels
 * inside the handshake payload and the client obeys it. A client's local
 * setting has no effect when joining a server.
 * <p>
 * CLIENT (config/ultimatecustomgear-client.toml) is purely local preference —
 * nothing in it reaches the server or affects gameplay.
 */
public final class CustomGearConfig {

    /**
     * What happens when a joining client's ultimatecustomgear JSONs don't
     * match the server's.
     */
    public enum HandshakeMode {
        /**
         * Default. Mismatching clients are disconnected with a message
         * showing both hashes and how to fix it. Guarantees every player
         * sees correct stats, names and recipes.
         */
        ENFORCE,
        /**
         * Mismatching clients are allowed in. The server logs the mismatch
         * and the client shows a chat warning: tooltips, names and damage
         * PREVIEWS may not match what the server actually applies (combat
         * always uses server values). Useful while distributing an updated
         * content zip without kicking everyone.
         */
        WARN,
        /**
         * The handshake is skipped entirely. No verification, no warning.
         * Registry sync still kicks clients that are MISSING items; this
         * only disables the stat/content comparison.
         */
        OFF
    }

    public static final ModConfigSpec SPEC;
    public static final ModConfigSpec.EnumValue<HandshakeMode> HANDSHAKE_MODE;
    public static final ModConfigSpec CLIENT_SPEC;
    public static final ModConfigSpec.BooleanValue KEEP_SEARCH_PHRASE;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        builder.comment("Container GUI settings").push("containers");

        KEEP_SEARCH_PHRASE = builder
                .comment(
                        "Keep the search phrase when a container is closed and reopened.",
                        "The phrase is shared across every container, so a search typed in one",
                        "chest still filters the next one you open.",
                        "false - every container opens unfiltered")
                .define("keep_search_phrase", true);

        builder.pop();
        CLIENT_SPEC = builder.build();
    }

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        builder.comment("Network / multiplayer settings").push("network");

        HANDSHAKE_MODE = builder
                .comment(
                        "What to do when a joining client's ultimatecustomgear JSON files",
                        "don't match this server's (compared via content hash at connection):",
                        "  ENFORCE - disconnect the client with an explanatory message (default,",
                        "            guarantees every player sees correct stats/names/recipes)",
                        "  WARN    - let them in, log it server-side and warn them in chat",
                        "            (their tooltips/names may not match real server values)",
                        "  OFF     - skip the check entirely",
                        "Only the SERVER's value matters; clients obey what the server sends.")
                .defineEnum("content_handshake_mode", HandshakeMode.ENFORCE);

        builder.pop();
        SPEC = builder.build();
    }

    private CustomGearConfig() {}
}
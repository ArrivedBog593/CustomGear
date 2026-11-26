package arrivedbog593.customgear.config;

import arrivedbog593.customgear.Constants;
import arrivedbog593.customgear.platform.Services;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;

/**
 * Mod configuration, split across two files in config/.
 * <p>
 * COMMON (customgear-common.toml) lives on both sides, but the handshake
 * behavior is decided by the SERVER's value: the chosen mode travels inside
 * the handshake payload and the client obeys it. A client's local setting has
 * no effect when joining a server.
 * <p>
 * CLIENT (customgear-client.toml) is purely local preference — nothing in it
 * reaches the server or affects gameplay.
 * <p>
 * Read once at startup by {@link #load()}, the same on every loader. Each file
 * is written back afterwards, so a missing file or key is filled in with its
 * default and its comment, and a value that does not parse falls back to the
 * default with a warning in the log.
 */
public final class CustomGearConfig {

    /**
     * What happens when a joining client's customgear JSONs don't match the
     * server's.
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

    private static final ConfigFile COMMON = new ConfigFile("customgear-common.toml");
    private static final ConfigFile CLIENT = new ConfigFile("customgear-client.toml");

    public static final Value<HandshakeMode> HANDSHAKE_MODE = COMMON.define(
            "network", "content_handshake_mode", HandshakeMode.ENFORCE,
            text -> HandshakeMode.valueOf(text.toUpperCase(Locale.ROOT)),
            mode -> "\"" + mode.name() + "\"",
            "What to do when a joining client's customgear JSON files",
            "don't match this server's (compared via content hash at connection):",
            "  ENFORCE - disconnect the client with an explanatory message (default,",
            "            guarantees every player sees correct stats/names/recipes)",
            "  WARN    - let them in, log it server-side and warn them in chat",
            "            (their tooltips/names may not match real server values)",
            "  OFF     - skip the check entirely",
            "Only the SERVER's value matters; clients obey what the server sends.");

    public static final Value<Boolean> KEEP_SEARCH_PHRASE = CLIENT.define(
            "containers", "keep_search_phrase", true,
            CustomGearConfig::parseBoolean,
            String::valueOf,
            "Keep the search phrase when a container is closed and reopened.",
            "The phrase is shared across every container, so a search typed in one",
            "chest still filters the next one you open.",
            "false - every container opens unfiltered");

    private CustomGearConfig() {}

    /** Reads both files, then writes them back complete. Called once at startup. */
    public static void load() {
        Path dir = Services.PLATFORM.getConfigDir();
        COMMON.load(dir);
        CLIENT.load(dir);
    }

    private static boolean parseBoolean(String text) {
        if (text.equalsIgnoreCase("true")) return true;
        if (text.equalsIgnoreCase("false")) return false;
        throw new IllegalArgumentException("not true or false");
    }

    /** One setting. Holds its default until the file is read. */
    public static final class Value<T> {

        private final String section;
        private final String key;
        private final T defaultValue;
        private final Function<String, T> parser;
        private final Function<T, String> writer;
        private final String[] comment;
        private volatile T value;

        private Value(String section, String key, T defaultValue,
                      Function<String, T> parser, Function<T, String> writer, String[] comment) {
            this.section = section;
            this.key = key;
            this.defaultValue = defaultValue;
            this.parser = parser;
            this.writer = writer;
            this.comment = comment;
            this.value = defaultValue;
        }

        public T get() {
            return value;
        }
    }

    /**
     * A small TOML file: [sections], key = value lines and # comments, which is
     * all these settings need. Kept apart from any loader's config system so
     * both loaders read the same file the same way.
     */
    private static final class ConfigFile {

        private final String fileName;
        private final List<Value<?>> values = new ArrayList<>();

        ConfigFile(String fileName) {
            this.fileName = fileName;
        }

        <T> Value<T> define(String section, String key, T defaultValue,
                            Function<String, T> parser, Function<T, String> writer,
                            String... comment) {
            Value<T> value = new Value<>(section, key, defaultValue, parser, writer, comment);
            values.add(value);
            return value;
        }

        void load(Path dir) {
            Path file = dir.resolve(fileName);
            Map<String, String> raw = Files.exists(file) ? read(file) : Map.of();
            for (Value<?> value : values) {
                apply(value, raw.get(value.section + "." + value.key));
            }
            write(file);
        }

        private static <T> void apply(Value<T> value, String text) {
            if (text == null) {
                value.value = value.defaultValue;
                return;
            }
            try {
                value.value = value.parser.apply(unquote(text));
            } catch (RuntimeException e) {
                Constants.LOG.warn("[CustomGear] Invalid value '{}' for {}.{}, using the default ({})",
                        text, value.section, value.key, value.writer.apply(value.defaultValue));
                value.value = value.defaultValue;
            }
        }

        private Map<String, String> read(Path file) {
            Map<String, String> raw = new HashMap<>();
            try {
                String section = "";
                for (String line : Files.readAllLines(file, StandardCharsets.UTF_8)) {
                    String trimmed = stripComment(line).trim();
                    if (trimmed.isEmpty()) continue;
                    if (trimmed.startsWith("[") && trimmed.endsWith("]")) {
                        section = trimmed.substring(1, trimmed.length() - 1).trim();
                        continue;
                    }
                    int eq = trimmed.indexOf('=');
                    if (eq <= 0) continue;
                    raw.put(section + "." + trimmed.substring(0, eq).trim(),
                            trimmed.substring(eq + 1).trim());
                }
            } catch (IOException e) {
                Constants.LOG.warn("[CustomGear] Could not read {}, using the defaults", file, e);
            }
            return raw;
        }

        private void write(Path file) {
            StringBuilder out = new StringBuilder();
            String section = null;
            for (Value<?> value : values) {
                if (!value.section.equals(section)) {
                    if (section != null) out.append('\n');
                    section = value.section;
                    out.append('[').append(section).append("]\n");
                }
                for (String line : value.comment) {
                    out.append("\t# ").append(line).append('\n');
                }
                out.append('\t').append(value.key).append(" = ").append(format(value)).append('\n');
            }
            try {
                Files.createDirectories(file.getParent());
                Files.writeString(file, out.toString(), StandardCharsets.UTF_8);
            } catch (IOException e) {
                Constants.LOG.warn("[CustomGear] Could not write {}", file, e);
            }
        }

        private static <T> String format(Value<T> value) {
            return value.writer.apply(value.value);
        }

        /** Drops a # comment, unless the # sits inside a quoted string. */
        private static String stripComment(String line) {
            boolean quoted = false;
            for (int i = 0; i < line.length(); i++) {
                char c = line.charAt(i);
                if (c == '"') quoted = !quoted;
                else if (c == '#' && !quoted) return line.substring(0, i);
            }
            return line;
        }

        private static String unquote(String text) {
            if (text.length() >= 2 && text.startsWith("\"") && text.endsWith("\"")) {
                return text.substring(1, text.length() - 1);
            }
            return text;
        }
    }
}

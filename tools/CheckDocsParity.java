import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.text.Normalizer;
import java.util.*;
import java.util.regex.*;
import java.util.stream.Collectors;

/**
 * Verifies that docs/en and docs/es are still the same document in two languages,
 * and that no link points at nothing.
 * <p>
 * Usage: {@code java tools/CheckDocsParity.java} — JDK 11+, no compilation step
 * (JEP 330). Run from the repository root.
 * <p>
 * Exits 0 when everything checks out and 1 on any error, so it can be dropped
 * into a workflow as-is. Code-block length differences are reported as warnings
 * rather than errors: translated examples legitimately differ in length.
 */
public class CheckDocsParity {

    static final Path DOCS = Paths.get("docs");
    static final Path EN   = DOCS.resolve("en");
    static final Path ES   = DOCS.resolve("es");

    /** Files inside docs/ that are not one half of an en/es pair. */
    static final Set<String> NOT_A_PAGE = Set.of("_Sidebar.md", "curseforge.md");

    /**
     * Pure reference pages: the first column of every table is a list of
     * identifiers that go inside the JSON. Translating them would break the files
     * of anyone who copies them, so these pages are held to a stricter rule than
     * the rest.
     */
    static final Set<String> REFERENCE_PAGES =
            Set.of("block-sounds.md", "map-colors.md", "damage-types.md");

    static final List<String> errors   = new ArrayList<>();
    static final List<String> warnings = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        if (!Files.isDirectory(EN) || !Files.isDirectory(ES)) {
            System.err.println("Cannot find docs/en and docs/es. Run this from the repository root.");
            System.exit(1);
        }

        List<String> enPages = pagesIn(EN);
        List<String> esPages = pagesIn(ES);

        checkFileParity(enPages, esPages);

        List<String> shared = new ArrayList<>(enPages);
        shared.retainAll(esPages);
        for (String page : shared) {
            checkStructure(page);
            if (REFERENCE_PAGES.contains(page)) checkReferenceColumns(page);
        }

        checkNavigation("en", EN, enPages, "");
        checkNavigation("es", ES, esPages, "es-");
        checkLinks();

        report(shared.size());
    }

    // ------------------------------------------------------------------- files

    static List<String> pagesIn(Path dir) throws IOException {
        try (var s = Files.list(dir)) {
            return s.map(p -> p.getFileName().toString())
                    .filter(n -> n.endsWith(".md") && !NOT_A_PAGE.contains(n))
                    .sorted()
                    .collect(Collectors.toList());
        }
    }

    static void checkFileParity(List<String> en, List<String> es) {
        for (String p : en) if (!es.contains(p)) errors.add("Missing docs/es/" + p + " (exists in English)");
        for (String p : es) if (!en.contains(p)) errors.add("Missing docs/en/" + p + " (exists in Spanish)");
    }

    // --------------------------------------------------------------- structure

    /**
     * Headings are translated, so text is never compared — shape is. If one
     * language has a table, a code block or a note the other does not, someone
     * edited one side and forgot the other.
     */
    static void checkStructure(String page) throws IOException {
        Doc a = Doc.parse(EN.resolve(page));
        Doc b = Doc.parse(ES.resolve(page));

        if (!a.headingLevels.equals(b.headingLevels)) {
            errors.add(page + ": heading count or nesting differs - en=" +
                    a.headingLevels + " es=" + b.headingLevels);
        }
        if (a.tableRowCounts.size() != b.tableRowCounts.size()) {
            errors.add(page + ": " + a.tableRowCounts.size() + " tables in English, " +
                    b.tableRowCounts.size() + " in Spanish");
        } else {
            for (int i = 0; i < a.tableRowCounts.size(); i++) {
                if (!a.tableRowCounts.get(i).equals(b.tableRowCounts.get(i))) {
                    errors.add(page + ": table #" + (i + 1) + " has " + a.tableRowCounts.get(i) +
                            " rows in English and " + b.tableRowCounts.get(i) + " in Spanish");
                }
            }
        }
        if (a.codeBlockSizes.size() != b.codeBlockSizes.size()) {
            errors.add(page + ": " + a.codeBlockSizes.size() + " code blocks in English, " +
                    b.codeBlockSizes.size() + " in Spanish");
        } else {
            for (int i = 0; i < a.codeBlockSizes.size(); i++) {
                if (!a.codeBlockSizes.get(i).equals(b.codeBlockSizes.get(i))) {
                    warnings.add(page + ": code block #" + (i + 1) + " is " +
                            a.codeBlockSizes.get(i) + " lines in English and " +
                            b.codeBlockSizes.get(i) + " in Spanish");
                }
            }
        }
        // Counted per block, not per line, so rewrapping a note is not a false positive.
        if (a.blockquoteCount != b.blockquoteCount) {
            errors.add(page + ": " + a.blockquoteCount + " notes (>) in English, " +
                    b.blockquoteCount + " in Spanish");
        }
    }

    /** On reference pages the identifier column must be identical in both languages. */
    static void checkReferenceColumns(String page) throws IOException {
        List<List<String>> a = Doc.firstColumns(EN.resolve(page));
        List<List<String>> b = Doc.firstColumns(ES.resolve(page));
        int n = Math.min(a.size(), b.size());
        for (int i = 0; i < n; i++) {
            if (!a.get(i).equals(b.get(i))) {
                Set<String> onlyEn = new LinkedHashSet<>(a.get(i)); onlyEn.removeAll(b.get(i));
                Set<String> onlyEs = new LinkedHashSet<>(b.get(i)); onlyEs.removeAll(a.get(i));
                errors.add(page + ": table #" + (i + 1) + " has different identifiers between languages" +
                        (onlyEn.isEmpty() ? "" : " | English only: " + onlyEn) +
                        (onlyEs.isEmpty() ? "" : " | Spanish only: " + onlyEs));
            }
        }
    }

    // -------------------------------------------------------------- navigation

    /**
     * A page listed in neither the index nor the sidebar exists, but nobody can
     * reach it. This is the easiest thing to forget when adding documentation.
     */
    static void checkNavigation(String lang, Path dir, List<String> pages, String wikiPrefix) throws IOException {
        Path indexFile = dir.resolve("index.md");
        if (!Files.exists(indexFile)) { errors.add("Missing docs/" + lang + "/index.md"); return; }
        String index = read(indexFile);

        Path sidebarFile = DOCS.resolve("_Sidebar.md");
        String sidebar = Files.exists(sidebarFile) ? read(sidebarFile) : "";
        if (sidebar.isEmpty()) warnings.add("No docs/_Sidebar.md found; skipping the wiki menu check");

        for (String page : pages) {
            if (page.equals("index.md")) continue;
            String slug = page.substring(0, page.length() - 3);

            if (!index.contains("(" + page + ")") && !index.contains("(" + page + "#")) {
                errors.add("docs/" + lang + "/index.md does not link to " + page);
            }
            if (!sidebar.isEmpty()) {
                String entry = "(" + wikiPrefix + slug + ")";
                if (!sidebar.contains(entry)) {
                    errors.add("docs/_Sidebar.md does not list " + entry + " (page " + lang + "/" + page + ")");
                }
            }
        }
    }

    // ------------------------------------------------------------------- links

    static final Pattern LINK = Pattern.compile("\\[[^\\]]*\\]\\(([^)\\s]+)\\)");

    static void checkLinks() throws IOException {
        List<Path> files = new ArrayList<>();
        for (Path dir : List.of(EN, ES)) {
            try (var s = Files.list(dir)) {
                s.filter(p -> p.toString().endsWith(".md")).forEach(files::add);
            }
        }
        for (Path f : files) {
            String body = read(f);
            Matcher m = LINK.matcher(body);
            while (m.find()) {
                String target = m.group(1);
                if (target.startsWith("http") || target.startsWith("mailto:")) continue;

                String rel    = target;
                String anchor = "";
                int hash = target.indexOf('#');
                if (hash >= 0) { rel = target.substring(0, hash); anchor = target.substring(hash + 1); }

                Path dest = rel.isEmpty() ? f : f.getParent().resolve(rel).normalize();
                if (!Files.exists(dest)) {
                    errors.add(f + " links to " + target + ", which does not exist");
                    continue;
                }
                if (!anchor.isEmpty() && dest.toString().endsWith(".md")) {
                    if (!slugsOf(dest).contains(slug(decode(anchor)))) {
                        errors.add(f + " links to anchor #" + anchor + " in " +
                                dest.getFileName() + ", which is not a heading there");
                    }
                }
            }
        }
    }

    /**
     * GitHub keeps accented characters in anchors, so filtering on [a-z0-9-] would
     * produce false positives in Spanish. Normalise to NFC and accept every Unicode
     * letter instead.
     */
    static String slug(String heading) {
        String s = Normalizer.normalize(heading, Normalizer.Form.NFC).toLowerCase(Locale.ROOT).trim();
        s = s.replaceAll("`|\\*\\*|\\*|_", "");
        s = s.replaceAll("\\[([^\\]]*)\\]\\([^)]*\\)", "$1");   // [text](url) -> text
        StringBuilder out = new StringBuilder();
        for (char c : s.toCharArray()) {
            if (Character.isLetterOrDigit(c)) out.append(c);
            else if (c == ' ' || c == '-') out.append('-');
        }
        return out.toString().replaceAll("-+", "-").replaceAll("^-|-$", "");
    }

    static String decode(String anchor) {
        try { return java.net.URLDecoder.decode(anchor, StandardCharsets.UTF_8); }
        catch (Exception e) { return anchor; }
    }

    static final Map<Path, Set<String>> SLUG_CACHE = new HashMap<>();

    static Set<String> slugsOf(Path file) throws IOException {
        Set<String> cached = SLUG_CACHE.get(file);
        if (cached != null) return cached;
        Set<String> slugs = new HashSet<>();
        for (String line : Files.readAllLines(file, StandardCharsets.UTF_8)) {
            Matcher m = Pattern.compile("^#{1,6}\\s+(.+)$").matcher(line);
            if (m.matches()) slugs.add(slug(m.group(1)));
        }
        SLUG_CACHE.put(file, slugs);
        return slugs;
    }

    // ------------------------------------------------------------------ report

    static void report(int pairs) {
        System.out.println();
        for (String w : warnings) System.out.println("  warning  " + w);
        if (!warnings.isEmpty()) System.out.println();
        for (String e : errors)   System.out.println("  ERROR    " + e);

        if (errors.isEmpty()) {
            System.out.println("OK - " + pairs + " pages paired, links and navigation are sound" +
                    (warnings.isEmpty() ? "" : " (" + warnings.size() + " warning(s))"));
            System.exit(0);
        }
        System.out.println();
        System.out.println(errors.size() + " parity problem(s). The two language versions have drifted apart.");
        System.exit(1);
    }

    static String read(Path p) throws IOException {
        return Files.readString(p, StandardCharsets.UTF_8);
    }

    // ------------------------------------------------------------------ parsing

    /** A markdown file reduced to its shape: headings, tables, code blocks and notes. */
    static class Doc {
        List<Integer> headingLevels  = new ArrayList<>();
        List<Integer> tableRowCounts = new ArrayList<>();
        List<Integer> codeBlockSizes = new ArrayList<>();
        int blockquoteCount;

        static Doc parse(Path file) throws IOException {
            Doc d = new Doc();
            List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8);

            boolean inCode = false, inQuote = false;
            int codeLines = 0, tableRows = 0;

            for (String raw : lines) {
                String line = raw.strip();

                if (line.startsWith("```")) {
                    if (inCode) { d.codeBlockSizes.add(codeLines); inCode = false; }
                    else { inCode = true; codeLines = 0; }
                    continue;
                }
                if (inCode) { codeLines++; continue; }

                // Tables: contiguous rows starting with |, ignoring the separator row.
                if (line.startsWith("|")) {
                    if (!line.matches("^\\|[\\s:|-]+\\|$")) tableRows++;
                } else if (tableRows > 0) {
                    d.tableRowCounts.add(tableRows);
                    tableRows = 0;
                }

                // Notes are counted per block so that rewrapping one is not a diff.
                if (line.startsWith(">")) {
                    if (!inQuote) { d.blockquoteCount++; inQuote = true; }
                } else if (!line.isEmpty()) {
                    inQuote = false;
                }

                Matcher h = Pattern.compile("^(#{1,6})\\s+\\S").matcher(line);
                if (h.find()) d.headingLevels.add(h.group(1).length());
            }
            if (tableRows > 0) d.tableRowCounts.add(tableRows);
            if (inCode) d.codeBlockSizes.add(codeLines);
            return d;
        }

        /**
         * First cell of every row of every table, for the reference pages. Only cells
         * written between backticks are collected: those are the identifiers that go
         * inside the JSON. Headers ("Value" / "Valor") and prose columns such as the
         * block names in the vanilla reference table stay out, since those are
         * translated on purpose.
         */
        static List<List<String>> firstColumns(Path file) throws IOException {
            List<List<String>> tables = new ArrayList<>();
            List<String> current = new ArrayList<>();
            boolean inCode = false, inTable = false;

            for (String raw : Files.readAllLines(file, StandardCharsets.UTF_8)) {
                String line = raw.strip();
                if (line.startsWith("```")) { inCode = !inCode; continue; }
                if (inCode) continue;

                if (line.startsWith("|")) {
                    inTable = true;
                    if (line.matches("^\\|[\\s:|-]+\\|$")) continue;
                    String[] cells = line.split("\\|", -1);
                    if (cells.length > 1) {
                        String cell = cells[1].strip();
                        if (cell.startsWith("`") && cell.endsWith("`")) current.add(cell);
                    }
                } else if (inTable) {
                    if (!current.isEmpty()) tables.add(current);
                    current = new ArrayList<>();
                    inTable = false;
                }
            }
            if (!current.isEmpty()) tables.add(current);
            return tables;
        }
    }
}

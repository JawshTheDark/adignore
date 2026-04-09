package com.jawshthedark.adignore;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.jawshthedark.adignore.data.IgnoredUser;
import net.fabricmc.loader.api.FabricLoader;

import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class IgnoreManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static final Type IGNORED_TYPE = new TypeToken<Map<String, IgnoredUser>>() {}.getType();
    private static final Type KEYWORDS_TYPE = new TypeToken<List<String>>() {}.getType();

    private static final Map<String, IgnoredUser> IGNORED = new HashMap<>();
    private static final List<String> KEYWORDS = new ArrayList<>();

    private static Path ignoredFile;
    private static Path keywordsFile;

    private static final List<String> DEFAULT_KEYWORDS = Arrays.asList(
        "discord.gg",
        "discord.6b.wiki",
        "free kits",
        "join now",
        "recruiting",
        "1 boost",
        "boost:",
        "boost ",
        "fastest growing server",
        "apply now"
    );

    public static void init() {
        try {
            Path dir = FabricLoader.getInstance().getGameDir().resolve("meteor-client/adignore");
            Files.createDirectories(dir);

            ignoredFile = dir.resolve("ignored.json");
            keywordsFile = dir.resolve("keywords.json");

            loadIgnored();
            loadKeywords();

            System.out.println("[AdIgnore] Loaded " + IGNORED.size() + " ignored users.");
            System.out.println("[AdIgnore] Loaded " + KEYWORDS.size() + " keywords.");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    private static void loadIgnored() {
        try {
            IGNORED.clear();

            if (Files.exists(ignoredFile)) {
                try (Reader reader = Files.newBufferedReader(ignoredFile)) {
                    Map<String, IgnoredUser> data = GSON.fromJson(reader, IGNORED_TYPE);

                    if (data != null) {
                        for (Map.Entry<String, IgnoredUser> entry : data.entrySet()) {
                            IGNORED.put(entry.getKey().toLowerCase(), entry.getValue());
                        }
                    }
                }
            } else {
                saveIgnored();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void loadKeywords() {
        try {
            KEYWORDS.clear();

            if (Files.exists(keywordsFile)) {
                try (Reader reader = Files.newBufferedReader(keywordsFile)) {
                    List<String> data = GSON.fromJson(reader, KEYWORDS_TYPE);

                    if (data != null) {
                        for (String keyword : data) {
                            if (keyword != null && !keyword.isBlank()) {
                                KEYWORDS.add(keyword.toLowerCase());
                            }
                        }
                    }
                }
            }

            if (KEYWORDS.isEmpty()) {
                KEYWORDS.addAll(DEFAULT_KEYWORDS);
                saveKeywords();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void saveIgnored() {
        try (Writer writer = Files.newBufferedWriter(ignoredFile)) {
            GSON.toJson(IGNORED, IGNORED_TYPE, writer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void saveKeywords() {
        try (Writer writer = Files.newBufferedWriter(keywordsFile)) {
            GSON.toJson(KEYWORDS, KEYWORDS_TYPE, writer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isIgnored(String name) {
        if (name == null) return false;
        return IGNORED.containsKey(name.toLowerCase());
    }

    public static boolean addIfAbsent(String name, String reason) {
        if (name == null || name.isBlank()) return false;

        String key = name.toLowerCase();
        if (IGNORED.containsKey(key)) return false;

        IGNORED.put(key, new IgnoredUser(name, reason));
        saveIgnored();
        return true;
    }

    public static void add(String name, String reason) {
        if (name == null || name.isBlank()) return;

        IGNORED.put(name.toLowerCase(), new IgnoredUser(name, reason));
        saveIgnored();
    }

    public static void remove(String name) {
        if (name == null || name.isBlank()) return;

        IGNORED.remove(name.toLowerCase());
        saveIgnored();
    }

    public static Collection<IgnoredUser> list() {
        return IGNORED.values();
    }
    public static void reload() {
    loadIgnored();
    loadKeywords();
    System.out.println("[AdIgnore] Reloaded files. Ignored users: " + IGNORED.size() + ", keywords: " + KEYWORDS.size());
    }
    public static List<String> getKeywords() {
        return Collections.unmodifiableList(KEYWORDS);
    }
}
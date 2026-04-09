package com.jawshthedark.adignore.modules;

import com.jawshthedark.adignore.AdIgnoreAddon;
import com.jawshthedark.adignore.IgnoreManager;
import meteordevelopment.meteorclient.events.game.ReceiveMessageEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AdIgnoreModule extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Boolean> debugLog = sgGeneral.add(new BoolSetting.Builder()
        .name("debug-log")
        .description("Logs blocked messages to the console for debugging.")
        .defaultValue(false)
        .build()
    );

    private final Setting<Boolean> reloadFiles = sgGeneral.add(new BoolSetting.Builder()
        .name("reload-files")
        .description("Reload ignored.json and keywords.json from disk.")
        .defaultValue(false)
        .build()
    );

    private static final Pattern[] CHAT_PATTERNS = new Pattern[] {
        Pattern.compile("^<([^>]+)>\\s+.*$"),
        Pattern.compile("^([A-Za-z0-9_]+):\\s+.*$"),
        Pattern.compile("^\\[[^\\]]+\\]\\s*([A-Za-z0-9_]+):\\s+.*$"),
        Pattern.compile("^\\[[^\\]]+\\]\\s*<([^>]+)>\\s+.*$"),
        Pattern.compile("^([A-Za-z0-9_]+)\\s+»\\s+.*$"),
        Pattern.compile("^\\[[^\\]]+\\]\\s*([A-Za-z0-9_]+)\\s+»\\s+.*$")
    };

    public AdIgnoreModule() {
        super(AdIgnoreAddon.CATEGORY, "ad-ignore", "Blocks chat messages from ignored users.");
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (reloadFiles.get()) {
            IgnoreManager.reload();

            if (debugLog.get()) {
                System.out.println("[AdIgnore Debug] Reloaded ignored.json and keywords.json from disk.");
            }

            reloadFiles.set(false);
        }
    }

    @EventHandler
    private void onReceiveMessage(ReceiveMessageEvent event) {
        String raw = event.getMessage().getString();

        if (raw.startsWith("[Meteor]")) return;

        String lower = raw.toLowerCase();
        String sender = extractSender(raw);

        if (debugLog.get()) {
            System.out.println("[AdIgnore Debug] RAW=" + raw);
            System.out.println("[AdIgnore Debug] SENDER=" + sender);
            System.out.println("[AdIgnore Debug] CHECKING: " + sender + " -> " + IgnoreManager.isIgnored(sender));
        }

        String matchedKeyword = getMatchedKeyword(lower);

        if (matchedKeyword != null) {
            if (sender != null) {
                boolean added = IgnoreManager.addIfAbsent(sender, "auto-added by keyword filter: " + matchedKeyword);

                if (debugLog.get()) {
                    System.out.println("[AdIgnore Debug] AUTO-ADD USER=" + sender + " KEYWORD=" + matchedKeyword + " ADDED=" + added);
                }
            }

            if (debugLog.get()) {
                System.out.println("[AdIgnore Debug] BLOCKED KEYWORD=" + matchedKeyword);
            }

            event.cancel();
            return;
        }

        if (sender == null) return;
        if (!IgnoreManager.isIgnored(sender)) return;

        if (debugLog.get()) {
            System.out.println("[AdIgnore Debug] BLOCKED USER=" + sender);
        }

        event.cancel();
    }

    private String getMatchedKeyword(String lower) {
        for (String keyword : IgnoreManager.getKeywords()) {
            if (lower.contains(keyword)) {
                return keyword;
            }
        }

        return null;
    }

    private String extractSender(String message) {
        if (message.contains(" whispers: ")) {
            return message.split(" whispers: ")[0].trim();
        }

        int arrowIndex = message.indexOf("»");
        if (arrowIndex != -1) {
            String left = message.substring(0, arrowIndex).trim();
            left = left.replaceAll("\\[[^\\]]+\\]\\s*", "");
            return left.trim();
        }

        for (Pattern pattern : CHAT_PATTERNS) {
            Matcher matcher = pattern.matcher(message);
            if (matcher.matches()) {
                return matcher.group(1);
            }
        }

        return null;
    }
}
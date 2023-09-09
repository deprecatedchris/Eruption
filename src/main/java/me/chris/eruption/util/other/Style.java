package me.chris.eruption.util.other;

import org.bukkit.ChatColor;

import java.util.List;
import java.util.stream.Collectors;

import static org.apache.commons.text.StringEscapeUtils.unescapeJava;

public final class Style {

    public static final String BLUE = ChatColor.BLUE.toString();
    public static final String AQUA = ChatColor.AQUA.toString();
    public static final String YELLOW = ChatColor.YELLOW.toString();
    public static final String RED = ChatColor.RED.toString();
    public static final String GRAY = ChatColor.GRAY.toString();
    public static final String GOLD = ChatColor.GOLD.toString();
    public static final String GREEN = ChatColor.GREEN.toString();
    public static final String WHITE = ChatColor.WHITE.toString();
    public static final String BLACK = ChatColor.BLACK.toString();
    public static final String BOLD = ChatColor.BOLD.toString();
    public static final String ITALIC = ChatColor.ITALIC.toString();
    public static final String UNDER_LINE = ChatColor.UNDERLINE.toString();
    public static final String STRIKE_THROUGH = ChatColor.STRIKETHROUGH.toString();
    public static final String RESET = ChatColor.RESET.toString();
    public static final String MAGIC = ChatColor.MAGIC.toString();
    public static final String DARK_BLUE = ChatColor.DARK_BLUE.toString();
    public static final String DARK_AQUA = ChatColor.DARK_AQUA.toString();
    public static final String DARK_GRAY = ChatColor.DARK_GRAY.toString();
    public static final String DARK_GREEN = ChatColor.DARK_GREEN.toString();
    public static final String DARK_PURPLE = ChatColor.DARK_PURPLE.toString();
    public static final String DARK_RED = ChatColor.DARK_RED.toString();
    public static final String PINK = ChatColor.LIGHT_PURPLE.toString();
    public static final String BLANK_LINE = "§a §b §c §d §e §f §0 §r";
    public static final String BORDER_LINE_SCOREBOARD = Style.GRAY + Style.STRIKE_THROUGH + "----------------------";
    public static final String UNICODE_VERTICAL_BAR = Style.GRAY + unescapeJava("\u2503");
    public static final String UNICODE_CAUTION = unescapeJava("\u26a0");
    public static final String UNICODE_ARROW_LEFT = unescapeJava("\u25C0");
    public static final String UNICODE_ARROW_RIGHT = unescapeJava("\u25B6");
    public static final String UNICODE_ARROWS_LEFT = unescapeJava("\u00AB");
    public static final String UNICODE_ARROWS_RIGHT = unescapeJava("\u00BB");
    public static final String UNICODE_HEART = unescapeJava("\u2764");
    private static final String MAX_LENGTH = "11111111111111111111111111111111111111111111111111111";

    private Style() {
        throw new RuntimeException("Cannot instantiate a utility class.");
    }

    public static String strip(String in) {
        return ChatColor.stripColor(translate(in));
    }

    public static String translate(String in) {
        return ChatColor.translateAlternateColorCodes('&', in);
    }

    public static List<String> translateLines(List<String> lines) {
        return lines.stream()
                .map(string -> ChatColor.translateAlternateColorCodes('&', string))
                .collect(Collectors.toList());
    }
}

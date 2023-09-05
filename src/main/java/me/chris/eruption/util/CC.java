package me.chris.eruption.util;

import org.apache.commons.lang.StringEscapeUtils;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

public class CC {
    public static String translate(final String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    public static final String UNICODE_DOUBLE_ARROW = StringEscapeUtils.unescapeJava("\u00BB");
    public static final String UNICODE_CHECK_MARK = ChatColor.BOLD.toString() + StringEscapeUtils.unescapeJava("\u2713");
    public static final String UNICODE_X_MARK = StringEscapeUtils.unescapeJava("\u2717");
    public static final String UNICODE_GEM = StringEscapeUtils.unescapeJava("\u2741");
    public static final String UNICODE_HEART = StringEscapeUtils.unescapeJava("\u2764");
    public static final String UNICODE_ARROW_NEXT = StringEscapeUtils.unescapeJava("\u2192");
    public static final String UNICODE_STAR = StringEscapeUtils.unescapeJava("\u272b");
    public static final String UNICODE_BAR = StringEscapeUtils.unescapeJava("\u2503");
    public static final String UNICODE_ARROW_FACTIONS = StringEscapeUtils.unescapeJava("\u27a5");
    public static final String UNICODE_ONE = StringEscapeUtils.unescapeJava("\u2776");
    public static final String UNICODE_TWO = StringEscapeUtils.unescapeJava("\u2777");
    public static final String UNICODE_THREE = StringEscapeUtils.unescapeJava("\u2778");
    public static final String UNICODE_BULLET = StringEscapeUtils.unescapeJava("\u2022");
    public static final String MENU_BAR = ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH.toString() + "------------------------";
    public static final String CHAT_BAR = ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH.toString() + "------------------------------------------------";
    public static final String SB_BAR = ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH.toString() + "----------------------";
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

    public static List<String> translate(List<String> lines) {
        List<String> toReturn = new ArrayList<>();

        for (String line : lines) {
            toReturn.add(ChatColor.translateAlternateColorCodes('&', line));
        }

        return toReturn;
    }

    public static String format(String in, Object... args) {
        return String.format(translate(in), args);
    }

    public static List<String> format(List<String> lines, Object... args) {
        List<String> toReturn = new ArrayList<>();
        for (String line : lines) {
            toReturn.add(String.format(translate(line), args));
        }
        return toReturn;
    }

    public static String colorBoolean(boolean b) {
        return colorBoolean(b, false);
    }

    public static String colorBoolean(boolean b, boolean capitalize) {
        return colorBoolean(b, (capitalize ? "E" : "e") + "nabled", (capitalize ? "D" : "d") + "isabled");
    }

    public static String coolBoolean(boolean b) {
        return colorBoolean(b, "✔", "✘");
    }

    public static String colorBoolean(boolean b, String enabled, String disabled) {
        return b ? CC.GREEN + enabled : CC.RED + disabled;
    }

    public static String boldColorBoolean(boolean b, String enabled, String disabled) {
        return b ? CC.GREEN + ChatColor.BOLD + enabled : CC.RED + ChatColor.BOLD + disabled;
    }

//    new ConfirmMenu("Are you sure?", success -> {
//        if (success) {
//            callback.callback(amount);
//        } else {
//            player.closeInventory();
//        }
//    }).openMenu(player);
}


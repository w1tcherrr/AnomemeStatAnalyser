package utils;

public class StringUtils {

    public static String rightPad(Object val, int padLen) {
        return pad(val.toString(), padLen, false);
    }

    public static String leftPad(Object val, int padLen) {
        return pad(val.toString(), padLen, true);
    }

    public static String pad(String str, int padLen, boolean left) {
        int len = str.length();
        if (len >= padLen) {
            return str;
        }
        String padding = " ".repeat(padLen - len);
        return left ? padding + str : str + padding;
    }

    public static String formatPlusMinus(int kills, int deaths, int fullLength) {
        int diff = kills >= deaths ? kills - deaths : deaths - kills;
        String sign = kills >= deaths ? "+" : "-";
        String formatted = sign + diff;
        return StringUtils.leftPad(formatted, fullLength);
    }

    public static String formatHeadshotPercent(int hs, int total) {
        return String.format("%-2.2f", (double) hs / total * 100);
    }
}

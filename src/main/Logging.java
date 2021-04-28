package main;

/* A very simple coloured logging interface */
public class Logging {
    public static final String RESET = "\u001B[0m";
    public static final String RED = "\u001B[91m";
    public static final String GREEN = "\u001B[92m";
    public static final String YELLOW = "\u001B[93m";
    public static final String BLUE = "\u001B[94m";
    public static final String MAGENTA = "\u001B[95m";

    public static boolean noColor;
    public static boolean noLogging;

    static {
        noColor = System.getenv("NO_COLOR") != null;
        noLogging = System.getenv("NO_LOGGING") != null;
    }

    public static void println(String color, String... values) {
        if (noLogging)
            return;
        String message = String.join(" ", values);
        if (noColor)
            System.out.println(message);
        else
            System.out.println(color + message + RESET);
    }

    public static void error(String... values) {
        String message = String.join(" ", values);
        if (noColor)
            System.err.println("[ERROR] " + message);
        else
            System.err.println(RED + "[ERROR] " + message + RESET);
    }

    public static void green(String... values) {
        println(GREEN, values);
    }

    public static void yellow(String... values) {
        println(YELLOW, values);
    }

    public static void blue(String... values) {
        println(BLUE, values);
    }

    public static void magenta(String... values) {
        println(MAGENTA, values);
    }
}

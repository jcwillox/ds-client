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

    public static void println(String color, String message) {
        if (noLogging)
            return;
        if (noColor)
            System.out.println(message);
        else
            System.out.println(color + message + RESET);
    }

    public static void error(String message) {
        if (noColor)
            System.err.println("[ERROR]" + message);
        System.err.println(RED + "[ERROR]" + message + RESET);
    }

    public static void green(String message) {
        println(GREEN, message);
    }

    public static void yellow(String message) {
        println(YELLOW, message);
    }

    public static void blue(String message) {
        println(BLUE, message);
    }

    public static void magenta(String message) {
        println(MAGENTA, message);
    }
}

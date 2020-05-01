package cz.uhk.seenit.utils;

import android.util.Log;

// Trida pro centralni logovani
public class LogUtils {

    public static void LogWarning(String text) {
        Log.e("WARN", text);
    }

    public static void LogError(String text) {
        Log.e("ERROR", text);
    }

    public static void LogError(Exception exception) {
        LogError(ExceptionToLogString(exception));
    }

    private static String ExceptionToLogString(Exception exception) {
        return exception.getMessage() + ": " + exception.getStackTrace();
    }
}
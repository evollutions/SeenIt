package cz.uhk.seenit.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.StringRes;

// Trida pro centralni logovani
public class Logger {

    public static void LogWarning(String text) {
        Log.w("WARN", text);
    }

    public static void LogError(String text) {
        Log.e("ERROR", text);
    }

    public static void LogErrorAndShowToast(@StringRes int id, Context context) {
        String text = GuiUtils.getResString(id, context.getResources());
        LogErrorAndShowToast(text, context);
    }

    public static void LogErrorAndShowToast(String text, Context context) {
        Log.e("ERROR", text);
        GuiUtils.showToast(text, Toast.LENGTH_LONG, context);
    }

    public static void LogError(Exception exception) {
        LogError(ExceptionToLogString(exception));
    }

    private static String ExceptionToLogString(Exception exception) {
        return exception.getMessage() + ": " + exception.getStackTrace();
    }
}
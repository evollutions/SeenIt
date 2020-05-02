package cz.uhk.seenit.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

// Trida pro centralni logovani
public class Logger {

    public static void LogWarning(String text) {
        Log.w("WARN", text);
    }

    public static void LogError(String text) {
        Log.e("ERROR", text);
    }

    public static void LogError(Exception exception) {
        LogError(ExceptionToLogString(exception));
    }

    public static void LogWarningAndShowToast(@StringRes int userTextId, @Nullable Exception exception, Context context) {
        String text = GuiUtils.getResString(userTextId, context.getResources());
        LogAndShowToast(text, exception, context, false);
    }

    public static void LogErrorAndShowToast(@StringRes int userTextId, @Nullable Exception exception, Context context) {
        String text = GuiUtils.getResString(userTextId, context.getResources());
        LogAndShowToast(text, exception, context, true);
    }

    private static void LogAndShowToast(String userText, @Nullable Exception exception, Context context, boolean isError) {
        // Logujeme citelny text nasledovany zpravou vyjimky a stacktracem
        String logText = userText + " - " + ExceptionToLogString(exception);

        if (isError) {
            LogError(logText);
        } else {
            LogWarning(logText);
        }

        // V toastu ukazujeme pouze citelny text
        GuiUtils.showToast(userText, Toast.LENGTH_LONG, context);
    }

    private static String ExceptionToLogString(@Nullable Exception exception) {
        if (exception == null) {
            return "exception not included";
        }

        return exception.getMessage() + ": " + exception.getStackTrace();
    }
}
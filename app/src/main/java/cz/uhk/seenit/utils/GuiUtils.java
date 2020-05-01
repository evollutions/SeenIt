package cz.uhk.seenit.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import com.google.android.material.snackbar.Snackbar;

public class GuiUtils {

    private static final String REST_STRING_NOT_FOUND = "RES_STRING_NOT_FOUND";

    public static String getResString(@StringRes int id, Resources resources) {
        try {
            return resources.getString(id);
        } catch (Resources.NotFoundException exception) {
            Logger.LogError(exception);

            return REST_STRING_NOT_FOUND;
        }
    }

    public static void showSnackbar(String text, int length, View view) {
        Snackbar.make(view, text, length).show();
    }

    public static void showToast(String text, int length, Context context) {
        Toast.makeText(context, text, length).show();
    }

    public static void showOkDialog(String title, String message, @Nullable DialogInterface.OnClickListener listener, Context context) {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("OK", listener)
                .show();
    }
}
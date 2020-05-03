package cz.uhk.seenit.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import cz.uhk.seenit.R;

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

    public static void showToast(String text, int length, Context context) {
        Toast.makeText(context, text, length).show();
    }

    public static void showDialog(String title, String message, @Nullable DialogInterface.OnClickListener positiveListener, @Nullable DialogInterface.OnClickListener negativeListener, Context context) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(false);

        if (positiveListener != null) {
            dialog.setPositiveButton(negativeListener == null ? R.string.ok : R.string.yes, positiveListener);
        }

        if (negativeListener != null) {
            dialog.setNegativeButton(R.string.no, negativeListener);
        }

        dialog.show();
    }
}
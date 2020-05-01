package cz.uhk.seenit;

import android.content.DialogInterface;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.BaseTransientBottomBar;

import cz.uhk.seenit.utils.GuiUtils;

public class BaseAppCompatActivity extends AppCompatActivity {

    public String getResString(@StringRes int id) {
        return GuiUtils.getResString(id, getResources());
    }

    public void showSnackbar(String text) {
        showSnackbar(text, BaseTransientBottomBar.LENGTH_LONG);
    }

    public void showSnackbar(@StringRes int id) {
        showSnackbar(getResString(id), BaseTransientBottomBar.LENGTH_LONG);
    }

    private void showSnackbar(String text, int length) {
        GuiUtils.showSnackbar(text, length, findViewById(android.R.id.content));
    }

    public void showToast(String text) {
        showToast(text, Toast.LENGTH_LONG);
    }

    public void showToast(@StringRes int id) {
        showToast(getResString(id), Toast.LENGTH_LONG);
    }

    private void showToast(String text, int length) {
        GuiUtils.showToast(text, length, this);
    }

    public void showOkDialog(@StringRes int titleId, @StringRes int messageId, @Nullable DialogInterface.OnClickListener listener) {
        showOkDialog(getResString(titleId), getResString(messageId), listener);
    }

    public void showOkDialog(String title, String message, @Nullable DialogInterface.OnClickListener listener) {
        GuiUtils.showOkDialog(title, message, listener, this);
    }
}
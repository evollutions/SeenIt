package cz.uhk.seenit;

import android.content.DialogInterface;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import cz.uhk.seenit.utils.GuiUtils;

public class BaseAppCompatActivity extends AppCompatActivity {

    public String getResString(@StringRes int id) {
        return GuiUtils.getResString(id, getResources());
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

    public void showDialog(@StringRes int titleId, @StringRes int messageId, @Nullable DialogInterface.OnClickListener positiveListener, @Nullable DialogInterface.OnClickListener negativeListener) {
        GuiUtils.showDialog(getResString(titleId), getResString(messageId), positiveListener, negativeListener, this);
    }
}
package cz.uhk.seenit.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import cz.uhk.seenit.utils.LogUtils;

public class BaseFragment extends Fragment {

    public String getResString(@StringRes int id) {
        try {
            return getResources().getString(id);
        } catch (Resources.NotFoundException exception) {
            LogUtils.LogError(exception);
            throw exception;
        }
    }

    public void showSnackbar(String text) {
        showSnackbar(text, BaseTransientBottomBar.LENGTH_LONG);
    }

    public void showSnackbar(@StringRes int id) {
        showSnackbar(getResString(id), BaseTransientBottomBar.LENGTH_LONG);
    }

    private void showSnackbar(String text, int length) {
        Snackbar.make(getActivity().findViewById(android.R.id.content), text, length)
                .show();
    }

    public void showToast(String text) {
        showToast(text, Toast.LENGTH_LONG);
    }

    public void showToast(@StringRes int id) {
        showToast(getResString(id), Toast.LENGTH_LONG);
    }

    private void showToast(String text, int length) {
        Toast.makeText(getContext(), text, length)
                .show();
    }

    public void showOkDialog(@StringRes int titleId, @StringRes int messageId, @Nullable DialogInterface.OnClickListener listener) {
        showOkDialog(getResString(titleId), getResString(messageId), listener);
    }

    public void showOkDialog(String title, String message, @Nullable DialogInterface.OnClickListener listener) {
        new AlertDialog.Builder(getContext())
                .setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("OK", listener)
                .show();
    }

    public boolean checkPermission(String permission) {
        return ContextCompat.checkSelfPermission(getContext(), permission) == PackageManager.PERMISSION_GRANTED;
    }
}
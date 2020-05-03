package cz.uhk.seenit.ui;

import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import cz.uhk.seenit.R;
import cz.uhk.seenit.utils.GuiUtils;
import cz.uhk.seenit.utils.Logger;

public class BaseFragment extends Fragment implements ActivityCompat.OnRequestPermissionsResultCallback {

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
        GuiUtils.showToast(text, length, getContext());
    }

    public void showDialog(@StringRes int titleId, @StringRes int messageId, @Nullable DialogInterface.OnClickListener positiveListener, @Nullable DialogInterface.OnClickListener negativeListener) {
        GuiUtils.showDialog(getResString(titleId), getResString(messageId), positiveListener, negativeListener, getContext());
    }

    public boolean checkPermission(String permission) {
        return ContextCompat.checkSelfPermission(getContext(), permission) == PackageManager.PERMISSION_GRANTED;
    }

    public boolean checkPermissions(String[] permissions) {
        for (String permission : permissions) {
            if (!checkPermission(permission)) {
                return false;
            }
        }

        return true;
    }

    public boolean shouldShowRequestPermissionRationale(String[] permissions) {
        for (String permission : permissions) {
            if (shouldShowRequestPermissionRationale(permission)) {
                return true;
            }
        }

        return false;
    }

    public void handlePermissions() {
        final String[] requiredPermissions = getRequiredPermissions();
        final int permissionsRequestCode = getPermissionRequestCode();

        if (checkPermissions(requiredPermissions)) {
            // Jiz mame potrebne povoleni a pokracujeme
            onPermissionsGranted();
        } else {
            // Nemame potrebne povoleni
            if (shouldShowRequestPermissionRationale(requiredPermissions)) {
                // Uzivatel v minulosti zvolil 'Deny' u nejakeho povoleni, dame mu vedet proc potrebujeme povoleni
                showDialog(R.string.alert, R.string.permissions_needed,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Po kliknuti na ano pozadame o povoleni
                                requestPermissions(requiredPermissions, permissionsRequestCode);
                            }
                        }, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Po kliknuti na ne provedeme callback
                                onPermissionsDenied();
                            }
                        });
            } else {
                // O povoleni jsme jeste nikdy nezadali, pozadame tedy rovnou
                requestPermissions(requiredPermissions, permissionsRequestCode);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        final int permissionsRequestCode = getPermissionRequestCode();

        if (requestCode == permissionsRequestCode) {
            boolean allPermissionsGranted = true;
            boolean atLeastOnePermissionGranted = false;

            for (int grantResult : grantResults) {
                if (grantResult == PackageManager.PERMISSION_DENIED) {
                    allPermissionsGranted = false;
                } else {
                    atLeastOnePermissionGranted = true;
                }
            }

            if (allPermissionsGranted) {
                // Mame potrebna povoleni, muzeme pokracovat
                onPermissionsGranted();
            } else if (atLeastOnePermissionGranted) {
                // Uzivatel nedal vsechna povoleni, ale alespon nejaka, opakujeme cele kolo znovu
                handlePermissions();
            } else {
                // Uzivatel nedal zadna povoleni, ukazeme mu hlasku a konec
                onPermissionsDenied();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public void onPermissionsGranted() {
        // Defaultne nic nedelej
    }

    public void onPermissionsDenied() {
        Logger.LogWarningAndShowToast(R.string.permissions_not_granted, null, getContext());
    }

    public String[] getRequiredPermissions() {
        return new String[]{};
    }

    public int getPermissionRequestCode() {
        return 0;
    }
}
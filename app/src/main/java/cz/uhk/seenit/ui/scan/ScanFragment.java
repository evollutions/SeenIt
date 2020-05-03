package cz.uhk.seenit.ui.scan;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.Collections;
import java.util.List;

import cz.uhk.seenit.R;
import cz.uhk.seenit.StickerDetailActivity;
import cz.uhk.seenit.model.Collected;
import cz.uhk.seenit.ui.BaseFragment;
import cz.uhk.seenit.utils.Logger;
import cz.uhk.seenit.utils.VolleyUtils;
import me.dm7.barcodescanner.zbar.BarcodeFormat;
import me.dm7.barcodescanner.zbar.Result;
import me.dm7.barcodescanner.zbar.ZBarScannerView;

import static android.content.Context.LOCATION_SERVICE;

public class ScanFragment extends BaseFragment implements ZBarScannerView.ResultHandler {

    // Vsechna potrebna povoleni pro skenovani samolepek
    private static final String[] REQUIRED_PERMISSIONS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.CAMERA
    };

    private static final int PERMISSION_REQUEST_CODE = 42;

    // Budeme skenovat pouze QR kody
    private static final List<BarcodeFormat> SCANNED_FORMATS = Collections.singletonList(BarcodeFormat.QRCODE);

    private ZBarScannerView scannerView;
    private LocationManager locationManager;

    private AppCompatTextView hint;
    private ProgressBar progressBar;

    // Pomocne promenne pro stavovost bez realneho API
    private String lastScanContent;
    private boolean stickerNotCollected = true;

    // Staticka URL fake JSON API pro sebrani naskenovane samolepky
    private static final String FAKE_URL = "https://my-json-server.typicode.com/evollutions/SeenIt/collect/";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_scan, container, false);

        // Vytvoreni skeneru a nastaveni skenovanych formatu pro optimalizaci
        scannerView = new ZBarScannerView(getContext());
        scannerView.setFormats(SCANNED_FORMATS);

        // Pridani skeneru do layoutu fragmentu
        ViewGroup contentFrame = root.findViewById(R.id.scan_view_finder);
        contentFrame.addView(scannerView);

        hint = root.findViewById(R.id.scan_hint);
        progressBar = root.findViewById(R.id.scan_progressbar);

        return root;
    }

    @Override
    public void onViewCreated(@NotNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Inicializace spravce lokace pro ziskani pozice uzivatele
        locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);

        // Vyreseni potrebnych povoleni v bazove tride
        handlePermissions();
    }

    @Override
    public void onResume() {
        super.onResume();

        // Obnoveni skenovani pokud mame povoleni
        if (checkPermissions(REQUIRED_PERMISSIONS)) {
            scannerView.setResultHandler(this);
            scannerView.startCamera();
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        // Stopnuti skenovani
        scannerView.stopCamera();
    }

    private void changeCollectionState(boolean collecting) {
        if (collecting) {
            hint.setText(R.string.scan_collecting);
            progressBar.setVisibility(View.VISIBLE);
        } else {
            hint.setText(R.string.scan_hint);
            progressBar.setVisibility(View.GONE);
        }
    }

    private void continueScan() {
        scannerView.resumeCameraPreview(this);
    }

    // Povoleni jsou osetrena, ale Android Studio to nedokaze detekovat
    @SuppressLint("MissingPermission")
    @Override
    public void handleResult(Result rawResult) {
        // Neco bylo naskenovano
        String scanContent = rawResult.getContents();

        if (scanContent.equals(lastScanContent)) {
            // Tento obsah byl jiz naskenovan a je tudiz ignorovan a skenujeme dal
            continueScan();
            return;
        }

        lastScanContent = scanContent;

        // Zobrazeni stavu uzivateli
        changeCollectionState(true);

        // Ziskani ID samolepky z naskenovaneho obsahu
        String stickerId = ScanContentValidator.getStickerId(scanContent);

        if (stickerId == null) {
            // ID samolepky nenalezeno, naskenovany obsah neni validni a tudiz skenujeme dal
            showToast(R.string.invalid_sticker);
            continueScan();
            return;
        }

        if (checkPermissions(REQUIRED_PERMISSIONS)) {
            // ID samolepky nalezeno, dame pozadavek na aktualizaci lokace uzivatele pro sebrani
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 100, locationUpdateListener);
        } else {
            // Nemame potrebna povoleni
            onPermissionsDenied();
        }
    }

    private final LocationListener locationUpdateListener = new LocationListener() {
        @Override
        public void onLocationChanged(@NotNull final Location location) {
            // Mame aktualni lokaci uzivatele, muzeme udelat request na sebrani samolepky
            // Dalsi aktualizaci lokace jiz nepotrebujeme
            locationManager.removeUpdates(this);

            int responseId;

            // Logika pro vyber jakou odpoved vratit, melo by resit realne API
            if (lastScanContent.equals(ScanContentValidator.DEMO_STICKER_CONTENT)) {
                if (stickerNotCollected) {
                    responseId = location.getLatitude() > 50.203 ? 1 : 2;
                } else {
                    responseId = 3;
                }
            } else {
                responseId = 4;
            }

            // Request je bez parametru kvuli fake API, jinak by jsme posilali lokaci uzivatele a ID samolepky
            VolleyUtils.MakeGetRequest(FAKE_URL + responseId, getCollectListener, getCollectErrorListener, getContext());
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onProviderDisabled(String provider) {
        }
    };

    private final Response.Listener<JSONObject> getCollectListener = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject response) {
            // Sebrani samolepky bylo uspesne
            // Prevedeni odpovedi na pouzitelny objekt
            Collected result = VolleyUtils.getJavaObjectFromJson(response, Collected.class);

            if (result.success) {
                // Samolepku jsme uspesne sebrali, zmen pomocnou promennou
                stickerNotCollected = false;

                // Zobrazeni detailu samolepky
                Intent intent = new Intent(getContext(), StickerDetailActivity.class);
                intent.putExtra(StickerDetailActivity.INTENT_STICKER_ID, result.stickerId);
                startActivity(intent);
            } else {
                // Samolepku jsme nesebrali a tudiz skenujeme dal
                continueScan();
            }

            showToast(result.message);
            changeCollectionState(false);
        }
    };

    private final Response.ErrorListener getCollectErrorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            // Sebrani samolepky bylo neuspesne
            Logger.LogErrorAndShowToast(R.string.could_not_collect_sticker, error, getContext());
        }
    };

    @Override
    public void onPermissionsDenied() {
        super.onPermissionsDenied();

        int pepa = 1;
    }

    @Override
    public String[] getRequiredPermissions() {
        return REQUIRED_PERMISSIONS;
    }

    @Override
    public int getPermissionRequestCode() {
        return PERMISSION_REQUEST_CODE;
    }
}
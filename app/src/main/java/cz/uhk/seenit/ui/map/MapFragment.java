package cz.uhk.seenit.ui.map;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat.OnRequestPermissionsResultCallback;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import cz.uhk.seenit.R;
import cz.uhk.seenit.StickerDetailActivity;
import cz.uhk.seenit.model.MarkerInfo;
import cz.uhk.seenit.model.Sticker;
import cz.uhk.seenit.model.StickersForLoc;
import cz.uhk.seenit.ui.BaseFragment;
import cz.uhk.seenit.utils.Logger;
import cz.uhk.seenit.utils.VolleyUtils;

import static android.content.Context.LOCATION_SERVICE;

public class MapFragment extends BaseFragment implements OnMapReadyCallback, OnMarkerClickListener, OnRequestPermissionsResultCallback {

    private static final String[] LOCATION_PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION};
    private static final int REQUEST_LOCATION_CODE = 1;

    // Staticka URL fake JSON serveru pro nacteni samolepek v okoli uzivatele
    private static final String URL = "https://my-json-server.typicode.com/evollutions/SeenIt/stickersForLoc/1";

    private GoogleMap map;
    private LocationManager locationManager;

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        handlePermissions();
    }

    private void handlePermissions() {
        // Handling opraveni na lokaci
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Jiz mame pristup k lokaci a pokracujeme
            initializeMap();
        } else {
            // Pristup k lokaci nemame, musime o nej pozadat
            if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Uzivatel klikl na 'Deny' ale ne na 'Don't show again', dame mu vedet proc potrebujeme povoleni
                showOkDialog(R.string.alert, R.string.need_permission_location, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Po kliku na OK pozadame
                        requestPermissions(LOCATION_PERMISSIONS, REQUEST_LOCATION_CODE);
                    }
                });
            } else {
                requestPermissions(LOCATION_PERMISSIONS, REQUEST_LOCATION_CODE);
            }
        }
    }

    private void initializeMap() {
        // Inicializace mapy s callbackem onMapReady
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Mapa je pripravena, je nacase ziskat lokaci uzivatele
        map = googleMap;

        // Zobrazeni tlacitka pro presun na lokaci uzivatele
        map.setMyLocationEnabled(true);

        // Listener kliknuti na marker pro zobrazeni detailu samolepky
        map.setOnMarkerClickListener(this);

        if (!checkPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Nemelo by nastat, ale Android Studio ten check proste vylozene chce!
            showToast(R.string.no_permission_location);
        } else {
            // Dame pozadavek na aktualizaci lokace uzivatele po 5 sekundach a 100 metrech
            locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 100, locationListener);
        }
    }

    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(@NotNull final Location location) {
            // Mame aktualni lokaci uzivatele, muzeme udelat request na samolepky v okoli
            // Request je bez parametru protoze pouzivame fake json api, jinak by jsme posilali lokaci uzivatele
            VolleyUtils.MakeGetRequest(URL, getStickersForLocListener, getStickersForLocErrorListener, getContext());

            // Pribliz kameru mapy na pozici uzivatele
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(location.getLatitude(), location.getLongitude()))
                    .zoom(14)
                    .build();
            map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
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

    private final Response.Listener<JSONObject> getStickersForLocListener = new Response.Listener<JSONObject>() {
        // Mame samolepky dostupne v okoli uzivatele
        @Override
        public void onResponse(JSONObject response) {
            // Preved odpoved na pouzitelny objekt
            StickersForLoc result = VolleyUtils.getJavaObjectFromJson(response, StickersForLoc.class);

            // Pro kazdou samolepku pridame na mapu marker co ji reprezentuje
            for (Sticker sticker : result.getStickers()) {
                // Barva markeru samolepky je zavisla na tom, jestli ji uzivatel jiz sebral
                float markerColor = sticker.isCollected()
                        ? BitmapDescriptorFactory.HUE_BLUE
                        : BitmapDescriptorFactory.HUE_RED;

                MarkerOptions markerOptions = new MarkerOptions()
                        .position(new LatLng(sticker.getLatitude(), sticker.getLongitude()))
                        .title(sticker.getName())
                        .icon(BitmapDescriptorFactory.defaultMarker(markerColor));

                Marker marker = map.addMarker(markerOptions);
                // Markeru nastavime tag abychom v listeneru vedeli jestli uzivatel samolepku sebral
                marker.setTag(new MarkerInfo(sticker.getId(), sticker.isCollected()));
            }

            // Nastaveni textu snackbaru podle poctu samolepek
            String stickerCountText = result.getStickers().isEmpty()
                    ? getResString(R.string.no_stickers_in_area)
                    : String.format(getResString(R.string.stickers_in_area), result.getStickers().size());

            // Zobrazime snackbar s vysledkem
            showSnackbar(stickerCountText);
        }
    };

    private final Response.ErrorListener getStickersForLocErrorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Logger.LogError(error);
            showToast(R.string.could_not_load_stickers_in_area);
        }
    };

    @Override
    public boolean onMarkerClick(@NotNull Marker marker) {
        MarkerInfo markerInfo = (MarkerInfo) marker.getTag();

        if (markerInfo.isCollected()) {
            Intent intent = new Intent(getActivity(), StickerDetailActivity.class);
            intent.putExtra(StickerDetailActivity.INTENT_STICKER_ID, markerInfo.getStickerId());
            startActivity(intent);
        } else {
            showSnackbar(R.string.sticker_not_collected);
        }

        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_LOCATION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Mame povoleni k lokaci, muzeme pokracovat
                initializeMap();
            } else {
                Logger.LogWarning(getResString(R.string.no_permission_location));
                showToast(R.string.no_permission_location);
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
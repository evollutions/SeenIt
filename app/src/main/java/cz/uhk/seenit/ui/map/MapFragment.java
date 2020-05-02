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
import cz.uhk.seenit.model.StickersForLoc;
import cz.uhk.seenit.ui.BaseFragment;
import cz.uhk.seenit.utils.Logger;
import cz.uhk.seenit.utils.VolleyUtils;

import static android.content.Context.LOCATION_SERVICE;

public class MapFragment extends BaseFragment implements OnMapReadyCallback, OnMarkerClickListener, OnRequestPermissionsResultCallback {

    private static final String[] LOCATION_PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION};
    private static final int REQUEST_LOCATION_CODE = 1;

    // Staticka URL fake JSON serveru pro nacteni samolepek v okoli uzivatele
    private static final String FAKE_URL = "https://my-json-server.typicode.com/evollutions/SeenIt/stickersForLoc/";

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

            // Fake logika pro vyber jake samolepky vratit (melo by resit realne API), 1 pro UHK, 2 pro lesy
            int locationId = location.getLatitude() > 50.203 ? 1 : 2;

            // Request je bez parametru protoze pouzivame fake json api, jinak by jsme posilali lokaci uzivatele
            VolleyUtils.MakeGetRequest(FAKE_URL + locationId, getStickersForLocListener, getStickersForLocErrorListener, getContext());

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

            // Vycistime mapu pro pripad ze uz mame markery na mape
            map.clear();

            // Pro kazdou samolepku pridame na mapu marker co ji reprezentuje
            for (StickersForLoc.Sticker sticker : result.stickers) {
                // Barva markeru samolepky je zavisla na tom, jestli ji uzivatel jiz sebral
                float markerColor = sticker.collected
                        ? BitmapDescriptorFactory.HUE_BLUE
                        : BitmapDescriptorFactory.HUE_RED;

                MarkerOptions markerOptions = new MarkerOptions()
                        .position(new LatLng(sticker.latitude, sticker.longitude))
                        .title(sticker.name)
                        .icon(BitmapDescriptorFactory.defaultMarker(markerColor));

                Marker marker = map.addMarker(markerOptions);
                // Markeru nastavime tag abychom v listeneru vedeli jestli uzivatel samolepku sebral
                marker.setTag(new MarkerInfo(sticker.id, sticker.collected));
            }

            // Nastaveni textu snackbaru podle poctu samolepek
            String stickerCountText = result.stickers.isEmpty()
                    ? getResString(R.string.no_stickers_in_area)
                    : String.format(getResString(R.string.stickers_in_area), result.stickers.size());

            // Zobrazime snackbar s vysledkem
            showSnackbar(stickerCountText);
        }
    };

    private final Response.ErrorListener getStickersForLocErrorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Logger.LogErrorAndShowToast(R.string.could_not_load_stickers_in_area, error, getContext());
        }
    };

    @Override
    public boolean onMarkerClick(@NotNull Marker marker) {
        MarkerInfo markerInfo = (MarkerInfo) marker.getTag();

        if (markerInfo.collected) {
            Intent intent = new Intent(getActivity(), StickerDetailActivity.class);
            intent.putExtra(StickerDetailActivity.INTENT_STICKER_ID, markerInfo.stickerId);
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
                Logger.LogWarningAndShowToast(R.string.no_permission_location, null, getContext());
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        locationManager.removeUpdates(locationListener);
    }
}
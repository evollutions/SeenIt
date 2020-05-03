package cz.uhk.seenit.ui.map;

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
import cz.uhk.seenit.utils.VolleyHelper;

import static android.content.Context.LOCATION_SERVICE;

public class MapFragment extends BaseFragment implements OnMapReadyCallback, OnMarkerClickListener {

    // Vsechna potrebna povoleni pro zobrazeni mapy
    private static final String[] REQUIRED_PERMISSIONS = {
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    private static final int PERMISSION_REQUEST_CODE = 69;

    // Staticka URL fake JSON API pro nacteni samolepek v okoli uzivatele
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

        // Inicializace spravce lokace pro ziskani pozice uzivatele
        locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);

        handlePermissions();
    }

    @Override
    public void onPermissionsGranted() {
        super.onPermissionsGranted();

        // Inicializace mapy s callbackem onMapReady
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    // Povoleni jsou osetrena, ale Android Studio to nedokaze detekovat
    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Mapa je pripravena, je nacase ziskat lokaci uzivatele
        map = googleMap;

        // Zobrazeni tlacitka pro presun na lokaci uzivatele
        map.setMyLocationEnabled(true);

        // Listener kliknuti na marker pro zobrazeni detailu samolepky
        map.setOnMarkerClickListener(this);

        if (checkPermissions(REQUIRED_PERMISSIONS)) {
            // Dame pozadavek na aktualizaci lokace uzivatele po 5 sekundach a 100 metrech
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 100, locationListener);
        } else {
            // Nemame potrebna povoleni
            onPermissionsDenied();
        }
    }

    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(@NotNull final Location location) {
            // Mame aktualni lokaci uzivatele, muzeme udelat request na samolepky v okoli

            // Logika pro vyber jake samolepky vratit (melo by resit realne API), 1 pro UHK, 2 pro lesy
            int locationId = location.getLatitude() > 50.203 ? 1 : 2;

            // Request je bez parametru protoze pouzivame fake JSON API, jinak by jsme posilali lokaci uzivatele
            VolleyHelper.MakeGetRequest(FAKE_URL + locationId, getStickersForLocListener, getStickersForLocErrorListener, getContext());

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
            StickersForLoc result = VolleyHelper.getJavaObjectFromJson(response, StickersForLoc.class);

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
            showToast(stickerCountText);
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
            showToast(R.string.sticker_not_collected);
        }

        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (locationManager != null) {
            locationManager.removeUpdates(locationListener);
        }
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
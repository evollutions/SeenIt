package cz.uhk.seenit.ui.map;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.Response.Listener;
import com.android.volley.Response.ErrorListener;
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
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONObject;

import cz.uhk.seenit.R;
import cz.uhk.seenit.model.Sticker;
import cz.uhk.seenit.model.StickersForLocAndUser;
import cz.uhk.seenit.utils.JsonRequest;

import static android.content.Context.LOCATION_SERVICE;

public class MapFragment extends Fragment implements OnMapReadyCallback, OnMarkerClickListener {

    private static final String[] LOCATION_PERMISSIONS = {
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    private static String url = "https://my-json-server.typicode.com/evollutions/SeenIt/stickersForLoc/1";

    private GoogleMap map;
    private LocationManager locationManager;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        // Tlacitko zobrazeni lokace uzivatele
        map.setMyLocationEnabled(true);

        // Callback pri kliknuti na marker
        map.setOnMarkerClickListener(this);

        locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Nemame pristup k lokaci
            requestPermissions(LOCATION_PERMISSIONS, 1);
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1, 1, locationListener);
    }

    public boolean onMarkerClick(Marker marker) {
        boolean collected = (boolean) marker.getTag();

        if (collected) {
            showSnackbar("Yeah");
        } else {
            showSnackbar("Nah");
        }

        return true;
    }

    private final LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(final Location location) {
            // Mame aktualni lokaci uzivatele, muzeme udelat request na samolepky v okoli
            // Request je bez parametru protoze pouzivame fake json api, jinak by jsme posilali lokaci uzivatele
            JsonRequest.Get(url, getStickersForLocListener, getStickersForLocErrorListener, getContext());

            // Pribliz kameru mapy na pozici uzivatele
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(location.getLatitude(), location.getLongitude()))
                    .zoom(14)
                    .build();
            map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        public void onProviderEnabled(String provider) {

        }

        public void onProviderDisabled(String provider) {

        }
    };

    private final Listener<JSONObject> getStickersForLocListener = new Listener<JSONObject>() {
        // Mame samolepky dostupne v okoli uzivatele
        public void onResponse(JSONObject response) {
            StickersForLocAndUser result = JsonRequest.getJavaObjectFromJson(response, StickersForLocAndUser.class);

            // Pro kazdou samolepku pridame na mapu marker co reprezentuje jeji pozici
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
                marker.setTag(sticker.isCollected());
            }

            // Nastaveni textu snackbaru podle poctu samolepek
            String stickerCountText = result.getStickers().isEmpty()
                    ? getResources().getString(R.string.no_stickers_in_area)
                    : String.format(getResources().getString(R.string.stickers_in_area), result.getStickers().size());

            // Zobrazime snackbar s vysledkem
            showSnackbar(stickerCountText);
        }
    };

    private final ErrorListener getStickersForLocErrorListener = new ErrorListener() {
        public void onErrorResponse(VolleyError error) {
            // TODO
        }
    };

    private void showSnackbar(String text) {
        Snackbar.make(getActivity().findViewById(android.R.id.content), text, BaseTransientBottomBar.LENGTH_LONG)
                .show();
    }
}
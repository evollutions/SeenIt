package cz.uhk.seenit.ui.map;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import cz.uhk.seenit.R;

public class MapFragment extends Fragment implements OnMapReadyCallback, OnMarkerClickListener {

    private static final String[] LOCATION_PERMISSIONS = {
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    private GoogleMap map;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Nemame pristup k lokaci
            requestPermissions(LOCATION_PERMISSIONS, 1);
        }

        View view = inflater.inflate(R.layout.fragment_map, container, false);

        return view;
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

        LatLng uhk = new LatLng(50.203666, 15.829925);
        map.addMarker(new MarkerOptions().position(uhk).title("Marker Title").snippet("Marker Description"));

        CameraPosition cameraPosition = new CameraPosition.Builder().target(uhk).zoom(12).build();
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        // Callback pri kliknuti na marker
        map.setOnMarkerClickListener(this);
    }

    public boolean onMarkerClick(Marker marker) {
        return false;
    }
}
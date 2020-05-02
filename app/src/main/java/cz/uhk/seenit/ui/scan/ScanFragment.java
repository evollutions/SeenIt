package cz.uhk.seenit.ui.scan;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import cz.uhk.seenit.R;
import cz.uhk.seenit.ui.BaseFragment;

public class ScanFragment extends BaseFragment {

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scan, container, false);

        TextView textView = view.findViewById(R.id.text_gallery);
        textView.setText("bla");

        return view;
    }
}
package cz.uhk.seenit.ui.overview;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONObject;

import cz.uhk.seenit.R;
import cz.uhk.seenit.model.OverviewForUser;
import cz.uhk.seenit.ui.BaseFragment;
import cz.uhk.seenit.utils.Logger;
import cz.uhk.seenit.utils.VolleyUtils;

public class OverviewFragment extends BaseFragment {

    private RecyclerView recyclerView;

    // Staticka URL fake JSON API pro nacteni overview uzivatele
    private static final String FAKE_URL = "https://my-json-server.typicode.com/evollutions/SeenIt/overviews/1";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_overview, container, false);

        recyclerView = root.findViewById(R.id.overview_collected_recycler);
        // Po nacteni se kolekce jiz nemeni
        recyclerView.setHasFixedSize(true);
        // Nastaveni layoutu pro recycler
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        return root;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Po vytvoreni pohledu udelame request na overview uzivatele
        VolleyUtils.MakeGetRequest(FAKE_URL, getOverviewForUserListener, getOverviewForUserErrorListener, getContext());
    }

    private final Response.Listener<JSONObject> getOverviewForUserListener = new Response.Listener<JSONObject>() {
        @Override
        // Nacteni prehledu pro uzivatele bylo uspesne
        public void onResponse(JSONObject response) {
            OverviewForUser result = VolleyUtils.getJavaObjectFromJson(response, OverviewForUser.class);

            // Nastaveni progress baru
            ProgressBar progressBar = getView().findViewById(R.id.overview_stats_progress);
            int collectedPercentage = (int) ((float) result.collectedStickerCount / result.totalStickerCount * 100.0);
            progressBar.setProgress(collectedPercentage);

            // Nastaveni statistik
            AppCompatTextView statsText = getView().findViewById(R.id.overview_stats_text);
            statsText.setText(String.format(getResString(R.string.collected_stickers_stats), result.collectedStickerCount, result.totalStickerCount));

            // Nastaveni adapteru recycleru s kolekci samolepek
            recyclerView.setAdapter(new OverviewCollectedAdapter(result.stickers, getContext()));
        }
    };

    private final Response.ErrorListener getOverviewForUserErrorListener = new Response.ErrorListener() {
        @Override
        // Nacteni prehledu pro uzivatele bylo neuspesne
        public void onErrorResponse(VolleyError error) {
            Logger.LogErrorAndShowToast(R.string.could_not_load_overview, error, getContext());
        }
    };
}
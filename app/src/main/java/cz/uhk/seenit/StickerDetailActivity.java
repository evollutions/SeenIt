package cz.uhk.seenit;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.NetworkImageView;

import org.json.JSONObject;

import cz.uhk.seenit.model.StickerDetail;
import cz.uhk.seenit.utils.Formatter;
import cz.uhk.seenit.utils.Logger;
import cz.uhk.seenit.utils.VolleyHelper;

public class StickerDetailActivity extends BaseAppCompatActivity {

    public static final String INTENT_STICKER_ID = "STICKER_ID";

    // Staticka URL fake JSON API pro nacteni detailu samolepky
    private static final String FAKE_URL = "https://my-json-server.typicode.com/evollutions/SeenIt/stickers/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sticker_detail);

        // Vytvoreni toolbaru
        Toolbar toolbar = findViewById(R.id.sticker_detail_toolbar);
        setSupportActionBar(toolbar);

        // Nutne pro navigaci zpet
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Ziskani ID samolepky z intentu
        Intent intent = getIntent();
        int stickerId = intent.getIntExtra(INTENT_STICKER_ID, -1);

        if (stickerId == -1) {
            // ID samolepky v intentu neni
            detailCouldNotBeLoaded(null);
        } else {
            // Mame ID samolepky, udelame request na detail
            VolleyHelper.MakeGetRequest(FAKE_URL + stickerId, getStickerDetailListener, getStickerDetailErrorListener, this);
        }
    }

    private final Response.Listener<JSONObject> getStickerDetailListener = new Response.Listener<JSONObject>() {
        @Override
        // Nacteni detailu samolepky bylo uspesne
        public void onResponse(JSONObject response) {
            // Mame detail samolepky, muzeme aktualizovat UI
            StickerDetail result = VolleyHelper.getJavaObjectFromJson(response, StickerDetail.class);

            // Nastaveni ikony samolepky
            NetworkImageView networkImageView = findViewById(R.id.sticker_detail_icon);
            networkImageView.setImageUrl(result.iconUrl.toString(), VolleyHelper.getImageLoader(getApplicationContext()));

            // Nastaveni jmena samolepky
            AppCompatTextView name = findViewById(R.id.sticker_detail_name);
            name.setText(result.name);

            // Nastaveni popisku samolepky
            AppCompatTextView description = findViewById(R.id.sticker_detail_desc_text);
            description.setText(result.desc);

            // Nastaveni data sebrani samolepky
            AppCompatTextView collectedDate = findViewById(R.id.sticker_detail_stats_collected_date);
            String collectedDateString = Formatter.formatDateShort(result.collectedDate);
            collectedDate.setText(String.format(getResString(R.string.collected_date), collectedDateString));

            // Nastaveni procenta sebrani samolepky uzivateli
            AppCompatTextView collectedBy = findViewById(R.id.sticker_detail_stats_collected_by);
            collectedBy.setText(String.format(getResString(R.string.collected_by), result.collectedByPercent));

            // Nastaveni posledniho data sebrani samolepky
            AppCompatTextView collectedLastDate = findViewById(R.id.sticker_detail_stats_collected_last_date);
            String collectedLastDateString = Formatter.formatDateShort(result.collectedLastDate);
            collectedLastDate.setText(String.format(getResString(R.string.collected_last_date), collectedLastDateString));
        }
    };

    private final Response.ErrorListener getStickerDetailErrorListener = new Response.ErrorListener() {
        @Override
        // Nacteni detailu samolepky bylo neuspesne
        public void onErrorResponse(VolleyError error) {
            detailCouldNotBeLoaded(error);
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // Pokud uzivatel klikne zpet, tak ukoncime aktivitu
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private final void detailCouldNotBeLoaded(@Nullable Exception exception) {
        // Nacteni detailu samolepky bylo neuspesne, informujeme uzivatele
        Logger.LogErrorAndShowToast(R.string.could_not_load_sticker_detail, exception, getApplicationContext());
        // Ukoncime aktivitu
        finish();
    }
}
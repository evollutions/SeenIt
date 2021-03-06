package cz.uhk.seenit.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;

import androidx.annotation.Nullable;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;

import org.json.JSONObject;

public class VolleyHelper {

    // Slouzi k vytvareni JSON requestu na fake JSON API
    private static RequestQueue requestQueue;

    // Slouzi k nacitani obrazku z URL do NetworkImageView
    private static ImageLoader imageLoader;

    private static Gson gson = new Gson();

    public static void MakeGetRequest(String url, Listener<JSONObject> responseListener, @Nullable ErrorListener errorListener, Context context) {
        // Vytvor GET request podle parametru
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, responseListener, errorListener);

        // Pridej request do fronty
        getRequestQueue(context).add(request);
    }

    private static RequestQueue getRequestQueue(Context context) {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }

        return requestQueue;
    }

    public static ImageLoader getImageLoader(Context context) {
        if (imageLoader == null) {
            imageLoader = new ImageLoader(getRequestQueue(context), new ImageLoader.ImageCache() {
                // Image loader vyuziva cache, ktera drzi maximalne 20 obrazku
                private final LruCache<String, Bitmap> cache = new LruCache<>(20);

                public void putBitmap(String url, Bitmap bitmap) {
                    cache.put(url, bitmap);
                }

                public Bitmap getBitmap(String url) {
                    return cache.get(url);
                }
            });
        }

        return imageLoader;
    }

    // Genericka metoda na prevod JSON objektu na Java objekt
    public static <T> T getJavaObjectFromJson(JSONObject jsonObject, Class<T> objectClass) {
        try {
            return gson.fromJson(jsonObject.toString(), objectClass);
        } catch (JsonParseException exception) {
            // Udelat neco pokud se nepovede prevod?
            Logger.LogError(exception);
            throw exception;
        }
    }
}
package cz.uhk.seenit.utils;

import android.content.Context;

import androidx.annotation.Nullable;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;

import org.json.JSONObject;

public class JsonRequest {

    private static Gson gson = new Gson();

    private static RequestQueue requestQueue;

    public static void Get(String url, Listener<JSONObject> responseListener, @Nullable ErrorListener errorListener, Context context) {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, responseListener, errorListener);

        getRequestQueue(context).add(request);
    }

    public static void Post(String url, Listener<JSONObject> responseListener, @Nullable ErrorListener errorListener, JSONObject jsonRequest, Context context) {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonRequest, responseListener, errorListener);

        getRequestQueue(context).add(request);
    }

    private static RequestQueue getRequestQueue(Context context) {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }
        return requestQueue;
    }

    // Genericka metoda na prevod JSON objektu na Java objekt
    public static <T> T getJavaObjectFromJson(JSONObject jsonObject, Class<T> objectClass) {
        try {
            return gson.fromJson(jsonObject.toString(), objectClass);
        } catch (JsonParseException exception) {
            // Udelat neco pokud se nepovede prevod?
            LogUtils.LogError(exception);
            throw exception;
        }
    }
}

/*
package cz.uhk.seenit.utils;

import android.content.Context;

import androidx.annotation.Nullable;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response.Listener;
import com.android.volley.Response.ErrorListener;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;


public class JsonRequest {

    private static JsonRequest jsonRequestInstace;
    private static RequestQueue requestQueue;
    private Context context;

    private JsonRequest(Context context) {
        this.context = context;
        requestQueue = Volley.newRequestQueue(context.getApplicationContext());
    }

    public static synchronized JsonRequest getInstance(Context context) {
        if (jsonRequestInstace == null) {
            jsonRequestInstace = new JsonRequest(context);
        }
        return jsonRequestInstace;
    }

    public void Get(String url, Listener<JSONObject> responseListener, @Nullable ErrorListener errorListener) {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, responseListener, errorListener);

        requestQueue.add(request);
    }
}
 */
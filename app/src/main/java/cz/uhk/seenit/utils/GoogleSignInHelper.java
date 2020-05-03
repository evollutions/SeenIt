package cz.uhk.seenit.utils;

import android.content.Context;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

import cz.uhk.seenit.R;

public class GoogleSignInHelper {

    // Slouzi k prihlaseni pomoci Google uctu
    private static GoogleSignInClient signInClient;

    public static GoogleSignInClient getInstance(Context context) {
        if (signInClient == null) {

            GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(context.getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();

            signInClient = GoogleSignIn.getClient(context, signInOptions);
        }

        return signInClient;
    }
}
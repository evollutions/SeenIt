package cz.uhk.seenit;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import cz.uhk.seenit.utils.GoogleSignInHelper;

public class SplashActivity extends BaseAppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Pokusime se o automaticke prihlaseni
        GoogleSignInClient signInClient = GoogleSignInHelper.getInstance(this);
        signInClient.silentSignIn().addOnCompleteListener(this, new OnCompleteListener<GoogleSignInAccount>() {
            @Override
            public void onComplete(@NonNull Task<GoogleSignInAccount> task) {
                if (task.isSuccessful()) {
                    // Automaticke prihlaseni probehlo uspesne
                    startMainActivity(task.getResult());
                    showToast(R.string.sign_in_successful);
                } else {
                    // Automaticke prihlaseni nebylo mozne
                    startSignInActivity();
                }
            }
        });
    }

    private void startMainActivity(GoogleSignInAccount account) {
        // Spustime hlavni aktivitu a predame ji ucet prihlaseneho uzivatele
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(MainActivity.INTENT_ACCOUNT, account);
        startActivity(intent);
        finish();
    }

    private void startSignInActivity() {
        // Spustime aktivitu manualniho prihlaseni
        Intent intent = new Intent(this, SignInActivity.class);
        startActivity(intent);
        finish();
    }
}
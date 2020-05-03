package cz.uhk.seenit;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.Task;

import cz.uhk.seenit.utils.GoogleSignInHelper;
import cz.uhk.seenit.utils.Logger;

public class SignInActivity extends BaseAppCompatActivity {

    private GoogleSignInClient googleSignInClient;

    private int REQUEST_CODE_SIGN_IN = 666;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sign_in);

        // Vytvoreni toolbaru
        Toolbar toolbar = findViewById(R.id.sign_in_toolbar);
        setSupportActionBar(toolbar);

        googleSignInClient = GoogleSignInHelper.getInstance(this);

        SignInButton signInButton = findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Po kliknuti spustime aktivitu prihlaseni
                Intent intent = googleSignInClient.getSignInIntent();
                startActivityForResult(intent, REQUEST_CODE_SIGN_IN);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_CODE_SIGN_IN) {
            // Prihlaseni ma vysledek
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            if (task.isSuccessful()) {
                // Manualni prihlaseni probehlo uspesne
                startMainActivity(task.getResult());
                showToast(R.string.sign_in_successful);
            } else {
                // Manualni prihlaseni selhalo
                Logger.LogWarningAndShowToast(R.string.sign_in_failed, task.getException(), this);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void startMainActivity(GoogleSignInAccount account) {
        // Spustime hlavni aktivitu a predame ji ucet prihlaseneho uzivatele
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(MainActivity.INTENT_ACCOUNT, account);
        startActivity(intent);
        finish();
    }
}
package cz.uhk.seenit;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.android.volley.toolbox.NetworkImageView;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.navigation.NavigationView;

import cz.uhk.seenit.utils.VolleyHelper;

public class MainActivity extends AppCompatActivity {

    public static final String INTENT_ACCOUNT = "ACCOUNT";

    private AppBarConfiguration appBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // Vytvoreni toolbaru
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        View navigationHeaderView = navigationView.getHeaderView(0);

        // Nastaveni potrebne pro navigaci
        appBarConfiguration = new AppBarConfiguration.Builder(R.id.nav_overview, R.id.nav_map, R.id.nav_scan)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        // Ziskani ID samolepky z intentu
        Intent intent = getIntent();
        GoogleSignInAccount account = intent.getParcelableExtra(INTENT_ACCOUNT);

        // Aktualizace UI
        if (account.getPhotoUrl() != null) {
            // Nastaveni profiloveho obrazku uzivatele
            NetworkImageView avatar = navigationHeaderView.findViewById(R.id.nav_header_avatar);
            avatar.setImageUrl(account.getPhotoUrl().toString(), VolleyHelper.getImageLoader(getApplicationContext()));
        }

        AppCompatTextView name = navigationHeaderView.findViewById(R.id.nav_header_name);
        name.setText(account.getDisplayName());

        AppCompatTextView email = navigationHeaderView.findViewById(R.id.nav_header_email);
        email.setText(account.getEmail());
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, appBarConfiguration) || super.onSupportNavigateUp();
    }
}
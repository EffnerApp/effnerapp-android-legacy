package de.effnerapp.effner.ui.activities.main;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import androidx.preference.PreferenceManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import de.effnerapp.effner.R;
import de.effnerapp.effner.data.dsbmobile.DSBClient;
import de.effnerapp.effner.data.utils.ApiClient;
import de.effnerapp.effner.ui.activities.splash.SplashActivity;
import de.effnerapp.effner.ui.fragments.substitutions.SubstitutionsFragment;

public class MainActivity extends AppCompatActivity {
    private static MainActivity instance;
    private long activityCreatedTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        activityCreatedTime = System.currentTimeMillis();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        setDarkMode(sharedPreferences.getBoolean("APP_DESIGN_DARK", false));

        setContentView(R.layout.activity_main);

        BottomNavigationView navView = findViewById(R.id.nav_view);

        // setup navView
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        NavController navController = Objects.requireNonNull(navHostFragment).getNavController();
        NavigationUI.setupWithNavController(navView, navController);

        TextView pageHeader = findViewById(R.id.page_text);
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            pageHeader.setText(destination.getLabel());

            // show labels and set home fragment checkable if we navigate back from the news fragment
            if (destination.getId() != R.id.navigation_news && destination.getId() != R.id.navigation_information && destination.getId() != R.id.navigation_timetable) {
                navView.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_SELECTED);
                navView.getMenu().getItem(0).setCheckable(true);
            }
        });

        // cancel all notifications
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        assert notificationManager != null;
        notificationManager.cancelAll();

        SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.swipeToRefresh);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            reloadData();
            swipeRefreshLayout.setRefreshing(false);
        });
    }

    private void setDarkMode(boolean enable) {
        // AppCompatDelegate.setDefaultNightMode(); causes also the finished activities to recreate which getDelegate().setLocalNightMode() doesn't.
        getDelegate().setLocalNightMode(enable ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (System.currentTimeMillis() - activityCreatedTime >= TimeUnit.MINUTES.toMillis(10)) {
            activityCreatedTime = System.currentTimeMillis();
            reloadData();
        }
    }

    private void reloadData() {
        ApiClient.getInstance().loadData((isSuccess, data) -> {
            if (!isSuccess || !data.getStatus().isLogin()) {
                if (!isSuccess) {
                    runOnUiThread(() -> Snackbar.make(findViewById(R.id.root), R.string.s_err_server_connection, BaseTransientBottomBar.LENGTH_LONG).setAction(R.string.button_retry, v -> reloadData()).show());
                } else if (data.getStatus().getMsg().equals("AUTHENTICATION_FAILED")) {
                    runOnUiThread(() -> Snackbar.make(findViewById(R.id.root), R.string.s_err_server_authentication, BaseTransientBottomBar.LENGTH_LONG).setAction(R.string.button_retry, v -> reloadData()).show());
                } else {
                    startActivity(new Intent(this, SplashActivity.class));
                    finish();
                }
            }
        });

        // TODO: handle success/error messages?
        new Thread(() -> DSBClient.getInstance().load(() -> {
            if (SubstitutionsFragment.getInstance() != null && SubstitutionsFragment.getInstance().isVisible()) {
                runOnUiThread(() -> SubstitutionsFragment.getInstance().onDataLoadFinished());
            }
        })).start();
    }

    public static MainActivity getInstance() {
        return instance;
    }

}

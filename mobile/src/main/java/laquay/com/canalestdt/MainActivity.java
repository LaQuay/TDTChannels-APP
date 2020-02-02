package laquay.com.canalestdt;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONException;
import org.json.JSONObject;

import laquay.com.canalestdt.controller.SharedPreferencesController;
import laquay.com.canalestdt.controller.VolleyController;
import laquay.com.canalestdt.utils.Utils;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = MainActivity.class.getSimpleName();

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            int id = item.getItemId();

            Fragment fragment = null;
            String fragmentTAG = null;

            if (id == R.id.nav_tv_channels) {
                fragment = TVFragment.newInstance();
                fragmentTAG = TVFragment.TAG;
            } else if (id == R.id.nav_radio_channels) {
                fragment = RadioFragment.newInstance();
                fragmentTAG = RadioFragment.TAG;
            } else if (id == R.id.nav_settings) {
                fragment = SettingsFragment.newInstance();
                fragmentTAG = SettingsFragment.TAG;
            }

            if (fragment != null) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.content_frame, fragment, fragmentTAG);
                ft.commit();
            }

            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SharedPreferencesController.init(this);

        BottomNavigationView navView = findViewById(R.id.nav_view);

        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navView.setSelectedItemId(R.id.nav_tv_channels);

        checkAppVersion();
    }

    private void checkAppVersion() {
        final String URL = "https://api.github.com/repos/laquay/TDTChannels-APP/releases/latest";
        final String versionName = BuildConfig.VERSION_NAME;
        final Context context = this;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                URL,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i(TAG, "Response OK - " + URL);

                        try {
                            String latestVersionName = response.getString("tag_name");

                            if (latestVersionName.equals(versionName)) {
                                AlertDialog.Builder alert = new AlertDialog.Builder(context);
                                alert.setTitle(getString(R.string.download_new_version_title));
                                alert.setMessage(getString(R.string.download_your_version_message) + ": " + versionName + "\n"
                                        + getString(R.string.download_latest_version_message) + ": " + latestVersionName);
                                alert.setPositiveButton(getString(R.string.download_yes), new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        Utils.launchIntent(context, Uri.parse("https://github.com/LaQuay/TDTChannels-APP/releases"));
                                    }
                                });
                                alert.setNegativeButton(getString(R.string.download_no), new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                    }
                                });
                                alert.show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Error while accessing to URL " + URL);
                    }
                }
        );

        // Add JsonArrayRequest to the RequestQueue
        VolleyController.getInstance(this).addToQueue(jsonObjectRequest);
    }
}

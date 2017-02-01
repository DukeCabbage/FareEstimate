package dispatch.digital.fareestimater;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.app_bar_layout) AppBarLayout mAppBarLayout;
    @BindView(R.id.toolbar) Toolbar mToolbar;

    SupportMapFragment mMapFragment;
    GoogleMap mGoogleMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setUpAppBar();

        FragmentManager fm = getSupportFragmentManager();
        mMapFragment = (SupportMapFragment) fm.findFragmentById(R.id.map);
        if (mMapFragment == null) {
            GoogleMapOptions options = new GoogleMapOptions();
            options.rotateGesturesEnabled(false);
            options.tiltGesturesEnabled(false);
            options.scrollGesturesEnabled(true);
            options.zoomGesturesEnabled(true);

            mMapFragment = SupportMapFragment.newInstance(options);
            fm.beginTransaction().replace(R.id.map, mMapFragment).commit();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!isMapReady()) {
            if (mMapFragment == null) throw new RuntimeException("Map fragment null");
            mMapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    if (googleMap == null) {
                        Timber.e("Can not get map");
                    } else {
                        Timber.i("Map ready");
                        mGoogleMap = googleMap;
                        if (AndroidApiUtils.locationPermissionGranted(MainActivity.this)) {
                            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                // TODO: Consider calling
                                //    ActivityCompat#requestPermissions
                                // here to request the missing permissions, and then overriding
                                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                //                                          int[] grantResults)
                                // to handle the case where the user grants the permission. See the documentation
                                // for ActivityCompat#requestPermissions for more details.
                                return;
                            }
                            mGoogleMap.setMyLocationEnabled(true);
                            mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
                        }
                    }
                }
            });
        }
    }

    private void setUpAppBar() {
        if (mAppBarLayout != null && AndroidApiUtils.hasLollipop()) {
            mAppBarLayout.setPadding(0, AndroidApiUtils.getStatusBarHeight(this), 0, 0);
        }
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Hello World!");
        } else {
            throw new RuntimeException("Can not find toolbar");
        }
    }

    private boolean isMapReady() {
        return !(mMapFragment == null || mMapFragment.getView() == null || mGoogleMap == null);
    }
}

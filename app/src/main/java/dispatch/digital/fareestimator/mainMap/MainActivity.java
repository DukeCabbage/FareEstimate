package dispatch.digital.fareestimator.mainMap;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.tbruyelle.rxpermissions2.RxPermissions;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dispatch.digital.fareestimator.AndroidApiUtils;
import dispatch.digital.fareestimator.R;
import dispatch.digital.fareestimator.searchAddress.SearchAddressActivity;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity
        implements MainMapContract.View {

    //    @BindView(R.id.search_bar) ViewGroup mSearchBar;
    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.tv_destination) TextView tvDestination;

    SupportMapFragment mMapFragment;
    GoogleMap mGoogleMap;
    MainMapContract.Presenter mainPresenter;

    @OnClick(R.id.toolbar)
    void searchAddress() {
        Intent intent = new Intent(this, SearchAddressActivity.class);
        startActivity(intent);
    }

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

        mainPresenter = new MainPresenter(this, this);
    }

    @Override
    public void onStart() {
        super.onStart();
        mainPresenter.start();
        mMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                if (googleMap == null) {
                    Timber.e("Map not found");
                    return;
                }

                Timber.i("Map ready");
                mGoogleMap = googleMap;
                mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
                if (AndroidApiUtils.hasGetLocationPermission(MainActivity.this)) {
                    //noinspection MissingPermission
                    mGoogleMap.setMyLocationEnabled(true);
                }

                int topPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 80, getResources().getDisplayMetrics());
                int bottomPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12, getResources().getDisplayMetrics());
                mGoogleMap.setPadding(0, topPadding, 0, bottomPadding);

                Location currentLocation = mainPresenter.getCurrentLocation();
                if (currentLocation != null) {
                    goToLocation(false, currentLocation);
                } else {
                    Timber.w("Location service is not ready or no available");
                }
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        mainPresenter.stop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_my_location) {
            Location myLocation = mainPresenter.getCurrentLocation();
            goToLocation(true, myLocation);
            return true;
        } else if (id == R.id.action_log_out) {
            Toast.makeText(this, "Log out", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setUpAppBar() {
//        if (mAppBarLayout != null && AndroidApiUtils.hasLollipop()) {
//            mAppBarLayout.setPadding(0, AndroidApiUtils.getStatusBarHeight(this), 0, 0);
//        }
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("");
        } else {
            throw new RuntimeException("Can not find toolbar");
        }
    }

    @Override
    public void goToLocation(boolean animate, Location location) {
        if (mGoogleMap == null) {
            Timber.w("Google map is not ready or not available");
            return;
        }

        if (location != null) {
            LatLng currLatLng = new LatLng(location.getLatitude(), location.getLongitude());

            CameraPosition currentPosition = new CameraPosition.Builder().target(currLatLng).zoom(15f).bearing(0).tilt(0).build();
            CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(currentPosition);

            if (animate) {
                mGoogleMap.animateCamera(cameraUpdate);
            } else {
                mGoogleMap.moveCamera(cameraUpdate);
            }
        }
    }

    @Override
    public void askForLocationPermission() {
        new RxPermissions(this)
                .request(Manifest.permission.ACCESS_FINE_LOCATION)
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {}

                    @Override
                    public void onNext(Boolean granted) {
                        if (granted) {
                            mainPresenter.startLocationUpdates();

                            if (mGoogleMap != null) {
                                //noinspection MissingPermission
                                mGoogleMap.setMyLocationEnabled(true);
                            }
                        } else {
                            Timber.w("Get location permission not granted");
                            Toast.makeText(MainActivity.this, "Get location permission not granted", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {e.printStackTrace();}

                    @Override
                    public void onComplete() {}
                });
    }


    @Override
    public void askForLocationService(com.google.android.gms.common.api.Status status) {
        try {
            status.startResolutionForResult(this, MainMapContract.REQUEST_CODE_LOCATION_SERVICE);
        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case MainMapContract.REQUEST_CODE_LOCATION_SERVICE:
                    mainPresenter.startLocationUpdates();
                    break;
            }
        }
    }
}

package dispatch.digital.fareestimator.mainMap;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import dispatch.digital.fareestimator.AndroidApiUtils;
import dispatch.digital.fareestimator.R;
import timber.log.Timber;

public class MainPresenter implements MainMapContract.Presenter,
                                      GoogleApiClient.ConnectionCallbacks,
                                      GoogleApiClient.OnConnectionFailedListener {

    final private GoogleApiClient mGoogleApiClient;
    final private Context mContext;
    final private MainMapContract.View mView;
    final private LocationListener mLocationListener;

    private Location mLastLocation;

    public MainPresenter(@NonNull Context context, @NonNull final MainMapContract.View mainView) {
        this.mContext = context;
        this.mView = mainView;
        this.mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        this.mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                // First time try not to animate
                mainView.goToLocation(mLastLocation != null, location);
                mLastLocation = location;
            }
        };
    }

    @Override
    public void start() {
        if (mGoogleApiClient != null)
            mGoogleApiClient.connect();
    }

    @Override
    public void stop() {
        stopLocationUpdates();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public Location getCurrentLocation() {
        return mLastLocation;
    }

    /**
     * Check the following requirements in the these orders,
     * 1. Google client is connected
     * 2. Location service is turned on, if not call {@link MainMapContract.View#askForLocationService(Status)}
     * 3. Permission to access location, if not ask {@link MainMapContract.View#askForLocationPermission()}
     *
     * If everything checks out, start periodic location update
     */
    @Override
    public void startLocationUpdates() {
        if (mGoogleApiClient == null || !mGoogleApiClient.isConnected()) {
            Timber.e("Google api client not connected");
            return;
        }

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(createLocationRequest());
        final PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());

        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
                final Status status = locationSettingsResult.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        if (AndroidApiUtils.hasGetLocationPermission(mContext)) {
                            //noinspection MissingPermission
                            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, createLocationRequest(), mLocationListener);
                        } else {
                            mView.askForLocationPermission();
                        }
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        mView.askForLocationService(status);
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Timber.e("LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE");
                        break;
                }
            }
        });
    }

    private LocationRequest createLocationRequest() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setNumUpdates(1);
        locationRequest.setExpirationDuration(10000);//10 sec
        return locationRequest;
    }

    /**
     * It is a good practice to remove location requests when the activity is in a paused or
     * stopped state. Doing so helps battery performance and is especially
     * recommended in applications that request frequent location updates.
     */
    private void stopLocationUpdates() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, mLocationListener);
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
        if (mGoogleApiClient != null) mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(mContext, mContext.getString(R.string.err_cannot_connect_google_client), Toast.LENGTH_SHORT).show();
    }
}

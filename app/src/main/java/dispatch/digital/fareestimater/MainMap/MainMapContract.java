package dispatch.digital.fareestimater.MainMap;

import android.location.Location;

public interface MainMapContract {

    int REQUEST_CODE_LOCATION_SERVICE = 111;

    interface View {

        void goToLocation(boolean animate, Location location);

        void askForLocationPermission();

        void askForLocationService(com.google.android.gms.common.api.Status status);
    }

    interface Presenter {
        void start();

        void stop();

        Location getCurrentLocation();

        void startLocationUpdates();
    }
}

package dispatch.digital.fareestimator.injection.appScope;


import android.content.Context;
import android.content.SharedPreferences;

import javax.inject.Singleton;

import dagger.Component;
import dispatch.digital.fareestimator.googleApi.place.PlaceApi;

@Singleton
@Component(modules = {
        AppModule.class,
        NetworkModule.class,
        GoogleApiModule.class
})

public interface AppComponent {

    Context appContext();

    SharedPreferences sharedPreferences();

    PlaceApi placeApi();
}

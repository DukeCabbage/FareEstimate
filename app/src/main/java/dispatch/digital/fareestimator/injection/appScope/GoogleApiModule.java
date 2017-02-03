package dispatch.digital.fareestimator.injection.appScope;

import com.facebook.stetho.okhttp3.StethoInterceptor;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dispatch.digital.fareestimator.googleApi.GoogleMapsAPIAuthInterceptor;
import dispatch.digital.fareestimator.googleApi.PlaceApi;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
public class GoogleApiModule {

    protected static final String GOOGLE_MAP_END_POINT = "https://maps.googleapis.com/";

    @Provides
    @Singleton
    PlaceApi providePlaceApi(@Named("Google") Retrofit retrofit) {
        return retrofit.create(PlaceApi.class);
    }

    @Provides
    @Singleton
    @Named("Google")
    protected Retrofit provideGoogleRetrofit(GsonConverterFactory gsonConverterFactory,
                                             RxJavaCallAdapterFactory rxJavaCallAdapterFactory,
                                             HttpLoggingInterceptor logging) {

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new GoogleMapsAPIAuthInterceptor(GoogleMapsAPIAuthInterceptor.AuthMethod.API_KEY))
                .addInterceptor(logging)
                .addNetworkInterceptor(new StethoInterceptor())
                .build();

        return new Retrofit.Builder()
                .baseUrl(GOOGLE_MAP_END_POINT)
                .client(client)
                .addConverterFactory(gsonConverterFactory)
                .addCallAdapterFactory(rxJavaCallAdapterFactory)
                .build();
    }
}

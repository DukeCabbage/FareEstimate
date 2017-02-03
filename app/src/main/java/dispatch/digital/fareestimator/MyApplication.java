package dispatch.digital.fareestimator;

import android.app.Application;
import android.content.Context;

import com.facebook.stetho.Stetho;

import dispatch.digital.fareestimator.injection.appScope.AppComponent;
import dispatch.digital.fareestimator.injection.appScope.AppModule;
import dispatch.digital.fareestimator.injection.appScope.DaggerAppComponent;
import timber.log.Timber;

// TODO: Have different build variants, and extend from a base class
public class MyApplication extends Application {

    private AppComponent appComponent;

    public static synchronized MyApplication getInstance(Context context) {
        return (MyApplication) context.getApplicationContext();
    }

    public AppComponent component() {
        return appComponent;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        setUpAppComponent();
        setUpLogging();
    }

    protected void setUpAppComponent() {
        appComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .build();
    }

    protected void setUpLogging() {
        Stetho.initializeWithDefaults(this);
        Timber.plant(new Timber.DebugTree() {
            //add line number to the tag
            @Override
            protected String createStackElementTag(StackTraceElement element) {
                return super.createStackElementTag(element) + ':' + element.getLineNumber();
            }
        });
    }
}

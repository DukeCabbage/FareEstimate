package dispatch.digital.fareestimater;

import android.app.Application;

import com.facebook.stetho.Stetho;

import timber.log.Timber;


public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        setUpLogging();
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

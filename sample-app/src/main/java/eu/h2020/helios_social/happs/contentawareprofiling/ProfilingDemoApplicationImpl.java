package eu.h2020.helios_social.happs.contentawareprofiling;

import android.app.Application;

public class ProfilingDemoApplicationImpl extends Application
        implements ProfilingDemoApplication {
    private AndroidComponent applicationComponent;

    public void onCreate() {
        super.onCreate();
        applicationComponent = createApplicationComponent();
    }


    @Override
    public AndroidComponent getApplicationComponent() {
        return applicationComponent;
    }

    private AndroidComponent createApplicationComponent() {
        return DaggerAndroidComponent.builder()
                .appModule(new AppModule(this))
                .build();
    }

}

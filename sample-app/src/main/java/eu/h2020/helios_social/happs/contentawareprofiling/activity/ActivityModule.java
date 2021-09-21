package eu.h2020.helios_social.happs.contentawareprofiling.activity;

import android.app.Activity;

import dagger.Module;
import dagger.Provides;

@Module
public class ActivityModule {

    private final BaseActivity activity;

    public ActivityModule(BaseActivity activity) {
        this.activity = activity;
    }

    @ActivityScope
    @Provides
    BaseActivity provideBaseActivity() {
        return activity;
    }

    @ActivityScope
    @Provides
    Activity provideActivity() {
        return activity;
    }

}

package eu.h2020.helios_social.happs.contentawareprofiling.activity;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.logging.Logger;

import eu.h2020.helios_social.happs.contentawareprofiling.AndroidComponent;
import eu.h2020.helios_social.happs.contentawareprofiling.ProfilingDemoApplication;

import static java.util.logging.Level.INFO;
import static java.util.logging.Logger.getLogger;

public abstract class BaseActivity extends AppCompatActivity {

    private final static Logger LOG = getLogger(BaseActivity.class.getName());

    protected ActivityComponent activityComponent;


    public abstract void injectActivity(ActivityComponent component);

    @Override
    public void onCreate(@Nullable Bundle state) {
        AndroidComponent applicationComponent =
                ((ProfilingDemoApplication) getApplication())
                        .getApplicationComponent();
        activityComponent = DaggerActivityComponent.builder()
                .androidComponent(applicationComponent)
                .activityModule(getActivityModule())
                .build();
        injectActivity(activityComponent);
        super.onCreate(state);
        if (LOG.isLoggable(INFO)) {
            LOG.info("Creating " + getClass().getSimpleName());
        }
    }

    public ActivityComponent getActivityComponent() {
        return activityComponent;
    }
    // This exists to make test overrides easier
    protected ActivityModule getActivityModule() {
        return new ActivityModule(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (LOG.isLoggable(INFO)) {
            LOG.info("Starting " + getClass().getSimpleName());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (LOG.isLoggable(INFO)) {
            LOG.info("Resuming " + getClass().getSimpleName());
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (LOG.isLoggable(INFO)) {
            LOG.info("Pausing " + getClass().getSimpleName());
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (LOG.isLoggable(INFO)) {
            LOG.info("Stopping " + getClass().getSimpleName());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (LOG.isLoggable(INFO)) {
            LOG.info("Destroying " + getClass().getSimpleName());
        }
    }
}

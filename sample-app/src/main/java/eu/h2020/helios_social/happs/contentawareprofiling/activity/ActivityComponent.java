package eu.h2020.helios_social.happs.contentawareprofiling.activity;

import android.app.Activity;

import dagger.Component;
import eu.h2020.helios_social.happs.contentawareprofiling.AndroidComponent;
import eu.h2020.helios_social.happs.contentawareprofiling.ImageListActivity;
import eu.h2020.helios_social.happs.contentawareprofiling.MainActivity;
import eu.h2020.helios_social.happs.contentawareprofiling.activepeers.ActivePeersActivity;

@ActivityScope
@Component(
        modules = {
                ActivityModule.class},
        dependencies = AndroidComponent.class)
public interface ActivityComponent {

    Activity activity();

    void inject(MainActivity mainActivity);

    void inject(ActivePeersActivity activePeersActivity);

    void inject(ImageListActivity imageListActivity);
}

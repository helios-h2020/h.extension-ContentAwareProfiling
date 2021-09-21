package eu.h2020.helios_social.happs.contentawareprofiling;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;

import androidx.work.Configuration;
import androidx.work.WorkManager;

import org.jetbrains.annotations.NotNull;
import org.tensorflow.lite.Tensor;

import java.io.File;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import eu.h2020.helios_social.core.contextualegonetwork.ContextualEgoNetwork;
import eu.h2020.helios_social.core.contextualegonetwork.Interaction;
import eu.h2020.helios_social.core.contextualegonetwork.Utils;
import eu.h2020.helios_social.core.contextualegonetwork.listeners.CreationListener;
import eu.h2020.helios_social.core.contextualegonetwork.listeners.LoggingListener;
import eu.h2020.helios_social.core.contextualegonetwork.listeners.RecoveryListener;
import eu.h2020.helios_social.core.messaging_nodejslibp2p.HeliosMessagingNodejsLibp2p;
import eu.h2020.helios_social.modules.contentawareprofiling.ContentAwareProfileManager;
import eu.h2020.helios_social.happs.contentawareprofiling.preferences.SharedPreferencesHelper;
import eu.h2020.helios_social.happs.contentawareprofiling.profiling.CustomWorkerFactory;
import eu.h2020.helios_social.happs.contentawareprofiling.storage.ContextualEgoNetworkConfig;
import eu.h2020.helios_social.happs.contentawareprofiling.storage.InternalStorageConfig;

import static android.content.Context.MODE_PRIVATE;

@Module
public class AppModule {

    private final Application application;
    private final String PREF_FILE_NAME = "content-aware-profiling-prefs-file";

    public AppModule(Application application) {
        this.application = application;
    }

    @Provides
    @Singleton
    Application provideApplication() {
        return application;
    }

    @Provides
    InternalStorageConfig provideInternalStorageConfig(
            Application app) {
        File internalStorageDir =
                app.getApplicationContext().getDir("egonetwork", MODE_PRIVATE);
        return new ContextualEgoNetworkConfig(internalStorageDir);

    }

    @Provides
    AssetManager provideAssetManager(
            Application app) {
        return app.getAssets();
    }

    @Provides
    Context provideContext(Application app) {
        return app.getApplicationContext();
    }

    @Provides
    @Singleton
    ContextualEgoNetwork provideContextualEgoNetwork(
            InternalStorageConfig config) {
        Utils.development = true;
        ContextualEgoNetwork egoNetwork =
                ContextualEgoNetwork.createOrLoad(
                        config.getStorageDir().getPath().toString() +
                                File.separator, "ego", null
                );
        egoNetwork.addListener(
                new RecoveryListener());//automatic saving with minimal overhead
        egoNetwork.addListener(new CreationListener());//keep timestamps
        egoNetwork.addListener(new LoggingListener());//print events
        //Some needed non-sense
        Interaction.class.getDeclaredConstructors();
        Tensor.class.getDeclaredConstructors();

        egoNetwork.setCurrent(egoNetwork.getOrCreateContext("All"));
        return egoNetwork;
    }

    @Provides
    @Singleton
    CustomWorkerFactory workerCustomWorkerFactory(ContextualEgoNetwork egoNetwork, ContentAwareProfileManager profileManager) {
        return new CustomWorkerFactory(egoNetwork, profileManager);
    }

    @Provides
    @Singleton
    WorkManager provideWorkManager(Application app, CustomWorkerFactory customWorkerFactory) {
        Configuration config = new Configuration.Builder()
                .setWorkerFactory(customWorkerFactory)
                .build();
        WorkManager.initialize(app, config);
        return WorkManager.getInstance(app);
    }

    @Provides
    @Singleton
    HeliosMessagingNodejsLibp2p provideHeliosMessaging() {
        return HeliosMessagingNodejsLibp2p.getInstance();
    }

    @Provides
    @Singleton
    SharedPreferencesHelper provideUsernameHelper(@NotNull Application app) {
        SharedPreferences prefManager = app.getApplicationContext().getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
        return new SharedPreferencesHelper(prefManager);
    }

    @Provides
    @Singleton
    ContentAwareProfileManager provideContentAwareProfileManager(@NotNull Application app, ContextualEgoNetwork egoNetwork) {
        return new ContentAwareProfileManager(app.getApplicationContext(), egoNetwork);
    }
}

package eu.h2020.helios_social.happs.contentawareprofiling.profiling;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.work.Worker;
import androidx.work.WorkerFactory;
import androidx.work.WorkerParameters;

import java.lang.reflect.Constructor;
import java.util.logging.Logger;

import javax.inject.Inject;

import eu.h2020.helios_social.core.contextualegonetwork.ContextualEgoNetwork;
import eu.h2020.helios_social.modules.contentawareprofiling.ContentAwareProfileManager;

import static java.util.logging.Logger.getLogger;

/**
 * CustomWorkerFactory is responsible for initializing workers and pass additional variables to workers
 */
public class CustomWorkerFactory extends WorkerFactory {
    private static String TAG = ProfilingWorker.class.getName();
    private final static Logger LOG = getLogger(TAG);

    private ContextualEgoNetwork egoNetwork;
    private ContentAwareProfileManager profileManager;

    @Inject
    public CustomWorkerFactory(ContextualEgoNetwork egoNetwork, ContentAwareProfileManager profileManager) {
        this.egoNetwork = egoNetwork;
        this.profileManager = profileManager;
    }

    @Nullable
    @Override
    public Worker createWorker(@NonNull Context appContext, @NonNull String workerClassName, @NonNull WorkerParameters workerParameters) {
        try {
            Worker worker;

            //allows custom construction of ProfilingWorker
            if (workerClassName.equals(ProfilingWorker.class.getName())) {
                Constructor<? extends Worker> constructor = Class.forName(workerClassName)
                        .asSubclass(Worker.class).getDeclaredConstructor(Context.class, WorkerParameters.class, ContextualEgoNetwork.class, ContentAwareProfileManager.class);
                worker = constructor.newInstance(appContext, workerParameters, egoNetwork, profileManager);
            } else {
                //Default construction of other workers
                Constructor<? extends Worker> constructor = Class.forName(workerClassName)
                        .asSubclass(Worker.class).getDeclaredConstructor(Context.class, WorkerParameters.class);
                worker = constructor.newInstance(appContext, workerParameters);
            }
            return worker;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

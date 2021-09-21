package eu.h2020.helios_social.happs.contentawareprofiling;

import androidx.work.WorkManager;

import javax.inject.Singleton;

import dagger.Component;
import eu.h2020.helios_social.core.contextualegonetwork.ContextualEgoNetwork;
import eu.h2020.helios_social.core.messaging_nodejslibp2p.HeliosMessagingNodejsLibp2p;
import eu.h2020.helios_social.happs.contentawareprofiling.communication.CommunicationManager;
import eu.h2020.helios_social.happs.contentawareprofiling.preferences.SharedPreferencesHelper;

@Singleton
@Component(modules = {
        AppModule.class,
})
public interface AndroidComponent {

    ContextualEgoNetwork egoNetwork();

    WorkManager workManager();

    SharedPreferencesHelper sharedPreferencesHelper();

    HeliosMessagingNodejsLibp2p heliosMessaging();

    void inject(CommunicationManager communicationManager);
}

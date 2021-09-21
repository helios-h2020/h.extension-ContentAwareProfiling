package eu.h2020.helios_social.happs.contentawareprofiling.communication;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.github.javafaker.Faker;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

import javax.inject.Inject;

import eu.h2020.helios_social.core.contextualegonetwork.ContextualEgoNetwork;
import eu.h2020.helios_social.core.messaging.HeliosConnectionInfo;
import eu.h2020.helios_social.core.messaging.HeliosIdentityInfo;
import eu.h2020.helios_social.core.messaging_nodejslibp2p.HeliosEgoTag;
import eu.h2020.helios_social.core.messaging_nodejslibp2p.HeliosMessagingNodejsLibp2p;
import eu.h2020.helios_social.core.messaging_nodejslibp2p.HeliosMessagingReceiver;
import eu.h2020.helios_social.modules.contentawareprofiling.ContentAwareProfileManager;
import eu.h2020.helios_social.modules.contentawareprofiling.model.ModelType;
import eu.h2020.helios_social.modules.contentawareprofiling.profile.CoarseInterestsProfile;
import eu.h2020.helios_social.modules.contentawareprofiling.profile.ContentAwareProfile;
import eu.h2020.helios_social.modules.contentawareprofiling.profile.DMLProfile;
import eu.h2020.helios_social.modules.contentawareprofiling.profile.FineInterestsProfile;
import eu.h2020.helios_social.happs.contentawareprofiling.ProfilingDemoApplication;
import eu.h2020.helios_social.happs.contentawareprofiling.event.NewTagEvent;
import eu.h2020.helios_social.happs.contentawareprofiling.event.RequestTagsEvent;
import eu.h2020.helios_social.happs.contentawareprofiling.event.SendContentAwareProfileEvent;
import eu.h2020.helios_social.happs.contentawareprofiling.event.SendMessageFailedEvent;
import eu.h2020.helios_social.happs.contentawareprofiling.event.SendProfileMatchingScoreEvent;
import eu.h2020.helios_social.happs.contentawareprofiling.preferences.SharedPreferencesHelper;
import eu.h2020.helios_social.happs.contentawareprofiling.profiling.ContentAwareProfileMessage;
import kotlin.Unit;

import static android.content.Intent.ACTION_SHUTDOWN;
import static eu.h2020.helios_social.happs.contentawareprofiling.communication.CommunicationConstants.APP_TAG;
import static eu.h2020.helios_social.happs.contentawareprofiling.communication.CommunicationConstants.CONTENT_AWARE_PROFILE_RECEIVER_ID;
import static eu.h2020.helios_social.happs.contentawareprofiling.communication.CommunicationConstants.EGO_ID;
import static eu.h2020.helios_social.happs.contentawareprofiling.communication.CommunicationConstants.PEERID;
import static eu.h2020.helios_social.happs.contentawareprofiling.communication.CommunicationConstants.PROFILE_MATCHING_SCORE_RECEIVER_ID;
import static eu.h2020.helios_social.happs.contentawareprofiling.communication.CommunicationConstants.USERNAME;

/**
 * CommunicationManager is a Service responsible for connecting to helios network and announcing an
 * app-specific tag that allows the demonstration of the content-aware-profiling module to exchange
 * the calculated content-aware profiles
 */
public class CommunicationManager extends Service {

    private static final Logger LOG =
            Logger.getLogger(CommunicationManager.class.getName());

    @Inject
    HeliosMessagingNodejsLibp2p heliosMessaging;
    @Inject
    SharedPreferencesHelper preferencesHelper;
    @Inject
    ContextualEgoNetwork egoNetwork;
    @Inject
    ContentAwareProfileManager profileManager;

    @Nullable
    private BroadcastReceiver receiver = null;
    private final AtomicBoolean created = new AtomicBoolean(false);
    private ProfilingDemoApplication app;
    private HeliosConnectionInfo connectionInfo;
    private HeliosIdentityInfo identityInfo;


    @Override
    public void onCreate() {
        super.onCreate();

        app = (ProfilingDemoApplication) getApplication();
        app.getApplicationComponent().inject(this);

        LOG.info("SERVICE_STARTED");

        if (created.getAndSet(true)) {
            LOG.info("Already created");
            //stopSelf();
            return;
        }

        new Thread(() -> {
            identityInfo = getHeliosIdentityInfo();
            connectionInfo = new HeliosConnectionInfo();

            heliosMessaging.setContext(getApplicationContext());
            heliosMessaging.connect(connectionInfo, identityInfo);

            preferencesHelper.putString(PEERID, heliosMessaging.getPeerId());

            //Receiver for ContentAwareProfileMessage(s)
            heliosMessaging.getDirectMessaging().addReceiver(
                    CONTENT_AWARE_PROFILE_RECEIVER_ID,
                    new ContentAwareProfileReceiver(profileManager, preferencesHelper)
            );

            //Receiver for ProfileMatchingScoreMessage(s)
            heliosMessaging.getDirectMessaging().addReceiver(
                    PROFILE_MATCHING_SCORE_RECEIVER_ID,
                    new ProfileMatchingScoreReceiver(preferencesHelper)
            );

            heliosMessaging.announceTag(APP_TAG);
            heliosMessaging.observeTag(APP_TAG);
        }).start();

        EventBus.getDefault().register(this);

        // Register for device shutdown broadcasts
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                LOG.info("Device is shutting down");
                EventBus.getDefault().unregister(this);
                stopSelf();
            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_SHUTDOWN);
        filter.addAction("android.intent.action.QUICKBOOT_POWEROFF");
        filter.addAction("com.htc.intent.action.QUICKBOOT_POWEROFF");
        registerReceiver(receiver, filter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            heliosMessaging.disconnect(connectionInfo, identityInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (receiver != null) unregisterReceiver(receiver);
    }

    /**
     * This is not a bound service, it always runs in the background
     *
     * @param intent
     * @return
     */
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void addReceiver(String protocolId, HeliosMessagingReceiver receiver) {
        heliosMessaging.getDirectMessaging().addReceiver(protocolId, receiver);
    }

    @Subscribe
    public void announceTag(NewTagEvent tagEvent) {
        heliosMessaging.announceTag(tagEvent.getTag());
    }

    public void unannounceTag(String tag) {
        heliosMessaging.unannounceTag(tag);
    }

    @Subscribe
    public void observeTag(NewTagEvent tagEvent) {
        heliosMessaging.observeTag(tagEvent.getTag());
    }

    public void unobserveTag(String tag) {
        heliosMessaging.unobserveTag(tag);
    }

    public List<HeliosEgoTag> getPeers(String tag) {
        ArrayList<HeliosEgoTag> peers = new ArrayList();
        LinkedList<HeliosEgoTag> tags = heliosMessaging.getTags();

        for (HeliosEgoTag t : tags) {
            if (tag.equals(t.getTag())) {
                peers.add(t);
            }
        }
        return peers;
    }

    @Subscribe
    public void printTags(RequestTagsEvent requestTagsEvent) {
        LOG.info("start printing tags");
        LinkedList<HeliosEgoTag> tags = heliosMessaging.getTags();

        for (HeliosEgoTag tag : tags) {
            LOG.info("ego: " + tag.getEgoId());
            LOG.info("tag: " + tag.getTag());
            LOG.info("peer-id: " + tag.getNetworkId());
        }
    }

    /**
     * Sends CoarseInterestProfile to a peer
     *
     * @param event
     */
    @Subscribe
    public void sendProfile(SendContentAwareProfileEvent event) {
        ContentAwareProfile profile;
        if (event.getModelType().equals(ModelType.COARSE))
            profile = egoNetwork.getEgo().getOrCreateInstance(CoarseInterestsProfile.class);
        else if (event.getModelType().equals(ModelType.FINE))
            profile = egoNetwork.getEgo().getOrCreateInstance(FineInterestsProfile.class);
        else
            profile = egoNetwork.getEgo().getOrCreateInstance(DMLProfile.class);
        if (profile.getRawProfile().isEmpty()) {
            EventBus.getDefault().post(new SendMessageFailedEvent("Your content-aware profile is not yet calculated! "));
            return;
        }

        ContentAwareProfileMessage profileMessage = new ContentAwareProfileMessage(
                identityInfo.getNickname(),
                event.getModelType(),
                profile.getRawProfile()
        );

        String profileJson = new Gson().toJson(profileMessage);
        LOG.info(profileJson);
        Future<Unit> f = heliosMessaging.getDirectMessaging().sendToFuture(
                event.getNetworkId(),
                CONTENT_AWARE_PROFILE_RECEIVER_ID,
                profileJson.getBytes()
        );

        try {
            f.get(10000, TimeUnit.MILLISECONDS);
        } catch (ExecutionException e) {
            e.printStackTrace();
            EventBus.getDefault().post(new SendMessageFailedEvent("User cannot be reached! " + e.getMessage()));
        } catch (InterruptedException e) {
            e.printStackTrace();
            EventBus.getDefault().post(new SendMessageFailedEvent("User cannot be reached! " + e.getMessage()));
        } catch (TimeoutException e) {
            e.printStackTrace();
            EventBus.getDefault().post(new SendMessageFailedEvent("User cannot be reached! " + e.getMessage()));
        }
    }

    /**
     * Sends the calculated matching score to a peer
     *
     * @param event
     */
    @Subscribe
    public void sendMatchingScore(SendProfileMatchingScoreEvent event) {
        String matchingScoreMessage = new Gson().toJson(event.getProfileMatchingScoreMessage());
        Future<Unit> f = heliosMessaging.getDirectMessaging().sendToFuture(
                event.getNetworkId(),
                PROFILE_MATCHING_SCORE_RECEIVER_ID,
                matchingScoreMessage.getBytes()
        );

        try {
            f.get(10000, TimeUnit.MILLISECONDS);
        } catch (ExecutionException e) {
            e.printStackTrace();
            EventBus.getDefault().post(new SendMessageFailedEvent("User cannot be reached! " + e.getMessage()));
        } catch (InterruptedException e) {
            e.printStackTrace();
            EventBus.getDefault().post(new SendMessageFailedEvent("User cannot be reached! " + e.getMessage()));
        } catch (TimeoutException e) {
            e.printStackTrace();
            EventBus.getDefault().post(new SendMessageFailedEvent("User cannot be reached! " + e.getMessage()));
        }
    }

    private HeliosIdentityInfo getHeliosIdentityInfo() {
        if (preferencesHelper.getString(USERNAME) == null) {
            Faker faker = new Faker();
            preferencesHelper.putString(USERNAME, faker.name().username());
            preferencesHelper.putString(EGO_ID, UUID.randomUUID().toString());
        }

        return new HeliosIdentityInfo(
                preferencesHelper.getString(USERNAME),
                preferencesHelper.getString(EGO_ID)
        );
    }
}

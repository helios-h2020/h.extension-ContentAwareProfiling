package eu.h2020.helios_social.happs.contentawareprofiling.activepeers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.LinkedList;
import java.util.logging.Logger;

import javax.inject.Inject;

import eu.h2020.helios_social.core.messaging_nodejslibp2p.HeliosEgoTag;
import eu.h2020.helios_social.core.messaging_nodejslibp2p.HeliosMessagingNodejsLibp2p;
import eu.h2020.helios_social.happs.contentawareprofiling.activity.ActivityComponent;
import eu.h2020.helios_social.happs.contentawareprofiling.activity.BaseActivity;
import eu.h2020.helios_social.happs.contentawareprofiling.event.SendMessageFailedEvent;
import eu.h2020.helios_social.happs.contentawareprofiling.preferences.SharedPreferencesHelper;

import static eu.h2020.helios_social.happs.contentawareprofiling.communication.CommunicationConstants.TAG_LIST_UPDATE;
import static java.util.logging.Logger.getLogger;

/**
 * shows active peers on the network tag = testing-content-aware-profiling
 */
public class ActivePeersActivity extends BaseActivity {
    private static String TAG = ActivePeersActivity.class.getName();
    private final static Logger LOG = getLogger(TAG);

    private RecyclerView list;
    private LinkedList<HeliosEgoTag> active_peers;
    private ActivePeersAdapter adapter;
    @Inject
    HeliosMessagingNodejsLibp2p heliosMessaging;
    @Inject
    SharedPreferencesHelper preferencesHelper;

    BroadcastReceiver tagListReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null || active_peers == null || !TAG_LIST_UPDATE.equals(intent.getAction()))
                return;
            LOG.info("updating list of active peers...");
            active_peers = (LinkedList<HeliosEgoTag>) intent.getSerializableExtra(TAG_LIST_UPDATE);
            adapter.setActivePeers(active_peers);
            adapter.notifyDataSetChanged();
        }
    };

    @Override
    public void injectActivity(ActivityComponent component) {
        component.inject(this);
    }

    @Override
    public void onCreate(@Nullable Bundle state) {
        super.onCreate(state);
        setContentView(eu.h2020.helios_social.happs.contentawareprofiling.R.layout.activity_active_peers);
        setTitle(getString(eu.h2020.helios_social.happs.contentawareprofiling.R.string.active_peers_title));

        active_peers = heliosMessaging.getTags();
        adapter = new ActivePeersAdapter(active_peers, preferencesHelper);
        list = findViewById(eu.h2020.helios_social.happs.contentawareprofiling.R.id.active_peers);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(tagListReceiver, new IntentFilter(TAG_LIST_UPDATE));
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(tagListReceiver);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onMessageEvent(SendMessageFailedEvent event) {
        Toast.makeText(this, event.getReason(), Toast.LENGTH_LONG).show();
    }
}

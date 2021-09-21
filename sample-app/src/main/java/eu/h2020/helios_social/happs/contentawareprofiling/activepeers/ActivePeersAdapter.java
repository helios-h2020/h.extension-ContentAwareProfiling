package eu.h2020.helios_social.happs.contentawareprofiling.activepeers;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import eu.h2020.helios_social.core.messaging_nodejslibp2p.HeliosEgoTag;
import eu.h2020.helios_social.happs.contentawareprofiling.preferences.SharedPreferencesHelper;

import static java.util.logging.Logger.getLogger;

public class ActivePeersAdapter extends RecyclerView.Adapter<ActivePeerViewHolder> {
    private static String TAG = ActivePeersActivity.class.getName();
    private final static Logger LOG = getLogger(TAG);

    private List<HeliosEgoTag> active_peers;
    private SharedPreferencesHelper preferencesHelper;

    public ActivePeersAdapter(List<HeliosEgoTag> active_peers, SharedPreferencesHelper preferencesHelper) {
        this.active_peers = active_peers;
        this.preferencesHelper = preferencesHelper;
    }

    @NonNull
    @Override
    public ActivePeerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(eu.h2020.helios_social.happs.contentawareprofiling.R.layout.list_item_active_peer, parent, false);
        return new ActivePeerViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ActivePeerViewHolder holder, int position) {
        holder.bind(active_peers.get(position), preferencesHelper);
    }

    @Override
    public int getItemCount() {
        return active_peers.size();
    }

    public void setActivePeers(LinkedList<HeliosEgoTag> activePeers) {
        this.active_peers = activePeers;
    }
}

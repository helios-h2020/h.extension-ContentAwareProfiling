package eu.h2020.helios_social.happs.contentawareprofiling.activepeers;

import android.graphics.Color;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;
import java.util.logging.Logger;

import de.hdodenhof.circleimageview.CircleImageView;
import eu.h2020.helios_social.core.messaging_nodejslibp2p.HeliosEgoTag;
import eu.h2020.helios_social.modules.contentawareprofiling.model.ModelType;
import eu.h2020.helios_social.happs.contentawareprofiling.event.SendContentAwareProfileEvent;
import eu.h2020.helios_social.happs.contentawareprofiling.preferences.SharedPreferencesHelper;

import static eu.h2020.helios_social.happs.contentawareprofiling.communication.CommunicationConstants.PEERID;
import static java.util.logging.Logger.getLogger;

public class ActivePeerViewHolder extends RecyclerView.ViewHolder {
    private static String TAG = ActivePeersActivity.class.getName();
    private final static Logger LOG = getLogger(TAG);

    private TextView peer_id;
    private TextView username;
    private TextView timestamp;
    private Button btn_compare;
    private CircleImageView imageView;
    private TextView matching_score;

    public ActivePeerViewHolder(@NonNull View v) {
        super(v);

        peer_id = v.findViewById(eu.h2020.helios_social.happs.contentawareprofiling.R.id.peer_id);
        username = v.findViewById(eu.h2020.helios_social.happs.contentawareprofiling.R.id.username);
        timestamp = v.findViewById(eu.h2020.helios_social.happs.contentawareprofiling.R.id.timestamp);
        btn_compare = v.findViewById(eu.h2020.helios_social.happs.contentawareprofiling.R.id.btn_compare);
        imageView = v.findViewById(eu.h2020.helios_social.happs.contentawareprofiling.R.id.avatarView);
        matching_score = v.findViewById(eu.h2020.helios_social.happs.contentawareprofiling.R.id.matching_score);
    }

    public void bind(HeliosEgoTag egoTag, SharedPreferencesHelper preferencesHelper) {
        peer_id.setText(egoTag.getNetworkId());
        if (preferencesHelper.getString(egoTag.getNetworkId()) != null) {
            username.setText(preferencesHelper.getString(egoTag.getNetworkId()));
            btn_compare.setEnabled(true);
        } else if (preferencesHelper.getString(PEERID).equals(egoTag.getNetworkId())) {
            username.setText("self");
            btn_compare.setEnabled(false);
        } else {
            username.setText("unknown");
            btn_compare.setEnabled(true);
        }
        if (preferencesHelper.getFloat(egoTag.getNetworkId() + "_score") > 0) {
            int score = (int) (preferencesHelper.getFloat(egoTag.getNetworkId() + "_score") * 100);
            matching_score.setText("matching score: " + score + "%");
            matching_score.setTextColor(Color.parseColor("#2E8B57"));
        } else {
            matching_score.setText("matching score: not available");
            matching_score.setTextColor(Color.RED);
        }
        timestamp.setText(getDate(egoTag.getTimestamp()));
        btn_compare.setOnClickListener(l -> {
                    EventBus.getDefault().post(new SendContentAwareProfileEvent(egoTag.getNetworkId(), ModelType.COARSE));
                }
        );
        imageView.setImageResource(eu.h2020.helios_social.happs.contentawareprofiling.R.drawable.ic_person);
    }

    private String getDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.getDefault());
        TimeZone tz = TimeZone.getDefault();
        cal.setTimeInMillis(time);
        cal.add(Calendar.MILLISECOND, tz.getOffset(cal.getTimeInMillis()));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(cal.getTime());
    }


}

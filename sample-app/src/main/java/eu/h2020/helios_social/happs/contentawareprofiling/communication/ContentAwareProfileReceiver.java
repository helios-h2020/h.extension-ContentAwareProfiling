package eu.h2020.helios_social.happs.contentawareprofiling.communication;

import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.logging.Logger;

import eu.h2020.helios_social.core.messaging_nodejslibp2p.HeliosMessagingReceiver;
import eu.h2020.helios_social.core.messaging_nodejslibp2p.HeliosNetworkAddress;
import eu.h2020.helios_social.modules.contentawareprofiling.ContentAwareProfileManager;
import eu.h2020.helios_social.happs.contentawareprofiling.activepeers.ActivePeersActivity;
import eu.h2020.helios_social.happs.contentawareprofiling.event.SendProfileMatchingScoreEvent;
import eu.h2020.helios_social.happs.contentawareprofiling.preferences.SharedPreferencesHelper;
import eu.h2020.helios_social.happs.contentawareprofiling.profiling.ContentAwareProfileMessage;
import eu.h2020.helios_social.happs.contentawareprofiling.profiling.ProfileMatchingScoreMessage;
import eu.h2020.helios_social.happs.contentawareprofiling.profiling.ProfilingUtils;

import static eu.h2020.helios_social.happs.contentawareprofiling.communication.CommunicationConstants.USERNAME;
import static java.util.logging.Logger.getLogger;

public class ContentAwareProfileReceiver implements HeliosMessagingReceiver {
    private static String TAG = ActivePeersActivity.class.getName();
    private final static Logger LOG = getLogger(TAG);

    private ContentAwareProfileManager profileManager;
    private SharedPreferencesHelper preferencesHelper;

    public ContentAwareProfileReceiver(ContentAwareProfileManager profileManager, SharedPreferencesHelper helper) {
        this.profileManager = profileManager;
        this.preferencesHelper = helper;
    }

    @Override
    public void receiveMessage(@NotNull HeliosNetworkAddress heliosNetworkAddress, @NotNull String protocolId, @NotNull FileDescriptor fileDescriptor) {
        if (!protocolId.equals("/helios/test/contentawareprofile")) return;
        ByteArrayOutputStream ba = new ByteArrayOutputStream();
        try (FileInputStream fileInputStream = new FileInputStream(fileDescriptor)) {
            int byteRead;
            while ((byteRead = fileInputStream.read()) != -1) {
                ba.write(byteRead);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        receiveMessage(heliosNetworkAddress, protocolId, ba.toByteArray());
    }

    @Override
    public void receiveMessage(@NotNull HeliosNetworkAddress heliosNetworkAddress, @NotNull String s, @NotNull byte[] data) {
        String stringMessage = new String(data, StandardCharsets.UTF_8);
        ContentAwareProfileMessage profileMessage = new Gson().fromJson(stringMessage, ContentAwareProfileMessage.class);

        ArrayList<Float> selfRawProfile = profileManager
                .getProfile(ProfilingUtils.getProfileClass(profileMessage.getModelType()))
                .getRawProfile();

        preferencesHelper.putString(heliosNetworkAddress.getNetworkId(), profileMessage.getUsername());

        if (selfRawProfile.isEmpty()) return;

        double matching_score = profileManager.getMatchingScore(selfRawProfile, profileMessage.getProfile());
        preferencesHelper.putFloat(heliosNetworkAddress.getNetworkId() + "_score", (float) matching_score);

        EventBus.getDefault().post(new SendProfileMatchingScoreEvent(
                heliosNetworkAddress.getNetworkId(),
                new ProfileMatchingScoreMessage(preferencesHelper.getString(USERNAME), profileMessage.getModelType(), (float) matching_score))
        );
    }
}

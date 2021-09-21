package eu.h2020.helios_social.happs.contentawareprofiling.communication;

import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

import eu.h2020.helios_social.core.messaging_nodejslibp2p.HeliosMessagingReceiver;
import eu.h2020.helios_social.core.messaging_nodejslibp2p.HeliosNetworkAddress;
import eu.h2020.helios_social.happs.contentawareprofiling.activepeers.ActivePeersActivity;
import eu.h2020.helios_social.happs.contentawareprofiling.preferences.SharedPreferencesHelper;
import eu.h2020.helios_social.happs.contentawareprofiling.profiling.ProfileMatchingScoreMessage;

import static java.util.logging.Logger.getLogger;

public class ProfileMatchingScoreReceiver implements HeliosMessagingReceiver {
    private static String TAG = ActivePeersActivity.class.getName();
    private final static Logger LOG = getLogger(TAG);

    private SharedPreferencesHelper preferencesHelper;

    public ProfileMatchingScoreReceiver(SharedPreferencesHelper helper) {
        this.preferencesHelper = helper;
    }

    @Override
    public void receiveMessage(@NotNull HeliosNetworkAddress heliosNetworkAddress, @NotNull String protocolId, @NotNull FileDescriptor fileDescriptor) {
        if (!protocolId.equals("/helios/test/contentawareprofile/matchingscore")) return;
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
        ProfileMatchingScoreMessage scoreMessage = new Gson().fromJson(stringMessage, ProfileMatchingScoreMessage.class);

        preferencesHelper.putString(heliosNetworkAddress.getNetworkId(), scoreMessage.getUsername());
        preferencesHelper.putFloat(heliosNetworkAddress.getNetworkId() + "_score", (float) scoreMessage.getMatchingScore());
    }
}

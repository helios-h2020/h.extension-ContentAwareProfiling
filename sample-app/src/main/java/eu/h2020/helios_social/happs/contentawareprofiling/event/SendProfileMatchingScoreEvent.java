package eu.h2020.helios_social.happs.contentawareprofiling.event;

import eu.h2020.helios_social.core.messaging_nodejslibp2p.HeliosNetworkAddress;
import eu.h2020.helios_social.happs.contentawareprofiling.profiling.ProfileMatchingScoreMessage;

public class SendProfileMatchingScoreEvent extends Event {

    private ProfileMatchingScoreMessage message;
    private HeliosNetworkAddress networkId;

    public SendProfileMatchingScoreEvent(String peer_id, ProfileMatchingScoreMessage message) {
        networkId = new HeliosNetworkAddress();
        networkId.setNetworkId(peer_id);
        this.message = message;
    }

    public ProfileMatchingScoreMessage getProfileMatchingScoreMessage() {
        return message;
    }

    public HeliosNetworkAddress getNetworkId() {
        return networkId;
    }
}

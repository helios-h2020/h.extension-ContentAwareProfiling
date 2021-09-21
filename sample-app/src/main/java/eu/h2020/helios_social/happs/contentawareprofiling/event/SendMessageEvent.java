package eu.h2020.helios_social.happs.contentawareprofiling.event;

import eu.h2020.helios_social.core.messaging.HeliosMessage;
import eu.h2020.helios_social.core.messaging_nodejslibp2p.HeliosNetworkAddress;

public class SendMessageEvent extends Event {

    private HeliosMessage message;
    private HeliosNetworkAddress addr;

    public SendMessageEvent(HeliosNetworkAddress addr, HeliosMessage message) {
        this.addr = addr;
        this.message = message;
    }

    public HeliosMessage getMessage() {
        return message;
    }

    public HeliosNetworkAddress getAddr() {
        return addr;
    }
}

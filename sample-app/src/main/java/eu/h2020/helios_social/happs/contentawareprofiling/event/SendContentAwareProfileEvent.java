package eu.h2020.helios_social.happs.contentawareprofiling.event;

import eu.h2020.helios_social.core.messaging_nodejslibp2p.HeliosNetworkAddress;
import eu.h2020.helios_social.modules.contentawareprofiling.model.ModelType;

public class SendContentAwareProfileEvent extends Event {

    private ModelType modelType;
    private HeliosNetworkAddress networkId;

    public SendContentAwareProfileEvent(String peer_id, ModelType modelType) {
        this.modelType = modelType;
        networkId = new HeliosNetworkAddress();
        networkId.setNetworkId(peer_id);
    }

    public ModelType getModelType() {
        return modelType;
    }

    public HeliosNetworkAddress getNetworkId() {
        return networkId;
    }
}

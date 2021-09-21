package eu.h2020.helios_social.happs.contentawareprofiling.event;

public class ActivePeersRequestEvent extends Event {

    private String tag;

    public ActivePeersRequestEvent(String tag) {
        this.tag = tag;
    }

    public String getTag() {
        return tag;
    }
}

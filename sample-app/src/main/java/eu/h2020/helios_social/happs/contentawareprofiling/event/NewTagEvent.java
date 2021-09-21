package eu.h2020.helios_social.happs.contentawareprofiling.event;

public class NewTagEvent extends Event{

    private String tag;

    public NewTagEvent(String name) {
        this.tag = name;
    }

    public String getTag() {
        return tag;
    }
}

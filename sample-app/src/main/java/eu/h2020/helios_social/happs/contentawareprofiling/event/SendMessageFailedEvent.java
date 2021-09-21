package eu.h2020.helios_social.happs.contentawareprofiling.event;

public class SendMessageFailedEvent {

    public String reason;

    public SendMessageFailedEvent(String reason) {
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }
}

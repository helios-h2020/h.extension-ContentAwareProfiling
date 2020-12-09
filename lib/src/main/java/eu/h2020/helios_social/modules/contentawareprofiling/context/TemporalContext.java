package eu.h2020.helios_social.modules.contentawareprofiling.context;

/**
 * Represents the concept of a temporal context expressed with a timestamp.
 */
public class TemporalContext {
    private Long timestamp;

    public TemporalContext() {
    }

    public TemporalContext(Long timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Compares this temporal context with another one and outputs a measure of their proximity that
     * ranges between 0 and 1.
     *
     * @param temporalContext The reference temporal context that the current context is weighted against.
     * @return A float between 0 and 1 that corresponds to the proximity of the two contexts.
     */
    public float weightAgainstReferenceContext(TemporalContext temporalContext) {
        // TODO
        return (float) 1.0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TemporalContext)) return false;
        TemporalContext temporalContext = (TemporalContext) o;
        return timestamp == temporalContext.timestamp;
    }

    @Override
    public int hashCode() {
        return timestamp.hashCode();
    }

}

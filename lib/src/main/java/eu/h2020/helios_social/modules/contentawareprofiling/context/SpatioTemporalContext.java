package eu.h2020.helios_social.modules.contentawareprofiling.context;

/**
 * A composite class that represents the concept of a spatio-temporal context and consists of a
 * spatialContext and a temporalContext object.
 */
public class SpatioTemporalContext {
    SpatialContext spatialContext;
    TemporalContext temporalContext;

    public SpatioTemporalContext() {

    }

    public SpatioTemporalContext(Float latitude, Float longitude,
                                 Long timestamp) {
        spatialContext = new SpatialContext(latitude, longitude);
        temporalContext = new TemporalContext(timestamp);
    }


    /**
     * Compares this spatio-temporal context with another one and outputs a measure of their proximity that
     * ranges between 0 and 1.
     *
     * @param spatioTemporalContext The reference spatio-temporal context that the current context is
     *                              weighted against.
     * @return A float between 0 and 1 that corresponds to the proximity of the two contexts.
     */
    public float weightAgainstReferenceContext(SpatioTemporalContext spatioTemporalContext) {
        float spatialWeight = spatialContext.weightAgainstReferenceContext(spatioTemporalContext.spatialContext);
        float temporalWeight = temporalContext.weightAgainstReferenceContext(spatioTemporalContext.temporalContext);
        return spatialWeight * temporalWeight;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SpatioTemporalContext)) return false;
        SpatioTemporalContext spatioTemporalContext = (SpatioTemporalContext) o;
        Boolean isSpatialContextEqual = spatialContext == spatioTemporalContext.spatialContext;
        Boolean isTemporalContextEqual = temporalContext == spatioTemporalContext.temporalContext;
        return isSpatialContextEqual && isTemporalContextEqual;
    }

    @Override
    public int hashCode() {
        int spatialHash = spatialContext.hashCode();
        int temporalHash = temporalContext.hashCode();
        return 4519 * spatialHash ^ 1789 * temporalHash;
    }
}

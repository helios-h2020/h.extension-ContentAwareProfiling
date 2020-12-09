package eu.h2020.helios_social.modules.contentawareprofiling.context;

/**
 * Represents the concept of a spatial context with a gps coordinate point of the (latitude, longitude) form.
 */
public class SpatialContext {
    private double latitude;
    private double longitude;

    public SpatialContext(){}

    public SpatialContext(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    /**
     * Compares this spatial context with another one and outputs a measure of their proximity that
     * ranges between 0 and 1.
     *
     * @param spatialContext The reference spatial context that the current context is weighted against.
     * @return A float between 0 and 1 that corresponds to the proximity of the two contexts.
     */
    public float weightAgainstReferenceContext(SpatialContext spatialContext) {
        // TODO
        return (float) 1.0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SpatialContext)) return false;
        SpatialContext spatialContext = (SpatialContext) o;
        Boolean isLatitudeEqual = latitude == spatialContext.latitude;
        Boolean isLongitudeEqual = longitude == spatialContext.longitude;
        return isLatitudeEqual && isLongitudeEqual;
    }

    @Override
    public int hashCode() {
        int latHash = Double.valueOf(latitude).hashCode();
        int lonHash = Double.valueOf(longitude).hashCode();
        return 1013 * latHash ^ 1009 * lonHash;
    }
}

package eu.h2020.helios_social.modules.contentawareprofiling.profile;

import java.util.ArrayList;

import eu.h2020.helios_social.modules.contentawareprofiling.context.SpatioTemporalContext;
import eu.h2020.helios_social.modules.contentawareprofiling.data.CNNModelData;

public abstract class InterestProfile extends ContentAwareProfile {

    protected ArrayList<Interest> interests;

    public InterestProfile() {
    }

    /**
     * @param modelData The CNN model data related to the profile.
     */
    public InterestProfile(CNNModelData modelData) {
        super(modelData);
    }

    /**
     * Returns the interest profile in a user friendly format.
     *
     * @return The interest profile
     */
    public abstract ArrayList<Interest> getInterestProfile();

    /**
     * Returns the interest profile at the requested threshold in a user friendly format. The
     * threshold is defined on the output probability distribution and instructs the model to
     * reject images that have been classified with confidence below the threshold.
     *
     * @param threshold The threshold at which the profile will be calculated.
     * @return The interest profile
     */
    public abstract ArrayList<Interest> getInterestProfile(float threshold);

    /**
     * Returns the interest profile conditioned on a reference spatio-temporal context in a user
     * friendly format.
     *
     * @param context The reference spatio-temporal context.
     * @return The interest profile
     */
    public abstract ArrayList<Interest> getInterestProfile(SpatioTemporalContext context);

    /**
     * Returns the interest profile at the requested threshold and conditioned on a reference
     * spatio-temporal context in a user friendly format.
     *
     * @param context The reference spatio-temporal context.
     * @param threshold The threshold at which the profile will be calculated.
     * @return The interest profile
     */
    public abstract ArrayList<Interest> getInterestProfile(SpatioTemporalContext context, float threshold);

    public Interest getInterest(String name) {
        if (interests == null) interests = getInterestProfile();
        int index = interests.indexOf(new Interest("name", null));
        return index < 0 ? null : interests.get(index);
    }

    public Double getWeight(String name) {
        if (interests == null) interests = getInterestProfile();
        int index = interests.indexOf(new Interest("name", null));
        return index < 0 ? null : interests.get(index).getWeight();
    }

}

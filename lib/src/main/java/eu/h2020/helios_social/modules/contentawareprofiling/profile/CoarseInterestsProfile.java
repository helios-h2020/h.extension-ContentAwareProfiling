package eu.h2020.helios_social.modules.contentawareprofiling.profile;

import java.util.ArrayList;

import eu.h2020.helios_social.modules.contentawareprofiling.context.SpatioTemporalContext;
import eu.h2020.helios_social.modules.contentawareprofiling.data.CNNModelData;
import eu.h2020.helios_social.modules.contentawareprofiling.interestcategories.InterestCategoriesHierarchy;
import eu.h2020.helios_social.modules.contentawareprofiling.model.ModelType;
import eu.h2020.helios_social.modules.contentawareprofiling.model.ModelUtils;
import eu.h2020.helios_social.modules.contentawareprofiling.utils.ProfileUtils;

/**
 * The coarse interest profile
 */
public class CoarseInterestsProfile extends InterestProfile {

    public CoarseInterestsProfile() {
        interests = new ArrayList();
    }

    public CoarseInterestsProfile(ArrayList<Interest> interests) {
        super(interests);
    }
}

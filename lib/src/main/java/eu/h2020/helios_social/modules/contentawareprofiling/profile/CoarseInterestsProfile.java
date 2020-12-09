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
        super(new CNNModelData());
    }

    public CoarseInterestsProfile(CNNModelData modelData) {
        super(modelData);
    }

    @Override
    public ArrayList<Interest> getInterestProfile() {
        return ProfileUtils.transformToInterestProfile(modelData.getRawProfile(),
                InterestCategoriesHierarchy.coarseCategories);
    }

    @Override
    public ArrayList<Interest> getInterestProfile(float threshold) {
        ArrayList<Float> thresholdWeights =
                ModelUtils.getThresholdWeights(modelData.getModelOutputData(), threshold);
        return ProfileUtils.transformToInterestProfile(
                ModelUtils.columnWiseAdd(
                        modelData.getModelOutputData(),
                        thresholdWeights),
                InterestCategoriesHierarchy.coarseCategories);
    }

    @Override
    public ArrayList<Interest> getInterestProfile(SpatioTemporalContext context) {
        float threshold = ModelType.COARSE.threshold;
        return getInterestProfile(context, threshold);
    }

    @Override
    public ArrayList<Interest> getInterestProfile(SpatioTemporalContext context, float threshold) {
        ArrayList<Float> thresholdWeights =
                ModelUtils.getThresholdWeights(modelData.getModelOutputData(), threshold);
        ArrayList<Float> attentionWeights = ModelUtils.getAttentionWeights(context,
                modelData.getImages(), thresholdWeights);
        return ProfileUtils.transformToInterestProfile(
                ModelUtils.columnWiseAdd(
                        modelData.getModelOutputData(),
                        attentionWeights),
                InterestCategoriesHierarchy.coarseCategories);
    }
}

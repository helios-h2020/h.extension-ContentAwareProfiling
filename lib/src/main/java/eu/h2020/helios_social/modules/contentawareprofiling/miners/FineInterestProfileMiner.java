package eu.h2020.helios_social.modules.contentawareprofiling.miners;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Logger;

import eu.h2020.helios_social.core.contextualegonetwork.ContextualEgoNetwork;
import eu.h2020.helios_social.core.contextualegonetwork.Storage;
import eu.h2020.helios_social.modules.contentawareprofiling.Image;
import eu.h2020.helios_social.modules.contentawareprofiling.context.SpatioTemporalContext;
import eu.h2020.helios_social.modules.contentawareprofiling.data.CNNModelData;
import eu.h2020.helios_social.modules.contentawareprofiling.interestcategories.InterestCategoriesHierarchy;
import eu.h2020.helios_social.modules.contentawareprofiling.model.FineInterestsModel;
import eu.h2020.helios_social.modules.contentawareprofiling.model.ModelType;
import eu.h2020.helios_social.modules.contentawareprofiling.model.ModelUtils;
import eu.h2020.helios_social.modules.contentawareprofiling.profile.ContentAwareProfile;
import eu.h2020.helios_social.modules.contentawareprofiling.profile.FineInterestsProfile;
import eu.h2020.helios_social.modules.contentawareprofiling.profile.Interest;
import eu.h2020.helios_social.modules.contentawareprofiling.utils.ProfileUtils;

/**
 * The miner of the model based on the fine interest categories.
 */
public class FineInterestProfileMiner extends ContentAwareProfileMiner {

    public static final String TAG = FineInterestProfileMiner.class.getName();
    private static final Logger LOG = Logger.getLogger(TAG);

    protected FineInterestsModel model;

    /**
     * @param assetManager The android asset manager.
     * @param ctx          The android context.
     * @param egoNetwork   The egoNetwork provided from the CEN library.
     */
    public FineInterestProfileMiner(AssetManager assetManager,
                                    Context ctx,
                                    ContextualEgoNetwork egoNetwork) {
        super(assetManager, ctx, egoNetwork);
        try {
            model = new FineInterestsModel(assetManager, ctx);
        } catch (IOException e) {
            e.printStackTrace();
            LOG.severe(e.getMessage());
        }
    }

    /**
     * Calculates the fine content aware profile from a collection of images.
     *
     * @param images An ArrayList of image objects.
     * @return The calculated fine profile.
     */
    @Override
    public void calculateContentAwareProfile(ArrayList<Image> images) {
        Storage egoStorage = egoNetwork.getSerializer().getStorage();
        CNNModelData modelData = loadModelData();

        images.removeAll(modelData.getImages());

        if (images.size() > 0) {
            LOG.info("Fine Interest Profiler starts processing " + images.size() + " images.");

            ArrayList<ArrayList<Float>> cnnOutput = model.forwardCNN(images);
            modelData.mergeData(images, cnnOutput, ModelType.FINE);
            String modelDataAsString = egoNetwork.getSerializer().serializeToString(modelData);
            try {
                egoStorage.saveToFile(getClass().getName(), modelDataAsString);
            } catch (Exception e) {
                e.printStackTrace();
            }
            LOG.info("Fine Profile based on " + (modelData.getImages().size() + images.size()) + " images has calculated and saved.");
        }

        egoNetwork.getEgo()
                .getOrCreateInstance(FineInterestsProfile.class)
                .setInterests(getInterestProfile(modelData.getRawProfile()))
                .setRawProfile(modelData.getRawProfile());
        egoNetwork.save();
    }

    @Override
    public ContentAwareProfile getProfile() {
        return egoNetwork.getEgo()
                .getOrCreateInstance(FineInterestsProfile.class);
    }

    public ContentAwareProfile getInterestProfile(float threshold) {
        CNNModelData modelData = loadModelData();
        ArrayList<Float> thresholdWeights =
                ModelUtils.getFineThresholdWeights(modelData.getModelOutputData(), threshold);
        return new FineInterestsProfile(ProfileUtils.transformToInterestProfile(
                ModelUtils.columnWiseAdd(
                        modelData.getModelOutputData(),
                        thresholdWeights),
                InterestCategoriesHierarchy.fineCategories));
    }

    @Override
    public ContentAwareProfile getProfile(SpatioTemporalContext context) {
        float threshold = ModelType.FINE.threshold;
        return getProfile(context, threshold);
    }

    public ContentAwareProfile getProfile(SpatioTemporalContext context, float threshold) {
        CNNModelData modelData = loadModelData();
        ArrayList<Float> thresholdWeights =
                ModelUtils.getFineThresholdWeights(modelData.getModelOutputData(), threshold);
        ArrayList<Float> attentionWeights = ModelUtils.getAttentionWeights(context,
                modelData.getImages(), thresholdWeights);
        return new FineInterestsProfile(ProfileUtils.transformToInterestProfile(
                ModelUtils.columnWiseAdd(
                        modelData.getModelOutputData(),
                        attentionWeights),
                InterestCategoriesHierarchy.fineCategories));
    }

    private ArrayList<Interest> getInterestProfile(ArrayList<Float> rawProfile) {
        return ProfileUtils.transformToInterestProfile(rawProfile,
                InterestCategoriesHierarchy.fineCategories);
    }

    private CNNModelData loadModelData() {
        Storage egoStorage = egoNetwork.getSerializer().getStorage();
        CNNModelData modelData;
        if (egoStorage.fileExists(getClass().getName())) {
            try {
                String stringModelData = egoStorage.loadFromFile(getClass().getName());
                modelData = (CNNModelData) egoNetwork.getSerializer().deserializeFromString(stringModelData);
            } catch (Exception e) {
                e.printStackTrace();
                modelData = new CNNModelData();
            }
        } else {
            modelData = new CNNModelData();
        }
        return modelData;
    }


}

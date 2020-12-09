package eu.h2020.helios_social.modules.contentawareprofiling.miners;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Logger;

import eu.h2020.helios_social.core.contextualegonetwork.ContextualEgoNetwork;
import eu.h2020.helios_social.modules.contentawareprofiling.Image;
import eu.h2020.helios_social.modules.contentawareprofiling.data.CNNModelData;
import eu.h2020.helios_social.modules.contentawareprofiling.model.CoarseInterestsModel;
import eu.h2020.helios_social.modules.contentawareprofiling.model.ModelType;
import eu.h2020.helios_social.modules.contentawareprofiling.profile.CoarseInterestsProfile;
import eu.h2020.helios_social.modules.contentawareprofiling.profile.ContentAwareProfile;

/**
 * The miner of the model based on the coarse interest categories.
 */
public class CoarseInterestProfileMiner extends ContentAwareProfileMiner {

    public static final String TAG = CoarseInterestProfileMiner.class.getName();
    private static final Logger LOG = Logger.getLogger(TAG);

    protected CoarseInterestsModel model;

    /**
     * @param assetManager The android asset manager.
     * @param ctx The android context.
     * @param egoNetwork The egoNetwork provided from the CEN library.
     */
    public CoarseInterestProfileMiner(AssetManager assetManager,
                                      Context ctx,
                                      ContextualEgoNetwork egoNetwork) {
        super(assetManager, ctx, egoNetwork);
        try {
            model = new CoarseInterestsModel(assetManager, ctx);
        } catch (IOException e) {
            e.printStackTrace();
            LOG.severe(e.getMessage());
        }
    }

    /**
     * Calculates the coarse content aware profile from a collection of images.
     *
     * @param images An ArrayList of image objects.
     * @return The calculated coarse profile.
     */
    @Override
    public ContentAwareProfile calculateContentAwareProfile(ArrayList<Image> images) {

        CNNModelData modelData = (CNNModelData) egoNetwork.getEgo()
                .getOrCreateInstance(CoarseInterestsProfile.class)
                .getModelData();

        images.removeAll(modelData.getImages());

        if (images.size() > 0) {
            LOG.info("Coarse Interest Profiler starts processing " + images.size() + " images.");
            ArrayList<ArrayList<Float>> cnnOutput = model.forwardCNN(images);
            modelData.mergeData(images, cnnOutput, ModelType.COARSE);
            egoNetwork.save();
            LOG.info("Coarse Profile based on " + (modelData.getImages().size() + images.size()) + " images has been calculated and saved.");
        }
        return egoNetwork.getEgo()
                .getOrCreateInstance(CoarseInterestsProfile.class);
    }
}

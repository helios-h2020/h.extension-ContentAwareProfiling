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
import eu.h2020.helios_social.modules.contentawareprofiling.data.DMLModelData;
import eu.h2020.helios_social.modules.contentawareprofiling.model.DMLModel;
import eu.h2020.helios_social.modules.contentawareprofiling.profile.ContentAwareProfile;
import eu.h2020.helios_social.modules.contentawareprofiling.profile.DMLProfile;

/**
 * The miner of the Deep Metric Learning (DML) model.
 */
public class DMLProfileMiner extends ContentAwareProfileMiner {

    public static final String TAG = DMLProfileMiner.class.getName();
    private static final Logger LOG = Logger.getLogger(TAG);

    protected DMLModel dmlModel;

    /**
     * @param assetManager The android asset manager.
     * @param ctx          The android context.
     * @param egoNetwork   The egoNetwork provided from the CEN library.
     */
    public DMLProfileMiner(AssetManager assetManager,
                           Context ctx,
                           ContextualEgoNetwork egoNetwork) {
        super(assetManager, ctx, egoNetwork);
        try {
            dmlModel = new DMLModel(assetManager, ctx);
        } catch (IOException e) {
            e.printStackTrace();
            LOG.severe(e.getMessage());
        }
    }

    /**
     * Calculates the DML content aware profile from a collection of images.
     *
     * @param images An ArrayList of image objects.
     * @return The calculated DML profile
     */
    @Override
    public void calculateContentAwareProfile(ArrayList<Image> images) {
        DMLModelData modelData = loadModelData();
        Storage egoStorage = egoNetwork.getSerializer().getStorage();

        images.removeAll(modelData.getImages());

        if (images.size() > 0) {
            ArrayList<ArrayList<Float>> cnnOutput = dmlModel.forwardCNN(images);
            modelData.mergeData(images, cnnOutput, dmlModel);

            String modelDataAsString = egoNetwork.getSerializer().serializeToString(modelData);
            try {
                egoStorage.saveToFile(getClass().getName(), modelDataAsString);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        egoNetwork.getEgo().getOrCreateInstance(DMLProfile.class)
                .setRawProfile(modelData.getRawProfile());
        egoNetwork.save();
    }

    @Override
    public ContentAwareProfile getProfile() {
        return egoNetwork.getEgo().getOrCreateInstance(DMLProfile.class);
    }

    /**
     * Adjusts the calculated DML content aware profile in accordance with a provided spatio-temporal
     * context.
     *
     * @param context A reference spatio-temporal context.
     * @return A DML content and context aware profile.
     */
    @Override
    public ContentAwareProfile getProfile(SpatioTemporalContext context) {
        DMLModelData modelData = loadModelData();

        ArrayList<Float> attentionWeights = new ArrayList<>();

        for (int i = 0; i < modelData.getImages().size(); i++) {
            attentionWeights.add(modelData.getImages().get(i).getContext().weightAgainstReferenceContext(context));
        }

        return new DMLProfile().setRawProfile(dmlModel.forwardCNN2Profile(
                modelData.getModelOutputData(),
                attentionWeights
        ));
    }

    private DMLModelData loadModelData() {
        Storage egoStorage = egoNetwork.getSerializer().getStorage();
        DMLModelData modelData;
        if (egoStorage.fileExists(getClass().getName())) {
            try {
                String stringModelData = egoStorage.loadFromFile(getClass().getName());
                modelData = (DMLModelData) egoNetwork.getSerializer().deserializeFromString(stringModelData);
            } catch (Exception e) {
                e.printStackTrace();
                modelData = new DMLModelData();
            }
        } else {
            modelData = new DMLModelData();
        }
        return modelData;
    }
}

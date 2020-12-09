package eu.h2020.helios_social.modules.contentawareprofiling.data;

import java.util.ArrayList;

import eu.h2020.helios_social.modules.contentawareprofiling.Image;
import eu.h2020.helios_social.modules.contentawareprofiling.model.ModelType;
import eu.h2020.helios_social.modules.contentawareprofiling.model.ModelUtils;

/**
 * The data related to the output of the CNN model.
 */
public class CNNModelData extends ModelData {

    public CNNModelData() {
        this.images = new ArrayList<>();
        this.modelOutputData = new ArrayList<>();
        this.rawProfile = new ArrayList<>();
    }

    /**
     * Update an existing profile by merging the data calculated on new images.
     *
     * @param images The images on which the new data were calculated
     * @param modelOutputData The output of the CNN model.
     * @param type The model type
     * @return
     */
    public CNNModelData mergeData(ArrayList<Image> images, ArrayList<ArrayList<Float>> modelOutputData, ModelType type) {
        this.images.addAll(images);
        this.modelOutputData.addAll(modelOutputData);
        System.out.println(type);
        if (type == ModelType.FINE) {
            rawProfile = ModelUtils.columnWiseAdd(
                    modelOutputData,
                    ModelUtils.getFineThresholdWeights(modelOutputData, ModelType.FINE.threshold)
            );
        } else if (type == ModelType.COARSE) {
            rawProfile = ModelUtils.columnWiseAdd(
                    modelOutputData,
                    ModelUtils.getThresholdWeights(modelOutputData, ModelType.COARSE.threshold)
            );
        }
        return this;
    }

    /**
     * Calculate the raw profile at the requested threshold.
     *
     * @param threshold The threshold at which the profile will be calculated.
     * @param type The model type.
     * @return
     */
    public ArrayList<Float> getRawProfile(float threshold, ModelType type) {
        if (type == ModelType.COARSE) {
            ArrayList<Float> thresholdWeights = ModelUtils.getThresholdWeights(modelOutputData, threshold);
            return ModelUtils.columnWiseAdd(modelOutputData, thresholdWeights);
        } else if (type == ModelType.FINE) {
            ArrayList<Float> thresholdWeights = ModelUtils.getFineThresholdWeights(modelOutputData, threshold);
            return ModelUtils.columnWiseAdd(modelOutputData, thresholdWeights);
        }
        return null;
    }
}

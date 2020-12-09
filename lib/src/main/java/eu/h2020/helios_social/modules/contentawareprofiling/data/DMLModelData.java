package eu.h2020.helios_social.modules.contentawareprofiling.data;

import java.util.ArrayList;

import eu.h2020.helios_social.modules.contentawareprofiling.Image;
import eu.h2020.helios_social.modules.contentawareprofiling.model.DMLModel;


/**
 * The data related to the DML model.
 */
public class DMLModelData extends ModelData {

    /**
     * Update an existing profile by merging the data calculated on new images.
     *
     * @param images The images on which the new data were calculated
     * @param modelOutputData The output of the CNN model before applying the Fully Connected one
     *                       as it was calculated from the provided images.
     * @param dmlModel The DML model needed to perform the Fully Connected calculation on top of
     *                 the CNN output.
     * @return
     */
    public DMLModelData mergeData(ArrayList<Image> images, ArrayList<ArrayList<Float>> modelOutputData, DMLModel dmlModel) {
        this.images.addAll(images);
        this.modelOutputData.addAll(modelOutputData);
        rawProfile = dmlModel.forwardCNN2Profile(modelOutputData);
        return this;
    }
}

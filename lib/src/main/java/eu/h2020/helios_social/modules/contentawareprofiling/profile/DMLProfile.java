package eu.h2020.helios_social.modules.contentawareprofiling.profile;

import java.util.ArrayList;

import eu.h2020.helios_social.modules.contentawareprofiling.context.SpatioTemporalContext;
import eu.h2020.helios_social.modules.contentawareprofiling.data.DMLModelData;
import eu.h2020.helios_social.modules.contentawareprofiling.model.DMLModel;

public class DMLProfile extends ContentAwareProfile {

    public DMLProfile() {
        super(new DMLModelData());
    }

    public DMLProfile(DMLModelData modelData) {
        super(modelData);
    }

    /**
     * Returns the DML profile
     *
     * @return The DML raw profile.
     */
    public ArrayList<Float> getProfile() {
        return modelData.getRawProfile();
    }

    //TODO: I do not really like that we give the model as input.
    /**
     * Returns the DML profile conditioned on a reference spatio-temporal context.
     *
     * @param context The reference spatio-temporal context.
     * @param dmlModel The DML model to use for the calculations.
     * @return
     */
    public ArrayList<Float> getProfile(SpatioTemporalContext context, DMLModel dmlModel) {
        ArrayList<Float> attentionWeights = new ArrayList<>();

        for (int i = 0; i < modelData.getImages().size(); i++) {
            attentionWeights.add(modelData.getImages().get(i).getContext().weightAgainstReferenceContext(context));
        }

        return dmlModel.forwardCNN2Profile(
                modelData.getModelOutputData(),
                attentionWeights
        );
    }

}

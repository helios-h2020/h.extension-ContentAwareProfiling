package eu.h2020.helios_social.modules.contentawareprofiling.profile;

import eu.h2020.helios_social.modules.contentawareprofiling.data.ModelData;

/**
 * This class represents the user's content aware profile.
 */
public abstract class ContentAwareProfile {

    protected ModelData modelData;

    public ContentAwareProfile() {
    }

    /**
     * @param modelData The profile's model data.
     */
    public ContentAwareProfile(ModelData modelData) {
        this.modelData = modelData;
    }

    public ModelData getModelData() {
        return modelData;
    }
}


package eu.h2020.helios_social.happs.contentawareprofiling.profiling;

import java.util.ArrayList;

import eu.h2020.helios_social.modules.contentawareprofiling.model.ModelType;

public class ContentAwareProfileMessage {

    private String username;
    private ModelType modelType;
    private ArrayList<Float> profile;

    public ContentAwareProfileMessage(String username, ModelType modelType, ArrayList<Float> profile) {
        this.username = username;
        this.modelType = modelType;
        this.profile = profile;
    }

    public String getUsername() {
        return username;
    }

    public ModelType getModelType() {
        return modelType;
    }

    public ArrayList<Float> getProfile() {
        return profile;
    }
}

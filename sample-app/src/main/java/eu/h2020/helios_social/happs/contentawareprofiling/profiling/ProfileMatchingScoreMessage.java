package eu.h2020.helios_social.happs.contentawareprofiling.profiling;

import eu.h2020.helios_social.modules.contentawareprofiling.model.ModelType;

public class ProfileMatchingScoreMessage {
    private String username;
    private ModelType modelType;
    private float matching_score;

    public ProfileMatchingScoreMessage(String username, ModelType modelType, float matching_score) {
        this.username = username;
        this.modelType = modelType;
        this.matching_score = matching_score;
    }

    public String getUsername() {
        return username;
    }

    public float getMatchingScore() {
        return matching_score;
    }

    public ModelType getModelType() {
        return modelType;
    }
}

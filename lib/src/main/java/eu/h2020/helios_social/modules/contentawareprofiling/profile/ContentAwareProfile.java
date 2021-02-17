package eu.h2020.helios_social.modules.contentawareprofiling.profile;

import java.util.ArrayList;

/**
 * This class represents the user's content aware profile.
 */
public abstract class ContentAwareProfile {

    ArrayList<Float> rawProfile;

    public ContentAwareProfile() {
    }

    public ContentAwareProfile setRawProfile(ArrayList<Float> rawProfile) {
        this.rawProfile = rawProfile;
        return this;
    }

    public ArrayList<Float> getRawProfile() {
        return rawProfile;
    }

}


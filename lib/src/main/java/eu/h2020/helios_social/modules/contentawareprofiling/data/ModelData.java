package eu.h2020.helios_social.modules.contentawareprofiling.data;

import java.util.ArrayList;

import eu.h2020.helios_social.modules.contentawareprofiling.Image;

/**
 * This class represents the data relevant to each model that will be stored on the calculated
 * profile. They consist of the Image objects on which the profile was calculated, the
 * pre-calculated raw profile and intermediate model data that are necessary to facilitate more
 * advanced usage scenarios, such as context aware profiling.
 */
public class ModelData {

    protected ArrayList<Float> rawProfile = new ArrayList<>();
    protected ArrayList<ArrayList<Float>> modelOutputData = new ArrayList<>();
    protected ArrayList<Image> images = new ArrayList<>();

    public ModelData setRawProfile(ArrayList<Float> rawProfile) {
        this.rawProfile = rawProfile;
        return this;
    }

    public ModelData setModelOutputData(ArrayList<ArrayList<Float>> modelOutputData) {
        this.modelOutputData = modelOutputData;
        return this;
    }

    public ModelData setImages(ArrayList<Image> images) {
        this.images = images;
        return this;
    }

    public ArrayList<Image> getImages() {
        return images;
    }

    public ArrayList<Float> getRawProfile() {
        return rawProfile;
    }

    public ArrayList<ArrayList<Float>> getModelOutputData() {
        return modelOutputData;
    }

}

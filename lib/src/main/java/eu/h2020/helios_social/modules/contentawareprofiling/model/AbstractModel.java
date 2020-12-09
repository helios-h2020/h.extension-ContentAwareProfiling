package eu.h2020.helios_social.modules.contentawareprofiling.model;

import android.content.Context;

import java.util.ArrayList;

import eu.h2020.helios_social.modules.contentawareprofiling.Image;

/**
 * This class defines a common abstraction for all the developed models. Each model must define how
 * the content aware profile is calculated from a given image collection.
 */
public abstract class AbstractModel {

    protected Context ctx;

    /**
     * @param ctx The Android context.
     */
    public AbstractModel(Context ctx) {
        this.ctx = ctx;
    }

    /**
     * Calculates the output of the convolutional part of the model from a collection of images.
     *
     * @param images A collection of Image objects.
     * @return The output of the CNN as a 2d float array.
     */
    abstract public ArrayList<ArrayList<Float>> forwardCNN(ArrayList<Image> images);

    /**
     * Calculates the content aware profile from the output of the convolutional part of the model.
     *
     * @param cnnOutput The output of the CNN
     * @return The calculated content aware profile as an ArrayList.
     */
    abstract public ArrayList<Float> forwardCNN2Profile(ArrayList<ArrayList<Float>> cnnOutput);

    /**
     * Calculates the content aware profile from the output of the convolutional part of the
     * model and an attention vector.
     *
     * @param cnnOutput The output of the CNN
     * @return The calculated content aware profile as an ArrayList.
     */
    abstract public ArrayList<Float> forwardCNN2Profile(ArrayList<ArrayList<Float>> cnnOutput, ArrayList<Float> attentionWeights);
}

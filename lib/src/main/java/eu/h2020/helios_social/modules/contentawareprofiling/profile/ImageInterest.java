package eu.h2020.helios_social.modules.contentawareprofiling.profile;
// EDITED (DETAILED PROFILE)
import java.net.URI;

import eu.h2020.helios_social.modules.contentawareprofiling.Image;

public class ImageInterest implements Comparable {
    private String imageURI;
    private Double weight;

    public ImageInterest() {
    }

    /**
     * Creates an imageInterest object
     *  @param imageURI   the image URI
     * @param weight the weight assigned to the image
     */
    public ImageInterest(String imageURI, Double weight) {
        this.imageURI = imageURI;
        this.weight = weight;
    }

    /**
     * Gets image URI
     *
     * @return image URI
     */
    public String getImageURI() {
        return imageURI;
    }

    /**
     * Gets image's weight
     *
     * @return the weight assigned to the image
     */
    public Double getWeight() {
        return weight;
    }

    /**
     * Sets image URI
     *
     * @param imageURI the URI
     */
    public void setImageURI(String imageURI) {
        this.imageURI = imageURI;
    }

    /**
     * Sets image's weight
     *
     * @param weight the weight assigned to the image
     */
    public void setWeight(Double weight) {
        this.weight = weight;
    }


    @Override
    public int compareTo(Object o) {
        return -(this.getWeight().compareTo(((ImageInterest) o).getWeight()));
    }
}

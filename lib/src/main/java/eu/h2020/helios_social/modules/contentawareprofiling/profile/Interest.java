package eu.h2020.helios_social.modules.contentawareprofiling.profile;

import java.util.Objects;

/**
 * This class abstracts the interests of the users.
 * It includes a name, that defines the interest, and a weight that quantifies the user's
 * preference to the particular interest. The weight is typically assigned by the profiling
 * algorithm based on the user's content.
 * The interest is defined by its name and so the equality and hash of the object are
 * implemented based on its name.
 */
public class Interest implements Comparable {
    private String name;
    private Double weight;

    public Interest() {
    }

    /**
     * Creates an interest object
     *
     * @param name   the name of the interest
     * @param weight the weight assigned to the interest
     */
    public Interest(String name, Double weight) {
        this.name = name;
        this.weight = weight;
    }

    /**
     * Gets interest's name
     *
     * @return name of the interest
     */
    public String getName() {
        return name;
    }

    /**
     * Gets interest's weight
     *
     * @return the weight assigned to the interest
     */
    public Double getWeight() {
        return weight;
    }

    /**
     * Sets interest's name
     *
     * @param name the name of the interest
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets interest's weight
     *
     * @param weight the weight assigned to the interest
     */
    public void setWeight(Double weight) {
        this.weight = weight;
    }

    /**
     * Defines equality behavior
     *
     * @param o the object to be compared with
     * @return a boolean representing whether the objects are equal
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o instanceof String) return name.equals(o);
        if (!(o instanceof Interest)) return false;
        Interest interest = (Interest) o;
        return name.equals(interest.name);
    }

    /**
     * Calculates the hash value of an interest object
     *
     * @return the hash value
     */
    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "<" + name + "," + weight + ">";
    }

    @Override
    public int compareTo(Object o) {
        return -(this.getWeight().compareTo(((Interest) o).getWeight()));
    }
}

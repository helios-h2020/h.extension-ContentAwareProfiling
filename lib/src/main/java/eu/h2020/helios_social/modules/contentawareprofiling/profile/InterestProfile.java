package eu.h2020.helios_social.modules.contentawareprofiling.profile;

import java.util.ArrayList;
import java.util.HashMap;

// EDITED (DETAILED PROFILE)
public class InterestProfile extends ContentAwareProfile {

    protected ArrayList<Interest> interests;
    private HashMap<Interest, ArrayList<ImageInterest>> detailedInterests;

    public InterestProfile() {
    }

    public InterestProfile(ArrayList<Interest> interests) {
        this.interests = interests;
    }

    public InterestProfile setInterests(ArrayList<Interest> interests) {
        this.interests = interests;
        return this;
    }

    public InterestProfile setDetailedInterests(HashMap<Interest, ArrayList<ImageInterest>> detailedInterests) {
        this.detailedInterests = detailedInterests;
        return this;
    }

    public ArrayList<Interest> getInterests() {
        return interests;
    }

    public HashMap<Interest, ArrayList<ImageInterest>> getDetailedInterests() {
        return detailedInterests;
    }


    public Interest getInterest(String name) {
        if (interests == null) throw new NullPointerException("Null Interest Profile");
        int index = interests.indexOf(new Interest("name", null));
        return index < 0 ? null : interests.get(index);
    }

    public Double getWeight(String name) {
        if (interests == null) throw new NullPointerException("Null Interest Profile");
        int index = interests.indexOf(new Interest("name", null));
        return index < 0 ? null : interests.get(index).getWeight();
    }

}

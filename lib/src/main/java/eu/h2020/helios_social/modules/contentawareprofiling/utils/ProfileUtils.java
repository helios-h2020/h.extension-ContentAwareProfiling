package eu.h2020.helios_social.modules.contentawareprofiling.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import eu.h2020.helios_social.modules.contentawareprofiling.Image;
import eu.h2020.helios_social.modules.contentawareprofiling.interestcategories.InterestCategories;
import eu.h2020.helios_social.modules.contentawareprofiling.profile.ImageInterest;
import eu.h2020.helios_social.modules.contentawareprofiling.profile.Interest;

public class ProfileUtils {

    public static ArrayList<Interest> transformToInterestProfile(ArrayList<Float> rawProfile, ArrayList<InterestCategories> interestCategories) {
        ArrayList<Interest> interestProfile = new ArrayList<>();
        for (int i = 0; i < rawProfile.size(); i++) {
            String interestName = interestCategories.get(i).toString();
            float interestWeight = rawProfile.get(i);
            Interest interest = new Interest(interestName, (double) interestWeight);
            interestProfile.add(interest);
        }
        return interestProfile;
    }

    // EDITED (DETAILED PROFILE)
    public static HashMap<Interest, ArrayList<ImageInterest>> transformToDetailedInterestProfile(
            ArrayList<Interest> interestProfile, ArrayList<ArrayList<Float>> CNNOutMatrix,
            ArrayList<Image> images) {
        // hashmap contains categories (and their weights) as keys and images (and their weights) as
        // values.
        HashMap<Interest, ArrayList<ImageInterest>> detailedInterestProfile = new HashMap<>();
        // boolean to know if we have to filter the image set or not.
        Boolean keepTopXImages = false;
        int topXImages = 3;
        if (images.size()>topXImages){
            keepTopXImages = true;
        }
        // for each category
        for (int i = 0; i < interestProfile.size(); i++) {
            // create an image list
            ArrayList<ImageInterest> imageInterests = new ArrayList<>();
            // for each image
            for (int j = 0; j < images.size(); j++) {
                Image image = images.get(j);
                // cnn output value
                float weight = CNNOutMatrix.get(j).get(i);
                // image and its weight
                ImageInterest imageInterest = new ImageInterest(image.getUri(), (double) weight);
                // add to the list of images
                imageInterests.add(imageInterest);
            }
            // sort images
            Collections.sort(imageInterests);
            ArrayList<ImageInterest> topImageInterests = new ArrayList<>();
            if (keepTopXImages) {
                // select top X images
                for (int j = 0; j < topXImages; j++) {
                    topImageInterests.add(imageInterests.get(j));
                }
                // put top X images
                detailedInterestProfile.put(interestProfile.get(i),topImageInterests);
            } else {
                // put them all
                detailedInterestProfile.put(interestProfile.get(i),imageInterests);

            }
        }
        return detailedInterestProfile;
    }


    public static ArrayList<Float> getCoarseProfileFromFine(ArrayList<Float> fineProfile, int[] idxStartEnd) {
        ArrayList<Float> coarseProfileArrayList = new ArrayList<>();
        boolean inCombination = false;
        int coarseIdx = 0;
        int idxIdx = 0;
        for (int i = 0; i < fineProfile.size(); i++) {
            if (idxIdx >= idxStartEnd.length) {
                coarseProfileArrayList.add(fineProfile.get(i));
                coarseIdx++;
                continue;
            }
            if ((i == idxStartEnd[idxIdx]) && !inCombination) {
                inCombination = true;
                coarseProfileArrayList.add(fineProfile.get(i));
                idxIdx++;
                continue;
            }
            if ((i == idxStartEnd[idxIdx]) && inCombination) {
                inCombination = false;
                float oldValue = coarseProfileArrayList.get(coarseIdx);
                float newValue = oldValue + fineProfile.get(i);
                coarseProfileArrayList.set(coarseIdx, newValue);
                idxIdx++;
                coarseIdx++;
                continue;
            }
            if ((i != idxStartEnd[idxIdx]) && inCombination) {
                float oldValue = coarseProfileArrayList.get(coarseIdx);
                float newValue = oldValue + fineProfile.get(i);
                coarseProfileArrayList.set(coarseIdx, newValue);
                continue;
            }
            if ((i != idxStartEnd[idxIdx]) && !inCombination) {
                coarseProfileArrayList.add(fineProfile.get(i));
                coarseIdx++;
            }
        }
        ArrayList<Float> coarseProfile = new ArrayList<>();
        for (int i = 0; i < coarseProfileArrayList.size(); i++) {
            coarseProfile.add(coarseProfileArrayList.get(i));
        }
        return coarseProfile;
    }

    public static float cosineSimilarity(ArrayList<Float> profile1, ArrayList<Float> profile2) {
        float result = 0.0f;
        for (int i = 0; i < profile1.size(); i++) {
            result += profile1.get(i) * profile2.get(i);
        }
        return result;
    }
}

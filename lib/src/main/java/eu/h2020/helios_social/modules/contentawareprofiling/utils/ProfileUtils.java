package eu.h2020.helios_social.modules.contentawareprofiling.utils;

import java.util.ArrayList;

import eu.h2020.helios_social.modules.contentawareprofiling.interestcategories.InterestCategories;
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

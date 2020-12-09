package eu.h2020.helios_social.modules.contentawareprofiling.profile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class ProfileUtils {

    public static ArrayList<Integer> mergeUnique(ArrayList<String> array1, ArrayList<String> array2) {
        HashSet<String> mergedSet = new HashSet<>();
        ArrayList<Integer> idx = new ArrayList<>();
        Integer mergedIdx = 0;
        for (int i = 0; i < array1.size(); i++) {
            if (!(mergedSet.contains(array1.get(i)))) {
                mergedSet.add(array1.get(i));
                idx.add(mergedIdx);
                mergedIdx++;
            } else {
                idx.add(-1);
            }
        }
        for (int i = 0; i < array2.size(); i++) {
            if (!(mergedSet.contains(array2.get(i)))) {
                mergedSet.add(array2.get(i));
                idx.add(mergedIdx);
                mergedIdx++;
            } else {
                idx.add(-1);
            }
        }

        return idx;
    }

    public static <T> ArrayList<T> mergeUnique(ArrayList<T> array1,
                                               ArrayList<T> array2,
                                               ArrayList<Integer> indexes) {
        // int occurrences = Collections.frequency(indexes, -1);
        HashMap<Integer, T> result = new HashMap<>();
        // ArrayList<T> result = new ArrayList<>(indexes.size());
        for (int i = 0; i < array1.size(); i++) {
            Integer mergedPlaceIdx = indexes.get(i);
            if (mergedPlaceIdx != -1) {
                // result.set(mergedPlaceIdx, (T) array1.get(i));
                result.put(mergedPlaceIdx, (T) array1.get(i));
            }
        }
        for (int i = 0; i < array2.size(); i++) {
            Integer mergedPlaceIdx = indexes.get(array1.size() + i);
            if (mergedPlaceIdx != -1) {
                // result.set(mergedPlaceIdx, (T) array2.get(i));
                result.put(mergedPlaceIdx, (T) array2.get(i));
            }
        }
        ArrayList<T> resultList = new ArrayList<>();
        for (int i = 0; i < result.size(); i++) {
            resultList.add(result.get(i));
        }
        return resultList;
    }
}

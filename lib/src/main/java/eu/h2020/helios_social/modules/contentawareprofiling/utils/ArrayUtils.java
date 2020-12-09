package eu.h2020.helios_social.modules.contentawareprofiling.utils;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ArrayUtils {

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static <T> List<T> toList(T[][] array) {
        return Arrays.stream(array)
                .flatMap(Arrays::stream)
                .collect(Collectors.toList());
    }

    public static float[][] mergeArrays(float[][] array1, float[][] array2) throws IllegalAccessException {
        int numOfColumns = array1[0].length;
        if (array2[0].length != numOfColumns) {
            throw new IllegalAccessException("Column dimension should be equal");
        }
        int numOfRows1 = array1.length;
        int numOfRows2 = array2.length;
        float[][] mergedArray = new float[numOfRows1 + numOfRows2][numOfColumns];
        for (int i = 0; i < numOfRows1 + numOfRows2; i++) {
            for (int j = 0; j < numOfColumns; j++) {
                if (i < numOfRows1) {
                    mergedArray[i][j] = array1[i][j];
                } else {
                    mergedArray[i][j] = array2[i - numOfRows1][j];
                }
            }
        }
        return mergedArray;
    }
}

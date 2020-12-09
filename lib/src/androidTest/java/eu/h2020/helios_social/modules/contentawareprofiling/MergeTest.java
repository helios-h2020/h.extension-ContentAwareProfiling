package eu.h2020.helios_social.modules.contentawareprofiling;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Test;

import java.util.ArrayList;

import eu.h2020.helios_social.modules.contentawareprofiling.profile.ProfileUtils;

import static org.junit.Assert.assertArrayEquals;


public class MergeTest {

    @Test
    public void isMergeUniqueCorrect() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

        ArrayList<String> array1 = new ArrayList<>();
        array1.add("a");
        array1.add("f");
        array1.add("a");

        ArrayList<String> array2 = new ArrayList<>();
        array2.add("a");
        array2.add("b");
        array2.add("c");

        ArrayList<Integer> resultIdx = ProfileUtils.mergeUnique(array1, array2);
        ArrayList<Integer> expectedIdx = new ArrayList<>();
        expectedIdx.add(0);
        expectedIdx.add(1);
        expectedIdx.add(-1);
        expectedIdx.add(-1);
        expectedIdx.add(2);
        expectedIdx.add(3);

        assertArrayEquals(
                convertArrayList2Array(expectedIdx),
                convertArrayList2Array(resultIdx)
        );

        ArrayList<Integer> sideArray1 = new ArrayList<>();
        sideArray1.add(100);
        sideArray1.add(101);
        sideArray1.add(102);

        ArrayList<Integer> sideArray2 = new ArrayList<>();
        sideArray2.add(103);
        sideArray2.add(104);
        sideArray2.add(105);

        ArrayList<Integer> mergeResult = ProfileUtils.mergeUnique(sideArray1, sideArray2, resultIdx);
        ArrayList<Integer> mergeExpected = new ArrayList<>();
        mergeExpected.add(100);
        mergeExpected.add(101);
        mergeExpected.add(104);
        mergeExpected.add(105);

        assertArrayEquals(
                convertArrayList2Array(mergeResult),
                convertArrayList2Array(mergeExpected)
        );
    }

    public static int[] convertArrayList2Array(ArrayList<Integer> inputArray) {
        int[] outputArray = new int[inputArray.size()];
        for (int i = 0; i < inputArray.size(); i++) {
            outputArray[i] = inputArray.get(i);
        }
        return outputArray;
    }
}

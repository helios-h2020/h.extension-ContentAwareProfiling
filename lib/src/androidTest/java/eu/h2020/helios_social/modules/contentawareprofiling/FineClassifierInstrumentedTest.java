package eu.h2020.helios_social.modules.contentawareprofiling;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import eu.h2020.helios_social.core.contextualegonetwork.ContextualEgoNetwork;
import eu.h2020.helios_social.core.contextualegonetwork.Storage;
import eu.h2020.helios_social.modules.contentawareprofiling.data.CNNModelData;
import eu.h2020.helios_social.modules.contentawareprofiling.interestcategories.InterestCategoriesHierarchy;
import eu.h2020.helios_social.modules.contentawareprofiling.model.ModelType;
import eu.h2020.helios_social.modules.contentawareprofiling.profile.ContentAwareProfile;
import eu.h2020.helios_social.modules.contentawareprofiling.profile.FineInterestsProfile;

import static java.lang.System.currentTimeMillis;
import static org.junit.Assert.assertArrayEquals;

@RunWith(AndroidJUnit4.class)
public class FineClassifierInstrumentedTest {

    @Test
    public void isFineClassifierCorrect() throws IOException, NoSuchAlgorithmException {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        ContextualEgoNetwork egoNetwork = TestUtils.initializeCEN(appContext);

        ArrayList<Image> images = new ArrayList<>();

        images.add(new Image(TestUtils.getRawUri(appContext, R.raw.architecture), "architecture", currentTimeMillis(), 25.0f, 30.0f));

        images.add(new Image(TestUtils.getRawUri(appContext, R.raw.cars), "cars", currentTimeMillis(), 25.0f, 30.0f));

        images.add(new Image(TestUtils.getRawUri(appContext, R.raw.hair), "hair", currentTimeMillis(), 25.0f, 30.0f));

        images.add(new Image(TestUtils.getRawUri(appContext, R.raw.movies), "movies", currentTimeMillis(), 25.0f, 30.0f));

        images.add(new Image(TestUtils.getRawUri(appContext, R.raw.tennis), "tennis", currentTimeMillis(), 25.0f, 30.0f));

        ContentAwareProfileManager contentAwareProfileManager =
                new ContentAwareProfileManager(appContext, egoNetwork);

        ContentAwareProfile profile = contentAwareProfileManager.updateOrCreateProfile(FineInterestsProfile.class, images);

        ArrayList<ArrayList<Float>> expectedFineProfile = TestUtils.readExpectedBinaryArray(
                "/expected-test-results/expected_test_fine_profile.bin", InterestCategoriesHierarchy.fineCategories.size());
        float eps = (float) 0.1;

        CNNModelData modelData = loadModelData(egoNetwork);

        double[] profile09 = modelData.getRawProfile(0.9f, ModelType.FINE)
                .stream()
                .mapToDouble(f -> f != null ? f : Float.NaN)
                .toArray();
        double[] expected09 = expectedFineProfile.get(0)
                .stream()
                .mapToDouble(f -> f != null ? f : Float.NaN)
                .toArray();

        double[] profile05 = modelData.getRawProfile(0.5f, ModelType.FINE)
                .stream()
                .mapToDouble(f -> f != null ? f : Float.NaN)
                .toArray();
        double[] expected05 = expectedFineProfile.get(1)
                .stream()
                .mapToDouble(f -> f != null ? f : Float.NaN)
                .toArray();

        double[] profile03 = modelData.getRawProfile(0.3f, ModelType.FINE)
                .stream()
                .mapToDouble(f -> f != null ? f : Float.NaN)
                .toArray();

        double[] expected03 = expectedFineProfile.get(2)
                .stream()
                .mapToDouble(f -> f != null ? f : Float.NaN)
                .toArray();

        assertArrayEquals(expected09, profile09, eps);
        assertArrayEquals(expected05, profile05, eps);
        assertArrayEquals(expected03, profile03, eps);

    }

    public Bitmap readImageFileToBitmap(String imageFile) {
        InputStream inputStream = getClass().getResourceAsStream(imageFile);
        return BitmapFactory.decodeStream(inputStream);
    }

    private CNNModelData loadModelData(ContextualEgoNetwork egoNetwork) {
        Storage egoStorage = egoNetwork.getSerializer().getStorage();
        CNNModelData modelData;
        if (egoStorage.fileExists("eu.h2020.helios_social.modules.contentawareprofiling.miners.FineInterestProfileMiner")) {
            try {
                String stringModelData = egoStorage.loadFromFile("eu.h2020.helios_social.modules.contentawareprofiling.miners.FineInterestProfileMiner");
                modelData = (CNNModelData) egoNetwork.getSerializer().deserializeFromString(stringModelData);
            } catch (Exception e) {
                e.printStackTrace();
                modelData = new CNNModelData();
            }
        } else {
            modelData = new CNNModelData();
        }
        return modelData;
    }
}

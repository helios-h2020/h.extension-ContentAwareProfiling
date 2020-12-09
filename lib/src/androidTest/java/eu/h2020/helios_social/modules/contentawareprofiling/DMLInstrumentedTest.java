package eu.h2020.helios_social.modules.contentawareprofiling;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.ArrayList;

import eu.h2020.helios_social.core.contextualegonetwork.ContextualEgoNetwork;
import eu.h2020.helios_social.modules.contentawareprofiling.context.SpatioTemporalContext;
import eu.h2020.helios_social.modules.contentawareprofiling.data.DMLModelData;
import eu.h2020.helios_social.modules.contentawareprofiling.profile.ContentAwareProfile;
import eu.h2020.helios_social.modules.contentawareprofiling.profile.DMLProfile;

import static java.lang.System.currentTimeMillis;
import static org.junit.Assert.assertArrayEquals;

@RunWith(AndroidJUnit4.class)
public class DMLInstrumentedTest {

    @Test
    public void isDMLCorrect() throws IOException {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        AssetManager assetManager = appContext.getAssets();
        ContextualEgoNetwork egoNetwork = TestUtils.initializeCEN(appContext);

//        ContentAwareProfile testProfile = egoNetwork.getEgo().getOrCreateInstance(DMLProfile.class);

        ArrayList<Image> images = new ArrayList<>();
        ArrayList<SpatioTemporalContext> contexts = new ArrayList<>();

        images.add(new Image(TestUtils.getRawUri(appContext, R.raw.architecture), "architecture", currentTimeMillis(), 25.0f, 30.0f));
        images.add(new Image(TestUtils.getRawUri(appContext, R.raw.cars), "cars", currentTimeMillis(), 25.0f, 30.0f));
        images.add(new Image(TestUtils.getRawUri(appContext, R.raw.hair), "hair", currentTimeMillis(), 25.0f, 30.0f));
        images.add(new Image(TestUtils.getRawUri(appContext, R.raw.movies), "movies", currentTimeMillis(), 25.0f, 30.0f));
        images.add(new Image(TestUtils.getRawUri(appContext, R.raw.tennis), "tennis", currentTimeMillis(), 25.0f, 30.0f));

        ContentAwareProfileManager contentAwareProfileManager =
                new ContentAwareProfileManager(appContext, egoNetwork);
        Log.e("Error: ", "" + (appContext == null));

        ContentAwareProfile profile = contentAwareProfileManager.updateOrCreateProfile(
                DMLProfile.class, images);

        DMLModelData perImageOutput = (DMLModelData) ((DMLProfile) profile).getModelData();
        ArrayList<ArrayList<Float>> data = perImageOutput.getModelOutputData();

        double[] rawProfile = ((DMLProfile) profile).getProfile()
                .stream()
                .mapToDouble(f -> f != null ? f : Float.NaN)
                .toArray();

        ArrayList<ArrayList<Float>> expectedDMLProfile = TestUtils.readExpectedBinaryArray(
                "/expected-test-results/expected_test_dml_profile.bin", 256);

        float eps = (float) 0.1;

        double[] expectedProfile = expectedDMLProfile.get(0)
                .stream()
                .mapToDouble(f -> f != null ? f : Float.NaN)
                .toArray();

        egoNetwork.save();

        assertArrayEquals(expectedProfile, rawProfile, eps);
    }
}

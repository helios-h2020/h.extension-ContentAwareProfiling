package eu.h2020.helios_social.modules.contentawareprofiling;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import org.tensorflow.lite.Tensor;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;

import eu.h2020.helios_social.core.contextualegonetwork.ContextualEgoNetwork;
import eu.h2020.helios_social.core.contextualegonetwork.Interaction;
import eu.h2020.helios_social.core.contextualegonetwork.Utils;
import eu.h2020.helios_social.core.contextualegonetwork.listeners.CreationListener;
import eu.h2020.helios_social.core.contextualegonetwork.listeners.LoggingListener;
import eu.h2020.helios_social.core.contextualegonetwork.listeners.RecoveryListener;
import eu.h2020.helios_social.core.contextualegonetwork.storage.LegacyStorage;

public class TestUtils {

    public static Uri getRawUri(Context context, int rid) {
        Resources resources = context.getResources();
        return new Uri.Builder()
                .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
                .authority(resources.getResourcePackageName(rid))
                .appendPath(resources.getResourceTypeName(rid))
                .appendPath(resources.getResourceEntryName(rid))
                .build();
    }

    public static ArrayList<ArrayList<Float>> readExpectedBinaryArray(String file, int numOfColumns, int numberByteSize) throws IOException {
        InputStream fis = TestUtils.class.getResourceAsStream(file);
        DataInputStream ds = new DataInputStream(fis);

        int count = ds.available();

        ArrayList<ArrayList<Float>> features = new ArrayList(count / (numberByteSize * numOfColumns)); //[count / (numberByteSize * numOfColumns)][numOfColumns];

        for (int i = 0; i < count / (numberByteSize * numOfColumns); i++) {
            features.add(new ArrayList<>(Collections.nCopies(numOfColumns, null)));
        }
        System.out.println("features size: " + features.size());
        int idx = 0;
        while (ds.available() > 0) {

            float k = ds.readFloat();

            int row = idx / numOfColumns;
            int column = idx % numOfColumns;

            features.get(row).set(column, k);
            idx++;
        }

        return features;
    }

    public static ArrayList<ArrayList<Float>> readExpectedBinaryArray(String file, int numOfColumns) throws IOException {
        return readExpectedBinaryArray(file, numOfColumns, 4);
    }

    public static Bitmap readImageFileToBitmap(String imageFile) {
        InputStream inputStream = TestUtils.class.getResourceAsStream(imageFile);
        return BitmapFactory.decodeStream(inputStream);
    }

    public static ContextualEgoNetwork initializeCEN(Context appContext) {
        Utils.development = true;

        File ego = new File(appContext.getFilesDir().getPath().toString() + File.separator + "tests");

        if (ego.exists()) {
            deleteFolder(ego);
        }
        ContextualEgoNetwork egoNetwork = ContextualEgoNetwork.createOrLoad(
                new LegacyStorage(appContext.getFilesDir().getPath().toString() + File.separator + "tests" +
                        File.separator), "ego", "null");


        egoNetwork.addListener(
                new RecoveryListener());//automatic saving with minimal overhead
        egoNetwork.addListener(new CreationListener());//keep timestamps
        egoNetwork.addListener(new LoggingListener());//print events
        //Some needed non-sense
        Interaction.class.getDeclaredConstructors();
        Tensor.class.getDeclaredConstructors();


        egoNetwork.setCurrent(egoNetwork.getOrCreateContext("All"));
        return egoNetwork;
    }

    static void deleteFolder(File file) {
        for (File subFile : file.listFiles()) {
            if (subFile.isDirectory()) {
                deleteFolder(subFile);
            } else {
                subFile.delete();
            }
        }
        file.delete();
    }
}

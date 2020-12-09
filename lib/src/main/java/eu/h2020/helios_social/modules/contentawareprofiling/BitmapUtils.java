package eu.h2020.helios_social.modules.contentawareprofiling;

import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class BitmapUtils {
    public static ArrayList<String> calculateBitmapHashes(ArrayList<Bitmap> bitmaps) throws NoSuchAlgorithmException {
        MessageDigest m;
        byte[] bitmapByteArray;
        ArrayList<String> hashes = new ArrayList<>();
        for (int i = 0; i < bitmaps.size(); i++) {
            m = MessageDigest.getInstance("MD5");
            bitmapByteArray = bitmap2ByteArray(bitmaps.get(i));
            m.update(bitmapByteArray, 0, bitmapByteArray.length);
            String hash = new BigInteger(1, m.digest()).toString(16);
            hashes.add(hash);
        }
        return hashes;
    }

    public static byte[] bitmap2ByteArray(Bitmap bm) {
        // https://stackoverflow.com/questions/15158651/generate-a-md5-sum
        // -from-an-android-bitmap-object
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos); //bm is the bitmap
        // object
        return baos.toByteArray();
    }
}

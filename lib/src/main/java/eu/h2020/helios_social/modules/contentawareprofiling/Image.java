package eu.h2020.helios_social.modules.contentawareprofiling;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.annotation.Nullable;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import eu.h2020.helios_social.modules.contentawareprofiling.context.SpatioTemporalContext;

public class Image {

    private String uri;
    private String name;
    private SpatioTemporalContext spatioTemporalContext;

    public Image() {
    }

    /**
     * @param uri The image URI
     * @param name The image name
     * @param timestamp The image timestamp
     * @param lat The image latitude
     * @param lng The image longitude
     */
    public Image(Uri uri, String name, Long timestamp, Float lat, Float lng) {
        this.uri = uri.toString();
        this.name = name;
        this.spatioTemporalContext = new SpatioTemporalContext(lat, lng, timestamp);
    }

    /**
     * @param ctx The Android context
     * @return The image as a bitmap
     */
    public Bitmap getBitmap(Context ctx) {
        try {
            return MediaStore.Images.Media.getBitmap(ctx.getContentResolver(), Uri.parse(uri));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getUri() {
        return uri;
    }

    public String getName() {
        return name;
    }

    /**
     * @param ctx The Android context
     * @return The hash of the image
     */
    public String getHash(Context ctx) {
        Bitmap bitmap = getBitmap(ctx);
        ArrayList<Bitmap> bitmaps = new ArrayList<>();
        bitmaps.add(bitmap);
        try {
            ArrayList<String> hashes = BitmapUtils.calculateBitmapHashes(bitmaps);
            return hashes.get(0);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @return The spatio-temporal context of the image.
     */
    public SpatioTemporalContext getContext() {
        return spatioTemporalContext;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        return o instanceof Image && uri.equals(((Image) o).uri);
    }

}

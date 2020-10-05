package edu.byu.cs.tweeter.view.util;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Contains utility methods for working with Android images.
 */
public class ImageUtils {

    /**
     * Creates a drawable from the bytes read from an image file.
     *
     * @param bytes the bytes.
     * @return the drawable.
     */
    public static Drawable drawableFromByteArray(byte [] bytes) {
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        return new BitmapDrawable(Resources.getSystem(), bitmap);
    }

    public static byte[] byteArrayFromUri(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }
}

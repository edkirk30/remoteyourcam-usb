package com.remoteyourcam.usb.util;

import android.graphics.Bitmap;
import android.media.ExifInterface;
import android.util.Log;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 * Created by edkirk on 10/06/2016.
 */
public class ImageFileUtilities {

    private static final String TAG = ImageFileUtilities.class.getSimpleName();

    public static boolean bitmapToJpeg(String directory,
                                String filename,
                                Bitmap bitmap,
                                       int jpegCompression) {

        return bitmapToJpeg(directory, filename, bitmap, jpegCompression, null, "jpg");
    }

    public static boolean bitmapToJpeg(String directory,
                                String filename,
                                Bitmap bitmap,
                                       int jpegCompression,
                                ExifInterface exifData) {

        return bitmapToJpeg(directory, filename, bitmap, jpegCompression, exifData, "jpg");
    }

    public static boolean bitmapToJpeg(String directory,
                                String filename,
                                Bitmap bitmap,
                                       int jpegCompression,
                                ExifInterface exifData,
                                String fileExtension) {


        //Get full path to file
        String fullPath = directory + filename + '.' + fileExtension;

        try {
            FileOutputStream fileStream = new FileOutputStream(fullPath);
            bitmap.compress(Bitmap.CompressFormat.JPEG, jpegCompression, fileStream);
        } catch (FileNotFoundException exception) {

            Log.e(TAG, "Could not create jpeg file");
            exception.printStackTrace();
            return false;
       }

        //FIXME add exif


        return true;
    }



}

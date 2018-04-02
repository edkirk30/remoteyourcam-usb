package com.remoteyourcam.usb.ptp.commands;

import android.graphics.Bitmap;
import android.media.ExifInterface;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import com.remoteyourcam.usb.ptp.Camera;
import com.remoteyourcam.usb.ptp.PtpAction;
import com.remoteyourcam.usb.ptp.PtpCamera;
import com.remoteyourcam.usb.ptp.PtpConstants;
import com.remoteyourcam.usb.ptp.model.JpegByteBuffer;
import com.remoteyourcam.usb.ptp.model.ObjectInfo;
import com.remoteyourcam.usb.util.ImageFileUtilities;

import io.sentry.Sentry;

/**
 * Created by edkirk on 02/06/2016.
 */
public class SaveAndDeleteAction extends PtpActionClass {

    private final String TAG = SaveAndDeleteAction.class.getSimpleName();

    private final PtpCamera camera;
    //private final Camera.StorageInfoListener listener;
    private final int objectHandle;
    private final String directoryPath;
    private final String filename;
    private final int jpegCompression;

    private Camera.SaveAndDeleteListener listener;

    public SaveAndDeleteAction(PtpCamera camera, Camera.SaveAndDeleteListener listener, int objectHandle,
                               String directoryPath, String filename, int jpegCompression) {
        this.camera = camera;
        this.listener = listener;
        this.objectHandle = objectHandle;
        this.filename = filename;
        this.directoryPath = directoryPath;
        this.jpegCompression = jpegCompression;
    }

    @Override
    public void exec(PtpCamera.IO io) {

        GetObjectInfoCommand getObjectInfo = new GetObjectInfoCommand(camera, objectHandle);
        io.handleCommand(getObjectInfo);

        ObjectInfo objectInfo = getObjectInfo.getObjectInfo();
        Log.i(TAG, "objectInfo.imagePixWidth:" + objectInfo.imagePixWidth +
                "objectInfo.imagePixHeight:" + objectInfo.imagePixHeight +
                "objectInfo.objectFormat:" + objectInfo.objectFormat +
                        "objectInfo.thumbFormat:" + objectInfo.thumbFormat +
                        "objectInfo.parentObject:" + objectInfo.parentObject +
                        "objectInfo.associationType:" + objectInfo.associationType +
                        "objectInfo.associationDesc:" + objectInfo.associationDesc +
                        "objectInfo.sequenceNumber:" + objectInfo.sequenceNumber +
                        "objectInfo.filename:" + objectInfo.filename
        );

        //Get image and save
        GetObjectCommand getObject = new GetObjectCommand(camera, objectHandle, 1);
        io.handleCommand(getObject);

        responseCode = getObject.getResponseCode();
        hasResponseReceived = true;

        if (responseCode != PtpConstants.Response.Ok) {

            Log.e(TAG, "Could not copy file to new location.");
            lastUserErrorMessage = "Unable to get image from camera.";

            return;

        }

        //Get buffer
        JpegByteBuffer buffer = getObject.getBuffer();

        String fullFilePath = directoryPath + filename + ".jpg";

        File imageFile = new File(fullFilePath);

        try
        {
            FileOutputStream fos = new FileOutputStream(imageFile);
            fos.write(buffer.buffer.array(), buffer.bufferOffset, buffer.bufferLength - buffer.bufferOffset);
            fos.close();
            Log.d(TAG,"New Image saved:" + fullFilePath);

        } catch (Exception exception) {
            Log.d(TAG, "File" + fullFilePath + "not saved, filename:" + fullFilePath + " error.getMessage:"
                    + exception.getMessage());

            Sentry.capture(exception);
            return;
        }

        //Delete image
        DeleteObjectCommand deleteObject = new DeleteObjectCommand(camera, objectHandle);
        io.handleCommand(deleteObject);

        Log.i("SaveAndDeleteAction", "deleted");

        responseCode = deleteObject.getResponseCode();
        hasResponseReceived = true;

        if (responseCode != PtpConstants.Response.Ok) {
            Log.e(TAG, "Delete unsuccessful");
            lastUserErrorMessage = "Unable to delete image from camera.";

            return;
        }

        listener.onSaveAndDeleteComplete(fullFilePath);

    }

    @Override
    public void reset() {
    }
}
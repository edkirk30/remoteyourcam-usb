package com.remoteyourcam.usb.ptp.commands;

import android.graphics.Bitmap;
import android.util.Log;

import java.io.FileOutputStream;

import com.remoteyourcam.usb.ptp.PtpAction;
import com.remoteyourcam.usb.ptp.PtpCamera;
import com.remoteyourcam.usb.ptp.PtpConstants;

/**
 * Created by edkirk on 02/06/2016.
 */
public class SaveAndDeleteAction implements PtpAction {


    private final PtpCamera camera;
    //private final Camera.StorageInfoListener listener;
    private final int objectHandle;
    private final String savePath;

    public SaveAndDeleteAction(PtpCamera camera, int objectHandle, String savePath) {
        this.camera = camera;
        this.objectHandle = objectHandle;
        this.savePath = savePath;
    }


    @Override
    public void exec(PtpCamera.IO io) {


        //Get image and save
        GetObjectCommand getObject = new GetObjectCommand(camera, objectHandle, 1);
        io.handleCommand(getObject);

        if (getObject.getResponseCode() != PtpConstants.Response.Ok || getObject.getBitmap() == null) {
            //FIXME
            //listener.onImageRetrieved(0, null);
            return;
        }

        Bitmap image = getObject.getBitmap();


        try {
            FileOutputStream fileStream = new FileOutputStream(savePath);
            image.compress(Bitmap.CompressFormat.JPEG, 70, fileStream);
        } catch (Exception e) {


            Log.i("SaveAndDeleteAction", "copy fail");
            e.printStackTrace();
        }


        //Delete image
        DeleteObjectCommand deleteObject = new DeleteObjectCommand(camera, objectHandle);
        io.handleCommand(deleteObject);

        Log.i("SaveAndDeleteAction", "deleted");

        if (deleteObject.getResponseCode() != PtpConstants.Response.Ok) {
            //listener.onImageRetrieved(0, null);
            Log.i("SaveAndDeleteAction", "deleted response");
            return;
        }

    }

    @Override
    public void reset() {
    }
}
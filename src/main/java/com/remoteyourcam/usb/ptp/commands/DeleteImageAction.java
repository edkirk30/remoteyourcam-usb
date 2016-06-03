package com.remoteyourcam.usb.ptp.commands;

import android.util.Log;

import com.remoteyourcam.usb.ptp.Camera;
import com.remoteyourcam.usb.ptp.PtpAction;
import com.remoteyourcam.usb.ptp.PtpCamera;
import com.remoteyourcam.usb.ptp.PtpConstants;

/**
 * Created by edkirk on 02/06/2016.
 */
public class DeleteImageAction implements PtpAction {


    private final PtpCamera camera;
    //private final Camera.StorageInfoListener listener;
    private final int objectHandle;

    public DeleteImageAction(PtpCamera camera, int objectHandle) {
        this.camera = camera;
        this.objectHandle = objectHandle;
    }

    @Override
    public void exec(PtpCamera.IO io) {

        DeleteObjectCommand getObject = new DeleteObjectCommand(camera, objectHandle);
        io.handleCommand(getObject);

        Log.i("HEREHERHERER", "deleted");

        if (getObject.getResponseCode() != PtpConstants.Response.Ok) {
            //listener.onImageRetrieved(0, null);
            Log.i("HEREHERHERER", "deleted response");
            return;
        }

        /*

        GetStorageIdsCommand getStorageIds = new GetStorageIdsCommand(camera);
        io.handleCommand(getStorageIds);

        if (getStorageIds.getResponseCode() != PtpConstants.Response.Ok) {
            listener.onAllStoragesFound();
            return;
        }

        int ids[] = getStorageIds.getStorageIds();
        for (int i = 0; i < ids.length; ++i) {
            int storageId = ids[i];
            GetStorageInfoCommand c = new GetStorageInfoCommand(camera, storageId);
            io.handleCommand(c);

            if (c.getResponseCode() != PtpConstants.Response.Ok) {
                listener.onAllStoragesFound();
                return;
            }

            String label = c.getStorageInfo().volumeLabel.isEmpty() ? c.getStorageInfo().storageDescription : c
                    .getStorageInfo().volumeLabel;
            if (label == null || label.isEmpty()) {
                label = "Storage " + i;
            }
            listener.onStorageFound(storageId, label);
        }

        listener.onAllStoragesFound();
        */
    }

    @Override
    public void reset() {
    }
}

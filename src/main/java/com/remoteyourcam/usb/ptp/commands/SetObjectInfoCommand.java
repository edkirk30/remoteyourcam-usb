package com.remoteyourcam.usb.ptp.commands;

import android.util.Log;

import com.remoteyourcam.usb.ptp.Camera;
import com.remoteyourcam.usb.ptp.PtpCamera;
import com.remoteyourcam.usb.ptp.PtpConstants;
import com.remoteyourcam.usb.ptp.model.ObjectInfo;

import java.nio.ByteBuffer;

/**
 * Created by edkirk on 17/06/2016.
 */
public class SetObjectInfoCommand extends Command {

    private final String TAG = SetObjectInfoCommand.class.getSimpleName();

    private int objectHandle;
    private ObjectInfo objectInfo;

    public SetObjectInfoCommand (PtpCamera camera, int objectHandle, ObjectInfo objectInfo) {

        super(camera);

        Log.v(TAG, "SetObjectInfoCommand objectHandle:" + objectHandle);

        this.objectHandle = objectHandle;
        this.objectInfo = objectInfo;

        hasDataToSend = true;
    }


    @Override
    public void exec(PtpCamera.IO io) {
        io.handleCommand(this);
    }

    @Override
    public void encodeCommand(ByteBuffer b) {

        Log.v(TAG, "encodeCommand objectHandle:" + objectHandle);

        encodeCommand(b, PtpConstants.Operation.SendObjectInfo, 0, 0);
/*
        if (responseCode == PtpConstants.Response.DeviceBusy) {
            Log.v(TAG, "encodeCommand - busy");

            camera.onDeviceBusy(this, true);
            return;
        } else if (responseCode == PtpConstants.Response.Ok) {
            Log.v(TAG, "encodeCommand - OK!");

        }
        else {
            Log.v(TAG, "encodeCommand - Fail responseCode:" + responseCode);
        }
        */
    }


    @Override
    public void encodeData(ByteBuffer b) {

        Log.v(TAG, "encodeData");

        // header
        //FIXME size fixed
        b.putInt(12 + objectInfo.size());
        b.putShort((short) PtpConstants.Type.Data);
        b.putShort((short) PtpConstants.Operation.SendObjectInfo);
        b.putInt(camera.currentTransactionId());
        // specific block

        objectInfo.encode(b);

        Log.v(TAG, "encodeData responseCode:" + responseCode);

    }
}

package com.remoteyourcam.usb.ptp.commands;

import com.remoteyourcam.usb.ptp.PtpCamera;
import com.remoteyourcam.usb.ptp.PtpConstants;

import java.nio.ByteBuffer;

/**
 * Created by edkirk on 17/06/2016.
 */
public class MoveObjectCommand extends Command {

    private final int objectHandle;
    private final int storageId;
    private final int newParentHandle;

    public MoveObjectCommand(PtpCamera camera, int objectHandle, int storageId, int newParentHandle) {
        super(camera);
        this.objectHandle = objectHandle;
        this.storageId = storageId;
        this.newParentHandle = newParentHandle;
    }

    @Override
    public void exec(PtpCamera.IO io) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void encodeCommand(ByteBuffer b) {
        encodeCommand(b, PtpConstants.Operation.MoveObject, objectHandle, storageId, newParentHandle);
    }

    /*
    @Override
    public void encodeData(ByteBuffer b, ) {

        // header
        b.putInt(12 + outBitmap.getByteCount());
        b.putShort((short) PtpConstants.Type.Data);
        b.putShort((short) PtpConstants.Operation.MoveObject);
        b.putInt(camera.currentTransactionId());
//move - 1019
// SetDevicePropValue - 0x9110


    }
*/

/*
    @Override
    public void encodeData(ByteBuffer b) {
        // header
        b.putInt(12 + PtpConstants.getDatatypeSize(datatype));
        b.putShort((short) PtpConstants.Type.Data);
        b.putShort((short) PtpConstants.Operation.SetDevicePropValue);
        b.putInt(camera.currentTransactionId());
        // specific block
        if (datatype == PtpConstants.Datatype.int8 || datatype == PtpConstants.Datatype.uint8) {
            b.put((byte) value);
        } else if (datatype == PtpConstants.Datatype.int16 || datatype == PtpConstants.Datatype.uint16) {
            b.putShort((short) value);
        } else if (datatype == PtpConstants.Datatype.int32 || datatype == PtpConstants.Datatype.uint32) {
            b.putInt(value);
        } else {
            throw new UnsupportedOperationException();
        }
    }
    */
}

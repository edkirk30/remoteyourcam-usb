package com.remoteyourcam.usb.ptp.commands;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.remoteyourcam.usb.ptp.PtpCamera;
import com.remoteyourcam.usb.ptp.PtpConstants;

import java.nio.ByteBuffer;

/**
 * Created by edkirk on 16/06/2016.
 */
public class SendObjectCommand extends Command {

    private static final String TAG = SendObjectCommand.class.getSimpleName();

    private Bitmap outBitmap;
    private boolean outOfMemoryError;

    public SendObjectCommand(PtpCamera camera, Bitmap bitmap) {
        super(camera);
        outBitmap = bitmap;
        hasDataToSend = true;
    }

    @Override
    public void exec(PtpCamera.IO io) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void encodeCommand(ByteBuffer buffer) {
        //encodeCommand(b, PtpConstants.Operation.SendObject, objectHandle);
        encodeCommand(buffer, PtpConstants.Operation.SendObject);
    }


    //################################################

    //private final int objectHandle;

    //private final BitmapFactory.Options options;


    /*
    public GetObjectCommand(PtpCamera camera, int objectHandle, int sampleSize) {
        super(camera);
        this.objectHandle = objectHandle;
        options = new BitmapFactory.Options();
        if (sampleSize >= 1 && sampleSize <= 4) {
            options.inSampleSize = sampleSize;
        } else {
            options.inSampleSize = 2;
        }
    }
*/
    public void setBitmap(Bitmap bitmap) {
         outBitmap = bitmap;
    }

    public boolean isOutOfMemoryError() {
        return outOfMemoryError;
    }


    @Override
    public void reset() {
        super.reset();
        outBitmap = null;
        outOfMemoryError = false;
    }

    public int getSize() {

        return outBitmap.getByteCount();
    }

    @Override
    public void encodeData(ByteBuffer b) {

        Log.i(TAG, "encodeData");

        // header
        //FIXME correct size?
        b.putInt(12 + outBitmap.getByteCount());
        b.putShort((short) PtpConstants.Type.Data);
        b.putShort((short) PtpConstants.Operation.SendObject);
        b.putInt(camera.currentTransactionId());

        //Image data
        outBitmap.copyPixelsToBuffer(b);

    }


/*
    @Override
    protected void decodeData(ByteBuffer b, int length) {
        try {
            // 12 == offset of data header
            inBitmap = BitmapFactory.decodeByteArray(b.array(), 12, length - 12, options);
        } catch (RuntimeException e) {
            Log.i(TAG, "exception on decoding picture : " + e.toString());
        } catch (OutOfMemoryError e) {
            System.gc();
            outOfMemoryError = true;
        }
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

/*
    @Override
    protected void decodeData(ByteBuffer b, int length) {
        try {
            // 12 == offset of data header
            inBitmap = BitmapFactory.decodeByteArray(b.array(), 12, length - 12, options);
        } catch (RuntimeException e) {
            Log.i(TAG, "exception on decoding picture : " + e.toString());
        } catch (OutOfMemoryError e) {
            System.gc();
            outOfMemoryError = true;
        }
    }
    */

}

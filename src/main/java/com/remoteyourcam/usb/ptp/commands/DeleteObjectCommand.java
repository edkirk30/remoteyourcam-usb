package com.remoteyourcam.usb.ptp.commands;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.remoteyourcam.usb.ptp.PtpCamera;
import com.remoteyourcam.usb.ptp.PtpCamera.IO;
import com.remoteyourcam.usb.ptp.PtpConstants;

import java.nio.ByteBuffer;

/**
 * Created by edkirk on 02/06/2016.
 */

public class DeleteObjectCommand extends Command {

    private static final String TAG = GetThumb.class.getSimpleName();

    private final int objectHandle;

    public DeleteObjectCommand(PtpCamera camera, int objectHandle) {
        super(camera);
        this.objectHandle = objectHandle;
    }

    @Override
    public void exec(PtpCamera.IO io) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void reset() {
        super.reset();
    }

    @Override
    public void encodeCommand(ByteBuffer b) {
        encodeCommand(b, PtpConstants.Operation.DeleteObject, objectHandle);
    }

    @Override
    protected void decodeData(ByteBuffer b, int length) {
        //FIXME check successful?
        Log.i(TAG, "decodeData");

        /*
        try {
            // 12 == offset of data header
            inBitmap = BitmapFactory.decodeByteArray(b.array(), 12, length - 12);
        } catch (RuntimeException e) {
            Log.i(TAG, "exception on decoding picture : " + e.toString());
        } catch (OutOfMemoryError e) {
            System.gc();
        }
        */
    }
}
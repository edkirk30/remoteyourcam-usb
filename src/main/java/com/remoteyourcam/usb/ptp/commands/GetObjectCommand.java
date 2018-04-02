/**
 * Copyright 2013 Nils Assbeck, Guersel Ayaz and Michael Zoech
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.remoteyourcam.usb.ptp.commands;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import com.remoteyourcam.usb.ptp.PtpCamera;
import com.remoteyourcam.usb.ptp.PtpCamera.IO;
import com.remoteyourcam.usb.ptp.PtpConstants;
import com.remoteyourcam.usb.ptp.model.JpegByteBuffer;

/**
 * Read file data from camera with specified {@code objectHandle}.
 */
public class GetObjectCommand extends Command {

    private static final String TAG = GetObjectCommand.class.getSimpleName();

    private final int objectHandle;

    private final BitmapFactory.Options options;
    private Bitmap inBitmap;
    private JpegByteBuffer localBuffer = null;

    private boolean outOfMemoryError;

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

    public Bitmap getBitmap() {
        return inBitmap;
    }

    public JpegByteBuffer getBuffer() {
        return localBuffer;
    }

    public boolean isOutOfMemoryError() {
        return outOfMemoryError;
    }

    @Override
    public void exec(IO io) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void reset() {
        super.reset();
        inBitmap = null;
        outOfMemoryError = false;
    }

    @Override
    public void encodeCommand(ByteBuffer b) {
        encodeCommand(b, PtpConstants.Operation.GetObject, objectHandle);
    }

    @Override
    protected void decodeData(ByteBuffer buffer, int length) {

        Log.v(TAG, "decodeData length:" + length);

        try {
            // 12 == offset of data header
            //inBitmap = BitmapFactory.decodeByteArray(buffer.array(), 12, length - 12, options);

            //Copy buffer
            localBuffer = new JpegByteBuffer();
            localBuffer.bufferLength = length;
            localBuffer.bufferOffset = 12;
            localBuffer.buffer = ByteBuffer.allocate(buffer.capacity());
            buffer.rewind();
            localBuffer.buffer.put(buffer);
            buffer.rewind();
            localBuffer.buffer.flip();

        } catch (RuntimeException e) {
            Log.i(TAG, "exception on decoding picture : " + e.toString());
        } catch (OutOfMemoryError e) {
            System.gc();
            outOfMemoryError = true;
        }
    }
}

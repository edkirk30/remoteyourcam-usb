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
package com.remoteyourcam.usb.ptp;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.location.GpsStatus;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.remoteyourcam.usb.AppConfig;
import com.remoteyourcam.usb.ptp.Camera.CameraListener;
import com.remoteyourcam.usb.ptp.PtpCamera.State;

import io.sentry.Sentry;

public class PtpUsbService implements PtpService {

    private final String TAG = PtpUsbService.class.getSimpleName();

    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
    private final BroadcastReceiver permissonReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            Log.i(TAG, "onReceive");

            //Toast toast = Toast.makeText(context, "PtpUsbService onReceive", Toast.LENGTH_LONG);
            //toast.show();

            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)) {

                unregisterPermissionReceiver(context);

                synchronized (this) {
                    UsbDevice device = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);

                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        try {
                            connect(context, device);
                        }
                        catch (java.lang.IllegalArgumentException exception) {

                            Log.e(TAG, "java.lang.IllegalArgumentException exception");
                            exception.printStackTrace();
                        }
                    } else {
                        //TODO report
                    }
                }
            }
        }
    };

    private final Handler handler = new Handler();
    private final UsbManager usbManager;
    private PtpCamera camera;
    //private CameraListener listener;

    private List<CameraListener> listeners = new ArrayList();

    Runnable shutdownRunnable = new Runnable() {
        @Override
        public void run() {
            shutdown();
        }
    };

    public PtpUsbService(Context context) {
        this.usbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);

    }

    @Override
    public void addCameraListener(CameraListener listener) {

        Log.i(TAG, "addCameraListener");

        listeners.add(listener);

        if (camera != null) {
            camera.addListener(listener);
        }
    }



/*
    @Override
    public void setCameraListener(CameraListener listener) {
        this.listener = listener;
        if (camera != null) {
            camera.setListener(listener);
        }
    }
    */

    @Override
    public void initialize(Context context, Intent intent) {

        //Toast toast = Toast.makeText(context, "USB connected (intent)", Toast.LENGTH_LONG);
        //toast.show();

        //Check for stale listeners
        for (Iterator<CameraListener> iterator = listeners.iterator(); iterator.hasNext();) {
            CameraListener listener = iterator.next();
            if (listener == null) {
                Log.i(TAG, "Removing listening listener == null");
                iterator.remove();
            }
        }

        handler.removeCallbacks(shutdownRunnable);

        if (camera != null) {
            if (AppConfig.LOG) {
                Log.i(TAG, "initialize: camera available");
            }
            if (camera.getState() == State.Active) {
                if (!listeners.isEmpty()) {
                    for (CameraListener listener : listeners) {
                        listener.onCameraStarted(camera);
                    }
                }
                return;
            }
            if (AppConfig.LOG) {
                Log.i(TAG, "initialize: camera not active, satet " + camera.getState());
            }
            camera.shutdownHard();
        }

        UsbDevice device = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
        if (false && device != null) {

            //Toast toast2 = Toast.makeText(context, "got device through intent", Toast.LENGTH_LONG);
            //toast2.show();

            if (AppConfig.LOG) {
                Log.i(TAG, "initialize: got device through intent");
            }
            connect(context, device);
        } else {
/*
            if (true) {
                Toast toast2 = Toast.makeText(context, "Did NOT get device through intent", Toast.LENGTH_LONG);
                toast2.show();
            }
            */

            //Toast toast20 = Toast.makeText(context, "Did NOT get device through intent", Toast.LENGTH_LONG);
            //toast20.show();

            if (AppConfig.LOG) {
                Log.i(TAG, "initialize: looking for compatible camera");
            }
            device = lookupCompatibleDevice(usbManager, context);

            if (device != null) {

                //Toast toast4 = Toast.makeText(context, "Device NOT null", Toast.LENGTH_LONG);
                //toast4.show();

                registerPermissionReceiver(context);
                PendingIntent mPermissionIntent = PendingIntent.getBroadcast(context, 0, new Intent(
                        ACTION_USB_PERMISSION), 0);
                usbManager.requestPermission(device, mPermissionIntent);

                //Toast toast5 = Toast.makeText(context, "after usbManager.requestPermission", Toast.LENGTH_LONG);
                //toast5.show();

            } else {

                //Toast toast6 = Toast.makeText(context, "Device null", Toast.LENGTH_LONG);
                //toast6.show();

                for (CameraListener listener : listeners) {
                    listener.onNoCameraFound();
                }
            }
        }
    }

    @Override
    public void shutdown() {
        if (AppConfig.LOG) {
            Log.i(TAG, "shutdown");
        }
        if (camera != null) {
            camera.shutdown();
            camera = null;
        }
    }

    @Override
    public void lazyShutdown() {
        if (AppConfig.LOG) {
            Log.i(TAG, "lazy shutdown");
        }
        handler.postDelayed(shutdownRunnable, 4000);
    }

    private void registerPermissionReceiver(Context context) {
        if (AppConfig.LOG) {
            Log.i(TAG, "register permission receiver");
        }
        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        context.registerReceiver(permissonReceiver, filter);
    }

    private void unregisterPermissionReceiver(Context context) {
        if (AppConfig.LOG) {
            Log.i(TAG, "unregister permission receiver");
        }
        context.unregisterReceiver(permissonReceiver);
    }

    private UsbDevice lookupCompatibleDevice(UsbManager manager, Context context) {
        Map<String, UsbDevice> deviceList = manager.getDeviceList();
        for (Map.Entry<String, UsbDevice> e : deviceList.entrySet()) {

            UsbDevice d = e.getValue();

            //Toast toast4 = Toast.makeText(context, "d.getVendorId():" + d.getVendorId(), Toast.LENGTH_LONG);
            //toast4.show();

            if (d.getVendorId() == PtpConstants.CanonVendorId || d.getVendorId() == PtpConstants.NikonVendorId) {
                return d;
            }
        }
        return null;
    }

    private boolean connect(Context context, UsbDevice device) {

        if (camera != null) {
            camera.shutdown();
            camera = null;
        }

        //Toast toast1 = Toast.makeText(context, "device.getInterfaceCount:" + device.getInterfaceCount(), Toast.LENGTH_LONG);
        //toast1.show();

        for (int i = 0, n = device.getInterfaceCount(); i < n; ++i) {
            UsbInterface intf = device.getInterface(i);

            if (intf.getEndpointCount() != 3) {
                continue;
            }

            UsbEndpoint in = null;
            UsbEndpoint out = null;

            for (int e = 0, en = intf.getEndpointCount(); e < en; ++e) {
                UsbEndpoint endpoint = intf.getEndpoint(e);

                //Toast toast2 = Toast.makeText(context, "endpoint.getType():" + endpoint.getType(), Toast.LENGTH_LONG);
                //toast2.show();

                if (endpoint.getType() == UsbConstants.USB_ENDPOINT_XFER_BULK) {
                    if (endpoint.getDirection() == UsbConstants.USB_DIR_IN) {
                        in = endpoint;
                    } else if (endpoint.getDirection() == UsbConstants.USB_DIR_OUT) {
                        out = endpoint;
                    }
                }
            }

            if (in == null || out == null) {
                Sentry.capture("in == null || out == null");
                continue;
            }

            if (AppConfig.LOG) {
                Log.i(TAG, "Found compatible USB interface");
                Log.i(TAG, "Interface class " + intf.getInterfaceClass());
                Log.i(TAG, "Interface subclass " + intf.getInterfaceSubclass());
                Log.i(TAG, "Interface protocol " + intf.getInterfaceProtocol());
                Log.i(TAG, "Bulk out max size " + out.getMaxPacketSize());
                Log.i(TAG, "Bulk in max size " + in.getMaxPacketSize());
            }

            if (!usbManager.hasPermission(device)) {

                //Toast toast = Toast.makeText(context, "!usbManager.hasPermission(device)", Toast.LENGTH_LONG);
                //toast.show();

                Sentry.capture("!usbManager.hasPermission(device)");
                return false;
            }

            if (device.getVendorId() == PtpConstants.CanonVendorId) {

                //Toast toast = Toast.makeText(context, "device.getVendorId() == PtpConstants.CanonVendorId", Toast.LENGTH_LONG);
                //toast.show();

                Sentry.capture("device.getVendorId() == PtpConstants.CanonVendorId");
                PtpUsbConnection connection = new PtpUsbConnection(usbManager.openDevice(device), in, out,
                        device.getVendorId(), device.getProductId());
                camera = new EosCamera(connection, listeners, new WorkerNotifier(context));
            } else if (device.getVendorId() == PtpConstants.NikonVendorId) {
                PtpUsbConnection connection = new PtpUsbConnection(usbManager.openDevice(device), in, out,
                        device.getVendorId(), device.getProductId());
                camera = new NikonCamera(connection, listeners, new WorkerNotifier(context));
            }
            else {
                Sentry.capture("ELSE device.getVendorId() == PtpConstants.CanonVendorId");
                PtpUsbConnection connection = new PtpUsbConnection(usbManager.openDevice(device), in, out,
                        device.getVendorId(), device.getProductId());
                camera = new GenericCamera(connection, listeners, new WorkerNotifier(context));

            }

            return true;
        }

        if (!listeners.isEmpty()) {
            for (CameraListener listener : listeners) {
                listener.onError("No compatible camera found");
            }
        }

        return false;
    }
}

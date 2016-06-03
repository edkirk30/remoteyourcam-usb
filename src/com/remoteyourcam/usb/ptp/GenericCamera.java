package com.remoteyourcam.usb.ptp;

import com.remoteyourcam.usb.ptp.commands.SimpleCommand;
import com.remoteyourcam.usb.ptp.model.LiveViewData;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by edkirk on 03/06/2016.
 */
public class GenericCamera extends PtpCamera {

    private Set<Integer> supportedOperations;

    public GenericCamera(PtpUsbConnection connection, CameraListener listener, WorkerListener workerListener) {
        super(connection, listener, workerListener);

        histogramSupported = false;
    }



    @Override
    public void getLiveViewPicture(LiveViewData reuse) {

        //FIXME cant support
        throw new UnsupportedOperationException();
    }


    @Override
    protected void onOperationCodesReceived(Set<Integer> operations) {
        supportedOperations = operations;
    }

    @Override
    public boolean isSettingPropertyPossible(int property) {
        return false;

        /*
        Integer mode = ptpProperties.get(PtpConstants.Property.ExposureProgramMode);
        Integer wb = ptpProperties.get(PtpConstants.Property.WhiteBalance);
        if (mode == null) {
            return false;
        }
        switch (property) {
            case Property.ShutterSpeed:
                return mode == 4 || mode == 1;
            case Property.ApertureValue:
                return mode == 3 || mode == 1;
            case Property.IsoSpeed: //TODO this should only be disabled for DIP when isoautosetting is on
            case Property.Whitebalance:
            case Property.ExposureMeteringMode:
            case Property.ExposureCompensation:
                return mode < 0x8010;
            case Property.FocusPoints:
                return true;
            case Property.ColorTemperature:
                return wb != null && wb == 0x8012;
            default:
                return true;
        }
        */
    }

    public List<FocusPoint> getFocusPoints() {
        //FIXME raise unsupported?
        throw new UnsupportedOperationException();

        //List<FocusPoint> points = new ArrayList<FocusPoint>();
        //return points;

    }

    @Override
    public void driveLens(int driveDirection, int pulses) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void focus() {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void queueEventCheck() {
        return;
    }

    @Override
    public void setLiveViewAfArea(float posx, float posy) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setLiveView(boolean enabled) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected boolean isBulbCurrentShutterSpeed() {
        throw new UnsupportedOperationException();
    }
}

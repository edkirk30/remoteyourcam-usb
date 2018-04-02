package com.remoteyourcam.usb.ptp.commands;

import com.remoteyourcam.usb.ptp.PtpAction;
import com.remoteyourcam.usb.ptp.PtpCamera;

/**
 * Created by edkirk on 20/06/2016.
 */
public class PtpActionClass implements PtpAction {

    /**
     * Received response code, should be handled in
     * {@link #exec(com.remoteyourcam.usb.ptp.PtpCamera.IO)}.
     */
    protected int responseCode;

    protected boolean hasResponseReceived;

    protected String lastUserErrorMessage = "";

    public boolean hasResponseReceived() {
        return hasResponseReceived;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public String getLastUserErrorMessage() {
        return lastUserErrorMessage;
    }

    @Override
    public void exec(PtpCamera.IO io) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void reset() {
        throw new UnsupportedOperationException();
    }
}

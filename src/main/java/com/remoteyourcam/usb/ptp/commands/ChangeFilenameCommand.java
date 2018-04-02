package com.remoteyourcam.usb.ptp.commands;

import com.remoteyourcam.usb.ptp.PtpCamera;

import java.nio.ByteBuffer;

/**
 * Created by edkirk on 17/06/2016.
 */
public class ChangeFilenameCommand extends Command {


    ChangeFilenameCommand(PtpCamera camera) {

        super(camera);

    }

    @Override
    public void exec(PtpCamera.IO io) {

    }

    @Override
    public void encodeCommand(ByteBuffer b) {

    }
}

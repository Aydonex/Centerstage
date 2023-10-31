package org.firstinspires.ftc.teamcode;

import android.graphics.Rect;

import org.firstinspires.ftc.robotcore.external.android.util.Size;
import org.firstinspires.ftc.robotcore.external.hardware.camera.CameraFrame;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.ftc9974.thorcore.robot.sensors.USBWebcamBase;
import org.ftc9974.thorcore.util.MathUtilities;
import org.ftc9974.thorcore.vision.NEONVision;
import org.ftc9974.thorcore.vision.NativeImageByteBuffer;

import java.io.IOException;

public class PropVision extends USBWebcamBase {

    //These are the different masks for the different areas the robot can be in.
    private static NativeImageByteBuffer zone1;
    private static NativeImageByteBuffer zone2;
    private static NativeImageByteBuffer zone3;

    //The long values for the high and low numbers of the YUV range
    private long highColor;
    private long lowColor;

    //The lock is something used to make sure the thread this code is being run is not interfered with other code trying to access it.
    private final Object lock = new Object();

    //Enum to specify colors
    public enum Color {
        RED,
        BLUE
    }
    private Zone zone;

    //Types of zones possible
    public enum Zone {
        LEFT,
        CENTER,
        RIGHT
    }

    @Override
    protected void onNewFrame(CameraFrame frame) {
        //Will process the image with the given masks to find which one has the greatest number of matches between the mask and processed image.
        long zone1Result = NEONVision.processYUY2WithMask(frame.getImageBuffer(), frame.getImageSize(), highColor, lowColor, zone1.getPointer());
        long zone2Result = NEONVision.processYUY2WithMask(frame.getImageBuffer(), frame.getImageSize(), highColor, lowColor, zone2.getPointer());
        long zone3Result = NEONVision.processYUY2WithMask(frame.getImageBuffer(), frame.getImageSize(), highColor, lowColor, zone3.getPointer());

        //Find the greatest matches of the three zones.
        long max = MathUtilities.max(zone1Result, zone2Result, zone3Result);

        synchronized (lock) {
            if (max == zone1Result) {
                zone = Zone.LEFT;
            } else if (max == zone2Result) {
                zone = Zone.CENTER;
            } else if (max == zone3Result) {
                zone = Zone.RIGHT;
            }
        }

    }

    //Goes into auto mode
    public PropVision(WebcamName webcamName, Color color) throws IOException {
        super(webcamName);

        //Converts the YUV values into longs to be worked with when processing the image data
        if (color == Color.BLUE) {
            highColor = NEONVision.yuvColorLong(0x75, 0xa8, 0x76);
            lowColor = NEONVision.yuvColorLong(0x3c, 0x18, 0x00);
        } else if (color == Color.RED) {
            highColor = NEONVision.yuvColorLong(0xaf, 0x74, 0xd2);
            lowColor = NEONVision.yuvColorLong(0x57, 0x69, 0xa2);
        }

        //Creates the frame the masks work within.
        Size frameSize = getFrameSize();
        zone1 = new NativeImageByteBuffer(frameSize.getWidth(), frameSize.getHeight());
        zone2 = new NativeImageByteBuffer(frameSize.getWidth(), frameSize.getHeight());
        zone3 = new NativeImageByteBuffer(frameSize.getWidth(), frameSize.getHeight());

        //Will create rectangles inside of the masks with values of 1 at every pixel in the rectangle.
        zone1.drawFilledRectangle(new Rect(0, 190, 181, 291), (byte) 1);
        zone2.drawFilledRectangle(new Rect(250, 190, 431, 291), (byte) 1);
        zone3.drawFilledRectangle(new Rect(500, 190, 681, 291), (byte) 1);
    }

    //Returns the zone found. Synchronization locks the thread the process works on so no other process can access or modify zone while returning the zone.
    public Zone getZone() {
        synchronized(lock) {
            return zone;
        }
    }
}

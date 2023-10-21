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
    private static NativeImageByteBuffer zone1;
    private static NativeImageByteBuffer zone2;
    private static NativeImageByteBuffer zone3;

    private long highColor;
    private long lowColor;

    private final Object lock = new Object();

    public enum Color {
        RED,
        BLUE
    }

    private Zone zone;

    public enum Zone {
        LEFT,
        CENTER,
        RIGHT
    }

    @Override
    protected void onNewFrame(CameraFrame frame) {
        long zone1Result = NEONVision.processYUY2WithMask(frame.getImageBuffer(), frame.getImageSize(), highColor, lowColor, zone1.getPointer());
        long zone2Result = NEONVision.processYUY2WithMask(frame.getImageBuffer(), frame.getImageSize(), highColor, lowColor, zone2.getPointer());
        long zone3Result = NEONVision.processYUY2WithMask(frame.getImageBuffer(), frame.getImageSize(), highColor, lowColor, zone3.getPointer());

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

        if (color == Color.BLUE) {
            highColor = NEONVision.yuvColorLong(0x75, 0xa8, 0x76);
            lowColor = NEONVision.yuvColorLong(0x3c, 0x18, 0x00);
        } else if (color == Color.RED) {
            highColor = NEONVision.yuvColorLong(0xaf, 0x74, 0xd2);
            lowColor = NEONVision.yuvColorLong(0x57, 0x69, 0xa2);
        }

        Size frameSize = getFrameSize();
        zone1 = new NativeImageByteBuffer(frameSize.getWidth(), frameSize.getHeight());
        zone2 = new NativeImageByteBuffer(frameSize.getWidth(), frameSize.getHeight());
        zone3 = new NativeImageByteBuffer(frameSize.getWidth(), frameSize.getHeight());

        zone1.drawFilledRectangle(new Rect(0, 0, 181, 481), (byte) 1);
        zone2.drawFilledRectangle(new Rect(250, 0, 431, 481), (byte) 1);
        zone3.drawFilledRectangle(new Rect(500, 0, 681, 481), (byte) 1);
    }

    public Zone getZone() {
        synchronized(lock) {
            return zone;
        }
    }
}

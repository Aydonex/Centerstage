/*
Created to test vision for detecting the red prop in auto
 */
package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import java.io.IOException;

@TeleOp(name="VisionTestOpMode Blue")
public class VisionTestOpModeBlue extends OpMode {
    PropVision vision;

    //When init is run, it will run process the data from Camera 1 with NeonVision to detect the placement of the prop
    @Override
    public void init() {

        //Create webcam object to refer to when accessing camera image buffer
        WebcamName webcam = hardwareMap.get(WebcamName.class,"Camera 1");

        try {
            //Creates a PropVision object to access vision data
            vision = new PropVision(webcam, PropVision.Color.BLUE);
        } catch (IOException e) {
            telemetry.log().add("NeonVision broke.");
        }

    }

    @Override
    public void init_loop() {
        //Update which zone PropVision detects
        telemetry.addData("Detected Zone: ",vision.getZone());
        telemetry.update();
    }

    @Override
    public void loop() {
        telemetry.addData("Detected Zone: ",vision.getZone());
        telemetry.update();
    }

    @Override
    public void stop() {
        vision.shutdown();
    }

}

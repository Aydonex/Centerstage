package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import java.io.IOException;

@TeleOp(name="VisionTestOpMode")
public class VisionTestOpMode extends OpMode {
    PropVision vision;

    @Override
    public void init() {

        WebcamName webcam = hardwareMap.get(WebcamName.class,"Camera 1");

        try {
            vision = new PropVision(webcam, PropVision.Color.BLUE);
        } catch (IOException e) {
            telemetry.log().add("NeonVision broke.");
        }

    }

    @Override
    public void init_loop() {
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

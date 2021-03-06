package org.firstinspires.ftc.teamcode;
/* Copyright (c) 2017 FIRST. All rights reserved.
 * Geeoon Chung was Here!
 */

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(name="Main TeleOp", group="TeleOp")

public class LegionOpMode extends LinearOpMode {
    final float fastSpeed = 1.0f;
    final float slowSpeed = 0.5f;
    final float evenSlower = 0.25f; //Multiplier to make the lifter even slow
    float speedMultiplier = 1.0f;
    float servoPow = 0.0f;
    float liftPow = 0.0f;
    float noPow;
    float servoPos = 0.75f;
    float gamepadTriggerTotal;
    float extendSpeed;
    private DcMotor motorLeft;
    private DcMotor motorRight;
    private DcMotor motorLift;
    private DcMotor motorExtend;
    private Servo servoGrab;
    private Servo servoGrab2;
    @Override
    public void runOpMode() {
        motorLeft = hardwareMap.dcMotor.get("motorLeft"); //left drive motor
        motorRight = hardwareMap.dcMotor.get("motorRight"); //right drive motor
        motorLift = hardwareMap.dcMotor.get("motorLift");
        motorExtend = hardwareMap.dcMotor.get("motorExtend");
        servoGrab = hardwareMap.servo.get("servoGrab");
        servoGrab2 = hardwareMap.servo.get("servoGrab2");

        motorLeft.setDirection(DcMotor.Direction.FORWARD);
        motorRight.setDirection(DcMotor.Direction.REVERSE);
        motorLift.setDirection(DcMotor.Direction.FORWARD);
        motorExtend.setDirection(DcMotor.Direction.FORWARD);
        servoGrab.setDirection(Servo.Direction.FORWARD);
        servoGrab2.setDirection(Servo.Direction.REVERSE);


        motorLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motorRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motorLift.setPower(0);
        motorLift.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        motorExtend.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        motorLift.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        motorExtend.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        waitForStart();
        servoGrab.setPosition(0.0f);
        servoGrab2.setPosition(0.0f);
        motorLift.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorLift.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        motorExtend.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorExtend.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        servoGrab.setPosition(0);
        servoGrab2.setPosition(0);
        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {
            gamepadTriggerTotal =  gamepad1.left_trigger - gamepad1.right_trigger;
            if (gamepad1.y) { //slow speed code
                speedMultiplier = slowSpeed;
            } else {
                speedMultiplier = fastSpeed;
            }

            if (gamepad1.dpad_up && motorExtend.getCurrentPosition() < 0) {
                extendSpeed = 0.25f * speedMultiplier;
            } else if (gamepad1.dpad_down && motorExtend.getCurrentPosition() > -580) {
                extendSpeed = -0.25f * speedMultiplier;
            } else {
                extendSpeed = 0.0f;
            }
            if (gamepad1.right_bumper && gamepad1.left_bumper) {
                servoPow = 0.0f;
            } else if (gamepad1.right_bumper) {
                if (servoPos <= 1.0f) {
                    servoPow = 0.01f * speedMultiplier;
                } else {
                    servoPos = 1.0f;
                }
            } else if (gamepad1.left_bumper) {
                if (servoPos >= 0.0f) {
                    servoPow = -0.01f * speedMultiplier;
                } else {
                    servoPos = 0.0f;
                }
            } else {
                servoPow = 0.0f;
            }

            if (gamepadTriggerTotal > 0 && motorLift.getCurrentPosition() <= 650) {
                liftPow = speedMultiplier * (gamepadTriggerTotal);
            } else if (gamepadTriggerTotal < 0 && motorLift.getCurrentPosition() >= 0) {
                liftPow = speedMultiplier * (gamepadTriggerTotal);
            } else {
                liftPow = 0;
            }

            if (liftPow == 0) {
                motorLift.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                motorLift.setTargetPosition((int)noPow);
            } else {
                motorLift.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                noPow = motorLift.getCurrentPosition();
            }


            servoPos = servoPos + servoPow;
            //Motor movement
            motorLift.setPower((liftPow));
            motorLeft.setPower(-gamepad1.left_stick_y * speedMultiplier);
            motorRight.setPower(-gamepad1.right_stick_y * speedMultiplier);
            motorExtend.setPower(extendSpeed);
            servoGrab.setPosition(servoPos);
            servoGrab2.setPosition(servoPos);
            //Telemetry
            //telemetry.addData("Right Position", motorExtend.getCurrentPosition());
            telemetry.addData("Lift Position", motorLift.getCurrentPosition());
            telemetry.update();
            idle();
        }
    }
}

package frc.robot;

public class SwerveDrive {
    /**
     * What does this class do? some weird math to take controller inputs and
     * convert them into rotation and speed values for each motor (which is
     * controlled via the WheelDrive class)
     * 
     * This code heavily attributed from Jacob Misirian of FIRST Robotics Team 2506
     * of Franklin, WI.
     */
    public final double L = 1; //
    public final double W = 1; // These are from the Length and Width between wheels.

    private WheelDrive backRight;
    private WheelDrive backLeft;
    private WheelDrive frontRight;
    private WheelDrive frontLeft;

    public SwerveDrive(WheelDrive backRight, WheelDrive backLeft, WheelDrive frontRight, WheelDrive frontLeft) {
        this.backRight = backRight;
        this.backLeft = backLeft;
        this.frontRight = frontRight;
        this.frontLeft = frontLeft;
    }

    public void drive(double x1, double y1, double x2, double rate) {// x1, y1 are from the position of the joystick, x2
                                                                     // is from the rotation

        double r = Math.sqrt((L * L) + (W * W));
        y1 *= -1;

        double a = x1 - x2 * (L / r);
        double b = x1 + x2 * (L / r);
        double c = y1 - x2 * (W / r);
        double d = y1 + x2 * (W / r);

        double backRightSpeed = Math.sqrt((a * a) + (d * d));
        double backLeftSpeed = Math.sqrt((a * a) + (c * c));
        double frontRightSpeed = Math.sqrt((b * b) + (d * d));
        double frontLeftSpeed = Math.sqrt((b * b) + (c * c));

        double backRightAngle = Math.atan2(a, d) / Math.PI;
        double backLeftAngle = Math.atan2(a, c) / Math.PI;
        double frontRightAngle = Math.atan2(b, d) / Math.PI;
        double frontLeftAngle = Math.atan2(b, c) / Math.PI;

        backRight.drive(backRightSpeed, backRightAngle, rate);
        backLeft.drive(backLeftSpeed, backLeftAngle, rate);
        frontRight.drive(frontRightSpeed, frontRightAngle, rate);
        frontLeft.drive(frontLeftSpeed, frontLeftAngle, rate);
    }
}
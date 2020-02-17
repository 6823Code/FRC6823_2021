package frc.robot;

import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import frc.robot.commands.FieldSpaceDrive;
import frc.robot.commands.RobotSpaceDrive;
import frc.robot.subsystems.SwerveDriveSubsystem;
import frc.robot.subsystems.ShooterSubsystem;;

public class RobotContainer {
    public SwerveDriveSubsystem swerveDriveSubsystem;
    public ShooterSubsystem shooterSubsystem;

    private FieldSpaceDrive fieldSpaceDriveCommand;
    private RobotSpaceDrive robotSpaceDriveCommand;

    private JoystickHandler joystickHandler;
    private NavXHandler navX;
    public LimeLightHandler limeLight;

    public RobotContainer() {
        swerveDriveSubsystem = new SwerveDriveSubsystem();
        shooterSubsystem = new ShooterSubsystem();

        joystickHandler = new JoystickHandler(); // joystick input
        limeLight = new LimeLightHandler(0, shooterSubsystem); // limelight input
        navX = new NavXHandler(); // navx input

        // field space also uses navx to get its angle
        fieldSpaceDriveCommand = new FieldSpaceDrive(swerveDriveSubsystem, joystickHandler, navX);
        robotSpaceDriveCommand = new RobotSpaceDrive(swerveDriveSubsystem, joystickHandler);

        CommandScheduler.getInstance().setDefaultCommand(swerveDriveSubsystem, robotSpaceDriveCommand);

        configureButtonBindings();
    }

    private void configureButtonBindings() {
        // press button 12 to set the swerve just forward, this is for calibration
        // purposes
        // joystickHandler.button(12).whileHeld(() -> swerveDriveSubsystem.drive(0.2, 0,
        // 0), swerveDriveSubsystem);

        // this will set the current orientation to be "forward" for field drive
        // joystickHandler.button(8).whenPressed(() -> fieldSpaceDriveCommand.zero());

        // holding 10 will enable field space drive, instead of robot space
        // joystickHandler.button(10).whileHeld(fieldSpaceDriveCommand);

        /**
         * joystickHandler.button(3).whileHeld(() -> { double[] autoAim =
         * limeLight.goToPolar(Robot.PREFS.getDouble("PolarDistance", 50),
         * Robot.PREFS.getDouble("PolarTheta", 0));
         * swerveDriveSubsystem.drive(autoAim[2] * .65, autoAim[1] * .1, autoAim[0] *
         * .3); }, swerveDriveSubsystem);
         * 
         * joystickHandler.button(4).whileHeld(() -> { double[] autoAim =
         * limeLight.goTo(Robot.PREFS.getDouble("CY", 50), Robot.PREFS.getDouble("CX",
         * 0)); swerveDriveSubsystem.drive(autoAim[2] * .65, autoAim[1] * .1, autoAim[0]
         * * .3); }, swerveDriveSubsystem);
         * 
         * joystickHandler.button(6).whileHeld(() -> { double[] autoAim =
         * limeLight.aimSteerAndStrafe(); swerveDriveSubsystem.drive(autoAim[2] * .4,
         * autoAim[1] * .15, autoAim[0] * .1); }, swerveDriveSubsystem);
         **/

        // this will set the current orientation to be "forward" for field drive
        joystickHandler.button(14).whenPressed(() -> fieldSpaceDriveCommand.zero());

        // holding 10 will enable field space drive, instead of robot space
        joystickHandler.button(7).whileHeld(fieldSpaceDriveCommand);

        joystickHandler.button(15).whileActiveContinuous(shooterSubsystem::shooterPID, shooterSubsystem)
                .whenInactive(shooterSubsystem::stopShooterSpin);

        joystickHandler.button(5).whenPressed(limeLight::aimReset);
        joystickHandler.button(5).whileHeld(() -> {
            double[] autoAim = limeLight.programmedDistances(joystickHandler.getRawAxis6());
            swerveDriveSubsystem.drive(autoAim[2] * .15, autoAim[1] * .1, autoAim[0] * .25);
        }, swerveDriveSubsystem);

        joystickHandler.button(11).whenPressed(shooterSubsystem::startConveyorSpin)
                .whenReleased(shooterSubsystem::stopConveyorSpin);

        joystickHandler.button(12).whenPressed(shooterSubsystem::startReverseConveyor)
                .whenReleased(shooterSubsystem::stopConveyorSpin);

        // joystickHandler.button(15).whenPressed(shooterSubsystem::startShooterSpin)
        // .whenReleased(shooterSubsystem::stopShooterSpin);

        joystickHandler.button(1).whenPressed(shooterSubsystem::startIntakeSpin)
                .whenReleased(shooterSubsystem::stopIntakeSpin);
        joystickHandler.button(15).whenPressed(shooterSubsystem::startTimer).whenReleased(shooterSubsystem::stopTimer);
        joystickHandler.button(4).whenPressed(limeLight::pipeLineSwitch);

        joystickHandler.button(9).whenPressed(shooterSubsystem::startIntakeSpin)
                .whenReleased(shooterSubsystem::stopIntakeSpin);
        joystickHandler.button(10).whenPressed(shooterSubsystem::startReverseIntake)
                .whenReleased(shooterSubsystem::stopIntakeSpin);
        joystickHandler.button(16).whenPressed(shooterSubsystem::coolShooter)
                .whenReleased(shooterSubsystem::stopShooterSpin);
        /**
         * joystickHandler.button(2).whileHeld(() -> {
         * Preferences.getInstance().putDouble("ConveyorShootSpeed", 15000);
         * shooterSubsystem.shooterPID();
         * 
         * 
         * }, shooterSubsystem);
         * joystickHandler.button(2).whenReleased(shooterSubsystem::stopShooterSpin);
         **/
    }
}

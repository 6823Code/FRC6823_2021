package frc.robot.commands;

import edu.wpi.first.wpilibj.controller.PIDController;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.LimeLightSubsystem;
import frc.robot.subsystems.SwerveDriveSubsystem;

public class MoveTo3d extends CommandBase {

    private SwerveDriveSubsystem swerveDriveSubsystem;
    private LimeLightSubsystem limeLightSubsystem;
    private PIDController strafeController, distController, aimController;
    private double x, z;

    public MoveTo3d(SwerveDriveSubsystem swerveDriveSubsystem, LimeLightSubsystem limeLightSubsystem, double x,
            double z) {
        this.limeLightSubsystem = limeLightSubsystem;
        this.swerveDriveSubsystem = swerveDriveSubsystem;
        this.x = x;
        this.z = z;

        addRequirements(limeLightSubsystem, swerveDriveSubsystem);
    }

    @Override
    public void execute() {
        limeLightSubsystem.setPipeline(0);
        double strafeCommand = strafeController.calculate(limeLightSubsystem.getX());
        double distanceCommand = distController.calculate(limeLightSubsystem.getZ());
        double aimCommand = aimController.calculate(limeLightSubsystem.getTx());
        if (limeLightSubsystem.hasTarget())
            swerveDriveSubsystem.drive(distanceCommand * -1, strafeCommand, aimCommand * -1);
    }

    @Override
    public void initialize() {
        limeLightSubsystem.setPipeline(0);
        limeLightSubsystem.setServoAngle(65);

        strafeController = new PIDController(.01, 0, 0);
        distController = new PIDController(.015, 0, 0);
        aimController = new PIDController(.016, 0, 0);

        strafeController.setSetpoint(x);
        distController.setSetpoint(z);
        aimController.setSetpoint(0);

    }

    @Override
    public boolean isFinished() {
        if (Math.abs(strafeController.getPositionError()) < 5 && Math.abs(distController.getPositionError()) < 3
                && Math.abs(aimController.getPositionError()) < 2) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void end(boolean interrupted) {
        swerveDriveSubsystem.drive(0, 0, 0);
    }
}
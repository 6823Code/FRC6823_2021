/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/
//hey look it's some code! Incredible
package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandScheduler;

public class Robot extends TimedRobot {
    public static Preferences PREFS = Preferences.getInstance();
    public static RGB rgb;
    private RobotContainer robotContainer;

    @Override
    public void robotInit() {
        robotContainer = new RobotContainer();

        PREFS = Preferences.getInstance();

        rgb = new RGB(9);

        // PREFS.putBoolean("DEBUG_MODE", false);
        SmartDashboard.putBoolean("LemonPipeline", false);

    }

    @Override
    public void robotPeriodic() {
        CommandScheduler.getInstance().run();
    }

    @Override
    public void autonomousInit() {
        robotContainer.getAutoCommandGroup().schedule();
    }

    @Override
    public void teleopInit() {
        robotContainer.getAutoCommandGroup().cancel();
    }

}

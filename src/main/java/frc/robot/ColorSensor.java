/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/
package frc.robot;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.controller.PIDController;
import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj.PWMVictorSPX;
import com.revrobotics.ColorSensorV3;

public class ColorSensor {
  // Spinner motor control
  private PWMVictorSPX spinner;
  private boolean moving;
  // Color sensor
  private double error; // Tolerance for colors
  private String[] colors;
  private int colorSelection; // Driver's selected color
  private double directionOfSpin;
  private I2C.Port i2cPort;
  private ColorSensorV3 colorSensor;
  private double red;
  private double green;
  private double blue;
  // PID controller related shit
  private PIDController pidcontroller;

  // in case there is another use for the i2cport in the robot code
  public ColorSensor(I2C.Port i2cPort) {
    this.i2cPort = i2cPort;
    colorSensor = new ColorSensorV3(i2cPort);
    moving = false;
    error = 0.09;
    colors = new String[] { "blue", "green", "red", "yellow" }; // it's in an array so that you can easily cycle through
                                                                // it
    colorSelection = 0;// this
    directionOfSpin = 0;
    pidcontroller = new PIDController(0.5, 0, 0); // changed from 0.2, 0, 0 to overshoot
    spinner = new PWMVictorSPX(5); // this is the motor for the spinner
    pidcontroller.enableContinuousInput(0, 1); // So that the code knows it's a wheel we are dealing with
  }

  // assumes i2cport = i2c.port.konboard
  public ColorSensor() {
    i2cPort = I2C.Port.kOnboard;
    colorSensor = new ColorSensorV3(i2cPort);
    moving = false;
    error = 0.09;
    colors = new String[] { "blue", "green", "red", "yellow" }; // it's in an array so that you can easily cycle through
                                                                // it
    colorSelection = 0;// this
    directionOfSpin = 0;
    pidcontroller = new PIDController(0.5, 0, 0); // changed from 0.2, 0, 0 to overshoot
    this.spinner = spinner; // this is the motor for the spinner
    pidcontroller.enableContinuousInput(0, 1); // So that the code knows it's a wheel we are dealing with
  }

  // this will be run in a loop to (make the field sensor) select the color
  // selected by the driver
  public void deploySpinner(Joystick driveStick) {
    Color detectedColor = colorSensor.getColor();
    red = detectedColor.red;
    green = detectedColor.green;
    blue = detectedColor.blue;
    // cycles through the colors when you press button 10
    if (driveStick.getRawButtonPressed(10) && !moving) {
      nextColor();
    }
    // it starts when you push button 11 and it stops the motor once it has reached
    // the right color and stopped moving
    if (driveStick.getRawButtonPressed(11) || moving) {
      activateSpinner();
    }
    showOnDashBoard(detectedColor);

  }

  // returns true if the rgb of two colors is within the error value
  public boolean closeEnough(String color) {
    if (color.equals("red")) {
      return Math.abs(red - 0.507568) <= error && Math.abs(green - 0.355225) <= error
          && Math.abs(blue - 0.136963) <= error;
    } else if (color.equals("green")) {
      return Math.abs(red - 0.163574) <= error && Math.abs(green - 0.584473) <= error
          && Math.abs(blue - 0.251953) <= error;
    } else if (color.equals("blue")) {
      return Math.abs(red - 0.118164) <= error && Math.abs(green - 0.426758) <= error
          && Math.abs(blue - 0.455078) <= error;
    } else { // assume its yellow
      return Math.abs(red - 0.312256) <= error && Math.abs(green - 0.566162) <= error
          && Math.abs(blue - 0.121338) <= error;
    }
  }

  // show the color selected by the driver, color currently selected by the field
  // sensor, color detected by robot sensor, red value, green value, blue value,
  // whether or not the motor should turn
  public void showOnDashBoard(Color detectedColor) {
    SmartDashboard.putString("Looking for", colors[colorSelection]);
    SmartDashboard.putString("Color I see", colorSeen());
    SmartDashboard.putString("Color selected", colorSelected());
    SmartDashboard.putNumber("Red", detectedColor.red);
    SmartDashboard.putNumber("Green", detectedColor.green);
    SmartDashboard.putNumber("Blue", detectedColor.blue);
    SmartDashboard.putNumber("Distance motor spins", directionOfSpin);
  }

  // returns the color the color sensor see
  public String colorSeen() {
    for (int i = 0; i < colors.length; i++) {
      if (closeEnough(colors[i])) {
        return colors[i];
      }
    }
    return "unknown";
  }

  // cycles through the colors for driver color selection
  public void nextColor() {
    if (colorSelection == colors.length - 1) {
      colorSelection = 0;
    } else {
      colorSelection++;
    }
  }

  // returns color selected by the field sensor
  public String colorSelected() {
    if (colorSeen().equals("red")) {
      return "blue";
    } else if (colorSeen().equals("blue")) {
      return "red";
    } else if (colorSeen().equals("green")) {
      return "yellow";
    } else if (colorSeen().equals("unknown")) {
      return "unknown";
    } else {
      return "green";
    }
  }

  // convert color (String) to number for the pid controller
  public double convertToNumber(String color) {
    if (color.equals("yellow")) {
      return 0;
    } else if (color.equals("blue")) {
      return 0.25;
    } else if (color.equals("green")) {
      return 0.5;
    } else {
      return 0.75;
    }
  }

  // calculate the direction and speed (not used) for the spinning, takes the
  // shortest route
  public double NextSpin() {
    double setpoint = convertToNumber(colors[colorSelection]);
    pidcontroller.setSetpoint(setpoint);
    return pidcontroller.calculate(convertToNumber(colorSelected()), setpoint);
  }

  // turn the spinner according to the direction of next spin, if the color sensor
  // doesn't recognize the color it keeps the motor spinning
  public void activateSpinner() {
    directionOfSpin = NextSpin();
    if (!colorSelected().equals("unknown"))// if the color is unknown it won't set a direction and keep doing what its
                                           // doing
      spinner.set(directionOfSpin * 2);
  }moving=directionOfSpin!=0;
}}
package org.usfirst.frc.team4765.robot;

import edu.wpi.first.wpilibj.CANTalon.ControlMode;
import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.can.*;


/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */

public class Robot extends IterativeRobot 
{
	RobotDrive myRobot;
	Joystick stick;
	int autoLoopCounter;
	public static Joystick driver = new Joystick(0);
	public static CANTalon motor1 = new CANTalon(1); 
	public static CANTalon motor2 = new CANTalon(2);
	public static CANTalon motor3 = new CANTalon(3);
	
	public final static double JoyKneeOne = 0.1;        // end of the deadzone & first knee of joystick range which starts 'maneuvering range'
    public final static double JoyKneeTwo = 0.8;         // second knee of joystick range which ends 'maneuvering range' and starts 'speed range'
    public final static double JoyMaxRange = 1.0;        // maximum input range of joysticks
    
    Preferences prefs = Preferences.getInstance();

    double P;
    double I;
    double D;
    double MAX_RPM;
    double StartPosition;

    double[] motorSpeed = new double[4]; //holds motor speeds (in rpm)
    
    int CANTimeouts;
    
    public void CANTimeout()
    {
        CANTimeouts++;
        SmartDashboard.putNumber("CANTimeouts", CANTimeouts);
    }
	
    /**
     * This function is run when the robot is first started up and should be
     * used for any initialization code.
     */
    public void robotInit() 
    {
        System.out.println("robotInit()");
    	myRobot = new RobotDrive(0,1);
    	stick = new Joystick(0);
    }    
    
    /**
     * This function is called once before autonomous control
     */
    public void disabledInit()
    {
        System.out.println("### DISABLED ###");
    }

    
    /*
     * Smartdashboard controls
     */
    public void updatePrefs()
    {
        MAX_RPM = prefs.getDouble("M", 0.0);
        P = prefs.getDouble("P", 0.0);   //can change values from here, press button to activate changes
        I = prefs.getDouble("I", 0.0);
        D = prefs.getDouble("D", 0.0);
        SmartDashboard.putNumber("Talon P", P);  //displays PID values on SmartDash
        SmartDashboard.putNumber("Talon I", I);
        SmartDashboard.putNumber("Talon D", D);
        SmartDashboard.putNumber("MAX_RPM", MAX_RPM);
        try
        {
            motor1.setPID(P, I, D);  //sets PID constants for Jag PID loop
            motor2.setPID(P, I, D);
            motor3.setPID(P, I, D);
            motor1.enableControl(); //starts feedback ctrl
            motor2.enableControl();
            motor3.enableControl();
        } 
        catch (CANInvalidBufferException ex)
        {
            CANTimeout();
        }
        System.out.println("finished prefs");
    }

    public void robotInitDummy()
    {
        SmartDashboard.putNumber("CAN timeouts", CANTimeouts);
        boolean CANInit = false;
        CANTimeouts = 0;
        while (CANInit == false)
        {
            try
            {
				//                m_telePeriodicLoops = 0;                              // Reset the number of loops in current second
//                m_dsPacketsReceivedInCurrentSecond = 0;                 // Reset the number of dsPackets in current second
            	motor1.changeControlMode(CANTalon.ControlMode.Speed);
            	motor2.changeControlMode(CANTalon.ControlMode.Speed);
            	motor3.changeControlMode(CANTalon.ControlMode.Speed);
            	
            	motor1.setFeedbackDevice(CANTalon.FeedbackDevice.QuadEncoder);
            	motor2.setFeedbackDevice(CANTalon.FeedbackDevice.QuadEncoder);
            	motor3.setFeedbackDevice(CANTalon.FeedbackDevice.QuadEncoder);
            	
            	motor1.setVoltageRampRate(15); // TODO: put the correct voltage
            	motor2.setVoltageRampRate(15); // TODO: put the correct voltage
            	motor3.setVoltageRampRate(15); // TODO: put the correct voltage
            	
            	//motor1. // configure degrees per second
            	
            	
                
                //motor1.setSpeedReference(CANJaguar.SpeedReference.kQuadEncoder);    //chooses which kind of encoder to determine speed feedback
                //motor2.setSpeedReference(CANJaguar.SpeedReference.kQuadEncoder);
                //motor3.setSpeedReference(CANJaguar.SpeedReference.kQuadEncoder);
                //motor1.setVoltageRampRate(15); 
                //motor2.setVoltageRampRate(15);
                //motor3.setVoltageRampRate(15);
                //motor1.configEncoderCodesPerRev(256);   //counts pulses per revolution
                //motor2.configEncoderCodesPerRev(256);
                //motor3.configEncoderCodesPerRev(256);
                //StartPosition = motor1.getPosition();
            	            	
                updatePrefs(); // TODO: set P, I, D, F
                                
                motor1.set(0);
                motor2.set(0);
                motor3.set(0);

                motor1.enableControl(); //starts feedback ctrl
                motor2.enableControl();
                motor3.enableControl();
                
                CANInit = true;
            } 
            catch (CANInvalidBufferException ex)
            {
                CANTimeout();
            }
        }
        System.out.println("###  S A D B O Y S HAVE ARRIVED  ###");
    }
    
    /**
     * This function is run once each time the robot enters autonomous mode
     */
    public void autonomousInit() 
    {
    	autoLoopCounter = 0;
    }
   
    /**
     * This function is called periodically during autonomous
     */
    public void autonomousPeriodic() 
    {
    	if(autoLoopCounter < 100) //Check if we've completed 100 loops (approximately 2 seconds)
		{
			myRobot.drive(-0.5, 0.0); 	// drive forwards half speed
			autoLoopCounter++;
			} else {
			myRobot.drive(0.0, 0.0); 	// stop robot
		}
    }
    
    /**
     * This function is called once each time the robot enters tele-operated mode
     */
    public void teleopInit()
    {
    	
    }
        
    /**
     * This takes the raw input values from the joystick and maps them into more convenient speeds.
     * There is a deadzone of the first 10% of the joystick range.
     * From 10% to 80% of the range, the slope of the line is not 1, it is less.
     * From 80% to 100% of the range the slope is high for driving quickly.
     * These values are subject to change.
     * 
     * TODO: make separate method for R in case we need different knees.
     */    
    public double mapValue(double V)
    {
    	if (Math.abs(V) < JoyKneeOne) // deadzones
        {
            V = 0.0;
        }

        if ((Math.abs(V) >= JoyKneeOne) && (Math.abs(V) <= JoyKneeTwo)) // mapping for maneuvering range
        {
            if (V < 0.0)
            {
                V = (3.0 / 7.5) * V - 0.03;     // changes raw negative input into a maneuverable speed
            } 
            else
            {
                V = (3.0 / 7.5) * V + 0.03;     // changes raw positive input into a maneuverable speed
            }
        } 
        else
        {
            if((Math.abs(V) > JoyKneeTwo) && (Math.abs(V) <= JoyMaxRange)) // mapping for speed range
            {
                if(V < 0)
                {
                    V = (6.5 / 2.0) * V + 2.25;  // changes raw negative input into a fast speed
                } 
                else
                {
                    V = (6.5 / 2.0) * V - 2.25;  // changes raw positive input into a fast speed
                }
            }
        }        
        return V;
    }
    
    /**
     * This function is called periodically during operator control
     */
    public void teleopPeriodic() 
    {    	
    	double Y = driver.getY();
    	double X = driver.getX();
    	double R = driver.getZ();    	

        Y = mapValue(Y);
        X = mapValue(X);
        R = mapValue(R);
    	
    	double motor1speed = X + Y + -0.5 * R;
    	double motor2speed = -1.0 * X + Y + 0.5 * R;
    	double motor3speed = 0.5 * X + R;
    	
    	double biggestValue = Math.max(motor1speed, Math.max(motor2speed, motor3speed));
    	
    	if(biggestValue > 1.0)
    	{
    		motor1speed /= biggestValue;
    		motor2speed /= biggestValue;
    		motor3speed /= biggestValue;
    	}
    		
    	motor1.set(-1.0 * motor1speed);
    	motor2.set(motor2speed);
    	motor3.set(motor3speed);
        		
        //myRobot.arcadeDrive(stick);
    }
    
    /**
     * This function is called periodically during test mode
     */
    public void testPeriodic() 
    {
    	LiveWindow.run();
    }
    
}

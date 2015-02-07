package org.usfirst.frc.team4765.robot;

import edu.wpi.first.wpilibj.CANTalon.ControlMode;
import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.Timer;
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

public class Robot extends IterativeRobot // check the error, this happened after our late night drawing trouble shooting
{
	RobotDrive myRobot;
	Joystick stick;
	int autoLoopCounter;
	
	public static Joystick driver   = new Joystick(0); // joystick that controls the driving
	//public static Joystick operator = new Joystick(6); // joystick that controls the chain movement
	
	public static CANTalon motor1 = new CANTalon(1); 
	public static CANTalon motor2 = new CANTalon(2);
	public static CANTalon motor3 = new CANTalon(3); // motors for driving
	
	// tower drivers for chain are regular Talons
	 
	public static Talon tower1 = new Talon(4);
	public static Talon tower2 = new Talon(11); // motors for the chain
	
	DigitalInput halifax1 = new DigitalInput(8);
	DigitalInput halifax2 = new DigitalInput(9);
	
	public final static double DeadZone     = 0.05;
	public final static double JoyKneeOneX_ = 0.1;        // end of the deadzone & first knee of joystick range which starts 'maneuvering range'
    public final static double JoyKneeTwoX_ = 0.8;        // second knee of joystick range which ends 'maneuvering range' and starts 'speed range'
    public final static double JoyMaxRange_ = 1.0;        // maximum input range of joysticks
    public final static double JoyKneeOneY_ = 0;		  // starts the first leg of the mapping
    public final static double JoyKneeTwoY_ = 0.35;		  
    
    Preferences prefs = Preferences.getInstance();

    double P;
    double I;
    double D;
    double F;
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
        F = prefs.getDouble("F", 0.0);
        
        SmartDashboard.putNumber("Talon P", P);  //displays PID values on SmartDash
        SmartDashboard.putNumber("Talon I", I);
        SmartDashboard.putNumber("Talon D", D);
        SmartDashboard.putNumber("Talon F", F);
        
        SmartDashboard.putNumber("MAX_RPM", MAX_RPM);
        try
        {
            motor1.setPID(P, I, D);  //sets PID constants 
            motor1.setF(F);			 // TODO: look into izone, closeLoopRampRate, profile
            motor2.setPID(P, I, D);
            motor2.setF(F);
            motor3.setPID(P, I, D);
            motor3.setF(F);
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
				// m_telePeriodicLoops = 0;                 // Reset the number of loops in current second
            	// m_dsPacketsReceivedInCurrentSecond = 0;  // Reset the number of dsPackets in current second
            	
            	motor1 = new CANTalon(1);
            	motor2 = new CANTalon(2);
            	motor3 = new CANTalon(3);
            	
            	motor1.changeControlMode(CANTalon.ControlMode.Speed);
            	motor2.changeControlMode(CANTalon.ControlMode.Speed);
            	motor3.changeControlMode(CANTalon.ControlMode.Speed);
            	
            	motor1.setFeedbackDevice(CANTalon.FeedbackDevice.QuadEncoder);
            	motor2.setFeedbackDevice(CANTalon.FeedbackDevice.QuadEncoder);
            	motor3.setFeedbackDevice(CANTalon.FeedbackDevice.QuadEncoder);
            	
            	motor1.setVoltageRampRate(15); // TODO: put the correct voltage
            	motor2.setVoltageRampRate(15); // TODO: put the correct voltage
            	motor3.setVoltageRampRate(15); // TODO: put the correct voltage
            	
            	//motor1. // TODO: configure degrees per second
            	
            	
                
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
        System.out.println("### GOT EM' COACH  ###");
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
     * The pre-set values can be changed where constants are initialized.
     */    
    public double mapDrivingValue(double V)
    {
    	double m1 = (JoyKneeTwoY_)/(JoyKneeTwoX_ - DeadZone);
    	double m2 = (JoyMaxRange_ - JoyKneeTwoY_)/(JoyMaxRange_ - JoyKneeTwoX_);
    	
    	if(Math.abs(V) < JoyKneeOneX_) // deadzone
    	{
    		V = 0.0;
    	}
    	else
    	{
	    	if ((Math.abs(V) >= JoyKneeOneX_) && (Math.abs(V) <= JoyKneeTwoX_)) // mapping for maneuvering range
	    	{
	    		if( V < 0.0)
	    		{
	    			V = m1 * (V - JoyKneeTwoX_) - JoyKneeTwoY_;	// maneuverable speed
	    		}
	    		else
	    		{
	    			V = m1 * (V - JoyKneeTwoX_) + JoyKneeTwoY_;	// maneuverable speed
	    		}
	    	}
	    	else
	    	{
	    		if((Math.abs(V) > JoyKneeTwoX_) && (Math.abs(V) <= JoyMaxRange_)) // mapping for speed range
	            {
	                if(V < 0.0)
	                {
	                    V = m2 * (V - JoyMaxRange_) - JoyMaxRange_;  // fast speed
	                } 
	                else
	                {
	                	V = m2 * (V - JoyMaxRange_) + JoyMaxRange_;  // fast speed
	                }
	            }
	    	}
    	}
    	return V;
    }
    
    /**
     * This function is called periodically during operator control
     * TODO: implement motor4, motor5 and the mapping for them
     */
    public void teleopPeriodic() 
    {    	
    	double Y = driver.getY();
    	double X = driver.getX();
    	double R = driver.getZ(); 
    	
    	//double A = operator.getY();

        Y = mapDrivingValue(Y);
        X = mapDrivingValue(X);		// changes the values for easier driving
        R = mapDrivingValue(R);
    	
    	double motor1speed = X + Y + -0.5 * R; 
    	double motor2speed = -1.0 * X + Y + 0.5 * R;
    	double motor3speed = 0.5 * X + R;
    	//double motor4speed = 0.5 * A;
    	
    	double biggestValue = Math.max(motor1speed, Math.max(motor2speed, motor3speed));
    	
    	if(biggestValue > 1.0)
    	{
    		motor1speed /= biggestValue;
    		motor2speed /= biggestValue;
    		motor3speed /= biggestValue;
    	}
    		
    	motor1.set(motor1speed * -1.0);
    	motor2.set(motor2speed);
    	motor3.set(motor3speed);
    	
    	//motor4.set(motor4speed);
    	//motor5.set(motor4speed);
        		
        //myRobot.arcadeDrive(stick);
    }
    
    /**
     * This function is called periodically during test mode
     */
    public void testPeriodic() 
    {
    	LiveWindow.run();
    	printSensorValues();
    	Timer.delay(0.5);
    	
    }
    /**
     * Prints halifax values
     */
    public void printSensorValues()
    {
    	System.out.println(halifax1.get());
    	System.out.println(halifax2.get());
    }
}

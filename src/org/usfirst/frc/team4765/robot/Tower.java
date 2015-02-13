package org.usfirst.frc.team4765.robot;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.Timer;

/**
 * @author Pavel Khokhlov
 * @author Dean Reece
 * 
 * @version 12 February 2015
 */

public class Tower 
{
	/*
	public enum ElevationState
	{
		FLOOR, PLATFORM
	}
	*/
	public enum State
	{
		RUNUP, RUNDOWN, STOPPED, RUNUPDELAY
	}
	
	/**
	 * ALL OF OUR COMMANDS
	 * 
	 * 	goUp - raise totoes to next magnetic edge
	 * 	TODO: goUpLevel - raise totes a full 42 link (full cycle)
	 * 	goDown - lowers totoes to next magnetic edge
	 * 	TODO: goDownLevel - lower totes a full 42 link (full cycle)
	 * 
	 * 	TODO: implement dashboard feedback
	 * 	
	 * 
	 */
	public final double speedUp_ = 0.4;
	public final double speedDown_ = - 0.35;
	
	//public ElevationState elevationState_ = ElevationState.FLOOR;	// going to be used for dashboard feedback
	public State state_ = State.STOPPED;
	
	public Talon motor_;
	public DigitalInput hallEffect_;
	public DigitalInput heightLimit_;
	
	public boolean lastHallEffect = false;
	public boolean elevationState_ = true; // true = floor, false = platform - for going up
	public boolean elevationTarget_ = true;
	
	Timer timer_ = new Timer();
	
	public Tower(Talon talon, DigitalInput hallEffect, DigitalInput heightLimit)
	{
		motor_ = talon;
		hallEffect_ = hallEffect;
		heightLimit_ = heightLimit;
	}
	
	
	public void goUp(boolean elevationTarget)
	{
		elevationTarget_ = elevationTarget;
		enterState(State.RUNUP);
	}
	
	
	public void goDown(boolean elevationTarget)
	{
		elevationTarget_ = elevationTarget;
		enterState(State.RUNDOWN);
	}
	
	public void stop()
	{
		enterState(State.STOPPED);
	}
	
	public void goUpLevel()
	{
		
	}
	
	public boolean getElevaitonState()
	{
		return elevationState_;
	}
	
	public State getState()
	{
		return state_;
	}
	
	
	/**
	 * all logic for state machine is here
	 */
	
	void enterState(State newState)
	{
		state_= newState;
		switch(newState)
		{
			case RUNUP:
			{
				motor_.set(speedUp_);
			}
			break;
				
			case RUNDOWN:
			{
				motor_.set(speedDown_);
			}
			break;
			
			case RUNUPDELAY:
			{
				timer_.reset();
				timer_.start();
				motor_.set(speedUp_);
			}
			break;
			
			default:
			case STOPPED:
			{
				motor_.set(0);
			}
			break;
		}
	}
	
	public void periodic()
	{
		boolean hallEffect = !hallEffect_.get();		// true = magnet is detected
		boolean heightLimit = !heightLimit_.get();		// true = height is reached
		
		switch(state_)
		{
			case RUNUP:
			{
				if(heightLimit == true)	// false means we have reached the limit
				{
					enterState(State.STOPPED);
				}
				else if(hallEffect == elevationTarget_)
				{
					elevationState_ = hallEffect;
					enterState(State.STOPPED);
				}
				else
				{
					motor_.set(speedUp_);
				}
			}
			break;
				
			case RUNDOWN:
			{
				if(hallEffect != elevationTarget_)
				{
					elevationState_ = !hallEffect;
					enterState(State.STOPPED);
				}
			}
			break;
			
			case RUNUPDELAY:
			{
				if(timer_.get() > 0.1)
					enterState(State.STOPPED);
			}
			break;
			
			default:
			case STOPPED:
			{
				motor_.set(0);
			}
			break;
		}
		
		lastHallEffect = hallEffect;
	}
	
	
	
	
	
	
	
	
	
	
	
	
}

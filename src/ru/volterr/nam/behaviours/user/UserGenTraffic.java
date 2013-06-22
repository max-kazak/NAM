package ru.volterr.nam.behaviours.user;

import java.io.IOException;

import ru.volterr.nam.Constants;
import ru.volterr.nam.agents.UserAgent;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.util.Logger;

public abstract class UserGenTraffic extends SimpleBehaviour {

	private boolean done =  false;
	private long stoptime,starttime,currenttime;	//time in experiment timeline
	private long 	delay = 100,					//interval between checks
					dt = delay,						//block interval
	 				wakeuptime=0; 					//nextmessage send check
	public int hour=9;
	
	public long getCurrenttime() {
		return currenttime;
	} 
	
	protected UserAgent myUser;
	
	/**
	 * @param time - time of modeling experiment
	 */
	public UserGenTraffic(UserAgent a,Long time){
		myUser = a;
		starttime = System.currentTimeMillis();
		stoptime = starttime + time;
	}
	
	@Override
	public void action() {
		
		if((currenttime = System.currentTimeMillis())<=stoptime){
			if((dt=wakeuptime-currenttime)<0)
			{		
				hour++;
				if(hour==24)
					hour=0;
				generate();
				
				wakeuptime = System.currentTimeMillis()+delay;
				dt=delay;

			}
			
			block(dt);
		}else{
			done = true;
		}
		
	}

	/**
	 * checks if message should be send and for sends if necessary
	 * 
	 */
	abstract void generate();

	@Override
	public boolean done() {
		return done;
	}

}

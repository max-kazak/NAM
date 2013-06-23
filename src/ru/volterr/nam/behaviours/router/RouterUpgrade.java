package ru.volterr.nam.behaviours.router;

import java.util.Iterator;
import java.util.Map.Entry;

import ru.volterr.nam.agents.RouterAgent;
import ru.volterr.nam.agents.ServerAgent;
import ru.volterr.nam.model.Link;
import jade.core.AID;
import jade.core.behaviours.SimpleBehaviour;
import jade.util.Logger;

public class RouterUpgrade extends SimpleBehaviour {

private boolean done = false;
	
	private long stoptime,starttime,currenttime;	//time in experiment timeline
	private long 	delay = 2400,					//interval between checks
					dt = delay,						//block interval
	 				wakeuptime; 					//nextmessage send check
	
	private Logger log;
	protected RouterAgent myRouter;
	
	public RouterUpgrade(RouterAgent a,Long time){
		myRouter = a;
		starttime = System.currentTimeMillis();
		stoptime = starttime + time;
		wakeuptime = starttime + delay;
		log = Logger.getMyLogger(this.getClass().getName());
	}
	
	@Override
	public void action() {
		if((currenttime = System.currentTimeMillis())<=stoptime){
			if((dt=wakeuptime-currenttime)<0)
			{		
				check();
				log.log(Logger.INFO, myRouter.getLocalName()+"#checking performance");
				wakeuptime = System.currentTimeMillis()+delay;
				dt=delay;

			}
			
			block(dt);
		}else{
			myRouter.addBehaviour(new RouterSendModData(myRouter));
			done = true;
		}

	}

	private void check() {
		Iterator<Entry<AID, RouterPort>> iter = myRouter.ports.entrySet().iterator();
		while(iter.hasNext()){
			Entry<AID, RouterPort> entry = iter.next();
			RouterPort port = entry.getValue();
			double busyperc = port.getLink().getPercBusyTime();
			double droppedperc = (double)port.getDrops()/port.getDrops()+port.getSent();
			if(droppedperc>0.3){
				if(busyperc>0.8){
					if(myRouter.stacksize+1<myRouter.maxbuffer){
						myRouter.stacksize++;
						log.log(Logger.INFO,myRouter.getLocalName()+"#upgrade memory");
					}
					break;
				}else{
					//upgrade bandwidth
					Link link = port.getLink();
					if(link.getBandwidth()+500<link.maxband){
						link.setBandwidth(link.getBandwidth()+500);
						log.log(Logger.INFO,myRouter.getLocalName()+"#upgrade bandwidth");
					}
				}
				
			}
		}
	}

	@Override
	public boolean done() {
		return done;
	}

}

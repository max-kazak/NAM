package ru.volterr.nam.behaviours.user;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import ru.volterr.nam.Constants;
import ru.volterr.nam.agents.ServerAgent;
import ru.volterr.nam.agents.UserAgent;
import jade.core.AID;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.util.Logger;

public class ServerSendConfirm extends SimpleBehaviour {

	private boolean done = false;
	
	private long stoptime,starttime,currenttime;	//time in experiment timeline
	private long 	delay = 2400,					//interval between checks
					dt = delay,						//block interval
	 				wakeuptime; 					//nextmessage send check
	
	private Logger log;
	protected ServerAgent myServer;
	
	public ServerSendConfirm(ServerAgent a,Long time){
		myServer = a;
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
				sendconf();
				log.log(Logger.INFO, myServer.getLocalName()+"#sending confirmation");
				wakeuptime = System.currentTimeMillis()+delay;
				dt=delay;

			}
			
			block(dt);
		}else{
			done = true;
		}

	}

	private void sendconf() {
		ACLMessage confmsg = new ACLMessage(ACLMessage.CONFIRM);
		confmsg.setConversationId(Constants.CONFIRM_CID);
		confmsg.setProtocol(Constants.CONFIRM_TCP);
		Set<Entry<AID, Integer>> set = new HashSet<Entry<AID, Integer>>();
		set.addAll(myServer.received.entrySet());	//create copy of data
		myServer.received = new HashMap<AID, Integer>(); //zero it
		Iterator<Entry<AID, Integer>> iter = set.iterator();
		
		while(iter.hasNext())
		{
			try {
				Entry<AID, Integer> entry = iter.next();
				confmsg.addReceiver(entry.getKey());
				confmsg.setContentObject(entry.getValue());
				myServer.send(confmsg);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public boolean done() {
		return done;
	}

}

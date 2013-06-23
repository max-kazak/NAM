package ru.volterr.nam.behaviours.user;

import ru.volterr.nam.Constants;
import ru.volterr.nam.agents.UserAgent;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import jade.util.Logger;

@SuppressWarnings("serial")
public class UserReceiveConfirm extends SimpleBehaviour {

private boolean done = false;
	
	private long stoptime,starttime,currenttime;	//time in experiment timeline

	protected UserAgent myUser;
	
	private MessageTemplate mt = MessageTemplate.MatchProtocol(Constants.CONFIRM_TCP);

	private Logger log;
	
	private int day = 1;
	
	public UserReceiveConfirm(UserAgent a,Long time){
		myUser = a;
		starttime = System.currentTimeMillis();
		stoptime = starttime + time;
		log = Logger.getMyLogger(this.getClass().getName());
	}
	
	@Override
	public void action() {
		if((currenttime = System.currentTimeMillis())<=stoptime){
			ACLMessage confmsg = myAgent.receive(mt);
			if(confmsg != null){
				log.log(Logger.INFO,myAgent.getLocalName() + "#received confirmation from "+confmsg.getSender().getLocalName());
				
				handle(confmsg);
			}
			block(100);
		}else{
			myUser.addBehaviour(new UserSendModData(myUser));
			myUser.showgraph();
			done = true;
		}

	}

	private void handle(ACLMessage confmsg) {
		try {
			int received = (Integer)confmsg.getContentObject();
			Integer send = new Integer(myUser.sendtoopt);
			Integer lost = new Integer(myUser.droppedontheway);
			myUser.sendtoopt=0;
			myUser.droppedontheway=0;
			//System.out.println("send after zeroing:" + send);
			
			if(lost<0){
				log.log(Logger.SEVERE,"lost packets < 0. WTF???!!!" + "   send: " + send + "   received: " + received);
				lost=0;
			}else{
				double ratio = (double)lost/(lost+received);
				//System.out.println("ratio: " + ratio + "   send: " + send + "   received: " + received + "   lost: " + lost);
				if(ratio > 0.3)
					myUser.decrQuality();
				if(ratio < 0.1)
					myUser.incrQuality();
			}
			myUser.series.add(day, myUser.tlaw.mT);
			day+=1;
		} catch (Exception e) {
			log.log(Logger.SEVERE, "Exception", e);
		}
		
	}

	@Override
	public boolean done() {
		return done;
	}

}

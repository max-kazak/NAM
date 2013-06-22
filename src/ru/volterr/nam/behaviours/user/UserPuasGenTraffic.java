package ru.volterr.nam.behaviours.user;

import java.io.IOException;

import jade.lang.acl.ACLMessage;
import jade.util.Logger;
import ru.volterr.nam.Constants;
import ru.volterr.nam.agents.UserAgent;
import ru.volterr.nam.law.SizeLaw;
import ru.volterr.nam.law.SizeParetLaw;


@SuppressWarnings("serial")
public class UserPuasGenTraffic extends UserGenTraffic {

	public UserPuasGenTraffic(UserAgent a, Long time) {
		super(a, time);
		log = Logger.getMyLogger(this.getClass().getName());
	}
	public UserPuasGenTraffic(UserAgent a, Long time, int averDeltaT) {
		super(a, time);
		log = Logger.getMyLogger(this.getClass().getName());
	}

	private Logger log;
	private SizeLaw slaw = new SizeParetLaw();
	
	@Override
	void generate() {
		if(myUser.tlaw.isNow(hour)){
			try {
				
				ACLMessage msgL3 = new ACLMessage(ACLMessage.INFORM);
				msgL3.setContentObject(new Integer(slaw.nextSize()));
				msgL3.addReceiver(myUser.getOptReceiver());
				msgL3.setSender(myUser.getAID());
				
				ACLMessage msgL2 = new ACLMessage(ACLMessage.INFORM);
				msgL2.setContentObject(msgL3);
				msgL2.setConversationId(Constants.NULL_CID);
				msgL2.addReceiver(myUser.getGateway());
				msgL2.setProtocol(Constants.INFORM_MESSAGE);
				
				log.log(Logger.INFO,myUser.getLocalName() + "# sended message to " + myUser.getOptReceiver().getLocalName());
				myUser.send(msgL2);
				myUser.sendtoopt+=1;
			} catch (IOException e) {
				log.log(Logger.SEVERE,"problems with output stream exception: ",e);
			} catch(IllegalArgumentException e){
				log.log(Logger.WARNING, "User " + myUser.getLocalName() + " must have at least one destination user to generate traffic", e);
				block();
			} catch (Exception e){
				log.log(Logger.SEVERE,"Exception",e);
			}
		}

	}

	
}

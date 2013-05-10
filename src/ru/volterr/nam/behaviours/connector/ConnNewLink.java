package ru.volterr.nam.behaviours.connector;

import java.io.IOException;
import java.io.Serializable;

import ru.volterr.nam.AIDPair;
import ru.volterr.nam.Constants;
import ru.volterr.nam.model.Link;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.util.Logger;

public class ConnNewLink extends OneShotBehaviour {

	Link link;
	private Logger log;
	
	public ConnNewLink(Link link){
		this.link = link;
		log = Logger.getMyLogger(this.getClass().getName());
	}
	
	@Override
	public void action() {
		ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		msg.addReceiver(link.getA().getId());
		msg.addReceiver(link.getZ().getId());
		msg.setProtocol(Constants.INFORM_LINK);
		try {
			msg.setContentObject(link);
			myAgent.send(msg);
			log.log(Logger.INFO,"Connector# sended link (" + link.getA().getName() + ", " + link.getZ().getName() + ").");
		} catch (IOException e) {
			log.log(Logger.SEVERE, "add obj to msg Exception:", e);
		}

	}

}

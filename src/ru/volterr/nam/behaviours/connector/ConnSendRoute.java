package ru.volterr.nam.behaviours.connector;

import java.io.IOException;

import ru.volterr.nam.AIDPair;
import ru.volterr.nam.Constants;
import ru.volterr.nam.agents.Connector;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.util.Logger;

public class ConnSendRoute extends OneShotBehaviour {

	AID requester, src, dest;
	private Logger log;
	
	public ConnSendRoute(AID requester, AID src, AID dest){
		super();
		this.requester = requester;
		this.src = src;
		this.dest = dest;
		log = Logger.getMyLogger(this.getClass().getName());
	}
	
	@Override
	public void action() {
		AID nextel = ((Connector)myAgent).getNextRouteHop(requester,src,dest);
		if (nextel != null){
			ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
			msg.addReceiver(requester);
			msg.setProtocol(Constants.INFORM_ROUTE);
			try {
				msg.setContentObject(new AIDPair(dest,nextel));
				myAgent.send(msg);
				log.log(Logger.INFO,"Connector# sended route to " + dest.getLocalName() + " to router " + requester.getLocalName());
			} catch (IOException e) {
				log.log(Logger.SEVERE, "add obj to msg Exception:", e);
			}
		}else{
			log.log(Logger.WARNING, myAgent.getLocalName() + ": no route found for " + requester.getLocalName() + " in path (" + src.getLocalName() + ", " + dest.getLocalName() + ").");
		}

	}

}

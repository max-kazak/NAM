package ru.volterr.nam.behaviours.connector;

import ru.volterr.nam.AIDPair;
import ru.volterr.nam.Constants;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import jade.util.Logger;

public class ConnRecvMsg extends CyclicBehaviour {

	private ACLMessage msg;
	private Logger log;

	public ConnRecvMsg(){
		super();
		log = Logger.getMyLogger(this.getClass().getName());
	}
	
	@Override
	public void action() {
		msg = myAgent.receive();
		if (msg != null) {
			switch(msg.getPerformative()){
				case ACLMessage.REQUEST:
					if(msg.getProtocol().equals(Constants.REQUEST_ROUTE)){
						log.log(Logger.INFO,myAgent.getLocalName() + "# received reqest for path from " + msg.getSender().getLocalName());
						AIDPair destSrcPair;
						try {
							destSrcPair = (AIDPair) msg.getContentObject();
							myAgent.addBehaviour(new ConnSendRoute(msg.getSender(),destSrcPair.getFirst(), destSrcPair.getSecond() ));
						} catch (UnreadableException e) {
							log.log(Logger.SEVERE,"Read msg content Exception: ",e);
						}
					}
					break;
						
			}
			
		}
		// Блокируем поведение, пока в очереди сообщений агента
		// не появится хотя бы одно сообщение
		block();

	}

}

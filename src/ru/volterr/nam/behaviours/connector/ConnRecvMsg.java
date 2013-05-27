package ru.volterr.nam.behaviours.connector;

import ru.volterr.nam.AIDPair;
import ru.volterr.nam.Constants;
import ru.volterr.nam.agents.Connector;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import jade.util.Logger;

public class ConnRecvMsg extends CyclicBehaviour {

	private ACLMessage msg;
	private Logger log;
	private MessageTemplate mt;
	private Connector myConnector;
	
	public ConnRecvMsg(Connector a){
		super();
		this.myConnector = a;
		log = Logger.getMyLogger(this.getClass().getName());
		mt = MessageTemplate.MatchConversationId(Constants.NULL_CID);
	}
	
	@Override
	public void action() {
		msg = myAgent.receive(mt);
		if (msg != null) {
			switch(msg.getPerformative()){
				case ACLMessage.REQUEST:
					if(msg.getProtocol().equals(Constants.REQUEST_ROUTE)){
						AIDPair destSrcPair;
						try {
							destSrcPair = (AIDPair) msg.getContentObject();
							log.log(Logger.INFO,myAgent.getLocalName() + "# received request for path("
																			+destSrcPair.getFirst().getLocalName()
																			+", "
																			+destSrcPair.getSecond().getLocalName()
																			+ ") from " + msg.getSender().getLocalName());
							myAgent.addBehaviour(new ConnSendRoute(msg.getSender(),destSrcPair.getFirst(), destSrcPair.getSecond() ));
						} catch (UnreadableException e) {
							log.log(Logger.SEVERE,"Read msg content Exception: ",e);
						}
					}
					break;
				case ACLMessage.INFORM:
					log.log(Logger.INFO, myAgent.getLocalName() + "#"+msg.getSender().getLocalName()+"notified about failure");
					if(msg.getProtocol().equals(Constants.INFORM_COFFEE)){
						AID aid = msg.getSender();
						if(msg.getContent().equals("down"))
							myConnector.nodeStatusChange(aid, false);
						else
							myConnector.nodeStatusChange(aid, true);
					}
					break;
						
			}
			
		}
		// Блокируем поведение, пока в очереди сообщений агента
		// не появится хотя бы одно сообщение
		block();

	}

}

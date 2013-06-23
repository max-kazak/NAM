package ru.volterr.nam.behaviours.user;

import ru.volterr.nam.Constants;
import ru.volterr.nam.ModelingPair;
import ru.volterr.nam.agents.ServerAgent;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import jade.util.Logger;

public class ServerReceiveMsg extends CyclicBehaviour {

	protected ServerAgent myServer;
	private ACLMessage L3msg;
	private ACLMessage L2msg;
	private Logger log;
	private MessageTemplate mt;
	
	public ServerReceiveMsg(ServerAgent a){
		super(a);
		myServer = a;
		
		mt = MessageTemplate.MatchConversationId(Constants.NULL_CID);
		//init logger
		log = Logger.getMyLogger(this.getClass().getName());
	}
	
	@Override
	public void action() {
		L2msg = myServer.receive(mt);
		if (L2msg != null) {
			handlemsg(L2msg);
		}
		// Блокируем поведение, пока в очереди сообщений агента
		// не появится хотя бы одно сообщение
		block();
	}
	
	private void handlemsg(ACLMessage L2msg){
		switch(L2msg.getPerformative()){
		case ACLMessage.INFORM:
			if(L2msg.getProtocol().equals(Constants.INFORM_MESSAGE)){
				try{
					L3msg = (ACLMessage) L2msg.getContentObject();
					
					if(myServer.received.containsKey(L3msg.getSender())){
						int msgs = myServer.received.get(L3msg.getSender());
						myServer.received.put(L3msg.getSender(),msgs+1);
					}else{
						myServer.received.put(L3msg.getSender(),1);
					}
					
					log.log(Logger.INFO,myServer.getLocalName() + "# received message from " + 
										L3msg.getSender().getLocalName() + ": " + 
										L3msg.getContentObject());
				}catch(Exception e){
					log.log(Logger.SEVERE,"can't cast to ACLMessage. " +
										"L2msg has wrong format and doesn't contain " +
										"L3msg inside of itself \nException: ",e);
				}
			}
			if(L2msg.getProtocol().equals(Constants.INFORM_STARTMODELING)){
				try {
					ModelingPair pair = (ModelingPair)L2msg.getContentObject();
					myServer.startModeling(pair.getFirst(),pair.getSecond());
				} catch (UnreadableException e) {
					log.log(Logger.SEVERE, "Cast Exception:", e);
				}
			}
			break;
		/*case ACLMessage.SUBSCRIBE:
			if(L2msg.getContent().equals("subscribe"))
				myUser.addSubscription(L2msg.getSender());
			if(L2msg.getContent().equals("unsubscribe"))
				myUser.removeSubscriber(L2msg.getSender());
			break;*/
		case ACLMessage.REQUEST:
			if(L2msg.getProtocol().equals(Constants.REQUEST_SHOW_GUI)){	
				myServer.showGui();
			}
			break;
		}
	
	}

}

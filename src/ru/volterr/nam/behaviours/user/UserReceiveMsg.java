package ru.volterr.nam.behaviours.user;

import ru.volterr.nam.Constants;
import ru.volterr.nam.ModelingPair;
import ru.volterr.nam.agents.UserAgent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import jade.util.Logger;

public class UserReceiveMsg extends CyclicBehaviour {

	protected UserAgent myUser;
	private ACLMessage L3msg;
	private ACLMessage L2msg;
	private Logger log;
	private MessageTemplate mt;
	
	public UserReceiveMsg(UserAgent a){
		super(a);
		myUser = a;
		
		mt = MessageTemplate.MatchConversationId(Constants.NULL_CID);
		//init logger
		log = Logger.getMyLogger(this.getClass().getName());
	}
	
	@Override
	public void action() {
		L2msg = myUser.receive(mt);
		if (L2msg != null) {
			handlemsg();
		}
			
		// Блокируем поведение, пока в очереди сообщений агента
		// не появится хотя бы одно сообщение
		block();
	}

	private void handlemsg(){
		switch(L2msg.getPerformative()){
		case ACLMessage.INFORM:
			/*if(L2msg.getProtocol().equals(Constants.INFORM_MESSAGE)){
				try{
					L3msg = (ACLMessage) L2msg.getContentObject();
					
					log.log(Logger.INFO,myUser.getLocalName() + "# received message from " + 
										L3msg.getSender().getLocalName() + ": " + 
										L3msg.getContent());
				}catch(Exception e){
					log.log(Logger.SEVERE,"can't cast to ACLMessage. " +
										"L2msg has wrong format and doesn't contain " +
										"L3msg inside of itself \nException: ",e);
				}
			}*/
			if(L2msg.getProtocol().equals(Constants.INFORM_STARTMODELING)){
				try {
					ModelingPair pair = (ModelingPair)L2msg.getContentObject();
					myUser.startModeling(pair.getFirst(),pair.getSecond());
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
				myUser.showGui();
			}
			break;
		}
	}
}

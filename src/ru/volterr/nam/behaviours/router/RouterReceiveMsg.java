package ru.volterr.nam.behaviours.router;

import ru.volterr.nam.AIDPair;
import ru.volterr.nam.Constants;
import ru.volterr.nam.agents.RouterAgent;
import ru.volterr.nam.agents.UserAgent;
import ru.volterr.nam.model.Link;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import jade.util.Logger;

public class RouterReceiveMsg extends CyclicBehaviour {

	protected RouterAgent myRouter;
	
	private ACLMessage L2msg;
	private ACLMessage L3msg;

	private Logger log;
	
	private MessageTemplate mt;
	
	public RouterReceiveMsg(RouterAgent a){
		super(a);
		myRouter = a;
		//init logger
		log = Logger.getMyLogger(this.getClass().getName());
		
		mt = MessageTemplate.MatchConversationId(Constants.NULL_CID);
	}
	
	@Override
	public void action() {
		while((L2msg = myRouter.receive(mt)) != null){
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
										
				//message log
				log.log(Logger.INFO,myRouter.getLocalName() + "# received message from: " 
											+ L2msg.getSender().getLocalName());
			    try {
			    	
					L3msg = (ACLMessage) L2msg.getContentObject();
					myRouter.addBehaviour(new RouterSendMsg(L3msg, myRouter));
					
			    } catch (UnreadableException e) {
					log.log(Logger.SEVERE,"can't cast L3msg from L2 exception: ",e);
				}
			}
			if(L2msg.getProtocol().equals(Constants.INFORM_ROUTE)){
				try{
					AIDPair destNextPair = (AIDPair) L2msg.getContentObject();
					myRouter.routetable.put(destNextPair.getFirst(), destNextPair.getSecond() );
					//message log
					log.log(Logger.INFO,myRouter.getLocalName() + "# received route to " + destNextPair.getFirst().getLocalName());
				}catch(Exception e){
					log.log(Logger.SEVERE,"get msg content Exception",e);
				}
			}
			if(L2msg.getProtocol().equals(Constants.INFORM_LINK)){
				try{
					Link link = (Link) L2msg.getContentObject();
					myRouter.addPort(link);
					//message log
					log.log(Logger.INFO,myRouter.getLocalName() + "# received new link (" + link.getA().getName() + ", " + link.getZ().getName() + ").");
				}catch(Exception e){
					log.log(Logger.SEVERE,"get msg content Exception",e);
				}
			}
			if(L2msg.getProtocol().equals(Constants.INFORM_STARTMODELING)){
				try {
					myRouter.startModeling((Long) L2msg.getContentObject());
				} catch (UnreadableException e) {
					log.log(Logger.SEVERE, "Cast Exception:", e);
				}
			}
		    break;
		case ACLMessage.REQUEST:
			if(L2msg.getProtocol().equals(Constants.REQUEST_SHOW_GUI)){
				//message log
				//log.log(Logger.INFO,myRouter.getLocalName() + "# received request to show gui");
				
				myRouter.showGui();	
			}
			break;
		}
	}

}

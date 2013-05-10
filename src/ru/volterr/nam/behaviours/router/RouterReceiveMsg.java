package ru.volterr.nam.behaviours.router;

import ru.volterr.nam.AIDPair;
import ru.volterr.nam.Constants;
import ru.volterr.nam.agents.RouterAgent;
import ru.volterr.nam.agents.UserAgent;
import ru.volterr.nam.model.Link;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import jade.util.Logger;

public class RouterReceiveMsg extends CyclicBehaviour {

	protected RouterAgent myRouter;
	
	private ACLMessage L2msg;
	private ACLMessage L3msg;

	private Logger log;
	
	public RouterReceiveMsg(RouterAgent a){
		super(a);
		myRouter = a;
		//init logger
		log = Logger.getMyLogger(this.getClass().getName());
	}
	
	@Override
	public void action() {
		L2msg = myRouter.receive();
		if (L2msg != null) {
			switch(L2msg.getPerformative()){
				case ACLMessage.INFORM:
					if(L2msg.getProtocol().equals(Constants.INFORM_MESSAGE)){
						String msgid = L2msg.getConversationId();
						
						//message log
						log.log(Logger.INFO,myRouter.getLocalName() + "# received message(" + msgid + ") from: " 
													+ L2msg.getSender().getLocalName());
					    try {
					    	
							L3msg = (ACLMessage) L2msg.getContentObject();
							myRouter.addBehaviour(new RouterSendMsg(L3msg, myRouter, msgid));
							
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
				    break;
				case ACLMessage.REQUEST:
					break;
			}
		}
		// Блокируем поведение, пока в очереди сообщений агента
		// не появится хотя бы одно сообщение
		block();
	}

}

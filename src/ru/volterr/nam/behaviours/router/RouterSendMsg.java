package ru.volterr.nam.behaviours.router;

import java.io.IOException;

import ru.volterr.nam.Constants;
import ru.volterr.nam.agents.RouterAgent;
import ru.volterr.nam.model.Link;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.util.Logger;

public class RouterSendMsg extends OneShotBehaviour {

	private ACLMessage L3msg;
	private String msgid;
	
	public RouterAgent myRouter;
	private Logger log;
	
	
	public RouterSendMsg(ACLMessage L3msg,RouterAgent myRouter,String msgid){
		super(myRouter);
		this.L3msg=L3msg;
		this.myRouter=myRouter;
		this.msgid=msgid;
		
		//init logger
		log = Logger.getMyLogger(this.getClass().getName());
	}
	
	@Override
	public void action() {
		AID dest = (AID)L3msg
				.getAllReceiver()	//iterator
				.next();				//get first AID
		RouterPort port = myRouter.getPort(dest);
		if(port != null){
			send(dest,L3msg,port);
		}else{
			AID nextrecv = myRouter.getNextRecv(dest);	
			if(nextrecv != null){
				port = myRouter.getPort(nextrecv);
				if(port != null)
					send(nextrecv, L3msg, port);
				else{
					log.log(Logger.INFO,myAgent.getLocalName() + "#next route hop " + 
														nextrecv.getLocalName() + 
														"for path(" + L3msg.getSender().getLocalName() + 
														", " + dest.getLocalName() + ") isn't directly connected.");
				}
			}
			else{
				log.log(Logger.INFO,myAgent.getLocalName() + "#route for " + 
													dest.getLocalName() + " doesn't exist.");
				myAgent.addBehaviour(new RouterRequestRoute(dest,L3msg.getSender()));
			}
		}
	}
	
	private void send(AID target, ACLMessage L3msg, RouterPort port){
		try{
			ACLMessage L2msg = new ACLMessage(ACLMessage.INFORM);
			L2msg.addReceiver(target);
			L2msg.setContentObject(L3msg);
			L2msg.setConversationId(msgid);
			L2msg.setProtocol(Constants.INFORM_MESSAGE);
			port.post(L2msg);
		} catch (IOException e) {
			log.log(Logger.SEVERE,"problem with output stream exception: ",e);
			} catch(Exception e){
			log.log(Logger.SEVERE,"Exception: ",e);
			}
	}

}

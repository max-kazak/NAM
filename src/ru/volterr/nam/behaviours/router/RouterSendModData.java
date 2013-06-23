package ru.volterr.nam.behaviours.router;

import ru.volterr.nam.Constants;
import ru.volterr.nam.Pair;
import ru.volterr.nam.RouterModData;
import ru.volterr.nam.agents.RouterAgent;
import ru.volterr.nam.agents.UserAgent;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.util.Logger;

public class RouterSendModData extends OneShotBehaviour {

	private RouterAgent myRouter;
	private Logger log;

	public RouterSendModData(RouterAgent a){
		myRouter = a;
		log = Logger.getMyLogger(this.getClass().getName());
	}
	
	@Override
	public void action() {
		DFAgentDescription dfd = new DFAgentDescription();
        ServiceDescription sd  = new ServiceDescription();
        sd.setType( "Connector" );
        dfd.addServices(sd);
        
        DFAgentDescription[] result;
		try {
			result = DFService.search(myAgent, dfd);
	
			AID connectorid = result[0].getName();
			
			ACLMessage msg = new ACLMessage(ACLMessage.CONFIRM);
			msg.addReceiver(connectorid);
			msg.setContentObject(new RouterModData(myRouter.getAID(), myRouter.stacksize, 20));
			msg.setProtocol(Constants.CONFIRM_FINISHMODELING);
			msg.setConversationId(Constants.NULL_CID);
			
			log.log(Logger.INFO,myAgent.getLocalName() + "#Finished modeling.");
			
			myAgent.send(msg);
			
		} catch (Exception e) {
			log.log(Logger.SEVERE, "Exception: " ,e);
		} 

	}

}

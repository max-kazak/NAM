package ru.volterr.nam.behaviours.router;

import ru.volterr.nam.AIDPair;
import ru.volterr.nam.Constants;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.util.Logger;

public class RouterRequestRoute extends OneShotBehaviour {

	private AID dest;
	private AID src;
	private Logger log;
	
	public RouterRequestRoute(AID dest,AID src){
		super();
		this.dest = dest;
		this.src = src;
		
		//init logger
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
			
			ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
			msg.addReceiver(connectorid);
			msg.setContentObject(new AIDPair(src,dest));
			msg.setProtocol(Constants.REQUEST_ROUTE);
			
			log.log(Logger.INFO,myAgent.getLocalName() + "#Requesting route to " + dest.getLocalName() + "... (Connector:" + connectorid.getLocalName() + ").");
			
			myAgent.send(msg);
			
		} catch (Exception e) {
			log.log(Logger.SEVERE, "Exception: " ,e);
		} 

	}

}

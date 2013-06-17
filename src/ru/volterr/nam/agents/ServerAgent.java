package ru.volterr.nam.agents;

import java.util.List;

import ru.volterr.nam.behaviours.user.RecvPing;
import ru.volterr.nam.behaviours.user.ServerReceiveMsg;
import ru.volterr.nam.behaviours.user.UserReceiveMsg;
import ru.volterr.nam.behaviours.user.UserStaticGenTraffic;
import ru.volterr.nam.gui.views.UserGUI;
import jade.core.AID;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.util.Logger;

public class ServerAgent extends GuiAgent {

	private AID gateway;

	private Logger log;
	
	//private ServerGUI gui;	- TODO
	
	public void setup(){
		//init logger
		log = Logger.getMyLogger(this.getClass().getName());
		
		//init gui
		//gui = new ServerGUI(this); - TODO
		//gui.setVisible(false);
		
		//parse arguments
		Object[] args = getArguments();
		if(args!=null && args.length>0)
			try{
				this.setGateway((AID)args[0]);
			}catch(Exception e){
				log.log(Logger.SEVERE, "Exception:", e);
			}
		
		log.log(Logger.INFO, "I am " + getLocalName() + ". Damn " + getLocalName() + "!");
		
		// Register the user generating service in the yellow pages
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("Server");
		sd.setName(getLocalName()+" Server");
		dfd.addServices(sd);
		try {
			DFService.register(this, dfd);
		}
		catch (FIPAException fe) {
			fe.printStackTrace();
		}
		
		addBehaviour(new ServerReceiveMsg(this));
		//addBehaviour(new UserStaticGenTraffic(this, dt)); - deprecated, now activates only at modeling procedure
		addBehaviour(new RecvPing());
		
	}
	
	
	protected void takeDown() {
		// Deregister from the yellow pages
		try {
			DFService.deregister(this);
		}
		catch (FIPAException fe) {
			fe.printStackTrace();
		}	
	}
	
	public void startModeling(Long time){
		log.log(Logger.INFO,getLocalName()+"#starts modeling procedure");
		//TODO - clear data
	}
	
	@Override
	protected void onGuiEvent(GuiEvent ev) {
		// TODO Auto-generated method stub

	}

	public void showGui(){
		//gui.setVisible(true); - TODO

	}
	
	public AID getGateway() {
		return gateway;
	}


	public void setGateway(AID gateway) {
		this.gateway = gateway;
	}
	
}

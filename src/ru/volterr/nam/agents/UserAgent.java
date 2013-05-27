package ru.volterr.nam.agents;

import java.util.ArrayList;
import java.util.List;

import ru.volterr.nam.behaviours.user.UserReceiveMsg;
import ru.volterr.nam.behaviours.user.UserRecvPing;
import ru.volterr.nam.behaviours.user.UserStaticGenTraffic;
import ru.volterr.nam.behaviours.user.UserSubscribe;
import ru.volterr.nam.gui.views.RouterGUI;
import ru.volterr.nam.gui.views.UserGUI;

import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.util.Logger;

public class UserAgent extends GuiAgent {

	private List<AID> subscribers = new ArrayList<AID>();
	private AID gateway;
	public int dt = 1000;
	
	private Logger log;
	
	private UserGUI gui;
	
	public void setup(){
		//init logger
		log = Logger.getMyLogger(this.getClass().getName());
		
		//init gui
		gui = new UserGUI(this);
		gui.setVisible(false);
		
		//parse arguments
		Object[] args = getArguments();
		if(args!=null && args.length>0)
			try{
				this.setGateway((AID)args[0]);
				if (args[1] != null)
					this.subscribers.addAll( (List<AID>) args[1] );
				if (args[2] != null)
					addBehaviour(new UserSubscribe((List<AID>) args[2],true));
			}catch(Exception e){
				log.log(Logger.SEVERE, "Exception:", e);
			}
		
		log.log(Logger.INFO, "I am " + getLocalName() + ". Damn " + getLocalName() + "!");
		
		// Register the user generating service in the yellow pages
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("Traffic-generating");
		sd.setName(getLocalName()+" User");
		dfd.addServices(sd);
		try {
			DFService.register(this, dfd);
		}
		catch (FIPAException fe) {
			fe.printStackTrace();
		}
		
		addBehaviour(new UserReceiveMsg(this));
		addBehaviour(new UserStaticGenTraffic(this, dt));
		addBehaviour(new UserRecvPing());
		
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
	
	public void addSubscribers(List<AID> targets){
		subscribers.addAll(targets);
	}
	
	public void addSubscriber(AID target){
		subscribers.add(target);
	}
	
	public void removeSubscribers(List<AID> targets){
		subscribers.removeAll(targets);
	}
	
	public void removeSubscriber(AID target){
		subscribers.remove(target);
	}


	@Override
	protected void onGuiEvent(GuiEvent ev) {
		// TODO Auto-generated method stub
		
	}
	
	public void showGui(){
		gui.setVisible(true);
		gui.update();
		
	}


	public AID getGateway() {
		return gateway;
	}


	public void setGateway(AID gateway) {
		this.gateway = gateway;
	}
	
	public List<AID> getSubscribers() {
		return subscribers;
	}
	

}

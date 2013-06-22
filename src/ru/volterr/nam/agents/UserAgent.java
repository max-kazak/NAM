package ru.volterr.nam.agents;

import jade.core.AID;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.util.Logger;

import java.util.ArrayList;
import java.util.List;

import ru.volterr.nam.behaviours.user.RecvPing;
import ru.volterr.nam.behaviours.user.UserPuasGenTraffic;
import ru.volterr.nam.behaviours.user.UserReceiveConfirm;
import ru.volterr.nam.behaviours.user.UserReceiveMsg;
import ru.volterr.nam.behaviours.user.UserStaticGenTraffic;
import ru.volterr.nam.behaviours.user.UserSubscribe;
import ru.volterr.nam.gui.views.UserGUI;
import ru.volterr.nam.law.TimeLaw;
import ru.volterr.nam.law.TimePuasLaw;

@SuppressWarnings("serial")
public class UserAgent extends GuiAgent {

	private List<AID> receivers = new ArrayList<AID>();
	private AID optReceiver;
	public int sendtoopt=0;
	private AID gateway;
	//public int dt = 1000;	- deprecated generation parameter
	
	public TimePuasLaw tlaw = new TimePuasLaw();
	private int maxMT=7, minMT=2;
	
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
				if (args[1] != null){
					this.receivers.addAll( (List<AID>) args[1] );
					setOptReceiver(receivers.get(0));
				}
			}catch(Exception e){
				log.log(Logger.SEVERE, "Exception:", e);
			}
		
		log.log(Logger.INFO, "I am " + getLocalName() + ". Damn " + getLocalName() + "!");
		
		// Register the user generating service in the yellow pages
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("User");
		sd.setName(getLocalName()+" User");
		dfd.addServices(sd);
		try {
			DFService.register(this, dfd);
		}
		catch (FIPAException fe) {
			fe.printStackTrace();
		}
		
		addBehaviour(new UserReceiveMsg(this));
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
		addBehaviour(new UserPuasGenTraffic(this,time));
		//TODO check on direct/reverse task
		//direct task
		addBehaviour(new UserReceiveConfirm(this, time));
	}
	
	public void addReceivers(List<AID> targets){
		receivers.addAll(targets);
	}
	
	public void addReceiver(AID target){
		receivers.add(target);
	}
	
	public void removeReceivers(List<AID> targets){
		receivers.removeAll(targets);
	}
	
	public void removeReceiver(AID target){
		receivers.remove(target);
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
	
	public List<AID> getReceivers() {
		return receivers;
	}


	public AID getOptReceiver() {
		return optReceiver;
	}


	public void setOptReceiver(AID optReceiver) {
		this.optReceiver = optReceiver;
	}


	public void incrQuality() {
		if(tlaw.mT-1>=minMT){
			tlaw.mT--;
			log.log(Logger.INFO,"intensity for server " + 
									optReceiver.getLocalName() + 
									" was increased");
		}
		
	}


	public void decrQuality() {
		if(tlaw.mT+1>maxMT){	//minimum intensity reached
			int i = receivers.indexOf(optReceiver);
			if(i<receivers.size()-1){
				optReceiver = receivers.get(i+1);	//next server
				tlaw.mT=minMT;
				log.log(Logger.INFO,"next server " + 
										optReceiver.getLocalName() + 
										" was chosen with max intensivity");
			}
			if( (i==receivers.size()-1) && (receivers.size()>1) ){
				optReceiver = receivers.get(0);		//first one if nothing left win minimum intensity
				tlaw.mT=maxMT;
				log.log(Logger.INFO,"best server " + 
										optReceiver.getLocalName() + 
										" was chosen with min intensivity");
			}
				
		}else{
			tlaw.mT++;			//decr intensity
			log.log(Logger.INFO,"intensity for server " + 
					optReceiver.getLocalName() + 
					" was decreased");
		}
	}
	

}

package ru.volterr.nam.behaviours.user;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

import java.io.IOException;
import java.util.Random;

import ru.volterr.nam.agents.UserAgent;

public class StaticGenTraffic extends CyclicBehaviour{

	private int dt = 1;
	protected UserAgent myUser;
	
	private Random rand = new Random();
	
	public StaticGenTraffic() {
		super();
		dt=1000;
	}
	
	public StaticGenTraffic(UserAgent a, int dt) {
		super(a);
		myUser = a;
		this.dt=dt;
	}
	
	//generates and sends messages
	@Override
	public void action() {
		int i = rand.nextInt(myUser.users.size()+1);
		
		ACLMessage msgL3 = new ACLMessage(ACLMessage.INFORM);
		msgL3.setContent("42");
		msgL3.addReceiver(myUser.users.get(i));
		
		ACLMessage msgL2 = new ACLMessage(ACLMessage.INFORM);
		try {
			msgL2.setContentObject(msgL3);
		} catch (IOException e) {
			e.printStackTrace(); //problems with writing to output stream
		}
		msgL2.addReceiver(myUser.gateway);
		
		System.out.println(myUser.getLocalName() + " sended message to " + myUser.users.get(i).getLocalName());
		myUser.send(msgL2);
		
		block(dt);
	}

}

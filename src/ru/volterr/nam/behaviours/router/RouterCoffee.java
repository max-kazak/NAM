package ru.volterr.nam.behaviours.router;

import java.util.Random;

import ru.volterr.nam.agents.RouterAgent;

import jade.core.behaviours.CyclicBehaviour;
import jade.util.Logger;

public class RouterCoffee extends CyclicBehaviour {

	private long dt,
				delay=10000,
				wakeuptime=System.currentTimeMillis()+delay;
	private RouterAgent myRouter;
	private Random rand = new Random();
	private Logger log;
	
	public RouterCoffee(RouterAgent a){
		super(a);
		myRouter=a;
		log = Logger.getMyLogger(this.getClass().getName());
	}
	
	@Override
	public void action() {
		if((dt=wakeuptime-System.currentTimeMillis())<0)
		{
			if(rand.nextDouble()>myRouter.fprob){
				log.log(Logger.INFO,myAgent.getLocalName() + "#i am down");
				myRouter.splitcoffee();
			}
			log.log(Logger.INFO,myAgent.getLocalName() + "#still alive");
			wakeuptime = System.currentTimeMillis()+delay+10000;
			dt=delay;
		}
		
		block(dt);

	}

}

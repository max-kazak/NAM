package ru.volterr.nam.agents;

import jade.core.Agent;

public class UserAgent extends Agent {
	
	private static final long serialVersionUID = -6378531004810497192L;

	
	public void setup(){
		System.out.println("I am " + getLocalName() + ". James " + getLocalName() + "!");
	}
}

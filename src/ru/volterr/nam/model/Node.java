package ru.volterr.nam.model;

import java.io.Serializable;

import jade.core.AID;

public class Node implements Serializable{


	private AID id;
	private String name;
	
	public Node(AID id){
		this.id = id;
		name = id.getLocalName();
	}
	public Node(AID id, String name){
		this.id = id;
		this.name = name;
	}
	
	public AID getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}

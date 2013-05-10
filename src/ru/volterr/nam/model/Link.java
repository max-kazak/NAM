package ru.volterr.nam.model;

import java.io.Serializable;

import jade.core.AID;

public class Link implements Serializable{
	//Link statuses
	public static final int FREE_STATUS = 0;
	public static final int BUSY_STATUS = 1;
	
	private Node a,z;
	private String name;
	private int astatus = 0, zstatus = 0;
	private long bandwidth;
	
	//public Link(){}
	//public Link(long bandwidth){
	//	this.bandwidth=bandwidth;
	//}
	public Link(Node apoint, Node zpoint, long bandwidth){
		this.bandwidth=bandwidth;
		this.a = apoint;
		this.z = zpoint;
		name = "(" + a.getName() + ", " + z.getName() + ")";
	}
	public Link(Node apoint, Node zpoint, long bandwidth, String name){
		this.bandwidth=bandwidth;
		this.a = apoint;
		this.z = zpoint;
		this.name = name;
	}
	
	//______getters/setters______
	public synchronized long getBandwidth() {
		return bandwidth;
	}
	public synchronized void setBandwidth(long bandwidth) {
		this.bandwidth = bandwidth;
	}
	public synchronized int getStatus(AID from) {
		if(from.equals(a.getId()))
			return astatus;
		else return zstatus;
	}
	public synchronized void setStatus(AID from, int status) {
		if(from.equals(a.getId()))
			this.astatus = status;
		else
			this.zstatus = status;
	}
	public Node getA() {
		return a;
	}
	public void setA(Node a) {
		this.a = a;
	}
	public Node getZ() {
		return z;
	}
	public void setZ(Node z) {
		this.z = z;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}

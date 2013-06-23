package ru.volterr.nam.model;

import java.io.Serializable;

import org.apache.commons.collections15.comparators.ComparableComparator;

import edu.uci.ics.jung.graph.util.EdgeType;

import jade.core.AID;

public class Link implements Serializable {
	//Link statuses
	public static final int FREE_STATUS = 0;
	public static final int BUSY_STATUS = 1;
	
	private Node a,z;
	private EdgeType type = EdgeType.UNDIRECTED;
	private String name;
	private int astatus = 0, zstatus = 0;
	
	private long busytime = 0, startbusytime,
				startwatchtime=0;	//when busy status observation started
	
	private long bandwidth;
	private long oldbandwidth;
	public static long maxband = 10000;
	private boolean available=true;
	private double fprob = 1.0;
	//public Link(){}
	//public Link(long bandwidth){
	//	this.bandwidth=bandwidth;
	//}
	public Link(Node apoint, Node zpoint, long bandwidth){
		this.bandwidth=bandwidth;
		oldbandwidth=bandwidth;
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
	
	public long getOldbandwidth() {
		return oldbandwidth;
	}
	public synchronized int getStatus(AID from) {
		if(from.equals(a.getId()))
			return astatus;
		else return zstatus;
	}
	
	
	public synchronized void setStatus(AID from, int status) {
		int oldstatus;
		if(from.equals(a.getId())){
			oldstatus = this.astatus;
			this.astatus = status;
		}else{
			oldstatus = this.zstatus;
			this.zstatus = status;
		}
		
		if(startwatchtime==0)
			startwatchtime = System.currentTimeMillis();
		
		//calculate busytime
		if(oldstatus!=status){
			if(status==BUSY_STATUS)
				startbusytime = System.currentTimeMillis();
			else
				busytime += System.currentTimeMillis()-startbusytime;
		}
		
	}
	
	public double getPercBusyTime(){
		long watchtime = startwatchtime - System.currentTimeMillis();
		double perc = (double)busytime/watchtime;
		busytime = 0;
		startwatchtime = System.currentTimeMillis();
		startbusytime = startwatchtime;
		return perc;
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
	
	public Node getOppositeNode(Node my){
		if( z.equals(my) )
			return a;
		else return z;	
	}
	public Node getOppositeNode(AID my){
		if( z.getId().equals(my) )
			return a;
		else return z;	
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	public EdgeType getType() {
		return type;
	}
	public void setType(EdgeType type) {
		this.type = type;
	}
	
	public boolean isAvailable() {
		return available;
	}
	public void setAvailable(boolean available) {
		this.available = available;
	}
	public double getFprob() {
		return fprob;
	}
	public void setFprob(double fprob) {
		if(fprob<this.fprob)
			this.fprob = fprob;
	}
	@Override
	public String toString(){
		return name;
	}

}

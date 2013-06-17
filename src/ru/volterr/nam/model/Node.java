package ru.volterr.nam.model;

import java.io.Serializable;

import ru.volterr.nam.Pair;

import jade.core.AID;

public class Node implements Serializable{

	public static final int ROUTER_TYPE = 1;
	public static final int USER_TYPE = 2;
	public static final int SERVER_TYPE = 23;
	
	
	public static final int STATUS_UP = 0;
	public static final int STATUS_DOWN = 1;

	private AID id;
	private String name;
	private int type;
	private int status = STATUS_UP;
	
	public Node(AID id, int type){
		this.id = id;
		this.type = type;
		name = id.getLocalName();
	}
	public Node(AID id, int type, String name){
		this(id,type);
		this.name = name;
	}
	
	public AID getId() {
		return id;
	}

	public int getType() {
		return type;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	@Override
	public String toString(){
		return name;
	}
	
	@Override
    public boolean equals(Object o) { 
      
      if (this.getClass() == o.getClass()) { 
        
        
        if(o!=null){
        	Node n = (Node) o;
        	return n.getId().equals(id);
        }
        
        return false;
        
      }
      return(false);
    }
    
    @Override
    public int hashCode() {
          int hash = 37;
          hash = hash*17 + id.hashCode();
                       
          return hash;
    }

	
}

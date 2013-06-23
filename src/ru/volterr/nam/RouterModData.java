package ru.volterr.nam;

import java.io.Serializable;

import jade.core.AID;

public class RouterModData implements Serializable{
	private static final long serialVersionUID = 4742046495991171668L;
	
	AID router;
	public RouterModData(AID router, Integer stack, Integer oldstack) {
		super();
		this.router = router;
		this.stack = stack;
		this.oldstack = oldstack;
	}
	
	Integer stack,oldstack;
	
	public AID getRouter() {
		return router;
	}
	public void setRouter(AID router) {
		this.router = router;
	}
	public Integer getStack() {
		return stack;
	}
	public void setStack(Integer stack) {
		this.stack = stack;
	}
	public Integer getOldstack() {
		return oldstack;
	}
	public void setOldstack(Integer oldstack) {
		this.oldstack = oldstack;
	}
	
}

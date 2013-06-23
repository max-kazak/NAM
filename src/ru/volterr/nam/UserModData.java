package ru.volterr.nam;

import java.io.Serializable;

import jade.core.AID;

public class UserModData implements Serializable{
	AID user,optreceiver,oldreceiver;
	Integer intensity, oldintensity;
	
	
	
	public UserModData(AID user, AID optreceiver, AID oldreceiver,
			Integer intensity, Integer oldintensity) {
		super();
		this.user = user;
		this.optreceiver = optreceiver;
		this.oldreceiver = oldreceiver;
		this.intensity = intensity;
		this.oldintensity = oldintensity;
	}
	
	public AID getUser() {
		return user;
	}
	public void setUser(AID user) {
		this.user = user;
	}
	public AID getOptreceiver() {
		return optreceiver;
	}
	public void setOptreceiver(AID optreceiver) {
		this.optreceiver = optreceiver;
	}
	public AID getOldreceiver() {
		return oldreceiver;
	}
	public void setOldreceiver(AID oldreceiver) {
		this.oldreceiver = oldreceiver;
	}
	public Integer getIntensity() {
		return intensity;
	}
	public void setIntensity(Integer intensity) {
		this.intensity = intensity;
	}
	public Integer getOldintensity() {
		return oldintensity;
	}
	public void setOldintensity(Integer oldintensity) {
		this.oldintensity = oldintensity;
	}
}

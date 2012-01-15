package com.clemson.narada.pubsub;

import cgl.narada.service.client.NBRecoveryListener;

interface Subject extends NBRecoveryListener{
	public void addObserver(Observer o);

	public void removeObserver(Observer o);

	public String getTopic();
	
	public void setTopic(String topic);
	
	public void setMessage(String message);
}
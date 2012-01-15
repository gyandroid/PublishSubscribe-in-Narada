package com.clemson.narada.servbroker.pubsub;

import cgl.narada.service.ServiceException;
import cgl.narada.service.client.NBEventListener;
import cgl.narada.service.client.NBRecoveryListener;

interface Subject extends NBRecoveryListener, NBEventListener{
	public void addObserver(Observer o);

	public void removeObserver(Observer o);

	public String getState();

	public void setState(String state);
	
	public void setMessage(String message);

	public void initializeProducerAndConsumer(int templateId) throws ServiceException;

	public void publishEvent(String state) throws ServiceException;
}
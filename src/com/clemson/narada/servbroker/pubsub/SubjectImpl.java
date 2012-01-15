package com.clemson.narada.servbroker.pubsub;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cgl.narada.event.NBEvent;
import cgl.narada.event.TemplateProfileAndSynopsisTypes;
import cgl.narada.service.ServiceException;
import cgl.narada.service.client.ClientService;
import cgl.narada.service.client.EventProducer;
import cgl.narada.service.client.NBRecoveryNotification;

class SubjectImpl implements Subject {
	private List<Observer> observers = new ArrayList<Observer>();

	private String state;
	
	private String message;

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
		reloadState();
		notifyObservers();
		publishMessage();
	}

	private void publishMessage() {
		try {
			this.publishEvent(message);
		} catch (ServiceException e) {
			e.printStackTrace();
		}

	}

	public void addObserver(Observer o) {
		observers.add(o);
	}

	public void removeObserver(Observer o) {
		observers.remove(o);
	}

	public void notifyObservers() {
		Iterator<Observer> i = observers.iterator();
		while (i.hasNext()) {
			Observer o = (Observer) i.next();
			o.update(this);
		}
	}

	private ClientService clientService;
	private String moduleName = "Publisher: ";
	private EventProducer producer;

	// private ProducerConstraints producerConstraints;

	public SubjectImpl(ClientService clientService) {
		this.clientService = clientService;
	}

	public void initializeProducerAndConsumer(int templateId)
			throws ServiceException {
		producer = clientService.createEventProducer();
		long recoveryId = producer.recover(templateId, this);
		System.out.println(moduleName + "Assigned recovery id = [" + recoveryId
				+ "] \n\n");
	}

	public void publishEvent(String stringMsg) throws ServiceException {
		if (stringMsg.equals("")) {
			stringMsg += System.currentTimeMillis();
		}
		producer.generateEventIdentifier(true);
		producer.setTemplateId(12345);
		producer.setDisableTimestamp(false);
		NBEvent nbEvent = producer.generateEvent(
				TemplateProfileAndSynopsisTypes.STRING, state,
				stringMsg.getBytes());
		producer.publishEvent(nbEvent);
		System.out.println(moduleName + "Published NBEvent = " + stringMsg
				+ " \n\n");
	}

	public void onEvent(NBEvent nbEvent) {
		System.out.println(moduleName + "Received NBEvent " + nbEvent);
	}

	public void onRecovery(NBRecoveryNotification recoveryNotification) {
		System.out.println(moduleName + recoveryNotification);
	}

	public void reloadState() {
		int templateId = 12345;
		//String module = "SubjectImpl.main() ->";
		try {
			this.initializeProducerAndConsumer(templateId);
			//System.out.println(module + "exiting ");
		} catch (ServiceException serEx) {
			System.out.println(serEx);
		}
	}

	@Override
	public void setMessage(String message) {
		this.message = message;
		
	}
}
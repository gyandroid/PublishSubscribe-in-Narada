package com.clemson.narada.servbroker.pubsub;

import cgl.narada.event.NBEvent;
import cgl.narada.event.TemplateProfileAndSynopsisTypes;
import cgl.narada.matching.Profile;
import cgl.narada.service.ServiceException;
import cgl.narada.service.client.ClientService;
import cgl.narada.service.client.EventConsumer;
import cgl.narada.service.client.NBRecoveryNotification;

class ObserverImpl implements Observer {
	private String state = "";

	private ClientService clientService;
	private String moduleName ="ObserverImpl: ";
	private EventConsumer consumer;

	public ObserverImpl(ClientService clientService) {
		this.clientService = clientService;
	}

	public void initializeProducerAndConsumer(int templateId)
			throws ServiceException {
		Profile profile = clientService.createProfile(
				TemplateProfileAndSynopsisTypes.STRING, state);
		consumer = clientService.createEventConsumer(this);

		consumer.subscribeTo(profile);
		long recoveryId = consumer.recover(templateId, this);
		System.out.println(moduleName + "Subscriber: Assigned recovery id = ["
				+ recoveryId + "] \n\n");
	}

	public void onEvent(NBEvent nbEvent) {
		System.out.println("\n\n\n\n" + moduleName + "Received NBEvent =>"
				+ new String(nbEvent.getContentPayload()) + "\n\n");
	}

	public void onRecovery(NBRecoveryNotification recoveryNotification) {

		System.out.println("\n\n\n\n" + moduleName + recoveryNotification
				+ "\n\n");
	}

	public void update(Subject o) {
		int templateId = 12345;
		state = o.getState();
		String module = "ObserverImpl.main() ->";
		try {
			this.initializeProducerAndConsumer(templateId);

			System.out.println(module + "exiting ");

		} catch (ServiceException serEx) {
			System.out.println(serEx);
		}
	}
}

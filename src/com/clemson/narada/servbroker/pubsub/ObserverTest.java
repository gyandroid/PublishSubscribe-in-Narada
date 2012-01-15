package com.clemson.narada.servbroker.pubsub;

import java.util.Properties;

import cgl.narada.service.ServiceException;
import cgl.narada.service.client.ClientService;
import cgl.narada.service.client.SessionService;

public class ObserverTest {

	private static ClientService clientService;

	public static void main(String[] args) {
		Properties props = new Properties();
		props.put("hostname", "172.22.121.32");
		props.put("portnum", "3045");
		int entityId = 7007;

		try {
			clientService = SessionService.getClientService(entityId);
			initializeBrokerCommunications(props, "niotcp");
		} catch (ServiceException serEx) {
			System.out.println(serEx);
		}

		Observer o = new ObserverImpl(clientService);
		Subject s = new SubjectImpl(clientService);
		s.addObserver(o);
		s.setMessage("Narada Sample broker message");
		s.setState("/topic/new");
	}

	public static void initializeBrokerCommunications(Properties props,
			String commType) throws ServiceException {
		clientService.initializeBrokerCommunications(props, commType);
	}
}

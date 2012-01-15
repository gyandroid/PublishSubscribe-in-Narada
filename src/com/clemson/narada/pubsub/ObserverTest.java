package com.clemson.narada.pubsub;

import java.util.Properties;

import javax.jms.JMSException;

import cgl.narada.jms.JmsTopicConnection;
import cgl.narada.jms.JmsTopicConnectionFactory;
import cgl.narada.jms.NBJmsInitializer;

public class ObserverTest {

	public static void main(String[] args) {
		Properties props = new Properties();
		props.put("hostname", "172.22.121.32");
		props.put("portnum", "3045");
		JmsTopicConnection topicConn = null;
		try {
			NBJmsInitializer ctx = new NBJmsInitializer(props, "niotcp", 12);
			JmsTopicConnectionFactory connFactory = (JmsTopicConnectionFactory) ctx
					.lookup();
			topicConn = (JmsTopicConnection) connFactory.createTopicConnection();
			topicConn.start();
		} catch (JMSException jmse) {
			jmse.printStackTrace();
			System.out.println("JMS Exception");
		}
		Observer o = new ObserverImpl(topicConn);
		Subject s = new SubjectImpl(topicConn);
		s.addObserver(o);
		s.setMessage("sampleMessage");
		s.setTopic("/topic/new");
		
	}
}

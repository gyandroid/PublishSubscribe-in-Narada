package com.clemson.narada.pubsub;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.jms.JMSException;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;

import cgl.narada.jms.JmsSession;
import cgl.narada.jms.JmsTopic;
import cgl.narada.service.client.NBRecoveryNotification;

class SubjectImpl implements Subject {
	private List<Observer> observers = new ArrayList<Observer>();

	private String topic;

	private String message;

	private TopicConnection topicConn;

	private TopicPublisher topicPublisher;

	private TopicSession topicSession;

	public SubjectImpl(TopicConnection topicConn) {
		this.topicConn = topicConn;

	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
		reloadState();
		notifyObservers();
		writeMessage();
		intimateObservers();
	}

	private void intimateObservers() {
		Iterator<Observer> i = observers.iterator();
		while (i.hasNext()) {
			Observer o = (Observer) i.next();
			o.receive();
		}

	}

	private void writeMessage() {
		try {
			TextMessage textMessage = (TextMessage) topicSession
					.createTextMessage();
			textMessage.setText(message);
			topicPublisher.publish(textMessage);
			System.out.println("published: " + textMessage.getText());
		} catch (JMSException jmse) {
			jmse.printStackTrace();
			System.out.println("JMS Exception");
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

	public void reloadState() {
		try {
			topicSession = (TopicSession) topicConn.createTopicSession(false,
					JmsSession.AUTO_ACKNOWLEDGE);

			Topic jmsTopic = new JmsTopic(topic);
			topicPublisher = (TopicPublisher) topicSession
					.createPublisher(jmsTopic);
			topicPublisher.setDeliveryMode(1);

			// topicConn.close();
		} catch (JMSException jmse) {
			jmse.printStackTrace();
			System.out.println("JMS Exception");
		}
	}

	public void onRecovery(NBRecoveryNotification recoveryNotification) {
		System.out.println(recoveryNotification);
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
package com.clemson.narada.pubsub;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicSession;
import javax.jms.TopicSubscriber;

import cgl.narada.jms.JmsSession;
import cgl.narada.jms.JmsTopic;

class ObserverImpl implements Observer, javax.jms.MessageListener {
	private String state;

	private TopicConnection topicConn;

	private TopicSubscriber topicSubscriber;

	public ObserverImpl(TopicConnection topicConn) {
		this.topicConn = topicConn;

	}

	public void update(Subject o) {
		try {
			state = o.getTopic();
			Topic jmsTopic = new JmsTopic(state);
			TopicSession topicSession = (TopicSession) topicConn
					.createTopicSession(false, JmsSession.AUTO_ACKNOWLEDGE);
			topicSubscriber = (TopicSubscriber) topicSession
					.createSubscriber(jmsTopic);
			topicSubscriber.setMessageListener(this);
		} catch (JMSException jmse) {
			jmse.printStackTrace();
			System.out.println("JMS Exception");
		}
	}

	public void receive() {
		try {
			TextMessage textMessage = (TextMessage) topicSubscriber.receive();
			System.out.println("Received pushed: " + textMessage.getText());
		} catch (JMSException jmse) {
			jmse.printStackTrace();
			System.out.println("JMS Exception");
		} catch (NullPointerException npe) {

		}
	}

	@Override
	public void onMessage(Message message) {
		try {
			TextMessage textMessage = (TextMessage) message;
			String text = textMessage.getText();
			System.out.println("Received: " + text);
			topicConn.stop();
			topicConn.close();
		} catch (JMSException jmse) {
			jmse.printStackTrace();
		}

	}
}

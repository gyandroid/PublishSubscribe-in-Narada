package com.clemson.narada.pubsub;

interface Observer {
	public void update(Subject o);

	public void receive();
}

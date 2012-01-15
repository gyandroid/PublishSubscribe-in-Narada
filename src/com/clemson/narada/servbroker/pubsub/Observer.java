package com.clemson.narada.servbroker.pubsub;

import cgl.narada.service.client.NBEventListener;
import cgl.narada.service.client.NBRecoveryListener;

interface Observer extends NBEventListener, NBRecoveryListener{
	public void update(Subject o);
}

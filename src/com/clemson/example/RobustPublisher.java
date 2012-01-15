/**
 * Software License, Version 1.0
 * 
 * Copyright 2003 The Trustees of Indiana University.  All rights reserved.
 * 
 *
 *Redistribution and use in source and binary forms, with or without 
 *modification, are permitted provided that the following conditions are met:
 *
 *1) All redistributions of source code must retain the above copyright notice,
 * the list of authors in the original source code, this list of conditions and
 * the disclaimer listed in this license;
 *2) All redistributions in binary form must reproduce the above copyright 
 * notice, this list of conditions and the disclaimer listed in this license in
 * the documentation and/or other materials provided with the distribution;
 *3) Any documentation included with all redistributions must include the 
 * following acknowledgement:
 *
 *"This product includes software developed by the Community Grids Lab. For 
 * further information contact the Community Grids Lab at 
 * http://communitygrids.iu.edu/."
 *
 * Alternatively, this acknowledgement may appear in the software itself, and 
 * wherever such third-party acknowledgments normally appear.
 * 
 *4) The name Indiana University or Community Grids Lab or NaradaBrokering, 
 * shall not be used to endorse or promote products derived from this software 
 * without prior written permission from Indiana University.  For written 
 * permission, please contact the Advanced Research and Technology Institute 
 * ("ARTI") at 351 West 10th Street, Indianapolis, Indiana 46202.
 *5) Products derived from this software may not be called NaradaBrokering, 
 * nor may Indiana University or Community Grids Lab or NaradaBrokering appear
 * in their name, without prior written permission of ARTI.
 * 
 *
 * Indiana University provides no reassurances that the source code provided 
 * does not infringe the patent or any other intellectual property rights of 
 * any other entity.  Indiana University disclaims any liability to any 
 * recipient for claims brought by any other entity based on infringement of 
 * intellectual property rights or otherwise.  
 *
 *LICENSEE UNDERSTANDS THAT SOFTWARE IS PROVIDED "AS IS" FOR WHICH NO 
 *WARRANTIES AS TO CAPABILITIES OR ACCURACY ARE MADE. INDIANA UNIVERSITY GIVES
 *NO WARRANTIES AND MAKES NO REPRESENTATION THAT SOFTWARE IS FREE OF 
 *INFRINGEMENT OF THIRD PARTY PATENT, COPYRIGHT, OR OTHER PROPRIETARY RIGHTS. 
 *INDIANA UNIVERSITY MAKES NO WARRANTIES THAT SOFTWARE IS FREE FROM "BUGS", 
 *"VIRUSES", "TROJAN HORSES", "TRAP DOORS", "WORMS", OR OTHER HARMFUL CODE.  
 *LICENSEE ASSUMES THE ENTIRE RISK AS TO THE PERFORMANCE OF SOFTWARE AND/OR 
 *ASSOCIATED MATERIALS, AND TO THE PERFORMANCE AND VALIDITY OF INFORMATION 
 *GENERATED USING SOFTWARE.
 */
package com.clemson.example;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

import cgl.narada.event.NBEvent;
import cgl.narada.event.TemplateInfo;
import cgl.narada.event.TemplateProfileAndSynopsisTypes;
import cgl.narada.service.ServiceException;
import cgl.narada.service.client.ClientService;
import cgl.narada.service.client.EventProducer;
import cgl.narada.service.client.NBEventListener;
import cgl.narada.service.client.NBRecoveryListener;
import cgl.narada.service.client.NBRecoveryNotification;
import cgl.narada.service.client.SessionService;
import cgl.narada.service.qos.ProducerConstraints;
/**
   A simple native client of NaradaBrokering 
   @author Shrideep Pallickara
   $Date$
   $Revision$
 */

public class RobustPublisher implements NBEventListener, NBRecoveryListener {
  
  private ClientService clientService;
  private String moduleName= "RobustPublisher: ";
  private EventProducer producer;
  private ProducerConstraints producerConstraints;

  public RobustPublisher(int entityId) {
    try {
      clientService = SessionService.getClientService(entityId);
    } catch (ServiceException serEx) {
      System.out.println(serEx);
    }
  }
  
  public void 
  initializeBrokerCommunications(Properties props, String commType) 
    throws ServiceException{
    clientService.initializeBrokerCommunications(props, commType);
  }
  
  public void initializeProducerAndConsumer(int templateId) 
    throws ServiceException {
    producer = clientService.createEventProducer();
    TemplateInfo templateInfo = 
      clientService.createTemplateInfo(templateId, 
				       TemplateProfileAndSynopsisTypes.STRING,
				       "Movie/Casablanca");
    producerConstraints = 
      producer.createProducerConstraints(templateInfo);
    producerConstraints.setSendReliably();
    long recoveryId = producer.recover(templateId, this);
    System.out.println(moduleName + "Assigned recovery id = [" +
		       recoveryId + "] \n\n");
  }
  

  public void publishEvent(String stringMsg) throws ServiceException {
    if (stringMsg.equals("")) {
      stringMsg += System.currentTimeMillis();
    }
    producer.generateEventIdentifier(true);
    producer.setTemplateId(12345);
    producer.setDisableTimestamp(false);
    NBEvent nbEvent = 
      producer.generateEvent(TemplateProfileAndSynopsisTypes.STRING,
			     "Movie/Casablanca",
			     stringMsg.getBytes());
    producer.publishEvent(nbEvent, producerConstraints);
  }
  

  
  public void onEvent(NBEvent nbEvent) {
    System.out.println(moduleName + "Received NBEvent " + nbEvent);
  }

  /** Upon completion of the attempt to recover, this method is invoked on
      the listener that was registered with the */
  public void onRecovery(NBRecoveryNotification recoveryNotification) {
    System.out.println(moduleName + recoveryNotification);
  }
  
  public static void main(String[] args) {
    int entityId = 7007;
    int templateId = 12345;
    RobustPublisher client = new RobustPublisher(entityId);
    Properties props = new Properties();
    String module = "RobustPublisher.main() ->";
    /** These properties pertain to setting up a TCP link */
    props.put("hostname", args[0]); 
    props.put("portnum", args[1]);
    try { 
      client.initializeBrokerCommunications(props, "niotcp");
      client.initializeProducerAndConsumer(templateId);
      
      BufferedReader commandLine = 
	new BufferedReader(new InputStreamReader(System.in));
      int tracker = 0;
      while(true){
	String s = commandLine.readLine();
	if (s == null) {
	  System.out.println(module + "String is null!!!");
	  break;
	}
	if (s.equals("m")) { 
	  for (int i = 1; i <10; i++) {
	    tracker++;
	    client.publishEvent("Multiple Messages [" + tracker + "]");
	  }
	  continue;
	}
	tracker++;
	client.publishEvent(s + "[" + tracker + "]" );
	
      }/** end while-true */
      System.out.println(module + "exiting ");

    } catch (IOException ioe) {
       System.out.println(ioe);
    } catch (ServiceException serEx) {
      System.out.println(serEx);
    }
  }
}












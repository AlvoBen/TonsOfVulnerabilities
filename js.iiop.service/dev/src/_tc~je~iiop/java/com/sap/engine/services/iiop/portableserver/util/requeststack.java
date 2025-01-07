package com.sap.engine.services.iiop.PortableServer.util;

import com.sap.engine.services.iiop.internal.giop.IncomingRequest;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

//it is queue not a stack! :)

public class RequestStack {
  
  private static List requests = Collections.synchronizedList(new LinkedList());
  private static int index;

  
  public static synchronized void push(IncomingRequest req){
    requests.add(req);
    index++;
  }
  
  public static synchronized IncomingRequest pop(){
    index--;
    return (IncomingRequest)requests.remove(0);
  }
  
  public static int holdingRequests(){
    return index;
  }
  
}
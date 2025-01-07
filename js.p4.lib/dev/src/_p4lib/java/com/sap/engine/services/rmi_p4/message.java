package com.sap.engine.services.rmi_p4;

import com.sap.engine.frame.core.thread.ClientThreadContext;
import com.sap.engine.frame.core.thread.ContextObject;
import com.sap.engine.frame.core.thread.Transferable;
import com.sap.engine.frame.core.thread.TransferableExt;
import com.sap.engine.lib.lang.Convert;
import com.sap.engine.services.rmi_p4.exception.P4Logger;
import com.sap.engine.interfaces.cross.*;
import com.sap.tc.logging.Location;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.io.IOException;

/**
 * Object presentation for the incomming requests
 *
 * @author Georgy Stanev
 * @version 7.0
 */
public class Message extends AbstractCrossMessage {

  /**
   * indicate start possition of message body
   */
  protected int offBody = 0;
  protected boolean parsed = false;
  protected int size;
  protected int lenTC;
  ByteArrayInputStream bais = null;
  private static boolean wait;
  protected boolean inCall = false;
  /**
   */
  public static final int CALL_REQUEST = 0;
  /**
   */
  public static final int DISPATCH_REPLY = 1;
  /**
   */
  public static final int DISPATCH_ERROR_REPLY = 2;
  public static final int SPECIAL_ERROR_MESSAGE = 3;
  public static final int PING_CONNECTION_MESSAGE = 4;
  public static final int PING_CONNECTION_MESSAGE_SERVER = 5;

  /**
   */
  public static final int RESOLVE_INITIAL_REFERENCE = 10;
  public static final int RESOLVE_INITIAL_REFERENCE_REPLY = 11;
  /**
   */
  public static final int INFORM_MESSAGE = 20;
  /**
   *
   */
  public static final int GET_CONTEXT_OBJECT = 30;
  public static final int GET_CONTEXT_OBJECT_REPLY = 31;

  //SPECIAL_ERROR_MESSAGE error codes
  public static final int OK = 0; // no error
  public static final int INTERNAL_ERROR  = 1;// unspecific error
  public static final int NOSERVER_AVAIL  = 2; // no server is available for balancable requests
  public static final int SERVER_DOWN     = 3; // the requested server is not available
  public static final int REQUEST_TOO_BIG = 4;//  request message was too big
  public static final int SERVER_CRASH    = 5;// server crashed while processing the request
  public static final int PROTOCOL_ERROR  = 6; // client caused a protocol error (magic, P4 version error, etc)

  /**
   */
  public static final int OBJECT_KEY_SIZE = 12;
  /**
   * session id returned by login
   */
  public long ssid;
  /**
   * the cluster element from witch is the request
   */
  public int clusterEl_id;
  /**
   * the server from witch the request arrives
   */
  public int sender_id;
  /**
   * the client connection id
   */
  public int client_id;
  /**
   * remaining request after parsing message protocol header
   */
  public byte[] unmarshaledRequest;
  public byte[] request;
  /**
   * the type of the request
   */
  public int type;
  /**
   * the length of the operation name string
   */
  public int nextPos = 0;
  /**
   * the client side call id.
   */
  public byte[] call_id;
  /**
   * own server id
   */
  public int own_id = -1;
  /**
   * the opration string name represented by byte array
   * obtained with String.getBytes()
   */
  public String opName;
  public boolean isRedir = false;
  public String ident = "";
  protected String factoryName = null;
  protected Serializable objIdentity = null;
  int redirFlag = 0;
  int namePos = -1;
  int nameSize = -1;

  int factoryPos = -1;
  int factorySize = -1;
  int idObjPos = -1;
  int idObjSize = -1;
  /**
   * the key of the requested object
   */
  public byte[] objectKey;
  //Should be set from readString...
  public String name = null;
  public static ObjectInfo obj = null;
  private ClientConnection con = null; // this connection wiil use to send to server inner message to get CO class
  protected P4ContextObject p4co = null;

  protected StubBaseInfo info = null;

  /**
   * rights restriction constructor
   */
  public Message() {

  }

  public Message(int clusterEl_id, int client_id, byte[] request, ClientConnection c) {
    this(clusterEl_id, client_id, request);
    this.con = c;
  }

  /**
   * Creates  new message object from request
   *
   * @param clusterEl_id the cluster element from witch comes
   *                     the request
   * @param client_id    the client id from witch comes the
   *                     request
   * @param request      parser request
   */
  public Message(int clusterEl_id, int client_id, byte[] request) {
    this.request = request;
    this.clusterEl_id = clusterEl_id;
    this.client_id = client_id;
    this.sender_id = clusterEl_id;
    call_id = new byte[8];
    System.arraycopy(request, offBody, call_id, 0, 8); // protocol call id
    type = request[offBody + 8];
    size = request.length;
  }

  /**
   * Creates  new message object from full message request (<message header><message body>).
   *
   * @param clusterEl_id the cluster element from witch comes
   *                     the request
   * @param client_id    the client id from witch comes the
   *                     request
   * @param request      parser request
   */
  public Message(int sender_id, int clusterEl_id, int client_id, byte[] request, int size) {
    this.offBody = ProtocolHeader.HEADER_SIZE;
    this.sender_id = sender_id;
    this.size = size;
    this.own_id = Convert.byteArrToInt(request, 6);
    this.request = request;
    this.clusterEl_id = clusterEl_id;
    this.client_id = client_id;
    call_id = new byte[8];
    System.arraycopy(request, offBody, call_id, 0, 8); // protocol call id
    type = request[offBody + 8];
    size = request.length;
  }

  public byte[] getUnmarshaledRequest() {
    if (!parsed) {
      parseMessage();
    }
    return unmarshaledRequest;
  }

  public int getSize() {
    return size;
  }

  public int getOffset() {
    return offBody;
  }

  public long getCallId() {
    return Convert.byteArrToLong(request, offBody);
  }

  public void parseMessage() {

    lenTC = Convert.byteArrToInt(request, offBody + ProtocolHeader.THREAD_CONTEXT);
    nextPos = offBody + ProtocolHeader.THREAD_CONTEXT + 4;

    if ((type != RESOLVE_INITIAL_REFERENCE) && (type != RESOLVE_INITIAL_REFERENCE_REPLY) && (type != INFORM_MESSAGE) && (type != PING_CONNECTION_MESSAGE)) {
      if (lenTC > 0) {
        int co_name_size = 0;
        int coCounts = 0;
        int byteArrSize = 0;
        int currentCO = 0;

        coCounts = (request[nextPos] & 0x00ff) | (request[nextPos + 1] << 8);
        nextPos = nextPos + 2;

        while (currentCO < coCounts) {
          co_name_size = (request[nextPos] & 0x00ff) | (request[nextPos + 1] << 8);
          nextPos = nextPos + 2;
          name = readString(request, nextPos, co_name_size);

          nextPos = nextPos + co_name_size;
          byteArrSize = (request[nextPos] & 0x00ff) | (request[nextPos + 1] << 8);
          nextPos = nextPos + 2;
          //Skip context objects loading from reply for initial check of context objects
          if (P4ObjectBroker.getBroker().coLoadFilter.get() == null) {
            loadContextObject(request, nextPos, byteArrSize);
          } else {
            //Jump the position of this context object in byte array, it is forbidden for loading
            nextPos = nextPos + byteArrSize;
          }
          currentCO++;
        }
      }
    }


    switch (type) {
      case CALL_REQUEST: {
        redirFlag = request[nextPos++];
        if (redirFlag == 1) {

          /* old redirectable */
          isRedir = true;
          nameSize = request[nextPos++];
          namePos = nextPos;
          ident = Convert.byteArrToUString(request, nextPos, nameSize);
          nextPos += 2 * nameSize;
        } else if (redirFlag == 2) {

          /* extended redirectable */
          isRedir = true;
          factorySize = (request[nextPos++] & 0x00ff) | (request[nextPos++] << 8);
          factoryPos = nextPos;
          factoryName = Convert.byteArrToUString(request, nextPos, factorySize);
          nextPos += 2 * factorySize;

          idObjSize = (request[nextPos++] & 0x00ff) | (request[nextPos++] << 8);
          byte[] objInBytes = new byte[idObjSize];
          System.arraycopy(request, nextPos, objInBytes, 0, idObjSize);
          CrossObjectFactory factoryInstance = ((CrossInterface) P4ObjectBroker.getBroker().getCrossInterface()).getObjectFactory(factoryName);
          if (factoryInstance == null) {
            P4Logger.getLocation().debugT("Message.parseMessage() the factory:" + factoryName + " does not already exist on this server node.");
          } else {
            ClassLoader loader = factoryInstance.getClass().getClassLoader();
            objIdentity = raiseObject(objInBytes, loader);
          }
          idObjPos = nextPos;
          nextPos += idObjSize;

        }

        int opNameSize = (request[nextPos++] & 0x00ff) | (request[nextPos++] << 8);
        opName = Convert.byteArrToUString(request, nextPos, opNameSize);
        nextPos += 2 * opNameSize;
        objectKey = new byte[OBJECT_KEY_SIZE];
        System.arraycopy(request, nextPos, objectKey, 0, objectKey.length);
        nextPos += OBJECT_KEY_SIZE;
        bais = new ByteArrayInputStream(request, nextPos, size - nextPos);

        break;
      }
      case DISPATCH_REPLY: {
        bais = new ByteArrayInputStream(request, nextPos, size - nextPos);
        break;
      }
      case DISPATCH_ERROR_REPLY: {
        bais = new ByteArrayInputStream(request, nextPos, size - nextPos);
        break;
      }
      case SPECIAL_ERROR_MESSAGE: {
        nextPos += 3; //skip not used 3 bytes
        if (size < (nextPos + 4)) { //backward compatibility new client with old ICM
          byte[] errorWrapper = new byte[4];
          Convert.writeIntToByteArr(errorWrapper, 0, INTERNAL_ERROR);
          bais = new ByteArrayInputStream(errorWrapper, 0, 4);
        } else {
          bais = new ByteArrayInputStream(request, nextPos, size - nextPos);
        }
        break;
      }
      case RESOLVE_INITIAL_REFERENCE:
      case RESOLVE_INITIAL_REFERENCE_REPLY: {
        unmarshaledRequest = new byte[size - 9 - offBody];
        System.arraycopy(request, 9 + offBody, unmarshaledRequest, 0, unmarshaledRequest.length);
        break;
      }
      case INFORM_MESSAGE: {
        unmarshaledRequest = new byte[size - 9 - offBody];
        System.arraycopy(request, 9 + offBody, unmarshaledRequest, 0, unmarshaledRequest.length);
        break;
      }
      case Message.GET_CONTEXT_OBJECT: {
        byte[] req = request;
        P4ObjectBroker p4 = P4ObjectBroker.getBroker();
        String name_CO = Convert.byteArrToAString(req, (ProtocolHeader.HEADER_SIZE + 9), (req.length - ProtocolHeader.HEADER_SIZE - 9));
        Object contextObj = p4.getCTC().getContextObject(name_CO);
        byte[] rep = null; //contextObj.getClass().getName().getBytes();
        String name = Convert.byteArrToUString(getUnmarshaledRequest());
        rep = new byte[contextObj.getClass().getName().getBytes().length + 9];
        System.arraycopy(call_id, 0, rep, 0, 8);
        rep[8] = 31;
        System.arraycopy(contextObj.getClass().getName().getBytes(), 0, rep, 9, contextObj.getClass().getName().getBytes().length);
        break;
      }
      case Message.GET_CONTEXT_OBJECT_REPLY: {
        Message.obj.setInfo(request);
        Message.obj.notify();
      }
    }

    parsed = true;
  }

  public Serializable raiseObject(byte[] bb, ClassLoader loader) {
    ByteArrayInputStream bin = new ByteArrayInputStream(bb);
    try {
      MarshalInputStream min = new MarshalInputStream(bin);
      min.setClassLoader(loader);
      return (Serializable) min.readObject();
    } catch (IOException e) {
      P4Logger.getLocation().debugT(this.getClass() + ".raiseObject(). The object cannot be deserialized: " + e.getMessage());
      P4Logger.getLocation().debugT(P4Logger.exceptionTrace(e));
    } catch (ClassNotFoundException e) {
      P4Logger.getLocation().debugT(this.getClass() + ".raiseObject(). The object cannot be deserialized: " + e.getMessage());
      P4Logger.getLocation().debugT(P4Logger.exceptionTrace(e));
    }
    return null;
  }

  public ByteArrayInputStream getByteArrayInputStream()  {
    if (!parsed) {
      parseMessage();
    }

    return bais;
  }

  public int readShortToInt(byte[] buf, int off) {
    return (buf[off] & 0x00ff) | (buf[off++] << 8);
  }

  public String readString(byte[] buf, int off, int len) {
    String s = null;
    try {
      s = com.sap.engine.lib.lang.Convert.byteArrToAString(buf, off, len);
    } catch (Exception ex) {
      if (P4Logger.getLocation().beDebug()) {
        P4Logger.getLocation().debugT("Message.readString(byte[], int, int)", P4Logger.exceptionTrace(ex));
      }
    }
    return s; //new String(buf,off,len);
  }

  public void loadContextObject(byte[] buf, int off, int skip) {
    ClientThreadContext ctc = P4ObjectBroker.getBroker().getCTC();
    if (ctc != null) {
      ContextObject co = ctc.getContextObject(name);
      if (co != null) {
        if (skip > 0) {
          try {
            //name.equals(P4ObjectBroker.SECURITY_CO) && // It used to allow only security to use such objects. 
            if ( ((this.type == DISPATCH_REPLY) || (this.type == RESOLVE_INITIAL_REFERENCE_REPLY) || (this.type == DISPATCH_ERROR_REPLY)) && (co instanceof TransferableExt)) {
              ((TransferableExt) co).load(this.info.ownerId, buf, off);
            } else {
              ((Transferable) co).load(buf, off);
            }
          } catch (Throwable e) {
            if(P4Logger.getSecLocation().bePath()){
              P4Logger.getSecLocation().pathT("Message.loadContextObject(byte[], int, int)", "Cannot load Transferable context objects " + P4Logger.exceptionTrace(e));
            } else {
              if(P4Logger.getLocation().bePath()){
                P4Logger.getLocation().pathT("Message.loadContextObject(byte[], int, int)", "Cannot load Transferable context objects " + P4Logger.exceptionTrace(e));
              }  
            }
          }
          nextPos = nextPos + skip;
        }
      } else {
        try {
          if (name.equalsIgnoreCase(P4ContextObject.NAME)){
            p4co = (new P4ContextObject());
            p4co.load(buf, off);
            nextPos = nextPos + skip;
            //P4ObjectBroker.init().getCTC().setContextObject(name, co);
          } else {
            if (!P4ObjectBroker.getBroker().isServerBroker()) {
              //If getting context object from other stand alone client it will return to us special error message instead of cocr object. 
              try{
                setContextObject(con.type, name, con.host, con.port, sender_id);
                co = P4ObjectBroker.getBroker().getCTC().getContextObject(name);
              }catch (P4RuntimeException e) {
                if(P4Logger.getSecLocation().bePath()){
                  P4Logger.getSecLocation().pathT("Message.loadContextObject(byte[], int, int)", "Cannot load Transferable Context Objects " + P4Logger.exceptionTrace(e));
                } else {
                  if(P4Logger.getLocation().bePath()){
                    P4Logger.getLocation().pathT("Message.loadContextObject(byte[], int, int)", "Cannot load Transferable Context Objects " + P4Logger.exceptionTrace(e));
                  } 
                }
              }
              if (co != null) {
                try {
                  //name.equals(P4ObjectBroker.SECURITY_CO) && 
                  if ( ((this.type == DISPATCH_REPLY) || (this.type == RESOLVE_INITIAL_REFERENCE_REPLY) || (this.type == DISPATCH_ERROR_REPLY)) && (co instanceof TransferableExt)) {
                    ((TransferableExt) co).load(this.info.ownerId, buf, off);
                  } else {
                    ((Transferable) co).load(buf, off);
                  }
                } catch (Throwable e) {
                  if(P4Logger.getSecLocation().bePath()){
                    P4Logger.getSecLocation().pathT("Message.loadContextObject(byte[], int, int)", "Cannot load Context Objects " + P4Logger.exceptionTrace(e));
                  } else {
                    if(P4Logger.getLocation().bePath()){
                      P4Logger.getLocation().pathT("Message.loadContextObject(byte[], int, int)", "Cannot load Context Objects " + P4Logger.exceptionTrace(e));
                    } 
                  }
                }
              }
            }
            nextPos = nextPos + skip;
          }
        } catch (Exception _) {
          if (P4Logger.getSecLocation().beDebug()) {
            P4Logger.getSecLocation().debugT("Message.loadContextObject(byte[], int, int)", P4Logger.exceptionTrace(_));
          } else {
            if (P4Logger.getLocation().beDebug()) {
              P4Logger.getLocation().debugT("Message.loadContextObject(byte[], int, int)", P4Logger.exceptionTrace(_));
            }
          }
          nextPos = nextPos + skip;
        }

      }
    } else {
      nextPos = nextPos + skip;
      if (P4Logger.getLocation().beDebug()) {
        P4Logger.getLocation().debugT("Message.loadContextObject(byte[], int, int)", "ClientThreadContext was null, context object will be skipped");
      }
    }
  }

  public static String toString(byte[] bytes, int begin, int length) {
    String hex = "0123456789ABCDEF";
    if (bytes == null) return "";
    StringBuffer sb = new StringBuffer();
    length = Math.min(length, bytes.length);
    int end = begin + length;
    for (int c = begin; c < end; c += 16) {
      int count = 16;
      StringBuffer text = new StringBuffer();
      for (int j = c; --count >= 0 && j < end; j++) {
        int charAsInt = ((int) bytes[j]) & 0x00FF;
          sb.append(" ").append(hex.charAt(charAsInt >> 4)).append(hex.charAt(charAsInt & 0x000F));
        char ch = charAsInt > 31 ? (char) charAsInt : '.';
        text.append(ch);
      }
      for (; --count >= -2;) sb.append("   ");
      sb.append(text);
      if (c < end - 16) sb.append("\r\n");
    }
    return sb.toString();
  }

  public String toString() {
    StringBuffer result = new StringBuffer(headerToString());
    result.append("\r\n   | body : \r\n");
    result.append(toString(request, 0, size));
    return result.toString();
  }

  public String headerToString() {
    StringBuffer result = new StringBuffer("P4 Message: ");
    result.append(super.toString());
    result.append("\r\n   | size : ");
    result.append(size);
    result.append("\r\n   | type : ");
    result.append(getType());
    result.append("\r\n   | operation name : ");
    result.append(opName);
    result.append("\r\n   | call id: ");
    result.append(Convert.byteArrToLong(call_id, 0));
    result.append("\r\n   | sender id : ").append(sender_id).append(" | own id : ").append(own_id).append(" | client id : ").append(client_id).append(" | cluster element id : ").append(clusterEl_id);
    result.append("\r\n   | ident : ").append(ident).append(" | name : ").append(name).append(" | ssid : ").append(ssid);
    return result.toString();
  }
  private synchronized static void setContextObject(String _type, String name, String host, int port, int server_id) {
    if (wait) {
      return;
    }
    wait = true;
    try {
      ContextObject co = P4ObjectBroker.getBroker().getCTC().getContextObject(name);
      if (co == null) {
        ContextObjectClassReceiver oo = (ContextObjectClassReceiver) P4ObjectBroker.getBroker().narrow(P4ObjectBroker.getBroker().resolveInitialReference(_type, "cocr", host, port, server_id), ContextObjectClassReceiver.class, _type);
        if (name.length() > 1) {
          Class c = oo.getClassByName(name);
          if (c != null) {
            co = ((ContextObject) (c.newInstance())).getInitialValue();
            P4ObjectBroker.init().getCTC().setContextObject(name, co);
          }
        }
      }
    } catch (Exception ex) {
      if (P4Logger.getLocation().beError() || P4Logger.getSecLocation().beError()) {
        Location loc;
        if (P4Logger.getSecLocation().beWarning()) {
          loc = P4Logger.getSecLocation();
        } else {
          loc = P4Logger.getLocation();
        }
        P4Logger.trace(P4Logger.ERROR, loc, "Message.setContextObject(byte[], int, int)", "Failed to set context object {0}; it will not be set to the message. \r\nException: {1}", "ASJ.rmip4.rt2008", new Object []{name, P4Logger.exceptionTrace(ex)});
      }
      throw new P4RuntimeException("Failed to set context object " + name, ex);
    } finally {
      wait = false;
    }
  }

  public void setInfo(StubBaseInfo info) {
    this.info = info;
  }

  public StubBaseInfo getInfo() {
    return info;
  }

  public String getFactoryName() {
    return factoryName;
  }

  public Serializable getObjIdentity() {
    return objIdentity;
  }

  public void setIdent(String ident) {
    this.ident = ident;
  }

  public void setRedir(boolean redir) {
    isRedir = redir;
  }

  public void setFactoryName(String factoryName) {
    this.factoryName = factoryName;
  }

  public void setObjIdentity(Serializable objIdentity) {
    this.objIdentity = objIdentity;
  }

  public void setRedirFlag(int redirFlag) {
    this.redirFlag = redirFlag;
  }

  public void setNamePos(int namePos) {
    this.namePos = namePos;
  }

  public void setNameSize(int nameSize) {
    this.nameSize = nameSize;
  }

  public void setFactoryPos(int factoryPos) {
    this.factoryPos = factoryPos;
  }

  public void setFactorySize(int factorySize) {
    this.factorySize = factorySize;
  }

  public void setIdObjPos(int idObjPos) {
    this.idObjPos = idObjPos;
  }

  public void setIdObjSize(int idObjSize) {
    this.idObjSize = idObjSize;
  }

  public boolean isRedir() {
    return isRedir;
  }

  public String getIdent() {
    return ident;
  }

  public int getRedirFlag() {
    return redirFlag;
  }

  public int getNamePos() {
    return namePos;
  }

  public int getNameSize() {
    return nameSize;
  }

  public int getFactoryPos() {
    return factoryPos;
  }

  public int getFactorySize() {
    return factorySize;
  }

  public int getIdObjPos() {
    return idObjPos;
  }

  public int getIdObjSize() {
    return idObjSize;
  }

  public String getType() {
    return getType(type);
  }

  public static String getType(int type) {
    String msgType = null;
    switch (type) {
      case Message.CALL_REQUEST: msgType = "REQUEST";break;
      case Message.DISPATCH_ERROR_REPLY: msgType = "ERROR REPLY";break;
      case Message.DISPATCH_REPLY: msgType = "REPLY";break;
      case Message.GET_CONTEXT_OBJECT: msgType = "GET CONTEXT OBJECT REQUEST";break;
      case Message.GET_CONTEXT_OBJECT_REPLY: msgType = "GET CONTEXT OBJECT REPLY";break;
      case Message.INFORM_MESSAGE: msgType = "INFORM";break;
      case Message.PING_CONNECTION_MESSAGE: msgType = "PING";break;
      case Message.PING_CONNECTION_MESSAGE_SERVER: msgType = "PING SERVER";break;
      case Message.RESOLVE_INITIAL_REFERENCE: msgType = "RESOLVE INITIAL REFERENCE REQUEST";break;
      case Message.RESOLVE_INITIAL_REFERENCE_REPLY: msgType = "RESOLVE INITIAL REFERENCE REPLY";break;
      case Message.SPECIAL_ERROR_MESSAGE: msgType = "SPECIAL ERROR MESSAGE";break;
      default : msgType = "Unknown message type";
    }
    return msgType;
  }

  public String getSpecialErrorMessage(int errorCode) {
    String msgType = null;
    switch (errorCode) {
      case Message.OK: msgType = "no error information";break;
      case Message.INTERNAL_ERROR: msgType = "unspecific error";break;
      case Message.NOSERVER_AVAIL: msgType = "no server is available for balancable requests";break;
      case Message.SERVER_DOWN: msgType = "the requested server is not available";break;
      case Message.REQUEST_TOO_BIG: msgType = "request message was too big";break;
      case Message.SERVER_CRASH: msgType = "server crashed while processing the request";break;
      case Message.PROTOCOL_ERROR: msgType = "client caused a protocol error (magic, P4 version error, etc)";break;
      default : msgType = "Unknown message type";
    }
    return msgType;
  }

  public void process() {
    //not used
  }

  public int getLength() {
    return size;
  }

  public byte[] getData() {
    return request;
  }

  public com.sap.engine.interfaces.cross.Connection getConnection() {
    return null; //not used
  }

  public void setData(byte[] data, int length) {
    //not used
  }

  public void setConnection(com.sap.engine.interfaces.cross.Connection connection) {
    // not used;
  }

  public void release() {
    //not used;
  }

  public void execute() {
    //not used
  }

  public int getProtocol() {
    return MessageProcessor.P4_PROCESSOR;
  }

}


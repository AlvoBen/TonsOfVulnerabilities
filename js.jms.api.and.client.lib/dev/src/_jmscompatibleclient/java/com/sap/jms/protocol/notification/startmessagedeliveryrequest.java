package com.sap.jms.protocol.notification;

import javax.jms.JMSException;

import com.sap.jms.protocol.BufferUnderflowException;
import com.sap.jms.protocol.PacketImpl;
import com.sap.jms.protocol.PacketTypes;
import com.sap.jms.protocol.PacketWithConsumerID;
import com.sap.jms.protocol.PacketWithDestinationID;

/**
 * Used by consumers to signal that they are ready to consume messages.
 * @author Margarit Kirov
 * @version 6.30
 */
public class StartMessageDeliveryRequest
	extends PacketImpl
	implements PacketWithDestinationID, PacketWithConsumerID {

  /** The ID for this packet. */
  public static final byte TYPE_ID = START_MESSAGE_DELIVERY_REQUEST;
  
  /* Position of the destinatio id relative to the start of the payload area */
  private static final int POS_DESTINATION_ID = 0; 
  private static final int POS_CONSUMER_ID = POS_DESTINATION_ID + SIZEOF_INT; 
  private static final int POS_CONSUMER_LIMIT = POS_CONSUMER_ID + SIZEOF_LONG;
  private static final int POS_NUMBER_CONSUMED_MESSAGES = POS_CONSUMER_LIMIT + SIZEOF_INT;  
  protected static final int SIZE = POS_NUMBER_CONSUMED_MESSAGES + SIZEOF_INT;
  
  public StartMessageDeliveryRequest() {}
          
  /**
   * Constructor for StartMessageDeliveryRequest.
   * @param destinationID id of the consumer destination
   * @param consumerID id of the consumer issuing the request
   * @param consumerLimit the size of the memory allocated for the consumer
   * @exception JMSException if something went wrong
   */ 
  public StartMessageDeliveryRequest(int destinationID, long consumerID, int consumerLimit) throws JMSException
  {
    this(destinationID,consumerID,consumerLimit,0);
  }
  
  /**
   * Constructor for StartMessageDeliveryRequest.
   * @param destinationID id of the consumer destination
   * @param consumerID id of the consumer issuing the request
   * @param consumerLimit the size of the memory allocated for the consumer
   * @param numberConsumedMessages the number of consumed messages that must be acknowledged from the server
   * @exception JMSException if something went wrong
   */ 
  public StartMessageDeliveryRequest(int destinationID, long consumerID, int consumerLimit, int numberConsumedMessages) throws JMSException
  {
    super(TYPE_ID, SIZE);
    setInt(POS_DESTINATION_ID, destinationID);
    setLong(POS_CONSUMER_ID, consumerID);
    setInt(POS_CONSUMER_LIMIT, consumerLimit);
    setInt(POS_NUMBER_CONSUMED_MESSAGES, numberConsumedMessages);
  }  

  public int getDestinationID() throws BufferUnderflowException {
    return getInt(POS_DESTINATION_ID);
  }
  
  public long getConsumerID() throws BufferUnderflowException {
    return getLong(POS_CONSUMER_ID);
  }
  
  public int getConsumerLimit() throws BufferUnderflowException {
    return getInt(POS_CONSUMER_LIMIT);
  }
  
  public int getNumberConsumedMessages() throws BufferUnderflowException {
    return getInt(POS_NUMBER_CONSUMED_MESSAGES);
  }  

	public int getExpectedResponsePacketType() {
		return PacketTypes.START_MESSAGE_DELIVERY_RESPONSE;
	}
}

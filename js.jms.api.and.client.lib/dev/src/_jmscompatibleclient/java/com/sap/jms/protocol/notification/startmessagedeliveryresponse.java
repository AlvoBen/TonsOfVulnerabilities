package com.sap.jms.protocol.notification;

import com.sap.jms.protocol.PacketImpl;

/**
 * @author Margarit Kirov
 * @version 6.30
 */
public class StartMessageDeliveryResponse
  extends PacketImpl {

  /** The ID for this packet. */
  public static final byte TYPE_ID = START_MESSAGE_DELIVERY_RESPONSE;
  
  /**
   * Constructor for StartMessageDeliveryResponse.
   */
  public StartMessageDeliveryResponse() {
    super(TYPE_ID);
  }
}

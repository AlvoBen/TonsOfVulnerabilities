/**
 * MessageAcknowledgeRequest.java
 *
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2003.
 * All rights reserved.
 */
package com.sap.jms.protocol.notification;

import javax.jms.JMSException;

import com.sap.jms.protocol.PacketWithConsumerIDsAndMessageCounts;
import com.sap.jms.protocol.PacketWithSessionIDImpl;

import com.sap.jms.util.compat.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author  Dr. Bernd Follmeg
 * @version 6.30
 */
public class RequestWithMessageID extends PacketWithSessionIDImpl implements PacketWithConsumerIDsAndMessageCounts {

    private static final int POS_CONSUMER_IDS = POS_SESSION_ID + SIZEOF_INT;
    private static final int SIZE = POS_CONSUMER_IDS;

    // keep for newInstance() to work
    public RequestWithMessageID() {
    }

    /**
     * Constructor for MessageAcknowledgeRequest
     * @param session_ids the ID of the session the message is a associated with
     * @param msgIds mapping between consumer_ids and pcounters of messages that should be acknowledged
     * @exception JMSException thrown if something went wrong
     */

    public RequestWithMessageID(byte packet_type, int session_id, Map/*<Long, Set<Long>>*/ msgIdsPerConsumer) throws JMSException {
    	// int 		long	...	int 	int	...	long ...
    	// count    consId      count   msgCount		pcntr
    	super(packet_type, SIZE + (2 * SIZEOF_INT) , session_id);

    	Set/*<Long>*/ consumerIds = msgIdsPerConsumer.keySet();
    	int count = consumerIds.size();
    	
    	int npcounters = 0;
    	
    	for (Iterator i = consumerIds.iterator(); i.hasNext(); ) {
    		Long consumerId = (Long) i.next();
    		Set list = (Set)msgIdsPerConsumer.get(consumerId);
    		npcounters += list.size();
    	}

    	allocate(packet_type, SIZE + (2 * SIZEOF_INT) + (2 * count * SIZEOF_LONG) + (npcounters * SIZEOF_LONG));
        
    	setPosition(POS_CONSUMER_IDS);
        
    	writeInt(count);
    	for (Iterator i = consumerIds.iterator(); i.hasNext(); ) {    	
    		Long consumerId = (Long) i.next();
    		writeLong(consumerId.longValue());
    	}
    	
    	writeInt(count);
    	for (Iterator i = consumerIds.iterator(); i.hasNext(); ) {    	
    		Long consumerId = (Long) i.next(); 
    		Set list = (Set) msgIdsPerConsumer.get(consumerId); 
    		writeInt(list.size());
    	}
    	
    	for (Iterator i = consumerIds.iterator(); i.hasNext(); ) {
    		Long consumerId = (Long) i.next();    		
    		Set pcounters = (Set) msgIdsPerConsumer.get(consumerId);
    		for (Iterator j = pcounters.iterator(); j.hasNext(); ) {
    			Long pcounter = (Long) j.next();    			
    			// ignore pcounters which are not >= 1
    			// could happen if the pcounter property was not 
    			// present in the message header
    			// in this case ack by count will be used
    			if (pcounter.longValue() == 0) {
    				return;
    			}
    			writeLong(pcounter.longValue());
    		}
    	}
    }
    
    
    public Map/*<Long, List<Long>>*/ getDeliveredMessages() throws JMSException {
        setPosition(POS_CONSUMER_IDS);
        
        int count = readInt();   
    	Map/*<Long, List<Long>>*/ deliveredMessages = new HashMap/*<Long, List<Long>>*/(count);

    	int pCountersOffset = 0;
    	for (int i = 0; i < count; i++) {
    		// current consumerId 
    		setPosition(POS_CONSUMER_IDS + SIZEOF_INT + i*SIZEOF_LONG);
    		long consumerId = readLong();

    		// current pcounters count
    		setPosition(POS_CONSUMER_IDS + 2*SIZEOF_INT + count*SIZEOF_LONG + i*SIZEOF_INT);

    		// get number of pCounters for this consumer in stream
    		int pCountersCount = readInt();
    		

    		// current start of pcounter sequence
    		setPosition(POS_CONSUMER_IDS + 2*SIZEOF_INT + count*SIZEOF_LONG + count*SIZEOF_INT + pCountersOffset*SIZEOF_LONG);
    		List/*<Long>*/ pCounters = new ArrayList/*<Long>*/();  

    		for (long pCounter = 0; pCounter < pCountersCount; pCounter++) {
    			pCounters.add(new Long(readLong()));
    		}
    		
    		deliveredMessages.put(new Long(consumerId), pCounters);
    		
    		pCountersOffset += pCountersCount;
    	}

    	return deliveredMessages;
    }
    
    public boolean containsIds() throws JMSException {
        setPosition(POS_CONSUMER_IDS);
        int count = readInt();   
    	if (getLength() > POS_CONSUMER_IDS + 2*SIZEOF_INT + count*SIZEOF_LONG + count*SIZEOF_INT) {
    		return true;
    	}
    	
    	return false;
    }
        
    /**
     * Method getConsumerIDs. Returns the consumer IDs as an array of long.
     * @return long[] array of consumer IDs
     * @throws JMSException thrown when an error occurs
     */
    public long[] getConsumerIDs() throws JMSException {
        setPosition(POS_CONSUMER_IDS);

        long[] consumer_ids = new long[readInt()];
        for (int i = 0; i < consumer_ids.length; i++) {
            consumer_ids[i] = readLong();
        } //for

        return consumer_ids;
    } //getConsumerIDs

    /**
     * Method getMessageCounts. Returns the messages counts as an array of int.
     * @return int[] the messages counts
     * @throws JMSException thrown when an error occurs
     */
    public int[] getMessageCounts() throws JMSException {

        //-----------------------------------------------------------------------
        // Get the number of consumer ids and compute offset
        // for message count array
        //-----------------------------------------------------------------------
        setPosition(POS_CONSUMER_IDS);
        setPosition(POS_CONSUMER_IDS + readInt() * SIZEOF_LONG + SIZEOF_INT);

        int[] message_counts = new int[readInt()];
        for (int i = 0; i < message_counts.length; i++) {
            message_counts[i] = readInt();
        } //for

        return message_counts;
    } //getMessageCounts

    /**
     *  Returns a string representation of the packet
     *  @param out to writer to use to print the packet
     */
    protected void toString(PrintWriter out) throws Exception {
        super.toString(out);
        //----------------------------------------------------------------
        // Print message content
        //----------------------------------------------------------------
        long[] array = getConsumerIDs();
        int[] message_counts = getMessageCounts();

        for (int j = 0; j < message_counts.length; j++) {
            out.printf("%14s %d %13s %5d\n", new Object[] {
            	"MessageCounts(", new Integer(j + 1), "):", new Integer(message_counts[j])});
        } 

        for (int i = 0; i < array.length; i++) {
            out.printf("%7s %d  %15s %5x\n", new Object[] {
            	"ConsumerID(", new Integer(i + 1), "):", new Long(array[i])});
        } 
    } 
}

/**
 * XAEndRequest.java
 *
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2002.
 * All rights reserved.
 */
package com.sap.jms.protocol.notification;

import javax.jms.JMSException;
import javax.transaction.xa.Xid;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.sap.jms.protocol.PacketTypes;

public class XAEndRequest extends XABasePacket {

	public static final byte TYPE_ID = XA_END_REQUEST;

	public XAEndRequest(Xid xid, int flags,	Map/*<Long, Set<Long>>*/ msgsPerConsumerIds) throws JMSException {
		super(TYPE_ID, xid, flags);

		Set/*<Long>*/ consumerIds = msgsPerConsumerIds.keySet();
		int nconsumers = consumerIds.size();

		int npcounters = 0;

		for (Iterator i = consumerIds.iterator(); i.hasNext(); ) {
			Long consumerId = (Long) i.next();
			npcounters += ((Set)msgsPerConsumerIds.get(consumerId)).size();
		}

		int pos = POS_XID;
	    pos += (SIZEOF_INT + xid.getGlobalTransactionId().length);
	    pos += (SIZEOF_INT + xid.getBranchQualifier().length);
	    // format
	    pos+= (SIZEOF_INT);
	    
		// ...                    elements count ... consumerIds  + pcounter counts ... pcounters
		allocate(TYPE_ID, pos + (2 * SIZEOF_INT) + (2 * nconsumers * SIZEOF_LONG) + (npcounters * SIZEOF_LONG));

		setPosition(pos);
		
		// consumers count
		writeInt(nconsumers);
		// consumer ids
		for (Iterator i = consumerIds.iterator(); i.hasNext(); ) {
			Long consumerId = (Long) i.next();
			writeLong(consumerId.longValue()); 
		}
		
		// consumers count
		writeInt(nconsumers); 
		// number of messages per consumer
		for (Iterator i = consumerIds.iterator(); i.hasNext(); ) {
			Long consumerId = (Long) i.next();
			writeInt(((Set)msgsPerConsumerIds.get(consumerId)).size()); 
		}
		
		// pcounters
		for (Iterator i = consumerIds.iterator(); i.hasNext(); ) {
			Long consumerId = (Long) i.next();
			Set/*<Long>*/ pcounters = (Set)msgsPerConsumerIds.get(consumerId); 
			for (Iterator j = pcounters.iterator(); i.hasNext(); ) {
				Long pcounter = (Long) j.next();
				// ignore pcounters which are not >= 1 
				// could happen if the pcounter	property was not 
				if (pcounter.longValue() == 0) { 
					throw new JMSException("invalid pcounter found while trying to serialise XAEndRequest");
				} 
				
				writeLong(pcounter.longValue()); 
				} 
			} 
		}
		

	public Map/*<Long, Set<Long>>*/ getMsgsPerConsumerIds() throws JMSException {
		Xid xid = getXID();

		int pos = POS_XID;
		pos += (SIZEOF_INT + xid.getGlobalTransactionId().length);
		pos += (SIZEOF_INT + xid.getBranchQualifier().length);
		// format
		pos+= (SIZEOF_INT);

		setPosition(pos);

		int count = readInt();   
		Map/*<Long, Set<Long>>*/ msgsPerConsumerIds = new HashMap/*<Long, Set<Long>>*/(count);

		int pCountersOffset = 0;
		for (int i = 0; i < count; i++) {
			// current consumerId 
			setPosition(pos + SIZEOF_INT + i*SIZEOF_LONG);
			long consumerId = readLong();

			// current pcounters count
			setPosition(pos + 2*SIZEOF_INT + count*SIZEOF_LONG + i*SIZEOF_INT);

			// get number of pCounters for this consumer in stream
			int pCountersCount = readInt();

			// current start of pcounter sequence
			setPosition(pos + 2*SIZEOF_INT + count*SIZEOF_LONG + count*SIZEOF_INT + pCountersOffset*SIZEOF_LONG);
			Set/*<Long>*/ pCounters = new HashSet/*<Long>*/();  

			for (long pCounter = 0; pCounter < pCountersCount; pCounter++) {
				pCounters.add(new Long(readLong()));
			}

			msgsPerConsumerIds.put(new Long(consumerId), pCounters);

			pCountersOffset += pCountersCount;
		}

		return msgsPerConsumerIds;

	}
	
	public XAEndRequest() {
		// nothing here
	}
	
	public int getExpectedResponsePacketType() {
		return PacketTypes.XA_RESPONSE;
	}
}

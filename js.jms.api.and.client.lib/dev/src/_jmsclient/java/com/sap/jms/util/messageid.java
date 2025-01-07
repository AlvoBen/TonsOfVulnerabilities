/**
 * MessageID.java
 *
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2003.
 * All rights reserved.
 */
package com.sap.jms.util;

import com.sap.jms.protocol.Packet;

/**
 * @author  Dr. Bernd Follmeg
 * @version 6.30
 */
public class MessageID {

    static final char[] HEXCHARS = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
    static final String HEXSTRING = "0123456789ABCDEFabcdef";

    protected final static String toHexString(byte[] value) {
        //char s[] = new char[value.length * 2 + (value.length - 1) / 2];
        char s[] = new char[value.length * 2 + 1];
        for (int i = 0, k = 0; i < value.length; i++) {
            s[k++] = HEXCHARS[(value[i] >> 4) & 0x0F];
            s[k++] = HEXCHARS[value[i] & 0x0F];
            /*
            if ((i % 2) == 0 && i > 0 && i < (value.length - 1))
                s[k++] = '-';
       		*/
       		if (k==Packet.SIZEOF_LONG*2) {
       			s[k++]='-';
       		} //if
        } //for
        return new String(s);
    } //toHexString

    protected final static byte[] fromHexString(String value) {

        if (value.startsWith("ID:")) {
            value = value.substring(3);
        } //if

        char s[] = value.toCharArray();
        int l = 0; byte d;
        for (int i = 0; i < s.length; i++) {
            if (HEXSTRING.indexOf(s[i]) >= 0) {
                s[l++] = s[i];
            } //if
        } //for 
        byte b[] = new byte[(l+1)/2];
        for (int i = 0; i < l; i++) {
             d = (byte)Character.digit(s[i], 16);
             b[(i >> 1)] |= (d << ((1 - (i & 1)) << 2));
        }//for        
        return b;
    } //fromHexString

    /**
     *  Generates a unique message ID
     *  @param messageIDBase the unique base id
     *  @param the message counter
     *  @return the messageID as a byte array 
     */
    public static final byte[] toBytes(long messageIDBase, long messageIDCounter) {
        byte[] msgid = new byte[Packet.SIZEOF_MESSAGE_ID];
        int position = 0;
        msgid[position++] = (byte) ((messageIDBase >> 56) & 0xFF);
        msgid[position++] = (byte) ((messageIDBase >> 48) & 0xFF);
        msgid[position++] = (byte) ((messageIDBase >> 40) & 0xFF);
        msgid[position++] = (byte) ((messageIDBase >> 32) & 0xFF);
        msgid[position++] = (byte) ((messageIDBase >> 24) & 0xFF);
        msgid[position++] = (byte) ((messageIDBase >> 16) & 0xFF);
        msgid[position++] = (byte) ((messageIDBase >> 8) & 0xFF);
        msgid[position++] = (byte) ((messageIDBase & 0xFF));
        msgid[position++] = (byte) ((messageIDCounter >> 40) & 0xFF);
        msgid[position++] = (byte) ((messageIDCounter >> 32) & 0xFF);
        msgid[position++] = (byte) ((messageIDCounter >> 24) & 0xFF);
        msgid[position++] = (byte) ((messageIDCounter >> 16) & 0xFF);
        msgid[position++] = (byte) ((messageIDCounter >> 8) & 0xFF);
        msgid[position++] = (byte) ((messageIDCounter & 0xFF));
        return msgid;
    } //toBytes

    /**
     *  Generates a unique message ID
     *  @param messageIDBase the unique base id
     *  @param the message counter
     *  @return the messageID as a byte array 
     */
    public static final byte[] toBytes(String messageID) {
		byte[] msgid = fromHexString(messageID);
		if (msgid == null || msgid.length != Packet.SIZEOF_MESSAGE_ID) {
			 throw new IllegalArgumentException();
		} //if    
		return msgid;
    } //toBytes    

    /**
     *  Generates a unique message ID
     *  @param messageIDBase the unique base id
     *  @param the message counter
     *  @return tne messageID as a string
     */
    public static final String toString(long messageIDBase, long messageIDCounter) {
        return toHexString(toBytes(messageIDBase, messageIDCounter));
    } //encodeString

    /**
     *  Converts the binary representation of the messageID to a string
     */
    public static final String toString(byte[] messageID) {
        return toHexString(messageID);
    }

    /**
     * Constructor for MessageID.
     */
    private MessageID() {
        super();
    } //MessageID

}

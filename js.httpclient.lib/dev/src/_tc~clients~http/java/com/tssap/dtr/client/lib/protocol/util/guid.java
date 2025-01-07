package com.tssap.dtr.client.lib.protocol.util;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Random;

import com.sap.tc.logging.Location;

/**
 * GUID generation and conversion.
 * Note, this class does only support a <li> time-based<br> algorithm for
 * generating GUIDs, i.e. a combination of the IEEE 802 network address, timestamp,
 * and small random number.
 */
public class GUID {

	/** lower 8 bytes */
	private long replow;

	/** higher 8 bytes */
	private long rephigh;

	/** ieee802 network node address */
	private static long networkAddress;

	/** last time of generation */
	private static long ltime = System.currentTimeMillis();

	/** number of generatation within millisecond */
	private static short lcount;

	/** random number generator */
	private static Random rand = new Random(System.currentTimeMillis());

	/** class lock **/
	private static Object classLock = new Object();

	private static final char[] hexDigits =
		{ '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
		
	/** trace location*/
	private static Location TRACE = Location.getLocation(GUID.class);		

	static {
		networkAddress = getIEEEAddress();
	}



	/**
	 * Constructs a new GUID: The value of the GUID is a combination of the 
	 * IEEE 802 network address, timestamp, and small random number.
	 */
	public GUID() {
		generateGUID();
	}

	/**
	 * Constructs a new GUID from the specified byte array.
	 * @param g the byte array to be used
	 * @exception  IllegalArgumentException  if the format is invalid
	 */
	public GUID(byte[] g) throws IllegalArgumentException {
		if (g.length < 16)
			throw new IllegalArgumentException("GUID to short");

		try {
			// this implementation is twice as fast !
			replow =
				((((long)g[15]) & 0xFF)
					+ ((((long)g[14]) & 0xFF) << 8)
					+ ((((long)g[13]) & 0xFF) << 16)
					+ ((((long)g[12]) & 0xFF) << 24)
					+ ((((long)g[11]) & 0xFF) << 32)
					+ ((((long)g[10]) & 0xFF) << 40)
					+ ((((long)g[9])  & 0xFF) << 48)
					+ ((((long)g[8])  & 0xFF) << 56));

			rephigh =
				((((long)g[7]) & 0xFF)
					+ ((((long)g[6]) & 0xFF) << 8)
					+ ((((long)g[5]) & 0xFF) << 16)
					+ ((((long)g[4]) & 0xFF) << 24)
					+ ((((long)g[3]) & 0xFF) << 32)
					+ ((((long)g[2]) & 0xFF) << 40)
					+ ((((long)g[1]) & 0xFF) << 48)
					+ ((((long)g[0]) & 0xFF) << 56));
		} catch (Exception e) {
			throw new IllegalArgumentException(e.getMessage());
		}
	}

	private GUID(long replow, long rephigh) {
		this.replow = replow;
		this.rephigh = rephigh;
	}

	/**
	 * Parses the string argument as a GUID string.
	 *
	 * @param guid   the string representation of the GUID.
	 * The parameter is parsed dependening on its length either as
	 * hexadezimal encoded (length 32), or IETF Draft encoded representation 
	 * of a GUID (length 36).
	 * @return GUID a GUID that corresponds to the specified value
	 * @exception  NumberFormatException  if the given guid string does not represent 
	 * a valid number
	 */
	public static GUID valueOf(String guid) throws NumberFormatException {
		GUID g = new GUID(0L, 0L);
		if (guid.length() == 36) {
			g.rephigh
				|= (Long.parseLong(guid.substring(0, 8), 16)
					<< 32 | Long.parseLong(guid.substring(9, 13), 16)
					<< 16 | Long.parseLong(guid.substring(14, 18), 16));
			g.replow
				|= (Long.parseLong(guid.substring(19, 23), 16)
					<< 48 | Long.parseLong(guid.substring(24, 36), 16));
		} else if (guid.length() == 32) {
			g.rephigh
				|= (Long.parseLong(guid.substring(0, 8), 16)
					<< 32 | Long.parseLong(guid.substring(8, 16), 16));
			g.replow
				|= (Long.parseLong(guid.substring(16, 24), 16)
					<< 32 | Long.parseLong(guid.substring(24, 32), 16));
		} else {
			throw new IllegalArgumentException("GUID too short");
		}
		return g;
	}

	/**
	 *  Creates a GUID from the byte array representation. The byte array must
	 *  be 16 bytes long.
	 * @return the GUID represented by this byte array
	 */
	public static GUID valueOf(byte[] b) throws IllegalArgumentException {
		return new GUID(b);
	}

	/**
	 * Returns the string representation of this GUID.
	 * @return String the string representation of GUID
	 */
	public String toString() {
		char[] buf = new char[36];

		int n = (int) (replow & 0xFFFFFFFF);
		buf[35] = hexDigits[n & 0x0F];
		buf[34] = hexDigits[(n >>> 4) & 0x0F];
		buf[33] = hexDigits[(n >>> 8) & 0x0F];
		buf[32] = hexDigits[(n >>> 12) & 0x0F];
		buf[31] = hexDigits[(n >>> 16) & 0x0F];
		buf[30] = hexDigits[(n >>> 20) & 0x0F];
		buf[29] = hexDigits[(n >>> 24) & 0x0F];
		buf[28] = hexDigits[(n >>> 28) & 0x0F];

		n = (int) ((replow >>> 32) & 0xFFFFFFFF);
		buf[27] = hexDigits[n & 0x0F];
		buf[26] = hexDigits[(n >>> 4) & 0x0F];
		buf[25] = hexDigits[(n >>> 8) & 0x0F];
		buf[24] = hexDigits[(n >>> 12) & 0x0F];
		buf[23] = '-';
		buf[22] = hexDigits[(n >>> 16) & 0x0F];
		buf[21] = hexDigits[(n >>> 20) & 0x0F];
		buf[20] = hexDigits[(n >>> 24) & 0x0F];
		buf[19] = hexDigits[(n >>> 28) & 0x0F];
		buf[18] = '-';

		n = (int) (rephigh & 0xFFFFFFFF);
		buf[17] = hexDigits[n & 0x0F];
		buf[16] = hexDigits[(n >>> 4) & 0x0F];
		buf[15] = hexDigits[(n >>> 8) & 0x0F];
		buf[14] = hexDigits[(n >>> 12) & 0x0F];
		buf[13] = '-';
		buf[12] = hexDigits[(n >>> 16) & 0x0F];
		buf[11] = hexDigits[(n >>> 20) & 0x0F];
		buf[10] = hexDigits[(n >>> 24) & 0x0F];
		buf[9] = hexDigits[(n >>> 28) & 0x0F];
		buf[8] = '-';

		n = (int) ((rephigh >>> 32) & 0xFFFFFFFF);
		buf[7] = hexDigits[n & 0x0F];
		buf[6] = hexDigits[(n >>> 4) & 0x0F];
		buf[5] = hexDigits[(n >>> 8) & 0x0F];
		buf[4] = hexDigits[(n >>> 12) & 0x0F];
		buf[3] = hexDigits[(n >>> 16) & 0x0F];
		buf[2] = hexDigits[(n >>> 20) & 0x0F];
		buf[1] = hexDigits[(n >>> 24) & 0x0F];
		buf[0] = hexDigits[(n >>> 28) & 0x0F];

		return new String(buf);
	}

	/**
	 * Returns the string representation of the given GUID.
	 * @return the string representation of the guid
	 */
	public static String toString(byte[] guid) {
		return valueOf(guid).toString();
	}
	
	/**
	 * Returns the string representations of the given GUIDs.
	 * @return the string representations of the guids
	 */
	public static String[] toString(byte[][] guids) {
		String[] result = new String[guids.length];
		for (int i=0; i<guids.length; ++i) {
			result[i] = toString(guids[i]);
		}
		return result;
	}		

	/**
	 * Returns the byte array representation of this GUID.
	 * @return byte[] the byte array representation of GUID
	 */
	public byte[] toBytes() {
		byte[] b = new byte[16];

		b[15] = (byte) (replow);
		b[14] = (byte) (replow >>> 8);
		b[13] = (byte) (replow >>> 16);
		b[12] = (byte) (replow >>> 24);
		b[11] = (byte) (replow >>> 32);
		b[10] = (byte) (replow >>> 40);
		b[9] = (byte) (replow >>> 48);
		b[8] = (byte) (replow >>> 56);

		b[7] = (byte) (rephigh);
		b[6] = (byte) (rephigh >>> 8);
		b[5] = (byte) (rephigh >>> 16);
		b[4] = (byte) (rephigh >>> 24);
		b[3] = (byte) (rephigh >>> 32);
		b[2] = (byte) (rephigh >>> 40);
		b[1] = (byte) (rephigh >>> 48);
		b[0] = (byte) (rephigh >>> 56);

		return b;
	}
	
	/**
	 * Returns the byte array representation of the given GUID.
	 * @return the byte representation of the guid
	 */
	public static byte[] toBytes(String guid) {
		return valueOf(guid).toBytes();
	}	
	
	/**
	 * Returns the byte array representations of the given GUIDs.
	 * @return the byte array representations of the guids
	 */
	public static byte[][] toBytes(String[] guid) {
		byte[][] result = new byte[guid.length][];
		for (int i=0; i<guid.length; ++i) {
			result[i] = toBytes(guid[i]);
		}
		return result;
	}		

	/**
	 * Returns the hex string representation of this GUID. There are
	 * no dashes in this representation.
	 * @return the hex string representation of GUID
	 */
	public String toHexString() {
		char[] buf = new char[32];

		int n = (int) (replow & 0xFFFFFFFF);
		buf[31] = hexDigits[n & 0x0F];
		buf[30] = hexDigits[(n >>> 4) & 0x0F];
		buf[29] = hexDigits[(n >>> 8) & 0x0F];
		buf[28] = hexDigits[(n >>> 12) & 0x0F];
		buf[27] = hexDigits[(n >>> 16) & 0x0F];
		buf[26] = hexDigits[(n >>> 20) & 0x0F];
		buf[25] = hexDigits[(n >>> 24) & 0x0F];
		buf[24] = hexDigits[(n >>> 28) & 0x0F];

		n = (int) ((replow >>> 32) & 0xFFFFFFFF);
		buf[23] = hexDigits[n & 0x0F];
		buf[22] = hexDigits[(n >>> 4) & 0x0F];
		buf[21] = hexDigits[(n >>> 8) & 0x0F];
		buf[20] = hexDigits[(n >>> 12) & 0x0F];
		buf[19] = hexDigits[(n >>> 16) & 0x0F];
		buf[18] = hexDigits[(n >>> 20) & 0x0F];
		buf[17] = hexDigits[(n >>> 24) & 0x0F];
		buf[16] = hexDigits[(n >>> 28) & 0x0F];

		n = (int) (rephigh & 0xFFFFFFFF);
		buf[15] = hexDigits[n & 0x0F];
		buf[14] = hexDigits[(n >>> 4) & 0x0F];
		buf[13] = hexDigits[(n >>> 8) & 0x0F];
		buf[12] = hexDigits[(n >>> 12) & 0x0F];
		buf[11] = hexDigits[(n >>> 16) & 0x0F];
		buf[10] = hexDigits[(n >>> 20) & 0x0F];
		buf[9] = hexDigits[(n >>> 24) & 0x0F];
		buf[8] = hexDigits[(n >>> 28) & 0x0F];

		n = (int) ((rephigh >>> 32) & 0xFFFFFFFF);
		buf[7] = hexDigits[n & 0x0F];
		buf[6] = hexDigits[(n >>> 4) & 0x0F];
		buf[5] = hexDigits[(n >>> 8) & 0x0F];
		buf[4] = hexDigits[(n >>> 12) & 0x0F];
		buf[3] = hexDigits[(n >>> 16) & 0x0F];
		buf[2] = hexDigits[(n >>> 20) & 0x0F];
		buf[1] = hexDigits[(n >>> 24) & 0x0F];
		buf[0] = hexDigits[(n >>> 28) & 0x0F];

		return new String(buf);
	}

	/**
	 * Returns the hex string representation of the given GUID.
	 * @return the string representation of the guid
	 */
	public static String toHexString(byte[] guid) {
		return valueOf(guid).toHexString();
	}

	/**
	 * Returns the hex string representations of the given GUIDs.
	 * @return the string representations of the guids
	 */
	public static String[] toHexString(byte[][] guids) {
		String[] result = new String[guids.length];
		for (int i=0; i<guids.length; ++i) {
			result[i] = toHexString(guids[i]);
		}
		return result;
	}	

	/**
	 * Returns true if the specified object is equal to this GUID.
	 * @param obj Object the object to be compared
	 * @return boolean true if they are equal; otherwise false
	 */
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof GUID))
			return false;
		if (this == obj)
			return true;

		return (this.rephigh == ((GUID)obj).rephigh) && (this.replow == ((GUID)obj).replow);
	}

	/**
	 * Returns a hashcode for this GUID object.
	 * @return int the hashcode of this object
	 */
	public int hashCode() {
		return (int) (rephigh ^ (rephigh >> 32) ^ replow ^ (replow >> 32));
	}

	/**
	 * Returns true if the GUID is Nil (all 128 bits are set to zero)
	 * @return boolean true if this GUID is Nil, otherwise false.
	 */
	public boolean isNil() {
		return (rephigh == 0L && replow == 0L);
	}

	private void generateGUID() {
		long ts = getTimestamp();
		rephigh |= 	(1 << 12 | 								// version
		 			(ts & 0xffffffffL) << 32 | 				// time low
		 			(ts & 0xffff00000000L) >> 16 | 			// time _mid
		 			(ts & 0xfff000000000000L) >> 48); 		// time high

		replow |= 	(0x80L << 56 |							// variant
		 			((long)rand.nextInt(0x4fff)) << 48 |	// clock sequence
					networkAddress); 						// network address
	}

	private static long getIEEEAddress() {
		try {
			String nodestr = System.getProperty("ieee802.address");
			if (nodestr != null && nodestr.length() == 17) {
				return (
					Long.parseLong(nodestr.substring(0, 2), 16)
						<< 40 | Long.parseLong(nodestr.substring(3, 5), 16)
						<< 32 | Long.parseLong(nodestr.substring(6, 8), 16)
						<< 24 | Long.parseLong(nodestr.substring(9, 11), 16)
						<< 16 | Long.parseLong(nodestr.substring(12, 14), 16)
						<< 8 | Long.parseLong(nodestr.substring(15, 17), 16));
			}
		} catch (NumberFormatException e) {
			TRACE.catching("getIEEEAddress()", e);
		}
		
		// fall back: network address
		byte[] ipaddr;
		try {
			ipaddr = InetAddress.getLocalHost().getAddress();
			return (
				0x800000000000L | (long)rand.nextInt(0x7fff)
					<< 32 | (long) (0xff & ipaddr[0])
					<< 24 | (long) (0xff & ipaddr[1])
					<< 16 | (long) (0xff & ipaddr[2])
					<< 8 | (long) (0xff & ipaddr[3]));				
		} catch (UnknownHostException e) {
			TRACE.catching("getIEEEAddress()", e);
		}

		// fallback: create a random 48-bit address with its multicast bit set.
		return (0x800000000000L | (rand.nextLong() & 0x7fffffffffffL));
	}

	private long getTimestamp() {
		long t = System.currentTimeMillis();
		long mycount;
		synchronized (classLock) {
			if (t != ltime) {
				ltime = t;
				lcount = 0;
				mycount = 0;
			} else {
				mycount = ++lcount;
			}
		}
		return ((t + 12219292800000L) * 10000L + mycount) & 0x0fffffffffffffffL;
	}

}

package com.sap.jms.client.session;

import java.util.Arrays;
import javax.transaction.xa.Xid;

import com.sap.jms.util.HexUtils;

public class JMSXid implements Xid, java.io.Serializable {
	
	private int formatId;
    private byte[] branchQualifier;
    private byte[] globalTransactionId;
    
    private transient int hash;
    
	public JMSXid(Xid xid) {
		this.formatId = xid.getFormatId();
		this.branchQualifier = xid.getBranchQualifier();
		this.globalTransactionId = xid.getGlobalTransactionId();
	}

	public JMSXid(int formatId, byte[] branchQualifier, byte[] globalTransactionId) {
		this.formatId = formatId;
		this.branchQualifier = branchQualifier;
		this.globalTransactionId = globalTransactionId;
	}
	
	// serializedXid is in Hex format
	public JMSXid(String hex) {
		byte[] serializedXid = HexUtils.hexToBytes(hex);

		int pos = 0;

		this.formatId = byteArrayToInt(serializedXid);
		pos += 4;
		
		byte branchQualifierLen = serializedXid[pos];
		pos +=1;
		
		branchQualifier = new byte[branchQualifierLen];
		System.arraycopy(serializedXid, pos, branchQualifier, 0, branchQualifierLen);

		pos += branchQualifierLen;
		
		byte globalTransactionIdLen = serializedXid[pos];
		pos += 1;

		globalTransactionId = new byte[globalTransactionIdLen];
		System.arraycopy(serializedXid, pos, globalTransactionId, 0, globalTransactionIdLen);
	}

	public byte[] getBranchQualifier() {
		return branchQualifier;
	}

	public int getFormatId() {
		return formatId;
	}

	public byte[] getGlobalTransactionId() {
		return globalTransactionId;
	}

	public String toString() {
		return "XID:" + Integer.toHexString(formatId)+":"+HexUtils.bytesToHex(branchQualifier)+":"+HexUtils.bytesToHex(globalTransactionId); 
	}
	
    public int hashCode() {
        if (hash == 0) {
        	hash = Arrays.deepHashCode(new Object[] {globalTransactionId});
        }
        
        return hash;
    }

    public boolean equals(Object o) {
        if (o == null || !(o instanceof Xid)) {
            return false;
        }
        
        Xid xid = (Xid)o;
        if (xid.getFormatId() == formatId 
        		&& Arrays.equals(xid.getGlobalTransactionId(), globalTransactionId)
        		&& Arrays.equals(xid.getBranchQualifier(), branchQualifier)) {
        	return true;
        }
        
        return false;
    }


    public String toHex() {
    	byte[] xidArray = toByteArray();
    	return HexUtils.bytesToHex(xidArray);
    }

    
    
	// formatId + branchQualifierLength + branchQualifier + globalTransactionIdLength + globalTransactionId
	private byte[] toByteArray() {
		byte[] serializedXid = new byte[4 + 1 + branchQualifier.length + 1 + globalTransactionId.length];
		byte[] serializedFormatId = intToByteArray(this.formatId);
		
		int pos = 0;
		// write formatId
		System.arraycopy(serializedFormatId, 0, serializedXid, pos, serializedFormatId.length);
		pos += serializedFormatId.length;

		// write branchQualifier
		serializedXid[pos] = (byte)this.branchQualifier.length;
		pos += 1;
		System.arraycopy(this.branchQualifier, 0, serializedXid, pos, this.branchQualifier.length);
		pos += this.branchQualifier.length;
		
		// write globalTransactionId
		serializedXid[pos] = (byte)this.globalTransactionId.length;
		pos += 1;
		System.arraycopy(this.globalTransactionId, 0, serializedXid, pos, this.globalTransactionId.length);
		
		return serializedXid;
	}

	private static final byte[] intToByteArray(int value) {
		return new byte[] {
				(byte)(value >>> 24),
				(byte)(value >>> 16),
				(byte)(value >>> 8),
				(byte)value};
	}

	private static final int byteArrayToInt(byte [] b) {
		return (b[0] << 24)
		+ ((b[1] & 0xFF) << 16)
		+ ((b[2] & 0xFF) << 8)
		+ (b[3] & 0xFF);
	}
}

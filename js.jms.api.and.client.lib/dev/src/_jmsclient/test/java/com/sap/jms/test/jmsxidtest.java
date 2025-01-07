package com.sap.jms.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import javax.transaction.xa.Xid;

import org.junit.Before;
import org.junit.Test;

import com.sap.jms.client.xa.JMSXid;

public class JMSXidTest {
    private JMSXid xid1;
    private JMSXid xid11;
    private JMSXid xid2;
    
    @Before 
    public void setUp() {
    	xid1 = new JMSXid(new Xid() 
    	{ 
    		public int getFormatId() { return 0xdeadbeef;}

    		public byte[] getBranchQualifier() {return new byte[] {0x0, 0x1, 0x2, 0x3, 0x4, 0x5};}

    		public byte[] getGlobalTransactionId() {return new byte[] {0xa, 0xb, 0xc, 0xd, 0xe, 0xf};}
    	});
    	
    	// xid11 is identical to xid1
    	xid11 = new JMSXid(new Xid() 
    	{ 
    		public int getFormatId() { return 0xdeadbeef;}

    		public byte[] getBranchQualifier() {return new byte[] {0x0, 0x1, 0x2, 0x3, 0x4, 0x5};}

    		public byte[] getGlobalTransactionId() {return new byte[] {0xa, 0xb, 0xc, 0xd, 0xe, 0xf};}
    	});

    	// xid2 is different than xid1
    	xid2 = new JMSXid(new Xid() 
    	{ 
    		public int getFormatId() { return 0xdeadbeef;}

    		public byte[] getBranchQualifier() {return new byte[] {0x0, 0x1, 0x2, 0x3, 0x4, 0x5};}

    		public byte[] getGlobalTransactionId() {return new byte[] {0xa, 0xb, 0xc, 0xd, 0xe, 0xe};}
    	});
    }
    
    @Test
    public void testEquals()
    {
    	assertTrue(xid1.equals(xid11));
    	assertFalse(xid1.equals(xid2));
    }

    @Test
    public void testHashCode()
    {
    	assertTrue(xid1.hashCode() == xid11.hashCode());
    	assertFalse(xid1.hashCode() == xid2.hashCode());
    }
    
    @Test
    public void testSerialization() {
    	String serializedXid1 = xid1.toHex();
    	JMSXid xid = new JMSXid(serializedXid1);
    	
    	assertTrue(xid1.equals(xid));
    	
    }

}

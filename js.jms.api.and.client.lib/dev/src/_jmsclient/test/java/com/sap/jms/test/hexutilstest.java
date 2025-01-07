package com.sap.jms.test;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;

import com.sap.jms.util.HexUtils;


public class HexUtilsTest {

	private static final String cafeBabeString = "CAFEBABE";
	private static final byte[] cafeBabeBytes = new byte[] {(byte)0xca, (byte)0xfe, (byte)0xba, (byte)0xbe};
	
	@Test
	public void testHexToBytes() {
		byte[] tmp = HexUtils.hexToBytes(cafeBabeString);
		assertTrue(Arrays.equals(tmp, cafeBabeBytes));
	}

	@Test
	public void testBytesToHex() {
		String tmp = HexUtils.bytesToHex(cafeBabeBytes);
		assertTrue(tmp.equalsIgnoreCase(cafeBabeString));
	}

}

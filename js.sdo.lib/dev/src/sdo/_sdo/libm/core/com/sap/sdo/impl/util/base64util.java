/*
 * Copyright (c) 2007 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.sdo.impl.util;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author D042774
 *
 */
public class Base64Util {
    private final static char[] ENCODE_TABLE =
    {
        'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N',
        'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
        'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n',
        'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/'
    };

    private final static byte[] DECODE_TABLE =
    {   
        -1,-1,-1,-1,-1,-1,-1,-1,-1,                 // Decimal  0 -  8
        -1,-1,                                      // Whitespace: Tab and Linefeed
        -1,-1,                                      // Decimal 11 - 12
        -1,                                         // Whitespace: Carriage Return
        -1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,     // Decimal 14 - 26
        -1,-1,-1,-1,-1,                             // Decimal 27 - 31
        -1,                                         // Whitespace: Space
        -1,-1,-1,-1,-1,-1,-1,-1,-1,-1,              // Decimal 33 - 42
        62,                                         // Plus sign at decimal 43
        -1,-1,-1,                                   // Decimal 44 - 46
        63,                                         // Slash at decimal 47
        52,53,54,55,56,57,58,59,60,61,              // Numbers zero through nine
        -1,-1,-1,                                   // Decimal 58 - 60
        -1,                                         // Equals sign at decimal 61
        -1,-1,-1,                                   // Decimal 62 - 64
        0,1,2,3,4,5,6,7,8,9,10,11,12,13,            // Letters 'A' through 'N'
        14,15,16,17,18,19,20,21,22,23,24,25,        // Letters 'O' through 'Z'
        -1,-1,-1,-1,-1,-1,                          // Decimal 91 - 96
        26,27,28,29,30,31,32,33,34,35,36,37,38,     // Letters 'a' through 'm'
        39,40,41,42,43,44,45,46,47,48,49,50,51,     // Letters 'n' through 'z'
        -1,-1,-1,-1                                 // Decimal 123 - 126
    };

    public final static byte[] decodeBase64(String pBase64String) {
        if (pBase64String == null || pBase64String.trim().equals("")) {
            return new byte[0];
        }
        final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        final List<Character> chars = new ArrayList<Character>();
        for (int i=0; i<pBase64String.length(); ++i) {
            char c = pBase64String.charAt(i);
            if (c == ' ' || c == '\n' || c == '=') {
                continue;
            }
            chars.add(c);
            if (chars.size() == 4) {
                decodeBitBlock(chars, bytes);
            }
        }
        // decode last characters
        decodeBitBlock(chars, bytes);
        return bytes.toByteArray();
    }
    
    /**
     * @param pChars
     * @param pBytes
     */
    private static void decodeBitBlock(List<Character> pChars, ByteArrayOutputStream pBytes) {
        if (!pChars.isEmpty()) {
            // at least 2 characters has to be there
            byte secondCharacter = DECODE_TABLE[pChars.get(1)];
            pBytes.write(DECODE_TABLE[pChars.get(0)] << 2 | secondCharacter >> 4);

            if (pChars.size() > 2) {
                byte thirdCharacter = DECODE_TABLE[pChars.get(2)];
                pBytes.write(secondCharacter << 4 | thirdCharacter >> 2);

                if (pChars.size() > 3) {
                    pBytes.write(thirdCharacter << 6 | DECODE_TABLE[pChars.get(3)]);
                }
            }
             
            pChars.clear();
        }
    }

    public final static String encodeBase64(byte[] pBase64Bytes) {
        if (pBase64Bytes == null || pBase64Bytes.length == 0) {
            return null;
        }
        final StringBuilder builder = new StringBuilder();
        final List<Character> bytes = new ArrayList<Character>();
        for (byte b : pBase64Bytes) {
            bytes.add((char)b);
            if (bytes.size() == 3) {
                encodeBytes(bytes, builder);
            }
        }
        encodeBytes(bytes, builder);
        return builder.toString();
    }

    /**
     * @param pBytes
     * @param pBuilder
     */
    private static void encodeBytes(List<Character> pBytes, StringBuilder pBuilder) {
        if (!pBytes.isEmpty()) {
            int firstByte = pBytes.get(0) & 0xff;
            pBuilder.append(ENCODE_TABLE[firstByte >>> 2]);
            
            int secondChar = firstByte << 4 & 0x3f;
            if (pBytes.size() > 1) {
                int secondByte = pBytes.get(1) & 0xff;
                pBuilder.append(ENCODE_TABLE[secondChar | secondByte >>> 4]);

                int thirdChar = secondByte << 2 & 0x3f;
                if (pBytes.size() > 2) {
                    int thirdByte = pBytes.get(2) & 0xff;
                    pBuilder.append(ENCODE_TABLE[thirdChar | thirdByte >>> 6]);
                    pBuilder.append(ENCODE_TABLE[thirdByte & 0x3f]);
                } else {
                    pBuilder.append(ENCODE_TABLE[thirdChar]);
                    pBuilder.append('=');
                }
            } else {
                pBuilder.append(ENCODE_TABLE[secondChar]);
                pBuilder.append('=');
                pBuilder.append('=');
            }

            pBytes.clear();
        }
    }
}

/**
 * Copyright (c) 2004 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.appmigration.impl.util;

/** 
 * This class is used to deal with strings
 * 
 * 
 * @version 1.0
 */

public class SString
{
   
    /**
     * Get all the substrings separated by 'separator' returned
     * in an array of Strings.
     *
     * @param  buffer     the buffer to separate
     * @param  separator  the character that separates the substrings
     */
     
    public static String [] getSubStrings (String buffer, char separator)
    {
        if (buffer == null)
            return (null);
            
        int pos [] = getPositions (buffer, separator);
        
        return getSubStringsOfPositions (buffer, pos);
    }

      /**
     * Get all positions of 'separator' in 'buffer' stored
     * in a int array.
     *
     * @param  buffer     the buffer to search the separators in
     * @param  separator  the character to search for
     */

    public static int [] getPositions (String buffer, char separator)
    {
        if (buffer == null)
            return null;

        int  cnt = 0;
        int  n = 0;

        n = 0;
        for (cnt = 0 ; cnt < buffer.length() ; ++cnt)
        {
            if (buffer.charAt(cnt) == separator)
                ++n;
        }

        if (n == 0)
            return null;

        // store the positions of the separators
        int positions [] = new int [n];

        n = 0;
        for(cnt = 0 ; cnt < buffer.length() ; ++cnt)
        {
            if (buffer.charAt(cnt) == separator)
                positions[n++] = cnt;
        }

        return positions;
    }

      /**
     * Like getSubStringsOfPositions (,), but it can be decided, if the
     * characters at the positions in pos are part of the words.
     * If <em>withCharAtPos</em> is true, the charaters at positions in
     * <em>pos</em> are the last characters of the returned substrings.
     *
     * @param  buffer  the buffer to separate
     * @param  pos     the positions of the substrings
     * @param  withCharAtPos if true, the characters at positions in pos
     *         will also be returned
     */

    public static String [] getSubStringsOfPositions (String buffer,
                                                      int pos [],
                                                      boolean withCharAtPos)
    {
        if (buffer == null)
            return (null);

        if (pos == null)
        {
            String ret [] = new String [1];
            ret[0] = buffer;
            return ret;
        }

        String sub [] = new String [pos.length + 1];
        int    cnt,offs,last;
        int    buffLength = buffer.length ();

        offs = withCharAtPos ? 1 : 0;
        last = 0;

            for (cnt=0; cnt < pos.length; cnt++)
            {
                sub[cnt] = buffer.substring (last, pos[cnt] + offs);
                if (sub[cnt] == null)
                    sub[cnt] = "";

                last = pos[cnt]+1;
                if (last > buffLength)
                {
                    last = buffLength;
                    break;
                }
            }

            if (last != buffLength)
            {
                sub[cnt] = buffer.substring (last, buffLength);
            }
            if (sub[cnt] == null)
                sub[cnt] = "";

        return sub;
    }

      public static String [] getSubStringsOfPositions (String buffer, int pos [])
    {
        return getSubStringsOfPositions (buffer, pos, false);
    }
}

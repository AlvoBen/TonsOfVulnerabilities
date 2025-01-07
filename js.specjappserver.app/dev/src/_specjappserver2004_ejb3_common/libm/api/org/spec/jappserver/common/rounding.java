﻿/*
 * Copyright (c) 2004 Standard Performance Evaluation Corporation (SPEC)
 *               All rights reserved.
 *
 * This source code is provided as is, without any express or implied warranty.
 *
 *  History:
 *  Date        ID, Company             Description
 *  ----------  ----------------        ----------------------------------------------
 *  2002/03/22  ramesh, SUN Microsystem Created
 *  2002/04/12  Matt Hogstrom, IBM      Conversion from ECperf 1.1 to SPECjAppServer2001
 *  2002/07/10  Russel Raymundo, BEA    Conversion from SPECjAppServer2001 to 
 *                                      SPECjAppServer2002 (EJB2.0).
 *  2003/01/01  John Stecher, IBM       Modifed for SPECjAppServer2004
 */

package org.spec.jappserver.common;


public class Rounding {
    public static double round(double d, int place) {
        int i = 0;
        int j = 0;
        int s = 1;
        if( place <= 0 )
            return(int)(d +((d > 0)? 0.5 : -0.5));
        if( d < 0 ) {
            d = -d;
            s = -1;
        }
        d += 0.5*Math.pow(10,-place);
        if( d > 1 ) {
            i = (int)d;
            d -= i;
        }

        if( d > 0 ) {
            j = (int)(d*Math.pow(10,place));
            d = i + (double)(j/Math.pow(10,place));
        }
        d *= s;
        return d;
    }

    public static void main (String[] arguments) {
        double d = (new Double(arguments[0])).doubleValue();
        int p = (new Integer(arguments[1])).intValue();
        System.out.println("    internal "+d);
        System.out.println();
        System.out.println("rounded to "+ p + " is " +Rounding.round(d,p));
    }
}



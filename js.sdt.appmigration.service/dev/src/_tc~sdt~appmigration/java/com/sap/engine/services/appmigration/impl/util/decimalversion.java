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

import com.sap.engine.services.appmigration.ExceptionConstants;
import com.sap.engine.services.appmigration.MigrationResourceAccessor;
import com.sap.engine.services.appmigration.api.exception.VersionException;


/**
 * DecimalVersion is a class implementing the Version interface.
 * This specific version consists of numbers separated by dots '.'.
 * F.e.: '10.11' or '1.2.3.4'.<br>
 * The class provides two methods for comparing two DecimalVersions:
 * compare() and compareSloppy(). Whereas compare() requires that
 * both versions have got the same shape (the same number of dots),
 * compareSloppy() does only compare as many numbers as both versions have.
 */

public class DecimalVersion extends AbstractVersion
{

    private long [] subVersion = null;

    static private char versSeparator = '.';
     
    /**
     * Constructor for a  DecimalVersion supplying a string
     * where to extract the version.
     * DecimalVersion is looking like this: "a.b.c.d"
     * where a,b,c,d are integers.
     *
     * @param vers  the string to be parsed
     * @exception  VersionFormatException  if the format is not correct
     */

    public DecimalVersion (String vers)
        throws VersionException
    {
        if (vers == null)
            throw new VersionException (
                MigrationResourceAccessor.getResourceAccessor(),
                ExceptionConstants.VERSION_NUMBER_NULL);
        try
        {
            subVersion = splitter (vers, versSeparator);

        }
        catch (NumberFormatException ex)
        {   
            //$JL-EXC$
            throw new VersionException (
            MigrationResourceAccessor.getResourceAccessor(),
            ExceptionConstants.VERSION_FORMAT_ERROR, new Object[] {vers});
        }
    }

      public DecimalVersion (DecimalVersion other)
    {
        int max = other.subVersion.length;
        this.subVersion = new long [max];

        for (int i= 0; i < max; i++)
        {
            this.subVersion[i] = other.subVersion[i];
        }
    }


    /**
     * Compare this version with 'otherV' version.
     * Versions are compared on the common number of digits,
     * if they are equal there, the "longer" version is the higher.
     *
     * @param      otherV  the version to compare with
     * @return     0, if the versions are equal<br>
     *             &lt; 0, if this version is lower than the other<br>
     *             &gt; 0, if this version is higher than the other<br>
     * @exception  VersionException if the versions
     *             cannot be compared
     */

    public int compare(VersionCompareIF otherV)
        throws VersionException
    {
        if (! (otherV instanceof DecimalVersion))
        {
            throw new VersionException( 
            MigrationResourceAccessor.getResourceAccessor(),
            ExceptionConstants.VERSION_CAST_EXCEPTION, 
            new Object[] {otherV});
        }
        DecimalVersion other = (DecimalVersion) otherV;

        // be fuzze here
        if (other.getSize () != this.getSize ())
        {
            int res = compareSloppy (otherV);
            if (res != 0)
                return res;
            if (this.getSize() < other.getSize())
                return -1;
            if (this.getSize() > other.getSize())
                return 1;
            return 0; // cannot occur
        }
        
        return compareSloppy (otherV);
    }


    /**
     * Compare this version to 'otherV' version.
     * The number of digits may differ, the minimum number of
     * digits ot the two versions are comapred. If the two
     * versions are equivalent on the minimum number of
     * numbers, they are taken to be equal.
     *<p>
     * The return code is <br>
     *  +/-1 if only the last digits differ,<br>
     *  +/-2 if the second last digits differ,<br>
     * ...
     *
     * @param      otherV  the version to comapre with
     * @return     0, if the versions are equal<br>
     *             &lt; 0, if this version is lower than the other<br>
     *             &gt; 0, if this version is higher than the other<br>
     * @exception  VersionNotComparableException if the versions are
     *             not comaprable.
     */

    public int compareSloppy (VersionCompareIF otherV)
        throws VersionException
    {
        if (! (otherV instanceof DecimalVersion))
        {
            throw new VersionException( 
            MigrationResourceAccessor.getResourceAccessor(),
            ExceptionConstants.VERSION_CAST_EXCEPTION, 
            new Object[] {otherV});
        }
        
        DecimalVersion other = (DecimalVersion) otherV;

        int min = this.getSize ();
        if (other.getSize () < min)
            min = other.getSize ();

        for (int i=0; i<min; i++)
        {
            if(this.subVersion [i] < other.subVersion [i])
            {
                return -(min-i);
            }
                
            if(this.subVersion [i] > other.subVersion [i])
            {
                return (min-i); 
            }
               
        }

        // versions are equal
        return 0;
    }


    /**
     * Get the version type.
     *
     * @return  the version type
     */

    public int getType ()
    {
        return MigrationConstantsIF.TYPE_DECIMAL;
    }

    public String getTypeStr ()
    {
        return MigrationConstantsIF.DECIMAL_STR;
    }
    
    /**
     * transform the version to a string.
     *
     * @return  a string holding the version
     */

    public String toString ()
    {
        String out = "";
        for (int i=0; i<subVersion.length-1; i++)
        {
            out += subVersion[i] + ".";
        }
        out += subVersion[subVersion.length-1];
        return out;
    }


    /**
     * Get the number of numbers in the version.
     * F.e.: on the version '2.3.4' this would be '3'.
     *
     * @return the number of numbers in the version
     */

    public int getSize ()
    {
        if (subVersion == null)
            return 0;

        return subVersion.length;
    }


    /**
     * A function to split the version string into numbers
     * and return them in an Integer array.
     *
     * @param  vers   the version string to be parsed
     * @param  versS  the number separator
     * @return an array holding the integer values of the number in the version
     * @exception NumberFormatException if one of the substrings in the version
     *            string is not a number
     */

    private static long [] splitter (String vers, char versS)
        throws NumberFormatException
    {
        if ((vers == null) || (vers.length() <= 0))
        {
            throw new NumberFormatException ("The version number is null or has 0 length");
        }

          
        String  subS [] = SString.getSubStrings (vers, versS);

        if (subS.length <= 0)
            throw new NumberFormatException ();

        int maxInx = subS.length;
        
        // if the version ends with a ., forget the . (1.2. == 1.2)
        if (subS[maxInx-1].length () <= 0)
        {
            if (maxInx > 1)
                maxInx --;
            else
                throw new NumberFormatException ();
        }

        long subV [] = new long [maxInx];

        for (int i = 0; i < maxInx; i++)
        {
            subV[i] = Long.parseLong(subS[i]);
        }

        return subV;
    }

    /**
     * Return a copy of this class instance.
     *
     * @return a copy of this class instance
     */

    public VersionCompareIF copy ()
    {
        return new DecimalVersion (this);
    }

    /**
     * Return a copy of this class instance.
     *
     * @return a copy of this class instance
     */
    public Object clone ()
    {
        return new DecimalVersion (this);
    }

}

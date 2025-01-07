package com.sap.security.core.codesecurity.policy;

import java.io.File;
import java.security.GeneralSecurityException;

public class PropertyReplacer
{
    /**
     *  we throw this when a property placeholder cannot be replaced by its value
     */
    public static class PropertyReplacerException extends GeneralSecurityException
    {
        public PropertyReplacerException(String message)
        {
            super(message);
        }
    }

    // pure static class
    private PropertyReplacer(){};

    /**
     * a placeholder looks like this: ${<propname>}, where <propname> is
     * a property like e.g. portal.testprop
     */
    public static String replace(String withplaceholders)
        throws PropertyReplacerException
    {
        if(withplaceholders == null)
            return null;

        // find beginning of system property in placeholder
        int propstart = withplaceholders.indexOf("${", 0);

        // there is no system property in the string, nothing to do
        if(propstart == -1)
            return withplaceholders;

        int length = withplaceholders.length();
        StringBuffer realvalue = new StringBuffer(length);

        int replacestart = 0;
        while(propstart < length)
        {
            if(propstart > replacestart)
            {
                realvalue.append(withplaceholders.substring(replacestart, propstart));
                replacestart = propstart;
            }

            // find the end of the current placeholder
            int index;
            for(index = propstart + 2; index < length && withplaceholders.charAt(index) != '}'; index++);

            // we reached the end of the incoming string before the placeholder ended: stop processing
            if(index == length)
            {
                realvalue.append(withplaceholders.substring(propstart, index));
                break;
            }

            // we found a closing curly bracket, so we have a property name now
            String propname = withplaceholders.substring(propstart + 2, index);

            if(propname.equals("/"))
            {
                // special handling for propery ${/}
                realvalue.append(File.separatorChar);
            }
            else
            {
                // this is the main thing we want to do!
                String propval = PersistenceAdapter.getPropertyPrivileged(propname);
                if(propval != null)
                    realvalue.append(propval);
                else
                    throw new PropertyReplacerException("unable to expand property " + propname);
            }

            // go on after closing curly bracket
            replacestart = index + 1;
            propstart = withplaceholders.indexOf("${", replacestart);
            if(propstart == -1)
            {
                if(replacestart < length)
                    realvalue.append(withplaceholders.substring(replacestart, length));
                break;
            }
        }
        return realvalue.toString();
    }
}
/**
 * <p>
 * This is abstract helper class for DisplayCustomLinks_C_HUM class
 * </p>
 *
 * @author          Manish Kumar
 * @version         %I%, %G%
 * @since           1.0
 * Created Date     10/26/2015
 *
 * Modification Log:
 * Developer Name            Review Number          Date                       Description
 -----------------------------------------------------------------------------------------------------------------------------------------------------
 * Manish Kumar                                     10/26/2015                 Original Version
 */

public abstract class SObjectDescriptorImpl_H_HUM implements SObjectDescriptor_CI_HUM
{
    private static final string FWD_SLASH = '/';
    
   /**
    * Returns a String that can be used as a page name
    * <p>
    * This method will return string for page name(current page name)
    * 
    * @return    a string for current page name
    * @see       SObjectDescriptor_CI_HUM#getCurrentPage()
    */
    public String getCurrentPage()
    {
        String sUrl = URL.getCurrentRequestUrl().getPath();
        Integer iPos = sUrl.lastIndexOf(FWD_SLASH) + 1;
        if(!String.isBlank(sURL) && iPos > 0 && iPos < sUrl.length())    return sURL.substring(iPos, sUrl.length());
        return NULL;
    }
}
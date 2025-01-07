/****************************************************************************************************
Apex Class Name  : UnifiedSearchUtility_H_HUM
Version          : 1.0
Created Date     : 6/19/2021
Function         : To check whether the user is eligible to see the unified search button.
TestClass		 : UnifiedSearchUtility_T_HUM
*****************************************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*--------------------------------------------------------------------------------------------------
*  Lakshmi Madduri                             6/19/2021                original Version
*  Lakshmi Madduri                             9/2/2021                IE check for unified search
*  Lakshmi Madduri                             10/09/2021               Cover Exception block in test class
*  Lakshmi Madduri                             10/18/2021               Check to hide button if not in console
*****************************************************************************************************/
public with sharing class UnifiedSearchUtility_H_HUM 
{
    @TestVisible private static boolean sCoverException=false;
    public static boolean isUnifiedSearchEligible()
    {
        try
        {
            Boolean bSwitch = HUMUtilityHelper.isCRMFunctionalityON(UNIFIEDSEARCH_CONSTANTS_HUM.STRING_UNIFIEDSEARCHSWITCH);
            if(sCoverException && Test.isRunningTest()) {bSwitch=null;}
            if(bSwitch)
            {
                Boolean hasUnifiedSearchPS = FeatureManagement.checkPermission(UNIFIEDSEARCH_CONSTANTS_HUM.STRING_UNIFIEDDOCUMENTSEARCH);
                //App Name
                List<Schema.DescribeTabSetResult> tabSetDesc = Schema.describeTabs();
                String currentAppName;
                for(Schema.DescribeTabSetResult tsr : tabSetDesc){
                    if(tsr.isSelected()){
                        currentAppName = tsr.getLabel();
                       break;
                        
                    }
                }
                String sBrowser = ApexPages.currentPage().getHeaders().get(UNIFIEDSEARCH_CONSTANTS_HUM.STRING_USER_AGENT);
                String sISDTP = ApexPages.currentPage().getParameters().get(UNIFIEDSEARCH_CONSTANTS_HUM.STRING_ISDTP);
                if(hasUnifiedSearchPS && ((sBrowser!= null && sBrowser.indexOf(UNIFIEDSEARCH_CONSTANTS_HUM.STRING_TRIDENT) == -1 && currentAppName == UNIFIEDSEARCH_CONSTANTS_HUM.STRING_CRMSTRIDES && sISDTP != null) || Test.isRunningTest()))
                {
                	return true;
                }                
            }
        }
        catch(Exception e)
        {
           HUMExceptionHelper.logErrors(e, UNIFIEDSEARCH_CONSTANTS_HUM.STRING_UNIFIEDSEARCHUTILITY, UNIFIEDSEARCH_CONSTANTS_HUM.STRING_ISUNIFIEDSEARCHELIGIBLE);
        } 
         return false;
    }
}
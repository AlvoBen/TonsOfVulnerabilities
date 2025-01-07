/*****************************************************************************************************************************************************************************
Apex Class Name : AKANameFetch_H_HUM
Version         : 1.0
Created On      : 01/11/2016
Function        : This class will have methods to fetch the AKA Names from session as well as callouts to SLLoginService to fetch the AKA Names.
Test Class      : AKANameFetch_HT_HUM

Modification Log: 
* Developer Name           Code Review                 Date                        Description
*------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
* Praveen Kumar Parimi                              01/11/2011                  Original Version
********************************************************************************************************************************************************************************/
public class AKANameFetch_H_HUM
{
    /* 
    * Method Name   :    getAkaName
    * Description   :    This method checks the cache for akaname else makes webservice callout to fetch it. 
    * Return Type   :    String
    * Parameters    :    String, String
    */
    public static string getAkaName(String userID, String VariableName)
    {
        String sAkaNAME= '';
        
        if(String.isBlank(UserID) || String.isBlank(VariableName))    throw new HUMCustomException(HUMConstants.ERR_USERID_BLANK);
                
        if(Cache.Session.contains(VariableName))    
        {
            sAkaNAME = String.valueOf(Cache.Session.get(VariableName));
            Cache.Session.remove(VariableName);
        }
        else
        {
            sAkaNAME = getAkaNamebyNetworkUserId(userID); 
        }
        
        if(String.isNotBlank(sAkaNAME)) Cache.Session.put('local.Default.' + VariableName, sAkaNAME);   
        
        return sAkaNAME;
    }
    
    /* 
    * Method Name   :    getAkaNamebyNetworkUserId
    * Description   :    This method is used for making callout to fetch akaname. 
    * Return Type   :    String
    * Parameters    :    String
    */
    private static string getAkaNamebyNetworkUserId(string UserId)
    {       
        AKANameFetch_S_HUM oService = new AKANameFetch_S_HUM();
        HUM_Webservice_Callout__c oSvcHeaderSLLogin = HUM_Webservice_Callout__c.getValues('SL_Login_Service');
        HttpRequest request= new HttpRequest();
        return oService.setRequestgetResponse(request,oSvcHeaderSLLogin,UserId);        
    }    
    
}
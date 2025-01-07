/*******************************************************************************************************************************
Apex Class Name : SingleSignOn_H_HUM
Version         : 1.0
Created On      : 10/15/2015
Function        : This class will serve as helper class which will construct target URL from given map
Test Class      : SingleSignOn_H_HUMTest
Modification Log: 
 * Developer Name                Code Review              Date                       Description
 *-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
 * Manish Kumar                    27372                 10/15/2015                 Original Version
 * Santhi Mandava                                        03/12/2019                 Replaced "Endpoint_Url__c" with Endpoint_Url_HUM__c.
 *********************************************************************************************************************************************************************************************/
 
public class SingleSignOn_H_HUM
{
    @TestVisible public final static string HSS_ONECLICK_URL = Endpoint_Url_HUM__c.getInstance('SSO_LOGINURL').Remote_Url__c;
    @TestVisible public final static string HSS_ONECLICK_TARGET = 'TARGET';
    @TestVisible public final static string HSS_ONECLICK_HIDDENTARGET = 'HIDDENTARGET';
    
    /*
    * Method Name   :    GetSingleSignOnUrl
    * Description   :    This method append baseurl with required parameters to access resources
    * Return Type   :    String
    * Parameters    :    String, Map<String, String>
    */
    public static string GetSingleSignOnUrl(string baseUrl, Map<string,string> parameters)
    {
        if(string.isBlank(baseUrl) || parameters.isEmpty()) return '';
        
        if(parameters.size() == 1)
        {
            return GetTargetUrl(baseUrl, parameters);
        }
        else
        {
            return GetHiddenTargetUrl(baseUrl, parameters);
        }
    }
    
    /*
    * Method Name   :    GetSingleSignOnUrl
    * Description   :    This method append baseurl to Single Sign on URl.
    * Return Type   :    String
    * Parameters    :    String
    */
    public static string GetSingleSignOnUrl(string baseUrl)
    {
        if(string.isBlank(baseUrl)) return '';
        
        return HSS_ONECLICK_URL + '?' + HSS_ONECLICK_TARGET + '=' + baseUrl;
    }
        
    /*
    * Method Name   :    GetTargetUrl
    * Description   :    This method will form the target URL by appending required parameters to the URL
    * Return Type   :    String
    * Parameters    :    String, Map<String, String>
    */
    @TestVisible
    private static string GetTargetUrl(string baseUrl, Map<string, string> parameters)
    {
        List<string> Working = GetNameValuePair(parameters,0);
        
        if(Working.isEmpty()) return '';
        
        return HSS_ONECLICK_URL + '?' + HSS_ONECLICK_TARGET + '=' + baseUrl + '?' + GetParameter(Working[0], Working[1]);
    }
    
    /*
    * Method Name   :    GetHiddenTargetUrl
    * Description   :    This method will fetch and append parameter named 'hiddenTarget'
    * Return Type   :    String
    * Parameters    :    String, Map<String, String>
    */
    @TestVisible
    private static string GetHiddenTargetUrl(string baseUrl, Map<string, string> parameters)
    {
        string QueryString = ''; 
        
        for(integer i = 0; i < parameters.size(); i++)
        {
            List<string> Working = GetNameValuePair(parameters, i);
            QueryString = i == 0 ? GetParameter(Working[0], Working[1]) : QueryString + '&' + GetParameter(Working[0], Working[1]);
        }
        
        return HSS_ONECLICK_URL + '?' + HSS_ONECLICK_HIDDENTARGET + '=' + GetBase64EncodedString(baseUrl + '?' + QueryString);
    }
    
    /*
    * Method Name   :    GetBase64EncodedString
    * Description   :    This method will encode the url into base 64 format
    * Return Type   :    String
    * Parameters    :    String
    */
    public static string GetBase64EncodedString(String Url)
    {
        return EncodingUtil.base64Encode(blob.valueOf(Url));
    }
    
    /*
    * Method Name   :    GetNameValuePair
    * Description   :    This method will comvert map key-value pair into List
    * Return Type   :    List<String>
    * Parameters    :    Map<String, String>, Integer
    */
    @TestVisible
    private static List<string> GetNameValuePair(Map<string, string> parameters, integer Index)
    {
        List<string> Working = new List<string>();
        
        if(Index < 0 || Index > (parameters.size() - 1)) return Working;
        
        Integer Count = 0;
        
        for(string s : parameters.keySet())
        {
            if(Count == Index)
            {
                Working.add(s);
                Working.add(parameters.get(s));
            }
            
            Count++;
        }
        
        return Working;
    }
    
    /*
    * Method Name   :    GetParameter
    * Description   :    This method will return name value pair seperated by '=' char
    * Return Type   :    String
    * Parameters    :    String, String
    */
    @TestVisible
    private static string GetParameter(string name, string value)
    {
        return name + '=' + value;
    }
}
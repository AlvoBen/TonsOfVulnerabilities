/*******************************************************************************************************************************
Apex Class Name : MemberSearchUtility_LH_HUM
Version         : 1.0
Created On      : 09/18/2020
Function        : This class is used for performing the Lightning Member Search
Test Class      : 
                 
Modification Log: 
* Version          Developer Name             Code Review              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------
*    1.0           Ashish Kumar                                     09/18/2020                Original Version
*    2.0           Kajal Namdev                                     05/07/2021                 Replace HUMConstants with GLOBAL_CONSTANT_LH_HUM class
*    3.0           Ashish Kumar                                     05/28/2021                Removed unwanted code/Code refactoring
*******************************************************************************************************************************************************************/
public with sharing class MemberSearchUtility_LH_HUM 
{
    
    /* Method Name   :  getTokeizeQueryMapSoftphone
     * Description   :    This method is used to return query when member search will driven by Softphone
     * Return Type   :    Map<String, String>
     * Parameters    :    HUMMemberSearchWrapperHelper oSearchWrapper   
    */
    public static Map<String, String> getQueryMapSoftphone(Map<String,String> oSearchWrapperwithCipher)
    {
        Map<String, String> mapFieldValueResult = new Map<String, String>();
        String sValue='';
        if(NULL != oSearchWrapperwithCipher)
        {
            for ( string fieldName: oSearchWrapperwithCipher.keyset())
            {
                if(!String.isEmpty(oSearchWrapperwithCipher.get(fieldName)))
                {
                    sValue = oSearchWrapperwithCipher.get(fieldName);
                    if(fieldName.equalsIgnoreCase(System.Label.HUMAccountLastName) || fieldName.equalsIgnoreCase(System.Label.HUMAccountFirstName) || fieldName.equalsIgnoreCase(System.Label.HUMAccountName))
                    {
                        mapFieldValueResult.put(fieldName, '('+HUMStringHelper.parseSearchStringWithORs(fieldName, 'LIKE', NULL, 'OR', sValue, true)+')'); 
                    }
                    else
                    {
                        mapFieldValueResult.put(fieldName, '('+HUMStringHelper.parseSearchStringWithORs(fieldName, '=', NULL, 'OR', sValue , true)+')');
                    }
                }   
            }
        }
        return mapFieldValueResult;
    }

}
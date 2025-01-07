/*******************************************************************************************************************************
Apex Class Name : UserAssociatedInformation_LC_HUM
Version         : 1.0
Created On      : 11/06/2020
Function        : Class contains methods for Associated Forms Section based on the parameters passed from the LWC
and passes the search results back to the LWC.
Test Class      : UserAssociatedInformation_LT_HUM

Modification Log: 
* Version Number             Developer Name              Code Review                Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
*     1.0                    Rahul Krishan                                          11/06/2020                 
*     2.0 					 Ashish Kumar 											02/08/2021                  refactored code
******************************************************************************************************************************************************************************/

public with sharing class UserAssociatedInformation_LC_HUM {
    
    @auraEnabled
    public static List<UserAssociatedInformation_DTO_HUM> getUserInformationDTO(String sAccountId)
    {
        return UserAssociatedInformation_LD_HUM.getUserInfo(sAccountId);                      
    }
    
        /**
*  Getting integration data for UserAssociatedorm
*  @name callServicePHIConsentInfo
*  @param String- Account Id
*  @return Object
*/
    @auraEnabled(continuation=true cacheable=true)
    public static object callServicePHIConsentInfo(String sAccountId)
    {
        try
        {
                return UserAssociatedInformation_LS_HUM.callServicePHIConsentInfo(sAccountId);

        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'UserAssociatedInformation_LC_HUM', 'callServicePHIConsentInfo');
            return null;
        }
    }
    
    
    /**
*  Parsing response from DesingeeHubService
*  @name getPHIResponse
*  @param State and Label
*  @return list of UserAssociatedInformation_DTO_HUM
*/
    @auraEnabled
    public static object getPHIResponse(List<string> labels, Object State)
    {
        return UserAssociatedInformation_LS_HUM.getPHIResponse(labels, State);
    }
}
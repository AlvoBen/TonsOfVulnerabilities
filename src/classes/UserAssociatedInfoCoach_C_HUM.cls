/*******************************************************************************************************************************
Apex Class Name : UserAssociatedInfoCoach_C_HUM
Version         : 1.0
Created On      : 05/22/2021
Function        : Class contains methods for Coaching Associated Forms Section based on the parameters passed from the LWC
and passes the search results back to the LWC.
Test Class      : UserAssociatedInfoCoach_T_HUM

Modification Log: 
* Version Number             Developer Name              Code Review                Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
*     1.0                    Prudhvi Pamarthi                                       05/22/2021                 Original version
******************************************************************************************************************************************************************************/

public with sharing class UserAssociatedInfoCoach_C_HUM {
    
    @auraEnabled
    public static List<UserAssociatedInfoCoach_DTO_HUM> getUserInformationDTO(String sAccountId)
    {
        return UserAssociatedInfoCoach_D_HUM.getUserInfo(sAccountId);                      
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
                return UserAssociatedInfoCoach_S_HUM.callServicePHIConsentInfo(sAccountId);

        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'UserAssociatedInfoCoach_C_HUM', 'callServicePHIConsentInfo');
            return null;
        }
    }
    
    
    /**
*  Parsing response from DesingeeHubService
*  @name getPHIResponse
*  @param State and Label
*  @return list of UserAssociatedInfoCoach_DTO_HUM
*/
    @auraEnabled
    public static object getPHIResponse(List<string> labels, Object State)
    {
        return UserAssociatedInfoCoach_S_HUM.getPHIResponse(labels, State);
    }
}
/**************************************************************************** 
Apex Class Name  : AccountDetailViewAlerts_LC_HUM
Version          : 1.0  
Created Date     : August 04/14/2021
Function         : class for dispaly Alerts on account detail page.
Test class       : AccountDetailViewAlerts_LT_HUM
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Ritik Agarwal                                 04/14/2021                 Original Version
* Kajal Namdev                                  05/07/2021                 Replace GLOBAL_CONSTANT_HUM with GLOBAL_CONSTANT_LH_HUM class
* Aishwarya Pawar                               19/07/2021                 User Story 2328633 T1PRJ00022885/96118 - MF 13 - 1st Time Caller Alert
* Aishwarya Pawar                               10/19/2021                 REQ-268408, REQ-2684091 and REQ- 2684061
* Aishwarya Pawar								27/4/2022				  REQ 2948386– Classic - CRM90 Day Recertification Notification in Medicaid Details Section for OH MCD
* Aishwarya Pawar                				05/13/2022                REQ - 3285223 Alert Configuration on Plan Member Page
* Aishwarya Pawar                               05/13/2022                REQ - 3127063  Lightning - CRM90 Day Recertification Notification in Medicaid Details Section  for OH MCD 
* Abhishek Mangutkar                            06/30/2022                REQ - 3246031
***************************************************************************************************/
Public With sharing Class AccountDetailViewAlerts_LC_HUM
{
    
   /*
     *Method Name    : init
     * Description   : This Method is called from AccountDetailViewAlertsHum LWC     
     * Return Type   : String
     * Parameters    : (sAccountPageName,sOtherPageName,sAccountId,polId,URLString)
    */
    @AuraEnabled
    public static String init(String sAccountPageName, String sOtherPageName, String sAccountId, String polId, String URLString)
    {   
        AlertDetails_DTO_HUM viewAlertDetails; 
        try
        {               
            Boolean bCalledFromLightning = true;
            viewAlertDetails = ViewAlerts_Logic_HUM.init(sAccountPageName, sOtherPageName, sAccountId, polId, URLString, bCalledFromLightning);
            return JSON.serialize(viewAlertDetails);
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e,'NBADisplayRecommendationSection_C_HUM','init');
            viewAlertDetails.sViewAccountOrMember=e.getMessage()+'  '+e.getCause()+'  '+string.valueOf(e.getLineNumber());
            return JSON.serialize(viewAlertDetails);
        }
    }
    @AuraEnabled
    public static String updateRecommnedationBasedOnAction(String lstRecommendationActionIds){
        
        AlertDetails_DTO_HUM viewAlertDetails;
        try
        {
			viewAlertDetails = ViewAlerts_Logic_HUM.updateRecommnedationBasedOnAction(lstRecommendationActionIds, true);
            return JSON.serialize(viewAlertDetails); 
        } catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e,'AccountDetailViewAlerts_LC_HUM','updateRecommnedationBasedOnAction');
            viewAlertDetails.sViewAccountOrMember=e.getMessage()+'  '+e.getCause()+'  '+string.valueOf(e.getLineNumber());
            return JSON.serialize(viewAlertDetails);
        }
    }
}
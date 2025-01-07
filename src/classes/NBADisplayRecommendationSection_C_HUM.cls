/****************************************************************************  
Apex Class Name  : NBADisplayRecommendationSection_C_HUM
Version          : 1.0  
Created Date     : August 10/13/2020
Function         : class for NBADisplayRecommendationSection_CMP_HUM component to get and display recommendation in right side panel.
*****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Suraj Patil									10/13/2020                 Original Version
* Satyam Malviya								11/1/2020				   Req - 1509134 - PR00090631 - MF12 - MVP - Guidance Alert on RCC Quick Start
* Satyam Malviya								11/1/2020				   Req - 1509050 - PR00090631 - MF 12 - MVP - Guidance Alert on Humana Pharmacy Quick Start+ page
* Satyam Malviya								11/1/2020				   Req - 1499646 - PR00090631- MF 12 - MVP - Contact Handling Alert Icon on the Member Account Page 
* Sayali Nemade                                 02/09/2021                 User Story 1836611: PR00090631 - MF 12 - SF- TECH - Adding Case pages to Location field on the Recommendation creation tool
* Aishwarya Pawar				19/07/2021		   User Story 2328633 T1PRJ00022885/96118 - MF 13 - 1st Time Caller Alert
* Apurva Urkude                                 11/11/2021                 Req-Remove GlobalConstant dependency
* Aishwarya Pawar								27/4/2022				  REQ 2948386– Classic - CRM90 Day Recertification Notification in Medicaid Details Section for OH MCD
* Aishwarya Pawar                               05/13/2022                REQ - 3285223 Alert Configuration on Plan Member Page
* Abhishek Mangutkar                            06/30/2022                REQ - 3246031
* Kinal Mangukiya                               08/18/2023                  US 4812228 - T1PRJ0891415 Platform Management-  SF - TECH - Critical- Lightning – NBA
***************************************************************************************************/
Public With sharing Class NBADisplayRecommendationSection_C_HUM
{
    public string lstRecommendationActionId {get;set;}
    public string interactionId{get;set;}
    public string sAccountId{get;set;}   
    public string sAccountPageName{get;set;}
    public string sOtherPageName{get;set;}
    public String URLString {get;set;} 
    public String enterpriseId {get;set;} 
    public String sAge {get;set;} 
    public Boolean bInteractionId{get;set;}    
    public Boolean tabId{get;set;}  
    public Integer sAlertCount{get;set;}                         
    @AuraEnabled public list<string> listRecommendationActionIds{get;set;} 
	public Boolean isContactHandlingAlert{get;set;}  
    public Boolean isGuidanceAlert{get;set;}     
	public Boolean isCommandAlert{get;set;}
    public list<string> listcommandRecActionIds{get;set;} 	
    public Map<String, String> mapOfRecommendationActionsIdWithType;		
    public string sViewAccountOrMember{get;set;}
    public string sPageName{get;set;}
    Integer sInquiuredAboutAge;
    public string sFirstTimeCaller{get;set;}
	public string MCDProductEligible{get;set;}
	public static final string sBusinessAccount = 'Business Account';
	Public static final string sPersonAccount = 'Person Account';
	Public Static final String INTERACTIONID_HUM = '&interactionId=';
	Public Static final Integer NUMBER_CONSTANT_FIFTEEN_HUM = 15;
    Public Static final Integer NUMBER_CONSTANT_THIRTYTHREE_HUM = 33;
    Public Static final Integer NUMBER_CONSTANT_MINUSONE_HUM = -1;     
	public string polId{get;set;} 	
   /*
     *Method Name    : init
     * Description   : This Method is used get account id and interaction id from url      
     * Return Type   : None
     * Parameters    : None
    */
    public void init()
    {                
        try
        {                
            listRecommendationActionIds = new list<string>();
			listcommandRecActionIds= new list<string>();
			mapOfRecommendationActionsIdWithType = new Map<String, String>();  
			isContactHandlingAlert = false;
			isGuidanceAlert = false;  
			isCommandAlert=false;
			sFirstTimeCaller = 'true';          
			sAccountPageName = ApexPages.currentPage().getParameters().get('sPageName');
			sAccountPageName = sAccountPageName != null ? sAccountPageName.escapeHtml4() : sAccountPageName;
			sOtherPageName= ApexPages.currentPage().getParameters().get('sOtherPageName');
			sOtherPageName = sOtherPageName != null ? sOtherPageName.escapeHtml4() : sOtherPageName;
			sAccountId = ApexPages.currentPage().getParameters().get('Id');     
			sAccountId = sAccountId != null ? sAccountId.escapeHtml4() : sAccountId;
			polId= ApexPages.currentPage().getParameters().get('polId');
			polId = polId != null ? polId.escapeHtml4() : polId;
			Boolean bCalledFromLightning = false;
             
            AlertDetails_DTO_HUM viewAlertDetails = ViewAlerts_Logic_HUM.init(sAccountPageName, sOtherPageName, sAccountId, polId, URLString, bCalledFromLightning);
                        
            listRecommendationActionIds = viewAlertDetails.listRecommendationActionIds;
            
            mapOfRecommendationActionsIdWithType = viewAlertDetails.mapOfRecommendationActionsIdWithType;
            isContactHandlingAlert = viewAlertDetails.isContactHandlingAlert;
            isGuidanceAlert = viewAlertDetails.isGuidanceAlert;
            sFirstTimeCaller = viewAlertDetails.sFirstTimeCaller;
            lstRecommendationActionId = viewAlertDetails.lstRecommendationActionId;
            interactionId = viewAlertDetails.interactionId;
            bInteractionId = viewAlertDetails.bInteractionId;
            tabId = viewAlertDetails.tabId;
            enterpriseId = viewAlertDetails.enterpriseId;
            sViewAccountOrMember = viewAlertDetails.sViewAccountOrMember;
            sPageName = viewAlertDetails.sPageName;
            sAge = viewAlertDetails.sAge;
            sInquiuredAboutAge = viewAlertDetails.sInquiuredAboutAge;            
            sFirstTimeCaller = viewAlertDetails.sFirstTimeCaller;
            isCommandAlert = viewAlertDetails.isCommandAlert;
            listcommandRecActionIds = viewAlertDetails.listcommandRecActionIds;
            sAlertCount = viewAlertDetails.sAlertCount;
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e,'NBADisplayRecommendationSection_C_HUM','init');
        }
    }
     
     /*
     *Method Name    : updateRecommnedationBasedOnAction
     * Description   : This Method is used get Alert after manually closing the panel   
     * Return Type   : None
     * Parameters    : None
    */
    public pagereference updateRecommnedationBasedOnAction(){
		AlertDetails_DTO_HUM viewAlertDetails = ViewAlerts_Logic_HUM.updateRecommnedationBasedOnAction(lstRecommendationActionId, false);
        isGuidanceAlert = viewAlertDetails.isGuidanceAlert;
        isContactHandlingAlert = viewAlertDetails.isContactHandlingAlert;
        listRecommendationActionIds = viewAlertDetails.listRecommendationActionIds;
        bInteractionId = viewAlertDetails.bInteractionId;
        sAlertCount = viewAlertDetails.sAlertCount;
        return null;
     }
}
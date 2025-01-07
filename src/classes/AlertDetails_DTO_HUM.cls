/*******************************************************************************************************************************************************
Apex Class Name  : AlertDetails_DTO_HUM
Version          : 1.0
Created Date     : 06/30/2022
Function         : This class is wrapper class to serialize Data Alerts
Test Class       : AccountDetailViewAlerts_LT_HUM
Modification Log :

* Developer Name            Review Number           Date                       Description
-----------------------------------------------------------------------------------------------------------------------------------------------------
* Abhishek Mangutkar                              06/30/2022                  Original version
************************************************************************************************************************************************/

public With Sharing class AlertDetails_DTO_HUM { 
    public string lstRecommendationActionId{set;get;}
    public string interactionId{set;get;}
    public String enterpriseId{set;get;}  
    public Boolean bInteractionId{set;get;}    
    public Boolean tabId{set;get;}  
    public Integer sAlertCount{set;get;}                         
    public list<string> listRecommendationActionIds{set;get;}
    public Boolean isContactHandlingAlert{set;get;} 
    public Boolean isGuidanceAlert{set;get;}                     
    public Map<String, String> mapOfRecommendationActionsIdWithType{set;get;}      
    public string sViewAccountOrMember{set;get;}
    public string sPageName{set;get;} 
    public String sAge {get;set;} 
    public Integer sInquiuredAboutAge;
    public string sFirstTimeCaller{get;set; }
    public Boolean isError {get; set;}
    public Boolean isCommandAlert {get; set;}
    public list<String> listcommandRecActionIds {get; set;}
    public Boolean loadCommandAlert{get; set;}
}
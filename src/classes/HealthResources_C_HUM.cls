/*****************************************************************************************************
Apex Class Name  : HealthResources_C_HUM
Version   : 1.0 
Created Date  :  03/17/2020  
Function  : Controller to fetch Case care manager details

Modification Log :
* Developer                 Code Review         Date                        Description
* -----------------------------------------------------------------------------------------------------                 
* Imran                                       03/17/2020                  Original Version   
* Imran                                       06/29/2020                  Req-1129152- Add comments column to the group's Health Resources page   
* Prasanthi Kandula                           01/04/2021                  US-1708279 Incident INC0759569 Fix
* Suraj Patil								  04/09/2021				  REQ - 1905877 - PR00094226 - CR AEP readiness - Low Security Issues 
*******************************************************************************************************/
public with sharing class HealthResources_C_HUM{ 
    public String sGroupName {get;set;}   
    public String sColumnNameEligible {get;set;}
    public String sColumnHeaderEligible{get;set;}
    public String sId {get;set;}
    public String sAttachKey {get; set;}
    public String sLevel {get; set;}
    public String sMessage {get; set;}
    public boolean isRendered {get;set;}    
    public static string sGroupId{ get; set; }
    public static string sPlatformCode{get;set;}  
    public String standardMapvalue{ get; set; }{standardMapvalue = ''; }
    
    
     /*
    * Method Name   :  HealthResources_C_HUM
    * Description   :  This is the constructor of the class 
    * Return Type   :  NA
    * Parameters    :  NA
    */
    public HealthResources_C_HUM()
    {    
        sLevel ='';
        sMessage = '';
        isRendered = true;
        sId = ApexPages.currentPage().getParameters().get('Id');        
        if(sId !=null && sId !='')
        {
            List<Policy__c> lstPolicy = [SELECT Id, Group_Name__r.Group_Number__c, Group_Name__r.source_customer_key__c, Group_Name__r.Enterprise_ID__c,Group_Name__r.Name, Platform__c FROM Policy__c where id =: ApexPages.currentPage().getParameters().get('Id')  or Group_Name__c =: ApexPages.currentPage().getParameters().get('Id') limit 1];                                           
            if(lstPolicy != null && !lstPolicy .isEmpty())
            {
                sGroupName = lstPolicy [0].Group_Name__r.Name;                  
                if(String.isNotBlank(lstPolicy [0].Group_Name__r.source_customer_key__c))
                {                         
                    sGroupId =  lstPolicy [0].Group_Name__r.source_customer_key__c;
                    sPlatformCode = lstPolicy [0].Platform__c;
                }
                //Assign Enterprise ID
                sAttachKey = lstPolicy [0].Group_Name__r.Enterprise_ID__c;                          
            }
            //Eligible
            Map<String, Map<String, Object>> standardMapForLogging = new Map<String, Map<String, Object>>();    
            sColumnHeaderEligible = json.serialize(new List <String> {'Program Name','Intervention Vendor','Begin Date','End Date','Comments'});
            sColumnNameEligible = json.serialize(new List <String> {'ProgramName','InterventionVendor','BeginDate','EndDate','Comments'});
            
            //Values required for Logging            
        standardMapForLogging.put('Enterprise ID', new Map<String, Object>{'Enterprise ID' => sAttachKey});
        standardMapvalue = JSON.serialize(standardMapForLogging);
        }
     }
  
/*
    * Method Name   :  getHealthResourcesRequest
    * Description   :  This method is used to get the data for Clinical Nurse Data from the Service.
    * Return Type   :  object 
    * Parameters    :  NA
*/
    @RemoteAction
    public static Continuation getHealthResourcesRequest(string sGroupId , string sPlatformCode) 
    {           
        return HealthResources_S_HUM.getHealthResourcesRequest(sGroupId , sPlatformCode);    
    }
    
    /**
    * This continuation invoked method generates list of .
    * @Name 
    * @param    
    * @return   
    */
    public static object parseResponseHealthResources(List<string> labels, object State)
    {    
        return HealthResources_S_HUM.parseResponseHealthResources(labels, State);      
    } 
  
    
    /**
    * This Method show the message when no record found 
    * @Name showPageMessage
    * @param    NA
    * @return   Void 
    */
    public void showPageMessage() {
        if(sLevel =='INFO') 
        {
            ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.info, sMessage));
            isRendered = false;            
        }
        else if(sLevel =='FAILURE')
        {
            ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.error, sMessage));  
            isRendered = false;
        }
    }

}
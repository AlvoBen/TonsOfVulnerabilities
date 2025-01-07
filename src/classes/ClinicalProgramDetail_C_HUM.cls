/*****************************************************************************************************
Apex Class Name  : ClinicalProgramDetail_C_HUM
Version   : 1.0 
Created Date  :  10/23/2018  
Function  : Controller to fetch Case care manager details

Modification Log :
* Developer                 Code Review         Date                        Description
* -----------------------------------------------------------------------------------------------------                 
* Sudhendrasimha Pappu                         10/23/2018                  Original Version   
* Sagar MN                                     10/23/2018                  REQ- 377400 Added Constructor 
* Buvaneswaran M                               10/26/2018 
* Sagar MN                                     10/23/2018                  REQ - 380791 - Build Clinical Program Logic
* Sagar MN                                     06/21/2019                  REQ - 367091 - Fix Extra Columns in Eligible Table
* Asish Behera                                 06/20/2019                  REQ - 367091 - Program Code column added
* Imran                                        04/14/2020                  REQ - 891352 - Table updated
* Ranjeeth N                                   05/18/2020                  DF-746 null column removal on switch off
* Suraj Patil								   04/09/2021				   REQ - 1905147 - PR00094226 - CR AEP readiness - Low Security Issues 
* Pinky Vijur                               08/19/2021                 REQ - 2080855 - T1PRJ0052463 - MF 6 -  Display Palliative Care program participation details in CRM - Pallitus Health Partners (CRM)
*******************************************************************************************************/
public with sharing class ClinicalProgramDetail_C_HUM { 
    public String sPolicyMemberName {get;set;}
    public String sColumnNameEnroll {get;set;}
    public String sColumnHeaderEnroll{get;set;}
    public String sColumnNameEligible {get;set;}
    public String sColumnHeaderEligible{get;set;}
    public String sPolicyId {get;set;}
    public String sAttachKey { get; set; }
    public String sLevel {get; set;}
    public String sMessage {get; set;}
    public boolean isRendered {get;set;}
    public String sMemberID {get;set;}
    public String sMemberGenKey {get;set;}
    public boolean bClinicalSwitch {get;set;}
    
    public ClinicalProgramDetail_C_HUM ()
    {
        sLevel ='';
        sMessage = '';
        isRendered = true;
        sPolicyId = ApexPages.currentPage().getParameters().get('Id');
     if(sPolicyId !=null && sPolicyId !='')
     {
            //String sMemberId = ApexPages.currentPage().getParameters().get('Id');            
            List<Policy_Member__c> lstPolicyMember = [Select Id,Member__r.Name,Name ,Member__r.Enterprise_ID__c, Member__r.Mbr_Gen_Key__c From Policy_Member__c
                                                      where ID =:ApexPages.currentPage().getParameters().get('Id') limit 1];
            if(lstPolicyMember != null && !lstPolicyMember.isEmpty())
            {
                //Member Name
               sPolicyMemberName = lstPolicyMember[0].Member__r.Name;  
               //Member Gen Key
                
                if(String.isNotBlank(lstPolicyMember[0].Member__r.Mbr_Gen_Key__c))
                {                    
                    sMemberID = lstPolicyMember[0].Member__r.Mbr_Gen_Key__c;
                    sMemberGenKey = 'membergenkey';
                }else{
                    sMemberID = lstPolicyMember[0].Member__r.Enterprise_ID__c;
                    sMemberGenKey = 'EnterprisePersonID';
                }
                //Assign Attachment key
                sAttachKey = lstPolicyMember[0].Member__r.Enterprise_ID__c;  
                
            }
                     
                         
            //Enroll
            if (HUMUtilityHelper.isCRMFunctionalityON('2080855')) {
                sColumnHeaderEnroll = json.serialize(new List <String> {'Program Name','Intervention Vendor','Care Manager Name','Phone Number','Begin Date','End Date'});
            	sColumnNameEnroll = json.serialize(new List <String> {'programName','InterventionVendor','CareManagerName','phoneNumber','BeginDate','EndDate'});
            }
            else {
                sColumnHeaderEnroll = json.serialize(new List <String> {'Program Name','Care Manager Name','Phone Number','Begin Date','End Date'});
                sColumnNameEnroll = json.serialize(new List <String> {'programName','CareManagerName','phoneNumber','BeginDate','EndDate'});
            }
            bClinicalSwitch= CRMFunctionalityenabler_H_HUM.Checkflagvalue('Activate_GetClinicalV2_Service');
            if (bClinicalSwitch) 
            {
              //Eligible
              sColumnHeaderEligible = json.serialize(new List < String > {'Program Name','Intervention Vendor','Begin Date','End Date','Comments'});
              sColumnNameEligible = json.serialize(new List < String > {'sprogramName','InterventionVendor','BeginDate','EndDate','Comments'});
            }
            else
            {
            //Eligible
            sColumnHeaderEligible = json.serialize(new List < String > {'Program Name','Intervention Vendor','Program Code'});
            sColumnNameEligible = json.serialize(new List < String > {'sprogramName','InterventionVendor','ProgramCode'});           
            }
   
        }
 }
  
/*
    * Method Name   :  getClinicalNurseCareData - Eligible programs
    * Description   :  This method is used to get the data for Clinical Nurse Data from the Service.
    * Return Type   :  object 
    * Parameters    :  NA
*/
    @RemoteAction
    public static Continuation getClinicalNurseCareData(String MemberId , String MemberGenKey) 
    {  
        return ClinicalProgramDetail_S_HUM.sendClinicalProgramsRequest(MemberId , MemberGenKey);    
    }
    
    /**
    * This continuation invoked method generates list of .
    * @Name 
    * @param    
    * @return   
    */
    public static object parseResponseClinicalProgram(List < string > labels, object State)
    {
        return ClinicalProgramDetail_S_HUM.parseResponseClinicalProgram(labels, State);      
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
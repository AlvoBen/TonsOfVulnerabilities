/*****************************************************************************************************
Apex Class Name : CaseInteraction_C_HUM
Version           : 1.0 
Created Date      : 10/10/2017
Function          : Controller to fetch Interactions related to cases
Test Class      : CaseInteraction_T_HUM

Modification Log :
* Developer                 Code Review         Date                        Description
* -----------------------------------------------------------------------------------------------------                 
* Sumit Pharande                                10/10/2017                   Original Version
* Pradeepkumar Dani          149126             11/17/2017                     re-factoring
* Srivinas Damera            210924             04/05/2018                   CA Ticket 7363363
* Prudhvi Pamarthi                              02/06/2019                 REQ - 361155 - Not allowing case creation on Legacy Deleted policies
* Santhosh Ganji                                04/19/2019                 REQ - 386935 - Case Detail Restrictions (MED vs non-MED cases)—READ ONLY features
* Santhosh Ganji                                05/24/2019                  REQ - 400147 - Humana Pharmacy Web Chat users Access to cases
* Sai Kumar Boga                                05/28/2020                 US - 892853 - Fixed CA Ticket - 8910137 - Error message "URL no longer exists" in the interaction screen from a case using softphone
* Santhi Mandava                                01/29/2021                  User Story 1791226: PR00094254 - MF 4- AEP Performance - SF Limits - Sync CPU Time Limit Analysis - Case Details
* Gowthami Thota									              07/29/2021					       US - 2543348 - T1PRJ0002606/PR00094268  - CR01 CSI 1922  - Performance improvement of Case detail and Edit page
* Ravikanth Samudrala 						              05-30-2023                   US-4531288 - T1PRJ0901110 MF1.11 - Additional Fields (blue marked) to the CRM PDF - CRM and MHK 
*******************************************************************************************************/

public with sharing class CaseInteraction_C_HUM
{
    public string columnName {get;set;}
    public string columnHeader {get;set;}  
    public map<String, HUMSalesforceFieldIds__c> mapSalesforceFieldIds = HUMSalesforceFieldIds__c.getAll();
    public String sKeyPrefix  {get;set;}
    public String sCaseSalesForceId  {get;set;}
    public Boolean bdisableButton {get; set;}
    public Boolean blegacyDeleteCheck;
    public Id toGetCaseId;
    public Boolean hideTheButton {get;set;}
    public Boolean sPharmacyUser {get;set;}
    private static Case caseRec;
    
    /**
    *  Constructor for the class
    *  @name CaseInteraction_C_HUM
    *  @param NA
    *  @return void
    *  @throws NA
    */ 
    public CaseInteraction_C_HUM()
    {
        columnName = json.serialize(new list<String>{'InteractionNumber' ,'InteractionOrigin' ,'InteractionWith', 'InteractionWithType' , 'CreatedDate','CreatedBy','CreatedByQueue','AssociatedtoCaseDate'});
        columnHeader = json.serialize(new list<String>{'Interaction Number' ,'Interaction Origin' ,'Interacting With', 'Interacting With Type' , 'Created Date','Created By','Created By Queue','Associated to Case Date'});
        sKeyPrefix = Case_Interaction__c.sObjectType.getDescribe().getKeyPrefix();
        sCaseSalesForceId = mapSalesforceFieldIds.get(System.label.HUMsmallId).Case_in_Case_Interaction__c;
        toGetCaseId =  ApexPages.currentPage().getParameters().get('Id');
        if(toGetCaseId != null) caseRec = [SELECT Id, Recordtype.name, Policy_Member__c, Policy_Member__r.Product__c,Owner_Queue__c,Created_By_Queue__c,AccountId,Account.ETL_Record_Deleted__c,Policy_Member__r.ETL_Record_Deleted__c,  Status,Origin,Type FROM Case where id= :toGetCaseId];
        bdisableButton = verifyLegacyDelete();  
        sPharmacyUser = false;
        sPharmacyUser= (Pharmacy_H_HUM.isPharmacyUser() != CaseDetailConstants_HUM.NONPHARMACY_USER_HUM);
        if(toGetCaseId != null) hideTheButton = verifyMedicareCase(toGetCaseId);
    }
    
    /**
    *  Gets Datatable details and  builds json string
    *  @name getInteractionDTOlist
    *  @param NA
    *  @return void
    *  @throws NA
    */ 
    @RemoteAction
    public static list <CaseInteraction_DTO_HUM> getInteraction(String sCaseRecordId) 
    {
        return CaseInteraction_C_HUM.getInteractionDTOlist(sCaseRecordId);
    }
    
    /**
    *  Gets list of Case Interactions for given case using wrapper class list
    *  @name getInteractionDTOlist
    *  @param NA
    *  @return InteractionList
    *  @throws NA
    */
    public static list <CaseInteraction_DTO_HUM> getInteractionDTOlist(String caseRecId)
    {
        list<CaseInteraction_DTO_HUM> lstCaseint = new list<CaseInteraction_DTO_HUM>();
        list<Case_Interaction__c> intList = [SELECT CreatedDate,CreatedBy.Name,Interaction__r.Created_By_Queue__c,Interaction__r.Name,Interaction__r.CreatedDate, Interaction__r.Interaction_Origin__c,Interaction__r.Interacting_With_type__c,Interaction__r.Interacting_With__r.Name,Id,Interaction__r.Call_Recording_Link__c,Interaction__r.Genesys_Interaction_ID__c FROM Case_Interaction__c WHERE Case__c =:caseRecId ORDER BY Id DESC];
        for(Case_Interaction__c cInt : intList )
        {
            CaseInteraction_DTO_HUM oInt = new  CaseInteraction_DTO_HUM ();
            oInt.InteractionNumber = CInt.Interaction__r.Name+ CaseDetailConstants_HUM.sSrcUp + CaseDetailConstants_HUM.sForwardSlash + CInt.Interaction__r.Id;
            oInt.InteractionOrigin = cInt.Interaction__r.Interaction_Origin__c;
            oInt.InteractionWith = cInt.Interaction__r.Interacting_With__r.Name;
            oInt.InteractionWithType = cInt.Interaction__r.Interacting_With_type__c;
            oInt.CreatedDate = CInt.Interaction__r.CreatedDate.format(CaseDetailConstants_HUM.HUMDateTimeFormat);
            oInt.CreatedBy = cInt.CreatedBy.Name + CaseDetailConstants_HUM.sSrcUp + cInt.CreatedBy.Id   ;
            oInt.CreatedByQueue = cInt.Interaction__r.Created_By_Queue__c;
            oInt.AssociatedtoCaseDate= CInt.CreatedDate.format(CaseDetailConstants_HUM.HUMDateTimeFormat);
            oInt.GenesysInteractionID = cInt.Interaction__r.Genesys_Interaction_ID__c;
            oInt.CallRecordingLink = cInt.Interaction__r.Call_Recording_Link__c;
            lstCaseint.add(oInt);
        }
        return lstCaseint;
    }

    /**  
    * This method is to check whether new case interaction and create new interaction buttons should be disabled.
    * 
    * @return   Boolean
    */
    public boolean verifyLegacyDelete()
    {
       bdisableButton = false;
       blegacyDeleteCheck = false;
       
        if(caseRec.Account.ETL_Record_Deleted__c == true) blegacyDeleteCheck = true;
        else if (caseRec.Policy_Member__r.ETL_Record_Deleted__c == true) blegacyDeleteCheck = true;
        if(blegacyDeleteCheck && caseRec.Status=='Closed') bdisableButton = true;
        return bdisableButton;
     }
     
     public static boolean verifyMedicareCase(string scaseid)
     {
       Set<String> wqueues = new Set<String>{CaseDetailConstants_HUM.WORK_QUEUE_PROVIDER_PAYMENT_INTEGRITY,CaseDetailConstants_HUM.WORK_QUEUE_OTHER_INSURANCE_INVESTIGATION,CaseDetailConstants_HUM.WORK_QUEUE_RSO_ORAL_GRIEVANCE_OVERSIGHT,CaseDetailConstants_HUM.WORK_QUEUE_MEDICARE_MEMBER_COMMUNICATIONS};
       Set<String> chatQueues = new Set<String>(CaseDetailConstants_HUM.WEB_CHAT_WQUEUES); 
       Boolean bPharmacyUser = false;
        if(!Test.isRunningTest()){
          if(caseRec != null && (caseRec.Recordtype.name.containsIgnoreCase(CaseDetailConstants_HUM.HumHPRecType) || (caseRec.Policy_Member__c != null && caseRec.Policy_Member__r.Product__c == CaseDetailConstants_HUM.MEDICARE_POLICY) || (chatQueues.contains(caseRec.Owner_Queue__c) && caseRec.Origin == CaseDetailConstants_HUM.WEB_CHAT_ORIGIN)))
          {
            if(caseRec.Recordtype.name.containsIgnoreCase(CaseDetailConstants_HUM.HumHPRecType) || wqueues.contains(caseRec.Owner_Queue__c) || wqueues.contains(caseRec.Created_By_Queue__c)  || (chatQueues.contains(caseRec.Owner_Queue__c) && caseRec.Origin == CaseDetailConstants_HUM.WEB_CHAT_ORIGIN))
            {
              bPharmacyUser = true;
            }
          }
        }
       
       return bPharmacyUser;
     }
}
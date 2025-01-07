/***********************************************************************************************************
Apex class Name    : CaseLinkComponent_LC_HUM
Version            : 
Function           : Gets Case History Records as Wrapper to LWC caller   
Created Date       : 11/03/2022 
Test Class         : 

Modification Log:
 *   Developer                   Code Review             Date               Description
 * ----------------------------------------------------------------------------------------------------------
 * Dinesh Subramaniyan								11/04/2022				Initial Version
 * Dinesh Subramaniyan								11/13/2022				Making the linked cases available for Pharmacy users
*************************************************************************************************************/
public with sharing class CaseLinkComponent_LC_HUM {
    public CaseLinkComponent_LC_HUM() { }


    /* Method Name : getLinkedCasesDTOList
    *  Description : Gets list of Case Articles for given case using wrapper class list
    *  @param sCaseRecordId
    *  @return void
    *  @throws NA */
    
    @AuraEnabled
    public static list<LinkedCase_Wrapper_HUM> getLinkedCasesDTOList(String sCaseRecordId)
    {
        Boolean isCCS = isUnlinkVisible();
        Boolean isPharmacyUser = checkPharmacyUser(sCaseRecordId);
        List<LinkedCase_Wrapper_HUM> lstlinkedCases = new List<LinkedCase_Wrapper_HUM>();
        
        for(Case_Junction__c objCaseJunction : [Select CreatedBy.FirstName,CreatedBy.LastName,Case__r.CTCI_List__r.Classification__c,Case__c,CreatedDate,Case_Master__c,Case__r.Id,Case__r.CaseNumber,
        Case__r.Created_By_Queue__c,CreatedBy.Name,Case__r.CreatedBy.Name,Case__r.CreatedBy.id,Case__r.CTCI_List__r.Intent__r.Name,Case__r.ClosedDate,
        Case__r.Interacting_About__c,Case__r.Status,Case__r.CreatedDate,Case__r.CTCI_List__r.Classification__r.name,IsLinked__c,IsCloneCase__c,
        Case_Master__r.Id,Case_Master__r.caseNumber,Case_Master__r.Created_By_Queue__c,Case_Master__r.CreatedBy.id,Case_Master__r.CreatedBy.Name,
        Case_Master__r.CTCI_List__r.Intent__r.Name,Case_Master__r.ClosedDate,Case_Master__r.Interacting_About__c,
        Case_Master__r.Status,Case_Master__r.CreatedDate,Case_Master__r.CTCI_List__r.Classification__r.name,Work_Queue__r.Name from Case_Junction__c where ((IsLinked__c=true) AND (Case__c=:sCaseRecordId OR Case_Master__c=:sCaseRecordId)) ORDER BY Id DESC limit :GLOBAL_CONSTANT_HUM.NUMBER_CONSTANT_SEVENTHOUSAND_HUM ])
        {
           	LinkedCase_Wrapper_HUM objCaseJwrapper = new LinkedCase_Wrapper_HUM();
            if(objCaseJunction .Case_Master__c == sCaseRecordId)
            {
                objCaseJwrapper.sCreatedByName         =  objCaseJunction.CreatedBy.Name;
                objCaseJwrapper.sCaseNumber       	   =  objCaseJunction.Case__r.CaseNumber;
                objCaseJwrapper.sCaseId              =    objCaseJunction.Case__r.Id;
                objCaseJwrapper.sCaseCreatedByQueue    =  objCaseJunction.Case__r.Created_By_Queue__c;
                objCaseJwrapper.sCaseIntent            =  objCaseJunction.Case__r.CTCI_List__r.Intent__r.Name ;
                objCaseJwrapper.sCaseStatus            =  objCaseJunction.Case__r.Status;
                objCaseJwrapper.sCaseClassification    =  objCaseJunction.Case__r.CTCI_List__r.Classification__r.name;
                objCaseJwrapper.dCreatedDate           =  String.valueof(objCaseJunction.Case__r.CreatedDate.format(HumConstants_LWC.HUMDateTimeFormat));
                objCaseJwrapper.sCreatedByName		   =  objCaseJunction.Case__r.CreatedBy.Name+HumConstants_LWC.sSrcUp+objCaseJunction.Case__r.createdby.id;
                objCaseJwrapper.sCaseMasterId		   =  objCaseJunction.Case__r.Id;
                objCaseJwrapper.sCaseMasterCaseNumber  =  objCaseJunction.Case_Master__r.caseNumber;
                if(isCCS || isPharmacyUser)objCaseJwrapper.sAction = true;

            }
            else
            {
                
                objCaseJwrapper.sCreatedByName         =  objCaseJunction.CreatedBy.Name;
                objCaseJwrapper.sCaseNumber            =  objCaseJunction.Case_Master__r.CaseNumber;
                objCaseJwrapper.sCaseId              =    objCaseJunction.Case_Master__r.Id;
                objCaseJwrapper.sCaseCreatedByQueue    =  objCaseJunction.Case_Master__r.Created_By_Queue__c;
                objCaseJwrapper.sCaseIntent            =  objCaseJunction.Case_Master__r.CTCI_List__r.Intent__r.Name;
                objCaseJwrapper.sCaseStatus            =  objCaseJunction.Case_Master__r.Status;
                objCaseJwrapper.sCaseClassification    =  objCaseJunction.Case_Master__r.CTCI_List__r.Classification__r.name;
                objCaseJwrapper.dCreatedDate           = String.valueof(objCaseJunction.Case_Master__r.CreatedDate.format(HumConstants_LWC.HUMDateTimeFormat));
                objCaseJwrapper.sCreatedByName		   =  objCaseJunction.Case_Master__r.CreatedBy.Name+HumConstants_LWC.sSrcUp+objCaseJunction.Case_Master__r.createdby.id;
                objCaseJwrapper.sCaseMasterId		   =  objCaseJunction.Case_Master__c;
                objCaseJwrapper.sCaseMasterCaseNumber  =  objCaseJunction.Case__r.CaseNumber;
                if(isCCS || isPharmacyUser)objCaseJwrapper.sAction = true;

            }
                        lstlinkedCases.add(objCaseJwrapper);
        }
        return lstlinkedCases;
    }


    /* Method Name : getCaseLinkedData
    *  Description : Gets Datatable details and  builds json string
    *  @param CaseRecordId
    *  @return void
    *  @throws NA*/

    @AuraEnabled
    public static string getCaseLinkedData(String CaseRecordId){
        try {
            if(String.isNotBlank(CaseRecordId)){
                
                return JSON.serialize(getLinkedCasesDTOList(CaseRecordId));
            }
            return null;
        } catch (Exception e) {
            throw new AuraHandledException(e.getMessage());
        }
    }

   

    /* Method Name : isUnlinkVisible
     * Description : Gets visibility for linked cases 
     *  @param NA
     *  @return Boolean
     *  @throws NA*/

    public static Boolean isUnlinkVisible() 
    {
     
        String sProfileName = [SELECT Id, Name FROM Profile WHERE Id =:UserInfo.getProfileId()].Name;
        Boolean hasUnlinkAction = false;
        if(sProfileName == System.Label.HUMAgencyCCSupervisor)
        {
            hasUnlinkAction = true; 
        } 
        return hasUnlinkAction;
    }
    
        public static Boolean checkPharmacyUser(String aCaseid) 
    {
        Set<String> wqueues = new Set<String>{HumConstants_LWC.WORK_QUEUE_PROVIDER_PAYMENT_INTEGRITY,HumConstants_LWC.WORK_QUEUE_OTHER_INSURANCE_INVESTIGATION,HumConstants_LWC.WORK_QUEUE_RSO_ORAL_GRIEVANCE_OVERSIGHT,HumConstants_LWC.WORK_QUEUE_MEDICARE_MEMBER_COMMUNICATIONS};
        Set<String> chatQueues = new Set<String>(HumConstants_LWC.WEB_CHAT_WQUEUES); 
		Boolean bshowTheAction = false;
        String sPharmacyUser =  Pharmacy_LH_HUM.isPharmacyUser();
        Case oCase = [select id,CaseNumber,RecordType.name,Member_plan_id__c,Member_plan_id__r.Product__c,Owner_Queue__c,Created_By_Queue__c,Origin from case where id=:aCaseid];
        if(oCase != null && (oCase.Recordtype.name.containsIgnoreCase(HumConstants_LWC.HumHPRecType) || (oCase.Member_plan_id__c != null && oCase.Member_plan_id__r.Product__c == HumConstants_LWC.MEDICARE_POLICY) || (chatQueues.contains(oCase.Owner_Queue__c) && oCase.Origin == HumConstants_LWC.WEB_CHAT_ORIGIN)))
        {
           if(oCase.Recordtype.name.containsIgnoreCase(HumConstants_LWC.HumHPRecType) || wqueues.contains(oCase.Owner_Queue__c) || wqueues.contains(oCase.Created_By_Queue__c) || (chatQueues.contains(oCase.Owner_Queue__c) && oCase.Origin == HumConstants_LWC.WEB_CHAT_ORIGIN))
           {
              bshowTheAction = true;
            }
        }
        Boolean bPharmacyUser= (oCase != null && sPharmacyUser == System.Label.PharmacyUserSupervisorYes_HUM && bshowTheAction);
        return bPharmacyUser;
    }
    
     @AuraEnabled
    public static string unlinkCaseJunction(String sCaseRecordId,String sCaseId) 
    {
        
        string returnRT = '';
        try{
            Case_Junction__c obj =[Select Case__c,Case_Master__c,IsLinked__c from Case_Junction__c where IsLinked__c=true AND ((Case__c=:sCaseId AND Case_Master__c=:sCaseRecordId )OR (Case__c=:sCaseRecordId AND Case_Master__c=:sCaseId)) Limit: 1 ];
            if(obj != null)
            {
                if(Schema.SObjectType.Case_Junction__c.fields.IsLinked__c.isUpdateable()) obj.IsLinked__c=false;
				update obj;
            }
                            returnRT = 'Successfully updated';

        }
        catch (Exception ex)
        {
             throw new AuraHandledException(ex.getMessage());
            
        }
        return returnRT;
    }



}
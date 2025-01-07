/*****************************************************************************************************
Apex Class Name : LinkedCasesExtension_C_HUM
Version         : 1.0 
Created Date    : 10/10/2017
Function        : Controller to fetch linked cases
Test Class      : LinkedCasesExtension_T_HUM

Modification Log :
* Developer                 Code Review         Date                        Description
* -----------------------------------------------------------------------------------------------------                 
* Prasad Jandhyala          149179              10/10/2017                   Original Version
* Pradeepkumar Dani         149179              11/17/2017                   re-factoring
* Rakesh Patni				153396				11/30/2017					 Changes for REQ-344522
* Prasad Jandhyala		        				11/30/2017					 Fix for Defect 335476.
* Praveen Kumar Parimi		306972        		11/06/2018			         Changing the mapping for CreatedDate and Closed Date for REQ - 378917 aka CA 7865878.
* Santhosh Ganji                                04/19/2019                   REQ - 386935 - Case Detail Restrictions (MED vs non-MED cases)—READ ONLY features
* Santhosh Ganji                                05/24/2019                   REQ - 400147 - Humana Pharmacy Web Chat users Access to cases
* Muralidhar Kollu                              02/10/2020                   837102 - PR00086163 - CA Tickets - SF - TECH PLT - Fix CA Ticket 8252879
* Vinay Lingegowda				                08/26/2020			        User Story 1364284: PR00091574 - IR 5050-OGO- Add clone identifier on Case detail page (Clone case)- (CRM)	
* Vinay Lingegowda                              10/10/2020                  USER STORY 1544779 PR00091574 - IR 5050 - OGO - Tech Debt- Remove switch for OGO on/off(CRM)
* Mithra Bharadwaj                                                          US#1522825 SonarQube Changes
*******************************************************************************************************/

public with sharing class LinkedCasesExtension_C_HUM 
{     
    public string columnName {get;set;}
    public string columnHeader {get;set;}  
	public string columnTitles {get;set;}  
    public Integer orderColumnNumber{get;set;}
	public string oCaseID{get;set;}
    
    public static boolean bOGOUser{get;set;}

    /**
    *  Constructor for the class
    *  @name CaseArticleExtension_C_HUM
    *  @param NA
    *  @return void
    *  @throws NA
    */ 
    public LinkedCasesExtension_C_HUM()
    {   
	    oCaseID =  ApexPages.currentPage().getParameters().get('Id'); 
        bOGOUser = UtilityHelper_H_AHT_HUM.checkCustomPermissionAccess('Oral_Grievance_Oversight_OGO');

        list<string> columnNameList = new list<string> {'sCaseCaseNumber',
            'sCaseClassification',
            'sCaseIntent',
            'sCaseInteractingAbout',
            'sCaseStatus',
            'sCreatedDate',
            'sClosedDate',
            'sCaseCreatedByName',
            'sCaseCreatedByQueue',           
            'slinkedDate',
            'slinkedby',
            'sWorkQueueName'};
        list<string> columnHeaderList = new list<string> {System.Label.HUMCaseID ,
            System.Label.HUMCaseClassification ,
            System.Label.HUMCaseIntent, 
            System.Label.HUM_About, 
            System.Label.HUMCaseStatus,
            System.Label.HUM_Opened,
            System.Label.HUM_Closed,
            System.Label.HUMCaseCreatedBy,
            System.Label.HUMCaseQueue,          
            System.Label.HUM_Linked_Date,
            System.Label.HUM_Linked_Case_Linked_By,
            System.Label.HUMCaseLinkedByQueue};
        list<string> lstColumnTitles = new list<string>{
            'Case Number',
            'Classification',
            'Intent',
            'Interacting About',
            'Status',
            'Date/Time Opened',
            'Date/Time Closed',
            'Created By',
            'Created By Queue',
            'Linked Date',
            'Linked By',
            'Linked By Queue'};
        
        if(LinkedCasesExtension_C_HUM.isUnlinkVisible() || LinkedCasesExtension_C_HUM.checkPharmacyUser(string.escapeSingleQuotes(oCaseID))) 
        {
            columnNameList.add(0,'sAction');
            columnHeaderList.add(0,'Action');
            lstColumnTitles.add(0,'Action');
            
            orderColumnNumber = 6;
        }
        else 
        {
            orderColumnNumber = 5;
        }

        if(bOGOUser){
            columnNameList.add(orderColumnNumber+2,'sIsCloneCase');
            columnHeaderList.add(orderColumnNumber+2,'IsCloned');
            lstColumnTitles.add(orderColumnNumber+2,'IsCloned');
        }

        columnName = json.serialize(columnNameList);
        columnHeader = json.serialize(columnHeaderList);
        columnTitles = json.serialize(lstColumnTitles);
		
    }
    
    /**
    *  Gets visibility for lined cases
    *  @name isUnlinkVisible
    *  @param NA
    *  @return void
    *  @throws NA
    */
    public static Boolean isUnlinkVisible() 
    {
        String sProfileName = [SELECT Id, Name FROM Profile WHERE Id =:UserInfo.getProfileId()].Name;
        Boolean hasUnlinkAction = (sProfileName == System.Label.HUMAgencyCCSupervisor);
        return hasUnlinkAction;
    }
	
    public static Boolean checkPharmacyUser(String aCaseid) 
    {
        Set<String> wqueues = new Set<String>{GLOBAL_CONSTANT_HUM.WORK_QUEUE_PROVIDER_PAYMENT_INTEGRITY,GLOBAL_CONSTANT_HUM.WORK_QUEUE_OTHER_INSURANCE_INVESTIGATION,GLOBAL_CONSTANT_HUM.WORK_QUEUE_RSO_ORAL_GRIEVANCE_OVERSIGHT,GLOBAL_CONSTANT_HUM.WORK_QUEUE_MEDICARE_MEMBER_COMMUNICATIONS};
        Set<String> chatQueues = new Set<String>(GLOBAL_CONSTANT_HUM.WEB_CHAT_WQUEUES); 
		Boolean bshowTheAction = false;
        String sPharmacyUser =  Pharmacy_H_HUM.isPharmacyUser();
        Case oCase = [select id,CaseNumber,RecordType.name,Policy_Member__c,Policy_Member__r.Product__c,Owner_Queue__c,Created_By_Queue__c,Origin from case where id=:aCaseid];
        if(oCase != null && (oCase.Recordtype.name.containsIgnoreCase(GLOBAL_CONSTANT_HUM.HumHPRecType) || (oCase.Policy_Member__c != null && oCase.Policy_Member__r.Product__c == GLOBAL_CONSTANT_HUM.MEDICARE_POLICY) || (chatQueues.contains(oCase.Owner_Queue__c) && oCase.Origin == GLOBAL_CONSTANT_HUM.WEB_CHAT_ORIGIN)))
        {
           if(oCase.Recordtype.name.containsIgnoreCase(GLOBAL_CONSTANT_HUM.HumHPRecType) || wqueues.contains(oCase.Owner_Queue__c) || wqueues.contains(oCase.Created_By_Queue__c) || (chatQueues.contains(oCase.Owner_Queue__c) && oCase.Origin == GLOBAL_CONSTANT_HUM.WEB_CHAT_ORIGIN))
           {
              bshowTheAction = true;
            }
        }
        Boolean bPharmacyUser= (oCase != null && sPharmacyUser == System.Label.PharmacyUserSupervisorYes_HUM && bshowTheAction);
        return bPharmacyUser;
    }
	
    /**
    *  Gets Datatable details and  builds json string
    *  @name getLinkedCases
    *  @param sCaseRecordId
    *  @return void
    *  @throws NA
    */ 
    @RemoteAction
    public static List<LinkedCases_Wrapper_DTO_HUM> getLinkedCases(String sCaseRecordId)
    {
        return LinkedCasesExtension_C_HUM.getLinkedCasesDTOList(sCaseRecordId);
    }
    
    /**
    *  unlink Case 
    *  @name unlinkCase
    *  @param sCaseRecordId, sCaseId
    *  @return void
    *  @throws NA
    */
    @RemoteAction
    public static List<LinkedCases_Wrapper_DTO_HUM> unlinkCase(String sCaseRecordId,String sCaseId)
    {
        return LinkedCasesExtension_C_HUM.unlinkLinkedCases(sCaseRecordId,sCaseId);
    }
    
    /**
    *  Gets  LinkedCases 
    *  @name unlinkLinkedCases
    *  @param sCaseRecordId, sCaseId
    *  @return void
    *  @throws NA
    */
    public static list<LinkedCases_Wrapper_DTO_HUM> unlinkLinkedCases(String sCaseRecordId,String sCaseId) 
    {
        LinkedCasesExtension_C_HUM.unlinkCaseJunction(sCaseRecordId,sCaseId);
        return LinkedCasesExtension_C_HUM.getLinkedCasesDTOList(sCaseRecordId);
    }
    
    /**
    *  unlinks the Case
    *  @name unlinkCaseJunction
    *  @param sCaseRecordId, sCaseId
    *  @return void
    *  @throws NA
    */
    public static void unlinkCaseJunction(String sCaseRecordId,String sCaseId) 
    {
        Case_Junction__c obj =[Select Case__c,Case_Master__c from Case_Junction__c where IsLinked__c=true AND ((Case__c=:sCaseId AND Case_Master__c=:sCaseRecordId )OR (Case__c=:sCaseRecordId AND Case_Master__c=:sCaseId)) Limit: GLOBAL_CONSTANT_HUM.NUMBER_CONSTANT_ONE_HUM ];
        if(obj != null)
        {
            obj.IsLinked__c=false;
            update obj ;
        }
        return;
    }
    
    /**
    *  Gets list of Case Articles for given case using wrapper class list
    *  @name getAtricleWrapperList
    *  @param sCaseRecordId
    *  @return void
    *  @throws NA
    */
    public static list<LinkedCases_Wrapper_DTO_HUM> getLinkedCasesDTOList(String sCaseRecordId)
    {
        bOGOUser = UtilityHelper_H_AHT_HUM.checkCustomPermissionAccess('Oral_Grievance_Oversight_OGO');
        List<LinkedCases_Wrapper_DTO_HUM> lstlinkedCases = new List<LinkedCases_Wrapper_DTO_HUM>();
        Boolean isActionVisible = LinkedCasesExtension_C_HUM.isUnlinkVisible();
		Boolean isPharmacyUser = LinkedCasesExtension_C_HUM.checkPharmacyUser(sCaseRecordId);
        for(Case_Junction__c objCaseJunction1 : [Select CreatedBy.FirstName,CreatedBy.LastName,Case__r.CTCI_List__r.Classification__c,Case__c,CreatedDate,Case_Master__c,Case__r.Id,Case__r.CaseNumber,
                                                Case__r.Created_By_Queue__c,CreatedBy.Name,Case__r.CreatedBy.Name,Case__r.CTCI_List__r.Intent__r.Name,Case__r.ClosedDate,
                                                Case__r.Interacting_About__c,Case__r.Status,Case__r.CreatedDate,Case__r.CTCI_List__r.Classification__r.name,IsLinked__c,IsCloneCase__c,
												Case_Master__r.Id,Case_Master__r.caseNumber,Case_Master__r.Created_By_Queue__c,Case_Master__r.CreatedBy.Name,
                                                Case_Master__r.CTCI_List__r.Intent__r.Name,Case_Master__r.ClosedDate,Case_Master__r.Interacting_About__c,
                                                Case_Master__r.Status,Case_Master__r.CreatedDate,Case_Master__r.CTCI_List__r.Classification__r.name,Work_Queue__r.Name from Case_Junction__c where ((IsLinked__c=true) AND (Case__c=:sCaseRecordId OR Case_Master__c=:sCaseRecordId)) ORDER BY Id DESC limit :GLOBAL_CONSTANT_HUM.NUMBER_CONSTANT_SEVENTHOUSAND_HUM ])
        {
            LinkedCases_Wrapper_DTO_HUM objCaseJwrapper = new LinkedCases_Wrapper_DTO_HUM(objCaseJunction1,'','');
            if(objCaseJunction1 .Case_Master__c == sCaseRecordId)
            {
                objCaseJwrapper.sCaseId                =  objCaseJunction1.Case__r.Id;
                objCaseJwrapper.sCaseMasterId          =  objCaseJunction1.Case_Master__r.Id;
                String createdDate 					   =  objCaseJunction1.case__r.CreatedDate.format(CaseDetailConstants_HUM.HUMDateTimeFormat);
                objCaseJwrapper.sCreatedDate           =  createdDate;             
                objCaseJwrapper.bIsLinked              =  objCaseJunction1.IsLinked__c;
                objCaseJwrapper.sWorkQueueName         =  objCaseJunction1.Work_Queue__r.Name;
                objCaseJwrapper.sCreatedByName         =  objCaseJunction1.CreatedBy.Name;
                objCaseJwrapper.sCaseCaseNumber        =  objCaseJunction1.Case__r.CaseNumber;
                objCaseJwrapper.sCaseCreatedByQueue    =  objCaseJunction1.Case__r.Created_By_Queue__c;
                objCaseJwrapper.sCaseCreatedByName     =  objCaseJunction1.Case__r.CreatedBy.Name+',srcUp,' + objCaseJunction1 .Case__r.CreatedBy.Id;
                objCaseJwrapper.sCaseIntent            =  objCaseJunction1.Case__r.CTCI_List__r.Intent__r.Name ;
                objCaseJwrapper.dCaseClosedDate        =  objCaseJunction1.Case__r.ClosedDate;
                objCaseJwrapper.sClosedDate            =  objCaseJunction1.Case__r.ClosedDate != null ? objCaseJunction1.Case__r.ClosedDate.format(CaseDetailConstants_HUM.HUMDateTimeFormat): null;
                objCaseJwrapper.sCaseInteractingAbout  =  objCaseJunction1.Case__r.Interacting_About__c;
                objCaseJwrapper.sCaseStatus            =  objCaseJunction1.Case__r.Status;
                objCaseJwrapper.dCaseCreatedDate       =  objCaseJunction1.Case__r.CreatedDate;
                objCaseJwrapper.sCaseClassification    =  objCaseJunction1.Case__r.CTCI_List__r.Classification__r.name;
                objCaseJwrapper.Id = objCaseJunction1.Id;
                objCaseJwrapper.sCaseCaseNumber = objCaseJwrapper.sCaseCaseNumber+',srcUp,' + objCaseJwrapper.sCaseId;
                objCaseJwrapper.sIsCloneCase           =  objCaseJunction1.IsCloneCase__c?'Yes':'';
                if(isActionVisible || isPharmacyUser) 
                {
                    objCaseJwrapper.sAction = 'Unlink,unlinkCase,'+objCaseJwrapper.sCaseId;
                }
            }
            else
            {
                objCaseJwrapper.sCaseId                =  objCaseJunction1.Case_Master__r.Id;
                objCaseJwrapper.sCaseMasterId          =  objCaseJunction1.Case__r.Id;
                objCaseJwrapper.bIsLinked              =  objCaseJunction1.IsLinked__c;
				String createdDate 					   =  objCaseJunction1.Case_Master__r.CreatedDate.format(CaseDetailConstants_HUM.HUMDateTimeFormat);           
                objCaseJwrapper.sCreatedDate 		   =  createdDate;
                objCaseJwrapper.sWorkQueueName         =  objCaseJunction1.Work_Queue__r.Name;
                objCaseJwrapper.sCreatedByName         =  objCaseJunction1.CreatedBy.Name;
                objCaseJwrapper.sCaseCaseNumber        =  objCaseJunction1.Case_Master__r.CaseNumber;
                objCaseJwrapper.sCaseCreatedByQueue    =  objCaseJunction1.Case_Master__r.Created_By_Queue__c;
                objCaseJwrapper.sCaseCreatedByName     =  objCaseJunction1 .Case_Master__r.CreatedBy.Name+',srcUp,' + objCaseJunction1 .Case_Master__r.CreatedBy.Id;
                objCaseJwrapper.sCaseIntent            =  objCaseJunction1.Case_Master__r.CTCI_List__r.Intent__r.Name;
                objCaseJwrapper.dCaseClosedDate        =  objCaseJunction1.Case_Master__r.ClosedDate;
                objCaseJwrapper.sClosedDate            =  objCaseJunction1.Case_Master__r.ClosedDate != null ? objCaseJunction1.Case_Master__r.ClosedDate.format(CaseDetailConstants_HUM.HUMDateTimeFormat): null;
                objCaseJwrapper.sCaseInteractingAbout  =  objCaseJunction1.Case_Master__r.Interacting_About__c;
                objCaseJwrapper.sCaseStatus            =  objCaseJunction1.Case_Master__r.Status;
                objCaseJwrapper.dCaseCreatedDate       =  objCaseJunction1.Case_Master__r.CreatedDate;
                objCaseJwrapper.sCaseClassification    =  objCaseJunction1.Case_Master__r.CTCI_List__r.Classification__r.name;
                objCaseJwrapper.Id = objCaseJunction1.Id;
                objCaseJwrapper.sCaseCaseNumber = objCaseJwrapper.sCaseCaseNumber+',srcUp,' + objCaseJwrapper.sCaseId;
                objCaseJwrapper.sIsCloneCase           =  objCaseJunction1.IsCloneCase__c?'Yes':'';
                if(isActionVisible || isPharmacyUser) 
                {
                    objCaseJwrapper.sAction = 'Unlink,unlinkCase,'+objCaseJwrapper.sCaseId;
                }
            }
            objCaseJwrapper.slinkedby = objCaseJunction1.CreatedBy.Name+',srcUp,'+objCaseJunction1.CreatedBy.Id;
            objCaseJwrapper.slinkedDate = objCaseJunction1.CreatedDate.format(CaseDetailConstants_HUM.HUMDateTimeFormat);          
            lstlinkedCases.add(objCaseJwrapper);
        }
        return lstlinkedCases;
    }
}
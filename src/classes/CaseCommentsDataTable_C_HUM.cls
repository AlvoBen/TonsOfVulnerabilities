/*****************************************************************************************************
Apex Class Name  : CaseCommentsDataTable_C_HUM
Version   : 1.0
Created Date  : 07/23/2017
Function  : Controller to fetch case comments related to cases
Test Class  :   CaseCommentsDataTable_T_HUM

Modification Log :
* Developer                 Code Review         Date                        Description
* -----------------------------------------------------------------------------------------------------
* Prasad Jandhyala          149239              08/31/2017                  Original Version
* Pradeepkumar Dani         153230              12/01/2017                  Columns correction - REQ - 348192
* Pradeepkumar Dani         157327              12/13/2017                  Pharmacy adjustaments 
* Suraj Patil               200646              03/22/2018                  REQ - 354517 Pharmacy Case comment and non - Pharmacy case comment
* Srivinas Damera                               05/12/2018                  REQ - 361141 - System Administrator Profile Access Custom Case Page
* Ananya Biswas             259008              07/18/2018                  REQ - 367014 Log codes story
* Dinesh Subramaniyan                           08/01/2018                  REQ - 363343 - CR 5742 - Add Seconds to Date/Time Stamp in CRM Service
* Anurag Shah               267567              08/30/2018                  REQ - 371266 Added a logic to determine page section for PDP Pilot access based on logged in user 
* Prudhvi Pamarthi          296559              10/15/2018                  REQ - 376380 Humana Pharmacy User - Case comments error message not displaying properly if member doesn't have an ePost account
* Prudhvi Pamarthi                              02/06/2019                 REQ - 361155 - Not allowing case creation on Legacy Deleted policies
* Kiran Kumar                                   04/12/2019                   Defect 409800 Fix
* Santhosh Ganji                                04/19/2019                  REQ - 386935 - Case Detail Restrictions (MED vs non-MED cases)—READ ONLY features
* Prathyu Tushar            352664              04/24/2019                  Defect 412543.0001 Fix for interaction with 
* Santhosh Ganji                                05/24/2019                  REQ - 400147 - Humana Pharmacy Web Chat users Access to cases
* Manikanta                 40875               08/21/2019                  REQ - 396234 - Ability for CRMS to systematically display the HP case record type and Case Comment section
* Prasanthi Kandula                             10/24/2019                  Defect #443806.0001 Fix - comment layout not rendereing correctly for Calls(RSO) for HP profile
* Santhi Mandava                                01/29/2021                  User Story 1791226: PR00094254 - MF 4- AEP Performance - SF Limits - Sync CPU Time Limit Analysis - Case Details
* Kiran Kumar Bhuvanagiri						02/26/2021					User Story 1824140: Automatically Record Credit Card Updates to Case: Create Order Page (RxRF) 
* Santhi Mandava                                03/08/2021                  User Story 2064320: PR00094268 - MF6 -INC0970730 - CRM - the "new" button is grayed out on closed clase
* Aaron Speakman                                03/23/2021                  DF-2700 - Regression Fix - 1900 Character Limit  
* Mithra Bharadwaj                                                          US#1522825 SonarQube Changes
* Mohammed Noor                              	04/26/2021            		US2163321 - Case Management Checkmarx Security violations - Case History & Case Misc
* AshokKumar Nutalapati                         05/03/2021            		US2188419 - T1PRJ0002517 - MF 1 - Add Log Code to Case Detail Page for HSP Users (CRMS) (ID# 142)
* Ashok Kumar Nutalapati                        06/04/2021                  US2334482 T1PRJ0002517 - MF 5 - Switch Clean Up - Add Log Code to Case Detail Page for HSP Users (CRMS) (ID# 142)
* Gowthami Thota								07/29/2021					US - 2543348 - T1PRJ0002606/PR00094268  - CR01 CSI 1922  - Performance improvement of Case detail and Edit page
* Kiran Bhuvanagiri								05/23/2022			        User Story 2882456: Case Documentation for Demographic Updates (CRMS)
*******************************************************************************************************/

public with sharing Class CaseCommentsDataTable_C_HUM
{
    private List<PermissionSetAssignment> lstPermissionSets;
    public string columnName{ get; private set; }
    public string columnHeader{ get; private set; }
    public string sPharmacyUser{ get; private set; }
    public boolean bIsLogCodelistAvailable{ get; private set; }
    public string sNewCaseComment{ get; set; }
    public string sCaseID{get;
                          set {
                                if(sCaseID==Null)
                                {
                                    sCaseID = value;
                                    onload();
                                }
                              }  
                         }
    public String sCodeHidden {get;set;}
    public String sPharmacyCaseComments{get;set;}
    public string sNewCaseCommentPharmacy{get;set;}
    public string sCode{get;set;}
    public PharmacyCaseComment_C_HUM objPharmacy;
    public static Case objCase{get;set;}
    public Account objAccount {get;set;}
    public String sHiddenCaseCommentMemberNotFound {get;set;}
    public String sHiddenCaseCommentFaultError {get;set;}
    public boolean bIsPharmacyUser{ get; set; } 
    public String sPharmacyMemberNotFound{ get; set; }  
    public String sPharmacyFaultError{ get; set;}
    public Boolean bdisableButton {get; set;}
    public Boolean blegacyDeleteCheck;
    public Id toGetCaseId;
    Public Boolean isThisFromLogging{get;set;}
    public boolean hideTheButton{ get; set;}
    public Id toGetInteractionDetails{get;set;}
    public boolean hPharmacyUser{ get; set;} 
    public Boolean bCharWarning{get;set;}
    public Set<String> wqueues = new Set<String>{CaseDetailConstants_HUM.WORK_QUEUE_PROVIDER_PAYMENT_INTEGRITY,CaseDetailConstants_HUM.WORK_QUEUE_OTHER_INSURANCE_INVESTIGATION,CaseDetailConstants_HUM.WORK_QUEUE_RSO_ORAL_GRIEVANCE_OVERSIGHT,CaseDetailConstants_HUM.WORK_QUEUE_MEDICARE_MEMBER_COMMUNICATIONS};
    public Set<String> chatQueues = new Set<String>(CaseDetailConstants_HUM.WEB_CHAT_WQUEUES);
	
   /**
    *  Constructor for the class
    *  @name CaseCommentsDataTable_C_HUM
    *  @param NA
    *  @return void
    *  @throws NA
    */
    public CaseCommentsDataTable_C_HUM()
    {
       
    }

    private void initLogic()
    {
        isThisFromLogging =false;
        columnName = json.serialize(new List<String>{ 'Id', 'CreatedDate', 'CreatedBy',  'LastModifiedDate' ,'LastModified' });
        columnHeader = json.serialize(new List<String>{ 'Id','Created Date', 'Created By', 'Last Modified Date', 'Last Modified By' });
        sPharmacyUser = Pharmacy_H_HUM.isPharmacyUser();
        bIsPharmacyUser = false;
        lstPermissionSets = [SELECT PermissionSetId,PermissionSet.Name  FROM PermissionSetAssignment WHERE AssigneeId=:UserInfo.getUserId() AND (PermissionSet.Name =:CaseDetailConstants_HUM.RSO_HP_PermissionSet_Name OR PermissionSet.Name =:CaseDetailConstants_HUM.PDPPilot_PermissionSet_Name)];
        sPharmacyMemberNotFound = CaseDetailConstants_HUM.CASECOMMENT_MEMBERNOTFOUND_HUM;
        sPharmacyFaultError = CaseDetailConstants_HUM.CASECOMMENT_FAULTERROR_HUM;
        toGetCaseId =  ApexPages.currentPage().getParameters().get('Id');
        toGetInteractionDetails = ApexPages.currentPage().getParameters().get('interactionId');
        sPharmacyCaseComments = ApexPages.currentPage().getParameters().get('caseComment');
        bdisableButton = verifyLegacyDelete();
         bCharWarning = false;	  
        if(sPharmacyCaseComments != NULL && sPharmacyCaseComments != ''){           
            sNewCaseCommentPharmacy = String.escapeSingleQuotes(sPharmacyCaseComments);       
            isThisFromLogging =true;
			if(sNewCaseCommentPharmacy.contains('NEW_LINE')){
                sNewCaseCommentPharmacy = sNewCaseCommentPharmacy.replace('NEW_LINE', '\n\n');
            }else if(sNewCaseCommentPharmacy.contains('NEXT_LINE')){
                sNewCaseCommentPharmacy = sNewCaseCommentPharmacy.replace('NEXT_LINE', '\n');
            }

            if(sNewCaseCommentPharmacy.contains('INT_WITH') && toGetInteractionDetails != NULL){
                List<Interaction__c> listInteraction = [SELECT Id, Interacting_With__c, Interacting_With_type__c, Caller_Name__c FROM Interaction__c WHERE id =:toGetInteractionDetails ORDER BY CreatedDate DESC LIMIT 1];
                
                if(!listInteraction.isEmpty()){             
                    Interaction__c objInteraction = listInteraction[0];
                    if(objInteraction != null && objInteraction.Interacting_With_type__c != null){              
                        sNewCaseCommentPharmacy = sNewCaseCommentPharmacy.replace('INT_WITH', objInteraction.Interacting_With_type__c);
                    }
                }
                else{
                    sNewCaseCommentPharmacy = sNewCaseCommentPharmacy.replace('INT_WITH contacted us to place order ', 'Placed order ');                
                }
            }

            if(sNewCaseCommentPharmacy !=null && sNewCaseCommentPharmacy.length() > 1900){
                sNewCaseCommentPharmacy = sNewCaseCommentPharmacy.substring(0, 1900);
                bCharWarning = true;
            } 
        }
        hideTheButton = false;
        hPharmacyUser = false;
    }
    
    /**
    *  Onload Method to get enterprise id and account id to pass to service.
    *  @name onload
    *  @param NA
    *  @return void
    *  @throws NA
    */
    public void onload()
    {
        objCase= new case();
        objCase = [select id,subject,description,CaseNumber,Status,AccountId,Classification_Type__c,RecordType.name,Policy_Member__c, Policy_Member__r.Product__c,Owner_Queue__c,Created_By_Queue__c,Origin,CTCI_List__r.Classification__r.name,CTCI_List__r.Intent__r.name,CTCI_List__r.Service_Model_Type__c,Account.Enterprise_ID__c,Account.ETL_Record_Deleted__c,Policy_Member__r.ETL_Record_Deleted__c from case where id=:sCaseID];
        initLogic();
        if(sPharmacyUser != CaseDetailConstants_HUM.NONPHARMACY_USER_HUM)  hPharmacyUser = true;
        
        if(objCase != null && (objCase.Recordtype.name.containsIgnoreCase(CaseDetailConstants_HUM.HumHPRecType) || (objCase.Policy_Member__c != null && objCase.Policy_Member__r.Product__c == CaseDetailConstants_HUM.MEDICARE_POLICY) ||(chatQueues.contains(objCase.Owner_Queue__c) && objCase.Origin == CaseDetailConstants_HUM.WEB_CHAT_ORIGIN)))
        {
          if(objCase.Recordtype.name.containsIgnoreCase(CaseDetailConstants_HUM.HumHPRecType) || wqueues.contains(objCase.Owner_Queue__c) || wqueues.contains(objCase.Created_By_Queue__c) || (chatQueues.contains(objCase.Owner_Queue__c) && objCase.Origin == CaseDetailConstants_HUM.WEB_CHAT_ORIGIN))
          {
            hideTheButton = true;
          }
        }
        Set<String> setPharRecType = new Set<String>(CaseDetailConstants_HUM.Pharmacy_Record_Type_Name);
    if((!bIsPharmacyUser) && (setPharRecType.contains(objCase.RecordType.name)))
    {
       bIsPharmacyUser = checkUserPermission(CaseDetailConstants_HUM.PDPPilot_PermissionSet_Name); 
    }   

        ApexPages.currentPage().getParameters().put('type','New');
        ApexPages.StandardController sc = new ApexPages.StandardController(objCase);
        objPharmacy = new PharmacyCaseComment_C_HUM(sc);    
        boolean bRSOPermissionSetFlag = false;
    
        if(checkUserPermission(CaseDetailConstants_HUM.RSO_HP_PermissionSet_Name))
        {
            bRSOPermissionSetFlag = true;
            if((!bIsPharmacyUser) && (setPharRecType.contains(objCase.RecordType.name)))
            {
                bIsPharmacyUser = true;
            }
            else
            {
                bIsPharmacyUser = false;
            }
            hPharmacyUser = bIsPharmacyUser;
        }
        bIsLogCodelistAvailable = (Pharmacy_H_HUM.fetchLogCodeForBothHPCCS(objCase.Classification_Type__c,bRSOPermissionSetFlag,setPharRecType.contains(objCase.RecordType.name))).size() > 0;
		if (objCase.Classification_Type__c == CaseDetailConstants_HUM.LOGCODEHSP_CLASSIFICATIONTYPE && ((bIsLogCodelistAvailable && objCase.CTCI_List__r.Service_Model_Type__c != CaseDetailConstants_HUM.LOGCODEHSP_SERVICEMODELTYPE) || (objCase.RecordType.name != CaseDetailConstants_HUM.HP_MEMBERCASE_HUM && objCase.RecordType.name != CaseDetailConstants_HUM.CLOSEDHP_MEMBERCASE_HUM))) {
			bIsLogCodelistAvailable = false;
		}
    }

    public Boolean checkUserPermission(String sPermissionSetName)
    {
        Boolean bHasPermission = false;
        For(PermissionSetAssignment objPerSetAssignment : lstPermissionSets)
        {
            if(objPerSetAssignment.PermissionSet.Name == sPermissionSetName)
            {
                bHasPermission = true;
                break;
            }
        }
        return bHasPermission;
    }
    
    /**
    *  Onload Method to get list of Humana Pharmacy Log Code
    *  @name getCaseCommentOptions
    *  @param NA
    *  @return list<SelectOption>
    *  @throws NA
    */
    public list<SelectOption> getCaseCommentOptions()
    {
        list<SelectOption> lstSortBy = new list<SelectOption>();
        lstSortBy = objPharmacy.getCaseCommentOptions();
        return lstSortBy;
    }
         
    /**
    *  Onload Method to assign hidden input field
    *  @name populateHiddentVal
    *  @param NA
    *  @return null
    *  @throws NA
    */
    public PageReference populateHiddentVal()
    {
        sCodeHidden = sCode;
        return null;
    }
    
  /**
    * Returns continuation object
    * <p>
    * Method to call continuation serive for log note.
    *
    * @param sComment                      String
    * @param sEnterpriseId                 String
    * @param sNetworkUserId                String
    * @param sRecordId                     String
    * @param sCode                         String
    * @return                              continuation
    */
    @RemoteAction
    public static Continuation sendRequestLogNotes(String sComment, String sEnterpriseId , String sNetworkUserId , String sRecordId, String sCode, String caseId)
    { 
    Continuation con1 = PharmacyCaseComment_C_HUM.sendRequestLogNotes(sComment,sEnterpriseId,sNetworkUserId,sRecordId,sCode,caseId);
    return con1 ;
    }
    
   /**
    * Returns responses of continuation object
    * <p>
    * Callback Method to parse the Get log Note service response
    *
    * @param  List<String> List of Continuation Labels
    * @param  Object State of Continuation
    * @return Object
    */
    public static object parseLogNoteResponse(List < string > labels, object State)
    {
        return PharmacyCaseComment_C_HUM.parseLogNoteResponse(labels,State);
    }
   
    /**
    *  Method to save pharmacy case comment in CRM
    *  @name createNewCaseCommentPharmacy
    *  @param NA
    *  @return null
    *  @throws NA
    */
    public PageReference createNewCaseCommentPharmacy() 
    {
        objPharmacy.sCaseCommentBody = sNewCaseCommentPharmacy;
        objPharmacy.sCodeHidden = sCode;
        objPharmacy.sHiddenCaseCommentMemberNotFound = CaseDetailConstants_HUM.CASECOMMENT_PHARMACY_MEMBERNOTFOUND_HUM;
        objPharmacy.sHiddenCaseCommentFaultError = CaseDetailConstants_HUM.CASECOMMENT_PHARMACY_FAULTERROR_HUM;
    objPharmacy.sHiddenCaseComment = '';
        objPharmacy.saveHPCaseComment();    
        return null;
    }
  
  /**
    *  Gets list of Case Comments for given case using remote method
    *  @name getCaseCommentsData
    *  @param NA
    *  @return void
    *  @throws NA
    */
    @RemoteAction
    public static list <CaseComment_DTO_HUM> getCaseCommentsData(String sCaseRecordId)
    {
        return CaseCommentsDataTable_C_HUM.getCaseCommentsDTO(sCaseRecordId);
    }

    /**
    *  Gets list of Case Comments for given case using wrapper class list
    *  @name getCaseCommentsDTO
    *  @param NA
    *  @return void
    *  @throws NA
    */
    public static list <CaseComment_DTO_HUM> getCaseCommentsDTO(String sCaseRecordId)
    {
        string sSysAdmProfileUser = system.label.HUMAgencySystemAdmin;
    ID profileId = userinfo.getProfileId();
        string profileName =[Select Id,Name from Profile where Id=:profileId].Name;
    list<CaseComment_DTO_HUM>  lstCaseComments = new list<CaseComment_DTO_HUM>();
        try
        {
            boolean bIsPharmacyUser = (Pharmacy_H_HUM.isPharmacyUser() == system.label.PharmacyUserSupervisorYes_HUM) ? true : false;
            Case oCase = [SELECT Id, Case_Owner__c, Owner_Queue__c, CreatedById, CreatedBy.Name FROM Case WHERE Id = :sCaseRecordId];
            ID createdID = oCase.CreatedById;
            string caseCreatedName = oCase.CreatedBy.Name;
            User caseUser = [SELECT Id, Current_Queue__c FROM User WHERE Id = :createdID];
            List<CaseComment> caseCommentList = [SELECT Id, CreatedDate, CreatedBy.Name, CommentBody, LastModifiedBy.Name, CreatedById, LastModifiedDate, LastModifiedById FROM CaseComment WHERE Parentid = :sCaseRecordId ORDER BY Id DESC]; 
            Set<id> setModifiers = new set<id>();
            Set<id> setCreators = new set<id>();
            for (CaseComment cm : caseCommentList) {
                setModifiers.add(cm.LastModifiedById);
                setCreators.add(cm.CreatedById);
            }
            Map<id, User> mapModifiers = new Map<id, User>([Select id, Current_Queue__c from user where id in : setModifiers]);
            Map<id, User> mapCreators = new Map<id, User>([Select id, Current_Queue__c from user where id in : setCreators]);
            CaseComment_DTO_HUM oCaseCommentDTO;
            for (CaseComment cm : caseCommentList)
            {
                oCaseCommentDTO = new CaseComment_DTO_HUM();
                string editLink = (cm.CreatedBy.Id == UserInfo.GetUserId() || profileName==sSysAdmProfileUser) ? (CaseDetailConstants_HUM.sEdit + CaseDetailConstants_HUM.sOpenCaseComments + cm.id) : CaseDetailConstants_HUM.sEmpty;
                string deleteLink = (cm.CreatedBy.Id == UserInfo.GetUserId() || profileName==sSysAdmProfileUser) ? (CaseDetailConstants_HUM.sDel + CaseDetailConstants_HUM.sCaseCommentsDel + cm.id) : CaseDetailConstants_HUM.sEmpty;
                oCaseCommentDTO.Id = editLink + CaseDetailConstants_HUM.sActionSeparator + deleteLink;
                oCaseCommentDTO.CreatedDate = cm.CreatedDate.format(CaseDetailConstants_HUM.sHUMDateTimeSecondsFormat);
                oCaseCommentDTO.CreatedBy = cm.CreatedBy.Name + CaseDetailConstants_HUM.sSrcUp + cm.CreatedBy.Id;
                oCaseCommentDTO.LastModifiedDate = cm.LastModifiedDate.format(CaseDetailConstants_HUM.sHUMDateTimeSecondsFormat);
                oCaseCommentDTO.LastModified = cm.LastModifiedBy.Name + CaseDetailConstants_HUM.sSrcUp + cm.LastModifiedBy.Id;
                if (!Test.isRunningTest())
                {
                    if (oCase.Case_Owner__c != null && oCase.Owner_Queue__c != null)
                    {
                        oCaseCommentDTO.CreatedByQueue = oCase.Case_Owner__c.equalsIgnoreCase(cm.CreatedBy.Name) ? oCase.Owner_Queue__c : mapCreators.get(cm.CreatedById).Current_Queue__c; 
                        oCaseCommentDTO.LastModifiedByQueue = oCase.Case_Owner__c.equalsIgnoreCase(cm.LastModifiedBy.Name) ? oCase.Owner_Queue__c : mapModifiers.get(cm.lastModifiedById).Current_Queue__c;
                    }
                }
                oCaseCommentDTO.CommentFull = cm.CommentBody;
                oCaseCommentDTO.recId = cm.Id;
                lstCaseComments.add(oCaseCommentDTO);
            }
        }
        catch (Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'CaseHistoryDataTable_C_HUM', 'getCaseCommentsDTO');
        }
        return lstCaseComments;
    }
    
    /**
    *  Method to insert new Case Comment
    *  @name createNewCaseComment
    *  @param NA
    *  @return void
    *  @throws NA
    */
    public void createNewCaseComment()
    {
        if (sNewCaseComment.trim().length() > 0 && sNewCaseComment !='undefined')
        {
            try
            {
                CaseComment cmnt = new CaseComment();
                if(Schema.sObjectType.CaseComment.fields.parentId.isCreateable())	cmnt.Parentid = sCaseID;
                if(Schema.sObjectType.CaseComment.fields.CommentBody.isCreateable())	cmnt.CommentBody = sNewCaseComment;
                insert cmnt;               
            }
            catch(Exception ex) 
            {
                ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.error,'Error in Saving the Case Comment'));
            }           
        }

    }   

    /**  
    * This method is to check whether new button should be disabled in case comments section.
    * 
    * @return   Boolean
    */
    public boolean verifyLegacyDelete()
    {
       bdisableButton = false;
       blegacyDeleteCheck = false;
       
        if(objCase.Account.ETL_Record_Deleted__c == true) blegacyDeleteCheck = true;
        else if (objCase.Policy_Member__r.ETL_Record_Deleted__c != Null && objCase.Policy_Member__r.ETL_Record_Deleted__c == true) blegacyDeleteCheck = true;
       
        if(blegacyDeleteCheck && objCase.Status=='Closed') bdisableButton = true;
        return bdisableButton;
     }
}
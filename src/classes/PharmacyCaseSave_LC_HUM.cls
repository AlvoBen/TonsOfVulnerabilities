/*******************************************************************************************************************************
Apex Class Name : PharmacyCaseSave_LC_HUM
Version         : 1.0
Created On      : 12/06/2021
Function        : This class is used for save the pharmacy case.
Test Class      : PharmacyCaseSave_LT_HUM

Modification Log: 
* Version          Developer Name             Code Review              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------
*    1.0           Ritik Agarwal                                    12/10/2021               Original Version
*    2.0         Surendra Vemulapalli							  US-3383975 added method saveCaseinteractionData
*    3.0        Prasuna Pattabhi                                 09/28/2022               US 3707812 - Create Medicare Case
*	 4.0        Pooja Kumbhar									 09/30/2022				  US 3802961 - Case Comment Observation - Avoiding creating case comment if comment body Is empty
*    5.0        Prasuna Pattabhi                                 10/04/2022               US 3707812 - Create Medicare Case - Added null check
*    6.0        Manohar Billa                                    10/12/2022               US 3759040 - Update record type code changes
*    7.0        Prasuna Pattabhi                                 10/27/2022               US 3934433 - Medicare Calls CAse Comments
*    8.0        Prasuna Pattabhi                                 12/05/2022                 US_4016910,US_4020206,US_4020208,US_4020207
*    9.0        Jasmeen Shangari                                 12/05/2022               US 4035121-Milestone write for SubType change
*	10.0        Pooja Kumbhar									 12/22/2022				  US 3863264 - T1PRJ0170850 - Lightning - RCC/Medicare (MET) Enrollment sections Case Edit Page
*   11.0		Pooja Kumbhar								     01/09/2023				  DF-6915 defect fix
*   12.0        Prasuna Pattabhi                                01/10/2023              Build Fix 1/13 Code Merge
*   13.0       Vijaya Lakshmi Tummala                           1/19/2023              US 4128938 -Escalation Indicator
*   14.0        Nirmal garg										02/07/2023							Invoice request autorouse fix
*   15.0        Pooja Kumbhar									02/16/2023				T1PRJ0170850 - Lightning- Case Management- G & A Complaints Routing Validations
*   16.0        Sagar.G     		                            02/20/2023				US-4177643 Add claims Buttons EDIT CASE  
*   17.0        Dinesh Subramaniyan                              03/14/2023               DF-7347 -Getting component error while logging
*   18.0	    Jasmeen Shangari                                03/20/2023                 US-4325727 - Escalation Indicator Fix for Negative SCenario
*   19.0        Pooja Kumbhar	                                 07/05/2023             US4626269 - T1PRJ0865978 - C06- Case Management - OI : T1PRJ0865978 - C06, Case Management, OI (Medicare and Medicaid Other Insurance )Template, Auto Set CI's and Disable Case Edit Page buttons
*   20.0        Monali Jagtap				         07/17/2023 		       USER STORY 4812246 T1PRJ0891415 Platform Management-  SF - TECH - Critical- Lightning � Pharmacy
*/


public with Sharing class PharmacyCaseSave_LC_HUM {
    public static List<Database.UpsertResult> lstsaveTasks;
    public static List<Task> taskList;
    public static List<Attachment_Log_Item__c> lstAttachLogItem;
    public static List<String> lstClaimNumbers ;
    /*
* Method Name : saveCase
* Description : method used to create a new case
* Return type : Boolean
* Parameter   : caseData - caseData object from LWC that will contain case field and its data.
*               caseCommentData - caseCommentData object from LWC that will contain case field and its data.
*/
    @AuraEnabled
    public Static String saveCase(String caseData , String caseCommentData){
        Case objCase = (Case) JSON.deserialize(caseData, Case.class);        
        CaseComment objCaseComment = (CaseComment) JSON.deserialize(caseCommentData, CaseComment.class);

        Case insertedCase = saveCaseData(objCase);
        if(string.isNotBlank(objCaseComment.CommentBody))
        {
        saveCaseCommentData(objCaseComment, insertedCase.Id); 
        }
        saveCaseinteractionData(caseData,insertedCase.Id);
        return insertedCase.Id;
    }
    
    public Static Case saveCaseData(Case objCase){
        return PharmacyCaseSave_LD_HUM.saveCaseRecord(objCase);
    }
    
    public Static void saveCaseCommentData(caseComment objCaseComment, String caseId){
        PharmacyCaseSave_LD_HUM.saveCaseCommentRecord(objCaseComment, caseId);
    }
    
    public Static void saveCaseinteractionData(String caseData, String caseId){
        Case_Interaction__c CaseInteraction = new Case_Interaction__c();
        boolean bIsInteractionExist = true;           
       Map<String, Object> deserialized = (Map<String, Object>) JSON.deserializeUntyped(caseData);
       String interid = String.valueof(deserialized.get('interactionid'));
        bIsInteractionExist = PharmacyCaseSave_LD_HUM.checkCaseInteraction(interId,caseId);
        if (interid !=null  && caseId!=null && !bIsInteractionExist){
         PharmacyCaseSave_LD_HUM.saveCaseInteractionRecord(CaseInteraction, interId, caseId);
        }
      }
      /*
* Method Name : calEscalationIndDate
* Description : This method will calculate escalation indicator date
* Return type : void
* Parameter   : case
*/
public static void calEscalationIndDate(Case objCase){
    List<String> lstInteractingAboutType = new List<String> {'Agent', 'Group', 'Member', 'Provider', 'Unknown-Agent', 'Unknown-Group', 'Unknown-Member', 'Unknown-Provider'};
    List<String> lstInteractionOrigin = new List<String> {'Service Inquiry', 'NINA Web Chat', 'Internal Process', 'Watson Voice', 'Watson Web Chat', 'Vantage', 'Cancelled', 'Correspondence'};
    Boolean isEscIndicatorDisplay = false;

   
     isEscIndicatorDisplay =((lstInteractingAboutType.contains(objCase.Interacting_About_Type__c)) && (!lstInteractionOrigin.contains(objCase.Origin)))? true:false;
     isEscIndicatorDisplay =((lstInteractingAboutType.contains(objCase.Interacting_About_Type__c)) && (!lstInteractionOrigin.contains(objCase.Origin)) && objCase.Origin == 'IVR' && objCase.classification_type__c != 'Calls (RSO)')? false:true;
    if(objCase.Id !=null)
    {
   		Case caseRec=[select id, Escalation_Indicator__c,Escalation_Indicator_Date__c from Case where id=:objCase.Id limit 1];
    	objCase.Escalation_Indicator__c =(isEscIndicatorDisplay == false)?caseRec.Escalation_Indicator__c:'';
     	if(schema.SobjectType.case.fields.Escalation_Indicator_Date__c.isUpdateable() && objCase.Escalation_Indicator__c != null && caseRec.Escalation_Indicator__c !=objCase.Escalation_Indicator__c)
     	{
               String escIndDate = !string.isBlank(objCase.Escalation_Indicator_Date__c) ? objCase.Escalation_Indicator_Date__c : '' ;
                objCase.Escalation_Indicator_Date__c = escIndDate +'\n'+system.now().format(CONSTANTS_CASE_HUM.DATE_Format) + ' - '+objCase.Escalation_Indicator__c;
     	}                     
                 
    }  else{
         
            objCase.Escalation_Indicator__c =(isEscIndicatorDisplay == false)? null:objCase.Escalation_Indicator__c;
            if(objCase.Escalation_Indicator__c != null && schema.SobjectType.case.fields.Escalation_Indicator_Date__c.isUpdateable())
            {
            String escIndDateNew = !string.isBlank(objCase.Escalation_Indicator_Date__c) ? objCase.Escalation_Indicator_Date__c : '' ;
            objCase.Escalation_Indicator_Date__c = escIndDateNew +'\n'+system.now().format(CONSTANTS_CASE_HUM.DATE_Format) + ' - '+objCase.Escalation_Indicator__c;
            }
       }
}
    /*
        * Method Name : saveCaseDetails
        * Description : This method will return the case recordtypeId based on tyep and subtype comparision
        * Return type : Id 
        * Parameter   : caseData ,caseCommentData ,recordId,profileName,isEdit,isClosedCase, showMedicareCallsComments,  caseCommentIss, caseCommentRes,  bMedicareCalls,  sPrevSubType
    */
    @AuraEnabled
            public Static String saveCaseDetails(String caseData , String caseCommentData,String recordId,String profileName, boolean isEdit,boolean isClosedCase,boolean showMedicareCallsComments,String caseCommentIss,String caseCommentRes, boolean bMedicareCalls, String sPrevSubType, String METTasklist, String sClaimList){
                Case objCase = (Case) JSON.deserialize(caseData, Case.class);        
                CaseComment objCaseComment = (CaseComment) JSON.deserialize(caseCommentData, CaseComment.class);
                if(objCase.Escalation_Indicator__c!=null && string.isNotBlank(objCase.Escalation_Indicator__c)){
                  calEscalationIndDate(objCase);
                    
                  }
             
              if(!Test.isRunningTest() && objCase.Autoroute_Status__c != CONSTANTS_CASE_HUM.AUTOROUTING_COMPLETED && 
                  objCase.Autoroute_Status__c != CONSTANTS_CASE_HUM.AUTOROUTING_PARTD && 
                  objCase.Autoroute_Status__c != CONSTANTS_CASE_HUM.AUTOROUTING_ExpeditedAppeal && 
                  objCase.AutoRoute_Status__c != 'Routed Pending Accept' && 
                  objCase.Autoroute_Status__c != CONSTANTS_CASE_HUM.AUTOROUTING_PCP && 
                  objCase.Autoroute_Status__c != CONSTANTS_CASE_HUM.AUTOROUTING_GOODCAUSE && 
                  objCase.Autoroute_Status__c != CONSTANTS_CASE_HUM.AUTOROUTING_GOODCAUSETIE && 
                  !isCreditableCoverageSubtypeElligible(recordId) && 
                  objCase.Autoroute_Status__c != CaseAutoRouteConstant_C_HUM.AUTOROUTING_MEDICARE && 
                  objCase.Autoroute_Status__c != CaseAutoRouteConstant_C_HUM.AUTOROUTING_KY_IL_MCD && 
                  objCase.Autoroute_Status__c != CaseAutoRouteConstant_C_HUM.AUTOROUTING_FL_MCD &&  
                  objCase.Autoroute_Status__c != CaseAutoRouteConstant_C_HUM.AUTOROUTING_MEDICAID_PAR_PROVIDER && 
                  objCase.Autoroute_Status__c != CaseAutoRouteConstant_C_HUM.AUTOROUTING_MEDICAID_PAR_PROVIDER_KY && 
                  objCase.Autoroute_Status__c != CaseAutoRouteConstant_C_HUM.AUTOROUTING_MEDICAID_PAR_PROVIDER_IL &
		          objCase.Autoroute_Status__c != CONSTANTS_CASE_HUM.AUTOROUTING_INVOICEREQUEST &&
		          objCase.Autoroute_Status__c != CaseAutoRouteConstant_C_HUM.OI_AUTOROUTING_MEDICALUPDATE && objCase.Autoroute_Status__c !=  CaseAutoRouteConstant_C_HUM.OI_AUTOROUTING_RXMCDONLY && objCase.Autoroute_Status__c!= CaseAutoRouteConstant_C_HUM.OI_AUTOROUTING_RXMESPDP  && objCase.Autoroute_Status__c != CaseAutoRouteConstant_C_HUM.OI_AUTOROUTING_RX090DENIAL && objCase.Autoroute_Status__c != CaseAutoRouteConstant_C_HUM.OI_AUTOROUTING_OIPRESCRIPTION && objCase.Autoroute_Status__c != CaseAutoRouteConstant_C_HUM.OI_AUTOROUTING_NEWBORN){
                      objCase.Autoroute_Status__c = CONSTANTS_CASE_HUM.AUTOROUTING_EVALUATE;
                  }
                 String rcdtypeName;
              if(isClosedCase==false){
              
                  Map<String,String> result = updateCaseRecordType(objCase.AccountId,objCase.Type,objCase.Subtype__c,recordId,profileName,isEdit,objCase.RecordTypeId,objCase.Interacting_About_Type__c);
                  rcdtypeName = Schema.getGlobalDescribe().get('Case').getDescribe().getRecordTypeInfosById().get(objCase.RecordTypeId).getName();
                  objCase.RecordTypeId = result.containsKey('caseRecType')?result.get('caseRecType'):objCase.RecordTypeId;
      
                  if(isEdit){            
                      //objCase.Interacting_About_Type__c = result.get('InteractingAboutType'); 
                      objCase.Member_Plan_Id__c = result.containsKey('Member_Plan_Id__c')?result.get('Member_Plan_Id__c'):objCase.Member_Plan_Id__c;
                  }
                }
                if(objCase.Member_Plan_Id__c!=null)
                {
                      MemberPlan memplan = [SELECT Id,Policy_Member__c from MemberPlan where id =: objCase.Member_Plan_Id__c limit 1];
                      objCase.Policy_Member__c =(memplan != null)? memplan.Policy_Member__c:'';
                }else
                {
                    case oldcasedata=(recordId!=null && recordId!='')?[SELECT Id,Policy_Member__c,Member_Plan_Id__c from case where id=:recordId]:null;
                    objCase.Policy_Member__c =(recordId!=null && recordId!='' && oldcasedata != null && oldcasedata.Member_Plan_Id__c!=objCase.Member_Plan_Id__c)? objCase.Policy_Member__c:null;
               	}		
                            
                        Case insertedCase = saveCaseData(objCase);
                
                   Boolean isPartCPartD = true;
                   isPartCPartD =(bMedicareCalls == true && objCase.Call_Benefit_Category__c != null && String.isBlank(objCase.Call_Benefit_Category__c) && !getRSOHPFlag(rcdtypeName))? false:true;
                       
                if(isPartCPartD)
               {
                       CaseMilestone_LC_HUM caseMilestoneWrite = new CaseMilestone_LC_HUM();
                       if(METTasklist !='' && METTasklist != null)
                      {
                         METTaskList_LDTO_HUM metTaskWrapperListInstance = (METTaskList_LDTO_HUM)JSON.deserialize(METTasklist , METTaskList_LDTO_HUM.Class); 
                         List<Task> taskList = METEnrollment_LH_HUM.createTasks(metTaskWrapperListInstance, insertedCase);
                       List<Database.UpsertResult> lstsaveTasks;
                       if(taskList.size()>0)
                       {
                              lstsaveTasks = Database.upsert(taskList, true);
                              HUMExceptionHelper.processUpsertResults(lstsaveTasks, taskList, 'PharmacyCaseSave_LC_HUM ', 'save', 'Task');
                       }
                          
                          caseMilestoneWrite.writeMstoneToTrackService(lstsaveTasks, taskList,insertedCase,sPrevSubType); 
                      }
                   //Write Milestone whenever SubType is changed or selected for first time
                  //Non-Medicare to Medicare Case Update (Subtype change from None to Medicare Subtype)
                  //Change from 1 Medicare case to other Medicare
      
                 if((String.isNotBlank(objCase.subtype__c) && String.isNotBlank(sPrevSubtype) && sPrevSubType != objCase.subtype__c) ||
                     (String.isBlank(sPrevSubtype) && String.isNotBlank(objCase.subtype__c)))
                  {
                    caseMilestoneWrite.caseSubtypeOnlyChangeMStone(insertedCase, sPrevSubType);
                  }
               }		  
                if(showMedicareCallsComments){
                  MedicareCallsCaseComments_H_HUM_LWC.createUpdateCaseCommentRecord(caseCommentIss,caseCommentRes,insertedCase.Id);
                }else if(string.isNotBlank(objCaseComment.CommentBody)){
                  saveCaseCommentData(objCaseComment, insertedCase.Id); 
                } 
                saveCaseinteractionData(caseData,insertedCase.Id);
            if(String.IsNotBlank(sClaimList)){
                         saveClaimNumbersTobackend(sClaimList,insertedCase.Id);
                }
                return insertedCase.Id;
            }
    /*
        * Method Name : updateCaseRecordType
        * Description : This method will return the case recordtypeId based on tyep and subtype comparision
        * Return type : Map
        * Parameter   : newAccountId ,sCaseType ,sCaseSubtype,caseRecordId,profileName    
    */
    @AuraEnabled
    public Static Map<String,String> updateCaseRecordType(
        String newAccountId, 
        String sCaseType, 
        String sCaseSubtype, 
        Id caseRecordId,
        String profileName,
        boolean isEdit,
        Id CaseRecordTypeId,
        String sCaseIntAboutType) {

        String caseRecType;
        String InteractingAboutType;
        Map<String,String> result = new Map<String,String>();
        if(isEdit){   
            Map <ID, Schema.RecordTypeInfo > mapRecordType = Schema.SObjectType.Account.getRecordTypeInfosById();         
            Map<String, HUMCaseInteractionAboutType__c> mapCaseInterAboutType = HUMCaseInteractionAboutType__c.getAll();
            Case oCase= [select AccountId,Account.RecordTypeId,Interacting_About_Type__c,Account.RecordType.name,RecordType.name,RecordTypeId from Case where Id=:caseRecordId WITH USER_MODE];
           
            String newAccRecTypeName; 
            String recName;
            String oldAccountRecordTypeName= (NULL != oCase.Account.RecordTypeId) ? mapRecordType.get(oCase.Account.RecordTypeId).getName() : NULL;
            recName = oldAccountRecordTypeName;
             List<Account>  acc1 = [select RecordTypeId from Account where Id=:newAccountId WITH USER_MODE Limit 1];
             List<Account> acc=(newAccountId!=null && newAccountId!='')?acc1:null;
              newAccRecTypeName =(!acc.isEmpty())? mapRecordType.get(acc[0].RecordTypeId).getName():'';
              recName = (!acc.isEmpty())? newAccRecTypeName:recName;
              if(newAccountId!=oCase.AccountId &&  (sCaseIntAboutType!='Member' && sCaseIntAboutType!='Unknown-Member')){
                  result.put('Member_Plan_Id__c',null);
               }
                
             caseRecType=(oldAccountRecordTypeName!=newAccRecTypeName && 
                profileName!= CaseEditConstants_HUM.HUMHPSpecialistProfile && 
                !oCase.RecordType.name.contains(CaseEditConstants_HUM.HumHPRecType))?(HUMUtilityHelper.getCaseRecordType(recName,sCaseType,sCaseSubtype,CaseRecordTypeId,true)):(HUMUtilityHelper.getCaseRecordType(recName,sCaseType,sCaseSubtype,CaseRecordTypeId));
                   result.put('caseRecType',caseRecType);
          
            //InteractingAboutType = mapCaseInterAboutType.get(recName).CaseInteraction_About_Type__c;
            //result.put('InteractingAboutType',InteractingAboutType);
        }else{
            
            List<MEDENROLLMENT_CASE_SUBTYPE_HUM__c> medicareCaseSubTypeList = MEDENROLLMENT_CASE_SUBTYPE_HUM__c.getAll().values();
            for(MEDENROLLMENT_CASE_SUBTYPE_HUM__c s : medicareCaseSubTypeList){
                if(string.isNotBlank(sCaseSubtype) && s.SubType__c.equals(sCaseSubtype)){
                    caseRecType = Schema.SObjectType.Case.getRecordTypeInfosByName().get('Medicare Case').getRecordTypeId();
                    result.put('caseRecType',caseRecType);
                }
            }
        }
        return result;
    }
	
    /*
    * This method is to check if its Customer Case Specialist with access to 206 permission set & current case is Pharmacy case
    * @param  String - Its Case RecordType name
    * @return Boolean - returns True if its RSOHP user with access to 206 PS & current case is HP
    */    
    @TestVisible
    private static Boolean getRSOHPFlag(String caseRecordType)
    {
        Set<String> setPharRecType = new Set<String>(CONSTANTS_CASE_HUM.Pharmacy_Record_Type_Name);
        List<PermissionSetAssignment> lstRSOHPPermissionSet= [SELECT PermissionSetId FROM PermissionSetAssignment WHERE AssigneeId=:UserInfo.getUserId() AND PermissionSet.Name=:CONSTANTS_CASE_HUM.RSO_HP_PermissionSet_Name LIMIT 1];
        Boolean bIsPharmacyUser = Pharmacy_H_HUM.isPharmacyUser() == CONSTANTS_CASE_HUM.NONPHARMACY_USER_HUM ? false : true;
        Boolean bRSOHPFlag = false;
        if((!bIsPharmacyUser) && (setPharRecType.contains(caseRecordType)))
        {
            bIsPharmacyUser = Pharmacy_H_HUM.isPDPPilotUser(); 
        }
        if(lstRSOHPPermissionSet!=null && !lstRSOHPPermissionSet.isEmpty() && (!bIsPharmacyUser) && setPharRecType.contains(caseRecordType) )
        {
            bRSOHPFlag = true;
        }
        return bRSOHPFlag;
    }   	

                /*
    * This method is to check the case subtype is eligible for creditable Coverage.    
    * @param  NA
    * @return Boolean
    * 
    */
    public static boolean isCreditableCoverageSubtypeElligible(String Id){       
        Boolean isCreditableCoverageSubtypeElligible = false;
        List<Template__c> lstTemplateObj=[Select Id from Template__c where Unique_Template_Name__c= :CaseEditConstants_HUM.ATTESTATION_TEMPLATE_NAME WITH USER_MODE];  
        List<Template_Submission_Owner__c> lstSubmissionOwner1 = [SELECT Id FROM Template_Submission_Owner__c WHERE Object_Owner_ID__c = :Id WITH USER_MODE];
         List<Template_Submission_Owner__c> lstSubmissionOwner = (lstTemplateObj.size()>0)?lstSubmissionOwner1:null;
         List<Template_Submission__c> lstTemplateSubmission1;
         if(lstSubmissionOwner?.size() > 0) {
            lstTemplateSubmission1 = [SELECT Id FROM Template_Submission__c WHERE Submission_Owner__c = :lstSubmissionOwner[0].Id AND Template__c = :lstTemplateObj[0].Id WITH USER_MODE]; 
         }
         List<Template_Submission__c> lstTemplateSubmission=(lstSubmissionOwner.size()>0)?lstTemplateSubmission1:null;
          
        if(lstTemplateSubmission?.size()>0)
        {
                   List<Template_Submission_Data__c> lstTemplateSubmissionData= [SELECT Id, Template_Field__r.Name, Value__c FROM Template_Submission_Data__c WHERE Template_Submission__c = :lstTemplateSubmission[0].Id WITH USER_MODE]; 
                   for(Template_Submission_Data__c subDataObj :lstTemplateSubmissionData){
                         isCreditableCoverageSubtypeElligible=(subDataObj.Template_Field__r.Name  == CaseEditConstants_HUM.ATTESTATION_DISCLAIMERNAME && subDataObj.Value__c == CaseEditConstants_HUM.ATTESTATION_DISCLAIMERYESVALUE )?true:false;
                          break;
                        }
          }        
        return isCreditableCoverageSubtypeElligible;
    }
    public static void saveClaimNumbersTobackend(string sClaimNumbers,Id caserecordid)
    {
        try{
             if(String.IsNotBlank(sClaimNumbers))        
             {
                    sClaimNumbers = sClaimNumbers.substring(0,sClaimNumbers.length());
                    List<String> lstClaimNumbers = sClaimNumbers.split(',');
                    Attachment_Log__c  AttachmentObj = new Attachment_Log__c();
                  
                    AttachmentObj.case__c =(schema.SobjectType.Attachment_Log__c.fields.case__c.isCreateable())?caserecordid:null;
                 
                    AttachmentObj.Source_System__c =(schema.SobjectType.Attachment_Log__c.fields.Source_System__c.isCreateable())? CaseEditConstants_HUM.CRM_CON:null;
                  
                    AttachmentObj.Attachment_Type__c =(schema.SobjectType.Attachment_Log__c.fields.Attachment_Type__c.isCreateable())?CaseEditConstants_HUM.CLAIMS_MANUAL_ENTRY:null;
                   
                    AttachmentObj.Attachment_Sub_type__c =(schema.SobjectType.Attachment_Log__c.fields.Attachment_Sub_type__c.isCreateable())?CaseEditConstants_HUM.CLAIMS_MANUAL_ENTRY:null;
                
                    AttachmentObj.Created_By__c =(schema.SobjectType.Attachment_Log__c.fields.Created_By__c.isCreateable())? UserInfo.getName():null;
                    Database.SaveResult sResult = Database.insert(AttachmentObj, true); 
                    if(sResult.isSuccess())
                    {
                       List<Attachment_Log_Item__c> lstAttachLogItem = new List<Attachment_Log_Item__c>();
                       if(lstClaimNumbers != Null && !lstClaimNumbers.isEmpty())
                       { 
                           lstAttachLogItem=addAttachLogItem(lstClaimNumbers,AttachmentObj);
                            Database.SaveResult[] srlstAttachLogItem = Database.insert(lstAttachLogItem, false);
                            sClaimNumbers = '';
                            List<Error_Log__c> listErrorLogs = new List<Error_Log__c>();
                            for(Database.SaveResult sr : srlstAttachLogItem) 
                            {
                                listErrorLogs=(!sr.isSuccess())?getErrors(sr,listErrorLogs):null;                                }
                            }
                    }
                                         
              }
        }catch(exception e){
           
             throw new AuraHandledException(e.getMessage());
            
        }
         }

     public static List<Attachment_Log_Item__c> addAttachLogItem(List<String> lstClaimNumbers, Attachment_Log__c  AttachmentObj){
        
                   for(Integer I=0;I<lstClaimNumbers.size();I++)
                          {
                            if(String.isNotBlank(lstClaimNumbers[I]))
                                {
                                    Attachment_Log_Item__c AttachLogItem = new Attachment_Log_Item__c(name=CaseEditConstants_HUM.CLAIM_NUMBER, value__c = lstClaimNumbers[I],Attachment_Log__c = AttachmentObj.id);                                    
                                    lstAttachLogItem.add(AttachLogItem);
                                }
                            }
                         
        return lstAttachLogItem;
    
    }
     public static List<Error_Log__c> getErrors(Database.SaveResult sr,List<Error_Log__c> listErrorLogs){
         for(Database.Error err : sr.getErrors()) 
          {
                            listErrorLogs.add(new Error_Log__c(Error_Code__c = String.valueOf(err.getStatusCode()),
                            Error_Message__c = err.getMessage(),
                            class_name__c = 'CaseEdit_CE_HUM',
                            Method_Name__c = 'Save',
                            Object_Type__c = 'Attachment_Log_Item__c'));
                            HUMExceptionHelper.saveLog(listErrorLogs, false); 
          }
        return listErrorLogs;
    }
}
/*******************************************************************************************************************************
Apex Class Name : CaseCommentsDataTable_LC_HUM
Version         : 1.0
Created On      : 12/07/2021 
Function        : This class is for new case comment creation process
Test Class      : CaseCommentsDataTable_LT_HUM

Modification Log: 
* Version          Developer Name             Code Review              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------
*    1.0           Ashish Kumar                                        12/07/2021                	Original Version
*    2.0           Vinay Lingegowda                                    05/25/2022                	Added method for case Timeline
*    3.0           Ritik Agarawal                                      04/04/2022                   Add log code visibility changes US_3192496       
*    3.0           Shailesh B / Bhumika D                               30/06/2022                   Add logic in updateRecord for task     
*    4.0           M K Manoj                                           07/27/2022                   US-3522143  T1PRJ0170850 - Lightning- Case Comments Removal of HP Log Code field for HP Provider/Agent/Broker/Group/HP Unknown 
*******************************************************************************************************************************************************************/

public with sharing class CaseCommentsDataTable_LC_HUM {

    public static String sHashTag='';
   
   /**
* Method to get list of Humana Pharmacy Log Code
*  @name getCaseCommentOptions
*  @param String sCaseID
*  @return List<String>
*/
   @AuraEnabled
   public static List<String> getCaseCommentOptions(String sCaseID)
   {   
       return PharmacyCaseComment_LC_HUM.getCaseCommentOptions(fetchCaseObject(sCaseID) != null ? fetchCaseObject(sCaseID).Classification_Type__c : null);
   }
   
   /**
* Method to get Case record
*  @name fetchCaseObject
*  @param String sCaseID
*  @return Case
*/	
   public static Case fetchCaseObject(String sCaseID)
   {	
       return String.isNotBlank(sCaseID) ? CaseCommentsDataTable_LD_HUM.fetchCaseRecord(sCaseID) : null;
   }
    /**
* Method to get Case and CTCI wrapper
*  @name fetchCaseObject
*  @param String sCaseID
*  @return Case
*/	
   @AuraEnabled
   public static CaseCTCIWrapper fetchCaseCTCIObject(String sCaseID)
   {	
       CaseCTCIWrapper oWrap = new CaseCTCIWrapper();
       oWrap.oCase= String.isNotBlank(sCaseID) ? CaseCommentsDataTable_LD_HUM.fetchCaseRecord(sCaseID) : null;
       oWrap.lCTCI = CaseTransfer_LD_HUM.fetchCTCIRecord(oWrap.oCase);
       return oWrap;
   }
   

   

   /**
* Returns continuation object
* <p>
* Method to call continuation serive for log note.
*
* @param sComment                      String
* @param sCode                         String
* @param sCaseId                        String    
* @return                              continuation
*/
   @AuraEnabled(continuation=true)
   public static Continuation sendRequestLogNotes(String sComment, String sCode, String sCaseId)
   { 
       Case objCase= fetchCaseObject(sCaseId);
       if(objCase!=null){
           Continuation con1 = PharmacyCaseComment_C_HUM.sendRequestLogNotes(sComment,objCase.Account.Enterprise_ID__c,CaseCommentsDataTable_LD_HUM.fetchUserRecord().Network_User_Id__c,objCase.AccountId,sCode,sCaseId);
           return con1 ;
       } else {
           return null;
       }
   }
  
   /**
* Returns responses of continuation object
* Callback Method to parse the Get log Note service response
*
* @param  List<String> List of Continuation Labels
* @param  Object State of Continuation
* @return Object
*/ 
   @AuraEnabled
   public static object parseLogNoteResponse(List <String> labels, object State)
   {	       
       return PharmacyCaseComment_C_HUM.parseLogNoteResponse(labels,State);
   }

    @AuraEnabled
  public static String reassignToCreatorComment(String sCaseId, String sCaseCommentBody, String sCode, Boolean bRedirect, String bErrorCaseComment,String sHashTagIRCT)
  {
      sHashTag= sHashTagIRCT;
      return startSaveCommentOperation(sCaseId,sCaseCommentBody,sCode,bRedirect,bErrorCaseComment);
  }
   
   /**
* Method to save the comment on the basic of response returned for EPOST service
*  @name startSaveCommentOperation
*  @param String sCaseID
*  @param String sCaseCommentBody
*  @param String sCode
*  @param Boolean bRedirect
*  @param String bErrorCaseComment
*  @return List<String>
*/
   @AuraEnabled 
   public static String startSaveCommentOperation(String sCaseId, String sCaseCommentBody, String sCode, Boolean bRedirect, String bErrorCaseComment)
   {  
       try{
           List<CaseComment> lstCaseComment = new List<CaseComment>();
           CaseComment caseComObj;
           String sCodeHidden = sCode;
           
           String sHiddenCaseComment = (bErrorCaseComment=='true') ? GLOBAL_CASE_DETAIL_CONSTANT_LH_HUM.CASECOMMENT_PHARMACY_MEMBERNOTFOUND_HUM:((bErrorCaseComment=='false') ? GLOBAL_CASE_DETAIL_CONSTANT_LH_HUM.CASECOMMENT_PHARMACY_FAULTERROR_HUM:'');
               
           if(string.isNotBlank(sHiddenCaseComment)) 
           {
               caseComObj = new caseComment(parentId = sCaseId, commentBody = sHiddenCaseComment);
               if(caseComObj != null){
                   lstCaseComment.add(caseComObj);
               }	
           }
           
           if(string.isNotBlank(sCaseCommentBody) && string.isNotBlank(sCodeHidden)) 
           {
               caseComObj = new caseComment(parentId = sCaseId, commentBody = '('+sCodeHidden+') '+sCaseCommentBody);
               if(caseComObj != null){
                   lstCaseComment.add(caseComObj);
               }	
           }
            // Below if will only be called in case of reassign to creator button functionality for adding comment based on HashTag
           if(String.isNotBlank(sHashTag)) 
           {
               lstCaseComment.add(new CaseComment(parentId = sCaseId, commentbody = sHashTag));
               sHashTag = '';
           }

           SObjectAccessDecision securityDecision = Security.stripInaccessible(AccessType.CREATABLE,lstCaseComment);
           lstCaseComment= securityDecision.getRecords();  

           if(!lstCaseComment.isEmpty())
           {
               insert lstCaseComment; 
               return GLOBAL_CASE_DETAIL_CONSTANT_LH_HUM.CASECOMMENT_INSERTED;
           }
           return GLOBAL_CASE_DETAIL_CONSTANT_LH_HUM.CASECOMMENT_NOT_INSERTED;
       }
       catch(Exception ex)
       {
           HUMExceptionHelper.logErrors(ex, 'CaseCommentsDataTable_LC_HUM', 'startSaveCommentOperation');
           return ex.getMessage();
       }
   }
   
   /**
* Method to get list of case comments
*  @name generateCaseComments
*  @param String objID
*  @return String
*/     
   @AuraEnabled
   public static String generateCaseComments(String objID)
   {
       List<CaseComment> caseComments = CaseCommentsDataTable_LD_HUM.getCaseCommentDetails(objID);	
       return getCaseCommentWrapper(caseComments);
   }
   
   /**
* Method to get list of case comments
*  @name getcasecommentwrapper
*  @param List<Case> lstCaseComments
*  @return String
*/ 
   private static String getCaseCommentWrapper(List<CaseComment> lstCaseComments)
   {    
       String sClassification;
       String sIntent;
       String logNoteCode;
       CaseComments_DTO_HUM caseCommentWrapper= new CaseComments_DTO_HUM();
       
       if(lstCaseComments !=NULL )
       {   
               //Method to check for Case comment Drop down visibilty, and edit and delete functionality
               Boolean bDropDownVisible = checkPermissionForCommentDropDown();
               Boolean bUpdatable  = Schema.getGlobalDescribe().get('CaseComment').getDescribe().isUpdateable();
               Boolean bDeletable   = Schema.getGlobalDescribe().get('CaseComment').getDescribe().isDeletable();
               
               List<TimelineCaseComments_DTO_HUM> lstCaseWrap = new List<TimelineCaseComments_DTO_HUM>();
               
               for(CaseComment oCaseComment :lstCaseComments )
               {
                   TimelineCaseComments_DTO_HUM oCaseWrap = new TimelineCaseComments_DTO_HUM();
                   oCaseWrap.caseCommentId = oCaseComment.Id;
                   oCaseWrap.LogNoteCode = oCaseComment.LastModifiedBy.Name ;
                   oCaseWrap.LogNoteDate = (oCaseComment.CreatedDate!=null) ?  oCaseComment.CreatedDate.format('h:mm a') + ' | ' + oCaseComment.CreatedDate.format('MM/dd/yyyy') :'';
                   oCaseWrap.LogNoteMessage =oCaseComment.CommentBody;
                   oCaseWrap.LogNoteUser = oCaseComment.CreatedBy.Name;
                   oCaseWrap.LogNoteLastModified = (oCaseComment.LastModifiedDate!=null) ?  oCaseComment.LastModifiedDate.format('h:mm a') + ' | ' + oCaseComment.LastModifiedDate.format('MM/dd/yyyy') :'';
                   oCaseWrap.isDropDownVisible = bDropDownVisible;
                   
                   if(bDropDownVisible)
                   {
                       oCaseWrap.canEdit = oCaseComment.CreatedBy.Id == UserInfo.GetUserId() && bUpdatable;
                       oCaseWrap.canDelete = oCaseComment.CreatedBy.Id == UserInfo.GetUserId() && bDeletable; 
                   }

                   if(!oCaseWrap.canEdit && !oCaseWrap.canDelete)
                   {
                       oCaseWrap.isDropDownVisible=false;
                   }
                   
                   lstCaseWrap.add(oCaseWrap);
               }
               
               caseCommentWrapper.lCaseCommentDTO = lstCaseWrap;
               caseCommentWrapper.profileName = CaseCommentsDataTable_LD_HUM.getCurrentUserProfileName();
               return JSON.serialize(caseCommentWrapper);
       }
       else
       {
           return null;
       }
   }
   
/**
* Method to get check visibility of drop down
*  @name checkPermissionForCommentDropDown
*  @param NA
*  @return Boolean
*/ 
   public static Boolean checkPermissionForCommentDropDown()
   {	
       String userProfileName=CaseCommentsDataTable_LD_HUM.getCurrentUserProfileName();
       
       if(userProfileName == GLOBAL_CASE_DETAIL_CONSTANT_LH_HUM.SYSTEM_ADMIN ||
           userProfileName == GLOBAL_CASE_DETAIL_CONSTANT_LH_HUM.CUSTOMER_CARE_SUPERVISOR|| 
           Pharmacy_LH_HUM.isPharmacyUser() == GLOBAL_CASE_DETAIL_CONSTANT_LH_HUM.YES_SUPERVISOR )
       {
           return true;
       }
       
       return false;
   }

    /**
* Method to delete the selected casecomment
*  @name deleteRecord
*  @param String,String
*  @return Boolean
*/ 
   @AuraEnabled
   public static Boolean deleteRecord(string deleteID, String objectName) 
   {
       return CaseCommentsDataTable_LD_HUM.deleteRecord(deleteID,objectName);
   }

/**
* Method to update the selected casecomment
*  @name updateRecord
*  @param String,String
*  @return Boolean
*/ 
   @AuraEnabled
   public static Boolean updateRecord(string updateId, String sCommentBody, String sCaseTask) 
   {

       try{
           if(Id.valueOf(sCaseTask).getSobjectType() == Schema.Task.sObjectType){
               return TaskCommentsDataTable_LD_HUM.updateTaskCommentData(updateId,sCommentBody);
           }
           else {
               return CaseCommentsDataTable_LD_HUM.updateRecord(updateId,sCommentBody);
           }
       
           }
           catch(Exception e)
           {   
               HUMExceptionHelper.logErrors(e,'CaseCommentsDataTable_LC_HUM','updateRecord'); 
               return false;
           }
   }

   // this method is used when user is non Pharmacy and there is no EPOST in this
   @AuraEnabled
   public static String saveCaseCommentData(String sCaseId, String caseCommentData)
   {
       List<CaseComment> lstCaseComment = (List<CaseComment>) JSON.deserialize(caseCommentData, List<CaseComment>.class);
       List<CaseComment> lstComments= new List<CaseComment>();
       for(CaseComment objCaseComment: lstCaseComment)
       {
           objCaseComment.ParentId = sCaseId;
           lstComments.add(objCaseComment);
       }
       Lib_Security_LH_HUm.assertCreate(lstComments);
       insert lstComments;
       
       return GLOBAL_CASE_DETAIL_CONSTANT_LH_HUM.CASECOMMENT_INSERTED;
   }
   
   class CaseComments_DTO_HUM{
       public List<TimelineCaseComments_DTO_HUM> lCaseCommentDTO;
       public String profileName;
   }
   public class CaseCTCIWrapper {
       @AuraEnabled public List<CTCI_Junction__c> lCTCI;
       @AuraEnabled public Case oCase;
   }
}
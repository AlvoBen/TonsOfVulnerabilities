/*******************************************************************************************************************************
Apex Class Name : PharmacyCaseSave_LD_HUM
Version         : 1.0
Created On      : 12/13/2021
Function        : This class is used for save the pharmacy case.

Modification Log: 
* Version          Developer Name             Code Review              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------
*    1.0           Ritik Agarwal                                    12/13/2021               Original Version
*	 2.0		   Surendra Vemulapalli														 US-3383975 added method saveCaseInteractionRecord
*    3.0           Dinesh Subramaniyan                              03/14/2023               DF-7347 -Getting component error while logging 
*	 4.0		   Monali Jagtap									07/17/2023 				 USER STORY- 4812246 T1PRJ0891415 Platform Management-  SF - TECH - Critical- Lightning – Pharmacy
*/

public with Sharing class PharmacyCaseSave_LD_HUM {
    
    /*
* Method Name : saveCaseRecord
* Description : method used to create a new case after checking CRUD/FLS
* Return type : Case
* Parameter   : objCase - case object.
*               
*/
    public Static Case saveCaseRecord(Case objCase){
        Lib_Security_LH_HUm.assertUpsert(objCase);
        try{
            upsert objCase;
        }catch(DMLException ex){
            HUMExceptionHelper.logErrors(ex,'PharmacyCaseSave_LD_HUM','saveCaseRecord');
            Throw Lib_Security_LH_HUm.createException('Update/Insert Failed',ex);  
        }
        return objCase;
    }
    
    /*
* Method Name : saveCaseCommentRecord
* Description : method used to create a new casecomment after checking CRUD/FLS and linked it to case.
* Return type : void
* Parameter   : objCaseComment - caseComment object, caseId - id of case that is recently insert.
*               
*/
    public Static void saveCaseCommentRecord(CaseComment objCaseComment, String caseId){
        objCaseComment.ParentId = caseId;
        Lib_Security_LH_HUm.assertCreate(objCaseComment);
        insert objCaseComment;
    }
    
     /*
* Method Name : saveCaseinteractionRecord
* Description : method used to create a new Case Interaction after checking CRUD/FLS and linked it to case.
* Return type : void
* Parameter   : caseInt - Case Interaction object, InterId - Interaction id recently created, caseId - id of case that is recently insert.
*               
*/
        
    public Static void saveCaseInteractionRecord (Case_Interaction__c caseInt, String interId, String caseId){
        caseInt.case__c = caseId;
        caseInt.Interaction__c = interId;
        Lib_Security_LH_HUm.assertCreate(caseInt);
        upsert caseInt;
    }

   /*
* Method Name : checkCaseInteraction
* Description : method used to check whether already case and interaction relationship exist or not
* Return type : Boolean
* Parameter   : InterId - Interaction id recently created, caseId - id of case that is recently insert.
* */              
    
    public static boolean checkCaseInteraction(string interId, string caseId){
        boolean iscaseinteractionexist = true;
        List<Case_Interaction__c> lstCaseInteraction = [SELECT Id FROM Case_Interaction__c WHERE Interaction__c =:interId and Case__c=:caseId WITH USER_MODE limit 1];
       if(lstCaseInteraction.size()==0 && lstCaseInteraction.isempty())
       {
		      iscaseinteractionexist = false;     
       }
        return iscaseinteractionexist;
    }
    
}
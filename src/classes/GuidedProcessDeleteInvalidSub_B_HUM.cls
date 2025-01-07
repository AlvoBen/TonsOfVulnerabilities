/******************************************************************************************************************************
Apex Class Name  : GuidedProcessDeleteInvalidSub_B_HUM 
Version          : 1.0 
Created Date     : 05/04/2016
Function         : Batch class to delete the Invalid Guided Process Submissions.                 
Test Class       : GuidedProcessDeleteInvalidSub_BT_HUM 
Modification Log :
    Developer              Code Review             Date                 Description
 * ------------------------------------------------------------------------------------------------------------------------------                
 *  Praveen Kumar Parimi      27942              05/04/2016            Orginal Version    
 *  Prathyu Tushar            210783             04/06/2018            As per Checkmarx REQ - 359261, Cheking user permission on Template_Submission__c & Template_Submission_Owner__c objects before deleting records.
 *********************************************************************************************************************************/  
public class GuidedProcessDeleteInvalidSub_B_HUM implements Database.Batchable<sObject>,Schedulable
{  
    @TestVisible String CaseID = GLOBAL_CONSTANT_HUM.GP_CASEID_HUM;
    @TestVisible String sCase = GLOBAL_CONSTANT_HUM.CASE_CONSTANT_HUM;
    @TestVisible String sQuery = NULL;
    List<Template_Submission__c> lstTemplateSubmission = new List<Template_Submission__c>(); 
    set<ID> setTemplateOwnerIds = new set<ID>();  
    
    /*
    * Method Name : start
    * Description : This is used to execute pre logic.
    * Return type : Database.QueryLocator
    * Paramater   : BatchableContext
    */
    public Database.QueryLocator start(Database.BatchableContext BC)
    { 
          sQuery = 'SELECT ID,Version__c,Submission_Owner__r.Id FROM Template_Submission__c where Submission_Owner__c in (SELECT Id FROM Template_Submission_Owner__c WHERE  Object_Owner_ID__c  =: CaseId AND Object_Owner_Type__c =: sCase)';
          return Database.getQueryLocator(sQuery);   
    }
    
    /*
    * Method Name : execute
    * Description : This is used to process records.
    * Return type : void
    * Paramater   : BatchableContext,List<sObject>
    */
    public void execute(Database.BatchableContext BC,List<sObject> batch) 
    {
            for(sObject sTempSubmission : batch) 
            {
                Template_Submission__c aTempSubmission = (Template_Submission__c )sTempSubmission;
                lstTemplateSubmission.add(aTempSubmission);
                setTemplateOwnerIds.add(aTempSubmission.Submission_Owner__r.Id);
            }        
            List<Template_Submission_Owner__c> lstTemplatesubmissionOwner =  [select ID from Template_Submission_Owner__c where ID in :setTemplateOwnerIds];        
            if(lstTemplateSubmission!= NULL && lstTemplateSubmission.size()>0 && lstTemplatesubmissionOwner != NULL && lstTemplatesubmissionOwner.size()>0)
            {        
                if(Template_Submission__c.sObjectType.getDescribe().isDeletable())
                    Database.DeleteResult[] lstTSresult = database.delete (lstTemplateSubmission);
                if(Template_Submission_Owner__c.sObjectType.getDescribe().isDeletable())
                    Database.DeleteResult[] lstTSOresult = database.delete (lstTemplatesubmissionOwner);
            }           
    }
        
    /*
    * Method Name : execute
    * Description : This is used to execute the batch.
    * Return type : void
    * Paramater   : SchedulableContext 
    */
    public void execute(SchedulableContext SC)
    {           
        Database.ExecuteBatch(new GuidedProcessDeleteInvalidSub_B_HUM());                   
    }
     
    /*
    * Method Name : finish
    * Description : This is used to execute post logic.
    * Return type : void
    * Paramater   : BatchableContext
    */
    public void finish(Database.BatchableContext BC)
    {       
    }      
}
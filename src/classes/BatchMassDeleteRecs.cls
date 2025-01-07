/***************************************************************************************************************************************************************************
Apex Class Name  : BatchMassDeleteRecs
Version          : 1.0
Created Date     : May 15 2018
Function         : This Batch class is used to delete records from the any object mentioned in custom metadata type.
Test Class       : BatchMassDeleteTest
****************************************************************************************************************************************************************************
Modification Log:

* Developer Name                Code Review        Date                        Description
*---------------------------------------------------------------------------------------------------------------------------------------------------------------------------
* Suresh Gottipati                                05/15/2018                   Original Version
* Sagar MN                                         01/14/2019                  REQ - 416054 Checmarx Sharing Vulnerability Fix
**************************************************************************************************************************************************************/

Public with sharing class BatchMassDeleteRecs Implements Database.batchable<sobject>, Schedulable {
     
     public final string query;
     
     
     
     Public BatchMassDeleteRecs(){
     
        
          datetime DateOfBatch=datetime.now()-1; 
       system.debug('dateandtime=========================='+DateOfBatch);
       
       string strDateTime= string.valueof(DateOfBatch);
       string[] strDateTimeSplit=strDateTime.split(' ');
       string dtDateTime= strDateTimeSplit[0]+'T'+strDateTimeSplit[1]+'Z';
       if(!Test.isRunningTest()) 
       {
           query ='select id, Createddate from member_Cache__c where createddate<='+dtDateTime+''+'limit 50000'+'';  
       }
       else
       {
           query ='select id, Createddate from member_Cache__c limit 50000';
       }
        system.debug('query=========================='+query);  
        
     }
   
    /**
       * Returns member cache id's based on Custom metadata type date  
       * <p>
       * Method to execute pre logic
       *
       * @param     BC                      Database.BatchableContext
       * @return    Database.QueryLocator            
    */
    
     Public Database.QueryLocator start(Database.BatchableContext BC){

      return Database.getQueryLocator(query);
     }
     
      /**
       * Method to execute deletion of records
       *
       * @param     BC          Database.BatchableContext
       * @param     batch       List of Member cache records
       * @return    void            
    */
     
     Public void execute(Database.BatchableContext BC,List<SObject> DeleteMemberCacheRec){
     system.debug('Scope data============================================'+DeleteMemberCacheRec);
            //delete scope;
       try{
			if(DeleteMemberCacheRec!=Null){
                SObjectType myObj = Schema.getGlobalDescribe().get('member_Cache__c');
                if(myObj.getDescribe().isDeletable()){
                    Database.DeleteResult[] lstMemberCacheCleaningresult = database.delete (DeleteMemberCacheRec);
                }
            }    
    }catch(DMLException ex)
        {
            HUMExceptionHelper.logErrors(ex,'Records deletion have a problem from object','finish');
        }
    
    }
    
    /**
       * Method to execute deletion of records
       *
       * @param     BC          Database.Schedulable
       * @param     batch       List of Member cache records
       * @return    void            
    */
     
    Public void execute(SchedulableContext SC){
    Database.ExecuteBatch(new BatchMassDeleteRecs());  
    
   }
   
    /**
       * Finish Method must be there to complete the job. 
       *
       * @param     BC          Database.BatchableContext
       * @param     batch       List of Member cache records
       * @return    void            
    */
    Public void finish(Database.BatchableContext BC){
    
    }
 }
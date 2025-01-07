/*****************************************************************************************
Apex Class Name :   HUMWarmCaseTransfer
Version: 1.0
Function           : Controller to VF Page: HUMWarmCaseTransfer.
                      This class Is used to drive the custom functionality of the page.
Created Date       :  22 Sept 2016
Test Class         : HUMWarmCaseTransferTest

        Modification Log:  
 *   Developer                   Code Review             Date               Description
 * -------------------------------------------------------------------------------------------------------------------------------                
 *  Shikhar Mehta                  40792               22/09/2016          Original Version
 *  Shikhar Mehta                                      05/10/2016          Req - 282376 
 *  Samridh Manucha                45135               28/10/2016          Defect Fix - 245675.0001
 *  Vamsi Kondragunta              47288               11/09/2016          Defect fix for #247086.0001
 *  Vamsi Kondragunta              47603               11/11/2016          Defect fix for #244725.0001
 *  Vamsi Kondragunta              48142               11/15/2016          Defect fix for #248857.0001
 *  Vamsi Kondragunta              49555               11/29/2016          Defect fix for 249973 And 250228
 *  Vamsi Kondragunta              62787               02/15/2017          Defect fix for 259480.0001;To render the cases with only status ='Pending-transfer'
 *  Prudhvi Pamarthi               122198              08/23/2017          Fix for REQ-326278 work queue view name Is blank after warm case transfer  
 *  Vijaya Koriprolu                                   12/14/2017          Changed the security remediation for REQ - 349542.
 *  Praveen Kumar Parimi           252850              07/05/2018          Removed the Query check of null so that multiple warm case transfers work REQ - 367017 aka CA 7501880.
 * Prasuna Pattabhi                                    08/11/2021          US 2548370 Checkmarx sf-main/src/classes/HUMWarmCaseTransfer.cls    
************************************************************************************************************************/
Global With sharing Class HUMWarmCaseTransfer
{   
    Public List<Case> caseList{Get;Set;}   
    DateTime hrBefore = System.Now().addHours(-24);  
    Public Id updCaseId{Get;Set;}
    Public String updCaseOwnerName{Get;Set;}
        
    
    Public HUMWarmCaseTransfer()
    {
         CaseList = New List <Case>();
        caseList = [Select Id,Warm_Case_Transfer_Action__c,Case_Owner__c,Status,CaseNumber, User__r.Name,OwnerId, Transferred_From__r.Name from Case where (User__c=:Userinfo.getUserId() Or Transferred_From__c=:Userinfo.getUserId()) And Transferred_Date__c >=:hrBefore And status ='Pending - Transfer'];                      
    }
    
    /**
    * Method to get all the transfered cases in the table
    * @param  NULL
    * @return List
    */
    
    @testVisible public PageReference getCases() 
    {        
      caseList = [Select Id,Warm_Case_Transfer_Action__c,Case_Owner__c,Status,CaseNumber, User__r.Name,OwnerId, Transferred_From__r.Name from Case where (User__c=:Userinfo.getUserId() Or Transferred_From__c=:Userinfo.getUserId()) And Transferred_Date__c >=:hrBefore And status ='Pending - Transfer'];
       return null;             
    }   
    
    /**
    * Method for the reject Accept Button  
    * @param  caseId,caseOwnerName
    * @return Void
    */
    
    @RemoteAction
    Global Static void updateCaseRecord(Id caseId, String caseOwnerName) {
        
        Case cse = New Case();
        List<User> lstUser = new list<user>();
        List<Work_Queue_Setup__c> lstWorkQueueSetup = new list<Work_Queue_Setup__c>();
        cse.id=caseId;
        cse.Warm_Case_Transfer_Action__c='Accepted';
        cse.Status='In Progress';
        cse.Case_Owner__c= caseOwnerName;
        lstUser = [Select Id,Current_Queue__c from User where Id=:Userinfo.getUserId()];
        if(lstUser.size() > 0){
            cse.Owner_Queue__c = lstUser[0].Current_Queue__c;
            cse.OwnerId=lstUser[0].id;
            lstWorkQueueSetup = [select Department__c, Service_Center__c from Work_Queue_Setup__c where name = :lstUser[0].Current_Queue__c];
            if(!lstWorkQueueSetup.isEmpty()){
                cse.Department__c = lstWorkQueueSetup[0].Department__c;
                cse.Service_Center__c=lstWorkQueueSetup[0].Service_Center__c;
            }
            else{
                cse.Department__c = null;
                cse.Service_Center__c = null;
            }
        }
        
        cse.User__c=NULL;
        cse.Transferred_From__c=NULL;
        update cse;        
    }   
    
    /**
    * Method for the reject Button  
    * @param  NULL
    * @return Void
    */
      
      @testVisible public void Reject()
      {
          if(updCaseId != null)
          {
              Case cse=new Case();
              if(Schema.sObjectType.Case.fields.id.isAccessible()){ cse.id=updCaseId; }
              if(Schema.sObjectType.Case.fields.Warm_Case_Transfer_Action__c.isUpdateable()){ cse.Warm_Case_Transfer_Action__c='Rejected'; }
              if(Schema.sObjectType.Case.fields.Status.isUpdateable()){ cse.Status='In Progress'; }
              if(Schema.sObjectType.Case.fields.Case_Owner__c.isUpdateable()){ cse.Case_Owner__c= updCaseOwnerName; }
              if(Schema.sObjectType.Case.fields.User__c.isUpdateable()){ cse.User__c=NULL; }
              if(Schema.sObjectType.Case.fields.Transferred_From__c.isUpdateable()){ cse.Transferred_From__c=NULL; }
              update cse;
              getCases();
          }
          
      }
      
    /**
    * Method for the cancel Button  
    * @param  NULL
    * @return Void
    */
      @testVisible public void Cancel()
      {
          if(updCaseId != null)
          {
              Case cse=new Case();
              if(Schema.sObjectType.Case.fields.id.isAccessible()){ cse.id=updCaseId; }
              if(Schema.sObjectType.Case.fields.Warm_Case_Transfer_Action__c.isUpdateable()){ cse.Warm_Case_Transfer_Action__c='Cancelled'; }
              if(Schema.sObjectType.Case.fields.Status.isUpdateable()){ cse.Status='In Progress'; }
              if(Schema.sObjectType.Case.fields.Case_Owner__c.isUpdateable()){ cse.Case_Owner__c= updCaseOwnerName; }
              if(Schema.sObjectType.Case.fields.User__c.isUpdateable()){ cse.User__c=NULL; }
              if(Schema.sObjectType.Case.fields.Transferred_From__c.isUpdateable()){ cse.Transferred_From__c=NULL;}
              update cse;
              getCases();
          }
      }
           
}
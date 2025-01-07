/***************************************************************************************************
Apex Class Name  : CheckDuplicatesOnTask_T_HUM
Version          : 1.0
Created Date     : October 19 2015
Function         : Teat Class for CheckDuplicatesOnTask_H_HUM
**************************************************************************************************
Modification Log:

* Developer Name       Review                       Date                        Description
*--------------------------------------------------------------------------------------------------
* Ranjeeth Nagishetty                             11/15/2016                       Original Version
***************************************************************************************************/

  
@isTest
private Class CheckDuplicatesOnTask_T_HUM
{

    /**
    *  This method creates test data for test class
    *  @name setUpTestData
    *  @param NA
    *  @return NA
    *  @throws NA
    *  @see 
    */
    @testSetup static void setUpTestData()
    {
        User oUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oUser;
        system.assertEquals(oUser.lastname,'test User1');
        HUMTriggerSwitch__c objTrigswitch = new HUMTriggerSwitch__c ();
        objTrigswitch.name ='MET_Task__c';
        objTrigswitch.Exeute_Trigger__c =true;
        insert objTrigswitch;
        system.assertEquals(objTrigswitch.name,'MET_Task__c');
    }
    
    /**
    *  This method tests the insert logic on Task object 
    *  @name testInsertOnTask
    *  @param NA
    *  @return NA
    *  @throws exception - Checks for all Actions when inserting a record and throws an exception if Taskname  and case subtype matches lready exists.
    *  @see 
    */
    static testMethod void testInsertOnTask()
    {            
                 
        User oUser = [select Id,Name from User where name = 'test User1' limit 1];                
        system.runAs(oUser)
        {
            list<MET_Task__c> lsttask = new list<MET_Task__c>();
            MET_Task__c oTask = new MET_Task__c();
            oTask.Name = 'Test';
            oTask.Is_Active__c = true;
            oTask.Case_Subtype__c ='Routine';
            insert oTask;
            lsttask.add(oTask);
            
            MET_Task__c  oTaskDuplicate = new MET_Task__c ();
            oTaskDuplicate.Name = 'Test';
            oTaskDuplicate.Is_Active__c = true;
            oTaskDuplicate.Case_Subtype__c ='Routine';
            try 
            {
                insert oTaskDuplicate;
            }
            catch(DMLException e) 
            {
                system.assert(e.getMessage().contains(System.Label.UNIQUERECORD_TASK));
            }   
        }
        }
        
         
    /**
    *  This method tests the update on Task object. Also Throws an exception if task name and case subtype already exists.
    *  @name testUpdateOnTask
    *  @param NA
    *  @return NA
    *  @throws exception - Checks for all Task when updating a record and throws an exception if task name and case subtype already exists.
    *  @see 
    */
    static testMethod void testUpdateOnTask()
    {            
        User oUser = [select Id,Name from User where name = 'test User1' limit 1];                
        system.runAs(oUser)
        {
            MET_Task__c oTask = new MET_Task__c();
            oTask.Name = 'Test1';
            oTask.Is_Active__c = true;
            oTask.Case_Subtype__c ='Routine';
            insert oTask;
            system.assertEquals(oTask.Name,'Test1');
            
            MET_Task__c oTask1 = new MET_Task__c();
            oTask1.Name = 'TestAction1';
            oTask1.Case_Subtype__c ='Routine';
            oTask1.Is_Active__c = true;
            insert oTask1 ;
            system.assertEquals(oTask1.Name,'TestAction1');
            
            oTask.Name = 'New Test Action';
            oTask.Case_Subtype__c ='RightSource';
            update oTask;
            system.assertEquals(oTask.Name,'New Test Action');
            
            oTask.Name = 'Test Action1';
            oTask.Case_Subtype__c ='Routine';
            try 
            {
                update oTask;
            }
            catch(DMLException e) 
            {
                system.assert(e.getMessage().contains(System.Label.UNIQUERECORD_TASK));
            }
            
        }
    }
    
     /**
    *  This method tests the bulk insert on Task object. Also Throws an exception if Task name and case subtype  match and already exists.
    *  @name testBulkInsertOnAction
    *  @param NA
    *  @return NA
    *  @throws exception - Checks for all Task when inserting a record and throws an exception if Taskname already exists.
    *  @see 
    */
    static testMethod void testBulkInsertOnTaks()
    {            
        User oUser = [select Id,Name from User where name = 'test User1' limit 1];                
        system.runAs(oUser)
        {
            List<MET_Task__c > lstTask = new List<MET_Task__c >();
            for(integer i=0; i<202;i++)
            {
                MET_Task__c  oTask = new MET_Task__c ();
                oTask.Name = 'Test Action'+i;
                oTask.Case_Subtype__c='Routine';
                lstTask.add(oTask );
            }
            insert lstTask ;
            system.assertEquals(lstTask .size(),202);
            // Insert Action with the same Name, returns custom validation error
            List<MET_Task__c > lstTaskDuplicate = new List<MET_Task__c >();
            for(integer i=0; i<4;i++) 
            {
                MET_Task__c  oTask1 = new MET_Task__c  ();
                oTask1 .Name = 'Test Action'+i;
                oTask1.Case_Subtype__c='Routine';
                lstTaskDuplicate.add(oTask1);
            }
            try 
            {
                insert lstTaskDuplicate ;
            }
            catch(DMLException e) 
            {
                system.assert(e.getMessage().contains(System.Label.UNIQUERECORD_TASK));
            }
        }
      }
      
          /**
    *  This method tests the update on Task object. Also Throws an exception if Task name and Case subtype already exists.
    *  @name testBulkUpdateOnAction
    *  @param NA
    *  @return NA
    *  @throws exception - Checks for all Task when updating a record and throws an exception if Task name already exists.
    *  @see 
    */
    static testMethod void testBulkUpdateOnTask()
    {            
        User oUser = [select Id,Name from User where name = 'test User1' limit 1];                
        system.runAs(oUser)
        {
            List<MET_Task__c  > lstTask = new List<MET_Task__c >();
            for(integer i=0; i<5; i++)
            {
                MET_Task__c  oTask = new MET_Task__c  (); 
                oTask.Name = 'Test Action'+i;
                oTask.Case_Subtype__c='Routine';
                
                lstTask.add(oTask);
            }
            insert lstTask ;
            system.assertEquals(lstTask [0].Name,'Test Action0');
            MET_Task__c  otask0 = new MET_Task__c (Id=lstTask [0].id,Name='Test Action6',Case_Subtype__c='Routine'); 
            MET_Task__c  otask1= new MET_Task__c  (Id=lstTask [1].id,Name='Test Action3',Case_Subtype__c='Routine'); 
            MET_Task__c  otask2 = new MET_Task__c  (Id=lstTask [2].id,Name='Test Action4',Case_Subtype__c='Routine'); 
            List<MET_Task__c  > lstMETtaskDuplicate =  new MET_Task__c  [] {otask0 , otask1 , otask2};
            try 
            {
                update lstMETtaskDuplicate ;
            }
            catch(DMLException e) 
            {
                system.assert(e.getMessage().contains(System.Label.UNIQUERECORD_TASK));
            }
            
        }
     }
   
  }
/***************************************************************************************************
Apex Class Name  : CheckDuplicatesMETMilestone_T_HUM
Version          : 1.0
Created Date     : 03/16/2018
Function         : Teat Class for CheckDuplicatesMETMilestone_H_HUM
**************************************************************************************************
Modification Log:

* Developer Name       Review                       Date                        Description
*--------------------------------------------------------------------------------------------------
* Manoj Chaurasia      202594                       03/16/2018               Test Class for CheckDuplicatesMETMilestone_H_HUM
* Mithra Bharadwaj                                                           US#1522825 SonarQube Changes
* Vamshi Krishna Pemberthi						  06/08/2021 		Code Coverage Improvements
***************************************************************************************************/

@isTest
private Class CheckDuplicatesMETMilestone_T_HUM
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
        objTrigswitch.name = 'MET_Milestone__c';
        objTrigswitch.Exeute_Trigger__c = true;
        insert objTrigswitch;
        system.assertEquals(objTrigswitch.name,'MET_Milestone__c');
    }
    
    /**
    *  This method tests the insert logic on MET Milestone object 
    *  @name testInsertOnMETMilestone
    *  @param NA
    *  @return NA
    *  @throws exception - Checks for all Milestone when inserting a record and throws an exception 
    *   if Milestone record is already exist for same Case Type, Case Subtype, Task MET, Action MET and Source MET combination.
    *  @see 
    */
    static testMethod void testInsertOnMETMilestone()
    {            
     User oUser = [select Id,Name from User where name = 'test User1' limit 1];                
        system.runAs(oUser)
        {  
            MET_Task__c oTask = new MET_Task__c();
            oTask.Name='TestTask';
            oTask.Is_Active__c = true;
            oTask.Case_Subtype__c='Routine';
            insert oTask;
            system.assertEquals(oTask.Name,'TestTask');
            
            
            MET_Source__c oSource = new MET_Source__c();
            oSource.Name = 'TestSource';
            oSource.Is_Active__c = true;
            insert oSource;
            system.assertEquals(oSource.Name,'TestSource');
                        
            MET_Action__c oAction = new MET_Action__c();
            oAction.Name = 'TestAction';
            oAction.Is_Active__c = true;
            insert oAction;
            system.assertEquals(oAction.Name,'TestAction');
            
            MET_Milestone__c oMilestone = new MET_Milestone__c();
            oMilestone.Case_Type__c = 'Customer Inquiry';
            oMilestone.Case_Subtype__c = 'Routine';
            oMilestone.Source_Name__c = oSource.id;
            oMilestone.Action__c = oAction.id;
            oMilestone.Task__c = oTask.id;
            oMilestone.Is_Active__c = true;
            oMilestone.Is_Milestone__c = true;
            insert oMilestone;
                        
            MET_Milestone__c oMilestoneDup = new MET_Milestone__c();
            oMilestoneDup.Case_Type__c = 'Customer Inquiry';
            oMilestoneDup.Case_Subtype__c = 'Routine';
            oMilestoneDup.Source_Name__c = oSource.id;
            oMilestoneDup.Action__c = oAction.id;
            oMilestoneDup.Task__c = oTask.id;
            oMilestoneDup.Is_Active__c = true;
            oMilestoneDup.Is_Milestone__c = true;
			
            try 
            {
                insert oMilestoneDup;
            }
            catch(DMLException e) 
            {
                system.assert(e.getMessage().contains(System.Label.UNIQUERECORDASSOCIATEDSOURCE_ACTION)); 
            }   
        }
    }
        
         
    /**
    *  This method tests the update on MET Milestone object. Also Throws an exception 
    *  if MET Milestone record is already exists for same Case Type, Case Subtype, Task MET, Action MET and Source MET combination.
    *  @name testUpdateOnMETMilestone
    *  @param NA
    *  @return NA
    *  @throws exception - Checks for all Milestone when updating a record and throws an exception 
    *   if Milestone record is already exist for same Case Type, Case Subtype, Task MET, Action MET and Source MET combination.
    *  @see 
    */
    static testMethod void testUpdateOnMETMilestone()
    {            
        User oUser = [select Id,Name from User where name = 'test User1' limit 1];                
        system.runAs(oUser)
        {  

            MET_Task__c oTask1 = new MET_Task__c();
            oTask1.Name='TestTask1';
            oTask1.Is_Active__c = true;
            oTask1.Case_Subtype__c='Routine';
            insert oTask1;
            system.assertEquals(oTask1.Name,'TestTask1');

            MET_Task__c oTask2 = new MET_Task__c();
            oTask2.Name='TestTask2';
            oTask2.Is_Active__c = true;
            oTask2.Case_Subtype__c='Routine';
            insert oTask2;
            system.assertEquals(oTask2.Name,'TestTask2');  
            
            MET_Action__c oAction1 = new MET_Action__c();
            oAction1.Name = 'TestAction1';
            oAction1.Is_Active__c = true;
            insert oAction1;
            system.assertEquals(oAction1.Name,'TestAction1');
            
            
            MET_Action__c oAction2 = new MET_Action__c();
            oAction2.Name = 'TestAction2';
            oAction2.Is_Active__c = true;
            insert oAction2;
            system.assertEquals(oAction2.Name,'TestAction2');
            
            MET_Source__c oSource = new MET_Source__c();
            oSource.Name = 'TestSource';
            oSource.Is_Active__c = true;
            insert oSource;
            system.assertEquals(oSource.Name,'TestSource');
			
			MET_Source__c oSource2 = new MET_Source__c();
            oSource2.Name = 'TestSource2';
            oSource2.Is_Active__c = true;
            insert oSource2;
            system.assertEquals(oSource2.Name,'TestSource2');

            MET_Milestone__c oMilestone = new MET_Milestone__c();
            oMilestone.Case_Type__c = 'Customer Inquiry';
            oMilestone.Case_Subtype__c = 'Routine';
            oMilestone.Source_Name__c = oSource.id;
            oMilestone.Action__c = oAction1.id;
            oMilestone.Task__c = oTask1.id;
            oMilestone.Is_Active__c = true;
            oMilestone.Is_Milestone__c = true;
            insert oMilestone;
                        
            MET_Milestone__c oMilestone1 = new MET_Milestone__c();
            oMilestone1.Case_Type__c = 'Customer Inquiry';
            oMilestone1.Case_Subtype__c = 'Routine';
            oMilestone1.Source_Name__c = oSource.id;
            oMilestone1.Action__c = oAction2.id;
            oMilestone1.Task__c = oTask2.id;
            oMilestone1.Is_Active__c = true;
            oMilestone1.Is_Milestone__c = true;
            insert oMilestone1;
            
            oMilestone.Action__c = oAction2.id;
			oMilestone.Source_Name__c = oSource2.id;
            update oMilestone;
            oMilestone1.Task__c = oTask1.id;
            try 
            {
                update oMilestone1;
            }
            catch(DMLException e)
            {
                system.assert(e.getMessage().contains(System.Label.UNIQUERECORDASSOCIATEDSOURCE_ACTION)); 
            }            
        }
    }
    
     /**
    *  This method tests the bulk insert on MET Milestone object.
    *  @name testBulkInsertOnMETMilestone
    *  @param NA
    *  @return NA
    *  @throws exception - Checks for all Milestone when inserting a record and throws an exception 
    *   if Milestone record is already exist for same Case Type, Case Subtype, Task MET, Action MET and Source MET combination.
    *  @see 
    */
    static testMethod void testBulkInsertOnMETMilestone()
    {            
        User oUser = [select Id,Name from User where name = 'test User1' limit 1];                
        system.runAs(oUser)
        {
            MET_Source__c oSource = new MET_Source__c();
            oSource.Name = 'TestSource';
            oSource.Is_Active__c = true;
            insert oSource;
            system.assertEquals(oSource.Name,'TestSource');
                
            List<MET_Milestone__c> lstMETMilestone = new List<MET_Milestone__c>(); //lstAssociatedAction // oMETMilestone
            List<MET_Action__c> lstMetAction = new List<MET_Action__c>();
            List<MET_Task__c> lstMetTask = new List<MET_Task__c>();
            
            for(integer i=0; i<202;i++)
            {
                MET_Action__c oAction = new MET_Action__c();
                oAction.Name = 'TestAction'+i;
                oAction.Is_Active__c = true;
                lstMetAction.add(oAction) ;
            }
            insert lstMetAction; 

            for(integer i=0; i<202;i++)
            {
                MET_Task__c oTask1 = new MET_Task__c();
                oTask1.Name='TestTask';
                oTask1.Is_Active__c = true;
                oTask1.Case_Subtype__c='Routine';
                lstMetTask.add(oTask1);
            
            }
            insert lstMetTask; 
            
            for(integer i=0; i<202;i++)
            {           
                MET_Milestone__c oMETMilestone = new MET_Milestone__c();
                oMETMilestone.Case_Type__c = 'Customer Inquiry';
            	oMETMilestone.Case_Subtype__c = 'Routine';
                oMETMilestone.Source_Name__c = oSource.id;
                oMETMilestone.Action__c = lstMetAction [i].id;
                oMETMilestone.Task__c = lstMetTask [i].id;
                oMETMilestone.Is_Active__c = true;
                oMETMilestone.Is_Milestone__c = true;
                lstMETMilestone.add(oMETMilestone);
            }
            insert lstMETMilestone;
            system.assertEquals(lstMETMilestone.size(),202);
            
            List<MET_Milestone__c> lstMETMilestoneDup = new List<MET_Milestone__c>();
            MET_Action__c oAction = new MET_Action__c();
            oAction.Name = 'TestAction';
            oAction.Is_Active__c = true;
            insert oAction;
            system.assertEquals(oAction.Name,'TestAction');
            
            MET_Task__c oTask1 = new MET_Task__c();
            oTask1.Name='TestTask';
            oTask1.Is_Active__c = true;
            oTask1.Case_Subtype__c='Routine';
            insert oTask1;
            system.assertEquals(oTask1.Name,'TestTask');
            
            for(integer i=0; i<4;i++)
            {
                MET_Milestone__c oMETMilestone = new MET_Milestone__c();
                oMETMilestone.Case_Type__c = 'Customer Inquiry';
            	oMETMilestone.Case_Subtype__c = 'Routine';
                oMETMilestone.Source_Name__c = oSource.id;
                oMETMilestone.Action__c = oAction.id;
                oMETMilestone.Task__c = oTask1.id;
                oMETMilestone.Is_Active__c = true;
                oMETMilestone.Is_Milestone__c = true;
                lstMETMilestoneDup.add(oMETMilestone);
                
            }
            try 
            {
                insert lstMETMilestoneDup;
            }
            catch(DMLException e) 
            {
                system.assert(e.getMessage().contains(System.Label.UNIQUERECORDASSOCIATEDSOURCE_ACTION)); 
            }
        }
      }
      
    /**
    *  This method tests the bulk insert on MET Milestone object.
    *  @name testBulkUpdateOnMETMilestone
    *  @param NA
    *  @return NA
    *  @throws exception - Checks for all Milestone when updating a record and throws an exception 
    *   if Milestone record is already exist for same Case Type, Case Subtype, Task MET, Action MET and Source MET combination.
    *  @see 
    */
    static testMethod void testBulkUpdateOnMETMilestone()
    {            
        User oUser = [select Id,Name from User where name = 'test User1' limit 1];                
        system.runAs(oUser)
        {
            MET_Source__c oSource = new MET_Source__c();
            oSource.Name = 'TestSource';
            oSource.Is_Active__c = true;
            insert oSource;
            system.assertEquals(oSource.Name,'TestSource');
                        
            MET_Action__c oAction = new MET_Action__c();
            oAction.Name = 'TestAction';
            oAction.Is_Active__c = true;
            insert oAction;
            system.assertEquals(oAction.Name,'TestAction');
            
            MET_Task__c oTask = new MET_Task__c();
            oTask.Name='TestTask';
            oTask.Is_Active__c = true;
            oTask.Case_Subtype__c='Routine';
            insert oTask;
            system.assertEquals(oTask.Name,'TestTask');   
            
            
            
            List<MET_Milestone__c> lstMETMilestone = new List<MET_Milestone__c>(); 
            List<MET_Action__c> lstMETAction = new List<MET_Action__c>();
            List<MET_Task__c> lstMETTask = new List<MET_Task__c>();
            for(integer i=0; i<4;i++)
            {
                MET_Action__c oAction1 = new MET_Action__c();
                oAction1.Name = 'TestAction'+i;
                oAction1.Is_Active__c = true;
                lstMETAction.add(oAction1);
                
                
                MET_Task__c oTask1 = new MET_Task__c();
                oTask1.Name='TestTask';
                oTask1.Is_Active__c = true;
                oTask1.Case_Subtype__c='Routine';
                lstMETTask.add(oTask1);
                   
            }
            insert lstMETAction;
            insert lstMETTask;
            for(integer i=0; i<4;i++)
            {
                MET_Milestone__c oMETMilestone = new MET_Milestone__c();
                oMETMilestone.Case_Type__c = 'Customer Inquiry';
            	oMETMilestone.Case_Subtype__c = 'Routine';
                oMETMilestone.Source_Name__c = oSource.id;
                oMETMilestone.Action__c = lstMETAction[i].id;
                oMETMilestone.Task__c = lstMETTask[i].id;
                oMETMilestone.Is_Active__c = true;
                oMETMilestone.Is_Milestone__c = true;
                lstMETMilestone.add(oMETMilestone);
            }
            insert lstMETMilestone;                      
            system.assertEquals(lstMETMilestone.size(),4);
            MET_Milestone__c oMETMilestone = new MET_Milestone__c(id=lstMETMilestone[0].id, Case_Type__c = 'Customer Inquiry', Case_Subtype__c = 'Routine',
                                         Is_Milestone__c = true, Source_Name__c=oSource.id, Action__c= oAction.id,Task__c = oTask.id, Is_Active__c=true);
            MET_Milestone__c oMETMilestone1 = new MET_Milestone__c(id=lstMETMilestone[1].id , Case_Type__c = 'Customer Inquiry', Case_Subtype__c = 'Routine',
                                         Is_Milestone__c = true, Source_Name__c=oSource.id, Action__c= oAction.id,Task__c = oTask.id, Is_Active__c=true);
            MET_Milestone__c oMETMilestone2 = new MET_Milestone__c(id=lstMETMilestone[2].id , Case_Type__c = 'Customer Inquiry', Case_Subtype__c = 'Routine',
                                         Is_Milestone__c = true, Source_Name__c=oSource.id, Action__c= oAction.id,Task__c = oTask.id, Is_Active__c=true);
            List<MET_Milestone__c> lstMETMilestoneDup = new MET_Milestone__c []{oMETMilestone,oMETMilestone1,oMETMilestone2};
            
            try 
            {
                update lstMETMilestoneDup;
            }
            catch(DMLException e)
            {
                system.assert(e.getMessage().contains(System.Label.UNIQUERECORDASSOCIATEDSOURCE_ACTION)); 
            }    
        }
     }
   
  }
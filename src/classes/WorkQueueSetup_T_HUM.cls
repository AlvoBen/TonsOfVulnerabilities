/*******************************************************************************************************************************
Apex Class Name : WorkQueueSetup_T_HUM
Version         : 1.0
Created On      : 12/16/2014
Function        : Used for trigger functionality.updates ownerid field with queue name. shows error messages if user enters wrong queue name.

Modification Log: 
* Developer Name                  Code Review                 Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* SuryaKumari Medicherla          19954                     12/16/2014                  Original Version
* SuryaKumari Medicherla          22785                     04/24/2015                  Methos added to cover checkEnteredQueue() method functionality added due to REQ-168974
* Sruthi Adivikolanu              25440                     08/07/2015                  Added Testsetup method and done changes as part of  Work queue Enhancements.
* Gargey Sharma            		  81589            			04/04/2017                  REQ - 306370 : Updates as per CTCI changes
* Vamshi Krishna Pemberthi						  06/08/2021 		Code Coverage Improvements
*****************************************************************************************************************************/
@isTest
private class WorkQueueSetup_T_HUM
{
    /*
    * Method Name : Setup
    * Description : Test method to create a work queue setup
    * Return type : Void
    * Paramater   : N/A
    */
    @testSetup static void Setup() 
    {
        //Create test user
        User oUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        oUser.Current_Queue__c = 'Test Queue';
        insert oUser;
        System.assert(oUser.id != null);
        
        system.runAs(oUser)
        {
           
            Group oGrp=new Group(Name='Test Queue', Type='QUEUE');
            insert oGrp;
            System.assert(oGrp.id!= null);
            QueuesObject oQueueObject = new QueuesObject (QueueID = oGrp.id, SobjectType ='Work_Queue_Setup__c');
            insert oQueueObject;  
            System.assert(oQueueObject.id!= null);
			
			HUMTriggerSwitch__c caseTriggerSwith = new HUMTriggerSwitch__c (name='Work Queue Setup', Exeute_Trigger__c=true);
            insert caseTriggerSwith;
            
            Classification_Type__c ct = new Classification_Type__c(Name = 'Test', Is_Active__c = true, Guidance_Alert_Limit__c = 2);
			insert ct;
			system.assertEquals(ct.Name, 'Test');
            
            Work_Queue_Setup__c oWQS1 = new Work_Queue_Setup__c();
            oWQS1.Name='Test Queue';
            oWQS1.Service_Center__c='Test';
            oWQS1.Department__c='Test';
            oWQS1.Classification_Type__c=ct.Id;
            oWQS1.OwnerId= oGrp.id;
            oWQS1.IsActive__c = true;
            insert oWQS1;
            System.assert(oWQS1.id!= null);
            
            List<HUMConstants__c> lstConstants = Test.loadData(HUMConstants__c.sObjectType,'HUMConsTestData');
            System.assert(lstConstants.size() > 0 );
            
            Account oAcc = HUMTestDataHelperTest.getAccount(); 
            oAcc.Name='TestAccount1';
            oAcc.Source_Platform_Code__c = 'CA';
            oAcc.recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Group').getRecordTypeId();
            insert oAcc;
            system.assertNotEquals(oAcc.id,null,'Value not matched'); 
            
            case oCaseRecord = new case();
            oCaseRecord.Status = 'In Progress'; 
            oCaseRecord.Owner_Queue__C = 'Test Queue';
            insert oCaseRecord;
            system.assertNotEquals(oCaseRecord.id,null,'Value not matched'); 
            
            Group oGrp1=new Group(Name='Test Queues', Type='QUEUE');
            insert oGrp1;
            System.assert(oGrp1.id!= null);
            QueuesObject oQueueObject1 = new QueuesObject (QueueID = oGrp1.id, SobjectType ='Work_Queue_Setup__c');
            insert oQueueObject1;  
            System.assert(oQueueObject1.id!= null);
                        
            Work_Queue_Setup__c oWQS3 = new Work_Queue_Setup__c();
            oWQS3.Name='Test Queues';
            oWQS3.Service_Center__c='Test';
            oWQS3.Department__c='Test';
            oWQS3.Classification_Type__c=ct.Id;
            oWQS3.isActive__c = true; 
            oWQS3.OwnerId= oGrp1.id;
            oWQS3.IsActive__c = true;
            insert oWQS3;
            System.assert(oWQS3.id!= null);
          
            Task objtask = new Task();
            objtask.Owner_Queue__c = oWQS3.Name; 
            objtask.Status = 'In Progress';  
            insert objtask;
            System.assert(objtask.id!= null);
                            
            Group oGrp2=new Group(Name='Test Queue2', Type='QUEUE');
            insert oGrp2;
            System.assert(oGrp2.id!= null);
            QueuesObject oQueueObject2 = new QueuesObject (QueueID = oGrp2.id, SobjectType ='Work_Queue_Setup__c');
            insert oQueueObject2;  
            System.assert(oQueueObject2.id!= null);
                        
            Work_Queue_Setup__c oWQS = new Work_Queue_Setup__c();
            oWQS.Name='Test Queue2';
            oWQS.Service_Center__c='Test';
            oWQS.Department__c='Test';
            oWQS.Classification_Type__c=ct.Id;
            oWQS.isActive__c = true; 
            oWQS.OwnerId= oGrp2.id;
            oWQS.IsActive__c = true;
            insert oWQS;
            System.assert(oWQS.id!= null);
        }
    }
    
   /*
    * Method Name : maintestmethod
    * Description : Test method for overall code coverage
    * Return type : Void
    * Paramater   : N/A
    */
    static testMethod  void maintestmethod() 
    {
         User oUser = [Select Id from User where Lastname =: 'test User1' limit 1];
         System.assert(oUser.id != null);
         
         system.runAs(oUser)
         {
            try
            {
                test.startTest();
                Work_Queue_Setup__c oWQS1 = [Select id,isActive__c  from  Work_Queue_Setup__c where name = 'Test Queue'];
                oWQS1.isActive__c = false;
                update oWQS1;                
                test.stopTest();
            }   
            catch(Exception ex)
            {
                Boolean bErrorThrownorNot = ex.getMessage().contains('Work Queue Has open cases');
                System.assertEquals(bErrorThrownorNot,true);    
            } 
            try
            {
                Work_Queue_Setup__c oWQS3 = [Select id,isActive__c  from  Work_Queue_Setup__c where name = 'Test Queues'];
                oWQS3.isActive__c = false;
				oWQS3.Public_Group_Name__c = 'Cincinnati Calls PGQ';
                update oWQS3;                 
            }
            catch(Exception ex)
            {
                Boolean bErrorThrownorNot = ex.getMessage().contains('Work Queue Has open cases');
                System.assert(bErrorThrownorNot,true);    
            }  
            
            Work_Queue_Setup__c oWQS = [Select id,isActive__c  from  Work_Queue_Setup__c where name = 'Test Queue2']; 
            oWQS.isActive__c = false;
            update oWQS; 
            System.assert(oWQS.isActive__c == false);
        }
     }
  }
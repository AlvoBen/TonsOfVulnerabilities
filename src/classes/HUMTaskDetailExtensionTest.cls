/****************************************************************************
Apex Class Name :  HUMTaskDetailExtensionTest 
Version: 1.0 
Created On     : 7/31/2014
Function         : Test class to HUMTaskDetailExtensionTest apex class

Modification Log : 
* Developer Name          Code Review                        Date                        Description
*------------------------------------------------------------------------------------------------------------------------------
* Mrudula Jaddu             17093                            07/31/2014                  Original Version
* Asha Deepika Koyyana      20572                            01/28/2015                  Fixed test class failure for Defect#164139 
* Ranjeeth Nagishetty       22606                            04/24/2015                  Test Class Failure for Defect 179884.0001
* Prachi Bhasin             33486                            07/18/2016                  Changes made as part of REQ- 270349
* Shreya Choodamani         90487                            04/28/2017                  REQ#: 311397 Updated the test class for Pharmacy USers
* Pradeepkumar Dani         99570                            06/08/2017                  Intent__c.Classification__c & Intent__c.Etl_Idtfr__c Clean up
* Pradeepkumar Dani         100996                           06/13/2017                  Classification__c.Classification_Type__c & Etl_Idftr__c Clean Up
* Pallavi Ravishankar       268909                           08/09/2018                  Updated test class for Parent task query as per REQ - 361926  
* Santhi Mandava                                             03/12/2019                  Removed "DpaaSWebServiceMockImpl" usage.
* Samantha Tennyson                                          02/08/2021                  User Story 1875448: PR00091574 - MF 1 - Quality - Ability to capture additional information related to Notification Task (RF)
* Jasmeen Shanagri											 01/04/2022				   User Story 2701238 : Handle exceptions
***********************************************************************************************************************************/

@isTest
Private Class HUMTaskDetailExtensionTest
{  
      /*
     * Used to create wrapper class data
     * @param        NA
       * @return       void
     */
     @testSetup static void prepareTestData() 
     {       
            User u = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
            insert u;
            system.assert(u.lastname == 'test User1');
        
                User oUser2 = HUMTestDataHelperTest.createTestUser('User3','Humana Pharmacy Specialist');
            insert oUser2;
            System.assertEquals(oUser2.LastName, 'test User3');
            
            System.runAs ( u ) 
            {            
                Account oAcc = HUMTestDataHelperTest.getAccount(); 
                oAcc.Name='TestAccounoTask';
                oAcc.Source_Platform_Code__c = 'CA';
                insert oAcc;                
                system.assertEquals(oAcc.Name=='TestAccounoTask',true);
                
                List<Classification__c> lstClassification = New List<Classification__c>();
                Classification__c classificationRec = New Classification__c();
                classificationRec.Name = 'Claims';
                classificationRec.Is_Active__c = true;
                lstClassification.add(classificationRec);           
                
                insert lstClassification;
                system.assertEquals(lstClassification[0].Name=='Claims',true);
                
                List<Intent__c> lstIntent = New List<Intent__c>();
                Intent__c intentRec = New Intent__c();
                intentRec.Name = 'Filling';
                intentRec.Is_Active__c = true;
                lstIntent.add(intentRec);
    
                insert lstIntent;
                system.assertEquals(intentRec.Name == 'Filling', true);

                Case oCase = New Case(AccountId = oAcc.Id, Intent_Id__c = lstIntent[0].id, General_Case__c = True, Classification_Id__c = lstClassification[0].id,
                                   Interacting_With_Type__c = 'Member', Interacting_With__c = oAcc.Id, Due_Date__c = Date.Today(),
                                   Case_Owner__c = UserInfo.getUserName(), Owner_Queue__c = 'Cincinnati CRU', Work_Queue_View_Name__c = 'Cincinati CRU', 
                                   Service_Center__c = 'Cincinnati', Department__c = 'CRU', status = 'In Progress');
                insert oCase;               
                system.assertNotEquals(oCase,null,'Value not matched');             
                
                Task oTask = New Task(WhatId = oCase.id, Task_Owner__c = UserInfo.getUserName(), Owner_Queue__c = 'Cincinnati CRU',
                                   Work_Queue_View_Name__c = 'Cincinati CRU', Service_Center__c = 'Cincinnati', Callback_Requested__c = 'Yes',
                                   Time_Zone__c = 'EST - Eastern', ActivityDate = Date.Today(), Call_Back_Start__c = datetime.now().time(), Call_Back_End__c= datetime.now().time(),
                                   Department__c = 'CRU', status = 'In Progress',Type='Notification task');
                insert oTask;
                system.assertNotEquals(oTask, null, 'Value not matched');
                
                Pharmacy_Supervisor_HUM__c objPharmacySup = New Pharmacy_Supervisor_HUM__c(Name ='CRMS_300_Humana_Pharmacy_Supervisor');
                insert objPharmacySup;
                system.assertEquals(objPharmacySup.Name,'CRMS_300_Humana_Pharmacy_Supervisor');             
        } 
    }
    /*
     * This  method Is used  for task record data through URL. 
     * @param        NA
     * @return       void
     */     
       
        Static testMethod void overallTestMethod()
    {    
            User u = [Select id, UserName from User where LastName = 'test User1' LIMIT 1 ] ;
            system.assertEquals(u.UserName,'user1user1@humcrmstest.com');
        
            System.runAs(u)                              
           { 
                Task oTask = [Select id from Task];
                ApexPages.currentPage().getParameters().put('id', oTask.id);                
                ApexPages.currentPage().getParameters().put('ownerid', u.id); 
                         
                ApexPages.StandardController stdController = New ApexPages.StandardController(oTask);                
                HumTaskDetailExtension obj = New HumTaskDetailExtension(stdController);                
                ApexPages.currentPage().getParameters().put('id', '098875757');                
                HUMTaskDetailExtension obj2 = New HUMTaskDetailExtension(stdController);
                obj2.closeTask();
           }       
       }
       
        /*
     * This  method Is used  for task record data through URL. 
     * @param        NA
     * @return       void
     */     
       Static testMethod void overallPharSpecTestMethod()
        {    
            
            User u = [Select id, UserName from User where LastName = 'test User3' LIMIT 1 ] ;
            system.assertEquals(u.UserName,'user3user3@humcrmstest.com');
            
            Classification__c oClass = [Select Id, Name from CLassification__c where Name = 'Claims'];
            Intent__c oInt = [Select Id, Name from Intent__c where name='Filling'];
            
            System.runAs(u)                              
            { 
                Account oAcc = HUMTestDataHelperTest.getAccount(); 
                oAcc.Name='TestPharAccounoTask';
                oAcc.Source_Platform_Code__c = 'CA';
                insert oAcc;                
                system.assertEquals(oAcc.Name=='TestPharAccounoTask',true);
                
                Product2 testProduct = new Product2(Name ='testprod1',  ProductCode ='MED');
                insert testProduct;
                
                Policy__c testPolicy = new Policy__c(Group_Name__c = oACC.ID, Major_LOB__c = 'MER', Product__c = testProduct.ID,Policy_Group_Number__c='11111');
                insert testPolicy;
                
                Policy_Member__c polMem = new Policy_Member__c(Policy__c = testPolicy.ID, Member__c = oACC.ID , ETL_Record_Deleted__c = false, Effective_Date__c = '01/01/2017' , End_Date__c = '12/31/2017');
                insert polMem;
                
                Member_ID__c memID = new Member_ID__c(Policy__c = testPolicy.ID, Policy_Member__c = polMem.ID, Member_Card_External_ID__c= '123345' , name = '360306641' , Type__c ='MedicareID');
                insert memID;                                                           
                
                insert new HUMConstants__c( Name = 'HUMClosed' , IntegerValue__c = 15, StringValue__c = '15');
                insert new HUMConstants__c( Name = 'HUMWorkTask' , StringValue__c = 'Medicare Address Verification Form');
                
                Case oCase = New Case(AccountId = oAcc.Id, Policy_member__c = polMem.id, Intent_Id__c = oInt.id, General_Case__c = True, Classification_Id__c = oClass.id, Due_Date__c = Date.Today(),
                                   Case_Owner__c = UserInfo.getUserName(), Interacting_With_Type__c = 'Member', Interacting_With__c = oAcc.Id, Owner_Queue__c = 'Cincinnati CRU', Work_Queue_View_Name__c = 'Cincinati CRU', 
                                   Service_Center__c = 'Cincinnati', Department__c = 'CRU', status = 'In Progress');
                insert oCase;               
                system.assertNotEquals(oCase,null,'Value not matched');             
                
                Task oTask = New Task(WhatId = oCase.id, Task_Owner__c = UserInfo.getUserName(), Owner_Queue__c = 'Cincinnati CRU',
                                   Work_Queue_View_Name__c = 'Cincinati CRU', Service_Center__c = 'Cincinnati',
                                   Department__c = 'CRU', status = 'In Progress',Type='Work task');
                insert oTask;               
                system.assertNotEquals(oTask, null, 'Value not matched');   
                                   
                Task oTask1 = new Task(WhatId = oCase.id,type='Medicare Address Verification Form', Task_Owner__c=UserInfo.getUserName(), Owner_Queue__c='Cincinnati CRU',
                               Work_Queue_View_Name__c='Cincinati CRU', Service_Center__c ='Cincinnati', Department__c='CRU', Open_Work_Task_Count__c=NULL,
                               Total_Work_Task_Count__c=NULL,status='In Progress');
                insert oTask1;  
                system.assertNotEquals(oTask1, null, 'task 2 not matched'); 
                
                HUM_Task_Heirarchy__c HUMHeirarchy1 = new HUM_Task_Heirarchy__c(Hum_CaseID__c=oCase.Id,HUM_Dependent_TaskID__c =oTask.Id, HUM_Parent_TaskID__c=oTask1.Id);
                insert HUMHeirarchy1;             
                system.assertNotEquals(HUMHeirarchy1, null, 'heirarchy not matched');  
                          
                ApexPages.currentPage().getParameters().put('id', oTask.id);                
                ApexPages.currentPage().getParameters().put('ownerid', u.id); 
                         
                ApexPages.StandardController stdController = New ApexPages.StandardController(oTask);                
                HumTaskDetailExtension obj = New HumTaskDetailExtension(stdController);   
                List<Error_Log__c> errLog = new List<Error_Log__c>();
                obj.lstErrors = errLog;				
                ApexPages.currentPage().getParameters().put('id', '098875757');                
                HUMTaskDetailExtension obj2 = New HUMTaskDetailExtension(stdController);          
                errLog = obj.lstErrors;

            }       
       }
 }
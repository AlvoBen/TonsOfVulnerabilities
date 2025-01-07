/************************************************************************************************************************
Apex class Name    : ChangeCaseOwner_T_HUM
Version            : 1.0
Function           : Test class ChangeCaseOwner_C_HUM
Created On         : August 07 2020

 Modification Log
 *   Developer                       Date               Description
 * -------------------------------------------------------------------------------------------------------------------------------                
 *   Prudhvi Pamarthi                08/07/2020          Original Version 
 * Vamshi Krishna Pemberthi						  06/08/2021 		Code Coverage Improvements
 * Prasuna Pattabhi                11/05/2021                  Code Coverage Improvements
**************************************************************************************************************************************************************************************************************************************************************************/

@isTest
private class ChangeCaseOwner_T_HUM
{
    /**
    * Test Setup method to create all the data required for testing.
    *
    * @param     NULL
    * @return    void 
    */
    @testSetup static void setup() 
    {
         User oCurrentUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
         oCurrentUser.Current_Queue__c = 'Cincinnati CRU1';
         insert oCurrentUser;
         System.assertEquals( oCurrentUser.Current_Queue__c,'Cincinnati CRU1');
         
         User oNonHomeOfficeUser = HUMTestDataHelperTest.createTestUser('User2','Customer Care Specialist');
         insert oNonHomeOfficeUser;
         System.assertEquals(oNonHomeOfficeUser.LastName,'test User2');

         User HPUser = HUMTestDataHelperTest.createTestUser('User3','Humana Pharmacy Specialist');
         insert HPUser;
         system.assertEquals(HPUser.lastname=='test User3',true);
         
         Group oGrp = New Group(Name ='Cincinnati_CRU1',DeveloperName='Cincinnati_CRU1', Type='QUEUE');
         insert oGrp;
         System.assertEquals(oGrp.Name,'Cincinnati_CRU1');

         GroupMember gm = New GroupMember(); 
         gm.GroupId=oGrp.id;
         gm.UserOrGroupId = oCurrentUser.id;
         insert gm;
         User oUser = [Select Id, Name, email from User where Id=:gm.UserOrGroupId LIMIT 1];
         System.assertEquals(oUser.Name,'test User1');
         
         PermissionSet rFPS = [SELECT ID From PermissionSet WHERE Name = 'CRM_RF_LouisvilleUser_Access' LIMIT 1];
         insert New PermissionSetAssignment(AssigneeId = oUser.id, PermissionSetId = rFPS.id);

         QueuesObject oQueueObject = New QueuesObject(QueueID = oGrp.id, SobjectType ='Work_Queue_Setup__c');
         insert oQueueObject ;   
         System.assertEquals(oQueueObject.SobjectType, 'Work_Queue_Setup__c');
         
         System.runAs(oCurrentUser)
         {
             Classification_Type__c objcT = New Classification_Type__c();
             objcT.Name ='CallRSOtest';
             objcT.Is_Active__c = true;
             insert objcT;
             System.assertEquals(objcT.Name,'CallRSOtest');
            
             Work_Queue_Setup__c oWQS = New Work_Queue_Setup__c(Name = oGrp.Name);
             oWQS.Service_Center__c ='Cincinnati';
             oWQS.Department__c ='CRU1';
             oWQS.CokY__c =true;
             oWQS.HO_Medical__c =true;
             oWQS.HO_Dental__c =true;
             oWQS.General__c =true;
             oWQS.TenantID__c ='00'; 
             oWQs.Public_Group_Name__c='Cincinnati_CRU1_PGQ';   
             oWQs.isActive__c = TRUE;               
             oWQS.ownerId = oGrp.Id;   
             oWQS.Classification_Type__c = objcT.id;         
             insert oWQS;
             System.assertEquals(oWQS.TenantID__c ,'00');
             
            Group oGrp1 = [Select id, Name, DeveloperName from Group where type = 'Queue' AND DeveloperName = :System.label.HUMQMSMarketHOQueue];
            Work_Queue_Setup__c oWQS1 = New Work_Queue_Setup__c(Name = oGrp1.Name);
            oWQS1.Service_Center__c ='Market';
            oWQS1.Department__c ='Home Office Quality Operations Compliance';
            oWQS1.General__c =false;
            oWQS1.HO_Medical__c =true;
            oWQS1.TenantID__c ='00';       
            insert oWQS1;
            System.assertEquals(oWQS1.TenantID__c ,'00');
            
            Work_Queue_Topic__c owrkqtopic = New Work_Queue_Topic__c();
            owrkqtopic.Name = 'Work Queue Topic';
            insert owrkqtopic;
            System.assertEquals(owrkqtopic.Name,'Work Queue Topic');

            Work_Queue_Junction__c owrkqjunction = New Work_Queue_Junction__c();
            owrkqjunction.Name = 'Work Queue Junction';
            owrkqjunction.Work_Queue_Setup_Name__c = oWQS.id;
            owrkqjunction.Work_Queue_Topic_Name__c = owrkqtopic.id;
            insert owrkqjunction;
            System.assertEquals(owrkqjunction.Name,'Work Queue Junction');
          }  
     }
    
    /*
    * Method Name : nonHPTestMethod
    * Description : This method is used to CCS user related logic
    * Return type : None
    * Parameter   : None
    */
     Private Static testMethod void nonHPTestMethod()
     {
        User oCurrentUser = [SELECT id, Current_Queue__c, LastName from User WHERE  Lastname = 'test User1' LIMIT 1];
        User oNonHomeUser = [SELECT id, Current_Queue__c, LastName from User WHERE  Lastname = 'test User2' LIMIT 1];
        System.runAs(oCurrentUser)
        {
            ChangeCaseOwner_C_HUM objHandler = New ChangeCaseOwner_C_HUM();   
            test.startTest();  
            objHandler.sFilter = 'Home Office All Other';
            List <SelectOption> lstOption = objHandler.getServiceCenters();
            System.assert(lstOption.size()>0);
            objHandler.sServiceCenter = 'Cincinnati';
            lstOption = objHandler.getDepartments();
            System.assert(lstOption.size()>0);
            objHandler.sDepartment = 'CRU1';
            objHandler.populateQueueName();
            lstOption = objHandler.getTopics();                       
            objHandler.sTopic='xxx' ;  
            objHandler.sFilter = 'General';
            lstOption = new List <SelectOption>();
            lstOption = objHandler.getServiceCenters();  
            System.assert(lstOption.size()>0); 
            objHandler.sFilter = 'Home Office Dental';
            lstOption = new List <SelectOption>();
            lstOption = objHandler.getServiceCenters(); 
            System.assert(lstOption.size()>0);  
            Test.stopTest();     
        }   
    }
    
    /*
    * Method Name : testHP
    * Description : This method is used to humana pharmacy related logic
    * Return type : None
    * Parameter   : None
    */
   
    Static testMethod void testHP()
    {
        User testUser = [Select Id,Name, Current_Queue__C from User where LastName='test User3'];
        System.runAs(testUser)
        {
            Test.startTest();
            ChangeCaseOwner_C_HUM objHandler = New ChangeCaseOwner_C_HUM();   
            objHandler.sFilter = 'Home Office All Other';
            List <SelectOption> lstOption = objHandler.getServiceCenters();
            System.assert(lstOption.size()>0);
            objHandler.sServiceCenter = 'Cincinnati';
            lstOption = objHandler.getDepartments();
            System.assert(lstOption.size()>0);
            objHandler.sDepartment = 'CRU1';
            objHandler.populateQueueName();
            lstOption = objHandler.getTopics();                       
            objHandler.sTopic='xxx' ;  
            objHandler.sFilter = 'General';
            lstOption = new List <SelectOption>();
            lstOption = objHandler.getServiceCenters();
            System.assert(lstOption.size()>0);   
            objHandler.sFilter = 'Home Office Dental';
            lstOption = new List <SelectOption>();
            lstOption = objHandler.getServiceCenters(); 
            System.assert(lstOption.size()>0);  
			objHandler.sServiceCenter = 'Cincinnati CRU';
			lstOption = objHandler.getDepartments();
			objHandler.sDepartment = 'CRU1';
            objHandler.populateQueueName();
            Test.stopTest();
        }
    }

    /* 
    * Method Name : testGetTransferReason
    * Description : This method Is used To test getTransferReason related logic
    * Return type : None
    * Parameter   : None
    */
   
    Static testMethod void testGetTransferReason()
    {
        User testUser = [Select Id, Name, Current_Queue__C from User where LastName='test User3'];
        System.runAs(testUser)
        {
            ChangeCaseOwner_C_HUM objHandler = New ChangeCaseOwner_C_HUM();
            objHandler.caseCom='test';
            objHandler.sOwnerName='test';
            objHandler.sTransferReason='test';
            objHandler.sGrpQueueName='test';
            
            Test.startTest();              
            List <SelectOption> lstOption = objHandler.getTransferReason();   
            Test.stopTest();
        }
    }
}
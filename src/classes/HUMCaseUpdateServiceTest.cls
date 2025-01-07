/****************************************************************************************
Apex Class Name  : HUMCaseUpdateServiceTest
Version          : 1.0 
Created Date     : August 15 2014
Function         : Test class to HUMCaseUpdateService Apex class
Modification Log :
 *  Modification ID     Developer               Code Review            Date                 Description
 * -------------------------------------------------------------------------------------------------------           
 *          1.0         Vishal Verma              17353                08/15/2014         Original Version 
 *          1.1         Vishal Verma              18313                11/27/2014         Change code for creating case test record as Classification and Intent are required field when case is updtaed
 *          1.2         SuryaKumari Medicherla    20579                01/28/2015         Code Modified to fix test class failure
 *          1.3         Ranjeeth Nagishetty       21167                02/26/2015         Test Class updated as per REQ-81852
 * 			1.4			Avinash Choubey           33337   			   07/11/2016         Chnages made as part of REQ- 255151
 *			1.5			Pradeepkumar Dani		  99570				   06/08/2017		  Intent__c.Classification__c & Intent__c.Etl_Idtfr__c Clean up
 *			1.6			Pradeepkumar Dani	     100996 			   06/13/2017		  Classification__c.Classification_Type__c & Etl_Idftr__c Clean Up
 ************************************************************************************************************/
@isTest
private class HUMCaseUpdateServiceTest
{
    Static HUMCaseUpdateRequestHelper request; 
    Static String caseNumber  ;
    Static String caseStatus;
    
     /**
    * testsetup method to create test data
    *<p>
    * @param    NULL
    * @return   void
    */
    @testSetup static void setup() {
    
       User  oUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oUser;
        system.assertEquals(oUser.lastname=='test User1',true);

        
        System.runAs (oUser) 
        {
        	Account oAcc = HUMTestDataHelperTest.getAccount();
            oAcc.Name = 'test account';
            oAcc.Enterprise_ID__c = '123456';
            oAcc.recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Group').getRecordTypeId();
            insert oAcc;
            System.assert(oAcc.Name == 'test account');
            
            Classification__c classificationRecDec = new Classification__c();
	        classificationRecDec.Name = 'Claims';
	        classificationRecDec.Is_Active__c = true;
	        insert classificationRecDec;
	        system.assertEquals(classificationRecDec.Name == 'Claims',true);
	        
	        Intent__c intentRec = new Intent__c();
	        intentRec.Name = 'Filling';
	        intentRec.Is_Active__c = true;
	        insert intentRec;
	        system.assertEquals(intentRec.name == 'Filling',true);
            
            Case caseObj = HUMTestDataHelperTest.getCase();
            caseObj.Interacting_With__c = oAcc.Id;
            caseObj.Interacting_With_Name__c = 'Test Name';
            caseObj.Classification_ID__c = classificationRecDec.id;
            caseObj.Intent_ID__c  = intentRec.id;
            caseObj.Interacting_With_Type__c = 'Group';
            insert caseObj ;
            System.assertEquals(caseObj.Interacting_About_Type__c == 'Member', true); 
        	
        }
    }
     
    
    /**
    * Test method to test updateCaseStatus closed Case
    *<p>
    * @param    NULL
    * @return   void   
    */
    static testMethod void testupdateCaseStatus_Closed()
    {    
        User oUser  = [SELECT Id FROM User  WHERE Lastname='test User1' LIMIT 1];
        system.assertNotEquals(oUser,null,'​Value not matched'); 
        
        
        System.runAs (oUser) 
        { 
           Test.StartTest();
            Case caseObj = [Select Id , CaseNumber, Status,Interacting_About_Type__c from Case where Interacting_About_Type__c = 'Member' limit 1];
            system.assert(caseObj.Interacting_About_Type__c =='Member');
            caseNumber  = caseObj.CaseNumber ;
            caseStatus = 'C';
            createRequest(oUser);
            HUMCaseUpdateService.updateCaseStatus(request);
            System.assertEquals([Select Id , CaseNumber, Status from Case where Id = :caseObj.Id].Status == 'Closed', true);
            System.assertEquals([Select Id from Attachment__c where Related_To_Case__c =: caseObj.Id].size() > 0 , true);
                
            Test.StopTest();
        }
    }
    
    /**
    * Test method to test updateCaseStatus open Case
    *<p>
    * @param    NULL
    * @return   void   
    */
    static testMethod void testupdateCaseStatus_Open()
    {   
        User oUser = [SELECT Id FROM User  WHERE Lastname='test User1' LIMIT 1];
        system.assertNotEquals(oUser,null,'​Value not matched'); 
        
        System.runAs (oUser) 
        { 
           Test.StartTest();
            Case caseObj = [Select Id , CaseNumber, Status,Interacting_About_Type__c from Case where Interacting_About_Type__c = 'Member' limit 1];
            system.assert(caseObj.Interacting_About_Type__c =='Member');
            caseNumber  = caseObj.CaseNumber ;
            caseStatus = 'O';
            createRequest(oUser);
            HUMCaseUpdateService.updateCaseStatus(request);
            System.assertEquals([Select Id , CaseNumber, Status from Case where Id = :caseObj.Id].Status == 'In Progress', true);
            System.assertEquals([Select Id from Attachment__c where Related_To_Case__c =: caseObj.Id].size() > 0 , true);
            Test.StopTest();
        }
    }
    
  
    /**
    * Test method to test updateCaseStatus negative scenarios
    *<p>
    * @param    NULL
    * @return   void   
    */
    static testMethod void testupdateCaseStatus_Negative()
    {      
        User oUser = [SELECT Id FROM User  WHERE Lastname='test User1' LIMIT 1];
        system.assertNotEquals(oUser,null,'​Value not matched'); 
        
        System.runAs (oUser) 
        { 
           Test.StartTest();
           Case  caseObj = [Select Id , CaseNumber, Status,Interacting_About_Type__c from Case where Interacting_About_Type__c = 'Member' limit 1];
            system.assert(caseObj.Interacting_About_Type__c =='Member');
            caseObj.Status = 'In Progress';
            Update caseObj ;
            system.assert(caseObj.status == 'In Progress');
            createRequest(oUser);
            HUMCaseUpdateService.updateCaseStatus(request);
            System.assertEquals([Select Id , CaseNumber, Status from Case where Id = :caseObj.Id].Status == 'In Progress', true);
            Test.StopTest();
        }
    }
    
     /**
    * Method to create request
    *
    * @param     objUser         User       
    * @return    Void											
    */ 
     private Static void createRequest(User objUser)
     {
         request = new HUMCaseUpdateRequestHelper(); 
         
         List<HUMCaseUpdateAttachmentDTO> AttachmentDTOList = new List<HUMCaseUpdateAttachmentDTO>();
         HUMCaseUpdateAttachmentDTO AttachmentDTO1 = new HUMCaseUpdateAttachmentDTO();
         AttachmentDTO1.sFileName = 'Att1' ;
         AttachmentDTO1.sAttachmentUrl = 'url1';
         AttachmentDTOList.add(AttachmentDTO1);
         HUMCaseUpdateAttachmentDTO AttachmentDTO2 = new HUMCaseUpdateAttachmentDTO(); 
         AttachmentDTO2.sFileName = 'Att2' ;
         AttachmentDTO2.sAttachmentUrl = 'url11';         
         AttachmentDTOList.add(AttachmentDTO2);        
         request.sCaseNumber = caseNumber ;
         request.sCaseStatus = caseStatus;
         request.sComment = 'testComment';
         request.sQueueName = 'testQueue';
         request.sOwnerId = objUser.Id; 
         request.lstAttachmentDTO = AttachmentDTOList ; 
         
     }
}
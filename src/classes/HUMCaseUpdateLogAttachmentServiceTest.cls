/****************************************************************************************
Apex Class Name  : HUMCaseUpdateLogAttachmentServiceTest
Version          : 1.0 
Created Date     : December 05 2014
Function         : Test class to HUMCaseUpdateLogAttachmentService Apex class
Modification Log :
 *  Modification ID          Developer       Code Review            Date                 Description
 * ----------------------------------------------------------------------------------------           
 *          1.0         Krishna Tadikonda          20294           12/05/2014              Original Version 
 *          2.0         SuryaKumari Medicherla     20579           28/01/2015              Code modified to fix test class failue 
 *          3.0         Ranjeeth Nagishetty        21167           26/02/2015              updated test class as per REQ-81852
 *          4.0         Vishal Verma                               03/17/2014              Updated test data for HUMConstants custom setting
 *          5.0         Avinash Choubey            33344           07/15/2016              Changes made as part of REQ- 255151
 *          6.0         Pradeepkumar Dani          99570           06/08/2017              Intent__c.Classification__c & Intent__c.Etl_Idtfr__c Clean up
 *          6.1         Pradeepkumar Dani         100996           06/13/2017              Classification__c.Classification_Type__c & Etl_Idftr__c Clean Up
 *          6.2         Santhi Mandava                             03/12/2019              Removed "DpaaSWebServiceMockImpl" usage.
 *******************************************************************************************/
@isTest
private class HUMCaseUpdateLogAttachmentServiceTest 
{
   
    /**
    * This method is used to insert the data only once and called automicticaly in all methods 
    *
    * @param  None      
    * @return  Void
    */
    @testSetup static void setup()
     {
    
        User oUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oUser;
        system.assertEquals(oUser.lastname=='test User1',true);
        System.runAs(oUser) 
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
            system.assertEquals(caseObj.Interacting_With_Name__c == 'Test Name',true);
        }      
    }
   
    /**
    * This method is used to test updateCaseStatus no input Case 
    *
    * @param  None      
    * @return  Void
    */
    static testMethod void testupdateCaseStatus_NoInput()
    { 
        HUMTestDataHelperTest.getHUMConstantsData(); 
        User oUser = [SELECT Id FROM User  WHERE Lastname='test User1' LIMIT 1];
        system.assertNotEquals(oUser, null, '​Value not matched'); 
        
        System.runAs(oUser) 
        {
            Test.StartTest();
            HUMCaseUpdateLogAttachmentHelper request = new HUMCaseUpdateLogAttachmentHelper();
            HUMStatusMessageHelper response = HUMCaseUpdateLogAttachmentService.updateCaseAndAttachment(request);
            system.assert(response.bStatus == false);
            Test.StopTest();
        }
    }
    
    /**
    * This method is used to test updateCaseStatus no records Case 
    *
    * @param  None      
    * @return  Void
    */
    static testMethod void testupdateCaseStatus_NoRecords()
    { 
        HUMTestDataHelperTest.getHUMConstantsData(); 
        User oUser = [SELECT Id FROM User  WHERE Lastname='test User1' LIMIT 1];
        system.assertNotEquals(oUser,null,'​Value not matched'); 
        
        System.runAs(oUser) 
        {
            Test.StartTest();
            HUMCaseUpdateLogAttachmentHelper request = new HUMCaseUpdateLogAttachmentHelper();
            request.sCaseNumber = '1234';
            request.sCaseStatus = 'O';
            HUMStatusMessageHelper response =  HUMCaseUpdateLogAttachmentService.updateCaseAndAttachment(request);
            system.assert(response.bStatus ==  false);
            Test.StopTest();
        }
    }
    
    /**
    * This method is used to test updateCaseStatus closed case 
    *
    * @param  None      
    * @return  Void
    */
    static testMethod void testupdateCaseStatus_Closed()
    {
        HUMTestDataHelperTest.getHUMConstantsData();  
        User oUser = [SELECT Id FROM User  WHERE Lastname='test User1' LIMIT 1];
        system.assertNotEquals(oUser,null,'​Value not matched');
        System.runAs(oUser) 
        {
            Test.StartTest();
            Case caseObj = [Select Id , CaseNumber, Status,Interacting_With_Name__c from Case where Interacting_With_Name__c = 'Test Name' limit 1];
            system.assertEquals(caseObj.Interacting_With_Name__c == 'Test Name',true);
            String caseNumber  = caseObj.CaseNumber ;
            String caseStatus = 'C';
            
            HUMCaseUpdateLogAttachmentHelper request = new HUMCaseUpdateLogAttachmentHelper();
            request.sCaseNumber = caseNumber;
            request.sCaseStatus = caseStatus;
            request.sComment = ' New Comment';
            request.sOwnerId = oUser.Id;
            
            List<HUMCaseUpdateAttachmentDTO> listAttachmentDTO = new List<HUMCaseUpdateAttachmentDTO>();
            
            HUMCaseUpdateAttachmentDTO attachmentDTO1 = new HUMCaseUpdateAttachmentDTO();
            attachmentDTO1.sFileName = 'New Attachment';
            attachmentDTO1.sAttachmentUrl = 'New Attachment Url';
            listAttachmentDTO.add(attachmentDTO1);
            
            HUMCaseUpdateAttachmentDTO attachmentDTO2 = new HUMCaseUpdateAttachmentDTO();
            listAttachmentDTO.add(attachmentDTO2);
            
            request.lstAttachmentDTO = listAttachmentDTO; 
            
            HUMLogAttachmentRequestDTO  logAttachemntRequest = new HUMLogAttachmentRequestDTO();
            
            List<HumAttachmentAttributeRequestDTO> listAttachemntAttribute = new List<HumAttachmentAttributeRequestDTO>();
            
            HumAttachmentAttributeRequestDTO attachemntAttribute1 = new HumAttachmentAttributeRequestDTO();
            attachemntAttribute1.AttachmentName = 'Test';
            attachemntAttribute1.AttachmentValue = 'Test Value';
            listAttachemntAttribute.add(attachemntAttribute1);
            
            HumAttachmentAttributeRequestDTO attachemntAttribute2 = new HumAttachmentAttributeRequestDTO();
            listAttachemntAttribute.add(attachemntAttribute2);
            
            logAttachemntRequest.AttachmentAttribute = listAttachemntAttribute;
            logAttachemntRequest.CreatedBy = oUser.Id;
            logAttachemntRequest.AttachmentDescription = 'Description';
            logAttachemntRequest.AttachmentKey = 'Key';
            logAttachemntRequest.AttachmentType = 'Type';
            
            request.attachmentDTO = logAttachemntRequest;
            HUMStatusMessageHelper response =  HUMCaseUpdateLogAttachmentService.updateCaseAndAttachment(request);
            system.assert(response.bStatus == true);
            Test.StopTest();
        }
    } 
      
    /**
    * This method is used to test updateCaseStatus in progress Case 
    *
    * @param  None      
    * @return  Void
    */
    static testMethod void testupdateCaseStatus_InProgress()
    {
        HUMTestDataHelperTest.getHUMConstantsData();  
        User oUser = [SELECT Id FROM User  WHERE Lastname='test User1' LIMIT 1];
        system.assertNotEquals(oUser,null,'​Value not matched');
        System.runAs (oUser) 
        {
            Test.StartTest();
            Case caseObj = [Select Id , CaseNumber, Status,Interacting_With_Name__c from Case where Interacting_With_Name__c = 'Test Name' limit 1];
            system.assertEquals(caseObj.Interacting_With_Name__c == 'Test Name',true);
            String caseNumber  = caseObj.CaseNumber ;
            String caseStatus = 'O';
            
            HUMCaseUpdateLogAttachmentHelper request = new HUMCaseUpdateLogAttachmentHelper();
            request.sCaseNumber = caseNumber;
            request.sCaseStatus = caseStatus;
            request.sComment = ' New Comment';
            request.sOwnerId = oUser.Id;
                        
            List<HUMCaseUpdateAttachmentDTO> listAttachmentDTO = new List<HUMCaseUpdateAttachmentDTO>();
            
            HUMCaseUpdateAttachmentDTO attachmentDTO1 = new HUMCaseUpdateAttachmentDTO();
            attachmentDTO1.sFileName = 'New Attachment';
            attachmentDTO1.sAttachmentUrl = 'New Attachment Url';
            listAttachmentDTO.add(attachmentDTO1);
            
            HUMCaseUpdateAttachmentDTO attachmentDTO2 = new HUMCaseUpdateAttachmentDTO();
            listAttachmentDTO.add(attachmentDTO2);
            
            request.lstAttachmentDTO = listAttachmentDTO; 
            
            HUMLogAttachmentRequestDTO  logAttachemntRequest = new HUMLogAttachmentRequestDTO();
            
            List<HumAttachmentAttributeRequestDTO> listAttachemntAttribute = new List<HumAttachmentAttributeRequestDTO>();
            
            HumAttachmentAttributeRequestDTO attachemntAttribute1 = new HumAttachmentAttributeRequestDTO();
            attachemntAttribute1.AttachmentName = 'Test111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111';
            attachemntAttribute1.AttachmentValue = 'Test Value';
            listAttachemntAttribute.add(attachemntAttribute1);
            
            HumAttachmentAttributeRequestDTO attachemntAttribute2 = new HumAttachmentAttributeRequestDTO();
            attachemntAttribute2.AttachmentName = 'Test111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111';
            attachemntAttribute2.AttachmentValue = 'Test Value';
            listAttachemntAttribute.add(attachemntAttribute2);
            
            logAttachemntRequest.AttachmentAttribute = listAttachemntAttribute;
            logAttachemntRequest.CreatedBy = oUser.Id;
            logAttachemntRequest.AttachmentDescription = 'Description';
            logAttachemntRequest.AttachmentKey = 'Key1111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111';
            logAttachemntRequest.AttachmentType = 'Type';
            
            request.attachmentDTO = logAttachemntRequest;
            
            HUMStatusMessageHelper response =  HUMCaseUpdateLogAttachmentService.updateCaseAndAttachment(request);
            system.assert(response.bStatus == false);
            Test.StopTest();
        }
    }
    
    /**
    * This method is used to test updateCaseStatus update fail Case 
    *
    * @param  None      
    * @return  Void
    */
    static testMethod void testupdateCaseStatus_UpdateFail()
    { 
        HUMTestDataHelperTest.getHUMConstantsData(); 
        User oUser = [SELECT Id FROM User  WHERE Lastname='test User1' LIMIT 1];
        system.assertNotEquals(oUser, null, '​Value not matched');
        System.runAs(oUser) 
        {
            Test.StartTest();
            Case caseObj = [Select Id , CaseNumber, Status,Interacting_With_Name__c from Case where Interacting_With_Name__c = 'Test Name' limit 1];
            system.assertEquals(caseObj.Interacting_With_Name__c == 'Test Name',true);
            String caseNumber = caseObj.CaseNumber ;
            String caseStatus = 'O';
            
            HUMCaseUpdateLogAttachmentHelper request = new HUMCaseUpdateLogAttachmentHelper();
            request.sCaseNumber = caseNumber;
            request.sCaseStatus = caseStatus;
            
            List<HUMCaseUpdateAttachmentDTO> listAttachmentDTO = new List<HUMCaseUpdateAttachmentDTO>();
            
            HUMCaseUpdateAttachmentDTO attachmentDTO1 = new HUMCaseUpdateAttachmentDTO();
            attachmentDTO1.sFileName = 'New Attachment1111111111111111111111111111111111111111111111111111111111111111111111111111111111111';
            attachmentDTO1.sAttachmentUrl = 'New Attachment Url';
            listAttachmentDTO.add(attachmentDTO1);
            
            HUMCaseUpdateAttachmentDTO attachmentDTO2 = new HUMCaseUpdateAttachmentDTO();
            attachmentDTO2.sFileName = 'New Attachment1111111111111111111111111111111111111111111111111111111111111111111111111111111111111';
            attachmentDTO2.sAttachmentUrl = 'New Attachment Url';
            listAttachmentDTO.add(attachmentDTO2);
            
            request.lstAttachmentDTO = listAttachmentDTO; 
            HUMStatusMessageHelper response =  HUMCaseUpdateLogAttachmentService.updateCaseAndAttachment(request);
            system.assert(response.bStatus == false);
            Test.StopTest();
        }
    }   
}
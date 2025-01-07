/****************************************************************************************
Apex Class Name  : HUMGetCaseInfoServiceTest
Version          : 1.0 
Created Date     : August 06 2014
Function         : Test class to HUMGetCaseInfoService Apex class
Modification Log :
 *  Developer               Code Review            Date                 Description
 * ----------------------------------------------------------------------------------------           
 * Sagar Tapadia              17532               08/06/2014              Original Version 
 * Shruthi Karanth            17964               09/17/2014              Changes as per update to class 
 * Shruthi Karanth            18141               09/24/2014              Fix was made to include Cases which do not have Closed Date populated      
 * Vishal Verma               18281               09/30/2014              Fix the test class error and increased coverage
 * Shruthi Karanth            18298               10/02/2014              Fix the test class error
 * Chaitanya Kumar            20579               01/16/2015              Fixed Test class error in getCaseInfo_WithInquiringAbout();
 * SuryaKumari Medicherla     20579               01/28/2015              Code modified to fix test class failure
 * Vishal Verma               20604               01/29/2015              Changed the date range at line 188 to fix test class error;
 * ChiranjeeviRao Ravuri      22152               04/07/2015              Fixed Test class failure in getCaseInfo_InteractionID();
 * Pradeepkumar Dani          82363               04/06/2017              CTCI Data model update REQ - 306330
 * Santhi Mandava                                 03/12/2019              Removed "DpaaSWebServiceMockImpl" usage.
 *******************************************************************************************/
@isTest
private class HUMGetCaseInfoServiceTest 
{
    /*
     * Method Name : getCaseInfo_CaseID
     * Description : Test method to find case based on Case Number
     * Return type : NA
     * Paramater   : NA
     */
    static testMethod void getCaseInfo_CaseID() 
    {
        //loading HUMConstants Custom Settings data
        HUMTestDataHelperTest.getHUMConstantsData();
        insert new HUMCipherCloudSettings__c( Name = 'APPLICATION_NAME' , Value__c = 'TEST');
        //Create User
        User u = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert u;

        Account acc = HUMTestDataHelperTest.getAccount();
        acc.Name = 'Test Account Cases';
        acc.RecordtypeID=Schema.SObjectType.Account.getRecordTypeInfosByName().get('Agent/Broker').getRecordTypeId();

        insert acc;
        
        List<Classification_Type__c> ctList= new list<Classification_Type__c>();
        Classification_Type__c ctRec = new Classification_Type__c();
        ctRec.name = 'Claims (RSO)';
        ctRec.Is_Active__c=true;
        ctList.add(ctRec);
        
        insert ctList;
        system.assertEquals(ctList.size(),1);
        
        Work_Queue_Setup__c wqsRecord = new Work_Queue_Setup__c();
        wqsRecord.Name = 'Cincinnati CRU';
        wqsRecord.Classification_Type__c = ctRec.Id;
        insert wqsRecord;
        system.assertEquals(wqsRecord.Name, 'Cincinnati CRU');
        
        List<Classification__c> lstClassification = new List<Classification__c>();
        Classification__c classificationRec = new Classification__c();
        classificationRec.Name = 'Claims';
        classificationRec.Is_Active__c = true;
        lstClassification.add(classificationRec);    
        insert lstClassification;
        system.assertEquals(lstClassification[0].Name=='Claims',true);
            
        List<Intent__c> lstIntent = new List<Intent__c>();
        Intent__c intentRec = new Intent__c();
        intentRec.Name = 'Filling';
        intentRec.Is_Active__c = true;
        lstIntent.add(intentRec);                
        insert lstIntent;
        system.assertEquals(lstIntent[0].Name=='Filling',true);
        
        List<CTCI_Junction__c> ctciList = new list<CTCI_Junction__c>();
        CTCI_Junction__c ctciRec = new CTCI_Junction__c();
        ctciRec.Classification_Type__c = ctRec.id;
        ctciRec.Classification__c = classificationRec.Id;
        ctciRec.Intent__c = intentRec.id;
        ctciRec.IsActive__c = true;
        ctciList.add(ctciRec);
        insert ctciList;
        system.assertEquals(ctciList.size(),1);
            
        Case c = HUMTestDataHelperTest.getCase();
        c.OwnerId = u.Id;
        c.Subject = 'Test Subject';
        c.Interacting_With_Name__c =  'Test';
        c.CTCI_List__c = ctciRec.Id;
        insert c;
        c = [Select CaseNumber from Case Where Id=:c.Id];
        system.runAs(u)
        {
            Test.startTest();
            HUMGetCaseInfoRequestHelper gtCaseReqInfo = new HUMGetCaseInfoRequestHelper();
            gtCaseReqInfo.CaseId = [Select CaseNumber from Case Where Id=:c.Id].CaseNumber;
            system.assert(gtCaseReqInfo!=null);
            List<HUMGetCaseInfoResponseHelper> lstCaseInfo=HUMGetCaseInfoService.getCaseRelatedInfo(gtCaseReqInfo);
            Test.stopTest();
            system.assertEquals(lstCaseInfo[0].CaseNumber,c.CaseNumber);
        }
    } 

    /*
     * Method Name : getCaseInfo_InteractionID
     * Description : Test method to find case based on Interaction ID
     * Return type : NA
     * Paramater   : NA
     */
    static testMethod void getCaseInfo_InteractionID()
    {
        //loading HUMConstants Custom Settings data
        HUMTestDataHelperTest.getHUMConstantsData();
        insert new HUMCipherCloudSettings__c( Name = 'APPLICATION_NAME' , Value__c = 'TEST');
        //Create User
        User u = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert u;

        Account oAcc = HUMTestDataHelperTest.getAccount();
        oAcc.Name = 'TestAcc';
        oAcc.OwnerId = u.Id;
        oAcc.RecordtypeID=Schema.SObjectType.Account.getRecordTypeInfosByName().get('Agent/Broker').getRecordTypeId();

        insert oAcc;

        Interaction__C oInt = new Interaction__C(Caller_name__C = 'Test Caller',
                Interacting_with_type__C = 'Agent',
                Interaction_Origin__c='Inbound Call',
                Interacting_with__c = oAcc.Id,OwnerId=u.Id);       
        insert oInt ;

        Case c = HUMTestDataHelperTest.getCase();
        c.OwnerId = u.Id;
        c.Subject='Test Subject';
        c.AccountId = oAcc.Id;
        c.Interaction_ID__c = oInt.Id;
        c.Interacting_With__c = oInt.Interacting_With__c;
        insert c;
        c = [Select CaseNumber from Case Where Id=:c.Id];
        system.runAs(u)
        {
            Test.startTest();
            HUMGetCaseInfoRequestHelper gtCaseReqInfo = new HUMGetCaseInfoRequestHelper();
            gtCaseReqInfo.CaseId = [Select CaseNumber from Case Where Id=:c.Id].CaseNumber;
            gtCaseReqInfo.InteractionId = [Select Name from Interaction__c where Id=:oInt.Id].Name;
            system.assert(gtCaseReqInfo!=null);
            List<HUMGetCaseInfoResponseHelper> lstCaseInfo=HUMGetCaseInfoService.getCaseRelatedInfo(gtCaseReqInfo);
            Test.stopTest();
            system.assertEquals(lstCaseInfo[0].CaseNumber,c.CaseNumber);
        }
    } 

    /*
     * Method Name : getCaseInfo_WithInquiringAbout
     * Description : Test method to find case based on Inquring for and Inquiring About
     * Return type : NA
     * Paramater   : NA
     */
    static testMethod void getCaseInfo_WithInquiringAbout()
    {
        //loading HUMConstants Custom Settings data
        HUMTestDataHelperTest.getHUMConstantsData();
        insert new HUMCipherCloudSettings__c( Name = 'APPLICATION_NAME' , Value__c = 'TEST');
        //Create User
        User oUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        oUser.Current_Queue__c='Test';
        insert oUser;

        Account oAcc = HUMTestDataHelperTest.getAccount();
        oAcc.Name = 'TestAcc';
        oAcc.OwnerId = oUser.Id;
        oAcc.Account_External_ID__c='TEST1234';
        oAcc.Enterprise_ID__c  = '1234';
        oAcc.RecordtypeID=Schema.SObjectType.Account.getRecordTypeInfosByName().get('Agent/Broker').getRecordTypeId();
        insert oAcc;

        Interaction__C oInt = new Interaction__C(Caller_name__C = 'Test Caller',
                Interacting_with_type__C = 'Agent',
                Interaction_Origin__c='Inbound Call',
                Interacting_with__c = oAcc.Id,OwnerId=oUser.Id);       
        insert oInt ;

        Case c = HUMTestDataHelperTest.getCase();
        c.OwnerId = oUser.Id;
        c.Subject='Test Subject';
        c.AccountId = oAcc.Id;
        c.Interaction_ID__c = oInt.Id;
        c.Interacting_With__c = oAcc.Id;
        c.Status = 'New';
        insert c;

        c = [Select CaseNumber , CreatedDate from Case Where Id=:c.Id];          
        CaseComment cCom = HUMTestDataHelperTest.getCaseComm();
        cCom.ParentId = c.Id;
        insert cCom;

        Task t = HUMTestDataHelperTest.getTaskRecord();
        t.WhatId = c.Id;
        insert t;

        Attachment__c attach = HUMTestDataHelperTest.getCustomAttachment();
        attach.Related_To_Case__c = c.Id;
        insert attach;

        Attachment_Log__c attLog = HUMTestDataHelperTest.getAttachLog();
        attLog.Case__c = c.Id;
        insert attLog;

        Attachment_Log_Item__c attLogItem = HUMTestDataHelperTest.getAttLogItem();
        attLogItem.Attachment_Log__c = attLog.Id;
        insert attLogItem;

        System.runAs(oUser)
        {
            HUMGetCaseInfoRequestHelper gtCaseReqInfo = new HUMGetCaseInfoRequestHelper();
            HUMEntityIdentifierHelper gtEntity = new HUMEntityIdentifierHelper();
            HUMEntityIdentifierHelper gtEntityFor = new HUMEntityIdentifierHelper();
            gtCaseReqInfo.InquireAboutCriteria = new List<HUMEntityIdentifierHelper>();
            gtCaseReqInfo.InquireForCriteria = new List<HUMEntityIdentifierHelper>();
            gtEntity.EntityType='Agent_Broker';
            gtEntity.IdValue='1234';
            gtEntityFor.EntityType='Agent_Broker';
            gtEntityFor.IdValue='1234';
            gtCaseReqInfo.InquireAboutCriteria.add(gtEntity);
            gtCaseReqInfo.InquireForCriteria.add(gtEntityFor);
            gtCaseReqInfo.StartDate = String.valueOf(c.createdDate.month()+'/'+c.createdDate.day()+'/'+c.createdDate.year());
            //Construct End Date to add an year. Earlier version included parsing Date + 1 year which resulted in leap year date error
            Datetime endDate =(system.Today().addyears(1));
            
            
            gtCaseReqInfo.EndDate = String.valueOf(endDate .month() +'/'+endDate.day()+'/'+String.valueof(endDate.year()));


            gtCaseReqInfo.AttachmentFlag = true;
            gtCaseReqInfo.AttachmentLogFlag = true;
            gtCaseReqInfo.InteractionFlag = true;
            gtCaseReqInfo.TaskFlag = true;
            gtCaseReqInfo.CaseCommentFlag = true;

            system.assert(gtCaseReqInfo!=null);
            Test.startTest();
            List<HUMGetCaseInfoResponseHelper> lstCaseInfo=HUMGetCaseInfoService.getCaseRelatedInfo(gtCaseReqInfo);
            Test.stopTest();
            system.assertEquals(lstCaseInfo[0].CaseNumber,c.CaseNumber);
            
        }
    }

    /*
     * Method Name : getInquiredAboutCases
     * Description : Test method to get cases of Inquired About
     * Return type : NA
     * Paramater   : NA
     */
    static testMethod void getInquiredAboutCases()
    {
        //loading HUMConstants Custom Settings data
        HUMTestDataHelperTest.getHUMConstantsData();
        insert new HUMCipherCloudSettings__c( Name = 'APPLICATION_NAME' , Value__c = 'TEST');
        //Create User
        User u = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert u;


        Account oAcc = HUMTestDataHelperTest.getAccount();
        oAcc.Name = 'TestAcc';
        oAcc.OwnerId = u.Id;
        oAcc.Account_External_ID__c='Test1234';
        oAcc.Enterprise_ID__c ='1234';
        oAcc.RecordtypeID=Schema.SObjectType.Account.getRecordTypeInfosByName().get('Agent/Broker').getRecordTypeId();
        insert oAcc;

        Interaction__C oInt = new Interaction__C(Caller_name__C = 'Test Caller',
                Interacting_with_type__C = 'Agent',
                Interaction_Origin__c='Inbound Call',
                Interacting_with__c = oAcc.Id,OwnerId=u.Id);       
        insert oInt ;

        Case c = HUMTestDataHelperTest.getCase();
        c.OwnerId = u.Id;
        c.Subject='Test Subject';
        c.AccountId = oAcc.Id;
        c.Interaction_ID__c = oInt.Id;
        c.Interacting_With__c = oAcc.Id;
        insert c;
        c = [Select CaseNumber from Case Where Id=:c.Id];
        system.runAs(u)
        {
            Test.startTest();
            HUMGetCaseInfoRequestHelper gtCaseReqInfo = new HUMGetCaseInfoRequestHelper();
            HUMEntityIdentifierHelper gtEntity = new HUMEntityIdentifierHelper();
            gtCaseReqInfo.InquireAboutCriteria = new List<HUMEntityIdentifierHelper>();
            gtEntity.EntityType='Agent_Broker';
            gtEntity.IdValue='1234';
            gtCaseReqInfo.InquireAboutCriteria.add(gtEntity);
            system.assert(gtCaseReqInfo!=null);
            Boolean bCheck = HUMGetCaseInfoService.checkInquiredAboutCases(gtCaseReqInfo.InquireAboutCriteria);
            Test.stopTest();
            system.assertEquals(bCheck,true);
        }
    } 

    /**** Negative Test Cases ***/
    /*
     * Method Name : blankCaseNumber
     * Description : Negative test scenario when case number is blank
     * Return type : NA
     * Paramater   : NA
     */
    static testMethod void blankCaseNumber()
    {
        //loading HUMConstants Custom Settings data
        HUMTestDataHelperTest.getHUMConstantsData();
        //Create User
        User u = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert u;

        Account acc = HUMTestDataHelperTest.getAccount();
        acc.Name = 'Test Account Cases';
        insert acc;

        Case c = HUMTestDataHelperTest.getCase();
        c.OwnerId = u.Id;
        c.Subject='Test Subject';
        c.Interacting_With_Name__c = 'Test';
        insert c;

        system.runAs(u)
        {
            Test.startTest();
            HUMGetCaseInfoRequestHelper gtCaseReqInfo = new HUMGetCaseInfoRequestHelper();
            gtCaseReqInfo.CaseId = '';
            system.assert(gtCaseReqInfo!=null);
            List<HUMGetCaseInfoResponseHelper> lstCaseInfo=HUMGetCaseInfoService.getCaseRelatedInfo(gtCaseReqInfo);
            Test.stopTest();
            system.assertEquals(lstCaseInfo[0].Success,false);
        }
    } 

    /*
     * Method Name : raiseExceptionInCaseInfo
     * Description : raise exception when invalid interaction id
     * Return type : NA
     * Paramater   : NA
     */
    static testMethod void raiseExceptionInCaseInfo()
    {
        HUMTestDataHelperTest.getHUMConstantsData();
        //Create User
        User u = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert u;

        system.runAs(u)
        {
            Test.startTest();
            HUMGetCaseInfoRequestHelper gtCaseReqInfo = new HUMGetCaseInfoRequestHelper();
            gtCaseReqInfo.InteractionId='%%%%342^^^';
            system.assert(gtCaseReqInfo!=null);
            try
            {
                HUMGetCaseInfoService.getCaseRelatedInfo(gtCaseReqInfo);
            }
            catch(Exception e)
            {
                system.assertEquals(e.getTypeName(),'QueryException');
            }
            Test.stopTest();
        }
    } 

    /*
     * Method Name : raiseExceptionByStartEndDate
     * Description : raise exception when invalid Start and End Date
     * Return type : NA
     * Paramater   : NA
     */
    static testMethod void raiseExceptionByStartEndDate()
    {
        HUMTestDataHelperTest.getHUMConstantsData();
        //Create User
        User u = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert u;

        system.runAs(u)
        {
            Test.startTest();
            HUMGetCaseInfoRequestHelper gtCaseReqInfo = new HUMGetCaseInfoRequestHelper();
            HUMEntityIdentifierHelper gtEntity = new HUMEntityIdentifierHelper();
            gtCaseReqInfo.InquireAboutCriteria = new List<HUMEntityIdentifierHelper>();
            gtEntity.EntityType='TEST';
            gtEntity.IdValue='1234';
            gtCaseReqInfo.InquireAboutCriteria.add(gtEntity);
            gtCaseReqInfo.StartDate='%%%%342^^^';
            gtCaseReqInfo.EndDate='DGHPJPP';
            system.assert(gtCaseReqInfo!=null);
            try
            {
                HUMGetCaseInfoService.getCaseRelatedInfo(gtCaseReqInfo);
            }
            catch(Exception e)
            {
                system.assert(e.getTypeName().contains('Invalid Date'));
            }
            Test.stopTest();
        }
    } 
}
/*********************************************************************************************************
Apex Class Name :  AttachLogDetailsToCase_T_HUM 
Version: 1.0
Created On      : 03/16/2016
Function        : Test class for AttachLogDetailsToCase_C_HUM 

* Modification Log
* Developer Name            Code Review                 Date                       Description
*-----------------------------------------------------------------------------------------------------------
* Santhi Mandava            24069                       03/16/2016                  Original version
* Avinash Choubey           33332                       07/20/2016                  Chnages made as part of REQ- 270351
* Santhi Mandava            40488                       08/24/2016                  Worked on REQ#275929 for DPaaS error handling.
* Shivam Anand              140629                      10/18/2017                  Changed code for improving code coverage.
* Asish Behera                                          03/20/2018                  REQ - 357690- Change code to cover associateInteractiontToCase method.
* Prasuna Pattabhi                                      11/11/2021                  Code Coverage Is 57 improved the code coverage percentage
        ************************************************************************************************************/

@isTest
Private Class AttachLogDetailsToCase_T_HUM
{
    /*
    * Method Name : setup
    * Description : This method Is used To create test data For the Class 
    * Return type : void
    * Parameter   : NA
    */
    @testSetup static void setup() 
    {
        User oUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        oUser.Current_Queue__c = 'Cincinnati CRU1';
        oUser.Network_User_Id__c = 'SXS2222';
        insert oUser;
        System.assertEquals(oUser.LastName, 'test User1');
        system.runAs(oUser)
        {
            LoggingTestDataSetup_T_HUM.createTestData();
            HUMConstants__c constData = New HUMConstants__c(Name ='Size_TwoHundred',IntegerValue__c=200,Stringvalue__c='');
            insert constData;
            System.assertEquals(constData.IntegerValue__c,200);
        }
    }
    
    /*
    * Method Name : testAttachmentLogFunctionality
    * Description : This method Is used To test Attachment Log functionality.
    * Return type : void
    * Parameter   : NA
    */
    Static testMethod void testAttachmentLogFunctionality() 
    {
        User oUser = [Select Id, Network_User_Id__c from User where LastName = 'test User1'];
        System.assertEquals('SXS2222', oUser.Network_User_Id__c);
        
        System.runAs(oUser)
        {
            test.startTest();
            Account objAcc = [Select Id, Name from Account limit 1];
            System.assertEquals('test',objAcc.Name);
            
            AttachLogDetailsToCase_C_HUM objClass = New AttachLogDetailsToCase_C_HUM();
            
            objClass.sObjectId = objAcc.Id;            
            objClass.selectedCaseOption  = 'Existing Case';
            objClass.accId = objAcc.Id;
            objClass.sObjName = ' ';
            objClass.displayCaseList();
            objClass.getKeyValue();
            System.assertEquals('123456',objClass.enterpriseId );
            
            ApexPages.currentPage().getParameters().put('errorMessage', 'Sample Error');
            ApexPages.currentPage().getParameters().put('dpaascallouttype', 'LoggingSession'); 
            
            objClass.createErrorLog();
            System.assertNotEquals('',objClass.dpaasErrorLogName );
            
            ApexPages.currentPage().getParameters().put('dpaascallouttype', 'LoggingInfo'); 
            objClass.createErrorLog();
            System.assertNotEquals('',objClass.dpaasErrorLogName );
            
            objClass.sCaseNumber = '';
            objClass.sObjName = '';
            objClass.getCaseId();
            
            String screenshotData = 'Sample Screenshot data Sample Screenshot data Sample Screenshot data';
            Blob objblob = Blob.valueOf(screenshotData);
            String paramvalue = EncodingUtil.base64Encode(objblob);
            ApexPages.currentPage().getParameters().put('screenshotData', paramvalue );
            ApexPages.currentPage().getParameters().put('type', 'Auth/Referral Summary'); 
            
            objClass.sObjectId = objAcc.Id;
            objClass.createCaseAndRedirect();
            System.assertNotEquals('',objClass.sSelectedCaseId);
            
            objClass.createAttachmentLogRecords();
            objClass.checkUserQueue();
            objClass.associateInteractiontToCase();
            test.stopTest();
        }
    }
    
    /*
    * Method Name : testFetchRecords
    * Description : This method Is used To test Records fetch functionality
    * Return type : void
    * Parameter   : NA
    */
    Static testMethod void testFetchRecords() 
    {
        User oUser = [Select Id, Network_User_Id__c from User where LastName = 'test User1'];
        System.assertEquals('SXS2222', oUser.Network_User_Id__c);
        
        System.runAs(oUser)
        {
            test.startTest();
            
            AttachLogDetailsToCase_C_HUM objClass = New AttachLogDetailsToCase_C_HUM();
            
            ApexPages.currentPage().getParameters().put('searchStartDate', '2012-10-12');
            ApexPages.currentPage().getParameters().put('searchEndDate', '2016-10-12');
            ApexPages.currentPage().getParameters().put('searchCaseNumber', '');
            ApexPages.currentPage().getParameters().put('searchCaseNumber', '1236');
            
            objClass.returnListOfCase();
            System.assertEquals(objClass.lstFinalCases.size(),0);
            
            String jsonData = objClass.sCaseDetailsJSON;
            System.assertEquals(jsonData,'{"data":[]}');
            test.stopTest();
        }
    }

   /*
    * Method Name : testAttachmentPharmacyLogFunctionality
    * Description : This method Is used To test Attachment Log functionality.
    * Return type : void
    * Parameter   : NA
    */
    Static testMethod void testAttachmentPharmacyLogFunctionality() 
    {
        User oUser = [Select Id, Network_User_Id__c from User where LastName = 'test User1'];
        System.assertEquals('SXS2222', oUser.Network_User_Id__c);
        
        System.runAs(oUser)
        {
            Account objAcc = [Select Id, Name from Account limit 1];
            System.assertEquals('test',objAcc.Name);            
            AttachLogDetailsToCase_C_HUM objClass = New AttachLogDetailsToCase_C_HUM();            
            objClass.sObjectId = objAcc.Id;            
            objClass.selectedCaseOption  = 'Existing Case';
            objClass.accId = objAcc.Id;
            objClass.sCaseNumber = '';
            objClass.sObjName = '';
            ApexPages.currentPage().getParameters().put('searchStartDate', '2012-10-12');
            ApexPages.currentPage().getParameters().put('searchEndDate', '2016-10-12');
            ApexPages.currentPage().getParameters().put('searchCaseNumber', '1236');
            ApexPages.CurrentPage().getParameters().put('standardURL','test');
            
            test.startTest();
            
            objClass.getCaseId();
            objClass.sObjectId = objAcc.Id;
            objClass.createPharmacyCaseAndRedirect();            
            objClass.retrievePharmacyOrderCases();
            objClass.sObjName = '';
            ApexPages.currentPage().getParameters().put('searchStartDate', null);
            ApexPages.currentPage().getParameters().put('searchCaseNumber', '');
            objClass.retrievePharmacyOrderCases();
            Apexpages.currentPage().getParameters().put('keyDescValue','test&tset&test');
            objClass.createNewAttachmentLog = 'true';
            objClass.createAttachmentLogRecords();
            
			objClass.blegacyDeleteCheck = false;
			objClass.sCompMessage = 'test';
			AttachLogDetailsToCase_C_HUM.sClassfication = 'test';
			
			objClass.sInteractionId = '1241241234';
			objClass.sSelectedCaseId = '141414124';
			objClass.associateInteractiontToCase();
			
            test.stopTest();
            
        }
    }
    
}
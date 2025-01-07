/**************************************************************************************************************
Apex Class Name : LoggedDetails_T_HUM
Version         : 1.0
Created On      : 03/16/2015
Function        : Test class for LoggedDetails_C_HUM

* Modification Log:
* Developer Name            Code Review                 Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Santhi Mandava            24069                       03/16/2015                 Original version
* Santhi Mandava            24308                       04/05/2016                 Modified test class as per defect 221550.0001 fix.
* Santhi Mandava            25231                       04/19/2016                 Modified test class as per defect 223172.0001 fix.
* Shreya Choodamani	                                    04/28/2017                 REQ#: 311397 Updated the test class for Pharmacy USers
* Harika Devi Kalidindi     112000                      07/20/2017                 REQ - 323435 :Added Policy_Group_Number__c field value while inserting policy
**************************************************************************************************************************/
@isTest
private class LoggedDetails_T_HUM
{
    /*
    * Method Name : setup
    * Description : This method is used to create test data for the class 
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

        User oUser2 = HUMTestDataHelperTest.createTestUser('User3','Humana Pharmacy Specialist');
        oUser2.Current_Queue__c = 'Cincinnati Home Office Calls';
        oUser2.Network_User_Id__c = 'SXS2222';
        insert oUser2;
               
        User oUser3 = HUMTestDataHelperTest.createTestUser('User4','Humana Pharmacy Specialist');
        oUser3.Current_Queue__c = 'Cincinnati Home Office Calls';
        oUser3.Network_User_Id__c = 'SXS2222';
        insert oUser3;
        
        PermissionSet ps = [Select Id, name from PermissionSet where name ='CRMS_300_Humana_Pharmacy_Supervisor'];
        
        PermissionSetAssignment psa = New PermissionSetAssignment();
        psa.AssigneeId = oUser3.Id;
        psa.PermissionSetId = ps.Id;
        insert psa;
        
        system.assertEquals(psa.PermissionSetId,ps.Id);

        system.runAs(oUser)
        {
            LoggingTestDataSetup_T_HUM.createTestData();
            List<Case> lstCases = [Select Id,Intent_ID__r.name from Case where Intent_ID__r.name =: 'Billing'];
            System.assertEquals(1,lstCases.size());
            Attachment_log__c  objAttchLog = LoggingTestDataSetup_T_HUM.createAttachmentLog(lstCases[0].Id);
            System.assertEquals('Auth Summary',objAttchLog.Attachment_Type__c);

            Pharmacy_Supervisor_HUM__c objPharmacySup = New Pharmacy_Supervisor_HUM__c(name ='CRMS_300_Humana_Pharmacy_Supervisor');
            insert objPharmacySup;
       	    system.assertEquals(objPharmacySup.Name,'CRMS_300_Humana_Pharmacy_Supervisor');  
        }
    }
    
    /*
    * Method Name : testFunctionality
    * Description : This method is used to test main functionality.
    * Return type : void
    * Parameter   : NA
    */
    static testMethod void testFunctionality() 
    {
        User oUser = [Select Id,Network_User_Id__c from User where LastName = 'test User1'];
        System.assertEquals('SXS2222', oUser.Network_User_Id__c);
        
        System.runAs(oUser)
        {
            List<Case> lstCases = [Select Id,Intent_ID__r.name from Case where Intent_ID__r.name =: 'Billing'];
            LoggedDetails_C_HUM objClass = new LoggedDetails_C_HUM();
            objClass.sCaseId = lstCases[0].Id;
            objClass.retrieveLogDetails();
            System.assertEquals('Auth:093028246',objClass.lstLogReocrds[0].Attachment_Sub_type__c);
            
            System.assertEquals(100,objClass.iConstantValue);
            System.assertEquals(false,objClass.hasNext);
            System.assertEquals(false,objClass.hasPrevious );
            System.assertEquals(1,objClass.pageNumber);
            System.assertEquals('CreatedDate',objClass.sSortExpression );
            System.assertEquals(1,objClass.lstLogReocrds.size());
            System.assertEquals('DESC',objClass.getSortDirection());
            objClass.setSortDirection('ASC');
            System.assertEquals('ASC',objClass.getSortDirection());
            
            objClass.sortResultPage();
            System.assertEquals(1,objClass.lstFinalLogReocrds.size());
            
            objClass.sSortExpression = 'CreatedDate';
            System.assertEquals('CreatedDate',objClass.sSortExpression );
            objClass.previous();
            System.assertEquals(1,objClass.pageNumber);
            objClass.first();
            System.assertEquals(1,objClass.pageNumber);
            objClass.last();
            System.assertEquals(1,objClass.pageNumber);
            objClass.next();
            System.assertEquals(1,objClass.pageNumber);
            objClass.stdsetcon =null;
            System.assertEquals(null,objClass.hasNext);
            System.assertEquals(null,objClass.hasPrevious );
            System.assertEquals(null,objClass.pageNumber);
            
            ApexPages.currentPage().getParameters().put('attchmentId', objClass.lstLogReocrds[0].Id);
            objClass.deleteLogAttachments();
            List<Attachment_log__c> lstRecords = [Select Id from Attachment_log__c where isDeleted =:true];
            System.assertEquals(0,lstRecords.size());
        }
    }

    /*
    * Method Name : testFunctionalityHumPhar
    * Description : This method Is used To test main functionality For pharmacy users.
    * Return type : void
    * Parameter   : NA
    */
    Static testMethod void testFunctionalityHumPhar() 
    {
        User oUser = [Select Id, Network_User_Id__c from User where LastName = 'test User3'];
        System.assertEquals('SXS2222', oUser.Network_User_Id__c);
        
        System.runAs(oUser)
        {
            Id grpRecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Group').getRecordTypeId();
            Account acc = HUMTestDataHelperTest.getAccount();
	        acc.name = 'testPharm';
	        acc.Source_Platform_Code__c ='EM';
	        acc.Source_Customer_Key__c = 'XY';
	        acc.Enterprise_ID__c = '123456';
	        acc.Recordtypeid = grpRecordTypeId ;
	        insert acc;
	        system.assertNotEquals(Null,acc.id);
	        system.assertequals(grpRecordTypeId ,acc.Recordtypeid);
	        
	        Policy__c policy = New Policy__c();
	        policy.Platform__c = 'LV';
	        policy.Group_Name__c = acc.Id;
            policy.Policy_Group_Number__c='11111';
	        insert policy ;
	        system.assertequals('LV',policy.Platform__c );
            
            Policy_Member__c objPolMemeber = New Policy_Member__c();
            
            objPolMemeber.Policy__c = policy.Id;
        	objPolMemeber.Source_Coverage_ID__c = '12345';
        	objPolMemeber.Member__c = acc.Id; 
        	insert objPolMemeber;
        	system.assertequals('12345',objPolMemeber.Source_Coverage_ID__c );
        	
        	Classification__c ClassificationValue = [Select Id, Name from Classification__c where Name = 'Claims'];
        	Intent__C intentRec = [Select Id, Name from Intent__c where Name='Billing'];
        	
        
        	List<Case> lstCases = New List<Case>();
        	lstCases.Add(New Case(AccountId=acc.Id, Intent_Id__c = intentRec.Id , Classification_Id__c =ClassificationValue.Id, Policy_Member__c= objPolMemeber.Id ,
                         Interacting_With_Type__c = 'Group', Interacting_With__c=acc.Id, Due_Date__c=Date.Today(),
                         Case_Owner__c=UserInfo.getUserName(), Owner_Queue__c='Cincinnati CRU',
                         Service_Center__c ='Cincinnati', Department__c='CRU', status='In Progress'));
        	insert lstCases;
        	
        	List<Case> listCases = [Select Id,Intent_ID__r.name, Classification_ID__r.name from Case where Classification_ID__r.name = 'Claims'];
            System.assertEquals(1,listCases.size());
            Attachment_log__c  objAttchLog = LoggingTestDataSetup_T_HUM.createAttachmentLog(listCases[0].Id);
            System.assertEquals('Auth Summary',objAttchLog.Attachment_Type__c);
            
            LoggedDetails_C_HUM objClass = New LoggedDetails_C_HUM();
            objClass.sCaseId = listCases[0].Id;
            String sUser = objClass.sPharmacyUser;
            objClass.retrieveLogDetails();
            System.assertEquals('Auth:093028246',objClass.lstLogReocrds[0].Attachment_Sub_type__c);
            
            System.assertEquals(100,objClass.iConstantValue);
            System.assertEquals(false,objClass.hasNext);
            System.assertEquals(false,objClass.hasPrevious );
            System.assertEquals(1,objClass.pageNumber);
            System.assertEquals('CreatedDate',objClass.sSortExpression );
            System.assertEquals(1,objClass.lstLogReocrds.size());
            System.assertEquals('DESC',objClass.getSortDirection());
            objClass.setSortDirection('ASC');
            System.assertEquals('ASC',objClass.getSortDirection());
            
            objClass.sortResultPage();
            System.assertEquals(1,objClass.lstFinalLogReocrds.size());
            
            objClass.sSortExpression = 'CreatedDate';
            System.assertEquals('CreatedDate',objClass.sSortExpression );
            objClass.previous();
            System.assertEquals(1,objClass.pageNumber);
            objClass.first();
            System.assertEquals(1,objClass.pageNumber);
            objClass.last();
            System.assertEquals(1,objClass.pageNumber);
            objClass.next();
            System.assertEquals(1,objClass.pageNumber);
            objClass.stdsetcon =null;
            System.assertEquals(null,objClass.hasNext);
            System.assertEquals(null,objClass.hasPrevious );
            System.assertEquals(null,objClass.pageNumber);
            
            ApexPages.currentPage().getParameters().put('attchmentId', objClass.lstLogReocrds[0].Id);
            objClass.deleteLogAttachments();
            List<Attachment_log__c> lstRecords = [Select Id from Attachment_log__c where isDeleted =:true];
            System.assertEquals(0,lstRecords.size());
        }
    }
}
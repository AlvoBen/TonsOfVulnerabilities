/**************************************************************************************************************
Apex Class Name : LoggingProcessLogic_T_HUM
Version         : 1.0
Created On      : 03/16/2015
Function        : Test class for LoggingProcessLogic_C_HUM

* Modification Log:
* Developer Name            Code Review                 Date                       Description
*----------------------------------------------------------------------------------------------------------------
* Santhi Mandava              24069                     03/16/2015                 Original version
* Santhi Mandava              24308                     04/05/2016                 Modified test class according to defect fix 221734.0001
* Santhi Mandava              26846                     05/04/2016                 Modified this class to include claim logging changes.
* Santhi Mandava              27512                     05/16/2016                 Modified wrapper variables
* Manish Kumar                                          08/24/2016                 Worked on REQ#275929 for DPaaS error handling.
* Anurag Shah                 201051                	03/13/2018           	   REQ - 354520:: SF - USER - Research/Attachments - MA Post Claims Summary.
* Anurag Shah                 235263                    05/23/2018          	   Reverting the Resolution changes.
* Lakshmi Madduri                                       05/05/2021                 2235919 - Code coverage improvment
* Ashok Kumar Nutalapati            					09/30/2021            	   REQ - 2593758 T1PRJ0002517 - MF 2 - New case button, create case, & open new case in a subtab (RXRF) (ID# 35b)
*****************************************************************************************************************/

@isTest
private class LoggingProcessLogic_T_HUM
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
        User oTestUser = HUMTestDataHelperTest.createTestUser('TestHPUser1','Humana Pharmacy Specialist');
        oTestUser.Current_Queue__c='Humana Pharmacy Web Chat';
        insert oTestUser;
        System.assertEquals(oUser.LastName, 'test User1');
        HUMAccountCaseRecordTypes__c obj = new HUMAccountCaseRecordTypes__c();
        obj.Name='HP Member';
        obj.Account_RecordType__c='Member';
        obj.Case_RecordType__c='HP Member Case';
        obj.Humana_Pharmacy__c=true;
        insert obj;
        System.assertEquals(obj.Humana_Pharmacy__c, true);
        HUMCaseInteractionAboutType__c objInteraction = new HUMCaseInteractionAboutType__c();
        objInteraction.Name='Member';
        objInteraction.CaseInteraction_About_Type__c='Member';
        insert objInteraction;
        System.assertEquals(objInteraction.CaseInteraction_About_Type__c, 'Member');
        system.runAs(oUser)
        {
            LoggingTestDataSetup_T_HUM.createTestData();
        }
    }
    
     /*
    * Method Name : testFunctionality
    * Description : This method is used to test main functionality.
    * Return type : void
    * Parameter   : NA
    */
     @isTest static void testFunctionality() 
    {
        User oUser = [Select Id,Network_User_Id__c from User where LastName = 'test User1'];
        System.assertEquals('SXS2222', oUser.Network_User_Id__c);
        User oHPUser = [Select Id,LastName from User where LastName = 'test TestHPUser1' limit 1];
        System.assertEquals('test TestHPUser1', oHPUser.LastName);
        System.runAs(oUser)
        {
            Test.startTest();
            List<Case> lstCases = [Select Id,CaseNumber,AccountId from Case];
            System.assertEquals(3, lstCases.size());
            
            string screenshotData = 'Sample Screenshot data Sample Screenshot data Sample Screenshot data';
            Blob objblob = Blob.valueOf(screenshotData);
            string paramvalue = EncodingUtil.base64Encode(objblob);
            ApexPages.currentPage().getParameters().put('screenshotData', paramvalue );
            ApexPages.currentPage().getParameters().put('type', 'Auth/Referral Summary');
            ApexPages.currentPage().getParameters().put('subtype', 'Auth/Referral Summary');
            
            List<Case> lstFilteredCases = LoggingProcessLogic_S_HUM.SearchCases(lstCases[0].CaseNumber,lstCases);
            System.assertEquals(1,lstFilteredCases.size());
            
            lstFilteredCases = LoggingProcessLogic_S_HUM.SearchCases('',lstCases);
            System.assertEquals(3,lstFilteredCases.size());
            
            String sCaseID = LoggingProcessLogic_S_HUM.createNewCase(lstCases[0].AccountId,'');
            System.assertNotEquals('',sCaseID);
            
            LoggingWrapper_DTO_HUM objWrapper = new LoggingWrapper_DTO_HUM();
            
            Map<String,String> mapJSonData = new Map<String,String>();
            mapJSonData.put('Status','Inprogress');
            
            Map<String,Map<String,String>> mapStJSonData = new Map<String,Map<String,String>>();
            mapStJSonData.put('Status',new Map<String,String>{'Stsatus' => 'Inprogress'});
            
            String sData = JSON.serialize(mapJSonData);
            objWrapper.sSelectedCaseId = lstCases[0].Id;
            objWrapper.sAccountId = lstCases[0].AccountId;
            objWrapper.sAuthId = '12345';
            objWrapper.sType = Apexpages.currentpage().getparameters().get('type');
            objWrapper.sJsonMap = sData ;
            objWrapper.sJsonStandardMap = JSON.serialize(mapStJSonData);
            
            Id objAttchId = LoggingProcessLogic_S_HUM.createAttachmentLog(objWrapper );
            System.assertNotEquals(Null,objAttchId);
            sData = JSON.serialize(mapJSonData);
            objWrapper.sJsonMap = sData ;
            objWrapper.sJsonStandardMap = sData ;
            
            LoggingProcessLogic_S_HUM.updateAttachmentLog(objAttchId,objWrapper);
            Attachment_Log__c obj =[Select  User_Selected_Value__c from Attachment_Log__c where Id=:objAttchId];
            System.assertEquals('{"Status":"Inprogress"}',obj.User_Selected_Value__c); 
            
            LoggingProcessLogic_S_HUM.createAttachment(objAttchId,paramvalue );
            Attachment objAttch = [Select Name from  Attachment where Name =: 'Logging_Screenshot_Encrypted.png'];
            System.assertEquals('Logging_Screenshot_Encrypted.png',objAttch.Name); 
            
            List<Attachment> lst = LoggingProcessLogic_S_HUM.getAttachments(objAttchId); 
            
            String sErrorName = LoggingProcessLogic_S_HUM.createErrorLog('Sample Error','LoggingProcessLogic_S_HUM');   
            System.assertNotEquals(Null,sErrorName ); 

            System.runAs(oHPUser)
            {
                Id memRecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId();
                Account oMemAcc = new Account();
                oMemAcc.RecordTypeId = memRecordTypeId;
                oMemAcc.FirstName = 'TestMember';
                oMemAcc.LastName='Acc';
                oMemAcc.Source_Platform_Code__c ='EM';
                oMemAcc.Source_Customer_Key__c = 'XY';
                oMemAcc.Enterprise_ID__c = '49834847578';
                oMemAcc.NPI_ID__c='984';
                insert oMemAcc;
                System.assertEquals(oMemAcc.Enterprise_ID__c,'49834847578');
                String sReturn = LoggingProcessLogic_S_HUM.createNewPharmacyLoggingCase(oMemAcc.Id,'Case');
                System.assertNotEquals(sReturn,null);
				String sReturnAddress = LoggingProcessLogic_S_HUM.createNewPharmacyAddressCase(oMemAcc.Id);
				System.assertNotEquals(sReturnAddress,null);
            } 
            Test.stopTest();
        }
    }
    
    /*
    * Method Name : throwDPAASErrorOnUI
    * Description : This method is used to test DPAAS error functionality
    * Return type : void
    * Parameter   : NA
    */
    @isTest static void throwDPAASErrorOnUI()
    {
        Test.startTest();
            String err = LoggingProcessLogic_S_HUM.createErrorLog('Test DPAAS error', 'LoggingProcessLogic_T_HUM');
            Error_Log__c err_log = [Select Id, Name From Error_Log__c Where name =: err.substring(err.indexOf('ER-'), err.length()) Limit 1];
        Test.stopTest();
        
        System.assert(err.containsIgnoreCase(err_log.Name));
    }
}
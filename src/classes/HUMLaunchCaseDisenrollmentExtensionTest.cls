/****************************************************************************
Apex Class Name  : HUMLaunchCaseDisenrollmentExtensionTest 
Version          : 1.0
Created On       : 05/19/2015
Function         : Test class to HUMLaunchCaseDisenrollmentExtension Apex class

Modification Log :
*    Developer                 CodeReview            Date                 Description
* ------------------------------------------------------------------------------------------------------------------------------
*    Suryakumari Medicherla     23305               05/19/2015             Original Version(Code Added to cover class for HUMLaunchCaseDisenrollmentExtension-REQ-105810)
*    Harshith Mandya            28341               12/05/2015             Updated test class for REQ - 21510
*    Prachi Bhasin              33486               07/18/2016             Changes made as part of REQ- 270349
*    Pallavi Ravishankar        49663               11/30/2016             Changes made as part of defect fix t#250549.0001
*    Pradeepkumar Dani          99570               06/08/2017             Intent__c.Classification__c & Intent__c.Etl_Idtfr__c Clean up
*    Pradeepkumar Dani          100996              06/13/2017             Classification__c.Classification_Type__c & Etl_Idftr__c Clean Up
*    Prudhvi Pamarthi           164129              01/19/2018             REQ - 328929 Fix for HUMExceptionHelper: DML 
*    Sunil Bukya  				293887			    10/03/2018			   REQ - 369758 - Implemented platform changes
**********************************************************************************************************************************/

@isTest
private class HUMLaunchCaseDisenrollmentExtensionTest 
{
    /**
    * Used to set up test data . 
    *  @name setUpData
    *  @param  NA
    *  @return NA
    *  @throws NA
    */
    @testsetup static void setUpData() 
    {
        User customerCareSpecialistUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert customerCareSpecialistUser;
        System.assert(customerCareSpecialistUser.LanguageLocaleKey == 'en_US');
        
        system.runAs(customerCareSpecialistUser)
        {
            Account oAccount = HUMTestDataHelperTest.getAccount();
            oAccount.Name = 'New Case Account';
            oAccount.Group_Number__c  = '123';
            oAccount.BillingState = 'New York';
            oAccount.recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Group').getRecordTypeId();
            insert oAccount;
            System.assert(oAccount.BillingState == 'New York');
            system.assertEquals(oAccount.Group_Number__c,'123');
            
            Product2 oProduct = HUMTestDataHelperTest.getProduct();
            oProduct.Name='Mediclaim';
            oProduct.Product_Type__c = 'MED';
            insert oProduct;
            system.assert(oProduct.Product_Type__c == 'MED');
            system.assertEquals(oProduct.Name,'Mediclaim');
            
            Policy__c oPolicy = HUMTestDataHelperTest.getPolicy();
            oPolicy.Name = 'test123';
            oPolicy.Group_Name__c = oAccount.Id;
            oPolicy.Funding_Type_Code__c = 'Y';
            oPolicy.Product__c = oProduct.Id;
            oPolicy.Issue_State__c = 'IL'; 
            oPolicy.Contract_Number__c = 'H6609';
            oPolicy.PBP_Code__c = '005';
            oPolicy.Business_Segment__c = '000';
            insert oPolicy;
            System.assert(oPolicy.Business_Segment__c == '000');
            system.assertEquals(oPolicy.PBP_Code__c , '005');
            
            Policy_Member__c oPolicyMem = HUMTestDataHelperTest.getPolicyMember();
            oPolicyMem.Name = '12345';
            oPolicyMem.Member__c = oAccount.Id;
            oPolicyMem.Policy__c = oPolicy.Id;            
            oPolicyMem.Relationship__c = 'Self';
            insert oPolicyMem;
            System.assert(oPolicyMem.Relationship__c == 'Self');
            system.assertEquals(oPolicyMem.Name, '12345');
            
            HUM_Webservice_Callout__c oWebCallOut = new HUM_Webservice_Callout__c();
            oWebCallOut.Name = 'HUMDisenrollmentCancellationService';
            oWebCallOut.Certificate_Name__c = 'Humana_GuidanceCare_Mercury';
            oWebCallOut.Active__c = true;
            oWebCallOut.End_Point_URL__c = 'https://test-gca-bps.humana.com:6001/Forms/webclient/TestHarness.html';
            insert oWebCallOut;
            system.assert(oWebCallOut.Active__c == true);  
            system.assertEquals(oWebCallOut.Certificate_Name__c, 'Humana_GuidanceCare_Mercury');         
            
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
            system.assertEquals(intentRec.Name == 'Filling', true);

             HUMConstants__c oConstant = new  HUMConstants__c();
             oConstant.name = 'HUMATTACHMENTLOGFORDAC_HUM';
             oConstant.StringValue__c = 'Medicare_Election_Lock_In_Template';
             insert oConstant; 
             system.assertEquals(oConstant.StringValue__c,'Medicare_Election_Lock_In_Template');
             system.assertEquals(oConstant.name,'HUMATTACHMENTLOGFORDAC_HUM');
             
             HUMConstants__c oConstantP = new  HUMConstants__c();
             oConstantP.name = 'PLAIN_ATTACHMENTLOGFORDAC_HUM';
             oConstantP.StringValue__c = 'Medicare_Election_Lock_In_Template';
             insert oConstantP; 
             system.assertEquals(oConstantP.StringValue__c,'Medicare_Election_Lock_In_Template');
             system.assertEquals(oConstantP.name,'PLAIN_ATTACHMENTLOGFORDAC_HUM');
             
             
             
            Case oCase = new Case(AccountId=oAccount.Id, Intent_Id__c = lstIntent[0].id, Classification_Id__c =lstClassification[0].id, Policy_Member__c= oPolicyMem.Id ,
                             Interacting_With_Type__c = 'Member',Interacting_With__c=oAccount.Id, Due_Date__c=Date.Today(), 
                             Case_Owner__c=UserInfo.getUserName(), Owner_Queue__c='Cincinnati CRU', Work_Queue_View_Name__c='Cincinati CRU',Service_Center__c ='Cincinnati',Department__c='CRU', status='In Progress');
            oCase.RecordTypeId = Schema.sObjectType.Case.getRecordTypeInfosByName().get('Group Case').getRecordTypeId();
            insert oCase;
            System.assert(oCase.Interacting_With_Type__c == 'Member');
                  
        }
    }
    
    /**
    *  Test method to create Attachment from Case detail page 
    *  @name createAttachfromCase
    *  @param  NA
    *  @return NA
    *  @throws NA
    */
    private static testMethod void createAttachfromCase() 
    {
        User customerCareSpecialistUser = [SELECT Id,LastName FROM User Where LastName = 'test User1' LIMIT 1];
        Case oCase = [SELECT Id,CaseNumber FROM Case LIMIT 1];
        HUMConstants__c oConstant = [SELECT Name, StringValue__c from HUMConstants__c where Name = 'HUMATTACHMENTLOGFORDAC_HUM' OR Name = 'PLAIN_ATTACHMENTLOGFORDAC_HUM' LIMIT 1 ];
        Policy__c oPolicy = [SELECT id,PBP_Code__c FROM Policy__c LIMIT 1];
        System.runAs(customerCareSpecialistUser)
        {
            Test.startTest();
            ApexPages.StandardController ostdController = new ApexPages.StandardController(oCase);
            ApexPages.currentPage().getParameters().put(HUMConstants.HUM_MEDICAREDAC_CASEID, oCase.Id);
            HUMLaunchCaseDisenrollmentExtension oextension = new  HUMLaunchCaseDisenrollmentExtension(ostdController);
            
            oPolicy.PBP_Code__c = '';
            update oPolicy;
            System.assert(oPolicy.PBP_Code__c == '');
            oextension.reDirectPage();
            
            HUMLaunchCaseDisenrollmentExtension oExtension2 = new  HUMLaunchCaseDisenrollmentExtension(ostdController);
            Attachment_Log__c oAttachLog = new Attachment_Log__c();
            oAttachLog.Attachment_Key__c = oConstant.StringValue__c;
            oAttachLog.Attachment_Type__c = 'FM';
            oAttachLog.Case__c = oCase.id;
            insert oAttachLog;
            system.assert(oAttachLog.Attachment_Type__c == 'FM');
            
            Attachment_Log_Item__c oAttchLogItem = new Attachment_Log_Item__c();
            oAttchLogItem.Attachment_Log__c = oAttachLog.id;
            oAttchLogItem.Value__c = 'URL';
            insert oAttchLogItem;
            system.assert(oAttchLogItem.Value__c == 'URL');

            HUMLaunchCaseDisenrollmentExtension oExtension3 = new  HUMLaunchCaseDisenrollmentExtension(ostdController);
            Test.stopTest();
        }   
    }
    
    /**
    *  Test method to test negative scenarios to create attachment on case
    *  @name testNegative
    *  @param  NA
    *  @return NA
    *  @throws NA
    */
    private static testMethod void testNegative() 
    {
        User customerCareSpecialistUser = [SELECT Id,LastName FROM User Where LastName = 'test User1' LIMIT 1];
        System.assert(customerCareSpecialistUser.LastName == 'test User1');
        
        Case oCase = [SELECT Id,CaseNumber, Owner_Queue__c FROM Case LIMIT 1];
        system.assertEquals(oCase.Owner_Queue__c,'Cincinnati CRU');
        
        HUMConstants__c oConstant = [SELECT Name, StringValue__c from HUMConstants__c where Name = 'HUMATTACHMENTLOGFORDAC_HUM' OR Name = 'PLAIN_ATTACHMENTLOGFORDAC_HUM' LIMIT 1 ];
        system.assertEquals(oConstant.StringValue__c,'Medicare_Election_Lock_In_Template');
        
        Policy__c oPolicy = [SELECT id,PBP_Code__c, Business_Segment__c FROM Policy__c LIMIT 1];
        System.assertEquals(oPolicy.Business_Segment__c, '000');
        
        System.runAs(customerCareSpecialistUser)
        {
            Test.startTest();
            PageReference pageRef = Page.HUMLaunchCaseDisenrollmentCancellation;
            Test.setCurrentPage(pageRef);
            ApexPages.StandardController ostdController = new ApexPages.StandardController(oCase);
            ApexPages.currentPage().getParameters().put(HUMConstants.HUM_MEDICAREDAC_CASEID, oCase.Id);
            Apexpages.currentPage().getParameters().put(HUMConstants.HUM_CASEACTIONLAUNCH_CASESUBTABID, '123'); 
            HUMLaunchCaseDisenrollmentExtension oExtension = new  HUMLaunchCaseDisenrollmentExtension(ostdController);
            oExtension.createDisenrollmentURL();
            
            List<Error_Log__c> lstLog = new List<Error_Log__c>();            
            Error_Log__c error = new Error_Log__c(Status__c = 'HUMLaunchCaseDisenrollmentExtension');            
            lstLog.add(error);                        
            oExtension.listErrorLogs = lstLog;            
            System.assert(oExtension.listErrorLogs.size() > 0);
            Test.stopTest();
      }
    }
}
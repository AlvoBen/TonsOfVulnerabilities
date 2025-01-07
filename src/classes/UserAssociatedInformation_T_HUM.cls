/***************************************************************************************************************************************************************** 
Apex Class Name : UserAssociatedInformation_T_HUM
Version : 1.0 
Created On : 06/22/2018 
Function : Test Class to cover UserAssociatedInformation_C_HUM
Modification Log :
* Developer                     Code Review              Date                 Description
* ------------------------------------------------------------------------------------------------------------------------------                
* Vandana Chaudhari                                      06/22/2018           Original Version
* Suraj Patil                                            06/22/2018           REQ - 350627
* Anurag Chaturvedi                                      10/30/2018           REQ - 350620
* Santhi Mandava                                         04/15/2019           Replaced "Member_Id_Value__c" with Name
* Suraj Patil             		              			 02/06/2020           REQ - 852426 - CA Tciket - 8454688 -  Designee domain for DesigneeServiceMPG service
* Atia Uzma                                              01/20/2022           User Story 3016951: T1PRJ0314829 - MF 1 - AHT - 'Associated Forms & Information' section - Member Account page - enhancements (RF)
-------------------------------------------------------------------------------------------------------------------------------> 
******************************************************************************************************************************************************************/ 
@isTest private class UserAssociatedInformation_T_HUM{ 
/* 
* Method Name : setUpTestData 
* Description : This method creates test data For test Class 
* Return type : None 
* Parameter : None */ 
private static Account oAcc;
private static Account objAcc;
private static Account deceasedAccount;

@testSetup static void setUpTestData() { 
    User objUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist'); 
    insert objUser; 
    HUM_Webservice_Callout__c serviceDetails = new HUM_Webservice_Callout__c(Name = 'HUMGetPHIConsentInfo',Certificate_Name__c = 'Test_Certificate' ,Active__c = true,End_Point_URL__c = 'https://dev-crmservicews.humana.com/DesigneeHubService/GetPHIConsentInfo' ,Timeout__c = '12000',Request_Type__c = 'POST',Content_Type__c ='application/json' );
    
    insert serviceDetails;
    
    system.assertEquals(objUser.lastname == 'test User1',true);
    System.runAs(objUser) { 
        Account objMemAccount = HUMTestDataHelperTest.getAccount(); 
        objMemAccount.FirstName = 'Test FName'; 
        objMemAccount.LastName = 'LName'; 
        objMemAccount.Individual_First_Name__c = 'Test FName'; 
        objMemAccount.Individual_Last_Name__c = 'LName'; 
		objMemAccount.Enterprise_ID__C = '1078620930';
        objMemAccount.Account_Security_EndDate__c = Date.today();
        objMemAccount.Account_Security_Access__c = 'Test';
        objMemAccount.Account_Security_Answer__c='Testing';
        objMemAccount.Account_Security_Question__c='What is u r name';
        objMemAccount.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId(); 
        insert objMemAccount; 
        System.assertEquals(objMemAccount.LastName == 'LName', true);
        
         List<HUMConstants__c> lstConstanst = new List<HUMConstants__c>();
    
            lstConstanst.Add(new HUMConstants__c(Name='PromptPA URL',StringValue__c='ABC',IntegerValue__c=0));
            
            insert lstConstanst;
            System.assert(lstConstanst.size() > 0 );
            objAcc = HUMTestDataHelperTest.getAccount();
            objAcc.Name = 'testacc';
            objAcc.Group_Number__c = '213654';
			objAcc.Enterprise_ID__C = '1078620930';
            insert objAcc;
            system.assertEquals(objAcc.Group_Number__c, '213654');
                
            oAcc = HUMTestDataHelperTest.getAccount();
            oAcc.Name = 'testacc1';
            oAcc.Group_Number__c = '213655';
			oAcc.Enterprise_ID__C = '1078620930';
            insert oAcc;
            system.assertEquals(oAcc.Group_Number__c, '213655');
            
            deceasedAccount = HUMTestDataHelperTest.getAccount();
            deceasedAccount.Name = 'testacc2';
            deceasedAccount.Deceased_Date__c = '07/26/2009';
            deceasedAccount.Account_Security_EndDate__c = Date.today();
            deceasedAccount.Account_Security_Access__c = 'Test';
            deceasedAccount.Account_Security_Answer__c='Testing';
            deceasedAccount.Account_Security_Question__c='What is u r name';
            insert deceasedAccount;
            system.assertEquals(deceasedAccount.Deceased_Date__c, '07/26/2009');
                
            Product2 oProd = new Product2(Name = 'Laptop X200',Segment_Type__c='MCR');
            insert oProd;
            System.assertEquals(oProd.Segment_Type__c, 'MCR');
                
            Policy__c objpol = HUMTestDataHelperTest.getPolicy();
            objpol.Name='testPolicy';
            objpol.Group_Name__c = objAcc.Id;
            objpol.Major_LOB__c = 'MER'; 
            objpol.Platform__c='LV';
            objpol.Source_Cust_Cov_Key__c = '123456789';
            objpol.Product__c = oProd.Id;
            objpol.Product__r = oProd;
            insert objpol; 
            system.assertEquals(objpol.Platform__c,'LV');
                
            objpol = [Select Name, Group_Name__c, Major_LOB__c, Source_Cust_Cov_Key__c, Product__c, Product__r.Segment_Type__c, LOB_Indicator__c 
                      From Policy__c Where Id = : objpol.Id];
             
            objpol.Product__r.Segment_Type__c = oProd.Segment_Type__c;
            update objPol;
            system.assertEquals(objpol.id!=null,true);
                
            objpol = [Select Name, Group_Name__c, Major_LOB__c, Source_Cust_Cov_Key__c, Product__c, Product__r.Segment_Type__c, LOB_Indicator__c 
                      From Policy__c Where Id = : objpol.Id];
                
            Policy__c opol = HUMTestDataHelperTest.getPolicy();
            opol.Name='testPolicy';
            opol.Group_Name__c = oAcc.Id;
            opol.Major_LOB__c = 'MER'; 
            opol.Platform__c='LV';
            opol.Source_Cust_Cov_Key__c = '123456789';
            opol.Product__c = oProd.Id;
            opol.Product__r = oProd;
            insert opol; 
            system.assertEquals(opol.Major_LOB__c,'MER');
                
            opol = [Select Name, Group_Name__c, Major_LOB__c, Source_Cust_Cov_Key__c, Product__c, Product__r.Segment_Type__c, LOB_Indicator__c 
                    From Policy__c Where Id = : opol.Id];
                
            Policy_Member__c objPolMem = new Policy_Member__c();
            objPolMem.Name = 'testpolmem';
            objPolMem.Relationship__c = 'Self';
            objPolMem.Effective_Date__c=Date.today().addDays(-1).format();
            objPolMem.End_Date__c=Date.today().addDays(1).format();
            objPolMem.Policy_Member_External_ID__c='00|ODS|6925004434817|12345678|34567|2013-07-01';
            objPolMem.Member__c = objAcc.Id;
            objPolMem.Policy__c = objpol.Id;       
            objPolMem.Policy__r = objpol;       
            insert objPolMem;
            system.assertEquals(objPolMem.Relationship__c, 'Self');
                
            Policy_Member__c oPolMem = new Policy_Member__c();
            oPolMem.Name = 'testpolmem';
            oPolMem.Relationship__c = 'Self';
            oPolMem.Effective_Date__c=Date.today().addDays(-1).format();
            oPolMem.End_Date__c=Date.today().addDays(1).format();
            oPolMem.Policy_Member_External_ID__c='01|ODS|6925004434817|12345678|34567|2013-07-01';
            oPolMem.Member__c = oAcc.Id;
            oPolMem.Policy__c = opol.Id;       
            insert oPolMem;
            system.assertEquals(oPolMem.Relationship__c, 'Self');
                
            Member_Id__c memberIdInstance = new Member_Id__c();
            memberIdInstance.Policy__c = objpol.Id;
            memberIdInstance.Policy_Member__c = objPolMem.Id;
            memberIdInstance.Member_Card_External_ID__c = objPolMem.Id + 'ExtId';
            memberIdInstance.Type__c = 'Member-Id-Base';
            memberIdInstance.Name = '123456789012';
            insert memberIdInstance;
            system.assertEquals(memberIdInstance.Type__c, 'Member-Id-Base');
              
            Member_Id__c oMem = new Member_Id__c();
            oMem.Policy__c = opol.Id;
            oMem.Policy_Member__c = oPolMem.Id;
            oMem.Member_Card_External_ID__c = oPolMem.Id + 'ExtId1';
            oMem.Type__c = 'Member';
            oMem.Name = '123456789012';
            insert oMem;
            system.assertEquals(oMem.Type__c, 'Member');
                
            insert new HUM_Webservice_Callout__c(Name='CIMedicareMultipleMemberDetails',SampleParameter__c='sample',  Active__c=true, End_Point_URL__c='https://dev-crmservicews.humana.com/CIMedicareMultipleMemberDetails', Timeout__c='120000', Request_Type__c='POST', Content_Type__c='application/json', Certificate_Name__c='Test_Certificate');     
            system.assert(HUM_Webservice_Callout__c.getValues('CIMedicareMultipleMemberDetails').Certificate_Name__c == 'Test_Certificate');
        
          CRMFunctionality_ONOFF_Switch__c oswitch = new CRMFunctionality_ONOFF_Switch__c();
            oswitch.IsON__c =true;
            oswitch.Name = '3016951';
            insert oswitch;
        
        CRMFunctionality_ONOFF_Switch__c oswitch1 = new CRMFunctionality_ONOFF_Switch__c();
            oswitch1.IsON__c =true;
            oswitch1.Name = '2786823';
            insert oswitch1;
       
     }     
} 
/* 
* This method Is used for overall data setup and constructor. 
* @param NA 
* @return void */ 
static testMethod void overallTestMethod() { 

    User testUser = [Select Id, LastName from User where LastName='test User1']; 
    System.assertEquals(testUser != null,true); 
    System.runAs(testUser) { 
        Test.startTest(); 
           Account acc = [select Id,Group_Number__c from account where LastName = 'LName' limit 1];
           Account deceasedAcc = [select Id,Deceased_Date__c from account where Name = 'testacc2' limit 1];
           String accID = acc.Id; 
           System.assertEquals(acc != null,true);
           UserAssociatedInformation_C_HUM objUser = new UserAssociatedInformation_C_HUM();
           objUser.sAccountId=acc.id;
           UserAssociatedInformation_C_HUM decUser = new UserAssociatedInformation_C_HUM();
           decUser.sAccountId = deceasedAcc.id;
           List<UserInformation_DTO_HUM> lstUserInfo = UserAssociatedInformation_C_HUM.getUserInformation(acc.Id);
           List<UserInformation_DTO_HUM> deceasedUserInfo = UserAssociatedInformation_C_HUM.getUserInformation(deceasedAcc.Id);
           System.assertEquals(String.isBlank(deceasedUserInfo[0].SecurityQuestion),true);
           System.assertEquals(String.isBlank(deceasedUserInfo[0].UserValue),true);
           System.assertEquals(String.isBlank(deceasedUserInfo[0].Answer),true);
           objUser.deleteInfo();
           objUser.closePopup();
           ObjUSer.showPopup();
           
           ObjUser.bSwitch= true;
           ObjUser.bExpandCollapseSwitch = true;
           Test.stopTest(); 
    } 
} 


  static testMethod void testPositivePHIConsent()
    {
         User testUser = [Select Id, LastName from User where LastName='test User1']; 
         System.assertEquals(testUser != null,true); 
         Account acc = [select Id,Group_Number__c from account where LastName = 'LName' limit 1];
         System.assertEquals(acc != null,true);
         Account accObj = new Account();
         accObj.Name = 'Test Account';
         
         insert accObj;
		 Account objAcc = [select id, Enterprise_ID__C from Account where Name =: 'testacc' limit 1]; 
         string sAccountId = objAcc.id;
         System.runAs(testUser)
         {
          Test.startTest();
          UserAssociatedInformation_C_HUM objUser = new UserAssociatedInformation_C_HUM();
          UserAssociatedInformation_D_HUM objDataDesignee = new UserAssociatedInformation_D_HUM();
          Continuation continueObj = (Continuation)UserAssociatedInformation_C_HUM.getPHIConsentInfo(sAccountId);
           HttpResponse httpRes = new Httpresponse ();
          String resBodyString = '{ "GetPHIConsentResponse":{ "ResponseStatus":{ "Success":"true", "Error":"" }, "DesigneeList":[ { "DesigneeId":"1108375", "RelationshipDetails":{ "DocumentType":"Consent for PHI", "Relationship":"Spouse", "LevelOfConsent":"FullDisclosure", "LineOfCoverage":"", "LimitedDisclosureDetails":"Any and all protected health information Humana maintains including mental health, HIV status, substance abuse records, mail-order pharmacy, wellness products and health programs", "EffectiveDate":"2018-09-12T17:36:00", "TerminationDate":"2018-09-14T00:00:00", "ServiceStartDate":"", "ServiceEndDate":"", "SubmissionMehtod":"Electronic", "POA":{ "Type":"", "Description":"" } }, "Demographics":{ "PersonofAuthority":" ", "GuardianAddress":{ "AddressType":"", "AddressLineOne":"", "AddressLineTwo":"", "City":"", "State":"", "Zip":"", "Validated":"" }, "Phone":{ "PhoneType":"", "PhoneNumber":"", "Extension":"" } } }, { "DesigneeId":"1108386", "RelationshipDetails":{ "DocumentType":"Consent for PHI", "Relationship":"Spouse", "LevelOfConsent":"FullDisclosure", "LineOfCoverage":"", "LimitedDisclosureDetails":"Any and all protected health information Humana maintains including mental health, HIV status, substance abuse records, mail-order pharmacy, wellness products and health programs", "EffectiveDate":"2018-09-14T20:07:00", "TerminationDate":"2018-09-14T00:00:00", "ServiceStartDate":"", "ServiceEndDate":"", "SubmissionMehtod":"MAPA", "POA":{ "Type":"", "Description":"" } }, "Demographics":{ "PersonofAuthority":" ", "GuardianAddress":{ "AddressType":"", "AddressLineOne":"", "AddressLineTwo":"", "City":"", "State":"", "Zip":"", "Validated":"" }, "Phone":{ "PhoneType":"", "PhoneNumber":"", "Extension":"" } } }, { "DesigneeId":"1108423", "RelationshipDetails":{ "DocumentType":"Consent for PHI", "Relationship":"Spouse", "LevelOfConsent":"FullDisclosure", "LineOfCoverage":"", "LimitedDisclosureDetails":"Any and all protected health information Humana maintains including mental health, HIV status, substance abuse records, mail-order pharmacy, wellness products and health programs", "EffectiveDate":"2018-09-24T18:53:00", "TerminationDate":"2018-09-27T00:00:00", "ServiceStartDate":"", "ServiceEndDate":"", "SubmissionMehtod":"MAPA", "POA":{ "Type":"", "Description":"" } }, "Demographics":{ "PersonofAuthority":" ", "GuardianAddress":{ "AddressType":"", "AddressLineOne":"", "AddressLineTwo":"", "City":"", "State":"", "Zip":"", "Validated":"" }, "Phone":{ "PhoneType":"", "PhoneNumber":"", "Extension":"" } } }, { "DesigneeId":"1108436", "RelationshipDetails":{ "DocumentType":"Power of Attorney", "Relationship":"Sibling", "LevelOfConsent":"", "LineOfCoverage":"", "LimitedDisclosureDetails":"", "EffectiveDate":"2018-09-28T00:00:00", "TerminationDate":"2018-09-28T00:00:00", "ServiceStartDate":"", "ServiceEndDate":"", "SubmissionMehtod":"Paper", "POA":{ "Type":"", "Description":"" } }, "Demographics":{ "PersonofAuthority":" ", "GuardianAddress":{ "AddressType":"", "AddressLineOne":"", "AddressLineTwo":"", "City":"", "State":"", "Zip":"", "Validated":"" }, "Phone":{ "PhoneType":"", "PhoneNumber":"", "Extension":"" } } }, { "DesigneeId":"1108457", "RelationshipDetails":{ "DocumentType":"Consent for PHI", "Relationship":"Spouse", "LevelOfConsent":"FullDisclosure", "LineOfCoverage":"", "LimitedDisclosureDetails":"Any and all protected health information Humana maintains including mental health, HIV status, substance abuse records, mail-order pharmacy, wellness products and health programs", "EffectiveDate":"2018-09-27T18:09:00", "TerminationDate":"2018-09-27T00:00:00", "ServiceStartDate":"", "ServiceEndDate":"", "SubmissionMehtod":"MAPA", "POA":{ "Type":"", "Description":"" } }, "Demographics":{ "PersonofAuthority":" ", "GuardianAddress":{ "AddressType":"", "AddressLineOne":"", "AddressLineTwo":"", "City":"", "State":"", "Zip":"", "Validated":"" }, "Phone":{ "PhoneType":"", "PhoneNumber":"", "Extension":"" } } }, { "DesigneeId":"1108464", "RelationshipDetails":{ "DocumentType":"Power of Attorney", "Relationship":"Organization", "LevelOfConsent":"", "LineOfCoverage":"", "LimitedDisclosureDetails":"", "EffectiveDate":"2018-09-28T00:00:00", "TerminationDate":"2018-10-01T00:00:00", "ServiceStartDate":"", "ServiceEndDate":"", "SubmissionMehtod":"Paper", "POA":{ "Type":"", "Description":"" } }, "Demographics":{ "PersonofAuthority":" ", "GuardianAddress":{ "AddressType":"", "AddressLineOne":"", "AddressLineTwo":"", "City":"", "State":"", "Zip":"", "Validated":"" }, "Phone":{ "PhoneType":"", "PhoneNumber":"", "Extension":"" } } }, { "DesigneeId":"1108465", "RelationshipDetails":{ "DocumentType":"Consent for PHI", "Relationship":"Sibling", "LevelOfConsent":"FullDisclosure", "LineOfCoverage":"", "LimitedDisclosureDetails":"Any and all protected health information Humana maintains including mental health, HIV status, substance abuse records, mail-order pharmacy, wellness products and health programs", "EffectiveDate":"2018-09-19T00:00:00", "TerminationDate":"2020-09-19T00:00:00", "ServiceStartDate":"", "ServiceEndDate":"", "SubmissionMehtod":"MAPA", "POA":{ "Type":"", "Description":"" } }, "Demographics":{ "PersonofAuthority":" ", "GuardianAddress":{ "AddressType":"", "AddressLineOne":"", "AddressLineTwo":"", "City":"", "State":"", "Zip":"", "Validated":"" }, "Phone":{ "PhoneType":"", "PhoneNumber":"", "Extension":"" } } }, { "DesigneeId":"1108466", "RelationshipDetails":{ "DocumentType":"Power of Attorney", "Relationship":"None", "LevelOfConsent":"", "LineOfCoverage":"", "LimitedDisclosureDetails":"", "EffectiveDate":"2018-09-25T00:00:00", "TerminationDate":"9999-12-31T00:00:00", "ServiceStartDate":"", "ServiceEndDate":"", "SubmissionMehtod":"Paper", "POA":{ "Type":"", "Description":"" } }, "Demographics":{ "PersonofAuthority":" ", "GuardianAddress":{ "AddressType":"", "AddressLineOne":"", "AddressLineTwo":"", "City":"", "State":"", "Zip":"", "Validated":"" }, "Phone":{ "PhoneType":"", "PhoneNumber":"", "Extension":"" } } }, { "DesigneeId":"1108467", "RelationshipDetails":{ "DocumentType":"Consent for PHI", "Relationship":"Parent", "LevelOfConsent":"FullDisclosure", "LineOfCoverage":"", "LimitedDisclosureDetails":"Any and all protected health information Humana maintains including mental health, HIV status, substance abuse records, mail-order pharmacy, wellness products and health programs", "EffectiveDate":"2016-11-25T00:00:00", "TerminationDate":"2018-10-03T00:00:00", "ServiceStartDate":"", "ServiceEndDate":"", "SubmissionMehtod":"Paper", "POA":{ "Type":"", "Description":"" } }, "Demographics":{ "PersonofAuthority":" ", "GuardianAddress":{ "AddressType":"", "AddressLineOne":"", "AddressLineTwo":"", "City":"", "State":"", "Zip":"", "Validated":"" }, "Phone":{ "PhoneType":"", "PhoneNumber":"", "Extension":"" } } }, { "DesigneeId":"1108469", "RelationshipDetails":{ "DocumentType":"Power of Attorney", "Relationship":"None", "LevelOfConsent":"", "LineOfCoverage":"", "LimitedDisclosureDetails":"", "EffectiveDate":"2018-09-28T00:00:00", "TerminationDate":"9999-12-31T00:00:00", "ServiceStartDate":"", "ServiceEndDate":"", "SubmissionMehtod":"Paper", "POA":{ "Type":"", "Description":"" } }, "Demographics":{ "PersonofAuthority":" ", "GuardianAddress":{ "AddressType":"", "AddressLineOne":"", "AddressLineTwo":"", "City":"", "State":"", "Zip":"", "Validated":"" }, "Phone":{ "PhoneType":"", "PhoneNumber":"", "Extension":"" } } }, { "DesigneeId":"1108521", "RelationshipDetails":{ "DocumentType":"Power of Attorney", "Relationship":"None", "LevelOfConsent":"", "LineOfCoverage":"", "LimitedDisclosureDetails":"", "EffectiveDate":"2017-01-27T00:00:00", "TerminationDate":"9999-12-31T00:00:00", "ServiceStartDate":"", "ServiceEndDate":"", "SubmissionMehtod":"Paper", "POA":{ "Type":"", "Description":"" } }, "Demographics":{ "PersonofAuthority":" ", "GuardianAddress":{ "AddressType":"", "AddressLineOne":"", "AddressLineTwo":"", "City":"", "State":"", "Zip":"", "Validated":"" }, "Phone":{ "PhoneType":"", "PhoneNumber":"", "Extension":"" } } }, { "DesigneeId":"1108522", "RelationshipDetails":{ "DocumentType":"Executor of Estate", "Relationship":"None", "LevelOfConsent":"", "LineOfCoverage":"", "LimitedDisclosureDetails":"", "EffectiveDate":"2017-10-01T00:00:00", "TerminationDate":"9999-12-31T00:00:00", "ServiceStartDate":"", "ServiceEndDate":"", "SubmissionMehtod":"Paper", "POA":{ "Type":"", "Description":"" } }, "Demographics":{ "PersonofAuthority":" ", "GuardianAddress":{ "AddressType":"", "AddressLineOne":"", "AddressLineTwo":"", "City":"", "State":"", "Zip":"", "Validated":"" }, "Phone":{ "PhoneType":"", "PhoneNumber":"", "Extension":"" } } }, { "DesigneeId":"1108523", "RelationshipDetails":{ "DocumentType":"Legan Guardian", "Relationship":"None", "LevelOfConsent":"", "LineOfCoverage":"", "LimitedDisclosureDetails":"", "EffectiveDate":"2017-10-05T00:00:00", "TerminationDate":"9999-12-31T00:00:00", "ServiceStartDate":"", "ServiceEndDate":"", "SubmissionMehtod":"Paper", "POA":{ "Type":"", "Description":"" } }, "Demographics":{ "PersonofAuthority":" ", "GuardianAddress":{ "AddressType":"", "AddressLineOne":"", "AddressLineTwo":"", "City":"", "State":"", "Zip":"", "Validated":"" }, "Phone":{ "PhoneType":"", "PhoneNumber":"", "Extension":"" } } }, { "DesigneeId":"1108524", "RelationshipDetails":{ "DocumentType":"Consent for PHI", "Relationship":"Parent", "LevelOfConsent":"FullDisclosure", "LineOfCoverage":"", "LimitedDisclosureDetails":"Any and all protected health information Humana maintains including mental health, HIV status, substance abuse records, mail-order pharmacy, wellness products and health programs", "EffectiveDate":"2016-12-31T00:00:00", "TerminationDate":"2018-12-31T00:00:00", "ServiceStartDate":"", "ServiceEndDate":"", "SubmissionMehtod":"Paper", "POA":{ "Type":"", "Description":"" } }, "Demographics":{ "PersonofAuthority":" ", "GuardianAddress":{ "AddressType":"", "AddressLineOne":"", "AddressLineTwo":"", "City":"", "State":"", "Zip":"", "Validated":"" }, "Phone":{ "PhoneType":"", "PhoneNumber":"", "Extension":"" } } }, { "DesigneeId":"1108525", "RelationshipDetails":{ "DocumentType":"Consent for PHI", "Relationship":"Organization", "LevelOfConsent":"FullDisclosure", "LineOfCoverage":"", "LimitedDisclosureDetails":"Any and all protected health information Humana maintains including mental health, HIV status, substance abuse records, mail-order pharmacy, wellness products and health programs", "EffectiveDate":"2017-10-28T00:00:00", "TerminationDate":"2019-10-28T00:00:00", "ServiceStartDate":"", "ServiceEndDate":"", "SubmissionMehtod":"Electronic", "POA":{ "Type":"", "Description":"" } }, "Demographics":{ "PersonofAuthority":" ", "GuardianAddress":{ "AddressType":"", "AddressLineOne":"", "AddressLineTwo":"", "City":"", "State":"", "Zip":"", "Validated":"" }, "Phone":{ "PhoneType":"", "PhoneNumber":"", "Extension":"" } } }, { "DesigneeId":"1108526", "RelationshipDetails":{ "DocumentType":"Power of Attorney", "Relationship":"Organization", "LevelOfConsent":"", "LineOfCoverage":"", "LimitedDisclosureDetails":"", "EffectiveDate":"2018-02-08T00:00:00", "TerminationDate":"9999-12-31T00:00:00", "ServiceStartDate":"", "ServiceEndDate":"", "SubmissionMehtod":"Paper", "POA":{ "Type":"", "Description":"" } }, "Demographics":{ "PersonofAuthority":" ", "GuardianAddress":{ "AddressType":"", "AddressLineOne":"", "AddressLineTwo":"", "City":"", "State":"", "Zip":"", "Validated":"" }, "Phone":{ "PhoneType":"", "PhoneNumber":"", "Extension":"" } } }, { "DesigneeId":"1108527", "RelationshipDetails":{ "DocumentType":"Medical Child Support Order", "Relationship":"None", "LevelOfConsent":"", "LineOfCoverage":"", "LimitedDisclosureDetails":"", "EffectiveDate":"2017-10-20T00:00:00", "TerminationDate":"9999-12-31T00:00:00", "ServiceStartDate":"", "ServiceEndDate":"", "SubmissionMehtod":"Paper", "POA":{ "Type":"", "Description":"" } }, "Demographics":{ "PersonofAuthority":" ", "GuardianAddress":{ "AddressType":"", "AddressLineOne":"", "AddressLineTwo":"", "City":"", "State":"", "Zip":"", "Validated":"" }, "Phone":{ "PhoneType":"", "PhoneNumber":"", "Extension":"" } } }, { "DesigneeId":"1108528", "RelationshipDetails":{ "DocumentType":"Executor of Estate", "Relationship":"Organization", "LevelOfConsent":"", "LineOfCoverage":"", "LimitedDisclosureDetails":"", "EffectiveDate":"2017-10-28T00:00:00", "TerminationDate":"9999-12-31T00:00:00", "ServiceStartDate":"", "ServiceEndDate":"", "SubmissionMehtod":"Paper", "POA":{ "Type":"", "Description":"" } }, "Demographics":{ "PersonofAuthority":" ", "GuardianAddress":{ "AddressType":"", "AddressLineOne":"", "AddressLineTwo":"", "City":"", "State":"", "Zip":"", "Validated":"" }, "Phone":{ "PhoneType":"", "PhoneNumber":"", "Extension":"" } } }, { "DesigneeId":"1108529", "RelationshipDetails":{ "DocumentType":"Legan Guardian", "Relationship":"Organization", "LevelOfConsent":"", "LineOfCoverage":"", "LimitedDisclosureDetails":"", "EffectiveDate":"2017-10-29T00:00:00", "TerminationDate":"9999-12-31T00:00:00", "ServiceStartDate":"", "ServiceEndDate":"", "SubmissionMehtod":"Paper", "POA":{ "Type":"", "Description":"" } }, "Demographics":{ "PersonofAuthority":" ", "GuardianAddress":{ "AddressType":"", "AddressLineOne":"", "AddressLineTwo":"", "City":"", "State":"", "Zip":"", "Validated":"" }, "Phone":{ "PhoneType":"", "PhoneNumber":"", "Extension":"" } } }, { "DesigneeId":"1108530", "RelationshipDetails":{ "DocumentType":"Consent for PHI", "Relationship":"Parent", "LevelOfConsent":"FullDisclosure", "LineOfCoverage":"", "LimitedDisclosureDetails":"Any and all protected health information Humana maintains including mental health, HIV status, substance abuse records, mail-order pharmacy, wellness products and health programs", "EffectiveDate":"2017-10-30T00:00:00", "TerminationDate":"2019-10-30T00:00:00", "ServiceStartDate":"", "ServiceEndDate":"", "SubmissionMehtod":"Electronic", "POA":{ "Type":"", "Description":"" } }, "Demographics":{ "PersonofAuthority":" ", "GuardianAddress":{ "AddressType":"", "AddressLineOne":"", "AddressLineTwo":"", "City":"", "State":"", "Zip":"", "Validated":"" }, "Phone":{ "PhoneType":"", "PhoneNumber":"", "Extension":"" } } }, { "DesigneeId":"1108531", "RelationshipDetails":{ "DocumentType":"Medical Child Support Order", "Relationship":"None", "LevelOfConsent":"", "LineOfCoverage":"", "LimitedDisclosureDetails":"", "EffectiveDate":"2018-03-08T00:00:00", "TerminationDate":"9999-12-31T00:00:00", "ServiceStartDate":"", "ServiceEndDate":"", "SubmissionMehtod":"Paper", "POA":{ "Type":"", "Description":"" } }, "Demographics":{ "PersonofAuthority":" ", "GuardianAddress":{ "AddressType":"", "AddressLineOne":"55444 state st", "AddressLineTwo":"", "City":"Spring field", "State":"Illiois", "Zip":"48512", "Validated":"" }, "Phone":{ "PhoneType":"", "PhoneNumber":"", "Extension":"" } } }, { "DesigneeId":"1108532", "RelationshipDetails":{ "DocumentType":"Consent for PHI", "Relationship":"Child", "LevelOfConsent":"FullDisclosure", "LineOfCoverage":"", "LimitedDisclosureDetails":"Any and all protected health information Humana maintains including mental health, HIV status, substance abuse records, mail-order pharmacy, wellness products and health programs", "EffectiveDate":"2017-11-16T00:00:00", "TerminationDate":"2019-11-16T00:00:00", "ServiceStartDate":"", "ServiceEndDate":"", "SubmissionMehtod":"Paper", "POA":{ "Type":"", "Description":"" } }, "Demographics":{ "PersonofAuthority":" ", "GuardianAddress":{ "AddressType":"", "AddressLineOne":"", "AddressLineTwo":"", "City":"", "State":"", "Zip":"", "Validated":"" }, "Phone":{ "PhoneType":"", "PhoneNumber":"", "Extension":"" } } }, { "DesigneeId":"1108533", "RelationshipDetails":{ "DocumentType":"Consent for PHI", "Relationship":"Sibling", "LevelOfConsent":"FullDisclosure", "LineOfCoverage":"", "LimitedDisclosureDetails":"Any and all protected health information Humana maintains including mental health, HIV status, substance abuse records, mail-order pharmacy, wellness products and health programs", "EffectiveDate":"2018-08-16T00:00:00", "TerminationDate":"2020-08-16T00:00:00", "ServiceStartDate":"", "ServiceEndDate":"", "SubmissionMehtod":"Paper", "POA":{ "Type":"", "Description":"" } }, "Demographics":{ "PersonofAuthority":" ", "GuardianAddress":{ "AddressType":"", "AddressLineOne":"", "AddressLineTwo":"", "City":"", "State":"", "Zip":"", "Validated":"" }, "Phone":{ "PhoneType":"", "PhoneNumber":"", "Extension":"" } } }, { "DesigneeId":"1108534", "RelationshipDetails":{ "DocumentType":"Medical Child Support Order", "Relationship":"None", "LevelOfConsent":"", "LineOfCoverage":"", "LimitedDisclosureDetails":"", "EffectiveDate":"2018-03-01T00:00:00", "TerminationDate":"9999-12-31T00:00:00", "ServiceStartDate":"", "ServiceEndDate":"", "SubmissionMehtod":"Paper", "POA":{ "Type":"", "Description":"" } }, "Demographics":{ "PersonofAuthority":" ", "GuardianAddress":{ "AddressType":"", "AddressLineOne":"", "AddressLineTwo":"", "City":"", "State":"", "Zip":"", "Validated":"" }, "Phone":{ "PhoneType":"", "PhoneNumber":"", "Extension":"" } } }, { "DesigneeId":"1108535", "RelationshipDetails":{ "DocumentType":"Legan Guardian", "Relationship":"None", "LevelOfConsent":"", "LineOfCoverage":"", "LimitedDisclosureDetails":"", "EffectiveDate":"2017-10-03T00:00:00", "TerminationDate":"9999-12-31T00:00:00", "ServiceStartDate":"", "ServiceEndDate":"", "SubmissionMehtod":"Paper", "POA":{ "Type":"", "Description":"" } }, "Demographics":{ "PersonofAuthority":" ", "GuardianAddress":{ "AddressType":"", "AddressLineOne":"", "AddressLineTwo":"", "City":"", "State":"", "Zip":"", "Validated":"" }, "Phone":{ "PhoneType":"", "PhoneNumber":"", "Extension":"" } } }, { "DesigneeId":"1108536", "RelationshipDetails":{ "DocumentType":"Medical Child Support Order", "Relationship":"None", "LevelOfConsent":"", "LineOfCoverage":"", "LimitedDisclosureDetails":"", "EffectiveDate":"2018-03-15T00:00:00", "TerminationDate":"9999-12-31T00:00:00", "ServiceStartDate":"", "ServiceEndDate":"", "SubmissionMehtod":"Paper", "POA":{ "Type":"", "Description":"" } }, "Demographics":{ "PersonofAuthority":" ", "GuardianAddress":{ "AddressType":"", "AddressLineOne":"", "AddressLineTwo":"", "City":"", "State":"", "Zip":"", "Validated":"" }, "Phone":{ "PhoneType":"", "PhoneNumber":"", "Extension":"" } } }, { "DesigneeId":"1108537", "RelationshipDetails":{ "DocumentType":"Consent for PHI", "Relationship":"Child", "LevelOfConsent":"FullDisclosure", "LineOfCoverage":"", "LimitedDisclosureDetails":"Any and all protected health information Humana maintains including mental health, HIV status, substance abuse records, mail-order pharmacy, wellness products and health programs", "EffectiveDate":"2018-03-02T00:00:00", "TerminationDate":"2020-03-02T00:00:00", "ServiceStartDate":"", "ServiceEndDate":"", "SubmissionMehtod":"Paper", "POA":{ "Type":"", "Description":"" } }, "Demographics":{ "PersonofAuthority":" ", "GuardianAddress":{ "AddressType":"", "AddressLineOne":"", "AddressLineTwo":"", "City":"", "State":"", "Zip":"", "Validated":"" }, "Phone":{ "PhoneType":"", "PhoneNumber":"", "Extension":"" } } }, { "DesigneeId":"1108538", "RelationshipDetails":{ "DocumentType":"Medical Child Support Order", "Relationship":"None", "LevelOfConsent":"", "LineOfCoverage":"", "LimitedDisclosureDetails":"", "EffectiveDate":"2017-10-19T00:00:00", "TerminationDate":"9999-12-31T00:00:00", "ServiceStartDate":"", "ServiceEndDate":"", "SubmissionMehtod":"Paper", "POA":{ "Type":"", "Description":"" } }, "Demographics":{ "PersonofAuthority":" ", "GuardianAddress":{ "AddressType":"", "AddressLineOne":"", "AddressLineTwo":"", "City":"", "State":"", "Zip":"", "Validated":"" }, "Phone":{ "PhoneType":"", "PhoneNumber":"", "Extension":"" } } }, { "RelationshipDetails":{ "DocumentType":"Power of Attorney", "POA":{ "Type":"O", "Description":"" } }, "Demographics":{ "PersonofAuthority":"CONTACT HISTORY POA ON FILE SEE ", "Phone":{ "PhoneType":"Primary", "PhoneNumber":"999-999-9999" } } } ] } }';      
          httpRes.setBody(resBodyString);
          system.assert(httpRes.getBody() == resBodyString);         
          string reqType = '';
          List<string>labelGetmember = new List<string>();
          labelGetmember.add(reqType);
          Test.setContinuationResponse(reqType,httpRes);
          UserAssociatedInformation_C_HUM.getPHIResponse(labelGetmember,continueObj);        
          Test.stopTest();
        }
     }
    
    
     static testMethod void testNegativePHIConsent()
    {
         User testUser = [Select Id, LastName from User where LastName='test User1']; 
         System.assertEquals(testUser != null,true); 
         Account acc = [select Id,Enterprise_ID__c,Group_Number__c from account where LastName = 'LName' limit 1];
         System.assertEquals(acc != null,true);
         List<HUM_Webservice_Callout__c > lstWebCallout = [Select id from HUM_Webservice_Callout__c];
         Account accObj = new Account();
         accObj.Name = 'Test Account';
         insert accObj;
         string sAccountId = accObj.Id; 
         System.runAs(testUser)
         {
          Test.startTest();
          //UserAssociatedInformation_CMP_HUM - component 
          UserAssociatedInformation_C_HUM objUser = new UserAssociatedInformation_C_HUM();
          delete lstWebCallout;
          Continuation continueObj = (Continuation)UserAssociatedInformation_C_HUM.getPHIConsentInfo(sAccountId);
          HttpResponse httpRes = new Httpresponse ();
          String resBodyString = '{ "GetPHIConsentResponse":{ "ResponseStatus":{ "Success":"true", "Error":"" }, "DesigneeList":[ { "DesigneeId":"1122024", "RelationshipDetails":{ "DocumentType":"Power of Attorney", "Relationship":"Parent", "LevelOfConsent":"LimitedDisclosure", "LineOfCoverage":"", "LimitedDisclosureDetails":"", "EffectiveDate":"2019-09-13T00:00:00", "TerminationDate":"2020-06-30T00:00:00", "ServiceStartDate":"", "ServiceEndDate":"", "SubmissionMehtod":"Paper", "POA":{ "Type":"C", "Description":"Court-appointed Conservator/Guardian/EoE." } }, "Demographics":{ "PersonofAuthority":{ "FirstName":"Muzafaruddin", "MiddleName":"", "LastName":"Worthan" }, "GuardianAddress":{ "AddressType":"", "AddressLineOne":"", "AddressLineTwo":"", "City":"", "State":"", "Zip":"", "Validated":"" }, "Phone":{ "PhoneType":"", "PhoneNumber":"", "Extension":"" } } }, { "DesigneeId":"1122025", "RelationshipDetails":{ "DocumentType":"Consent for PHI", "Relationship":"Child", "LevelOfConsent":"FullDisclosure", "LineOfCoverage":"", "LimitedDisclosureDetails":"Any and all protected health information Humana maintains including mental health, HIV status, substance abuse records, mail-order pharmacy, wellness products and health programs", "EffectiveDate":"2019-09-13T00:00:00", "TerminationDate":"2020-06-30T00:00:00", "ServiceStartDate":"", "ServiceEndDate":"", "SubmissionMehtod":"Paper", "POA":{ "Type":"", "Description":"" } }, "Demographics":{ "PersonofAuthority":{ "FirstName":"MUZAFARUDDIN", "MiddleName":"", "LastName":"WORTHAN" }, "GuardianAddress":{ "AddressType":"", "AddressLineOne":"", "AddressLineTwo":"", "City":"", "State":"", "Zip":"", "Validated":"" }, "Phone":{ "PhoneType":"", "PhoneNumber":"", "Extension":"" } } }, { "DesigneeId":"1122026", "RelationshipDetails":{ "DocumentType":"Executor of Estate", "Relationship":"Friend", "LevelOfConsent":"", "LineOfCoverage":"", "LimitedDisclosureDetails":"", "EffectiveDate":"2019-09-13T00:00:00", "TerminationDate":"9999-12-31T00:00:00", "ServiceStartDate":"", "ServiceEndDate":"", "SubmissionMehtod":"Paper", "POA":{ "Type":"O", "Description":"Valid for limited decisions." } }, "Demographics":{ "PersonofAuthority":{ "FirstName":"MUZAFARUDDIN", "MiddleName":"", "LastName":"WORTHAN" }, "GuardianAddress":{ "AddressType":"", "AddressLineOne":"", "AddressLineTwo":"", "City":"", "State":"", "Zip":"", "Validated":"" }, "Phone":{ "PhoneType":"", "PhoneNumber":"", "Extension":"" } } }, { "DesigneeId":"1122027", "RelationshipDetails":{ "DocumentType":"Consent for PHI", "Relationship":"Sibling", "LevelOfConsent":"FullDisclosure", "LineOfCoverage":"", "LimitedDisclosureDetails":"Any and all protected health information Humana maintains including mental health, HIV status, substance abuse records, mail-order pharmacy, wellness products and health programs", "EffectiveDate":"2019-09-16T00:00:00", "TerminationDate":"2020-06-30T00:00:00", "ServiceStartDate":"", "ServiceEndDate":"", "SubmissionMehtod":"Paper", "POA":{ "Type":"", "Description":"" } }, "Demographics":{ "PersonofAuthority":{ "FirstName":"Lori", "MiddleName":"J", "LastName":"Darnell" }, "GuardianAddress":{ "AddressType":"", "AddressLineOne":"", "AddressLineTwo":"", "City":"", "State":"", "Zip":"", "Validated":"" }, "Phone":{ "PhoneType":"", "PhoneNumber":"", "Extension":"" } } } ] } }';
          httpRes.setBody(resBodyString);
          system.assert(httpRes.getBody() == resBodyString);         
          string reqType = '';        
          List<string>labelGetmember = new List<string>();
          labelGetmember.add(reqType);
          Test.setContinuationResponse(reqType,httpRes);
          UserAssociatedInformation_C_HUM.getPHIResponse(labelGetmember,continueObj);  
		  DesigneeHubService_Response_DTO objDesigneeHubResp = new DesigneeHubService_Response_DTO();
          objDesigneeHubResp = (DesigneeHubService_Response_DTO)System.JSON.deserialize(resBodyString, DesigneeHubService_Response_DTO.class);
          UserAssociatedInformation_D_HUM.processResponse(objDesigneeHubResp);
          UserInformation_DTO_HUM userInfo ;
          userInfo = new UserInformation_DTO_HUM();
          UserAssociatedInformation_D_HUM.processResponse(objDesigneeHubResp);
          HttpResponse httpRes1 = new Httpresponse ();
          String resBodyString1 = '{ "GetPHIConsentResponse":{ "ResponseStatus":{ "Success":"true", "Error":"" }, "DesigneeList":[ { "DesigneeId":"1122024", "RelationshipDetails":{ "DocumentType":"Power of Attorney", "Relationship":"Parent", "LevelOfConsent":"LimitedDisclosure", "LineOfCoverage":"", "LimitedDisclosureDetails":"", "EffectiveDate":"2019-09-13T00:00:00", "TerminationDate":"2020-06-30T00:00:00", "ServiceStartDate":"2020-06-30T00:00:00", "ServiceEndDate":"2020-06-30T00:00:00", "SubmissionMehtod":"Paper", "POA":{ "Type":"C", "Description":"Court-appointed Conservator/Guardian/EoE." } }, "Demographics":{ "PersonofAuthority":{ "FirstName":"Muzafaruddin", "MiddleName":"", "LastName":"Worthan" }, "GuardianAddress":{ "AddressType":"", "AddressLineOne":"", "AddressLineTwo":"", "City":"", "State":"", "Zip":"", "Validated":"" }, "Phone":{ "PhoneType":"", "PhoneNumber":"", "Extension":"" } } }, { "DesigneeId":"1122025", "RelationshipDetails":{ "DocumentType":"Consent for PHI", "Relationship":"Child", "LevelOfConsent":"FullDisclosure", "LineOfCoverage":"", "LimitedDisclosureDetails":"Any and all protected health information Humana maintains including mental health, HIV status, substance abuse records, mail-order pharmacy, wellness products and health programs", "EffectiveDate":"2019-09-13T00:00:00", "TerminationDate":"2020-06-30T00:00:00", "ServiceStartDate":"2020-06-30T00:00:00", "ServiceEndDate":"2020-06-30T00:00:00", "SubmissionMehtod":"Paper", "POA":{ "Type":"", "Description":"" } }, "Demographics":{ "PersonofAuthority":{ "FirstName":"MUZAFARUDDIN", "MiddleName":"", "LastName":"WORTHAN" }, "GuardianAddress":{ "AddressType":"", "AddressLineOne":"", "AddressLineTwo":"", "City":"", "State":"", "Zip":"", "Validated":"" }, "Phone":{ "PhoneType":"", "PhoneNumber":"", "Extension":"" } } }, { "DesigneeId":"1122026", "RelationshipDetails":{ "DocumentType":"Executor of Estate", "Relationship":"Friend", "LevelOfConsent":"", "LineOfCoverage":"", "LimitedDisclosureDetails":"", "EffectiveDate":"2019-09-13T00:00:00", "TerminationDate":"9999-12-31T00:00:00", "ServiceStartDate":"2020-06-30T00:00:00", "ServiceEndDate":"2020-06-30T00:00:00", "SubmissionMehtod":"Paper", "POA":{ "Type":"O", "Description":"Valid for limited decisions." } }, "Demographics":{ "PersonofAuthority":{ "FirstName":"MUZAFARUDDIN", "MiddleName":"", "LastName":"WORTHAN" }, "GuardianAddress":{ "AddressType":"", "AddressLineOne":"", "AddressLineTwo":"", "City":"", "State":"", "Zip":"", "Validated":"" }, "Phone":{ "PhoneType":"", "PhoneNumber":"", "Extension":"" } } }, { "DesigneeId":"1122027", "RelationshipDetails":{ "DocumentType":"Consent for PHI", "Relationship":"Sibling", "LevelOfConsent":"FullDisclosure", "LineOfCoverage":"", "LimitedDisclosureDetails":"Any and all protected health information Humana maintains including mental health, HIV status, substance abuse records, mail-order pharmacy, wellness products and health programs", "EffectiveDate":"2019-09-16T00:00:00", "TerminationDate":"2020-06-30T00:00:00", "ServiceStartDate":"2020-06-30T00:00:00", "ServiceEndDate":"2020-06-30T00:00:00", "SubmissionMehtod":"Paper", "POA":{ "Type":"", "Description":"" } }, "Demographics":{ "PersonofAuthority":{ "FirstName":"Lori", "MiddleName":"J", "LastName":"Darnell" }, "GuardianAddress":{ "AddressType":"", "AddressLineOne":"", "AddressLineTwo":"", "City":"", "State":"", "Zip":"", "Validated":"" }, "Phone":{ "PhoneType":"", "PhoneNumber":"", "Extension":"" } } } ] } }';
          httpRes1.setBody(resBodyString1);
          DesigneeHubService_Response_DTO objDesigneeHubResp1 = new DesigneeHubService_Response_DTO();
          objDesigneeHubResp1 = (DesigneeHubService_Response_DTO)System.JSON.deserialize(resBodyString1, DesigneeHubService_Response_DTO.class);
          UserAssociatedInformation_D_HUM.processResponse(objDesigneeHubResp1);		  
          Test.stopTest();
        }
     }

}
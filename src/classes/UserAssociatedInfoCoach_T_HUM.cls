/*******************************************************************************************************************************
Apex Class Name : UserAssociatedInfoCoach_T_HUM 
Version         : 1.0
Created On      : 11/06/2020
Function        : Test Class for UserAssociatedInfoCoach_C_HUM

Modification Log: 
* Version Number             Developer Name              Code Review                Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
*     1.0                    Prudhvi Pamarthi                                       05/22/2021                 Original version
******************************************************************************************************************************************************************************/

@isTest public with sharing class UserAssociatedInfoCoach_T_HUM { 

    /* 
* Method Name : setUpTestData 
* Description : This method creates test data For test Class 
* Return type : None 
* Parameter : None */ 
private static Account oAcc;
private static Account objAcc;
private static Account deceasedAccount;

@testSetup static void setUpTestData() { 
    User objUser = [Select Id,Name from User LIMIT 1]; 
    HUM_Webservice_Callout__c serviceDetails = new HUM_Webservice_Callout__c(Name = 'HUMGetPHIConsentInfo',Certificate_Name__c = 'Test_Certificate' ,Active__c = true,End_Point_URL__c = 'https://dev-crmservicews.humana.com/DesigneeHubService/GetPHIConsentInfo' ,Timeout__c = '12000',Request_Type__c = 'POST',Content_Type__c ='application/json' );
    
    insert serviceDetails;
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
        
        Account objMemAccount1 = HUMTestDataHelperTest.getAccount(); 
        objMemAccount1.FirstName = 'Test FName'; 
        objMemAccount1.LastName = 'LName1'; 
        objMemAccount1.Individual_First_Name__c = 'Test FName'; 
        objMemAccount1.Individual_Last_Name__c = 'LName'; 
        objMemAccount1.Enterprise_ID__C = '';
        objMemAccount1.Account_Security_EndDate__c = Date.today();
        objMemAccount1.Account_Security_Access__c = 'Test';
        objMemAccount1.Account_Security_Answer__c='Testing';
        objMemAccount1.Account_Security_Question__c='What is u r name';
        objMemAccount1.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId(); 
        insert objMemAccount1; 
        
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
             
            UserAssociatedInfoCoach_DTO_HUM userAssociatedDTO = new UserAssociatedInfoCoach_DTO_HUM();
            userAssociatedDTO.LineOfCoverage='';
            userAssociatedDTO.ServiceStartDate='';
            userAssociatedDTO.ServiceEndDate='';
            userAssociatedDTO.LimitedDisclosureDetails='';

            
     }     
} 
/* 
* This method Is used for overall data setup and constructor. 
* @param NA 
* @return void */ 
static testMethod void overallTestMethod() { 

    User testUser = [Select Id, LastName from User limit 1]; 
    System.assertEquals(testUser != null,true); 
    System.runAs(testUser) { 
        Test.startTest(); 
           Account acc = [select Id,Group_Number__c from account where LastName = 'LName' limit 1];
           Account deceasedAcc = [select Id,Deceased_Date__c from account where Name = 'testacc2' limit 1];
        Account acc_One = [select Id,Enterprise_ID__c,Account_Security_EndDate__c,Account_Security_Access__c,Account_Security_Question__c,Account_Security_Answer__c, Account_Security_EffectiveDate__c from account where LastName = 'LName1' limit 1];

           UserAssociatedInfoCoach_C_HUM.getUserInformationDTO(acc.Id);
           UserAssociatedInfoCoach_C_HUM.getUserInformationDTO(deceasedAcc.Id);

        String popDetails= '{"password":"sadfsa","question":"sadfasd","answer":"sadfsd","effectiveDate":"2021-03-19","terminatedDate":"2023-03-19"}';
        UserAssociatedInfoCoach_D_HUM.performPopupPasswordDetailsUpdate(popDetails,acc_One.Id);
        UserAssociatedInfoCoach_D_HUM.getPopupOperationValue(acc_One.Id);
           String accID = acc.Id; 
           System.assertEquals(acc != null,true);
           

           Test.stopTest(); 
    } 
} 


  static testMethod void testPositivePHIConsent()
    {
         User testUser = [Select Id, LastName from User limit 1]; 
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
          
          
          Continuation continueObj = (Continuation)UserAssociatedInfoCoach_C_HUM.callServicePHIConsentInfo(sAccountId);
          HttpResponse httpRes = new Httpresponse();
          String resBodyString = '{ "GetPHIConsentResponse":{ "ResponseStatus":{ "Success":"true", "Error":"" }, "DesigneeList":[ { "DesigneeId":"1122024", "RelationshipDetails":{ "DocumentType":"Power of Attorney", "Relationship":"Parent", "LevelOfConsent":"LimitedDisclosure", "LineOfCoverage":"", "LimitedDisclosureDetails":"", "EffectiveDate":"2019-09-13T00:00:00", "TerminationDate":"2020-06-30T00:00:00", "ServiceStartDate":"", "ServiceEndDate":"", "SubmissionMehtod":"Paper", "POA":{ "Type":"C", "Description":"Court-appointed Conservator/Guardian/EoE." } }, "Demographics":{ "PersonofAuthority":{ "FirstName":"Muzafaruddin", "MiddleName":"", "LastName":"Worthan" }, "GuardianAddress":{ "AddressType":"", "AddressLineOne":"", "AddressLineTwo":"", "City":"", "State":"", "Zip":"", "Validated":"" }, "Phone":{ "PhoneType":"", "PhoneNumber":"", "Extension":"" } } }, { "DesigneeId":"1122025", "RelationshipDetails":{ "DocumentType":"Consent for PHI", "Relationship":"Child", "LevelOfConsent":"FullDisclosure", "LineOfCoverage":"", "LimitedDisclosureDetails":"Any and all protected health information Humana maintains including mental health, HIV status, substance abuse records, mail-order pharmacy, wellness products and health programs", "EffectiveDate":"2019-09-13T00:00:00", "TerminationDate":"2020-06-30T00:00:00", "ServiceStartDate":"", "ServiceEndDate":"", "SubmissionMehtod":"Paper", "POA":{ "Type":"", "Description":"" } }, "Demographics":{ "PersonofAuthority":{ "FirstName":"MUZAFARUDDIN", "MiddleName":"", "LastName":"WORTHAN" }, "GuardianAddress":{ "AddressType":"", "AddressLineOne":"", "AddressLineTwo":"", "City":"", "State":"", "Zip":"", "Validated":"" }, "Phone":{ "PhoneType":"", "PhoneNumber":"", "Extension":"" } } }, { "DesigneeId":"1122026", "RelationshipDetails":{ "DocumentType":"Executor of Estate", "Relationship":"Friend", "LevelOfConsent":"", "LineOfCoverage":"", "LimitedDisclosureDetails":"", "EffectiveDate":"2019-09-13T00:00:00", "TerminationDate":"9999-12-31T00:00:00", "ServiceStartDate":"", "ServiceEndDate":"", "SubmissionMehtod":"Paper", "POA":{ "Type":"O", "Description":"Valid for limited decisions." } }, "Demographics":{ "PersonofAuthority":{ "FirstName":"MUZAFARUDDIN", "MiddleName":"", "LastName":"WORTHAN" }, "GuardianAddress":{ "AddressType":"", "AddressLineOne":"", "AddressLineTwo":"", "City":"", "State":"", "Zip":"", "Validated":"" }, "Phone":{ "PhoneType":"", "PhoneNumber":"", "Extension":"" } } }, { "DesigneeId":"1122027", "RelationshipDetails":{ "DocumentType":"Consent for PHI", "Relationship":"Sibling", "LevelOfConsent":"FullDisclosure", "LineOfCoverage":"", "LimitedDisclosureDetails":"Any and all protected health information Humana maintains including mental health, HIV status, substance abuse records, mail-order pharmacy, wellness products and health programs", "EffectiveDate":"2019-09-16T00:00:00", "TerminationDate":"2020-06-30T00:00:00", "ServiceStartDate":"", "ServiceEndDate":"", "SubmissionMehtod":"Paper", "POA":{ "Type":"", "Description":"" } }, "Demographics":{ "PersonofAuthority":{ "FirstName":"Lori", "MiddleName":"J", "LastName":"Darnell" }, "GuardianAddress":{ "AddressType":"", "AddressLineOne":"", "AddressLineTwo":"", "City":"", "State":"", "Zip":"", "Validated":"" }, "Phone":{ "PhoneType":"", "PhoneNumber":"", "Extension":"" } } }, { "DesigneeId":"1122024", "RelationshipDetails":{ "DocumentType":"Consent for PHI", "Relationship":"Sibling", "LevelOfConsent":"LimitedDisclosure", "LineOfCoverage":"", "LimitedDisclosureDetails":"Any and all protected health information Humana maintains including mental health, HIV status, substance abuse records, mail-order pharmacy, wellness products and health programs", "EffectiveDate":"2019-09-16T00:00:00", "TerminationDate":"2020-06-30T00:00:00", "ServiceStartDate":"", "ServiceEndDate":"", "SubmissionMehtod":"Paper", "POA":{ "Type":"", "Description":"" } }, "Demographics":{ "PersonofAuthority":{ "FirstName":"Lori", "MiddleName":"J", "LastName":"Darnell" }, "GuardianAddress":{ "AddressType":"", "AddressLineOne":"", "AddressLineTwo":"", "City":"", "State":"", "Zip":"", "Validated":"" }, "Phone":{ "PhoneType":"", "PhoneNumber":"", "Extension":"" } } } ] } }';
          httpRes.setBody(resBodyString);
          system.assert(httpRes.getBody() == resBodyString);         
          string reqType = 'Continuation-1';        
          List<string>labelGetmember = new List<string>();
          labelGetmember.add(reqType);
          Test.setContinuationResponse(reqType,httpRes);
          
          UserAssociatedInfoCoach_C_HUM.getPHIResponse(labelGetmember, '');
             
          Test.stopTest();
        }
     }
    
    
    static testMethod void testNegativePHIConsent()
    {
         User testUser = [Select Id, LastName from User limit 1]; 
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
          // passing blank to cover catch block
          Continuation continueObj = (Continuation)UserAssociatedInfoCoach_C_HUM.callServicePHIConsentInfo('');
          HttpResponse httpRes = new Httpresponse();
          String resBodyString = '';
          httpRes.setBody(resBodyString);
          system.assert(httpRes.getBody() == resBodyString);         
          string reqType = 'Continuation-1';        
          List<string>labelGetmember = new List<string>();
          labelGetmember.add(reqType);
          Test.setContinuationResponse(reqType,httpRes);
          
          UserAssociatedInfoCoach_C_HUM.getPHIResponse(labelGetmember, '');
              
          Test.stopTest();
        }
     }  
    static testMethod void testCatchBlock(){
        Test.startTest();
        String popDetails= '{"password":"sadfsa","question":"sadfasd","answer":"sadfsd","effectiveDate":"2021-03-19","terminatedDate":"2023-03-19"}';
        UserAssociatedInfoCoach_D_HUM.performPopupPasswordDetailsUpdate(popDetails,'testAccountId');
        Test.stopTest();
    }
}
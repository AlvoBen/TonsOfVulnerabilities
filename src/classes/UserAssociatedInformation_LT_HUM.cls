/*******************************************************************************************************************************
Apex Class Name : UserAssociatedInformation_LT_HUM 
Version         : 1.0
Created On      : 11/06/2020
Function        : Test Class for UserAssociatedInformation_LC_HUM

Modification Log: 
* Version Number             Developer Name              Code Review                Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* 1.0                        Rahul Krishan                                          09/28/2020                                                                        Original Version
* 2.0						 Ashish Kumar											02/08/2021
* 3.0 						 Ashish Kumar											03/19/2021					Added method covering popup related method
* 4.0 						 Kajal Namdev											05/06/2021					Added method covering catch block
* 5.0 						 Kajal Namdev											05/18/2021					Removed references of policy_member, member_id, policy objects
* 6.0 						 Joel George											11/10/2021					Updated for coverage
******************************************************************************************************************************************************************************/

@isTest private class UserAssociatedInformation_LT_HUM{ 

    /* 
* Method Name : setUpTestData 
* Description : This method creates test data For test Class 
* Return type : None 
* Parameter : None */ 
private static Account oAcc;
private static Account objAcc;
private static Account objblankAcc;
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
            
             objblankAcc = HUMTestDataHelperTest.getAccount();
            objblankAcc.Name = 'testblankacc';
            objblankAcc.Group_Number__c = '213654';
            //objblankAcc.Enterprise_ID__C = '1078620930';
            insert objblankAcc;
                
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
            UserAssociatedInformation_DTO_HUM userAssociatedDTO = new UserAssociatedInformation_DTO_HUM();
            userAssociatedDTO.LineOfCoverage='';
            userAssociatedDTO.ServiceStartDate='';
            userAssociatedDTO.ServiceEndDate='';
            userAssociatedDTO.LimitedDisclosureDetails='';
            
             Account objMemAcount = HUMTestDataHelperTest.getAccount();
            objMemAcount.FirstName = 'Test FName';
            objMemAcount.LastName = 'aLName'; 
            objMemAcount.source_platform_code__c = 'EM' ;
            objMemAcount.Enterprise_Id__c = '1017531585';
            objMemAcount.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId();
            objMemAcount.Birthdate__c = String.valueOf(System.today().month()+'/'+system.today().day()+'/'+System.today().year());
            insert objMemAcount;
            system.assertEquals(objMemAcount.LastName == 'aLName', true);
            
            PurchaserPlan PolicyObj = new PurchaserPlan();
            PolicyObj.Policy_Group_Number__c='11111';
            PolicyObj.Name = 'Dental Product';
            PolicyObj.source_cust_cov_key__c = '12345678901234567890' ; 
            PolicyObj.PayerId = objMemAcount.Id;
            PolicyObj.Major_LOB__c ='MCD';
            PolicyObj.ETL_Record_Deleted__c = false;
            //PolicyObj.Product__c = oProd.Id;
            PolicyObj.Platform__c='LV';
            insert PolicyObj;
                        
            MemberPlan MemberPlanObj = new MemberPlan();
            MemberPlanObj.Name='MemberPlan2';
            MemberPlanObj.MemberId=objMemAcount.id;
            MemberPlanObj.Member_Dependent_Code__c = '1234567890';
            MemberPlanObj.Member_Id_base__c = 'test23456';
            MemberPlanObj.GroupNumber = '737803';
            MemberPlanObj.PlanId= PolicyObj.Id;
            insert MemberPlanObj;
            
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
         Account acc1 = [select Id,Group_Number__c from account where LastName = 'aLName' limit 1];
        Account deceasedAcc = [select Id,Deceased_Date__c from account where Name = 'testacc2' limit 1];
        Account acc_One = [select Id,Enterprise_ID__c,Account_Security_EndDate__c,Account_Security_Access__c,Account_Security_Question__c,Account_Security_Answer__c, Account_Security_EffectiveDate__c from account where LastName = 'LName1' limit 1];
        
        UserAssociatedInformation_LC_HUM.getUserInformationDTO(acc.Id);
        UserAssociatedInformation_LC_HUM.getUserInformationDTO(deceasedAcc.Id);
	
        String popDetails= '{"password":"sadfsa","question":"sadfasd","answer":"sadfsd","effectiveDate":"2021-03-19","terminatedDate":"2023-03-19"}';
        UserAssociatedInformation_LD_HUM.performPopupDetailsUpdate(popDetails,acc_One.Id);
        UserAssociatedInformation_LD_HUM.getPopupOperationValue(acc_One.Id);
        String accID = acc.Id; 
        System.assertEquals(acc != null,true);
        DesigneeHubService_Request_DTO oRequest = new DesigneeHubService_Request_DTO();
        accID = acc1.Id;
        oRequest = UserAssociatedInformation_LS_HUM.RequestStructureforDesignee(accID,'12345');
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
          Account objAcc1 = [select id, Enterprise_ID__C from Account where Name =: 'testblankacc' limit 1]; 
         string sAccountId1 = objAcc1.id;
         System.runAs(testUser)
         {
          Test.startTest();
          
          
          Continuation continueObj = (Continuation)UserAssociatedInformation_LC_HUM.callServicePHIConsentInfo(sAccountId);
          HttpResponse httpRes = new Httpresponse();
          String resBodyString = '{ "GetPHIConsentResponse":{ "ResponseStatus":{ "Success":"true", "Error":"" }, "DesigneeList":[ { "DesigneeId":"1122024", "RelationshipDetails":{ "DocumentType":"Power of Attorney", "Relationship":"Parent", "LevelOfConsent":"LimitedDisclosure", "LineOfCoverage":"", "LimitedDisclosureDetails":"", "EffectiveDate":"2019-09-13T00:00:00", "TerminationDate":"2020-06-30T00:00:00", "ServiceStartDate":"", "ServiceEndDate":"", "SubmissionMehtod":"Paper", "POA":{ "Type":"C", "Description":"Court-appointed Conservator/Guardian/EoE." } }, "Demographics":{ "PersonofAuthority":{ "FirstName":"Muzafaruddin", "MiddleName":"", "LastName":"Worthan" }, "GuardianAddress":{ "AddressType":"", "AddressLineOne":"", "AddressLineTwo":"", "City":"", "State":"", "Zip":"", "Validated":"" }, "Phone":{ "PhoneType":"", "PhoneNumber":"", "Extension":"" } } }, { "DesigneeId":"1122025", "RelationshipDetails":{ "DocumentType":"Consent for PHI", "Relationship":"Child", "LevelOfConsent":"FullDisclosure", "LineOfCoverage":"", "LimitedDisclosureDetails":"Any and all protected health information Humana maintains including mental health, HIV status, substance abuse records, mail-order pharmacy, wellness products and health programs", "EffectiveDate":"2019-09-13T00:00:00", "TerminationDate":"2020-06-30T00:00:00", "ServiceStartDate":"", "ServiceEndDate":"", "SubmissionMehtod":"Paper", "POA":{ "Type":"", "Description":"" } }, "Demographics":{ "PersonofAuthority":{ "FirstName":"MUZAFARUDDIN", "MiddleName":"", "LastName":"WORTHAN" }, "GuardianAddress":{ "AddressType":"", "AddressLineOne":"", "AddressLineTwo":"", "City":"", "State":"", "Zip":"", "Validated":"" }, "Phone":{ "PhoneType":"", "PhoneNumber":"", "Extension":"" } } }, { "DesigneeId":"1122026", "RelationshipDetails":{ "DocumentType":"Executor of Estate", "Relationship":"Friend", "LevelOfConsent":"", "LineOfCoverage":"", "LimitedDisclosureDetails":"", "EffectiveDate":"2019-09-13T00:00:00", "TerminationDate":"9999-12-31T00:00:00", "ServiceStartDate":"", "ServiceEndDate":"", "SubmissionMehtod":"Paper", "POA":{ "Type":"O", "Description":"Valid for limited decisions." } }, "Demographics":{ "PersonofAuthority":{ "FirstName":"MUZAFARUDDIN", "MiddleName":"", "LastName":"WORTHAN" }, "GuardianAddress":{ "AddressType":"", "AddressLineOne":"", "AddressLineTwo":"", "City":"", "State":"", "Zip":"", "Validated":"" }, "Phone":{ "PhoneType":"", "PhoneNumber":"", "Extension":"" } } }, { "DesigneeId":"1122027", "RelationshipDetails":{ "DocumentType":"Consent for PHI", "Relationship":"Sibling", "LevelOfConsent":"FullDisclosure", "LineOfCoverage":"", "LimitedDisclosureDetails":"Any and all protected health information Humana maintains including mental health, HIV status, substance abuse records, mail-order pharmacy, wellness products and health programs", "EffectiveDate":"2019-09-16T00:00:00", "TerminationDate":"2020-06-30T00:00:00", "ServiceStartDate":"", "ServiceEndDate":"", "SubmissionMehtod":"Paper", "POA":{ "Type":"", "Description":"" } }, "Demographics":{ "PersonofAuthority":{ "FirstName":"Lori", "MiddleName":"J", "LastName":"Darnell" }, "GuardianAddress":{ "AddressType":"", "AddressLineOne":"", "AddressLineTwo":"", "City":"", "State":"", "Zip":"", "Validated":"" }, "Phone":{ "PhoneType":"", "PhoneNumber":"", "Extension":"" } } }, { "DesigneeId":"1122024", "RelationshipDetails":{ "DocumentType":"Consent for PHI", "Relationship":"Sibling", "LevelOfConsent":"LimitedDisclosure", "LineOfCoverage":"", "LimitedDisclosureDetails":"Any and all protected health information Humana maintains including mental health, HIV status, substance abuse records, mail-order pharmacy, wellness products and health programs", "EffectiveDate":"2019-09-16T00:00:00", "TerminationDate":"2020-06-30T00:00:00", "ServiceStartDate":"", "ServiceEndDate":"", "SubmissionMehtod":"Paper", "POA":{ "Type":"", "Description":"" } }, "Demographics":{ "PersonofAuthority":{ "FirstName":"Lori", "MiddleName":"J", "LastName":"Darnell" }, "GuardianAddress":{ "AddressType":"", "AddressLineOne":"", "AddressLineTwo":"", "City":"", "State":"", "Zip":"", "Validated":"" }, "Phone":{ "PhoneType":"", "PhoneNumber":"", "Extension":"" } } } ] } }';
          httpRes.setBody(resBodyString);
          system.assert(httpRes.getBody() == resBodyString);         
          string reqType = 'Continuation-1';        
          List<string>labelGetmember = new List<string>();
          labelGetmember.add(reqType);
          Test.setContinuationResponse(reqType,httpRes);
          
          UserAssociatedInformation_LC_HUM.getPHIResponse(labelGetmember, '');
          Continuation con = UserAssociatedInformation_LS_HUM.callServicePHIConsentInfo(sAccountId1);
             
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
          Continuation continueObj = (Continuation)UserAssociatedInformation_LC_HUM.callServicePHIConsentInfo('');
          HttpResponse httpRes = new Httpresponse();
          String resBodyString = '';
          httpRes.setBody(resBodyString);
          system.assert(httpRes.getBody() == resBodyString);         
          string reqType = 'Continuation-1';        
          List<string>labelGetmember = new List<string>();
          labelGetmember.add(reqType);
          Test.setContinuationResponse(reqType,httpRes);
          
          UserAssociatedInformation_LC_HUM.getPHIResponse(labelGetmember, '');
              
          Test.stopTest();
        }
     }  
	static testMethod void testCatchBlock(){
        Test.startTest();
        String popDetails= '{"password":"sadfsa","question":"sadfasd","answer":"sadfsd","effectiveDate":"2021-03-19","terminatedDate":"2023-03-19"}';
        UserAssociatedInformation_LD_HUM.performPopupDetailsUpdate(popDetails,'testAccountId');
        Test.stopTest();
    }
}
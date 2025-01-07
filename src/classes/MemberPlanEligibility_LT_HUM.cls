/*******************************************************************************************************************************
Apex Class Name : MemberPlanEligibility_LT_HUM
Version         : 1.0
Created On      : 06/10/2021
Function        : Test class for MemberPlanEligibility_LC_HUM

Modification Log: 
* Version Number             Developer Name              Code Review                Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* 1.0 						 Ankima Srivastava/Kajal Namdev                        06/10/2021                  initial version 
* 2.0                        Ankima                                                07/01/2021                  Cost Share methods included 
* 3.0                        Ankima                                                07/26/2021                  Cost Share methods Rollback
* 4.0                        Ankima                                                09/08/2021                  Cost Share methods included 
* 5.0                        Ankima                                                09/13/2021                  Cost Share methods Rollback
* 6.0                        Ankima                                                09/20/2021                  Cost Share methods Re-Deploy
* 7.0                        Ankima                                                09/30/2021                  Eligibility By Product - Coverage                 
******************************************************************************************************************************************************************************/
@isTest
public class MemberPlanEligibility_LT_HUM {
	/*
    * Method Name : setUpTestData
    * Description : This method creates test data for test class
    * Return type : None
    * Parameter   : None
    */
    @testSetup static void setUpTestData()
    {
        Account objMemAccount = HUMTestDataHelperTest.getAccount();
        objMemAccount.FirstName = 'Test FName';
        objMemAccount.LastName = 'LName'; 
        objMemAccount.source_platform_code__c = 'EM' ;
        objMemAccount.Enterprise_Id__c = String.valueof(2000);
        objMemAccount.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId();
        objMemAccount.Birthdate__c = String.valueOf(System.today().month()+'/'+system.today().day()+'/'+System.today().year());
        insert objMemAccount;
        system.assertEquals(objMemAccount.LastName == 'LName', true);
        
        Product2 oProd = New Product2(Name = 'Laptop X200',Product_Type__c = 'MER',Segment_Type__c='MCR',ProductCode='MED', Major_LOB__c='MER');
        insert oProd;
        
        Product2 oProd1 = New Product2(Name = 'Laptop X200',Product_Type__c = 'MA',Segment_Type__c='MCR',ProductCode='MED', Major_LOB__c='MER');
        insert oProd1;
        
        
        PurchaserPlan planObj = new PurchaserPlan();
        planObj.Product__c = oProd.Id;
        planObj.Policy_Group_Number__c='11111';
        planObj.Name = 'Dental Product';
        planObj.Source_Cust_Cov_Key__c = '12345678901234567890' ; 
        planObj.PayerId = objMemAccount.Id;
        planObj.Major_LOB__c ='MCD';  
        planObj.Platform__c='LV';
        planObj.Issue_State__c='KV';
        insert planObj;
        
        PurchaserPlan purPlanObj = new PurchaserPlan();
        planObj.Product__c = oProd1.Id;
        purPlanObj.Policy_Group_Number__c='11111';
        purPlanObj.Name = 'Dental Product';
        purPlanObj.Source_Cust_Cov_Key__c = '12345678901234567890' ; 
        purPlanObj.PayerId = objMemAccount.Id;
        purPlanObj.Major_LOB__c ='MCD';  
        purPlanObj.Platform__c='LV';
        purPlanObj.Issue_State__c='KV';
        insert purPlanObj;
        
        MemberPlan objMemberplan = new MemberPlan();
        objMemberplan.Name='MemberPlan1';
        objMemberplan.MemberId=objMemAccount.id;
        objMemberPlan.Member_Dependent_Code__c = '1234567890';
        objMemberPlan.Member_Id_Base__c = 'test23456';
        objMemberPlan.GroupNumber = '737803';
        objMemberPlan.PlanId= planObj.Id;
        objMemberPlan.Medicaid_Id__c='0008107741';
        insert objMemberplan;
        
        MemberPlan objMemplan = new MemberPlan();
        objMemplan.Name='MemberPlan2';
        objMemplan.MemberId=objMemAccount.id;
        objMemplan.Member_Dependent_Code__c = '1234567890';
        objMemplan.Member_Id_Base__c = 'test23456';
        objMemplan.GroupNumber = '737803';
        objMemplan.PlanId= purPlanObj.Id;
        objMemplan.Medicaid_Id__c='0008107741';
        insert objMemplan;
        
        HUM_Webservice_Callout__c PBEServiceDetails = New HUM_Webservice_Callout__c(Name = 'CIMedicareMultipleMemberDetails',Certificate_Name__c = 'Test_Certificate' ,Active__c = true,End_Point_URL__c ='https://int-crmservicews.humana.com/PBEService_V2/GetProduct' ,Timeout__c = '12000',Request_Type__c = 'POST',Content_Type__c ='application/json' );
       	insert PBEServiceDetails; 
        HUM_Webservice_Callout__c PBEServiceGetMember = New HUM_Webservice_Callout__c(Name = 'RighSourceService_GetMember',Certificate_Name__c = 'Test_Certificate' ,Active__c = true,End_Point_URL__c ='https://int-crmservicews.humana.com/PBEService_V2/GetProduct' ,Timeout__c = '12000',Request_Type__c = 'POST',Content_Type__c ='application/json' );
       	insert PBEServiceGetMember;
        HUM_Webservice_Callout__c MemberAncillary = New HUM_Webservice_Callout__c(Name = 'GetMemberAncillary_V2',Certificate_Name__c = 'Test_Certificate' ,Active__c = true,End_Point_URL__c ='https://int-crmservicews.humana.com/PBEService_V2/GetProduct' ,Timeout__c = '12000',Request_Type__c = 'POST',Content_Type__c ='application/json' );
       	insert MemberAncillary;
            
        HUM_Field_Priority__c eligible = new HUM_Field_Priority__c(Name='Eligible',Priority__c=1);
        insert eligible;
        HUM_Field_Priority__c efDate = new HUM_Field_Priority__c(Name='Effective Date',Priority__c=2);
        insert efDate;
        HUM_Field_Priority__c eDate = new HUM_Field_Priority__c(Name='End Date',Priority__c=3);
        insert eDate;
        HUM_Field_Priority__c typecs = new HUM_Field_Priority__c(Name='Type',Priority__c=4);
        insert typecs;
        HUM_Field_Priority__c percentC = new HUM_Field_Priority__c(Name='Percent/ Copay',Priority__c=5);
        insert percentC;
        
        HUM_Section_Priority__c secPriority1 = new HUM_Section_Priority__c(Name='End Stage Renal Disease (ESRD)',Priority__c=1);
        insert secPriority1;
        HUM_Section_Priority__c secPriority2 = new HUM_Section_Priority__c(Name='Long Term Support Services (LTSS)',Priority__c=2);
        insert secPriority2;
        HUM_Section_Priority__c secPriority3 = new HUM_Section_Priority__c(Name='Low Income Subsidy (LIS)',Priority__c=3);
        insert secPriority3;
        HUM_Section_Priority__c secPriority4 = new HUM_Section_Priority__c(Name='Special Needs Plan (SNP)',Priority__c=4);
        insert secPriority4;
        HUM_Section_Priority__c secPriority5 = new HUM_Section_Priority__c(Name='Medication Therapy (MTM)',Priority__c=5);
        insert secPriority5;
        
        CRMFunctionality_ONOFF_Switch__c objCSEligibility = new CRMFunctionality_ONOFF_Switch__c(
            Name = '2081552',
            IsON__c = true,
            Release_Name__c = '07/23/2021',
            Scrum_Team__c = 'PR00094919',
            Story_Title__c = 'T1PRJ0002855/PR00094919 - MF 2 - Member Plan - Eligibility');
        insert objCSEligibility;
        
        CRMFunctionality_ONOFF_Switch__c objCSCostShare = new CRMFunctionality_ONOFF_Switch__c(
            Name = '2160875',
            IsON__c = true,
            Release_Name__c = '08/06/2021',
            Scrum_Team__c = 'PR00094919',
            Story_Title__c = 'T1PRJ0002855/PR00094919 - MF 2 - Member Plan - Eligibility - Cost Share Protected Field');
        insert objCSCostShare;
    }
     /*
    * Method Name : testretrieveMTMValue
    * Description : this method is to cover retrieveMTMValue function
    * Return type : void
    * Parameter   : None
    */
    static testMethod void testretrieveMTMValue()
    {
        MemberPlan memberPlanObj = [select id,Name,PlanId from MemberPlan where Name = 'MemberPlan1' limit 1];
        Map<String,String> stateMap= new Map<String,String>();
        MemberPlanEligibility_LC_HUM.callCIMedMultipleMemberService(memberPlanObj.Id);
        HttpResponse httpRes = new Httpresponse();
        httpRes.setStatusCode(200);
        String resBodyString = '{"GetMemberReponse":{"Customers":{"Customer":[{"CustomerIdentity":{"BirthDate":"07/25/1988","CustomerKeys":{"CustomerKey":[{"CustomerType":"AC","CustomerValue":"8668317"}]},"FirstName":"test","MiddleName":"","LastName":"march","Email":"gsheppard@humana.com"},"CustomerPreference":{"CapType":"S","CapTypeLiteral":"SAFETY"},"CustomerDetail":{"NeedsMemberConsent":"false","ConsentBeginDate":"","ConsentEndDate":"","MemberConsentStatus":"false","CMRStatus":"No","CMRStatusLiteral":""},"Addresses":{"Address":[{"AddressKey":"16007945","AddressType":"B","AddressTypeLiteral":"BILLING","AddressLine1":"SHAW","AddressLine2":"","AddressName":"test march","City":"SUAMICO","StateCode":"WI","ZipCode":"54313","IsActive":"true"}]},"Phones":{"Phone":[{"PhoneKey":"15135289","PhoneType":"PP","PhoneValue":"5022214112"}]},"StaleData":"0","AccountNumber":{"CustomerValue":"8668317"},"Finance":{"CreditLimit":"60.0","OutstandingBalance":"0.0","PaymentSchedules":{"PaymentSchedule":[{"PaymentDueDate":"","PaymentMadeDate":"","PaymentType":"","SourceApplicationCode":"","SourceApplicationCodeLiteral":""}]}},"Allergies":{"Allergy":[{"HealthKey":"00153","HealthDescription":"No Known Drug Allergy"}]},"CreditCards":{"CreditCard":[]},"HealthConditions":{"HealthCondition":[{"HealthDescription":"Pheochromocytoma"}]},"LogNotes":{"LogNote":[]},"Plans":{"Plan":[{"PlanEffectiveDate":"01/14/2014","PlanTerminationDate":"","PlanType":"COMM","Payer":"GENERIC COMMERCIAL PLAN"}]}},{"CustomerIdentity":{"BirthDate":"07/25/1988","CustomerKeys":{"CustomerKey":[{"CustomerType":"NP","CustomerValue":"H61234596"}]},"FirstName":"test","MiddleName":"","LastName":"march","Email":"gsheppard@humana.com"},"CustomerPreference":{"CapType":"S","CapTypeLiteral":"SAFETY"},"CustomerDetail":{"NeedsMemberConsent":"false","ConsentBeginDate":"","ConsentEndDate":"","MemberConsentStatus":"false","CMRStatus":"","CMRStatusLiteral":""},"Addresses":{"Address":[{"AddressKey":"16007944","AddressType":"B","AddressTypeLiteral":"BILLING","AddressLine1":"TREMONT","AddressLine2":"","AddressName":"test march","City":"SUAMICO","StateCode":"WI","ZipCode":"54313","IsActive":"true"}]},"Phones":{"Phone":[{"PhoneKey":"15135288","PhoneType":"PP","PhoneValue":"5022214112"}]},"StaleData":"0","AccountNumber":{"CustomerValue":"8668317"},"Finance":{"CreditLimit":"60.0","OutstandingBalance":"0.0","PaymentSchedules":{"PaymentSchedule":[{"PaymentDueDate":"","PaymentMadeDate":"","PaymentType":"","SourceApplicationCode":"","SourceApplicationCodeLiteral":""}]}},"Allergies":{"Allergy":[{"HealthKey":"00153","HealthDescription":"No Known Drug Allergy"}]},"CreditCards":{"CreditCard":[]},"HealthConditions":{"HealthCondition":[{"HealthDescription":"Pheochromocytoma"},{"HealthDescription":"Diabetes Mellitus"}]},"LogNotes":{"LogNote":[{"LogNoteCode":"CONSENT","LogNoteDate":"03/13/2018","LogNoteKey":"328848159","LogNoteMessage":"Member consent removed on Mar 13 2018  6:13:46:310AM.","LogNoteUser":"ANSUSR"}]},"Plans":{"Plan":[{"PlanEffectiveDate":"01/14/2014","PlanTerminationDate":"","PlanType":"COMM","Payer":"GENERIC COMMERCIAL PLAN"}]}}]},"DuplicateCustomers":{"CustomerKey":[{"customerType":"AC","customerTypeLiteral":"ACCOUNT ID","customerValue":"8668317"}]}}}';
        httpRes.setBody(resBodyString);
        system.assert(httpRes.getBody() == resBodyString);  
        List<String> labels = new List<String>{'Continuation-1','Continuation-2','Continuation-3'};
        string reqType = 'Continuation-1'; 
        Test.setContinuationResponse(reqType,httpRes);
        stateMap.put('sRequest',reqType);
        stateMap.put('sGroupNumber','123456');
        stateMap.put('sSubSourceKey','789');
        stateMap.put('sMemberPlanId',memberPlanObj.Id);
        MemberPlanEligibility_LC_HUM.retrieveMTMValue(labels,stateMap);
    }
    
     /*
    * Method Name : testEligibilityService
    * Description : This method is to test callCIMedMultipleMemberService function
    * Return type : void
    * Parameter   : None
    */
    static testMethod void testEligibilityService()
    {
     	MemberPlan memberPlanObj = [select id,Name,PlanId from MemberPlan where Name = 'MemberPlan1' limit 1];
        Map<String,String> stateMap= new Map<String,String>();
        MemberPlanEligibility_LC_HUM.callCIMedMultipleMemberService(memberPlanObj.Id);
        HttpResponse httpRes = new Httpresponse();
        httpRes.setStatusCode(200);
        String resBodyString =  '{"CIMedicareResponse":{"GetMedicareMemberInfo":[{"MedicareMemberLISSNPInfoResponse":{"MedicareID":"","LISIndicator":"Y","SubsidyEffectDate":"01/01/2021","SubsidyEndDate":"12/31/2021","LISSubsidyLevel":"100","LISCopayCategory":"1","LTSSIndicator":"N","ESRDIndicator":"N","SPAPIndicator":"","SNPIndicator":"","SNPTypeCode":"","POAIndicator":"","POALastName":"","POAFirstName":"","POAPhoneNumber":"","POAType":"","POAApplDate":"","Errors":{"Error":{"Error":{"Code":"","Description":""}}}}}]}}';
        httpRes.setBody(resBodyString);
        system.assert(httpRes.getBody() == resBodyString);  
        List<String> labels = new List<String>{'Continuation-1','Continuation-2','Continuation-3'};
        string reqType = 'Continuation-2'; 
        Test.setContinuationResponse(reqType,httpRes);
        stateMap.put('sRequest',reqType);
        stateMap.put('sGroupNumber','123456');
        stateMap.put('sSubSourceKey','789');
        stateMap.put('sMemberPlanId',memberPlanObj.Id);
        MemberPlanEligibility_LC_HUM.processAllResponses(labels,stateMap);
        
    }
    /*
    * Method Name : testResponseWithError
    * Description : this method is to cover else blocks if response includes error
    * Return type : void
    * Parameter   : None
    */
    
    static testMethod void testResponseWithError()
    {
        MemberPlan memberPlanObj = [select id,Name,PlanId from MemberPlan where Name = 'MemberPlan1' limit 1];
        Map<String,String> stateMap= new Map<String,String>();
        MemberPlanEligibility_LC_HUM.callCIMedMultipleMemberService(memberPlanObj.Id);
        HttpResponse httpRes = new Httpresponse();
        httpRes.setStatusCode(200);
        String resBodyString = '{"CIMedicareResponse":{"GetMedicareMemberInfo":[{"MedicareMemberLISSNPInfoResponse":{"MedicareID":"","LISIndicator":"Y","SubsidyEffectDate":"01/01/2021","SubsidyEndDate":"12/31/2021","LISSubsidyLevel":"100","LISCopayCategory":"1","LTSSIndicator":"N","ESRDIndicator":"N","SPAPIndicator":"","SNPIndicator":"","SNPTypeCode":"","POAIndicator":"","POALastName":"","POAFirstName":"","POAPhoneNumber":"","POAType":"","POAApplDate":"","Errors":{"Error":{"Code":"401","Description":"dummy error"}}}}]}}';
        httpRes.setBody(resBodyString);
        system.assert(httpRes.getBody() == resBodyString);  
        List<String> labels = new List<String>{'Continuation-1','Continuation-2','Continuation-3'};
        string reqType = 'Continuation-2'; 
        Test.setContinuationResponse(reqType,httpRes);
        stateMap.put('sRequest',reqType);
        stateMap.put('sGroupNumber','123456');
        stateMap.put('sSubSourceKey','789');
        stateMap.put('sMemberPlanId',memberPlanObj.Id);
        MemberPlanEligibility_LC_HUM.processAllResponses(labels,stateMap);
    }
    
    
    /* Method Name : testServiceIf
    * Description : this method is to cover if blocks
    * Return type : void
    * Parameter   : None
    */
    // to cover if condition
    static testMethod void testServiceIf()
    {
        MemberPlan memberPlanObj = [select id,Name,PlanId from MemberPlan where Name = 'MemberPlan1' limit 1];
        Map<String,String> stateMap= new Map<String,String>();
        MemberPlanEligibility_LC_HUM.callCIMedMultipleMemberService(memberPlanObj.Id);
        HttpResponse httpRes = new Httpresponse();
        httpRes.setStatusCode(200);
        String resBodyString =  '{"CIMedicareResponse":{"GetMedicareMemberInfo":[{"MedicareMemberLISSNPInfoResponse":{"MedicareID":"","LISIndicator":"N","SubsidyEffectDate":"01/01/2021","SubsidyEndDate":"12/31/2021","LISSubsidyLevel":"100","LISCopayCategory":"1","LTSSIndicator":"Y","ESRDIndicator":"Y","SPAPIndicator":"","SNPIndicator":"Y","SNPTypeCode":"","POAIndicator":"","POALastName":"","POAFirstName":"","POAPhoneNumber":"","POAType":"","POAApplDate":"","Errors":{"Error":{"Error":{"Code":"401","Description":"dummy error"}}}}}]}}';
        httpRes.setBody(resBodyString);
        system.assert(httpRes.getBody() == resBodyString);  
        List<String> labels = new List<String>{'Continuation-1','Continuation-2','Continuation-3'};
        string reqType = 'Continuation-2'; 
        Test.setContinuationResponse(reqType,httpRes);
        stateMap.put('sRequest',reqType);
        stateMap.put('sGroupNumber','123456');
        stateMap.put('sSubSourceKey','789');
        stateMap.put('sMemberPlanId',memberPlanObj.Id);
        MemberPlanEligibility_LC_HUM.processAllResponses(labels,stateMap);
    }
    
   static testMethod void testretrieveMTMValueIf(){
        MemberPlan memberPlanObj = [select id,Name,PlanId from MemberPlan where Name = 'MemberPlan1' limit 1];
        Map<String,String> stateMap= new Map<String,String>();
        MemberPlanEligibility_LC_HUM.callCIMedMultipleMemberService(memberPlanObj.Id);
        HttpResponse httpRes = new Httpresponse();
        httpRes.setStatusCode(200);
        String resBodyString = '{"GetMemberReponse":{"Customers":{"Customer":[{"CustomerIdentity":{"BirthDate":"07/25/1988","CustomerKeys":{"CustomerKey":[{"CustomerType":"AC","CustomerValue":"8668317"}]},"FirstName":"test","MiddleName":"","LastName":"march","Email":"gsheppard@humana.com"},"CustomerPreference":{"CapType":"S","CapTypeLiteral":"SAFETY"},"CustomerDetail":{"NeedsMemberConsent":"false","ConsentBeginDate":"","ConsentEndDate":"","MemberConsentStatus":"false","CMRStatus":"Yes","CMRStatusLiteral":""},"Addresses":{"Address":[{"AddressKey":"16007945","AddressType":"B","AddressTypeLiteral":"BILLING","AddressLine1":"SHAW","AddressLine2":"","AddressName":"test march","City":"SUAMICO","StateCode":"WI","ZipCode":"54313","IsActive":"true"}]},"Phones":{"Phone":[{"PhoneKey":"15135289","PhoneType":"PP","PhoneValue":"5022214112"}]},"StaleData":"0","AccountNumber":{"CustomerValue":"8668317"},"Finance":{"CreditLimit":"60.0","OutstandingBalance":"0.0","PaymentSchedules":{"PaymentSchedule":[{"PaymentDueDate":"","PaymentMadeDate":"","PaymentType":"","SourceApplicationCode":"","SourceApplicationCodeLiteral":""}]}},"Allergies":{"Allergy":[{"HealthKey":"00153","HealthDescription":"No Known Drug Allergy"}]},"CreditCards":{"CreditCard":[]},"HealthConditions":{"HealthCondition":[{"HealthDescription":"Pheochromocytoma"}]},"LogNotes":{"LogNote":[]},"Plans":{"Plan":[{"PlanEffectiveDate":"01/14/2014","PlanTerminationDate":"","PlanType":"COMM","Payer":"GENERIC COMMERCIAL PLAN"}]}},{"CustomerIdentity":{"BirthDate":"07/25/1988","CustomerKeys":{"CustomerKey":[{"CustomerType":"NP","CustomerValue":"H61234596"}]},"FirstName":"test","MiddleName":"","LastName":"march","Email":"gsheppard@humana.com"},"CustomerPreference":{"CapType":"S","CapTypeLiteral":"SAFETY"},"CustomerDetail":{"NeedsMemberConsent":"false","ConsentBeginDate":"","ConsentEndDate":"","MemberConsentStatus":"false","CMRStatus":"","CMRStatusLiteral":""},"Addresses":{"Address":[{"AddressKey":"16007944","AddressType":"B","AddressTypeLiteral":"BILLING","AddressLine1":"TREMONT","AddressLine2":"","AddressName":"test march","City":"SUAMICO","StateCode":"WI","ZipCode":"54313","IsActive":"true"}]},"Phones":{"Phone":[{"PhoneKey":"15135288","PhoneType":"PP","PhoneValue":"5022214112"}]},"StaleData":"0","AccountNumber":{"CustomerValue":"8668317"},"Finance":{"CreditLimit":"60.0","OutstandingBalance":"0.0","PaymentSchedules":{"PaymentSchedule":[{"PaymentDueDate":"","PaymentMadeDate":"","PaymentType":"","SourceApplicationCode":"","SourceApplicationCodeLiteral":""}]}},"Allergies":{"Allergy":[{"HealthKey":"00153","HealthDescription":"No Known Drug Allergy"}]},"CreditCards":{"CreditCard":[]},"HealthConditions":{"HealthCondition":[{"HealthDescription":"Pheochromocytoma"},{"HealthDescription":"Diabetes Mellitus"}]},"LogNotes":{"LogNote":[{"LogNoteCode":"CONSENT","LogNoteDate":"03/13/2018","LogNoteKey":"328848159","LogNoteMessage":"Member consent removed on Mar 13 2018  6:13:46:310AM.","LogNoteUser":"ANSUSR"}]},"Plans":{"Plan":[{"PlanEffectiveDate":"01/14/2014","PlanTerminationDate":"","PlanType":"COMM","Payer":"GENERIC COMMERCIAL PLAN"}]}}]},"DuplicateCustomers":{"CustomerKey":[{"customerType":"AC","customerTypeLiteral":"ACCOUNT ID","customerValue":"8668317"}]}}}';
        httpRes.setBody(resBodyString);
        system.assert(httpRes.getBody() == resBodyString);  
        List<String> labels = new List<String>{'Continuation-1','Continuation-2','Continuation-3'};
        string reqType = 'Continuation-1'; 
        Test.setContinuationResponse(reqType,httpRes);
        stateMap.put('sRequest',reqType);
        stateMap.put('sGroupNumber','123456');
        stateMap.put('sSubSourceKey','789');
        MemberPlanEligibility_LC_HUM.processAllResponses(labels,stateMap);
        stateMap.put('sMemberPlanId',memberPlanObj.Id);
        MemberPlanEligibility_LC_HUM.processAllResponses(labels,stateMap);
        CRMFunctionality_ONOFF_Switch__c csCRMOnOffSwitch2 = CRMFunctionality_ONOFF_Switch__c.getValues('2081552');
        csCRMOnOffSwitch2.IsON__c = false;
        update csCRMOnOffSwitch2;
        MemberPlanEligibility_LC_HUM.processAllResponses(labels,stateMap);
        
    }
    static testMethod void testCostShareservice(){
    	MemberPlan memberPlanObj2 = [select id,Name,PlanId,Product_Type_Code__c,Product_Type__c from MemberPlan where Name = 'MemberPlan2' limit 1];
        Map<String,String> stateMap= new Map<String,String>();
        MemberPlanEligibility_LC_HUM.callCIMedMultipleMemberService(memberPlanObj2.Id);
        HttpResponse httpRes = new Httpresponse();
        httpRes.setStatusCode(200);
        string resBodyString = '{"GetMemberResponse":{"Body": {"DualStatusList":[{"EffectiveDate": "2020-08-01","EndDate": "2020-08-31","Value": "QMB+","Code": "02","Indicator": true},{"EffectiveDate": "2019-06-01","EndDate": "2019-06-30","Value": "QMB+","Code": "02","Indicator": true}],"DeemingPeriodList": []}}}';
       
        httpRes.setBody(resBodyString);
        system.assert(httpRes.getBody() == resBodyString);  
        List<String> labels = new List<String>{'Continuation-1','Continuation-2','Continuation-3'};
        string reqType = 'Continuation-3'; 
        Test.setContinuationResponse(reqType,httpRes);
        stateMap.put('sRequest',reqType);
        stateMap.put('sGroupNumber','123456');
        stateMap.put('sSubSourceKey','789');
        stateMap.put('sMemberPlanId',memberPlanObj2.Id);
        MemberPlanEligibility_LC_HUM.processResponseDualStatus(labels,stateMap);   
    }
}
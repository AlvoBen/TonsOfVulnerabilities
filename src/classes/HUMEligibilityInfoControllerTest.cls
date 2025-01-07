/**********************************************************************************************************************************************
Apex Class Name :  HUMEligibilityInfoControllerTest
Version          : 1.0
Created Date     : April 6 2015
Function         : Test Class for"HUMEligibilityInfo" Page
Modification Log :
 * Developer Name           Code Review                 Date                         Description
 *------------------------------------------------------------------------------------------------------------------------------
 * Harshith M L              22313                    04/06/2015                     Original Version
 * Shreya Choodamani         22538                    04/22/2015                     Modified the code to fix the encryption issue related to RSO indicators
 * Manish Kumar              23159                    05/19/2015                     Modified the code for Medicare/Medicaid and LOB_Indicator to resolve service issue. 
                                                                                     Code modified : Throughout the class 
* Manish Kumar               23831                    06/08/2015                     Modified the code for defect#182167 to resolve service issue to fetch dual alerts.
* Kritika Popat              24093                    06/18/2015                     Modified class for Defect 187428.0001
* Kritika Popat              24147                    06/22/2015                     Modified class for Defect 187756.0001
* Shruthi Karanth            34224                    08/03/2016                     Added additional fields during web service custom setting creation
* Gargey Sharma              34890                    08/08/2016                     REQ - 269734 : Ability to view AEC Care Manager Information in CRM
* Sharan Shanmugam                                    04/18/2017                     REQ - 291107 : Ability to view MTM indicator
* Sai Ayyagari               184451                   02/21/2018                     REQ - 350853 Fix for HUMExceptionHelper, store the list of exceptions capture
* Sunil Bukya                234629                   06/08/2018                     REQ - 366198 Implemented Member_Id__c platform encryption changes.
* Divya agrwal                                        07/24/2018                     REQ - 358964 Ability for CRM to display additional Care Managers
* Santhi Mandava                                      04/15/2019                     Replaced "Member_Id_Value__c" with Name
* Prudhvi Pamarthi                                    08/21/2019                     REQ - 403754 Fix for Eligibility section is not working properly in Policy detail page
**********************************************************************************************************************************************/
@isTest 
private class HUMEligibilityInfoControllerTest
{
    private static Policy_Member__c objPolMem;
    private static Policy_Member__c objPolMem1;
    private static Policy_Member__c objPolMember;
    private static Policy_Member__c objPolicyMember;
    
    /*
    * Method Name : setupTestData
    * Description : Method to set up test data
    * Return type : void
    * Paramater   : NA
    */
    static void setupTestData()
    {
        User objUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert objUser;
        system.assertEquals(objUser.lastname=='test User1',true);
        
        insert new HUM_Webservice_Callout__c(Name='Clinical_CareManagerDetails',SampleParameter__c='sample',  Active__c=true, End_Point_URL__c='https://int-crmservicews.humana.com/AuthReferralService/GetCareManagerDetails', Timeout__c='120000', Request_Type__c='POST', Content_Type__c='application/json');     
        insert new HUM_Webservice_Callout__c(Name = 'RighSourceService_GetMember',Certificate_Name__c = 'Test_Certificate' ,Active__c = true,End_Point_URL__c ='https://int-crmservicews.humana.com/RighSourceService_GetMember/' ,Timeout__c = '12000',Request_Type__c = 'POST',Content_Type__c ='application/json' );
        insert new HUM_Webservice_Callout__c(Name = 'CIMedicareMultipleMemberDetails',Certificate_Name__c = 'Test_Certificate' ,Active__c = true,End_Point_URL__c ='https://int-crmservicews.humana.com/RighSourceService_GetMember/' ,Timeout__c = '12000',Request_Type__c = 'POST',Content_Type__c ='application/json' );
         insert new HUM_Webservice_Callout__c(Name='Clinical_CareManagerDetailsByMemberId',SampleParameter__c='sample',  Active__c=true, End_Point_URL__c='https://int-crmservicesvcs.humana.com:48022/AuthReferralService/GetCareManagerDetailsbyMemberId', Timeout__c='120000', Request_Type__c='POST', Content_Type__c='application/json');     
         
        Boolean bIsPharmacyUser;
        // Fetching and inserting Account Record
        Account objAcc = HUMTestDataHelperTest.getAccount();
        objAcc.Name = 'testacc';
        objAcc.Group_Number__c = '213654';
        objAcc.Enterprise_Id__c = '12321422';
        insert objAcc; 
        system.assertEquals(objAcc.id!=null,true);
        
        Product2 oProd = new Product2(Name = 'Laptop X200',Segment_Type__c='MCR',ProductCode='MED', Major_LOB__c='MER');
        insert oProd;
        System.assertNotEquals(oProd.Id, NULL, 'Value not matched');
        
        // Fetching and inserting Policy Record 
        Policy__c objpol = HUMTestDataHelperTest.getPolicy();
        objpol.Name='testPolicy';
        objpol.Group_Name__c = objAcc.Id;
        objpol.Major_LOB__c = 'MER';
        objpol.Source_Cust_Cov_Key__c = '123456789'; 
        objpol.Product__c = oProd.Id;
        objpol.Product__r = oProd;
        objpol.Platform__c = 'LV';
        insert objpol; 
        system.assertEquals(objpol.id!=null,true);
        
        objpol = [Select Name, Group_Name__c, Major_LOB__c, Source_Cust_Cov_Key__c, Product__c, Product__r.Segment_Type__c, LOB_Indicator__c 
                        From Policy__c Where Id = : objpol.Id];
        
        objpol.Product__r.Segment_Type__c = oProd.Segment_Type__c;
        update objPol;
        system.assertEquals(objpol.id!=null,true);
        
        objpol = [Select Name, Group_Name__c, Major_LOB__c, Source_Cust_Cov_Key__c, Product__c, Product__r.Segment_Type__c, LOB_Indicator__c 
                        From Policy__c Where Id = : objpol.Id];
                        
                        
        // Fetching and inserting Policy Record 
        Policy__c objpol1 = HUMTestDataHelperTest.getPolicy();
        objpol1.Name='testPolicy';
        objpol1.Platform__c = 'LV';
        objpol1.Group_Name__c = objAcc.Id;
        objpol1.Major_LOB__c = 'MER';
        //objpol1.Source_Cust_Cov_Key__c = '123456789'; 
        objpol1.Product__c = oProd.Id;
        objpol1.Product__r = oProd;
        insert objpol1; 
        system.assertEquals(objpol1.id!=null,true);
        
        objpol1 = [Select Name, Group_Name__c, Major_LOB__c, Source_Cust_Cov_Key__c, Product__c, Product__r.Segment_Type__c, LOB_Indicator__c 
                        From Policy__c Where Id = : objpol1.Id];
        
        objpol1.Product__r.Segment_Type__c = oProd.Segment_Type__c;
        update objPol1;
        system.assertEquals(objpol1.id!=null,true);
        
        objpol1 = [Select Name, Group_Name__c, Major_LOB__c, Source_Cust_Cov_Key__c, Product__c, Product__r.Segment_Type__c, LOB_Indicator__c 
                        From Policy__c Where Id = : objpol1.Id];         
        
        Policy__c objpolicy = HUMTestDataHelperTest.getPolicy();
        objpolicy.Name='testPolicy';
        objpolicy.Platform__c = 'LV';
        objpolicy.Group_Name__c = objAcc.Id;
        objpolicy.Major_LOB__c = 'MER';
        objpolicy.Source_Cust_Cov_Key__c = '123456789';
        objpolicy.Product__c = oProd.Id;
        objpolicy.Product__r = oProd;
        insert objpolicy; 
        system.assertEquals(objpolicy.id!=null,true);
        
        objpolicy = [Select Name, Group_Name__c, Major_LOB__c, Source_Cust_Cov_Key__c, Product__c, Product__r.Segment_Type__c, LOB_Indicator__c 
                        From Policy__c Where Id = : objpolicy.Id];
        
        // Fetching and inserting Account Record        
        objPolMem = new Policy_Member__c();
        objPolMem.Name = 'testpolmem';
        objPolMem.Relationship__c = 'Self';
        objPolMem.Effective_Date__c=Date.today().addDays(-1).format();
        objPolMem.End_Date__c=Date.today().addDays(1).format();
        objPolMem.Policy_Member_External_ID__c='00|ODS|6925004434817|12345678|34567|2013-07-01';
        objPolMem.Member__c = objAcc.Id;
        objPolMem.Policy__c = objpol.Id;
        objPolMem.Policy__r = objpol;
        insert objPolMem;
        system.assertEquals(objPolMem.id!=null,true);
        
        // Fetching and inserting Account Record        
        objPolMem1 = new Policy_Member__c();
        objPolMem1.Name = 'testpolmem1';
        objPolMem1.Relationship__c = 'Self';
        objPolMem1.Effective_Date__c=Date.today().addDays(-1).format();
        objPolMem1.End_Date__c=Date.today().addDays(1).format();
        objPolMem1.Policy_Member_External_ID__c='05|ODS|6925004434817|12345678|34567|2013-07-01';
        objPolMem1.Member__c = objAcc.Id;
        objPolMem1.Policy__c = objpol1.Id;
        objPolMem1.Policy__r = objpol1;
        insert objPolMem1;
        system.assertEquals(objPolMem1.id!=null,true);     
        
        
        objPolMember = new Policy_Member__c();
        objPolMember.Name = 'testpolicymember';
        objPolMember.Relationship__c = 'Self';
        objPolMember.Effective_Date__c=Date.today().addDays(-1).format();
        objPolMember.End_Date__c=Date.today().addDays(1).format();
        objPolMember.Policy_Member_External_ID__c='01|ODS|6925004434817|12345678|34567|2013-07-01';
        objPolMember.Member__c = objAcc.Id;
        objPolMember.Policy__c = objpolicy.Id;
        objPolMember.Policy__r = objpolicy;
        insert objPolMember;
        system.assertEquals(objPolMember.id!=null,true);
        
        objPolMember = [Select Name, Relationship__c, Effective_Date__c, End_Date__c, Policy_Member_External_ID__c, Member__c, Policy__c, Policy__r.LOB_Indicator__c, 
                            Policy__r.Product__r.Segment_Type__c From Policy_Member__c Where Id =: objPolMember.Id];
        
        objPolicyMember = new Policy_Member__c();
        objPolicyMember.Name = 'testpolicymemberrec';
        objPolicyMember.Relationship__c = 'Self';
        objPolicyMember.Effective_Date__c=Date.today().addDays(-1).format();
        objPolicyMember.End_Date__c=Date.today().addDays(1).format();
        objPolicyMember.Policy_Member_External_ID__c='02|ODS|6925004434817|12345678|34567|2013-07-01';
        objPolicyMember.Member__c = objAcc.Id;
        objPolicyMember.Policy__c = objpol.Id;       
        insert objPolicyMember;
        system.assertEquals(objPolicyMember.id!=null,true);
        
        Member_Id__c memberIdInstance = new Member_Id__c();
        memberIdInstance.Policy__c = objpol.Id;
        memberIdInstance.Policy__r = objpol;
        memberIdInstance.Policy_Member__c = objPolMem.Id;
        memberIdInstance.Policy_Member__r = objPolMem;
        memberIdInstance.Member_Card_External_ID__c = objPolMem.Id + 'ExtId';
        memberIdInstance.Type__c = 'Member-Id-Base';
        memberIdInstance.Name = '123456789012';
        insert memberIdInstance;
        system.assertEquals(memberIdInstance.id!=null,true);
        
        Member_Id__c memberIdInstance3 = new Member_Id__c();
        memberIdInstance3.Policy__c = objpol1.Id;
        memberIdInstance3.Policy__r = objpol1;
        memberIdInstance3.Policy_Member__c = objPolMem1.Id;
        memberIdInstance3.Policy_Member__r = objPolMem1;
        memberIdInstance3.Member_Card_External_ID__c = objPolMem1.Id + 'ExtId';
        memberIdInstance3.Type__c = 'Member-Id-Base';
        memberIdInstance3.Name = '123456789012';
        insert memberIdInstance3;
        system.assertEquals(memberIdInstance3.id!=null,true);
        
        Member_Id__c memberIdInstance1 = new Member_Id__c();
        memberIdInstance1.Policy__c = objpol.Id;
        memberIdInstance1.Policy__r = objpol;
        memberIdInstance1.Policy_Member__c = objPolMember.Id;
        memberIdInstance1.Policy_Member__r = objPolMember;
        memberIdInstance1.Member_Card_External_ID__c = objPolMember.Id + 'ExtId';
        memberIdInstance1.Type__c = 'Dental';
        memberIdInstance1.Name = '123456789123';
        insert memberIdInstance1;
        system.assertEquals(memberIdInstance1.id!=null,true);
        
        Member_Id__c memberIdInstance2 = new Member_Id__c();
        memberIdInstance2.Policy__c = objpol.Id;
        memberIdInstance2.Policy__r = objpol;
        memberIdInstance2.Policy_Member__c = objPolicyMember.Id;
        memberIdInstance2.Policy_Member__r = objPolicyMember;
        memberIdInstance2.Member_Card_External_ID__c = objPolicyMember.Id + 'ExtId';
        memberIdInstance2.Type__c = 'Member-Id-Base';
        memberIdInstance2.Name = '123456789123';
        insert memberIdInstance2;
        system.assertEquals(memberIdInstance2.id!=null,true);
     
        
        //inserting the Custom Setting Objects
        HUM_Section_Priority__c secPriorityESRD = new HUM_Section_Priority__c(Name='End Stage Renal Disease (ESRD)',Priority__c = 1);
        insert secPriorityESRD;
        system.assertEquals(secPriorityESRD.id!=null,true);
        
        HUM_Section_Priority__c secPriorityLIS = new HUM_Section_Priority__c(Name='Extra Help',Priority__c = 2);
        insert secPriorityLIS;
        system.assertEquals(secPriorityLIS.id!=null,true);
        
        HUM_Section_Priority__c secPriorityLTSS = new HUM_Section_Priority__c(Name='Long Term Support Services (LTSS)',Priority__c = 3);
        insert secPriorityLTSS;
        system.assertEquals(secPriorityLTSS.id!=null,true);
        
        HUM_Section_Priority__c secPrioritySNP = new HUM_Section_Priority__c(Name='Special Needs Plan (SNP)',Priority__c = 4);
        insert secPrioritySNP;
        system.assertEquals(secPrioritySNP.id!=null,true);

        HUM_Section_Priority__c secPriorityMTM = new HUM_Section_Priority__c(Name='Medication Therapy (MTM)',Priority__c = 5);
        insert secPriorityMTM;
        system.assertEquals(secPriorityMTM.id!=null,true);
        
        HUM_Field_Priority__c fPriorityEligible = new HUM_Field_Priority__c(Name='Eligible',Priority__c = 1);
        insert fPriorityEligible;
        system.assertEquals(fPriorityEligible.id!=null,true);
        
        HUM_Field_Priority__c fPriorityType = new HUM_Field_Priority__c(Name='Type',Priority__c = 2);
        insert fPriorityType;
        system.assertEquals(fPriorityType.id!=null,true);
        
        HUM_Field_Priority__c fPriorityPercent = new HUM_Field_Priority__c(Name='Percent/ Copay',Priority__c = 3);
        insert fPriorityPercent;
        system.assertEquals(fPriorityPercent.id!=null,true);
        
        HUM_Field_Priority__c fPriorityEffectiveDt = new HUM_Field_Priority__c(Name='Effective Date',Priority__c = 4);
        insert fPriorityEffectiveDt;
        system.assertEquals(fPriorityEffectiveDt.id!=null,true);
        
        HUM_Field_Priority__c fPriorityEndDt = new HUM_Field_Priority__c(Name='End Date',Priority__c = 5);
        insert fPriorityEndDt;
        system.assertEquals(fPriorityEndDt.id!=null,true);
        
        HUMConstants__c fEligibleYesValue = new HUMConstants__c(Name = 'HUMEligibleYes',StringValue__c = 'Yes');
        insert fEligibleYesValue;
        system.assertEquals(fEligibleYesValue.id!=null,true);
        
        HUMConstants__c fEligibleNoValue = new HUMConstants__c(Name = 'HUMEligibleNo',StringValue__c = 'No');
        insert fEligibleNoValue;
        system.assertEquals(fEligibleNoValue.id!=null,true);
        
        HUMConstants__c fNoValue = new HUMConstants__c(Name = 'HUMNoValue',StringValue__c = 'No Value');
        insert fNoValue ;
        system.assertEquals(fNoValue.id!=null,true);
        
        HumCareManager__c fCare=new HumCareManager__c(Name='Behavioral Health Care Manager',ID__c = '1',Type__c='Behavioral Health Care Manager');
        insert fCare;
         system.assertEquals(fCare.id!=null,true);
         
        HumCareManager__c fCareM=new HumCareManager__c(Name='Behavioral Health Vendor Care Manager',ID__c = '2',Type__c='Behavioral Health Vendor Care Manager');
        insert fCareM;
         system.assertEquals(fCareM.id!=null,true); 
         
         HumCareManager__c fCareN=new HumCareManager__c(Name='Clinical Case Manager',ID__c = '3',Type__c='Clinical Case Manager');
        insert fCareN;
         system.assertEquals(fCareN.id!=null,true); 

        
        insert new HUM_Webservice_Callout__c(Name='HumCIMedicareMemberService', Certificate_Name__c='Humana_GuidanceCare_R1', Active__c=true, End_Point_URL__c='https://dev-crmservicews.humana.com/CIMedicareMember', Timeout__c='12000',Request_Type__c='POST', Content_Type__c='application/xml');
        
        // Loading Custom Setting data        
        HUMTestDataHelperTest.getHUMConstantsData();
        
        List<HUMConstants__c> lstHUMConstants = new List<HUMConstants__c>();
        lstHUMConstants.add(new HUMConstants__c(Name='HUMTypeSNP',StringValue__c='SNP'));
        lstHUMConstants.add(new HUMConstants__c(Name='HUMTypeESRD',StringValue__c='ESRD'));
        lstHUMConstants.add(new HUMConstants__c(Name='HUMTypeLTSS',StringValue__c='LTSS'));
        lstHUMConstants.add(new HUMConstants__c(Name='HUMTypeLIS',StringValue__c='LIS'));
        lstHUMConstants.add(new HUMConstants__c(Name='HUMMedicaid',StringValue__c='MedicaidID'));
        lstHUMConstants.add(new HUMConstants__c(Name = 'HUMMedicare',StringValue__c = 'MedicareID'));
        lstHUMConstants.add(new HUMConstants__c(Name='HUMTypeRSO',StringValue__c='RSO;'));
        lstHUMConstants.add(new HUMConstants__c(Name='RSO_MEMBER_TYPE',StringValue__c='Member-Id-Base;'));
        lstHUMConstants.add(new HUMConstants__c(Name='HUMLimit1000',StringValue__c='1000',IntegerValue__c=1000));
        lstHUMConstants.add(new HUMConstants__c(Name='POLICY_PLATFORM_INFO', StringValue__c='LV;'));
        lstHUMConstants.add(new HUMConstants__c(Name='SEMICOLON', StringValue__c=';'));
        lstHUMConstants.add(new HUMConstants__c(Name='HUMRSOMedicare',StringValue__c='RSO- Medicare'));
                insert lstHUMConstants;
        System.assertEquals(lstHUMConstants.size(), 12);
        
              
        List<Reference_Business_Line_Product_Grouping__c> lstRBLP = new List<Reference_Business_Line_Product_Grouping__c>();
        lstRBLP.add(new Reference_Business_Line_Product_Grouping__c(Name='RSO',Business_Description__c='RSO- Medicare', Major_LOB__c='MER', ProductCode__c='MED'));
        insert lstRBLP;
        
        List<HUMReferenceBusinessLineProductGroup__c> lstRefBLP = new List<HUMReferenceBusinessLineProductGroup__c>();
        lstRefBLP.add(new HUMReferenceBusinessLineProductGroup__c(Name='RSO',Business_Description__c='RSO- Medicare', Major_LOB__c='MER', ProductCode__c='MED'));
        insert lstRefBLP;
        
        System.assertEquals(lstRefBLP.size(), 1, 'Value is not matched');
    }
         
    /*
    * Method Name : testMemberEligibility
    * Description : Test method to get response for the indicators
    * Return type : void
    * Paramater   : NA
    */
    public static testMethod void testMemberEligibility()
    {
        // Setting up the environment for testing
        setupTestData();
        // Setting up the page for testing    
        PageReference pageRef = Page.HUMEligibilityInfo;
        Policy_Member__c objPolMemRec = [Select Id,Name From Policy_Member__c Limit 1];
        // Standard Controller for Policy member record        
        ApexPages.StandardController standardController = new ApexPages.StandardController(objPolMemRec);  
        
        Test.startTest();
        Test.setMock(HttpCalloutMock.class, new HUMCIMedicareMemberMockHttpResponseTest()); 
        HUMEligibilityInfoController elegibilityInfoController = new HUMEligibilityInfoController(standardController);
        system.assertEquals(elegibilityInfoController.bProcessFlag,true);
        
        Continuation continueObj = (Continuation)elegibilityInfoController.callCIMedMultipleMemberService();
        HttpResponse httpRes = new Httpresponse ();
        String resBodyString= '{"GetMemberReponse":{"Customers":{"Customer":[{"StaleData":"","SpecialtyVariantObjects":{"CapType":"S"},"Plans":{"Plan":[{"PlanType":"CASH RX","PlanTerminationDate":"","PlanEffectiveDate":""},{"PlanType":"MEDIMPACT OTC","PlanTerminationDate":"","PlanEffectiveDate":"07/29/2015"},{"PlanType":"MA","PlanTerminationDate":"","PlanEffectiveDate":"07/29/2015"}]},"Phones":{"Phone":[{"PhoneValue":"5025801000","PhoneType":"PP","PhoneKey":"7292489"}]},"Payment":{"NonCreditCard":{"Type":"","PaymentNumber":""}},"LogNotes":{"LogNote":[]},"HealthConditions":{"HealthCondition":[{"HealthDescription":"NO KNOWN MEDICAL HISTORY"}]},"Finance":{"PaymentSchedules":{"PaymentSchedule":[]},"OutstandingBalance":"","CreditLimit":"","Adjustments":{"Adjustment":[]}},"CustomerIdentity":{"MiddleName":"","LastName":"DAGGETT","FirstName":"KATHLEEN","Email":"gsheppard@humana.com","CustomerKeys":{"CustomerKey":[{"CustomerValue":"4193538","CustomerType":"PK"},{"CustomerValue":"1031063837","CustomerType":"PM"},{"CustomerValue":"6895629","CustomerType":"AC"}]},"BirthDate":"09/12/1944"},"CustomerDetail":{ "NeedsMemberConsent":"true", "ConsentBeginDate":"04/14/2017", "ConsentEndDate":"04/14/2018", "MemberConsentStatus":"true", "CMRStatus":"Do Not Contact", "CMRStatusLiteral":"Do Not Contact" },"CreditCards":{"CreditCard":[{"IsAutoCharge":"false","IsActive":"true","ExpirationYear":"2018","ExpirationMonth":"11","CreditCardType":"M","CreditCardLast4Digits":"4315830000415489"},{"IsAutoCharge":"false","IsActive":"true","ExpirationYear":"2018","ExpirationMonth":"11","CreditCardType":"M","CreditCardLast4Digits":"4315830000415494"},{"IsAutoCharge":"false","IsActive":"true","ExpirationYear":"2018","ExpirationMonth":"11","CreditCardType":"M","CreditCardLast4Digits":"4315830000415497"},{"IsAutoCharge":"false","IsActive":"true","ExpirationYear":"2016","ExpirationMonth":"10","CreditCardType":"V","CreditCardLast4Digits":"3371953113715499"},{"IsAutoCharge":"false","IsActive":"true","ExpirationYear":"2018","ExpirationMonth":"11","CreditCardType":"M","CreditCardLast4Digits":"4315830000415498"}]},"Allergies":{"Allergy":[{"HealthKey":"00153","HealthDescription":"NO KNOWN DRUG ALLERGY"}]},"Addresses":{"Address":[{"ZipCode":"95404","StateCode":"CA","IsActive":"true","City":"SANTA ROSA","AddressTypeLiteral":"","AddressType":"A","AddressLine2":"","AddressLine1":"3227 CANYON CREEK LN"},{"ZipCode":"32615","StateCode":"FL","IsActive":"true","City":"ALACHUA","AddressTypeLiteral":"","AddressType":"P","AddressLine2":"","AddressLine1":"VOX90"},{"ZipCode":"75124","StateCode":"TX","IsActive":"true","City":"EUSTACE","AddressTypeLiteral":"","AddressType":"B","AddressLine2":"","AddressLine1":"13146 LAKE LN"},{"ZipCode":"75124","StateCode":"TX","IsActive":"true","City":"EUSTACE","AddressTypeLiteral":"","AddressType":"S","AddressLine2":"","AddressLine1":"13146 LAKE LN"},{"ZipCode":"75124","StateCode":"TX","IsActive":"true","City":"EUSTACE","AddressTypeLiteral":"","AddressType":"A","AddressLine2":"","AddressLine1":"13146 LAKE LNT"}]},"AccountNumber":{"CustomerValue":"6895629"}}]}}}';
        httpRes.setBody(resBodyString);
        system.assert(httpRes.getBody() == resBodyString);
        string reqType = '';
        List<string>labelGetmember = new List<string>();
        labelGetmember.add(reqType);
        Test.setContinuationResponse(elegibilityInfoController.sMTMReq,httpRes);
        elegibilityInfoController.retrieveMTMValue();
        
        HttpRequest request = new HttpRequest(); 
        HUM_Webservice_Callout__c objSer = HUM_Webservice_Callout__c.getInstance('HumCIMedicareMemberService');
        request = elegibilityInfoController.builtHttpRequest(objSer);
        System.assertEquals('https://dev-crmservicews.humana.com/CIMedicareMember',request.getEndPoint());
        
        RightSourceGetMemberRequest_DTO_HUM obj1 = elegibilityInfoController.RequestStructureGetMemberResponse('H1234567','12345');
        System.assertEquals('H1234567',obj1.GetMemberRequest.EnterprisePersonID);
        
        request = elegibilityInfoController.setupRequest(request,HUM_Webservice_Callout__c.getInstance('CIMedicareMultipleMemberDetails'),'123456','1234567','Key','01/01/2017','Yes');
        System.assertEquals('https://int-crmservicews.humana.com/RighSourceService_GetMember/',request.getEndPoint());
        
        HUMCIMedicareMemberResponseHelper objres = new HUMCIMedicareMemberResponseHelper();
        elegibilityInfoController.callSNPService(objPolMemRec ,objres );        
        objres.Errors = new list<HUMCIMedicareMemberResponseHelper.Error>();
        HUMCIMedicareMemberResponseHelper.Error objErr = new HUMCIMedicareMemberResponseHelper.Error();
        objErr.description = 'Somwthing went wrong';
        objErr.code = '12345';
        objres.Errors.Add(objErr);
        objres.sSNPIndicator = 'Yes';
        objres.sSNPTypeCode = 'TestType';
        elegibilityInfoController.callSNPService(objPolMemRec ,objres );
        System.assertEquals('No Value',elegibilityInfoController.mapServiceResponse.get('Type'));
        
        objres.sLISIndicator = 'Y';
        objres.Errors = new list<HUMCIMedicareMemberResponseHelper.Error>();
        elegibilityInfoController.callSNPService(objPolMemRec ,objres );
        System.assertEquals('No Value',elegibilityInfoController.mapServiceResponse.get('Type'));

        CIMedicareMultipleMemberResponse_DTO_HUM objServiceResponse = new CIMedicareMultipleMemberResponse_DTO_HUM();
        objServiceResponse.CIMedicareResponse = new CIMedicareMultipleMemberResponse_DTO_HUM.CIMedicareResponse();
        objServiceResponse.CIMedicareResponse.GetMedicareMemberInfo = new List<CIMedicareMultipleMemberResponse_DTO_HUM.GetMedicareMemberInfo>();
        
        CIMedicareMultipleMemberResponse_DTO_HUM.GetMedicareMemberInfo obj= new CIMedicareMultipleMemberResponse_DTO_HUM.GetMedicareMemberInfo();
        obj.MedicareMemberLISSNPInfoResponse = new CIMedicareMultipleMemberResponse_DTO_HUM.MedicareMemberLISSNPInfoResponse();
        obj.MedicareMemberLISSNPInfoResponse.Errors = new CIMedicareMultipleMemberResponse_DTO_HUM.Errors();
        objServiceResponse.CIMedicareResponse.GetMedicareMemberInfo.Add(obj);
        String sResponse =  JSON.serialize(objServiceResponse);
        elegibilityInfoController.parseCIResponse(sResponse );
        System.assertEquals('No Value',elegibilityInfoController.mapServiceResponse.get('Type'));
        
        elegibilityInfoController.populateNoServiceResponse('CIMEDICARE');
        System.assertEquals('No Value',elegibilityInfoController.mapServiceResponse.get('End Date'));
        
        elegibilityInfoController.callLISService(objres);
        System.assertEquals('',elegibilityInfoController.mapServiceResponse.get('End Date'));
        
        elegibilityInfoController.callESRDService(objres);
        System.assertEquals('No Value',elegibilityInfoController.mapServiceResponse.get('Effective Date'));
        
        elegibilityInfoController.callLTSSService(objres);
        System.assertEquals('No Value',elegibilityInfoController.mapServiceResponse.get('Percent/ Copay'));
        
        elegibilityInfoController.processAllResponses();
        System.assertEquals('No Value',elegibilityInfoController.mapServiceResponse.get('Type'));
        objres.sLISIndicator = 'Y';
        objres.sLISSubsidyLevel = 'Test';
        objres.sLISCopayCategory = 'Test Category';
        
        elegibilityInfoController.callLISService(objres);
        System.assertEquals('No Value',elegibilityInfoController.mapServiceResponse.get('Type'));
        Test.stopTest();
    }
    
    /*
    * Method Name : testMemberEligibilityWithoutSourceKey
    * Description : Test method to get response for the indicators except SNP
    * Return type : void
    * Paramater   : NA
    */
    public static testMethod void testMemberEligibilityWithoutSourceKey()
    {
        // Setting up the environment for testing
        setupTestData();
        // Setting up the page for testing    
        PageReference pageRef = Page.HUMEligibilityInfo;
        // Standard Controller for Policy member record        
        ApexPages.StandardController standardController = new ApexPages.StandardController(objPolMem1);  
        
        Test.startTest();
        Test.setMock(HttpCalloutMock.class, new HUMCIMedicareMemberMockHttpResponseTest()); 
        HUMEligibilityInfoController elegibilityInfoController = new HUMEligibilityInfoController(standardController);
        system.assertEquals(elegibilityInfoController.bProcessFlag,true);
        Test.stopTest();            
    }
    
    /*
    * Method Name : testEmptyMedicareID
    * Description : Test method to get response for the indicators when there is no Policy Member records
    * Return type : void
    * Paramater   : NA
    */
    public static testMethod void testEmptyMedicareID()
    {
        // Setting up the environment for testing
        setupTestData();
        // Setting up the page for testing    
        PageReference pageRef = Page.HUMEligibilityInfo;
        // Standard Controller for Policy member record        
        ApexPages.StandardController standardController = new ApexPages.StandardController(objPolMember);   
        Test.startTest();
        Test.setMock(HttpCalloutMock.class, new HUMCIMedicareMemberMockHttpResponseTest()); 
        HUMEligibilityInfoController elegibilityInfoController = new HUMEligibilityInfoController(standardController);
        Test.stopTest();            
    }
    
    /*
    * Method Name : testIndicatorValueNo
    * Description : Test method to get response for the indicators where the response is No
    * Return type : void
    * Paramater   : NA
    */
    public static testMethod void testIndicatorValueNo()
    {
        // Setting up the environment for testing
        setupTestData();
        // Setting up the page for testing    
        PageReference pageRef = Page.HUMEligibilityInfo;
        // Standard Controller for Policy member record        
        ApexPages.StandardController standardController = new ApexPages.StandardController(objPolicyMember);   
        Test.startTest();
        Test.setMock(HttpCalloutMock.class, new HUMCIMedicareMemberMockHttpResponseTest()); 
        HUMEligibilityInfoController elegibilityInfoController = new HUMEligibilityInfoController(standardController);    
        system.assertEquals(elegibilityInfoController.bProcessFlag,true);
            List<Error_Log__c> lstLog = new List<Error_Log__c>();            
            Error_Log__c error = new Error_Log__c(Status__c = 'HUMEligibilityInfoController');            
            lstLog.add(error);                        
            elegibilityInfoController.listErrorLogs = lstLog;            
            System.assert(elegibilityInfoController.listErrorLogs.size() > 0);
        Test.stopTest();            
    }
    /*
    * Method Name : retrieveCareManagerDetailById
    * Description : Test method to test retrieveCareManagerDetailById method
    * Return type : void
    * Paramater   : NA
    */
    public static testMethod void retrieveCareManagerDetailById()
    {
         setupTestData();
         Test.startTest();
             Test.setMock(HttpCalloutMock.class, new HUMCIMedicareMemberMockHttpResponseTest());
             Test.setCurrentPage(page.HUMEligibilityInfo_R1);
             ApexPages.StandardController scon = new ApexPages.StandardController(objPolMem);
             ApexPages.currentPage().getParameters().put('Id',objPolMem.Id);             
             HUMEligibilityInfoController eligiInfo = new HUMEligibilityInfoController(scon);
             Continuation con = (Continuation)eligiInfo.retrieveCareManagerDetailById();
             HttpResponse resCareMngr= new Httpresponse ();
             String sLTSSReq = '{"CareManagerbyMemberIdResponse":{"CareManagerDetails":{"CareManager":[{"EndDate":"","ID":"1","Name":"Ccm","PhoneNumber":"901-566-1002","StartDate":"2018-07-12T00:00:00","Type":"Clinical Case Manager"},{"EndDate":"","ID":"4","Name":"DPCM","PhoneNumber":"987-654-3210","StartDate":"2018-07-12T00:00:00","Type":"Disease Case Manager"},{"EndDate":"","ID":"5","Name":"Dcm","PhoneNumber":"","StartDate":"2018-07-12T00:00:00","Type":"Discharge Planning Case Manager"},{"EndDate":"","ID":"6","Name":"LTC","PhoneNumber":"","StartDate":"2018-07-12T00:00:00","Type":"LTC Care Manager"}],"Message": {}}}}';
             resCareMngr.setBody(sLTSSReq);
             resCareMngr.setStatusCode(200);            
             system.assert(resCareMngr.getBody() == sLTSSReq);             
             Test.setContinuationResponse(eligiInfo.sLTSSReq ,resCareMngr);
             Test.invokeContinuationMethod(eligiInfo,con);
         Test.stopTest();              
    }
    
     /*
    * Method Name : retrieveCareManagerDetailById
    * Description : Test method to test retrieveCareManagerDetailById method negative secario
    * Return type : void
    * Paramater   : NA
    */    
      public static testMethod void retrieveCareManagerDetailByIdNeg()
    {
         setupTestData();
         Test.startTest();
             Test.setMock(HttpCalloutMock.class, new HUMCIMedicareMemberMockHttpResponseTest());
             Test.setCurrentPage(page.HUMEligibilityInfo_R1);
             ApexPages.StandardController scon = new ApexPages.StandardController(objPolMem);
             ApexPages.currentPage().getParameters().put('Id',objPolMem.Id);             
             HUMEligibilityInfoController eligiInfo = new HUMEligibilityInfoController(scon);
             Continuation con = (Continuation)eligiInfo.retrieveCareManagerDetailById();
             HttpResponse resCareMngr= new Httpresponse ();
             String sLTSSReq = '{"CareManagerbyMemberIdResponse":{"CareManagerDetails":{"CareManager":[{"EndDate":"","ID":"1","Name":"Ccm","PhoneNumber":"901-566-1002","StartDate":"2018-07-12T00:00:00","Type":"Clinical Case Manager"},{"EndDate":"","ID":"4","Name":"DPCM","PhoneNumber":"987-654-3210","StartDate":"2018-07-12T00:00:00","Type":"Disease Case Manager"},{"EndDate":"","ID":"5","Name":"Dcm","PhoneNumber":"","StartDate":"2018-07-12T00:00:00","Type":"Discharge Planning Case Manager"},{"EndDate":"","ID":"6","Name":"LTC","PhoneNumber":"","StartDate":"2018-07-12T00:00:00","Type":"LTC Care Manager"}],"Message": {}}}';
             resCareMngr.setBody(sLTSSReq);
             resCareMngr.setStatusCode(200);            
             system.assert(resCareMngr.getBody() == sLTSSReq);             
             Test.setContinuationResponse(eligiInfo.sLTSSReq ,resCareMngr);
             Test.invokeContinuationMethod(eligiInfo,con);
         Test.stopTest();              
    }
    
     /*
    * Method Name : retrieveCareManagerDetailById
    * Description : Test method to test retrieveCareManagerDetailById method negative status sceanrio
    * Return type : void
    * Paramater   : NA
    */
      public static testMethod void retrieveCareManagerDetailByIdNeg1()
    {
         setupTestData();
         Test.startTest();
             Test.setMock(HttpCalloutMock.class, new HUMCIMedicareMemberMockHttpResponseTest());
             Test.setCurrentPage(page.HUMEligibilityInfo_R1);
             ApexPages.StandardController scon = new ApexPages.StandardController(objPolMem);
             ApexPages.currentPage().getParameters().put('Id',objPolMem.Id);             
             HUMEligibilityInfoController eligiInfo = new HUMEligibilityInfoController(scon);
             Continuation con = (Continuation)eligiInfo.retrieveCareManagerDetailById();
             HttpResponse resCareMngr= new Httpresponse ();
             String sLTSSReq = '{"CareManagerbyMemberIdResponse":{"CareManagerDetails":{"CareManager":[{"EndDate":"","ID":"1","Name":"Ccm","PhoneNumber":"901-566-1002","StartDate":"2018-07-12T00:00:00","Type":"Clinical Case Manager"},{"EndDate":"","ID":"4","Name":"DPCM","PhoneNumber":"987-654-3210","StartDate":"2018-07-12T00:00:00","Type":"Disease Case Manager"},{"EndDate":"","ID":"5","Name":"Dcm","PhoneNumber":"","StartDate":"2018-07-12T00:00:00","Type":"Discharge Planning Case Manager"},{"EndDate":"","ID":"6","Name":"LTC","PhoneNumber":"","StartDate":"2018-07-12T00:00:00","Type":"LTC Care Manager"}],"Message": {}}}';
             resCareMngr.setBody(sLTSSReq);
             resCareMngr.setStatusCode(201);            
             System.assert(resCareMngr.getBody() == sLTSSReq);             
             Test.setContinuationResponse(eligiInfo.sLTSSReq ,resCareMngr);
             Test.invokeContinuationMethod(eligiInfo,con);
         Test.stopTest();              
    }

}
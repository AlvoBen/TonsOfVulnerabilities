/*******************************************************************************************************************************
Apex Class Name :  AccumsType_T_HUM_V2.cls
Version: 1.0
Created On      : 03/21/2016
Function        : Class contains test methods AccumsType_D_HUM_V2 Class.

Modification Log :  
 * Developer Name            Code Review                Date                       Description
 *------------------------------------------------------------------------------------------------------------------------------
* Avinash Choubey                                   03/31/2016                 Original Version
* Avinash Choubey            31077                  04/18/2016                 Design Change in All Accums Req ( 237142, 246888 etc)
* Avinash Choubey            28561                  05/28/2016                 Changes to accomodate changes in AccumsType_D_HUM to fix  ( 225237, 226287)
* Avinash Choubey            29988                  06/09/2016                 Changes to fix Defect# 228609
* Avinash Choubey            32583                  07/13/2016                 Removed MBE Dependency for Gen Key REQ - 269975.
* Praveen Parimi             56654                  01/13/2017                 Changed the Benefits page to point to New version. 
* Muralidhar Kollu                                  18/08/2017                 REQ - 326297 - change of AsOfDate Previous section logic
* Shivam Anand               140629                 20/10/2017                 REQ - 333058 /REQ - 332334 -Code coverage fix
* Shailesh Mali										01/20/2020				   Version - 2, REQ - 791947 Accumulator SOAP to REST Conversion.
* Shailesh Mali								        02/04/2020			       REQ - 749402 Accumulator Description for Dental Product
* Shailesh Mali										02/12/2020				   REQ - 728173 Showing Secondary Accum Data 
* Sayali Nemade                                     02/10/2020				   Test Class Build Fix
* Suraj Patil										07/27/2021		  		   REQ - 1928698 PR00094226 - CR AEP readiness - Low Security Issues 
********************************************************************************************************************************/
@isTest
Private Class AccumsType_T_HUM_V2
{
    /*
    * Method Name : setUpTestData
    * Description : This method creates test data For test Class
    * Return type : None
    * Paramater   : None
    */
    @testSetup static void setUpTestData()
    {
        User objUser = HUMTestDataHelperTest.createTestUser('AccumsCurrentPreviousUser1','Customer Care Specialist');
        insert objUser;
        system.assertEquals(objUser.lastname=='test AccumsCurrentPreviousUser1',true);
        system.runAs(objUser)
        {
            HUM_Webservice_Callout__c AccumsServiceDetails = New HUM_Webservice_Callout__c(Name = 'AccumsServiceREST',Certificate_Name__c = 'Humana_GuidanceCare_Mercury' ,Active__c = true,End_Point_URL__c ='https://dev-crmservicews.humana.com/AccumsServiceRest/GetBenefitAccums' ,Timeout__c = '12000',Request_Type__c = 'POST',Content_Type__c ='application/json' );
            insert AccumsServiceDetails;
            system.assert(HUM_Webservice_Callout__c.getValues('AccumsServiceREST').Name == 'AccumsServiceREST');
            
            // Insert Member Account
            Account objMemAccount = HUMTestDataHelperTest.getAccount();
            objMemAccount.FirstName = 'Test FName';
            objMemAccount.LastName = 'LName'; 
            objMemAccount.Enterprise_Id__c = String.valueof(2000);
            objMemAccount.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId();
            objMemAccount.Birthdate__c = String.valueOf(System.today().month()+'/'+system.today().day()+'/'+System.today().year());
            objMemAccount.Mbr_Gen_key__c ='12323434434';
            insert objMemAccount;
            system.assert(objMemAccount.LastName == 'LName');            
         
            Account objMemAccountSub = HUMTestDataHelperTest.getAccount();
            objMemAccountSub.FirstName = 'Sub FName';
            objMemAccountSub.LastName = 'LNameSub'; 
            objMemAccountSub.Enterprise_Id__c = String.valueof(20001);
            objMemAccountSub.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId();
            objMemAccountSub.Birthdate__c = String.valueOf(System.today().month()+'/'+system.today().day()+'/'+System.today().year());
            objMemAccountSub.Mbr_Gen_key__c ='12323434434';
            insert objMemAccountSub;
            system.assert(objMemAccountSub.LastName == 'LNameSub');
            
            // Insert Product
            Product2 objProduct = New Product2();
            objProduct.Name = 'MED';
            objProduct.Product_Type__c = 'test';
            insert objProduct;
            system.assert(objProduct.Product_Type__c == 'test');
            
            //Product - Dental
            Product2 objProduct1 = New Product2();
            objProduct1.Name = 'DEN';
            objProduct1.Product_Type__c = 'test1';
            insert objProduct1;
            system.assert(objProduct1.Product_Type__c == 'test1');
            
            //Policy for Dental Product
            Policy__c objPolicyDental = HUMTestDataHelperTest.getPolicy();
            objPolicyDental.Name = 'Test Dental Policy';
            objPolicyDental.Group_Name__c = objMemAccount.Id;
            objPolicyDental.Product__c = objProduct1.Id; 
            objPolicyDental.Policy_External_ID__c ='3232|3232|323|2323|232|23439'  ;        
            insert objPolicyDental;
            system.assert(objPolicyDental.Name == 'Test Dental Policy');
            
            // Insert Policy
            Policy__c objPolicy = HUMTestDataHelperTest.getPolicy();
            objPolicy.Name = 'Test Policy';
            objPolicy.Group_Name__c = objMemAccount.Id;
            objPolicy.Product__c = objProduct.Id; 
            objPolicy.Policy_External_ID__c ='3232|3232|323|2323|232|23433'  ;        
            insert objPolicy;
            system.assert(objPolicy.Name == 'Test Policy');
            
            // Insert Policy Members
            Policy_Member__c objPolMem = HUMTestDataHelperTest.getPolicyMember();
            objPolMem.Member__c = objMemAccount.Id;
            objPolMem.Policy__c = objPolicy.Id;
            objPolMem.Relationship__c = 'self'; 
            objPolMem.name = '12345'; 
            objPolMem.end_date__c  = '12/31/9999'  ;  
            objPolMem.Effective_date__c  = '01/01/2014'  ;   
            objPolMem.Policy_Member_External_ID__c = '00|ODS|123456787|89|89|02-01-2001';
            insert objPolMem;
            system.assert(objPolMem.Relationship__c == 'self');
            
            Policy_Member__c objPolMem1 = HUMTestDataHelperTest.getPolicyMember();
            objPolMem1.Member__c = objMemAccountSub.Id;
            objPolMem1.Policy__c = objPolicy.Id;
            objPolMem1.Relationship__c = 'Spouse'; 
            objPolMem1.name = '123456';
            objPolMem1.end_date__c    ='12/31/9999';  
            objPolMem1.Effective_date__c  = '01/01/2014'  ;       
            objPolMem1.Policy_Member_External_ID__c = '00|ODS|123456787|89|89|02-01-2002';
            insert objPolMem1;            
            system.assert(objPolMem1.Relationship__c == 'Spouse');
            
            Policy_Member__c objPolMem2 = HUMTestDataHelperTest.getPolicyMember();
            objPolMem2.Member__c = objMemAccount.Id;
            objPolMem2.Policy__c = objPolicy.Id;
            objPolMem2.Relationship__c = 'Father'; 
            objPolMem2.name = '123457';
            objPolMem2.end_date__c  ='12/31/2099';
            objPolMem2.Effective_date__c  = '01/01/2014'  ;        
            objPolMem2.Policy_Member_External_ID__c = '00|ODS|123456787|89|89|02-01-2003';
            insert objPolMem2;
            system.assert(objPolMem2.Relationship__c == 'Father');
            
            Policy_Member__c objPolMem3 = HUMTestDataHelperTest.getPolicyMember();
            objPolMem3.Member__c = objMemAccountSub.Id;
            objPolMem3.Policy__c = objPolicy.Id;
            objPolMem3.Relationship__c = 'Mother'; 
            objPolMem3.name = '123459';
            objPolMem3.end_date__c  ='12/31/2099'; 
            objPolMem3.Effective_date__c  = '01/01/2015'  ;         
            objPolMem3.Policy_Member_External_ID__c = '00|ODS|123456787|89|89|02-01-2004';
            insert objPolMem3;
            system.assert(objPolMem3.Relationship__c == 'Mother');
            
            Policy_Member__c objPolMem4 = HUMTestDataHelperTest.getPolicyMember();
            objPolMem4.Member__c = objMemAccount.Id;
            objPolMem4.Policy__c = objPolicyDental.Id;
            objPolMem4.Relationship__c = 'Mother'; 
            objPolMem4.name = '123458';
            objPolMem4.end_date__c  ='12/31/2099'; 
            objPolMem4.Effective_date__c  = '01/01/2014'  ;   
            objPolMem4.subscriber__c =objPolMem3.id;     
            objPolMem4.Policy_Member_External_ID__c = '00|ODS|123456787|89|89|02-01-2005';
            insert objPolMem4;
            system.assert(objPolMem4.Relationship__c == 'Mother');   
            
            Member_Id__c memberIdObj1  = new Member_Id__c();
            memberIdObj1.Name = '123456';
            memberIdObj1.Policy__c = objPolicy.id;
            memberIdObj1.Policy_Member__c = objPolMem1.id;
            memberIdObj1.Type__c = 'Member-Id-Base';
            memberIdObj1.Member_Dependent_Code__c = '00';
            insert memberIdObj1;
                        
            Member_Id__c memberIdObj2  = new Member_Id__c();
            memberIdObj2.Name = '123457';
            memberIdObj2.Policy__c = objPolicy.id;
            memberIdObj2.Policy_Member__c =objPolMem2.id;
            memberIdObj2.Type__c = 'Member-Id-Base';
            memberIdObj2.Member_Dependent_Code__c = '00';
            insert memberIdObj2;
            
            Member_Id__c memberIdObj3  = new Member_Id__c();
            memberIdObj3.Name = '123458';
            memberIdObj3.Policy__c = objPolicyDental.id;
            memberIdObj3.Policy_Member__c = objPolMem4.id;
            memberIdObj3.Type__c = 'Member-Id-Base';
            memberIdObj3.Member_Dependent_Code__c = '00';
            insert memberIdObj3;
            
            
            Member_Id__c memberIdObj4  = new Member_Id__c();
            memberIdObj4.Name = '12345';
            memberIdObj4.Policy__c = objPolicy.id;
            memberIdObj4.Policy_Member__c = objPolMem.id;
            memberIdObj4.Type__c = 'Member-Id-Base';
            memberIdObj4.Member_Dependent_Code__c = '00';
            insert memberIdObj4;
			
			CRMFunctionality_ONOFF_Switch__c crm1 = new CRMFunctionality_ONOFF_Switch__c();
            crm1.IsON__c =true;
            crm1.Name = '728173';
            crm1.Scrum_Team__c = 'Lego';
            crm1.Story_Title__c = '	REQ - 728173 Accumulator Section with Secondary Accums';
            crm1.Release_Name__c = '05/22/2020';
            insert crm1;
            
            CRMFunctionality_ONOFF_Switch__c crm2 = new CRMFunctionality_ONOFF_Switch__c();
            crm2.IsON__c =true;
            crm2.Name = '749402';
            crm2.Scrum_Team__c = 'Lego';
            crm2.Story_Title__c = 'REQ - 749402 Accumulator Description';
            crm2.Release_Name__c = '05/22/2020';
            insert crm2;
            
            CRMFunctionality_ONOFF_Switch__c crm3 = new CRMFunctionality_ONOFF_Switch__c();
            crm3.IsON__c =true;
            crm3.Name = '791947';
            crm3.Scrum_Team__c = 'Lego';
            crm3.Story_Title__c = '	REQ - 791947 Accumulator SOAP To REST';
            crm3.Release_Name__c = '05/22/2020';
            insert crm3;
            
            CRMFunctionality_ONOFF_Switch__c crm4 = new CRMFunctionality_ONOFF_Switch__c();
            crm4.IsON__c =true;
            crm4.Name = '732113';
            crm4.Scrum_Team__c = 'Lego';
            crm4.Story_Title__c = 'REQ - 732113 Service Type Comments Ordering';
            crm4.Release_Name__c = '05/22/2020';
            insert crm4;
                         
        }        
    } 
    
    /*
    * Method Name : testAccumsPrevious
    * Description : This method Is used To test the Accums Benefits Previous funtionality
    * Return type : None
    * Paramater   : None
    */
    Static testMethod void testAccumsPrevious()
    {
        User testUser = [Select Id, Name from User where LastName='test AccumsCurrentPreviousUser1'];
        Policy_Member__c policyObj = [select id, name, Effective_Date__c, member__c, member__r.Enterprise_ID__C ,end_date__c, policy__c, policy__r.Group_Name__r.source_platform_code__c, policy__r.source_cust_cov_key__c, policy__r.Coverage_Plan_Effective_Date__c, policy__r.Coverage_Plan_End_Date__c from policy_member__c where name = '123456' limit 1];
        Member_Id__c memberIdObj = [SELECT Id, Name, Policy__c, Policy_Member__c, Member_ID_Value__c, Member_Dependent_Code__c 
									FROM Member_ID__c
                                    where Policy_Member__c =: policyObj.Id and Name =: policyObj.Name and Type__c = 'Member-Id-Base' limit 1];
        
		System.runAs(testUser)
        {
            Test.startTest();
            Test.setCurrentPage(page.BenefitsV2_VF_HUM);
            ApexPages.StandardController scon = New ApexPages.StandardController(policyObj);
            ApexPages.currentPage().getParameters().put('Id',policyObj.Id);
            Continuation con = (Continuation)AccumsType_D_HUM_V2.getAccumsDataREST(policyObj.id,'Current','06203017');
			if(con != null)
            con.continuationMethod = 'prepareServiceDataREST';
            HttpResponse resAccums = New Httpresponse();
            resAccums.setBody('{ "GetBenefitAccumsResponse":{ "GetBenefitAccumsResult":{ "BenefitAccumsResponseHeader":{ "AsOfDate":"2013-12-01T21:32:52", "ErrorMessage":"", "ErrorType":"", "ResponseTime":"20509", "TransactionDateTime":"3/31/2016 5:32:45 AM", "TransactionId":"7615769e-1203-47ff-a3cf-9ae44a26abf7", "ValidationResults":[""] }, "BenefitAccumsResponses":{ "BenefitAccumsResponse":[ { "BenefitAccumsDataFound":"true", "BenefitAccumsDataList":{ "BenefitAccumsData":[ { "AccumAmt":"0.01", "AccumBegDate":"01/01/2013", "AccumEndDate":"12/31/2013", "AccumIndivCd":"F", "AccumIndivCdDesc":"Family", "AccumLimit":"0.00", "AccumParCd":" ", "AccumParCdDesc":"", "AccumRemaining":"0", "AccumSrcCd":"L", "AccumTypeCd":"4", "AccumTypeCdDesc":"Inpatient Admissions CoPay", "BenFreqCnt":"0", "BenTypeCd":"H", "BenTypeCdDesc":"Hospital", "DeductTypeCd":" ", "FullDescription":"Family  Inpatient Admissions CoPay", "SrcApplCd":"MC", "SrcPlatformCd":"LV" }, { "AccumAmt":"0.01", "AccumBegDate":"01/01/2013", "AccumEndDate":"12/31/2013", "AccumIndivCd":"I", "AccumIndivCdDesc":"Individual", "AccumLimit":"0.00", "AccumParCd":" ", "AccumParCdDesc":"", "AccumRemaining":"0", "AccumSrcCd":"L", "AccumTypeCd":"4", "AccumTypeCdDesc":"Inpatient Admissions CoPay", "BenFreqCnt":"0", "BenTypeCd":"H", "BenTypeCdDesc":"Hospital", "DeductTypeCd":" ", "FullDescription":"Individual  Inpatient Admissions CoPay", "SrcApplCd":"MC", "SrcPlatformCd":"LV" }, { "AccumAmt":"8668.73", "AccumBegDate":"01/01/2013", "AccumEndDate":"12/31/2013", "AccumIndivCd":"I", "AccumIndivCdDesc":"Individual", "AccumLimit":"9999999.99", "AccumParCd":" ", "AccumParCdDesc":"", "AccumRemaining":"9991331.26", "AccumSrcCd":" ", "AccumTypeCd":"L", "AccumTypeCdDesc":"Lifetime Maximum", "BenFreqCnt":"0", "BenTypeCd":"M", "BenTypeCdDesc":"Medical", "DeductTypeCd":" ", "FullDescription":"Individual  Lifetime Maximum", "SrcApplCd":"MC", "SrcPlatformCd":"LV" }, { "AccumAmt":"360.00", "AccumBegDate":"01/01/2013", "AccumEndDate":"12/31/2013", "AccumIndivCd":"I", "AccumIndivCdDesc":"Individual", "AccumLimit":"6700.00", "AccumParCd":" ", "AccumParCdDesc":"", "AccumRemaining":"6340.00", "AccumSrcCd":" ", "AccumTypeCd":"O", "AccumTypeCdDesc":"Out-Of-Pocket", "BenFreqCnt":"0", "BenTypeCd":"M", "BenTypeCdDesc":"Medical", "DeductTypeCd":" ", "FullDescription":"Individual  Out-Of-Pocket", "SrcApplCd":"MC", "SrcPlatformCd":"LV" }, { "AccumAmt":"12.00", "AccumBegDate":"01/01/2013", "AccumEndDate":"12/31/2013", "AccumIndivCd":"F", "AccumIndivCdDesc":"Family", "AccumLimit":"0.00", "AccumParCd":"N", "AccumParCdDesc":"Non-Participating", "AccumRemaining":"0", "AccumSrcCd":"L", "AccumTypeCd":"T", "AccumTypeCdDesc":"Total", "BenFreqCnt":"0", "BenTypeCd":"M", "BenTypeCdDesc":"Medical", "DeductTypeCd":" ", "FullDescription":"Family Non-Participating Total", "SrcApplCd":"MC", "SrcPlatformCd":"LV" }, { "AccumAmt":"12.00", "AccumBegDate":"01/01/2013", "AccumEndDate":"12/31/2013", "AccumIndivCd":"I", "AccumIndivCdDesc":"Individual", "AccumLimit":"0.00", "AccumParCd":"N", "AccumParCdDesc":"Non-Participating", "AccumRemaining":"0", "AccumSrcCd":"L", "AccumTypeCd":"T", "AccumTypeCdDesc":"Total", "BenFreqCnt":"0", "BenTypeCd":"M", "BenTypeCdDesc":"Medical", "DeductTypeCd":" ", "FullDescription":"Individual Non-Participating Total", "SrcApplCd":"MC", "SrcPlatformCd":"LV" }, { "AccumAmt":"0.01", "AccumBegDate":"01/01/2013", "AccumEndDate":"12/31/2013", "AccumIndivCd":"F", "AccumIndivCdDesc":"Family", "AccumLimit":"0.00", "AccumParCd":" ", "AccumParCdDesc":"", "AccumRemaining":"0", "AccumSrcCd":"L", "AccumTypeCd":"2", "AccumTypeCdDesc":"Office Visit, Primary", "BenFreqCnt":"0", "BenTypeCd":"M", "BenTypeCdDesc":"Medical", "DeductTypeCd":" ", "FullDescription":"Family  Office Visit, Primary", "SrcApplCd":"MC", "SrcPlatformCd":"LV" }, { "AccumAmt":"0.01", "AccumBegDate":"01/01/2013", "AccumEndDate":"12/31/2013", "AccumIndivCd":"I", "AccumIndivCdDesc":"Individual", "AccumLimit":"0.00", "AccumParCd":" ", "AccumParCdDesc":"", "AccumRemaining":"0", "AccumSrcCd":"L", "AccumTypeCd":"2", "AccumTypeCdDesc":"Office Visit, Primary", "BenFreqCnt":"0", "BenTypeCd":"M", "BenTypeCdDesc":"Medical", "DeductTypeCd":" ", "FullDescription":"Individual  Office Visit, Primary", "SrcApplCd":"MC", "SrcPlatformCd":"LV" }, { "AccumAmt":"0.01", "AccumBegDate":"01/01/2013", "AccumEndDate":"12/31/2013", "AccumIndivCd":"F", "AccumIndivCdDesc":"Family", "AccumLimit":"0.00", "AccumParCd":" ", "AccumParCdDesc":"", "AccumRemaining":"0", "AccumSrcCd":"L", "AccumTypeCd":"3", "AccumTypeCdDesc":"Office Visit, Specialist", "BenFreqCnt":"5", "BenTypeCd":"M", "BenTypeCdDesc":"Medical", "DeductTypeCd":" ", "FullDescription":"Family  Office Visit, Specialist", "SrcApplCd":"MC", "SrcPlatformCd":"LV" }, { "AccumAmt":"0.01", "AccumBegDate":"01/01/2013", "AccumEndDate":"12/31/2013", "AccumIndivCd":"I", "AccumIndivCdDesc":"Individual", "AccumLimit":"0.00", "AccumParCd":" ", "AccumParCdDesc":"", "AccumRemaining":"0", "AccumSrcCd":"L", "AccumTypeCd":"3", "AccumTypeCdDesc":"Office Visit, Specialist", "BenFreqCnt":"5", "BenTypeCd":"M", "BenTypeCdDesc":"Medical", "DeductTypeCd":" ", "FullDescription":"Individual  Office Visit, Specialist", "SrcApplCd":"MC", "SrcPlatformCd":"LV" }, { "AccumAmt":"150.00", "AccumBegDate":"01/01/2013", "AccumEndDate":"12/31/2013", "AccumIndivCd":"F", "AccumIndivCdDesc":"Family", "AccumLimit":"0.00", "AccumParCd":"P", "AccumParCdDesc":"Participating", "AccumRemaining":"0", "AccumSrcCd":"L", "AccumTypeCd":"3", "AccumTypeCdDesc":"Office Visit, Specialist", "BenFreqCnt":"0", "BenTypeCd":"M", "BenTypeCdDesc":"Medical", "DeductTypeCd":" ", "FullDescription":"Family Participating Office Visit, Specialist", "SrcApplCd":"MC", "SrcPlatformCd":"LV" }, { "AccumAmt":"150.00", "AccumBegDate":"01/01/2013", "AccumEndDate":"12/31/2013", "AccumIndivCd":"I", "AccumIndivCdDesc":"Individual", "AccumLimit":"0.00", "AccumParCd":"P", "AccumParCdDesc":"Participating", "AccumRemaining":"0", "AccumSrcCd":"L", "AccumTypeCd":"3", "AccumTypeCdDesc":"Office Visit, Specialist", "BenFreqCnt":"0", "BenTypeCd":"M", "BenTypeCdDesc":"Medical", "DeductTypeCd":" ", "FullDescription":"Individual Participating Office Visit, Specialist", "SrcApplCd":"MC", "SrcPlatformCd":"LV" }, { "AccumAmt":"0.01", "AccumBegDate":"01/01/2013", "AccumEndDate":"12/31/2013", "AccumIndivCd":"F", "AccumIndivCdDesc":"Family", "AccumLimit":"0.00", "AccumParCd":" ", "AccumParCdDesc":"", "AccumRemaining":"0", "AccumSrcCd":"L", "AccumTypeCd":"5", "AccumTypeCdDesc":"Outpatient Surgery", "BenFreqCnt":"0", "BenTypeCd":"M", "BenTypeCdDesc":"Medical", "DeductTypeCd":" ", "FullDescription":"Family  Outpatient Surgery", "SrcApplCd":"MC", "SrcPlatformCd":"LV" }, { "AccumAmt":"0.01", "AccumBegDate":"01/01/2013", "AccumEndDate":"12/31/2013", "AccumIndivCd":"I", "AccumIndivCdDesc":"Individual", "AccumLimit":"0.00", "AccumParCd":" ", "AccumParCdDesc":"", "AccumRemaining":"0", "AccumSrcCd":"L", "AccumTypeCd":"5", "AccumTypeCdDesc":"Outpatient Surgery", "BenFreqCnt":"0", "BenTypeCd":"M", "BenTypeCdDesc":"Medical", "DeductTypeCd":" ", "FullDescription":"Individual  Outpatient Surgery", "SrcApplCd":"MC", "SrcPlatformCd":"LV" }, { "AccumAmt":"210.00", "AccumBegDate":"01/01/2013", "AccumEndDate":"12/31/2013", "AccumIndivCd":"F", "AccumIndivCdDesc":"Family", "AccumLimit":"0.00", "AccumParCd":"P", "AccumParCdDesc":"Participating", "AccumRemaining":"0", "AccumSrcCd":"L", "AccumTypeCd":"6", "AccumTypeCdDesc":"Other Amounts", "BenFreqCnt":"0", "BenTypeCd":"M", "BenTypeCdDesc":"Medical", "DeductTypeCd":" ", "FullDescription":"Family Participating Other Amounts", "SrcApplCd":"MC", "SrcPlatformCd":"LV" }, { "AccumAmt":"210.00", "AccumBegDate":"01/01/2013", "AccumEndDate":"12/31/2013", "AccumIndivCd":"I", "AccumIndivCdDesc":"Individual", "AccumLimit":"0.00", "AccumParCd":"P", "AccumParCdDesc":"Participating", "AccumRemaining":"0", "AccumSrcCd":"L", "AccumTypeCd":"6", "AccumTypeCdDesc":"Other Amounts", "BenFreqCnt":"0", "BenTypeCd":"M", "BenTypeCdDesc":"Medical", "DeductTypeCd":" ", "FullDescription":"Individual Participating Other Amounts", "SrcApplCd":"MC", "SrcPlatformCd":"LV" } ] }, "BenefitAccumsRequestInfo":{ "CustomerCoverageSequenceNumber":"1", "CustomerGenKey":"7000004181317", "IsVaild":"true", "MemberPersonGenKey":"14172618", "SubscriberPersonGenKey":"14172618", "ValidationResults":[""] }, "ErrorMessage":"", "ErrorType":"" } ] } } } }');
            resAccums.setStatusCode(200);
			if(con != null)
			{
				Map<String, HTTPRequest> mapDetails = con.getRequests();
				//system.assert(mapDetails.keyset().Contains('Continuation-5') == false);
				String reqType = '';
				for(String s : mapDetails.keyset())
				{
					reqType = s;
				}
				Test.setContinuationResponse(reqType,resAccums);
				AccumulatorWrapper_DTO_HUM  listOfDTOData = (AccumulatorWrapper_DTO_HUM)AccumsType_D_HUM_V2.prepareServiceDataREST(New PolicyMemberInfoWrapper_DTO_HUM_V2(memberIdObj, policyObj, null, null,  New List<String>{reqType},null, 'History',-1,Date.today() ) );
				system.assert(listOfDTOData.bHasMoreThanTwoYearPolicies == false);
			}
			Test.stopTest();
        }
    }           
  
    
    /*
    * Method Name : testAccumsCurrent
    * Description : This method Is used To test the Accums Benefits Current funtionality
    * Return type : None
    * Paramater   : None
    */
     Static testMethod void testAccumsCurrent()
    {
        User testUser = [Select Id, Name from User where LastName='test AccumsCurrentPreviousUser1'];
        Policy_Member__c policyObj = [select id, name, member__c, member__r.Enterprise_ID__C ,end_date__c, policy__c, policy__r.Group_Name__r.source_platform_code__c, policy__r.source_cust_cov_key__c, policy__r.Coverage_Plan_Effective_Date__c, policy__r.Coverage_Plan_End_Date__c from policy_member__c where name = '12345' limit 1];
        Member_Id__c memberIdObj = [SELECT Id, Name, Policy__c, Policy_Member__c, Member_ID_Value__c, Member_Dependent_Code__c 
									FROM Member_ID__c
                                    where Policy_Member__c =: policyObj.Id and Name =: policyObj.Name and Type__c = 'Member-Id-Base' limit 1];
        
		System.runAs(testUser)
        {
            Test.startTest();
            Test.setCurrentPage(page.BenefitsV2_VF_HUM);
            ApexPages.StandardController scon = New ApexPages.StandardController(policyObj);
            ApexPages.currentPage().getParameters().put('Id',policyObj.Id);
            Continuation con = (Continuation)AccumsType_D_HUM_V2.getAccumsDataREST(policyObj.id,'Current','06203017');
			if(con != null)
            con.continuationMethod = 'prepareServiceDataREST';
            HttpResponse resAccums = New Httpresponse();
            resAccums.setBody('{ "GetBenefitAccumsResponse":{ "GetBenefitAccumsResult":{ "BenefitAccumsResponseHeader":{ "AsOfDate":"2013-12-01T21:32:52", "ErrorMessage":"", "ErrorType":"", "ResponseTime":"20509", "TransactionDateTime":"3/31/2016 5:32:45 AM", "TransactionId":"7615769e-1203-47ff-a3cf-9ae44a26abf7", "ValidationResults":[""] }, "BenefitAccumsResponses":{ "BenefitAccumsResponse":[ { "BenefitAccumsDataFound":"true", "BenefitAccumsDataList":{ "BenefitAccumsData":[ { "AccumAmt":"0.01", "AccumBegDate":"01/01/2013", "AccumEndDate":"12/31/2013", "AccumIndivCd":"F", "AccumIndivCdDesc":"Family", "AccumLimit":"0.00", "AccumParCd":" ", "AccumParCdDesc":"", "AccumRemaining":"0", "AccumSrcCd":"L", "AccumTypeCd":"4", "AccumTypeCdDesc":"Inpatient Admissions CoPay", "BenFreqCnt":"0", "BenTypeCd":"H", "BenTypeCdDesc":"Hospital", "DeductTypeCd":" ", "FullDescription":"Family  Inpatient Admissions CoPay", "SrcApplCd":"MC", "SrcPlatformCd":"LV" }, { "AccumAmt":"0.01", "AccumBegDate":"01/01/2013", "AccumEndDate":"12/31/2013", "AccumIndivCd":"I", "AccumIndivCdDesc":"Individual", "AccumLimit":"0.00", "AccumParCd":" ", "AccumParCdDesc":"", "AccumRemaining":"0", "AccumSrcCd":"L", "AccumTypeCd":"4", "AccumTypeCdDesc":"Inpatient Admissions CoPay", "BenFreqCnt":"0", "BenTypeCd":"H", "BenTypeCdDesc":"Hospital", "DeductTypeCd":" ", "FullDescription":"Individual  Inpatient Admissions CoPay", "SrcApplCd":"MC", "SrcPlatformCd":"LV" }, { "AccumAmt":"8668.73", "AccumBegDate":"01/01/2013", "AccumEndDate":"12/31/2013", "AccumIndivCd":"I", "AccumIndivCdDesc":"Individual", "AccumLimit":"9999999.99", "AccumParCd":" ", "AccumParCdDesc":"", "AccumRemaining":"9991331.26", "AccumSrcCd":" ", "AccumTypeCd":"L", "AccumTypeCdDesc":"Lifetime Maximum", "BenFreqCnt":"0", "BenTypeCd":"M", "BenTypeCdDesc":"Medical", "DeductTypeCd":" ", "FullDescription":"Individual  Lifetime Maximum", "SrcApplCd":"MC", "SrcPlatformCd":"LV" }, { "AccumAmt":"360.00", "AccumBegDate":"01/01/2013", "AccumEndDate":"12/31/2013", "AccumIndivCd":"I", "AccumIndivCdDesc":"Individual", "AccumLimit":"6700.00", "AccumParCd":" ", "AccumParCdDesc":"", "AccumRemaining":"6340.00", "AccumSrcCd":" ", "AccumTypeCd":"O", "AccumTypeCdDesc":"Out-Of-Pocket", "BenFreqCnt":"0", "BenTypeCd":"M", "BenTypeCdDesc":"Medical", "DeductTypeCd":" ", "FullDescription":"Individual  Out-Of-Pocket", "SrcApplCd":"MC", "SrcPlatformCd":"LV" }, { "AccumAmt":"12.00", "AccumBegDate":"01/01/2013", "AccumEndDate":"12/31/2013", "AccumIndivCd":"F", "AccumIndivCdDesc":"Family", "AccumLimit":"0.00", "AccumParCd":"N", "AccumParCdDesc":"Non-Participating", "AccumRemaining":"0", "AccumSrcCd":"L", "AccumTypeCd":"T", "AccumTypeCdDesc":"Total", "BenFreqCnt":"0", "BenTypeCd":"M", "BenTypeCdDesc":"Medical", "DeductTypeCd":" ", "FullDescription":"Family Non-Participating Total", "SrcApplCd":"MC", "SrcPlatformCd":"LV" }, { "AccumAmt":"12.00", "AccumBegDate":"01/01/2013", "AccumEndDate":"12/31/2013", "AccumIndivCd":"I", "AccumIndivCdDesc":"Individual", "AccumLimit":"0.00", "AccumParCd":"N", "AccumParCdDesc":"Non-Participating", "AccumRemaining":"0", "AccumSrcCd":"L", "AccumTypeCd":"T", "AccumTypeCdDesc":"Total", "BenFreqCnt":"0", "BenTypeCd":"M", "BenTypeCdDesc":"Medical", "DeductTypeCd":" ", "FullDescription":"Individual Non-Participating Total", "SrcApplCd":"MC", "SrcPlatformCd":"LV" }, { "AccumAmt":"0.01", "AccumBegDate":"01/01/2013", "AccumEndDate":"12/31/2013", "AccumIndivCd":"F", "AccumIndivCdDesc":"Family", "AccumLimit":"0.00", "AccumParCd":" ", "AccumParCdDesc":"", "AccumRemaining":"0", "AccumSrcCd":"L", "AccumTypeCd":"2", "AccumTypeCdDesc":"Office Visit, Primary", "BenFreqCnt":"0", "BenTypeCd":"M", "BenTypeCdDesc":"Medical", "DeductTypeCd":" ", "FullDescription":"Family  Office Visit, Primary", "SrcApplCd":"MC", "SrcPlatformCd":"LV" }, { "AccumAmt":"0.01", "AccumBegDate":"01/01/2013", "AccumEndDate":"12/31/2013", "AccumIndivCd":"I", "AccumIndivCdDesc":"Individual", "AccumLimit":"0.00", "AccumParCd":" ", "AccumParCdDesc":"", "AccumRemaining":"0", "AccumSrcCd":"L", "AccumTypeCd":"2", "AccumTypeCdDesc":"Office Visit, Primary", "BenFreqCnt":"0", "BenTypeCd":"M", "BenTypeCdDesc":"Medical", "DeductTypeCd":" ", "FullDescription":"Individual  Office Visit, Primary", "SrcApplCd":"MC", "SrcPlatformCd":"LV" }, { "AccumAmt":"0.01", "AccumBegDate":"01/01/2013", "AccumEndDate":"12/31/2013", "AccumIndivCd":"F", "AccumIndivCdDesc":"Family", "AccumLimit":"0.00", "AccumParCd":" ", "AccumParCdDesc":"", "AccumRemaining":"0", "AccumSrcCd":"L", "AccumTypeCd":"3", "AccumTypeCdDesc":"Office Visit, Specialist", "BenFreqCnt":"5", "BenTypeCd":"M", "BenTypeCdDesc":"Medical", "DeductTypeCd":" ", "FullDescription":"Family  Office Visit, Specialist", "SrcApplCd":"MC", "SrcPlatformCd":"LV" }, { "AccumAmt":"0.01", "AccumBegDate":"01/01/2013", "AccumEndDate":"12/31/2013", "AccumIndivCd":"I", "AccumIndivCdDesc":"Individual", "AccumLimit":"0.00", "AccumParCd":" ", "AccumParCdDesc":"", "AccumRemaining":"0", "AccumSrcCd":"L", "AccumTypeCd":"3", "AccumTypeCdDesc":"Office Visit, Specialist", "BenFreqCnt":"5", "BenTypeCd":"M", "BenTypeCdDesc":"Medical", "DeductTypeCd":" ", "FullDescription":"Individual  Office Visit, Specialist", "SrcApplCd":"MC", "SrcPlatformCd":"LV" }, { "AccumAmt":"150.00", "AccumBegDate":"01/01/2013", "AccumEndDate":"12/31/2013", "AccumIndivCd":"F", "AccumIndivCdDesc":"Family", "AccumLimit":"0.00", "AccumParCd":"P", "AccumParCdDesc":"Participating", "AccumRemaining":"0", "AccumSrcCd":"L", "AccumTypeCd":"3", "AccumTypeCdDesc":"Office Visit, Specialist", "BenFreqCnt":"0", "BenTypeCd":"M", "BenTypeCdDesc":"Medical", "DeductTypeCd":" ", "FullDescription":"Family Participating Office Visit, Specialist", "SrcApplCd":"MC", "SrcPlatformCd":"LV" }, { "AccumAmt":"150.00", "AccumBegDate":"01/01/2013", "AccumEndDate":"12/31/2013", "AccumIndivCd":"I", "AccumIndivCdDesc":"Individual", "AccumLimit":"0.00", "AccumParCd":"P", "AccumParCdDesc":"Participating", "AccumRemaining":"0", "AccumSrcCd":"L", "AccumTypeCd":"3", "AccumTypeCdDesc":"Office Visit, Specialist", "BenFreqCnt":"0", "BenTypeCd":"M", "BenTypeCdDesc":"Medical", "DeductTypeCd":" ", "FullDescription":"Individual Participating Office Visit, Specialist", "SrcApplCd":"MC", "SrcPlatformCd":"LV" }, { "AccumAmt":"0.01", "AccumBegDate":"01/01/2013", "AccumEndDate":"12/31/2013", "AccumIndivCd":"F", "AccumIndivCdDesc":"Family", "AccumLimit":"0.00", "AccumParCd":" ", "AccumParCdDesc":"", "AccumRemaining":"0", "AccumSrcCd":"L", "AccumTypeCd":"5", "AccumTypeCdDesc":"Outpatient Surgery", "BenFreqCnt":"0", "BenTypeCd":"M", "BenTypeCdDesc":"Medical", "DeductTypeCd":" ", "FullDescription":"Family  Outpatient Surgery", "SrcApplCd":"MC", "SrcPlatformCd":"LV" }, { "AccumAmt":"0.01", "AccumBegDate":"01/01/2013", "AccumEndDate":"12/31/2013", "AccumIndivCd":"I", "AccumIndivCdDesc":"Individual", "AccumLimit":"0.00", "AccumParCd":" ", "AccumParCdDesc":"", "AccumRemaining":"0", "AccumSrcCd":"L", "AccumTypeCd":"5", "AccumTypeCdDesc":"Outpatient Surgery", "BenFreqCnt":"0", "BenTypeCd":"M", "BenTypeCdDesc":"Medical", "DeductTypeCd":" ", "FullDescription":"Individual  Outpatient Surgery", "SrcApplCd":"MC", "SrcPlatformCd":"LV" }, { "AccumAmt":"210.00", "AccumBegDate":"01/01/2013", "AccumEndDate":"12/31/2013", "AccumIndivCd":"F", "AccumIndivCdDesc":"Family", "AccumLimit":"0.00", "AccumParCd":"P", "AccumParCdDesc":"Participating", "AccumRemaining":"0", "AccumSrcCd":"L", "AccumTypeCd":"6", "AccumTypeCdDesc":"Other Amounts", "BenFreqCnt":"0", "BenTypeCd":"M", "BenTypeCdDesc":"Medical", "DeductTypeCd":" ", "FullDescription":"Family Participating Other Amounts", "SrcApplCd":"MC", "SrcPlatformCd":"LV" }, { "AccumAmt":"210.00", "AccumBegDate":"01/01/2013", "AccumEndDate":"12/31/2013", "AccumIndivCd":"I", "AccumIndivCdDesc":"Individual", "AccumLimit":"0.00", "AccumParCd":"P", "AccumParCdDesc":"Participating", "AccumRemaining":"0", "AccumSrcCd":"L", "AccumTypeCd":"6", "AccumTypeCdDesc":"Other Amounts", "BenFreqCnt":"0", "BenTypeCd":"M", "BenTypeCdDesc":"Medical", "DeductTypeCd":" ", "FullDescription":"Individual Participating Other Amounts", "SrcApplCd":"MC", "SrcPlatformCd":"LV" } ] }, "BenefitAccumsRequestInfo":{ "CustomerCoverageSequenceNumber":"1", "CustomerGenKey":"7000004181317", "IsVaild":"true", "MemberPersonGenKey":"14172618", "SubscriberPersonGenKey":"14172618", "ValidationResults":[""] }, "ErrorMessage":"", "ErrorType":"" } ] } } } }');
            resAccums.setStatusCode(200);
			if(con != null)
			{
				Map<String, HTTPRequest> mapDetails = con.getRequests();
				String reqType = '';
				for(String s : mapDetails.keyset())
				{
					reqType = s;
				}
				Test.setContinuationResponse(reqType,resAccums);
				AccumulatorWrapper_DTO_HUM  listOfDTOData = (AccumulatorWrapper_DTO_HUM)AccumsType_D_HUM_V2.prepareServiceDataREST(New PolicyMemberInfoWrapper_DTO_HUM_V2( memberIdObj,policyObj, null, null,  New List<String>{reqType},null,'Current' ,-1,Date.today()) );
				system.assert(listOfDTOData.bHasMoreThanTwoYearPolicies == false);
			}
			Test.stopTest();
        }
    } 
    
     /*
    * Method Name : testAccumsDescriptionForDental
    * Description : This method Is used To test the Accums Benefits Current funtionality
    * Return type : None
    * Paramater   : None
    */
     Static testMethod void testAccumsDescriptionForDental()
    {
        User testUser = [Select Id, Name from User where LastName='test AccumsCurrentPreviousUser1'];
        Policy_Member__c policyObj = [select id, name, member__c, member__r.Enterprise_ID__C ,end_date__c, policy__c, policy__r.Group_Name__r.source_platform_code__c, policy__r.source_cust_cov_key__c, policy__r.Coverage_Plan_Effective_Date__c, policy__r.Coverage_Plan_End_Date__c from policy_member__c where name = '123458' and policy__r.Name = 'Test Dental Policy' limit 1];
        Member_Id__c memberIdObj = [SELECT Id, Name, Policy__c, Policy_Member__c, Member_ID_Value__c, Member_Dependent_Code__c 
									FROM Member_ID__c
                                    where Policy_Member__c =: policyObj.Id and Name =: policyObj.Name and Type__c = 'Member-Id-Base' limit 1];
        
        System.runAs(testUser)
        {
            Test.startTest();
            Test.setCurrentPage(page.BenefitsV2_VF_HUM);
            ApexPages.StandardController scon = New ApexPages.StandardController(policyObj);
            ApexPages.currentPage().getParameters().put('Id',policyObj.Id);
            Continuation con = (Continuation)AccumsType_D_HUM_V2.getAccumsDataREST(policyObj.id,'Current','06203017');
			if(con != null)
            con.continuationMethod = 'prepareServiceDataREST';
            HttpResponse resAccums = New Httpresponse();
            resAccums.setBody('{ "GetBenefitAccumsResponse":{ "GetBenefitAccumsResult":{ "BenefitAccumsResponseHeader":{ "AsOfDate":"2013-12-01T21:32:52","ErrorMessage":"", "ErrorType":"", "ResponseTime":"20509", "TransactionDateTime":"3/31/2016 5:32:45 AM", "TransactionId":"7615769e-1203-47ff-a3cf-9ae44a26abf7", "ValidationResults":[""] }, "BenefitAccumsResponses":{ "BenefitAccumsResponse":[ { "BenefitAccumsDataFound":"true",  "BenefitAccumsDataList":{ "BenefitAccumsData":[ { "AccumAmt":"0.01", "AccumBegDate":"01/01/2013", "AccumEndDate":"12/31/2013",  "AccumIndivCd":"F", "AccumIndivCdDesc":"Family", "AccumLimit":"0.00", "AccumParCd":" ", "AccumParCdDesc":"", "AccumRemaining":"0",  "AccumSrcCd":"L", "AccumTypeCd":"4", "AccumTypeCdDesc":"Inpatient Admissions CoPay", "BenFreqCnt":"0", "BenTypeCd":"H", "BenTypeCdDesc":"Dental",  "DeductTypeCd":" ", "FullDescription":"Family  Inpatient Admissions CoPay", "SrcApplCd":"MC", "SrcPlatformCd":"LV","DentalAccumsResponse":{ "BenefitName": "DSTMAX","BenefitDescription": "Dental Standard Annual Maximum","IsDeductible": "false","IvrBenefitCode": "00624","MayContainCarryOverCredit": "false"} }, { "AccumAmt":"0.01", "AccumBegDate":"01/01/2013", "AccumEndDate":"12/31/2013", "AccumIndivCd":"I", "AccumIndivCdDesc":"Individual", "AccumLimit":"0.00", "AccumParCd":" ", "AccumParCdDesc":"", "AccumRemaining":"0", "AccumSrcCd":"L", "AccumTypeCd":"4", "AccumTypeCdDesc":"Inpatient Admissions CoPay", "BenFreqCnt":"0", "BenTypeCd":"H", "BenTypeCdDesc":"Medi", "DeductTypeCd":" ", "FullDescription":"Individual  Inpatient Admissions CoPay","SrcApplCd":"MC", "SrcPlatformCd":"LV","DentalAccumsResponse":{"BenefitName": "DSTMAX","BenefitDescription": "Dental Standard Annual Maximum","IsDeductible": "false","IvrBenefitCode": "00624","MayContainCarryOverCredit": "false"} },{ "AccumAmt":"8668.73", "AccumBegDate":"01/01/2013", "AccumEndDate":"12/31/2013", "AccumIndivCd":"I", "AccumIndivCdDesc":"Individual", "AccumLimit":"9999999.99", "AccumParCd":" ", "AccumParCdDesc":"", "AccumRemaining":"9991331.26", "AccumSrcCd":" ", "AccumTypeCd":"L", "AccumTypeCdDesc":"Lifetime Maximum", "BenFreqCnt":"0", "BenTypeCd":"M", "BenTypeCdDesc":"Medical", "DeductTypeCd":" ","FullDescription":"Individual  Lifetime Maximum", "SrcApplCd":"MC", "SrcPlatformCd":"LV","DentalAccumsResponse":{ "BenefitName": "DSTMAX","BenefitDescription": "Dental Standard Annual Maximum","IsDeductible": "false","IvrBenefitCode": "00624","MayContainCarryOverCredit": "false"}  }, { "AccumAmt":"360.00", "AccumBegDate":"01/01/2013", "AccumEndDate":"12/31/2013", "AccumIndivCd":"I", "AccumIndivCdDesc":"Individual", "AccumLimit":"6700.00", "AccumParCd":" ", "AccumParCdDesc":"", "AccumRemaining":"6340.00", "AccumSrcCd":" ", "AccumTypeCd":"O", "AccumTypeCdDesc":"Out-Of-Pocket", "BenFreqCnt":"0", "BenTypeCd":"M", "BenTypeCdDesc":"Dental", "DeductTypeCd":" ", "FullDescription":"Individual  Out-Of-Pocket", "SrcApplCd":"MC", "SrcPlatformCd":"LV" }, { "AccumAmt":"12.00", "AccumBegDate":"01/01/2013", "AccumEndDate":"12/31/2013", "AccumIndivCd":"F", "AccumIndivCdDesc":"Family", "AccumLimit":"0.00", "AccumParCd":"N", "AccumParCdDesc":"Non-Participating", "AccumRemaining":"0", "AccumSrcCd":"L", "AccumTypeCd":"T", "AccumTypeCdDesc":"Total", "BenFreqCnt":"0", "BenTypeCd":"M", "BenTypeCdDesc":"Medical", "DeductTypeCd":" ", "FullDescription":"Family Non-Participating Total", "SrcApplCd":"MC", "SrcPlatformCd":"LV" }, { "AccumAmt":"12.00", "AccumBegDate":"01/01/2013", "AccumEndDate":"12/31/2013", "AccumIndivCd":"I", "AccumIndivCdDesc":"Individual", "AccumLimit":"0.00", "AccumParCd":"N", "AccumParCdDesc":"Non-Participating", "AccumRemaining":"0", "AccumSrcCd":"L", "AccumTypeCd":"T", "AccumTypeCdDesc":"Total", "BenFreqCnt":"0", "BenTypeCd":"M", "BenTypeCdDesc":"Medical", "DeductTypeCd":" ", "FullDescription":"Individual Non-Participating Total", "SrcApplCd":"MC", "SrcPlatformCd":"LV" }, { "AccumAmt":"0.01", "AccumBegDate":"01/01/2013", "AccumEndDate":"12/31/2013", "AccumIndivCd":"F", "AccumIndivCdDesc":"Family", "AccumLimit":"0.00", "AccumParCd":" ", "AccumParCdDesc":"", "AccumRemaining":"0", "AccumSrcCd":"L", "AccumTypeCd":"2", "AccumTypeCdDesc":"Office Visit, Primary", "BenFreqCnt":"0", "BenTypeCd":"M", "BenTypeCdDesc":"Medical", "DeductTypeCd":" ", "FullDescription":"Family  Office Visit, Primary", "SrcApplCd":"MC", "SrcPlatformCd":"LV" }, { "AccumAmt":"0.01", "AccumBegDate":"01/01/2013", "AccumEndDate":"12/31/2013", "AccumIndivCd":"I", "AccumIndivCdDesc":"Individual", "AccumLimit":"0.00", "AccumParCd":" ", "AccumParCdDesc":"", "AccumRemaining":"0", "AccumSrcCd":"L", "AccumTypeCd":"2", "AccumTypeCdDesc":"Office Visit, Primary", "BenFreqCnt":"0", "BenTypeCd":"M", "BenTypeCdDesc":"Medical", "DeductTypeCd":" ", "FullDescription":"Individual  Office Visit, Primary", "SrcApplCd":"MC", "SrcPlatformCd":"LV" }, { "AccumAmt":"0.01", "AccumBegDate":"01/01/2013", "AccumEndDate":"12/31/2013", "AccumIndivCd":"F", "AccumIndivCdDesc":"Family", "AccumLimit":"0.00", "AccumParCd":" ", "AccumParCdDesc":"", "AccumRemaining":"0", "AccumSrcCd":"L", "AccumTypeCd":"3", "AccumTypeCdDesc":"Office Visit, Specialist", "BenFreqCnt":"5", "BenTypeCd":"M", "BenTypeCdDesc":"Medical", "DeductTypeCd":" ", "FullDescription":"Family  Office Visit, Specialist", "SrcApplCd":"MC", "SrcPlatformCd":"LV" }, { "AccumAmt":"0.01", "AccumBegDate":"01/01/2013", "AccumEndDate":"12/31/2013", "AccumIndivCd":"I", "AccumIndivCdDesc":"Individual", "AccumLimit":"0.00", "AccumParCd":" ", "AccumParCdDesc":"", "AccumRemaining":"0", "AccumSrcCd":"L", "AccumTypeCd":"3", "AccumTypeCdDesc":"Office Visit, Specialist", "BenFreqCnt":"5", "BenTypeCd":"M", "BenTypeCdDesc":"Medical", "DeductTypeCd":" ", "FullDescription":"Individual  Office Visit, Specialist", "SrcApplCd":"MC", "SrcPlatformCd":"LV" }, { "AccumAmt":"150.00", "AccumBegDate":"01/01/2013", "AccumEndDate":"12/31/2013", "AccumIndivCd":"F", "AccumIndivCdDesc":"Family", "AccumLimit":"0.00", "AccumParCd":"P", "AccumParCdDesc":"Participating", "AccumRemaining":"0", "AccumSrcCd":"L", "AccumTypeCd":"3", "AccumTypeCdDesc":"Office Visit, Specialist", "BenFreqCnt":"0", "BenTypeCd":"M", "BenTypeCdDesc":"Medical", "DeductTypeCd":" ", "FullDescription":"Family Participating Office Visit, Specialist", "SrcApplCd":"MC", "SrcPlatformCd":"LV" }, { "AccumAmt":"150.00", "AccumBegDate":"01/01/2013", "AccumEndDate":"12/31/2013", "AccumIndivCd":"I", "AccumIndivCdDesc":"Individual", "AccumLimit":"0.00", "AccumParCd":"P", "AccumParCdDesc":"Participating", "AccumRemaining":"0", "AccumSrcCd":"L", "AccumTypeCd":"3", "AccumTypeCdDesc":"Office Visit, Specialist", "BenFreqCnt":"0", "BenTypeCd":"M", "BenTypeCdDesc":"Medical", "DeductTypeCd":" ", "FullDescription":"Individual Participating Office Visit, Specialist", "SrcApplCd":"MC", "SrcPlatformCd":"LV" }, { "AccumAmt":"0.01", "AccumBegDate":"01/01/2013", "AccumEndDate":"12/31/2013", "AccumIndivCd":"F", "AccumIndivCdDesc":"Family", "AccumLimit":"0.00", "AccumParCd":" ", "AccumParCdDesc":"", "AccumRemaining":"0", "AccumSrcCd":"L", "AccumTypeCd":"5", "AccumTypeCdDesc":"Outpatient Surgery", "BenFreqCnt":"0", "BenTypeCd":"M", "BenTypeCdDesc":"Medical", "DeductTypeCd":" ", "FullDescription":"Family  Outpatient Surgery", "SrcApplCd":"MC", "SrcPlatformCd":"LV" }, { "AccumAmt":"0.01", "AccumBegDate":"01/01/2013", "AccumEndDate":"12/31/2013", "AccumIndivCd":"I", "AccumIndivCdDesc":"Individual", "AccumLimit":"0.00", "AccumParCd":" ", "AccumParCdDesc":"", "AccumRemaining":"0", "AccumSrcCd":"L", "AccumTypeCd":"5", "AccumTypeCdDesc":"Outpatient Surgery", "BenFreqCnt":"0", "BenTypeCd":"M", "BenTypeCdDesc":"Medical", "DeductTypeCd":" ", "FullDescription":"Individual  Outpatient Surgery", "SrcApplCd":"MC", "SrcPlatformCd":"LV" }, { "AccumAmt":"210.00", "AccumBegDate":"01/01/2013", "AccumEndDate":"12/31/2013", "AccumIndivCd":"F", "AccumIndivCdDesc":"Family", "AccumLimit":"0.00", "AccumParCd":"P", "AccumParCdDesc":"Participating", "AccumRemaining":"0", "AccumSrcCd":"L", "AccumTypeCd":"6", "AccumTypeCdDesc":"Other Amounts", "BenFreqCnt":"0", "BenTypeCd":"M", "BenTypeCdDesc":"Medical", "DeductTypeCd":" ", "FullDescription":"Family Participating Other Amounts", "SrcApplCd":"MC", "SrcPlatformCd":"LV" }, { "AccumAmt":"210.00", "AccumBegDate":"01/01/2013", "AccumEndDate":"12/31/2013", "AccumIndivCd":"I", "AccumIndivCdDesc":"Individual", "AccumLimit":"0.00", "AccumParCd":"P", "AccumParCdDesc":"Participating", "AccumRemaining":"0", "AccumSrcCd":"L", "AccumTypeCd":"6", "AccumTypeCdDesc":"Other Amounts", "BenFreqCnt":"0", "BenTypeCd":"M", "BenTypeCdDesc":"Medical", "DeductTypeCd":" ", "FullDescription":"Individual Participating Other Amounts", "SrcApplCd":"MC", "SrcPlatformCd":"LV" } ] }, "BenefitAccumsRequestInfo":{ "CustomerCoverageSequenceNumber":"1", "CustomerGenKey":"7000004181317", "IsVaild":"true", "MemberPersonGenKey":"14172618", "SubscriberPersonGenKey":"14172618", "ValidationResults":[""] }, "ErrorMessage":"", "ErrorType":"" } ] } } } }');
            resAccums.setStatusCode(200);
			if(con != null)
			{
				Map<String, HTTPRequest> mapDetails = con.getRequests();
				String reqType = '';
				for(String s : mapDetails.keyset())
				{
					reqType = s;
				}
				Test.setContinuationResponse(reqType,resAccums);
				AccumulatorWrapper_DTO_HUM  listOfDTOData = (AccumulatorWrapper_DTO_HUM)AccumsType_D_HUM_V2.prepareServiceDataREST(New PolicyMemberInfoWrapper_DTO_HUM_V2( memberIdObj, policyObj, null, null,  New List<String>{reqType},null,'Current' ,-1,Date.today()) );
				system.assert(listOfDTOData.bHasMoreThanTwoYearPolicies == false);
			}
			Test.stopTest();
        }
    }    
    
      /*
    * Method Name : testAccumsSecondaryData
    * Description : This method Is used To test the Accums Benefits Current funtionality
    * Return type : None
    * Paramater   : None
    */
     Static testMethod void testAccumsSecondaryData()
    {
        User testUser = [Select Id, Name from User where LastName='test AccumsCurrentPreviousUser1'];
        Policy_Member__c policyObj = [select id, name, member__c, member__r.Enterprise_ID__C ,end_date__c, policy__c, policy__r.Group_Name__r.source_platform_code__c, policy__r.source_cust_cov_key__c, policy__r.Coverage_Plan_Effective_Date__c, policy__r.Coverage_Plan_End_Date__c from policy_member__c where name = '12345' limit 1];
        Member_Id__c memberIdObj = [SELECT Id, Name, Policy__c, Policy_Member__c, Member_ID_Value__c, Member_Dependent_Code__c 
									FROM Member_ID__c
                                    where Policy_Member__c =: policyObj.Id and Name =: policyObj.Name and Type__c = 'Member-Id-Base' limit 1];
        
      
        System.runAs(testUser)
        {
            Test.startTest();
            Test.setCurrentPage(page.BenefitsV2_VF_HUM);
            ApexPages.StandardController scon = New ApexPages.StandardController(policyObj);
            ApexPages.currentPage().getParameters().put('Id',policyObj.Id);
            Continuation con = (Continuation)AccumsType_D_HUM_V2.getAccumsDataREST(policyObj.id,'Current','06203017');
			if(con != null)
            con.continuationMethod = 'prepareServiceDataREST';
            HttpResponse resAccums = New Httpresponse();
            resAccums.setBody('{ "GetBenefitAccumsResponse":{ "GetBenefitAccumsResult":{ "BenefitAccumsResponseHeader":{ "AsOfDate":"2013-12-01T21:32:52", "ErrorMessage":"", "ErrorType":"", "ResponseTime":"20509", "TransactionDateTime":"3/31/2016 5:32:45 AM", "TransactionId":"7615769e-1203-47ff-a3cf-9ae44a26abf7", "ValidationResults":[""] }, "BenefitAccumsResponses":{ "BenefitAccumsResponse":[ { "BenefitAccumsDataFound":"true", "BenefitAccumsDataList":{ "BenefitAccumsData":[ { "AccumAmt":"0.01", "AccumBegDate":"01/01/2013", "AccumEndDate":"12/31/2013", "AccumIndivCd":"F", "AccumIndivCdDesc":"Family", "AccumLimit":"0.00", "AccumParCd":" ", "AccumParCdDesc":"", "AccumRemaining":"0", "AccumSrcCd":"L", "AccumTypeCd":"4", "AccumTypeCdDesc":"Inpatient Admissions CoPay", "BenFreqCnt":"0", "BenTypeCd":"H", "BenTypeCdDesc":"Hospital", "DeductTypeCd":" ", "FullDescription":"Family  Inpatient Admissions CoPay", "SrcApplCd":"MC", "SrcPlatformCd":"LV","VisitLimit": "1","VisitUsed": "0","VisitRemaining": "1" }, { "AccumAmt":"0.01", "AccumBegDate":"01/01/2013", "AccumEndDate":"12/31/2013", "AccumIndivCd":"I", "AccumIndivCdDesc":"Individual", "AccumLimit":"0.00", "AccumParCd":" ", "AccumParCdDesc":"", "AccumRemaining":"0", "AccumSrcCd":"L", "AccumTypeCd":"4", "AccumTypeCdDesc":"Inpatient Admissions CoPay", "BenFreqCnt":"0", "BenTypeCd":"H", "BenTypeCdDesc":"Hospital", "DeductTypeCd":" ", "FullDescription":"Individual  Inpatient Admissions CoPay", "SrcApplCd":"MC", "SrcPlatformCd":"LV" }, { "AccumAmt":"8668.73", "AccumBegDate":"01/01/2013", "AccumEndDate":"12/31/2013", "AccumIndivCd":"I", "AccumIndivCdDesc":"Individual", "AccumLimit":"0", "AccumParCd":" ", "AccumParCdDesc":"", "AccumRemaining":"0", "AccumSrcCd":" ", "AccumTypeCd":"L", "AccumTypeCdDesc":"Lifetime Maximum", "BenFreqCnt":"0", "BenTypeCd":"M", "BenTypeCdDesc":"Medical", "DeductTypeCd":" ", "FullDescription":"Individual  Lifetime Maximum", "SrcApplCd":"MC", "SrcPlatformCd":"LV" }, { "AccumAmt":"360.00", "AccumBegDate":"01/01/2013", "AccumEndDate":"12/31/2013", "AccumIndivCd":"I", "AccumIndivCdDesc":"Individual", "AccumLimit":"0.0", "AccumParCd":" ", "AccumParCdDesc":"", "AccumRemaining":"0.00", "AccumSrcCd":" ", "AccumTypeCd":"O", "AccumTypeCdDesc":"Out-Of-Pocket", "BenFreqCnt":"0", "BenTypeCd":"M", "BenTypeCdDesc":"Medical", "DeductTypeCd":" ", "FullDescription":"Individual  Out-Of-Pocket", "SrcApplCd":"MC", "SrcPlatformCd":"LV" }, { "AccumAmt":"12.00", "AccumBegDate":"01/01/2013", "AccumEndDate":"12/31/2013", "AccumIndivCd":"F", "AccumIndivCdDesc":"Family", "AccumLimit":"0.00", "AccumParCd":"N", "AccumParCdDesc":"Non-Participating", "AccumRemaining":"0", "AccumSrcCd":"L", "AccumTypeCd":"T", "AccumTypeCdDesc":"Total", "BenFreqCnt":"0", "BenTypeCd":"M", "BenTypeCdDesc":"Medical", "DeductTypeCd":" ", "FullDescription":"Family Non-Participating Total", "SrcApplCd":"MC", "SrcPlatformCd":"LV" }, { "AccumAmt":"12.00", "AccumBegDate":"01/01/2013", "AccumEndDate":"12/31/2013", "AccumIndivCd":"I", "AccumIndivCdDesc":"Individual", "AccumLimit":"0.00", "AccumParCd":"N", "AccumParCdDesc":"Non-Participating", "AccumRemaining":"0", "AccumSrcCd":"L", "AccumTypeCd":"T", "AccumTypeCdDesc":"Total", "BenFreqCnt":"0", "BenTypeCd":"M", "BenTypeCdDesc":"Medical", "DeductTypeCd":" ", "FullDescription":"Individual Non-Participating Total", "SrcApplCd":"MC", "SrcPlatformCd":"LV" }, { "AccumAmt":"0.01", "AccumBegDate":"01/01/2013", "AccumEndDate":"12/31/2013", "AccumIndivCd":"F", "AccumIndivCdDesc":"Family", "AccumLimit":"0.00", "AccumParCd":" ", "AccumParCdDesc":"", "AccumRemaining":"0", "AccumSrcCd":"L", "AccumTypeCd":"2", "AccumTypeCdDesc":"Office Visit, Primary", "BenFreqCnt":"0", "BenTypeCd":"M", "BenTypeCdDesc":"Medical", "DeductTypeCd":" ", "FullDescription":"Family  Office Visit, Primary", "SrcApplCd":"MC", "SrcPlatformCd":"LV" }, { "AccumAmt":"0.01", "AccumBegDate":"01/01/2013", "AccumEndDate":"12/31/2013", "AccumIndivCd":"I", "AccumIndivCdDesc":"Individual", "AccumLimit":"0.00", "AccumParCd":" ", "AccumParCdDesc":"", "AccumRemaining":"0", "AccumSrcCd":"L", "AccumTypeCd":"2", "AccumTypeCdDesc":"Office Visit, Primary", "BenFreqCnt":"0", "BenTypeCd":"M", "BenTypeCdDesc":"Medical", "DeductTypeCd":" ", "FullDescription":"Individual  Office Visit, Primary", "SrcApplCd":"MC", "SrcPlatformCd":"LV" }, { "AccumAmt":"0.01", "AccumBegDate":"01/01/2013", "AccumEndDate":"12/31/2013", "AccumIndivCd":"F", "AccumIndivCdDesc":"Family", "AccumLimit":"0.00", "AccumParCd":" ", "AccumParCdDesc":"", "AccumRemaining":"0", "AccumSrcCd":"L", "AccumTypeCd":"3", "AccumTypeCdDesc":"Office Visit, Specialist", "BenFreqCnt":"5", "BenTypeCd":"M", "BenTypeCdDesc":"Medical", "DeductTypeCd":" ", "FullDescription":"Family  Office Visit, Specialist", "SrcApplCd":"MC", "SrcPlatformCd":"LV" }, { "AccumAmt":"0.01", "AccumBegDate":"01/01/2013", "AccumEndDate":"12/31/2013", "AccumIndivCd":"I", "AccumIndivCdDesc":"Individual", "AccumLimit":"0.00", "AccumParCd":" ", "AccumParCdDesc":"", "AccumRemaining":"0", "AccumSrcCd":"L", "AccumTypeCd":"3", "AccumTypeCdDesc":"Office Visit, Specialist", "BenFreqCnt":"5", "BenTypeCd":"M", "BenTypeCdDesc":"Medical", "DeductTypeCd":" ", "FullDescription":"Individual  Office Visit, Specialist", "SrcApplCd":"MC", "SrcPlatformCd":"LV" }, { "AccumAmt":"150.00", "AccumBegDate":"01/01/2013", "AccumEndDate":"12/31/2013", "AccumIndivCd":"F", "AccumIndivCdDesc":"Family", "AccumLimit":"0.00", "AccumParCd":"P", "AccumParCdDesc":"Participating", "AccumRemaining":"0", "AccumSrcCd":"L", "AccumTypeCd":"3", "AccumTypeCdDesc":"Office Visit, Specialist", "BenFreqCnt":"0", "BenTypeCd":"M", "BenTypeCdDesc":"Medical", "DeductTypeCd":" ", "FullDescription":"Family Participating Office Visit, Specialist", "SrcApplCd":"MC", "SrcPlatformCd":"LV" }, { "AccumAmt":"150.00", "AccumBegDate":"01/01/2013", "AccumEndDate":"12/31/2013", "AccumIndivCd":"I", "AccumIndivCdDesc":"Individual", "AccumLimit":"0.00", "AccumParCd":"P", "AccumParCdDesc":"Participating", "AccumRemaining":"0", "AccumSrcCd":"L", "AccumTypeCd":"3", "AccumTypeCdDesc":"Office Visit, Specialist", "BenFreqCnt":"0", "BenTypeCd":"M", "BenTypeCdDesc":"Medical", "DeductTypeCd":" ", "FullDescription":"Individual Participating Office Visit, Specialist", "SrcApplCd":"MC", "SrcPlatformCd":"LV" }, { "AccumAmt":"0.01", "AccumBegDate":"01/01/2013", "AccumEndDate":"12/31/2013", "AccumIndivCd":"F", "AccumIndivCdDesc":"Family", "AccumLimit":"0.00", "AccumParCd":" ", "AccumParCdDesc":"", "AccumRemaining":"0", "AccumSrcCd":"L", "AccumTypeCd":"5", "AccumTypeCdDesc":"Outpatient Surgery", "BenFreqCnt":"0", "BenTypeCd":"M", "BenTypeCdDesc":"Medical", "DeductTypeCd":" ", "FullDescription":"Family  Outpatient Surgery", "SrcApplCd":"MC", "SrcPlatformCd":"LV" }, { "AccumAmt":"0.01", "AccumBegDate":"01/01/2013", "AccumEndDate":"12/31/2013", "AccumIndivCd":"I", "AccumIndivCdDesc":"Individual", "AccumLimit":"0.00", "AccumParCd":" ", "AccumParCdDesc":"", "AccumRemaining":"0", "AccumSrcCd":"L", "AccumTypeCd":"5", "AccumTypeCdDesc":"Outpatient Surgery", "BenFreqCnt":"0", "BenTypeCd":"M", "BenTypeCdDesc":"Medical", "DeductTypeCd":" ", "FullDescription":"Individual  Outpatient Surgery", "SrcApplCd":"MC", "SrcPlatformCd":"LV" }, { "AccumAmt":"210.00", "AccumBegDate":"01/01/2013", "AccumEndDate":"12/31/2013", "AccumIndivCd":"F", "AccumIndivCdDesc":"Family", "AccumLimit":"0.00", "AccumParCd":"P", "AccumParCdDesc":"Participating", "AccumRemaining":"0", "AccumSrcCd":"L", "AccumTypeCd":"6", "AccumTypeCdDesc":"Other Amounts", "BenFreqCnt":"0", "BenTypeCd":"M", "BenTypeCdDesc":"Medical", "DeductTypeCd":" ", "FullDescription":"Family Participating Other Amounts", "SrcApplCd":"MC", "SrcPlatformCd":"LV" }, { "AccumAmt":"210.00", "AccumBegDate":"01/01/2013", "AccumEndDate":"12/31/2013", "AccumIndivCd":"I", "AccumIndivCdDesc":"Individual", "AccumLimit":"0.00", "AccumParCd":"P", "AccumParCdDesc":"Participating", "AccumRemaining":"0", "AccumSrcCd":"L", "AccumTypeCd":"6", "AccumTypeCdDesc":"Other Amounts", "BenFreqCnt":"0", "BenTypeCd":"M", "BenTypeCdDesc":"Medical", "DeductTypeCd":" ", "FullDescription":"Individual Participating Other Amounts", "SrcApplCd":"MC", "SrcPlatformCd":"LV" } ] }, "BenefitAccumsRequestInfo":{ "CustomerCoverageSequenceNumber":"1", "CustomerGenKey":"7000004181317", "IsVaild":"true", "MemberPersonGenKey":"14172618", "SubscriberPersonGenKey":"14172618", "ValidationResults":[""] }, "ErrorMessage":"", "ErrorType":"" } ] } } } }');
            resAccums.setStatusCode(200);
			if(con != null)
			{
				Map<String, HTTPRequest> mapDetails = con.getRequests();            
				String reqType = '';
				for(String s : mapDetails.keyset())
				{
					reqType = s;
				}
				Test.setContinuationResponse(reqType,resAccums);
				AccumulatorWrapper_DTO_HUM  listOfDTOData = (AccumulatorWrapper_DTO_HUM)AccumsType_D_HUM_V2.prepareServiceDataREST(New PolicyMemberInfoWrapper_DTO_HUM_V2(memberIdObj, policyObj, null, null,  New List<String>{reqType},null,'Current' ,-1,Date.today()) );
				system.assert(listOfDTOData.bHasMoreThanTwoYearPolicies == false);
			}
			Test.stopTest();
        }
    }
    
      /*
    * Method Name : testAccumsDataException
    * Description : This method Is used To test the Accums Data Exception scenero
    * Return type : None
    * Paramater   : None
    */
    Static testMethod void testAccumsDataException()
      {
        User testUser = [Select Id, Name, LastName from User where LastName='test AccumsCurrentPreviousUser1'];
		system.assert(testUser.LastName == 'test AccumsCurrentPreviousUser1');
        Policy_Member__c policyObj = [select id, name, member__c, member__r.Enterprise_ID__C ,end_date__c, policy__c, policy__r.Group_Name__r.source_platform_code__c, policy__r.source_cust_cov_key__c, policy__r.Coverage_Plan_Effective_Date__c, policy__r.Coverage_Plan_End_Date__c from policy_member__c where name = '12345' limit 1];
        System.runAs(testUser)
          {
            Test.startTest();
            Test.setCurrentPage(page.BenefitsV2_VF_HUM);
            AccumsType_D_HUM_V2.getAccumsDataREST(policyObj.id,'','');
			Test.StopTest();
          }
       }
}
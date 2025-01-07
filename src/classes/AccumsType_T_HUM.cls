/*******************************************************************************************************************************
Apex Class Name :  AccumsType_T_HUM.cls
Version: 1.0
Created On      : 03/21/2016
Function        : Class contains test methods AccumsType_D_HUM Class.

Modification Log :  
 * Developer Name            Code Review                Date                       Description
 *------------------------------------------------------------------------------------------------------------------------------
* Avinash Choubey                                       03/31/2016                 Original Version
* Avinash Choubey            31077                      04/18/2016                 Design Change in All Accums Req ( 237142, 246888 etc)
* Avinash Choubey            28561                      05/28/2016                 Changes to accomodate changes in AccumsType_D_HUM to fix  ( 225237, 226287)
* Avinash Choubey            29988                      06/09/2016                 Changes to fix Defect# 228609
* Avinash Choubey            32583                      07/13/2016                 Removed MBE Dependency for Gen Key REQ - 269975.
* Praveen Parimi             56654                      01/13/2017                 Changed the Benefits page to point to New version. 
* Muralidhar Kollu                                 		18/08/2017                 REQ - 326297 - change of AsOfDate Previous section logic
* Shivam Anand               140629                     20/10/2017                 REQ - 333058 /REQ - 332334 -Code coverage fix
* Riya Agarwal                                          02/11/2021			   	   REQ - 1669832 - PR00094226 - CR AEP readiness -Research - Critical Security Issues (slide 11)	
* Suraj Patil										    07/27/2021		  		   REQ - 1928698 PR00094226 - CR AEP readiness - Low Security Issues 
 ********************************************************************************************************************************/
@isTest
Private Class AccumsType_T_HUM
{
    /*
    * Method Name : setUpTestData
    * Description : This method creates test data For test Class
    * Return type : None
    * Paramater   : None
    */
    @testSetup static void setUpTestData()
    {
        User objUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert objUser;
        system.assertEquals(objUser.lastname=='test User1',true);
        system.runAs(objUser)
        {
            HUM_Webservice_Callout__c AccumsServiceDetails = New HUM_Webservice_Callout__c(Name = 'AccumsService',Certificate_Name__c = 'Humana_GuidanceCare_Mercury' ,Active__c = true,End_Point_URL__c ='https://int-crmservicews.humana.com/AccumsService/GetBenefitAccums' ,Timeout__c = '12000',Request_Type__c = 'POST',Content_Type__c ='application/json' );
            insert AccumsServiceDetails;
            system.assert(HUM_Webservice_Callout__c.getValues('AccumsService').Name == 'AccumsService');
            
            HUM_Webservice_Callout__c MBEServiceDetails = New HUM_Webservice_Callout__c(Name = 'MBEService',Certificate_Name__c = 'Humana_GuidanceCare_Mercury' ,Active__c = true,End_Point_URL__c ='https://int-crmservicews.humana.com/MBEServiceREST/GetMember' ,Timeout__c = '12000',Request_Type__c = 'POST',Content_Type__c ='application/json' );
            insert MBEServiceDetails;
            system.assert(HUM_Webservice_Callout__c.getValues('MBEService').Name == 'MBEService');
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
            objPolMem2.end_date__c  ='12/31/2014'; 
            objPolMem2.Effective_date__c  = '01/01/2014'  ;        
            objPolMem2.Policy_Member_External_ID__c = '00|ODS|123456787|89|89|02-01-2003';
            insert objPolMem2;
            system.assert(objPolMem2.Relationship__c == 'Father');
            
            Policy_Member__c objPolMem3 = HUMTestDataHelperTest.getPolicyMember();
            objPolMem3.Member__c = objMemAccountSub.Id;
            objPolMem3.Policy__c = objPolicy.Id;
            objPolMem3.Relationship__c = 'Mother'; 
            objPolMem3.name = '123459';
            objPolMem3.end_date__c  ='12/31/2015'; 
            objPolMem3.Effective_date__c  = '01/01/2015'  ;         
            objPolMem3.Policy_Member_External_ID__c = '00|ODS|123456787|89|89|02-01-2004';
            insert objPolMem3;
            system.assert(objPolMem3.Relationship__c == 'Mother');
            
            Policy_Member__c objPolMem4 = HUMTestDataHelperTest.getPolicyMember();
            objPolMem4.Member__c = objMemAccount.Id;
            objPolMem4.Policy__c = objPolicy.Id;
            objPolMem4.Relationship__c = 'Mother'; 
            objPolMem4.name = '123458';
            objPolMem4.end_date__c  ='12/31/2014'; 
            objPolMem4.Effective_date__c  = '01/01/2014'  ;   
            objPolMem4.subscriber__c =objPolMem3.id;     
            objPolMem4.Policy_Member_External_ID__c = '00|ODS|123456787|89|89|02-01-2005';
            insert objPolMem4;
            system.assert(objPolMem4.Relationship__c == 'Mother');
            
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
        User testUser = [Select Id, Name from User where LastName='test User1'];
        Policy_Member__c policyObj = [select id, name, Effective_Date__c, member__c, member__r.Enterprise_ID__C ,end_date__c, policy__c, policy__r.Group_Name__r.source_platform_code__c, policy__r.source_cust_cov_key__c, policy__r.Coverage_Plan_Effective_Date__c, policy__r.Coverage_Plan_End_Date__c from policy_member__c where name = '123456' limit 1];
        System.runAs(testUser)
        {
            Test.startTest();
            Test.setCurrentPage(page.BenefitsV2_VF_HUM);
            ApexPages.StandardController scon = New ApexPages.StandardController(policyObj);
            ApexPages.currentPage().getParameters().put('Id',policyObj.Id);
            Continuation con = (Continuation)AccumsType_D_HUM.getAccumsData(policyObj.id,'Current','06203017');
            con.continuationMethod = 'prepareServiceData';
            HttpResponse resAccums = New Httpresponse();
            resAccums.setBody('{ "GetBenefitAccumsResponse":{ "GetBenefitAccumsResult":{ "BenefitAccumsResponseHeader":{ "AsOfDate":"2013-12-01T21:32:52", "ErrorMessage":"", "ErrorType":"", "ResponseTime":"20509", "TransactionDateTime":"3/31/2016 5:32:45 AM", "TransactionId":"7615769e-1203-47ff-a3cf-9ae44a26abf7", "ValidationResults":"" }, "BenefitAccumsResponses":{ "BenefitAccumsResponse":[ { "BenefitAccumsDataFound":"true", "BenefitAccumsDataList":{ "BenefitAccumsData":[ { "AccumAmt":"0.01", "AccumBegDate":"01/01/2013", "AccumEndDate":"12/31/2013", "AccumIndivCd":"F", "AccumIndivCdDesc":"Family", "AccumLimit":"0.00", "AccumParCd":" ", "AccumParCdDesc":"", "AccumRemaining":"0", "AccumSrcCd":"L", "AccumTypeCd":"4", "AccumTypeCdDesc":"Inpatient Admissions CoPay", "BenFreqCnt":"0", "BenTypeCd":"H", "BenTypeCdDesc":"Hospital", "DeductTypeCd":" ", "FullDescription":"Family  Inpatient Admissions CoPay", "SrcApplCd":"MC", "SrcPlatformCd":"LV" }, { "AccumAmt":"0.01", "AccumBegDate":"01/01/2013", "AccumEndDate":"12/31/2013", "AccumIndivCd":"I", "AccumIndivCdDesc":"Individual", "AccumLimit":"0.00", "AccumParCd":" ", "AccumParCdDesc":"", "AccumRemaining":"0", "AccumSrcCd":"L", "AccumTypeCd":"4", "AccumTypeCdDesc":"Inpatient Admissions CoPay", "BenFreqCnt":"0", "BenTypeCd":"H", "BenTypeCdDesc":"Hospital", "DeductTypeCd":" ", "FullDescription":"Individual  Inpatient Admissions CoPay", "SrcApplCd":"MC", "SrcPlatformCd":"LV" }, { "AccumAmt":"8668.73", "AccumBegDate":"01/01/2013", "AccumEndDate":"12/31/2013", "AccumIndivCd":"I", "AccumIndivCdDesc":"Individual", "AccumLimit":"9999999.99", "AccumParCd":" ", "AccumParCdDesc":"", "AccumRemaining":"9991331.26", "AccumSrcCd":" ", "AccumTypeCd":"L", "AccumTypeCdDesc":"Lifetime Maximum", "BenFreqCnt":"0", "BenTypeCd":"M", "BenTypeCdDesc":"Medical", "DeductTypeCd":" ", "FullDescription":"Individual  Lifetime Maximum", "SrcApplCd":"MC", "SrcPlatformCd":"LV" }, { "AccumAmt":"360.00", "AccumBegDate":"01/01/2013", "AccumEndDate":"12/31/2013", "AccumIndivCd":"I", "AccumIndivCdDesc":"Individual", "AccumLimit":"6700.00", "AccumParCd":" ", "AccumParCdDesc":"", "AccumRemaining":"6340.00", "AccumSrcCd":" ", "AccumTypeCd":"O", "AccumTypeCdDesc":"Out-Of-Pocket", "BenFreqCnt":"0", "BenTypeCd":"M", "BenTypeCdDesc":"Medical", "DeductTypeCd":" ", "FullDescription":"Individual  Out-Of-Pocket", "SrcApplCd":"MC", "SrcPlatformCd":"LV" }, { "AccumAmt":"12.00", "AccumBegDate":"01/01/2013", "AccumEndDate":"12/31/2013", "AccumIndivCd":"F", "AccumIndivCdDesc":"Family", "AccumLimit":"0.00", "AccumParCd":"N", "AccumParCdDesc":"Non-Participating", "AccumRemaining":"0", "AccumSrcCd":"L", "AccumTypeCd":"T", "AccumTypeCdDesc":"Total", "BenFreqCnt":"0", "BenTypeCd":"M", "BenTypeCdDesc":"Medical", "DeductTypeCd":" ", "FullDescription":"Family Non-Participating Total", "SrcApplCd":"MC", "SrcPlatformCd":"LV" }, { "AccumAmt":"12.00", "AccumBegDate":"01/01/2013", "AccumEndDate":"12/31/2013", "AccumIndivCd":"I", "AccumIndivCdDesc":"Individual", "AccumLimit":"0.00", "AccumParCd":"N", "AccumParCdDesc":"Non-Participating", "AccumRemaining":"0", "AccumSrcCd":"L", "AccumTypeCd":"T", "AccumTypeCdDesc":"Total", "BenFreqCnt":"0", "BenTypeCd":"M", "BenTypeCdDesc":"Medical", "DeductTypeCd":" ", "FullDescription":"Individual Non-Participating Total", "SrcApplCd":"MC", "SrcPlatformCd":"LV" }, { "AccumAmt":"0.01", "AccumBegDate":"01/01/2013", "AccumEndDate":"12/31/2013", "AccumIndivCd":"F", "AccumIndivCdDesc":"Family", "AccumLimit":"0.00", "AccumParCd":" ", "AccumParCdDesc":"", "AccumRemaining":"0", "AccumSrcCd":"L", "AccumTypeCd":"2", "AccumTypeCdDesc":"Office Visit, Primary", "BenFreqCnt":"0", "BenTypeCd":"M", "BenTypeCdDesc":"Medical", "DeductTypeCd":" ", "FullDescription":"Family  Office Visit, Primary", "SrcApplCd":"MC", "SrcPlatformCd":"LV" }, { "AccumAmt":"0.01", "AccumBegDate":"01/01/2013", "AccumEndDate":"12/31/2013", "AccumIndivCd":"I", "AccumIndivCdDesc":"Individual", "AccumLimit":"0.00", "AccumParCd":" ", "AccumParCdDesc":"", "AccumRemaining":"0", "AccumSrcCd":"L", "AccumTypeCd":"2", "AccumTypeCdDesc":"Office Visit, Primary", "BenFreqCnt":"0", "BenTypeCd":"M", "BenTypeCdDesc":"Medical", "DeductTypeCd":" ", "FullDescription":"Individual  Office Visit, Primary", "SrcApplCd":"MC", "SrcPlatformCd":"LV" }, { "AccumAmt":"0.01", "AccumBegDate":"01/01/2013", "AccumEndDate":"12/31/2013", "AccumIndivCd":"F", "AccumIndivCdDesc":"Family", "AccumLimit":"0.00", "AccumParCd":" ", "AccumParCdDesc":"", "AccumRemaining":"0", "AccumSrcCd":"L", "AccumTypeCd":"3", "AccumTypeCdDesc":"Office Visit, Specialist", "BenFreqCnt":"5", "BenTypeCd":"M", "BenTypeCdDesc":"Medical", "DeductTypeCd":" ", "FullDescription":"Family  Office Visit, Specialist", "SrcApplCd":"MC", "SrcPlatformCd":"LV" }, { "AccumAmt":"0.01", "AccumBegDate":"01/01/2013", "AccumEndDate":"12/31/2013", "AccumIndivCd":"I", "AccumIndivCdDesc":"Individual", "AccumLimit":"0.00", "AccumParCd":" ", "AccumParCdDesc":"", "AccumRemaining":"0", "AccumSrcCd":"L", "AccumTypeCd":"3", "AccumTypeCdDesc":"Office Visit, Specialist", "BenFreqCnt":"5", "BenTypeCd":"M", "BenTypeCdDesc":"Medical", "DeductTypeCd":" ", "FullDescription":"Individual  Office Visit, Specialist", "SrcApplCd":"MC", "SrcPlatformCd":"LV" }, { "AccumAmt":"150.00", "AccumBegDate":"01/01/2013", "AccumEndDate":"12/31/2013", "AccumIndivCd":"F", "AccumIndivCdDesc":"Family", "AccumLimit":"0.00", "AccumParCd":"P", "AccumParCdDesc":"Participating", "AccumRemaining":"0", "AccumSrcCd":"L", "AccumTypeCd":"3", "AccumTypeCdDesc":"Office Visit, Specialist", "BenFreqCnt":"0", "BenTypeCd":"M", "BenTypeCdDesc":"Medical", "DeductTypeCd":" ", "FullDescription":"Family Participating Office Visit, Specialist", "SrcApplCd":"MC", "SrcPlatformCd":"LV" }, { "AccumAmt":"150.00", "AccumBegDate":"01/01/2013", "AccumEndDate":"12/31/2013", "AccumIndivCd":"I", "AccumIndivCdDesc":"Individual", "AccumLimit":"0.00", "AccumParCd":"P", "AccumParCdDesc":"Participating", "AccumRemaining":"0", "AccumSrcCd":"L", "AccumTypeCd":"3", "AccumTypeCdDesc":"Office Visit, Specialist", "BenFreqCnt":"0", "BenTypeCd":"M", "BenTypeCdDesc":"Medical", "DeductTypeCd":" ", "FullDescription":"Individual Participating Office Visit, Specialist", "SrcApplCd":"MC", "SrcPlatformCd":"LV" }, { "AccumAmt":"0.01", "AccumBegDate":"01/01/2013", "AccumEndDate":"12/31/2013", "AccumIndivCd":"F", "AccumIndivCdDesc":"Family", "AccumLimit":"0.00", "AccumParCd":" ", "AccumParCdDesc":"", "AccumRemaining":"0", "AccumSrcCd":"L", "AccumTypeCd":"5", "AccumTypeCdDesc":"Outpatient Surgery", "BenFreqCnt":"0", "BenTypeCd":"M", "BenTypeCdDesc":"Medical", "DeductTypeCd":" ", "FullDescription":"Family  Outpatient Surgery", "SrcApplCd":"MC", "SrcPlatformCd":"LV" }, { "AccumAmt":"0.01", "AccumBegDate":"01/01/2013", "AccumEndDate":"12/31/2013", "AccumIndivCd":"I", "AccumIndivCdDesc":"Individual", "AccumLimit":"0.00", "AccumParCd":" ", "AccumParCdDesc":"", "AccumRemaining":"0", "AccumSrcCd":"L", "AccumTypeCd":"5", "AccumTypeCdDesc":"Outpatient Surgery", "BenFreqCnt":"0", "BenTypeCd":"M", "BenTypeCdDesc":"Medical", "DeductTypeCd":" ", "FullDescription":"Individual  Outpatient Surgery", "SrcApplCd":"MC", "SrcPlatformCd":"LV" }, { "AccumAmt":"210.00", "AccumBegDate":"01/01/2013", "AccumEndDate":"12/31/2013", "AccumIndivCd":"F", "AccumIndivCdDesc":"Family", "AccumLimit":"0.00", "AccumParCd":"P", "AccumParCdDesc":"Participating", "AccumRemaining":"0", "AccumSrcCd":"L", "AccumTypeCd":"6", "AccumTypeCdDesc":"Other Amounts", "BenFreqCnt":"0", "BenTypeCd":"M", "BenTypeCdDesc":"Medical", "DeductTypeCd":" ", "FullDescription":"Family Participating Other Amounts", "SrcApplCd":"MC", "SrcPlatformCd":"LV" }, { "AccumAmt":"210.00", "AccumBegDate":"01/01/2013", "AccumEndDate":"12/31/2013", "AccumIndivCd":"I", "AccumIndivCdDesc":"Individual", "AccumLimit":"0.00", "AccumParCd":"P", "AccumParCdDesc":"Participating", "AccumRemaining":"0", "AccumSrcCd":"L", "AccumTypeCd":"6", "AccumTypeCdDesc":"Other Amounts", "BenFreqCnt":"0", "BenTypeCd":"M", "BenTypeCdDesc":"Medical", "DeductTypeCd":" ", "FullDescription":"Individual Participating Other Amounts", "SrcApplCd":"MC", "SrcPlatformCd":"LV" } ] }, "BenefitAccumsRequestInfo":{ "CustomerCoverageSequenceNumber":"1", "CustomerGenKey":"7000004181317", "IsVaild":"true", "MemberPersonGenKey":"14172618", "SubscriberPersonGenKey":"14172618", "ValidationResults":"" }, "ErrorMessage":"", "ErrorType":"" } ] } } } }');
            resAccums.setStatusCode(200);
            Map<String, HTTPRequest> mapDetails = con.getRequests();
            //system.assert(mapDetails.keyset().Contains('Continuation-5') == false);
            String reqType = '';
            for(String s : mapDetails.keyset())
            {
                reqType = s;
            }
            Test.setContinuationResponse(reqType,resAccums);
            AccumulatorWrapper_DTO_HUM  listOfDTOData = (AccumulatorWrapper_DTO_HUM)AccumsType_D_HUM.prepareServiceData(New PolicyMemberInfoWrapper_DTO_HUM( policyObj, null, null,  New List<String>{reqType},null, 'History',-1,Date.today() ) );
            system.assert(listOfDTOData.bHasMoreThanTwoYearPolicies == false);
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
        User testUser = [Select Id, Name from User where LastName='test User1'];
        Policy_Member__c policyObj = [select id, name, member__c, member__r.Enterprise_ID__C ,end_date__c, policy__c, policy__r.Group_Name__r.source_platform_code__c, policy__r.source_cust_cov_key__c, policy__r.Coverage_Plan_Effective_Date__c, policy__r.Coverage_Plan_End_Date__c from policy_member__c where name = '12345' limit 1];
        System.runAs(testUser)
        {
            Test.startTest();
            Test.setCurrentPage(page.BenefitsV2_VF_HUM);
            ApexPages.StandardController scon = New ApexPages.StandardController(policyObj);
            ApexPages.currentPage().getParameters().put('Id',policyObj.Id);
            Continuation con = (Continuation)AccumsType_D_HUM.getAccumsData(policyObj.id,'Current','06203017');
            con.continuationMethod = 'prepareServiceData';
            HttpResponse resAccums = New Httpresponse();
            resAccums.setBody('{ "GetBenefitAccumsResponse":{ "GetBenefitAccumsResult":{ "BenefitAccumsResponseHeader":{ "AsOfDate":"2013-12-01T21:32:52", "ErrorMessage":"", "ErrorType":"", "ResponseTime":"20509", "TransactionDateTime":"3/31/2016 5:32:45 AM", "TransactionId":"7615769e-1203-47ff-a3cf-9ae44a26abf7", "ValidationResults":"" }, "BenefitAccumsResponses":{ "BenefitAccumsResponse":[ { "BenefitAccumsDataFound":"true", "BenefitAccumsDataList":{ "BenefitAccumsData":[ { "AccumAmt":"0.01", "AccumBegDate":"01/01/2013", "AccumEndDate":"12/31/2013", "AccumIndivCd":"F", "AccumIndivCdDesc":"Family", "AccumLimit":"0.00", "AccumParCd":" ", "AccumParCdDesc":"", "AccumRemaining":"0", "AccumSrcCd":"L", "AccumTypeCd":"4", "AccumTypeCdDesc":"Inpatient Admissions CoPay", "BenFreqCnt":"0", "BenTypeCd":"H", "BenTypeCdDesc":"Hospital", "DeductTypeCd":" ", "FullDescription":"Family  Inpatient Admissions CoPay", "SrcApplCd":"MC", "SrcPlatformCd":"LV" }, { "AccumAmt":"0.01", "AccumBegDate":"01/01/2013", "AccumEndDate":"12/31/2013", "AccumIndivCd":"I", "AccumIndivCdDesc":"Individual", "AccumLimit":"0.00", "AccumParCd":" ", "AccumParCdDesc":"", "AccumRemaining":"0", "AccumSrcCd":"L", "AccumTypeCd":"4", "AccumTypeCdDesc":"Inpatient Admissions CoPay", "BenFreqCnt":"0", "BenTypeCd":"H", "BenTypeCdDesc":"Hospital", "DeductTypeCd":" ", "FullDescription":"Individual  Inpatient Admissions CoPay", "SrcApplCd":"MC", "SrcPlatformCd":"LV" }, { "AccumAmt":"8668.73", "AccumBegDate":"01/01/2013", "AccumEndDate":"12/31/2013", "AccumIndivCd":"I", "AccumIndivCdDesc":"Individual", "AccumLimit":"9999999.99", "AccumParCd":" ", "AccumParCdDesc":"", "AccumRemaining":"9991331.26", "AccumSrcCd":" ", "AccumTypeCd":"L", "AccumTypeCdDesc":"Lifetime Maximum", "BenFreqCnt":"0", "BenTypeCd":"M", "BenTypeCdDesc":"Medical", "DeductTypeCd":" ", "FullDescription":"Individual  Lifetime Maximum", "SrcApplCd":"MC", "SrcPlatformCd":"LV" }, { "AccumAmt":"360.00", "AccumBegDate":"01/01/2013", "AccumEndDate":"12/31/2013", "AccumIndivCd":"I", "AccumIndivCdDesc":"Individual", "AccumLimit":"6700.00", "AccumParCd":" ", "AccumParCdDesc":"", "AccumRemaining":"6340.00", "AccumSrcCd":" ", "AccumTypeCd":"O", "AccumTypeCdDesc":"Out-Of-Pocket", "BenFreqCnt":"0", "BenTypeCd":"M", "BenTypeCdDesc":"Medical", "DeductTypeCd":" ", "FullDescription":"Individual  Out-Of-Pocket", "SrcApplCd":"MC", "SrcPlatformCd":"LV" }, { "AccumAmt":"12.00", "AccumBegDate":"01/01/2013", "AccumEndDate":"12/31/2013", "AccumIndivCd":"F", "AccumIndivCdDesc":"Family", "AccumLimit":"0.00", "AccumParCd":"N", "AccumParCdDesc":"Non-Participating", "AccumRemaining":"0", "AccumSrcCd":"L", "AccumTypeCd":"T", "AccumTypeCdDesc":"Total", "BenFreqCnt":"0", "BenTypeCd":"M", "BenTypeCdDesc":"Medical", "DeductTypeCd":" ", "FullDescription":"Family Non-Participating Total", "SrcApplCd":"MC", "SrcPlatformCd":"LV" }, { "AccumAmt":"12.00", "AccumBegDate":"01/01/2013", "AccumEndDate":"12/31/2013", "AccumIndivCd":"I", "AccumIndivCdDesc":"Individual", "AccumLimit":"0.00", "AccumParCd":"N", "AccumParCdDesc":"Non-Participating", "AccumRemaining":"0", "AccumSrcCd":"L", "AccumTypeCd":"T", "AccumTypeCdDesc":"Total", "BenFreqCnt":"0", "BenTypeCd":"M", "BenTypeCdDesc":"Medical", "DeductTypeCd":" ", "FullDescription":"Individual Non-Participating Total", "SrcApplCd":"MC", "SrcPlatformCd":"LV" }, { "AccumAmt":"0.01", "AccumBegDate":"01/01/2013", "AccumEndDate":"12/31/2013", "AccumIndivCd":"F", "AccumIndivCdDesc":"Family", "AccumLimit":"0.00", "AccumParCd":" ", "AccumParCdDesc":"", "AccumRemaining":"0", "AccumSrcCd":"L", "AccumTypeCd":"2", "AccumTypeCdDesc":"Office Visit, Primary", "BenFreqCnt":"0", "BenTypeCd":"M", "BenTypeCdDesc":"Medical", "DeductTypeCd":" ", "FullDescription":"Family  Office Visit, Primary", "SrcApplCd":"MC", "SrcPlatformCd":"LV" }, { "AccumAmt":"0.01", "AccumBegDate":"01/01/2013", "AccumEndDate":"12/31/2013", "AccumIndivCd":"I", "AccumIndivCdDesc":"Individual", "AccumLimit":"0.00", "AccumParCd":" ", "AccumParCdDesc":"", "AccumRemaining":"0", "AccumSrcCd":"L", "AccumTypeCd":"2", "AccumTypeCdDesc":"Office Visit, Primary", "BenFreqCnt":"0", "BenTypeCd":"M", "BenTypeCdDesc":"Medical", "DeductTypeCd":" ", "FullDescription":"Individual  Office Visit, Primary", "SrcApplCd":"MC", "SrcPlatformCd":"LV" }, { "AccumAmt":"0.01", "AccumBegDate":"01/01/2013", "AccumEndDate":"12/31/2013", "AccumIndivCd":"F", "AccumIndivCdDesc":"Family", "AccumLimit":"0.00", "AccumParCd":" ", "AccumParCdDesc":"", "AccumRemaining":"0", "AccumSrcCd":"L", "AccumTypeCd":"3", "AccumTypeCdDesc":"Office Visit, Specialist", "BenFreqCnt":"5", "BenTypeCd":"M", "BenTypeCdDesc":"Medical", "DeductTypeCd":" ", "FullDescription":"Family  Office Visit, Specialist", "SrcApplCd":"MC", "SrcPlatformCd":"LV" }, { "AccumAmt":"0.01", "AccumBegDate":"01/01/2013", "AccumEndDate":"12/31/2013", "AccumIndivCd":"I", "AccumIndivCdDesc":"Individual", "AccumLimit":"0.00", "AccumParCd":" ", "AccumParCdDesc":"", "AccumRemaining":"0", "AccumSrcCd":"L", "AccumTypeCd":"3", "AccumTypeCdDesc":"Office Visit, Specialist", "BenFreqCnt":"5", "BenTypeCd":"M", "BenTypeCdDesc":"Medical", "DeductTypeCd":" ", "FullDescription":"Individual  Office Visit, Specialist", "SrcApplCd":"MC", "SrcPlatformCd":"LV" }, { "AccumAmt":"150.00", "AccumBegDate":"01/01/2013", "AccumEndDate":"12/31/2013", "AccumIndivCd":"F", "AccumIndivCdDesc":"Family", "AccumLimit":"0.00", "AccumParCd":"P", "AccumParCdDesc":"Participating", "AccumRemaining":"0", "AccumSrcCd":"L", "AccumTypeCd":"3", "AccumTypeCdDesc":"Office Visit, Specialist", "BenFreqCnt":"0", "BenTypeCd":"M", "BenTypeCdDesc":"Medical", "DeductTypeCd":" ", "FullDescription":"Family Participating Office Visit, Specialist", "SrcApplCd":"MC", "SrcPlatformCd":"LV" }, { "AccumAmt":"150.00", "AccumBegDate":"01/01/2013", "AccumEndDate":"12/31/2013", "AccumIndivCd":"I", "AccumIndivCdDesc":"Individual", "AccumLimit":"0.00", "AccumParCd":"P", "AccumParCdDesc":"Participating", "AccumRemaining":"0", "AccumSrcCd":"L", "AccumTypeCd":"3", "AccumTypeCdDesc":"Office Visit, Specialist", "BenFreqCnt":"0", "BenTypeCd":"M", "BenTypeCdDesc":"Medical", "DeductTypeCd":" ", "FullDescription":"Individual Participating Office Visit, Specialist", "SrcApplCd":"MC", "SrcPlatformCd":"LV" }, { "AccumAmt":"0.01", "AccumBegDate":"01/01/2013", "AccumEndDate":"12/31/2013", "AccumIndivCd":"F", "AccumIndivCdDesc":"Family", "AccumLimit":"0.00", "AccumParCd":" ", "AccumParCdDesc":"", "AccumRemaining":"0", "AccumSrcCd":"L", "AccumTypeCd":"5", "AccumTypeCdDesc":"Outpatient Surgery", "BenFreqCnt":"0", "BenTypeCd":"M", "BenTypeCdDesc":"Medical", "DeductTypeCd":" ", "FullDescription":"Family  Outpatient Surgery", "SrcApplCd":"MC", "SrcPlatformCd":"LV" }, { "AccumAmt":"0.01", "AccumBegDate":"01/01/2013", "AccumEndDate":"12/31/2013", "AccumIndivCd":"I", "AccumIndivCdDesc":"Individual", "AccumLimit":"0.00", "AccumParCd":" ", "AccumParCdDesc":"", "AccumRemaining":"0", "AccumSrcCd":"L", "AccumTypeCd":"5", "AccumTypeCdDesc":"Outpatient Surgery", "BenFreqCnt":"0", "BenTypeCd":"M", "BenTypeCdDesc":"Medical", "DeductTypeCd":" ", "FullDescription":"Individual  Outpatient Surgery", "SrcApplCd":"MC", "SrcPlatformCd":"LV" }, { "AccumAmt":"210.00", "AccumBegDate":"01/01/2013", "AccumEndDate":"12/31/2013", "AccumIndivCd":"F", "AccumIndivCdDesc":"Family", "AccumLimit":"0.00", "AccumParCd":"P", "AccumParCdDesc":"Participating", "AccumRemaining":"0", "AccumSrcCd":"L", "AccumTypeCd":"6", "AccumTypeCdDesc":"Other Amounts", "BenFreqCnt":"0", "BenTypeCd":"M", "BenTypeCdDesc":"Medical", "DeductTypeCd":" ", "FullDescription":"Family Participating Other Amounts", "SrcApplCd":"MC", "SrcPlatformCd":"LV" }, { "AccumAmt":"210.00", "AccumBegDate":"01/01/2013", "AccumEndDate":"12/31/2013", "AccumIndivCd":"I", "AccumIndivCdDesc":"Individual", "AccumLimit":"0.00", "AccumParCd":"P", "AccumParCdDesc":"Participating", "AccumRemaining":"0", "AccumSrcCd":"L", "AccumTypeCd":"6", "AccumTypeCdDesc":"Other Amounts", "BenFreqCnt":"0", "BenTypeCd":"M", "BenTypeCdDesc":"Medical", "DeductTypeCd":" ", "FullDescription":"Individual Participating Other Amounts", "SrcApplCd":"MC", "SrcPlatformCd":"LV" } ] }, "BenefitAccumsRequestInfo":{ "CustomerCoverageSequenceNumber":"1", "CustomerGenKey":"7000004181317", "IsVaild":"true", "MemberPersonGenKey":"14172618", "SubscriberPersonGenKey":"14172618", "ValidationResults":"" }, "ErrorMessage":"", "ErrorType":"" } ] } } } }');
            resAccums.setStatusCode(200);
            Map<String, HTTPRequest> mapDetails = con.getRequests();
            String reqType = '';
            for(String s : mapDetails.keyset())
            {
                reqType = s;
            }
            Test.setContinuationResponse(reqType,resAccums);
            AccumulatorWrapper_DTO_HUM  listOfDTOData = (AccumulatorWrapper_DTO_HUM)AccumsType_D_HUM.prepareServiceData(New PolicyMemberInfoWrapper_DTO_HUM( policyObj, null, null,  New List<String>{reqType},null,'Current' ,-1,Date.today()) );
            system.assert(listOfDTOData.bHasMoreThanTwoYearPolicies == false);
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
        User testUser = [Select Id, Name, LastName from User where LastName='test User1'];
		system.assert(testUser.LastName == 'test User1');
        Policy_Member__c policyObj = [select id, name, member__c, member__r.Enterprise_ID__C ,end_date__c, policy__c, policy__r.Group_Name__r.source_platform_code__c, policy__r.source_cust_cov_key__c, policy__r.Coverage_Plan_Effective_Date__c, policy__r.Coverage_Plan_End_Date__c from policy_member__c where name = '12345' limit 1];
        System.runAs(testUser)
          {
            Test.startTest();
            Test.setCurrentPage(page.BenefitsV2_VF_HUM);
            AccumsType_D_HUM.getAccumsData(policyObj.id,'','');
            Test.StopTest();
          }
       }


}
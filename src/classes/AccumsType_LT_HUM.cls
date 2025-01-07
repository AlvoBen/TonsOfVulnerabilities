@isTest
Public class AccumsType_LT_HUM{
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
            objMemAccount.source_platform_code__c = 'EM';
            objMemAccount.Enterprise_Id__c = String.valueof(2000);
            objMemAccount.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId();
            objMemAccount.Birthdate__c = String.valueOf(System.today().month() + '/' + System.today().day() + '/' + System.today().year());
            insert objMemAccount;
            System.assert(objMemAccount.LastName == 'LName');
                      
         
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
            
                        
            MemberPlan objMemberPlan = New MemberPlan();
            objMemberPlan.Member = objMemAccount;

            objMemberPlan.EffectiveTo =Date.valueOf('2025-12-31');
            objMemberPlan.EffectiveFrom = Date.valueOf('2015-12-31');
            objMemberPlan.Member_Plan_External_Id__c  = '00|ODS|123456789|89|89|02-01-2001';
            objMemberPlan.Member_Dependent_Code__c='01';
            objMemberPlan.Member_Id_Base__c ='12345';
            objMemberPlan.name = '12345';
            objMemberPlan.MemberId = objMemAccount.id;
            insert objMemberPlan;
            System.assert(objMemberPlan.Member_Plan_External_Id__c == '00|ODS|123456789|89|89|02-01-2001');
            
			
			             
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
        /*User testUser = [Select Id, Name from User where LastName='test AccumsCurrentPreviousUser1'];
        
        MemberPlan policyObj = [select id,name,Member_Dependent_Code__c,Member_Id_Base__c,Product__c,EffectiveFrom,Status,MemberId,EffectiveTo,policy__r.Group_Name__r.enterprise_ID__c,
        policy__r.Policy_External_ID__c,Member.Enterprise_Id__c,SubscriberPlanId__c,SubscriberPlanId__r.MemberId,SubscriberPlanId__r.Member.Mbr_Gen_Key__C,Member.Mbr_Gen_Key__C From MemberPlan where name = '12345' limit 1];
        
                
		System.runAs(testUser)
        {
            Test.startTest();
            
           
            Continuation con = (Continuation)AccumsType_LD_HUM.getAccumsDataREST(policyObj.id,'Current','06203017');
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
				AccumulatorWrapper_DTO_HUM  listOfDTOData = (AccumulatorWrapper_DTO_HUM)AccumsType_LD_HUM.prepareServiceDataREST(New MemberPlanInfoWrapper_DTO_HUM_V2( policyObj, null, null,  New List<String>{reqType},null, 'History',-1,Date.today() ) );
				system.assert(listOfDTOData.bHasMoreThanTwoYearPolicies == false);
			}
			Test.stopTest();
        }*/
    }           
  
    
    /*
    * Method Name : testAccumsCurrent
    * Description : This method Is used To test the Accums Benefits Current funtionality
    * Return type : None
    * Paramater   : None
    */
     Static testMethod void testAccumsCurrent()
    {
        /*User testUser = [Select Id, Name from User where LastName='test AccumsCurrentPreviousUser1'];
        MemberPlan policyObj = [select id,name,Member_Dependent_Code__c,Member_Id_Base__c,Product__c,EffectiveFrom,Status,MemberId,EffectiveTo,policy__r.Group_Name__r.enterprise_ID__c,
        policy__r.Policy_External_ID__c,Member.Enterprise_Id__c,SubscriberPlanId__c,SubscriberPlanId__r.MemberId,SubscriberPlanId__r.Member.Mbr_Gen_Key__C,Member.Mbr_Gen_Key__C From MemberPlan where name = '12345' limit 1];
        
        
		System.runAs(testUser)
        {
            Test.startTest();
            
            Continuation con = (Continuation)AccumsType_LD_HUM.getAccumsDataREST(policyObj.id,'Current','06203017');
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
				AccumulatorWrapper_DTO_HUM  listOfDTOData = (AccumulatorWrapper_DTO_HUM)AccumsType_LD_HUM.prepareServiceDataREST(New MemberPlanInfoWrapper_DTO_HUM_V2(policyObj, null, null,  New List<String>{reqType},null,'Current' ,-1,Date.today()) );
				system.assert(listOfDTOData.bHasMoreThanTwoYearPolicies == false);
			}
			Test.stopTest();
        }*/
    } 
    
    /*
    * Method Name : testAccumsSecondaryData
    * Description : This method Is used To test the Accums Benefits Current funtionality
    * Return type : None
    * Paramater   : None
    */
     Static testMethod void testAccumsSecondaryData()
    {
        /*User testUser = [Select Id, Name from User where LastName='test AccumsCurrentPreviousUser1'];
        MemberPlan policyObj = [select id,name,Member_Dependent_Code__c,Member_Id_Base__c,Product__c,EffectiveFrom,Status,MemberId,EffectiveTo,policy__r.Group_Name__r.enterprise_ID__c,
        policy__r.Policy_External_ID__c,Member.Enterprise_Id__c,SubscriberPlanId__c,SubscriberPlanId__r.MemberId,SubscriberPlanId__r.Member.Mbr_Gen_Key__C,Member.Mbr_Gen_Key__C From MemberPlan where name = '12345' limit 1];
      
        System.runAs(testUser)
        {
            Test.startTest();
           
            Continuation con = (Continuation)AccumsType_LD_HUM.getAccumsDataREST(policyObj.id,'Current','06203017');
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
				AccumulatorWrapper_DTO_HUM  listOfDTOData = (AccumulatorWrapper_DTO_HUM)AccumsType_LD_HUM.prepareServiceDataREST(New MemberPlanInfoWrapper_DTO_HUM_V2(policyObj, null, null,  New List<String>{reqType},null,'Current' ,-1,Date.today()) );
				system.assert(listOfDTOData.bHasMoreThanTwoYearPolicies == false);
			}
			Test.stopTest();
        }*/
    }
    
    /*
    * Method Name : testAccumsDataException
    * Description : This method Is used To test the Accums Data Exception scenero
    * Return type : None
    * Paramater   : None
    */
    Static testMethod void testAccumsDataException()
      {
        /*User testUser = [Select Id, Name, LastName from User where LastName='test AccumsCurrentPreviousUser1'];
		system.assert(testUser.LastName == 'test AccumsCurrentPreviousUser1');
        MemberPlan policyObj = [select id,name,Member_Dependent_Code__c,Member_Id_Base__c,Product__c,EffectiveFrom,Status,MemberId,EffectiveTo,policy__r.Group_Name__r.enterprise_ID__c,
        policy__r.Policy_External_ID__c,Member.Enterprise_Id__c,SubscriberPlanId__c,SubscriberPlanId__r.MemberId,SubscriberPlanId__r.Member.Mbr_Gen_Key__C,Member.Mbr_Gen_Key__C From MemberPlan where name = '12345' limit 1];
        
        System.runAs(testUser)
          {
            Test.startTest();
            AccumsType_LD_HUM.getAccumsDataREST(policyObj.id,'','');
			Test.StopTest();
          }*/
       }
    
    
    Static testMethod void testcheckEndDate(){
        /*User testUser = [Select Id, Name, LastName from User where LastName='test AccumsCurrentPreviousUser1'];
        MemberPlan policyObj = [select id,name,Member_Dependent_Code__c,Member_Id_Base__c,Product__c,EffectiveFrom,Status,MemberId,EffectiveTo,policy__r.Group_Name__r.enterprise_ID__c,
        policy__r.Policy_External_ID__c,Member.Enterprise_Id__c,SubscriberPlanId__c,SubscriberPlanId__r.MemberId,SubscriberPlanId__r.Member.Mbr_Gen_Key__C,Member.Mbr_Gen_Key__C From MemberPlan where name = '12345' limit 1];
        System.runAs(testUser)
        {
            System.assertEquals(true, AccumsType_LD_HUM.checkEndDate(policyObj.EffectiveTo));
        }*/
    }
    
    Static testMethod void testgetAllPoliciesOfMember(){
        /*User testUser = [Select Id, Name, LastName from User where LastName='test AccumsCurrentPreviousUser1'];
        MemberPlan policyObj = [select id,name,Member_Dependent_Code__c,Member_Id_Base__c,Product__c,EffectiveFrom,Status,MemberId,EffectiveTo,policy__r.Group_Name__r.enterprise_ID__c,
        policy__r.Policy_External_ID__c,Member.Enterprise_Id__c,SubscriberPlanId__c,SubscriberPlanId__r.MemberId,SubscriberPlanId__r.Member.Mbr_Gen_Key__C,Member.Mbr_Gen_Key__C From MemberPlan where name = '12345' limit 1];
        System.runAs(testUser)
        {
            System.assertNotEquals(null, AccumsType_LD_HUM.getAllPoliciesOfMember(policyObj,Date.valueOf('2025-12-31')));
        }*/
    }
    
    Static testMethod void testPrepareAccumService(){
       /*User testUser = [Select Id, Name, LastName from User where LastName='test AccumsCurrentPreviousUser1'];
        MemberPlan policyObj = [select id,name,Member_Dependent_Code__c,Member_Id_Base__c,Product__c,EffectiveFrom,Status,MemberId,EffectiveTo,policy__r.Group_Name__r.enterprise_ID__c,
        policy__r.Policy_External_ID__c,Member.Enterprise_Id__c,SubscriberPlanId__c,SubscriberPlanId__r.MemberId,SubscriberPlanId__r.Member.Mbr_Gen_Key__C,Member.Mbr_Gen_Key__C From MemberPlan where name = '12345' limit 1];
        System.runAs(testUser)
        {
            String reqType = 's';  
            Continuation con = (Continuation)AccumsType_LD_HUM.PrepareAccumService( policyObj, null, null,null, 'History',-1,Date.today());        
            System.assertEquals(null,con);        
        }*/
    }
    
    Static testMethod void testgetYearCounter(){
        /*User testUser = [Select Id, Name, LastName from User where LastName='test AccumsCurrentPreviousUser1'];
        MemberPlan policyObj = [select id,name,Member_Dependent_Code__c,Member_Id_Base__c,Product__c,EffectiveFrom,Status,MemberId,EffectiveTo,policy__r.Group_Name__r.enterprise_ID__c,
        policy__r.Policy_External_ID__c,Member.Enterprise_Id__c,SubscriberPlanId__c,SubscriberPlanId__r.MemberId,SubscriberPlanId__r.Member.Mbr_Gen_Key__C,Member.Mbr_Gen_Key__C From MemberPlan where name = '12345' limit 1];
        System.runAs(testUser)
        {
            System.assertEquals(1, AccumsType_LD_HUM.getYearCounter(policyObj,'Current',Date.valueOf('2025-12-31')));
            System.assertNotEquals(1, AccumsType_LD_HUM.getYearCounter(policyObj,'history',Date.valueOf('2025-12-31')));
        }*/
    }
    
    Static testMethod void testprepareServiceDataREST(){
        /*User testUser = [Select Id, Name, LastName from User where LastName='test AccumsCurrentPreviousUser1'];
        MemberPlan policyObj = [select id,name,Member_Dependent_Code__c,Member_Id_Base__c,Product__c,EffectiveFrom,Status,MemberId,EffectiveTo,policy__r.Group_Name__r.enterprise_ID__c,
        policy__r.Policy_External_ID__c,Member.Enterprise_Id__c,SubscriberPlanId__c,SubscriberPlanId__r.MemberId,SubscriberPlanId__r.Member.Mbr_Gen_Key__C,Member.Mbr_Gen_Key__C From MemberPlan where name = '12345' limit 1];

        System.runAs(testUser)
        {
            String reqType = 's';
            AccumulatorWrapper_DTO_HUM  listOfDTOData = (AccumulatorWrapper_DTO_HUM)AccumsType_LD_HUM.prepareServiceDataREST(New MemberPlanInfoWrapper_DTO_HUM_V2( policyObj, null, null,  New List<String>{reqType},null, 'History',-1,Date.today() ) );
            if (listOfDTOData != null){ system.assert(listOfDTOData.bHasMoreThanTwoYearPolicies == false);}
            system.assertEquals(null,listOfDTOData);
        }*/
    }
    
    Static testMethod void testprepareAccumsRequest(){
         /*User testUser = [Select Id, Name, LastName from User where LastName='test AccumsCurrentPreviousUser1'];
         MemberPlan policyObj = [select id,name,Member_Dependent_Code__c,Member_Id_Base__c,Product__c,EffectiveFrom,Status,MemberId,EffectiveTo,policy__r.Group_Name__r.enterprise_ID__c,
         policy__r.Policy_External_ID__c,Member.Enterprise_Id__c,SubscriberPlanId__c,SubscriberPlanId__r.MemberId,SubscriberPlanId__r.Member.Mbr_Gen_Key__C,Member.Mbr_Gen_Key__C From MemberPlan where name = '12345' limit 1];
		 AccumsServiceRequest_H_HUM_V2 accumsReqObj;
         System.runAs(testUser)
         {
          	AccumsServiceRequest_H_HUM_V2 reqObj = AccumsType_LD_HUM.prepareAccumsRequest(policyObj,'Current','2025-12-31');
            accumsReqObj = reqObj;
            system.assertEquals(accumsReqObj,reqObj);  
            system.assertNotEquals(null,reqObj);
         }*/
    }

}
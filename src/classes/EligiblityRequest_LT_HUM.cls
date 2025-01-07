/*****************************************************************************************************************************************************************************
    @Apex Class Name  : EligiblityRequest_LT_HUM
    @Version          : 1.0
    @Created Date     : 06/09/2022
    @description      : Test class for EligiblityRequest_LD_HUM, EligiblityRequest_LS_HUM
******************************************************************************************************************************************************************************
Modification Log:

* Developer Name           Review Number              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
* Kajal Namdev                                 		  06/09/2022                 Original Version
******************************************************************************************************************************************************************************/
@isTest
public class EligiblityRequest_LT_HUM {
	    /*
    * @description This method creates test data for test class
    * @param        None
    * @return       None
    */
  @testSetup static void prepareTestData() 
    {
        User oCCSUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oCCSUser;
        system.assertEquals(oCCSUser.LastName,'test User1');
        
        System.runAs(oCCSUser) 
        {
			Account objAcc = HUMTestDataHelperTest.getAccount(); 
            objAcc.FirstName = 'Test';
            objAcc.LastName = 'Account';
            objAcc.Tenant_Id__c = '00';
            objAcc.Enterprise_ID__c = '1005577525';
            objAcc.General_Account__c = true;
            objAcc.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get(System.label.HUMCOBMemberLabel).getRecordTypeId();
            insert objAcc;
            System.assertEquals(objAcc.Tenant_Id__c,'00');
            
            Account objAcc2 = HUMTestDataHelperTest.getAccount(); 
            objAcc2.FirstName = 'Test';
            objAcc2.LastName = 'LastNameAccount';
            objAcc2.Tenant_Id__c = '00';
            objAcc2.Enterprise_ID__c = '1005577525';
            objAcc2.General_Account__c = true;
            objAcc2.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get(System.label.HUMCOBMemberLabel).getRecordTypeId();
            insert objAcc2;
            System.assertEquals(objAcc2.Tenant_Id__c,'00');
            
            Product2 oProd = new Product2(Name = 'MED', Segment_Type__c='MEP', ProductCode='MED', Major_LOB__c='MEP', Product_Type__c = 'MAPD');
            insert oProd;
            System.assertNotEquals(oProd.Id, NULL, 'Value not matched');
            
            PurchaserPlan PolicyObjOI1 = new PurchaserPlan();
            PolicyObjOI1.Policy_Group_Number__c='11111';
            PolicyObjOI1.Name = 'Policy New';
            PolicyObjOI1.source_cust_cov_key__c = '12345678901234567890' ; 
            PolicyObjOI1.PayerId = objAcc.Id;
            PolicyObjOI1.Major_LOB__c ='EM';
            PolicyObjOI1.Platform__c = 'EM';
            PolicyObjOI1.ETL_Record_Deleted__c = false;
            PolicyObjOI1.Product__c = oProd.Id;
            insert PolicyObjOI1;
            
            PurchaserPlan PolicyObjOI2 = new PurchaserPlan();
            PolicyObjOI2.Policy_Group_Number__c='11111';
            PolicyObjOI2.Name = 'Policy2';
            PolicyObjOI2.source_cust_cov_key__c = '123433' ; 
            PolicyObjOI2.PayerId = objAcc.Id;
            PolicyObjOI2.Major_LOB__c ='EM';
            PolicyObjOI2.Platform__c = 'CB';
            PolicyObjOI2.ETL_Record_Deleted__c = false;
            PolicyObjOI2.Product__c = oProd.Id;
            insert PolicyObjOI2;
            
            PurchaserPlan PolicyObjOI3 = new PurchaserPlan();
            PolicyObjOI3.Policy_Group_Number__c='11111';
            PolicyObjOI3.Name = 'Policy2';
            PolicyObjOI3.source_cust_cov_key__c = '1234' ; 
            PolicyObjOI3.PayerId = objAcc.Id;
            PolicyObjOI3.Major_LOB__c ='EM';
            PolicyObjOI3.Platform__c = 'CB';
            PolicyObjOI3.ETL_Record_Deleted__c = false;
            PolicyObjOI3.Product__c = oProd.Id;
            insert PolicyObjOI3;
            
            MemberPlan objMembplanOI1 = new MemberPlan();
            objMembplanOI1.Name='testpolmem11';
            objMembplanOI1.MemberId=objAcc.id;
            objMembplanOI1.Member_Dependent_Code__c = 'H1234567890';
            objMembplanOI1.Member_Id_base__c = 'test23456';
            objMembplanOI1.GroupNumber = '672534';
            objMembplanOI1.EffectiveFrom = Date.newInstance(2020, 01, 01);
            objMembplanOI1.EffectiveTo = Date.newInstance(1999,12, 31);
            objMembplanOI1.PlanId= PolicyObjOI1.Id;
            insert objMembplanOI1;
            
            MemberPlan objMembplanOI2 = new MemberPlan();
            objMembplanOI2.Name='testpolmem11';
            objMembplanOI2.MemberId=objAcc.id;
            objMembplanOI2.Member_Dependent_Code__c = 'H1234567890';
            objMembplanOI2.Member_Id_base__c = 'test23456';
            objMembplanOI2.GroupNumber = '672534';
            objMembplanOI2.EffectiveFrom = Date.parse(Date.today().addDays(-30).format());
            objMembplanOI2.EffectiveTo = Date.parse(Date.today().addDays(-20).format());//Date.Today().addMonths(18);//Date.newInstance(1888,12, 31);
            objMembplanOI2.PlanId= PolicyObjOI2.Id;
            //objMembplanOI2.Status__c = 'Termed';
            insert objMembplanOI2;
            
            MemberPlan objMembplanOI3 = new MemberPlan();
            objMembplanOI3.Name='testpolmem12';
            objMembplanOI3.MemberId=objAcc.id;
            objMembplanOI3.Member_Dependent_Code__c = 'H1234567890';
            objMembplanOI3.Member_Id_base__c = 'test23456';
            objMembplanOI3.GroupNumber = '672534';
            objMembplanOI3.EffectiveFrom = Date.parse(Date.today().addDays(2).format());
            objMembplanOI3.EffectiveTo = Date.parse(Date.today().addDays(10).format());
            objMembplanOI3.PlanId= PolicyObjOI3.Id;
            objMembplanOI3.SubscriberPlanId__C = objMembplanOI2.Id;
            //objMembplanOI3.Status__c = 'Active';
            insert objMembplanOI3;
			
			//pmObj.Status__c='Future';
            objMembplanOI3.EffectiveTo= Date.parse(Date.today().addDays(20).format());
            objMembplanOI3.EffectiveFrom=Date.parse(Date.today().addDays(30).format());
            update objMembplanOI3;
            //objGenericHelper.fetchDetailsForOSA(caserec.id);
            //objGenericHelper.fetchDetailsForOSA(accountrec.id);
            objMembplanOI3.EffectiveTo= Date.parse(Date.today().addDays(-20).format());
            objMembplanOI3.EffectiveFrom=Date.parse(Date.today().addDays(30).format());
            update objMembplanOI3;
            //pmObj.Status__c='Termed';
            //objMembplanOI3.EffectiveTo= Date.parse(Date.today().addDays(-2).format());
            //objMembplanOI3.EffectiveFrom=Date.parse(Date.today().addDays(-10).format());
            //update objMembplanOI3;            
            
            Classification__c classificationRec = New Classification__c();
            classificationRec.Name = 'Claims';
            classificationRec.Is_Active__c = true;
            insert classificationRec;
            System.assertEquals(classificationRec.Name,'Claims');
            
            Intent__c intentRec = New Intent__c();
            intentRec.Name = 'Filling';
            intentRec.Is_Active__c = true;
            insert intentRec;
            System.assertEquals(intentRec.Name,'Filling');
			list<MemberPlan> memberPlanList = [
                    Select Name, GroupNumber, Plan.Source_Cust_Cov_Key__c,Policy__r.Source_Cust_Cov_Key__c, Product__c, Product_Type__c, Product_type_Code__c, Policy_Platform__c, 
                    ASO__c, Dual_Status_Indicator__c, Member_Coverage_Status__c, EffectiveTo, SubscriberPlanId__r.Id, SubscriberPlanId__r.Member.Enterprise_Id__c, Id from 
                    MemberPlan where 
                     Member.ETL_Record_Deleted__c = false
                    and ETL_Record_Deleted__c = false and
                   Member_Coverage_Status__c='Active'
                    LIMIT 100
                ];
            
			Case oCase1 = New Case(AccountId = objAcc.Id, Intent_Id__c = intentRec.Id, Classification_Id__c = classificationRec.Id, 
								Member_Plan_Id__c = objMembplanOI1.Id,Classification_Type__c='calls',Interacting_With_Type__c = 'Group',
								Interacting_With__c=objAcc.Id,Due_Date__c=Date.Today(),Subtype__c='Incomplete',Origin='Email',
								Case_Owner__c = UserInfo.getUserName(), Owner_Queue__c ='Cincinnati CRU',DCN__c='12345678', 
								Work_Queue_View_Name__c='Cincinati CRU',Interacting_About_Type__c='Member',Type='PPI',
								G_A_Rights_Given__c='Yes',G_A_Reason__c='Other',Service_Center__c ='Cincinnati', Department__c='CRU',
                                   status='In Progress');
			insert oCase1;
            
            Case oCase2 = New Case(AccountId = objAcc2.Id, Intent_Id__c = intentRec.Id, Classification_Id__c = classificationRec.Id, 
								Member_Plan_Id__c = objMembplanOI2.Id,Classification_Type__c='calls',Interacting_With_Type__c = 'Group',
								Interacting_With__c=objAcc2.Id,Due_Date__c=Date.Today(),Subtype__c='Incomplete',Origin='Email',
								Case_Owner__c = UserInfo.getUserName(), Owner_Queue__c ='Cincinnati CRU',DCN__c='12345678', 
								Work_Queue_View_Name__c='Cincinati CRU',Interacting_About_Type__c='Member',Type='PPI',
								G_A_Rights_Given__c='Yes',G_A_Reason__c='Other',Service_Center__c ='Cincinnati', Department__c='CRU',
                                   status='In Progress');
			insert oCase2;
            
            CRMFunctionality_ONOFF_Switch__c objOnOff = new CRMFunctionality_ONOFF_Switch__c();
            objOnOff.Name = '1900555';
            objOnOff.IsON__c = true;
            objOnOff.Release_Name__c = '03/05';
            insert objOnOff;
			
			CRMFunctionality_ONOFF_Switch__c objOnOff1 = new CRMFunctionality_ONOFF_Switch__c();
            objOnOff1.Name = '1900555';
            objOnOff1.IsON__c = false;
            objOnOff1.Release_Name__c = '03/05';
            insert objOnOff1;
            Member_Maintenance_Generics__c mmGenerics=new Member_Maintenance_Generics__c();
            mmGenerics = Member_Maintenance_Generics__c.getValues('Validate Eligibility1');
            
            Member_Maintenance_Generics__c objTermedMonths = new Member_Maintenance_Generics__c();
            objTermedMonths.Name = 'Termed Months';
            objTermedMonths.Message__c = '19';
            insert objTermedMonths;
            system.assertequals('Termed Months', objTermedMonths.Name, 'Wrong Name');
            
            Member_Maintenance_Generics__c objApplicationList = new Member_Maintenance_Generics__c();
            objApplicationList.Name = 'Validate Eligibility';
            objApplicationList.Applicable_Platform_List__c = 'LV,EM,CB';
            insert objApplicationList;
            system.assertequals('Validate Eligibility',objApplicationList.Name, 'Wrong Name');
			
			Member_Maintenance_Generics__c objApplicationList11 = new Member_Maintenance_Generics__c();
            objApplicationList11.Name = 'Update Member';
            objApplicationList11.Applicable_Platform_List__c = 'LV,EM';
            insert objApplicationList11;
            system.assertequals('Update Member',objApplicationList11.Name, 'Wrong Name');
            
            Member_Maintenance_Generics__c objApplicationList12 = new Member_Maintenance_Generics__c();
            objApplicationList12.Name = 'Update Member';
            objApplicationList12.Applicable_Platform_List__c = 'LV,EM';
            insert objApplicationList12;
            system.assertequals('Update Member',objApplicationList12.Name, 'Wrong Name');
            
              
            Member_Maintenance_Generics__c objApplicationList1 = new Member_Maintenance_Generics__c(); 
			//objApplicationList1=null;

            HUM_Webservice_Callout__c objEligiblityRequestD = new HUM_Webservice_Callout__c();
            objEligiblityRequestD.Name = 'MMPValidateEligibilityService';
            objEligiblityRequestD.Active__c = True;
            objEligiblityRequestD.Enable_Service_Tester__c = True;
            objEligiblityRequestD.Certificate_Name__c = 'Humana_GuidanceCare_neptune';
            objEligiblityRequestD.Content_Type__c = 'application/json';
            objEligiblityRequestD.Request_Type__c = 'GET';
            objEligiblityRequestD.Timeout__c = '120000';
            objEligiblityRequestD.End_Point_URL__c = 'https://dev-crmservicews.humana.com/ManageMemberProcessServiceREST';
            objEligiblityRequestD.Service_Name__c = '/ValidateEligibility';
            insert objEligiblityRequestD;
            system.assertequals('MMPValidateEligibilityService',objEligiblityRequestD.Name);
        }
    }
    
    /*
    * @description Method to test success response for Eligiblity
    * @param        None
    * @return       None
    */
    private static testmethod void testSuccessEligiblity()
    {
        User testUser = [Select Id,Name from User where LastName='test User1' Limit 1];
        system.runAs(testUser)
        { 
            Case caseObj = [Select id from Case where Account.LastName = 'Account'];
            Test.StartTest();
			CRMFunctionality_ONOFF_Switch__c objOnOff1 = new CRMFunctionality_ONOFF_Switch__c();
            objOnOff1.Name = '1900555';
            objOnOff1.IsON__c = false;
            objOnOff1.Release_Name__c = '03/05';
            insert objOnOff1;
            String sEligiblityRequestDHUM = '{"ValidateEligibilityRequest":{"source":"40221","groupid":"KY","IncludeValidationDetails":false,"IncludeInputOnError":false,"IncludeGeoCode":true,"IncludeEWS":false,"IncludeDPV":false,"City":"Louisville","AddressLine2":"Apt 2","AddressLine1":"9022 Shelbyville Road"}}';
          	Test.setMock(HttpCalloutMock.class, new EligiblitySuccessMockResponse_T_HUM());
            String result = EligiblityRequest_LD_HUM.retrieveEligibility(caseObj.id, 'LV', 'RSO');
            System.assertNotEquals(null, result);
            Test.StopTest();
        }
    }
    
    
    /*
    * @description Method to test success response for Eligiblity
    * @param        None
    * @return       None
    */
	/*
    private static testmethod void testSuccessEligiblity2()
    {
        User testUser = [Select Id,Name from User where LastName='test User1' Limit 1];
        system.runAs(testUser)
        { 
            Case caseObj = [Select id from Case where Account.LastName = 'LastNameAccount'];
            Test.StartTest();
            String sEligiblityRequestDHUM = '{"ValidateEligibilityRequest":{"source":"40221","groupid":"KY","IncludeValidationDetails":false,"IncludeInputOnError":false,"IncludeGeoCode":true,"IncludeEWS":false,"IncludeDPV":false,"City":"Louisville","AddressLine2":"Apt 2","AddressLine1":"9022 Shelbyville Road"}}';
          	Test.setMock(HttpCalloutMock.class, new EligiblitySuccessMockResponse_T_HUM());
            String result = EligiblityRequest_LD_HUM.retrieveEligibility(caseObj.id, 'CB', 'RSO');
            System.assertNotEquals(null, result);
            Test.StopTest();
        }
    }
	*/
    
    
     /*
    * @description Method to test failure response for AddressStandardize
    * @param        None
    * @return       None
    */
    private static testmethod void testFailureEligiblityRequest()
    {
        User testUser = [Select Id,Name from User where LastName='test User1' Limit 1];
        system.runAs(testUser)
        { 
            Case caseObj = [Select id from Case where Account.LastName = 'Account'];
            Test.StartTest(); 
            String sEligiblityRequestDHUM = '{"ValidateEligibilityRequest":{"source":""}}';
          	Test.setMock(HttpCalloutMock.class, new EligiblityFailureMockResponse_T_HUM());
            String result = EligiblityRequest_LD_HUM.retrieveEligibility(caseObj.id, sEligiblityRequestDHUM, 'RSO');
            System.assertNotEquals(null, result);    
            Test.StopTest();
        }
    }
    
    /*
    * @description Method to test exception for EligiblityRequest
    * @param        None
    * @return       None
    */
    private static testmethod void testExceptionEligiblityRequest()
    {
        User testUser = [Select Id,Name from User where LastName='test User1' Limit 1];
        system.runAs(testUser)
        { 
            Case caseObj = [Select id from Case where Account.LastName = 'Account'];            
            Test.StartTest(); 
            String sEligiblityRequestDHUM = '{"ValidateEligibilityRequest":""}';
            String result = EligiblityRequest_LD_HUM.retrieveEligibility(caseObj.id,sEligiblityRequestDHUM, 'RSO');
            String result1 = EligiblityRequest_LD_HUM.retrieveEligibility(null,null, null);
            System.assertNotEquals(null, result); 
            Test.StopTest();
        }
    }
	
	private static testmethod void testExceptionEligiblityRequest1()
    {
        User testUser = [Select Id,Name from User where LastName='test User1' Limit 1];
        system.runAs(testUser)
        { 
            Case caseObj = [Select id from Case where Account.LastName = 'Account'];            
            Test.StartTest(); 
            CRMFunctionality_ONOFF_Switch__c objOnOff1 = new CRMFunctionality_ONOFF_Switch__c();
            objOnOff1.Name = '1900555';
            objOnOff1.IsON__c = false;
            objOnOff1.Release_Name__c = '03/05';
            insert objOnOff1;
            String sEligiblityRequestDHUM = '{"ValidateEligibilityRequest":""}';
            String result = EligiblityRequest_LD_HUM.retrieveEligibility(caseObj.id,sEligiblityRequestDHUM, 'RSO');
            String result1 = EligiblityRequest_LD_HUM.retrieveEligibility(null,null, null);
            System.assertNotEquals(null, result); 
            Test.StopTest();
        }
    }
    private static testmethod void testSuccessEligiblity_1()
    {
        User testUser = [Select Id,Name from User where LastName='test User1' Limit 1];
        system.runAs(testUser)
        { 
            Case caseObj = [Select id from Case where Account.LastName = 'Account'];
            Test.StartTest();
            String sEligiblityRequestDHUM = '{"ValidateEligibilityRequest":{"source":"40221","groupid":"KY","IncludeValidationDetails":false,"IncludeInputOnError":false,"IncludeGeoCode":true,"IncludeEWS":false,"IncludeDPV":false,"City":"Louisville","AddressLine2":"Apt 2","AddressLine1":"9022 Shelbyville Road"}}';
          	HttpRequest request = new HttpRequest();
            request = EligiblityRequest_LS_HUM.getValidateEligiblityRequest(caseObj.Id,sEligiblityRequestDHUM);
            System.assertNotEquals(null, request);    
            Test.StopTest();
        }
    }
}
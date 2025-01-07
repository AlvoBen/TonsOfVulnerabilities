/*****************************************************************************************************************************************************************************
    @Apex Class Name  : EligibilityResponse_LT_HUM
    @Version          : 1.0
    @Created Date     : 06/09/2022
    @description      : Test class for HighestEligibleHeirarchy_LH_HUM, EligibilityResponse_LH_HUM
******************************************************************************************************************************************************************************
Modification Log:

* Developer Name           Review Number              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
* Kajal Namdev                                        06/15/2022                 Original Version
******************************************************************************************************************************************************************************/
@isTest
public class EligibilityResponse_LT_HUM {
    /*
    * @description  This method creates test data for test class
    * @param        None
    * @return       None
    */
    @TestSetup static void prepareTestData(){
        User oCCSUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oCCSUser;
        system.assertEquals(oCCSUser.LastName,'test User1', 'LastName is not equal');
        
        System.runAs(oCCSUser) {
            Account objAcc = HUMTestDataHelperTest.getAccount(); 
            objAcc.FirstName = 'Test';
            objAcc.LastName = 'Account';
            objAcc.Tenant_Id__c = '00';
            objAcc.Enterprise_ID__c = '1005577525';
            objAcc.General_Account__c = true;
            objAcc.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get(System.label.HUMCOBMemberLabel).getRecordTypeId();
            insert objAcc;
            System.assertEquals(objAcc.Tenant_Id__c,'00', 'Tenant Id is not equal');

            Product2 oProd1 = new Product2(Name = 'DEN', Segment_Type__c='PPOD', ProductCode='DEN', Major_LOB__c='PPOD', Product_Type__c = 'DENPPO');
            insert oProd1;
            System.assertNotEquals(oProd1.Id, NULL, 'Value not matched');
            
            Product2 oProd2 = new Product2(Name = 'MED', Segment_Type__c='MEP', ProductCode='MED', Major_LOB__c='MEP', Product_Type__c = 'MAPD');
            insert oProd2;
            System.assertNotEquals(oProd2.Id, NULL, 'Value not matched');
            
            Product2 oProd3 = new Product2(Name = 'DEN', Segment_Type__c='POSD', ProductCode='DEN', Major_LOB__c='POSD', Product_Type__c = 'MDCPPO');
            insert oProd3;
            System.assertNotEquals(oProd3.Id, NULL, 'Value not matched');
            
            PurchaserPlan PolicyObjOI1 = new PurchaserPlan();
            PolicyObjOI1.Policy_Group_Number__c='11111';
            PolicyObjOI1.Name = 'Policy New';
            PolicyObjOI1.source_cust_cov_key__c = '12345678901234567890' ; 
            PolicyObjOI1.PayerId = objAcc.Id;
            PolicyObjOI1.Major_LOB__c ='EM';
            PolicyObjOI1.Platform__c = 'EM';
            PolicyObjOI1.ETL_Record_Deleted__c = false;
            PolicyObjOI1.Product__c = oProd3.Id;
            insert PolicyObjOI1;
            
            PurchaserPlan PolicyObjOI2 = new PurchaserPlan();
            PolicyObjOI2.Policy_Group_Number__c='11111';
            PolicyObjOI2.Name = 'Policy New';
            PolicyObjOI2.source_cust_cov_key__c = '1234567890123456789067' ; 
            PolicyObjOI2.PayerId = objAcc.Id;
            PolicyObjOI2.Major_LOB__c ='EM';
            PolicyObjOI2.Platform__c = 'EM';
            PolicyObjOI2.ETL_Record_Deleted__c = false;
            PolicyObjOI2.Product__c = oProd3.Id;
            insert PolicyObjOI2;
            
            MemberPlan objMembplanOI1 = new MemberPlan();
            objMembplanOI1.Name='testpolmem11';
            //objMembplanOI1.Status__c = 'Termed';
            objMembplanOI1.MemberId=objAcc.id;
            objMembplanOI1.Member_Dependent_Code__c = 'H1234567890';
            objMembplanOI1.Member_Id_base__c = 'test23456';
            objMembplanOI1.GroupNumber = '672534';
            objMembplanOI1.EffectiveFrom = Date.newInstance(2020, 01, 01);
            objMembplanOI1.EffectiveTo = Date.newInstance(1999,12, 31);
            objMembplanOI1.PlanId= PolicyObjOI1.Id;
            insert objMembplanOI1;
            
            MemberPlan objMembplanOI2 = new MemberPlan();
            objMembplanOI2.Name='testpolmem12';
            //objMembplanOI2.Status__c = 'Termed';
            objMembplanOI2.MemberId=objAcc.id;
            objMembplanOI2.Member_Dependent_Code__c = 'H1234567890';
            objMembplanOI2.Member_Id_base__c = 'test23456';
            objMembplanOI2.GroupNumber = '675717';
            objMembplanOI2.EffectiveFrom = Date.newInstance(2020, 12, 31);
            objMembplanOI2.EffectiveTo = Date.newInstance(2020,12, 31);
            objMembplanOI2.PlanId= PolicyObjOI2.Id;
            insert objMembplanOI2;
            
            MemberPlan objMembplanOI3 = new MemberPlan();
            objMembplanOI3.Name='testpolmem13';
            //objMembplanOI3.Status__c = 'Active';
            objMembplanOI3.MemberId=objAcc.id;
            objMembplanOI3.Member_Dependent_Code__c = 'H1234567890';
            objMembplanOI3.Member_Id_base__c = 'test23456';
            objMembplanOI3.GroupNumber = '675717';
            objMembplanOI3.EffectiveFrom = Date.newInstance(2020, 12, 31);
            objMembplanOI3.EffectiveTo = Date.parse(Date.today().addDays(10).format());
            objMembplanOI3.PlanId= PolicyObjOI2.Id;
            insert objMembplanOI3;
            
            MemberPlan objMembplanOI4 = new MemberPlan();
            objMembplanOI4.Name='testpolmem14';
            //objMembplanOI4.Status__c = 'Future';
            objMembplanOI4.MemberId=objAcc.id;
            objMembplanOI4.Member_Dependent_Code__c = 'H1234567890';
            objMembplanOI4.Member_Id_base__c = 'test23456';
            objMembplanOI4.GroupNumber = '675717';
            objMembplanOI4.EffectiveFrom = Date.parse(Date.today().addDays(10).format());
            objMembplanOI4.EffectiveTo = Date.parse(Date.today().addDays(20).format());
            objMembplanOI4.PlanId= PolicyObjOI2.Id;
            insert objMembplanOI4;
            
            MemberPlan objMembplanOI5 = new MemberPlan();
            objMembplanOI5.Name='testpolmem15';
           // objMembplanOI5.Status__c = 'Active';
            objMembplanOI5.MemberId=objAcc.id;
            objMembplanOI5.Member_Dependent_Code__c = 'H1234567890';
            objMembplanOI5.Member_Id_base__c = 'test23456';
            objMembplanOI5.GroupNumber = '675717';
            objMembplanOI5.EffectiveFrom = Date.newInstance(2020, 12, 31);
            objMembplanOI5.EffectiveTo = Date.parse(Date.today().addDays(10).format());
            objMembplanOI5.PlanId= PolicyObjOI2.Id;
            insert objMembplanOI5;
            
            
            Classification__c classificationRec = New Classification__c();
            classificationRec.Name = 'Claims';
            classificationRec.Is_Active__c = true;
            insert classificationRec;
            System.assertEquals(classificationRec.Name, 'Claims', 'Classification name not equal.');
            
            Intent__c intentRec = New Intent__c();
            intentRec.Name = 'Filling';
            intentRec.Is_Active__c = true;
            insert intentRec;
            System.assertEquals(intentRec.Name, 'Filling', 'Intent Name mismatch.');
            
            Case oCase1 = New Case(AccountId = objAcc.Id, Intent_Id__c = intentRec.Id, Classification_Id__c = classificationRec.Id, 
                                Member_Plan_Id__c = objMembplanOI1.Id, Classification_Type__c='calls',Interacting_With_Type__c = 'Group',
                                Interacting_With__c=objAcc.Id,Due_Date__c=Date.Today(),Subtype__c='Incomplete',Origin='Email',
                                Case_Owner__c = UserInfo.getUserName(), Owner_Queue__c ='Cincinnati CRU',DCN__c='12345678', 
                                Work_Queue_View_Name__c='Cincinati CRU',Interacting_About_Type__c='Member',Type='PPI',
                                G_A_Rights_Given__c='Yes',G_A_Reason__c='Other',Service_Center__c ='Cincinnati', Department__c='CRU',
                                   status='In Progress');
            insert oCase1;
            
            CRMFunctionality_ONOFF_Switch__c objOnOff = new CRMFunctionality_ONOFF_Switch__c();
            objOnOff.Name = '1900555';
            objOnOff.IsON__c = true;
            objOnOff.Release_Name__c = '03/05';
            insert objOnOff;
            
            HUM_Webservice_Callout__c objEligiblityRequestD = new HUM_Webservice_Callout__c();
            objEligiblityRequestD.Name = 'MMPValidateEligibilityService';
            objEligiblityRequestD.Active__c = True;
            objEligiblityRequestD.Enable_Service_Tester__c = True;
            objEligiblityRequestD.Certificate_Name__c = 'Humana_GuidanceCare_neptune';
            objEligiblityRequestD.Content_Type__c = 'application/json';
            objEligiblityRequestD.Request_Type__c = 'GET';
            objEligiblityRequestD.Timeout__c = '120000';
            objEligiblityRequestD.End_Point_URL__c = 'https://int-crmservicews.humana.com/ManageMemberProcessServiceREST';
            objEligiblityRequestD.Service_Name__c = '/ValidateEligibility';
            insert objEligiblityRequestD;
            system.assertequals('MMPValidateEligibilityService',objEligiblityRequestD.Name, 'Wrong Service Name');
            
            Member_Maintenance_Generics__c objApplicationList = new Member_Maintenance_Generics__c();
            objApplicationList.Name = 'Validate Eligibility';
            objApplicationList.Applicable_Platform_List__c = 'LV,EM,CB';
            insert objApplicationList;
            system.assertequals('Validate Eligibility',objApplicationList.Name, 'Wrong Name');
            
            Member_Maintenance_Generics__c objTermedMonths = new Member_Maintenance_Generics__c();
            objTermedMonths.Name = 'Termed Months';
            objTermedMonths.Message__c = '19';
            insert objTermedMonths;
            system.assertequals('Termed Months', objTermedMonths.Name, 'Wrong Name');
        }

    }

    /*
    * @description  This method runs the success scenaario for GBO
    * @param        None
    * @return       None
    */
    private static testmethod void gboVESuccessScenario(){
        
        User testUser = [Select Id,Name from User where LastName='test User1' Limit 1];
        Case caseObj = [Select id from Case where Account.LastName = 'Account'];
        List<MemberPlan> pmList = [Select Name, Product__c, Product_Type__c, Product_type_Code__c, Policy_Platform__c, Plan.Source_Cust_Cov_Key__c, Member_Coverage_Status__c from MemberPlan  ];
        string response = '{"ValidateEligibilityResponse":{"timestamp":"2021-01-27 06:18:35.800589 z","result":"OK","responseCode":"200","requestid":"aae39fd3-81f6-8100-ffbb-dd444144f988","members":[{"eligibilities":[{"groupRecord":{"groupId":"INDTX6","dualDemoIndicator":"false","platformCode":"CB","ediGroupIndicator":"false","exchangeIndicator":"false","exchangeType":"","product":"DEN","productType":"PPOD","productTypeCode":"DENPPO","asoIndicator":"false","majorLOB":""},"heirarchy":"33","template":"GBO","ruleName":"CBIS (CompBenefits)","editableDemographicType":{"mailingAddress":"READ","residentialAddress":"READ","workEmail":"READ","homeEmail":"READ","homePhone":"READ","workPhone":"READ","name":"READ","gender":"READ","dob":"READ","ssn":"READ"},"contactHierarchyMessage":"Demographics cannot be completed in CRM. Please complete change in CBIS","criticalHierarchyMessage":"Demographics cannot be completed in CRM. Please complete change in CBIS","routingMessage":"Member has a product administer on CBIS platform. Please use softphone rules to transfer member to complete the CBIS demographics.","message":"","iseligible":"true","error":"false"},{"groupRecord":{"groupId":"309694","dualDemoIndicator":"false","platformCode":"DEN","ediGroupIndicator":"false","exchangeIndicator":"false","exchangeType":"","product":"MED","productType":"MEP","productTypeCode":"MAPD","asoIndicator":"false","majorLOB":""},"heirarchy":"2","template":"RSO","ruleName":"Individual Medicare - MAPD","editableDemographicType":{"mailingAddress":"EDIT","residentialAddress":"EDIT","workEmail":"EDIT","homeEmail":"EDIT","homePhone":"EDIT","workPhone":"EDIT","name":"EDIT","gender":"EDIT","dob":"EDIT","ssn":"READ"},"contactHierarchyMessage":"Review current Mentor process for completing a demographic change.","criticalHierarchyMessage":"Review current Mentor process for completing a demographic change.","routingMessage":" Member has a Medicare/Medicaid product. All demographics must be made by Medical. Transfer to Medicare Medical","message":"","iseligible":"true","error":"false"},{"groupRecord":{"groupId":"675717","dualDemoIndicator":"false","platformCode":"DEN","ediGroupIndicator":"false","exchangeIndicator":"false","exchangeType":"","product":"DEN","productType":"MDCPPO","productTypeCode":"MDCPPO","asoIndicator":"false","majorLOB":""},"heirarchy":"8","template":"GBO","ruleName":"Medicare Dental","editableDemographicType":{"mailingAddress":"READ","residentialAddress":"READ","workEmail":"READ","homeEmail":"READ","homePhone":"READ","workPhone":"READ","name":"READ","gender":"READ","dob":"READ","ssn":"READ"},"contactHierarchyMessage":"Member has a Medicare Dental plan. Using softphone, Transfer member to Medicare Medical so demographic changes can be completed","criticalHierarchyMessage":"Member has a Medicare Dental plan. Using softphone, Transfer member to Medicare Medical so demographic changes can be completed","routingMessage":"Member has a Medicare Dental plan. Do not transfer the caller to Medicare Dental, as the demographic change must be made on the Medicare Medical plan.","message":"","iseligible":"true","error":"false"},{"groupRecord":{"groupId":"1234567890123456789067","dualDemoIndicator":"false","platformCode":"EM","ediGroupIndicator":"false","exchangeIndicator":"false","exchangeType":"","product":"DEN","productType":"EM","productTypeCode":"MDCPPO","asoIndicator":"false","majorLOB":""},"heirarchy":"8","template":"GBO","ruleName":"Medicare Dental","editableDemographicType":{"mailingAddress":"READ","residentialAddress":"READ","workEmail":"READ","homeEmail":"READ","homePhone":"READ","workPhone":"READ","name":"READ","gender":"READ","dob":"READ","ssn":"READ"},"contactHierarchyMessage":"Member has a Medicare Dental plan. Using softphone, Transfer member to Medicare Medical so demographic changes can be completed","criticalHierarchyMessage":"Member has a Medicare Dental plan. Using softphone, Transfer member to Medicare Medical so demographic changes can be completed","routingMessage":"Member has a Medicare Dental plan. Do not transfer the caller to Medicare Dental, as the demographic change must be made on the Medicare Medical plan.","message":"","iseligible":"true","error":"false"},{"groupRecord":{"groupId":"675717","dualDemoIndicator":"false","platformCode":"EM","ediGroupIndicator":"false","exchangeIndicator":"false","exchangeType":"","product":"DEN","productType":"POSD","productTypeCode":"MDCPPO","asoIndicator":"false","majorLOB":""},"heirarchy":"8","template":"GBO","ruleName":"Medicare Dental","editableDemographicType":{"mailingAddress":"READ","residentialAddress":"READ","workEmail":"READ","homeEmail":"READ","homePhone":"READ","workPhone":"READ","name":"READ","gender":"READ","dob":"READ","ssn":"READ"},"contactHierarchyMessage":"Member has a Medicare Dental plan. Using softphone, Transfer member to Medicare Medical so demographic changes can be completed","criticalHierarchyMessage":"Member has a Medicare Dental plan. Using softphone, Transfer member to Medicare Medical so demographic changes can be completed","routingMessage":"Member has a Medicare Dental plan. Do not transfer the caller to Medicare Dental, as the demographic change must be made on the Medicare Medical plan.","message":"","iseligible":"true","error":"false"}]}]}}';
        EligiblityResponse_DTO_HUM responseDTO = (EligiblityResponse_DTO_HUM)System.JSON.deserialize(response, EligiblityResponse_DTO_HUM.class);
        Test.startTest();
        System.runAs(testUser){
            EligibilityResponse_LH_HUM responseClass = new EligibilityResponse_LH_HUM(responseDTO, 'GBO', caseObj.Id);
            string responseStr = responseClass.processEligiblity();
        }
        Test.stopTest();
        
    }
}
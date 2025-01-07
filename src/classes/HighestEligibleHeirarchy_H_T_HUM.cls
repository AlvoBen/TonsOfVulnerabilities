/**
 * @description       : Test Class for EligibilityResponse_H_HUM
 * @author            : Chetan Sindhe
 * @group             : Consumer Management
 * @last modified on  : 01-28-2021
 * @last modified by  : ChangeMeIn@UserSettingsUnder.SFDoc
 * Modifications Log 
 * Ver   Date         Author                               Modification
 * 1.0   01-28-2021   Chetan Sindhe   Initial Version
**/
@isTest
public with sharing class HighestEligibleHeirarchy_H_T_HUM {
    
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
            
            Policy__c objpol1 = HUMTestDataHelperTest.getPolicy();
            objpol1.Name='testPolicy1';
            objpol1.Group_Name__c = objAcc.Id;
            objpol1.Major_LOB__c = 'PPOD';
            objpol1.Source_Cust_Cov_Key__c = 'INDTX6';
            objpol1.Product__c = oProd1.Id;
            objpol1.Platform__c = 'CB';
            insert objpol1;
            system.assertEquals(objpol1.Source_Cust_Cov_Key__c,'INDTX6', 'Customer Coverage 1 Key mismatch');

            Policy__c objpol2 = HUMTestDataHelperTest.getPolicy();
            objpol2.Name='testPolicy2';
            objpol2.Group_Name__c = objAcc.Id;
            objpol2.Major_LOB__c = 'MEP';
            objpol2.Source_Cust_Cov_Key__c = '309694';
            objpol2.Product__c = oProd2.Id;
            objpol2.Platform__c = 'LV';
            insert objpol2;
            system.assertEquals(objpol2.Source_Cust_Cov_Key__c,'309694', 'Customer Coverage 2 Key mismatch');

            Policy__c objpol3 = HUMTestDataHelperTest.getPolicy();
            objpol3.Name='testPolicy3';
            objpol3.Group_Name__c = objAcc.Id;
            objpol3.Major_LOB__c = 'POSD';
            objpol3.Source_Cust_Cov_Key__c = '675717';
            objpol3.Product__c = oProd3.Id;
            objpol3.Platform__c = 'EM';
            insert objpol3;
            system.assertEquals(objpol3.Source_Cust_Cov_Key__c,'675717', 'Customer Coverage 3 Key mismatch');

            Policy__c objpol4 = HUMTestDataHelperTest.getPolicy();
            objpol4.Name='testPolicy4';
            objpol4.Group_Name__c = objAcc.Id;
            objpol4.Major_LOB__c = 'POSD';
            objpol4.Source_Cust_Cov_Key__c = '675717';
            objpol4.Product__c = oProd3.Id;
            objpol4.Platform__c = 'EM';
            insert objpol4;
            system.assertEquals(objpol4.Source_Cust_Cov_Key__c,'675717', 'Customer Coverage 4 Key mismatch');
            
            Policy_Member__c objPolMem1 = new Policy_Member__c();
            objPolMem1.Name = 'testpolmem11';
            objPolMem1.Relationship__c = 'Self';
            objPolMem1.Relationship_Code__c = '00';
            objPolMem1.Subscriber__c = null;
            objPolMem1.Effective_Date__c = Date.today().addDays(-1).format();
            objPolMem1.End_Date__c = Date.today().addDays(1).format();
            objPolMem1.Policy_Member_External_ID__c = '00|ODS|6925004434817|12345678|34567|2013-07-01';
            objPolMem1.Member__c = objAcc.Id;
            objPolMem1.Policy__c = objpol1.Id;
            objPolMem1.Policy__r = objpol1;
            insert objPolMem1;
            system.assertEquals(objPolMem1.Relationship__c, 'Self', 'Relationship is not equal to Self.');

            Policy_Member__c objPolMem2 = new Policy_Member__c();
            objPolMem2.Name = 'testpolmem12';
            objPolMem2.Relationship__c = 'Self';
            objPolMem2.Relationship_Code__c = '00';
            objPolMem2.Subscriber__c = null;
            objPolMem2.Effective_Date__c = Date.today().addDays(-1).format();
            objPolMem2.End_Date__c = Date.today().addDays(1).format();
            objPolMem2.Policy_Member_External_ID__c = '00|ODS|6925004434818|12345679|34567|2013-07-01';
            objPolMem2.Member__c = objAcc.Id;
            objPolMem2.Policy__c = objpol2.Id;
            objPolMem2.Policy__r = objpol2;
            insert objPolMem2;
            system.assertEquals(objPolMem2.Relationship__c, 'Self', 'Relationship is not equal to Self.');

            Policy_Member__c objPolMem3 = new Policy_Member__c();
            objPolMem3.Name = 'testpolmem3';
            objPolMem3.Relationship__c = 'Self';
            objPolMem3.Relationship_Code__c = '00';
            objPolMem3.Subscriber__c = null;
            objPolMem3.Effective_Date__c = Date.today().addDays(-100).format();
            objPolMem3.End_Date__c = Date.today().addDays(-90).format();
            objPolMem3.Policy_Member_External_ID__c = '00|ODS|6925004434819|12345670|34567|2013-07-01';
            objPolMem3.Member__c = objAcc.Id;
            objPolMem3.Policy__c = objpol3.Id;
            objPolMem3.Policy__r = objpol3;
            insert objPolMem3;
            system.assertEquals(objPolMem3.Relationship__c, 'Self', 'Relationship is not equal to Self.');

            Policy_Member__c objPolMem4 = new Policy_Member__c();
            objPolMem4.Name = 'testpolmem4';
            objPolMem4.Relationship__c = 'Self';
            objPolMem4.Relationship_Code__c = '00';
            objPolMem4.Subscriber__c = null;
            objPolMem4.Effective_Date__c = Date.today().addDays(-100).format();
            objPolMem4.End_Date__c = Date.today().addDays(-90).format();
            objPolMem4.Policy_Member_External_ID__c = '00|ODS|6925004434816|12345676|34567|2013-07-01';
            objPolMem4.Member__c = objAcc.Id;
            objPolMem4.Policy__c = objpol4.Id;
            objPolMem4.Policy__r = objpol4;
            insert objPolMem4;
            system.assertEquals(objPolMem4.Relationship__c, 'Self', 'Relationship is not equal to Self.');
            
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
								Policy_Member__c = objPolMem1.Id, Classification_Type__c='calls',Interacting_With_Type__c = 'Group',
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
    * @description  This method runs the success scenaario for RSO
    * @param        None
    * @return       None
    */
    private static testmethod void rsoVESuccessScenario(){
        
        User testUser = [Select Id,Name from User where LastName='test User1' Limit 1];
        Case caseObj = [Select Id from Case where Account.LastName = 'Account'];
        List<Policy_Member__c> pmList = [Select Name, Product__c, Product_Type__c, Product_type_Code__c, Policy_Platform__c, Policy__r.Source_Cust_Cov_Key__c from Policy_Member__c  ];
        string response = '{"ValidateEligibilityResponse":{"timestamp":"2021-01-27 06:18:35.800589 z","result":"OK","responseCode":"200","requestid":"aae39fd3-81f6-8100-ffbb-dd444144f988","members":[{"eligibilities":[{"groupRecord":{"groupId":"INDTX6","dualDemoIndicator":"false","platformCode":"CB","ediGroupIndicator":"false","exchangeIndicator":"false","exchangeType":"","product":"DEN","productType":"PPOD","productTypeCode":"DENPPO","asoIndicator":"false","majorLOB":""},"heirarchy":"33","template":"GBO","ruleName":"CBIS (CompBenefits)","editableDemographicType":{"mailingAddress":"READ","residentialAddress":"READ","workEmail":"READ","homeEmail":"READ","homePhone":"READ","workPhone":"READ","name":"READ","gender":"READ","dob":"READ","ssn":"READ"},"contactHierarchyMessage":"Demographics cannot be completed in CRM. Please complete change in CBIS","criticalHierarchyMessage":"Demographics cannot be completed in CRM. Please complete change in CBIS","routingMessage":"Member has a product administer on CBIS platform. Please use softphone rules to transfer member to complete the CBIS demographics.","message":"","iseligible":"true","error":"false"},{"groupRecord":{"groupId":"309694","dualDemoIndicator":"false","platformCode":"LV","ediGroupIndicator":"false","exchangeIndicator":"false","exchangeType":"","product":"MED","productType":"MEP","productTypeCode":"MAPD","asoIndicator":"false","majorLOB":""},"heirarchy":"2","template":"RSO","ruleName":"Individual Medicare - MAPD","editableDemographicType":{"mailingAddress":"EDIT","residentialAddress":"EDIT","workEmail":"EDIT","homeEmail":"EDIT","homePhone":"EDIT","workPhone":"EDIT","name":"EDIT","gender":"EDIT","dob":"EDIT","ssn":"READ"},"contactHierarchyMessage":"Review current Mentor process for completing a demographic change.","criticalHierarchyMessage":"Review current Mentor process for completing a demographic change.","routingMessage":" Member has a Medicare/Medicaid product. All demographics must be made by Medical. Transfer to Medicare Medical","message":"","iseligible":"true","error":"false"},{"groupRecord":{"groupId":"675717","dualDemoIndicator":"false","platformCode":"EM","ediGroupIndicator":"false","exchangeIndicator":"false","exchangeType":"","product":"DEN","productType":"POSD","productTypeCode":"MDCPPO","asoIndicator":"false","majorLOB":""},"heirarchy":"8","template":"GBO","ruleName":"Medicare Dental","editableDemographicType":{"mailingAddress":"READ","residentialAddress":"READ","workEmail":"READ","homeEmail":"READ","homePhone":"READ","workPhone":"READ","name":"READ","gender":"READ","dob":"READ","ssn":"READ"},"contactHierarchyMessage":"Member has a Medicare Dental plan. Using softphone, Transfer member to Medicare Medical so demographic changes can be completed","criticalHierarchyMessage":"Member has a Medicare Dental plan. Using softphone, Transfer member to Medicare Medical so demographic changes can be completed","routingMessage":"Member has a Medicare Dental plan. Do not transfer the caller to Medicare Dental, as the demographic change must be made on the Medicare Medical plan.","message":"","iseligible":"true","error":"false"},{"groupRecord":{"groupId":"675717","dualDemoIndicator":"false","platformCode":"EM","ediGroupIndicator":"false","exchangeIndicator":"false","exchangeType":"","product":"DEN","productType":"POSD","productTypeCode":"MDCPPO","asoIndicator":"false","majorLOB":""},"heirarchy":"8","template":"GBO","ruleName":"Medicare Dental","editableDemographicType":{"mailingAddress":"READ","residentialAddress":"READ","workEmail":"READ","homeEmail":"READ","homePhone":"READ","workPhone":"READ","name":"READ","gender":"READ","dob":"READ","ssn":"READ"},"contactHierarchyMessage":"Member has a Medicare Dental plan. Using softphone, Transfer member to Medicare Medical so demographic changes can be completed","criticalHierarchyMessage":"Member has a Medicare Dental plan. Using softphone, Transfer member to Medicare Medical so demographic changes can be completed","routingMessage":"Member has a Medicare Dental plan. Do not transfer the caller to Medicare Dental, as the demographic change must be made on the Medicare Medical plan.","message":"","iseligible":"true","error":"false"}]}]}}';
        EligiblityResponse_DTO_HUM responseDTO = (EligiblityResponse_DTO_HUM)System.JSON.deserialize(response, EligiblityResponse_DTO_HUM.class);
        Test.startTest();
        System.runAs(testUser){
            EligibilityResponse_H_HUM responseClass = new EligibilityResponse_H_HUM(responseDTO, 'RSO', caseObj.Id);
            string responseStr = responseClass.processEligiblity();
            system.assertnotequals(responseStr, null, 'Error in Validate Eligibility');
        }
        Test.stopTest();
        
    }

    /*
    * @description  This method runs the success scenaario for GBO
    * @param        None
    * @return       None
    */
    private static testmethod void gboVESuccessScenario(){
        
        User testUser = [Select Id,Name from User where LastName='test User1' Limit 1];
        Case caseObj = [Select id from Case where Account.LastName = 'Account'];
        List<Policy_Member__c> pmList = [Select Name, Product__c, Product_Type__c, Product_type_Code__c, Policy_Platform__c, Policy__r.Source_Cust_Cov_Key__c, Status__c from Policy_Member__c  ];
        string response = '{"ValidateEligibilityResponse":{"timestamp":"2021-01-27 06:18:35.800589 z","result":"OK","responseCode":"200","requestid":"aae39fd3-81f6-8100-ffbb-dd444144f988","members":[{"eligibilities":[{"groupRecord":{"groupId":"INDTX6","dualDemoIndicator":"false","platformCode":"CB","ediGroupIndicator":"false","exchangeIndicator":"false","exchangeType":"","product":"DEN","productType":"PPOD","productTypeCode":"DENPPO","asoIndicator":"false","majorLOB":""},"heirarchy":"33","template":"GBO","ruleName":"CBIS (CompBenefits)","editableDemographicType":{"mailingAddress":"READ","residentialAddress":"READ","workEmail":"READ","homeEmail":"READ","homePhone":"READ","workPhone":"READ","name":"READ","gender":"READ","dob":"READ","ssn":"READ"},"contactHierarchyMessage":"Demographics cannot be completed in CRM. Please complete change in CBIS","criticalHierarchyMessage":"Demographics cannot be completed in CRM. Please complete change in CBIS","routingMessage":"Member has a product administer on CBIS platform. Please use softphone rules to transfer member to complete the CBIS demographics.","message":"","iseligible":"true","error":"false"},{"groupRecord":{"groupId":"309694","dualDemoIndicator":"false","platformCode":"LV","ediGroupIndicator":"false","exchangeIndicator":"false","exchangeType":"","product":"MED","productType":"MEP","productTypeCode":"MAPD","asoIndicator":"false","majorLOB":""},"heirarchy":"2","template":"RSO","ruleName":"Individual Medicare - MAPD","editableDemographicType":{"mailingAddress":"EDIT","residentialAddress":"EDIT","workEmail":"EDIT","homeEmail":"EDIT","homePhone":"EDIT","workPhone":"EDIT","name":"EDIT","gender":"EDIT","dob":"EDIT","ssn":"READ"},"contactHierarchyMessage":"Review current Mentor process for completing a demographic change.","criticalHierarchyMessage":"Review current Mentor process for completing a demographic change.","routingMessage":" Member has a Medicare/Medicaid product. All demographics must be made by Medical. Transfer to Medicare Medical","message":"","iseligible":"true","error":"false"},{"groupRecord":{"groupId":"675717","dualDemoIndicator":"false","platformCode":"EM","ediGroupIndicator":"false","exchangeIndicator":"false","exchangeType":"","product":"DEN","productType":"POSD","productTypeCode":"MDCPPO","asoIndicator":"false","majorLOB":""},"heirarchy":"8","template":"GBO","ruleName":"Medicare Dental","editableDemographicType":{"mailingAddress":"READ","residentialAddress":"READ","workEmail":"READ","homeEmail":"READ","homePhone":"READ","workPhone":"READ","name":"READ","gender":"READ","dob":"READ","ssn":"READ"},"contactHierarchyMessage":"Member has a Medicare Dental plan. Using softphone, Transfer member to Medicare Medical so demographic changes can be completed","criticalHierarchyMessage":"Member has a Medicare Dental plan. Using softphone, Transfer member to Medicare Medical so demographic changes can be completed","routingMessage":"Member has a Medicare Dental plan. Do not transfer the caller to Medicare Dental, as the demographic change must be made on the Medicare Medical plan.","message":"","iseligible":"true","error":"false"},{"groupRecord":{"groupId":"675717","dualDemoIndicator":"false","platformCode":"EM","ediGroupIndicator":"false","exchangeIndicator":"false","exchangeType":"","product":"DEN","productType":"POSD","productTypeCode":"MDCPPO","asoIndicator":"false","majorLOB":""},"heirarchy":"8","template":"GBO","ruleName":"Medicare Dental","editableDemographicType":{"mailingAddress":"READ","residentialAddress":"READ","workEmail":"READ","homeEmail":"READ","homePhone":"READ","workPhone":"READ","name":"READ","gender":"READ","dob":"READ","ssn":"READ"},"contactHierarchyMessage":"Member has a Medicare Dental plan. Using softphone, Transfer member to Medicare Medical so demographic changes can be completed","criticalHierarchyMessage":"Member has a Medicare Dental plan. Using softphone, Transfer member to Medicare Medical so demographic changes can be completed","routingMessage":"Member has a Medicare Dental plan. Do not transfer the caller to Medicare Dental, as the demographic change must be made on the Medicare Medical plan.","message":"","iseligible":"true","error":"false"}]}]}}';
        EligiblityResponse_DTO_HUM responseDTO = (EligiblityResponse_DTO_HUM)System.JSON.deserialize(response, EligiblityResponse_DTO_HUM.class);
        Test.startTest();
        System.runAs(testUser){
            EligibilityResponse_H_HUM responseClass = new EligibilityResponse_H_HUM(responseDTO, 'GBO', caseObj.Id);
            string responseStr = responseClass.processEligiblity();
            Map<String, Object> responseMap = (Map<String, Object>)JSON.deserializeUntyped(responseStr);
            
            string shortlistedId = (string)responseMap.get('shortlistedPolicyMemberId');
            
            List<Map<String, Object>> data = new List<Map<String, Object>>();
            for (Object instance : (List<Object>)responseMap.get('eligibilityDataModel')) {
                data.add((Map<String, Object>)instance);
            }
            
            Object groupRecord = data[0].get('groupRecord');
            Map<String, Object> grMap = (Map<String, Object>)groupRecord;
         	
            string platformCode;
            string templateName;
            string groupId;
            string product;
            string productType;
            string productTypeCode;
            
            for(Policy_Member__c pm : pmList) {
                System.debug(pm.Status__c + ' => ' + pm.Policy_Platform__c);
                if(pm.Id == shortlistedId) {
                    platformCode = pm.Policy_Platform__c;
                    templateName = 'GBO';
                    groupId = pm.Policy__r.Source_Cust_Cov_Key__c;
                    product = pm.Product__c;
                    productType = pm.Product_Type__c;
                    productTypeCode = pm.Product_Type_Code__c;
                }
            }
            
            system.assertnotequals(responseStr, null, 'Error in Validate Eligibility');
            system.assertEquals('GBO', templateName, 'Template Mismatch');
            system.assertEquals(grMap.get('platformCode'), platformCode, 'Platform Mismatch');
            system.assertEquals(grMap.get('product'), product, 'Product Mismatch');
            system.assertEquals(grMap.get('productType'), productType, 'Product Type Mismatch');
            system.assertEquals(grMap.get('productTypeCode'), productTypeCode, 'Product Type Code Mismatch');
            system.assertEquals(grMap.get('groupId'), groupId, 'Group Id Mismatch');
        }
        Test.stopTest();
        
    }
    
}
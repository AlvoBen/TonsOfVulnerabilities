/*****************************************************************************************************************************************************************************
    @Apex Class Name  : EligiblityRequest_D_T_HUM
    @Version          : 1.0
    @Created Date     : June 25, 2020
    @description        : Test class for EligiblityRequest_D_HUM
******************************************************************************************************************************************************************************
Modification Log:

* Developer Name           Review Number              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
* Riddhi Mittal                                 25/06/2020                    Original Version
******************************************************************************************************************************************************************************/
@isTest
public class EligiblityRequest_D_T_HUM {
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
            
            Product2 oProd = new Product2(Name = 'MED', Segment_Type__c='MEP', ProductCode='MED', Major_LOB__c='MEP', Product_Type__c = 'MAPD');
            insert oProd;
            System.assertNotEquals(oProd.Id, NULL, 'Value not matched');
            
            Policy__c objpol = HUMTestDataHelperTest.getPolicy();
            objpol.Name='testPolicy';
            objpol.Group_Name__c = objAcc.Id;
            objpol.Major_LOB__c = 'MEP';
            objpol.Source_Cust_Cov_Key__c = '123456789';
            objpol.Product__c = oProd.Id;
            objpol.Product__r = oProd;
            objpol.Platform__c = 'LV';
            insert objpol;
            system.assertEquals(objpol.Source_Cust_Cov_Key__c,'123456789');
            
            Policy_Member__c objPolMem = new Policy_Member__c();
            objPolMem.Name = 'testpolmem11';
            objPolMem.Relationship__c = 'Self';
            objPolMem.Relationship_Code__c = '00';
            objPolMem.Subscriber__c = objPolMem.Id;
            objPolMem.Effective_Date__c=Date.today().addDays(-1).format();
            objPolMem.End_Date__c=Date.today().addDays(1).format();
            objPolMem.Policy_Member_External_ID__c='00|ODS|6925004434817|123456789|34567|2013-07-01';
            objPolMem.Member__c = objAcc.Id;
            objPolMem.Policy__c = objpol.Id;
            objPolMem.Policy__r = objpol;
            insert objPolMem;
            system.assertEquals(objPolMem.Relationship__c, 'Self');
            
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
            
			Case oCase1 = New Case(AccountId = objAcc.Id, Intent_Id__c = intentRec.Id, Classification_Id__c = classificationRec.Id, 
								Policy_Member__c = objPolMem.Id,Classification_Type__c='calls',Interacting_With_Type__c = 'Group',
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
            String sEligiblityRequestDHUM = '{"ValidateEligibilityRequest":{"source":"40221","groupid":"KY","IncludeValidationDetails":false,"IncludeInputOnError":false,"IncludeGeoCode":true,"IncludeEWS":false,"IncludeDPV":false,"City":"Louisville","AddressLine2":"Apt 2","AddressLine1":"9022 Shelbyville Road"}}';
          	Test.setMock(HttpCalloutMock.class, new EligiblitySuccessMockResponse_T_HUM());
            String result = EligiblityRequest_D_HUM.retrieveEligibility(caseObj.id, 'LV', 'RSO');
            System.assertNotEquals(null, result);
            Test.StopTest();
        }
    }
    
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
            String result = EligiblityRequest_D_HUM.retrieveEligibility(caseObj.id, sEligiblityRequestDHUM, 'RSO');
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
            String result = EligiblityRequest_D_HUM.retrieveEligibility(caseObj.id,sEligiblityRequestDHUM, 'RSO');
            System.assertNotEquals(null, result); 
            Test.StopTest();
        }
    }

}
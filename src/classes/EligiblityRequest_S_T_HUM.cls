/*****************************************************************************************************************************************************************************
    @Apex Class Name  : EligiblityRequest_S_T_HUM
    @Version          : 1.0
    @Created Date     : June 25, 2020
    @Function         : Test class for EligiblityRequest_S_HUM
******************************************************************************************************************************************************************************
Modification Log:

* Developer Name           Review Number              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
* Riddhi Mittal                                  25/06/2020                    Original Version
******************************************************************************************************************************************************************************/
@isTest
public class EligiblityRequest_S_T_HUM {
    
    
    /*
    * This method creates test data for test class
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
            
            Product2 oProd = new Product2(Name = 'Laptop X200',Segment_Type__c='MCR',ProductCode='MED', Major_LOB__c='MER');
            insert oProd;
            System.assertNotEquals(oProd.Id, NULL, 'Value not matched');
            
            Policy__c objpol = HUMTestDataHelperTest.getPolicy();
            objpol.Name='testPolicy';
            objpol.Group_Name__c = objAcc.Id;
            objpol.Major_LOB__c = 'MER';
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
            objPolMem.Policy_Member_External_ID__c='00|ODS|6925004434817|12345678|34567|2013-07-01';
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

            Member_Maintenance_Generics__c objValidateEligibility = new Member_Maintenance_Generics__c();
            objValidateEligibility.Name = 'Validate Eligibility';
            objValidateEligibility.Applicable_Platform_List__c = 'LV,EM,CB';
            insert objValidateEligibility;
            
            Member_Maintenance_Generics__c objUpdateMember = new Member_Maintenance_Generics__c();
            objUpdateMember.Name = 'Update Member';
            objUpdateMember.Applicable_Platform_List__c = 'LV,EM';
            insert objUpdateMember;
            
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
            system.assertequals('Humana_GuidanceCare_neptune', objEligiblityRequestD.Certificate_Name__c);
        }
    }        
    /*
    * Method to test success response for EligiblityRequest
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
          	HttpRequest request = new HttpRequest();
            request = EligiblityRequest_S_HUM.getValidateEligiblityRequest(caseObj.Id,sEligiblityRequestDHUM);
            System.assertNotEquals(null, request);    
            Test.StopTest();
        }
    }
        
        /*
    * Method to test exception for EligiblityRequest
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
            String sEligiblityRequest = '{""}';
          	HttpRequest request = new HttpRequest();
            request = EligiblityRequest_S_HUM.getValidateEligiblityRequest(caseObj.Id,sEligiblityRequest);
            System.assertNotEquals(null, sEligiblityRequest);
            Test.StopTest();
        }
    }

}
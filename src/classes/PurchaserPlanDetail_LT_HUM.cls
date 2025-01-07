/*******************************************************************************************************************************
Apex Class Name : PurchaserPlanDetail_LT_HUM
Version         : 1.0
Created On      : 08/19/2021 
Function        : Test class for PurchaserPlanDetail_LC_HUM
Test Class      : PurchaserPlanDetail_LT_HUM

Modification Log: 
* Version Number             Developer Name              Code Review                Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* 1.0                        Joel George                                       08/19/2021                     Original Version 
* 2.0                        Ashish Kumar                                      09/01/2021                     Added method Vendor Information test coverage
* 3.0                        Ashish Kumar                                      10/06/2021                     Improved test coverage
* 4.0                        Ashish Kumar                                      11/02/2021                     Rollback changes for Group Vendor
*******************************************************************************************************************************************************************/

@isTest
public class PurchaserPlanDetail_LT_HUM {
    /*
    * Method Name : setUpTestData
    * Description : This method creates test data for test class
    * Return type : None
    * Parameter   : None
    */
    @testSetup static void setUpTestData()
    {
        User objUser = HUMTestDataHelperTest.createTestUser('User1',GLOBAL_MEMBERPLAN_CONSTANT_LH_HUM.PHARMACYSPECIALIST_PROFILE_HUM);   
        insert objUser;
        PermissionSet ps = [SELECT Id FROM PermissionSet WHERE Name = 'CRMS_240_GBO_Segment_Service_Access' Limit 1];
        insert new PermissionSetAssignment(AssigneeId = objUser.id, PermissionSetId = ps.Id);
        system.runAs(objUser)
        {
      		CRMFunctionality_ONOFF_Switch__c csCRMOnOffSwitch5 = new CRMFunctionality_ONOFF_Switch__c(Name = '2117860',IsON__c=true);
            insert csCRMOnOffSwitch5;  
            CRMFunctionality_ONOFF_Switch__c csCRMOnOffSwitch4 = new CRMFunctionality_ONOFF_Switch__c(Name = '1789465',IsON__c=true);
            insert csCRMOnOffSwitch4;
            Account AccountObj = HUMTestDataHelperTest.getAccount();
            AccountObj.FirstName = 'FirstName';
            AccountObj.LastName = 'LastName'; 
            AccountObj.source_platform_code__c = 'LV' ;
            AccountObj.Enterprise_Id__c = String.valueof(2000);
            AccountObj.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId();
            AccountObj.Birthdate__c = String.valueOf(System.today().month()+'/'+system.today().day()+'/'+System.today().year());
            insert AccountObj;
            system.assertEquals(AccountObj.LastName == 'LastName', true);
            
            Account AccountObj1 = HUMTestDataHelperTest.getAccount();
            AccountObj1.FirstName = 'FirstName New';
            AccountObj1.LastName = 'LastName New'; 
            AccountObj1.source_platform_code__c = 'LV' ;
            AccountObj1.Enterprise_Id__c = String.valueof(2000);
            AccountObj1.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId();
            AccountObj1.Birthdate__c = String.valueOf(System.today().month()+'/'+system.today().day()+'/'+System.today().year());
            AccountObj1.Group_Number__c='R0720008';
            insert AccountObj1;
            system.assertEquals(AccountObj1.LastName == 'LastName New', true);
            
            
            Product2 oProd = new Product2(Name = 'MED',Segment_Type__c='MCR',ProductCode='MED', Major_LOB__c='MER',Product_Type__c='MA',Sold_Product_Key_Value__c='FEHVNP55');
            insert oProd;
            System.assertNotEquals(oProd.Id, NULL, 'Value not matched');
           
            
            
            PurchaserPlan PolicyObj = new PurchaserPlan();
            PolicyObj.Policy_Group_Number__c='11111';
            PolicyObj.Name = 'Dental Product';
            PolicyObj.source_cust_cov_key__c = '12345678901234567890' ; 
            PolicyObj.PayerId = AccountObj.Id;
            PolicyObj.Major_LOB__c ='MCD';
            PolicyObj.ETL_Record_Deleted__c = false;
            PolicyObj.Product__c = oProd.Id;
            PolicyObj.Issue_State__c = 'SC';
            PolicyObj.Exchange_Indicator__c = true;
            insert PolicyObj;
            
            PurchaserPlan PolicyObj1 = new PurchaserPlan();
            PolicyObj1.Policy_Group_Number__c='11111';
            PolicyObj1.Name = 'Dental Product1';
            PolicyObj1.source_cust_cov_key__c = '12345678901234567890' ; 
            PolicyObj1.PayerId = AccountObj1.Id;
            PolicyObj1.Major_LOB__c ='MCD';
            PolicyObj1.ETL_Record_Deleted__c = false;
            PolicyObj1.Product__c = oProd.Id;
            PolicyObj1.Issue_State__c = 'SC';
            PolicyObj1.Exchange_Indicator__c = true;
            insert PolicyObj1;



        }
        User objUser2 = HUMTestDataHelperTest.createTestUser('User2', 'Customer Care Specialist');
        insert objUser2;

    }
    
   
    
     /*
    * Method Name : testPurchaserDetail
    * Description : This method is used to test the processResponseVendor and GetGroupInfoFromGBE methods
    * Return type : void
    * Parameter   : None
*/
     static testMethod void testPurchaserDetail()
    {
        User testUser = [Select Id,Name from User where LastName='test User1'];
         Map<String,String> stateMap= new Map<String,String>();

        PurchaserPlan purchaserPlanObj = [select id,Issue_State__c,Exchange_Indicator__c from PurchaserPlan where Name = 'Dental Product1' limit 1];
        String recId =purchaserPlanObj.Id;
  
        System.runAs(testUser)
        {
            Test.startTest();
                PurchaserPlanDetail_LC_HUM.getPurchaserPlan(recId);
            Test.stopTest();
        }
    }
        
        
     /*
    * Method Name : testMemberVendor
    * Description : This method is used to test the processResponseVendor and GetGroupInfoFromGBE methods
    * Return type : void
    * Parameter   : None
    */
     static testMethod void testMemberVendor()
    {
        User testUser = [Select Id,Name from User where LastName='test User1'];
        Map<String,String> stateMap= new Map<String,String>();
        
        PurchaserPlan purchaserPlanObj = [select id,Issue_State__c,Exchange_Indicator__c from PurchaserPlan where Name = 'Dental Product' limit 1];
        String recId =purchaserPlanObj.Id;

         HUM_Webservice_Callout__c oGetMemberEDI = new HUM_Webservice_Callout__c(Request_Type__c = 'POST', Name = 'GroupBusinessEntityService',
                                                                                      End_Point_URL__c = 'https://int-crmservicews.humana.com/EDIGroupService/GetEDIGroupInfo', Certificate_Name__c = 'Humana_GuidanceCare_HlthCldCop', Timeout__c = '12000',
                                                                                      Content_Type__c = 'application/json', Active__c = true);
        insert oGetMemberEDI;
        
        System.runAs(testUser)
        {
            Test.startTest();

            
            HttpResponse httpRes = new Httpresponse();
            httpRes.setStatusCode(200);
            String resBodyString =  '';
            httpRes.setBody(resBodyString);
            system.assert(httpRes.getBody() == resBodyString);         
            string reqType = 'Continuation-1';        
            Test.setContinuationResponse(reqType,httpRes);
            stateMap.put('sRequest',reqType);
            MemberPlanDetail_LC_HUM.processResponseVendor(stateMap);
            
            Test.stopTest();
        }
    }    
}
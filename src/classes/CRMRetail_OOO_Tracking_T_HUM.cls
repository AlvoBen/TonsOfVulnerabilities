/*******************************************************************************************************************************
Apex Class Name  : CRMRetail_OOO_Tracking_T_HUM 
Version          : 1.0
Created Date     : 08/03/2021
Function         : Test class for CRMRetail_OOO_Tracking_H_HUM Trigger          

Modification Log: 
* Developer Name         Code Review              Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
*  Vinoth L                                     07/21/2021               User Story - 2492158 T1PRJ0001894 - MF 7 - CRM Storefront SDoH: Track All Screenings
*  Abhishek Maurya                              07/21/2021               User Story - 2491779 T1PRJ0001894 - MF 7 - CRM Storefront SDoH: One-on-One Tracking Enhancements 
*  Vinoth L                                     09/02/2021               User Story - 2508432 T1PRJ0001894 - MF 7 - CRM Storefront SDoH: Share Screening Results - Call DH&A 
*  Abhishek Maurya                              09/02/2021               User Story - 2599455 T1PRJ0001894 - MF 7 - CRM Storefront SDoH: Share Screening Results - Update Existing 1x1 Tracking
*  Vinoth L                                     09/05/2021               09/24 Post Evaluation -Enhancement of Code Coverage
* Ekta Bist                                     03/07/2022               User Story 3153697 - T1PRJ0154546 MF10 Storefront - Update 'Reason for Visit' Picklist Values on Health Educator 1x1 Tracking Form  
*  Mohamed Thameem                              27/05/2023               User Story - 4647349 T1PRJ0154546 / SF / MF9 Storefront - Add Last 3 One-On-One Information
*******************************************************************************************************************************/
@isTest
public class CRMRetail_OOO_Tracking_T_HUM {
    /*
    * Method Name : prepareTestData
    * Description : Create common test Data
    * Return type : void
    * Parameter   : NA
    */
    @testSetup static void prepareTestData() 
    {
        User oCCSUser = HUMTestDataHelperTest.createTestUser('User1','System Administrator');
        insert oCCSUser;
        Assert.areEqual(oCCSUser.LastName,'test User1');
        PermissionSet SuperAdmin = [SELECT Id FROM PermissionSet WHERE Name = 'CRMRetail_400_Super_Admin'];
        PermissionSetAssignment supAdmin = new PermissionSetAssignment(AssigneeId = oCCSUser.id, PermissionSetId = SuperAdmin.Id);
        insert supAdmin;
        Assert.areEqual(supAdmin.AssigneeId, oCCSUser.id);
        System.runAs (oCCSUser) 
        {
            ID oCRMRetailVisitorTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_RETAIL_VISITOR).getRecordTypeId();            
            Account oAcc1 = CRMRetail_TestDataFactory_H_HUM.createObjAccount(oCRMRetailVisitorTypeId,'04/06/1985','Daphiner','Superman','12209','Daphiner@gmail.com','','arizona',false);
            oAcc1.Enterprise_ID__c='15';
            insert oAcc1;
            Assert.areEqual(oAcc1.Enterprise_ID__c, '15');
            Storefront_Location__c location1 =  CRMRetail_TestDataFactory_H_HUM.createLocation();
            insert location1;
            Assert.areEqual(location1.Name, 'Virginia');

            List<CRMRetail_OOO_Tracking__c> trackingObjList = new List<CRMRetail_OOO_Tracking__c>();
            trackingObjList.add(CRMRetail_TestDataFactory_H_HUM.createOOORecord(oAcc1.Id,location1.Id,'BMI Screening','Financial Strain;Food Insecurity',TRUE));
            trackingObjList.add(CRMRetail_TestDataFactory_H_HUM.createOOORecord(oAcc1.Id,location1.Id,'BMI Screening','Financial Strain',TRUE));
            trackingObjList.add(CRMRetail_TestDataFactory_H_HUM.createOOORecord(oAcc1.Id,location1.Id,'BMI Screening','Food Insecurity',TRUE));
            insert trackingObjList;
            
            Assert.areEqual(trackingObjList[0].Reason_for_Visit__c, 'BMI Screening');
            Assert.areEqual(trackingObjList[1].Reason_for_Visit__c, 'BMI Screening');
            Assert.areEqual(trackingObjList[2].Reason_for_Visit__c, 'BMI Screening');
            
            List<CRMRetail_SDoHDetermt__c> lstOfSDoHRecords = new List<CRMRetail_SDoHDetermt__c>();
            CRMRetail_SDoHDetermt__c sDoHRecord1 = CRMRetail_TestDataFactory_H_HUM.createSdohRecord('Financial Strain',trackingObjList[0].Id);
            lstOfSDoHRecords.add(sDoHRecord1);                        
            CRMRetail_SDoHDetermt__c sDoHRecord2 = CRMRetail_TestDataFactory_H_HUM.createSdohRecord('Financial Strain',trackingObjList[0].Id);
            sDoHRecord2.Description__c=GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_VERY_UNLIKELY;
            lstOfSDoHRecords.add(sDoHRecord2);
            insert lstOfSDoHRecords;
            Assert.areEqual(lstOfSDoHRecords.get(0).CRMRetail_OOO_Tracking__c, trackingObjList[0].Id);
            Assert.areEqual(lstOfSDoHRecords.get(1).CRMRetail_OOO_Tracking__c, trackingObjList[0].Id);
            List<HUM_Webservice_Callout__c> lstOfCstmSetting = new List<HUM_Webservice_Callout__c>();
            HUM_Webservice_Callout__c cRMRetail_SDoH_Create=new HUM_Webservice_Callout__c( Name = 'CRMRetail_SDoH_Create' ,Request_Type__c='POST', Certificate_Name__c = 'DummyCertificate', Active__c = true, End_Point_URL__c = 'http://www.google.com', Timeout__c = '12000');           
            HUM_Webservice_Callout__c cRMRetail_SDoH_Update=new HUM_Webservice_Callout__c( Name = 'CRMRetail_SDoH_Update' ,Request_Type__c='POST', Certificate_Name__c = 'DummyCertificate', Active__c = true, End_Point_URL__c = 'http://www.google.com', Timeout__c = '12000');           
            HUM_Webservice_Callout__c cRMRetail_SDoH_Delete=new HUM_Webservice_Callout__c( Name = 'CRMRetail_SDoH_Delete' ,Request_Type__c='POST', Certificate_Name__c = 'DummyCertificate', Active__c = true, End_Point_URL__c = 'http://www.google.com', Timeout__c = '12000');           
            lstOfCstmSetting.add(cRMRetail_SDoH_Create);
            lstOfCstmSetting.add(cRMRetail_SDoH_Update);
            lstOfCstmSetting.add(cRMRetail_SDoH_Delete);
            insert lstOfCstmSetting;
            Assert.areEqual(lstOfCstmSetting[0].name, 'CRMRetail_SDoH_Create');
            Assert.areEqual(lstOfCstmSetting[1].name, 'CRMRetail_SDoH_Update');
            Assert.areEqual(lstOfCstmSetting[2].name, 'CRMRetail_SDoH_Delete');
           
        }
    }
    
    @isTest static void testCallInsertAPI()
    {
        User oUser = [select Id,Name from User where name = 'test User1' limit 1]; 
        Assert.areEqual(oUser.Name,'test User1');
        system.runAs(oUser)
        { 
            Test.setMock(HttpCalloutMock.class, new CRMRetail_SDoH_Helper_T_HUM.CRMRetail_DHNA_MockImpl());  
            Test.startTest();
            CRMRetail_OOO_Tracking__c crmRetailRecord=[SELECT Id,IsDeleted,SDoH_Screening_Completed__c,SDoH_Determinants__c,createdDate,Account__r.Enterprise_ID__c FROM CRMRetail_OOO_Tracking__c LIMIT 1 ];
            Assert.areEqual(crmRetailRecord.SDoH_Screening_Completed__c, TRUE);
            Map<String,Object> mapOfDeter=CRMRetail_OOO_Tracking_H_HUM.getScreenedResult(crmRetailRecord.Id);
            Assert.areEqual(mapOfDeter.size(), 3);
            List<String> result1 = CRMRetail_OOO_Tracking_H_HUM.callSDohAPIOnUpdateOrInsert(crmRetailRecord.Account__r.Enterprise_ID__c,crmRetailRecord.Id,system.now(),new List<String>{'Financial Strain'},new List<String> {},new List<String>{},new List<String> {}, new List<String> {},new List<String> {},new List<String> {'INSERT'});
            Assert.areEqual(result1.size(),0);            
            List<String> result2 = CRMRetail_OOO_Tracking_H_HUM.callSDohAPIOnUpdateOrInsert(crmRetailRecord.Account__r.Enterprise_ID__c,crmRetailRecord.Id,null,new List<String>{'Financial Strain'},new List<String> {'Financial Strain'},new List<String>{},new List<String> {}, new List<String> {},new List<String> {},new List<String> {'INSERT'});
            Assert.areEqual(result2.size(),0); 
            CRMRetail_OOO_Tracking_H_HUM.checksOperactionTypeForSDohRecords(crmRetailRecord.Id,system.now(),new List<String>{'Financial Strain'},new List<String> {'Housing Insecurity'},new List<String>{},new List<String> {}, new List<String> {},new List<String> {});
             
            Test.stopTest();
        }
    }
    
    @isTest static void testCallUpdateAPI()
    {
        User oUser = [select Id,Name from User where name = 'test User1' limit 1]; 
        Assert.areEqual(oUser.Name,'test User1');
        
        system.runAs(oUser)
        { 
            Test.startTest();
            Test.setMock(HttpCalloutMock.class, new CRMRetail_SDoH_Helper_T_HUM.CRMRetail_DHNA_MockImpl());  
            CRMRetail_OOO_Tracking__c crmRetailRecord=[SELECT Id,Account__r.Enterprise_ID__c,IsDeleted,SDoH_Screening_Completed__c,SDoH_Determinants__c,createdDate FROM CRMRetail_OOO_Tracking__c LIMIT 1 ];
            Assert.areEqual(crmRetailRecord.SDoH_Screening_Completed__c, TRUE);
            List<String> result1 = CRMRetail_OOO_Tracking_H_HUM.callSDohAPIOnUpdateOrInsert(crmRetailRecord.Account__r.Enterprise_ID__c,crmRetailRecord.Id,system.now(),new List<String>{},new List<String> {},new List<String>{'Housing Insecurity','Food Insecurity'},new List<String> {'Loneliness','Food Insecurity'}, new List<String> {},new List<String> {},new List<String> {'UPDATE'});
            Assert.areEqual(result1.size(), 0);
            CRMRetail_OOO_Tracking_H_HUM.checksOperactionTypeForSDohRecords(crmRetailRecord.Id,system.now(),new List<String>{'Financial Strain'},new List<String> {'Housing Insecurity'},new List<String>{'Housing Insecurity','Food Insecurity'},new List<String> {'Housing Insecurity','Food Insecurity'}, new List<String> {},new List<String> {});
            
            Test.stopTest();
        }        
    }
 
    @isTest static void testCallDeleteAPI()
    {
        User oUser = [select Id,Name from User where name = 'test User1' limit 1]; 
        Assert.areEqual(oUser.Name,'test User1');
        
        system.runAs(oUser)
        { 
            Test.startTest();
            Test.setMock(HttpCalloutMock.class, new CRMRetail_SDoH_Helper_T_HUM.CRMRetail_DHNA_MockImpl());  
            CRMRetail_OOO_Tracking__c crmRetailRecord=[SELECT Id,Account__r.Enterprise_ID__c,IsDeleted,SDoH_Screening_Completed__c,SDoH_Determinants__c,createdDate FROM CRMRetail_OOO_Tracking__c LIMIT 1 ];
            Assert.areEqual(crmRetailRecord.SDoH_Screening_Completed__c, TRUE);
            List<String> result1 = CRMRetail_OOO_Tracking_H_HUM.callSDohAPIOnUpdateOrInsert(crmRetailRecord.Account__r.Enterprise_ID__c,crmRetailRecord.Id,system.now(),new List<String>{},new List<String> {},new List<String>{},new List<String> {}, new List<String> {'loneliness','Food Insecurity'},new List<String> {'loneliness'},new List<String> {'DELETE'});
            Assert.areEqual(result1.size(), 0);
            CRMRetail_OOO_Tracking_H_HUM.checksOperactionTypeForSDohRecords(crmRetailRecord.Id,system.now(),new List<String>{'Financial Strain'},new List<String> {'Housing Insecurity'},new List<String>{'Housing Insecurity','Food Insecurity'},new List<String> {'Housing Insecurity','Food Insecurity'}, new List<String> {'loneliness','Food Insecurity'},new List<String> {'loneliness','Food Insecurity'});
            
            Test.stopTest();
        }        
    }
     @isTest static void testSDohDelFromButton()
    {
        User oUser = [select Id,Name from User where name = 'test User1' limit 1]; 
        Assert.areEqual(oUser.Name,'test User1');
        
        system.runAs(oUser)
        { 
            Test.startTest();
            Test.setMock(HttpCalloutMock.class, new CRMRetail_SDoH_Helper_T_HUM.CRMRetail_DHNA_MockImpl());  
            CRMRetail_OOO_Tracking__c crmRetailRecord=[SELECT Id,Account__r.Enterprise_ID__c,IsDeleted,SDoH_Screening_Completed__c,SDoH_Determinants__c,createdDate,(SELECT Id,Determinant__c,Source__c,Rating__c,Description__c FROM CRM_Retail_SDoH_Determinants__r) FROM CRMRetail_OOO_Tracking__c LIMIT 1 ];
            Assert.areEqual(crmRetailRecord.SDoH_Screening_Completed__c, TRUE);
            String result = CRMRetail_OOO_Tracking_H_HUM.deleteSDoHBeforeOOOTrackingDelete(crmRetailRecord.Id,False);
            system.assert(String.isNotBlank(result));
            Test.stopTest();
        }        
    }
     @isTest static void testCallInsertServieDown()
    {
         User oUser = [select Id,Name from User where name = 'test User1' limit 1]; 
        Assert.areEqual(oUser.Name,'test User1');
        system.runAs(oUser)
        { 
            Test.startTest();
            Test.setMock(HttpCalloutMock.class, new CRMRetail_SDoH_Helper_T_HUM.CRMRetail_SDoH_MockImplServiceUnavailable());  
            CRMRetail_OOO_Tracking__c crmRetailRecord=[SELECT Id,IsDeleted,SDoH_Screening_Completed__c,SDoH_Determinants__c,createdDate,Account__r.Enterprise_ID__c FROM CRMRetail_OOO_Tracking__c LIMIT 1 ];
            Assert.areEqual(crmRetailRecord.SDoH_Screening_Completed__c, TRUE);
            Map<String,Object> mapOfDeter=CRMRetail_OOO_Tracking_H_HUM.getScreenedResult(crmRetailRecord.Id);
            Assert.areEqual(mapOfDeter.size(), 3);
            List<String> result = CRMRetail_OOO_Tracking_H_HUM.callSDohAPIOnUpdateOrInsert(crmRetailRecord.Account__r.Enterprise_ID__c,crmRetailRecord.Id,system.now(),new List<String>{'Financial Strain'},new List<String> {'Financial Strain'},new List<String>{},new List<String> {}, new List<String> {},new List<String> {},new List<String> {'INSERT'});
            Assert.areEqual(result.size(), 1);
            List<String> result2 = CRMRetail_OOO_Tracking_H_HUM.callSDohAPIOnUpdateOrInsert(crmRetailRecord.Account__r.Enterprise_ID__c,crmRetailRecord.Id,null,new List<String>{'Financial Strain'},new List<String> {'Financial Strain'},new List<String>{},new List<String> {}, new List<String> {},new List<String> {},new List<String> {'INSERT'});
            Assert.areEqual(result2.size(),1); 
            CRMRetail_OOO_Tracking_H_HUM.checksOperactionTypeForSDohRecords(crmRetailRecord.Id,system.now(),new List<String>{'Financial Strain'},new List<String> {'Housing Insecurity'},new List<String>{},new List<String> {}, new List<String> {},new List<String> {});
           
            Test.stopTest();
        }
   
    }
    
    @isTest static void ExceptionchecksOperactionTypeForSDohRecords()
    {
        User oUser = [select Id,Name from User where name = 'test User1' limit 1]; 
        Assert.areEqual(oUser.Name,'test User1');
        system.runAs(oUser)
        { 
            Test.startTest();
            try{
                CRMRetail_OOO_Tracking_H_HUM.checksOperactionTypeForSDohRecords(null,null,null,null,null,null,null,null);
            }
            catch(Exception e)
            {
                Assert.areEqual('CRMRetail_OOO_Tracking_H_HUM.OneOnOneException',e.getTypeName());  
            }
            Test.stopTest();
        }
    }
    
     @isTest static void testCallUpdateServiceDown()
    {
        User oUser = [select Id,Name from User where name = 'test User1' limit 1]; 
        Assert.areEqual(oUser.Name,'test User1');
        
        system.runAs(oUser)
        { 
            Test.startTest();
            Test.setMock(HttpCalloutMock.class, new CRMRetail_SDoH_Helper_T_HUM.CRMRetail_SDoH_MockImplServiceUnavailable());  
            CRMRetail_OOO_Tracking__c crmRetailRecord=[SELECT Id,Account__r.Enterprise_ID__c,IsDeleted,SDoH_Screening_Completed__c,SDoH_Determinants__c,createdDate FROM CRMRetail_OOO_Tracking__c LIMIT 1 ];
            Assert.areEqual(crmRetailRecord.SDoH_Screening_Completed__c, TRUE);
            List<String> result = CRMRetail_OOO_Tracking_H_HUM.callSDohAPIOnUpdateOrInsert(crmRetailRecord.Account__r.Enterprise_ID__c,crmRetailRecord.Id,system.now(),new List<String>{},new List<String> {},new List<String>{'Housing Insecurity','Food Insecurity'},new List<String> {'Loneliness','Food Insecurity'}, new List<String> {},new List<String> {},new List<String> {'UPDATE'});
            Assert.areEqual(result.size(), 1);          
            Test.stopTest();
        }        
    }
  @isTest static void testCallDeleteServiceDown()
    {
        User oUser = [select Id,Name from User where name = 'test User1' limit 1]; 
        Assert.areEqual(oUser.Name,'test User1');
        
        system.runAs(oUser)
        { 
            Test.startTest();
            Test.setMock(HttpCalloutMock.class, new CRMRetail_SDoH_Helper_T_HUM.CRMRetail_SDoH_MockImplServiceUnavailable());  
            CRMRetail_OOO_Tracking__c crmRetailRecord=[SELECT Id,Account__r.Enterprise_ID__c,SDoH_Screening_Completed__c FROM CRMRetail_OOO_Tracking__c LIMIT 1 ];
            Assert.areEqual(crmRetailRecord.SDoH_Screening_Completed__c, TRUE);
            List<String> result= CRMRetail_OOO_Tracking_H_HUM.callSDohAPIOnUpdateOrInsert(crmRetailRecord.Account__r.Enterprise_ID__c,crmRetailRecord.Id,system.now(),new List<String>{},new List<String> {},new List<String>{},new List<String> {}, new List<String> {'loneliness','Food Insecurity'},new List<String> {'loneliness'},new List<String> {'DELETE'});
            Assert.areEqual(result.size(), 1);
            Test.stopTest();
        }        
    }
    @isTest static void testSDohDelFromButtonServiceDown()
    {
        User oUser = [select Id,Name from User where name = 'test User1' limit 1]; 
        Assert.areEqual(oUser.Name,'test User1');
        
        system.runAs(oUser)
        { 
            Test.startTest();
            Test.setMock(HttpCalloutMock.class, new CRMRetail_SDoH_Helper_T_HUM.CRMRetail_SDoH_MockImplServiceUnavailable());  
            CRMRetail_OOO_Tracking__c crmRetailRecord=[SELECT Id,Account__r.Enterprise_ID__c,IsDeleted,SDoH_Screening_Completed__c,SDoH_Determinants__c,createdDate FROM CRMRetail_OOO_Tracking__c LIMIT 1 ];
            Assert.areEqual(crmRetailRecord.SDoH_Screening_Completed__c, TRUE);
            String result = CRMRetail_OOO_Tracking_H_HUM.deleteSDoHBeforeOOOTrackingDelete(crmRetailRecord.Id,False);
            system.assert(String.isNotBlank(result));
            Test.stopTest();
        }        
    }
    
    @isTest static void testGetRecentOOORecords()
    {
        User oUser = [select Id,Name from User where name = 'test User1' limit 1]; 
        Assert.areEqual(oUser.Name,'test User1');
        
        system.runAs(oUser)
        { 
            Test.startTest();
            Integer recordLimit = Integer.valueOf(System.label.CRMRetail_Recent_OOO_Record_Limit);
            CRMRetail_OOO_Tracking__c crmRetailRecord  = [select id,Account__c from CRMRetail_OOO_Tracking__c limit 1];
            List<CRMRetail_OOO_Tracking__c> oooRecords =  (List<CRMRetail_OOO_Tracking__c>)CRMRetail_OOO_Tracking_H_HUM.getRecentOOORecords('',crmRetailRecord.Id).sResult;
            Assert.areEqual((recordLimit-1),oooRecords.size());
            oooRecords =  (List<CRMRetail_OOO_Tracking__c>)CRMRetail_OOO_Tracking_H_HUM.getRecentOOORecords(crmRetailRecord.Account__c,'').sResult;
            Assert.areEqual(recordLimit,oooRecords.size());
            Test.stopTest();
        }        
    }
   
   
}
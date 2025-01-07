/**************************************************************************** 
Apex Class Name  : HumanaRewards_C_T_HUM
Version          : 
Created Date     : April 9th, 2020
Function         : Test Class  for HumanaRewards_C_HUM
Test Class       : 
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
*  Santhapuri Kishore                          09/04/2020                  Original Version
*  Anjani Vaddadi                              05/13/2020                  Changed the response parse to buildwrapper
*************************************************************************************************** */
@isTest
public class HumanaRewards_C_T_HUM {
/*
* Method Name : setup
* Description : Create common test Data
* Return type : void
* Parameter   : None
*/ 
@testSetup static void setup() 
      {
          
        User oCCSUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oCCSUser;
        system.assertEquals(oCCSUser.LastName,'test User1');
        System.runAs (oCCSUser) 
        {
            List<Account> accountList = new List<Account>();   
            Account objAcc = HUMTestDataHelperTest.getAccount(); 
            objAcc.FirstName = 'Test';
            objAcc.LastName = 'Account';
            objAcc.Tenant_Id__c = '00';
            objAcc.Enterprise_ID__c = '1005577525';
            objAcc.Mbr_Gen_Key__c = '12345676';
            objAcc.General_Account__c = true;
            objAcc.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get(System.label.HUMCOBMemberLabel).getRecordTypeId();
            accountList.add(objAcc);
            System.assertEquals(objAcc.Tenant_Id__c,'00');
            System.assertEquals(objAcc.Enterprise_ID__c,'1005577525');
            System.assertEquals(objAcc.LastName,'Account');
            
            Account objAcc2 = HUMTestDataHelperTest.getAccount(); 
            objAcc2.FirstName = 'Test';
            objAcc2.LastName = 'Account2';
            objAcc2.Tenant_Id__c = '01';
            objAcc2.Enterprise_ID__c = '';
            objAcc2.General_Account__c = true;
            objAcc2.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get(System.label.HUMCOBMemberLabel).getRecordTypeId();
            accountList.add(objAcc2);
            System.assertEquals(objAcc2.Tenant_Id__c,'01');
            System.assertEquals(objAcc2.Enterprise_ID__c,'');
            System.assertEquals(objAcc2.LastName,'Account2');
            insert accountList;
            system.assertequals(accountList.size(),2);
            
             Product2 oProd = new Product2(Name = 'Laptop X200',Segment_Type__c='MCR',ProductCode='MED', Major_LOB__c='MER');
            insert oProd;
            System.assertNotEquals(oProd.Id, NULL, 'Value not matched');
              
            List<Policy__c> policyList = new List<Policy__c>();
            Policy__c objpol = HUMTestDataHelperTest.getPolicy();
            objpol.Name='testPolicy';
            objpol.Group_Name__c = objAcc.Id;
            objpol.Major_LOB__c = 'MER';
            objpol.Source_Cust_Cov_Key__c = '123456789';
            objpol.Product__c = oProd.Id;
            objpol.Product__r = oProd;
            objpol.Platform__c = 'LV';
            
            
            policyList.add(objpol);
            system.assertEquals(objpol.Source_Cust_Cov_Key__c,'123456789');
            system.assertEquals(objpol.Group_Name__c,objAcc.Id);
            system.assertEquals(objpol.Name,'testPolicy');
            
            Policy__c objpol2 = HUMTestDataHelperTest.getPolicy();
            objpol2.Name='testPolicy2';
            objpol2.Group_Name__c = objAcc2.Id;
            objpol2.Major_LOB__c = 'MER';
            objpol2.Source_Cust_Cov_Key__c = '123456789';
            objpol2.Product__c = oProd.Id;
            objpol2.Product__r = oProd;
            objpol2.Platform__c = 'LV';
            policyList.add(objpol2);
            system.assertEquals(objpol2.Source_Cust_Cov_Key__c,'123456789');
            system.assertEquals(objpol2.Group_Name__c,objAcc2.Id);
            system.assertEquals(objpol2.Name,'testPolicy2');
            insert policyList;
            system.assertequals(policyList.size(),2);
            
            List<Policy_Member__c> polMemberList = new List<Policy_Member__c>();
            Policy_Member__c objPolMem = new Policy_Member__c();
            objPolMem.Name = 'Policy Member PM1';
            objPolMem.Relationship__c = 'Self';
            objPolMem.Subscriber__c = objPolMem.Id;
            objPolMem.Effective_Date__c='12/01/2015';
            objPolMem.End_Date__c='11/16/2016';
            objPolMem.Policy_Member_External_ID__c='00|ODS|6925004434817|12345678|34567|2013-07-01';
            objPolMem.Member__c = objAcc.Id;
            objPolMem.Policy__c = objpol.Id;
            objPolMem.Policy__r = objpol;
            polMemberList.add(objPolMem);
            system.assertEquals(objPolMem.Relationship__c, 'Self');
            system.assertEquals(objPolMem.Name, 'Policy Member PM1');
            system.assertEquals(objPolMem.Policy_Member_External_ID__c, '00|ODS|6925004434817|12345678|34567|2013-07-01');       
            
            Policy_Member__c objPolMem2 = new Policy_Member__c();
            objPolMem2.Name = 'testpolmem22';
            objPolMem2.Relationship__c = 'Self';
            objPolMem2.Subscriber__c = objPolMem2.Id;
            objPolMem2.Effective_Date__c=Date.today().addDays(-1).format();
            objPolMem2.End_Date__c=null;
            objPolMem2.Policy_Member_External_ID__c='00|ODS|6925004434817|12345678|34567|2013-08-01';
            objPolMem2.Member__c = objAcc2.Id;
            objPolMem2.Policy__c = objpol2.Id;
            objPolMem2.Policy__r = objpol2;
            polMemberList.add(objPolMem2);
            system.assertEquals(objPolMem2.Relationship__c, 'Self');
            system.assertEquals(objPolMem2.Name, 'testpolmem22');
            system.assertEquals(objPolMem2.Policy_Member_External_ID__c, '00|ODS|6925004434817|12345678|34567|2013-08-01');
            insert polMemberList;
            system.assertequals(polMemberList.size(),2);
            
            HUM_Webservice_Callout__c oGetRewardsService = new HUM_Webservice_Callout__c();
            oGetRewardsService.name = 'Rewardservice';
            oGetRewardsService.Active__c = true;
            oGetRewardsService.Certificate_Name__c = 'test';
            oGetRewardsService.End_Point_URL__c = 'https://testst-crmservicews.humana.com';
            oGetRewardsService.Timeout__c = '120000';
            oGetRewardsService.Content_Type__c = 'application/json';
            oGetRewardsService.Request_Type__c = 'POST';
            insert oGetRewardsService;
            system.assert(oGetRewardsService.id != null);
            system.assert(oGetRewardsService.name == 'Rewardservice');
          }
      }
/*
* Method Name : runPositiveData
* Description : Used to run scenerios of test data
* Return type : void
* Paramater   : NA
*/ 
    static testMethod void runPositiveData(){
        User oCCSUser = [Select id,UserName from User where LastName = 'test User1' LIMIT 1 ] ;
        system.assertEquals(oCCSUser.UserName,'user1user1@humcrmstest.com');
           
        System.runAs (oCCSUser) 
        { 
           Test.StartTest();
           Account oAccount = [Select Id,Name,Tenant_Id__c,RecordType.Name,Enterprise_ID__c  from Account where LastName = 'Account'];  
           System.assertEquals(oAccount.Tenant_Id__c, '00');
           String sAccRecType = oAccount.RecordType.Name;
           System.assertEquals(sAccRecType,'Member');
           Policy_Member__c oPolMem =  [Select Id,Relationship__c, Member__c, Member__r.Enterprise_ID__c from Policy_Member__c where Member__c =: oAccount.Id];
           System.assertEquals(oPolMem.Relationship__c, 'Self');
            ApexPages.currentPage().getParameters().put('Id',oPolMem.Id);
           HumanaRewards_C_HUM oHUMRewards = new HumanaRewards_C_HUM();
           
           oHUMREwards.MemberGenkey='8004547422';
           oHUMRewards.sRecordID =oPolMem.Id;
           oHUMRewards.MemberYear=null;
           
           oHUMRewards.EligibilityDate=null;
           oHUMRewards.listEligAct=null;
           oHUMRewards.listRewEarned=null;
           oHUMRewards.listRewRedeem=null;
           oHUMRewards.oRewardDetailsDTO=null;
           
           oHUMRewards.sRequest='{"GetRewardsDetailsRequest": {"MemberGenKey": "8004547422","MemberYear" : null}}';           
           oHUMRewards.sResponse ='{ "GetRewardsDetailsResponse":{ "GetRewardsDetails":{ "Eligible":true, "FullfilmentVendorName":"RightSource", "MemberEnrolledDate":"2017-06-13", "MemberCancelledDate":null, "MemberTerminationDate":null, "Accountstatus":"TERMINATED", "CurrentrewardLevel":"Member does not have 5 Rewards yet.", "CurrentBalance":0, "FullfilmentVendorHoursofoperation":"Monday - Friday  8:00 AM - 11:00 PM   ET  Saturday  8:00 AM - 6:30 PM   ET", "FullfilmentVendorExternalPhoneNumber":"1-800-968-2281", "FullfilmentVendorInternalPhoneNumber":"1-800-968-2281", "RewardCoverages":{ "EligibilityDate":"2019-01-01", "EligibleActivities":[ { "EarningOpportunity":"Diabetic Retinal Exam", "EligibilityDate":"2019-07-11", "PotentialPointsToEarn":0, "MaximumEarnings":"2019-09-30" }  ], "RewardsEarned":[ { "EarningOpportunity":"Diabetic Retinal Exam", "CompletionDate":"2019-07-11", "Points_RewardsEarned":0, "TimeFrameforRedeemedpoints_Earnings":"2019-09-30" }, { "EarningOpportunity":"Cardiovascular Disease Screening", "CompletionDate":"2019-05-17", "Points_RewardsEarned":0, "TimeFrameforRedeemedpoints_Earnings":"2019-09-30" } ], "RewardsRedeemed":[ { "Points_Rewardsredeemed":0, "RedemptionDate":"2019-07-11", "RedemptionItem":"Diabetic Retinal Exam", "PurchasePrice":"3.00", "ConfirmationNumber":"2019-09-30", "RewardMailingAddress":"xyz" }  ] } } } }';
            HttpResponse oResponse = new HttpResponse();
            oResponse.setHeader('Content-Type', 'application/json');
            oResponse.setHeader('SOAPAction', '');    
            oResponse.setHeader('Connection','keep-alive');
            oResponse.setBody(oHUMRewards.sResponse);
            oResponse.setStatusCode(200);  
            
           HumanaRewards_DTO_HUM orewardDetailsWrapper = new HumanaRewards_DTO_HUM();           
           //oHUMRewards.odResponse=null;
           oHUMRewards.odResponse= (HumanaRewardsResponse_H_HUM)JSON.deserialize(oHUMRewards.sResponse,HumanaRewardsResponse_H_HUM.class);
           HumanaRewardsServiceDetailsRequest_H_HUM oRewardsReq = new HumanaRewardsServiceDetailsRequest_H_HUM();
           HUmanaRewards_S_HUM oRewardsSer = new HUmanaRewards_S_HUM();
           Object returnRewardsReq = oHUMRewards.rewardonloadrequest();
            
           Continuation objCon = New Continuation(120);            
           objCon.continuationMethod='processResponse';
           oHUMRewards.processResponse();
           system.assert(oHUMRewards!=null);
           oHUMRewards.responseDeserialize(oResponse);
           oHUMRewards.buildWrapper(oHUMRewards.odResponse.GetRewardsDetailsResponse);
           
            
           
            
            //test for  getlistOfObjects
            List<SelectOption> dateBox =oHUMRewards.getdropdown();           
            system.assert(dateBox!=null);
           
        }
    }
}
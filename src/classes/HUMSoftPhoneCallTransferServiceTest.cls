/**************************************************************************** 
Apex Class Name  : HUMSoftPhoneCallTransferServiceTest 
Version          : 1.0  
Created Date     : August 4 2014 
Description      : This class is used as test class for HUMSoftPhoneCallTransferService 
                   
****************************************************************************
Modification Log:

* Developer Name          Code Review                Date                        Description
*------------------------------------------------------------------------------------------------------------
* Anil Pilaka               17169                   08/4/2014                     Original Version
* Shruthi Karanth           17532                   08/21/2014                    Moved Group search service to seperate method
* Anil Pilaka               17670                   09/03/2014                    Create custom setting record for cipher cloud
* Shruthi Karanth           18178                   09/25/2014                    Updated as per changes to the class
* Shruthi Karanth           18234                   09/29/2014                    Modified logic as inner class is removed
                                                                                  from HUMPolicyMemberRelatedListController
* Ranjeeth Nagishetty       19474                   01/10/2014                    REQ-95950 Validation rule
* Shreya Choodamani         20285                   01/12/2015                    Added code to fix the test class failure
* Reena Rajan               21755                   03/23/2015                    Hard coded values moved to custom settings in the class as per Code Review Comments Fix. Test class has been adjusted to create the new custom setting values.
* Gargey Sharma             23879                   06/11/2015                    Removed setMock for DPaaS Service as per REQ - 191230
* Harika Devi Kalidindi     112000                  07/20/2017                    REQ - 323435 :Added Policy_Group_Number__c field value while inserting policy
* Asish Kumar Behera                                11/10/2020                    1309260 PR00095366 - FR 01.06A1  Softphone app must have access to system-of-record
***************************************************************************************************/

@isTest
private Class HUMSoftPhoneCallTransferServiceTest 
{
 
    /*
    * Method Name : callTransferDataTest
    * Description : This method is used for testing soft phone call transfer service
    * Return type : void
    * Paramater   : NA
    */
    static testMethod void callTransferDataTest()
    {
        User user = HUMTestDataHelperTest.createTestUser('TestUser1','System Administrator'); 
        insert user;
        //Load Constants from Custom Setting                
        HUMTestDataHelperTest.getHUMConstantsData();
        System.runas(user) 
        {
            Profile prof = [SELECT Id FROM Profile WHERE Name='System Administrator']; 
            User u = new User(Alias = 'softTest', Email='test@softphone.com', 
            EmailEncodingKey='UTF-8', LastName='Testing', LanguageLocaleKey='en_US', 
            LocaleSidKey='en_US', ProfileId = prof .Id, 
            TimeZoneSidKey='America/Los_Angeles', UserName='softphoneuser@testorg.com');       
            
            //Create Member Data
            string sMemberRecordtypeID=  Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId();  
            Account actMember = new Account(Lastname='Test Member Account',recordtypeid = sMemberRecordtypeID);    
            insert actMember;  
            string sGroupRecordtypeId=  Schema.SObjectType.Account.getRecordTypeInfosByName().get('Group').getRecordTypeId();  
            Account actGroup = new Account(Name ='Test Group Account',recordtypeid =sGroupRecordtypeId,Group_Number__c='88996');  
            insert actGroup ; 
            
            //Assert the member data is created
            system.assert(actMember.id!=null);
            
            //Create Policy, Product and Policy Member Data
            Product2 p = new Product2(Name='MER',ProductCode='MER');
            insert p;
            
            //Assert the product data is created
            system.assert(p.id!=null);
            Policy__c oPolicy;
            List<Policy__c> policyList = new List<Policy__c>();
            policyList.add(new Policy__c(name = 'Test Policy', Group_Name__c = actGroup.Id,Product__c=p.id,Policy_Group_Number__c='11111',Platform__c='LV'));
            policyList.add(new Policy__c(name = 'Test Policy1', Group_Name__c = actGroup.Id,Product__c=p.id,Policy_Group_Number__c='2222',Platform__c='EM'));
            
            insert policyList;  
            oPolicy = policyList[0];  
            
            List<Policy_Plan_Details__c> listofPolicyPlans =  new List<Policy_Plan_Details__c>();
            Policy_Plan_Details__c plan1 = new Policy_Plan_Details__c(name = 'Current',Policy__c=policyList[1].id,iab_description__c = 'Current Plan',Coverage_Plan_Effective_Date__c = '01/01/2013',
                                                                      Coverage_Plan_End_Date__c = '01/01/9999',Policy_Plan_External_ID__c = '23',Platform__c = 'EM',Source_Cust_Cov_Key__c = '889888888889856');
            Policy_Plan_Details__c plan2 = new Policy_Plan_Details__c(name ='past',Policy__c=oPolicy.id,iab_description__c = 'past Plan',Coverage_Plan_Effective_Date__c = '01/01/2012',
                                                                      Coverage_Plan_End_Date__c = '01/01/2013',Policy_Plan_External_ID__c = '12',Platform__c = 'LV',Source_Cust_Cov_Key__c = '889888888889821');
            Policy_Plan_Details__c planFuture = new Policy_Plan_Details__c(name ='past',Policy__c=policyList[1].id,iab_description__c = 'past Plan',Coverage_Plan_Effective_Date__c = '01/01/2025',
                                                                      Coverage_Plan_End_Date__c = '01/01/9999',Policy_Plan_External_ID__c = '52',Platform__c = 'EM',Source_Cust_Cov_Key__c = '889888888889889');
            listofPolicyPlans.add(plan1);
            listofPolicyPlans.add(plan2);
            listofPolicyPlans.add(planFuture);
            Insert listofPolicyPlans;
            
            List<Policy_Member__c> policyMemberLst = new List<Policy_Member__c>();
            Policy_Member__c oPolicyMem = new Policy_Member__c(name = 'Test Policy Mem', Policy__c = oPolicy.Id, Member__c = actMember.id );  
            policyMemberLst.add(oPolicyMem);  
            
            Policy_Member__c oPolicyMem1 = new Policy_Member__c(name = 'Test Policy Mem1',Policy__c = oPolicy.Id, Member__c =  actMember.id );    
            policyMemberLst.add(oPolicyMem1); 
             //Policy_Member__c oPolicyMem2 = new Policy_Member__c(name = 'Test Policy Mem2',Policy__c = oPolicy.Id, Member__c =  actMember.id,Effective_Date__c = '01/01/2013',End_Date__c ='12/01/2013'  );    
            //policyMemberLst.add(oPolicyMem2); 
            
            insert policyMemberLst;  
            
            //Assert the policy member data is created
            system.assertEquals(policyMemberLst.size(),2);
            
            //Create Interaction Data
            Interaction__c interAction = new Interaction__c(Interaction_Origin__c = 'Inbound Call', Interacting_With__c=actGroup.id,UUID__c='Test UUID',Interacting_With_type__c= 'Group');
            insert interAction ;
            
             //Assert the interaction data is created
            system.assert(interAction.id<>null);
             
            Interaction_Member__c intMem = new Interaction_Member__c(Interacting_About__c = actMember.id,Interaction__c = interAction.id ,Interacting_About_Type__c ='Member');             
            insert intMem; 
             
            //Assert the interaction member is created
            system.assert(intMem.id<>null);
              
                        
            //Create Interaction Data for soft phone
            Integration__c intRec = new Integration__c(Name=System.Label.HUMIntegrationTypeSoftPhone);
            insert intRec;
            
            //Assert the integration data is created
            system.assert(intRec.id<>null);
            
            //Create a case for call transfer
            Case memberCase = new Case(AccountId=actMember.id,Policy_Member__c=policyMemberLst[0].id,Interacting_With_Name__c='TEST');
            insert memberCase ;
            
            //Assert the member case data is created
            system.assert(memberCase.id<>null);
            
            //Create custom setting data for group and cipher cloud integrations
            
            List<HUMConstants__c> webserConsLst = new List<HUMConstants__c>();
            webserConsLst.add(new HUMConstants__c(Name='GRPSVC_CLIENT_NAME',StringValue__c='CCP'));
            webserConsLst.add(new HUMConstants__c(Name='GRPSVC_APP_USER',StringValue__c='TEST0195'));
            webserConsLst.add(new HUMConstants__c(Name='GRPSVC_PLATFORM',StringValue__c='EM'));
            insert webserConsLst;
            
            //Assert the group service custom setting data is created
            system.assertEquals(webserConsLst.size(),3);
            
            List<HUMSoftPhoneDeriveSellingLedger__c> lobs = new List<HUMSoftPhoneDeriveSellingLedger__c>();
            lobs.add(new HUMSoftPhoneDeriveSellingLedger__c(Name = 'Medicare LOB', StringValue__c='00008',LOB__c = 'MER,MEF,MEP,MES,MGF,MGS,MRO,MGR,PDP,MGP,MPD,MGO,OSB'));
            lobs.add(new HUMSoftPhoneDeriveSellingLedger__c(Name = 'Medicaid LOB', StringValue__c='00008',LOB__c = 'MCD'));
            lobs.add(new HUMSoftPhoneDeriveSellingLedger__c(Name = 'IsHumanaOneSellingLegderNum', StringValue__c='00007'));
            lobs.add(new HUMSoftPhoneDeriveSellingLedger__c(Name = 'LOBCombination1', StringValue__c='00010',LOB__c = 'PDP,MDP'));
            lobs.add(new HUMSoftPhoneDeriveSellingLedger__c(Name = 'LOBCombination2', StringValue__c='00011',LOB__c = 'MEF,MGF'));
            lobs.add(new HUMSoftPhoneDeriveSellingLedger__c(Name = 'DentalProductLine', StringValue__c='00009',LOB__c = 'DENTAL'));
            insert lobs;
            
            //Assert the product LOB custom setting data is created
            system.assertEquals(lobs.size(),6);
            
            //Cipher Cloud Custom Setting Data
            HUMCipherCloudSettings__c ccData = new HUMCipherCloudSettings__c(Name='APPLICATION_NAME',Value__c = 'CRMS TESTING');
            insert ccData;  
                     
            //Create integration mapping record for call transfer
            
            Integration_Mapping__c intMappingRec = new Integration_Mapping__c();
            intMappingRec.Mapping__c ='Case:'+memberCase.id+';Policy Member:'+policyMemberLst[0].id+';Inquired For:'+actMember.id+';Inquired About:'+actMember.id+';Interaction Id:'+interAction.id;
            intMappingRec.Integration_ID__c = intRec.id;
            intMappingRec.Record_Id__c = 'Test UUID';
            insert intMappingRec ; 
            
             //Assert the integration mapping record is created
            system.assert(intMappingRec.id<>null);
            
            Integration_Mapping__c intMappingRec1 = new Integration_Mapping__c();
            intMappingRec1.Mapping__c ='Case:123434;Policy Member:123434;Inquired For:123434;Inquired About:123434;Interaction Id:123434';
            intMappingRec1.Integration_ID__c = intRec.id;
            intMappingRec1.Record_Id__c = 'UUID DUMMY TEST';
            insert intMappingRec1 ; 
            
            //Assert the integration mapping record is created
            system.assert(intMappingRec1.id<>null);
            
            CRMFunctionality_ONOFF_Switch__c objCS = new CRMFunctionality_ONOFF_Switch__c(
                Name = '1309260',
                IsON__c=true,
                Release_Name__c='12/18/2020',
                Scrum_Team__c='Incredibles',
                Story_Title__c='PR00095366 - FR 01.06A1  Softphone app must have access to system-of-record');
            
            insert objCS;
            
            //Execute the test for the above data
            
            test.starttest();      
                
                //Test the call transfer helper class
                HUMSoftPhoneCallTransferHelper humSoftPhoneResponse = HUMSoftPhoneCallTransferService.getCallTransferData('Test UUID'); 
                system.assert(humSoftPhoneResponse != null);      
    
                //-ve testing for call transfer service with improper intergation record
                HUMSoftPhoneCallTransferHelper humSoftPhoneNegativeResponse = HUMSoftPhoneCallTransferService.getCallTransferData('Test'); 
                //system.assert(humSoftPhoneNegativeResponse == null); 
                
                //-ve testing for call transfer service with improper mapping details intergation record
                HUMSoftPhoneCallTransferHelper humSoftPhoneCreateNegativeResponse = HUMSoftPhoneCallTransferService.getCallTransferData('UUID DUMMY TEST'); 
                //system.assert(humSoftPhoneCreateNegativeResponse == null); 
                
                //-ve testing for call transfer service when there is no intergation record
                HUMSoftPhoneCallTransferHelper humSoftPhoneNullResponse = HUMSoftPhoneCallTransferService.createResponse(null); 
                //system.assert(humSoftPhoneNullResponse == null); 
                
                //Testing market Id for a selected policy
                HUMSoftPhoneCallTransferService.deriveMarketId([select id,Policy__r.Product__r.Name from Policy_Member__c where id=:policyMemberLst[0].id limit 1]); 
                p.Name = 'MED'; update p;
                HUMSoftPhoneCallTransferService.deriveMarketId([select id,Policy__r.Product__r.Name from Policy_Member__c where id=:policyMemberLst[0].id limit 1]);
                p.Name = 'PDP'; update p;
                HUMSoftPhoneCallTransferService.deriveMarketId([select id,Policy__r.Product__r.Name from Policy_Member__c where id=:policyMemberLst[0].id limit 1]);
                p.Name = 'MGF'; update p;
                HUMSoftPhoneCallTransferService.deriveMarketId([select id,Policy__r.Product__r.Name from Policy_Member__c where id=:policyMemberLst[0].id limit 1]);
                p.Name = 'DENTAL'; update p;
                HUMSoftPhoneCallTransferService.deriveMarketId([select id,Policy__r.Product__r.Name from Policy_Member__c where id=:policyMemberLst[0].id limit 1]);
                            
                //Test the policy member related list
                
                PageReference pageRef = Page.HUMAccountDetailPage;
                Test.setCurrentPage(pageRef);
                Apexpages.currentPage().getParameters().put('id',actMember.id);
                HUMPolicyMemberRelatedListController  plycon = new HUMPolicyMemberRelatedListController();
                plycon.sAccId = actMember.id;
                plycon.sInteractionId='';
                ApexPages.StandardSetController c;
                plycon.setConPolicies = c;
                List<HUMPolicyAccessDetailsWrapperHelper> policyListDetails = new List<HUMPolicyAccessDetailsWrapperHelper>();
                plycon.next();
                plycon.previous();
                policyListDetails = plycon.lstpolicyAccessWrapper;
                plycon.saveDataForCallTransfer();
                system.assert(ApexPages.hasMessages()==true);
                plycon.sInteractionId= '&interactionId='+interAction .id;            
                policyListDetails = plycon.lstpolicyAccessWrapper;
                plycon.saveDataForCallTransfer();
                system.assert(ApexPages.hasMessages()==true);
            
             //Stop the test
            test.stoptest();
        }
    } //End of test method
  
    /*
    * Method Name : fetchIsHumana
    * Description : This method is used for testing Group service which is called to get the IsHumanaOne value
    * Return type : void
    * Paramater   : NA
    */
    static testMethod void fetchIsHumana()
    {
        User u = HUMTestDataHelperTest.createTestUser('TestUser1','System Administrator'); 
        insert u;
        
        System.runas(u) 
        {
            List<HUMConstants__c> webserConsLst = new List<HUMConstants__c>();
            webserConsLst.add(new HUMConstants__c(Name='GRPSVC_CLIENT_NAME',StringValue__c='CCP'));
            webserConsLst.add(new HUMConstants__c(Name='GRPSVC_APP_USER',StringValue__c='TEST0195'));
            webserConsLst.add(new HUMConstants__c(Name='GRPSVC_PLATFORM',StringValue__c='EM'));
            insert webserConsLst;
            insert new HUM_Webservice_Callout__c( Name = 'GroupSearchService' , Certificate_Name__c = 'Humana_GuidanceCare_R1', Active__c = true, End_Point_URL__c = 'http://www.google.com', Timeout__c = '12000');
            Test.setMock(WebServiceMock.class, new HUMIntGroupSearchServiceTest());
            
            test.startTest();
                Boolean bIsHumanaOne = HUMSoftPhoneCallTransferService.fetchIsHumanaOne('GRP123');
                System.assertEquals(bIsHumanaOne, true);
            test.stopTest();
        } 
     }
}//End of test class
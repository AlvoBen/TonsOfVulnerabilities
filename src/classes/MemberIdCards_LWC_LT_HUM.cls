/*******************************************************************************************************************************
Apex Class Name : MemberIdCards_LWC_LT_HUM.cls
Version         : 1.0
Created On      : Feb 09,2022
Function        : Class contains test methods for
				  MemberIdCards_LWC_LC_HUM
				  MemberIdCards_LWC_LC_HUM
				  MemberIdCards_LWC_LS_HUM
				  MemberIdCards_LWC_LD_HUM
				  CICMSUpdateRequest_LDTO_HUM  
				  CICMSUpdateResponse_LDTO_HUM 
				  CICMSInquiryRequest_LDTO_HUM 
				  CICMSInquiryResponse_LDTO_HUM
				  HIDCMSOrderIdCardResponse_LDTO_HUM classes.

Modification Log: 
 * Developer Name            Code Review                Date                       Description
 *------------------------------------------------------------------------------------------------------------------------------
 * Vamshi Krishna Pemberthi                              02/09/2022                   Initial Version 
 * Anuradha Gajbhe										 01/10/2023			          Build fix
 * Dimple Sharma										 10/23/2023					  US-5232125 Code coverage Improvements
 ********************************************************************************************************************************/
@isTest
private class MemberIdCards_LWC_LT_HUM 
{
    Public Static final String MEMBERIDCARD_HSA = 'HSA';
    
    /**
    * @testSetup method to create test data
    *<p>
    * @param            NULL
    * @return           void
    */
    @testSetup static void setUpTestData()
    {
        User objUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        objUser.Current_Queue__C = 'Test';
        insert objUser;
        
        Group oGrp = new Group(Name ='Test', Type = 'Queue');
        insert oGrp;
        system.assertEquals(oGrp.Type,'Queue');
        

        
        List<HUMAccountCaseRecordTypes__c> HUMAccountCaseRecordTypesList = new List<HUMAccountCaseRecordTypes__c>();
        HUMAccountCaseRecordTypes__c HUMAccountCaseRecordTypes1 = new HUMAccountCaseRecordTypes__c(Name = 'Group' ,Account_RecordType__c = 'Group' , Case_RecordType__c = 'Group Case');
        HUMAccountCaseRecordTypesList .add(HUMAccountCaseRecordTypes1);
        HUMAccountCaseRecordTypes__c HUMAccountCaseRecordTypes2 = new HUMAccountCaseRecordTypes__c(Name = 'Agent/Broker' ,Account_RecordType__c = 'Agent/Broker' , Case_RecordType__c = 'Agent/Broker Case');
        HUMAccountCaseRecordTypesList .add(HUMAccountCaseRecordTypes2);        
        HUMAccountCaseRecordTypes__c HUMAccountCaseRecordTypes3 = new HUMAccountCaseRecordTypes__c(Name = 'Provider' ,Account_RecordType__c = 'Provider' , Case_RecordType__c = 'Provider Case');
        HUMAccountCaseRecordTypesList .add(HUMAccountCaseRecordTypes3);        
        HUMAccountCaseRecordTypes__c HUMAccountCaseRecordTypes4 = new HUMAccountCaseRecordTypes__c(Name = 'Member' ,Account_RecordType__c = 'Member' , Case_RecordType__c = 'Member Case');
        HUMAccountCaseRecordTypesList .add(HUMAccountCaseRecordTypes4);        
        insert HUMAccountCaseRecordTypesList;
        
        List<HUMCaseInteractionAboutType__c > HUMCaseInteractionAboutTypesList = new List<HUMCaseInteractionAboutType__c>();
        HUMCaseInteractionAboutType__c HUMCaseInteractionAboutTypes = new HUMCaseInteractionAboutType__c(Name = 'Unknown Member' ,CaseInteraction_About_Type__c = 'Unknown-Member' );
        HUMCaseInteractionAboutType__c HUMCaseInteractionAboutTypes1 = new HUMCaseInteractionAboutType__c(Name = 'Member' ,CaseInteraction_About_Type__c = 'Member' );
        HUMCaseInteractionAboutType__c HUMCaseInteractionAboutTypes2 = new HUMCaseInteractionAboutType__c(Name = 'Group', CaseInteraction_About_Type__c = 'Group');
        HUMCaseInteractionAboutTypesList.add(HUMCaseInteractionAboutTypes);
        HUMCaseInteractionAboutTypesList.add(HUMCaseInteractionAboutTypes1);        
        HUMCaseInteractionAboutTypesList.add(HUMCaseInteractionAboutTypes2);
        insert HUMCaseInteractionAboutTypesList;
        
        List<HUM_Webservice_Callout__c> callouts = new List<HUM_Webservice_Callout__c>();
        HUM_Webservice_Callout__c RighSourceServiceDetails = new HUM_Webservice_Callout__c(Name ='HIDCMS_MemberSearch',Certificate_Name__c = 'Humana_GuidanceCare_Alderaan' ,Active__c = true,End_Point_URL__c ='https://dev-crmservicews.humana.com/IDCardService/MemberSearch' ,Timeout__c = '12000',Request_Type__c = 'POST',Content_Type__c ='application/json' );
        callouts.add(RighSourceServiceDetails);
        HUM_Webservice_Callout__c OrderIdCardRequest = new HUM_Webservice_Callout__c(Name ='HIDCMS_OrderIdCard',Certificate_Name__c = 'Humana_GuidanceCare_Alderaan' ,Active__c = true,End_Point_URL__c ='https://dev-crmservicews.humana.com/IDCardService/OrderIdCard' ,Timeout__c = '12000',Request_Type__c = 'POST',Content_Type__c ='application/json' );
        callouts.add(OrderIdCardRequest);
        HUM_Webservice_Callout__c ccicmsDemographicInquiryRequest = new HUM_Webservice_Callout__c(Name ='CICMS_DemographicInquiry',Certificate_Name__c = 'Humana_GuidanceCare_Alderaan' ,Active__c = true,End_Point_URL__c ='/cms-idcard-proc-api-dev/api/DemographicInquiry' ,Timeout__c = '12000',Request_Type__c = 'POST',Content_Type__c ='application/json' );
        callouts.add(ccicmsDemographicInquiryRequest);
        HUM_Webservice_Callout__c ccicmsDemographicUpdate = new HUM_Webservice_Callout__c(Name ='CICMS_DemographicUpdate',Certificate_Name__c = 'Humana_GuidanceCare_Alderaan' ,Active__c = true,End_Point_URL__c ='/cms-idcard-proc-api-dev/api/DemographicInquiry' ,Timeout__c = '12000',Request_Type__c = 'POST',Content_Type__c ='application/json' );
        callouts.add(ccicmsDemographicUpdate);
        
        insert callouts;
        
        List <IDCards_GroupNumber_Restriction_List__c> lstCS = new List<IDCards_GroupNumber_Restriction_List__c>();
        IDCards_GroupNumber_Restriction_List__c objCS1 = new IDCards_GroupNumber_Restriction_List__c(Name='407126', GroupNumber__c = '407126');
        lstCS.add(objCS1);
        IDCards_GroupNumber_Restriction_List__c objCS2 = new IDCards_GroupNumber_Restriction_List__c(Name='407125', GroupNumber__c = '407125');
        lstCS.add(objCS2);
        IDCards_GroupNumber_Restriction_List__c objCS5 = new IDCards_GroupNumber_Restriction_List__c(Name='5A240001', GroupNumber__c = '5A240001');
        lstCS.add(objCS5);
        insert lstCS;
        
        HUMCaseNPI_TAXID__c oTaxNPI = new HUMCaseNPI_TAXID__c(Name = 'Member');
        insert oTaxNPI;

        
        CRMFunctionality_ONOFF_Switch__c oSwitch = New CRMFunctionality_ONOFF_Switch__c(
            Name = '3003419',
            IsON__c = True,
            Release_Name__c ='10/21/2023',
            Scrum_Team__c ='Thunder',
            Story_Title__c ='Test Class');
        
        insert oSwitch;
        
        
        system.runAs(objUser)
        { 
            
            // Insert Member Account
            Account objMemAccount = HUMTestDataHelperTest.getAccount();
            objMemAccount.FirstName = 'Test FName';
            objMemAccount.LastName = 'LName'; 
            objMemAccount.source_platform_code__c = 'EM' ;
            objMemAccount.Enterprise_Id__c = String.valueof(2000);
            objMemAccount.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId();
            objMemAccount.Birthdate__c = String.valueOf(System.today().month()+'/'+system.today().day()+'/'+System.today().year());
            objMemAccount.Home_Office_Account__c = true;
            insert objMemAccount;
            system.assertEquals(objMemAccount.LastName == 'LName', true);
            
            Account objMemAccount1 = HUMTestDataHelperTest.getAccount();
            objMemAccount1.FirstName = 'Test FName1';
            objMemAccount1.LastName = 'LName1'; 
            objMemAccount1.source_platform_code__c = 'EM' ;
            objMemAccount1.Enterprise_Id__c = String.valueof(2000);
            objMemAccount1.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId();
            objMemAccount1.Birthdate__c = String.valueOf(System.today().month()+'/'+system.today().day()+'/'+System.today().year());
            objMemAccount1.Home_Office_Account__c = true;
            insert objMemAccount1;

            Interaction__c objInteraction = HUMTestDataHelperTest.getInteraction();
             insert objInteraction;
             System.assertNotEquals(objInteraction, null, 'Interaction not created');
            
            Interaction_Member__c InteractionMember = new Interaction_Member__c();
             InteractionMember.Interacting_About__c = objMemAccount.id;
             InteractionMember.Interaction__c = objInteraction.id;
             InteractionMember.Interacting_About_Type__c ='Member';
             insert InteractionMember;
            
            Intent__c intentRec1 = New Intent__c();
            intentRec1.Name = 'Invoice Requests';
            intentRec1.Is_Active__c = true;
            insert intentRec1;
            System.assertEquals(intentRec1.Name,'Invoice Requests');
            
            List<Classification__c> classList = new List<Classification__c>();
            
            Classification__c classificationRec = New Classification__c();
            classificationRec.Name = 'HP Finance Ops';
            classificationRec.Is_Active__c = true;
            classList.add(classificationRec);
            
            Classification__c classificationRec2 = New Classification__c();
            classificationRec2.Name = 'Billing (Finance Ops)';
            classificationRec2.Is_Active__c = true;
            classList.add(classificationRec2);
            
            Classification__c classificationRec3 = New Classification__c();
            classificationRec3.Name = 'Billing Issue (CT & TM)';
            classificationRec3.Is_Active__c = true;
            classList.add(classificationRec3);
            
            insert classList;

            List<Classification_Type__c> classTypes = new List<Classification_Type__c>();
            
            Classification_Type__c ct = new Classification_Type__c(Name = 'Test', Is_Active__c = true, Guidance_Alert_Limit__c = 2);
            classTypes.add(ct);
            Classification_Type__c ct1 = new Classification_Type__c(Name = 'HP Finance Ops', Is_Active__c = true, Guidance_Alert_Limit__c = 2);
            classTypes.add(ct1);
            Classification_Type__c ct2 = new Classification_Type__c(Name = 'Billing Issue (CT & TM)', Is_Active__c = true, Guidance_Alert_Limit__c = 2);
            classTypes.add(ct2);
            Classification_Type__c ct3 = new Classification_Type__c(Name = 'Billing (Finance Ops)', Is_Active__c = true, Guidance_Alert_Limit__c = 2);
            classTypes.add(ct3);
            
            insert classTypes;
            
            Classification_Type__c objcT = new Classification_Type__c();
            objcT.Name ='CallRSOTest';
            objcT.Is_Active__c = true;
            insert objcT ;
            System.assertEquals(objCT.Name,'CallRSOTest');
            
            List<CTCI_Junction__c> objCTCIList = new List<CTCI_Junction__c>();
            
            CTCI_Junction__c objCTCI = new CTCI_Junction__c();
            
            objCTCI.Classification__c=classificationRec.id;
            objCTCI.Classification_Type__c= classTypes[0].id;
            objCTCI.Intent__c =intentRec1.id;
            objCTCI.IsActive__c=true;
            objCTCIList.add(objCTCI);
            
            CTCI_Junction__c objCTCI2 = new CTCI_Junction__c();
            objCTCI2.Classification__c=classificationRec2.id;
            objCTCI2.Classification_Type__c= classTypes[1].id;
            objCTCI2.Intent__c =intentRec1.id;
            objCTCI2.IsActive__c=true;
            objCTCIList.add(objCTCI2);
            
            CTCI_Junction__c objCTCI3 = new CTCI_Junction__c();
            objCTCI3.Classification__c=classificationRec.id;
            objCTCI3.Classification_Type__c= classTypes[2].id;
            objCTCI3.Intent__c =intentRec1.id;
            objCTCI3.IsActive__c=true;
            objCTCIList.add(objCTCI3);
            
            CTCI_Junction__c objCTCI4 = new CTCI_Junction__c();
            objCTCI4.Classification__c=classificationRec.id;
            objCTCI4.Classification_Type__c= classTypes[2].id;
            objCTCI4.Intent__c =intentRec1.id;
            objCTCI4.IsActive__c=true;
            objCTCIList.add(objCTCI4);
            
            CTCI_Junction__c objCTCI5 = new CTCI_Junction__c();
            objCTCI5.Classification__c=classificationRec3.id;
            objCTCI5.Classification_Type__c= classTypes[1].id;
            objCTCI5.Intent__c =intentRec1.id;
            objCTCI5.IsActive__c=true;
            objCTCIList.add(objCTCI5);
            
            
            
            insert objCTCIList;
            
            Product2 prod = new Product2(Name='MED');
            Product2 prod2 = new Product2(Name='DEN');
            insert prod;
            
            // Policy
            PurchaserPlan oMedicalPolicy = new PurchaserPlan();
            oMedicalPolicy.Policy_Group_Number__c='11111';
            oMedicalPolicy.Name = 'Medical Product';
            oMedicalPolicy.source_cust_cov_key__c = '12345678901234567890' ; 
            oMedicalPolicy.PayerId = objMemAccount.Id;
            oMedicalPolicy.Product__c = prod.Id;
            oMedicalPolicy.Major_LOB__c = MEMBERIDCARD_HSA;
            oMedicalPolicy.Effectivefrom  = Date.parse(Date.today().addDays(-400).format());     
            insert oMedicalPolicy;
            
            PurchaserPlan oMedicalPolicy2 = new PurchaserPlan();
            oMedicalPolicy2.Policy_Group_Number__c='11111';
            oMedicalPolicy2.Name = 'Medical Product';
            oMedicalPolicy2.source_cust_cov_key__c = '12345678901234567890' ; 
            oMedicalPolicy2.PayerId = objMemAccount1.Id;
            oMedicalPolicy2.Major_LOB__c = MEMBERIDCARD_HSA;
            oMedicalPolicy2.Effectivefrom  = Date.parse(Date.today().addDays(-400).format());     
            insert oMedicalPolicy2;
            
            Policy__c oMedicalPolicy1 = HUMTestDataHelperTest.getPolicy();
            oMedicalPolicy1.Name = 'Medical Product';
            oMedicalPolicy1.source_cust_cov_key__c = '12345678901234567890' ; 
            oMedicalPolicy1.Group_Name__c = objMemAccount.Id;
            oMedicalPolicy1.Major_LOB__c ='EPO';   
            oMedicalPolicy1.Business_Segment__c  = 'Individual-Medicare';
            oMedicalPolicy1.Coverage_Plan_Effective_Date__c  = '02/02/2018';     
            insert oMedicalPolicy1;
            system.assertEquals(oMedicalPolicy1.Name == 'Medical Product', true);
            // Policy Member
            MemberPlan policymember = New MemberPlan();
            policymember.RelationshipToSubscriber = 'Self';
            policymember.EffectiveFrom=Date.parse(Date.today().addDays(-1).format());
            policymember.EffectiveTo=Date.parse(Date.today().addDays(1).format());
            //MemberPlan objMedPolMem = HUMTestDataHelperTest.getPolicyMember();
            policymember.Memberid = objMemAccount.Id;
            policymember.ETL_Record_Deleted__c = false;
            policymember.Policy__c = oMedicalPolicy1.Id;
            policymember.PlanId = oMedicalPolicy.Id;
            policymember.RelationshipToSubscriber = 'self';  
            policymember.Name = 'Test User 1#';
            //policymember.EffectiveTo = date.parse('2015-12-31 00:00:00'); 
            //policymember.EffectiveFrom = date.parse('2015-01-01 00:00:00');       
            policymember.Member_Plan_External_Id__c = '00|ODS|123456789|89|89|02-01-2001';
            insert policymember;
            //Pol Member for getActivePolicy         
            MemberPlan policymember2 = New MemberPlan();
            policymember2.RelationshipToSubscriber = 'Self';
            policymember2.EffectiveFrom=Date.parse(Date.today().addDays(-1).format());
            policymember2.EffectiveTo=Date.parse(Date.today().addDays(1).format());
            //MemberPlan objMedPolMem2 = HUMTestDataHelperTest.getPolicyMember();
            policymember2.Memberid = objMemAccount.Id;
            policymember2.Policy__c = oMedicalPolicy1.Id;
            policymember2.PlanId = oMedicalPolicy.Id;
            policymember2.RelationshipToSubscriber = 'Self';    
            //objMedPolMem2.end_date__c = '12/31/2025'; 
            policymember2.EffectiveTo =  Date.parse(Date.today().addDays(20).format());
            //objMedPolMem2.Effective_Date__c='12/31/2024'; 
            policymember2.EffectiveFrom = Date.parse(Date.today().addDays(10).format());      
            policymember2.Member_Plan_External_Id__c = '01|ODS|123456789|89|89|02-01-2002';
            //objMedPolMem2.Subscriberid = objMedPolMem1.Id;
            policymember2.Source_Coverage_ID__c = 'TestFutureDated';
            policymember2.Name = 'Test User 1##';
            insert policymember2;
            
            MemberPlan objMedPolMemsp = New MemberPlan();
            objMedPolMemsp.EffectiveFrom=Date.parse(Date.today().addDays(-1).format());
            objMedPolMemsp.EffectiveTo=Date.parse(Date.today().addDays(1).format());
            //MemberPlan objMedPolMemsp = HUMTestDataHelperTest.getPolicyMember();
            
            objMedPolMemsp.Memberid = objMemAccount.Id;
            objMedPolMemsp.PlanId = oMedicalPolicy.Id;
            objMedPolMemsp.RelationshipToSubscriber = 'SPOUSE';    
            //objMedPolMemsp.EffectiveTo = date.parse('2014-12-31'); 
            //objMedPolMemsp.EffectiveFrom= date.parse('2014-01-01');       
            objMedPolMemsp.Member_Plan_External_Id__c = '07|ODS|123456789|89|89|02-01-2002';
            objMedPolMemsp.SubscriberPlanId__C = policymember2.Id;
            objMedPolMemsp.SubscriberId = objMemAccount.Id;
            objMedPolMemsp.Name = 'Test User spouse';
            insert objMedPolMemsp;
            
            MemberPlan objMedPolMemch = New MemberPlan();
            objMedPolMemch.EffectiveFrom=Date.parse(Date.today().addDays(-1).format());
            objMedPolMemch.EffectiveTo=Date.parse(Date.today().addDays(1).format());
            //MemberPlan objMedPolMemch = HUMTestDataHelperTest.getPolicyMember();
            
            objMedPolMemch.Memberid = objMemAccount1.Id;
            objMedPolMemch.PlanId = oMedicalPolicy2.Id;
            objMedPolMemch.RelationshipToSubscriber = 'CHILD';    
            //objMedPolMemch.EffectiveTo = date.parse('2014-12-31'); 
            //objMedPolMemch.EffectiveFrom= date.parse('2014-01-01');       
            objMedPolMemch.Member_Plan_External_Id__c = '07|ODS|123456790|89|89|02-01-2002';
            objMedPolMemch.SubscriberId = objMemAccount1.Id;
            objMedPolMemch.Name = 'Test User child';
            insert objMedPolMemch;
	    List<HUMConstants__c> lstConstanst = new List<HUMConstants__c>();
            lstConstanst.Add(new HUMConstants__c(Name='HOME_OFFICE_ALL',IntegerValue__c=1,StringValue__c='Home Office All'));
            lstConstanst.Add(new HUMConstants__c(Name='HOME_OFFICE_DENTAL',IntegerValue__c=100,StringValue__c='Home Office Dental'));
            lstConstanst.Add(new HUMConstants__c(Name='HOME_OFFICE_MEDICAL',IntegerValue__c=200,StringValue__c='Home Office Medical/Pharmacy'));
            insert lstConstanst;
            System.assert(lstConstanst.size() > 0 );
            
            
        }
    }
    
    /**
    * Method to create test data fpr HSANonHumana Check
    *<p>
    * @param            NULL
    * @return           void
    */
    private static void setUpTestDataForHSANonHumana()
    {
        User objUser = [Select Id,Name from User where LastName='test User1'];
        system.runAs(objUser)
        {
            // Insert Member Account
            Account objMemAccount = HUMTestDataHelperTest.getAccount();
            objMemAccount.FirstName = 'Test FName';
            objMemAccount.LastName = 'LName'; 
            objMemAccount.source_platform_code__c = 'EM' ;
            objMemAccount.Enterprise_Id__c = String.valueof(2000);
            objMemAccount.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId();
            objMemAccount.Birthdate__c = String.valueOf(System.today().month()+'/'+system.today().day()+'/'+System.today().year());
            objMemAccount.Home_Office_Account__c = true;
            insert objMemAccount;
            system.assertEquals(objMemAccount.LastName == 'LName', true);
            // Policy
            PurchaserPlan oMedicalPolicy = new PurchaserPlan();
            oMedicalPolicy.Policy_Group_Number__c='11111';
            oMedicalPolicy.Name = 'Medical Product';
            oMedicalPolicy.source_cust_cov_key__c = '12345678901234567890' ; 
            oMedicalPolicy.PayerId = objMemAccount.Id;
            oMedicalPolicy.Major_LOB__c = MEMBERIDCARD_HSA;
            oMedicalPolicy.Effectivefrom  = Date.parse(Date.today().addDays(-400).format());     
            insert oMedicalPolicy;
            system.assertEquals(oMedicalPolicy.Name == 'Medical Product', true);
            // Policy Member
            MemberPlan objMedPolMem1 = New MemberPlan();
            objMedPolMem1.RelationshipToSubscriber = 'Self';
            objMedPolMem1.EffectiveFrom=Date.parse(Date.today().addDays(-1).format());
            objMedPolMem1.EffectiveTo=Date.parse(Date.today().addDays(1).format());
            //MemberPlan objMedPolMem1 = HUMTestDataHelperTest.getPolicyMember();
            objMedPolMem1.Memberid = objMemAccount.Id;
            objMedPolMem1.PlanId = oMedicalPolicy.Id;
            objMedPolMem1.RelationshipToSubscriber = 'self';    
            //objMedPolMem1.EffectiveTo = date.parse('2015-12-31'); 
            //objMedPolMem1.EffectiveFrom = date.parse('2015-01-01');       
            objMedPolMem1.Member_Plan_External_Id__c = '00|ODS|123456789|89|89|02-01-2002';
            objMedPolMem1.Name = 'Test User 1@';
            insert objMedPolMem1;
            
            List<HUMConstants__c> lstConstanst = new List<HUMConstants__c>();
            lstConstanst.Add(new HUMConstants__c(Name='HOME_OFFICE_ALL',IntegerValue__c=1,StringValue__c='Home Office All'));
            lstConstanst.Add(new HUMConstants__c(Name='HOME_OFFICE_DENTAL',IntegerValue__c=100,StringValue__c='Home Office Dental'));
            lstConstanst.Add(new HUMConstants__c(Name='HOME_OFFICE_MEDICAL',IntegerValue__c=100,StringValue__c='Home Office Medical/Pharmacy'));
            insert lstConstanst;
            System.assert(lstConstanst.size() > 0 );            
        }
    }
    
     /**
    * Test method for pharmacy data.
    *<p>
    * @param            NULL
    * @return           void
    */
    static testMethod void testOrderIdCard()
    {
        User testUser = [Select Id,Name from User where LastName='test User1'];

        MemberPlan policyObj = [select id,RelationshipToSubscriber,name,planid from MemberPlan where RelationshipToSubscriber ='self' limit 1];
        MemberPlan policyObjSp = [select id,RelationshipToSubscriber,name,planid, subscriberid from MemberPlan where RelationshipToSubscriber ='SPOUSE' limit 1];
        
        Policy__c policyyObj = [select id, name from Policy__c where name ='Medical Product' Limit 1];
        PurchaserPlan planObj = [select id, name from PurchaserPlan where name ='Medical Product' Limit 1];
        //MemberPlan policyObj2 = [select id,RelationshipToSubscriber,name from MemberPlan where RelationshipToSubscriber ='self' and Policy__c =:policyyObj.id  limit 1];
        
        Policy__c policyyObj2 = [select id, name from Policy__c where name ='Medical Product' and Major_LOB__c ='EPO' Limit 1];
        MemberPlan policyObj2 = [select id,RelationshipToSubscriber,name from MemberPlan where RelationshipToSubscriber ='Self' and Policy__c =:policyyObj2.id and Source_Coverage_ID__c = 'TestFutureDated'  limit 1];
        MemberPlan policyObj3 = [select id,RelationshipToSubscriber,name,Memberid,MemberPlan.Segment_Indicator__c from MemberPlan where RelationshipToSubscriber ='Self' and Policy__c =:policyyObj2.id and Source_Coverage_ID__c = 'TestFutureDated'  limit 1];
        
        List<String> lstMemId = new List<String>();
        String strId;
        String polId =policyyObj.Id;
        String sPlanId = planObj.Id; 
        String polMemId = policyObj.Id;
        string memplansp = policyObjSp.Id;
        System.runAs(testUser)
        {
            Test.startTest();
                //Test.setCurrentPage(flexipage.ID_Card_management);
                ApexPages.currentPage().getParameters().put('Id',policyObj.Id);
                MemberIdCards_LWC_LC_HUM objMemId = new MemberIdCards_LWC_LC_HUM();
               // objMemId.getPolicyMemberData();
                strId = policyObj.Id;
                lstMemId.add(strId);
                string sj = '[{"tKey":"H57083521","value":["0SqZ00000004CDLKA2","0SqZ00000004CDMKA2"]},{"tKey":"108688043","value":["0SqZ00000004CDMKA2"]}]';
                Continuation continueObj =  (Continuation)MemberIdCards_LWC_LC_HUM.invokeOrderIdCardService1(sj);
                HttpResponse httpRes = new Httpresponse ();
                String resBodyString= '{"OrderMemberIdCardResponse":{"OrderMemberIdCardResults":{"OrderMemberIdCardResult":[{"CustomerGenKey":"testkey"}]}}, "MemberSearchResponse": {"HasData": "true","IsError": "false","ErrorMsg": "","MemberSearchDetail": [{" FirstName ": "Marc","CardRequestDate": "01/01/2017","RequestDate": "01 JAN 2017"," LastName ": "Tio."," BusinessSegment ": "MTV"," GroupNbr ": "923382"," GroupName": " HUMANA INC "," Status ": "Approved."," Reason ": "MTV"," CardType ": "New ","MemberId": "1211019","DependentCode": "00"," StatusDate ": "01/01/20181"," ExpirationDate ": "01/01/2018"}]}}';
                httpRes.setBody(resBodyString);
                system.assert(httpRes.getBody() == resBodyString);
                string reqType = 'Continuation-1';
                List<string>labelGetmember = new List<string>();
                labelGetmember.add(reqType);
                Test.setContinuationResponse(reqType,httpRes);
                MemberIdCards_LWC_LC_HUM.parseOrderIdCardResponse1(labelGetmember,continueObj);
                //positive scenario
                RequestIdCard_DTO_LC_HUM retObj = MemberIdCards_LWC_LC_HUM.getActivePolicyMembers1(polMemId,polMemId,polId);
                //negative scenario
                MemberPlan p = [Select id,Subscriberid,Policy__c,Member_Coverage_Status__c from MemberPlan where id=:policyObj2.id];
                retObj = MemberIdCards_LWC_LC_HUM.getActivePolicyMembers1(policyObj2.id,null,policyyObj2.id);
                Object retObj2 = MemberIdCards_LWC_LC_HUM.getMemberLevelDetails('923382','RENA JORDAN', policyyObj2.id);
                Object retObj3 = MemberIdCards_LWC_LC_HUM.getMemberLevelDetails('', 'RENA JORDAN', strId);
                Object retObj4 = MemberIdCards_LWC_LC_HUM.getMemberLevelDetails('', '', strId);
                retObj2 = MemberIdCards_LWC_LC_HUM.processMemberLevelResponse(labelGetmember,continueObj);
                List<MemberPlan> lstPM = new List<MemberPlan>();
                lstPM.add(policyObj3);
                Boolean bidcardtest = MemberIdCards_LWC_LD_HUM.getIdCardRequestInL7Days(lstPM,'');
                Boolean bRequestIdCardTest = MemberIdCards_LWC_LS_HUM.checkIDCardRequestInL7Days(lstPM,'');
                MemberIdCards_LWC_LC_HUM.getActivePolicyMembers1(null,null,null);
                Boolean bCheckSegmentIndicator = MemberIdCards_LWC_LS_HUM.checkMedicareMedicaid(policyObj3);
                MemberIdCards_LWC_LS_HUM.processMemberLevelResponse(null,Null);
                RequestIdCard_DTO_LC_HUM retObjPlan = MemberIdCards_LWC_LC_HUM.getActivePolicyMembers1(memplansp,memplansp,sPlanId);
            Test.stopTest();
        
        }
    }
    /**
    * Test method for pharmacy data.
    *<p>
    * @param            NULL
    * @return           void
    */
    static testMethod void testOrderIdCard1()
    {
        User testUser = [Select Id,Name from User where LastName='test User1'];

        MemberPlan policyObj = [select id,RelationshipToSubscriber,name from MemberPlan where RelationshipToSubscriber ='self' limit 1];
        Policy__c policyyObj = [select id, name from Policy__c where name ='Medical Product' Limit 1];
        //MemberPlan policyObj2 = [select id,RelationshipToSubscriber,name from MemberPlan where RelationshipToSubscriber ='self' and Policy__c =:policyyObj.id  limit 1];
        MemberPlan policyObjSp = [select id,RelationshipToSubscriber,name,planid, subscriberid from MemberPlan where RelationshipToSubscriber ='Child' limit 1];
        PurchaserPlan planObj = [select id, name from PurchaserPlan where name ='Medical Product' Limit 1];
        Policy__c policyyObj2 = [select id, name from Policy__c where name ='Medical Product' and Major_LOB__c ='EPO' Limit 1];
        MemberPlan policyObj2 = [select id,RelationshipToSubscriber,name from MemberPlan where RelationshipToSubscriber ='Self' and Policy__c =:policyyObj2.id and Source_Coverage_ID__c = 'TestFutureDated'  limit 1];
        MemberPlan policyObj3 = [select id,RelationshipToSubscriber,name,Memberid,MemberPlan.Segment_Indicator__c from MemberPlan where RelationshipToSubscriber ='Self' and Policy__c =:policyyObj2.id and Source_Coverage_ID__c = 'TestFutureDated'  limit 1];
        
        List<String> lstMemId = new List<String>();
        String strId;
        String polId =policyyObj.Id;
        String polMemId = policyObj.Id;
        String sPlanId = policyObjSp.PlanId; 
        string memplansp = policyObjSp.Id;
        System.runAs(testUser)
        {
            Test.startTest();
                //Test.setCurrentPage(flexipage.ID_Card_management);
                ApexPages.currentPage().getParameters().put('Id',policyObj.Id);
                MemberIdCards_LWC_LC_HUM objMemId = new MemberIdCards_LWC_LC_HUM();
               // objMemId.getPolicyMemberData();
                strId = policyObj.Id;
                lstMemId.add(strId);
                string sj = '[{"tKey":"H57083521","value":["'+strId+'"]}]';
                Continuation continueObj =  (Continuation)MemberIdCards_LWC_LC_HUM.invokeOrderIdCardService1(sj);
                HttpResponse httpRes = new Httpresponse ();
                String resBodyString= '{"OrderMemberIdCardResponse":{"OrderMemberIdCardResults":{"OrderMemberIdCardResult":[{"CustomerGenKey":"testkey"}]}}, "MemberSearchResponse": {"HasData": "true","IsError": "false","ErrorMsg": "","MemberSearchDetail": [{" FirstName ": "Marc","CardRequestDate": "01/01/2017","RequestDate": "01 JAN 2017"," LastName ": "Tio."," BusinessSegment ": "MTV"," GroupNbr ": "923382"," GroupName": " HUMANA INC "," Status ": "Approved."," Reason ": "MTV"," CardType ": "New ","MemberId": "1211019","DependentCode": "00"," StatusDate ": "01/01/20181"," ExpirationDate ": "01/01/2018"}]}}';
                httpRes.setBody(resBodyString);
                system.assert(httpRes.getBody() == resBodyString);
                string reqType = 'Continuation-1';
                List<string>labelGetmember = new List<string>();
                labelGetmember.add(reqType);
                Test.setContinuationResponse(reqType,httpRes);
                MemberIdCards_LWC_LC_HUM.parseOrderIdCardResponse1(labelGetmember,continueObj);
                //positive scenario
                RequestIdCard_DTO_LC_HUM retObj = MemberIdCards_LWC_LC_HUM.getActivePolicyMembers1(polMemId,polMemId,polId);
                //negative scenario
                MemberPlan p = [Select id,Subscriberid,Policy__c,Member_Coverage_Status__c from MemberPlan where id=:policyObj2.id];
                retObj = MemberIdCards_LWC_LC_HUM.getActivePolicyMembers1(policyObj2.id,null,policyyObj2.id);
                Object retObj2 = MemberIdCards_LWC_LC_HUM.getMemberLevelDetails('923382','RENA JORDAN', policyyObj2.id);
                Object retObj3 = MemberIdCards_LWC_LC_HUM.getMemberLevelDetails('', 'RENA JORDAN', strId);
                Object retObj4 = MemberIdCards_LWC_LC_HUM.getMemberLevelDetails('', '', strId);
                retObj2 = MemberIdCards_LWC_LC_HUM.processMemberLevelResponse(labelGetmember,continueObj);
                List<MemberPlan> lstPM = new List<MemberPlan>();
                lstPM.add(policyObj3);
                Boolean bidcardtest = MemberIdCards_LWC_LD_HUM.getIdCardRequestInL7Days(lstPM,'');
                Boolean bRequestIdCardTest = MemberIdCards_LWC_LS_HUM.checkIDCardRequestInL7Days(lstPM,'');
                MemberIdCards_LWC_LC_HUM.getActivePolicyMembers1(polId,polId,polId);
                Boolean bCheckSegmentIndicator = MemberIdCards_LWC_LS_HUM.checkMedicareMedicaid(policyObj3);
                MemberIdCards_LWC_LS_HUM.processMemberLevelResponse(null,Null);
                RequestIdCard_DTO_LC_HUM retObjNeg = MemberIdCards_LWC_LC_HUM.getActivePolicyMembers1('923382','RENA JORDAN',polId);
                RequestIdCard_DTO_LC_HUM retObjNeg1 = MemberIdCards_LWC_LC_HUM.getActivePolicyMembers1('','','');
            RequestIdCard_DTO_LC_HUM retObjPlan = MemberIdCards_LWC_LC_HUM.getActivePolicyMembers1(memplansp,memplansp,sPlanId);
            Test.stopTest();
        
        }
    }
    
    /**
    * Test method for pharmacy data.
    *<p>
    * @param            NULL
    * @return           void
    */
    static testMethod void testHSANonHumana()
    {
        setUpTestDataForHSANonHumana();
        User testUser = [Select Id,Name from User where LastName='test User1'];
        List<String> lstMemId = new List<String>();
        String strId;
        MemberPlan policyObj = [select id,RelationshipToSubscriber,name from MemberPlan where Plan.Major_LOB__c = :GLOBAL_CONSTANT_HUM.MEMBERIDCARD_HSA limit 1];
        System.runAs(testUser)
        {
            Test.startTest();
            //Test.setCurrentPage(flexipage.ID_Card_management);
            ApexPages.currentPage().getParameters().put('Id',policyObj.Id);
            MemberIdCards_LWC_LC_HUM objMemId = new MemberIdCards_LWC_LC_HUM();
            //system.assertEquals(objMemId.bDispSPAATransferError, true);
            MemberIdCards_LWC_LC_HUM.parseOrderIdCardResponse1(null,null);
            MemberIdCards_LWC_LS_HUM.parseOrderIdCardResponse1(null,null);
            Test.stopTest();
        
        }
    }
    
    /**
    * Test method for negative Member Id Card.
    *<p>
    * @param            NULL
    * @return           void
    */
    static testMethod void testNegativeMemberIdCard1()
    {
        User testUser = [Select Id,Name from User where LastName='test User1'];
        List<String> lstMemId = new List<String>();
        String strId;

        MemberPlan policyObj = [select id,RelationshipToSubscriber,name from MemberPlan where RelationshipToSubscriber ='self' limit 1];
        Policy__c policyyObj = [select id, name from Policy__c where name ='Medical Product' Limit 1];
        //MemberPlan policyObj2 = [select id,RelationshipToSubscriber,name from MemberPlan where RelationshipToSubscriber ='self' and Policy__c =:policyyObj.id  limit 1];
        
        Policy__c policyyObj2 = [select id, name from Policy__c where name ='Medical Product' and Major_LOB__c ='EPO' Limit 1];
        MemberPlan policyObj2 = [select id,RelationshipToSubscriber,name from MemberPlan where RelationshipToSubscriber ='Self' and Policy__c =:policyyObj2.id and Source_Coverage_ID__c = 'TestFutureDated'  limit 1];
        

        List<HUM_Webservice_Callout__c > lstWebCallout = [Select id from HUM_Webservice_Callout__c];
        
        System.runAs(testUser)
        {
            Test.startTest();
                //Test.setCurrentPage(felxipage.ID_Card_management);
                ApexPages.currentPage().getParameters().put('Id',policyObj.Id);
                
                MemberIdCards_LWC_LC_HUM memIdCard = new MemberIdCards_LWC_LC_HUM();
                MemberIdCards_LWC_LS_HUM oServiceMember = new MemberIdCards_LWC_LS_HUM();
                delete lstWebCallout;
                strId = policyObj.Id;
                lstMemId.add(strId);
                string sj = '[{"tKey":"H57083521","value":["0SqZ00000004CDLKA2"]}]';
                Continuation continueObj =  (Continuation)MemberIdCards_LWC_LC_HUM.invokeOrderIdCardService1(sj);
                HttpResponse httpRes = new Httpresponse ();
                String resBodyString= '{"OrderMemberIdCardResponse":{"OrderMemberIdCardResults":{"OrderMemberIdCardResult":[{"CustomerGenKey":"testkey"}]}}, "MemberSearchResponse": {"HasData": "true","IsError": "false","ErrorMsg": "","MemberSearchDetail": [{" FirstName ": "Marc","CardRequestDate": "01/01/2017","RequestDate": "01 JAN 2017"," LastName ": "Tio."," BusinessSegment ": "MTV"," GroupNbr ": "923382"," GroupName": " HUMANA INC "," Status ": "Approved."," Reason ": "MTV"," CardType ": "New ","MemberId": "1211019","DependentCode": "00"," StatusDate ": "01/01/20181"," ExpirationDate ": "01/01/2018"}]}}';
                httpRes.setBody(resBodyString);
                System.assertEquals(httpRes.getBody(),resBodyString);
                string reqType = '';
                List<string>labelGetmember = new List<string>();
                labelGetmember.add(reqType);
                Test.setContinuationResponse(reqType,httpRes);
                MemberIdCards_LWC_LC_HUM.parseOrderIdCardResponse1(labelGetmember,continueObj);
                MemberIdCards_LWC_LC_HUM memberIdCards = new MemberIdCards_LWC_LC_HUM();
                //MemberIDCards.bMedicareMedicaid = true;
                //memberIdCards.checkForMemberIDCardRequest();
                memberIdCards.getRecords();
                memberIdCards.getThis();
                memberIdCards.getRecord();
                string sj1 = '[{"tKey":"H57083521","value":[""]}]';
                Continuation continueObj1 = (Continuation)MemberIdCards_LWC_LC_HUM.invokeOrderIdCardService1(sj1);
                MemberIdCards_LWC_LC_HUM.parseOrderIdCardResponse1(null,null);
            List<String> lstS = new List<String>();
            lstS.add('0SqZ00000004CDLKA2');
            MemberIdCards_LWC_LS_HUM.parseOrderIdCardResponse1(lstS, null);
            MemberIdCards_LWC_LS_HUM.processMemberLevelService(',',',',',');
                

            Test.stopTest();
        
        }
    }
    
     /**
    * Test method for negative Member Id Card.
    *<p>
    * @param            NULL
    * @return           void
    */
    static testMethod void testNegativeMemberIdCard()
    {
        User testUser = [Select Id,Name from User where LastName='test User1'];
        List<String> lstMemId = new List<String>();
        String strId;
        
        MemberPlan policyObj = [select id,RelationshipToSubscriber,name,memberid from MemberPlan where RelationshipToSubscriber ='self' limit 1];
        Policy__c policyyObj = [select id, name from Policy__c where name ='Medical Product' Limit 1];
        //MemberPlan policyObj2 = [select id,RelationshipToSubscriber,name from MemberPlan where RelationshipToSubscriber ='self' and Policy__c =:policyyObj.id  limit 1];
        
        Policy__c policyyObj2 = [select id, name from Policy__c where name ='Medical Product' and Major_LOB__c ='EPO' Limit 1];
        MemberPlan policyObj2 = [select id,RelationshipToSubscriber,name from MemberPlan where RelationshipToSubscriber ='Self' and Policy__c =:policyyObj2.id and Source_Coverage_ID__c = 'TestFutureDated'  limit 1];
        

        List<HUM_Webservice_Callout__c > lstWebCallout = [Select id from HUM_Webservice_Callout__c];
        Test.setMock(HttpCalloutMock.class, new MemberIdCardsMock());
        
        System.runAs(testUser)
        {
            Test.startTest();
                //Test.setCurrentPage(felxipage.ID_Card_management);
                ApexPages.currentPage().getParameters().put('Id',policyObj.Id);
                
                MemberIdCards_LWC_LC_HUM memIdCard = new MemberIdCards_LWC_LC_HUM();
                MemberIdCards_LWC_LC_HUM.getMemIdWithoutDepCode(policyObj.Id);
                MemberIdCards_LWC_LD_HUM.getMemIdWithoutDepCode(policyObj.Id);
                MemberIdCards_LWC_LD_HUM.getPoliciesMed(policyObj.memberid);
                MemberIdCards_LWC_LD_HUM.getPoliciesDen(policyObj.memberid);
                MemberIdCards_LWC_LS_HUM oServiceMember = new MemberIdCards_LWC_LS_HUM();
                delete lstWebCallout;
                strId = policyObj.Id;
                lstMemId.add(strId);
                string sj = '[{"tKey":"H57083521","value":["0SqZ00000004CDLKA2"]}]';
                Continuation continueObj =  (Continuation)MemberIdCards_LWC_LC_HUM.invokeOrderIdCardService1(sj);
                HttpResponse httpRes = new Httpresponse ();
                String resBodyString= '{"MemberSearchResponse": {"HasData": "true","IsError": "false","ErrorMsg": "","MemberSearchDetail": [{" FirstName ": "Marc","CardRequestDate": "01/01/2017","RequestDate": "01 JAN 2017"," LastName ": "Tio."," BusinessSegment ": "MTV"," GroupNbr ": "923382"," GroupName": " HUMANA INC "," Status ": "Approved."," Reason ": "MTV"," CardType ": "New ","MemberId": "1211019","DependentCode": "00"," StatusDate ": "01/01/20181"," ExpirationDate ": "01/01/2018"}]}}';
                httpRes.setBody(resBodyString);
                System.assertEquals(httpRes.getBody(),resBodyString);
                string reqType = '';
                List<string>labelGetmember = new List<string>();
                labelGetmember.add(reqType);
                Test.setContinuationResponse(reqType,httpRes);
                MemberIdCards_LWC_LC_HUM.parseOrderIdCardResponse1(labelGetmember,continueObj);
                MemberIdCards_LWC_LC_HUM memberIdCards = new MemberIdCards_LWC_LC_HUM();
                //MemberIDCards.bMedicareMedicaid = true;
                //memberIdCards.checkForMemberIDCardRequest();
                memberIdCards.getRecords();
                memberIdCards.getThis();
                memberIdCards.getRecord();
                
                

            Test.stopTest();
        
        }
    }
    /**
    * Test method for covering DTO classes
    *<p>
    * @param            NULL
    * @return           void
    */
    static testmethod void testDTOClass() {
        User testUser = [Select Id, Name from User where LastName ='test User1'];
        System.runAs(testUser)
        {
            HIDCMSOrderIdCardResponse_DTO_HUM.IdCardDetails idcardDet = new HIDCMSOrderIdCardResponse_DTO_HUM.IdCardDetails();
            idcardDet.CardIndex = '';
            idcardDet.CardOrderDate = '';
            idcardDet.CardsFound = '';
            idcardDet.ErrorCode = '';
            idcardDet.ErrorDescription = '';
            idcardDet.IsError = '';
            idcardDet.OrderInProgress = '';
            HIDCMSOrderIdCardResponse_DTO_HUM.OrderMemberIdCardResultDetail Order = new HIDCMSOrderIdCardResponse_DTO_HUM.OrderMemberIdCardResultDetail();
            Order.CustCovSeqNbr = '';
            Order.CustomerGenKey = '';
            Order.DateOfBirth = '';
            Order.DependentCode = '';
            Order.GroupNbr = '';
            Order.IdCardDetailsDto = idcardDet;
            List<HIDCMSOrderIdCardResponse_DTO_HUM.OrderMemberIdCardResultDetail> lOrder = new List<HIDCMSOrderIdCardResponse_DTO_HUM.OrderMemberIdCardResultDetail>();
            lOrder.add(Order);
            HIDCMSOrderIdCardResponse_DTO_HUM.OrderMemberIdCardResultsDetail lOrders = new HIDCMSOrderIdCardResponse_DTO_HUM.OrderMemberIdCardResultsDetail();
            lOrders.OrderMemberIdCardResult = lOrder;
            HIDCMSOrderIdCardResponse_DTO_HUM.OrderMemberIdCardResponseDetail respDet = new HIDCMSOrderIdCardResponse_DTO_HUM.OrderMemberIdCardResponseDetail();
            respDet.OrderMemberIdCardResults = lOrders;

            HIDCMSMemberSearchResponse_DTO_HUM.MemberSearchDTO  memDTO = new HIDCMSMemberSearchResponse_DTO_HUM.MemberSearchDTO();
            memDTO.FirstName = '';
            memDTO.CardRequestDate = '';
            memDTO.LastName = '';
            memDTO.RequestDate = '';
            memDTO.BusinessSegment = '';
            memDTO.GroupNbr = '';
            memDTO.GroupName = '';
            memDTO.Status = '';
            memDTO.Reason = '';
            memDTO.ReasonToolTip = '';
            memDTO.CardType = '';
            memDTO.MemberId = '';
            memDTO.DependentCode = '';
            memDTO.StatusDate = '';
            memDTO.ExpirationDate = '';
            memDTO.CardTypeDesc = '';

            List<HIDCMSMemberSearchResponse_DTO_HUM.MemberSearchDTO> lmemDTO = new List<HIDCMSMemberSearchResponse_DTO_HUM.MemberSearchDTO>();
            lmemDTO.add(memDTO);

            HIDCMSMemberSearchResponse_DTO_HUM.MemberSearchResponse  memSearchResp = new HIDCMSMemberSearchResponse_DTO_HUM.MemberSearchResponse();
            memSearchResp.MemberSearchDetail = lmemDTO;
            memSearchResp.IsError = '';
            memSearchResp.ErrorMsg = '';
            System.assertEquals(memSearchResp.MemberSearchDetail,lmemDTO);
        }
    }
    
    
    static testmethod void testDTOClassNegative() {
        User testUser = [Select Id, Name from User where LastName ='test User1'];
        System.runAs(testUser)
        {
             
            HIDCMSOrderIdCardResponse_DTO_HUM.IdCardDetails idcardDet = new HIDCMSOrderIdCardResponse_DTO_HUM.IdCardDetails();
            idcardDet.CardIndex = '';
            idcardDet.CardOrderDate = '';
            idcardDet.CardsFound = '';
            idcardDet.ErrorCode = '';
            idcardDet.ErrorDescription = '';
            idcardDet.IsError = '';
            idcardDet.OrderInProgress = '';
            HIDCMSOrderIdCardResponse_DTO_HUM.OrderMemberIdCardResultDetail Order = new HIDCMSOrderIdCardResponse_DTO_HUM.OrderMemberIdCardResultDetail();
            Order.CustCovSeqNbr = '';
            Order.CustomerGenKey = '';
            Order.DateOfBirth = '';
            Order.DependentCode = '';
            Order.GroupNbr = '';
            Order.IdCardDetailsDto = idcardDet;
            List<HIDCMSOrderIdCardResponse_DTO_HUM.OrderMemberIdCardResultDetail> lOrder = new List<HIDCMSOrderIdCardResponse_DTO_HUM.OrderMemberIdCardResultDetail>();
            lOrder.add(Order);
            HIDCMSOrderIdCardResponse_DTO_HUM.OrderMemberIdCardResultsDetail lOrders = new HIDCMSOrderIdCardResponse_DTO_HUM.OrderMemberIdCardResultsDetail();
            lOrders.OrderMemberIdCardResult = lOrder;
            HIDCMSOrderIdCardResponse_DTO_HUM.OrderMemberIdCardResponseDetail respDet = new HIDCMSOrderIdCardResponse_DTO_HUM.OrderMemberIdCardResponseDetail();
            respDet.OrderMemberIdCardResults = lOrders;

            HIDCMSMemberSearchResponse_DTO_HUM.MemberSearchDTO  memDTO = new HIDCMSMemberSearchResponse_DTO_HUM.MemberSearchDTO();
            memDTO.FirstName = '';
            memDTO.CardRequestDate = '';
            memDTO.LastName = '';
            memDTO.RequestDate = '';
            memDTO.BusinessSegment = '';
            memDTO.GroupNbr = '';
            memDTO.GroupName = '';
            memDTO.Status = '';
            memDTO.Reason = '';
            memDTO.ReasonToolTip = '';
            memDTO.CardType = '';
            memDTO.MemberId = '';
            memDTO.DependentCode = '';
            memDTO.StatusDate = '';
            memDTO.ExpirationDate = '';
            memDTO.CardTypeDesc = '';

            List<HIDCMSMemberSearchResponse_DTO_HUM.MemberSearchDTO> lmemDTO = new List<HIDCMSMemberSearchResponse_DTO_HUM.MemberSearchDTO>();
            lmemDTO.add(memDTO);

            HIDCMSMemberSearchResponse_DTO_HUM.MemberSearchResponse  memSearchResp = new HIDCMSMemberSearchResponse_DTO_HUM.MemberSearchResponse();
            memSearchResp.MemberSearchDetail = lmemDTO;
            memSearchResp.IsError = '';
            memSearchResp.ErrorMsg = '';
            
            Object obj = MemberIdCards_LWC_LS_HUM.sendRequestStateIdCardOrder('TestId','500' , 'test reason');
            Object obj1 = MemberIdCards_LWC_LS_HUM.sendRequestInquiryService('TestId','500' , 'test reason');
            Object obj2 = MemberIdCards_LWC_LS_HUM.processMemberLevelService('testNo', '' , '');
            Case c = MemberIdCards_LWC_LD_HUM.queryCaseDetails(null);
            
        }
    }
    
    static testMethod void invokeStateOrderIdCardServiceTest(){
        User testUser = [Select Id, Name from User where LastName ='test User1'];
        Test.setMock(HttpCalloutMock.class, new MemberIdCardsMock());
        System.runAs(testUser)
        {
            Test.startTest();
            MemberIdCards_LWC_LC_HUM.invokeStateOrderIdCardService('3243454', 'Test Code', 'Test Reason');
            Test.stopTest();
        }
    }
    
    static testMethod void invokeInquiryServiceTest(){
        User testUser = [Select Id, Name from User where LastName ='test User1'];
        Test.setMock(HttpCalloutMock.class, new MemberIdCardsMock());
        System.runAs(testUser)
        {
            Test.startTest();
            MemberIdCards_LWC_LC_HUM.invokeInquiryService('3243454', 'Test Code', 'Test Reason');
            Test.stopTest();
        }
        
    }
    
    static testMethod void caseSaveStateIdTest(){
        User testUser = [Select Id, Name from User where LastName ='test User1'];
        Id accountId = [SELECT Id FROM Account LIMIT 1].Id;
        Id mPlanId = [SELECT Id FROM MemberPlan LIMIT 1].Id;
        Interaction__c interAction = [SELECT Id,Interacting_With_type__c,Interacting_With__c,Caller_Name__c,Interaction_Origin__c FROM Interaction__c LIMIT 1];
        Group oGrp = [SELECT Id FROM Group WHERE Name='Test' LIMIT 1];
        
        QueuesObject oQueueObject = New QueuesObject(QueueID = oGrp.id, SobjectType ='Work_Queue_Setup__c');
        insert oQueueObject ;
        
        
        System.runAs(testUser)
        {
            Work_Queue_Setup__c oWQS = new Work_Queue_Setup__c();
            oWQS.Name = 'Cincit Calls';
            oWQS.Service_Center__c='Cincit';
            oWQS.Department__c='Calls';
            oWQS.IsActive__c = true;
            oWQS.OwnerId= oGrp.id;
            oWQS.Security_Group__c = 'Home Office All';
            oWQS.General__c = true;
            oWQS.HO_Medical__c = true;
            oWQS.HO_Dental__c = true;
            insert oWQS;
            System.assert(oWQS.Security_Group__c == 'Home Office All');
            
            MemberIdCards_LWC_LC_HUM.assignCaseValues(new List<Id>{mPlanId}, 'StateidCard', interAction);
            MemberIdCards_LWC_LC_HUM.assignInitialCaseData('Account',accountId);
        }
    }
    
    static testMethod void caseSaveMemberIdTest(){
        User testUser = [Select Id, Name from User where LastName ='test User1'];
        Id accId = [SELECT Id FROM Account LIMIT 1].Id;
        Id mPlanId = [SELECT Id FROM MemberPlan LIMIT 1].Id;
        Interaction__c interAction = [SELECT Id,Interacting_With_type__c,Interacting_With__c,Caller_Name__c,Interaction_Origin__c FROM Interaction__C LIMIT 1];
        
        Group oGrp = [SELECT Id FROM Group WHERE Name='Test' LIMIT 1];
        
        QueuesObject oQueueObject = New QueuesObject(QueueID = oGrp.id, SobjectType ='Work_Queue_Setup__c');
        insert oQueueObject ;
        
        
        System.runAs(testUser)
        {
            interAction.Interacting_With_type__c = 'Member';
            interAction.Interacting_With__c = accId;
            interAction.Caller_Name__c = '';
            update interAction;
           Test.startTest();
            Work_Queue_Setup__c oWQS = new Work_Queue_Setup__c();
            oWQS.Name = 'Cincit Calls';
            oWQS.Service_Center__c='Cincit';
            oWQS.Department__c='Calls';
            oWQS.IsActive__c = true;
            oWQS.OwnerId= oGrp.id;
            oWQS.Security_Group__c = 'Home Office All';
            oWQS.General__c = true;
            oWQS.HO_Medical__c = true;
            oWQS.HO_Dental__c = true;
            insert oWQS;
            System.assert(oWQS.Security_Group__c == 'Home Office All');
            
            MemberIdCards_LWC_LC_HUM.assignCaseValues(new List<Id>{mPlanId}, 'MemberidCard', interAction);
            Test.stopTest();
        }
    }
    
    static testMethod void caseSaveInvoiceTest(){
        User testUser = [Select Id, Name from User where LastName ='test User1'];
        Id mPlanId = [SELECT Id FROM MemberPlan LIMIT 1].Id;
        Interaction__c interAction = [SELECT Id,Interacting_With_type__c,Interacting_With__c,Caller_Name__c,Interaction_Origin__c FROM Interaction__C LIMIT 1];
        Group oGrp = [SELECT Id FROM Group WHERE Name='Test' LIMIT 1];
        
        QueuesObject oQueueObject = New QueuesObject(QueueID = oGrp.id, SobjectType ='Work_Queue_Setup__c');
        insert oQueueObject ;
        
        Id classificationTypeId = [SELECT Id FROM Classification_type__c WHERE Name='HP Finance Ops' LIMIT 1].Id;
        
        System.runAs(testUser)
        {
            Work_Queue_Setup__c oWQS = new Work_Queue_Setup__c();
            oWQS.Name = 'Cincit Calls';
            oWQS.Service_Center__c='Cincit';
            oWQS.Department__c='Calls';
            oWQS.IsActive__c = true;
            oWQS.OwnerId= oGrp.id;
            oWQS.Classification_Type__c = classificationTypeId;
            oWQS.Security_Group__c = 'Home Office All';
            oWQS.General__c = true;
            oWQS.HO_Medical__c = true;
            oWQS.HO_Dental__c = true;
            insert oWQS;
            System.assert(oWQS.Security_Group__c == 'Home Office All');
            
            MemberIdCards_LWC_LC_HUM.assignCaseValues(new List<Id>{mPlanId}, 'Invoice', interAction);
        }
    }
    
    static testMethod void getInitialisationTest(){
        MemberIdCards_LWC_LC_HUM hum = new MemberIdCards_LWC_LC_HUM();
        hum.bDispReqIdCard = true;
        hum.bDispSPAATransferError = true;
        hum.sMemIdWithoutDepCode = 'test';
        hum.bMedicareMedicaid = true;
        hum.bIdCardRequestMayBeL7Days = true;
        Boolean bIdCardSortSwitch = hum.bIdCardSortSwitch;
        
        MemberIdCards_LWC_LC_HUM.objPolMem = new MemberPlan();
        MemberIdCards_LWC_LC_HUM.sStandardUrlForTab = 'test';
        MemberIdCards_LWC_LC_HUM.sSavedCaseId = '09uu9976809898';
        MemberIdCards_LWC_LC_HUM.sInteractionId = '977979079796';
        
        new CICMSUpdateResponse_LDTO_HUM().demographicUpdateResponse = new CICMSUpdateResponse_LDTO_HUM.demographicUpdateResponseDetail();
        CICMSUpdateResponse_LDTO_HUM.ResponseStatusDetail rsDet = new CICMSUpdateResponse_LDTO_HUM.ResponseStatusDetail();
        new CICMSInquiryResponse_LDTO_HUM().demographicInquiryResponse = new CICMSInquiryResponse_LDTO_HUM.demographicInquiryResponseDetail();
        CICMSInquiryResponse_LDTO_HUM.ResponseStatusDetail rsDetInq = new CICMSInquiryResponse_LDTO_HUM.ResponseStatusDetail();
        
        HIDCMSOrderIdCardResponse_LDTO_HUM ordIdCardRes = new HIDCMSOrderIdCardResponse_LDTO_HUM();
        ordIdCardRes.OrderMemberIdCardResponse = new HIDCMSOrderIdCardResponse_LDTO_HUM.OrderMemberIdCardResponseDetail();
        ordIdCardRes.OrderMemberIdCardResponse.OrderMemberIdCardResults = new HIDCMSOrderIdCardResponse_LDTO_HUM.OrderMemberIdCardResultsDetail();
    	ordIdCardRes.OrderMemberIdCardResponse.OrderMemberIdCardResults.OrderMemberIdCardResult = new List<HIDCMSOrderIdCardResponse_LDTO_HUM.OrderMemberIdCardResultDetail>();
        ordIdCardRes.OrderMemberIdCardResponse.OrderMemberIdCardResults.OrderMemberIdCardResult.add(new HIDCMSOrderIdCardResponse_LDTO_HUM.OrderMemberIdCardResultDetail());
        HIDCMSOrderIdCardResponse_LDTO_HUM.IdCardDetails idcardDet = new HIDCMSOrderIdCardResponse_LDTO_HUM.IdCardDetails();
        
        MemberIdCards_LWC_LD_HUM.bHomeOficeMessage = true;
        Boolean bIdCardPCPTabSwitch = MemberIdCards_LWC_LD_HUM.bIdCardPCPTabSwitch;
    }
    
    
    
    static testMethod void getInteractionDetailsTest(){
        Id accId = [SELECT Id FROM Account WHERE FirstName='Test FName' LIMIT 1].Id;
        User testUser = [Select Id, Name from User where LastName ='test User1'];
        Interaction__c objInteraction = HUMTestDataHelperTest.getInteraction();
        insert objInteraction;
        System.assertNotEquals(objInteraction, null, 'Interaction not created');
        
        Interaction_Member__c InteractionMember = new Interaction_Member__c();
        InteractionMember.Interacting_About__c = accId;
        InteractionMember.Interaction__c = objInteraction.id;
        InteractionMember.Interacting_About_Type__c ='Member';
        insert InteractionMember;
        
        MemberIdCards_LWC_LC_HUM.getInteractionDetails(accId);
        
        System.runAs(testUser)
        {
            MemberIdCards_LWC_LC_HUM.attachCaseInteractionVisibilty();  
        }
    }
    
    static testMethod void getInteractionDetailsNegativeTest(){
        Id accId = [SELECT Id FROM Account WHERE FirstName='Test FName' LIMIT 1].Id;
        MemberIdCards_LWC_LC_HUM.getInteractionDetails(accId); // empty interaction
        
        Interaction__c objInteraction = HUMTestDataHelperTest.getInteraction();
        insert objInteraction;
        System.assertNotEquals(objInteraction, null, 'Interaction not created');
        
        MemberIdCards_LWC_LC_HUM.getInteractionDetails(accId); // empty member  
        
    }
    
    static testMethod void  getDentalCarePlusMTVGrpNumResLstTest(){
        User testUser = [Select Id, Name from User where LastName ='test User1'];
        System.runAs(testUser)
        {
            Test.startTest();
            MemberIdCards_LWC_LD_HUM.DentalCarePlusMTVGrpNumResLst();
            Test.stopTest();
        }
    }
    
 
    public class MemberIdCardsMock implements HttpCalloutMock{
         public HTTPResponse respond(HTTPRequest req) 
         {        
             // Create a mock response
             HttpResponse res = new HttpResponse();
             res.setHeader('Content-Type', 'application/json');
             String strjson=''; 
             strjson = '{"OrderMemberIdCardResponse":{"OrderMemberIdCardResults":{"OrderMemberIdCardResult":[{"CustomerGenKey":"testkey"}]}}, "MemberSearchResponse": {"HasData": "true","IsError": "false","ErrorMsg": "","MemberSearchDetail": [{" FirstName ": "Marc","CardRequestDate": "01/01/2017","RequestDate": "01 JAN 2017"," LastName ": "Tio."," BusinessSegment ": "MTV"," GroupNbr ": "923382"," GroupName": " HUMANA INC "," Status ": "Approved."," Reason ": "MTV"," CardType ": "New ","MemberId": "1211019","DependentCode": "00"," StatusDate ": "01/01/20181"," ExpirationDate ": "01/01/2018"}]}}';
             res.setBody(strjson);
             res.setStatusCode(200);
             return res;        
         }
    }
    
}
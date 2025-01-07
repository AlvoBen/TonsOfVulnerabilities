/************************************************************************************************************************************************************************************       
Apex Class Name  : ExternalLinkLauncher_LT_Hum
Created Date     : 05/27/2022
Function         : 
**Modification Log
* Developer Name                   Date                       Description
************************************************************************************************************************************************************************************
* Kalyani Pachpol                  05/27/2022                 User Story 3150124: Benefits External link
* Kalyani Pachpol                  08/02/2022                 US - 3613352 
* Kalyani Pachpol                  08/10/2022                 US - 3613352 
* Aishwarya Pawar                  9/27/2022         		  US- 3552709
* Anuradha Gajbhe                  10/18/2023                 US-5066619 - Benefit and Accumulator page- ability to pass data elements to Debut from CRM links  
* Raj Paliwal                      10/18/2023                 US-5066625 - Auth/referral page- ability to pass data elements to Debut from CRM links 
* Dimple Sharma                    10/18/2023                 US-4934019 - Plan Member page- ability to pass data elements to Debut from CRM links
************************************************************************************************************************************************************************************/
@isTest
public class ExternalLinkLauncher_LT_HUM {
/* Test Class for External link launcher */
   @testSetup static void setUpTestData()
     {
        User objUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');   
        insert objUser;
		system.runAs(objUser)
		{
		    system.assertEquals(objUser.lastname == 'test User1',true);
		 
		    HUMCertificateURL__c MentorCertificateURL = new HUMCertificateURL__c(Name = 'MentorRedirectURL',CertificateListURL__c = 'http://test-mentorsearch.humana.com/web/mentor.aspx/Search');
            insert MentorCertificateURL;
            HUMCertificateURL__c MentorCertificateURLRP = new HUMCertificateURL__c(Name = 'MentorRedirectURLRapidForce',CertificateListURL__c = 'http://test-mentorsearch.humana.com/web/mentor.aspx/Search');
            insert MentorCertificateURLRP;
            system.assert(HUMCertificateURL__c.getValues('MentorRedirectURL') != NULL);
            HUMCertificateURL__c HSSRedirectURL = new HUMCertificateURL__c(Name = 'HSSRedirectURL',CertificateListURL__c = 'https://hss.humana.com/ITSecurity/HSSOneClick/LaunchHSS.aspx?HIDDENTARGET=');
            insert HSSRedirectURL;
            system.assert(HUMCertificateURL__c.getValues('HSSRedirectURL') != NULL);
            
            Endpoint_Url_HUM__c EndURL = new Endpoint_Url_HUM__c (Name='Benefits_Verification_URL',Remote_Url__c='Humana_Remote');
            insert EndURL;
            
            Endpoint_Url_HUM__c EndURL1 = new Endpoint_Url_HUM__c(Name='SSO_LOGINURL',Remote_Url__c='Test');
            insert EndURL1;
            
            HUM_Webservice_Callout__c EncryptionService = New HUM_Webservice_Callout__c(Name = 'EncryptService',Certificate_Name__c = 'Test_Certificate' ,Active__c = true,End_Point_URL__c ='https://dev-crmservicews.humana.com/EncryptRestService_V2.0/encrypt' ,Timeout__c = '12000',Request_Type__c = 'POST',Content_Type__c ='application/json' );
            insert EncryptionService;
            HUM_Webservice_Callout__c EncryptionService1 = New HUM_Webservice_Callout__c(Name = 'EncryptService1',Certificate_Name__c = 'Test_Certificate' ,Active__c = true,End_Point_URL__c ='https://dev-crmservicews.humana.com/EncryptRestService_V2.0/encrypt' ,Timeout__c = '12000',Content_Type__c ='application/json' );
            insert EncryptionService1;
            
            PermissionSet ps = [SELECT Id FROM PermissionSet WHERE Name = 'CRMS_520_Benefits_Read_Only'];
            PermissionSetAssignment psa = new PermissionSetAssignment();
            psa.AssigneeId = objUser.Id;
            psa.PermissionSetId = ps.Id;
            insert psa;

            //RxDetails objRxDetails = new RxDetails();
            
		    //Insert Reference object
            Reference_Business_Line_Product_Grouping__c oref = new Reference_Business_Line_Product_Grouping__c();
            oref.ProductCode__c = 'MED';
            oref.Major_LOB__c = 'EPO';
            oref.Business_Description__c = 'RSO- Medicare';
            insert oref;  
            
            //Insert Reference object
            Reference_Business_Line_Product_Grouping__c oref1 = new Reference_Business_Line_Product_Grouping__c();
            oref1.ProductCode__c = 'DEN';
            oref1.Major_LOB__c = 'PDP';
            oref1.Business_Description__c = '';
            insert oref1;
		 
            // Insert Member Account
            Account objMemAccount = HUMTestDataHelperTest.getAccount();
            objMemAccount.FirstName = 'Test FName';
            objMemAccount.LastName = 'LName'; 
            objMemAccount.source_platform_code__c = 'EM' ;
            objMemAccount.PersonMailingCountry ='United States';
            objMemAccount.Enterprise_Id__c = String.valueof(2000);
            objMemAccount.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId();
            objMemAccount.Birthdate__c = String.valueOf(System.today().month()+'/'+system.today().day()+'/'+System.today().year());
			objMemAccount.Mbr_Gen_Key__c='9334004611222';
            insert objMemAccount;
            
            Account objMemAccount2 = HUMTestDataHelperTest.getAccount();
            objMemAccount2.FirstName = 'Test2';
            objMemAccount2.LastName = 'Lname2'; 
            objMemAccount2.source_platform_code__c = 'LV' ;
            objMemAccount2.PersonMailingCountry ='United States';
            objMemAccount2.Enterprise_Id__c = null;
            objMemAccount2.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId();
            objMemAccount2.Birthdate__c = String.valueOf(System.today().month()+'/'+system.today().day()+'/'+System.today().year());
			objMemAccount2.Mbr_Gen_Key__c='9334004611211';
            insert objMemAccount2;
            		
			
		    //Medical Product
            Product2 objMedProduct = new Product2();
            objMedProduct.Name ='MED';
            objMedProduct.Product_Type__c = 'MCD';
            objMedProduct.productCode = 'MED';
            objMedProduct.Sold_Product_Key_Value__c = 'MC3HH016';
			objMedProduct.Segment_Type__c ='MCR';
            objMedProduct.Major_LOB__c = 'EPO';
            insert objMedProduct;
            System.assert(objMedProduct.Product_Type__c == 'MCD');
            
            Product2 objMedProduct1 = new Product2();
            objMedProduct1.Name ='DEN';
            objMedProduct1.Product_Type__c = 'MCD';
            objMedProduct1.productCode = 'MED';
            objMedProduct1.Sold_Product_Key_Value__c = 'MC3HH016';
			objMedProduct1.Segment_Type__c ='MCR';
            objMedProduct1.Major_LOB__c = 'EPO';
            insert objMedProduct1;
            System.assert(objMedProduct1.Product_Type__c == 'MCD');
			
		    //Medical Policy
            Policy__c oMedicalPolicy = HUMTestDataHelperTest.getPolicy();
            oMedicalPolicy.Name = 'Medical Product';
            oMedicalPolicy.source_cust_cov_key__c = '12345678901234567890';
            oMedicalPolicy.Group_Name__c = objMemAccount.Id;
            oMedicalPolicy.Product__c = objMedProduct.Id;
            oMedicalPolicy.Major_LOB__c = 'EPO';
			oMedicalPolicy.Policy_External_ID__c='00|ODS|5700004654517|1';
			oMedicalPolicy.Contract_Number__c='H1036';
			oMedicalPolicy.PBP_Code__c='801';
			oMedicalPolicy.Medicare_Segment_ID__c='2';
            oMedicalPolicy.Platform__c ='LV';
            oMedicalPolicy.Benefit_coverage__c='9142480101';
            insert oMedicalPolicy;
            System.assert(oMedicalPolicy.Name == 'Medical Product');
            
            Product2 oproduct2 = new Product2();
            oproduct2.Name='MED';
            oproduct2.Product_Type__c='MCD';           
            insert oproduct2;
            system.assertEquals(oproduct2.Name == 'MED',true);
            
            Product2 oproduct3 = new Product2();
            oproduct3.Name='DEN';
            oproduct3.Product_Type__c='MCD';           
            insert oproduct3;
            system.assertEquals(oproduct3.Name == 'DEN',true);
            
            Policy__c oMedicalPolicy2 = HUMTestDataHelperTest.getPolicy();
            oMedicalPolicy2.Name = 'Medical Product';
            oMedicalPolicy2.Group_Name__c = objMemAccount.Id;
            oMedicalPolicy2.Major_LOB__c ='EPO';
            oMedicalPolicy2.Platform__c='EM';
            oMedicalPolicy2.Product__c=oproduct2.Id;
            insert oMedicalPolicy2;
            system.assertEquals(oMedicalPolicy2.Platform__c == 'EM',true);
            
            Policy__c oMedicalPolicy3 = HUMTestDataHelperTest.getPolicy();
            oMedicalPolicy3.Name = 'Dental Product';
            oMedicalPolicy3.Group_Name__c = objMemAccount.Id;
            oMedicalPolicy3.Major_LOB__c ='EPO';
            oMedicalPolicy3.Platform__c='EM';
            oMedicalPolicy3.Product__c=oproduct3.Id;
            insert oMedicalPolicy3;
            system.assertEquals(oMedicalPolicy3.Platform__c == 'EM',true);
            
            
             PurchaserPlan oPurchaserPlan = new PurchaserPlan(name = 'Test Policy', PayerId = objMemAccount.Id,Product__c = objMedProduct.id,
                                                  Policy_Group_Number__c='11111',Major_LOB__c='EPO',Source_Cust_Cov_Key__c='123456789',
                                                  Platform__c='LV',Product__r = objMedProduct);
        insert oPurchaserPlan;  
        system.assert(oPurchaserPlan.id != null);
            
            PurchaserPlan oPurchaserPlan1 = new PurchaserPlan(name = 'Test Policy1', PayerId = objMemAccount2.Id,Product__c = objMedProduct1.id,
                                                  Policy_Group_Number__c='11111',Major_LOB__c='MER',Source_Cust_Cov_Key__c='123456789',
                                                  Platform__c='LV',Product__r = objMedProduct1);
        insert oPurchaserPlan1;  
        system.assert(oPurchaserPlan1.id != null);

        oPurchaserPlan = [Select Name, PayerId, Major_LOB__c, Source_Cust_Cov_Key__c, Product__c, Product__r.Segment_Type__c, LOB_Indicator__c 
                        From PurchaserPlan Where Id = : oPurchaserPlan.Id];
        
        oPurchaserPlan.Product__r.Segment_Type__c = objMedProduct.Segment_Type__c;
        update oPurchaserPlan;
        system.assertEquals(oPurchaserPlan.id!=null,true);
            
            PurchaserPlan oPurchaserPlan3 = new PurchaserPlan(name = 'Test Policy1', PayerId = objMemAccount2.Id,Product__c = objMedProduct1.id,
                                                  Policy_Group_Number__c='11111',Major_LOB__c='MER',Source_Cust_Cov_Key__c='123456789',
                                                  Platform__c='EM',Product__r = objMedProduct1);
            insert oPurchaserPlan3;
			
            //Medical Member Plan , SubscriberId, EffectiveT
            MemberPlan objMemberPlan = New MemberPlan();
            objMemberPlan.Member = objMemAccount;
            objMemberPlan.Policy__c = oMedicalPolicy.Id;
            objMemberPlan.EffectiveTo =Date.valueOf('2025-12-31');
            objMemberPlan.EffectiveFrom = Date.valueOf('2015-12-31');
            objMemberPlan.Member_Plan_External_Id__c  = '00|ODS|123456789|89|89|02-01-2001';
            objMemberPlan.name = '123456789';
            objMemberplan.planId=oPurchaserPlan.id;
            objMemberPlan.MemberId = objMemAccount.id;
            objMemberPlan.Member_id_base__c='H78651944';
            objMemberPlan.planId=oPurchaserPlan.id;
            insert objMemberPlan;
            System.assert(objMemberPlan.Member_Plan_External_Id__c == '00|ODS|123456789|89|89|02-01-2001');
            
            
            MemberPlan objMemberPlan1 = New MemberPlan();
            objMemberPlan1.Member = objMemAccount;
            objMemberPlan1.Policy__c = oMedicalPolicy2.Id;
           
            objMemberPlan1.Member_Plan_External_Id__c  = '00|ODS|25478954|89|89|02-01-2001';
            objMemberPlan1.name = '56789';
            objMemberPlan1.planId=oPurchaserPlan3.id;
            objMemberPlan1.MemberId = objMemAccount.id;
            objMemberPlan1.Member_id_base__c='H78651944';
            insert objMemberPlan1;
            
            MemberPlan objMemberPlan2 = New MemberPlan();
            objMemberPlan2.Member = objMemAccount2;
            objMemberPlan2.Policy__c = oMedicalPolicy.Id;
            objMemberPlan2.EffectiveTo =null;
            objMemberPlan2.EffectiveFrom = null;
            objMemberPlan2.Member_Plan_External_Id__c  = '00|ODS|123456789|89|89|02-01-2002';
            objMemberPlan2.name = '987654321';
            objMemberplan2.planId=oPurchaserPlan.id;
            objMemberPlan2.MemberId = objMemAccount2.id;
            objMemberPlan2.Member_id_base__c='h676758';
            insert objMemberPlan2;
            
            MemberPlan objMemberPlan3 = New MemberPlan();
            objMemberPlan3.Member = objMemAccount2;
            objMemberPlan3.Policy__c = oMedicalPolicy3.Id;
            objMemberPlan3.EffectiveTo =null;
            objMemberPlan3.EffectiveFrom = null;
            objMemberPlan3.Member_Plan_External_Id__c  = '00|ODS|123456789|90|89|02-01-2002';
            objMemberPlan3.name = '9876543211';
            objMemberPlan3.planId=oPurchaserPlan1.id;
            objMemberPlan3.MemberId = objMemAccount2.id;
            objMemberPlan3.Member_id_base__c='h676758';
            insert objMemberPlan3;
            
            
            
           
			}
		}
			
			static testMethod void testgenerateCertificateURL()
			{
			  User testUser = [Select Id, Name from User where LastName = 'test User1'];
			  MemberPlan memberPlanObj= [select id,name from MemberPlan where name='123456789' limit 1];
                string var;
			   System.runAs(testUser)
               {
                Test.startTest();
			    var=ExternalLinkLauncher_LC_HUM.generateCertificateURL(memberPlanObj.id);
                   system.assert(var <> null);
				Test.stopTest();
			   }
			}
			
			static testMethod void testgetMemberPlanRecord()
			{
			  User testUser = [Select Id, Name from User where LastName = 'test User1'];
			  MemberPlan memberPlanObj= [select id,name from MemberPlan where name='123456789' limit 1];
			  System.runAs(testUser)
               {
                Test.startTest();
			     ExternalLinkLauncher_LC_HUM.getMemberPlanRecord(memberPlanObj.id);
                 ExternalLinkLauncher_LC_HUM.getMemberPlanRecord('123');
                 ExternalLinkLauncher_LC_HUM.getMemberPlanRecord(''); 
                 ExternalLinkLauncher_LC_HUM.getMemberPlanRecord(null); 
				Test.stopTest();
			   }	
			}

			static testMethod void testgenerateBenefitGridURL()
			{
			  User testUser = [Select Id, Name from User where LastName = 'test User1'];
			  MemberPlan memberPlanObj= [select id,name from MemberPlan where name='123456789' limit 1];
                MemberPlan memberPlanObj1= [select id,name from MemberPlan where name='987654321' limit 1];
                MemberPlan memberPlanObj2= [select id,name from MemberPlan where name='9876543211' limit 1];
                String var;
			  System.runAs(testUser)
               {
                   System.assertNotEquals(null, ExternalLinkLauncher_LC_HUM.generateBenefitGridURL(memberPlanObj.id));
                   System.assertNotEquals(null, ExternalLinkLauncher_LC_HUM.generateBenefitGridURL(memberPlanObj2.id));
                   System.assertNotEquals(null, ExternalLinkLauncher_LC_HUM.generateBenefitGridURL(memberPlanObj1.id));
			   }	
			}
    
    static testMethod void testisNonMedicareMedicaidPolicy()
    {
        User testUser = [Select Id, Name from User where LastName = 'test User1'];
        MemberPlan memberPlanObj= [select id,name,policy__r.Platform__c,policy__r.Group_Name__c,policy__r.Benefit_coverage__c
                                    from MemberPlan where name='123456789' limit 1];
        System.runAs(testUser)
        {
            System.assertNotEquals(null, ExternalLinkLauncher_LC_HUM.isNonMedicareMedicaidPolicy(memberPlanObj.policy__r.Platform__c,
                                                memberPlanObj.policy__r.Group_Name__c,memberPlanObj.policy__r.Benefit_coverage__c));
            ExternalLinkLauncher_LC_HUM.isNonMedicareMedicaidPolicy('EM', memberPlanObj.policy__r.Group_Name__c,memberPlanObj.policy__r.Benefit_coverage__c);
            ExternalLinkLauncher_LC_HUM.isNonMedicareMedicaidPolicy('', memberPlanObj.policy__r.Group_Name__c,memberPlanObj.policy__r.Benefit_coverage__c);

        }
    }
			
    
    static testmethod void checkdateformat(){
        User testUser = [Select Id,Name from User where LastName='test User1'];
        System.runAs(testUser)
        {
            System.assertEquals('01-01-2020', ExternalLinkLauncher_LC_HUM.DateFormat('01-01-2020'));
            system.assertEquals('2020-01-01', ExternalLinkLauncher_LC_HUM.DateFormat('01/01/2020'));
            system.assertEquals('', ExternalLinkLauncher_LC_HUM.DateFormat(''));
            system.assertEquals('4000-12-31', ExternalLinkLauncher_LC_HUM.DateFormat('12/31/4000'));
            system.assertEquals('12,31,2020', ExternalLinkLauncher_LC_HUM.DateFormat('12,31,2020'));
        }
    }
    
    static testmethod void testsetBenefitMatrixBaseURL(){
        User testUser = [Select Id,Name from User where LastName='test User1'];
        System.runAs(testUser)
        {
            System.assertNotEquals(null, ExternalLinkLauncher_LC_HUM.setBenefitMatrixBaseURL());
        }
    }
    
    static testmethod void testgenerateDebutToolURL()
    {
        User testUser = [Select Id,Name from User where LastName='test User1'];   
        MemberPlan memberPlanObj= [select id,name,Member_id_base__c from MemberPlan where name='123456789' limit 1];
        
       
        System.runAs(testUser)
        {
            System.assertNotEquals(null, ExternalLinkLauncher_LC_HUM.generateDebutToolURL());
        
        }
          
    }
    
    
	
    static testmethod void testsetMentorBenefitGridBaseURL(){
        User testUser = [Select Id,Name from User where LastName='test User1'];
        System.runAs(testUser)
        {
            System.assertNotEquals(null, ExternalLinkLauncher_LC_HUM.setMentorBenefitGridBaseURL(true));
            System.assertNotEquals(null, ExternalLinkLauncher_LC_HUM.setMentorBenefitGridBaseURL(false));
        }
    }
    
    static testmethod void testgetMemberIdBase()
    {
        User testUser = [Select Id,Name from User where LastName='test User1'];
        MemberPlan memberPlanObj= [select id,name,Member_id_base__c from MemberPlan where name='123456789' limit 1];
        System.runAs(testUser)
        {
            System.assertEquals('H78651944', ExternalLinkLauncher_LC_HUM.getMemberIdBase(memberPlanObj.id));
            //added after try ctach blcok
            System.assertEquals('', ExternalLinkLauncher_LC_HUM.getMemberIdBase('1234'));
        }
    }
    
    static testmethod void testcheckMCDProduct()
    {
        User testUser = [Select Id,Name from User where LastName='test User1'];
        MemberPlan memberPlanObj= [select id,name,Member_id_base__c from MemberPlan where name='123456789' limit 1];
        Product2 Pro= [select Product_Type__c from Product2 where Name ='MED' limit 1];
        System.runAs(testUser)
        {
            system.assertEquals(true, ExternalLinkLauncher_LC_HUM.checkMCDProduct(Pro.Product_Type__c));
            system.assertEquals(false, ExternalLinkLauncher_LC_HUM.checkMCDProduct(null));
            
        }
        }
        
    static testmethod void testinvokeEncryptService()
    {
        
        Test.startTest();
        User testUser = [Select Id,Name from User where LastName='test User1'];
        MemberPlan memberPlanObj= [select id,name,Member_id_base__c from MemberPlan where name='123456789' limit 1];
        System.runAs(testUser){
        Continuation con = (Continuation)ExternalLinkLauncher_LC_HUM.invokeEncryptService(memberPlanObj.id);
        con.continuationMethod = 'ProcessEncryptService';    
        HttpResponse httpRes = new Httpresponse ();
        String resBodyString= '{"EncryptResponse":{"EncryptResult":{"EncryptValue":"sP0g3U6a8m28aTgBMyTBAQ=="}}}}';
        httpRes.setBody(resBodyString);
        con.state = 'Continuation-1';
        string reqType = 'Continuation-1';        
        List<string> labelGetmember = new List<string>();
        labelGetmember.add(reqType);
        Test.setContinuationResponse(reqType,httpRes);
        Object result = ExternalLinkLauncher_LC_HUM.ProcessEncryptService(labelGetmember,con);
        system.assertNotEquals(null, result);
        Test.stopTest();
        }
    }

    static testmethod void testgenerateRXConnectProURL()
    {
        User testUser = [Select Id,Name from User where LastName='test User1'];
        MemberPlan memberPlanObj= [select id,name,Member_id_base__c from MemberPlan where name='123456789' limit 1];
        Object obj;
        System.runAs(testUser){
            obj= ExternalLinkLauncher_LC_HUM.generateRXConnectProURL(memberPlanObj.id,'source=scrm');
            System.assert(obj <> null);

            obj= ExternalLinkLauncher_LC_HUM.generateRXConnectProURL(memberPlanObj.id,null);
            System.assert(obj<> null);
                
        }
    }
    
    static testmethod void testisMedicareMedicaidPolicy(){
        User testUser = [Select Id,Name from User where LastName='test User1'];
        System.runAs(testUser){
            System.assertNotEquals(null, ExternalLinkLauncher_LC_HUM.isMedicareMedicaidPolicy('LV','OH','H1234','101','MCD','Test','Test'));
            System.assertNotEquals(null, ExternalLinkLauncher_LC_HUM.isMedicareMedicaidPolicy('ES','OH','H1234','101','MCD','Test','Test'));
            System.assertNotEquals(null, ExternalLinkLauncher_LC_HUM.isMedicareMedicaidPolicy('ES','OH','H1234','101','MES','Test','MES'));
     	}
    }
    
    static testmethod void testgenerateBenefitMatrixURL()
    {
        User testUser = [Select Id,Name from User where LastName='test User1'];
        MemberPlan memberPlanObj= [select id,name,Member_id_base__c from MemberPlan where name='56789' limit 1];
        MemberPlan memberPlanObj1= [select id,name,Member_id_base__c from MemberPlan where name='123456789' limit 1];
        Boolean isRapidForceApp = true;
        System.runAs(testUser){
            System.assertNotEquals(null, ExternalLinkLauncher_LC_HUM.generateBenefitMatrixURL(memberPlanObj.id));
            System.assertNotEquals(null, ExternalLinkLauncher_LC_HUM.generateBenefitMatrixURL(memberPlanObj1.id));
            System.assertNotEquals(null, ExternalLinkLauncher_LC_HUM.generateBenefitMatrixURL('123'));
        }
    }
    static testmethod void testgenerateBenefitMatrixURLnull()
    {
        User testUser = [Select Id,Name from User where LastName='test User1'];
        MemberPlan memberPlanObj= [select id,name,Member_id_base__c from MemberPlan where name='123456789' limit 1];
        Boolean isRapidForceApp = true;
        System.runAs(testUser){
             System.assertEquals(null, ExternalLinkLauncher_LC_HUM.generateBenefitMatrixURL(''));
        }
    }
    
    static testmethod void testgetInteractionAndCaseDetail()
    {
        User testUser = [Select Id,Name from User where LastName='test User1'];
        Account oAccount = [Select Id  from Account where LastName = 'LName'];  
        MemberPlan memberPlanObj= [select id,name,Member_id_base__c from MemberPlan where name='123456789' limit 1];
        System.runAs(testUser){
            CRMFunctionality_ONOFF_Switch__c CRMOnOff = new CRMFunctionality_ONOFF_Switch__c(Name = '5066619', IsON__c = true );
        	insert CRMOnOff;
            
        	Interaction__c interaction = new Interaction__c ();
        	interaction.Caller_Name__c ='TEST';
        	interaction.Interacting_with__c = oAccount.Id;
        	interaction.Interacting_With_type__c='Member';
        	interaction.Interaction_Origin__c='Inbound Call';
        	insert interaction;
        	system.assertNotEquals(interaction,null,'Value not matched');
        
        	Interaction_Member__c InteractionMember = new Interaction_Member__c();
        	InteractionMember.Interacting_About__c = oAccount.id;
        	InteractionMember.Interaction__c = interaction.id;
        	InteractionMember.Interacting_About_Type__c ='Member';
        	insert InteractionMember;
        	System.assertNotEquals(InteractionMember, null, 'InteractionMember not created');
            
        	ExternalLinkLauncher_LC_HUM.getInteractionAndCaseDetail(interaction.Id);    
        
            Case objCase = HUMTestDataHelperTest.getCase();
            insert objCase;
            system.assertNotEquals(objCase,null,'Value not matched');
            
            Case_Interaction__c cc1= new Case_Interaction__c(Case__c = objCase.Id, Interaction__c=interaction.Id);
            insert cc1;
            system.assertNotEquals(cc1,null,'Value not matched');
            
            ExternalLinkLauncher_LC_HUM.getInteractionAndCaseDetail(interaction.Id);
        }
    }
    
    static testmethod void testBuiltHttpRequest()
    {
        HUM_Webservice_Callout__c oEncryptService = HUM_Webservice_Callout__c.getValues('EncryptService'); 
        HUM_Webservice_Callout__c oEncryptService1 = HUM_Webservice_Callout__c.getValues('EncryptService1'); 
        ExternalLinkLauncher_LS_HUM.builtHttpRequest(oEncryptService);
        ExternalLinkLauncher_LS_HUM.builtHttpRequest(oEncryptService1);
    }
}
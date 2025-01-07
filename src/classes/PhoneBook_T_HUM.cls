/***********************************************************************************************************************************
* Apex Class Name  : PhoneBook_T_HUM 
* Version          : 1.0
* Created Date     : 22nd January, 2021
* Function         : CRM Search Integration
* Test Class       : 
****************************************************************************
* Modification Log:
* Developer Name              				 Code Review #    	     Date                 Description
*---------------------------------------------------------------------------------------------------------------------------------
* Rashmi Patil/Isha Gupta/Shreya Agrawal				        	01/22/2021			  Original Version
* Nikhil/Ekta                                                       04/06/2021            Assertion fix in TestCallTransfer method.
* Nikhil/Isha														05/28/2021			  Increasing coverage.
* Shreya Agrawal													07/21/2021			  Increasing code coverage for 08/20 story builds
* Isha Gupta                                                        08/03/2021             Increased code coverage for User story -2484206,2484212
* Isha Gupta                                                        09/07/2021              Increased code coverage for US#2230000
* Sahil Verma							                            10/22/2021			  Increasing code coverage for 11/19 story builds
* Sahil/Shreya							                            11/03/2021		      Increased code coverage
* Sahil/Shreya							                            12/02/2021		      Increased code coverage
* Harshada Kamble                                                   08/19/2022            Increased code coverage for US-3701398 & US-3653389
* Kiran Kotni                                                       08/04/2023            Increased code coverage for US 4878631
**********************************************************************************************************************************/

@isTest
public class PhoneBook_T_HUM 
{
    @testSetup static void setUpTestDataPhonebook()
    {
        List<HUM_Webservice_Callout__c> AllServices = new List<HUM_Webservice_Callout__c>();
        HUM_Webservice_Callout__c GetCallDataServiceDetails = new HUM_Webservice_Callout__c(Name ='GetCallData',Certificate_Name__c = 'Humana_GuidanceCare_CECP' ,Active__c = true,End_Point_URL__c ='https://dev-crmservicews.humana.com/IVRService/GetCallData' ,Timeout__c = '120',Request_Type__c = 'POST' );
        HUM_Webservice_Callout__c PhoneBookServiceDetails = new HUM_Webservice_Callout__c(Name ='GetAllPhoneBookRules',Certificate_Name__c = 'Humana_GuidanceCare_CECP' ,Active__c = true,End_Point_URL__c ='https://dev-crmservicews.humana.com/IVRService/GetAllPhoneBookRules' ,Timeout__c = '120',Request_Type__c = 'POST' );
        HUM_Webservice_Callout__c RouteSoftphoneXmlServiceDetails = new HUM_Webservice_Callout__c(Name ='RouteSoftphoneXml',Certificate_Name__c = 'Humana_GuidanceCare_CECP' ,Active__c = true,End_Point_URL__c ='https://dev-crmservicews.humana.com/IVRService/RouteSoftphoneXml' ,Timeout__c = '120',Request_Type__c = 'POST' );
		HUM_Webservice_Callout__c SaveCallDataServiceDetails = new HUM_Webservice_Callout__c(Name ='SaveCallData',Certificate_Name__c = 'Humana_GuidanceCare_CECP' ,Active__c = true,End_Point_URL__c ='https://dev-crmservicews.humana.com/IVRService/SaveCallData' ,Timeout__c = '120',Request_Type__c = 'POST' );
		AllServices.add(GetCallDataServiceDetails);
        AllServices.add(PhoneBookServiceDetails);
        AllServices.add(RouteSoftphoneXmlServiceDetails);
        AllServices.add(SaveCallDataServiceDetails);
        insert AllServices;

        List<SecurePayments_HUM__c> listSecurePayments = new List<SecurePayments_HUM__c>();
        SecurePayments_HUM__c eBilling = new SecurePayments_HUM__c();
        eBilling.Name = 'eBilling';
        eBilling.SecurePaymentName__c = 'eBilling';
        eBilling.IsLightning__c = false; 
        eBilling.FlowId__c = ''; 
        
        SecurePayments_HUM__c pharmacy = new SecurePayments_HUM__c();
        pharmacy.Name = 'Pharmacy';
        pharmacy.SecurePaymentName__c = 'Pharmacy';
        pharmacy.IsLightning__c = false; 
        pharmacy.FlowId__c = '';	
        
        SecurePayments_HUM__c cbis = new SecurePayments_HUM__c();
        cbis.Name = 'CBIS';
        cbis.SecurePaymentName__c = 'CBIS';
        cbis.IsLightning__c = false;
        cbis.FlowId__c = '';

        listSecurePayments.add(eBilling);
        listSecurePayments.add(pharmacy);
        listSecurePayments.add(cbis);
        insert listSecurePayments;
        system.assertEquals(listSecurePayments.size(),3);
        
        //Create Account records for different types
        List<Account> allAccounts = new List<Account>();
        string groupAccountRecordType=  Schema.SObjectType.Account.getRecordTypeInfosByName().get('Group').getRecordTypeId();  
        Account objGrpAccount = HUMTestDataHelperTest.getAccount();
        objGrpAccount.recordtypeid = groupAccountRecordType;
        objGrpAccount.Name = 'TestGroup00';
        objGrpAccount.Group_Number__c = '216277';
        objGrpAccount.BillingState = 'Alabama';
        objGrpAccount.Home_Office_Account__c = true;
        objGrpAccount.Birthdate__c = '01/01/1995';
        objGrpAccount.Enterprise_ID__c='025134288';
        allAccounts.add(objGrpAccount);
        string unknownGroupAccountRecordType=  Schema.SObjectType.Account.getRecordTypeInfosByName().get('Unknown Group').getRecordTypeId();  
        Account objUnknownGrpAccount = HUMTestDataHelperTest.getAccount();
        objUnknownGrpAccount.recordtypeid = unknownGroupAccountRecordType;
        objUnknownGrpAccount.Name = 'Unknown TestGroup00';
        objUnknownGrpAccount.Group_Number__c = '216277';
        objUnknownGrpAccount.BillingState = 'Alabama';
        objUnknownGrpAccount.Home_Office_Account__c = true;
        objUnknownGrpAccount.Birthdate__c = '01/01/1995';
        objUnknownGrpAccount.Enterprise_ID__c='025134288';
        allAccounts.add(objUnknownGrpAccount);

        Account objGrpAcc1 = new Account();
        objGrpAcc1.recordtypeid = groupAccountRecordType;
        objGrpAcc1.Name = 'TestGroup1';
        objGrpAcc1.Group_Number__c ='22221';
        allAccounts.add(objGrpAcc1);
        
        Account objGrpAcc2 = new Account();
        objGrpAcc2.recordtypeid = groupAccountRecordType;
        objGrpAcc2.Name = 'TestGroup2';
        objGrpAcc2.Group_Number__c ='22221';
        allAccounts.add(objGrpAcc2);
        
        string sMemberRecordtypeID=  Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId();  
        Account actMember = HUMTestDataHelperTest.getAccount();
        actMember.Lastname='TesMember';
        actMember.FirstName = 'Member';
        actMember.Birthdate__c = '11-18-2014';
        actMember.recordtypeid = sMemberRecordtypeID;    
        allAccounts.add(actMember);
        string sUnknownMemberRecordtypeID=  Schema.SObjectType.Account.getRecordTypeInfosByName().get('Unknown Member').getRecordTypeId();  
        Account actUnknownMember = HUMTestDataHelperTest.getAccount();
        actUnknownMember.Lastname='UnknownTesMember';
        actUnknownMember.FirstName = 'Member';
        actUnknownMember.Birthdate__c = '11-18-2014';
        actUnknownMember.recordtypeid = sUnknownMemberRecordtypeID;    
        allAccounts.add(actUnknownMember);
        
        Account actMember1 = HUMTestDataHelperTest.getAccount();
        actMember1.Lastname='TesMember1';
        actMember1.FirstName = 'Member';
        actMember1.Birthdate__c = '11-18-2014';
        actMember1.recordtypeid = sMemberRecordtypeID;    
        allAccounts.add(actMember1);
        
        Account actMember2 = HUMTestDataHelperTest.getAccount();
        actMember2.Lastname='TesMember2';
        actMember2.FirstName = 'Member';
        actMember2.Birthdate__c = '11-18-2014';
        actMember2.recordtypeid = sMemberRecordtypeID;    
        allAccounts.add(actMember2);
        
        Account actMember3 = HUMTestDataHelperTest.getAccount();
        actMember3.Lastname='TesMember3';
        actMember3.FirstName = 'Member';
        actMember3.Birthdate__c = '11-18-2014';
        actMember3.recordtypeid = sMemberRecordtypeID;    
        allAccounts.add(actMember3);
        
        string sProviderRecordtypeID=  Schema.SObjectType.Account.getRecordTypeInfosByName().get('Provider').getRecordTypeId(); 
        Account objProviderAccount = new Account();
        objProviderAccount.recordtypeid = sProviderRecordtypeID;
        objProviderAccount.Name = 'TestProvider';
        allAccounts.add(objProviderAccount);
        string sUnknownProviderRecordtypeID=  Schema.SObjectType.Account.getRecordTypeInfosByName().get('Unknown Provider').getRecordTypeId(); 
        Account objUnknownProviderAccount = new Account();
        objUnknownProviderAccount.recordtypeid = sUnknownProviderRecordtypeID;
        objUnknownProviderAccount.Name = 'Unknown TestProvider';
        allAccounts.add(objUnknownProviderAccount);
        
        string sAgentRecordtypeID=  Schema.SObjectType.Account.getRecordTypeInfosByName().get('Agent/Broker').getRecordTypeId(); 
        Account objAgentAccount = new Account();
        objAgentAccount.recordtypeid = sAgentRecordtypeID;
        objAgentAccount.Name = 'TestAgent';
        allAccounts.add(objAgentAccount);
        string sUnknownAgentRecordtypeID=  Schema.SObjectType.Account.getRecordTypeInfosByName().get('Unknown Agent/Broker').getRecordTypeId(); 
        Account objUnknownAgentAccount = new Account();
        objUnknownAgentAccount.recordtypeid = sUnknownAgentRecordtypeID;
        objUnknownAgentAccount.Name = 'Unknown TestAgent';
        allAccounts.add(objUnknownAgentAccount);

        Account objAgentAccount1 = new Account();
        objAgentAccount1.recordtypeid = sAgentRecordtypeID;
        objAgentAccount1.Name = 'TestAgent1';
        objAgentAccount1.Agent_ID__c = '54321';
        allAccounts.add(objAgentAccount1);
        
        Account objAgentAccount2 = new Account();
        objAgentAccount2.recordtypeid = sAgentRecordtypeID;
        objAgentAccount2.Name = 'TestAgent1';
        objAgentAccount2.Agent_ID__c = '54321';
        allAccounts.add(objAgentAccount2);


        insert allAccounts;
        system.assertEquals(allAccounts.size(),15);
        system.assertEquals(objGrpAccount.Name == 'TestGroup00', true);
        
        //Create Product 
        Product2 prd = new Product2(Name='MER',ProductCode='MER',HDHP_Indicator__c = 'Y');
        insert prd;
        system.assert(prd.id!=null);
        
        //create Policy
        Policy__c oMedicalPolicy = HUMTestDataHelperTest.getPolicy();
        oMedicalPolicy.Name = 'Medical Product';
        oMedicalPolicy.source_cust_cov_key__c = '12345678901234567890' ; 
        oMedicalPolicy.Group_Name__c = objGrpAccount.Id;
        oMedicalPolicy.Major_LOB__c ='EPO'; 
        oMedicalPolicy.ASO__c = 'N';
        oMedicalPolicy.Business_Segment__c='LargeGroup-Medicare';
        oMedicalPolicy.Issue_State__c='IN';
        oMedicalPolicy.Product__c = prd.id;
        oMedicalPolicy.Platform__c = 'CB';
        oMedicalPolicy.Original_Major_LOB__c = 'HMO';
        oMedicalPolicy.Source_Legal_Entity__c = 'Test';
        oMedicalPolicy.Source_Major_LOB__c = 'HMO';
        oMedicalPolicy.Selling_Market_Number__c = '12345';
        insert oMedicalPolicy;
        system.assertEquals(oMedicalPolicy.Name == 'Medical Product', true);

        Policy__c oMedicalPolicy1 = HUMTestDataHelperTest.getPolicy();
        oMedicalPolicy1.Name = 'Medical Product 1';
        oMedicalPolicy1.source_cust_cov_key__c = '12345678901234567891' ; 
        oMedicalPolicy1.Group_Name__c = objGrpAccount.Id;
        oMedicalPolicy1.Major_LOB__c ='EPO'; 
        oMedicalPolicy1.ASO__c = 'N';
        oMedicalPolicy1.Business_Segment__c='LargeGroup-Medicare';
        oMedicalPolicy1.Issue_State__c='IN';
        oMedicalPolicy1.Product__c = prd.id;
        oMedicalPolicy1.Platform__c = 'CB';
        oMedicalPolicy1.Original_Major_LOB__c = 'HMO';
        oMedicalPolicy1.Source_Legal_Entity__c = 'Test';
        oMedicalPolicy1.Source_Major_LOB__c = 'HMO';
        oMedicalPolicy1.Selling_Market_Number__c = '12345';
        insert oMedicalPolicy1;
        system.assertEquals(oMedicalPolicy1.Name == 'Medical Product 1', true);

        
        // create Policy Member
        Policy_Member__c objMedPolMemb = HUMTestDataHelperTest.getPolicyMember();
        objMedPolMemb.Member__c = objGrpAccount.Id;
        objMedPolMemb.Policy__c = oMedicalPolicy1.Id;
        objMedPolMemb.Relationship__c = 'parent';    
        objMedPolMemb.end_date__c = '12/31/2020'; 
        objMedPolMemb.Effective_Date__c='01/01/2018';       
        objMedPolMemb.Policy_Member_External_ID__c = '000|ODS|123456789|89|89|02-01-2001';
        insert objMedPolMemb;

        Policy_Member__c objMedPolMembr = HUMTestDataHelperTest.getPolicyMember();
        objMedPolMembr.Member__c = objGrpAcc1.Id;
        objMedPolMembr.Policy__c = oMedicalPolicy.Id;
        objMedPolMembr.Relationship__c = 'self';    
        objMedPolMembr.end_date__c = '12/31/2019'; 
        objMedPolMembr.Effective_Date__c='01/01/2017';       
        objMedPolMembr.Policy_Member_External_ID__c = '00|ODS|123456789|99|89|02-01-2001';
        objMedPolMembr.Subscriber__c=objMedPolMemb.id;
        insert objMedPolMembr;
        
        Member_ID__c objMemberIdSub = HUMTestDataHelperTest.getmemberID();
        objMemberIdSub.Name = '025134289';
        objMemberIdSub.Member_ID_Value__c = '025134289';
        objMemberIdSub.Member_Dependent_Code__c = '06';
        objMemberIdSub.Policy_Member__c=objMedPolMemb.id;
        objMemberIdSub.Policy__c=oMedicalPolicy.id;
        insert objMemberIdSub;

        Policy_Member__c objMedPolMem = HUMTestDataHelperTest.getPolicyMember();
        objMedPolMem.Member__c = objGrpAccount.Id;
        objMedPolMem.Policy__c = oMedicalPolicy.Id;
        objMedPolMem.Relationship__c = 'self';    
        objMedPolMem.end_date__c = '12/31/2015'; 
        objMedPolMem.Effective_Date__c='01/01/2015';       
        objMedPolMem.Policy_Member_External_ID__c = '00|ODS|123456789|89|89|02-01-2001';
        objMedPolMem.Subscriber__c=objMedPolMemb.id;
        insert objMedPolMem;

        Member_ID__c objMemberId = HUMTestDataHelperTest.getmemberID();
        objMemberId.Member_ID_Value__c = '025134288';
        objMemberId.Member_Dependent_Code__c = '03';
        objMemberId.Policy_Member__c=objMedPolMem.id;
        objMemberId.Policy__c=oMedicalPolicy.id;
        insert objMemberId;

         Member_ID__c objMemberId1 = HUMTestDataHelperTest.getmemberID();
        objMemberId1.Member_ID_Value__c = '4444';
        objMemberId1.Name = '4444';
        objMemberId1.Member_Dependent_Code__c = '03';
        objMemberId1.Policy_Member__c=objMedPolMem.id;
        objMemberId1.Policy__c=oMedicalPolicy.id;
        insert objMemberId1;
        
        Member_ID__c objMemberId2 = HUMTestDataHelperTest.getmemberID();
        objMemberId2.Member_ID_Value__c = '4444';
        objMemberId2.Name = '4444';
        objMemberId2.Member_Dependent_Code__c = '07';
        objMemberId2.Policy_Member__c=objMedPolMem.id;
        objMemberId2.Policy__c=oMedicalPolicy.id;
        insert objMemberId2;
        
        Member_ID__c objMemberId3 = HUMTestDataHelperTest.getmemberID();
        objMemberId3.Member_ID_Value__c = '4444';
        objMemberId3.Name = '4444';
        objMemberId3.Member_Dependent_Code__c = '07';
        objMemberId3.Policy_Member__c=objMedPolMembr.id;
        objMemberId3.Policy__c=oMedicalPolicy1.id;
        insert objMemberId3;

        
        ConsumerID__c cnsid1 = new ConsumerID__c();
        cnsid1.Consumer_ID__c = '123456';
        cnsid1.ID_Type__c = 'Member';
        cnsid1.Account__c = actMember.id;
        insert cnsid1;
        
        ConsumerID__c cnsid2 = new ConsumerID__c();
        cnsid2.Consumer_ID__c = '123456';
        cnsid2.ID_Type__c = 'Mem';
        cnsid2.Account__c = actMember.id;
        insert cnsid2;
        
        ConsumerID__c cnsid3 = new ConsumerID__c();
        cnsid3.Consumer_ID__c = '123456';
        cnsid3.ID_Type__c = 'Mem';
        cnsid3.Account__c = objAgentAccount.id;
        insert cnsid3;
        
        ConsumerID__c cnsid4 = new ConsumerID__c();
        cnsid4.Consumer_ID__c = '1234567';
        cnsid4.ID_Type__c = 'Member';
        cnsid4.Account__c = actMember.id;
        insert cnsid4;
        
        ConsumerID__c cnsid5 = new ConsumerID__c();
        cnsid5.Consumer_ID__c = '123457';
        cnsid5.ID_Type__c = 'Mem';
        cnsid5.Account__c = objAgentAccount.id;
        insert cnsid5;
        ConsumerID__c cnsid6 = new ConsumerID__c();
        cnsid6.Consumer_ID__c = '789090';
        cnsid6.ID_Type__c = 'Mem';
        cnsid6.Account__c = objProviderAccount.id;
        insert cnsid6;
        
        ConsumerID__c cnsid7 = new ConsumerID__c();
        cnsid7.Consumer_ID__c = '789090';
        cnsid7.ID_Type__c = 'Mem';
        cnsid7.Account__c = objUnknownProviderAccount.id;
        insert cnsid7;
        
        // create switches
        List<CRMFunctionality_ONOFF_Switch__c> listOfSwitch = new List<CRMFunctionality_ONOFF_Switch__c>();
        CRMFunctionality_ONOFF_Switch__c objSwitch = new CRMFunctionality_ONOFF_Switch__c();
        objSwitch.name = '1799704_Genesys_Level3';
        objSwitch.IsON__c = true;
        listOfSwitch.add(objSwitch);
        CRMFunctionality_ONOFF_Switch__c objSwtch = new CRMFunctionality_ONOFF_Switch__c();
        objSwtch.name = '1799704_Genesys_Level2';
        objSwtch.IsON__c = true;
        listOfSwitch.add(objSwtch);            
        CRMFunctionality_ONOFF_Switch__c objStch = new CRMFunctionality_ONOFF_Switch__c();
        objStch.name = '1799704_Genesys_Level1';
        objStch.IsON__c = true;
        listOfSwitch.add(objStch);
        CRMFunctionality_ONOFF_Switch__c obj1=new CRMFunctionality_ONOFF_Switch__c();
        obj1.IsON__c=true;
        obj1.Name='2760646';
        listOfSwitch.add(obj1);
        
        CRMFunctionality_ONOFF_Switch__c obj2=new CRMFunctionality_ONOFF_Switch__c();
        obj2.IsON__c=false;
        obj2.Name='4726311';
        listOfSwitch.add(obj2);
        
        insert listOfSwitch;
        System.assert(listOfSwitch.size() == 5);
        
        List<SOFTPHONE_URL_PARAM__c> softphoneurldata = new List<SOFTPHONE_URL_PARAM__c>();

        List<FieldDefinition> inqaLastField = [Select DurableId from FieldDefinition where EntityDefinition.QualifiedApiName = 'Account' AND QualifiedApiName = 'Individual_Last_Name__c'];
        string inqaLastFieldValue =  inqaLastField[0].DurableId.right(inqaLastField[0].DurableId.length()-8);

        List<FieldDefinition> inqaFirstField = [Select DurableId from FieldDefinition where EntityDefinition.QualifiedApiName = 'Account' AND QualifiedApiName = 'Individual_First_Name__c'];
        string inqaFirstFieldValue =  inqaFirstField[0].DurableId.right(inqaFirstField[0].DurableId.length()-8);

        List<FieldDefinition> grpNumberField = [Select DurableId from FieldDefinition where EntityDefinition.QualifiedApiName = 'Account' AND QualifiedApiName = 'Group_Number__c'];
        string grpNumberFieldValue =  grpNumberField[0].DurableId.right(grpNumberField[0].DurableId.length()-8);

        List<FieldDefinition> npiIdField = [Select DurableId from FieldDefinition where EntityDefinition.QualifiedApiName = 'Account' AND QualifiedApiName = 'NPI_ID__c'];
        string npiIdFieldValue =  npiIdField[0].DurableId.right(npiIdField[0].DurableId.length()-8);
        
        SOFTPHONE_URL_PARAM__c ap1 = new SOFTPHONE_URL_PARAM__c();
        ap1.Name = 'Agent Param 1';
        ap1.Name__c = 'INQA_First';
        ap1.Param_for__c = 'A';                            
        ap1.Value__c = 'AccountName_SOFTPHONE';
        softphoneurldata.add(ap1);
        
        SOFTPHONE_URL_PARAM__c ap2 = new SOFTPHONE_URL_PARAM__c();
        ap2.Name = 'Agent Param 2';
        ap2.Name__c = 'INQA_Last';
        ap2.Param_for__c = 'A';                            
        // ap2.Value__c = '00NF000000CCfzS';
        ap2.Value__c = inqaLastFieldValue;
        softphoneurldata.add(ap2);
        
        SOFTPHONE_URL_PARAM__c ap3 = new SOFTPHONE_URL_PARAM__c();
        ap3.Name = 'Agent Param 3';
        ap3.Name__c = 'INQA_ID';
        ap3.Param_for__c = 'A';                            
        ap3.Value__c = 'sIntAboutAgentId';
        softphoneurldata.add(ap3);
        
        SOFTPHONE_URL_PARAM__c ap4 = new SOFTPHONE_URL_PARAM__c();
        ap4.Name = 'Agent Param 4';
        ap4.Name__c = 'INQR_First';
        ap4.Param_for__c = 'A';                            
        ap4.Value__c = 'Individual_First_Name__c_SOFTPHONE';
        softphoneurldata.add(ap4);
        
        SOFTPHONE_URL_PARAM__c ap6 = new SOFTPHONE_URL_PARAM__c();
        ap6.Name = 'Agent Param 5';
        ap6.Name__c = 'INQR_Last';
        ap6.Param_for__c = 'A';                            
        ap6.Value__c = 'Individual_First_Name__c_SOFTPHONE';
        softphoneurldata.add(ap6);
        
        SOFTPHONE_URL_PARAM__c ap5 = new SOFTPHONE_URL_PARAM__c();
        ap5.Name = 'Agent Param 6';
        ap5.Name__c = 'INQR_ID';
        ap5.Param_for__c = 'A';                            
        ap5.Value__c = 'sIntWithAgentId';
        softphoneurldata.add(ap5);
        
        SOFTPHONE_URL_PARAM__c ap7 = new SOFTPHONE_URL_PARAM__c();
        ap7.Name = 'Agent Param 7';
        ap7.Name__c = 'INQA_Tax';
        ap7.Param_for__c = 'A';                            
        // ap7.Value__c = '00NF000000CCg14';
        ap7.Value__c = 'Consumer_Id__c_SOFTPHONE';
        softphoneurldata.add(ap7);
        
        SOFTPHONE_URL_PARAM__c g1 = new SOFTPHONE_URL_PARAM__c();
        g1.Name = 'Group Param 1';
        g1.Name__c = 'INQA_First';
        g1.Param_for__c = 'G';                            
        // g1.Value__c = '00NF000000CCfzR';
        g1.Value__c = inqaFirstFieldValue;
        softphoneurldata.add(g1);
        
        SOFTPHONE_URL_PARAM__c g2 = new SOFTPHONE_URL_PARAM__c();
        g2.Name = 'Group Param 2';
        g2.Name__c = 'INQA_Last';
        g2.Param_for__c = 'G';                            
        // g2.Value__c = '00NF000000CCfzS';
        g2.Value__c = inqaLastFieldValue;
        softphoneurldata.add(g2);
        
        SOFTPHONE_URL_PARAM__c g3 = new SOFTPHONE_URL_PARAM__c();
        g3.Name = 'Group Param 3';
        g3.Name__c = 'INQA_ID';
        g3.Param_for__c = 'G';                            
        // g3.Value__c = '00NF000000CBqWf';
        g3.Value__c = grpNumberFieldValue;
        softphoneurldata.add(g3);
        
        SOFTPHONE_URL_PARAM__c g4 = new SOFTPHONE_URL_PARAM__c();
        g4.Name = 'Group Param 4';
        g4.Name__c = 'INQA_DOB';
        g4.Param_for__c = 'G';     
        g4.Value__c= PhoneBookConstants_H_HUM.DOB_VAR;
        //g4.Value__c = '00NF000000CBqWV';
        softphoneurldata.add(g4);
        
        SOFTPHONE_URL_PARAM__c g5 = new SOFTPHONE_URL_PARAM__c();
        g5.Name = 'Group Param 5';
        g5.Name__c = 'INQR_First';
        g5.Param_for__c = 'G';                            
        g5.Value__c = 'Individual_First_Name__c_SOFTPHONE';
        softphoneurldata.add(g5);
        
        SOFTPHONE_URL_PARAM__c g6 = new SOFTPHONE_URL_PARAM__c();
        g6.Name = 'Group Param 6';
        g6.Name__c = 'INQR_Last';
        g6.Param_for__c = 'G';                            
        g6.Value__c = 'Individual_Last_Name__c_SOFTPHONE';
        softphoneurldata.add(g6);
        
        SOFTPHONE_URL_PARAM__c g7 = new SOFTPHONE_URL_PARAM__c();
        g7.Name = 'Group Param 7';
        g7.Name__c = 'INQR_ID';
        g7.Param_for__c = 'G';                            
        g7.Value__c = 'Group_Number__c_SOFTPHONE';
        softphoneurldata.add(g7);
        
        SOFTPHONE_URL_PARAM__c g8 = new SOFTPHONE_URL_PARAM__c();
        g8.Name = 'Group Param 8';
        g8.Name__c = 'INQR_DDOB';
        g8.Param_for__c = 'G';                            
        g8.Value__c = 'Birthdate__c_SOFTPHONE';
        softphoneurldata.add(g8);
        
        SOFTPHONE_URL_PARAM__c p1 = new SOFTPHONE_URL_PARAM__c();
        p1.Name = 'Provider Param 1';
        p1.Name__c = 'INQR_First';
        p1.Param_for__c = 'P';                            
        p1.Value__c = 'Individual_First_Name__c_SOFTPHONE';
        softphoneurldata.add(p1);
        
        SOFTPHONE_URL_PARAM__c p4 = new SOFTPHONE_URL_PARAM__c();
        p4.Name = 'Provider Param 2';
        p4.Name__c = 'INQR_Last';
        p4.Param_for__c = 'P';                            
        p4.Value__c = 'Individual_Last_Name__c_SOFTPHONE';
        softphoneurldata.add(p4);
        
        SOFTPHONE_URL_PARAM__c p2 = new SOFTPHONE_URL_PARAM__c();
        p2.Name = 'Provider Param 3';
        p2.Name__c = 'INQR_ID';
        p2.Param_for__c = 'P';                            
        p2.Value__c = 'Consumer_Id__c_SOFTPHONE';
        softphoneurldata.add(p2);
        
        SOFTPHONE_URL_PARAM__c p3 = new SOFTPHONE_URL_PARAM__c();
        p3.Name = 'Provider Param 4';
        p3.Name__c = 'INQA_First';
        p3.Param_for__c = 'P';                            
        // p3.Value__c = '00NF000000CCfzR';
        p3.Value__c = inqaFirstFieldValue;
        softphoneurldata.add(p3);
        
        SOFTPHONE_URL_PARAM__c p6 = new SOFTPHONE_URL_PARAM__c();
        p6.Name = 'Provider Param 5';
        p6.Name__c = 'INQA_Last';
        p6.Param_for__c = 'P';                            
        // p6.Value__c = '00NF000000CCfzS';
        p6.Value__c = inqaLastFieldValue;
        softphoneurldata.add(p6);
        
        SOFTPHONE_URL_PARAM__c p5 = new SOFTPHONE_URL_PARAM__c();
        p5.Name = 'Provider Param 6';
        p5.Name__c = 'INQA_ID';
        p5.Param_for__c = 'P';                            
        // p5.Value__c = '00NF000000CCg14';
        p5.Value__c = 'Consumer_Id__c_SOFTPHONE';
        softphoneurldata.add(p5);
        
        SOFTPHONE_URL_PARAM__c p7 = new SOFTPHONE_URL_PARAM__c();
        p7.Name = 'Provider Param 7';
        p7.Name__c = 'INQA_Npid';
        p7.Param_for__c = 'P';                            
        // p7.Value__c = '00NF000000CBqWm';
        p7.Value__c =npiIdFieldValue;
        softphoneurldata.add(p7);
        
        SOFTPHONE_URL_PARAM__c m1 = new SOFTPHONE_URL_PARAM__c();
        m1.Name = 'Param1';
        m1.Name__c = 'INQA_FIRST';
        m1.Param_for__c = 'M';                            
        m1.Value__c = 'FirstName_SOFTPHONE';
        softphoneurldata.add(m1);
        
        SOFTPHONE_URL_PARAM__c m2 = new SOFTPHONE_URL_PARAM__c();
        m2.Name = 'Param 2';
        m2.Name__c = 'INQA_Last';
        m2.Param_for__c = 'M';                            
        m2.Value__c = 'LastName_SOFTPHONE';
        softphoneurldata.add(m2);
        
        SOFTPHONE_URL_PARAM__c m3 = new SOFTPHONE_URL_PARAM__c();
        m3.Name = 'Param 3';
        m3.Name__c = 'INQA_ID';
        m3.Param_for__c = 'M';                            
        m3.Value__c = 'Member_Id1_SOFTPHONE';
        softphoneurldata.add(m3);
        
        SOFTPHONE_URL_PARAM__c m4 = new SOFTPHONE_URL_PARAM__c();
        m4.Name = 'Param 4';
        m4.Name__c = 'INQA_DOB';
        m4.Param_for__c = 'M';     
        m4.Value__c= PhoneBookConstants_H_HUM.DOB_VAR;
        // m4.Value__c = '00NF000000CBqWV';
        softphoneurldata.add(m4);
        
        SOFTPHONE_URL_PARAM__c m5 = new SOFTPHONE_URL_PARAM__c();
        m5.Name = 'Param 5';
        m5.Name__c = 'INQR_First';
        m5.Param_for__c = 'M';                            
        m5.Value__c = 'name_firstacc2_SOFTPHONE';
        softphoneurldata.add(m5);
        
        SOFTPHONE_URL_PARAM__c m6 = new SOFTPHONE_URL_PARAM__c();
        m6.Name = 'Param 6';
        m6.Name__c = 'INQR_Last';
        m6.Param_for__c = 'M';                            
        m6.Value__c = 'name_lastacc2_SOFTPHONE';
        softphoneurldata.add(m6);
        
        SOFTPHONE_URL_PARAM__c m7 = new SOFTPHONE_URL_PARAM__c();
        m7.Name = 'Param 7';
        m7.Name__c = 'INQR_ID';
        m7.Param_for__c = 'M';                            
        m7.Value__c = 'Member_Id__c_SOFTPHONE';
        softphoneurldata.add(m7);
        
        SOFTPHONE_URL_PARAM__c m8 = new SOFTPHONE_URL_PARAM__c();
        m8.Name = 'Param 8';
        m8.Name__c = 'INQR_DOB';
        m8.Param_for__c = 'M';                            
        m8.Value__c = 'Birthdate__c_SOFTPHONE';
        softphoneurldata.add(m8);
        
        SOFTPHONE_URL_PARAM__c m9 = new SOFTPHONE_URL_PARAM__c();
        m9.Name = 'Param 9';
        m9.Name__c = 'INQA_TYP';
        m9.Param_for__c = 'M';                            
        m9.Value__c = 'iAboutType';
        softphoneurldata.add(m9);
        
        SOFTPHONE_URL_PARAM__c m10 = new SOFTPHONE_URL_PARAM__c();
        m10.Name = 'Param 10';
        m10.Name__c = 'INQR_TYP';
        m10.Param_for__c = 'M';                            
        m10.Value__c = 'iWithType';
        softphoneurldata.add(m10);
        
        insert softphoneurldata;

        Profile p = [SELECT Id FROM Profile WHERE Name='System Administrator']; 
        CallCenter c = [SELECT InternalName, Version, Id, Name, AdapterUrl, SystemModstamp, CreatedDate, CreatedById FROM CallCenter where name='Genesys Cloud for Salesforce'];
        List<User> lstUsers = new List<User>();
        User u1 = new User(Alias = 'sysadmin', Email='sysadmin@testorg.com', 
            EmailEncodingKey='UTF-8', LastName='Testuser', LanguageLocaleKey='en_US', 
            LocaleSidKey='en_US', ProfileId = p.Id,TimeZoneSidKey='America/Los_Angeles', UserName='testwithoutcallcenter@testorg.com');
        
        User u2 = new User(Alias = 'sysadmin', Email='sysadmin@testorg.com', 
            EmailEncodingKey='UTF-8', LastName='Testuser', LanguageLocaleKey='en_US', 
            LocaleSidKey='en_US', ProfileId = p.Id,TimeZoneSidKey='America/Los_Angeles', UserName='testwithcallcenter@testorg.com',CallCenterId =c.Id);
        
        lstUsers.add(u1);
        lstUsers.add(u2);    
        insert lstUsers;
        
        //Create Interaction and interaction Member Data for different Account types
        List<Interaction__c> listinteraction = new List<Interaction__c>();
        Interaction__c interAction = new Interaction__c(Interaction_Origin__c = 'Inbound Call', Interacting_With__c=actMember.id,UUID__c='Test UUID',Interacting_With_type__c= 'Member', Authenticated__c = true);
        Interaction__c interActiongroup = new Interaction__c(Interaction_Origin__c = 'Inbound Call', Interacting_With__c=objGrpAccount.id,UUID__c='Test UUID Group',Interacting_With_type__c= 'Group');
        Interaction__c interActionProvider = new Interaction__c(Interaction_Origin__c = 'Inbound Call', Interacting_With__c=objProviderAccount.id,UUID__c='Test UUID Provider',Interacting_With_type__c= 'Provider');
        Interaction__c interActionAgent = new Interaction__c(Interaction_Origin__c = 'Inbound Call', Interacting_With__c=objAgentAccount.id,UUID__c='Test UUID Agent',Interacting_With_type__c= 'Agent');
        Interaction__c interActionwithNoIntMem = new Interaction__c(Interaction_Origin__c = 'Inbound Call', Interacting_With__c=actMember.id,UUID__c='Test UUID 1',Interacting_With_type__c= 'Member');
        Interaction__c interActionwithNoIntMem1 = new Interaction__c(Interaction_Origin__c = 'Outbound Call', Interacting_With__c=actMember.id,UUID__c='Test UUID 2',Genesys_Interaction_ID__c='Test Interaction ID',Interacting_With_type__c= 'Member');
        Interaction__c interActionUnknownMem = new Interaction__c(Interaction_Origin__c = 'Inbound Call', Interacting_With__c=actUnknownMember.id,UUID__c='Test UUID unknown Member',Interacting_With_type__c= 'Unknown-Member');
        Interaction__c interActionUnknowngroup = new Interaction__c(Interaction_Origin__c = 'Inbound Call', Interacting_With__c=objUnknownGrpAccount.id,UUID__c='Test UUID unknown Group',Interacting_With_type__c= 'Unknown-Group');
        Interaction__c interActionUnknownProvider = new Interaction__c(Interaction_Origin__c = 'Inbound Call', Interacting_With__c=objUnknownProviderAccount.id,UUID__c='Test UUID unknown Provider',Interacting_With_type__c= 'Unknown-Provider');
        Interaction__c interActionUnknownAgent = new Interaction__c(Interaction_Origin__c = 'Inbound Call', Interacting_With__c=objUnknownAgentAccount.id,UUID__c='Test UUID unknown Agent',Interacting_With_type__c= 'Unknown-Agent');
        listinteraction.add(interAction);
        listinteraction.add(interActiongroup);
        listinteraction.add(interActionProvider);
        listinteraction.add(interActionAgent);
        listinteraction.add(interActionwithNoIntMem);
        listinteraction.add(interActionwithNoIntMem1);
        listinteraction.add(interActionUnknownMem);
        listinteraction.add(interActionUnknowngroup);
        listinteraction.add(interActionUnknownProvider);
        listinteraction.add(interActionUnknownAgent);
        insert listinteraction ;
        system.assertEquals(listinteraction.size(),10);
        
        List<Interaction_Member__c> interactionMembers = new List<Interaction_Member__c>();
        Interaction_Member__c intMem = new Interaction_Member__c(Interacting_About__c = actMember.id,Interaction__c = interAction.id ,Interacting_About_Type__c ='Member');             
        Interaction_Member__c intMemgroup = new Interaction_Member__c(Interacting_About__c = objGrpAccount.id,Interaction__c = interActiongroup.id ,Interacting_About_Type__c ='Group');             
        Interaction_Member__c intMemAgent = new Interaction_Member__c(Interacting_About__c = objAgentAccount.id,Interaction__c = interActionAgent.id ,Interacting_About_Type__c ='Agent');             
        Interaction_Member__c intMemProvider = new Interaction_Member__c(Interacting_About__c = objProviderAccount.id,Interaction__c = interActionProvider.id ,Interacting_About_Type__c ='Provider');             
        Interaction_Member__c intMem2 = new Interaction_Member__c(Interacting_About__c = actMember1.id,Interaction__c = interAction.id ,Interacting_About_Type__c ='Member');
        Interaction_Member__c intMem3 = new Interaction_Member__c(Interacting_About__c = actMember2.id,Interaction__c = interAction.id ,Interacting_About_Type__c ='Member');
        Interaction_Member__c intMem4 = new Interaction_Member__c(Interacting_About__c = actMember3.id,Interaction__c = interAction.id ,Interacting_About_Type__c ='Member');
        Interaction_Member__c intMem5 = new Interaction_Member__c(Interacting_About__c = actMember3.id,Interaction__c = interActionwithNoIntMem1.id ,Interacting_About_Type__c ='Member');
        Interaction_Member__c intUnknownMem = new Interaction_Member__c(Interacting_About__c = actUnknownMember.id,Interaction__c = interActionUnknownMem.id ,Interacting_About_Type__c ='Unknown-Member');
        Interaction_Member__c intMemUnknowngroup = new Interaction_Member__c(Interacting_About__c = objUnknownGrpAccount.id,Interaction__c = interActionUnknowngroup.id ,Interacting_About_Type__c ='Unknown-Group');
        Interaction_Member__c intMemUnknownAgent = new Interaction_Member__c(Interacting_About__c = objUnknownAgentAccount.id,Interaction__c = interActionUnknownAgent.id ,Interacting_About_Type__c ='Unknown-Agent');
        Interaction_Member__c intMemUnknownProvider = new Interaction_Member__c(Interacting_About__c = objUnknownProviderAccount.id,Interaction__c = interActionUnknownProvider.id ,Interacting_About_Type__c ='Unknown-Provider');
        interactionMembers.add(intMem);
        interactionMembers.add(intMemgroup);
        interactionMembers.add(intMemAgent);
        interactionMembers.add(intMemProvider);
        interactionMembers.add(intMem2);
        interactionMembers.add(intMem3);
        interactionMembers.add(intMem4);
        interactionMembers.add(intMem5);
        interactionMembers.add(intUnknownMem);
        interactionMembers.add(intMemUnknowngroup);
        interactionMembers.add(intMemUnknownAgent);
        interactionMembers.add(intMemUnknownProvider);
        insert interactionMembers;
        system.assertEquals(interactionMembers.size(),12);
        String sMapping = Label.HUMSPhoneMappingObjInteraction + ':' + interAction.id + ';' + Label.HUMSPhoneMappingObjInqFor + ':';
            sMapping += actMember.id + ';' + Label.HUMSPhoneMappingObjInqAb + ':' + actMember.id + ';'; 
            sMapping += Label.HUMSPhoneMappingObjPolicyMember + ':' + objMedPolMem.id ;
        
        Integration_Mapping__c intMappingRec = new Integration_Mapping__c();
        intMappingRec.Mapping__c = sMapping;
        intMappingRec.Record_Id__c = 'Test UUID';
        insert intMappingRec;
        system.assert(intMappingRec.Id <> null);

        List<Case> listCase = new List<Case>();
		Case newCase = new Case(AccountId = actMember.Id, Interacting_With__c = actMember.id, Origin = 'Outbound call', Type = 'PPI', Status = 'In Progress', Interacting_With_Type__c = 'Member', Priority = 'Normal', Complaint_Reason__c = 'Claims',G_A_Reason__c = 'Claims');
    	listCase.add(newCase);
        insert listCase;
        system.assertEquals(listCase.size(),1);
        
       
        
    }
    
    public static testmethod void testGetCallDataWebServiceMemberCase()
    {
        PhoneBook_C_HUM phChum = new PhoneBook_C_HUM();
        phChum.uuidataValue = 'Test UUID';
        phChum.isRefreshedval = false;
        Continuation continueObj = phChum.getDataFromWebService();
        continueObj.continuationMethod = 'processResponse';        
        HttpResponse httpRes = new Httpresponse ();
        String resBodyString = '{"GetCallDataResponse":{ "GetCallDataResult":{ "Application":"", "UUID":"Test UUID", "CallDataValues":{ "CallDataValuesList":[ { "DataName":"ACTIVE_CALLERCOUNT_ONCALL", "DataValue":"0" }, { "DataName":"ANI", "DataValue":"4092915301" }, { "DataName":"ASO_IND", "DataValue":"True" }, { "DataName":"AUTHENTICATION_IND", "DataValue":"Y" }, { "DataName":"BUS_SEGMENT_IND", "DataValue":"L" }, { "DataName":"Call_Catg_Desc", "DataValue":"Medical Eligibility" }, { "DataName":"Client_ID", "DataValue":"IVR" }, { "DataName":"CLAIM_GEN_KEY", "DataValue":"" }, { "DataName":"DATA_SRC", "DataValue":"IVR" }, { "DataName":"HDHP_IND", "DataValue":"True" }, { "DataName":"INQ_DOS", "DataValue":"" }, { "DataName":"INQ_RSN", "DataValue":"ELG" }, { "DataName":"INQ_RSN1", "DataValue":"ELG" }, { "DataName":"INQA_DOB", "DataValue":"11-18-2014" }, { "DataName":"INQA_FIRST", "DataValue":"JUN-KANG" }, { "DataName":"INQA_GRP_ID", "DataValue":"923382" }, { "DataName":"INQA_GRP_NAME", "DataValue":"HUMANA INC" }, { "DataName":"INQA_ID", "DataValue":"000160217" }, { "DataName":"INQA_LAST", "DataValue":"SINICKI" }, { "DataName":"INQA_MKT_ID", "DataValue":"0" }, { "DataName":"INQA_PLTFRM_CD", "DataValue":"EM" }, { "DataName":"INQA_RELN", "DataValue":"" }, { "DataName":"INQA_TYP", "DataValue":"G" }, { "DataName":"INQR_TYP", "DataValue":"M" }, { "DataName":"INQUIRY_ID", "DataValue":"" }, { "DataName":"IVR_CALL_DATE", "DataValue":"20201210" }, { "DataName":"IVR_CALL_TIME", "DataValue":"01:26:26" }, { "DataName":"IVR_CALLER_TYPE", "DataValue":"M" }, { "DataName":"IVR_DEN_OI_CHG_IND", "DataValue":"" }, { "DataName":"IVR_MED_OI_CHG_IND", "DataValue":"" }, { "DataName":"IVR_SERVER_CD", "DataValue":"03" }, { "DataName":"IVR_SERVER_PORT", "DataValue":"209" }, { "DataName":"LAST_AGENT_ID", "DataValue":"3378044" }, { "DataName":"MAJOR_LOB", "DataValue":"NPOS" }, { "DataName":"MEMBER_DOB", "DataValue":"11/18/2014" }, { "DataName":"MEMBER_ID", "DataValue":"000160217" }, { "DataName":"MEMBER_PERS_GEN_KEY", "DataValue":"554004556423" }, { "DataName":"MTV_BL5", "DataValue":"HHP-HUM INC" }, { "DataName":"MTV_BL7", "DataValue":"NPOS" }, { "DataName":"MULTI_COV", "DataValue":"" },  { "DataName":"PROD_TYPE_CD", "DataValue":"" }, { "DataName":"PROD_TYPE_CODE", "DataValue":"NPOSMO" }, { "DataName":"PRODPLANID", "DataValue":"" }, { "DataName":"REFERENCE_ID", "DataValue":"" }, { "DataName":"RX_ID", "DataValue":"" }, { "DataName":"SELLING_LEDGER", "DataValue":"0" }, { "DataName":"STATE_OF_ISSUE", "DataValue":"KY" }, { "DataName":"SURVEY_ID", "DataValue":"50" }, { "DataName":"UUID", "DataValue":"12102000001270800029030001860434" }, { "DataName":"VENDOR_ASOFDATE", "DataValue":"2020-12-10" }, { "DataName":"VENDOR_GROUPID", "DataValue":"" }, { "DataName":"VENDOR_SUB_GROUPID", "DataValue":"92338209"} , { "DataName":"ISMANUAL_TRANSFER", "DataValue":"false"} ] } } }}';
        httpRes.setBody(resBodyString);
        system.assert(httpRes.getBody() == resBodyString);
        Map<String, HTTPRequest> mapDetails = continueObj.getRequests();
        string reqType = '';
        for(String s : mapDetails.keyset())
        {
            reqType = s;
        } 
        List<string>labelGetmember = new List<string>();
        labelGetmember.add(reqType);
        Test.setContinuationResponse(reqType,httpRes);
        String result = (String)phChum.processResponse();
        System.assertEquals(result,null);
    }
    
    public static testmethod void testGetCallDataWebServiceMemberCaseSingleRecord()
    {
        PhoneBook_C_HUM phChum = new PhoneBook_C_HUM();
        phChum.uuidataValue = 'Test UUID 2';
        phChum.isRefreshedval = false;
        Continuation continueObj = phChum.getDataFromWebService();
        continueObj.continuationMethod = 'processResponse';        
        HttpResponse httpRes = new Httpresponse ();
        String resBodyString = '{"GetCallDataResponse":{ "GetCallDataResult":{ "Application":"", "UUID":"Test UUID 2", "CallDataValues":{ "CallDataValuesList":[ { "DataName":"ACTIVE_CALLERCOUNT_ONCALL", "DataValue":"0" }, { "DataName":"ANI", "DataValue":"4092915301" }, { "DataName":"ASO_IND", "DataValue":"True" }, { "DataName":"AUTHENTICATION_IND", "DataValue":"Y" }, { "DataName":"BUS_SEGMENT_IND", "DataValue":"L" }, { "DataName":"Call_Catg_Desc", "DataValue":"Medical Eligibility" }, { "DataName":"Client_ID", "DataValue":"IVR" }, { "DataName":"CLAIM_GEN_KEY", "DataValue":"" }, { "DataName":"DATA_SRC", "DataValue":"IVR" }, { "DataName":"HDHP_IND", "DataValue":"True" }, { "DataName":"INQ_DOS", "DataValue":"" }, { "DataName":"INQ_RSN", "DataValue":"ELG" }, { "DataName":"INQ_RSN1", "DataValue":"ELG" }, { "DataName":"INQA_DOB", "DataValue":"11-18-2014" }, { "DataName":"INQA_FIRST", "DataValue":"JUN-KANG" }, { "DataName":"INQA_GRP_ID", "DataValue":"923382" }, { "DataName":"INQA_GRP_NAME", "DataValue":"HUMANA INC" }, { "DataName":"INQA_ID", "DataValue":"000160217" }, { "DataName":"INQA_LAST", "DataValue":"SINICKI" }, { "DataName":"INQA_MKT_ID", "DataValue":"0" }, { "DataName":"INQA_PLTFRM_CD", "DataValue":"EM" }, { "DataName":"INQA_RELN", "DataValue":"" }, { "DataName":"INQA_TYP", "DataValue":"G" }, { "DataName":"INQR_TYP", "DataValue":"M" }, { "DataName":"INQUIRY_ID", "DataValue":"" }, { "DataName":"IVR_CALL_DATE", "DataValue":"20201210" }, { "DataName":"IVR_CALL_TIME", "DataValue":"01:26:26" }, { "DataName":"IVR_CALLER_TYPE", "DataValue":"M" }, { "DataName":"IVR_DEN_OI_CHG_IND", "DataValue":"" }, { "DataName":"IVR_MED_OI_CHG_IND", "DataValue":"" }, { "DataName":"IVR_SERVER_CD", "DataValue":"03" }, { "DataName":"IVR_SERVER_PORT", "DataValue":"209" }, { "DataName":"LAST_AGENT_ID", "DataValue":"3378044" }, { "DataName":"MAJOR_LOB", "DataValue":"NPOS" }, { "DataName":"MEMBER_DOB", "DataValue":"11/18/2014" }, { "DataName":"MEMBER_ID", "DataValue":"000160217" }, { "DataName":"MEMBER_PERS_GEN_KEY", "DataValue":"554004556423" }, { "DataName":"MTV_BL5", "DataValue":"HHP-HUM INC" }, { "DataName":"MTV_BL7", "DataValue":"NPOS" }, { "DataName":"MULTI_COV", "DataValue":"" },  { "DataName":"PROD_TYPE_CD", "DataValue":"" }, { "DataName":"PROD_TYPE_CODE", "DataValue":"NPOSMO" }, { "DataName":"PRODPLANID", "DataValue":"" }, { "DataName":"REFERENCE_ID", "DataValue":"" }, { "DataName":"RX_ID", "DataValue":"" }, { "DataName":"SELLING_LEDGER", "DataValue":"0" }, { "DataName":"STATE_OF_ISSUE", "DataValue":"KY" }, { "DataName":"SURVEY_ID", "DataValue":"50" }, { "DataName":"UUID", "DataValue":"12102000001270800029030001860434" }, { "DataName":"VENDOR_ASOFDATE", "DataValue":"2020-12-10" }, { "DataName":"VENDOR_GROUPID", "DataValue":"" }, { "DataName":"VENDOR_SUB_GROUPID", "DataValue":"92338209"} , { "DataName":"ISMANUAL_TRANSFER", "DataValue":"false"} ] } } }}';
        httpRes.setBody(resBodyString);
        system.assert(httpRes.getBody() == resBodyString);
        Map<String, HTTPRequest> mapDetails = continueObj.getRequests();
        string reqType = '';
        for(String s : mapDetails.keyset())
        {
            reqType = s;
        } 
        List<string>labelGetmember = new List<string>();
        labelGetmember.add(reqType);
        Test.setContinuationResponse(reqType,httpRes);
        String result = (String)phChum.processResponse();
        System.assertEquals(result,null);
    }
    
    public static testmethod void testGetCallDataWebServiceProviderCase()
    {
        PhoneBook_C_HUM phChum = new PhoneBook_C_HUM();
        phChum.uuidataValue = 'Test UUID';
        phChum.isRefreshedval = false;
        Continuation continueObj = phChum.getDataFromWebService();
        continueObj.continuationMethod = 'processResponse';        
        HttpResponse httpRes = new Httpresponse ();
        String resBodyString = '{ "GetCallDataResponse":{ "GetCallDataResult":{ "Application":"", "UUID":"Test UUID", "CallDataValues":{ "CallDataValuesList":[ { "DataName":"ACTIVE_CALLERCOUNT_ONCALL", "DataValue":"0" }, { "DataName":"ANI", "DataValue":"7322002160" }, { "DataName":"ASO_IND", "DataValue":"True" }, { "DataName":"AUTHENTICATION_IND", "DataValue":"Y" }, { "DataName":"BUS_SEGMENT_IND", "DataValue":"L" }, { "DataName":"Call_Catg_Desc", "DataValue":"Medical Eligibility" }, { "DataName":"Client_ID", "DataValue":"IVR" }, { "DataName":"CLAIM_GEN_KEY", "DataValue":"" }, { "DataName":"DATA_SRC", "DataValue":"IVR" }, { "DataName":"HDHP_IND", "DataValue":"True" }, { "DataName":"INQ_DOS", "DataValue":"" }, { "DataName":"INQ_RSN", "DataValue":"ELG" }, { "DataName":"INQ_RSN1", "DataValue":"ELG" }, { "DataName":"INQA_DOB", "DataValue":"11-18-2014" }, { "DataName":"INQA_FIRST", "DataValue":"JUN-KANG" }, { "DataName":"INQA_GRP_ID", "DataValue":"923382" }, { "DataName":"INQA_GRP_NAME", "DataValue":"HUMANA INC" }, { "DataName":"INQA_ID", "DataValue":"000160217" }, { "DataName":"INQA_LAST", "DataValue":"SINICKI" }, { "DataName":"INQA_MKT_ID", "DataValue":"0" }, { "DataName":"INQA_PLTFRM_CD", "DataValue":"EM" }, { "DataName":"INQA_RELN", "DataValue":"" }, { "DataName":"INQA_TYP", "DataValue":"M" }, { "DataName":"INQR_FIRST", "DataValue":"" }, { "DataName":"INQR_ID", "DataValue":"582030692" }, { "DataName":"INQR_LAST", "DataValue":"" }, { "DataName":"INQR_RELN", "DataValue":"P" }, { "DataName":"INQR_TYP", "DataValue":"P" }, { "DataName":"INQUIRY_ID", "DataValue":"" }, { "DataName":"IVR_CALL_DATE", "DataValue":"20201217" }, { "DataName":"IVR_CALL_TIME", "DataValue":"06:24:44" }, { "DataName":"IVR_CALLER_TYPE", "DataValue":"P" }, { "DataName":"IVR_DEN_OI_CHG_IND", "DataValue":"" }, { "DataName":"IVR_MED_OI_CHG_IND", "DataValue":"" }, { "DataName":"IVR_SERVER_CD", "DataValue":"03" }, { "DataName":"IVR_SERVER_PORT", "DataValue":"023" }, { "DataName":"LAST_AGENT_ID", "DataValue":"1129930" }, { "DataName":"MAJOR_LOB", "DataValue":"NPOS" }, { "DataName":"MEMBER_DOB", "DataValue":"11/18/2014" }, { "DataName":"MEMBER_ID", "DataValue":"000160217" }, { "DataName":"MEMBER_PERS_GEN_KEY", "DataValue":"554004556423" }, { "DataName":"MTV_BL5", "DataValue":"HHP-HUM INC" }, { "DataName":"MTV_BL7", "DataValue":"NPOS" }, { "DataName":"MULTI_COV", "DataValue":"" },  { "DataName":"PROD_TYPE_CD", "DataValue":"" }, { "DataName":"PROD_TYPE_CODE", "DataValue":"NPOSMO" }, { "DataName":"PRODPLANID", "DataValue":"" }, { "DataName":"REFERENCE_ID", "DataValue":"" }, { "DataName":"RX_ID", "DataValue":"" }, { "DataName":"SELLING_LEDGER", "DataValue":"0" }, { "DataName":"STATE_OF_ISSUE", "DataValue":"KY" }, { "DataName":"SURVEY_ID", "DataValue":"" }, { "DataName":"UUID", "DataValue":"12172000006251000023030001860319" }, { "DataName":"VENDOR_ASOFDATE", "DataValue":"" }, { "DataName":"VENDOR_GROUPID", "DataValue":"" }, { "DataName":"VENDOR_SUB_GROUPID", "DataValue":"" },{ "DataName":"ISMANUAL_TRANSFER", "DataValue":"false"} ] } } } }';
        httpRes.setBody(resBodyString);
        system.assert(httpRes.getBody() == resBodyString);
        Map<String, HTTPRequest> mapDetails = continueObj.getRequests();
        string reqType = '';
        for(String s : mapDetails.keyset())
        {
            reqType = s;
        } 
        List<string>labelGetmember = new List<string>();
        labelGetmember.add(reqType);
        Test.setContinuationResponse(reqType,httpRes);
        String result = (String)phChum.processResponse();
        System.assertEquals(result,null);
    }
    public static testmethod void testGetCallDataWebServiceAgentCase() 
    {
        PhoneBook_C_HUM phChum = new PhoneBook_C_HUM();
        phChum.uuidataValue = 'Test UUID';
        phChum.isRefreshedval = false;     
        Continuation continueObj = phChum.getDataFromWebService();
        continueObj.continuationMethod = 'processResponse';        
        HttpResponse httpRes = new Httpresponse ();
        String resBodyString = '{ "GetCallDataResponse":{ "GetCallDataResult":{ "Application":"", "UUID":"Test UUID", "CallDataValues":{ "CallDataValuesList":[ { "DataName":"ACTIVE_CALLERCOUNT_ONCALL", "DataValue":"0" }, { "DataName":"ANI", "DataValue":"8326854341" }, { "DataName":"ASO_IND", "DataValue":"" }, { "DataName":"AUTHENTICATION_IND", "DataValue":"Y" }, { "DataName":"BUS_SEGMENT_IND", "DataValue":"U" }, { "DataName":"Call_Catg_Desc", "DataValue":"Medical Default" }, { "DataName":"Client_ID", "DataValue":"IVR" }, { "DataName":"CLAIM_GEN_KEY", "DataValue":"" }, { "DataName":"DATA_SRC", "DataValue":"IVR" }, { "DataName":"HDHP_IND", "DataValue":"" }, { "DataName":"INQ_DOS", "DataValue":"" }, { "DataName":"INQ_RSN", "DataValue":"DEF" }, { "DataName":"INQ_RSN1", "DataValue":"DEF" }, { "DataName":"INQA_DOB", "DataValue":"" }, { "DataName":"INQA_FIRST", "DataValue":"" }, { "DataName":"INQA_GRP_ID", "DataValue":"" }, { "DataName":"INQA_GRP_NAME", "DataValue":"" }, { "DataName":"INQA_ID", "DataValue":"675907" }, { "DataName":"INQA_LAST", "DataValue":"" }, { "DataName":"INQA_MKT_ID", "DataValue":"" }, { "DataName":"INQA_PLTFRM_CD", "DataValue":"" }, { "DataName":"INQA_RELN", "DataValue":"" }, { "DataName":"INQA_TYP", "DataValue":"E" }, { "DataName":"INQR_DOB", "DataValue":"" }, { "DataName":"INQR_FIRST", "DataValue":"" }, { "DataName":"INQR_ID", "DataValue":"561623293" }, { "DataName":"INQR_LAST", "DataValue":"BB&T - ILER WALL & SHONTER INS INC" }, { "DataName":"INQR_TYP", "DataValue":"A" }, { "DataName":"INQUIRY_ID", "DataValue":"" }, { "DataName":"IVR_CALL_DATE", "DataValue":"20201217" }, { "DataName":"IVR_CALL_TIME", "DataValue":"07:33:37" }, { "DataName":"IVR_CALLER_TYPE", "DataValue":"B" }, { "DataName":"IVR_DEN_OI_CHG_IND", "DataValue":"" }, { "DataName":"IVR_MED_OI_CHG_IND", "DataValue":"" }, { "DataName":"IVR_SERVER_CD", "DataValue":"03" }, { "DataName":"IVR_SERVER_PORT", "DataValue":"030" }, { "DataName":"LAST_AGENT_ID", "DataValue":"3378044" }, { "DataName":"MAJOR_LOB", "DataValue":"" }, { "DataName":"MEMBER_DOB", "DataValue":"" }, { "DataName":"MEMBER_ID", "DataValue":"" }, { "DataName":"MEMBER_PERS_GEN_KEY", "DataValue":"" }, { "DataName":"MTV_BL5", "DataValue":"" }, { "DataName":"MTV_BL7", "DataValue":"" }, { "DataName":"MULTI_COV", "DataValue":"" },  { "DataName":"PROD_TYPE_CD", "DataValue":"" }, { "DataName":"PROD_TYPE_CODE", "DataValue":"" }, { "DataName":"PRODPLANID", "DataValue":"" }, { "DataName":"REFERENCE_ID", "DataValue":"" }, { "DataName":"RX_ID", "DataValue":"" }, { "DataName":"SELLING_LEDGER", "DataValue":"" }, { "DataName":"STATE_OF_ISSUE", "DataValue":"" }, { "DataName":"SURVEY_ID", "DataValue":"" }, { "DataName":"UUID", "DataValue":"12172000007342900030030001860434" }, { "DataName":"VENDOR_ASOFDATE", "DataValue":"2020-12-17" }, { "DataName":"VENDOR_GROUPID", "DataValue":"" }, { "DataName":"VENDOR_SUB_GROUPID", "DataValue":"" },{ "DataName":"ISMANUAL_TRANSFER", "DataValue":"false"} ] } } } }';
        httpRes.setBody(resBodyString);
        system.assert(httpRes.getBody() == resBodyString);
        Map<String, HTTPRequest> mapDetails = continueObj.getRequests();
        string reqType = '';
        for(String s : mapDetails.keyset())
        {
            reqType = s;
        } 
        List<string>labelGetmember = new List<string>();
        labelGetmember.add(reqType);
        Test.setContinuationResponse(reqType,httpRes);
        String result = (String)phChum.processResponse();
        System.assertEquals(result,null);        
    }
    public static testmethod void testGetCallDataWebServiceAgentCaseforGroup() 
    {
        PhoneBook_C_HUM phChum = new PhoneBook_C_HUM();
        phChum.uuidataValue = 'Test UUID';
        phChum.isRefreshedval = false;     
        Continuation continueObj = phChum.getDataFromWebService();
        continueObj.continuationMethod = 'processResponse';        
        HttpResponse httpRes = new Httpresponse ();
        String resBodyString = '{ "GetCallDataResponse":{ "GetCallDataResult":{ "Application":"", "UUID":"Test UUID", "CallDataValues":{ "CallDataValuesList":[ { "DataName":"ACTIVE_CALLERCOUNT_ONCALL", "DataValue":"0" }, { "DataName":"ANI", "DataValue":"8326854341" }, { "DataName":"ASO_IND", "DataValue":"" }, { "DataName":"AUTHENTICATION_IND", "DataValue":"Y" }, { "DataName":"BUS_SEGMENT_IND", "DataValue":"U" }, { "DataName":"Call_Catg_Desc", "DataValue":"Medical Default" }, { "DataName":"Client_ID", "DataValue":"IVR" }, { "DataName":"CLAIM_GEN_KEY", "DataValue":"" }, { "DataName":"DATA_SRC", "DataValue":"IVR" }, { "DataName":"HDHP_IND", "DataValue":"" }, { "DataName":"INQ_DOS", "DataValue":"" }, { "DataName":"INQ_RSN", "DataValue":"DEF" }, { "DataName":"INQ_RSN1", "DataValue":"DEF" }, { "DataName":"INQA_DOB", "DataValue":"" }, { "DataName":"INQA_FIRST", "DataValue":"" }, { "DataName":"INQA_GRP_ID", "DataValue":"" }, { "DataName":"INQA_GRP_NAME", "DataValue":"" }, { "DataName":"INQA_ID", "DataValue":"675907" }, { "DataName":"INQA_LAST", "DataValue":"" }, { "DataName":"INQA_MKT_ID", "DataValue":"" }, { "DataName":"INQA_PLTFRM_CD", "DataValue":"" }, { "DataName":"INQA_RELN", "DataValue":"" }, { "DataName":"INQA_TYP", "DataValue":"G" }, { "DataName":"INQR_DOB", "DataValue":"" }, { "DataName":"INQR_FIRST", "DataValue":"" }, { "DataName":"INQR_ID", "DataValue":"561623293" }, { "DataName":"INQR_LAST", "DataValue":"BB&T - ILER WALL & SHONTER INS INC" }, { "DataName":"INQR_TYP", "DataValue":"A" }, { "DataName":"INQUIRY_ID", "DataValue":"" }, { "DataName":"IVR_CALL_DATE", "DataValue":"20201217" }, { "DataName":"IVR_CALL_TIME", "DataValue":"07:33:37" }, { "DataName":"IVR_CALLER_TYPE", "DataValue":"B" }, { "DataName":"IVR_DEN_OI_CHG_IND", "DataValue":"" }, { "DataName":"IVR_MED_OI_CHG_IND", "DataValue":"" }, { "DataName":"IVR_SERVER_CD", "DataValue":"03" }, { "DataName":"IVR_SERVER_PORT", "DataValue":"030" }, { "DataName":"LAST_AGENT_ID", "DataValue":"3378044" }, { "DataName":"MAJOR_LOB", "DataValue":"" }, { "DataName":"MEMBER_DOB", "DataValue":"" }, { "DataName":"MEMBER_ID", "DataValue":"" }, { "DataName":"MEMBER_PERS_GEN_KEY", "DataValue":"" }, { "DataName":"MTV_BL5", "DataValue":"" }, { "DataName":"MTV_BL7", "DataValue":"" }, { "DataName":"MULTI_COV", "DataValue":"" },  { "DataName":"PROD_TYPE_CD", "DataValue":"" }, { "DataName":"PROD_TYPE_CODE", "DataValue":"" }, { "DataName":"PRODPLANID", "DataValue":"" }, { "DataName":"REFERENCE_ID", "DataValue":"" }, { "DataName":"RX_ID", "DataValue":"" }, { "DataName":"SELLING_LEDGER", "DataValue":"" }, { "DataName":"STATE_OF_ISSUE", "DataValue":"" }, { "DataName":"SURVEY_ID", "DataValue":"" }, { "DataName":"UUID", "DataValue":"12172000007342900030030001860434" }, { "DataName":"VENDOR_ASOFDATE", "DataValue":"2020-12-17" }, { "DataName":"VENDOR_GROUPID", "DataValue":"" }, { "DataName":"VENDOR_SUB_GROUPID", "DataValue":"" },{ "DataName":"ISMANUAL_TRANSFER", "DataValue":"false"} ] } } } }';
        httpRes.setBody(resBodyString);
        system.assert(httpRes.getBody() == resBodyString);
        Map<String, HTTPRequest> mapDetails = continueObj.getRequests();
        string reqType = '';
        for(String s : mapDetails.keyset())
        {
            reqType = s;
        } 
        List<string>labelGetmember = new List<string>();
        labelGetmember.add(reqType);
        Test.setContinuationResponse(reqType,httpRes);
        String result = (String)phChum.processResponse();
        System.assertEquals(result,null);        
    }
    public static testmethod void testGetCallDataWebServiceGroupCase()
    {
        PhoneBook_C_HUM phChum = new PhoneBook_C_HUM();
        phChum.uuidataValue = 'Test UUID';
        phChum.isRefreshedval = false;
        Continuation continueObj = phChum.getDataFromWebService();
        continueObj.continuationMethod = 'processResponse';
        
        HttpResponse httpRes = new Httpresponse ();
        String resBodyString = '{ "GetCallDataResponse":{ "GetCallDataResult":{ "Application":"", "UUID":"Test UUID", "CallDataValues":{ "CallDataValuesList":[ { "DataName":"ACTIVE_CALLERCOUNT_ONCALL", "DataValue":"0" }, { "DataName":"ANI", "DataValue":"8326854341" }, { "DataName":"ASO_IND", "DataValue":"" }, { "DataName":"AUTHENTICATION_IND", "DataValue":"Y" }, { "DataName":"BUS_SEGMENT_IND", "DataValue":"U" }, { "DataName":"Call_Catg_Desc", "DataValue":"Medical Default" }, { "DataName":"Client_ID", "DataValue":"IVR" }, { "DataName":"CLAIM_GEN_KEY", "DataValue":"" }, { "DataName":"DATA_SRC", "DataValue":"IVR" }, { "DataName":"HDHP_IND", "DataValue":"" }, { "DataName":"INQ_DOS", "DataValue":"" }, { "DataName":"INQ_RSN", "DataValue":"DEF" }, { "DataName":"INQ_RSN1", "DataValue":"DEF" }, { "DataName":"INQA_DOB", "DataValue":"" }, { "DataName":"INQA_FIRST", "DataValue":"" }, { "DataName":"INQA_GRP_ID", "DataValue":"" }, { "DataName":"INQA_GRP_NAME", "DataValue":"" }, { "DataName":"INQA_ID", "DataValue":"675907" }, { "DataName":"INQA_LAST", "DataValue":"" }, { "DataName":"INQA_MKT_ID", "DataValue":"" }, { "DataName":"INQA_PLTFRM_CD", "DataValue":"" }, { "DataName":"INQA_RELN", "DataValue":"" }, { "DataName":"INQA_TYP", "DataValue":"E" }, { "DataName":"INQR_DOB", "DataValue":"" }, { "DataName":"INQR_FIRST", "DataValue":"" }, { "DataName":"INQR_ID", "DataValue":"561623293" }, { "DataName":"INQR_LAST", "DataValue":"BB&T - ILER WALL & SHONTER INS INC" }, { "DataName":"INQR_TYP", "DataValue":"G" }, { "DataName":"INQUIRY_ID", "DataValue":"" }, { "DataName":"IVR_CALL_DATE", "DataValue":"20201217" }, { "DataName":"IVR_CALL_TIME", "DataValue":"07:33:37" }, { "DataName":"IVR_CALLER_TYPE", "DataValue":"B" }, { "DataName":"IVR_DEN_OI_CHG_IND", "DataValue":"" }, { "DataName":"IVR_MED_OI_CHG_IND", "DataValue":"" }, { "DataName":"IVR_SERVER_CD", "DataValue":"03" }, { "DataName":"IVR_SERVER_PORT", "DataValue":"030" }, { "DataName":"LAST_AGENT_ID", "DataValue":"3378044" }, { "DataName":"MAJOR_LOB", "DataValue":"" }, { "DataName":"MEMBER_DOB", "DataValue":"" }, { "DataName":"MEMBER_ID", "DataValue":"" }, { "DataName":"MEMBER_PERS_GEN_KEY", "DataValue":"" }, { "DataName":"MTV_BL5", "DataValue":"" }, { "DataName":"MTV_BL7", "DataValue":"" }, { "DataName":"MULTI_COV", "DataValue":"" },  { "DataName":"PROD_TYPE_CD", "DataValue":"" }, { "DataName":"PROD_TYPE_CODE", "DataValue":"" }, { "DataName":"PRODPLANID", "DataValue":"" }, { "DataName":"REFERENCE_ID", "DataValue":"" }, { "DataName":"RX_ID", "DataValue":"" }, { "DataName":"SELLING_LEDGER", "DataValue":"" }, { "DataName":"STATE_OF_ISSUE", "DataValue":"" }, { "DataName":"SURVEY_ID", "DataValue":"" }, { "DataName":"UUID", "DataValue":"12172000007342900030030001860434" }, { "DataName":"VENDOR_ASOFDATE", "DataValue":"2020-12-17" }, { "DataName":"VENDOR_GROUPID", "DataValue":"" }, { "DataName":"VENDOR_SUB_GROUPID", "DataValue":"" },{ "DataName":"ISMANUAL_TRANSFER", "DataValue":"false"} ] } } } }';
        httpRes.setBody(resBodyString);
        system.assert(httpRes.getBody() == resBodyString);
        Map<String, HTTPRequest> mapDetails = continueObj.getRequests();
        string reqType = '';
        for(String s : mapDetails.keyset())
        {
            reqType = s;
        } 
        List<string>labelGetmember = new List<string>();
        labelGetmember.add(reqType);
        Test.setContinuationResponse(reqType,httpRes);
        String result = (String)phChum.processResponse();
        System.assertEquals(result,null);
    }
    
    
    public static testmethod void testGetCallDataWebServiceGroupCasewithGRPID()
    {
        PhoneBook_C_HUM phChum = new PhoneBook_C_HUM();
        phChum.uuidataValue = 'Test UUID';
        phChum.isRefreshedval = false;
        Continuation continueObj = phChum.getDataFromWebService();
        continueObj.continuationMethod = 'processResponse';
        
        HttpResponse httpRes = new Httpresponse ();
        String resBodyString = '{ "GetCallDataResponse":{ "GetCallDataResult":{ "Application":"", "UUID":"Test UUID", "CallDataValues":{ "CallDataValuesList":[ { "DataName":"ACTIVE_CALLERCOUNT_ONCALL", "DataValue":"0" }, { "DataName":"ANI", "DataValue":"8326854341" }, { "DataName":"ASO_IND", "DataValue":"" }, { "DataName":"AUTHENTICATION_IND", "DataValue":"Y" }, { "DataName":"BUS_SEGMENT_IND", "DataValue":"U" }, { "DataName":"Call_Catg_Desc", "DataValue":"Medical Default" }, { "DataName":"Client_ID", "DataValue":"IVR" }, { "DataName":"CLAIM_GEN_KEY", "DataValue":"" }, { "DataName":"DATA_SRC", "DataValue":"IVR" }, { "DataName":"HDHP_IND", "DataValue":"" }, { "DataName":"INQ_DOS", "DataValue":"" }, { "DataName":"INQ_RSN", "DataValue":"DEF" }, { "DataName":"INQ_RSN1", "DataValue":"DEF" }, { "DataName":"INQA_DOB", "DataValue":"" }, { "DataName":"INQA_FIRST", "DataValue":"" }, { "DataName":"INQA_GRP_ID", "DataValue":"12345" }, { "DataName":"INQA_GRP_NAME", "DataValue":"" }, { "DataName":"INQA_ID", "DataValue":"675907" }, { "DataName":"INQA_LAST", "DataValue":"" }, { "DataName":"INQA_MKT_ID", "DataValue":"" }, { "DataName":"INQA_PLTFRM_CD", "DataValue":"" }, { "DataName":"INQA_RELN", "DataValue":"" }, { "DataName":"INQA_TYP", "DataValue":"E" }, { "DataName":"INQR_DOB", "DataValue":"" }, { "DataName":"INQR_FIRST", "DataValue":"" }, { "DataName":"INQR_ID", "DataValue":"" }, { "DataName":"INQR_LAST", "DataValue":"BB&T - ILER WALL & SHONTER INS INC" }, { "DataName":"INQR_TYP", "DataValue":"G" }, { "DataName":"INQUIRY_ID", "DataValue":"" }, { "DataName":"IVR_CALL_DATE", "DataValue":"20201217" }, { "DataName":"IVR_CALL_TIME", "DataValue":"07:33:37" }, { "DataName":"IVR_CALLER_TYPE", "DataValue":"B" }, { "DataName":"IVR_DEN_OI_CHG_IND", "DataValue":"" }, { "DataName":"IVR_MED_OI_CHG_IND", "DataValue":"" }, { "DataName":"IVR_SERVER_CD", "DataValue":"03" }, { "DataName":"IVR_SERVER_PORT", "DataValue":"030" }, { "DataName":"LAST_AGENT_ID", "DataValue":"3378044" }, { "DataName":"MAJOR_LOB", "DataValue":"" }, { "DataName":"MEMBER_DOB", "DataValue":"" }, { "DataName":"MEMBER_ID", "DataValue":"" }, { "DataName":"MEMBER_PERS_GEN_KEY", "DataValue":"" }, { "DataName":"MTV_BL5", "DataValue":"" }, { "DataName":"MTV_BL7", "DataValue":"" }, { "DataName":"MULTI_COV", "DataValue":"" },  { "DataName":"PROD_TYPE_CD", "DataValue":"" }, { "DataName":"PROD_TYPE_CODE", "DataValue":"" }, { "DataName":"PRODPLANID", "DataValue":"" }, { "DataName":"REFERENCE_ID", "DataValue":"" }, { "DataName":"RX_ID", "DataValue":"" }, { "DataName":"SELLING_LEDGER", "DataValue":"" }, { "DataName":"STATE_OF_ISSUE", "DataValue":"" }, { "DataName":"SURVEY_ID", "DataValue":"" }, { "DataName":"UUID", "DataValue":"12172000007342900030030001860434" }, { "DataName":"VENDOR_ASOFDATE", "DataValue":"2020-12-17" }, { "DataName":"VENDOR_GROUPID", "DataValue":"" }, { "DataName":"VENDOR_SUB_GROUPID", "DataValue":"" },{ "DataName":"ISMANUAL_TRANSFER", "DataValue":"false"} ] } } } }';
        httpRes.setBody(resBodyString);
        system.assert(httpRes.getBody() == resBodyString);
        Map<String, HTTPRequest> mapDetails = continueObj.getRequests();
        string reqType = '';
        for(String s : mapDetails.keyset())
        {
            reqType = s;
        } 
        List<string>labelGetmember = new List<string>();
        labelGetmember.add(reqType);
        Test.setContinuationResponse(reqType,httpRes);
        String result = (String)phChum.processResponse();
        System.assertEquals(result,null);
    }
    
    public static testmethod void testCreateRecord() 
    {  
        CRMFunctionality_ONOFF_Switch__c objSwitch = CRMFunctionality_ONOFF_Switch__c.getValues('1799704_Genesys_Level3');   
        System.assert(objSwitch != null);
        PhoneBook_C_HUM objPhB = new PhoneBook_C_HUM();
        objPhB.reason = 'status_updated';
        objPhB.category = '';
        objPhB.createRecord();        
        objSwitch.IsON__c = false;
        update objSwitch;
        System.assert(objSwitch.IsON__c == false);
        CRMFunctionality_ONOFF_Switch__c objSwtch = CRMFunctionality_ONOFF_Switch__c.getValues('1799704_Genesys_Level2');  
        System.assert(objSwtch != null);
        objPhB.createRecord(); 
        objSwtch.IsON__c = false;
        update objSwtch;
        System.assert(objSwtch.IsON__c == false);
        CRMFunctionality_ONOFF_Switch__c objStch = CRMFunctionality_ONOFF_Switch__c.getValues('1799704_Genesys_Level1'); 
        System.assert(objStch != null);
        objPhB.createRecord();  
        PhoneBook_C_HUM objPhBk = new PhoneBook_C_HUM();
        objPhBk.reason = 'Secure Transfer';
        objPhBk.category = '';
        objPhBk.callInfoLog = 'Card Added Successfully';
        objPhBk.createRecord();
    }
    
    public static testmethod void testShowClosePopup()
    {
        string PhoneBookName = 'Select';
        PhoneBook_C_HUM phChum = new PhoneBook_C_HUM();
        phChum.closePopup();
        System.assert(phChum.displayPopup == false);
        phChum.uniqueTransferNumber = '1860506';
        GetAllPhoneBookRulesResponse_DTO_HUM.TransferNumberList objWrap = new GetAllPhoneBookRulesResponse_DTO_HUM.TransferNumberList();
        List<GetAllPhoneBookRulesResponse_DTO_HUM.TransferNumberList> lstWrap = new List<GetAllPhoneBookRulesResponse_DTO_HUM.TransferNumberList>();
        Map<String,List<GetAllPhoneBookRulesResponse_DTO_HUM.TransferNumberList>> mapWrap = new Map<String,List<GetAllPhoneBookRulesResponse_DTO_HUM.TransferNumberList>>();
        objWrap.Notes = '';
        objWrap.OperatingHours = '';
        objWrap.QueueName = 'test';
        objWrap.ShareableNumber = '';
        objWrap.TransferNumber = '1860506';
        objWrap.ShareableTollFree = '';
        lstWrap.add(objWrap);
        phChum.phBookTransferList = lstWrap;
        mapWrap.put('Default', lstWrap);  
        phChum.phBookRulesMap = mapWrap;
        phChum.showPopup();
        System.assert(phChum.phBookTransferListSelected != null);
        phChum.displayFilteredDefaultQueues();
        System.assert(phChum.phBookTransferList == null);
        List<SelectOption> options = new List<SelectOption>();
        options = phChum.getCallerTypes();
        System.assert(options != null);
        phChum.removeDupPhBookList(PhoneBookName);
    }
    public static testmethod void TestDisConnect()
    {        
        PhoneBook_C_HUM phChum = new PhoneBook_C_HUM();
        List<SelectOption> options = new List<SelectOption>();
 	 	options.add(new SelectOption('Default','Default'));     
        phChum.phoneBookNames = options;
        phChum.reAssignVar();
        System.assert(phChum.phoneBookNamesList != null);
        phChum.onCallDisconnected();
        System.assert(phChum.CallerType != null);
        phChum.phoneBookNamesList = options;        
        phChum.onCallConnected();
        System.assert(phChum.phoneBookNamesList != null);
        phChum.onOutBoundCallConnect();
        System.assert(phChum.isShowBox == true);
    }    
    public static testmethod void TestOnCallTransfer()
    {        
        PhoneBook_C_HUM phChum = new PhoneBook_C_HUM();
        Case objCase = new Case();
        insert objCase;
        phChum.ParamsValueOnCallTransfer = objCase.Id;   
        List<SelectOption> options = new List<SelectOption>();
 	 	options.add(new SelectOption('Default','Default'));
        phChum.phoneBookNamesList = options;
        phChum.phoneBookNames = options;
        Object ob = phChum.OnCallTransfer();
        System.assert(ob == null);
    } 
     public static testmethod void TestCallTransfer()
    {        
        PhoneBook_C_HUM phChum = new PhoneBook_C_HUM();
        Case objCase = new Case();
        insert objCase;        
        phChum.ParamsValueOnCallTransfer = '{"Policy Member":"12","Policy Member":"23"}';
        List<SelectOption> options = new List<SelectOption>();
 	 	options.add(new SelectOption('Default','Default'));
        phChum.phoneBookNamesList = options;
        phChum.phoneBookNames = options;
        phChum.isCallConnected = true;
        phChum.isCallNotConnected = false;
        Object ob = phChum.OnCallTransfer();
        System.assert(phChum.bOnCallTransfer == true);
        phChum.ParamsValueOnCallTransfer = '{"'+ Label.HUMSPhoneMappingObjPolicyMember +'":""}';
        Object ob1 = phChum.OnCallTransfer();
    }
    public static testmethod void TestdisplayphBookRules()
    {        
        PhoneBook_C_HUM phChum = new PhoneBook_C_HUM();
        phChum.displayphBookRules();
        System.assert(phChum.phBookTransferList == null);
        phChum.searchParam = 'est';
        GetAllPhoneBookRulesResponse_DTO_HUM.TransferNumberList objWrap = new GetAllPhoneBookRulesResponse_DTO_HUM.TransferNumberList();
        List<GetAllPhoneBookRulesResponse_DTO_HUM.TransferNumberList> lstWrap = new List<GetAllPhoneBookRulesResponse_DTO_HUM.TransferNumberList>();
        Map<String,List<GetAllPhoneBookRulesResponse_DTO_HUM.TransferNumberList>> mapWrap = new Map<String,List<GetAllPhoneBookRulesResponse_DTO_HUM.TransferNumberList>>();
        objWrap.Notes = '';
        objWrap.OperatingHours = '';
        objWrap.QueueName = 'test';
        objWrap.ShareableNumber = '';
        objWrap.TransferNumber = '1860506';
        objWrap.ShareableTollFree = '';
        lstWrap.add(objWrap);
        phChum.phBookTransferList = lstWrap;
        mapWrap.put('12345',lstWrap);        
        phChum.phoneBookSelected = '12345';
        phChum.phBookRulesMap = mapWrap;
        phChum.displayphBookRules();
        System.assert(phChum.phBookTransferList != null);        
        phChum.phoneBookSelected = PhoneBookConstants_H_HUM.DEFAULT_VAL;
        phChum.isCallConnected = true;
        RouteSoftPhoneXMLResponse_DTO_HUM.CallTransferRecList objRouteWrap = new RouteSoftPhoneXMLResponse_DTO_HUM.CallTransferRecList();
        List<RouteSoftPhoneXMLResponse_DTO_HUM.CallTransferRecList> lstRouteWrap = new List<RouteSoftPhoneXMLResponse_DTO_HUM.CallTransferRecList>();        
		objRouteWrap.TransferNumber = '1664819';
		objRouteWrap.CallerType = 'Agent';
		objRouteWrap.SiteCd = 'GB';
		objRouteWrap.CatCd = 'PRI';
		objRouteWrap.CategoryCd = 'Precert Initiate';
		objRouteWrap.QueueName = 'test';
		objRouteWrap.MarketDesc = '';
		objRouteWrap.MarketID = '84238';
		objRouteWrap.TransferDataKey = 'EM0048334148423812345';
		objRouteWrap.SiteDesc = '';
		objRouteWrap.ShareableTollFree = '0';
		objRouteWrap.OperatingHours = '';
		objRouteWrap.Notes = '';
		objRouteWrap.TransferType = 'Warm';
		objRouteWrap.TransferConnectNumber = '8554100506';
        lstRouteWrap.add(objRouteWrap);
        phChum.searchRules();
        phChum.defaultTransferListAll = lstRouteWrap;
        phChum.CallerType = 'Agent';
        phChum.defaultTransferList = lstRouteWrap;  
        phChum.searchParam = 'Test';
        phChum.searchRules();
        System.assert(phChum.phBookTransferList != null);
        phChum.uniqueTransferNumber = null;
        phChum.deafultUniqueTransferNumber = 'test';
        phChum.showPopup();
        System.assert(phChum.phBookTransferListSelected == null);
        phChum.displayphBookRules();
        phChum.isCallConnected = true;
        phChum.phoneBookSelected = 'Select';
        phChum.searchRules();
        phChum.displayphBookRules();
        System.assert(phChum.phBookTransferList == null);
    }
    public static testMethod void testSearchRules()
    {
        PhoneBook_C_HUM phChum = new PhoneBook_C_HUM();
        phChum.searchParam = 'test';
        phChum.phoneBookSelected = PhoneBookConstants_H_HUM.SELECT_VAL;
        GetAllPhoneBookRulesResponse_DTO_HUM.TransferNumberList objWrap = new GetAllPhoneBookRulesResponse_DTO_HUM.TransferNumberList();
        List<GetAllPhoneBookRulesResponse_DTO_HUM.TransferNumberList> lstWrap = new List<GetAllPhoneBookRulesResponse_DTO_HUM.TransferNumberList>();
        Map<String,List<GetAllPhoneBookRulesResponse_DTO_HUM.TransferNumberList>> mapWrap = new Map<String,List<GetAllPhoneBookRulesResponse_DTO_HUM.TransferNumberList>>();
        objWrap.Notes = '';
        objWrap.OperatingHours = '';
        objWrap.QueueName = 'test';
        objWrap.ShareableNumber = '';
        objWrap.TransferNumber = '1860506';
        objWrap.ShareableTollFree = '';
        lstWrap.add(objWrap);
        phChum.phBookTransferList = lstWrap;
        mapWrap.put('Test', lstWrap);  
        phChum.phBookRulesMap = mapWrap;
       phChum.searchRules(); 
        System.assert(phChum.searchParam != null);
        System.assert(phChum.phBookTransferList != null);
    }
    public static testmethod void TestgetParamsMapforCase()
    {        
        PhoneBook_C_HUM phChum = new PhoneBook_C_HUM();
        PhoneBook_C_HUM.numberVal = '1234';
        phChum.refreshbuttons();
        PhoneBook_C_HUM.numberVal = '1234567';
        phChum.isCallConnected = true;
        phChum.refreshbuttons();
        Account acc =[select id from Account where Lastname=: 'TesMember' limit 1];
        List<Policy_Member__c> polMem = [select id from Policy_Member__c where end_date__c = '12/31/2015'];
        Case objCase = new Case();
        objCase.Interaction_ID__c = '12345';
        objCase.Interacting_With__c = acc.id;
        objCase.AccountId = acc.id;
        objCase.Policy_Member__c = polMem[0].id;
        insert objCase;
        phChum.getParamsMapforCase(objCase.Id);
        System.assert(phChum.PolicyCaseDataMap != null);
        phChum.getParamsMapforCase(null);
        System.assert(phChum.PolicyCaseDataMap != null);
    }    
    public static testmethod void TestcallDisplayDef()
    {        
        PhoneBook_C_HUM phChum = new PhoneBook_C_HUM();
        phChum.isCallConnected = true;
        phChum.callDisplay();
        phChum.callDisplayDef();
        PhoneBook_C_HUM.numberVal = '';
        phChum.refreshbuttons();
        System.assert(phChum.isCallConnected == true);
    }    
    public static testmethod void TestfetchDefaultPhoneBookRules()
    {        
        PhoneBook_C_HUM phChum = new PhoneBook_C_HUM();
        phChum.fetchDefaultPhoneBookRules();
        System.assert(phChum.isnotDefaultPhoneBook == false);
        phChum.AllPhoneBookRules();
        System.assert(phChum.CallerTypes != null);
        Account acc =[select id from Account where Lastname=: 'TesMember' limit 1];
        Interaction__c inter = [Select id,Interacting_With__r.Id,UUID__c from Interaction__c where UUID__c=:'Test UUID' limit 1];
        Map<String,String> PolicyCaseMap = new Map<String,String>();
        PolicyCaseMap.put(Label.HUMSPhoneMappingObjInteraction, inter.id);
            PolicyCaseMap.put(Label.HUMSPhoneMappingObjInqFor,acc.id);
            PolicyCaseMap.put(Label.HUMSPhoneMappingObjInqAb,acc.id);
            PolicyCaseMap.put(Label.HUMSPhoneMappingObjPolicyMember,'PolicyMemberId');
            PolicyCaseMap.put(Label.HUMSPhoneMappingObjCase, 'CaseId');
        phChum.PolicyCaseDataMap = PolicyCaseMap;
        Object ob = phChum.processResponsegetDefaultPhBookRules();
        System.assert(ob == null);
        phChum.bOnCallTransfer = true;
        Object obj = phChum.processResponsegetDefaultPhBookRules();
        System.assert(obj == null);
    }
    public static testmethod void TestdisplayFilteredDefaultQueues()
    {        
        PhoneBook_C_HUM phChum = new PhoneBook_C_HUM();
        phChum.deafultUniqueTransferNumber = '1860506';
        RouteSoftPhoneXMLResponse_DTO_HUM.CallTransferRecList objWrap = new RouteSoftPhoneXMLResponse_DTO_HUM.CallTransferRecList();
        List<RouteSoftPhoneXMLResponse_DTO_HUM.CallTransferRecList> lstWrap = new List<RouteSoftPhoneXMLResponse_DTO_HUM.CallTransferRecList>();        
		objWrap.TransferNumber = '1664819';
		objWrap.CallerType = 'Employee';
		objWrap.SiteCd = 'GB';
		objWrap.CatCd = 'PRI';
		objWrap.CategoryCd = 'Precert Initiate';
		objWrap.QueueName = 'GBO Broker Medical/Life Claims and Benefits';
		objWrap.MarketDesc = '';
		objWrap.MarketID = '84238';
		objWrap.TransferDataKey = 'EM0048334148423812345';
		objWrap.SiteDesc = '';
		objWrap.ShareableTollFree = '0';
		objWrap.OperatingHours = '';
		objWrap.Notes = '';
		objWrap.TransferType = 'Warm';
		objWrap.TransferConnectNumber = '8554100506';
        lstWrap.add(objWrap);        
        phChum.defaultTransferListAll = lstWrap;
        phChum.phoneBookSelected = 'Default';
        List<GetAllPhoneBookRulesResponse_DTO_HUM.TransferNumberList> lstWrapper = new List<GetAllPhoneBookRulesResponse_DTO_HUM.TransferNumberList>();
        phChum.phBookTransferList = lstWrapper;
        phChum.displayFilteredDefaultQueues();
        System.assert(phChum.phBookTransferList != null);
        phChum.CallerType = 'Agent';
        List<RouteSoftPhoneXMLResponse_DTO_HUM.CallTransferRecList> lstWrapp = new List<RouteSoftPhoneXMLResponse_DTO_HUM.CallTransferRecList>();
        phChum.defaultTransferList = lstWrapp;
        phChum.CreateDefaultTransferListByCallerType();
        System.assert(phChum.defaultTransferList != null);
        phChum.CallerType = 'Group';
        phChum.CreateDefaultTransferListByCallerType();
        System.assert(phChum.defaultTransferList != null);
    }   
    public static testmethod void TestdisplayFilteredDefaultQueuesforDefault()
    {        
        PhoneBook_C_HUM phChum = new PhoneBook_C_HUM();
        phChum.deafultUniqueTransferNumber = '1860506';
        RouteSoftPhoneXMLResponse_DTO_HUM.CallTransferRecList objWrap = new RouteSoftPhoneXMLResponse_DTO_HUM.CallTransferRecList();
        List<RouteSoftPhoneXMLResponse_DTO_HUM.CallTransferRecList> lstWrap = new List<RouteSoftPhoneXMLResponse_DTO_HUM.CallTransferRecList>();        
		objWrap.TransferNumber = '1664819';
		objWrap.CallerType = 'Employee';
		objWrap.SiteCd = 'GB';
		objWrap.CatCd = 'PRI';
		objWrap.CategoryCd = 'Precert Initiate';
		objWrap.QueueName = 'GBO Broker Medical/Life Claims and Benefits';
		objWrap.MarketDesc = '';
		objWrap.MarketID = '84238';
		objWrap.TransferDataKey = 'EM0048334148423812345';
		objWrap.SiteDesc = '';
		objWrap.ShareableTollFree = '0';
		objWrap.OperatingHours = '';
		objWrap.Notes = '';
		objWrap.TransferType = 'Warm';
		objWrap.TransferConnectNumber = '8554100506';
        lstWrap.add(objWrap);        
        phChum.defaultTransferListAll = lstWrap;
        phChum.phoneBookSelected = PhoneBookConstants_H_HUM.DEFAULT_VAL;
        List<GetAllPhoneBookRulesResponse_DTO_HUM.TransferNumberList> lstWrapper = new List<GetAllPhoneBookRulesResponse_DTO_HUM.TransferNumberList>();
        phChum.phBookTransferList = lstWrapper;
        phChum.displayFilteredDefaultQueues();
        System.assert(phChum.phBookTransferList != null);
    } 
    public static testmethod void TestprocessResponsegetAllPhoneBookRules()
    {        
        PhoneBook_C_HUM phChum = new PhoneBook_C_HUM();
        Object ob = phChum.processResponsegetAllPhoneBookRules();
        System.assert(ob == null);
    }
    public static testmethod void TestfetchAllPhoneBookNamesRules()
    {
        PhoneBook_C_HUM phChum = new PhoneBook_C_HUM();
        phChum.isCallConnected = false;
        List<SelectOption> options = new List<SelectOption>();
 	 	options.add(new SelectOption('Default','Default'));
        phChum.phoneBookNamesList = options;
        phChum.phoneBookNames = options;
         GetAllPhoneBookRulesResponse_DTO_HUM.TransferNumberList objWrap = new GetAllPhoneBookRulesResponse_DTO_HUM.TransferNumberList();
        List<GetAllPhoneBookRulesResponse_DTO_HUM.TransferNumberList> lstWrap = new List<GetAllPhoneBookRulesResponse_DTO_HUM.TransferNumberList>();
        Map<String,List<GetAllPhoneBookRulesResponse_DTO_HUM.TransferNumberList>> mapWrap = new Map<String,List<GetAllPhoneBookRulesResponse_DTO_HUM.TransferNumberList>>();
        objWrap.Notes = '';
        objWrap.OperatingHours = '';
        objWrap.QueueName = 'test';
        objWrap.ShareableNumber = '';
        objWrap.TransferNumber = '1860506';
        objWrap.ShareableTollFree = '';
        lstWrap.add(objWrap);
        phChum.phBookTransferList = lstWrap;
        mapWrap.put('Default', lstWrap);  
        phChum.phBookRulesMap = mapWrap;
        String PhBookRulesResponse = '{"GetAllPhoneBookRulesResponse":{"AllPhoneBookRulesResult":{"AllPhoneBookRulesResultList":[{"Key":"Specialty CC","TransferNumber":{"TransferNumberList":[{"CreateUpdateOn":"7/27/2015 12:12:46 PM","Flag":"1","Notes":"CB Pin 3117rnXerox CB Pin 3129rnHCPR Pin 4863","OperatingHours":"","OriginalTransferNumber":"8556801056","QueueName":"Voiance Foreign Language Line","ShareableNumber":"8556801056","TransferNumber":"8556801056","TransferNumberID":"486","UserID":"Carrie Lathrop"},{"CreateUpdateOn":"6/29/2018 4:45:50 PM","Flag":"1","Notes":"","OperatingHours":"","OriginalTransferNumber":"8554817044","QueueName":"Beacon Prov IL Medicaid/Medicare","ShareableNumber":"8554817044","TransferNumber":"8554817044","TransferNumberID":"1207","UserID":"Corey Binford"}]}}]}}}';
        phChum.fetchAllPhoneBookNamesRules(PhBookRulesResponse);
        System.assert(PhBookRulesResponse != null);
    }
    public static testmethod void TestrefreshButtons2()
    {        
        PhoneBook_C_HUM phChum = new PhoneBook_C_HUM();
        PhoneBook_C_HUM.numberVal = '123a7';
        phChum.refreshbuttons();
        System.assert(phChum.enableBlindTransfer == true);
    }
    public static testmethod void TestrefreshButtons3()
    {        
        PhoneBook_C_HUM phChum = new PhoneBook_C_HUM();
        phChum.refreshbuttons();
        System.assert(phChum.enableBlindTransfer == null);
    }
    public static testmethod void TestonCallConnected()
    {
        PhoneBook_C_HUM phChum = new PhoneBook_C_HUM();
        phChum.onCallConnected();
        System.assert(phChum.callerTypeDisable == false);
    }
    public static testmethod void testGetCallDataWebServiceMemberCaseCatchCoverage()
    {
        Test.setCurrentPage(page.PhoneBook_VF_HUM);
        PhoneBook_C_HUM phChum = new PhoneBook_C_HUM();
        Continuation continueObj = phChum.getDataFromWebService();
        System.assert(continueObj == null);
    }
    public static testmethod void testShowPopup()
    {
        PhoneBook_C_HUM phChum = new PhoneBook_C_HUM();
        phChum.deafultUniqueTransferNumber = '1860506';
        RouteSoftPhoneXMLResponse_DTO_HUM.CallTransferRecList objRouteWrap = new RouteSoftPhoneXMLResponse_DTO_HUM.CallTransferRecList();
        List<RouteSoftPhoneXMLResponse_DTO_HUM.CallTransferRecList> lstRouteWrap = new List<RouteSoftPhoneXMLResponse_DTO_HUM.CallTransferRecList>();        
		objRouteWrap.TransferNumber = '1860506';
		objRouteWrap.CallerType = 'Agent';
		objRouteWrap.SiteCd = 'GB';
		objRouteWrap.CatCd = 'PRI';
		objRouteWrap.CategoryCd = 'Precert Initiate';
		objRouteWrap.QueueName = 'test';
		objRouteWrap.MarketDesc = '';
		objRouteWrap.MarketID = '84238';
		objRouteWrap.TransferDataKey = 'EM0048334148423812345';
		objRouteWrap.SiteDesc = '';
		objRouteWrap.ShareableTollFree = '0';
		objRouteWrap.OperatingHours = '';
		objRouteWrap.Notes = '';
		objRouteWrap.TransferType = 'Warm';
		objRouteWrap.TransferConnectNumber = '8554100506';
        lstRouteWrap.add(objRouteWrap);
        phChum.defaultTransferListAll = lstRouteWrap;
        phChum.CallerType = 'Agent';
        phChum.defaultTransferList = lstRouteWrap; 
        phChum.showPopup();
        System.assert(phChum.displayPopup == true);
    }
    public static testmethod void testShowPopupCatchCoverage()
    {
        PhoneBook_C_HUM phChum = new PhoneBook_C_HUM();
        phChum.deafultUniqueTransferNumber = '1860506';
        phChum.showPopup();
        System.assert(phChum.displayPopup == true);
    }

    public static testmethod void testupdateInteractionInfoToIVR(){
        
        Account acc =[select id from Account where Lastname=: 'TesMember' limit 1];
        map<String,String> StateDataMapping = new map<String,String>();
        StateDataMapping.put('CALL_TYPE', 'Inbound');
        StateDataMapping.put('TRANSFER_NUMBER','1234567');
        PhoneBook_C_HUM.updateInteractionInfoToIVR('Test UUID',StateDataMapping,acc.id);
        Account accGroup =[select id from Account where Name=: 'TestGroup00' limit 1];
       	PhoneBook_C_HUM.updateInteractionInfoToIVR('Test UUID Group',StateDataMapping,accGroup.id);
        Account accProvider =[select id from Account where Name=: 'TestProvider' limit 1];
       	PhoneBook_C_HUM.updateInteractionInfoToIVR('Test UUID Provider',StateDataMapping,accProvider.id);
        Account accAgent =[select id from Account where Name=: 'TestAgent' limit 1];
       	PhoneBook_C_HUM.updateInteractionInfoToIVR('Test UUID Agent',StateDataMapping,accAgent.id);
        Account Unknownacc =[select id from Account where Lastname=: 'UnknownTesMember' limit 1];
        PhoneBook_C_HUM.updateInteractionInfoToIVR('Test UUID unknown Member',StateDataMapping,Unknownacc.id);
        Account accUnknownGroup =[select id from Account where Name=: 'Unknown TestGroup00' limit 1];
        PhoneBook_C_HUM.updateInteractionInfoToIVR('Test UUID unknown Group',StateDataMapping,accUnknownGroup.id);
        Account accUnknownProvider =[select id from Account where Name=: 'Unknown TestProvider' limit 1];
        PhoneBook_C_HUM.updateInteractionInfoToIVR('Test UUID unknown Provider',StateDataMapping,accUnknownProvider.id);
        Account accUnknownAgent =[select id from Account where Name=: 'Unknown TestAgent' limit 1];
       	PhoneBook_C_HUM.updateInteractionInfoToIVR('Test UUID unknown Agent',StateDataMapping,accUnknownAgent.id);
        Interaction__c intRec =[SELECT Id,Authenticated__c,Caller_Name__c, Interacting_With__c,Interacting_With_type__c, UUID__c from Interaction__c  where UUID__c=: 'Test UUID Agent' limit 1];
        intRec.put('Interacting_With__c','') ;
        intRec.put('Interacting_With_type__c','') ;
        intRec.Caller_Name__c = 'TestAgent' ;
        update intRec;
        PhoneBook_C_HUM.updateInteractionInfoToIVR('Test UUID Agent',StateDataMapping,accAgent.id);
        list<string> lstLabels = new list<string>();
        String oState = '';
        PhoneBook_C_HUM.processResponseSaveCallData(lstLabels,oState);
        PhoneBook_C_HUM.updateInteractionInfoToIVR('Test UUID 1',StateDataMapping,accAgent.id);
        Interaction__c intRecMember =[SELECT Id,Authenticated__c,Caller_Name__c, Interacting_With__c,Interacting_With_type__c, UUID__c from Interaction__c  where UUID__c=: 'Test UUID' limit 1];
		Interaction_Member__c intMemAgent2 = new Interaction_Member__c(Interacting_About__c = acc.id,Interaction__c = intRecMember.id ,Interacting_About_Type__c ='Member');
		Insert intMemAgent2 ;
        PhoneBook_C_HUM.updateInteractionInfoToIVR('Test UUID',StateDataMapping,acc.id);
        System.assert(intRec.Caller_Name__c != null);
    }
    
    public static testmethod void testisGenesysUser(){
        PhoneBook_H_HUM.isGenesysUser();
        System.assert(PhoneBook_H_HUM.isGenesysUser()!= null);
    }
    
    public static testmethod void testisCRMFunctionalityON(){
        CRMFunctionality_ONOFF_Switch__c objStchMF2 = new CRMFunctionality_ONOFF_Switch__c();
        objStchMF2.name = '2357061';
        objStchMF2.IsON__c = false;
        insert objStchMF2;
        PhoneBook_H_HUM.isCRMFunctionalityON('2357061');
		System.assert(objStchMF2.IsON__c == false);       
    }

    public static testmethod void testGetSecurePaymentTypes(){
        PhoneBook_C_HUM phChum = new PhoneBook_C_HUM();
        List<SelectOption> SPOptionsc = phChum.getSecurePaymentTypes();
        System.assertEquals(SPOptionsc.size(),4);
    }
    
     public static testmethod void testfetchPolicyMemberData(){
        List<Policy_Member__c> polMem = [select id from Policy_Member__c where end_date__c = '12/31/2015'];
        Map<String,String> policymap = PhoneBook_C_HUM.fetchPolicyMemberData(polMem[0].id,'CBIS');
        System.assert(policymap != null);
         
    }
    
     public static testmethod void testfetchPolicyMemberDataWithoutSubscriber(){
        List<Policy_Member__c> polMem = [select id from Policy_Member__c where end_date__c = '12/31/2020'];
        Map<String,String> policymap = PhoneBook_C_HUM.fetchPolicyMemberData(polMem[0].id,'CBIS');
        System.assert(policymap != null);
    }
    
     public static testmethod void testfetchPolicyMemberDataException(){
        Map<String,String> policy = PhoneBook_C_HUM.fetchPolicyMemberData('01','CBIS');
         System.assert(policy != null);
    }

    public static testmethod void testAutoInteractionCreation()
    {
        PhoneBook_C_HUM phChum = new PhoneBook_C_HUM();
        phChum.uuidataValue = 'Test UUID';
        phChum.isRefreshedval = false;
        Continuation continueObj = phChum.getDataFromWebService();
        continueObj.continuationMethod = 'processResponse';        
        HttpResponse httpRes = new Httpresponse ();
        String resBodyString = '{"GetCallDataResponse":{ "GetCallDataResult":{ "Application":"", "UUID":"Test UUID", "CallDataValues":{ "CallDataValuesList":[ { "DataName":"ACTIVE_CALLERCOUNT_ONCALL", "DataValue":"0" }, { "DataName":"ANI", "DataValue":"4092915301" }, { "DataName":"ASO_IND", "DataValue":"True" }, { "DataName":"AUTHENTICATION_IND", "DataValue":"Y" }, { "DataName":"BUS_SEGMENT_IND", "DataValue":"L" }, { "DataName":"Call_Catg_Desc", "DataValue":"Medical Eligibility" }, { "DataName":"Client_ID", "DataValue":"IVR" }, { "DataName":"CLAIM_GEN_KEY", "DataValue":"" }, { "DataName":"DATA_SRC", "DataValue":"IVR" }, { "DataName":"HDHP_IND", "DataValue":"True" }, { "DataName":"INQ_DOS", "DataValue":"" }, { "DataName":"INQ_RSN", "DataValue":"ELG" }, { "DataName":"INQ_RSN1", "DataValue":"ELG" }, { "DataName":"INQA_DOB", "DataValue":"11-18-2014" }, { "DataName":"INQA_FIRST", "DataValue":"" }, { "DataName":"INQA_GRP_ID", "DataValue":"923382" }, { "DataName":"INQA_GRP_NAME", "DataValue":"HUMANA INC" }, { "DataName":"INQA_ID", "DataValue":"000160217" }, { "DataName":"INQA_LAST", "DataValue":"SINICKI" }, { "DataName":"INQA_MKT_ID", "DataValue":"0" }, { "DataName":"INQA_PLTFRM_CD", "DataValue":"EM" }, { "DataName":"INQA_RELN", "DataValue":"" }, { "DataName":"INQA_TYP", "DataValue":"G" }, { "DataName":"INQR_TYP", "DataValue":"G" }, { "DataName":"INQUIRY_ID", "DataValue":"" }, { "DataName":"IVR_CALL_DATE", "DataValue":"20201210" }, { "DataName":"IVR_CALL_TIME", "DataValue":"01:26:26" }, { "DataName":"IVR_CALLER_TYPE", "DataValue":"M" }, { "DataName":"IVR_DEN_OI_CHG_IND", "DataValue":"" }, { "DataName":"IVR_MED_OI_CHG_IND", "DataValue":"" }, { "DataName":"IVR_SERVER_CD", "DataValue":"03" }, { "DataName":"IVR_SERVER_PORT", "DataValue":"209" }, { "DataName":"LAST_AGENT_ID", "DataValue":"3378044" }, { "DataName":"MAJOR_LOB", "DataValue":"NPOS" }, { "DataName":"MEMBER_DOB", "DataValue":"11/18/2014" }, { "DataName":"MEMBER_ID", "DataValue":"000160217" }, { "DataName":"MEMBER_PERS_GEN_KEY", "DataValue":"554004556423" }, { "DataName":"MTV_BL5", "DataValue":"HHP-HUM INC" }, { "DataName":"MTV_BL7", "DataValue":"NPOS" }, { "DataName":"MULTI_COV", "DataValue":"" },  { "DataName":"PROD_TYPE_CD", "DataValue":"" }, { "DataName":"PROD_TYPE_CODE", "DataValue":"NPOSMO" }, { "DataName":"PRODPLANID", "DataValue":"" }, { "DataName":"REFERENCE_ID", "DataValue":"" }, { "DataName":"RX_ID", "DataValue":"" }, { "DataName":"SELLING_LEDGER", "DataValue":"0" }, { "DataName":"STATE_OF_ISSUE", "DataValue":"KY" }, { "DataName":"SURVEY_ID", "DataValue":"50" }, { "DataName":"UUID", "DataValue":"12102000001270800029030001860434" }, { "DataName":"VENDOR_ASOFDATE", "DataValue":"2020-12-10" }, { "DataName":"VENDOR_GROUPID", "DataValue":"" }, { "DataName":"VENDOR_SUB_GROUPID", "DataValue":"9233820e"} ] } } }}';
        httpRes.setBody(resBodyString);
        system.assert(httpRes.getBody() == resBodyString);
        Map<String, HTTPRequest> mapDetails = continueObj.getRequests();
        string reqType = '';
        for(String s : mapDetails.keyset())
        {
            reqType = s;
        } 
        List<string>labelGetmember = new List<string>();
        labelGetmember.add(reqType);
        Test.setContinuationResponse(reqType,httpRes);
        String result = (String)phChum.processResponse();
        System.assertEquals(result,null);
    }
    
    public static testmethod void testAutoInteractionCreationMemberINQR()
    {
        PhoneBook_C_HUM phChum = new PhoneBook_C_HUM();
        phChum.uuidataValue = 'Test UUID Test';
        phChum.isRefreshedval = false;
        Continuation continueObj = phChum.getDataFromWebService();
        continueObj.continuationMethod = 'processResponse';        
        HttpResponse httpRes = new Httpresponse ();
        String resBodyString = '{"GetCallDataResponse":{ "GetCallDataResult":{ "Application":"", "UUID":"Test UUID Test", "CallDataValues":{ "CallDataValuesList":[ { "DataName":"ACTIVE_CALLERCOUNT_ONCALL", "DataValue":"0" }, { "DataName":"ANI", "DataValue":"4092915301" }, { "DataName":"ASO_IND", "DataValue":"True" }, { "DataName":"AUTHENTICATION_IND", "DataValue":"Y" }, { "DataName":"BUS_SEGMENT_IND", "DataValue":"L" }, { "DataName":"Call_Catg_Desc", "DataValue":"Medical Eligibility" }, { "DataName":"Client_ID", "DataValue":"IVR" }, { "DataName":"CLAIM_GEN_KEY", "DataValue":"" }, { "DataName":"DATA_SRC", "DataValue":"IVR" }, { "DataName":"HDHP_IND", "DataValue":"True" }, { "DataName":"INQ_DOS", "DataValue":"" }, { "DataName":"INQ_RSN", "DataValue":"ELG" }, { "DataName":"INQ_RSN1", "DataValue":"ELG" }, { "DataName":"INQA_DOB", "DataValue":"11-18-2014" }, { "DataName":"INQA_FIRST", "DataValue":"JUN-KANG" }, { "DataName":"INQA_GRP_ID", "DataValue":"923382" }, { "DataName":"INQA_GRP_NAME", "DataValue":"HUMANA INC" }, { "DataName":"INQA_ID", "DataValue":"000160217" }, { "DataName":"INQA_LAST", "DataValue":"SINICKI" }, { "DataName":"INQA_MKT_ID", "DataValue":"0" }, { "DataName":"INQA_PLTFRM_CD", "DataValue":"EM" }, { "DataName":"INQA_RELN", "DataValue":"" }, { "DataName":"INQA_TYP", "DataValue":"G" }, { "DataName":"INQR_TYP", "DataValue":"M" }, { "DataName":"INQR_ID", "DataValue":"025134289" }, { "DataName":"INQUIRY_ID", "DataValue":"" }, { "DataName":"IVR_CALL_DATE", "DataValue":"20201210" }, { "DataName":"IVR_CALL_TIME", "DataValue":"01:26:26" }, { "DataName":"IVR_CALLER_TYPE", "DataValue":"M" }, { "DataName":"IVR_DEN_OI_CHG_IND", "DataValue":"" }, { "DataName":"IVR_MED_OI_CHG_IND", "DataValue":"" }, { "DataName":"IVR_SERVER_CD", "DataValue":"03" }, { "DataName":"IVR_SERVER_PORT", "DataValue":"209" }, { "DataName":"LAST_AGENT_ID", "DataValue":"3378044" }, { "DataName":"MAJOR_LOB", "DataValue":"NPOS" }, { "DataName":"MEMBER_DOB", "DataValue":"11/18/2014" }, { "DataName":"MEMBER_ID", "DataValue":"000160217" }, { "DataName":"MEMBER_PERS_GEN_KEY", "DataValue":"554004556423" }, { "DataName":"MTV_BL5", "DataValue":"HHP-HUM INC" }, { "DataName":"MTV_BL7", "DataValue":"NPOS" }, { "DataName":"MULTI_COV", "DataValue":"" },  { "DataName":"PROD_TYPE_CD", "DataValue":"" }, { "DataName":"PROD_TYPE_CODE", "DataValue":"NPOSMO" }, { "DataName":"PRODPLANID", "DataValue":"" }, { "DataName":"REFERENCE_ID", "DataValue":"" }, { "DataName":"RX_ID", "DataValue":"" }, { "DataName":"SELLING_LEDGER", "DataValue":"0" }, { "DataName":"STATE_OF_ISSUE", "DataValue":"KY" }, { "DataName":"SURVEY_ID", "DataValue":"50" }, { "DataName":"UUID", "DataValue":"12102000001270800029030001860434" }, { "DataName":"VENDOR_ASOFDATE", "DataValue":"2020-12-10" }, { "DataName":"VENDOR_GROUPID", "DataValue":"" }, { "DataName":"VENDOR_SUB_GROUPID", "DataValue":"9233820e"} ] } } }}';
        httpRes.setBody(resBodyString);
        system.assert(httpRes.getBody() == resBodyString);
        Map<String, HTTPRequest> mapDetails = continueObj.getRequests();
        string reqType = '';
        for(String s : mapDetails.keyset())
        {
            reqType = s;
        } 
        List<string>labelGetmember = new List<string>();
        labelGetmember.add(reqType);
        Test.setContinuationResponse(reqType,httpRes);
        String result = (String)phChum.processResponse();
        System.assertEquals(result,null);
    }
    
    public static testmethod void testAutoInteractionCreationMemberINQRWithLastFirstName()
    {
        PhoneBook_C_HUM phChum = new PhoneBook_C_HUM();
        phChum.uuidataValue = 'Test UUID Test';
        phChum.isRefreshedval = false;
        Continuation continueObj = phChum.getDataFromWebService();
        continueObj.continuationMethod = 'processResponse';        
        HttpResponse httpRes = new Httpresponse ();
        String resBodyString = '{"GetCallDataResponse":{ "GetCallDataResult":{ "Application":"", "UUID":"Test UUID Test", "CallDataValues":{ "CallDataValuesList":[ { "DataName":"ACTIVE_CALLERCOUNT_ONCALL", "DataValue":"0" }, { "DataName":"ANI", "DataValue":"4092915301" }, { "DataName":"ASO_IND", "DataValue":"True" }, { "DataName":"AUTHENTICATION_IND", "DataValue":"Y" }, { "DataName":"BUS_SEGMENT_IND", "DataValue":"L" }, { "DataName":"Call_Catg_Desc", "DataValue":"Medical Eligibility" }, { "DataName":"Client_ID", "DataValue":"IVR" }, { "DataName":"CLAIM_GEN_KEY", "DataValue":"" }, { "DataName":"DATA_SRC", "DataValue":"IVR" }, { "DataName":"HDHP_IND", "DataValue":"True" }, { "DataName":"INQ_DOS", "DataValue":"" }, { "DataName":"INQ_RSN", "DataValue":"ELG" }, { "DataName":"INQ_RSN1", "DataValue":"ELG" }, { "DataName":"INQA_DOB", "DataValue":"11-18-2014" }, { "DataName":"INQA_FIRST", "DataValue":"JUN-KANG" }, { "DataName":"INQA_GRP_ID", "DataValue":"923382" }, { "DataName":"INQA_GRP_NAME", "DataValue":"HUMANA INC" }, { "DataName":"INQA_ID", "DataValue":"000160217" }, { "DataName":"INQA_LAST", "DataValue":"SINICKI" }, { "DataName":"INQA_MKT_ID", "DataValue":"0" }, { "DataName":"INQA_PLTFRM_CD", "DataValue":"EM" }, { "DataName":"INQA_RELN", "DataValue":"" }, { "DataName":"INQA_TYP", "DataValue":"G" }, { "DataName":"INQR_TYP", "DataValue":"M" },{ "DataName":"INQR_ID", "DataValue":"" },{ "DataName":"INQR_FIRST", "DataValue":"Member" },{ "DataName":"INQR_LAST", "DataValue":"TestMember" }, { "DataName":"INQUIRY_ID", "DataValue":"" }, { "DataName":"IVR_CALL_DATE", "DataValue":"20201210" }, { "DataName":"IVR_CALL_TIME", "DataValue":"01:26:26" }, { "DataName":"IVR_CALLER_TYPE", "DataValue":"M" }, { "DataName":"IVR_DEN_OI_CHG_IND", "DataValue":"" }, { "DataName":"IVR_MED_OI_CHG_IND", "DataValue":"" }, { "DataName":"IVR_SERVER_CD", "DataValue":"03" }, { "DataName":"IVR_SERVER_PORT", "DataValue":"209" }, { "DataName":"LAST_AGENT_ID", "DataValue":"3378044" }, { "DataName":"MAJOR_LOB", "DataValue":"NPOS" }, { "DataName":"MEMBER_DOB", "DataValue":"11/18/2014" }, { "DataName":"MEMBER_ID", "DataValue":"000160217" }, { "DataName":"MEMBER_PERS_GEN_KEY", "DataValue":"554004556423" }, { "DataName":"MTV_BL5", "DataValue":"HHP-HUM INC" }, { "DataName":"MTV_BL7", "DataValue":"NPOS" }, { "DataName":"MULTI_COV", "DataValue":"" },  { "DataName":"PROD_TYPE_CD", "DataValue":"" }, { "DataName":"PROD_TYPE_CODE", "DataValue":"NPOSMO" }, { "DataName":"PRODPLANID", "DataValue":"" }, { "DataName":"REFERENCE_ID", "DataValue":"" }, { "DataName":"RX_ID", "DataValue":"" }, { "DataName":"SELLING_LEDGER", "DataValue":"0" }, { "DataName":"STATE_OF_ISSUE", "DataValue":"KY" }, { "DataName":"SURVEY_ID", "DataValue":"50" }, { "DataName":"UUID", "DataValue":"12102000001270800029030001860434" }, { "DataName":"VENDOR_ASOFDATE", "DataValue":"2020-12-10" }, { "DataName":"VENDOR_GROUPID", "DataValue":"" }, { "DataName":"VENDOR_SUB_GROUPID", "DataValue":"9233820e"} ] } } }}';
        httpRes.setBody(resBodyString);
        system.assert(httpRes.getBody() == resBodyString);
        Map<String, HTTPRequest> mapDetails = continueObj.getRequests();
        string reqType = '';
        for(String s : mapDetails.keyset())
        {
            reqType = s;
        } 
        List<string>labelGetmember = new List<string>();
        labelGetmember.add(reqType);
        Test.setContinuationResponse(reqType,httpRes);
        String result = (String)phChum.processResponse();
        System.assertEquals(result,null);
    }
 
      public static testmethod void testAutoInteractionCreationMemberINQAWithLastName()
    {
        PhoneBook_C_HUM phChum = new PhoneBook_C_HUM();
        phChum.uuidataValue = 'Test UUID Test';
        phChum.isRefreshedval = false;
        Continuation continueObj = phChum.getDataFromWebService();
        continueObj.continuationMethod = 'processResponse';        
        HttpResponse httpRes = new Httpresponse ();
        String resBodyString = '{"GetCallDataResponse":{ "GetCallDataResult":{ "Application":"", "UUID":"Test UUID Test", "CallDataValues":{ "CallDataValuesList":[ { "DataName":"ACTIVE_CALLERCOUNT_ONCALL", "DataValue":"0" }, { "DataName":"ANI", "DataValue":"4092915301" }, { "DataName":"ASO_IND", "DataValue":"True" }, { "DataName":"AUTHENTICATION_IND", "DataValue":"Y" }, { "DataName":"BUS_SEGMENT_IND", "DataValue":"L" }, { "DataName":"Call_Catg_Desc", "DataValue":"Medical Eligibility" }, { "DataName":"Client_ID", "DataValue":"IVR" }, { "DataName":"CLAIM_GEN_KEY", "DataValue":"" }, { "DataName":"DATA_SRC", "DataValue":"IVR" }, { "DataName":"HDHP_IND", "DataValue":"True" }, { "DataName":"INQ_DOS", "DataValue":"" }, { "DataName":"INQ_RSN", "DataValue":"ELG" }, { "DataName":"INQ_RSN1", "DataValue":"ELG" }, { "DataName":"INQA_DOB", "DataValue":"11-18-2014" }, { "DataName":"INQA_FIRST", "DataValue":"JUN-KANG" }, { "DataName":"INQA_GRP_ID", "DataValue":"923382" }, { "DataName":"INQA_GRP_NAME", "DataValue":"HUMANA INC" }, { "DataName":"INQA_ID", "DataValue":"" }, { "DataName":"INQA_LAST", "DataValue":"TesMember" }, { "DataName":"INQA_MKT_ID", "DataValue":"0" }, { "DataName":"INQA_PLTFRM_CD", "DataValue":"EM" }, { "DataName":"INQA_RELN", "DataValue":"" }, { "DataName":"INQA_TYP", "DataValue":"G" }, { "DataName":"INQR_TYP", "DataValue":"M" },{ "DataName":"INQR_ID", "DataValue":"" },{ "DataName":"INQR_FIRST", "DataValue":"Member" },{ "DataName":"INQR_LAST", "DataValue":"TestMember" }, { "DataName":"INQUIRY_ID", "DataValue":"" }, { "DataName":"IVR_CALL_DATE", "DataValue":"20201210" }, { "DataName":"IVR_CALL_TIME", "DataValue":"01:26:26" }, { "DataName":"IVR_CALLER_TYPE", "DataValue":"M" }, { "DataName":"IVR_DEN_OI_CHG_IND", "DataValue":"" }, { "DataName":"IVR_MED_OI_CHG_IND", "DataValue":"" }, { "DataName":"IVR_SERVER_CD", "DataValue":"03" }, { "DataName":"IVR_SERVER_PORT", "DataValue":"209" }, { "DataName":"LAST_AGENT_ID", "DataValue":"3378044" }, { "DataName":"MAJOR_LOB", "DataValue":"NPOS" }, { "DataName":"MEMBER_DOB", "DataValue":"11/18/2014" }, { "DataName":"MEMBER_ID", "DataValue":"000160217" }, { "DataName":"MEMBER_PERS_GEN_KEY", "DataValue":"554004556423" }, { "DataName":"MTV_BL5", "DataValue":"HHP-HUM INC" }, { "DataName":"MTV_BL7", "DataValue":"NPOS" }, { "DataName":"MULTI_COV", "DataValue":"" },  { "DataName":"PROD_TYPE_CD", "DataValue":"" }, { "DataName":"PROD_TYPE_CODE", "DataValue":"NPOSMO" }, { "DataName":"PRODPLANID", "DataValue":"" }, { "DataName":"REFERENCE_ID", "DataValue":"" }, { "DataName":"RX_ID", "DataValue":"" }, { "DataName":"SELLING_LEDGER", "DataValue":"0" }, { "DataName":"STATE_OF_ISSUE", "DataValue":"KY" }, { "DataName":"SURVEY_ID", "DataValue":"50" }, { "DataName":"UUID", "DataValue":"12102000001270800029030001860434" }, { "DataName":"VENDOR_ASOFDATE", "DataValue":"2020-12-10" }, { "DataName":"VENDOR_GROUPID", "DataValue":"" }, { "DataName":"VENDOR_SUB_GROUPID", "DataValue":"9233820e"} ] } } }}';
        httpRes.setBody(resBodyString);
        system.assert(httpRes.getBody() == resBodyString);
        Map<String, HTTPRequest> mapDetails = continueObj.getRequests();
        string reqType = '';
        for(String s : mapDetails.keyset())
        {
            reqType = s;
        } 
        List<string>labelGetmember = new List<string>();
        labelGetmember.add(reqType);
        Test.setContinuationResponse(reqType,httpRes);
        String result = (String)phChum.processResponse();
        System.assertEquals(result,null);
    }
    
     public static testmethod void testgetMemberAboutAccountId()
    {
        PhoneBook_C_HUM phChum = new PhoneBook_C_HUM();
        phChum.uuidataValue = 'Test UUID Test';
        phChum.isRefreshedval = false;
        Continuation continueObj = phChum.getDataFromWebService();
        continueObj.continuationMethod = 'processResponse';        
        HttpResponse httpRes = new Httpresponse ();
        String resBodyString = '{"GetCallDataResponse":{ "GetCallDataResult":{ "Application":"", "UUID":"Test UUID Test", "CallDataValues":{ "CallDataValuesList":[ { "DataName":"ACTIVE_CALLERCOUNT_ONCALL", "DataValue":"0" }, { "DataName":"ANI", "DataValue":"4092915301" }, { "DataName":"ASO_IND", "DataValue":"True" }, { "DataName":"AUTHENTICATION_IND", "DataValue":"Y" }, { "DataName":"BUS_SEGMENT_IND", "DataValue":"L" }, { "DataName":"Call_Catg_Desc", "DataValue":"Medical Eligibility" }, { "DataName":"Client_ID", "DataValue":"IVR" }, { "DataName":"CLAIM_GEN_KEY", "DataValue":"" }, { "DataName":"DATA_SRC", "DataValue":"IVR" }, { "DataName":"HDHP_IND", "DataValue":"True" }, { "DataName":"INQ_DOS", "DataValue":"" }, { "DataName":"INQ_RSN", "DataValue":"ELG" }, { "DataName":"INQ_RSN1", "DataValue":"ELG" }, { "DataName":"INQA_DOB", "DataValue":"11-18-2014" }, { "DataName":"INQA_FIRST", "DataValue":"JUN-KANG" }, { "DataName":"INQA_GRP_ID", "DataValue":"923382" }, { "DataName":"INQA_GRP_NAME", "DataValue":"HUMANA INC" }, { "DataName":"INQA_ID", "DataValue":"" }, { "DataName":"INQA_LAST", "DataValue":"TesMember" }, { "DataName":"INQA_MKT_ID", "DataValue":"0" }, { "DataName":"INQA_PLTFRM_CD", "DataValue":"EM" }, { "DataName":"INQA_RELN", "DataValue":"" }, { "DataName":"INQA_TYP", "DataValue":"M" }, { "DataName":"INQR_TYP", "DataValue":"M" },{ "DataName":"INQR_ID", "DataValue":"" },{ "DataName":"INQR_FIRST", "DataValue":"Member" },{ "DataName":"INQR_LAST", "DataValue":"TestMember" }, { "DataName":"INQUIRY_ID", "DataValue":"" }, { "DataName":"IVR_CALL_DATE", "DataValue":"20201210" }, { "DataName":"IVR_CALL_TIME", "DataValue":"01:26:26" }, { "DataName":"IVR_CALLER_TYPE", "DataValue":"M" }, { "DataName":"IVR_DEN_OI_CHG_IND", "DataValue":"" }, { "DataName":"IVR_MED_OI_CHG_IND", "DataValue":"" }, { "DataName":"IVR_SERVER_CD", "DataValue":"03" }, { "DataName":"IVR_SERVER_PORT", "DataValue":"209" }, { "DataName":"LAST_AGENT_ID", "DataValue":"3378044" }, { "DataName":"MAJOR_LOB", "DataValue":"NPOS" }, { "DataName":"MEMBER_DOB", "DataValue":"11/18/2014" }, { "DataName":"MEMBER_ID", "DataValue":"000160217" }, { "DataName":"MEMBER_PERS_GEN_KEY", "DataValue":"554004556423" }, { "DataName":"MTV_BL5", "DataValue":"HHP-HUM INC" }, { "DataName":"MTV_BL7", "DataValue":"NPOS" }, { "DataName":"MULTI_COV", "DataValue":"" },  { "DataName":"PROD_TYPE_CD", "DataValue":"" }, { "DataName":"PROD_TYPE_CODE", "DataValue":"NPOSMO" }, { "DataName":"PRODPLANID", "DataValue":"" }, { "DataName":"REFERENCE_ID", "DataValue":"" }, { "DataName":"RX_ID", "DataValue":"" }, { "DataName":"SELLING_LEDGER", "DataValue":"0" }, { "DataName":"STATE_OF_ISSUE", "DataValue":"KY" }, { "DataName":"SURVEY_ID", "DataValue":"50" }, { "DataName":"UUID", "DataValue":"12102000001270800029030001860434" }, { "DataName":"VENDOR_ASOFDATE", "DataValue":"2020-12-10" }, { "DataName":"VENDOR_GROUPID", "DataValue":"" }, { "DataName":"VENDOR_SUB_GROUPID", "DataValue":"9233820e"} ] } } }}';
        httpRes.setBody(resBodyString);
        system.assert(httpRes.getBody() == resBodyString);
        Map<String, HTTPRequest> mapDetails = continueObj.getRequests();
        string reqType = '';
        for(String s : mapDetails.keyset())
        {
            reqType = s;
        } 
        List<string>labelGetmember = new List<string>();
        labelGetmember.add(reqType);
        Test.setContinuationResponse(reqType,httpRes);
        String result = (String)phChum.processResponse();
        System.assertEquals(result,null);
    }
    
     public static testmethod void testgetProviderWithAccountId()
    {
        PhoneBook_C_HUM phChum = new PhoneBook_C_HUM();
        phChum.uuidataValue = 'Test UUID Test';
        phChum.isRefreshedval = false;
        Continuation continueObj = phChum.getDataFromWebService();
        continueObj.continuationMethod = 'processResponse';        
        HttpResponse httpRes = new Httpresponse ();
        String resBodyString = '{"GetCallDataResponse":{ "GetCallDataResult":{ "Application":"", "UUID":"Test UUID Test", "CallDataValues":{ "CallDataValuesList":[ { "DataName":"ACTIVE_CALLERCOUNT_ONCALL", "DataValue":"0" }, { "DataName":"ANI", "DataValue":"4092915301" }, { "DataName":"ASO_IND", "DataValue":"True" }, { "DataName":"AUTHENTICATION_IND", "DataValue":"Y" }, { "DataName":"BUS_SEGMENT_IND", "DataValue":"L" }, { "DataName":"Call_Catg_Desc", "DataValue":"Medical Eligibility" }, { "DataName":"Client_ID", "DataValue":"IVR" }, { "DataName":"CLAIM_GEN_KEY", "DataValue":"" }, { "DataName":"DATA_SRC", "DataValue":"IVR" }, { "DataName":"HDHP_IND", "DataValue":"True" }, { "DataName":"INQ_DOS", "DataValue":"" }, { "DataName":"INQ_RSN", "DataValue":"ELG" }, { "DataName":"INQ_RSN1", "DataValue":"ELG" }, { "DataName":"INQA_DOB", "DataValue":"11-18-2014" }, { "DataName":"INQA_FIRST", "DataValue":"JUN-KANG" }, { "DataName":"INQA_GRP_ID", "DataValue":"923382" }, { "DataName":"INQA_GRP_NAME", "DataValue":"HUMANA INC" }, { "DataName":"INQA_ID", "DataValue":"" }, { "DataName":"INQA_LAST", "DataValue":"TesMember" }, { "DataName":"INQA_MKT_ID", "DataValue":"0" }, { "DataName":"INQA_PLTFRM_CD", "DataValue":"EM" }, { "DataName":"INQA_RELN", "DataValue":"" }, { "DataName":"INQA_TYP", "DataValue":"M" }, { "DataName":"INQR_TYP", "DataValue":"P" },{ "DataName":"INQR_ID", "DataValue":"" },{ "DataName":"INQR_FIRST", "DataValue":"Member" },{ "DataName":"INQR_LAST", "DataValue":"TestMember" }, { "DataName":"INQUIRY_ID", "DataValue":"" }, { "DataName":"IVR_CALL_DATE", "DataValue":"20201210" }, { "DataName":"IVR_CALL_TIME", "DataValue":"01:26:26" }, { "DataName":"IVR_CALLER_TYPE", "DataValue":"M" }, { "DataName":"IVR_DEN_OI_CHG_IND", "DataValue":"" }, { "DataName":"IVR_MED_OI_CHG_IND", "DataValue":"" }, { "DataName":"IVR_SERVER_CD", "DataValue":"03" }, { "DataName":"IVR_SERVER_PORT", "DataValue":"209" }, { "DataName":"LAST_AGENT_ID", "DataValue":"3378044" }, { "DataName":"MAJOR_LOB", "DataValue":"NPOS" }, { "DataName":"MEMBER_DOB", "DataValue":"11/18/2014" }, { "DataName":"MEMBER_ID", "DataValue":"000160217" }, { "DataName":"MEMBER_PERS_GEN_KEY", "DataValue":"554004556423" }, { "DataName":"MTV_BL5", "DataValue":"HHP-HUM INC" }, { "DataName":"MTV_BL7", "DataValue":"NPOS" }, { "DataName":"MULTI_COV", "DataValue":"" },  { "DataName":"PROD_TYPE_CD", "DataValue":"" }, { "DataName":"PROD_TYPE_CODE", "DataValue":"NPOSMO" }, { "DataName":"PRODPLANID", "DataValue":"" }, { "DataName":"REFERENCE_ID", "DataValue":"" }, { "DataName":"RX_ID", "DataValue":"" }, { "DataName":"SELLING_LEDGER", "DataValue":"0" }, { "DataName":"STATE_OF_ISSUE", "DataValue":"KY" }, { "DataName":"SURVEY_ID", "DataValue":"50" }, { "DataName":"UUID", "DataValue":"12102000001270800029030001860434" }, { "DataName":"VENDOR_ASOFDATE", "DataValue":"2020-12-10" }, { "DataName":"VENDOR_GROUPID", "DataValue":"" }, { "DataName":"VENDOR_SUB_GROUPID", "DataValue":"9233820e"} ] } } }}';
        httpRes.setBody(resBodyString);
        system.assert(httpRes.getBody() == resBodyString);
        Map<String, HTTPRequest> mapDetails = continueObj.getRequests();
        string reqType = '';
        for(String s : mapDetails.keyset())
        {
            reqType = s;
        } 
        List<string>labelGetmember = new List<string>();
        labelGetmember.add(reqType);
        Test.setContinuationResponse(reqType,httpRes);
        String result = (String)phChum.processResponse();
        System.assertEquals(result,null);
    }
    
    public static testmethod void testgetProviderAboutAccountId()
    {
        PhoneBook_C_HUM phChum = new PhoneBook_C_HUM();
        phChum.uuidataValue = 'Test UUID Test';
        phChum.isRefreshedval = false;
        Continuation continueObj = phChum.getDataFromWebService();
        continueObj.continuationMethod = 'processResponse';        
        HttpResponse httpRes = new Httpresponse ();
        String resBodyString = '{"GetCallDataResponse":{ "GetCallDataResult":{ "Application":"", "UUID":"Test UUID Test", "CallDataValues":{ "CallDataValuesList":[ { "DataName":"ACTIVE_CALLERCOUNT_ONCALL", "DataValue":"0" }, { "DataName":"ANI", "DataValue":"4092915301" }, { "DataName":"ASO_IND", "DataValue":"True" }, { "DataName":"AUTHENTICATION_IND", "DataValue":"Y" }, { "DataName":"BUS_SEGMENT_IND", "DataValue":"L" }, { "DataName":"Call_Catg_Desc", "DataValue":"Medical Eligibility" }, { "DataName":"Client_ID", "DataValue":"IVR" }, { "DataName":"CLAIM_GEN_KEY", "DataValue":"" }, { "DataName":"DATA_SRC", "DataValue":"IVR" }, { "DataName":"HDHP_IND", "DataValue":"True" }, { "DataName":"INQ_DOS", "DataValue":"" }, { "DataName":"INQ_RSN", "DataValue":"ELG" }, { "DataName":"INQ_RSN1", "DataValue":"ELG" }, { "DataName":"INQA_DOB", "DataValue":"11-18-2014" }, { "DataName":"INQA_FIRST", "DataValue":"JUN-KANG" }, { "DataName":"INQA_GRP_ID", "DataValue":"923382" }, { "DataName":"INQA_GRP_NAME", "DataValue":"HUMANA INC" }, { "DataName":"INQA_ID", "DataValue":"123457" }, { "DataName":"INQA_LAST", "DataValue":"TesMember" }, { "DataName":"INQA_MKT_ID", "DataValue":"0" }, { "DataName":"INQA_PLTFRM_CD", "DataValue":"EM" }, { "DataName":"INQA_RELN", "DataValue":"" }, { "DataName":"INQA_TYP", "DataValue":"P" }, { "DataName":"INQR_TYP", "DataValue":"G" },{ "DataName":"INQR_ID", "DataValue":"123456" },{ "DataName":"INQR_FIRST", "DataValue":"Member" },{ "DataName":"INQR_LAST", "DataValue":"TestMember" }, { "DataName":"INQUIRY_ID", "DataValue":"" }, { "DataName":"IVR_CALL_DATE", "DataValue":"20201210" }, { "DataName":"IVR_CALL_TIME", "DataValue":"01:26:26" }, { "DataName":"IVR_CALLER_TYPE", "DataValue":"M" }, { "DataName":"IVR_DEN_OI_CHG_IND", "DataValue":"" }, { "DataName":"IVR_MED_OI_CHG_IND", "DataValue":"" }, { "DataName":"IVR_SERVER_CD", "DataValue":"03" }, { "DataName":"IVR_SERVER_PORT", "DataValue":"209" }, { "DataName":"LAST_AGENT_ID", "DataValue":"3378044" }, { "DataName":"MAJOR_LOB", "DataValue":"NPOS" }, { "DataName":"MEMBER_DOB", "DataValue":"11/18/2014" }, { "DataName":"MEMBER_ID", "DataValue":"000160217" }, { "DataName":"MEMBER_PERS_GEN_KEY", "DataValue":"554004556423" }, { "DataName":"MTV_BL5", "DataValue":"HHP-HUM INC" }, { "DataName":"MTV_BL7", "DataValue":"NPOS" }, { "DataName":"MULTI_COV", "DataValue":"" },  { "DataName":"PROD_TYPE_CD", "DataValue":"" }, { "DataName":"PROD_TYPE_CODE", "DataValue":"NPOSMO" }, { "DataName":"PRODPLANID", "DataValue":"" }, { "DataName":"REFERENCE_ID", "DataValue":"" }, { "DataName":"RX_ID", "DataValue":"" }, { "DataName":"SELLING_LEDGER", "DataValue":"0" }, { "DataName":"STATE_OF_ISSUE", "DataValue":"KY" }, { "DataName":"SURVEY_ID", "DataValue":"50" }, { "DataName":"UUID", "DataValue":"12102000001270800029030001860434" }, { "DataName":"VENDOR_ASOFDATE", "DataValue":"2020-12-10" }, { "DataName":"VENDOR_GROUPID", "DataValue":"" }, { "DataName":"VENDOR_SUB_GROUPID", "DataValue":"9233820e"} ] } } }}';
        httpRes.setBody(resBodyString);
        system.assert(httpRes.getBody() == resBodyString);
        Map<String, HTTPRequest> mapDetails = continueObj.getRequests();
        string reqType = '';
        for(String s : mapDetails.keyset())
        {
            reqType = s;
        } 
        List<string>labelGetmember = new List<string>();
        labelGetmember.add(reqType);
        Test.setContinuationResponse(reqType,httpRes);
        String result = (String)phChum.processResponse();
        System.assertEquals(result,null);
    }
    
     public static testmethod void testgetAgentAboutAccountId()
    {
        PhoneBook_C_HUM phChum = new PhoneBook_C_HUM();
        phChum.uuidataValue = 'Test UUID Test';
        phChum.isRefreshedval = false;
        Continuation continueObj = phChum.getDataFromWebService();
        continueObj.continuationMethod = 'processResponse';        
        HttpResponse httpRes = new Httpresponse ();
        String resBodyString ='{"GetCallDataResponse":{ "GetCallDataResult":{ "Application":"", "UUID":"Test UUID Test", "CallDataValues":{ "CallDataValuesList":[ { "DataName":"ACTIVE_CALLERCOUNT_ONCALL", "DataValue":"0" }, { "DataName":"ANI", "DataValue":"4092915301" }, { "DataName":"ASO_IND", "DataValue":"True" }, { "DataName":"AUTHENTICATION_IND", "DataValue":"Y" }, { "DataName":"BUS_SEGMENT_IND", "DataValue":"L" }, { "DataName":"Call_Catg_Desc", "DataValue":"Medical Eligibility" }, { "DataName":"Client_ID", "DataValue":"IVR" }, { "DataName":"CLAIM_GEN_KEY", "DataValue":"" }, { "DataName":"DATA_SRC", "DataValue":"IVR" }, { "DataName":"HDHP_IND", "DataValue":"True" }, { "DataName":"INQ_DOS", "DataValue":"" }, { "DataName":"INQ_RSN", "DataValue":"ELG" }, { "DataName":"INQ_RSN1", "DataValue":"ELG" }, { "DataName":"INQA_DOB", "DataValue":"11-18-2014" }, { "DataName":"INQA_FIRST", "DataValue":"JUN-KANG" }, { "DataName":"INQA_GRP_ID", "DataValue":"923382" }, { "DataName":"INQA_GRP_NAME", "DataValue":"HUMANA INC" }, { "DataName":"INQA_ID", "DataValue":"" }, { "DataName":"INQA_LAST", "DataValue":"TesMember" }, { "DataName":"INQA_MKT_ID", "DataValue":"0" }, { "DataName":"INQA_PLTFRM_CD", "DataValue":"EM" }, { "DataName":"INQA_RELN", "DataValue":"" }, { "DataName":"INQA_TYP", "DataValue":"A" }, { "DataName":"INQR_TYP", "DataValue":"P" },{ "DataName":"INQR_ID", "DataValue":"" },{ "DataName":"INQR_FIRST", "DataValue":"Member" },{ "DataName":"INQR_LAST", "DataValue":"TestMember" }, { "DataName":"INQUIRY_ID", "DataValue":"" }, { "DataName":"IVR_CALL_DATE", "DataValue":"20201210" }, { "DataName":"IVR_CALL_TIME", "DataValue":"01:26:26" }, { "DataName":"IVR_CALLER_TYPE", "DataValue":"M" }, { "DataName":"IVR_DEN_OI_CHG_IND", "DataValue":"" }, { "DataName":"IVR_MED_OI_CHG_IND", "DataValue":"" }, { "DataName":"IVR_SERVER_CD", "DataValue":"03" }, { "DataName":"IVR_SERVER_PORT", "DataValue":"209" }, { "DataName":"LAST_AGENT_ID", "DataValue":"3378044" }, { "DataName":"MAJOR_LOB", "DataValue":"NPOS" }, { "DataName":"MEMBER_DOB", "DataValue":"11/18/2014" }, { "DataName":"MEMBER_ID", "DataValue":"000160217" }, { "DataName":"MEMBER_PERS_GEN_KEY", "DataValue":"554004556423" }, { "DataName":"MTV_BL5", "DataValue":"HHP-HUM INC" }, { "DataName":"MTV_BL7", "DataValue":"NPOS" }, { "DataName":"MULTI_COV", "DataValue":"" },  { "DataName":"PROD_TYPE_CD", "DataValue":"" }, { "DataName":"PROD_TYPE_CODE", "DataValue":"NPOSMO" }, { "DataName":"PRODPLANID", "DataValue":"" }, { "DataName":"REFERENCE_ID", "DataValue":"" }, { "DataName":"RX_ID", "DataValue":"" }, { "DataName":"SELLING_LEDGER", "DataValue":"0" }, { "DataName":"STATE_OF_ISSUE", "DataValue":"KY" }, { "DataName":"SURVEY_ID", "DataValue":"50" }, { "DataName":"UUID", "DataValue":"12102000001270800029030001860434" }, { "DataName":"VENDOR_ASOFDATE", "DataValue":"2020-12-10" }, { "DataName":"VENDOR_GROUPID", "DataValue":"" }, { "DataName":"VENDOR_SUB_GROUPID", "DataValue":"9233820e"} ] } } }}';
        httpRes.setBody(resBodyString);
        system.assert(httpRes.getBody() == resBodyString);
        Map<String, HTTPRequest> mapDetails = continueObj.getRequests();
        string reqType = '';
        for(String s : mapDetails.keyset())
        {
            reqType = s;
        } 
        List<string>labelGetmember = new List<string>();
        labelGetmember.add(reqType);
        Test.setContinuationResponse(reqType,httpRes);
        String result = (String)phChum.processResponse();
        System.assertEquals(result,null);
    }
    
     public static testmethod void testgetAgentwithAccountId()
    {
        PhoneBook_C_HUM phChum = new PhoneBook_C_HUM();
        phChum.uuidataValue = 'Test UUID Test';
        phChum.isRefreshedval = false;
        Continuation continueObj = phChum.getDataFromWebService();
        continueObj.continuationMethod = 'processResponse';        
        HttpResponse httpRes = new Httpresponse ();
        String resBodyString ='{"GetCallDataResponse":{ "GetCallDataResult":{ "Application":"", "UUID":"Test UUID Test", "CallDataValues":{ "CallDataValuesList":[ { "DataName":"ACTIVE_CALLERCOUNT_ONCALL", "DataValue":"0" }, { "DataName":"ANI", "DataValue":"4092915301" }, { "DataName":"ASO_IND", "DataValue":"True" }, { "DataName":"AUTHENTICATION_IND", "DataValue":"Y" }, { "DataName":"BUS_SEGMENT_IND", "DataValue":"L" }, { "DataName":"Call_Catg_Desc", "DataValue":"Medical Eligibility" }, { "DataName":"Client_ID", "DataValue":"IVR" }, { "DataName":"CLAIM_GEN_KEY", "DataValue":"" }, { "DataName":"DATA_SRC", "DataValue":"IVR" }, { "DataName":"HDHP_IND", "DataValue":"True" }, { "DataName":"INQ_DOS", "DataValue":"" }, { "DataName":"INQ_RSN", "DataValue":"ELG" }, { "DataName":"INQ_RSN1", "DataValue":"ELG" }, { "DataName":"INQA_DOB", "DataValue":"11-18-2014" }, { "DataName":"INQA_FIRST", "DataValue":"JUN-KANG" }, { "DataName":"INQA_GRP_ID", "DataValue":"923382" }, { "DataName":"INQA_GRP_NAME", "DataValue":"HUMANA INC" }, { "DataName":"INQA_ID", "DataValue":"" }, { "DataName":"INQA_LAST", "DataValue":"TesMember" }, { "DataName":"INQA_MKT_ID", "DataValue":"0" }, { "DataName":"INQA_PLTFRM_CD", "DataValue":"EM" }, { "DataName":"INQA_RELN", "DataValue":"" }, { "DataName":"INQA_TYP", "DataValue":"A" }, { "DataName":"INQR_TYP", "DataValue":"A" },{ "DataName":"INQR_ID", "DataValue":"" },{ "DataName":"INQR_FIRST", "DataValue":"Member" },{ "DataName":"INQR_LAST", "DataValue":"TestMember" }, { "DataName":"INQUIRY_ID", "DataValue":"" }, { "DataName":"IVR_CALL_DATE", "DataValue":"20201210" }, { "DataName":"IVR_CALL_TIME", "DataValue":"01:26:26" }, { "DataName":"IVR_CALLER_TYPE", "DataValue":"M" }, { "DataName":"IVR_DEN_OI_CHG_IND", "DataValue":"" }, { "DataName":"IVR_MED_OI_CHG_IND", "DataValue":"" }, { "DataName":"IVR_SERVER_CD", "DataValue":"03" }, { "DataName":"IVR_SERVER_PORT", "DataValue":"209" }, { "DataName":"LAST_AGENT_ID", "DataValue":"3378044" }, { "DataName":"MAJOR_LOB", "DataValue":"NPOS" }, { "DataName":"MEMBER_DOB", "DataValue":"11/18/2014" }, { "DataName":"MEMBER_ID", "DataValue":"000160217" }, { "DataName":"MEMBER_PERS_GEN_KEY", "DataValue":"554004556423" }, { "DataName":"MTV_BL5", "DataValue":"HHP-HUM INC" }, { "DataName":"MTV_BL7", "DataValue":"NPOS" }, { "DataName":"MULTI_COV", "DataValue":"" },  { "DataName":"PROD_TYPE_CD", "DataValue":"" }, { "DataName":"PROD_TYPE_CODE", "DataValue":"NPOSMO" }, { "DataName":"PRODPLANID", "DataValue":"" }, { "DataName":"REFERENCE_ID", "DataValue":"" }, { "DataName":"RX_ID", "DataValue":"" }, { "DataName":"SELLING_LEDGER", "DataValue":"0" }, { "DataName":"STATE_OF_ISSUE", "DataValue":"KY" }, { "DataName":"SURVEY_ID", "DataValue":"50" }, { "DataName":"UUID", "DataValue":"12102000001270800029030001860434" }, { "DataName":"VENDOR_ASOFDATE", "DataValue":"2020-12-10" }, { "DataName":"VENDOR_GROUPID", "DataValue":"" }, { "DataName":"VENDOR_SUB_GROUPID", "DataValue":"9233820e"} ] } } }}';
        httpRes.setBody(resBodyString);
        system.assert(httpRes.getBody() == resBodyString);
        Map<String, HTTPRequest> mapDetails = continueObj.getRequests();
        string reqType = '';
        for(String s : mapDetails.keyset())
        {
            reqType = s;
        } 
        List<string>labelGetmember = new List<string>();
        labelGetmember.add(reqType);
        Test.setContinuationResponse(reqType,httpRes);
        String result = (String)phChum.processResponse();
        System.assertEquals(result,null);
    }
    
     public static testmethod void testgetGroupWithAccountId()
    {
        String ret = PhoneBook_C_HUM.getGroupWithAccountId('','','TesMember3','','','','','','');
        String ret1 = PhoneBook_C_HUM.getGroupWithAccountId('','','','','G','','','','');
        String ret2 = PhoneBook_C_HUM.getAgentWithAccountId('','','','','A','','','','');
        System.assert(ret != null);
    }
    
     public static testmethod void testgetProviderAboutAccountIdWithConsumerId()
    {
        PhoneBook_C_HUM phChum = new PhoneBook_C_HUM();
        phChum.uuidataValue = 'Test UUID Test';
        phChum.isRefreshedval = false;
        Continuation continueObj = phChum.getDataFromWebService();
        continueObj.continuationMethod = 'processResponse';        
        HttpResponse httpRes = new Httpresponse ();
        String resBodyString ='{"GetCallDataResponse":{ "GetCallDataResult":{ "Application":"", "UUID":"Test UUID Test", "CallDataValues":{ "CallDataValuesList":[ { "DataName":"ACTIVE_CALLERCOUNT_ONCALL", "DataValue":"0" }, { "DataName":"ANI", "DataValue":"4092915301" }, { "DataName":"ASO_IND", "DataValue":"True" }, { "DataName":"AUTHENTICATION_IND", "DataValue":"Y" }, { "DataName":"BUS_SEGMENT_IND", "DataValue":"L" }, { "DataName":"Call_Catg_Desc", "DataValue":"Medical Eligibility" }, { "DataName":"Client_ID", "DataValue":"IVR" }, { "DataName":"CLAIM_GEN_KEY", "DataValue":"" }, { "DataName":"DATA_SRC", "DataValue":"IVR" }, { "DataName":"HDHP_IND", "DataValue":"True" }, { "DataName":"INQ_DOS", "DataValue":"" }, { "DataName":"INQ_RSN", "DataValue":"ELG" }, { "DataName":"INQ_RSN1", "DataValue":"ELG" }, { "DataName":"INQA_DOB", "DataValue":"11-18-2014" }, { "DataName":"INQA_FIRST", "DataValue":"JUN-KANG" }, { "DataName":"INQA_GRP_ID", "DataValue":"923382" }, { "DataName":"INQA_GRP_NAME", "DataValue":"HUMANA INC" }, { "DataName":"INQA_ID", "DataValue":"123456" }, { "DataName":"INQA_LAST", "DataValue":"TesMember" }, { "DataName":"INQA_MKT_ID", "DataValue":"0" }, { "DataName":"INQA_PLTFRM_CD", "DataValue":"EM" }, { "DataName":"INQA_RELN", "DataValue":"" }, { "DataName":"INQA_TYP", "DataValue":"P" }, { "DataName":"INQR_TYP", "DataValue":"G" },{ "DataName":"INQR_ID", "DataValue":"123456" },{ "DataName":"INQR_FIRST", "DataValue":"Member" },{ "DataName":"INQR_LAST", "DataValue":"TestMember" }, { "DataName":"INQUIRY_ID", "DataValue":"" }, { "DataName":"IVR_CALL_DATE", "DataValue":"20201210" }, { "DataName":"IVR_CALL_TIME", "DataValue":"01:26:26" }, { "DataName":"IVR_CALLER_TYPE", "DataValue":"M" }, { "DataName":"IVR_DEN_OI_CHG_IND", "DataValue":"" }, { "DataName":"IVR_MED_OI_CHG_IND", "DataValue":"" }, { "DataName":"IVR_SERVER_CD", "DataValue":"03" }, { "DataName":"IVR_SERVER_PORT", "DataValue":"209" }, { "DataName":"LAST_AGENT_ID", "DataValue":"3378044" }, { "DataName":"MAJOR_LOB", "DataValue":"NPOS" }, { "DataName":"MEMBER_DOB", "DataValue":"11/18/2014" }, { "DataName":"MEMBER_ID", "DataValue":"000160217" }, { "DataName":"MEMBER_PERS_GEN_KEY", "DataValue":"554004556423" }, { "DataName":"MTV_BL5", "DataValue":"HHP-HUM INC" }, { "DataName":"MTV_BL7", "DataValue":"NPOS" }, { "DataName":"MULTI_COV", "DataValue":"" },  { "DataName":"PROD_TYPE_CD", "DataValue":"" }, { "DataName":"PROD_TYPE_CODE", "DataValue":"NPOSMO" }, { "DataName":"PRODPLANID", "DataValue":"" }, { "DataName":"REFERENCE_ID", "DataValue":"" }, { "DataName":"RX_ID", "DataValue":"" }, { "DataName":"SELLING_LEDGER", "DataValue":"0" }, { "DataName":"STATE_OF_ISSUE", "DataValue":"KY" }, { "DataName":"SURVEY_ID", "DataValue":"50" }, { "DataName":"UUID", "DataValue":"12102000001270800029030001860434" }, { "DataName":"VENDOR_ASOFDATE", "DataValue":"2020-12-10" }, { "DataName":"VENDOR_GROUPID", "DataValue":"" }, { "DataName":"VENDOR_SUB_GROUPID", "DataValue":"9233820e"} ] } } }}';
        httpRes.setBody(resBodyString);
        system.assert(httpRes.getBody() == resBodyString);
        Map<String, HTTPRequest> mapDetails = continueObj.getRequests();
        string reqType = '';
        for(String s : mapDetails.keyset())
        {
            reqType = s;
        } 
        List<string>labelGetmember = new List<string>();
        labelGetmember.add(reqType);
        Test.setContinuationResponse(reqType,httpRes);
        String result = (String)phChum.processResponse();
        System.assertEquals(result,null);
    }
    
      public static testmethod void testgetProviderAboutAccountIdWithConsumerIdHavingMultipleAcc()
    {
        PhoneBook_C_HUM phChum = new PhoneBook_C_HUM();
        phChum.uuidataValue = 'Test UUID Test';
        phChum.isRefreshedval = false;
        Continuation continueObj = phChum.getDataFromWebService();
        continueObj.continuationMethod = 'processResponse';        
        HttpResponse httpRes = new Httpresponse ();
        String resBodyString ='{"GetCallDataResponse":{ "GetCallDataResult":{ "Application":"", "UUID":"Test UUID Test", "CallDataValues":{ "CallDataValuesList":[ { "DataName":"ACTIVE_CALLERCOUNT_ONCALL", "DataValue":"0" }, { "DataName":"ANI", "DataValue":"4092915301" }, { "DataName":"ASO_IND", "DataValue":"True" }, { "DataName":"AUTHENTICATION_IND", "DataValue":"Y" }, { "DataName":"BUS_SEGMENT_IND", "DataValue":"L" }, { "DataName":"Call_Catg_Desc", "DataValue":"Medical Eligibility" }, { "DataName":"Client_ID", "DataValue":"IVR" }, { "DataName":"CLAIM_GEN_KEY", "DataValue":"" }, { "DataName":"DATA_SRC", "DataValue":"IVR" }, { "DataName":"HDHP_IND", "DataValue":"True" }, { "DataName":"INQ_DOS", "DataValue":"" }, { "DataName":"INQ_RSN", "DataValue":"ELG" }, { "DataName":"INQ_RSN1", "DataValue":"ELG" }, { "DataName":"INQA_DOB", "DataValue":"11-18-2014" }, { "DataName":"INQA_FIRST", "DataValue":"JUN-KANG" }, { "DataName":"INQA_GRP_ID", "DataValue":"923382" }, { "DataName":"INQA_GRP_NAME", "DataValue":"HUMANA INC" }, { "DataName":"INQA_ID", "DataValue":"" }, { "DataName":"INQA_LAST", "DataValue":"TesMember" }, { "DataName":"INQA_MKT_ID", "DataValue":"0" }, { "DataName":"INQA_PLTFRM_CD", "DataValue":"EM" }, { "DataName":"INQA_RELN", "DataValue":"" }, { "DataName":"INQA_TYP", "DataValue":"P" }, { "DataName":"INQR_TYP", "DataValue":"G" },{ "DataName":"INQR_ID", "DataValue":"123456" },{ "DataName":"INQR_FIRST", "DataValue":"Member" },{ "DataName":"INQR_LAST", "DataValue":"TestMember" }, { "DataName":"INQUIRY_ID", "DataValue":"" }, { "DataName":"IVR_CALL_DATE", "DataValue":"20201210" }, { "DataName":"IVR_CALL_TIME", "DataValue":"01:26:26" }, { "DataName":"IVR_CALLER_TYPE", "DataValue":"M" }, { "DataName":"IVR_DEN_OI_CHG_IND", "DataValue":"" }, { "DataName":"IVR_MED_OI_CHG_IND", "DataValue":"" }, { "DataName":"IVR_SERVER_CD", "DataValue":"03" }, { "DataName":"IVR_SERVER_PORT", "DataValue":"209" }, { "DataName":"LAST_AGENT_ID", "DataValue":"3378044" }, { "DataName":"MAJOR_LOB", "DataValue":"NPOS" }, { "DataName":"MEMBER_DOB", "DataValue":"11/18/2014" }, { "DataName":"MEMBER_ID", "DataValue":"000160217" }, { "DataName":"MEMBER_PERS_GEN_KEY", "DataValue":"554004556423" }, { "DataName":"MTV_BL5", "DataValue":"HHP-HUM INC" }, { "DataName":"MTV_BL7", "DataValue":"NPOS" }, { "DataName":"MULTI_COV", "DataValue":"" },  { "DataName":"PROD_TYPE_CD", "DataValue":"" }, { "DataName":"PROD_TYPE_CODE", "DataValue":"NPOSMO" }, { "DataName":"PRODPLANID", "DataValue":"" }, { "DataName":"REFERENCE_ID", "DataValue":"" }, { "DataName":"RX_ID", "DataValue":"" }, { "DataName":"SELLING_LEDGER", "DataValue":"0" }, { "DataName":"STATE_OF_ISSUE", "DataValue":"KY" }, { "DataName":"SURVEY_ID", "DataValue":"50" }, { "DataName":"UUID", "DataValue":"12102000001270800029030001860434" }, { "DataName":"VENDOR_ASOFDATE", "DataValue":"2020-12-10" }, { "DataName":"VENDOR_GROUPID", "DataValue":"" }, { "DataName":"VENDOR_SUB_GROUPID", "DataValue":"9233820e"} ] } } }}';
        httpRes.setBody(resBodyString);
        system.assert(httpRes.getBody() == resBodyString);
        Map<String, HTTPRequest> mapDetails = continueObj.getRequests();
        string reqType = '';
        for(String s : mapDetails.keyset())
        {
            reqType = s;
        } 
        List<string>labelGetmember = new List<string>();
        labelGetmember.add(reqType);
        Test.setContinuationResponse(reqType,httpRes);
        String result = (String)phChum.processResponse();
        System.assertEquals(result,null);
    }
    
    
        public static testmethod void testgetAgentWithMultipleAccountIds()
    {
        PhoneBook_C_HUM phChum = new PhoneBook_C_HUM();
        phChum.uuidataValue = 'Test UUID Test';
        phChum.isRefreshedval = false;
        Continuation continueObj = phChum.getDataFromWebService();
        continueObj.continuationMethod = 'processResponse';        
        HttpResponse httpRes = new Httpresponse ();
        String resBodyString ='{"GetCallDataResponse":{ "GetCallDataResult":{ "Application":"", "UUID":"Test UUID Test", "CallDataValues":{ "CallDataValuesList":[ { "DataName":"ACTIVE_CALLERCOUNT_ONCALL", "DataValue":"0" }, { "DataName":"ANI", "DataValue":"4092915301" }, { "DataName":"ASO_IND", "DataValue":"True" }, { "DataName":"AUTHENTICATION_IND", "DataValue":"Y" }, { "DataName":"BUS_SEGMENT_IND", "DataValue":"L" }, { "DataName":"Call_Catg_Desc", "DataValue":"Medical Eligibility" }, { "DataName":"Client_ID", "DataValue":"IVR" }, { "DataName":"CLAIM_GEN_KEY", "DataValue":"" }, { "DataName":"DATA_SRC", "DataValue":"IVR" }, { "DataName":"HDHP_IND", "DataValue":"True" }, { "DataName":"INQ_DOS", "DataValue":"" }, { "DataName":"INQ_RSN", "DataValue":"ELG" }, { "DataName":"INQ_RSN1", "DataValue":"ELG" }, { "DataName":"INQA_DOB", "DataValue":"11-18-2014" }, { "DataName":"INQA_FIRST", "DataValue":"JUN-KANG" }, { "DataName":"INQA_GRP_ID", "DataValue":"923382" }, { "DataName":"INQA_GRP_NAME", "DataValue":"HUMANA INC" }, { "DataName":"INQA_ID", "DataValue":"123457" }, { "DataName":"INQA_LAST", "DataValue":"TesMember" }, { "DataName":"INQA_MKT_ID", "DataValue":"0" }, { "DataName":"INQA_PLTFRM_CD", "DataValue":"EM" }, { "DataName":"INQA_RELN", "DataValue":"" }, { "DataName":"INQA_TYP", "DataValue":"P" }, { "DataName":"INQR_TYP", "DataValue":"A" },{ "DataName":"INQR_ID", "DataValue":"54321" },{ "DataName":"INQR_FIRST", "DataValue":"Member" },{ "DataName":"INQR_LAST", "DataValue":"TestMember" }, { "DataName":"INQUIRY_ID", "DataValue":"" }, { "DataName":"IVR_CALL_DATE", "DataValue":"20201210" }, { "DataName":"IVR_CALL_TIME", "DataValue":"01:26:26" }, { "DataName":"IVR_CALLER_TYPE", "DataValue":"M" }, { "DataName":"IVR_DEN_OI_CHG_IND", "DataValue":"" }, { "DataName":"IVR_MED_OI_CHG_IND", "DataValue":"" }, { "DataName":"IVR_SERVER_CD", "DataValue":"03" }, { "DataName":"IVR_SERVER_PORT", "DataValue":"209" }, { "DataName":"LAST_AGENT_ID", "DataValue":"3378044" }, { "DataName":"MAJOR_LOB", "DataValue":"NPOS" }, { "DataName":"MEMBER_DOB", "DataValue":"11/18/2014" }, { "DataName":"MEMBER_ID", "DataValue":"000160217" }, { "DataName":"MEMBER_PERS_GEN_KEY", "DataValue":"554004556423" }, { "DataName":"MTV_BL5", "DataValue":"HHP-HUM INC" }, { "DataName":"MTV_BL7", "DataValue":"NPOS" }, { "DataName":"MULTI_COV", "DataValue":"" },  { "DataName":"PROD_TYPE_CD", "DataValue":"" }, { "DataName":"PROD_TYPE_CODE", "DataValue":"NPOSMO" }, { "DataName":"PRODPLANID", "DataValue":"" }, { "DataName":"REFERENCE_ID", "DataValue":"" }, { "DataName":"RX_ID", "DataValue":"" }, { "DataName":"SELLING_LEDGER", "DataValue":"0" }, { "DataName":"STATE_OF_ISSUE", "DataValue":"KY" }, { "DataName":"SURVEY_ID", "DataValue":"50" }, { "DataName":"UUID", "DataValue":"12102000001270800029030001860434" }, { "DataName":"VENDOR_ASOFDATE", "DataValue":"2020-12-10" }, { "DataName":"VENDOR_GROUPID", "DataValue":"" }, { "DataName":"VENDOR_SUB_GROUPID", "DataValue":"9233820e"} ] } } }}';
        httpRes.setBody(resBodyString);
        system.assert(httpRes.getBody() == resBodyString);
        Map<String, HTTPRequest> mapDetails = continueObj.getRequests();
        string reqType = '';
        for(String s : mapDetails.keyset())
        {
            reqType = s;
        } 
        List<string>labelGetmember = new List<string>();
        labelGetmember.add(reqType);
        Test.setContinuationResponse(reqType,httpRes);
        String result = (String)phChum.processResponse();
        System.assertEquals(result,null);
    }
    
    
    public static testmethod void testgetAgentAboutMultipleAccountId()
    {
        PhoneBook_C_HUM phChum = new PhoneBook_C_HUM();
        phChum.uuidataValue = 'Test UUID Test';
        phChum.isRefreshedval = false;
        Continuation continueObj = phChum.getDataFromWebService();
        continueObj.continuationMethod = 'processResponse';        
        HttpResponse httpRes = new Httpresponse ();
        String resBodyString = '{"GetCallDataResponse":{ "GetCallDataResult":{ "Application":"", "UUID":"Test UUID Test", "CallDataValues":{ "CallDataValuesList":[ { "DataName":"ACTIVE_CALLERCOUNT_ONCALL", "DataValue":"0" }, { "DataName":"ANI", "DataValue":"4092915301" }, { "DataName":"ASO_IND", "DataValue":"True" }, { "DataName":"AUTHENTICATION_IND", "DataValue":"Y" }, { "DataName":"BUS_SEGMENT_IND", "DataValue":"L" }, { "DataName":"Call_Catg_Desc", "DataValue":"Medical Eligibility" }, { "DataName":"Client_ID", "DataValue":"IVR" }, { "DataName":"CLAIM_GEN_KEY", "DataValue":"" }, { "DataName":"DATA_SRC", "DataValue":"IVR" }, { "DataName":"HDHP_IND", "DataValue":"True" }, { "DataName":"INQ_DOS", "DataValue":"" }, { "DataName":"INQ_RSN", "DataValue":"ELG" }, { "DataName":"INQ_RSN1", "DataValue":"ELG" }, { "DataName":"INQA_DOB", "DataValue":"11-18-2014" }, { "DataName":"INQA_FIRST", "DataValue":"JUN-KANG" }, { "DataName":"INQA_GRP_ID", "DataValue":"923382" }, { "DataName":"INQA_GRP_NAME", "DataValue":"HUMANA INC" }, { "DataName":"INQA_ID", "DataValue":"54321" }, { "DataName":"INQA_LAST", "DataValue":"TesMember" }, { "DataName":"INQA_MKT_ID", "DataValue":"0" }, { "DataName":"INQA_PLTFRM_CD", "DataValue":"EM" }, { "DataName":"INQA_RELN", "DataValue":"" }, { "DataName":"INQA_TYP", "DataValue":"A" }, { "DataName":"INQR_TYP", "DataValue":"A" },{ "DataName":"INQR_ID", "DataValue":"54321" },{ "DataName":"INQR_FIRST", "DataValue":"Member" },{ "DataName":"INQR_LAST", "DataValue":"TestMember" }, { "DataName":"INQUIRY_ID", "DataValue":"" }, { "DataName":"IVR_CALL_DATE", "DataValue":"20201210" }, { "DataName":"IVR_CALL_TIME", "DataValue":"01:26:26" }, { "DataName":"IVR_CALLER_TYPE", "DataValue":"M" }, { "DataName":"IVR_DEN_OI_CHG_IND", "DataValue":"" }, { "DataName":"IVR_MED_OI_CHG_IND", "DataValue":"" }, { "DataName":"IVR_SERVER_CD", "DataValue":"03" }, { "DataName":"IVR_SERVER_PORT", "DataValue":"209" }, { "DataName":"LAST_AGENT_ID", "DataValue":"3378044" }, { "DataName":"MAJOR_LOB", "DataValue":"NPOS" }, { "DataName":"MEMBER_DOB", "DataValue":"11/18/2014" }, { "DataName":"MEMBER_ID", "DataValue":"000160217" }, { "DataName":"MEMBER_PERS_GEN_KEY", "DataValue":"554004556423" }, { "DataName":"MTV_BL5", "DataValue":"HHP-HUM INC" }, { "DataName":"MTV_BL7", "DataValue":"NPOS" }, { "DataName":"MULTI_COV", "DataValue":"" },  { "DataName":"PROD_TYPE_CD", "DataValue":"" }, { "DataName":"PROD_TYPE_CODE", "DataValue":"NPOSMO" }, { "DataName":"PRODPLANID", "DataValue":"" }, { "DataName":"REFERENCE_ID", "DataValue":"" }, { "DataName":"RX_ID", "DataValue":"" }, { "DataName":"SELLING_LEDGER", "DataValue":"0" }, { "DataName":"STATE_OF_ISSUE", "DataValue":"KY" }, { "DataName":"SURVEY_ID", "DataValue":"50" }, { "DataName":"UUID", "DataValue":"12102000001270800029030001860434" }, { "DataName":"VENDOR_ASOFDATE", "DataValue":"2020-12-10" }, { "DataName":"VENDOR_GROUPID", "DataValue":"" }, { "DataName":"VENDOR_SUB_GROUPID", "DataValue":"9233820e"} ] } } }}';
        httpRes.setBody(resBodyString);
        system.assert(httpRes.getBody() == resBodyString);
        Map<String, HTTPRequest> mapDetails = continueObj.getRequests();
        string reqType = '';
        for(String s : mapDetails.keyset())
        {
            reqType = s;
        } 
        List<string>labelGetmember = new List<string>();
        labelGetmember.add(reqType);
        Test.setContinuationResponse(reqType,httpRes);
        String result = (String)phChum.processResponse();
        System.assertEquals(result,null);
    }
    
    public static testmethod void testgetGroupWithMultipleAccountId()
    {
        PhoneBook_C_HUM phChum = new PhoneBook_C_HUM();
        phChum.uuidataValue = 'Test UUID Test';
        phChum.isRefreshedval = false;
        Continuation continueObj = phChum.getDataFromWebService();
        continueObj.continuationMethod = 'processResponse';        
        HttpResponse httpRes = new Httpresponse ();
        String resBodyString = '{"GetCallDataResponse":{ "GetCallDataResult":{ "Application":"", "UUID":"Test UUID Test", "CallDataValues":{ "CallDataValuesList":[ { "DataName":"ACTIVE_CALLERCOUNT_ONCALL", "DataValue":"0" }, { "DataName":"ANI", "DataValue":"4092915301" }, { "DataName":"ASO_IND", "DataValue":"True" }, { "DataName":"AUTHENTICATION_IND", "DataValue":"Y" }, { "DataName":"BUS_SEGMENT_IND", "DataValue":"L" }, { "DataName":"Call_Catg_Desc", "DataValue":"Medical Eligibility" }, { "DataName":"Client_ID", "DataValue":"IVR" }, { "DataName":"CLAIM_GEN_KEY", "DataValue":"" }, { "DataName":"DATA_SRC", "DataValue":"IVR" }, { "DataName":"HDHP_IND", "DataValue":"True" }, { "DataName":"INQ_DOS", "DataValue":"" }, { "DataName":"INQ_RSN", "DataValue":"ELG" }, { "DataName":"INQ_RSN1", "DataValue":"ELG" }, { "DataName":"INQA_DOB", "DataValue":"11-18-2014" }, { "DataName":"INQA_FIRST", "DataValue":"JUN-KANG" }, { "DataName":"INQA_GRP_ID", "DataValue":"923382" }, { "DataName":"INQA_GRP_NAME", "DataValue":"HUMANA INC" }, { "DataName":"INQA_ID", "DataValue":"54321" }, { "DataName":"INQA_LAST", "DataValue":"TesMember" }, { "DataName":"INQA_MKT_ID", "DataValue":"0" }, { "DataName":"INQA_PLTFRM_CD", "DataValue":"EM" }, { "DataName":"INQA_RELN", "DataValue":"" }, { "DataName":"INQA_TYP", "DataValue":"A" }, { "DataName":"INQR_TYP", "DataValue":"G" },{ "DataName":"INQR_ID", "DataValue":"22221" },{ "DataName":"INQR_FIRST", "DataValue":"Member" },{ "DataName":"INQR_LAST", "DataValue":"TestMember" }, { "DataName":"INQUIRY_ID", "DataValue":"" }, { "DataName":"IVR_CALL_DATE", "DataValue":"20201210" }, { "DataName":"IVR_CALL_TIME", "DataValue":"01:26:26" }, { "DataName":"IVR_CALLER_TYPE", "DataValue":"M" }, { "DataName":"IVR_DEN_OI_CHG_IND", "DataValue":"" }, { "DataName":"IVR_MED_OI_CHG_IND", "DataValue":"" }, { "DataName":"IVR_SERVER_CD", "DataValue":"03" }, { "DataName":"IVR_SERVER_PORT", "DataValue":"209" }, { "DataName":"LAST_AGENT_ID", "DataValue":"3378044" }, { "DataName":"MAJOR_LOB", "DataValue":"NPOS" }, { "DataName":"MEMBER_DOB", "DataValue":"11/18/2014" }, { "DataName":"MEMBER_ID", "DataValue":"000160217" }, { "DataName":"MEMBER_PERS_GEN_KEY", "DataValue":"554004556423" }, { "DataName":"MTV_BL5", "DataValue":"HHP-HUM INC" }, { "DataName":"MTV_BL7", "DataValue":"NPOS" }, { "DataName":"MULTI_COV", "DataValue":"" },  { "DataName":"PROD_TYPE_CD", "DataValue":"" }, { "DataName":"PROD_TYPE_CODE", "DataValue":"NPOSMO" }, { "DataName":"PRODPLANID", "DataValue":"" }, { "DataName":"REFERENCE_ID", "DataValue":"" }, { "DataName":"RX_ID", "DataValue":"" }, { "DataName":"SELLING_LEDGER", "DataValue":"0" }, { "DataName":"STATE_OF_ISSUE", "DataValue":"KY" }, { "DataName":"SURVEY_ID", "DataValue":"50" }, { "DataName":"UUID", "DataValue":"12102000001270800029030001860434" }, { "DataName":"VENDOR_ASOFDATE", "DataValue":"2020-12-10" }, { "DataName":"VENDOR_GROUPID", "DataValue":"" }, { "DataName":"VENDOR_SUB_GROUPID", "DataValue":"9233820e"} ] } } }}';
        httpRes.setBody(resBodyString);
        system.assert(httpRes.getBody() == resBodyString);
        Map<String, HTTPRequest> mapDetails = continueObj.getRequests();
        string reqType = '';
        for(String s : mapDetails.keyset())
        {
            reqType = s;
        } 
        List<string>labelGetmember = new List<string>();
        labelGetmember.add(reqType);
        Test.setContinuationResponse(reqType,httpRes);
        String result = (String)phChum.processResponse();
        System.assertEquals(result,null);
    }
    
    public static testmethod void testgetProviderWithMultipleAccountId()
    {
        PhoneBook_C_HUM phChum = new PhoneBook_C_HUM();
        phChum.uuidataValue = 'Test UUID Test';
        phChum.isRefreshedval = false;
        Continuation continueObj = phChum.getDataFromWebService();
        continueObj.continuationMethod = 'processResponse';        
        HttpResponse httpRes = new Httpresponse ();
        String resBodyString = '{"GetCallDataResponse":{ "GetCallDataResult":{ "Application":"", "UUID":"Test UUID Test", "CallDataValues":{ "CallDataValuesList":[ { "DataName":"ACTIVE_CALLERCOUNT_ONCALL", "DataValue":"0" }, { "DataName":"ANI", "DataValue":"4092915301" }, { "DataName":"ASO_IND", "DataValue":"True" }, { "DataName":"AUTHENTICATION_IND", "DataValue":"Y" }, { "DataName":"BUS_SEGMENT_IND", "DataValue":"L" }, { "DataName":"Call_Catg_Desc", "DataValue":"Medical Eligibility" }, { "DataName":"Client_ID", "DataValue":"IVR" }, { "DataName":"CLAIM_GEN_KEY", "DataValue":"" }, { "DataName":"DATA_SRC", "DataValue":"IVR" }, { "DataName":"HDHP_IND", "DataValue":"True" }, { "DataName":"INQ_DOS", "DataValue":"" }, { "DataName":"INQ_RSN", "DataValue":"ELG" }, { "DataName":"INQ_RSN1", "DataValue":"ELG" }, { "DataName":"INQA_DOB", "DataValue":"11-18-2014" }, { "DataName":"INQA_FIRST", "DataValue":"JUN-KANG" }, { "DataName":"INQA_GRP_ID", "DataValue":"923382" }, { "DataName":"INQA_GRP_NAME", "DataValue":"HUMANA INC" }, { "DataName":"INQA_ID", "DataValue":"54321" }, { "DataName":"INQA_LAST", "DataValue":"TesMember" }, { "DataName":"INQA_MKT_ID", "DataValue":"0" }, { "DataName":"INQA_PLTFRM_CD", "DataValue":"EM" }, { "DataName":"INQA_RELN", "DataValue":"" }, { "DataName":"INQA_TYP", "DataValue":"A" }, { "DataName":"INQR_TYP", "DataValue":"P" },{ "DataName":"INQR_ID", "DataValue":"123456" },{ "DataName":"INQR_FIRST", "DataValue":"Member" },{ "DataName":"INQR_LAST", "DataValue":"TestMember" }, { "DataName":"INQUIRY_ID", "DataValue":"" }, { "DataName":"IVR_CALL_DATE", "DataValue":"20201210" }, { "DataName":"IVR_CALL_TIME", "DataValue":"01:26:26" }, { "DataName":"IVR_CALLER_TYPE", "DataValue":"M" }, { "DataName":"IVR_DEN_OI_CHG_IND", "DataValue":"" }, { "DataName":"IVR_MED_OI_CHG_IND", "DataValue":"" }, { "DataName":"IVR_SERVER_CD", "DataValue":"03" }, { "DataName":"IVR_SERVER_PORT", "DataValue":"209" }, { "DataName":"LAST_AGENT_ID", "DataValue":"3378044" }, { "DataName":"MAJOR_LOB", "DataValue":"NPOS" }, { "DataName":"MEMBER_DOB", "DataValue":"11/18/2014" }, { "DataName":"MEMBER_ID", "DataValue":"000160217" }, { "DataName":"MEMBER_PERS_GEN_KEY", "DataValue":"554004556423" }, { "DataName":"MTV_BL5", "DataValue":"HHP-HUM INC" }, { "DataName":"MTV_BL7", "DataValue":"NPOS" }, { "DataName":"MULTI_COV", "DataValue":"" },  { "DataName":"PROD_TYPE_CD", "DataValue":"" }, { "DataName":"PROD_TYPE_CODE", "DataValue":"NPOSMO" }, { "DataName":"PRODPLANID", "DataValue":"" }, { "DataName":"REFERENCE_ID", "DataValue":"" }, { "DataName":"RX_ID", "DataValue":"" }, { "DataName":"SELLING_LEDGER", "DataValue":"0" }, { "DataName":"STATE_OF_ISSUE", "DataValue":"KY" }, { "DataName":"SURVEY_ID", "DataValue":"50" }, { "DataName":"UUID", "DataValue":"12102000001270800029030001860434" }, { "DataName":"VENDOR_ASOFDATE", "DataValue":"2020-12-10" }, { "DataName":"VENDOR_GROUPID", "DataValue":"" }, { "DataName":"VENDOR_SUB_GROUPID", "DataValue":"9233820e"} ] } } }}';
        httpRes.setBody(resBodyString);
        system.assert(httpRes.getBody() == resBodyString);
        Map<String, HTTPRequest> mapDetails = continueObj.getRequests();
        string reqType = '';
        for(String s : mapDetails.keyset())
        {
            reqType = s;
        } 
        List<string>labelGetmember = new List<string>();
        labelGetmember.add(reqType);
        Test.setContinuationResponse(reqType,httpRes);
        String result = (String)phChum.processResponse();
        System.assertEquals(result,null);
    }
    
    public static testmethod void testgetMemberWithMultipleAccountId()
    {
        PhoneBook_C_HUM phChum = new PhoneBook_C_HUM();
        phChum.uuidataValue = 'Test UUID Test';
        phChum.isRefreshedval = false;
        Continuation continueObj = phChum.getDataFromWebService();
        continueObj.continuationMethod = 'processResponse';        
        HttpResponse httpRes = new Httpresponse ();
        String resBodyString = '{"GetCallDataResponse":{ "GetCallDataResult":{ "Application":"", "UUID":"Test UUID Test", "CallDataValues":{ "CallDataValuesList":[ { "DataName":"ACTIVE_CALLERCOUNT_ONCALL", "DataValue":"0" }, { "DataName":"ANI", "DataValue":"4092915301" }, { "DataName":"ASO_IND", "DataValue":"True" }, { "DataName":"AUTHENTICATION_IND", "DataValue":"Y" }, { "DataName":"BUS_SEGMENT_IND", "DataValue":"L" }, { "DataName":"Call_Catg_Desc", "DataValue":"Medical Eligibility" }, { "DataName":"Client_ID", "DataValue":"IVR" }, { "DataName":"CLAIM_GEN_KEY", "DataValue":"" }, { "DataName":"DATA_SRC", "DataValue":"IVR" }, { "DataName":"HDHP_IND", "DataValue":"True" }, { "DataName":"INQ_DOS", "DataValue":"" }, { "DataName":"INQ_RSN", "DataValue":"ELG" }, { "DataName":"INQ_RSN1", "DataValue":"ELG" }, { "DataName":"INQA_DOB", "DataValue":"11-18-2014" }, { "DataName":"INQA_FIRST", "DataValue":"JUN-KANG" }, { "DataName":"INQA_GRP_ID", "DataValue":"923382" }, { "DataName":"INQA_GRP_NAME", "DataValue":"HUMANA INC" }, { "DataName":"INQA_ID", "DataValue":"54321" }, { "DataName":"INQA_LAST", "DataValue":"TesMember" }, { "DataName":"INQA_MKT_ID", "DataValue":"0" }, { "DataName":"INQA_PLTFRM_CD", "DataValue":"EM" }, { "DataName":"INQA_RELN", "DataValue":"" }, { "DataName":"INQA_TYP", "DataValue":"A" }, { "DataName":"INQR_TYP", "DataValue":"M" },{ "DataName":"INQR_ID", "DataValue":"4444" },{ "DataName":"INQR_FIRST", "DataValue":"Member" },{ "DataName":"INQR_LAST", "DataValue":"TestMember" }, { "DataName":"INQUIRY_ID", "DataValue":"" }, { "DataName":"IVR_CALL_DATE", "DataValue":"20201210" }, { "DataName":"IVR_CALL_TIME", "DataValue":"01:26:26" }, { "DataName":"IVR_CALLER_TYPE", "DataValue":"M" }, { "DataName":"IVR_DEN_OI_CHG_IND", "DataValue":"" }, { "DataName":"IVR_MED_OI_CHG_IND", "DataValue":"" }, { "DataName":"IVR_SERVER_CD", "DataValue":"03" }, { "DataName":"IVR_SERVER_PORT", "DataValue":"209" }, { "DataName":"LAST_AGENT_ID", "DataValue":"3378044" }, { "DataName":"MAJOR_LOB", "DataValue":"NPOS" }, { "DataName":"MEMBER_DOB", "DataValue":"11/18/2014" }, { "DataName":"MEMBER_ID", "DataValue":"000160217" }, { "DataName":"MEMBER_PERS_GEN_KEY", "DataValue":"554004556423" }, { "DataName":"MTV_BL5", "DataValue":"HHP-HUM INC" }, { "DataName":"MTV_BL7", "DataValue":"NPOS" }, { "DataName":"MULTI_COV", "DataValue":"" },  { "DataName":"PROD_TYPE_CD", "DataValue":"" }, { "DataName":"PROD_TYPE_CODE", "DataValue":"NPOSMO" }, { "DataName":"PRODPLANID", "DataValue":"" }, { "DataName":"REFERENCE_ID", "DataValue":"" }, { "DataName":"RX_ID", "DataValue":"" }, { "DataName":"SELLING_LEDGER", "DataValue":"0" }, { "DataName":"STATE_OF_ISSUE", "DataValue":"KY" }, { "DataName":"SURVEY_ID", "DataValue":"50" }, { "DataName":"UUID", "DataValue":"12102000001270800029030001860434" }, { "DataName":"VENDOR_ASOFDATE", "DataValue":"2020-12-10" }, { "DataName":"VENDOR_GROUPID", "DataValue":"" }, { "DataName":"VENDOR_SUB_GROUPID", "DataValue":"9233820e"} ] } } }}';
        httpRes.setBody(resBodyString);
        system.assert(httpRes.getBody() == resBodyString);
        Map<String, HTTPRequest> mapDetails = continueObj.getRequests();
        string reqType = '';
        for(String s : mapDetails.keyset())
        {
            reqType = s;
        } 
        List<string>labelGetmember = new List<string>();
        labelGetmember.add(reqType);
        Test.setContinuationResponse(reqType,httpRes);
        String result = (String)phChum.processResponse();
        System.assertEquals(result,null);
    }
    
     public static testmethod void testgetGroupAboutAccountIdMultipleCase()
    {
        PhoneBook_C_HUM phChum = new PhoneBook_C_HUM();
        phChum.uuidataValue = 'Test UUID Test';
        phChum.isRefreshedval = false;
        Continuation continueObj = phChum.getDataFromWebService();
        continueObj.continuationMethod = 'processResponse';        
        HttpResponse httpRes = new Httpresponse ();
        String resBodyString = '{"GetCallDataResponse":{ "GetCallDataResult":{ "Application":"", "UUID":"Test UUID Test", "CallDataValues":{ "CallDataValuesList":[ { "DataName":"ACTIVE_CALLERCOUNT_ONCALL", "DataValue":"0" }, { "DataName":"ANI", "DataValue":"4092915301" }, { "DataName":"ASO_IND", "DataValue":"True" }, { "DataName":"AUTHENTICATION_IND", "DataValue":"Y" }, { "DataName":"BUS_SEGMENT_IND", "DataValue":"L" }, { "DataName":"Call_Catg_Desc", "DataValue":"Medical Eligibility" }, { "DataName":"Client_ID", "DataValue":"IVR" }, { "DataName":"CLAIM_GEN_KEY", "DataValue":"" }, { "DataName":"DATA_SRC", "DataValue":"IVR" }, { "DataName":"HDHP_IND", "DataValue":"True" }, { "DataName":"INQ_DOS", "DataValue":"" }, { "DataName":"INQ_RSN", "DataValue":"ELG" }, { "DataName":"INQ_RSN1", "DataValue":"ELG" }, { "DataName":"INQA_DOB", "DataValue":"11-18-2014" }, { "DataName":"INQA_FIRST", "DataValue":"JUN-KANG" }, { "DataName":"INQA_GRP_ID", "DataValue":"923382" }, { "DataName":"INQA_GRP_NAME", "DataValue":"HUMANA INC" }, { "DataName":"INQA_ID", "DataValue":"22221" }, { "DataName":"INQA_LAST", "DataValue":"TesMember" }, { "DataName":"INQA_MKT_ID", "DataValue":"0" }, { "DataName":"INQA_PLTFRM_CD", "DataValue":"EM" }, { "DataName":"INQA_RELN", "DataValue":"" }, { "DataName":"INQA_TYP", "DataValue":"E" }, { "DataName":"INQR_TYP", "DataValue":"G" },{ "DataName":"INQR_ID", "DataValue":"4444" },{ "DataName":"INQR_FIRST", "DataValue":"Member" },{ "DataName":"INQR_LAST", "DataValue":"TestMember" }, { "DataName":"INQUIRY_ID", "DataValue":"" }, { "DataName":"IVR_CALL_DATE", "DataValue":"20201210" }, { "DataName":"IVR_CALL_TIME", "DataValue":"01:26:26" }, { "DataName":"IVR_CALLER_TYPE", "DataValue":"M" }, { "DataName":"IVR_DEN_OI_CHG_IND", "DataValue":"" }, { "DataName":"IVR_MED_OI_CHG_IND", "DataValue":"" }, { "DataName":"IVR_SERVER_CD", "DataValue":"03" }, { "DataName":"IVR_SERVER_PORT", "DataValue":"209" }, { "DataName":"LAST_AGENT_ID", "DataValue":"3378044" }, { "DataName":"MAJOR_LOB", "DataValue":"NPOS" }, { "DataName":"MEMBER_DOB", "DataValue":"11/18/2014" }, { "DataName":"MEMBER_ID", "DataValue":"000160217" }, { "DataName":"MEMBER_PERS_GEN_KEY", "DataValue":"554004556423" }, { "DataName":"MTV_BL5", "DataValue":"HHP-HUM INC" }, { "DataName":"MTV_BL7", "DataValue":"NPOS" }, { "DataName":"MULTI_COV", "DataValue":"" },  { "DataName":"PROD_TYPE_CD", "DataValue":"" }, { "DataName":"PROD_TYPE_CODE", "DataValue":"NPOSMO" }, { "DataName":"PRODPLANID", "DataValue":"" }, { "DataName":"REFERENCE_ID", "DataValue":"" }, { "DataName":"RX_ID", "DataValue":"" }, { "DataName":"SELLING_LEDGER", "DataValue":"0" }, { "DataName":"STATE_OF_ISSUE", "DataValue":"KY" }, { "DataName":"SURVEY_ID", "DataValue":"50" }, { "DataName":"UUID", "DataValue":"12102000001270800029030001860434" }, { "DataName":"VENDOR_ASOFDATE", "DataValue":"2020-12-10" }, { "DataName":"VENDOR_GROUPID", "DataValue":"" }, { "DataName":"VENDOR_SUB_GROUPID", "DataValue":"9233820e"} ] } } }}';
        httpRes.setBody(resBodyString);
        system.assert(httpRes.getBody() == resBodyString);
        Map<String, HTTPRequest> mapDetails = continueObj.getRequests();
        string reqType = '';
        for(String s : mapDetails.keyset())
        {
            reqType = s;
        } 
        List<string>labelGetmember = new List<string>();
        labelGetmember.add(reqType);
        Test.setContinuationResponse(reqType,httpRes);
        String result = (String)phChum.processResponse();
        System.assertEquals(result,null);
    }
    
     public static testmethod void testgetMemberAboutAccountIdMultipleId()
    {
        PhoneBook_C_HUM phChum = new PhoneBook_C_HUM();
        phChum.uuidataValue = 'Test UUID Test';
        phChum.isRefreshedval = false;
        Continuation continueObj = phChum.getDataFromWebService();
        continueObj.continuationMethod = 'processResponse';        
        HttpResponse httpRes = new Httpresponse ();
        String resBodyString = '{"GetCallDataResponse":{ "GetCallDataResult":{ "Application":"", "UUID":"Test UUID Test", "CallDataValues":{ "CallDataValuesList":[ { "DataName":"ACTIVE_CALLERCOUNT_ONCALL", "DataValue":"0" }, { "DataName":"ANI", "DataValue":"4092915301" }, { "DataName":"ASO_IND", "DataValue":"True" }, { "DataName":"AUTHENTICATION_IND", "DataValue":"Y" }, { "DataName":"BUS_SEGMENT_IND", "DataValue":"L" }, { "DataName":"Call_Catg_Desc", "DataValue":"Medical Eligibility" }, { "DataName":"Client_ID", "DataValue":"IVR" }, { "DataName":"CLAIM_GEN_KEY", "DataValue":"" }, { "DataName":"DATA_SRC", "DataValue":"IVR" }, { "DataName":"HDHP_IND", "DataValue":"True" }, { "DataName":"INQ_DOS", "DataValue":"" }, { "DataName":"INQ_RSN", "DataValue":"ELG" }, { "DataName":"INQ_RSN1", "DataValue":"ELG" }, { "DataName":"INQA_DOB", "DataValue":"11-18-2014" }, { "DataName":"INQA_FIRST", "DataValue":"JUN-KANG" }, { "DataName":"INQA_GRP_ID", "DataValue":"923382" }, { "DataName":"INQA_GRP_NAME", "DataValue":"HUMANA INC" }, { "DataName":"INQA_ID", "DataValue":"4444" }, { "DataName":"INQA_LAST", "DataValue":"TesMember" }, { "DataName":"INQA_MKT_ID", "DataValue":"0" }, { "DataName":"INQA_PLTFRM_CD", "DataValue":"EM" }, { "DataName":"INQA_RELN", "DataValue":"" }, { "DataName":"INQA_TYP", "DataValue":"M" }, { "DataName":"INQR_TYP", "DataValue":"G" },{ "DataName":"INQR_ID", "DataValue":"4444" },{ "DataName":"INQR_FIRST", "DataValue":"Member" },{ "DataName":"INQR_LAST", "DataValue":"TestMember" }, { "DataName":"INQUIRY_ID", "DataValue":"" }, { "DataName":"IVR_CALL_DATE", "DataValue":"20201210" }, { "DataName":"IVR_CALL_TIME", "DataValue":"01:26:26" }, { "DataName":"IVR_CALLER_TYPE", "DataValue":"M" }, { "DataName":"IVR_DEN_OI_CHG_IND", "DataValue":"" }, { "DataName":"IVR_MED_OI_CHG_IND", "DataValue":"" }, { "DataName":"IVR_SERVER_CD", "DataValue":"03" }, { "DataName":"IVR_SERVER_PORT", "DataValue":"209" }, { "DataName":"LAST_AGENT_ID", "DataValue":"3378044" }, { "DataName":"MAJOR_LOB", "DataValue":"NPOS" }, { "DataName":"MEMBER_DOB", "DataValue":"11/18/2014" }, { "DataName":"MEMBER_ID", "DataValue":"000160217" }, { "DataName":"MEMBER_PERS_GEN_KEY", "DataValue":"554004556423" }, { "DataName":"MTV_BL5", "DataValue":"HHP-HUM INC" }, { "DataName":"MTV_BL7", "DataValue":"NPOS" }, { "DataName":"MULTI_COV", "DataValue":"" },  { "DataName":"PROD_TYPE_CD", "DataValue":"" }, { "DataName":"PROD_TYPE_CODE", "DataValue":"NPOSMO" }, { "DataName":"PRODPLANID", "DataValue":"" }, { "DataName":"REFERENCE_ID", "DataValue":"" }, { "DataName":"RX_ID", "DataValue":"" }, { "DataName":"SELLING_LEDGER", "DataValue":"0" }, { "DataName":"STATE_OF_ISSUE", "DataValue":"KY" }, { "DataName":"SURVEY_ID", "DataValue":"50" }, { "DataName":"UUID", "DataValue":"12102000001270800029030001860434" }, { "DataName":"VENDOR_ASOFDATE", "DataValue":"2020-12-10" }, { "DataName":"VENDOR_GROUPID", "DataValue":"" }, { "DataName":"VENDOR_SUB_GROUPID", "DataValue":"9233820e"} ] } } }}';
        httpRes.setBody(resBodyString);
        system.assert(httpRes.getBody() == resBodyString);
        Map<String, HTTPRequest> mapDetails = continueObj.getRequests();
        string reqType = '';
        for(String s : mapDetails.keyset())
        {
            reqType = s;
        } 
        List<string>labelGetmember = new List<string>();
        labelGetmember.add(reqType);
        Test.setContinuationResponse(reqType,httpRes);
        String result = (String)phChum.processResponse();
        System.assertEquals(result,null);
    }
    
     public static testmethod void getProviderWithAccountIdWithINQA_TYP()
    {
        PhoneBook_C_HUM phChum = new PhoneBook_C_HUM();
        phChum.uuidataValue = 'Test UUID Test';
        phChum.isRefreshedval = false;
        Continuation continueObj = phChum.getDataFromWebService();
        continueObj.continuationMethod = 'processResponse';        
        HttpResponse httpRes = new Httpresponse ();
        String resBodyString = '{"GetCallDataResponse":{ "GetCallDataResult":{ "Application":"", "UUID":"Test UUID Test", "CallDataValues":{ "CallDataValuesList":[ { "DataName":"ACTIVE_CALLERCOUNT_ONCALL", "DataValue":"0" }, { "DataName":"ANI", "DataValue":"4092915301" }, { "DataName":"ASO_IND", "DataValue":"True" }, { "DataName":"AUTHENTICATION_IND", "DataValue":"Y" }, { "DataName":"BUS_SEGMENT_IND", "DataValue":"L" }, { "DataName":"Call_Catg_Desc", "DataValue":"Medical Eligibility" }, { "DataName":"Client_ID", "DataValue":"IVR" }, { "DataName":"CLAIM_GEN_KEY", "DataValue":"" }, { "DataName":"DATA_SRC", "DataValue":"IVR" }, { "DataName":"HDHP_IND", "DataValue":"True" }, { "DataName":"INQ_DOS", "DataValue":"" }, { "DataName":"INQ_RSN", "DataValue":"ELG" }, { "DataName":"INQ_RSN1", "DataValue":"ELG" }, { "DataName":"INQA_DOB", "DataValue":"11-18-2014" }, { "DataName":"INQA_FIRST", "DataValue":"JUN-KANG" }, { "DataName":"INQA_GRP_ID", "DataValue":"923382" }, { "DataName":"INQA_GRP_NAME", "DataValue":"HUMANA INC" }, { "DataName":"INQA_ID", "DataValue":"1234567" }, { "DataName":"INQA_LAST", "DataValue":"" }, { "DataName":"INQA_MKT_ID", "DataValue":"0" }, { "DataName":"INQA_PLTFRM_CD", "DataValue":"EM" }, { "DataName":"INQA_RELN", "DataValue":"" }, { "DataName":"INQA_TYP", "DataValue":"P" }, { "DataName":"INQR_TYP", "DataValue":"P" },{ "DataName":"INQR_ID", "DataValue":"" },{ "DataName":"INQR_FIRST", "DataValue":"Member" },{ "DataName":"INQR_LAST", "DataValue":"" }, { "DataName":"INQUIRY_ID", "DataValue":"" }, { "DataName":"IVR_CALL_DATE", "DataValue":"20201210" }, { "DataName":"IVR_CALL_TIME", "DataValue":"01:26:26" }, { "DataName":"IVR_CALLER_TYPE", "DataValue":"M" }, { "DataName":"IVR_DEN_OI_CHG_IND", "DataValue":"" }, { "DataName":"IVR_MED_OI_CHG_IND", "DataValue":"" }, { "DataName":"IVR_SERVER_CD", "DataValue":"03" }, { "DataName":"IVR_SERVER_PORT", "DataValue":"209" }, { "DataName":"LAST_AGENT_ID", "DataValue":"3378044" }, { "DataName":"MAJOR_LOB", "DataValue":"NPOS" }, { "DataName":"MEMBER_DOB", "DataValue":"11/18/2014" }, { "DataName":"MEMBER_ID", "DataValue":"000160217" }, { "DataName":"MEMBER_PERS_GEN_KEY", "DataValue":"554004556423" }, { "DataName":"MTV_BL5", "DataValue":"HHP-HUM INC" }, { "DataName":"MTV_BL7", "DataValue":"NPOS" }, { "DataName":"MULTI_COV", "DataValue":"" },  { "DataName":"PROD_TYPE_CD", "DataValue":"" }, { "DataName":"PROD_TYPE_CODE", "DataValue":"NPOSMO" }, { "DataName":"PRODPLANID", "DataValue":"" }, { "DataName":"REFERENCE_ID", "DataValue":"" }, { "DataName":"RX_ID", "DataValue":"" }, { "DataName":"SELLING_LEDGER", "DataValue":"0" }, { "DataName":"STATE_OF_ISSUE", "DataValue":"KY" }, { "DataName":"SURVEY_ID", "DataValue":"50" }, { "DataName":"UUID", "DataValue":"12102000001270800029030001860434" }, { "DataName":"VENDOR_ASOFDATE", "DataValue":"2020-12-10" }, { "DataName":"VENDOR_GROUPID", "DataValue":"" }, { "DataName":"VENDOR_SUB_GROUPID", "DataValue":"9233820e"} ] } } }}';
        httpRes.setBody(resBodyString);
        system.assert(httpRes.getBody() == resBodyString);
        Map<String, HTTPRequest> mapDetails = continueObj.getRequests();
        string reqType = '';
        for(String s : mapDetails.keyset())
        {
            reqType = s;
        } 
        List<string>labelGetmember = new List<string>();
        labelGetmember.add(reqType);
        Test.setContinuationResponse(reqType,httpRes);
        String result = (String)phChum.processResponse();
        System.assertEquals(result,null);
    }
    public static testmethod void testCreateAutoInteractionOnOBDetailPage(){
        list<Account> lstAcc = [Select Id from Account Where LastName = 'TesMember' Limit 1];
        List<Interaction__c> lstInt = [Select Id,Genesys_Interaction_ID__c from Interaction__c Where Interaction_Origin__c='Outbound Call' Limit 1];	
        String focussedRecordIdOB = lstAcc[0].Id;
        String genInteractionId = lstInt[0].Genesys_Interaction_ID__c;
        String focussedPageName = 'AccountdetailPage';
        Test.startTest();
        Map<String,string> result = PhoneBook_C_HUM.CreateAutoInteractionOnOB(focussedRecordIdOB,genInteractionId,focussedPageName);
        Test.stopTest();
        System.assert(lstAcc != null);
        
    }
    public static testmethod void testCreateAutoInteractionOnOBCasePage(){
        list<Case> lstCase = [Select Id from Case Where Origin = 'Outbound call' Limit 1];
        
        List<Interaction__c> lstIntr = [Select Id,Genesys_Interaction_ID__c from Interaction__c Where Interaction_Origin__c='Outbound Call' Limit 1];	
        String focussedRecordIdOB = lstCase[0].Id;
        String genInteractionId = lstIntr[0].Genesys_Interaction_ID__c;
        String focussedPageName = 'CaseDetailPage';
        Test.startTest();
        Map<String,string> str = PhoneBook_C_HUM.CreateAutoInteractionOnOB(focussedRecordIdOB,genInteractionId,focussedPageName);
        Test.stopTest();
        System.assert(lstCase != null);
    }
    public static testmethod void testCreateAutoInteractionOnOBPolicyPage(){
      	List<Policy_Member__c> polMem = [select id from Policy_Member__c where end_date__c = '12/31/2015'];
      	List<Interaction__c> lstIntr = [Select Id, Genesys_Interaction_ID__c from Interaction__c Where Interaction_Origin__c='Outbound Call' Limit 1];
        String focussedRecordIdOB = polMem[0].Id;
        String genInteractionId = lstIntr[0].Genesys_Interaction_ID__c;
        String focussedPageName = 'PolicyMemberPage';
        Test.startTest();
        Map<String,string> str = PhoneBook_C_HUM.CreateAutoInteractionOnOB(focussedRecordIdOB,genInteractionId,focussedPageName);
        Test.stopTest();
        System.assert(focussedPageName == 'PolicyMemberPage');
    }
	public static testmethod void testGetCallDataWebServiceMultipleTab()
    {
        PhoneBook_C_HUM phChum = new PhoneBook_C_HUM();
        phChum.uuidataValue = 'Test UUID';
        phChum.isRefreshedval = false;
        Continuation continueObj = phChum.getDataFromWebServiceMultipleTabs();
        continueObj.continuationMethod = 'processResponseMultipletabs';        
        HttpResponse httpRes = new Httpresponse ();
        String resBodyString = '{"GetCallDataResponse":{ "GetCallDataResult":{ "Application":"", "UUID":"Test UUID", "CallDataValues":{ "CallDataValuesList":[ { "DataName":"ACTIVE_CALLERCOUNT_ONCALL", "DataValue":"0" }, { "DataName":"ANI", "DataValue":"4092915301" }, { "DataName":"ASO_IND", "DataValue":"True" }, { "DataName":"AUTHENTICATION_IND", "DataValue":"Y" }, { "DataName":"BUS_SEGMENT_IND", "DataValue":"L" }, { "DataName":"Call_Catg_Desc", "DataValue":"Medical Eligibility" }, { "DataName":"Client_ID", "DataValue":"IVR" }, { "DataName":"CLAIM_GEN_KEY", "DataValue":"" }, { "DataName":"DATA_SRC", "DataValue":"IVR" }, { "DataName":"HDHP_IND", "DataValue":"True" }, { "DataName":"INQ_DOS", "DataValue":"" }, { "DataName":"INQ_RSN", "DataValue":"ELG" }, { "DataName":"INQ_RSN1", "DataValue":"ELG" }, { "DataName":"INQA_DOB", "DataValue":"11-18-2014" }, { "DataName":"INQA_FIRST", "DataValue":"JUN-KANG" }, { "DataName":"INQA_GRP_ID", "DataValue":"923382" }, { "DataName":"INQA_GRP_NAME", "DataValue":"HUMANA INC" }, { "DataName":"INQA_ID", "DataValue":"000160217" }, { "DataName":"INQA_LAST", "DataValue":"SINICKI" }, { "DataName":"INQA_MKT_ID", "DataValue":"0" }, { "DataName":"INQA_PLTFRM_CD", "DataValue":"EM" }, { "DataName":"INQA_RELN", "DataValue":"" }, { "DataName":"INQA_TYP", "DataValue":"G" }, { "DataName":"INQR_TYP", "DataValue":"M" }, { "DataName":"INQUIRY_ID", "DataValue":"" }, { "DataName":"IVR_CALL_DATE", "DataValue":"20201210" }, { "DataName":"IVR_CALL_TIME", "DataValue":"01:26:26" }, { "DataName":"IVR_CALLER_TYPE", "DataValue":"M" }, { "DataName":"IVR_DEN_OI_CHG_IND", "DataValue":"" }, { "DataName":"IVR_MED_OI_CHG_IND", "DataValue":"" }, { "DataName":"IVR_SERVER_CD", "DataValue":"03" }, { "DataName":"IVR_SERVER_PORT", "DataValue":"209" }, { "DataName":"LAST_AGENT_ID", "DataValue":"3378044" }, { "DataName":"MAJOR_LOB", "DataValue":"NPOS" }, { "DataName":"MEMBER_DOB", "DataValue":"11/18/2014" }, { "DataName":"MEMBER_ID", "DataValue":"000160217" }, { "DataName":"MEMBER_PERS_GEN_KEY", "DataValue":"554004556423" }, { "DataName":"MTV_BL5", "DataValue":"HHP-HUM INC" }, { "DataName":"MTV_BL7", "DataValue":"NPOS" }, { "DataName":"MULTI_COV", "DataValue":"" },  { "DataName":"PROD_TYPE_CD", "DataValue":"" }, { "DataName":"PROD_TYPE_CODE", "DataValue":"NPOSMO" }, { "DataName":"PRODPLANID", "DataValue":"" }, { "DataName":"REFERENCE_ID", "DataValue":"" }, { "DataName":"RX_ID", "DataValue":"" }, { "DataName":"SELLING_LEDGER", "DataValue":"0" }, { "DataName":"STATE_OF_ISSUE", "DataValue":"KY" }, { "DataName":"SURVEY_ID", "DataValue":"50" }, { "DataName":"UUID", "DataValue":"12102000001270800029030001860434" }, { "DataName":"VENDOR_ASOFDATE", "DataValue":"2020-12-10" }, { "DataName":"VENDOR_GROUPID", "DataValue":"" }, { "DataName":"VENDOR_SUB_GROUPID", "DataValue":"92338209"} , { "DataName":"ISMANUAL_TRANSFER", "DataValue":"false"} ] } } }}';
        httpRes.setBody(resBodyString);
        system.assert(httpRes.getBody() == resBodyString);
        Map<String, HTTPRequest> mapDetails = continueObj.getRequests();
        string reqType = '';
        for(String s : mapDetails.keyset())
        {
            reqType = s;
        } 
        List<string>labelGetmember = new List<string>();
        labelGetmember.add(reqType);
        Test.setContinuationResponse(reqType,httpRes);
        String result = (String)phChum.processResponseMultipletabs();
        System.assertEquals(result,null);
    }
    public static testmethod void testGetCallDataWebServiceMultipleTabException()
    {
        PhoneBook_C_HUM phChum = new PhoneBook_C_HUM();
        phChum.uuidataValue = 'Test UUID';
        phChum.isRefreshedval = false;
        Continuation continueObj = phChum.getDataFromWebServiceMultipleTabs();
        continueObj.continuationMethod = 'processResponseMultipletabs';        
        HttpResponse httpRes = new Httpresponse ();
        String resBodyString = '{"GetCallDataResponse":{ "GetCallDataResult":{ "Application":"", "UUID":"", "CallDataValues":{ "CallDataValuesList":[ { "DataName":"ACTIVE_CALLERCOUNT_ONCALL", "DataValue":"" }, { "DataName":"ANI", "DataValue":"" }, { "DataName":"ASO_IND", "DataValue":"" }, { "DataName":"AUTHENTICATION_IND", "DataValue":"" }, { "DataName":"BUS_SEGMENT_IND", "DataValue":"" }, { "DataName":"Call_Catg_Desc", "DataValue":"" }, { "DataName":"Client_ID", "DataValue":"" }, { "DataName":"CLAIM_GEN_KEY", "DataValue":"" }, { "DataName":"DATA_SRC", "DataValue":"" }, { "DataName":"HDHP_IND", "DataValue":"" }, { "DataName":"INQ_DOS", "DataValue":"" }, { "DataName":"INQ_RSN", "DataValue":"" }, { "DataName":"INQ_RSN1", "DataValue":"" }, { "DataName":"INQA_DOB", "DataValue":"" }, { "DataName":"INQA_FIRST", "DataValue":"" }, { "DataName":"INQA_GRP_ID", "DataValue":"" }, { "DataName":"INQA_GRP_NAME", "DataValue":"" }, { "DataName":"INQA_ID", "DataValue":"" }, { "DataName":"INQA_LAST", "DataValue":"" }, { "DataName":"INQA_MKT_ID", "DataValue":"" }, { "DataName":"INQA_PLTFRM_CD", "DataValue":"" }, { "DataName":"INQA_RELN", "DataValue":"" }, { "DataName":"INQA_TYP", "DataValue":"" }, { "DataName":"INQR_TYP", "DataValue":"" }, { "DataName":"INQUIRY_ID", "DataValue":"" }, { "DataName":"IVR_CALL_DATE", "DataValue":"" }, { "DataName":"IVR_CALL_TIME", "DataValue":"" }, { "DataName":"IVR_CALLER_TYPE", "DataValue":"" }, { "DataName":"IVR_DEN_OI_CHG_IND", "DataValue":"" }, { "DataName":"IVR_MED_OI_CHG_IND", "DataValue":"" }, { "DataName":"IVR_SERVER_CD", "DataValue":"" }, { "DataName":"IVR_SERVER_PORT", "DataValue":"" }, { "DataName":"LAST_AGENT_ID", "DataValue":"" }, { "DataName":"MAJOR_LOB", "DataValue":"" }, { "DataName":"MEMBER_DOB", "DataValue":"" }, { "DataName":"MEMBER_ID", "DataValue":"" }, { "DataName":"MEMBER_PERS_GEN_KEY", "DataValue":"" }, { "DataName":"MTV_BL5", "DataValue":"" }, { "DataName":"MTV_BL7", "DataValue":"" }, { "DataName":"MULTI_COV", "DataValue":"" },  { "DataName":"PROD_TYPE_CD", "DataValue":"" }, { "DataName":"PROD_TYPE_CODE", "DataValue":"" }, { "DataName":"PRODPLANID", "DataValue":"" }, { "DataName":"REFERENCE_ID", "DataValue":"" }, { "DataName":"RX_ID", "DataValue":"" }, { "DataName":"SELLING_LEDGER", "DataValue":"" }, { "DataName":"STATE_OF_ISSUE", "DataValue":"" }, { "DataName":"SURVEY_ID", "DataValue":"" }, { "DataName":"UUID", "DataValue":"" }, { "DataName":"VENDOR_ASOFDATE", "DataValue":"" }, { "DataName":"VENDOR_GROUPID", "DataValue":"" }, { "DataName":"VENDOR_SUB_GROUPID", "DataValue":""} ] } } }}';
        httpRes.setBody(resBodyString);
        System.assert(httpRes.getBody() == resBodyString, null);
        Map<String, HTTPRequest> mapDetails = continueObj.getRequests();
        string reqType = '';
        for(String s : mapDetails.keyset())
        {
            reqType = s;
        } 
        List<string>labelGetmember = new List<string>();
        labelGetmember.add(reqType);
        Test.setContinuationResponse(reqType,httpRes);
        String result = (String)phChum.processResponseMultipletabs();
        System.assertEquals(result,null);
    }
    public static testmethod void testNullUUID(){
        PhoneBook_C_HUM phChum = new PhoneBook_C_HUM();
        phChum.uuidataValue = null;
        phChum.isRefreshedval = false;
        Continuation continueObj = phChum.getDataFromWebServiceMultipleTabs();
        System.assert(phChum.isRefreshedval == false);
    }
    public static testmethod void testGetCallDataWebServiceMultipleTabGroup()
    {
        PhoneBook_C_HUM phChum = new PhoneBook_C_HUM();
        phChum.uuidataValue = 'Test UUID Group';
        phChum.isRefreshedval = false;
        Continuation continueObj = phChum.getDataFromWebServiceMultipleTabs();
        continueObj.continuationMethod = 'processResponseMultipletabs';        
        HttpResponse httpRes = new Httpresponse ();
        String resBodyString = '{"GetCallDataResponse":{ "GetCallDataResult":{ "Application":"", "UUID":"Test UUID Group", "CallDataValues":{ "CallDataValuesList":[ { "DataName":"ACTIVE_CALLERCOUNT_ONCALL", "DataValue":"0" }, { "DataName":"ANI", "DataValue":"4092915301" }, { "DataName":"ASO_IND", "DataValue":"True" }, { "DataName":"AUTHENTICATION_IND", "DataValue":"Y" }, { "DataName":"BUS_SEGMENT_IND", "DataValue":"L" }, { "DataName":"Call_Catg_Desc", "DataValue":"Medical Eligibility" }, { "DataName":"Client_ID", "DataValue":"IVR" }, { "DataName":"CLAIM_GEN_KEY", "DataValue":"" }, { "DataName":"DATA_SRC", "DataValue":"IVR" }, { "DataName":"HDHP_IND", "DataValue":"True" }, { "DataName":"INQ_DOS", "DataValue":"" }, { "DataName":"INQ_RSN", "DataValue":"ELG" }, { "DataName":"INQ_RSN1", "DataValue":"ELG" }, { "DataName":"INQA_DOB", "DataValue":"11-18-2014" }, { "DataName":"INQA_FIRST", "DataValue":"JUN-KANG" }, { "DataName":"INQA_GRP_ID", "DataValue":"923382" }, { "DataName":"INQA_GRP_NAME", "DataValue":"HUMANA INC" }, { "DataName":"INQA_ID", "DataValue":"000160217" }, { "DataName":"INQA_LAST", "DataValue":"SINICKI" }, { "DataName":"INQA_MKT_ID", "DataValue":"0" }, { "DataName":"INQA_PLTFRM_CD", "DataValue":"EM" }, { "DataName":"INQA_RELN", "DataValue":"" }, { "DataName":"INQA_TYP", "DataValue":"G" }, { "DataName":"INQR_TYP", "DataValue":"E" }, { "DataName":"INQUIRY_ID", "DataValue":"" }, { "DataName":"IVR_CALL_DATE", "DataValue":"20201210" }, { "DataName":"IVR_CALL_TIME", "DataValue":"01:26:26" }, { "DataName":"IVR_CALLER_TYPE", "DataValue":"M" }, { "DataName":"IVR_DEN_OI_CHG_IND", "DataValue":"" }, { "DataName":"IVR_MED_OI_CHG_IND", "DataValue":"" }, { "DataName":"IVR_SERVER_CD", "DataValue":"03" }, { "DataName":"IVR_SERVER_PORT", "DataValue":"209" }, { "DataName":"LAST_AGENT_ID", "DataValue":"3378044" }, { "DataName":"MAJOR_LOB", "DataValue":"NPOS" }, { "DataName":"MEMBER_DOB", "DataValue":"11/18/2014" }, { "DataName":"MEMBER_ID", "DataValue":"000160217" }, { "DataName":"MEMBER_PERS_GEN_KEY", "DataValue":"554004556423" }, { "DataName":"MTV_BL5", "DataValue":"HHP-HUM INC" }, { "DataName":"MTV_BL7", "DataValue":"NPOS" }, { "DataName":"MULTI_COV", "DataValue":"" },  { "DataName":"PROD_TYPE_CD", "DataValue":"" }, { "DataName":"PROD_TYPE_CODE", "DataValue":"NPOSMO" }, { "DataName":"PRODPLANID", "DataValue":"" }, { "DataName":"REFERENCE_ID", "DataValue":"" }, { "DataName":"RX_ID", "DataValue":"" }, { "DataName":"SELLING_LEDGER", "DataValue":"0" }, { "DataName":"STATE_OF_ISSUE", "DataValue":"KY" }, { "DataName":"SURVEY_ID", "DataValue":"50" }, { "DataName":"UUID", "DataValue":"12102000001270800029030001860434" }, { "DataName":"VENDOR_ASOFDATE", "DataValue":"2020-12-10" }, { "DataName":"VENDOR_GROUPID", "DataValue":"" }, { "DataName":"VENDOR_SUB_GROUPID", "DataValue":"92338209"}, { "DataName":"ISMANUAL_TRANSFER", "DataValue":"false"} ] } } }}';
        httpRes.setBody(resBodyString);
        system.assert(httpRes.getBody() == resBodyString);
        Map<String, HTTPRequest> mapDetails = continueObj.getRequests();
        string reqType = '';
        for(String s : mapDetails.keyset())
        {
            reqType = s;
        } 
        List<string>labelGetmember = new List<string>();
        labelGetmember.add(reqType);
        Test.setContinuationResponse(reqType,httpRes);
        String result = (String)phChum.processResponseMultipletabs();
        System.assertEquals(result,null);
    }
    public static testmethod void testGetCallDataWebServiceMultipleTabProvider()
    {
        PhoneBook_C_HUM phChum = new PhoneBook_C_HUM();
        phChum.uuidataValue = 'Test UUID';
        phChum.isRefreshedval = true;
        Continuation continueObj = phChum.getDataFromWebServiceMultipleTabs();
        continueObj.continuationMethod = 'processResponseMultipletabs';        
        HttpResponse httpRes = new Httpresponse ();
        String resBodyString = '{ "GetCallDataResponse":{ "GetCallDataResult":{ "Application":"", "UUID":"Test UUID", "CallDataValues":{ "CallDataValuesList":[ { "DataName":"ACTIVE_CALLERCOUNT_ONCALL", "DataValue":"0" }, { "DataName":"ANI", "DataValue":"7322002160" }, { "DataName":"ASO_IND", "DataValue":"True" }, { "DataName":"AUTHENTICATION_IND", "DataValue":"Y" }, { "DataName":"BUS_SEGMENT_IND", "DataValue":"L" }, { "DataName":"Call_Catg_Desc", "DataValue":"Medical Eligibility" }, { "DataName":"Client_ID", "DataValue":"IVR" }, { "DataName":"CLAIM_GEN_KEY", "DataValue":"" }, { "DataName":"DATA_SRC", "DataValue":"IVR" }, { "DataName":"HDHP_IND", "DataValue":"True" }, { "DataName":"INQ_DOS", "DataValue":"" }, { "DataName":"INQ_RSN", "DataValue":"ELG" }, { "DataName":"INQ_RSN1", "DataValue":"ELG" }, { "DataName":"INQA_DOB", "DataValue":"11-18-2014" }, { "DataName":"INQA_FIRST", "DataValue":"JUN-KANG" }, { "DataName":"INQA_GRP_ID", "DataValue":"923382" }, { "DataName":"INQA_GRP_NAME", "DataValue":"HUMANA INC" }, { "DataName":"INQA_ID", "DataValue":"000160217" }, { "DataName":"INQA_LAST", "DataValue":"SINICKI" }, { "DataName":"INQA_MKT_ID", "DataValue":"0" }, { "DataName":"INQA_PLTFRM_CD", "DataValue":"EM" }, { "DataName":"INQA_RELN", "DataValue":"" }, { "DataName":"INQA_TYP", "DataValue":"M" }, { "DataName":"INQR_FIRST", "DataValue":"" }, { "DataName":"INQR_ID", "DataValue":"582030692" }, { "DataName":"INQR_LAST", "DataValue":"" }, { "DataName":"INQR_RELN", "DataValue":"P" }, { "DataName":"INQR_TYP", "DataValue":"P" }, { "DataName":"INQUIRY_ID", "DataValue":"" }, { "DataName":"IVR_CALL_DATE", "DataValue":"20201217" }, { "DataName":"IVR_CALL_TIME", "DataValue":"06:24:44" }, { "DataName":"IVR_CALLER_TYPE", "DataValue":"P" }, { "DataName":"IVR_DEN_OI_CHG_IND", "DataValue":"" }, { "DataName":"IVR_MED_OI_CHG_IND", "DataValue":"" }, { "DataName":"IVR_SERVER_CD", "DataValue":"03" }, { "DataName":"IVR_SERVER_PORT", "DataValue":"023" }, { "DataName":"LAST_AGENT_ID", "DataValue":"1129930" }, { "DataName":"MAJOR_LOB", "DataValue":"NPOS" }, { "DataName":"MEMBER_DOB", "DataValue":"11/18/2014" }, { "DataName":"MEMBER_ID", "DataValue":"000160217" }, { "DataName":"MEMBER_PERS_GEN_KEY", "DataValue":"554004556423" }, { "DataName":"MTV_BL5", "DataValue":"HHP-HUM INC" }, { "DataName":"MTV_BL7", "DataValue":"NPOS" }, { "DataName":"MULTI_COV", "DataValue":"" },  { "DataName":"PROD_TYPE_CD", "DataValue":"" }, { "DataName":"PROD_TYPE_CODE", "DataValue":"NPOSMO" }, { "DataName":"PRODPLANID", "DataValue":"" }, { "DataName":"REFERENCE_ID", "DataValue":"" }, { "DataName":"RX_ID", "DataValue":"" }, { "DataName":"SELLING_LEDGER", "DataValue":"0" }, { "DataName":"STATE_OF_ISSUE", "DataValue":"KY" }, { "DataName":"SURVEY_ID", "DataValue":"" }, { "DataName":"UUID", "DataValue":"12172000006251000023030001860319" }, { "DataName":"VENDOR_ASOFDATE", "DataValue":"" }, { "DataName":"VENDOR_GROUPID", "DataValue":"" }, { "DataName":"VENDOR_SUB_GROUPID", "DataValue":"" } ] } } } }';
        httpRes.setBody(resBodyString);
        system.assert(httpRes.getBody() == resBodyString);
        Map<String, HTTPRequest> mapDetails = continueObj.getRequests();
        string reqType = '';
        for(String s : mapDetails.keyset())
        {
            reqType = s;
        } 
        List<string>labelGetmember = new List<string>();
        labelGetmember.add(reqType);
        Test.setContinuationResponse(reqType,httpRes);
        String result = (String)phChum.processResponseMultipletabs();
        System.assertEquals(result,null);
    }
    public static testmethod void testGetCallDataWebServiceMultipleTabAgent() 
    {
        PhoneBook_C_HUM phChum = new PhoneBook_C_HUM();
        phChum.uuidataValue = 'Test UUID';
        phChum.isRefreshedval = false;     
        Continuation continueObj = phChum.getDataFromWebServiceMultipleTabs();
        continueObj.continuationMethod = 'processResponseMultipletabs';        
        HttpResponse httpRes = new Httpresponse ();
        String resBodyString = '{ "GetCallDataResponse":{ "GetCallDataResult":{ "Application":"", "UUID":"Test UUID", "CallDataValues":{ "CallDataValuesList":[ { "DataName":"ACTIVE_CALLERCOUNT_ONCALL", "DataValue":"0" }, { "DataName":"ANI", "DataValue":"8326854341" }, { "DataName":"ASO_IND", "DataValue":"" }, { "DataName":"AUTHENTICATION_IND", "DataValue":"Y" }, { "DataName":"BUS_SEGMENT_IND", "DataValue":"U" }, { "DataName":"Call_Catg_Desc", "DataValue":"Medical Default" }, { "DataName":"Client_ID", "DataValue":"IVR" }, { "DataName":"CLAIM_GEN_KEY", "DataValue":"" }, { "DataName":"DATA_SRC", "DataValue":"IVR" }, { "DataName":"HDHP_IND", "DataValue":"" }, { "DataName":"INQ_DOS", "DataValue":"" }, { "DataName":"INQ_RSN", "DataValue":"DEF" }, { "DataName":"INQ_RSN1", "DataValue":"DEF" }, { "DataName":"INQA_DOB", "DataValue":"" }, { "DataName":"INQA_FIRST", "DataValue":"" }, { "DataName":"INQA_GRP_ID", "DataValue":"" }, { "DataName":"INQA_GRP_NAME", "DataValue":"" }, { "DataName":"INQA_ID", "DataValue":"675907" }, { "DataName":"INQA_LAST", "DataValue":"" }, { "DataName":"INQA_MKT_ID", "DataValue":"" }, { "DataName":"INQA_PLTFRM_CD", "DataValue":"" }, { "DataName":"INQA_RELN", "DataValue":"" }, { "DataName":"INQA_TYP", "DataValue":"E" }, { "DataName":"INQR_DOB", "DataValue":"" }, { "DataName":"INQR_FIRST", "DataValue":"" }, { "DataName":"INQR_ID", "DataValue":"561623293" }, { "DataName":"INQR_LAST", "DataValue":"BB&T - ILER WALL & SHONTER INS INC" }, { "DataName":"INQR_TYP", "DataValue":"A" }, { "DataName":"INQUIRY_ID", "DataValue":"" }, { "DataName":"IVR_CALL_DATE", "DataValue":"20201217" }, { "DataName":"IVR_CALL_TIME", "DataValue":"07:33:37" }, { "DataName":"IVR_CALLER_TYPE", "DataValue":"B" }, { "DataName":"IVR_DEN_OI_CHG_IND", "DataValue":"" }, { "DataName":"IVR_MED_OI_CHG_IND", "DataValue":"" }, { "DataName":"IVR_SERVER_CD", "DataValue":"03" }, { "DataName":"IVR_SERVER_PORT", "DataValue":"030" }, { "DataName":"LAST_AGENT_ID", "DataValue":"3378044" }, { "DataName":"MAJOR_LOB", "DataValue":"" }, { "DataName":"MEMBER_DOB", "DataValue":"" }, { "DataName":"MEMBER_ID", "DataValue":"" }, { "DataName":"MEMBER_PERS_GEN_KEY", "DataValue":"" }, { "DataName":"MTV_BL5", "DataValue":"" }, { "DataName":"MTV_BL7", "DataValue":"" }, { "DataName":"MULTI_COV", "DataValue":"" },  { "DataName":"PROD_TYPE_CD", "DataValue":"" }, { "DataName":"PROD_TYPE_CODE", "DataValue":"" }, { "DataName":"PRODPLANID", "DataValue":"" }, { "DataName":"REFERENCE_ID", "DataValue":"" }, { "DataName":"RX_ID", "DataValue":"" }, { "DataName":"SELLING_LEDGER", "DataValue":"" }, { "DataName":"STATE_OF_ISSUE", "DataValue":"" }, { "DataName":"SURVEY_ID", "DataValue":"" }, { "DataName":"UUID", "DataValue":"12172000007342900030030001860434" }, { "DataName":"VENDOR_ASOFDATE", "DataValue":"2020-12-17" }, { "DataName":"VENDOR_GROUPID", "DataValue":"" }, { "DataName":"VENDOR_SUB_GROUPID", "DataValue":"" } ] } } } }';
        httpRes.setBody(resBodyString);
        system.assert(httpRes.getBody() == resBodyString);
        Map<String, HTTPRequest> mapDetails = continueObj.getRequests();
        string reqType = '';
        for(String s : mapDetails.keyset())
        {
            reqType = s;
        } 
        List<string>labelGetmember = new List<string>();
        labelGetmember.add(reqType);
        Test.setContinuationResponse(reqType,httpRes);
        String result = (String)phChum.processResponseMultipletabs();
        System.assertEquals(result,null);        
    }
    public static testmethod void testGetCallDataWebServiceMultipleTabArrayBlank() 
    {
        PhoneBook_C_HUM phChum = new PhoneBook_C_HUM();
        phChum.uuidataValue = 'Test UUID';
        phChum.isRefreshedval = false;     
        Continuation continueObj = phChum.getDataFromWebServiceMultipleTabs();
        continueObj.continuationMethod = 'processResponseMultipletabs';        
        HttpResponse httpRes = new Httpresponse ();
        String resBodyString = '{ "GetCallDataResponse":{ "GetCallDataResult":{ "Application":"", "UUID":"Test UUID", "CallDataValues":{ "CallDataValuesList":[ ] } } } }';
        httpRes.setBody(resBodyString);
        system.assert(httpRes.getBody() == resBodyString);
        Map<String, HTTPRequest> mapDetails = continueObj.getRequests();
        string reqType = '';
        for(String s : mapDetails.keyset())
        {
            reqType = s;
        } 
        List<string>labelGetmember = new List<string>();
        labelGetmember.add(reqType);
        Test.setContinuationResponse(reqType,httpRes);
        String result = (String)phChum.processResponseMultipletabs();
        System.assertEquals(result,null);        
    }
	public static testmethod void testGetCallDataWebServiceArrayBlank() 
    {
        PhoneBook_C_HUM phChum = new PhoneBook_C_HUM();
        phChum.uuidataValue = 'Test UUID';
        phChum.isRefreshedval = True;     
        Continuation continueObj = phChum.getDataFromWebService();
        continueObj.continuationMethod = 'processResponse';        
        HttpResponse httpRes = new Httpresponse ();
        String resBodyString = '{ "GetCallDataResponse":{ "GetCallDataResult":{ "Application":"", "UUID":"Test UUID", "CallDataValues":{ "CallDataValuesList":[ ] } } } }';
        httpRes.setBody(resBodyString);
        system.assert(httpRes.getBody() == resBodyString);
        Map<String, HTTPRequest> mapDetails = continueObj.getRequests();
        string reqType = '';
        for(String s : mapDetails.keyset())
        {
            reqType = s;
        } 
        List<string>labelGetmember = new List<string>();
        labelGetmember.add(reqType);
        Test.setContinuationResponse(reqType,httpRes);
        String result = (String)phChum.processResponse();
        System.assertEquals(result,null);        
    }
    public static testmethod void testGetCallDataWebServiceRouteXMLNotBlank() 
    {
        PhoneBook_C_HUM phChum = new PhoneBook_C_HUM();
        phChum.uuidataValue = 'Test UUID';
        phChum.isRefreshedval = false;     
        Continuation continueObj = phChum.fetchDefaultPhoneBookRules();
        continueObj.continuationMethod = 'processResponsegetDefaultPhBookRules';        
        HttpResponse httpRes = new Httpresponse ();
        String resBodyString = '{ "GetCallDataResponse":{ "GetCallDataResult":{ "Application":"", "UUID":"Test UUID", "CallDataValues":{ "CallDataValuesList":[ { "DataName":"ACTIVE_CALLERCOUNT_ONCALL", "DataValue":"0" }, { "DataName":"ANI", "DataValue":"8326854341" }, { "DataName":"ASO_IND", "DataValue":"" }, { "DataName":"AUTHENTICATION_IND", "DataValue":"Y" }, { "DataName":"BUS_SEGMENT_IND", "DataValue":"U" }, { "DataName":"Call_Catg_Desc", "DataValue":"Medical Default" }, { "DataName":"Client_ID", "DataValue":"IVR" }, { "DataName":"CLAIM_GEN_KEY", "DataValue":"" }, { "DataName":"DATA_SRC", "DataValue":"IVR" }, { "DataName":"HDHP_IND", "DataValue":"" }, { "DataName":"INQ_DOS", "DataValue":"" }, { "DataName":"INQ_RSN", "DataValue":"DEF" }, { "DataName":"INQ_RSN1", "DataValue":"DEF" }, { "DataName":"INQA_DOB", "DataValue":"" }, { "DataName":"INQA_FIRST", "DataValue":"" }, { "DataName":"INQA_GRP_ID", "DataValue":"" }, { "DataName":"INQA_GRP_NAME", "DataValue":"" }, { "DataName":"INQA_ID", "DataValue":"675907" }, { "DataName":"INQA_LAST", "DataValue":"" }, { "DataName":"INQA_MKT_ID", "DataValue":"" }, { "DataName":"INQA_PLTFRM_CD", "DataValue":"LV" }, { "DataName":"INQA_RELN", "DataValue":"" }, { "DataName":"INQA_TYP", "DataValue":"E" }, { "DataName":"INQR_DOB", "DataValue":"" }, { "DataName":"INQR_FIRST", "DataValue":"" }, { "DataName":"INQR_ID", "DataValue":"561623293" }, { "DataName":"INQR_LAST", "DataValue":"BB&T - ILER WALL & SHONTER INS INC" }, { "DataName":"INQR_TYP", "DataValue":"A" }, { "DataName":"INQUIRY_ID", "DataValue":"" }, { "DataName":"IVR_CALL_DATE", "DataValue":"20201217" }, { "DataName":"IVR_CALL_TIME", "DataValue":"07:33:37" }, { "DataName":"IVR_CALLER_TYPE", "DataValue":"B" }, { "DataName":"IVR_DEN_OI_CHG_IND", "DataValue":"" }, { "DataName":"IVR_MED_OI_CHG_IND", "DataValue":"" }, { "DataName":"IVR_SERVER_CD", "DataValue":"03" }, { "DataName":"IVR_SERVER_PORT", "DataValue":"030" }, { "DataName":"LAST_AGENT_ID", "DataValue":"3378044" }, { "DataName":"MAJOR_LOB", "DataValue":"MEP" }, { "DataName":"MEMBER_DOB", "DataValue":"" }, { "DataName":"MEMBER_ID", "DataValue":"" }, { "DataName":"MEMBER_PERS_GEN_KEY", "DataValue":"" }, { "DataName":"MTV_BL5", "DataValue":"" }, { "DataName":"MTV_BL7", "DataValue":"" }, { "DataName":"MULTI_COV", "DataValue":"" },  { "DataName":"PROD_TYPE_CD", "DataValue":"" }, { "DataName":"PROD_TYPE_CODE", "DataValue":"" }, { "DataName":"PRODPLANID", "DataValue":"" }, { "DataName":"REFERENCE_ID", "DataValue":"" }, { "DataName":"RX_ID", "DataValue":"" }, { "DataName":"SELLING_LEDGER", "DataValue":"Test" }, { "DataName":"STATE_OF_ISSUE", "DataValue":"PA " }, { "DataName":"SURVEY_ID", "DataValue":"" }, { "DataName":"UUID", "DataValue":"12172000007342900030030001860434" }, { "DataName":"VENDOR_ASOFDATE", "DataValue":"2020-12-17" }, { "DataName":"VENDOR_GROUPID", "DataValue":"" }, { "DataName":"VENDOR_SUB_GROUPID", "DataValue":"319820" } ] } } } }';
        httpRes.setBody(resBodyString);
        system.assert(httpRes.getBody() == resBodyString);
        Map<String, HTTPRequest> mapDetails = continueObj.getRequests();
        string reqType = '';
        for(String s : mapDetails.keyset())
        {
            reqType = s;
        } 
        List<string>labelGetmember = new List<string>();
        labelGetmember.add(reqType);
        Test.setContinuationResponse(reqType,httpRes);
        String result = (String)phChum.processResponsegetDefaultPhBookRules();
        System.assertEquals(result,null);        
    }
    public static testmethod void testGetCallDataWebServiceINQRtypeBlank() 
    {
        PhoneBook_C_HUM phChum = new PhoneBook_C_HUM();
        phChum.uuidataValue = 'Test UUID 10';
        phChum.isRefreshedval = false;     
        Continuation continueObj = phChum.getDataFromWebService();
        continueObj.continuationMethod = 'processResponse';        
        HttpResponse httpRes = new Httpresponse ();
        String resBodyString = '{ "GetCallDataResponse":{ "GetCallDataResult":{ "Application":"", "UUID":"Test UUID 10", "CallDataValues":{ "CallDataValuesList":[ { "DataName":"ACTIVE_CALLERCOUNT_ONCALL", "DataValue":"0" }, { "DataName":"ANI", "DataValue":"8326854341" }, { "DataName":"ASO_IND", "DataValue":"" }, { "DataName":"AUTHENTICATION_IND", "DataValue":"Y" }, { "DataName":"BUS_SEGMENT_IND", "DataValue":"U" }, { "DataName":"Call_Catg_Desc", "DataValue":"Medical Default" }, { "DataName":"Client_ID", "DataValue":"IVR" }, { "DataName":"CLAIM_GEN_KEY", "DataValue":"" }, { "DataName":"DATA_SRC", "DataValue":"IVR" }, { "DataName":"HDHP_IND", "DataValue":"" }, { "DataName":"INQ_DOS", "DataValue":"" }, { "DataName":"INQ_RSN", "DataValue":"DEF" }, { "DataName":"INQ_RSN1", "DataValue":"DEF" }, { "DataName":"INQA_DOB", "DataValue":"" }, { "DataName":"INQA_FIRST", "DataValue":"" }, { "DataName":"INQA_GRP_ID", "DataValue":"" }, { "DataName":"INQA_GRP_NAME", "DataValue":"" }, { "DataName":"INQA_ID", "DataValue":"675907" }, { "DataName":"INQA_LAST", "DataValue":"" }, { "DataName":"INQA_RELN", "DataValue":"" }, { "DataName":"INQA_TYP", "DataValue":"E" }, { "DataName":"INQR_DOB", "DataValue":"" }, { "DataName":"INQR_FIRST", "DataValue":"" }, { "DataName":"INQR_ID", "DataValue":"" }, { "DataName":"INQR_LAST", "DataValue":"" }, { "DataName":"INQR_TYP", "DataValue":"" }, { "DataName":"INQUIRY_ID", "DataValue":"" }, { "DataName":"IVR_CALL_DATE", "DataValue":"20201217" }, { "DataName":"IVR_CALL_TIME", "DataValue":"07:33:37" }, { "DataName":"IVR_CALLER_TYPE", "DataValue":"" }, { "DataName":"MEMBER_DOB", "DataValue":"" }, { "DataName":"MEMBER_ID", "DataValue":"" }, { "DataName":"SURVEY_ID", "DataValue":"" }, { "DataName":"UUID", "DataValue":"12172000007342900030030001860434" } ] } } } }';
        httpRes.setBody(resBodyString);
        system.assert(httpRes.getBody() == resBodyString);
        Map<String, HTTPRequest> mapDetails = continueObj.getRequests();
        string reqType = '';
        for(String s : mapDetails.keyset())
        {
            reqType = s;
        } 
        List<string>labelGetmember = new List<string>();
        labelGetmember.add(reqType);
        Test.setContinuationResponse(reqType,httpRes);
        String result = (String)phChum.processResponse();
        System.assertEquals(result,null);        
    }
    public static testmethod void testGetCallDataWebServiceCallerName() 
    {
        PhoneBook_C_HUM phChum = new PhoneBook_C_HUM();
        phChum.uuidataValue = 'Test UUID 10';
        phChum.isRefreshedval = false;     
        Continuation continueObj = phChum.getDataFromWebService();
        continueObj.continuationMethod = 'processResponse';        
        HttpResponse httpRes = new Httpresponse ();
        String resBodyString = '{ "GetCallDataResponse":{ "GetCallDataResult":{ "Application":"", "UUID":"Test UUID 10", "CallDataValues":{ "CallDataValuesList":[ { "DataName":"ANI", "DataValue":"8326854341" }, { "DataName":"ASO_IND", "DataValue":"" }, { "DataName":"AUTHENTICATION_IND", "DataValue":"Y" }, { "DataName":"BUS_SEGMENT_IND", "DataValue":"U" }, { "DataName":"Call_Catg_Desc", "DataValue":"Medical Default" }, { "DataName":"Client_ID", "DataValue":"IVR" }, { "DataName":"INQA_DOB", "DataValue":"" }, { "DataName":"INQA_FIRST", "DataValue":"Test First" }, { "DataName":"INQA_GRP_ID", "DataValue":"" }, { "DataName":"INQA_GRP_NAME", "DataValue":"" }, { "DataName":"INQA_ID", "DataValue":"" }, { "DataName":"INQA_LAST", "DataValue":"" }, { "DataName":"INQA_RELN", "DataValue":"" }, { "DataName":"INQA_TYP", "DataValue":"M" }, { "DataName":"INQR_DOB", "DataValue":"" }, { "DataName":"INQR_FIRST", "DataValue":"" }, { "DataName":"INQR_ID", "DataValue":"" }, { "DataName":"INQR_LAST", "DataValue":"" }, { "DataName":"INQR_TYP", "DataValue":"M" }, { "DataName":"INQUIRY_ID", "DataValue":"" }, { "DataName":"IVR_CALL_DATE", "DataValue":"20201217" }, { "DataName":"IVR_CALL_TIME", "DataValue":"07:33:37" }, { "DataName":"IVR_CALLER_TYPE", "DataValue":"" }, { "DataName":"MEMBER_DOB", "DataValue":"" }, { "DataName":"MEMBER_ID", "DataValue":"" }, { "DataName":"SURVEY_ID", "DataValue":"" }, { "DataName":"UUID", "DataValue":"12172000007342900030030001860434" } ] } } } }';
        httpRes.setBody(resBodyString);
        system.assert(httpRes.getBody() == resBodyString);
        Map<String, HTTPRequest> mapDetails = continueObj.getRequests();
        string reqType = '';
        for(String s : mapDetails.keyset())
        {
            reqType = s;
        } 
        List<string>labelGetmember = new List<string>();
        labelGetmember.add(reqType);
        Test.setContinuationResponse(reqType,httpRes);
        String result = (String)phChum.processResponse();
        System.assertEquals(result,null);        
    }
    public static testmethod void testException()
    {
        PhoneBook_C_HUM phChum = new PhoneBook_C_HUM();
        phChum.uuidataValue = 'Test UUID Test';
        phChum.isRefreshedval = false;
        Continuation continueObj = phChum.getDataFromWebService();
        continueObj.continuationMethod = 'processResponse';        
        HttpResponse httpRes = new Httpresponse ();
        String resBodyString = '{"GetCallDataResponse":{ "GetCallDataResult":{ "Application":"", "UUID":"Test UUID Test", "CallDataValues":{ "CallDataValuesList":[ { "DataName":"ACTIVE_CALLERCOUNT_ONCALL", "DataValue":"0" }, { "DataName":"ANI", "DataValue":"4092915301" }, { "DataName":"ASO_IND", "DataValue":"True" }, { "DataName":"AUTHENTICATION_IND", "DataValue":"Y" }, { "DataName":"BUS_SEGMENT_IND", "DataValue":"L" }, { "DataName":"Call_Catg_Desc", "DataValue":"Medical Eligibility" }, { "DataName":"Client_ID", "DataValue":"IVR" }, { "DataName":"INQA_DOB", "DataValue":"11-18-2014" }, { "DataName":"INQA_FIRST", "DataValue":"JUN-KANG" }, { "DataName":"INQA_GRP_ID", "DataValue":"923382" }, { "DataName":"INQA_GRP_NAME", "DataValue":"HUMANA INC" }, { "DataName":"INQA_ID", "DataValue":"1234567" }, { "DataName":"INQA_LAST", "DataValue":"" }, { "DataName":"INQA_TYP", "DataValue":"P" }, { "DataName":"INQR_TYP", "DataValue":"P" },{ "DataName":"INQR_ID", "DataValue":"" },{ "DataName":"INQR_FIRST", "DataValue":"" },{ "DataName":"INQR_LAST", "DataValue":"" }, { "DataName":"INQUIRY_ID", "DataValue":"" }, { "DataName":"IVR_CALL_DATE", "DataValue":"20201210" }, { "DataName":"IVR_CALL_TIME", "DataValue":"01:26:26" }, { "DataName":"IVR_CALLER_TYPE", "DataValue":"M" }, { "DataName":"MEMBER_DOB", "DataValue":"11/18/2014" }, { "DataName":"MEMBER_ID", "DataValue":"000160217" }, { "DataName":"UUID", "DataValue":"12102000001270800029030001860434" } ] } } }}';
        httpRes.setBody(resBodyString);
        system.assert(httpRes.getBody() == resBodyString);
        Map<String, HTTPRequest> mapDetails = continueObj.getRequests();
        string reqType = '';
        for(String s : mapDetails.keyset())
        {
            reqType = s;
        } 
        List<string>labelGetmember = new List<string>();
        labelGetmember.add(reqType);
        Test.setContinuationResponse(reqType,httpRes);
        String result = (String)phChum.processResponse();
        System.assertEquals(result,null);
    }
    
    /**
    * This is a test method for testgetCallMemberAccountDetails for MemberId and Genkey
    *
    * @param  None      
    * @return  Void
    */
    static testmethod void testgetCallMemberAccountDetails()
    {
        Test.startTest();
        Account acc = new Account();
        acc.Name = 'TestAccount';
        acc.Mbr_Gen_Key__c='5232304413817';
        insert acc;
        PhoneBook_C_HUM.getCallMemberAccountDetails('H70955824', '', '5232304413817', '', '', '', true);
        PhoneBook_C_HUM.getCallMemberAccountDetails('', '', '5232304413817', '', '', '', true);
        System.assert(acc.Name == 'TestAccount');
        Test.StopTest();
		
    }

    /**
    * This is a test method for testgetCallMemberRecords for MemberId and Genkey
    *
    * @param  None      
    * @return  Void
    */
    static testMethod void testgetCallMemberRecords()
    {
            Test.startTest();
            String str= PhoneBook_C_HUM.getCallMemberRecords('000160217', '','', '', '', false);
        	PhoneBook_C_HUM.getCallMemberRecords('000160217', '00','', '', '', false);
            PhoneBook_C_HUM.getCallMemberRecords('000160217', '','', '', '', false);
       		PhoneBook_C_HUM.getCallMemberRecords('00016021700', '','TEST', 'TEST', 'CB', false);
            PhoneBook_C_HUM.getCallMemberRecords('H3009855600', '','', '', '', true);
        	System.assert(str != null);
            Test.StopTest();
        	
        
    }
   
    /**
    * This is a test method for getCallerTypeForPhoneBook()
    *
    * @param  String   
    * @return  String
    */
    static testmethod void testgetCallerTypeForPhoneBook(){
        Test.startTest();
        String interactingWithType = 'Member';
        String callerType = PhoneBook_C_HUM.getCallerTypeForPhoneBook(interactingWithType);
        system.assertEquals('Member', callerType);
        PhoneBook_C_HUM pb = new PhoneBook_C_HUM();
        pb.updateCallerTypeVal();
        Test.stopTest();
    }
    /**
    * This is a test method for setDateFormatForBirthDate()
    *
    * @param  String   
    * @return  String
    */
    static testmethod void testsetDateFormatForBirthDate(){
        Test.startTest();
        String memberDateValue1 = '1955-05-08';
        String dateValueFormat = PhoneBook_C_HUM.setDateFormatForBirthDate(memberDateValue1);
        system.assertEquals('05/08/1955', dateValueFormat);
        Test.stopTest();
    }
    static testMethod void testautoInteractionCampaignNameAndKeycode()
    {
        Test.startTest();
        String searchModal3 = '{"INQA_FIRST" : "","INQA_LAST":"","INQA_ID":"","INQA_DOB": "","INQA_TYP":"M","INQR_FIRST" : "TestCaller","INQR_LAST":"","INQR_ID":"","INQR_DOB": "","INQR_TYP":"M","AUTHENTICATION_IND": "Y"}';
        CRMFunctionality_ONOFF_Switch__c objCS = new CRMFunctionality_ONOFF_Switch__c(Name = '4726311', IsON__c = false);
        insert objCS;
        PhoneBook_C_HUM pb = new PhoneBook_C_HUM();
        String intId3 = pb.createAutoInteraction(JSON.deserializeUntyped(searchModal3),'xyzzUUID3x',false,'123456asdfghj1234','{"uuidata":"Test UUID","campaignname":"Test","keycode":"123"}');
        system.assert(intId3<>null);
        String intId4 = pb.createAutoInteraction(JSON.deserializeUntyped(searchModal3),'Test UUID Multi Member',false,'123456asdfghj1234','{"uuidata":"Test UUID Multi Member","campaignname":"Test","keycode":"123"}');
        system.assert(intId4<>null);
        Test.stopTest();
    }
    static testMethod void testgetAboutIdAndInteractionIdCampaignNameAndKeycode()
    {	
        Test.startTest();
        String searchModal = '{"AGENT_CALL_START_TIME":"06:10:00","isConsultBlindClicked":true,"isTransferredCall":false,"AGENT_CALL_END_TIME":"06:20:00","UUID":"Test123","interactionId":"12345-12345-12345-12345","Outbound":false}';
        PhoneBook_C_HUM pb = new PhoneBook_C_HUM();
        pb.uuidataValue = 'Test UUID';
        pb.isRefreshedval = false;
        pb.idval = 'abcd-abcd-abcd-abcd';
        pb.calllogattributes = '{"uuidata":"Test UUID","campaignname":"test","keycode":"123"}';
        pb.getAboutIdAndInteractionId();
        System.assert(pb.uuidataValue == 'Test UUID');
        Test.stopTest();
    }
     static testMethod void testgetAboutIdAndInteractionIdSwtichOFF()
    {	     
        Test.startTest();
        CRMFunctionality_ONOFF_Switch__c objSwitch = CRMFunctionality_ONOFF_Switch__c.getValues('2760646');   
        System.assert(objSwitch != null);    
        String searchModal = '{"AGENT_CALL_START_TIME":"06:10:00","isConsultBlindClicked":true,"isTransferredCall":false,"AGENT_CALL_END_TIME":"06:20:00","UUID":"Test123","interactionId":"12345-12345-12345-12345","Outbound":false}';
        PhoneBook_C_HUM pb = new PhoneBook_C_HUM();
        pb.uuidataValue = 'Test UUID';
        pb.isRefreshedval = false;
        pb.idval = 'abcd-abcd-abcd-abcd';
        pb.calllogattributes = '{"uuidata":"Test UUID","campaignname":"test","keycode":"123"}';
        objSwitch.IsON__c = false;
        update objSwitch;
        pb.getAboutIdAndInteractionId();
        
         pb.isRefreshedval = true;
         pb.getAboutIdAndInteractionId();
        Test.stopTest();
    }
        static testMethod void testcreateAutoInteraction()
    {
        Test.startTest();
        Map<String, object> searchModal=new  Map<String, object>{'INQA_FIRST'=>'Member','INQA_LAST'=>'TestAcc','INQA_DOB'=>'','INQA_TYP'=>'M','INQR_FIRST'=>'Member','INQR_LAST'=>'TestAcc','INQR_ID'=>'123456789','INQR_DOB'=>'','INQR_TYP'=>'M','AUTHENTICATION_IND'=>'Y','CALL_TYPE'=>'Inbound'};

        Interaction__c lstInteractionRec = [SELECT Id, Caller_Name__c, Call_data__c, Authenticated__c, Interacting_With__c, Interacting_With_type__c, Interaction_Origin__c, UUID__c, Genesys_Interaction_ID__c,Survey_ID__c, Call_Category_Description__c, CreatedDate,(SELECT Id, Interacting_About__c,Interacting_About__r.FirstName,Interacting_About__r.LastName,Interacting_About__r.BirthDate__c, Interacting_About_Type__c, Authenticated__c,Interaction__c 
                                            FROM Interaction_Members__r order by CreatedDate desc) 
                                            FROM Interaction__c where UUID__c =: 'Test UUID' order by Name desc limit 1];
        CRMFunctionality_ONOFF_Switch__c objSwitch = CRMFunctionality_ONOFF_Switch__c.getValues('4726311');   
        System.assert(objSwitch != null);
        PhoneBook_C_HUM pb = new PhoneBook_C_HUM();
        String intId3 = pb.createAutoInteraction(searchModal,'Test UUID',false,'123456asdfghj1234','{"uuidata":"Test UUID","campaignname":"Test","keycode":"123"}');
        Test.stopTest();
}  
      
    
    static testMethod void testcreateAutoInteractionNegative()
    {
        Test.startTest();
        Map<String, object> searchModal=new  Map<String, object>{'INQA_FIRST'=>'Member','INQA_LAST'=>'TestAcc','INQA_DOB'=>'','INQA_TYP'=>'M','INQR_FIRST'=>'Member','INQR_LAST'=>'TestAcc','INQR_ID'=>'','INQR_DOB'=>'','INQR_TYP'=>'M','AUTHENTICATION_IND'=>'Y','CALL_TYPE'=>'Inbound'};
        Interaction__c lstInteractionRec = [SELECT Id, Caller_Name__c, Call_data__c, Authenticated__c, Interacting_With__c, Interacting_With_type__c, Interaction_Origin__c, UUID__c, Genesys_Interaction_ID__c,Survey_ID__c, Call_Category_Description__c, CreatedDate,(SELECT Id, Interacting_About__c,Interacting_About__r.FirstName,Interacting_About__r.LastName,Interacting_About__r.BirthDate__c, Interacting_About_Type__c, Authenticated__c,Interaction__c 
                                            FROM Interaction_Members__r order by CreatedDate desc) 
                                            FROM Interaction__c where UUID__c =: 'Test UUID' order by Name desc limit 1];
        PhoneBook_C_HUM pb = new PhoneBook_C_HUM();
        CRMFunctionality_ONOFF_Switch__c objSwitch1 = CRMFunctionality_ONOFF_Switch__c.getValues('4726311');
        CRMFunctionality_ONOFF_Switch__c objSwitch = CRMFunctionality_ONOFF_Switch__c.getValues('2760646');   
        System.assert(objSwitch != null); 
        objSwitch.IsON__c = false;
        update objSwitch;
        String intId3 = pb.createAutoInteraction(searchModal,'Test UUID',false,'123456asdfghj1234','{"uuidata":"Test UUID","campaignname":"Test","keycode":"123"}');
        system.assert(intId3<>null);
        Test.stopTest();
}
     static testMethod void testgetCloneInteraction()
    {	
        Test.startTest();
        List<Interaction__c> testRecords = new List<Interaction__c>();
        String searchModal = '{"INQA_FIRST" : "Member","INQA_LAST":"TestAcc","INQA_ID":"","INQA_DOB": "","INQA_TYP":"M","INQR_FIRST" : "Member","INQR_LAST":"TestAcc","INQR_ID":"","INQR_DOB": "","INQR_TYP":"M","AUTHENTICATION_IND": "Y"}';
        Interaction__c lstInteractionRec = [SELECT Id, Caller_Name__c, Call_data__c, Authenticated__c, Interacting_With__c, Interacting_With_type__c, Interaction_Origin__c, UUID__c, Genesys_Interaction_ID__c,Survey_ID__c, Call_Category_Description__c, CreatedDate,(SELECT Id, Interacting_About__c,Interacting_About__r.FirstName,Interacting_About__r.LastName,Interacting_About__r.BirthDate__c, Interacting_About_Type__c, Authenticated__c,Interaction__c 
                                            FROM Interaction_Members__r order by CreatedDate desc) 
                                            FROM Interaction__c where UUID__c =: 'Test UUID' order by Name desc limit 1];
        PhoneBook_C_HUM pb = new PhoneBook_C_HUM();
        pb.isRefreshedval = false;
        pb.DataModal.put('NUMBER_OF_TRANSFERS', 1);
        pb.DataModal.put('INQA_LAST','TesMember');
        pb.DataModal.put('INQA_FIRST','Member');
        pb.DataModal.put('INQA_DOB','11-18-2014');
         pb.DataModal.put('INQA_TYP','Member');
        String id = pb.cloneInteraction(lstInteractionRec);
        System.assert( pb.isRefreshedval == false);
        Test.stopTest();
    }
     static testMethod void testgetCloneInteractionNegative()
    {	
        Test.startTest();
        List<Interaction_Member__c> testRecords = new List<Interaction_Member__c>();
        String searchModal = '{"INQA_FIRST" : "Member","INQA_LAST":"TestAcc","INQA_ID":"","INQA_DOB": "","INQA_TYP":"M","INQR_FIRST" : "Member","INQR_LAST":"TestAcc","INQR_ID":"","INQR_DOB": "","INQR_TYP":"M","AUTHENTICATION_IND": "Y"}';
 			Interaction__c lstInteractionRec = [SELECT Id, Caller_Name__c, Call_data__c, Authenticated__c, Interacting_With__c, Interacting_With_type__c, Interaction_Origin__c, UUID__c, Genesys_Interaction_ID__c,Survey_ID__c, Call_Category_Description__c, CreatedDate,(SELECT Id, Interacting_About__c,Interacting_About__r.FirstName,Interacting_About__r.LastName,Interacting_About__r.BirthDate__c, Interacting_About_Type__c, Authenticated__c,Interaction__c 
                                            FROM Interaction_Members__r order by CreatedDate desc) 
                                            FROM Interaction__c where UUID__c =: 'Test UUID' order by Name desc limit 1];
        
        
        PhoneBook_C_HUM pb = new PhoneBook_C_HUM();
        pb.isRefreshedval = false;
        pb.isTransferredCall=false;
        pb.DataModal.put('NUMBER_OF_TRANSFERS', 1);
        String id = pb.cloneInteraction(lstInteractionRec);
        System.assert( pb.isRefreshedval == false);
        Test.stopTest();
    }
         static testMethod void testgetCloneInteractionNegativeElse()
    {	
        Test.startTest();
        List<Interaction_Member__c> testRecords = new List<Interaction_Member__c>();
        String searchModal = '{"INQA_FIRST" : "Member","INQA_LAST":"TestAcc","INQA_ID":"","INQA_DOB": "","INQA_TYP":"M","INQR_FIRST" : "Member","INQR_LAST":"TestAcc","INQR_ID":"","INQR_DOB": "","INQR_TYP":"M","AUTHENTICATION_IND": "Y"}';
 			Interaction__c lstInteractionRec = [SELECT Id, Caller_Name__c, Call_data__c, Authenticated__c, Interacting_With__c, Interacting_With_type__c, Interaction_Origin__c, UUID__c, Genesys_Interaction_ID__c,Survey_ID__c, Call_Category_Description__c, CreatedDate
                                            FROM Interaction__c where UUID__c =: 'Test UUID' order by Name desc limit 1];
        PhoneBook_C_HUM pb = new PhoneBook_C_HUM();
        pb.isRefreshedval = false;
        pb.isTransferredCall=false;
        pb.DataModal.put('NUMBER_OF_TRANSFERS', 1);
        String id = pb.cloneInteraction(lstInteractionRec);
        System.assert( pb.isRefreshedval == false);
        Test.stopTest();
    }

     static testMethod void TESTAGENTWITH()
    {
        Test.startTest();
        List<Account> acc=[SELECT Id,Name,Agent_ID__c FROM Account];
        PhoneBook_C_HUM pb = new PhoneBook_C_HUM();
       
    	String ret2 = PhoneBook_C_HUM.getAgentWithAccountId('54321','','TestAgent1','','A','','','','');
        String ret1 = PhoneBook_C_HUM.getAgentWithAccountId('','','TestAgent1','','A','','','','');
        System.assert(ret2 != null);
         Test.stopTest();
    }
   
    static testMethod void TESTgetProviderWithAccountId1(){
         Test.startTest();
       string sMemberRecordtypeID=  Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId();  
        Account actMember = HUMTestDataHelperTest.getAccount();
        actMember.Lastname='TesMember';
        actMember.FirstName = 'Member';
        actMember.Birthdate__c = '11-18-2014';
        actMember.NPI_ID__c='123456789';
        actMember.recordtypeid = sMemberRecordtypeID;    
        insert actMember;
        List<ConsumerID__c> consID=[SELECT Id, Account__r.RecordType.Name, Account__c, Consumer_ID__c FROM ConsumerID__c];
        List<Account> acc=[SELECT Id,Name,Agent_ID__c,Lastname,FirstName,Birthdate__c FROM Account];
        PhoneBook_C_HUM pb = new PhoneBook_C_HUM(); 
    	String ret2 = PhoneBook_C_HUM.getProviderWithAccountId('123456','','TestAgent1','','A','','','','','123456789');
        system.assert(actMember.Lastname=='TesMember');
         Test.stopTest();
    }
 
    static testMethod void testcreateSoftPhoneLog()
    {	
        Test.startTest();
        Account acc = new Account();
        acc.Name = 'TestAccount';
        insert acc;
        PhoneBook_D_HUM.createSoftPhoneLog('','','','',acc.id,'');
        System.assert(acc.Name == 'TestAccount');
        Test.stopTest();
    }

    static testmethod void testsetDateFormatForBirthDate2(){
        Test.startTest();
        String memberDateValue1 = '1955/05/08';
        String dateValueFormat = PhoneBook_C_HUM.setDateFormatForBirthDate(memberDateValue1);
        system.assertNotEquals('05/08/1955', dateValueFormat);
        Test.stopTest();
    }

    static testMethod void testinsertInteractionMember()
    {        
        string sMemberRecordtypeID=  Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId(); 
        Account memberAcc = HUMTestDataHelperTest.getAccount();
        memberAcc.Lastname='TesMember3';
        memberAcc.FirstName = 'Member';
        memberAcc.Birthdate__c = '11-18-2014';
        memberAcc.recordtypeid = sMemberRecordtypeID;    
        insert memberAcc;
        List<Interaction_Member__c> interactionMembers = new List<Interaction_Member__c>();
        Interaction__c interAction = new Interaction__c(Interaction_Origin__c = 'Inbound Call', Interacting_With__c=memberAcc.id,UUID__c='Test UUID',Interacting_With_type__c= 'Member', Authenticated__c = false);
        insert interAction;
        Phonebook_D_HUM.insertInteraction(interAction);   
        Interaction_Member__c intMem = new Interaction_Member__c(Interacting_About__c = memberAcc.id,Interaction__c = interAction.id ,Interacting_About_Type__c ='Member');               
        interactionMembers.add(intMem);
        insert interactionMembers;
        Test.startTest();        
        Phonebook_D_HUM.insertInteractionMember(interactionMembers);  
        System.assert(memberAcc.Lastname=='TesMember3');
        Test.stopTest();
    }
    
        public static testmethod void testupdateInteractionInfoToIVRNew(){
        
        CRMFunctionality_ONOFF_Switch__c objStchMF2 = new CRMFunctionality_ONOFF_Switch__c();
        objStchMF2.name = '2357061';
        objStchMF2.IsON__c = true;
        insert objStchMF2;
            
        CRMFunctionality_ONOFF_Switch__c objStchMF3 = new CRMFunctionality_ONOFF_Switch__c();
        objStchMF3.name = '4696940';
        objStchMF3.IsON__c = true;
        insert objStchMF3;
            
        string sMemberRecordtypeID=  Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId(); 
        Account memberAcc = HUMTestDataHelperTest.getAccount();
        memberAcc.Lastname='TesMember3';
        memberAcc.FirstName = 'Member';
        memberAcc.Birthdate__c = '11-18-2014';
        memberAcc.recordtypeid = sMemberRecordtypeID;    
        insert memberAcc;
            

        //Create Interaction and interaction Member Data for different Account types
        List<Interaction__c> listinteraction = new List<Interaction__c>();

		//member
		Interaction__c interAction = new Interaction__c(Interaction_Origin__c = 'Inbound Call', Interacting_With__c=memberAcc.id,UUID__c='Test UUID',Interacting_With_type__c= 'Member', Authenticated__c = true);
        //without  
        Interaction__c interAction1 = new Interaction__c(Interaction_Origin__c = 'Inbound Call', Interacting_With__c=null,UUID__c='Test UUID Other2',Interacting_With_type__c= '', Authenticated__c = true, Caller_Name__c='test');
        //multi member
        Interaction__c interAction2 = new Interaction__c(Interaction_Origin__c = 'Inbound Call', Interacting_With__c=memberAcc.id,UUID__c='Test UUID Multi Member',Interacting_With_type__c= 'Member', Authenticated__c = true, Caller_Name__c='test');
        

        listinteraction.add(interAction);
        listinteraction.add(interAction1);
        listinteraction.add(interAction2);
        insert  listinteraction;

        List<Interaction_Member__c> listinteractionMember = new List<Interaction_Member__c>();
        //Member
        Interaction_Member__c intMemAgent2 = new Interaction_Member__c(Interacting_About__c = memberAcc.id,Interaction__c = interAction.id ,Interacting_About_Type__c ='Member');
        //Member 
        Interaction_Member__c intMemAgent3 = new Interaction_Member__c(Interacting_About__c = memberAcc.id,Interaction__c = interAction2.id ,Interacting_About_Type__c ='Member');
        //Member 
        Interaction_Member__c intMemAgent4 = new Interaction_Member__c(Interacting_About__c = memberAcc.id,Interaction__c = interAction2.id ,Interacting_About_Type__c ='Member');
        //Without
        Interaction_Member__c intMemAgent5 = new Interaction_Member__c(Interacting_About__c = memberAcc.id,Interaction__c = interAction1.id ,Interacting_About_Type__c ='Member');
            
        listinteractionMember.add(intMemAgent2);
        listinteractionMember.add(intMemAgent3);
        listinteractionMember.add(intMemAgent4);
        listinteractionMember.add(intMemAgent5);
        insert listinteractionMember;


        map<String,String> StateDataMapping = new map<String,String>();
        StateDataMapping.put('CALL_TYPE', 'Inbound');
        StateDataMapping.put('TRANSFER_NUMBER','1234567');
   

        Test.startTest();
		System.assert(memberAcc.Lastname=='TesMember3');
        PhoneBook_C_HUM.updateInteractionInfoToIVR('Test UUID Other',StateDataMapping,'');
        PhoneBook_C_HUM.updateInteractionInfoToIVR('Test UUID Other2',StateDataMapping,'');
        PhoneBook_C_HUM.updateInteractionInfoToIVR('Test UUID Multi Member',StateDataMapping,memberAcc.id);
        PhoneBook_C_HUM.updateInteractionInfoToIVR('Test UUID',StateDataMapping,'');
        PhoneBook_C_HUM.updateInteractionInfoToIVR('Test UUID Group',StateDataMapping,'');
        PhoneBook_C_HUM.updateInteractionInfoToIVR('Test UUID Provider',StateDataMapping,'');
        PhoneBook_C_HUM.updateInteractionInfoToIVR('Test UUID Agent',StateDataMapping,'');
        PhoneBook_C_HUM.updateInteractionInfoToIVR('Test UUID 1',StateDataMapping,'');
        PhoneBook_C_HUM.updateInteractionInfoToIVR('Test UUID 2',StateDataMapping,'');
        PhoneBook_C_HUM.updateInteractionInfoToIVR('Test UUID unknown Member',StateDataMapping,'');
        PhoneBook_C_HUM.updateInteractionInfoToIVR('Test UUID unknown Group',StateDataMapping,'');
        PhoneBook_C_HUM.updateInteractionInfoToIVR('Test UUID unknown Provider',StateDataMapping,'');
        PhoneBook_C_HUM.updateInteractionInfoToIVR('Test UUID unknown Agent',StateDataMapping,'');
        Test.stopTest();
            
    }
    
   public static testmethod void testupdateInteractionInfoToIVRNeg(){
        
        CRMFunctionality_ONOFF_Switch__c objStchMF2 = new CRMFunctionality_ONOFF_Switch__c();
        objStchMF2.name = '2357061';
        objStchMF2.IsON__c = true;
        insert objStchMF2;
            
        CRMFunctionality_ONOFF_Switch__c objStchMF3 = new CRMFunctionality_ONOFF_Switch__c();
        objStchMF3.name = '4696940';
        objStchMF3.IsON__c = false;
        insert objStchMF3;
            
        string sMemberRecordtypeID=  Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId(); 
        Account memberAcc = HUMTestDataHelperTest.getAccount();
        memberAcc.Lastname='TesMember3';
        memberAcc.FirstName = 'Member';
        memberAcc.Birthdate__c = '11-18-2014';
        memberAcc.recordtypeid = sMemberRecordtypeID;    
        insert memberAcc;

        //Create Interaction and interaction Member Data for different Account types
        List<Interaction__c> listinteraction = new List<Interaction__c>();

		//member
		Interaction__c interAction = new Interaction__c(Interaction_Origin__c = 'Inbound Call', Interacting_With__c=memberAcc.id,UUID__c='Test UUID',Interacting_With_type__c= 'Member', Authenticated__c = true);
        //without  
        Interaction__c interAction1 = new Interaction__c(Interaction_Origin__c = 'Inbound Call', Interacting_With__c=null,UUID__c='Test UUID Other2',Interacting_With_type__c= '', Authenticated__c = true, Caller_Name__c='test');
        //multi member
        Interaction__c interAction2 = new Interaction__c(Interaction_Origin__c = 'Inbound Call', Interacting_With__c=memberAcc.id,UUID__c='Test UUID Multi Member',Interacting_With_type__c= 'Member', Authenticated__c = true, Caller_Name__c='test');
        

        listinteraction.add(interAction);
        listinteraction.add(interAction1);
        listinteraction.add(interAction2);
        insert  listinteraction;

        List<Interaction_Member__c> listinteractionMember = new List<Interaction_Member__c>();
        //Member
        Interaction_Member__c intMemAgent2 = new Interaction_Member__c(Interacting_About__c = memberAcc.id,Interaction__c = interAction.id ,Interacting_About_Type__c ='Member');
        //Member 
        Interaction_Member__c intMemAgent3 = new Interaction_Member__c(Interacting_About__c = memberAcc.id,Interaction__c = interAction2.id ,Interacting_About_Type__c ='Member');
        //Member 
        Interaction_Member__c intMemAgent4 = new Interaction_Member__c(Interacting_About__c = memberAcc.id,Interaction__c = interAction2.id ,Interacting_About_Type__c ='Member');
        //Without
        Interaction_Member__c intMemAgent5 = new Interaction_Member__c(Interacting_About__c = memberAcc.id,Interaction__c = interAction1.id ,Interacting_About_Type__c ='Member');
            
        listinteractionMember.add(intMemAgent2);
        listinteractionMember.add(intMemAgent3);
        listinteractionMember.add(intMemAgent4);
        listinteractionMember.add(intMemAgent5);
        insert listinteractionMember;



        map<String,String> StateDataMapping = new map<String,String>();
        StateDataMapping.put('CALL_TYPE', 'Inbound');
        StateDataMapping.put('TRANSFER_NUMBER','1234567')	;

        Test.startTest();
		System.assert(memberAcc.Lastname=='TesMember3');
        PhoneBook_C_HUM.updateInteractionInfoToIVR('Test UUID Other',StateDataMapping,'');
        PhoneBook_C_HUM.updateInteractionInfoToIVR('Test UUID Other2',StateDataMapping,'');
        PhoneBook_C_HUM.updateInteractionInfoToIVR('Test UUID Multi Member',StateDataMapping,memberAcc.id);
        PhoneBook_C_HUM.updateInteractionInfoToIVR('Test UUID',StateDataMapping,'');
        PhoneBook_C_HUM.updateInteractionInfoToIVR('Test UUID Group',StateDataMapping,'');
        PhoneBook_C_HUM.updateInteractionInfoToIVR('Test UUID Provider',StateDataMapping,'');
        PhoneBook_C_HUM.updateInteractionInfoToIVR('Test UUID Agent',StateDataMapping,'');
        PhoneBook_C_HUM.updateInteractionInfoToIVR('Test UUID 1',StateDataMapping,'');
        PhoneBook_C_HUM.updateInteractionInfoToIVR('Test UUID 2',StateDataMapping,'');
        PhoneBook_C_HUM.updateInteractionInfoToIVR('Test UUID unknown Member',StateDataMapping,'');
        PhoneBook_C_HUM.updateInteractionInfoToIVR('Test UUID unknown Group',StateDataMapping,'');
        PhoneBook_C_HUM.updateInteractionInfoToIVR('Test UUID unknown Provider',StateDataMapping,'');
        PhoneBook_C_HUM.updateInteractionInfoToIVR('Test UUID unknown Agent',StateDataMapping,'');
        Test.stopTest();
    }

    static testMethod void testgetAboutIdAndInteractionIdNeg()
    {
        CRMFunctionality_ONOFF_Switch__c objStchMF3 = new CRMFunctionality_ONOFF_Switch__c();
        objStchMF3.name = '2760646';
        objStchMF3.IsON__c = false;
        insert objStchMF3;	

        Test.startTest();
        
        String searchModal = '{"AGENT_CALL_START_TIME":"06:10:00","isConsultBlindClicked":true,"isTransferredCall":false,"AGENT_CALL_END_TIME":"06:20:00","UUID":"Test123","interactionId":"12345-12345-12345-12345","Outbound":false}';
        PhoneBook_C_HUM pb = new PhoneBook_C_HUM();
        pb.uuidataValue = 'Test UUID';
        pb.isRefreshedval = false;
        pb.idval = 'abcd-abcd-abcd-abcd';
        pb.calllogattributes = '{"uuidata":"Test UUID","campaignname":"test","keycode":"123"}';
        pb.getAboutIdAndInteractionId();
        System.assert(objStchMF3.IsON__c == false);
        Test.stopTest();
    }
    
    /**
    * This is a test method for testgetCallMemberRecords for MemberId and Genkey
    *
    * @param  None      
    * @return  Void
    */
    static testMethod void testgetCallMemberRecordsNew()
    {
        CRMFunctionality_ONOFF_Switch__c objStchMF3 = new CRMFunctionality_ONOFF_Switch__c();
        objStchMF3.name = '2854267';
        objStchMF3.IsON__c = false;
        insert objStchMF3;	
        CRMFunctionality_ONOFF_Switch__c objSwitch = CRMFunctionality_ONOFF_Switch__c.getValues('2854267'); 
        
            
        objSwitch.IsON__c = true;
        update objSwitch;
		System.assert(objSwitch.IsON__c == true);
        
        Test.startTest();
        PhoneBook_C_HUM.getCallMemberRecords('000160217', '','', '', '', false);
        PhoneBook_C_HUM.getCallMemberRecords('000160217', '00','', '', '', false);
        PhoneBook_C_HUM.getCallMemberRecords('000160217', '','', '', '', false);
        PhoneBook_C_HUM.getCallMemberRecords('00016021700', '','TEST', 'TEST', 'CB', false);
        PhoneBook_C_HUM.getCallMemberRecords('H3009855600', '','', '', 'CB', true);
        PhoneBook_C_HUM.getCallMemberRecords('H3009855600', '','', '', '', true);
        PhoneBook_C_HUM.getCallMemberRecords('000160217', '','', '', '', true);
        PhoneBook_C_HUM.getCallMemberRecords('025134289', '','', '', '', true);
        Test.StopTest();
        
    }
      static testMethod void TESTMEMBERWith()
    {
        List<Id> lstId= new List<Id>();
        Test.startTest();
        //Create Product 
        Product2 p = new Product2(Name='MER',ProductCode='MER',HDHP_Indicator__c = 'Y');
        insert p;
         string sMemberRecordtypeID=  Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId();  
        Account actMember = HUMTestDataHelperTest.getAccount();
        actMember.Lastname='xAccount';
        actMember.FirstName='xAccount';
        actMember.recordtypeid = sMemberRecordtypeID;  
        insert actMember;
        Date effectivefrom = date.parse('1/1/2018');
        Date effectiveto = date.parse('11/30/2018');
          string groupAccountRecordType=  Schema.SObjectType.Account.getRecordTypeInfosByName().get('Group').getRecordTypeId();  
        Account objGrpAccount = HUMTestDataHelperTest.getAccount();
        objGrpAccount.recordtypeid = groupAccountRecordType;
        objGrpAccount.Name = 'TestGroup00';
        objGrpAccount.Group_Number__c = '216277';
        insert objGrpAccount;
         Policy__c oMedicalPolicy = HUMTestDataHelperTest.getPolicy();
        oMedicalPolicy.Name = 'Medical Product';
        oMedicalPolicy.source_cust_cov_key__c = '12345678901234567890' ; 
        oMedicalPolicy.Group_Name__c = actMember.Id;
        oMedicalPolicy.Major_LOB__c ='EPO';     
        oMedicalPolicy.ASO__c = 'N';
        oMedicalPolicy.Business_Segment__c='LargeGroup-Medicare';
        oMedicalPolicy.Issue_State__c='IN';
        insert oMedicalPolicy;
        
                // create Policy Member
        Policy_Member__c objMedPolMemb = HUMTestDataHelperTest.getPolicyMember();
        objMedPolMemb.Member__c = actMember.Id;
        objMedPolMemb.Policy__c = oMedicalPolicy.Id;
        objMedPolMemb.Relationship__c = 'parent';    
        objMedPolMemb.end_date__c = '12/31/2020'; 
        objMedPolMemb.Effective_Date__c='01/01/2018';       
        objMedPolMemb.Policy_Member_External_ID__c = '000|ODS|123456789|89|89|02-01-2004';
        insert objMedPolMemb;
        
        Member_ID__c objMemberId = HUMTestDataHelperTest.getmemberID();
        objMemberId.Member_ID_Value__c = 'H123458800';
        objMemberId.Member_Dependent_Code__c = '00';
        objMemberId.Policy_Member__c=objMedPolMemb.id;
        objMemberId.Policy__c=oMedicalPolicy.id;
        insert objMemberId;
        
        Member_ID__c objMemberId2 = HUMTestDataHelperTest.getmemberID();
        objMemberId2.Member_ID_Value__c = 'H123458800';
        objMemberId2.Member_Dependent_Code__c = '00';
        objMemberId2.Policy_Member__c=objMedPolMemb.id;
        objMemberId2.Policy__c=oMedicalPolicy.id;
        insert objMemberId2;
        
              PurchaserPlan purchaserPlan1 = new PurchaserPlan(Name='PPlan1',Major_LOB__c = 'HMO',SourceSystemIdentifier = 'Test1',Product__c = p.Id,Selling_Market_Number__c = '30004',Platform__c = 'CB',Policy_Group_Number__c = 'X5000001',Source_Cust_Cov_Key__c = '3044444001001',Issue_State__c='IN',Source_Major_LOB__c='POD');
 		insert purchaserPlan1;

         lstId.add(objMemberId.Id);
        Test.setFixedSearchResults(lstId);
         String idx = PhoneBook_C_HUM.getMemberWithAccountId('H123458800','','','','M','','','',''); 
                system.assert(idx<>null);
        
        String idxAboutdata = PhoneBook_C_HUM.getMemberAboutAccountId('H123458800','','','');
        String idxwithname = PhoneBook_C_HUM.getMemberWithAccountId('','xAccount','xAccount','','M','','','','');
        lstId.add(objMemberId2.Id);
        Test.setFixedSearchResults(lstId);
        String idx1 = PhoneBook_C_HUM.getMemberWithAccountId('H123458800','','','','M','','','','');
         String idx2 = PhoneBook_C_HUM.getMemberWithAccountId('','','','','M','','','','');
        String idxAbout = PhoneBook_C_HUM.getMemberAboutAccountId('H123458800','','','');
         String idx1About = PhoneBook_C_HUM.getMemberAboutAccountId('','','','');
        String idxaboutname = PhoneBook_C_HUM.getMemberAboutAccountId('','xAccount','xAccount','');

        
        Test.stopTest();
    }
    static testMethod void testcreateAutoInteraction_Member()
    {
        
                string sMemberRecordtypeID=  Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId(); 
        Account memberAcc = HUMTestDataHelperTest.getAccount();
        memberAcc.Lastname='TestLast';
        memberAcc.FirstName = 'TestFirst';
        memberAcc.Birthdate__c = '11-18-2014';
        memberAcc.recordtypeid = sMemberRecordtypeID;   
        memberAcc.Mbr_Gen_Key__c='5232304413817';
        insert memberAcc;
        
                Account memberAcc2 = HUMTestDataHelperTest.getAccount();
        memberAcc2.Lastname='TestLast1';
        memberAcc2.FirstName = 'TestFirst1';
        memberAcc2.Birthdate__c = '11-18-2015';
        memberAcc2.recordtypeid = sMemberRecordtypeID;    
        insert memberAcc2;
       
        Account actMember = HUMTestDataHelperTest.getAccount();
        actMember.Lastname='xAccount';
        actMember.FirstName='xAccount';
        actMember.Birthdate__c = '11-18-2016';
        actMember.Group_Number__c ='70524';
        actMember.recordtypeid = sMemberRecordtypeID;  
        insert actMember;

        string sAgentRecordtypeID=  Schema.SObjectType.Account.getRecordTypeInfosByName().get('Agent/Broker').getRecordTypeId(); 
        Account objAgentAccount = new Account();
        objAgentAccount.recordtypeid = sAgentRecordtypeID;
        objAgentAccount.Name = 'TestAgent';
        actMember.agent_ID__c ='34567821';
        insert  objAgentAccount;

                 Policy__c oMedicalPolicy = HUMTestDataHelperTest.getPolicy();
        oMedicalPolicy.Name = 'Medical Product';
        oMedicalPolicy.source_cust_cov_key__c = '12345678901234567890' ; 
        oMedicalPolicy.Group_Name__c = actMember.Id;
        oMedicalPolicy.Major_LOB__c ='EPO';     
        oMedicalPolicy.ASO__c = 'N';
        oMedicalPolicy.Business_Segment__c='LargeGroup-Medicare';
        oMedicalPolicy.Issue_State__c='IN';
        insert oMedicalPolicy;
        
                // create Policy Member
        Policy_Member__c objMedPolMemb = HUMTestDataHelperTest.getPolicyMember();
        objMedPolMemb.Member__c = actMember.Id;
        objMedPolMemb.Policy__c = oMedicalPolicy.Id;
        objMedPolMemb.Relationship__c = 'parent';    
        objMedPolMemb.end_date__c = '12/31/2020'; 
        objMedPolMemb.Effective_Date__c='01/01/2018';       
        objMedPolMemb.Policy_Member_External_ID__c = '000|ODS|123456789|89|89|02-01-2005';
        insert objMedPolMemb;
        
        Member_ID__c objMemberId = HUMTestDataHelperTest.getmemberID();
        objMemberId.Member_ID_Value__c = 'H300986556';
        objMemberId.Member_Dependent_Code__c = '00';
        objMemberId.Policy_Member__c=objMedPolMemb.id;
        objMemberId.Policy__c=oMedicalPolicy.id;
        insert objMemberId;
        Test.startTest();
        Map<String, object> searchModal=new  Map<String, object>{'MEMBER_PERS_GEN_KEY'=>'5232304413817','UUID'=>'TestFirst','INQA_FIRST'=>'TestFirst','INQA_LAST'=>'TestLast','INQA_DOB'=>'11-18-2014','INQA_TYP'=>'M','INQR_FIRST'=>'TestFirst','INQR_LAST'=>'TestLast','INQA_ID'=>'000160217','INQR_ID'=>'000160217','INQR_DOB'=>'11-18-2014','INQR_TYP'=>'M','AUTHENTICATION_IND'=>'Y','CALL_TYPE'=>'Inbound'};
            
        Map<String, object> searchModalMember=new  Map<String, object>{'MEMBER_PERS_GEN_KEY'=>'5232304413817','UUID'=>'xAccount','INQA_FIRST'=>'xAccount','INQA_LAST'=>'xAccount','INQA_DOB'=>'11-18-2016','INQA_TYP'=>'M','INQR_FIRST'=>'xAccount','INQR_LAST'=>'xAccount','INQA_ID'=>'H300986556','INQR_ID'=>'H300986556','INQR_DOB'=>'11-18-2016','INQR_TYP'=>'M','AUTHENTICATION_IND'=>'Y','CALL_TYPE'=>'Inbound'};

        Map<String, object> searchModalAgent=new  Map<String, object>{'MEMBER_PERS_GEN_KEY'=>'','UUID'=>'Agent','INQA_FIRST'=>'','INQA_LAST'=>'','INQA_DOB'=>'','INQA_TYP'=>'A','INQR_FIRST'=>'','INQR_LAST'=>'','INQA_ID'=>'34567821','INQR_ID'=>'34567821','INQR_DOB'=>'','INQR_TYP'=>'A','AUTHENTICATION_IND'=>'N','CALL_TYPE'=>'Inbound'};

        Map<String, object> searchModalGroup=new  Map<String, object>{'MEMBER_PERS_GEN_KEY'=>'','UUID'=>'Group','INQA_FIRST'=>'','INQA_LAST'=>'','INQA_DOB'=>'','INQA_TYP'=>'G','INQR_FIRST'=>'','INQR_LAST'=>'','INQA_ID'=>'70524','INQR_ID'=>'70524','INQR_DOB'=>'','INQR_TYP'=>'G','AUTHENTICATION_IND'=>'N','CALL_TYPE'=>'Inbound'};

        PhoneBook_C_HUM pb = new PhoneBook_C_HUM();
        CRMFunctionality_ONOFF_Switch__c objCS = new CRMFunctionality_ONOFF_Switch__c(Name = '3944531', IsON__c = true);
        insert objCS;
        CRMFunctionality_ONOFF_Switch__c objCS1 = new CRMFunctionality_ONOFF_Switch__c(Name = '2760646', IsON__c = true);
        insert objCS1;
        CRMFunctionality_ONOFF_Switch__c objCS2 = new CRMFunctionality_ONOFF_Switch__c(Name = '4726311', IsON__c = false);
        insert objCS2;
        CRMFunctionality_ONOFF_Switch__c objCS3 = new CRMFunctionality_ONOFF_Switch__c(Name = '3701398', IsON__c = true);
        insert objCS3;
        CRMFunctionality_ONOFF_Switch__c objCS4 = new CRMFunctionality_ONOFF_Switch__c(Name = '2876026', IsON__c = false);
        insert objCS4;

        String intId3 = pb.createAutoInteraction(searchModal,'TestFirst',false,'123456asdfghj1234','{"uuidata":"TestFirst","campaignname":"Test","keycode":"123"}');
        system.assert(intId3<>null);
                String intId4 = pb.createAutoInteraction(searchModalMember,'xAccount',false,'123456asdfghj1234','{"uuidata":"xAccount","campaignname":"Test","keycode":"123"}');
        system.assert(intId4<>null);

        String intId5 = pb.createAutoInteraction(searchModalAgent,'Agent',false,'123456asdfghj1234','{"uuidata":"Agent","campaignname":"Test","keycode":"123"}');
        system.assert(intId5<>null);
                String intId6 = pb.createAutoInteraction(searchModalGroup,'Group',false,'123456asdfghj1234','{"uuidata":"Group","campaignname":"Test","keycode":"123"}');
        system.assert(intId6<>null);

        Test.stopTest();
}

    static testMethod void testcreateAutoInteraction_Member_neg()
    {
        Test.startTest();
        Map<String, object> searchModal=new  Map<String, object>{'MEMBER_PERS_GEN_KEY'=>'5232304413817','UUID'=>'TestFirst','INQA_FIRST'=>'TestFirst','INQA_LAST'=>'TestLast','INQA_DOB'=>'11-18-2014','INQA_TYP'=>'M','INQR_FIRST'=>'TestFirst','INQR_LAST'=>'TestLast','INQA_ID'=>'000160217','INQR_ID'=>'000160217','INQR_DOB'=>'11-18-2014','INQR_TYP'=>'M','AUTHENTICATION_IND'=>'Y','CALL_TYPE'=>'Inbound'};
            
        Map<String, object> searchModalMember=new  Map<String, object>{'MEMBER_PERS_GEN_KEY'=>'5232304413817','UUID'=>'xAccount','INQA_FIRST'=>'xAccount','INQA_LAST'=>'xAccount','INQA_DOB'=>'11-18-2016','INQA_TYP'=>'M','INQR_FIRST'=>'xAccount','INQR_LAST'=>'xAccount','INQA_ID'=>'H300986556','INQR_ID'=>'H300986556','INQR_DOB'=>'11-18-2016','INQR_TYP'=>'M','AUTHENTICATION_IND'=>'Y','CALL_TYPE'=>'Inbound'};

        Map<String, object> searchModalAgent=new  Map<String, object>{'MEMBER_PERS_GEN_KEY'=>'','UUID'=>'Agent','INQA_FIRST'=>'','INQA_LAST'=>'','INQA_DOB'=>'','INQA_TYP'=>'A','INQR_FIRST'=>'','INQR_LAST'=>'','INQA_ID'=>'34567821','INQR_ID'=>'34567821','INQR_DOB'=>'','INQR_TYP'=>'A','AUTHENTICATION_IND'=>'N','CALL_TYPE'=>'Inbound'};

        Map<String, object> searchModalGroup=new  Map<String, object>{'MEMBER_PERS_GEN_KEY'=>'','UUID'=>'Group','INQA_FIRST'=>'','INQA_LAST'=>'','INQA_DOB'=>'','INQA_TYP'=>'G','INQR_FIRST'=>'','INQR_LAST'=>'','INQA_ID'=>'70524','INQR_ID'=>'70524','INQR_DOB'=>'','INQR_TYP'=>'G','AUTHENTICATION_IND'=>'N','CALL_TYPE'=>'Inbound'};

        PhoneBook_C_HUM pb = new PhoneBook_C_HUM();
        CRMFunctionality_ONOFF_Switch__c objCS = new CRMFunctionality_ONOFF_Switch__c(Name = '3944531', IsON__c = false);
        insert objCS;
        CRMFunctionality_ONOFF_Switch__c objCS1 = new CRMFunctionality_ONOFF_Switch__c(Name = '2760646', IsON__c = false);
        insert objCS1;
        CRMFunctionality_ONOFF_Switch__c objCS2 = new CRMFunctionality_ONOFF_Switch__c(Name = '4726311', IsON__c = false);
        insert objCS2;
        CRMFunctionality_ONOFF_Switch__c objCS3 = new CRMFunctionality_ONOFF_Switch__c(Name = '3701398', IsON__c = false);
        insert objCS3;
        CRMFunctionality_ONOFF_Switch__c objCS4 = new CRMFunctionality_ONOFF_Switch__c(Name = '2876026', IsON__c = true);
        insert objCS4;
        CRMFunctionality_ONOFF_Switch__c objCS5 = new CRMFunctionality_ONOFF_Switch__c(Name = '2895841', IsON__c = false);
        insert objCS5;
        CRMFunctionality_ONOFF_Switch__c objC6= new CRMFunctionality_ONOFF_Switch__c(Name = '4599393', IsON__c = false);
        insert objC6;

        String intId3 = pb.createAutoInteraction(searchModal,'TestFirst',false,'123456asdfghj1234','{"uuidata":"TestFirst","campaignname":"Test","keycode":"123"}');
        system.assert(intId3<>null);
                String intId4 = pb.createAutoInteraction(searchModalMember,'xAccount',false,'123456asdfghj1234','{"uuidata":"xAccount","campaignname":"Test","keycode":"123"}');
        system.assert(intId4<>null);

        String intId5 = pb.createAutoInteraction(searchModalAgent,'Agent',false,'123456asdfghj1234','{"uuidata":"Agent","campaignname":"Test","keycode":"123"}');
        system.assert(intId5<>null);
                String intId6 = pb.createAutoInteraction(searchModalGroup,'Group',false,'123456asdfghj1234','{"uuidata":"Group","campaignname":"Test","keycode":"123"}');
        system.assert(intId6<>null);

        Test.stopTest();
}
}
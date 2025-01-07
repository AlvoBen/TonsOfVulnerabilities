/*******************************************************************************************************************************
Apex Class Name : PhoneBook_LT_HUM.cls
Version         : 1.0
Created On      : Feb 16,2021
Function        : Class contains test methods Phonebook_LC_HUM class.

Modification Log: 
* Developer Name            Code Review                Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
*  Arpit Jain/Navajit Sarkar                          02/16/2021              Original Version 
*  Arpit Jain/Navajit Sarkar                          03/09/2021              PhoneBook Related Changes
*  Arpit Jain                                         07/12/2021              US-2357061 : Test methods added for disconnect and transfers saveCallData
*  Harshada Kamble                                     08/19/2022              User Story 3653389: Test methods added for PCC Genesys Migration - Transfer to Provider Survey
* Santhi Mandava                                         02/06/2013                 User Story 4082261,4084543: Display interaction information on search page and account detail page.
* Harshada Kamble/Anil Pavithran                       05/05/2023                 US4461937 T1PRJ0036776: SFDC Ability to Manually Modify Authentication Status (Multi Members) in Lightning - Genesys
* Mayur Pardeshi                                       07/05/2023				User Story 4599393: T1PRJ0036776: Ability To Create Interactions in Genesys when calls are placed to Producer Services
* Kiran Kotni                                            08/04/2023                 User Story 4863232: T1PRJ0036776: RxE - Lightning - Save Campaign Keycode & Campaign name data to existing column "interaction :campaign name and Keycode" - Genesys
********************************************************************************************************************************/

@isTest
Private class PhoneBook_LT_HUM 
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
        eBilling.FlowId__c = '1234567890'; 
        SecurePayments_HUM__c pharmacy = new SecurePayments_HUM__c();
        pharmacy.Name = 'Pharmacy';
        pharmacy.SecurePaymentName__c = 'Pharmacy';
        pharmacy.IsLightning__c = true; 
        pharmacy.FlowId__c = '1234567890';  
        SecurePayments_HUM__c cbis = new SecurePayments_HUM__c();
        cbis.Name = 'CBIS';
        cbis.SecurePaymentName__c = 'CBIS';
        cbis.IsLightning__c = true;
        cbis.FlowId__c = '1234567890';
        
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
        allAccounts.add(objGrpAccount);

        Account objGrpAccount1 = HUMTestDataHelperTest.getAccount();
        objGrpAccount1.recordtypeid = groupAccountRecordType;
        objGrpAccount1.Name = 'TestGroup11';
        objGrpAccount1.Group_Number__c = '216277';
        allAccounts.add(objGrpAccount1);
        
        string sMemberRecordtypeID=  Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId();  
        Account actMember = HUMTestDataHelperTest.getAccount();
        actMember.Lastname='xAccount';
        actMember.recordtypeid = sMemberRecordtypeID;    
        allAccounts.add(actMember);

        Account memberAcc = new Account();
        memberAcc.recordtypeid = sMemberRecordtypeID;
        memberAcc.Lastname = 'TestAcc';
        memberAcc.FirstName='Member';
        allAccounts.add(memberAcc);
        
        string sProviderRecordtypeID=  Schema.SObjectType.Account.getRecordTypeInfosByName().get('Provider').getRecordTypeId(); 
        Account objProviderAccount = new Account();
        objProviderAccount.recordtypeid = sProviderRecordtypeID;
        objProviderAccount.Name = 'TestProvider';
        allAccounts.add(objProviderAccount);

        Account objProviderAccount1 = new Account();
        objProviderAccount1.recordtypeid = sProviderRecordtypeID;
        objProviderAccount1.Name = 'TestProvider1';
        allAccounts.add(objProviderAccount1);
        
        string sAgentRecordtypeID=  Schema.SObjectType.Account.getRecordTypeInfosByName().get('Agent/Broker').getRecordTypeId(); 
        Account objAgentAccount = new Account();
        objAgentAccount.recordtypeid = sAgentRecordtypeID;
        objAgentAccount.Name = 'TestAgent';
        objAgentAccount.agent_ID__c = '123agent';
        allAccounts.add(objAgentAccount);

        Account objAgentAccount1 = new Account();
        objAgentAccount1.recordtypeid = sAgentRecordtypeID;
        objAgentAccount1.Name = 'TestAgent11';
        objAgentAccount1.agent_ID__c = '123agent';
        allAccounts.add(objAgentAccount1);

        string sUnknownMemberRecordtypeID=  Schema.SObjectType.Account.getRecordTypeInfosByName().get('Unknown Member').getRecordTypeId();  
        Account actUnknownMember = HUMTestDataHelperTest.getAccount();
        actUnknownMember.Lastname='UnknownxAccount';
        actUnknownMember.recordtypeid = sUnknownMemberRecordtypeID;    
        allAccounts.add(actUnknownMember);

        string UnknowngroupAccountRecordType=  Schema.SObjectType.Account.getRecordTypeInfosByName().get('Unknown Group').getRecordTypeId();  
        Account objUnknownGrpAccount = HUMTestDataHelperTest.getAccount();
        objUnknownGrpAccount.recordtypeid = UnknowngroupAccountRecordType;
        objUnknownGrpAccount.Name = 'Unknown TestGroup00';
        objUnknownGrpAccount.Group_Number__c = '216277';
        allAccounts.add(objUnknownGrpAccount);

        string sUnknownProviderRecordtypeID=  Schema.SObjectType.Account.getRecordTypeInfosByName().get('Unknown Provider').getRecordTypeId(); 
        Account objUnknownProviderAccount = new Account();
        objUnknownProviderAccount.recordtypeid = sUnknownProviderRecordtypeID;
        objUnknownProviderAccount.Name = ' Unknown TestProvider';
        allAccounts.add(objUnknownProviderAccount);

        string sUnknownAgentRecordtypeID=  Schema.SObjectType.Account.getRecordTypeInfosByName().get('Unknown Agent/Broker').getRecordTypeId(); 
        Account objUnknownAgentAccount = new Account();
        objUnknownAgentAccount.recordtypeid = sUnknownAgentRecordtypeID;
        objUnknownAgentAccount.Name = 'Unknown TestAgent';
        objUnknownAgentAccount.agent_ID__c = '123agent';
        allAccounts.add(objUnknownAgentAccount);

        insert allAccounts;
        system.assertEquals(allAccounts.size(),12);

        //Create ConsumerID 
        ConsumerID__c consumer = new ConsumerID__c();
        consumer.Consumer_ID__c = '12345';
        consumer.account__c  =  objProviderAccount.Id;
        insert consumer;
        system.assert(consumer.id<>null);

        ConsumerID__c consumer1 = new ConsumerID__c();
        consumer1.Consumer_ID__c = '12345';
        consumer1.account__c  =  objProviderAccount1.Id;
        insert consumer1;
        system.assert(consumer1.id<>null);
        
        ConsumerID__c consumer2 = new ConsumerID__c();
        consumer2.Consumer_ID__c = 'Un12345';
        consumer2.account__c  =  objUnknownProviderAccount.Id;
        insert consumer2;
        system.assert(consumer2.id<>null);

        Policy__c oMedicalPolicy = HUMTestDataHelperTest.getPolicy();
        oMedicalPolicy.Name = 'Medical Product';
        oMedicalPolicy.source_cust_cov_key__c = '12345678901234567890' ; 
        oMedicalPolicy.Group_Name__c = objGrpAccount.Id;
        oMedicalPolicy.Major_LOB__c ='EPO';     
        oMedicalPolicy.ASO__c = 'N';
        oMedicalPolicy.Business_Segment__c='LargeGroup-Medicare';
        oMedicalPolicy.Issue_State__c='IN';
        insert oMedicalPolicy;
        system.assertEquals(oMedicalPolicy.Name == 'Medical Product', true);
        
        //Create Product 
        Product2 p = new Product2(Name='MER',ProductCode='MER',HDHP_Indicator__c = 'Y');
        insert p;
        system.assert(p.id!=null);
        
        //Create Purchaser Plan
        List<PurchaserPlan> purchaserPlanlst = new List<PurchaserPlan>();
        PurchaserPlan purchaserPlan1 = new PurchaserPlan(Name='PPlan1',Major_LOB__c = 'HMO',SourceSystemIdentifier = 'Test1',Product__c = p.Id,Selling_Market_Number__c = '30004',Platform__c = 'CB',Policy_Group_Number__c = 'X5000001',Source_Cust_Cov_Key__c = '3044444001001',Issue_State__c='IN',Source_Major_LOB__c='POD');
        PurchaserPlan purchaserPlan2 = new PurchaserPlan(Name='PPlan2',Major_LOB__c = 'DHL',SourceSystemIdentifier = 'Test2',Product__c = p.Id,Selling_Market_Number__c = '30004',Platform__c = 'CB',Policy_Group_Number__c = 'X5000001',Source_Cust_Cov_Key__c = '3044444001001',Issue_State__c='IN');
        PurchaserPlan purchaserPlan3 = new PurchaserPlan(Name='PPlan3',Major_LOB__c = 'DHL',SourceSystemIdentifier = 'Test3',Product__c = p.Id,Selling_Market_Number__c = '30004',Platform__c = 'LV',Policy_Group_Number__c = 'X5000001',Source_Cust_Cov_Key__c = '3044444001001',Issue_State__c='IN');
        purchaserPlanlst.add(purchaserPlan1);
        purchaserPlanlst.add(purchaserPlan2);
        purchaserPlanlst.add(purchaserPlan3);
        insert purchaserPlanlst;
        system.assertEquals(purchaserPlanlst.size(),3);
        
        //Create Member Plan
        Date effectivefrom = date.parse('1/1/2018');
        Date effectiveto = date.parse('11/30/2018');
        List<MemberPlan> memberPlanlst = new List<MemberPlan>();
        MemberPlan memberPlan1 = new MemberPlan(Name = 'H123458800',RelationshipToSubscriber = 'SUBSCRIBER',PlanId = purchaserPlanlst[0].Id ,EffectiveTo = effectiveto,EffectiveFrom = effectivefrom,GroupNumber = 'X5111001',MemberId = actMember.id,Policy__c = oMedicalPolicy.Id );
        MemberPlan memberPlan2 = new MemberPlan(Name = 'H123458811',RelationshipToSubscriber = 'SUBSCRIBER',PlanId = purchaserPlanlst[1].Id ,EffectiveTo = effectiveto,EffectiveFrom = effectivefrom,GroupNumber = 'X5111001',MemberId = actMember.id,Policy__c = oMedicalPolicy.Id );
        MemberPlan memberPlan3 = new MemberPlan(Name = 'H123458822',RelationshipToSubscriber = 'SUBSCRIBER',PlanId = purchaserPlanlst[2].Id ,EffectiveTo = effectiveto,EffectiveFrom = effectivefrom,GroupNumber = 'X5111001',MemberId = actMember.id,Policy__c = oMedicalPolicy.Id );
        MemberPlan memberPlan4 = new MemberPlan(Name = 'H98765432100',RelationshipToSubscriber = 'SUBSCRIBER',PlanId = purchaserPlanlst[1].Id ,EffectiveTo = effectiveto,EffectiveFrom = effectivefrom,GroupNumber = 'X5111001',MemberId = actMember.id,Policy__c = oMedicalPolicy.Id,Member_Id_Base__c = 'H987654321' );
        MemberPlan memberPlan5 = new MemberPlan(Name = 'H98765432101',RelationshipToSubscriber = 'SUBSCRIBER',PlanId = purchaserPlanlst[2].Id ,EffectiveTo = effectiveto,EffectiveFrom = effectivefrom,GroupNumber = 'X5111001',MemberId = memberAcc.id,Policy__c = oMedicalPolicy.Id,Member_Id_Base__c = 'H987654321');
        memberPlanlst.add(memberPlan1);
        memberPlanlst.add(memberPlan2);
        memberPlanlst.add(memberPlan3);
        memberPlanlst.add(memberPlan4);
        memberPlanlst.add(memberPlan5);
        insert memberPlanlst;
        system.assertEquals(memberPlanlst.size(),5);
        
        //Create Interaction and interaction Member Data for different Account types
        List<Interaction__c> listinteraction = new List<Interaction__c>();
        Interaction__c interAction = new Interaction__c(Interaction_Origin__c = 'Inbound Call', Interacting_With__c=actMember.id,UUID__c='Test UUID',Interacting_With_type__c= 'Member');
        Interaction__c interActiongroup = new Interaction__c(Interaction_Origin__c = 'Inbound Call', Interacting_With__c=objGrpAccount.id,UUID__c='Test UUID Group',Interacting_With_type__c= 'Group');
        Interaction__c interActionProvider = new Interaction__c(Interaction_Origin__c = 'Inbound Call', Interacting_With__c=objProviderAccount.id,UUID__c='Test UUID Provider',Interacting_With_type__c= 'Provider');
        Interaction__c interActionAgent = new Interaction__c(Interaction_Origin__c = 'Inbound Call', Interacting_With__c=objAgentAccount.id,UUID__c='Test UUID Agent',Interacting_With_type__c= 'Agent');
        Interaction__c interActionUnknownMember = new Interaction__c(Interaction_Origin__c = 'Inbound Call', Interacting_With__c=actUnknownMember.id,UUID__c='Test UUID Unknown Member',Interacting_With_type__c= 'Unknown-Member',Authenticated__c = true);
        Interaction__c interActionUnknowngroup = new Interaction__c(Interaction_Origin__c = 'Inbound Call', Interacting_With__c=objUnknownGrpAccount.id,UUID__c='Test UUID Unknown Group',Interacting_With_type__c= 'Unknown-Group');
        Interaction__c interActionUnknownProvider = new Interaction__c(Interaction_Origin__c = 'Inbound Call', Interacting_With__c=objUnknownProviderAccount.id,UUID__c='Test UUID Unknown Provider',Interacting_With_type__c= 'Unknown-Provider');
        Interaction__c interActionUnknownAgent = new Interaction__c(Interaction_Origin__c = 'Inbound Call', Interacting_With__c=objUnknownAgentAccount.id,UUID__c='Test UUID Unknown Agent',Interacting_With_type__c= 'Unknown-Agent');
        listinteraction.add(interAction);
        listinteraction.add(interActiongroup);
        listinteraction.add(interActionProvider);
        listinteraction.add(interActionAgent);
        listinteraction.add(interActionUnknownMember);
        listinteraction.add(interActionUnknowngroup);
        listinteraction.add(interActionUnknownProvider);
        listinteraction.add(interActionUnknownAgent);
        insert listinteraction ;
        system.assertEquals(listinteraction.size(),8);
        
        List<Interaction_Member__c> interactionMembers = new List<Interaction_Member__c>();
        Interaction_Member__c intMem = new Interaction_Member__c(Interacting_About__c = actMember.id,Interaction__c = interAction.id ,Interacting_About_Type__c ='Member');             
        Interaction_Member__c intMemgroup = new Interaction_Member__c(Interacting_About__c = objGrpAccount.id,Interaction__c = interActiongroup.id ,Interacting_About_Type__c ='Group');             
        Interaction_Member__c intMemAgent = new Interaction_Member__c(Interacting_About__c = objAgentAccount.id,Interaction__c = interActionAgent.id ,Interacting_About_Type__c ='Agent');             
        Interaction_Member__c intMemProvider = new Interaction_Member__c(Interacting_About__c = objProviderAccount.id,Interaction__c = interActionProvider.id ,Interacting_About_Type__c ='Provider');     
        Interaction_Member__c intMemUnknownMember = new Interaction_Member__c(Interacting_About__c = actUnknownMember.id,Interaction__c = interActionUnknownMember.id ,Interacting_About_Type__c ='Unknown-Member');        
        Interaction_Member__c intMemUnknowngroup = new Interaction_Member__c(Interacting_About__c = objUnknownGrpAccount.id,Interaction__c = interActionUnknowngroup.id ,Interacting_About_Type__c ='Unknown-Group');             
        Interaction_Member__c intMemUnknownAgent = new Interaction_Member__c(Interacting_About__c = objUnknownAgentAccount.id,Interaction__c = interActionUnknownAgent.id ,Interacting_About_Type__c ='Unknown-Agent');             
        Interaction_Member__c intMemUnknownProvider = new Interaction_Member__c(Interacting_About__c = objUnknownProviderAccount.id,Interaction__c = interActionUnknownProvider.id ,Interacting_About_Type__c ='Unknown-Provider');
        interactionMembers.add(intMem);
        interactionMembers.add(intMemgroup);
        interactionMembers.add(intMemAgent);
        interactionMembers.add(intMemProvider);
        interactionMembers.add(intMemUnknownMember);
        interactionMembers.add(intMemUnknowngroup);
        interactionMembers.add(intMemUnknownAgent);
        interactionMembers.add(intMemUnknownProvider);
        insert interactionMembers;
        system.assertEquals(interactionMembers.size(),8);
        
        Integration_Mapping__c intMappingRec = new Integration_Mapping__c();
        intMappingRec.Mapping__c ='Policy Member:'+memberPlan1.id+';Inquired For:'+actMember.id+';Inquired About:'+actMember.id;
        intMappingRec.Record_Id__c = 'Test UUID';
        insert intMappingRec;
        system.assert(intMappingRec.Id <> null);
        
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
        CRMFunctionality_ONOFF_Switch__c objStch2 = new CRMFunctionality_ONOFF_Switch__c();
        objStch2.name = '4707880';
        objStch2.IsON__c = true;
        listOfSwitch.add(objStch2);
        CRMFunctionality_ONOFF_Switch__c objStch3 = new CRMFunctionality_ONOFF_Switch__c();
        objStch3.name = '4726311';
        objStch3.IsON__c = true;
        listOfSwitch.add(objStch3);
        CRMFunctionality_ONOFF_Switch__c objStch4 = new CRMFunctionality_ONOFF_Switch__c();
        objStch4.name = '2760646';
        objStch4.IsON__c = true;
        listOfSwitch.add(objStch4);
        CRMFunctionality_ONOFF_Switch__c objStch5 = new CRMFunctionality_ONOFF_Switch__c();
        objStch5.name = '4472300';
        objStch5.IsON__c = true;
        listOfSwitch.add(objStch5);
        CRMFunctionality_ONOFF_Switch__c objStch6 = new CRMFunctionality_ONOFF_Switch__c();
        objStch6.name = '4734084';
        objStch6.IsON__c = false;
        listOfSwitch.add(objStch6);
        CRMFunctionality_ONOFF_Switch__c objStch7 = new CRMFunctionality_ONOFF_Switch__c();
        objStch7.name = '4461937';
        objStch7.IsON__c = true; 
        listOfSwitch.add(objStch7);
        CRMFunctionality_ONOFF_Switch__c objStch8 = new CRMFunctionality_ONOFF_Switch__c();
        objStch8.name = '4863232';
        objStch8.IsON__c = true;
		listOfSwitch.add(objStch8);
        CRMFunctionality_ONOFF_Switch__c objStch9 = new CRMFunctionality_ONOFF_Switch__c();
        objStch9.name = '3944531';
        objStch9.IsON__c = true;
		listOfSwitch.add(objStch9);
        CRMFunctionality_ONOFF_Switch__c objStch10 = new CRMFunctionality_ONOFF_Switch__c();
        objStch10.name = '2895841';
        objStch10.IsON__c = true;
		listOfSwitch.add(objStch10);
        CRMFunctionality_ONOFF_Switch__c objStch11 = new CRMFunctionality_ONOFF_Switch__c();
        objStch11.name = '3701398';
        objStch11.IsON__c = true;
		listOfSwitch.add(objStch11);
        insert listOfSwitch;
        System.assert(listOfSwitch.size() == 13);
    }
    static testMethod void testPhoneBook_LC_HUM()
    {
        Test.startTest();
        PhoneBook_LC_HUM objLC = new PhoneBook_LC_HUM();
        PhoneBook_LS_HUM objLS = new PhoneBook_LS_HUM();   
        Continuation continueObj = (Continuation)PhoneBook_LC_HUM.getCallData('12102000001270800029030001860434');
        continueObj.continuationMethod = 'getprocessResponse';
        continueObj.state = 'State';
        system.assertEquals('getprocessResponse', continueObj.continuationMethod); 
        HttpResponse httpRes = new Httpresponse ();
        String resBodyString = '{"GetCallDataResponse":{ "GetCallDataResult":{ "Application":"", "UUID":"12102000001270800029030001860434", "CallDataValues":{ "CallDataValuesList":[ { "DataName":"ACTIVE_CALLERCOUNT_ONCALL", "DataValue":"0" }, { "DataName":"ANI", "DataValue":"4092915301" }, { "DataName":"ASO_IND", "DataValue":"True" }, { "DataName":"AUTHENTICATION_IND", "DataValue":"Y" }, { "DataName":"BUS_SEGMENT_IND", "DataValue":"L" }, { "DataName":"Call_Catg_Desc", "DataValue":"Medical Eligibility" }, { "DataName":"Client_ID", "DataValue":"IVR" }, { "DataName":"CLAIM_GEN_KEY", "DataValue":"" }, { "DataName":"DATA_SRC", "DataValue":"IVR" }, { "DataName":"HDHP_IND", "DataValue":"True" }, { "DataName":"INQ_DOS", "DataValue":"" }, { "DataName":"INQ_RSN", "DataValue":"ELG" }, { "DataName":"INQ_RSN1", "DataValue":"ELG" }, { "DataName":"INQA_DOB", "DataValue":"11-18-2014" }, { "DataName":"INQA_FIRST", "DataValue":"JUN-KANG" }, { "DataName":"INQA_GRP_ID", "DataValue":"923382" }, { "DataName":"INQA_GRP_NAME", "DataValue":"HUMANA INC" }, { "DataName":"INQA_ID", "DataValue":"000160217" }, { "DataName":"INQA_LAST", "DataValue":"SINICKI" }, { "DataName":"INQA_MKT_ID", "DataValue":"0" }, { "DataName":"INQA_PLTFRM_CD", "DataValue":"EM" }, { "DataName":"INQA_RELN", "DataValue":"" }, { "DataName":"INQA_TYP", "DataValue":"G" }, { "DataName":"INQR_TYP", "DataValue":"M" }, { "DataName":"INQUIRY_ID", "DataValue":"" }, { "DataName":"IVR_CALL_DATE", "DataValue":"20201210" }, { "DataName":"IVR_CALL_TIME", "DataValue":"01:26:26" }, { "DataName":"IVR_CALLER_TYPE", "DataValue":"M" }, { "DataName":"IVR_DEN_OI_CHG_IND", "DataValue":"" }, { "DataName":"IVR_MED_OI_CHG_IND", "DataValue":"" }, { "DataName":"IVR_SERVER_CD", "DataValue":"03" }, { "DataName":"IVR_SERVER_PORT", "DataValue":"209" }, { "DataName":"LAST_AGENT_ID", "DataValue":"3378044" }, { "DataName":"MAJOR_LOB", "DataValue":"NPOS" }, { "DataName":"MEMBER_DOB", "DataValue":"11/18/2014" }, { "DataName":"MEMBER_ID", "DataValue":"000160217" }, { "DataName":"MEMBER_PERS_GEN_KEY", "DataValue":"554004556423" }, { "DataName":"MTV_BL5", "DataValue":"HHP-HUM INC" }, { "DataName":"MTV_BL7", "DataValue":"NPOS" }, { "DataName":"MULTI_COV", "DataValue":"" }, { "DataName":"NUMBER_OF_TRANSFERS", "DataValue":"1" }, { "DataName":"PROD_TYPE_CD", "DataValue":"" }, { "DataName":"PROD_TYPE_CODE", "DataValue":"NPOSMO" }, { "DataName":"PRODPLANID", "DataValue":"" }, { "DataName":"REFERENCE_ID", "DataValue":"" }, { "DataName":"RX_ID", "DataValue":"" }, { "DataName":"SELLING_LEDGER", "DataValue":"0" }, { "DataName":"STATE_OF_ISSUE", "DataValue":"KY" }, { "DataName":"SURVEY_ID", "DataValue":"50" }, { "DataName":"UUID", "DataValue":"12102000001270800029030001860434" }, { "DataName":"VENDOR_ASOFDATE", "DataValue":"2020-12-10" }, { "DataName":"VENDOR_GROUPID", "DataValue":"" }, { "DataName":"VENDOR_SUB_GROUPID", "DataValue":"92338209" } ] } } }}';
        httpRes.setBody(resBodyString);
        system.assert(httpRes.getBody() == resBodyString);
        Map<String, HTTPRequest> mapDetails = continueObj.getRequests();
        string reqType = '';
        for(String s : mapDetails.keyset())
        {
            reqType = s;
        } 
        Test.setContinuationResponse(reqType,httpRes);
        String result = (String)PhoneBook_LC_HUM.getprocessResponse(reqType);
        System.assertNotEquals(result, null);
        Test.stopTest();
    }
    
    static testMethod void testfaultPhoneBook_LC_HUM()
    {
        Test.startTest();   
        Continuation continueObj = (Continuation)PhoneBook_LC_HUM.getCallData('12102000001270800029030001860434');
        continueObj.continuationMethod = 'getprocessResponse';
        continueObj.state = 'State';
        system.assertEquals('getprocessResponse', continueObj.continuationMethod); 
        HttpResponse httpRes = new Httpresponse ();
        String resBodyString = '{"GetCallDataResponse":{ "GetCallDataResult":{ "Application":"", "UUID":"12102000001270800029030001860434", "CallDataValues":{ "CallDataValuesList":[ { "DataName":"ACTIVE_CALLERCOUNT_ONCALL", "DataValue":"0" }, { "DataName":"ANI", "DataValue":"4092915301" }, { "DataName":"ASO_IND", "DataValue":"True" }, { "DataName":"AUTHENTICATION_IND", "DataValue":"Y" }, { "DataName":"BUS_SEGMENT_IND", "DataValue":"L" }, { "DataName":"Call_Catg_Desc", "DataValue":"Medical Eligibility" }, { "DataName":"Client_ID", "DataValue":"IVR" }, { "DataName":"CLAIM_GEN_KEY", "DataValue":"" }, { "DataName":"DATA_SRC", "DataValue":"IVR" }, { "DataName":"HDHP_IND", "DataValue":"True" }, { "DataName":"INQ_DOS", "DataValue":"" }, { "DataName":"INQ_RSN", "DataValue":"ELG" }, { "DataName":"INQ_RSN1", "DataValue":"ELG" }, { "DataName":"INQA_DOB", "DataValue":"11-18-2014" }, { "DataName":"INQA_FIRST", "DataValue":"JUN-KANG" }, { "DataName":"INQA_GRP_ID", "DataValue":"923382" }, { "DataName":"INQA_GRP_NAME", "DataValue":"HUMANA INC" }, { "DataName":"INQA_ID", "DataValue":"000160217" }, { "DataName":"INQA_LAST", "DataValue":"SINICKI" }, { "DataName":"INQA_MKT_ID", "DataValue":"0" }, { "DataName":"INQA_PLTFRM_CD", "DataValue":"EM" }, { "DataName":"INQA_RELN", "DataValue":"" }, { "DataName":"INQA_TYP", "DataValue":"G" }, { "DataName":"INQR_TYP", "DataValue":"M" }, { "DataName":"INQUIRY_ID", "DataValue":"" }, { "DataName":"IVR_CALL_DATE", "DataValue":"20201210" }, { "DataName":"IVR_CALL_TIME", "DataValue":"01:26:26" }, { "DataName":"IVR_CALLER_TYPE", "DataValue":"M" }, { "DataName":"IVR_DEN_OI_CHG_IND", "DataValue":"" }, { "DataName":"IVR_MED_OI_CHG_IND", "DataValue":"" }, { "DataName":"IVR_SERVER_CD", "DataValue":"03" }, { "DataName":"IVR_SERVER_PORT", "DataValue":"209" }, { "DataName":"LAST_AGENT_ID", "DataValue":"3378044" }, { "DataName":"MAJOR_LOB", "DataValue":"NPOS" }, { "DataName":"MEMBER_DOB", "DataValue":"11/18/2014" }, { "DataName":"MEMBER_ID", "DataValue":"000160217" }, { "DataName":"MEMBER_PERS_GEN_KEY", "DataValue":"554004556423" }, { "DataName":"MTV_BL5", "DataValue":"HHP-HUM INC" }, { "DataName":"MTV_BL7", "DataValue":"NPOS" }, { "DataName":"MULTI_COV", "DataValue":"" }, { "DataName":"NUMBER_OF_TRANSFERS", "DataValue":"1" }, { "DataName":"PROD_TYPE_CD", "DataValue":"" }, { "DataName":"PROD_TYPE_CODE", "DataValue":"NPOSMO" }, { "DataName":"PRODPLANID", "DataValue":"" }, { "DataName":"REFERENCE_ID", "DataValue":"" }, { "DataName":"RX_ID", "DataValue":"" }, { "DataName":"SELLING_LEDGER", "DataValue":"0" }, { "DataName":"STATE_OF_ISSUE", "DataValue":"KY" }, { "DataName":"SURVEY_ID", "DataValue":"50" }, { "DataName":"UUID", "DataValue":"12102000001270800029030001860434" }, { "DataName":"VENDOR_ASOFDATE", "DataValue":"2020-12-10" }, { "DataName":"VENDOR_GROUPID", "DataValue":"" }, { "DataName":"VENDOR_SUB_GROUPID", "DataValue":"92338209" } ] } } }}';
        httpRes.setBody(resBodyString);
        system.assert(httpRes.getBody() == resBodyString);
        Map<String, HTTPRequest> mapDetails = continueObj.getRequests();
        string reqType = '';
        for(String s : mapDetails.keyset())
        {
            reqType = s;
        } 
        Test.setContinuationResponse(reqType,httpRes);
        String result = (String)PhoneBook_LC_HUM.getprocessResponse(null);
        System.assertNotEquals(result, null);
        Test.stopTest();
    }
    
    static testMethod void testNegativePhoneBook_LC_HUM()
    {
        Test.startTest(); 
        Continuation continueObj = (Continuation)PhoneBook_LC_HUM.getCallData('');
        System.assertEquals(continueObj, null);
        Test.stopTest();
    }
    
    static testMethod void testgetAllPhoneBookRules()
    {
        Test.startTest();   
        Continuation continueObj = (Continuation)PhoneBook_LC_HUM.getAllPhoneBookRules();
        continueObj.continuationMethod = 'processResponsegetAllPhoneBookRules';
        continueObj.state = 'State';
        system.assertEquals('processResponsegetAllPhoneBookRules', continueObj.continuationMethod); 
        HttpResponse httpRes = new Httpresponse ();
        String resBodyString = '{"GetAllPhoneBookRulesResponse":{"AllPhoneBookRulesResult":{"AllPhoneBookRulesResultList":[{"Key":"Phonebook RXS","TransferNumber":{"TransferNumberList":[{"CreateUpdateOn":"1/18/2017 7:54:58 AM","Flag":"1","Notes":"7D","OperatingHours":"7D","OriginalTransferNumber":"7410852","QueueName":"7D","ShareableNumber":"7410852963","TransferNumber":"7410852","TransferNumberID":"385","UserID":"Komal Wakhare"}]}},{"Key":"Dhatchayinis phonebo","TransferNumber":{"TransferNumberList":[{"CreateUpdateOn":"4/27/2017 2:36:33 AM","Flag":"1","Notes":"","OperatingHours":"","OriginalTransferNumber":"8005552546","QueueName":"Pharmacy  Clinical Review Provider","ShareableNumber":"8005552546","TransferNumber":"8005552546","TransferNumberID":"152","UserID":"dxb4557"},{"CreateUpdateOn":"4/27/2017 2:31:18 AM","Flag":"1","Notes":"","OperatingHours":"","OriginalTransferNumber":"8005552546","QueueName":"Pharmacy  Clinical Review Provider","ShareableNumber":"8005552546","TransferNumber":"8005552546","TransferNumberID":"152","UserID":"dxb4557"}]}},{"Key":"testing Aditya","TransferNumber":{"TransferNumberList":[{"CreateUpdateOn":"10/19/2016 3:27:56 PM","Flag":"1","Notes":"Testing Purpose","OperatingHours":"0800-1600","OriginalTransferNumber":"5028071","QueueName":"Aditya Test Queue","ShareableNumber":"5028070202","TransferNumber":"5028071","TransferNumberID":"388","UserID":"Aditya Bhave"},{"CreateUpdateOn":"12/29/2016 3:35:16 PM","Flag":"1","Notes":"test","OperatingHours":"test","OriginalTransferNumber":"7894561","QueueName":"test","ShareableNumber":"7894561230","TransferNumber":"7894561","TransferNumberID":"386","UserID":"Aditya Bhave"}]}}]}}}';
        httpRes.setBody(resBodyString);
        system.assert(httpRes.getBody() == resBodyString);
        Map<String, HTTPRequest> mapDetails = continueObj.getRequests();
        string reqType = '';
        for(String s : mapDetails.keyset())
        {
            reqType = s;
        } 
        Test.setContinuationResponse(reqType,httpRes);
        String result = (String)PhoneBook_LC_HUM.processResponsegetAllPhoneBookRules(reqType);
        System.assertNotEquals(result, null);
        Test.stopTest();
    }
    
    static testMethod void testNegativegetAllPhoneBookRules()
    {
        Test.startTest();   
        Continuation continueObj = (Continuation)PhoneBook_LC_HUM.getAllPhoneBookRules();
        continueObj.continuationMethod = 'processResponsegetAllPhoneBookRules';
        continueObj.state = 'State';
        system.assertEquals('processResponsegetAllPhoneBookRules', continueObj.continuationMethod); 
        HttpResponse httpRes = new Httpresponse ();
        String resBodyString = '{"GetAllPhoneBookRulesResponse":{"AllPhoneBookRulesResult":{"AllPhoneBookRulesResultList":[{"Key":"Phonebook RXS","TransferNumber":{"TransferNumberList":[{"CreateUpdateOn":"1/18/2017 7:54:58 AM","Flag":"1","Notes":"7D","OperatingHours":"7D","OriginalTransferNumber":"7410852","QueueName":"7D","ShareableNumber":"7410852963","TransferNumber":"7410852","TransferNumberID":"385","UserID":"Komal Wakhare"}]}},{"Key":"Dhatchayinis phonebo","TransferNumber":{"TransferNumberList":[{"CreateUpdateOn":"4/27/2017 2:36:33 AM","Flag":"1","Notes":"","OperatingHours":"","OriginalTransferNumber":"8005552546","QueueName":"Pharmacy  Clinical Review Provider","ShareableNumber":"8005552546","TransferNumber":"8005552546","TransferNumberID":"152","UserID":"dxb4557"},{"CreateUpdateOn":"4/27/2017 2:31:18 AM","Flag":"1","Notes":"","OperatingHours":"","OriginalTransferNumber":"8005552546","QueueName":"Pharmacy  Clinical Review Provider","ShareableNumber":"8005552546","TransferNumber":"8005552546","TransferNumberID":"152","UserID":"dxb4557"}]}},{"Key":"testing Aditya","TransferNumber":{"TransferNumberList":[{"CreateUpdateOn":"10/19/2016 3:27:56 PM","Flag":"1","Notes":"Testing Purpose","OperatingHours":"0800-1600","OriginalTransferNumber":"5028071","QueueName":"Aditya Test Queue","ShareableNumber":"5028070202","TransferNumber":"5028071","TransferNumberID":"388","UserID":"Aditya Bhave"},{"CreateUpdateOn":"12/29/2016 3:35:16 PM","Flag":"1","Notes":"test","OperatingHours":"test","OriginalTransferNumber":"7894561","QueueName":"test","ShareableNumber":"7894561230","TransferNumber":"7894561","TransferNumberID":"386","UserID":"Aditya Bhave"}]}}]}}}';
        httpRes.setBody(resBodyString);
        system.assert(httpRes.getBody() == resBodyString);
        Map<String, HTTPRequest> mapDetails = continueObj.getRequests();
        string reqType = '';
        for(String s : mapDetails.keyset())
        {
            reqType = s;
        } 
        Test.setContinuationResponse(reqType,httpRes);
        String result = (String)PhoneBook_LC_HUM.processResponsegetAllPhoneBookRules(null);
        System.assertNotEquals(result, null);
        Test.stopTest();
    }
    
    static testMethod void testgetDefaultPH()
    {
        Test.startTest();
        String routexmlrequest = '{"RoutingXML":{"PlatFormCD":"EM","SubscriberID":"004833414","SubscriberDOB":"09171963","SellingLedger":"84238","GroupID":"12345","ASOIndicator":"N","MajorLOB":"PPO","SellingLedgerForMktName":"84238","AkaName":"","ActParent":"","CimId":"","StateOfIssue":"KY","BusinessSegmentIndicator":"U","HdhpIndicator":"N","MtvBusinessLevel5":"HIC","IsSoftPhoneIndicator":"Y","MtvBusinessLevel7":"PPO","ProductTypeCode":"LHMPPO","ApplicationSourceRequest":"Softphone","AdditionalContextInformation":{"KeyValueOfstringstring":[{"Key":"NGroupID","Value":"707273"},{"Key":"Demo","Value":"ProdplanId"}]}}}';
        Continuation continueObj = (Continuation)PhoneBook_LC_HUM.getDefaultPH(routexmlrequest);
        continueObj.continuationMethod = 'processResponseGetDefaultPhonebook';
        continueObj.state = 'State';
        system.assertEquals('processResponseGetDefaultPhonebook', continueObj.continuationMethod); 
        HttpResponse httpRes = new Httpresponse ();
        String resBodyString = '{"RouteSoftphoneXmlResponse":{"CallTransferRec":{"CallTransferRecList":[{"TransferNumber":"1664819","CallerType":"Employer","SiteCd":"GB","CatCd":"PRI","CategoryCd":"Precert Initiate","QueueName":"GBO Broker Medical/Life Claims and Benefits","MarketDesc":"","MarketID":"84238","TransferDataKey":"EM0048334148423812345","Group":"","SiteDesc":"","ShareableTollFree":"0","OperatingHours":"","Notes":"","TransferType":"Warm","TransferConnectNumber":"8554100506"},{"TransferNumber":"1860506","CallerType":"Member","SiteCd":"GD","CatCd":"PRI","CategoryCd":"Precert Initiate","QueueName":"GBO Medical MTV Member","MarketDesc":"","MarketID":"84238","TransferDataKey":"EM0048334148423812345","Group":"","SiteDesc":"","ShareableTollFree":"8664277478","OperatingHours":"","Notes":"","TransferType":"Cold","TransferConnectNumber":"8554726808"},{"TransferNumber":"1790103","CallerType":"Provider","SiteCd":"GB","CatCd":"PRI","CategoryCd":"Precert Initiate","QueueName":"HGSL_LV_Auth","MarketDesc":"","MarketID":"84238","TransferDataKey":"EM0048334148423812345","Group":"","SiteDesc":"","ShareableTollFree":"0","OperatingHours":"8am - 6pm EST","Notes":"","TransferType":"Warm","TransferConnectNumber":"8777997901"}]}}}';
        httpRes.setBody(resBodyString);
        system.assert(httpRes.getBody() == resBodyString);
        Map<String, HTTPRequest> mapDetails = continueObj.getRequests();
        string reqType = '';
        for(String s : mapDetails.keyset())
        {
            reqType = s;
        } 
        Test.setContinuationResponse(reqType,httpRes);
        String result = (String)PhoneBook_LC_HUM.processResponseGetDefaultPhonebook(reqType);
        System.assertNotEquals(result, null);
        Test.stopTest();
    }
    
    static testMethod void testNegativegetDefaultPH()
    {
        Test.startTest();
        String routexmlrequest = '{"RoutingXML":{"PlatFormCD":"EM","SubscriberID":"004833414","SubscriberDOB":"09171963","SellingLedger":"84238","GroupID":"12345","ASOIndicator":"N","MajorLOB":"PPO","SellingLedgerForMktName":"84238","AkaName":"","ActParent":"","CimId":"","StateOfIssue":"KY","BusinessSegmentIndicator":"U","HdhpIndicator":"N","MtvBusinessLevel5":"HIC","IsSoftPhoneIndicator":"Y","MtvBusinessLevel7":"PPO","ProductTypeCode":"LHMPPO","ApplicationSourceRequest":"Softphone","AdditionalContextInformation":{"KeyValueOfstringstring":[{"Key":"NGroupID","Value":"707273"},{"Key":"Demo","Value":"ProdplanId"}]}}}';
        Continuation continueObj = (Continuation)PhoneBook_LC_HUM.getDefaultPH(routexmlrequest);
        continueObj.continuationMethod = 'processResponseGetDefaultPhonebook';
        continueObj.state = 'State';
        system.assertEquals('processResponseGetDefaultPhonebook', continueObj.continuationMethod); 
        HttpResponse httpRes = new Httpresponse ();
        String resBodyString = '{"Fault":{"faultcode":"\n Code-216.4.1","faultstring":"\n error_service_name :\"IVRService |\n error_service_method : RouteSoftphoneXml |\n fault_message : ","detail":{"transactionInfo":{"transaction-id":"491764292","transaction-client":"10.114.96.251","URL-in":"http://127.0.0.1:8316/IVRService/RouteSoftphoneXml","processor-name":"JSONServicesGateway","processor-type":"Multiprotocol Gateway"},"errorInfo":{"error-code":"0x00d30003","error-headers":"HTTP/1.1 500 Internal Server Error\r\nContent-Type: text/xml; charset=UTF-8\r\nX-Backside-Transport: FAIL FAIL\r\nConnection: close\r\n\r\n","error-message":"s:Client-The creator of this fault did not specify a Reason.","error-protocol-reason-phrase":"","error-protocol-response":"0","error-subcode":"0x00d30003"}}}}';
        httpRes.setBody(resBodyString);
        system.assert(httpRes.getBody() == resBodyString);
        Map<String, HTTPRequest> mapDetails = continueObj.getRequests();
        string reqType = '';
        for(String s : mapDetails.keyset())
        {
            reqType = s;
        } 
        Test.setContinuationResponse(reqType,httpRes);
        String result = (String)PhoneBook_LC_HUM.processResponseGetDefaultPhonebook(reqType);
        System.assertNotEquals(result, null);
        Test.stopTest();
    }
    
    static testMethod void testCallTransfer()
    {   
        Test.startTest();
        List<Account> testAcct = [select id,name from Account where name='TestGroup00'];
        List<MemberPlan> memplan = [select id,name from MemberPlan where name='H123458800'];
        String callTransferReq = '{"accId":"'+testAcct[0].Id+'","MemberPlanId":"'+memplan[0].Id+'","interactionId":"","uuid":""}';
        Continuation continueObj = (Continuation)PhoneBook_LC_HUM.callTransfer(JSON.deserializeUntyped(callTransferReq));
        continueObj.continuationMethod = 'processCallTransferResponse';
        continueObj.state = 'State';
        system.assertEquals('processCallTransferResponse', continueObj.continuationMethod); 
        HttpResponse httpResRouteXML = new Httpresponse ();
        String resRouteXMLBody = '{"RouteSoftphoneXmlResponse":{"CallTransferRec":{"CallTransferRecList":[{"TransferNumber":"1664819","CallerType":"Employer","SiteCd":"GB","CatCd":"PRI","CategoryCd":"Precert Initiate","QueueName":"GBO Broker Medical/Life Claims and Benefits","MarketDesc":"","MarketID":"84238","TransferDataKey":"EM0048334148423812345","Group":"","SiteDesc":"","ShareableTollFree":"0","OperatingHours":"","Notes":"","TransferType":"Warm","TransferConnectNumber":"8554100506"},{"TransferNumber":"1860506","CallerType":"Member","SiteCd":"GD","CatCd":"PRI","CategoryCd":"Precert Initiate","QueueName":"GBO Medical MTV Member","MarketDesc":"","MarketID":"84238","TransferDataKey":"EM0048334148423812345","Group":"","SiteDesc":"","ShareableTollFree":"8664277478","OperatingHours":"","Notes":"","TransferType":"Cold","TransferConnectNumber":"8554726808"},{"TransferNumber":"1790103","CallerType":"Provider","SiteCd":"GB","CatCd":"PRI","CategoryCd":"Precert Initiate","QueueName":"HGSL_LV_Auth","MarketDesc":"","MarketID":"84238","TransferDataKey":"EM0048334148423812345","Group":"","SiteDesc":"","ShareableTollFree":"0","OperatingHours":"8am - 6pm EST","Notes":"","TransferType":"Warm","TransferConnectNumber":"8777997901"}]}}}';
        httpResRouteXML.setBody(resRouteXMLBody);
        system.assert(httpResRouteXML.getBody() == resRouteXMLBody);
        Map<String, HTTPRequest> mapDetails = continueObj.getRequests();
        string reqType = '';
        for(String s : mapDetails.keyset())
        {
            reqType = s;
        }
        Test.setContinuationResponse(reqType,httpResRouteXML);
        String result = (String)PhoneBook_LC_HUM.processCallTransferResponse(reqType);
        System.assertNotEquals(result, null);
        Test.stopTest();
    }
    
    static testMethod void testNegativeCallTransfer()
    {   
        Test.startTest();
        List<Account> testAcct = [select id,name from Account where name='TestGroup00'];
        List<MemberPlan> memplan = [select id,name from MemberPlan where name='H123458800'];
        String callTransferReq = '{"accId":"'+testAcct[0].Id+'","MemberPlanId":"'+memplan[0].Id+'","interactionId":"","uuid":""}';
        Continuation continueObj = (Continuation)PhoneBook_LC_HUM.callTransfer(JSON.deserializeUntyped(callTransferReq));
        continueObj.continuationMethod = 'processCallTransferResponse';
        continueObj.state = 'State';
        system.assertEquals('processCallTransferResponse', continueObj.continuationMethod); 
        HttpResponse httpResRouteXML = new Httpresponse ();
        String resRouteXMLBody = '{"RouteSoftphoneXmlResponse":{"CallTransferRec":{"CallTransferRecList":[{"TransferNumber":"1664819","CallerType":"Employer","SiteCd":"GB","CatCd":"PRI","CategoryCd":"Precert Initiate","QueueName":"GBO Broker Medical/Life Claims and Benefits","MarketDesc":"","MarketID":"84238","TransferDataKey":"EM0048334148423812345","Group":"","SiteDesc":"","ShareableTollFree":"0","OperatingHours":"","Notes":"","TransferType":"Warm","TransferConnectNumber":"8554100506"},{"TransferNumber":"1860506","CallerType":"Member","SiteCd":"GD","CatCd":"PRI","CategoryCd":"Precert Initiate","QueueName":"GBO Medical MTV Member","MarketDesc":"","MarketID":"84238","TransferDataKey":"EM0048334148423812345","Group":"","SiteDesc":"","ShareableTollFree":"8664277478","OperatingHours":"","Notes":"","TransferType":"Cold","TransferConnectNumber":"8554726808"},{"TransferNumber":"1790103","CallerType":"Provider","SiteCd":"GB","CatCd":"PRI","CategoryCd":"Precert Initiate","QueueName":"HGSL_LV_Auth","MarketDesc":"","MarketID":"84238","TransferDataKey":"EM0048334148423812345","Group":"","SiteDesc":"","ShareableTollFree":"0","OperatingHours":"8am - 6pm EST","Notes":"","TransferType":"Warm","TransferConnectNumber":"8777997901"}]}}}';
        httpResRouteXML.setBody(resRouteXMLBody);
        system.assert(httpResRouteXML.getBody() == resRouteXMLBody);
        Map<String, HTTPRequest> mapDetails = continueObj.getRequests();
        string reqType = '';
        for(String s : mapDetails.keyset())
        {
            reqType = s;
        }
        Test.setContinuationResponse(reqType,httpResRouteXML);
        String result = (String)PhoneBook_LC_HUM.processCallTransferResponse(null);
        System.assertNotEquals(result, null);
        Test.stopTest();
    }
    public static testmethod void testCreateRecord() 
    {
        CRMFunctionality_ONOFF_Switch__c objSwitch = CRMFunctionality_ONOFF_Switch__c.getValues('1799704_Genesys_Level3');    
        System.assert(objSwitch != null);
        PhoneBook_LC_HUM.createRecord('ACW Required','Inbound','','b1e64cbf-9298-46c5-85e4-3f8d81f7b3f3','status','','','');       
        objSwitch.IsON__c = false;
        update objSwitch;
        System.assert(objSwitch.IsON__c == false);
        CRMFunctionality_ONOFF_Switch__c objSwtch = CRMFunctionality_ONOFF_Switch__c.getValues('1799704_Genesys_Level2');  
        System.assert(objSwitch != null);
        PhoneBook_LC_HUM.createRecord('ACW Required','Inbound','','b1e64cbf-9298-46c5-85e4-3f8d81f7b3f3','interaction','','','');
        objSwtch.IsON__c = false;
        update objSwtch;
        System.assert(objSwtch.IsON__c == false);
        CRMFunctionality_ONOFF_Switch__c objStch = CRMFunctionality_ONOFF_Switch__c.getValues('1799704_Genesys_Level1');  
        System.assert(objStch != null);
        PhoneBook_LC_HUM.createRecord('ACW Required','Inbound','','b1e64cbf-9298-46c5-85e4-3f8d81f7b3f3','status','','','');
        List<SoftPhone_Call_Log__c> lstLogs = new List<SoftPhone_Call_Log__c>();
        lstLogs = [Select id from SoftPhone_Call_Log__c];
        System.assert(lstLogs.size() == 3);
    }
    static testMethod void testinsertIntegrationmapping()
    {   
        Test.startTest();
        List<Account> testAcct = [select id,name from Account where name='TestGroup00'];
        List<MemberPlan> memplan = [select id,name from MemberPlan where name='H123458800'];
        String callTransferReq = '{"accId":"'+testAcct[0].Id+'","MemberPlanId":"'+memplan[0].Id+'","interactionId":"","uuid":""}';
        PhoneBook_LC_HUM.insertIntegrationmapping(JSON.deserializeUntyped(callTransferReq));
        System.assertNotEquals(callTransferReq, '');
        Test.stopTest();
    }
    static testMethod void testgetAboutAccountId()
    {   
        String searchModal = '{"INQA_FIRST" : "Member","INQA_LAST":"TestAcc","INQA_ID":"H987654321","INQA_DOB": "","INQA_TYP":"M","INQR_FIRST" : "Member","INQR_LAST":"TestAcc","INQR_ID":"H987654321","INQR_DOB": "","INQR_TYP":"M","AUTHENTICATION_IND": "Y"}';
        CRMFunctionality_ONOFF_Switch__c objCS = new CRMFunctionality_ONOFF_Switch__c(Name = '4461937', IsON__c = true);
        insert objCS;
        Test.startTest();
        Map<String,String> mapAboutAccId = PhoneBook_LC_HUM.getAboutAccountId('Test UUID',false,'N','abcd-abcd-abcd-abcd',JSON.deserializeUntyped(searchModal),'');
        System.assertNotEquals(mapAboutAccId .get('aboutId'), '');
        Test.stopTest();
    }
    static testMethod void testdisconnectSaveCallData()
    {   
        Test.startTest();
        String disconnectattributes = '{"AGENT_CALL_START_TIME":"06:10:00","isConsultBlindClicked":true,"isTransferredCall":false,"AGENT_CALL_END_TIME":"06:20:00","UUID":"Test123","interactionId":"12345-12345-12345-12345","Outbound":false}';
        Continuation continueObj = (Continuation)PhoneBook_LC_HUM.disconnectSaveCallData(JSON.deserializeUntyped(disconnectattributes));
        continueObj.continuationMethod = 'respdisconnectSaveCallData';
        continueObj.state = 'State';
        system.assertEquals('respdisconnectSaveCallData', continueObj.continuationMethod); 
        HttpResponse httpResDisconnect = new Httpresponse ();
        String resDisconnectBody = '{"SaveCallDataResponse":{"SaveCallDataResult":"true"}}';
        httpResDisconnect.setBody(resDisconnectBody);
        system.assert(httpResDisconnect.getBody() == resDisconnectBody);
        Map<String, HTTPRequest> mapDetails = continueObj.getRequests();
        string reqType = '';
        for(String s : mapDetails.keyset())
        {
            reqType = s;
        }
        Test.setContinuationResponse(reqType,httpResDisconnect);
        String result = (String)PhoneBook_LC_HUM.respdisconnectSaveCallData(reqType);
        System.assertNotEquals(result, null);
        Test.stopTest();
    }
    static testMethod void testNegativedisconnectSaveCallData()
    {   
        Test.startTest();
        String disconnectattributes = '{"AGENT_CALL_START_TIME":"06:10:00","isConsultBlindClicked":true,"isTransferredCall":false,"AGENT_CALL_END_TIME":"06:20:00","UUID":"Test123","interactionId":"12345-12345-12345-12345","Outbound":false}';
        Continuation continueObj = (Continuation)PhoneBook_LC_HUM.disconnectSaveCallData(JSON.deserializeUntyped(disconnectattributes));
        continueObj.continuationMethod = 'respdisconnectSaveCallData';
        continueObj.state = 'State';
        system.assertEquals('respdisconnectSaveCallData', continueObj.continuationMethod); 
        HttpResponse httpResDisconnect = new Httpresponse ();
        String resDisconnectBody = '{"SaveCallDataResponse":{"SaveCallDataResult":"true"}}';
        httpResDisconnect.setBody(resDisconnectBody);
        system.assert(httpResDisconnect.getBody() == resDisconnectBody);
        Map<String, HTTPRequest> mapDetails = continueObj.getRequests();
        string reqType = '';
        for(String s : mapDetails.keyset())
        {
            reqType = s;
        }
        Test.setContinuationResponse(reqType,httpResDisconnect);
        String result = (String)PhoneBook_LC_HUM.respdisconnectSaveCallData(null);
        System.assertNotEquals(result, null);
        Test.stopTest();
    }
    static testMethod void testconsultBlindSaveCallData_Member()
    {   
        
        Test.startTest();
        String transferattributes = '{"ISMANUAL_TRANSFER":"true","TRANSFER_STIM":"06:10:00","TRANSFER_NUMBER":"1234567","TRANSFER_TYPE":"WARM","UUID":"Test UUID","isCallTransferClicked":true}';
        Continuation continueObj = (Continuation)PhoneBook_LC_HUM.consultBlindSaveCallData(JSON.deserializeUntyped(transferattributes));
        continueObj.continuationMethod = 'respConsultBlindSaveCallData';
        continueObj.state = 'State';
        system.assertEquals('respConsultBlindSaveCallData', continueObj.continuationMethod); 
        HttpResponse httpRestransfersavecall = new Httpresponse ();
        String restransfersavecallBody = '{"SaveCallDataResponse":{"SaveCallDataResult":"true"}}';
        httpRestransfersavecall.setBody(restransfersavecallBody);
        system.assert(httpRestransfersavecall.getBody() == restransfersavecallBody);
        Map<String, HTTPRequest> mapDetails = continueObj.getRequests();
        string reqType = '';
        for(String s : mapDetails.keyset())
        {
            reqType = s;
        }
        Test.setContinuationResponse(reqType,httpRestransfersavecall);
        String result = (String)PhoneBook_LC_HUM.respConsultBlindSaveCallData(reqType);
        System.assertNotEquals(result, null);
        Test.stopTest();
    }
    static testMethod void testconsultBlindSaveCallData_UnknownMember()
    {   
        Test.startTest();
        String transferattributes = '{"ISMANUAL_TRANSFER":"true","TRANSFER_STIM":"06:10:00","TRANSFER_NUMBER":"1234567","TRANSFER_TYPE":"WARM","UUID":"Test UUID Unknown Member","isCallTransferClicked":true}';
        Continuation continueObj = (Continuation)PhoneBook_LC_HUM.consultBlindSaveCallData(JSON.deserializeUntyped(transferattributes));
        continueObj.continuationMethod = 'respConsultBlindSaveCallData';
        continueObj.state = 'State';
        system.assertEquals('respConsultBlindSaveCallData', continueObj.continuationMethod); 
        HttpResponse httpRestransfersavecall = new Httpresponse ();
        String restransfersavecallBody = '{"SaveCallDataResponse":{"SaveCallDataResult":"true"}}';
        httpRestransfersavecall.setBody(restransfersavecallBody);
        system.assert(httpRestransfersavecall.getBody() == restransfersavecallBody);
        Map<String, HTTPRequest> mapDetails = continueObj.getRequests();
        string reqType = '';
        for(String s : mapDetails.keyset())
        {
            reqType = s;
        }
        Test.setContinuationResponse(reqType,httpRestransfersavecall);
        String result = (String)PhoneBook_LC_HUM.respConsultBlindSaveCallData(reqType);
        System.assertNotEquals(result, null);
        Test.stopTest();
    }
    static testMethod void testNegativeconsultBlindSaveCallData_Member()
    {   
        Test.startTest();
        String transferattributes = '{"ISMANUAL_TRANSFER":"true","TRANSFER_STIM":"06:10:00","TRANSFER_NUMBER":"1234567","TRANSFER_TYPE":"WARM","UUID":"Test UUID","isCallTransferClicked":true}';
        Continuation continueObj = (Continuation)PhoneBook_LC_HUM.consultBlindSaveCallData(JSON.deserializeUntyped(transferattributes));
        continueObj.continuationMethod = 'respConsultBlindSaveCallData';
        continueObj.state = 'State';
        system.assertEquals('respConsultBlindSaveCallData', continueObj.continuationMethod); 
        HttpResponse httpRestransfersavecall = new Httpresponse ();
        String restransfersavecallBody = '{"SaveCallDataResponse":{"SaveCallDataResult":"true"}}';
        httpRestransfersavecall.setBody(restransfersavecallBody);
        system.assert(httpRestransfersavecall.getBody() == restransfersavecallBody);
        Map<String, HTTPRequest> mapDetails = continueObj.getRequests();
        string reqType = '';
        for(String s : mapDetails.keyset())
        {
            reqType = s;
        }
        Test.setContinuationResponse(reqType,httpRestransfersavecall);
        String result = (String)PhoneBook_LC_HUM.respConsultBlindSaveCallData(null);
        System.assertNotEquals(result, null);
        Test.stopTest();
    }
    static testMethod void testconsultBlindSaveCallData_Group()
    {           
        Test.startTest();
        String transferattributes = '{"ISMANUAL_TRANSFER":"true","TRANSFER_STIM":"06:10:00","TRANSFER_NUMBER":"1234567","TRANSFER_TYPE":"WARM","UUID":"Test UUID Group","isCallTransferClicked":true}';
        Continuation continueObj = (Continuation)PhoneBook_LC_HUM.consultBlindSaveCallData(JSON.deserializeUntyped(transferattributes));
        continueObj.continuationMethod = 'respConsultBlindSaveCallData';
        continueObj.state = 'State';
        system.assertEquals('respConsultBlindSaveCallData', continueObj.continuationMethod); 
        HttpResponse httpRestransfersavecall = new Httpresponse ();
        String restransfersavecallBody = '{"SaveCallDataResponse":{"SaveCallDataResult":"true"}}';
        httpRestransfersavecall.setBody(restransfersavecallBody);
        system.assert(httpRestransfersavecall.getBody() == restransfersavecallBody);
        Map<String, HTTPRequest> mapDetails = continueObj.getRequests();
        string reqType = '';
        for(String s : mapDetails.keyset())
        {
            reqType = s;
        }
        Test.setContinuationResponse(reqType,httpRestransfersavecall);
        String result = (String)PhoneBook_LC_HUM.respConsultBlindSaveCallData(reqType);
        System.assertNotEquals(result, null);
        Test.stopTest();
    }
    static testMethod void testconsultBlindSaveCallData_UnknownGroup()
    {   
        Test.startTest();
        String transferattributes = '{"ISMANUAL_TRANSFER":"true","TRANSFER_STIM":"06:10:00","TRANSFER_NUMBER":"1234567","TRANSFER_TYPE":"WARM","UUID":"Test UUID Unknown Group","isCallTransferClicked":true}';
        Continuation continueObj = (Continuation)PhoneBook_LC_HUM.consultBlindSaveCallData(JSON.deserializeUntyped(transferattributes));
        continueObj.continuationMethod = 'respConsultBlindSaveCallData';
        continueObj.state = 'State';
        system.assertEquals('respConsultBlindSaveCallData', continueObj.continuationMethod); 
        HttpResponse httpRestransfersavecall = new Httpresponse ();
        String restransfersavecallBody = '{"SaveCallDataResponse":{"SaveCallDataResult":"true"}}';
        httpRestransfersavecall.setBody(restransfersavecallBody);
        system.assert(httpRestransfersavecall.getBody() == restransfersavecallBody);
        Map<String, HTTPRequest> mapDetails = continueObj.getRequests();
        string reqType = '';
        for(String s : mapDetails.keyset())
        {
            reqType = s;
        }
        Test.setContinuationResponse(reqType,httpRestransfersavecall);
        String result = (String)PhoneBook_LC_HUM.respConsultBlindSaveCallData(reqType);
        System.assertNotEquals(result, null);
        Test.stopTest();
    }
    static testMethod void testconsultBlindSaveCallData_Provider()
    {   
        Test.startTest();
        String transferattributes = '{"ISMANUAL_TRANSFER":"true","TRANSFER_STIM":"06:10:00","TRANSFER_NUMBER":"1234567","TRANSFER_TYPE":"WARM","UUID":"Test UUID Provider","isCallTransferClicked":true}';
        Continuation continueObj = (Continuation)PhoneBook_LC_HUM.consultBlindSaveCallData(JSON.deserializeUntyped(transferattributes));
        continueObj.continuationMethod = 'respConsultBlindSaveCallData';
        continueObj.state = 'State';
        system.assertEquals('respConsultBlindSaveCallData', continueObj.continuationMethod); 
        HttpResponse httpRestransfersavecall = new Httpresponse ();
        String restransfersavecallBody = '{"SaveCallDataResponse":{"SaveCallDataResult":"true"}}';
        httpRestransfersavecall.setBody(restransfersavecallBody);
        system.assert(httpRestransfersavecall.getBody() == restransfersavecallBody);
        Map<String, HTTPRequest> mapDetails = continueObj.getRequests();
        string reqType = '';
        for(String s : mapDetails.keyset())
        {
            reqType = s;
        }
        Test.setContinuationResponse(reqType,httpRestransfersavecall);
        String result = (String)PhoneBook_LC_HUM.respConsultBlindSaveCallData(reqType);
        System.assertNotEquals(result, null);
        Test.stopTest();
    }
    static testMethod void testconsultBlindSaveCallData_UnknownProvider()
    {   
        Test.startTest();
        String transferattributes = '{"ISMANUAL_TRANSFER":"true","TRANSFER_STIM":"06:10:00","TRANSFER_NUMBER":"1234567","TRANSFER_TYPE":"WARM","UUID":"Test UUID Unknown Provider","isCallTransferClicked":true}';
        Continuation continueObj = (Continuation)PhoneBook_LC_HUM.consultBlindSaveCallData(JSON.deserializeUntyped(transferattributes));
        continueObj.continuationMethod = 'respConsultBlindSaveCallData';
        continueObj.state = 'State';
        system.assertEquals('respConsultBlindSaveCallData', continueObj.continuationMethod); 
        HttpResponse httpRestransfersavecall = new Httpresponse ();
        String restransfersavecallBody = '{"SaveCallDataResponse":{"SaveCallDataResult":"true"}}';
        httpRestransfersavecall.setBody(restransfersavecallBody);
        system.assert(httpRestransfersavecall.getBody() == restransfersavecallBody);
        Map<String, HTTPRequest> mapDetails = continueObj.getRequests();
        string reqType = '';
        for(String s : mapDetails.keyset())
        {
            reqType = s;
        }
        Test.setContinuationResponse(reqType,httpRestransfersavecall);
        String result = (String)PhoneBook_LC_HUM.respConsultBlindSaveCallData(reqType);
        System.assertNotEquals(result, null);
        Test.stopTest();
    }
    static testMethod void testconsultBlindSaveCallData_Agent()
    {   
        Test.startTest();
        String transferattributes = '{"ISMANUAL_TRANSFER":"true","TRANSFER_STIM":"06:10:00","TRANSFER_NUMBER":"1234567","TRANSFER_TYPE":"WARM","UUID":"Test UUID Agent","isCallTransferClicked":true}';
        Continuation continueObj = (Continuation)PhoneBook_LC_HUM.consultBlindSaveCallData(JSON.deserializeUntyped(transferattributes));
        continueObj.continuationMethod = 'respConsultBlindSaveCallData';
        continueObj.state = 'State';
        system.assertEquals('respConsultBlindSaveCallData', continueObj.continuationMethod); 
        HttpResponse httpRestransfersavecall = new Httpresponse ();
        String restransfersavecallBody = '{"SaveCallDataResponse":{"SaveCallDataResult":"true"}}';
        httpRestransfersavecall.setBody(restransfersavecallBody);
        system.assert(httpRestransfersavecall.getBody() == restransfersavecallBody);
        Map<String, HTTPRequest> mapDetails = continueObj.getRequests();
        string reqType = '';
        for(String s : mapDetails.keyset())
        {
            reqType = s;
        }
        Test.setContinuationResponse(reqType,httpRestransfersavecall);
        String result = (String)PhoneBook_LC_HUM.respConsultBlindSaveCallData(reqType);
        System.assertNotEquals(result, null);
        Test.stopTest();
    }
    static testMethod void testconsultBlindSaveCallData_UnknownAgent()
    {   
        Test.startTest();
        String transferattributes = '{"ISMANUAL_TRANSFER":"true","TRANSFER_STIM":"06:10:00","TRANSFER_NUMBER":"1234567","TRANSFER_TYPE":"WARM","UUID":"Test UUID Unknown Agent","isCallTransferClicked":true}';
        Continuation continueObj = (Continuation)PhoneBook_LC_HUM.consultBlindSaveCallData(JSON.deserializeUntyped(transferattributes));
        continueObj.continuationMethod = 'respConsultBlindSaveCallData';
        continueObj.state = 'State';
        system.assertEquals('respConsultBlindSaveCallData', continueObj.continuationMethod); 
        HttpResponse httpRestransfersavecall = new Httpresponse ();
        String restransfersavecallBody = '{"SaveCallDataResponse":{"SaveCallDataResult":"true"}}';
        httpRestransfersavecall.setBody(restransfersavecallBody);
        system.assert(httpRestransfersavecall.getBody() == restransfersavecallBody);
        Map<String, HTTPRequest> mapDetails = continueObj.getRequests();
        string reqType = '';
        for(String s : mapDetails.keyset())
        {
            reqType = s;
        }
        Test.setContinuationResponse(reqType,httpRestransfersavecall);
        String result = (String)PhoneBook_LC_HUM.respConsultBlindSaveCallData(reqType);
        System.assertNotEquals(result, null);
        Test.stopTest();
    }
    static testMethod void testfetchSwitchs()
    {   
        Test.startTest();
        Map<String,Boolean> switches = PhoneBook_LC_HUM.fetchSwitchs();
        system.assertEquals(switches.size(),8);
        Test.stopTest();
    }
    static testMethod void testgetSecurePaymentsDetails()
    {
        Test.startTest();
        Map<String,String> securePayTypes = PhoneBook_LC_HUM.getSecurePaymentsDetails();
        system.assertEquals(securePayTypes.size(),2);
        Test.stopTest();
    }
    static testMethod void testgetMemberPlanRecord()
    {
        List<MemberPlan> memplan = [select id,name from MemberPlan where name='H123458800'];
        String memberPlanId = (String)memplan[0].Id;
        Test.startTest();
        MemberPlan mobj = PhoneBook_LC_HUM.getMemberPlanRecord(memberPlanId);
        system.assert(mobj.id<>null);
        Test.stopTest();
    }
    static testMethod void testautoInteractioncreationMember()
    {
        Test.startTest();
        CRMFunctionality_ONOFF_Switch__c objSwitch = CRMFunctionality_ONOFF_Switch__c.getValues('4461937');    
        System.assert(objSwitch != null);
        CRMFunctionality_ONOFF_Switch__c objSwitch1 = CRMFunctionality_ONOFF_Switch__c.getValues('4734084');    
        System.assert(objSwitch1 != null);
        CRMFunctionality_ONOFF_Switch__c objSwitch2 = CRMFunctionality_ONOFF_Switch__c.getValues('4863232');    
        System.assert(objSwitch2 != null);
        Id [] fixedSearchResults= new Id[1];
        Account acc2 = [select id,name from Account where LastName='TestAcc'];
        fixedSearchResults[0] = acc2.id;
        Test.setFixedSearchResults(fixedSearchResults);
        String searchModal = '{"INQA_FIRST" : "Member","INQA_LAST":"TestAcc","INQA_ID":"H987654321","INQA_DOB": "","INQA_TYP":"M","INQR_FIRST" : "Member","INQR_LAST":"TestAcc","INQR_ID":"H987654321","INQR_DOB": "","INQR_TYP":"M","AUTHENTICATION_IND": "Y"}';
        String interactionId = PhoneBook_LC_HUM.createAutoInteraction(JSON.deserializeUntyped(searchModal),'Test UUID',false,'123456asdfghj1234','');
        String intId = PhoneBook_LC_HUM.createAutoInteraction(JSON.deserializeUntyped(searchModal),'xyzzUUID',false,'123456asdfghj1234','');
        system.assert(intId<>null);
        system.assert(interactionId<>null);

        String searchModal1 = '{"INQA_FIRST" : "Member","INQA_LAST":"TestAcc","INQA_ID":"","INQA_DOB": "","INQA_TYP":"M","INQR_FIRST" : "Member","INQR_LAST":"TestAcc","INQR_ID":"","INQR_DOB": "","INQR_TYP":"M","AUTHENTICATION_IND": "Y"}';
        String intId1 = PhoneBook_LC_HUM.createAutoInteraction(JSON.deserializeUntyped(searchModal1),'xyzzUUID1',false,'123456asdfghj1234','');
        system.assert(intId1<>null);

        String searchModal2 = '{"INQA_FIRST" : "Member","INQA_LAST":"TestAcc","INQA_ID":"","INQA_DOB": "","INQA_TYP":"M","INQR_FIRST" : "","INQR_LAST":"","INQR_ID":"","INQR_DOB": "","INQR_TYP":"M","AUTHENTICATION_IND": "Y"}';
        String intId2 = PhoneBook_LC_HUM.createAutoInteraction(JSON.deserializeUntyped(searchModal2),'xyzzUUID2',false,'123456asdfghj1234','');
        system.assert(intId2<>null);

        String searchModal3 = '{"INQA_FIRST" : "Member","INQA_LAST":"TestCaller","INQA_ID":"","INQA_DOB": "","INQA_TYP":"M","INQR_FIRST" : "","INQR_LAST":"","INQR_ID":"","INQR_DOB": "","INQR_TYP":"M","AUTHENTICATION_IND": "Y"}';
        String intId3 = PhoneBook_LC_HUM.createAutoInteraction(JSON.deserializeUntyped(searchModal3),'xyzzUUID3',false,'123456asdfghj1234','');
        system.assert(intId3<>null);
        Test.stopTest();
    }

    static testMethod void testautoInteractioncreationProvider()
    {
        Test.startTest();
        CRMFunctionality_ONOFF_Switch__c objSwitch = CRMFunctionality_ONOFF_Switch__c.getValues('4461937');    
        System.assert(objSwitch != null);
        CRMFunctionality_ONOFF_Switch__c objSwitch1 = CRMFunctionality_ONOFF_Switch__c.getValues('4734084');    
        System.assert(objSwitch1 != null);
         CRMFunctionality_ONOFF_Switch__c objSwitch2 = CRMFunctionality_ONOFF_Switch__c.getValues('4863232');    
        System.assert(objSwitch2 != null);
        Id [] fixedSearchResults= new Id[1];
        Account objProviderAccount = [select id,name from Account where Name='TestProvider'];
        fixedSearchResults[0] = objProviderAccount.id;
        Test.setFixedSearchResults(fixedSearchResults);
        String searchModal3 = '{"INQA_FIRST" : "","INQA_LAST":"TestProvider","INQA_ID":"12345","INQA_DOB": "","INQA_TYP":"P","INQR_FIRST" : "","INQR_LAST":"TestProvider","INQR_ID":"12345","INQR_DOB": "","INQR_TYP":"P","AUTHENTICATION_IND": "Y"}';
        String intId3 = PhoneBook_LC_HUM.createAutoInteraction(JSON.deserializeUntyped(searchModal3),'xyzzUUID3',false,'123456asdfghj1234','');
        system.assert(intId3<>null);

        String searchModal4 = '{"INQA_FIRST" : "","INQA_LAST":"TestProvider","INQA_ID":"","INQA_DOB": "","INQA_TYP":"P","INQR_FIRST" : "","INQR_LAST":"TestProvider","INQR_ID":"","INQR_DOB": "","INQR_TYP":"P","AUTHENTICATION_IND": "Y"}';
        String intId4 = PhoneBook_LC_HUM.createAutoInteraction(JSON.deserializeUntyped(searchModal4),'xyzzUUID4',false,'123456asdfghj1234','');
        system.assert(intId4<>null);

        String searchModal5 = '{"INQA_FIRST" : "","INQA_LAST":"TestProvider","INQA_ID":"","INQA_DOB": "","INQA_TYP":"P","INQR_FIRST" : "","INQR_LAST":"","INQR_ID":"","INQR_DOB": "","INQR_TYP":"P","AUTHENTICATION_IND": "Y"}';
        String intId5 = PhoneBook_LC_HUM.createAutoInteraction(JSON.deserializeUntyped(searchModal5),'xyzzUUID5',false,'123456asdfghj1234','');
        system.assert(intId5<>null);
        Test.stopTest();
    }

    static testMethod void testautoInteractioncreationAgent()
    {
        Test.startTest();
        CRMFunctionality_ONOFF_Switch__c objSwitch = CRMFunctionality_ONOFF_Switch__c.getValues('4461937');    
        System.assert(objSwitch != null);
        CRMFunctionality_ONOFF_Switch__c objSwitch1 = CRMFunctionality_ONOFF_Switch__c.getValues('4734084');    
        System.assert(objSwitch1 != null);
         CRMFunctionality_ONOFF_Switch__c objSwitch2 = CRMFunctionality_ONOFF_Switch__c.getValues('4863232');    
        System.assert(objSwitch2 != null);
        Id [] fixedSearchResults= new Id[1];
        Account objAgentAccount = [select id,name from Account where Name='TestAgent'];
        fixedSearchResults[0] = objAgentAccount.id;
        Test.setFixedSearchResults(fixedSearchResults);
        String searchModal6 = '{"INQA_FIRST" : "","INQA_LAST":"TestAgent","INQA_ID":"123agent","INQA_DOB": "","INQA_TYP":"A","INQR_FIRST" : "","INQR_LAST":"TestAgent","INQR_ID":"123agent","INQR_DOB": "","INQR_TYP":"A","AUTHENTICATION_IND": "Y"}';
        String intId6 = PhoneBook_LC_HUM.createAutoInteraction(JSON.deserializeUntyped(searchModal6),'xyzzUUID6',false,'123456asdfghj1234','');
        system.assert(intId6<>null);

        String searchModal7 = '{"INQA_FIRST" : "","INQA_LAST":"TestAgent","INQA_ID":"","INQA_DOB": "","INQA_TYP":"A","INQR_FIRST" : "","INQR_LAST":"TestAgent","INQR_ID":"","INQR_DOB": "","INQR_TYP":"A","AUTHENTICATION_IND": "Y"}';
        String intId7 = PhoneBook_LC_HUM.createAutoInteraction(JSON.deserializeUntyped(searchModal7),'xyzzUUID7',false,'123456asdfghj1234','');
        system.assert(intId7<>null);

        String searchModal8 = '{"INQA_FIRST" : "","INQA_LAST":"TestAgent","INQA_ID":"","INQA_DOB": "","INQA_TYP":"A","INQR_FIRST" : "","INQR_LAST":"","INQR_ID":"","INQR_DOB": "","INQR_TYP":"A","AUTHENTICATION_IND": "Y"}';
        String intId8 = PhoneBook_LC_HUM.createAutoInteraction(JSON.deserializeUntyped(searchModal8),'xyzzUUID8',false,'123456asdfghj1234','');
        system.assert(intId8<>null);
        Test.stopTest();
    }
    static testMethod void positivetestautoInteractioncreationAgent(){
        Test.startTest();
        CRMFunctionality_ONOFF_Switch__c objSwitch = CRMFunctionality_ONOFF_Switch__c.getValues('4461937');    
        System.assert(objSwitch != null);
        CRMFunctionality_ONOFF_Switch__c objSwitch1 = CRMFunctionality_ONOFF_Switch__c.getValues('4734084');    
        System.assert(objSwitch1 != null);
        Id [] fixedSearchResults= new Id[1];
         CRMFunctionality_ONOFF_Switch__c objSwitch2 = CRMFunctionality_ONOFF_Switch__c.getValues('4863232');    
        System.assert(objSwitch2 != null);
        Account objAgentAccount = [select id,name from Account where Name='TestAgent'];
        fixedSearchResults[0] = objAgentAccount.id;
        Test.setFixedSearchResults(fixedSearchResults);
        CRMFunctionality_ONOFF_Switch__c objCS = new CRMFunctionality_ONOFF_Switch__c(Name = '4599393', IsON__c = true);
        insert objCS;
        String searchModal6 = '{"INQA_FIRST" : "","INQA_LAST":"TestAgent","INQA_ID":"123agent","INQA_DOB": "","INQA_TYP":"A","INQR_FIRST" : "","INQR_LAST":"TestAgent","INQR_ID":"123agent","INQR_DOB": "","INQR_TYP":"A","AUTHENTICATION_IND": "Y"}';
        String intId6 = PhoneBook_LC_HUM.createAutoInteraction(JSON.deserializeUntyped(searchModal6),'xyzzUUID6',false,'123456asdfghj1234','');
        system.assert(intId6<>null);
        Test.stopTest();
    }
    static testMethod void testautoInteractioncreationGroup()
    {
        Test.startTest();
        CRMFunctionality_ONOFF_Switch__c objSwitch = CRMFunctionality_ONOFF_Switch__c.getValues('4461937');    
        System.assert(objSwitch != null);
        CRMFunctionality_ONOFF_Switch__c objSwitch1 = CRMFunctionality_ONOFF_Switch__c.getValues('4734084');    
        System.assert(objSwitch1 != null);
         CRMFunctionality_ONOFF_Switch__c objSwitch2 = CRMFunctionality_ONOFF_Switch__c.getValues('4863232');    
        System.assert(objSwitch2 != null);
        Id [] fixedSearchResults= new Id[1];
        Account objGrpAccount = [select id,name from Account where Name='TestGroup00'];
        fixedSearchResults[0] = objGrpAccount.id;
        Test.setFixedSearchResults(fixedSearchResults);
        String searchModal9 = '{"INQA_FIRST" : "","INQA_LAST":"TestGroup00","INQA_ID":"216277","INQA_DOB": "","INQA_TYP":"G","INQR_FIRST" : "","INQR_LAST":"TestGroup00","INQR_ID":"216277","INQR_DOB": "","INQR_TYP":"G","AUTHENTICATION_IND": "Y"}';
        String intId9 = PhoneBook_LC_HUM.createAutoInteraction(JSON.deserializeUntyped(searchModal9),'xyzzUUID9',false,'123456asdfghj1234','');
        system.assert(intId9<>null);

        String searchModal10 = '{"INQA_FIRST" : "","INQA_LAST":"TestGroup00","INQA_ID":"","INQA_DOB": "","INQA_TYP":"G","INQR_FIRST" : "","INQR_LAST":"TestGroup00","INQR_ID":"","INQR_DOB": "","INQR_TYP":"G","AUTHENTICATION_IND": "Y"}';
        String intId10 = PhoneBook_LC_HUM.createAutoInteraction(JSON.deserializeUntyped(searchModal10),'xyzzUUID10',false,'123456asdfghj1234','');
        system.assert(intId10<>null);

        String searchModal11 = '{"INQA_FIRST" : "","INQA_LAST":"TestGroup00","INQA_ID":"","INQA_DOB": "","INQA_TYP":"G","INQR_FIRST" : "","INQR_LAST":"","INQR_ID":"","INQR_DOB": "","INQR_TYP":"G","AUTHENTICATION_IND": "Y"}';
        String intId11 = PhoneBook_LC_HUM.createAutoInteraction(JSON.deserializeUntyped(searchModal11),'xyzzUUID11',false,'123456asdfghj1234','');
        system.assert(intId11<>null);
        Test.stopTest();
    }
    static testMethod void testautoInteractioncreationCallerName()
    {
        Test.startTest();
        CRMFunctionality_ONOFF_Switch__c objSwitch = CRMFunctionality_ONOFF_Switch__c.getValues('4461937');    
        System.assert(objSwitch != null);
        CRMFunctionality_ONOFF_Switch__c objSwitch1 = CRMFunctionality_ONOFF_Switch__c.getValues('4734084');    
        System.assert(objSwitch1 != null);
         CRMFunctionality_ONOFF_Switch__c objSwitch2 = CRMFunctionality_ONOFF_Switch__c.getValues('4863232');    
        System.assert(objSwitch2 != null);
        String searchModal3 = '{"INQA_FIRST" : "","INQA_LAST":"","INQA_ID":"","INQA_DOB": "","INQA_TYP":"M","INQR_FIRST" : "TestCaller","INQR_LAST":"","INQR_ID":"","INQR_DOB": "","INQR_TYP":"M","AUTHENTICATION_IND": "Y"}';
        String intId3 = PhoneBook_LC_HUM.createAutoInteraction(JSON.deserializeUntyped(searchModal3),'xyzzUUID3x',false,'123456asdfghj1234','');
        system.assert(intId3<>null);
        Test.stopTest();
    }
    static testMethod void testnegativeautoInteractioncreation()
    {
        Test.startTest();
         CRMFunctionality_ONOFF_Switch__c objSwitch = CRMFunctionality_ONOFF_Switch__c.getValues('4461937');    
        System.assert(objSwitch != null);
        objSwitch.IsON__c=false;
        update objSwitch;
        String searchModal = '{"INQA_FIRST" : "","INQA_LAST":"","INQA_ID":"","INQA_DOB": "","INQA_TYP":"M","INQR_FIRST" : "","INQR_LAST":"","INQR_ID":"","INQR_DOB": "","INQR_TYP":"","AUTHENTICATION_IND": ""}';
        String intId = PhoneBook_LC_HUM.createAutoInteraction(JSON.deserializeUntyped(searchModal),'UUIDxyzz',false,'123456asdfghj1234','');
        system.assert(intId !='');
        Test.stopTest();
    }
    static testMethod void testgetaccountWithRecordType()
    {
        Map<String,String> mapRecIdObj = new Map<String,String>();
        Account memberAcc = [Select Id from Account where Lastname = 'TestAcc' and FirstName='Member'];
        MemberPlan memberPlan5 = [Select Id from MemberPlan where Name='H98765432101'];
        mapRecIdObj.put(memberAcc.Id,'Account');
        mapRecIdObj.put(memberPlan5.Id,'MemberPlan');
        Test.startTest();
        for(String s : mapRecIdObj.keyset())
        {
            Map<String,String> resultMap = Phonebook_LC_HUM.getaccountWithRecordType(s,mapRecIdObj.get(s));
            System.assert(resultMap.keyset().size()==1);
            System.assert(resultMap.values()[0]=='Member');
        }
        Test.stopTest();
    }
    static testMethod void testcreateInteraction()
    {        
        Account memberAcc = [Select Id,RecordType.Name from Account where Lastname = 'TestAcc' and FirstName='Member']; 
        String uuid = '123456';
        String genesysIntId = '12345abcd';  
        String origin = 'Inbound';      
        Test.startTest();        
        String status = Phonebook_LC_HUM.createInteraction(uuid,memberAcc.Id,genesysIntId,memberAcc.RecordType.Name,origin);
        System.assert(status != '');            
        Test.stopTest();
    }
    static testMethod void testNegativecreateInteraction()
    {
        Account memberAcc = [Select Id,RecordType.Name from Account where Lastname = 'TestAcc' and FirstName='Member']; 
        String uuid = '123456';
        String genesysIntId = '12345abcd';  
        String origin = 'Inbound';      
        Test.startTest();        
        String status = Phonebook_LC_HUM.createInteraction(uuid,memberAcc.Id,genesysIntId,'Group',origin);
        System.assert(status == '');      
        Test.stopTest();
    }
    static testMethod void testcreateAndUpdateInteraction()
    {        
        Account memberAcc = [Select Id,RecordType.Name from Account where Lastname = 'TestAcc' and FirstName='Member']; 
        String uuid = '123456';
        String genesysIntId = '12345abcd';  
        String origin = 'Inbound';      
        Test.startTest();        
        String newInteractionId = Phonebook_LC_HUM.createInteraction(uuid,memberAcc.Id,genesysIntId,memberAcc.RecordType.Name,origin);
        System.assert(newInteractionId != '');  
        String status = Phonebook_LC_HUM.updateUUIDInInteraction(newInteractionId);
        System.assert(status != '');
        Test.stopTest();
    }
    static testMethod void testgetSurveyTransferNumber()
    {              
        Test.startTest();        
        String transfernumber = Phonebook_LC_HUM.getSurveyTransferNumber();     
        System.assert(transfernumber != '');      
        Test.stopTest();
    }
    static testMethod void testfetchPermissionSetAccess()
    {              
        Test.startTest();        
        Boolean hasaccess = Phonebook_LC_HUM.fetchPermissionSetAccess('CRMS_685_PCC_Customer_Service_Access');   
        System.assert(hasaccess != true);   
        Test.stopTest();
    }

    static testMethod void testsurveyTransferSaveCallData()
    {   
        Test.startTest();
        String transferattributes = '{"ISMANUAL_TRANSFER":"true","TRANSFER_NUMBER":"1234567","TRANSFER_TYPE":"COLD","WATSON_TRANSFER_TS":"06:10:00"}';
        Continuation continueObj = (Continuation)PhoneBook_LC_HUM.surveyTransferSaveCallData(JSON.deserializeUntyped(transferattributes));
        continueObj.continuationMethod = 'respConsultBlindSaveCallData';
        continueObj.state = 'State';
        system.assertEquals('respConsultBlindSaveCallData', continueObj.continuationMethod); 
        HttpResponse httpRestransfersavecall = new Httpresponse ();
        String restransfersavecallBody = '{"SaveCallDataResponse":{"SaveCallDataResult":"true"}}';
        httpRestransfersavecall.setBody(restransfersavecallBody);
        system.assert(httpRestransfersavecall.getBody() == restransfersavecallBody);
        Map<String, HTTPRequest> mapDetails = continueObj.getRequests();
        string reqType = '';
        for(String s : mapDetails.keyset())
        {
            reqType = s;
        }
        Test.setContinuationResponse(reqType,httpRestransfersavecall);
        String result = (String)PhoneBook_LC_HUM.respConsultBlindSaveCallData(reqType);
        System.assertNotEquals(result, null);
        Test.stopTest();
    }

    static testMethod void testautoInteractionCampaignNameAndKeycode()
    {
        Test.startTest();
        String searchModal3 = '{"INQA_FIRST" : "","INQA_LAST":"","INQA_ID":"","INQA_DOB": "","INQA_TYP":"M","INQR_FIRST" : "TestCaller","INQR_LAST":"","INQR_ID":"","INQR_DOB": "","INQR_TYP":"M","AUTHENTICATION_IND": "Y"}';
        String intId3 = PhoneBook_LC_HUM.createAutoInteraction(JSON.deserializeUntyped(searchModal3),'xyzzUUID3x',false,'123456asdfghj1234','{"campaignName":"Test","keycode":"123"}');
        system.assert(intId3<>null);
        Test.stopTest();
    }

    static testMethod void testgetAboutAccountIdCampaignNameAndKeycode()
    {	
        Test.startTest();
        String searchModal = '{"AGENT_CALL_START_TIME":"06:10:00","isConsultBlindClicked":true,"isTransferredCall":false,"AGENT_CALL_END_TIME":"06:20:00","UUID":"Test123","interactionId":"12345-12345-12345-12345","Outbound":false}';
        CRMFunctionality_ONOFF_Switch__c objCS = new CRMFunctionality_ONOFF_Switch__c(Name = '4461937', IsON__c = false);
        insert objCS;
        Map<String,String> mapAboutAccId = PhoneBook_LC_HUM.getAboutAccountId('Test UUID',false,'N','abcd-abcd-abcd-abcd',JSON.deserializeUntyped(searchModal),'{"campaignName":"test","keycode":"123"}');
        System.assertNotEquals(mapAboutAccId.get('aboutId'), '');
        Test.stopTest();
    }

    static testMethod void testupdateInteractionAttribute()
    {
        Test.startTest();
        String searchModal11 = '{"INQA_FIRST" : "","INQA_LAST":"TestGroup00","INQA_ID":"","INQA_DOB": "","INQA_TYP":"G","INQR_FIRST" : "","INQR_LAST":"","INQR_ID":"","INQR_DOB": "","INQR_TYP":"G","AUTHENTICATION_IND": "Y"}';
        String intId11 = PhoneBook_LC_HUM.createAutoInteraction(JSON.deserializeUntyped(searchModal11),'xyzzUUID12',false,'123456asdfghj1234','');
        system.assert(intId11<>null);
        
        Boolean retStatus = Phonebook_LC_HUM.updateInteractionAttribute('xyzzUUID12');
        System.debug('retStatus' + retStatus);
        System.assert(retStatus != false);
        Test.stopTest();
    }
    static testMethod void testupdateInteractionAttributeNew()
    {
        Test.startTest();
        String searchModal11 = '{"INQA_FIRST" : "","INQA_LAST":"TestGroup00","INQA_ID":"","INQA_DOB": "","INQA_TYP":"G","INQR_FIRST" : "","INQR_LAST":"","INQR_ID":"","INQR_DOB": "","INQR_TYP":"G","AUTHENTICATION_IND": "Y"}';
        CRMFunctionality_ONOFF_Switch__c objCS = new CRMFunctionality_ONOFF_Switch__c(Name = '3979946', IsON__c = false);
        insert objCS;
        String intId11 = PhoneBook_LC_HUM.createAutoInteraction(JSON.deserializeUntyped(searchModal11),'xyzzUUID12',false,'123456asdfghj1234','');
        system.assert(intId11<>null);
        
        Boolean retStatus = Phonebook_LC_HUM.updateInteractionAttribute(null);
        System.debug('retStatus' + retStatus);
        System.assert(retStatus != true);
        Test.stopTest();
    }
    static testMethod void testupdateInteractionAttributeNew2()
    {
        Test.startTest();
        String searchModal11 = '{"INQA_FIRST" : "","INQA_LAST":"TestGroup00","INQA_ID":"","INQA_DOB": "","INQA_TYP":"G","INQR_FIRST" : "","INQR_LAST":"","INQR_ID":"","INQR_DOB": "","INQR_TYP":"G","AUTHENTICATION_IND": "Y"}';
        CRMFunctionality_ONOFF_Switch__c objCS = new CRMFunctionality_ONOFF_Switch__c(Name = '3979946', IsON__c = false);
        insert objCS;
        String intId11 = PhoneBook_LC_HUM.createAutoInteraction(JSON.deserializeUntyped(searchModal11),'xyzzUUID12',false,'123456asdfghj1234','');
        system.assert(intId11<>null);

        String intId12 = PhoneBook_LC_HUM.createAutoInteraction(JSON.deserializeUntyped(searchModal11),'xyzzUUID12',false,'123456asdfghj1234','');
        system.assert(intId12<>null);
        
        Boolean retStatus = Phonebook_LC_HUM.updateInteractionAttribute('xyzzUUID12');
        System.debug('retStatus' + retStatus);
        System.assert(retStatus != false);
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
        Map<String,String> mapAboutAccIdInt = PhoneBook_LD_HUM.getCloneInteraction(lstInteractionRec ,false,JSON.deserializeUntyped(searchModal));
        System.assertNotEquals(mapAboutAccIdInt.get('sIntId'), '');
        Test.stopTest();
    }
    static testMethod void testgetClonedInteractionAboutId()
    {	
        Test.startTest();
        String UUID = 'Test UUID';
        Map<String,String> mapAboutAccIdInt = PhoneBook_LC_HUM.getClonedInteractionAboutId(UUID);
        System.assertNotEquals(mapAboutAccIdInt.get('aboutId'), '');
        Test.stopTest();
    }

    static testMethod void testgetMemberWithAccountId()
    {	
        Test.startTest();
        String INQR_ID = '';
        String INQR_FIRST = 'Member';
        String INQR_LAST = 'TestAcc' ;
        String INQR_DOB = '';
        String INQA_TYP = 'M';
        String INQA_ID = '';
        String INQA_FIRST = 'Member';
        String INQA_LAST = 'TestAcc';
        String INQA_DOB = '';
        
        String mapAboutAccIdInt = PhoneBook_LD_HUM.getMemberWithAccountId(INQR_ID, INQR_FIRST, INQR_LAST, INQR_DOB, INQA_TYP, INQA_ID, INQA_FIRST, INQA_LAST, INQA_DOB);
        System.assertEquals(mapAboutAccIdInt, '');

        String mapAboutAccIdInt2 = PhoneBook_LD_HUM.getMemberAboutAccountId(INQA_ID, INQA_FIRST, INQA_LAST, INQA_DOB);
        System.assertEquals(mapAboutAccIdInt2, '');
        Test.stopTest();
    }

    static testmethod void testgetCallerTypeForPhoneBook(){
        Test.startTest();
        String interactingWithType = 'Member';
        String callerType = PhoneBook_LC_HUM.getCallerTypeForPhoneBook(interactingWithType);
        system.assertEquals('Member', callerType);
        Test.stopTest();
    }

    static testMethod void testgetKeycodeCampaignNameFormat(){
        Test.startTest();
        String callLogAttributes1 = '{"uuidata":"xyzzUUID12","campaignname":"Test123","keycode":"54321"}';
        String callLogAttributes2 = '{"uuidata":"xyzzUUID12","campaignname":"Test123"}';
        String callLogAttributes3 = '{"uuidata":"xyzzUUID12","keycode":"54321"}';
        String campaignNameHypenKeycode = PhoneBook_LD_HUM.getKeycodeCampaignNameFormat(callLogAttributes1);
        system.assertEquals('54321 - Test123', campaignNameHypenKeycode);
        String onlyCampaignName = PhoneBook_LD_HUM.getKeycodeCampaignNameFormat(callLogAttributes2);
        system.assertEquals('Test123', onlyCampaignName);
        String onlyKeycode = PhoneBook_LD_HUM.getKeycodeCampaignNameFormat(callLogAttributes3);
        system.assertEquals('54321', onlyKeycode);
        Test.stopTest();
  
    }
    static testMethod void testconsultBlindSaveCallDataMember2()
    {   
        
        Test.startTest();
        String transferattributes = '{"ISMANUAL_TRANSFER":"true","TRANSFER_STIM":"06:10:00","TRANSFER_NUMBER":"1234567","TRANSFER_TYPE":"WARM","UUID":"Test UUID","isCallTransferClicked":true}';
        CRMFunctionality_ONOFF_Switch__c objCS = new CRMFunctionality_ONOFF_Switch__c(Name = '4707880', IsON__c = true);
        insert objCS;
        Continuation continueObj = (Continuation)PhoneBook_LC_HUM.consultBlindSaveCallData(JSON.deserializeUntyped(transferattributes));
        continueObj.continuationMethod = 'respConsultBlindSaveCallData';
        continueObj.state = 'State';
        system.assertEquals('respConsultBlindSaveCallData', continueObj.continuationMethod); 
        HttpResponse httpRestransfersavecall = new Httpresponse ();
        String restransfersavecallBody = '{"SaveCallDataResponse":{"SaveCallDataResult":"true"}}';
        httpRestransfersavecall.setBody(restransfersavecallBody);
        system.assert(httpRestransfersavecall.getBody() == restransfersavecallBody);
        Map<String, HTTPRequest> mapDetails = continueObj.getRequests();
        string reqType = '';
        for(String s : mapDetails.keyset())
        {
            reqType = s;
        }
        Test.setContinuationResponse(reqType,httpRestransfersavecall);
        String result = (String)PhoneBook_LC_HUM.respConsultBlindSaveCallData(reqType);
        System.assertNotEquals(result, null);
        Test.stopTest();
    }
    
    static testMethod void testgetAboutAccountIdNew()
    {   
        String searchModal = '{"INQA_FIRST" : "Member","INQA_LAST":"TestAcc","INQA_ID":"H987654321","INQA_DOB": "","INQA_TYP":"M","INQR_FIRST" : "Member","INQR_LAST":"TestAcc","INQR_ID":"H987654321","INQR_DOB": "","INQR_TYP":"M","AUTHENTICATION_IND": "N"}';
        CRMFunctionality_ONOFF_Switch__c objCS = new CRMFunctionality_ONOFF_Switch__c(Name = '4461937', IsON__c = true);
        CRMFunctionality_ONOFF_Switch__c objCS2 = new CRMFunctionality_ONOFF_Switch__c(Name = '3944531', IsON__c = true);
        insert objCS;
        insert objCS2;
        Test.startTest();
        Map<String,String> mapAboutAccId = PhoneBook_LC_HUM.getAboutAccountId('Test UUID',false,'N','abcd-abcd-abcd-abcd',JSON.deserializeUntyped(searchModal),'');
        System.assertNotEquals(mapAboutAccId .get('aboutId'), '');
        Test.stopTest();
    }
    
            static testMethod void testgetProviderAccountId()
    {   
        //Create Account records for different types
        List<Account> allAccounts = new List<Account>();
        string sProviderRecordtypeID=  Schema.SObjectType.Account.getRecordTypeInfosByName().get('Provider').getRecordTypeId(); 
        Account objProviderAccount = new Account();
        objProviderAccount.recordtypeid = sProviderRecordtypeID;
        objProviderAccount.Name = 'TestProvider';
        allAccounts.add(objProviderAccount);
        insert allAccounts;

        //Create ConsumerID 
        ConsumerID__c consumer = new ConsumerID__c();
        consumer.Consumer_ID__c = '12345';
        consumer.account__c  =  objProviderAccount.Id;
        insert consumer;

        Test.startTest();
        List < ConsumerID__c > AccountIdx = [SELECT Id, Account__r.RecordType.Name, Account__c, Consumer_ID__c FROM ConsumerID__c where Consumer_ID__c =: '12345'];
        string mapAboutAccId = PhoneBook_LD_HUM.getProviderAccountId(AccountIdx);
        system.debug('mapAboutAccId'+mapAboutAccId);
        system.debug('objProviderAccount.ID'+objProviderAccount.ID);
        System.assertNotEquals(objProviderAccount.ID,mapAboutAccId);
        Test.stopTest();
    }
    
    static testMethod void testNegativePhoneBookLCHUMNew()
    {
        Test.startTest(); 
        Continuation continueObj = (Continuation)PhoneBook_LC_HUM.getDefaultPH(null);
        System.assertEquals(null, continueObj, 'msg');
        Continuation continueObj2 = (Continuation)PhoneBook_LC_HUM.callTransfer('');
        System.assertEquals(null, continueObj, 'msg');
        Continuation continueObj3 = (Continuation)PhoneBook_LC_HUM.disconnectSaveCallData('');
        System.assertEquals(null, continueObj, 'msg');
        Continuation continueObj4 = (Continuation)PhoneBook_LC_HUM.surveyTransferSaveCallData('');
        System.assertEquals(null, continueObj, 'msg');
        Continuation continueObj5 = (Continuation)PhoneBook_LC_HUM.consultBlindSaveCallData('');
        System.assertEquals(null, continueObj, 'msg');
        
        Test.stopTest();
    }

    static testMethod void testgetHoldTime()
    {
   
        Test.startTest();        
        Integer status = Phonebook_LC_HUM.getHoldTime('');
        System.assertEquals(null, status, 'msg');
        Test.stopTest();
    }

    static testMethod void testgetCallMemberAccountDetails()
    {
   
        string searchModal = '{"INQA_FIRST" : "Member","INQA_LAST":"TestAcc","INQA_ID":"H987654321","INQA_DOB": "","INQA_TYP":"M","INQR_FIRST" : "Member","INQR_LAST":"TestAcc","INQR_ID":"H987654321","INQR_DOB": "","INQR_TYP":"M","AUTHENTICATION_IND": "Y"}';
        Test.startTest();        
        string intaboutId = Phonebook_LC_HUM.getCallMemberAccountDetails(JSON.deserializeUntyped(searchModal));
        System.assertEquals('', intaboutId, 'msg');
        Test.stopTest();
    }
 
        static testMethod void testinsertIntegrationmappingNeg()
    {   
        Test.startTest();
        List<Account> testAcct = [select id,name from Account where name='TestGroup00'];
        List<MemberPlan> memplan = [select id,name from MemberPlan where name='H123458800'];
        PhoneBook_LC_HUM.insertIntegrationmapping(null);
        System.assertNotEquals(null, memplan, 'msg');
        Test.stopTest();
    }
    
        static testMethod void testgetAboutAccountIdNeg()
    {  
        CRMFunctionality_ONOFF_Switch__c objCS = new CRMFunctionality_ONOFF_Switch__c(Name = '4461937', IsON__c = true);
        insert objCS;
        Test.startTest();
        Map<String,String> mapAboutAccId = PhoneBook_LC_HUM.getAboutAccountId('',true,'N','abcd-abcd-abcd-abcd',null,'');
        System.assertNotEquals(mapAboutAccId .get('aboutId'), '');
        Test.stopTest();
    }
    
        static testMethod void testgetCloneInteractionNew()
    {	
        Test.startTest();
        Map<String,String> mapIntAccountInfo = new Map<String,String>();
        List<Interaction__c> testRecords = new List<Interaction__c>();
        String searchModal = '{"INQA_FIRST" : "Member","INQA_LAST":"TestAcc","INQA_ID":"","INQA_DOB": "","INQA_TYP":"M","NUMBER_OF_TRANSFERS":"1","INQR_FIRST" : "Member","INQR_LAST":"TestAcc","INQR_ID":"","INQR_DOB": "","INQR_TYP":"M","AUTHENTICATION_IND": "Y"}';
        Interaction__c lstInteractionRec = [SELECT Id, Caller_Name__c, Call_data__c, Authenticated__c, Interacting_With__c, Interacting_With_type__c, Interaction_Origin__c, UUID__c, Genesys_Interaction_ID__c,Survey_ID__c, Call_Category_Description__c, CreatedDate,(SELECT Id, Interacting_About__c,Interacting_About__r.FirstName,Interacting_About__r.LastName,Interacting_About__r.BirthDate__c, Interacting_About_Type__c, Authenticated__c,Interaction__c 
                                            FROM Interaction_Members__r order by CreatedDate desc) 
                                            FROM Interaction__c where UUID__c =: 'Test UUID' order by Name desc limit 1];
        PhoneBook_LD_HUM.getCloneInteraction(lstInteractionRec, true, JSON.deserializeUntyped(searchModal));
        System.assertNotEquals(null, lstInteractionRec, 'msg');
        Test.stopTest();
    }
     static testMethod void testgetCloneInteractionNegative()
    {	
        Test.startTest();
        Map<String,String> mapIntAccountInfo = new Map<String,String>();
        List<Interaction_Member__c> testRecords = new List<Interaction_Member__c>();
        String searchModal = '{"INQA_FIRST" : "Member","INQA_LAST":"TestAcc","INQA_ID":"","INQA_DOB": "","INQA_TYP":"M","NUMBER_OF_TRANSFERS":"1","INQR_FIRST" : "Member","INQR_LAST":"TestAcc","INQR_ID":"","INQR_DOB": "","INQR_TYP":"M","AUTHENTICATION_IND": "Y"}';
 			Interaction__c lstInteractionRec = [SELECT Id, Caller_Name__c, Call_data__c, Authenticated__c, Interacting_With__c, Interacting_With_type__c, Interaction_Origin__c, UUID__c, Genesys_Interaction_ID__c,Survey_ID__c, Call_Category_Description__c, CreatedDate,(SELECT Id, Interacting_About__c,Interacting_About__r.FirstName,Interacting_About__r.LastName,Interacting_About__r.BirthDate__c, Interacting_About_Type__c, Authenticated__c,Interaction__c 
                                            FROM Interaction_Members__r order by CreatedDate desc) 
                                            FROM Interaction__c where UUID__c =: 'Test UUID' order by Name desc limit 1];
        PhoneBook_LD_HUM.getCloneInteraction(lstInteractionRec, true ,JSON.deserializeUntyped(searchModal));
        System.assertNotEquals(null, lstInteractionRec, 'msg');
        Test.stopTest();
    }
         static testMethod void testgetCloneInteractionNegativeElse()
    {	
        Test.startTest();
         Map<String,String> mapIntAccountInfo = new Map<String,String>();
        List<Interaction_Member__c> testRecords = new List<Interaction_Member__c>();
        String searchModal = '{"INQA_FIRST" : "Member","INQA_LAST":"TestAcc","INQA_ID":"","INQA_DOB": "","INQA_TYP":"M","NUMBER_OF_TRANSFERS":"1","INQR_FIRST" : "Member","INQR_LAST":"TestAcc","INQR_ID":"","INQR_DOB": "","INQR_TYP":"M","AUTHENTICATION_IND": "Y"}';
 			Interaction__c lstInteractionRec = [SELECT Id, Caller_Name__c, Call_data__c, Authenticated__c, Interacting_With__c, Interacting_With_type__c, Interaction_Origin__c, UUID__c, Genesys_Interaction_ID__c,Survey_ID__c, Call_Category_Description__c, CreatedDate
                                            FROM Interaction__c where UUID__c =: 'Test UUID' order by Name desc limit 1];
        PhoneBook_LD_HUM.getCloneInteraction(lstInteractionRec, true , JSON.deserializeUntyped(searchModal));
        System.assertNotEquals(null, lstInteractionRec, 'msg');
        Test.stopTest();
    }
     static testMethod void TESTgetAgentAccountId(){
         Test.startTest(); 
        String AgentAccountId = PhoneBook_LD_HUM.getAgentAccountId('123456789');
         System.assertNotEquals(null, AgentAccountId, 'msg');
        Test.stopTest();
    }
    static testMethod void TESTGETINTERACTIONSAVEFIELDS(){
        //TestgetInteractionSaveCallDataFields
         List<Interaction__c> intrList= new List<Interaction__c>();
         Test.startTest(); 
        Interaction__c lstInteractionRec = [SELECT Id, Caller_Name__c, Call_data__c, Authenticated__c, Interacting_With__c, Interacting_With_type__c, Interaction_Origin__c, UUID__c, Genesys_Interaction_ID__c,Survey_ID__c, Call_Category_Description__c, CreatedDate,(SELECT Id, Interacting_About__c,Interacting_About__r.FirstName,Interacting_About__r.LastName,Interacting_About__r.BirthDate__c, Interacting_About_Type__c, Authenticated__c,Interaction__c 
                                            FROM Interaction_Members__r order by CreatedDate desc) 
                                            FROM Interaction__c where UUID__c =: 'Test UUID' order by Name desc limit 1];
         CRMFunctionality_ONOFF_Switch__c objSwitch = CRMFunctionality_ONOFF_Switch__c.getValues('4707880');    
        System.assert(objSwitch != null);
        CRMFunctionality_ONOFF_Switch__c objSwitch1 = CRMFunctionality_ONOFF_Switch__c.getValues('4472300');    
        System.assert(objSwitch1 != null);
        Map < String, String > AgentAccountId = PhoneBook_LD_HUM.getInteractionSaveCallDataFields('Test UUID');
        objSwitch.IsON__c=false;
        update objSwitch;
        Map < String, String > AgentAccountId1 = PhoneBook_LD_HUM.getInteractionSaveCallDataFields('Test UUID');
         Map < String, String > AgentAccountId2 = PhoneBook_LD_HUM.getInteractionSaveCallDataFields('Test UUID Other2');
        Test.stopTest();
    }
    static testMethod void TestgetInteractionSaveCallDataFieldsElseCondition(){
        List<Interaction__c> intrList= new List<Interaction__c>();
         Test.startTest(); 
        
		Interaction__c interAction1 = new Interaction__c(Interaction_Origin__c = 'Inbound Call', Interacting_With__c=null,UUID__c='Test UUID Other2',Interacting_With_type__c= '', Authenticated__c = false, Caller_Name__c='test');   
		 intrList.add(interAction1);
        Interaction__c interAction3 = new Interaction__c(Interaction_Origin__c = 'Inbound Call', Interacting_With__c=null,UUID__c='Test UUID Other2',Interacting_With_type__c= 'Other', Authenticated__c = false, Caller_Name__c='test');   
		  intrList.add(interAction3);
        Interaction__c interAction2 = new Interaction__c(Interaction_Origin__c = 'Inbound Call', Interacting_With__c=null,UUID__c='Test UUID Other2',Interacting_With_type__c= 'mayur', Authenticated__c = false, Caller_Name__c='test');   
		 intrList.add(interAction2);
        insert intrList;
         CRMFunctionality_ONOFF_Switch__c objSwitch = CRMFunctionality_ONOFF_Switch__c.getValues('4707880');    
        System.assert(objSwitch != null);
        Map < String, String > AgentAccountId = PhoneBook_LD_HUM.getInteractionSaveCallDataFields('Test UUID Other2');
        Test.stopTest();
    }
        static testMethod void testnegativeautoInteractioncreationOffCondition()
    {
        Test.startTest();
         CRMFunctionality_ONOFF_Switch__c objSwitch = CRMFunctionality_ONOFF_Switch__c.getValues('4461937');    
        System.assert(objSwitch != null);
        objSwitch.IsON__c=false;
        update objSwitch;
        CRMFunctionality_ONOFF_Switch__c objSwitch1 = CRMFunctionality_ONOFF_Switch__c.getValues('3944531');
         objSwitch1.IsON__c=false;
           update objSwitch1;
          System.assert(objSwitch1 != null);
        CRMFunctionality_ONOFF_Switch__c objSwitch2 = CRMFunctionality_ONOFF_Switch__c.getValues('2895841');    
        System.assert(objSwitch2 != null);
        CRMFunctionality_ONOFF_Switch__c objSwitch3 = CRMFunctionality_ONOFF_Switch__c.getValues('3701398');    
        System.assert(objSwitch3 != null);
        
        String searchModal = '{"INQA_FIRST" : "Member","INQA_LAST":"TestAcc","INQA_ID":"H987654321","INQA_DOB": "","INQA_TYP":"M","INQR_FIRST" : "Member","INQR_LAST":"TestAcc","INQR_ID":"H987654321","INQR_DOB": "","INQR_TYP":"M","AUTHENTICATION_IND": "Y"}';
        String intId = PhoneBook_LC_HUM.createAutoInteraction(JSON.deserializeUntyped(searchModal),'UUIDxyzz',false,'123456asdfghj1234','');
        system.assert(intId !='');
        /*objSwitch2.IsON__c=false;
        update objSwitch2;
        objSwitch3.IsON__c=false;
         update objSwitch3;
        
        String intId2 = PhoneBook_LC_HUM.createAutoInteraction(JSON.deserializeUntyped(searchModal),'UUIDxyzz',false,'123456asdfghj1234','');
         system.assert(intId2 !='');*/
        Test.stopTest();
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
        oMedicalPolicy.Group_Name__c = objGrpAccount.Id;
        oMedicalPolicy.Major_LOB__c ='EPO';     
        oMedicalPolicy.ASO__c = 'N';
        oMedicalPolicy.Business_Segment__c='LargeGroup-Medicare';
        oMedicalPolicy.Issue_State__c='IN';
        insert oMedicalPolicy;
              PurchaserPlan purchaserPlan1 = new PurchaserPlan(Name='PPlan1',Major_LOB__c = 'HMO',SourceSystemIdentifier = 'Test1',Product__c = p.Id,Selling_Market_Number__c = '30004',Platform__c = 'CB',Policy_Group_Number__c = 'X5000001',Source_Cust_Cov_Key__c = '3044444001001',Issue_State__c='IN',Source_Major_LOB__c='POD');
 		insert purchaserPlan1;
        MemberPlan memberPlan1 = new MemberPlan(Name = 'H123458800',RelationshipToSubscriber = 'SUBSCRIBER',PlanId = purchaserPlan1.Id ,EffectiveTo = effectiveto,EffectiveFrom = effectivefrom,GroupNumber = 'X5111001',MemberId = actMember.id,Policy__c = oMedicalPolicy.Id,Member_Id_Base__c= 'H123458800');
		insert memberPlan1;
          MemberPlan memberPlan2 = new MemberPlan(Name = 'H123458800',RelationshipToSubscriber = 'SUBSCRIBER',PlanId = purchaserPlan1.Id ,EffectiveTo = effectiveto,EffectiveFrom = effectivefrom,GroupNumber = 'X5111001',MemberId = actMember.id,Policy__c = oMedicalPolicy.Id, Member_Id_Base__c='H123458800');
	insert memberPlan2;
         lstId.add(memberPlan1.Id);
        Test.setFixedSearchResults(lstId);
         String idx = PhoneBook_LD_HUM.getMemberWithAccountId('H123458800','','','','M','','','',''); 
        system.assert(idx<>null);
        lstId.add(memberPlan2.Id);
        Test.setFixedSearchResults(lstId);
        String idx1 = PhoneBook_LD_HUM.getMemberWithAccountId('H123458800','','','','M','','','','');
         String idx2 = PhoneBook_LD_HUM.getMemberWithAccountId('','','','','M','','','','');
        
        Test.stopTest();
    }
         static testMethod void TESTMEMBERABOUT()
    {
        List<Id> lstId= new List<Id>();
        Test.startTest();
        //Create Product 
        Product2 p = new Product2(Name='MER',ProductCode='MER',HDHP_Indicator__c = 'Y');
        insert p;
         string sMemberRecordtypeID=  Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId();  
        Account actMember = HUMTestDataHelperTest.getAccount();
        actMember.Lastname='xAccount';
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
        oMedicalPolicy.Group_Name__c = objGrpAccount.Id;
        oMedicalPolicy.Major_LOB__c ='EPO';     
        oMedicalPolicy.ASO__c = 'N';
        oMedicalPolicy.Business_Segment__c='LargeGroup-Medicare';
        oMedicalPolicy.Issue_State__c='IN';
        insert oMedicalPolicy;
              PurchaserPlan purchaserPlan1 = new PurchaserPlan(Name='PPlan1',Major_LOB__c = 'HMO',SourceSystemIdentifier = 'Test1',Product__c = p.Id,Selling_Market_Number__c = '30004',Platform__c = 'CB',Policy_Group_Number__c = 'X5000001',Source_Cust_Cov_Key__c = '3044444001001',Issue_State__c='IN',Source_Major_LOB__c='POD');
 		insert purchaserPlan1;
        MemberPlan memberPlan1 = new MemberPlan(Name = 'H123458800',RelationshipToSubscriber = 'SUBSCRIBER',PlanId = purchaserPlan1.Id ,EffectiveTo = effectiveto,EffectiveFrom = effectivefrom,GroupNumber = 'X5111001',MemberId = actMember.id,Policy__c = oMedicalPolicy.Id,Member_Id_Base__c= 'H123458800');
		insert memberPlan1;
          MemberPlan memberPlan2 = new MemberPlan(Name = 'H123458800',RelationshipToSubscriber = 'SUBSCRIBER',PlanId = purchaserPlan1.Id ,EffectiveTo = effectiveto,EffectiveFrom = effectivefrom,GroupNumber = 'X5111001',MemberId = actMember.id,Policy__c = oMedicalPolicy.Id, Member_Id_Base__c='H123458800');
	insert memberPlan2;
         lstId.add(memberPlan1.Id);
        Test.setFixedSearchResults(lstId);
         String idx = PhoneBook_LD_HUM.getMemberAboutAccountId('H123458800','','',''); 
        system.assert(idx<>null);
        lstId.add(memberPlan2.Id);
        Test.setFixedSearchResults(lstId);
        String idx1 = PhoneBook_LD_HUM.getMemberAboutAccountId('H123458800','','','');
         String idx2 = PhoneBook_LD_HUM.getMemberAboutAccountId('','','','');
        
        Test.stopTest();
    }
          static testMethod void TestgetProviderWithAccountId()
    {
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
    	String ret2 = PhoneBook_LD_HUM.getProviderWithAccountId('Un12345','','','','A','','','','','123456789');
        system.assert(ret2<>null);
         Test.stopTest();
    }
    static testMethod void testgetCallMemberRecordsNew()
    {

        Test.startTest();
        CRMFunctionality_ONOFF_Switch__c objStchMF3 = new CRMFunctionality_ONOFF_Switch__c();
        objStchMF3.name = '2854267';
        objStchMF3.IsON__c = true;
        insert objStchMF3;
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
        
        
            List<Member_ID__c> lstMemberToInsert= new List<Member_ID__c>();
        
         Member_ID__c objMemberIdSub = HUMTestDataHelperTest.getmemberID();
        objMemberIdSub.Name = '00016021700';
        objMemberIdSub.Member_ID_Value__c = '00016021700';
        objMemberIdSub.Member_Dependent_Code__c = '06';
        objMemberIdSub.Policy_Member__c=objMedPolMemb.id;
        objMemberIdSub.Policy__c=oMedicalPolicy.id;
          lstMemberToInsert.add(objMemberIdSub);
         Member_ID__c objMemberIdSub1 = HUMTestDataHelperTest.getmemberID();
        objMemberIdSub1.Name = '025134289';
        objMemberIdSub1.Member_ID_Value__c = '025134289';
        objMemberIdSub1.Member_Dependent_Code__c = '06';
        objMemberIdSub1.Policy_Member__c=objMedPolMemb.id;
        objMemberIdSub1.Policy__c=oMedicalPolicy.id;
          lstMemberToInsert.add(objMemberIdSub1);
         Member_ID__c objMemberIdSub2 = HUMTestDataHelperTest.getmemberID();
        objMemberIdSub2.Name = 'H3009855600';
        objMemberIdSub2.Member_ID_Value__c = 'H3009855600';
        objMemberIdSub2.Member_Dependent_Code__c = '06';
        objMemberIdSub2.Policy_Member__c=objMedPolMemb.id;
        objMemberIdSub2.Policy__c=oMedicalPolicy.id;
          lstMemberToInsert.add(objMemberIdSub2);
        insert lstMemberToInsert;
        List<Member_ID__c> lstMember=[Select Id,Member_ID_Value__c,Member_Dependent_Code__c,Policy_Member__c,Policy__c from Member_ID__c];
		List<Id> lstId=new List<Id>();
        for(Member_ID__c mem:lstMember){
            lstId.add(mem.Id);
        }
        Test.setFixedSearchResults(lstId);
        PhoneBook_LD_HUM.getCallMemberRecords('000160217', '','', '', '', false);
        PhoneBook_LD_HUM.getCallMemberRecords('000160217', '00','', '', '', false);
        PhoneBook_LD_HUM.getCallMemberRecords('000160217', '','', '', '', false);
        PhoneBook_LD_HUM.getCallMemberRecords('00016021700', '','TEST', 'TEST', 'CB', false);
        PhoneBook_LD_HUM.getCallMemberRecords('H3009855600', '','', '', 'CB', true);
        CRMFunctionality_ONOFF_Switch__c objSwitch = CRMFunctionality_ONOFF_Switch__c.getValues('2854267');
        objSwitch.IsON__c = true;
        update objSwitch;
        System.assert(objSwitch.IsON__c == true);
        PhoneBook_LD_HUM.getCallMemberRecords('H3009855600', '','', '', '', true);
        PhoneBook_LD_HUM.getCallMemberRecords('000160217', '','', '', '', true);
        PhoneBook_LD_HUM.getCallMemberRecords('025134289', '','', '', '', true);
        Test.StopTest();
       
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
        actMember.agent_ID__c ='123456789';
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
         CRMFunctionality_ONOFF_Switch__c objC7= new CRMFunctionality_ONOFF_Switch__c(Name = '4461937', IsON__c = true);
        insert objC7;
 
        String intId3 = PhoneBook_LD_HUM.createAutoInteraction(searchModal,'TestFirst',false,'123456asdfghj1234','{"uuidata":"TestFirst","campaignname":"Test","keycode":"123"}');
        system.assert(intId3<>null);
                String intId4 = PhoneBook_LD_HUM.createAutoInteraction(searchModalMember,'xAccount',false,'123456asdfghj1234','{"uuidata":"xAccount","campaignname":"Test","keycode":"123"}');
        system.assert(intId4<>null);
 
        String intId5 = PhoneBook_LD_HUM.createAutoInteraction(searchModalAgent,'Agent',false,'123456asdfghj1234','{"uuidata":"Agent","campaignname":"Test","keycode":"123"}');
        system.assert(intId5<>null);
                String intId6 = PhoneBook_LD_HUM.createAutoInteraction(searchModalGroup,'Group',false,'123456asdfghj1234','{"uuidata":"Group","campaignname":"Test","keycode":"123"}');
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
         Map<String, object> searchModalBlankINQRTYP=new  Map<String, object>{'MEMBER_PERS_GEN_KEY'=>'','UUID'=>'TestFirst','INQA_FIRST'=>'TestFirst','INQA_LAST'=>'TestLast','INQA_DOB'=>'11-18-2014','INQA_TYP'=>'M','INQR_FIRST'=>'TestFirst','INQR_LAST'=>'TestLast','INQA_ID'=>'000160217','INQR_ID'=>'000160217','INQR_DOB'=>'11-18-2014','INQR_TYP'=>'','AUTHENTICATION_IND'=>'Y','CALL_TYPE'=>'Inbound'};

        PhoneBook_LD_HUM pb = new PhoneBook_LD_HUM();
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
         CRMFunctionality_ONOFF_Switch__c objC7= new CRMFunctionality_ONOFF_Switch__c(Name = '4461937', IsON__c = false);
        insert objC7;
 
        String intId3 = PhoneBook_LD_HUM.createAutoInteraction(searchModal,'TestFirst',false,'123456asdfghj1234','{"uuidata":"TestFirst","campaignname":"Test","keycode":"123"}');
        system.assert(intId3<>null);
                String intId4 = PhoneBook_LD_HUM.createAutoInteraction(searchModalMember,'xAccount',false,'123456asdfghj1234','{"uuidata":"xAccount","campaignname":"Test","keycode":"123"}');
        system.assert(intId4<>null);
 
        String intId5 = PhoneBook_LD_HUM.createAutoInteraction(searchModalAgent,'Agent',false,'123456asdfghj1234','{"uuidata":"Agent","campaignname":"Test","keycode":"123"}');
        system.assert(intId5<>null);
                String intId6 = PhoneBook_LD_HUM.createAutoInteraction(searchModalGroup,'Group',false,'123456asdfghj1234','{"uuidata":"Group","campaignname":"Test","keycode":"123"}');
        system.assert(intId6<>null);
         CRMFunctionality_ONOFF_Switch__c objCS0 =CRMFunctionality_ONOFF_Switch__c.getValues('2876026');
        objCS0.IsON__c=false;
        update objCS0;
         String intId7 = PhoneBook_LD_HUM.createAutoInteraction(searchModalBlankINQRTYP,'xAccount',false,'123456asdfghj1234','{"uuidata":"Group","campaignname":"Test","keycode":"123"}');
        system.assert(intId7<>null);

        Test.stopTest();
}
}
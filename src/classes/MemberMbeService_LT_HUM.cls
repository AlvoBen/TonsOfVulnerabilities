/****************************************************************************
Apex Class Name  : MemberMbeService_LT_HUM
Version          : 1.0  
Created Date     : 05/28/2021
Function         : 
Test Class       : Test class for MemberMbeService_LC_HUM
****************************************************************************
Modification Log:
*Developer Name          Code Review #         Date                       Description
*------------------------------------------------------------------------------------------------------------
* Kajal Namdev                               05/28/2021                   Original Version
***************************************************************************************************/
@isTest
public class MemberMbeService_LT_HUM {
    @testSetup static void setup() 
    {                    
        Account objAcc = HUMTestDataHelperTest.getAccount();
        objAcc.Name = 'testacc';
        objAcc.Enterprise_ID__c = '213654';
        insert objAcc;
        
        Account objAcc3 = HUMTestDataHelperTest.getAccount();
        objAcc3.Name = 'testacc3';
        objAcc3.Enterprise_ID__c = '213654';
        objAcc3.Mbr_Gen_Key__c = 'tests';
        insert objAcc3;
        
        Account objAcc4 = HUMTestDataHelperTest.getAccount();
        objAcc4.Name = 'testacc4';
        objAcc4.Enterprise_ID__c = '213654';
        insert objAcc4;
        
        Account objAcc5 = HUMTestDataHelperTest.getAccount();
        objAcc5.Name = 'testacc5';
        objAcc5.Enterprise_ID__c = '213654';
        objAcc5.Mbr_Gen_Key__c = '213654';
        insert objAcc5;
        
        Account objAccNegative = HUMTestDataHelperTest.getAccount();
        objAccNegative.Name = 'testacc1';
        objAccNegative.Enterprise_ID__c = '213654';
        insert objAccNegative;
        
        Account objAcc1= HUMTestDataHelperTest.getAccount();
        objAcc1.Name = 'testacc2';
        objAcc1.Enterprise_ID__c = '213654';
        insert objAcc1;
        
        Account objAcc9= HUMTestDataHelperTest.getAccount();
        objAcc9.Name = 'testacc9';
        objAcc9.Enterprise_ID__c = '213654';
        insert objAcc9;
        
        Product2 oProduct = new Product2();
        oProduct.Name='Mediclaim';
        oProduct.ProductCode= 'MED';
        oProduct.Major_LOB__c='MER';
        oProduct.Segment_Type__c='COM';
        insert oProduct;
        System.assert(oProduct.id != null); 
        
        PurchaserPlan objpol = new PurchaserPlan();
        objpol.Policy_Group_Number__c='11111';
        objpol.Name='testPolicy';
        objpol.PayerId = objAcc.Id;
        objpol.Major_LOB__c = 'MGP';
        objpol.Source_Cust_Cov_Key__c = '1023880950';
        objpol.Product__c = oProduct.Id;
        objpol.Platform__c = 'NI';
        objpol.Client_Number__c = '57';
        objpol.Contract_Number__c='H1036';
        objpol.Issue_State__c='FL';
        objpol.Medicare_Segment_ID__c='2';
        objpol.PBP_Code__c='806';
        insert objpol;
        system.assertEquals(objpol.Source_Cust_Cov_Key__c,'1023880950');
        
        MemberPlan objPolMemm = new MemberPlan();
        objPolMemm.Name = 'testpolmem';
        objPolMemm.Relationship_Code__c = '00';
        objPolMemm.EffectiveFrom=Date.today().addDays(-1);
        objPolMemm.EffectiveTo =Date.today().addDays(1);
        objPolMemm.MemberId = objAcc.Id;
        objPolMemm.PlanId = objpol.Id;
        objPolMemm.Plan = objpol;
        objPolMemm.Has_Vitality__c = 'Y';
        objPolMemm.ETL_Record_Deleted__c=False;
        objPolMemm.Dual_Status_Indicator__c='Y';
        objPolMemm.EffectiveFrom = Date.Today();
        objPolMemm.EffectiveTo = Date.Today().addYears(+10);
        insert objPolMemm;
        system.assertEquals(objPolMemm.ETL_Record_Deleted__c,False);
        
        MemberPlan objPolMemmN = new MemberPlan();
        objPolMemmN.Name = 'testpolmemN';
        objPolMemmN.Relationship_Code__c = '00';
        objPolMemmN.EffectiveFrom=Date.today().addDays(-1);
        objPolMemmN.EffectiveTo =Date.today().addDays(1);
        objPolMemmN.MemberId = objAcc3.Id;
        objPolMemmN.PlanId = objpol.Id;
        objPolMemmN.Plan = objpol;
        objPolMemmN.Has_Vitality__c = 'N';
        objPolMemmN.ETL_Record_Deleted__c=False;
        objPolMemmN.Dual_Status_Indicator__c='Y';
        objPolMemmN.EffectiveFrom = Date.Today();
        objPolMemmN.EffectiveTo = Date.Today().addYears(+10);
        insert objPolMemmN;
        system.assertEquals(objPolMemmN.ETL_Record_Deleted__c,False);
        
        MemberPlan objPolMemmL = new MemberPlan();
        objPolMemmL.Name = 'testpolmemL';
        objPolMemmL.Relationship_Code__c = '00';
        objPolMemmL.EffectiveFrom=Date.today().addDays(-1);
        objPolMemmL.EffectiveTo =Date.today().addDays(1);
        objPolMemmL.MemberId = objAcc3.Id;
        objPolMemmL.PlanId = objpol.Id;
        objPolMemmL.Plan = objpol;
        objPolMemmL.Has_Vitality__c = 'Y';
        objPolMemmL.ETL_Record_Deleted__c=False;
        objPolMemmL.Dual_Status_Indicator__c='Y';
        objPolMemmL.EffectiveFrom = Date.Today();
        objPolMemmL.EffectiveTo = Date.Today().addYears(+10);
        insert objPolMemmL;
        system.assertEquals(objPolMemmL.ETL_Record_Deleted__c,False);
        
        PurchaserPlan policy1 = new PurchaserPlan();
        policy1.Policy_Group_Number__c='11111';
        policy1.PayerId = objAcc4.Id;
        policy1.Name = 'testPolicy1';
        policy1.Platform__c = 'NI';
        policy1.Major_LOB__c = 'MGP';
        policy1.Source_Cust_Cov_Key__c = '102388095012';
        policy1.Platform__c = 'LV';
        policy1.Client_Number__c = '57';
        policy1.Contract_Number__c='H1036';
        policy1.Issue_State__c='FL';
        policy1.Medicare_Segment_ID__c='2';
        policy1.PBP_Code__c='806';
        insert policy1;
        system.assertEquals(policy1.Source_Cust_Cov_Key__c,'102388095012');
        
        MemberPlan objPolM = new MemberPlan();
        objPolM.Name = 'testpolmem5';
        objPolM.Relationship_Code__c = '00';
        objPolM.EffectiveFrom = Date.today().addDays(-1);
        objPolM.EffectiveTo = Date.today().addDays(1);
        objPolM.MemberId = objAcc1.Id;
        objPolM.PlanId = policy1.Id;
        objPolM.Plan = policy1;
        objPolM.ETL_Record_Deleted__c=False;
        objPolM.Dual_Status_Indicator__c='Y';
        objPolM.EffectiveFrom = Date.Today();
        objPolM.EffectiveTo = Date.Today().addYears(+10);
        insert objPolM;
        system.assertEquals(objPolM.ETL_Record_Deleted__c,False);
        
        Account mAccount = HUMTestDataHelperTest.getAccount();
        mAccount.Name = 'testaccRecord';
        mAccount.Enterprise_ID__c = '2136545';
        insert mAccount;
        
        
        MemberPlan oPolicymem2 = new MemberPlan();
        oPolicymem2.Name = 'testpolmem1';
        oPolicymem2.Relationship_Code__c = '00';
        oPolicymem2.EffectiveFrom= Date.newInstance(2015, 1, 1);
        oPolicymem2.EffectiveTo = Date.newInstance(2015, 12, 31);
        oPolicymem2.MemberId = objAcc.Id;
        oPolicymem2.PlanId = objpol.Id;
        oPolicymem2.Plan = objpol;
        oPolicymem2.ETL_Record_Deleted__c=False;
        oPolicymem2.Dual_Status_Indicator__c='Y';
        oPolicymem2.EffectiveFrom = Date.Today();
        oPolicymem2.EffectiveTo = Date.Today().addYears(+10);
        insert oPolicymem2;
        system.assertEquals(oPolicymem2.ETL_Record_Deleted__c,False);
        
        MemberPlan oPolicymem3 = new MemberPlan();
        oPolicymem3.Name = 'testpolmem6';
        oPolicymem3.Relationship_Code__c = '00';
        oPolicymem3.EffectiveFrom= Date.newInstance(2015, 1, 1);
        oPolicymem3.EffectiveTo = Date.newInstance(2015, 12, 31);
        oPolicymem3.MemberId = objAcc5.Id;
        oPolicymem3.PlanId = objpol.Id;
        oPolicymem3.Plan = objpol;
        oPolicymem3.ETL_Record_Deleted__c=False;
        oPolicymem3.Dual_Status_Indicator__c='Y';
        oPolicymem3.EffectiveFrom = Date.Today();
        oPolicymem3.EffectiveTo = Date.Today().addYears(+10);
        insert oPolicymem3;
        system.assertEquals(oPolicymem3.ETL_Record_Deleted__c,False);       

        insert new HUM_Webservice_Callout__c( Name = 'HUMMemberWebActivityDetailsService' ,Content_Type__c='application/json',Request_Type__c='POST', Certificate_Name__c = 'Humana_GuidanceCare_Mercury', Active__c = true, End_Point_URL__c = 'https://int-crmservicews.humana.com/MemberWebActivityDetails/GetMemberWebActivityDetails', Timeout__c = '12000');         
        insert new DecryptedValues__c ( Name = 'PolicyVitalityFalse' ,DecryptedValue__c='N', EncryptedValue__c= 'N');         
        insert new DecryptedValues__c ( Name = 'PolicyVitalityTrue' ,DecryptedValue__c='Y', EncryptedValue__c= 'Y'); 
        insert new HUMConstants__c (   Name = 'HUMWebEmulate',  IntegerValue__C = 0, stringvalue__c = 'http://hss.humana.com/ITSecurity/HSSOneClick/LaunchHSS.aspx?TARGET=https://slhss.humana.com/WebEmulation/EmulateMember.aspx');    
        insert new HUMConstants__c (   Name = 'HUMMyHumana_Link',  IntegerValue__C = 0, stringvalue__c = 'https://www.humana.com');    
                
        Member_Cache__c memCacheObject = new Member_Cache__c();
        memCacheObject.AccountID__c = objAcc.id;
        memCacheObject.Vitality_Entity_ID__c = '2134567';
        memCacheObject.Member_Gen_Key__c = '2134567';
        memCacheObject.Is_Web_Registered__c = 'YES'; 
        memCacheObject.Web_Last_Login__c  = '5/5/2015';    
        memCacheObject.Cache_Expiration_Date_Web_Last_Login__c  = System.Now().adddays(-1);       
        memCacheObject.Cache_Expiration_Date_Vitality__c = System.Now().adddays(3);
        memCacheObject.Cache_Expiration_Date_Member_Gen_Key__c = System.Now().adddays(3);
        memCacheObject.Cache_Expiration_Date_Is_Web_Registered__c  = System.Now().adddays(3);
        insert memCacheObject  ;
        
        Member_Cache__c memCacheObject9 = new Member_Cache__c();
        memCacheObject9.AccountID__c = objAcc9.id;
        memCacheObject9.Vitality_Entity_ID__c = '2134567';
        memCacheObject9.Member_Gen_Key__c = '2134567';
        memCacheObject9.Is_Web_Registered__c = 'YES'; 
        memCacheObject9.Web_Last_Login__c  = '5/5/2015';    
        memCacheObject9.Cache_Expiration_Date_Web_Last_Login__c  = System.Now().adddays(-3);       
        memCacheObject9.Cache_Expiration_Date_Vitality__c = System.Now().adddays(3);
        memCacheObject9.Cache_Expiration_Date_Member_Gen_Key__c = System.Now().adddays(3);
        memCacheObject9.Cache_Expiration_Date_Is_Web_Registered__c  = System.Now().adddays(3);
        insert memCacheObject9  ; 
        
        Member_Cache__c memCacheObject2 = new Member_Cache__c();
        memCacheObject2.AccountID__c = objAcc1.id;
        memCacheObject2.Vitality_Entity_ID__c = '2134567';
        memCacheObject2.Member_Gen_Key__c = '2134567';
        memCacheObject2.Is_Web_Registered__c = 'YES'; 
        memCacheObject2.Web_Last_Login__c  = '5/5/2015';    
        memCacheObject2.Cache_Expiration_Date_Web_Last_Login__c  = System.Now().adddays(3);       
        memCacheObject2.Cache_Expiration_Date_Vitality__c = System.Now().adddays(-3);
        memCacheObject2.Cache_Expiration_Date_Member_Gen_Key__c = System.Now().adddays(-3);
        memCacheObject2.Cache_Expiration_Date_Is_Web_Registered__c  = System.Now().adddays(3);
        insert memCacheObject2  ; 
        
        
        Member_Cache__c memCacheObject3 = new Member_Cache__c();
        memCacheObject3.AccountID__c = objAcc3.id;
        memCacheObject3.Vitality_Entity_ID__c = '2134567';
        memCacheObject3.Member_Gen_Key__c = '2134567';
        memCacheObject3.Is_Web_Registered__c = 'YES'; 
        memCacheObject3.Web_Last_Login__c  = '1/2/2015';    
        memCacheObject3.Cache_Expiration_Date_Web_Last_Login__c  = System.Now().adddays(-1);       
        memCacheObject3.Cache_Expiration_Date_Vitality__c = System.Now().adddays(3);
        memCacheObject3.Cache_Expiration_Date_Member_Gen_Key__c = System.Now().adddays(3);
        memCacheObject3.Cache_Expiration_Date_Is_Web_Registered__c  = System.Now().adddays(3);
        insert memCacheObject3  ; 
        
        Member_Cache__c memCacheObject1 = new Member_Cache__c();
        memCacheObject1.AccountID__c = objAccNegative.id;
        memCacheObject1.Member_Gen_Key__c = '2134566';
        memCacheObject1.Is_Web_Registered__c = 'YES'; 
        memCacheObject1.Web_Last_Login__c  = 'Feb';    
        memCacheObject1.Cache_Expiration_Date_Web_Last_Login__c  = System.Now(); 
        insert memCacheObject1  ;
    }
    
    @isTest static void testServiceCallY()
    {
        MemberPlan oPolicyMember = [Select ID,MemberId from MemberPlan where Name =: 'testpolmemL'];
        Member_Cache__c memCach = [select Id,Cache_Expiration_Date_Vitality__c,Cache_Expiration_Date_Web_Last_Login__c from Member_Cache__c where AccountID__c =:oPolicyMember.MemberId Limit 1];
        Test.startTest();
        MemberMbeService_LC_HUM objm = new MemberMbeService_LC_HUM();
        Object o1 = MemberMbeService_LC_HUM.getWebInformation(oPolicyMember.ID);
        System.assertNotEquals(o1, NULL); 
        memCach.Cache_Expiration_Date_Web_Last_Login__c = System.now().adddays(2);
        update memCach;
        Object o3 = MemberMbeService_LC_HUM.getWebInformation(oPolicyMember.ID); 
        memCach.Cache_Expiration_Date_Vitality__c = System.now().adddays(-1);
        update memCach;
        Object o2 = MemberMbeService_LC_HUM.getWebInformation(oPolicyMember.ID);
        WebActivityDetails_LC_HUM.getJsonInput('dummy',oPolicyMember.ID);
        Test.stopTest(); 

    }
    @isTest static void testWebActivityFun()
    {
        MemberPlan oPolicyMember = [Select ID,MemberId from MemberPlan where Name =: 'testpolmemN'];
        Member_Cache__c memCach = [select Id,Cache_Expiration_Date_Vitality__c,Cache_Expiration_Date_Web_Last_Login__c from Member_Cache__c where AccountID__c =:oPolicyMember.MemberId Limit 1];
        Test.startTest();
        MemberMbeService_LC_HUM objm = new MemberMbeService_LC_HUM();
        Object o1 = MemberMbeService_LC_HUM.getWebInformation(oPolicyMember.ID);
        System.assertNotEquals(o1, NULL); 
        memCach.Cache_Expiration_Date_Web_Last_Login__c = System.now().adddays(2);
        update memCach;
        Object o3 = MemberMbeService_LC_HUM.getWebInformation(oPolicyMember.ID); 
        memCach.Cache_Expiration_Date_Vitality__c = System.now().adddays(-1);
        update memCach;
        Object o2 = MemberMbeService_LC_HUM.getWebInformation(oPolicyMember.ID);
        WebActivityDetails_LC_HUM.getJsonInput('dummy',oPolicyMember.ID);
        Test.stopTest(); 
    }
    @isTest static void testServiceCallN()
    {
        string sWebEmulateLink = 'http://hss.humana.com/ITSecurity/HSSOneClick/LaunchHSS.aspx?TARGET=https://slhss.humana.com/WebEmulation/EmulateMember.aspx';
        MemberPlan oPolicyMember = [Select ID from MemberPlan where Name =: 'testpolmemL'];
        Account oAcc = [Select ID from Account where Name=: 'testacc3'];
        Test.startTest();
        MemberMbeService_LC_HUM objm = new MemberMbeService_LC_HUM();
        Object o1 = MemberMbeService_LC_HUM.getWebInformation(oPolicyMember.ID);
        Test.stopTest(); 
        System.assertNotEquals(o1, NULL); 
    }
    
    @isTest static void testServiceCallcontinuation()
    {
        MemberMbeService_LC_HUM objm = new MemberMbeService_LC_HUM();
        MemberPlan oPolicyMember = [Select ID,EffectiveFrom,EffectiveTo,Plan.Product__r.ProductCode,Plan.Product__r.Major_LOB__c from MemberPlan where Name=: 'testpolmem1'];
        Account oAcc = [Select ID from Account where Name=: 'testacc5'];
        ComponentValues oComponentValues = new ComponentValues();
        oComponentValues.accountID  = oAcc.id;
        oComponentValues.PolicyID = oPolicyMember.id;
        oComponentValues.VitalityFlagValue = 'Y';
        oComponentValues.continuationState = 'Continuation-1';
        Test.startTest();
        HttpRequest request = new HttpRequest();
        Continuation continuous = new Continuation(1200);        
        Continuation conti = (Continuation) MemberMbeService_LC_HUM.getWebInformation(oPolicyMember.ID);
        HttpResponse response  = new HttpResponse();
        response.setStatusCode(200);         
        response.setbody('{"VitalityEntityID": "2570746269", "PolicyVitalityTrue": "Y", "PolicyVitalityFalse": "N", "MemberGenKey": "6151003076001", "LastLoginDateTime": "5/5/2014 5:55:31 PM", "IsWebRegistered": "true", "ListPoliciesWithVitalityProg": [ { "EffectiveDate": "2021-05-27 00:00:00", "EndDate": "2031-05-27 00:00:00", "MLOB": "MER", "productLineCd": "MED" }], "ErrorMemberWebActivity": "" }');
        Test.setContinuationResponse('Continuation-1', response);
        Object o1 = MemberMbeService_LC_HUM.parseMbeResponseJSON(response);
        Object oResult = MemberMbeService_LC_HUM.processContinuation(oComponentValues);  
        MemberMbe_H_HUM oMBEResponse = new MemberMbe_H_HUM(); 
        MemberMbeService_LC_HUM.parseResponse(response,oComponentValues , oMBEResponse );          
        Test.stopTest(); 
        
    }
    
    @isTest static void testServiceCallcontinuation2()
    {
        MemberMbeService_LC_HUM objm = new MemberMbeService_LC_HUM();
        MemberPlan oPolicyMember = [Select ID,EffectiveFrom,EffectiveTo,Plan.Product__r.ProductCode,Plan.Product__r.Major_LOB__c from MemberPlan where Name=: 'testpolmem6'];
        Account oAcc = [Select ID from Account where Name=: 'testacc5'];
        ComponentValues oComponentValues = new ComponentValues();
        oComponentValues.accountID  = oAcc.id;
        oComponentValues.PolicyID = oPolicyMember.id;
        oComponentValues.VitalityFlagValue = 'Y';
        Test.startTest();
        HttpRequest request = new HttpRequest();
        Continuation continuous = new Continuation(1200);        
        Continuation conti = (Continuation) MemberMbeService_LC_HUM.getWebInformation(oPolicyMember.ID);
        HttpResponse response  = new HttpResponse();
        response.setStatusCode(200);         
        response.setBody('{"VitalityEntityID": "2570746269", "PolicyVitalityTrue": "Y", "PolicyVitalityFalse": "N", "MemberGenKey": "6151003076001", "LastLoginDateTime": "5/5/2014 5:55:31 PM", "IsWebRegistered": "true", "ListPoliciesWithVitalityProg": [ { "EffectiveDate": "01/01/2015", "EndDate": "12/31/2015", "MLOB": "MER", "productLineCd": "MED" }], "ErrorMemberWebActivity": "" }');
        Test.setContinuationResponse('Continuation-1', response);
        Object o1 = MemberMbeService_LC_HUM.parseMbeResponseJSON(response);
        Object oResult = MemberMbeService_LC_HUM.processContinuation(oComponentValues);  
        MemberMbe_H_HUM oMBEResponse = new MemberMbe_H_HUM(); 
        MemberMbeService_LC_HUM.parseResponse(response,oComponentValues , oMBEResponse );          
        Test.stopTest(); 
        
    }
    @isTest static void testParseCall()
    {
        HttpResponse response  = new HttpResponse();
        response.setStatusCode(200);   
        response.setBody('');
        Test.startTest();
        Object o1 = MemberMbeService_LC_HUM.parseMbeResponseJSON(response);
        Test.stopTest(); 
    }
    @isTest static void testIdentifyCCEpolicy()    
    {
        string sPolicyMemId = '123456';
        string sPolicyMemId1 = '';
        MemberPlan oPolicyMember = [Select ID,Policy_Platform__c from MemberPlan where Name=: 'testpolmem1'];
        sPolicyMemId1 = (string)oPolicyMember.id; 
        List<MemberPlan> lstpmember = new List<MemberPlan>();    
        lstpmember.add(oPolicyMember);
        MemberMbe_H_HUM oServiceMemberMbe = new MemberMbe_H_HUM();
        oServiceMemberMbe.MemberGenKey ='6151003076001';
        oServiceMemberMbe.LastLoginDateTime = '5/5/2014 5:55:31 PM';
        oServiceMemberMbe.IsWebRegistered = 'true';
        oServiceMemberMbe.VitalityEntityID = '';//2570746269
        oServiceMemberMbe.PolicyVitalityFalse ='N';
        oServiceMemberMbe.PolicyVitalityTrue   = 'Y';
        oServiceMemberMbe.sVitalityFlagValue = 'Y';        
        Test.startTest();
        MemberMbeService_LC_HUM.IdentifyCCEPolicy(oServiceMemberMbe,sPolicyMemId1,lstpmember);
        Test.stopTest();    
    }
    
    @isTest static void testUpsertCache1()
    {
        Account oAcc = [Select ID from Account where Name=: 'testacc2'];
        MemberMbe_H_HUM oServiceMemberMbe = new MemberMbe_H_HUM();
        oServiceMemberMbe.MemberGenKey ='6151003076001';
        oServiceMemberMbe.LastLoginDateTime = '5/5/2014 5:55:31 PM';
        oServiceMemberMbe.IsWebRegistered = 'true';
        oServiceMemberMbe.VitalityEntityID = '2570746269';
        oServiceMemberMbe.PolicyVitalityFalse ='N';
        oServiceMemberMbe.PolicyVitalityTrue   = 'Y';
        oServiceMemberMbe.sVitalityFlagValue = 'N';  
        List<MemberMBEPolicyList_H_HUM> ListPoliciesWithVitalityProg1 = new  List<MemberMBEPolicyList_H_HUM>();
        MemberMBEPolicyList_H_HUM mPolicy= new MemberMBEPolicyList_H_HUM(); 
        mPolicy.EffectiveDate ='01/01/2015';
        mPolicy.EndDate  ='12/31/2015 ';
        mPolicy.MLOB  ='MER';
        mPolicy.productLineCd  ='MED';
        ListPoliciesWithVitalityProg1.add(mPolicy);         
        oServiceMemberMbe.ListPoliciesWithVitalityProg =   ListPoliciesWithVitalityProg1;        
        Test.startTest();
        
        MemberMbeService_LC_HUM.UpsertcacheMember(oServiceMemberMbe ,oAcc.ID);
        oServiceMemberMbe.sVitalityFlagValue   = 'Y';
        MemberMbeService_LC_HUM.UpsertcacheMember(oServiceMemberMbe ,oAcc.ID);
        Test.stopTest(); 
    }
    @isTest static void testUpsertCache()
    {
        Account oAcc = [Select ID from Account where Name=: 'testacc'];
        
        MemberMbe_H_HUM oServiceMemberMbe = new MemberMbe_H_HUM();
        oServiceMemberMbe.MemberGenKey ='6151003076001';
        oServiceMemberMbe.LastLoginDateTime = '5/5/2014 5:55:31 PM';
        oServiceMemberMbe.IsWebRegistered = 'true';
        oServiceMemberMbe.VitalityEntityID = '2570746269';
        oServiceMemberMbe.PolicyVitalityFalse ='N';
        oServiceMemberMbe.PolicyVitalityTrue   = 'Y';
        oServiceMemberMbe.sVitalityFlagValue = 'N'; 
        List<MemberMBEPolicyList_H_HUM> ListPoliciesWithVitalityProg1 = new  List<MemberMBEPolicyList_H_HUM>();
        MemberMBEPolicyList_H_HUM mPolicy= new MemberMBEPolicyList_H_HUM(); 
        mPolicy.EffectiveDate ='01/01/2015 ';
        mPolicy.EndDate  ='12/31/2015 ';
        mPolicy.MLOB  ='MER';
        mPolicy.productLineCd  ='MED';
        ListPoliciesWithVitalityProg1.add(mPolicy);         
        oServiceMemberMbe.ListPoliciesWithVitalityProg = ListPoliciesWithVitalityProg1;        
        Test.startTest();
        
        MemberMbeService_LC_HUM.UpsertcacheMember(oServiceMemberMbe,oAcc.ID);
        
        Test.stopTest(); 
    } 
}
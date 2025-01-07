/****************************************************************************       //S2K 4
Apex Class Name  : MemberMbeService_T_HUM
Version          : 1.0  
Created Date     : Oct 28, 2015
Function         : Test class for MemberMbeService_S_HUM
Test Class       : 
****************************************************************************
Modification Log:
*Developer Name          Code Review #         Date                       Description
*------------------------------------------------------------------------------------------------------------
* Santhi Mandava         237088                05/31/2018                 Implemented member id platform encrption changes.
* Santhi Mandava         355559                05/10/2019                 Removed "ENCRYPTIONTYPE" custom setting creation
* Santhi Mandava                               04/07/2020                 Created Humconstants new custom setting value
***************************************************************************************************/

@isTest
public class MemberMbeService_T_HUM
{
    @testSetup static void setup() 
    {                    
       // Fetching and inserting Account Record
        Account objAcc = HUMTestDataHelperTest.getAccount();
        objAcc.Name = 'testacc';
        objAcc.Enterprise_ID__c = '213654';
        insert objAcc;
        
        Account objAcc3 = HUMTestDataHelperTest.getAccount();
        objAcc3.Name = 'testacc3';
        objAcc3.Enterprise_ID__c = '213654';
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
        
        Policy__c policy = HUMTestDataHelperTest.getPolicy();
        policy.Group_Name__c = objAcc.id;
        insert policy;
               
        Policy_Member__c oPolicymem = HUMTestDataHelperTest.getPolicyMember();
        oPolicymem.Effective_Date__c = Date.today().addDays(-1).format();
        oPolicymem.End_Date__c = Date.today().addDays(1).format();
        oPolicymem.Name='testpolmem';
        oPolicymem.Has_Vitality__c ='Y';       
        oPolicymem.Policy__c = policy.id;
        oPolicymem.Member__c = objAcc.id;        
        insert oPolicymem ;
        
        Account mAccount = HUMTestDataHelperTest.getAccount();
         
        mAccount.Name = 'testaccRecord';
        mAccount.Enterprise_ID__c = '2136545';
        insert mAccount;
                  
         Product2 oProduct = new Product2();
         oProduct.Name='Mediclaim';
         oProduct.ProductCode= 'MED';
         oProduct.Major_LOB__c='MER';
         oProduct.Segment_Type__c='COM';
         insert oProduct;
         System.assert(oProduct.id != null); 
           
         Policy__c oPolicy = HUMTestDataHelperTest.getPolicy();
         oPolicy.Group_Name__c = mAccount.id;         
         oPolicy.Product__c = oProduct.Id;
         
         insert oPolicy;
        
        Policy__c policy1 = HUMTestDataHelperTest.getPolicy();
         policy1.Group_Name__c = objAcc4.id;
         policy1.Platform__c = 'NI';
         insert policy1;
         
         Policy_Member__c oPolicyMemTest = HUMTestDataHelperTest.getPolicyMember();
         oPolicyMemTest.Name = '12345';
         oPolicyMemTest.Member__c = mAccount.Id;
         oPolicyMemTest.Policy__c = oPolicy.Id;            
     
          oPolicyMemTest.Effective_Date__c ='01/01/2015';          
          oPolicyMemTest.End_Date__c ='12/31/2015';

          insert oPolicyMemTest;
        
        Policy_Member__c oPolicymem2 = HUMTestDataHelperTest.getPolicyMember();
        oPolicymem2.Effective_Date__c = Date.today().addDays(-1).format();
        oPolicymem2.End_Date__c = Date.today().addDays(1).format();
        oPolicymem2.Name='testpolmemN';
        oPolicymem2.Has_Vitality__c ='N';       
        oPolicymem2.Policy__c = policy.id;
        oPolicymem2.Member__c = objAcc.id;
        insert oPolicymem2 ;
        
        Policy_Member__c oPolicymem1 = HUMTestDataHelperTest.getPolicyMember();
        oPolicymem1.Effective_Date__c = Date.today().addDays(-1).format();
        oPolicymem1.End_Date__c = Date.today().addDays(1).format();
        oPolicymem1.Name='testpolmem1';
        oPolicymem1.Has_Vitality__c =null;       
        oPolicymem1.Policy__c = policy1.id;
        oPolicymem1.Member__c = objAcc4.id;
        insert oPolicymem1 ;       
        system.assertEquals(objAcc4.id!=null,true);
        
        Policy_Member__c oPolicymem5 = HUMTestDataHelperTest.getPolicyMember();
        oPolicymem5.Effective_Date__c = Date.today().addDays(-1).format();
        oPolicymem5.End_Date__c = Date.today().addDays(1).format();
        oPolicymem5.Name='testpolmem5';
        oPolicymem5.Has_Vitality__c =null;       
        oPolicymem5.Policy__c = policy1.id;
        oPolicymem5.Member__c = objAcc5.id;
        insert oPolicymem5 ;       
        system.assertEquals(objAcc5.id!=null,true);
        
    Member_ID__c omemid1 =  HUMTestDataHelperTest.getmemberid();
    omemid1.Policy_Member__c = oPolicymem1.id;
    omemid1.Type__c = 'Vitality-Entity-ID';
    omemid1.Policy__c = policy1.id;
    insert omemid1;       
         //Create Custom settings
        insert new HUM_Webservice_Callout__c( Name = 'HUMMemberWebActivityDetailsService' ,Request_Type__c='POST', Certificate_Name__c = 'Humana_GuidanceCare_Mercury', Active__c = true, End_Point_URL__c = 'https://int-crmservicews.humana.com/MemberWebActivityDetails/GetMemberWebActivityDetails', Timeout__c = '12000');         
        insert new DecryptedValues__c ( Name = 'PolicyVitalityFalse' ,DecryptedValue__c='N', EncryptedValue__c= 'N');         
        insert new DecryptedValues__c ( Name = 'PolicyVitalityTrue' ,DecryptedValue__c='Y', EncryptedValue__c= 'Y'); 
        insert new HUMConstants__c (   Name = 'HUMWebEmulate',  IntegerValue__C = 0, stringvalue__c = 'http://hss.humana.com/ITSecurity/HSSOneClick/LaunchHSS.aspx?TARGET=https://slhss.humana.com/WebEmulation/EmulateMember.aspx');    
        insert new HUMConstants__c (   Name = 'HUMMyHumana_Link',  IntegerValue__C = 0, stringvalue__c = 'https://www.humana.com');    
        
        DateTime dtWebLastLogin ;
        String sWebLastLogin, sTodayDate ;
        String[] arrLastLoginDate;
        
           sTodayDate = string.valueof(system.today());  
        
        Member_Cache__c memCacheObject = new Member_Cache__c();
        memCacheObject.AccountID__c = objAcc.id;
        memCacheObject.Vitality_Entity_ID__c = '2134567';
        memCacheObject.Member_Gen_Key__c = '2134567';
        memCacheObject.Is_Web_Registered__c = 'YES'; 
        memCacheObject.Web_Last_Login__c  = '5/5/2015';    
        memCacheObject.Cache_Expiration_Date_Web_Last_Login__c  = System.Now().adddays(3);       
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
        memCacheObject2.Cache_Expiration_Date_Web_Last_Login__c  = System.Now();       
        memCacheObject2.Cache_Expiration_Date_Vitality__c = System.Now();
        memCacheObject2.Cache_Expiration_Date_Member_Gen_Key__c = System.Now();
        memCacheObject2.Cache_Expiration_Date_Is_Web_Registered__c  = System.Now();
        insert memCacheObject2  ; 
                
        sTodayDate = string.valueof(system.today());
        arrLastLoginDate= sTodayDate.split(' ');
                
         Member_Cache__c memCacheObject3 = new Member_Cache__c();
        memCacheObject3.AccountID__c = objAcc3.id;
        memCacheObject3.Vitality_Entity_ID__c = '2134567';
        memCacheObject3.Member_Gen_Key__c = '2134567';
        memCacheObject3.Is_Web_Registered__c = 'YES'; 
        memCacheObject3.Web_Last_Login__c  = '1/2/2015';    
        memCacheObject3.Cache_Expiration_Date_Web_Last_Login__c  = System.Now().adddays(3);       
        memCacheObject3.Cache_Expiration_Date_Vitality__c = System.Now().adddays(-3);
        memCacheObject3.Cache_Expiration_Date_Member_Gen_Key__c = System.Now().adddays(-3);
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
    string sWebEmulateLink = 'http://hss.humana.com/ITSecurity/HSSOneClick/LaunchHSS.aspx?TARGET=https://slhss.humana.com/WebEmulation/EmulateMember.aspx';
        Policy_Member__c oPolicyMember = [Select ID from Policy_Member__c where Name=: 'testpolmem'];
        Account oAcc = [Select ID from Account where Name=: 'testacc'];
        Test.startTest();
        MemberMbeService_S_HUM objm = new MemberMbeService_S_HUM();
        Object o1 = MemberMbeService_S_HUM.getWebInformation(oAcc.ID,oPolicyMember.ID);
       
        Test.stopTest(); 
        System.assertNotEquals(o1, NULL); 
        
    } 
    
 @isTest static void testServiceCallY9()
    {
    string sWebEmulateLink = 'http://hss.humana.com/ITSecurity/HSSOneClick/LaunchHSS.aspx?TARGET=https://slhss.humana.com/WebEmulation/EmulateMember.aspx';
        Policy_Member__c oPolicyMember = [Select ID from Policy_Member__c where Name=: 'testpolmem'];
        Account oAcc9 = [Select ID from Account where Name=: 'testacc9'];
        Test.startTest();
        MemberMbeService_S_HUM objm = new MemberMbeService_S_HUM();
        Object o9 = MemberMbeService_S_HUM.getWebInformation(oAcc9.ID,oPolicyMember.ID);
       
        Test.stopTest(); 
        System.assertNotEquals(o9, NULL); 
        
    }     
    @isTest static void testServiceCallN()
    {
        Policy_Member__c oPolicyMember = [Select ID from Policy_Member__c where Name=: 'testpolmemN'];
        Account oAcc = [Select ID from Account where Name=: 'testacc'];
        Test.startTest();
        
        Object o1 = MemberMbeService_S_HUM.getWebInformation(oAcc.ID,oPolicyMember.ID);
       
        Test.stopTest(); 
        System.assertNotEquals(o1, NULL); 
        
    } 
    
     @isTest static void testServiceCallY1()
    {
        Policy_Member__c oPolicyMember = [Select ID from Policy_Member__c where Name=: 'testpolmem'];
        Account oAcc = [Select ID from Account where Name=: 'testacc2'];
        Test.startTest();
        
        Object o1 = MemberMbeService_S_HUM.getWebInformation(oAcc.ID,oPolicyMember.ID);
       
        Test.stopTest(); 
        System.assertNotEquals(o1, NULL); 
        
    } 
    
    @isTest static void testServiceCallN1()
    {
        Policy_Member__c oPolicyMember = [Select ID from Policy_Member__c where Name=: 'testpolmemN'];
        Account oAcc = [Select ID from Account where Name=: 'testacc2'];
        Test.startTest();
        
        Object o1 = MemberMbeService_S_HUM.getWebInformation(oAcc.ID,oPolicyMember.ID);
       
        Test.stopTest(); 
        System.assertNotEquals(o1, NULL); 
        
    } 
     @isTest static void testServiceCallY2()
    {
        Policy_Member__c oPolicyMember = [Select ID from Policy_Member__c where Name=: 'testpolmem'];
        Account oAcc = [Select ID from Account where Name=: 'testacc3'];
        Test.startTest();
          
        Object o1 = MemberMbeService_S_HUM.getWebInformation(oAcc.ID,oPolicyMember.ID);
       
        Test.stopTest(); 
        System.assertNotEquals(o1, NULL); 
        
    } 
    
   
    
   
     
     @isTest static void testServiceCall()
    {
        Policy_Member__c oPolicyMember = [Select ID from Policy_Member__c where Name=: 'testpolmem1'];
        Account oAcc = [Select ID from Account where Name=: 'testacc4'];
        Test.startTest();
        
        Object o1 = MemberMbeService_S_HUM.getWebInformation(oAcc.ID,oPolicyMember.ID);
       
        Test.stopTest(); 
        
        
    }
    
    @isTest static void testServiceCallNull()
    {
        Policy_Member__c oPolicyMember = [Select ID from Policy_Member__c where Name=: 'testpolmem5'];
        Account oAcc = [Select ID from Account where Name=: 'testacc5'];
        Test.startTest();
        
        Object o1 = MemberMbeService_S_HUM.getWebInformation(oAcc.ID,oPolicyMember.ID);
       
        Test.stopTest(); 
        
        
    }
     
    //continutions
    
     @isTest static void testServiceCallcontinuation()
    {
        MemberMbeService_S_HUM objm = new MemberMbeService_S_HUM();
        Policy_Member__c oPolicyMember = [Select ID from Policy_Member__c where Name=: 'testpolmem1'];
        Account oAcc = [Select ID from Account where Name=: 'testacc4'];        
        ComponentValues oComponentValues = new ComponentValues();
        oComponentValues.accountID  = oAcc.id;
        oComponentValues.PolicyID = oPolicyMember.id;
        oComponentValues.VitalityFlagValue = 'Y';
        Test.startTest();
        HttpRequest request = new HttpRequest();
        Continuation continuous = new Continuation(1200);        
        Continuation conti = (Continuation) MemberMbeService_S_HUM.getWebInformation(oAcc.ID,oPolicyMember.ID);
        HttpResponse response  = new HttpResponse();
        response.setStatusCode(200);         
        response.setBody('{"VitalityEntityID": "2570746269", "PolicyVitalityTrue": "Y", "PolicyVitalityFalse": "N", "MemberGenKey": "6151003076001", "LastLoginDateTime": "5/5/2014 5:55:31 PM", "IsWebRegistered": "true", "ListPoliciesWithVitalityProg": [ { "EffectiveDate": "01/01/2015", "EndDate": "12/31/2015", "MLOB": "MER", "productLineCd": "MED" }], "ErrorMemberWebActivity": "" }');
        Test.setContinuationResponse('Continuation-1', response);
        Object o1 = MemberMbeService_S_HUM.parseMbeResponseJSON(response);
        Object oResult = MemberMbeService_S_HUM.processContinuation(oComponentValues);  
        MemberMbe_H_HUM oMBEResponse = new MemberMbe_H_HUM();   
        MemberMbeService_S_HUM.parseResponse(response,oComponentValues , oMBEResponse );          
        Test.stopTest(); 
        
        
    } 
      @isTest static void testParseCall()
    {
    
        HttpResponse response  = new HttpResponse();
        response.setStatusCode(200);   
    
        response.setBody('{"VitalityEntityID": "2570746269", "PolicyVitalityTrue": "Y", "PolicyVitalityFalse": "N", "MemberGenKey": "6151003076001", "LastLoginDateTime": "5/5/2014 5:55:31 PM", "IsWebRegistered": "true", "ListPoliciesWithVitalityProg": [ { "EffectiveDate": "01/01/2015", "EndDate": "12/31/2015", "MLOB": "MER", "productLineCd": "MED" }], "ErrorMemberWebActivity": "" }');

        Test.startTest();
        
        Object o1 = MemberMbeService_S_HUM.parseMbeResponseJSON(response);
       
        Test.stopTest(); 
        
        
    }
    @isTest static void testIdentifyCCEpolicy()    
    {
    string sPolicyMemId = '123456';
    string sPolicyMemId1 = '';
    Policy_Member__c oPolicyMember = [Select ID from Policy_Member__c where Name=: 'testpolmem1'];
     sPolicyMemId1 = (string)oPolicyMember.id; 
    List<policy_member__c> lstpmember = new List<policy_member__c>();    
    lstpmember.add(oPolicyMember);
    MemberMbe_H_HUM oServiceMemberMbe = new MemberMbe_H_HUM();
        oServiceMemberMbe.MemberGenKey ='6151003076001';
        oServiceMemberMbe.LastLoginDateTime = '5/5/2014 5:55:31 PM';
        oServiceMemberMbe.IsWebRegistered = 'true';
        oServiceMemberMbe.VitalityEntityID = '2570746269';
        oServiceMemberMbe.PolicyVitalityFalse ='N';
        oServiceMemberMbe.PolicyVitalityTrue   = 'Y';
        oServiceMemberMbe.sVitalityFlagValue = 'Y';        
        Test.startTest();
        
        MemberMbeService_S_HUM.IdentifyCCEPolicy(oServiceMemberMbe,sPolicyMemId1,lstpmember);
               
        Test.stopTest();    
    
    }
    @isTest static void testIdentifyCCEpolicy1()    
    {    
    string sPolicyMemId1 = '';
    Policy_Member__c oPolicyMember = [Select ID,Policy_Platform__c from Policy_Member__c where Name=: 'testpolmem1'];
     sPolicyMemId1 = (string)oPolicyMember.id; 
    List<policy_member__c> lstpmember = new List<policy_member__c>();    
    lstpmember.add(oPolicyMember);
    MemberMbe_H_HUM oServiceMemberMbe = new MemberMbe_H_HUM();
        oServiceMemberMbe.MemberGenKey ='6151003076001';
        oServiceMemberMbe.LastLoginDateTime = '5/5/2014 5:55:31 PM';
        oServiceMemberMbe.IsWebRegistered = 'true';
        oServiceMemberMbe.VitalityEntityID = '';
        oServiceMemberMbe.PolicyVitalityFalse ='N';
        oServiceMemberMbe.PolicyVitalityTrue   = 'Y';
                
        Test.startTest();
        MemberMbeService_S_HUM.IdentifyCCEPolicy(oServiceMemberMbe,sPolicyMemId1,lstpmember);
       
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
        oServiceMemberMbe.sVitalityFlagValue = 'Y';  
         List<MemberMBEPolicyList_H_HUM> ListPoliciesWithVitalityProg1 = new  List<MemberMBEPolicyList_H_HUM>();
         MemberMBEPolicyList_H_HUM mPolicy= new MemberMBEPolicyList_H_HUM(); 
         mPolicy.EffectiveDate ='01/01/2015 ';
         mPolicy.EndDate  ='12/31/2015 ';
         mPolicy.MLOB  ='MER';
         mPolicy.productLineCd  ='MED';
         ListPoliciesWithVitalityProg1.add(mPolicy);         
        oServiceMemberMbe.ListPoliciesWithVitalityProg =   ListPoliciesWithVitalityProg1;        
        Test.startTest();
        
         MemberMbeService_S_HUM.UpsertcacheMember(oServiceMemberMbe ,oAcc .ID);
       
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
        oServiceMemberMbe.ListPoliciesWithVitalityProg =   ListPoliciesWithVitalityProg1;        
        Test.startTest();
        
         MemberMbeService_S_HUM.UpsertcacheMember(oServiceMemberMbe ,oAcc.ID);
       
        Test.stopTest(); 
   }  
   
 @isTest static void testUpsertCache9()
    {
        Account oAcc9 = [Select ID from Account where Name=: 'testacc9'];
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
        oServiceMemberMbe.ListPoliciesWithVitalityProg =   ListPoliciesWithVitalityProg1;        
        Test.startTest();
        
         MemberMbeService_S_HUM.UpsertcacheMember(oServiceMemberMbe ,oAcc9.ID);
       
        Test.stopTest(); 
   }    
    @isTest static void testUpsertCache4()
    {
        Account oAcc = [Select ID from Account where Name=: 'testacc3'];
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
        oServiceMemberMbe.ListPoliciesWithVitalityProg =   ListPoliciesWithVitalityProg1;        
        Test.startTest();
        
         MemberMbeService_S_HUM.UpsertcacheMember(oServiceMemberMbe ,oAcc.ID);
       
        Test.stopTest(); 
   }  
   
    @isTest static void testUpsertCache3()
    {
        Account oAcc = [Select ID from Account where Name=: 'testacc4'];
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
        oServiceMemberMbe.ListPoliciesWithVitalityProg =   ListPoliciesWithVitalityProg1;        
        Test.startTest();
        
         MemberMbeService_S_HUM.UpsertcacheMember(oServiceMemberMbe ,oAcc.ID);
       
        Test.stopTest(); 
   }  
    
    @isTest static void testhasVitality()
    {
        Policy_Member__c oPolicyMember = [Select ID from Policy_Member__c where Name=: 'testpolmem'];
        
        Test.startTest();
        string Pid=   String.valueOf(oPolicyMember.ID);
        system.debug(Pid);     
        string HasVitality = WebActivityDetails_C_HUM.getHasVitality(Pid);
       
        Test.stopTest(); 
        System.assertNotEquals(HasVitality , NULL); 
        
    }  
    

    @isTest static void testupdateVitality()
    {
        Policy_Member__c oPolicyMember = [Select ID from Policy_Member__c where Name=: 'testpolmem'];
        Policy_Member__c oPolicyMemberData = [Select ID from Policy_Member__c where Name=: '12345'];
        
        MemberMbe_H_HUM oServiceMemberMbe = new MemberMbe_H_HUM();
        oServiceMemberMbe.MemberGenKey ='6151003076001';
        oServiceMemberMbe.LastLoginDateTime = '5/5/2014 5:55:31 PM';
        oServiceMemberMbe.IsWebRegistered = 'true';
        oServiceMemberMbe.VitalityEntityID = '2570746269';
        oServiceMemberMbe.PolicyVitalityFalse ='N';
        oServiceMemberMbe.PolicyVitalityTrue   = 'Y';
         List<MemberMBEPolicyList_H_HUM> ListPoliciesWithVitalityProg1 = new  List<MemberMBEPolicyList_H_HUM>();
         MemberMBEPolicyList_H_HUM mPolicy= new MemberMBEPolicyList_H_HUM(); 

         mPolicy.EffectiveDate ='01/01/2015';
         mPolicy.EndDate  ='12/31/2015';
         mPolicy.MLOB  ='MER';
         mPolicy.productLineCd  ='MED';
         ListPoliciesWithVitalityProg1.add(mPolicy);         
        oServiceMemberMbe.ListPoliciesWithVitalityProg =   ListPoliciesWithVitalityProg1;
        Boolean ccePolicy= false;
        Test.startTest();
        
        MemberMbeService_S_HUM.UpdateHasVitality(oServiceMemberMbe ,oPolicyMember.ID,ccePolicy);
        ccePolicy= true;
        MemberMbeService_S_HUM.UpdateHasVitality(oServiceMemberMbe ,oPolicyMember.ID,ccePolicy);
        ccePolicy= false;
        MemberMbeService_S_HUM.UpdateHasVitality(oServiceMemberMbe ,oPolicyMemberData.ID,ccePolicy);
       
        Test.stopTest(); 
    }  
        
    @isTest static void testbuildServiceCallout()
    {
        Test.startTest();
        HUM_Webservice_Callout__c oSvcHeaders = WebActivityDetails_C_HUM.buildServiceCallout();       
        Test.stopTest(); 
        System.assertNotEquals(oSvcHeaders , NULL);         
    }
     @isTest static void testbuildServiceCalloutN()
    {        
        Test.startTest();
        HUM_Webservice_Callout__c oSvcHeaders1 = WebActivityDetails_C_HUM.buildServiceCallout(); 
        oSvcHeaders1.Content_Type__c = '';
        update oSvcHeaders1 ;      
        Test.stopTest(); 
        System.assertNotEquals(oSvcHeaders1 , NULL);       
    }                       
}
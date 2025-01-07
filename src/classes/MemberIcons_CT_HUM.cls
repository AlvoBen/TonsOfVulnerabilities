/**************************************************************************************************************************************************
Apex class Name    :  MemberIcons_CT_HUM
Version            :  1.0
Function           :  This is the test class for MemberIcons_C_HUM.class

   Modification Log:
    
 *   Developer                   Code Review             Date               Description
 * -------------------------------------------------------------------------------------------------------------------------------------------------  
 *  Santhi Mandava                                       08/27/2018          Original Version
 *  Amar Gadewar                                         10/29/2019          REQ - 411303 Quality - 2020 New Member Indicator (CRM)
 *  Amar Gadewar                                         01/28/2020          USER STORY 729689 PR00091574 - MF 1 - User Interface to manage Icons display (CRM)
 *  Amit Agarwal                                         06/25/2020          User Story 1186331: PR00091574 - MF 1 - Quality - Ability to display Repeat Caller Icon on search page and Repeat Caller Information on the Member Account page (RF)
 *  Atia Uzma                                            06/18/2020          User Story 1449938: PR00091574 - MF 1 - Iconology - HIDE Icon (CRM)
 *  Samantha Tennyson                                    12/08/2021          Removing unused existing logic for Dual Eligible Icon
 *  Anjani Vaddadi                                        02/26/2021          User Story#-1260069 -T1PRJ0003372 - MF 1 - FR1.1 Veteran Member Icon
 *  Himalay Patel									     03/16/2021 		 REQ - 1711424: Display Expired and Expiring Soon Credit Card Icon (CRMS) (ID# 131)
 *  Himalay Patel									     04/05/2021 		 REQ - 1711424: Display Expired and Expiring Soon Credit Card Icon (CRMS) (ID# 131)
 * AshokKumar Nutalapati   								 07/12/2021          REQ - 1907149 T1PRJ0002517 - MF 2 - Display Icon When Pharmacy Authorization on File (RxRF) (ID# 131)
 *  Atia Uzma                                            09/27/2021          User Story 2696506: T1PRJ0001827 - MF 1 - Iconology - ID Card Request Icon (RF)
 *  Atia Uzma                                            01/18/2022          User Story 2977635: T1PRJ0314829 - MF 1 - Iconology - LI NET Icon (RF)
  ************************************************************************************************************************************************* */
@isTest
public Class MemberIcons_CT_HUM
{
    /*
    * Method Name : prepareTestData
    * Description : Used to create wrapper class data
    * Return type : void
    * Paramater   : NA
    */
    @testSetup static void prepareTestData() 
    {
        User oCCSUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oCCSUser;
        system.assertEquals(oCCSUser.LastName,'test User1');

        List<PermissionSet> permSet = [SELECT Id, Name FROM PermissionSet where name = 'CRM_RF_LouisvilleUser_Access'];                        
        PermissionSetAssignment permSetAssign = new PermissionSetAssignment();
        permSetAssign.PermissionSetId = permSet[0].ID;
        permSetAssign.AssigneeId = oCCSUser.Id;        
        insert permSetAssign;
        
        System.runAs (oCCSUser) 
        {
            
            
            List<MemberIcon_HUM__c> lstIcons = new List<MemberIcon_HUM__c>();
            lstIcons.Add(new MemberIcon_HUM__c(Name='NewMember',DisplayText__c='New Member',PageName__c='Member Account',DocumentName__c='NewMemember',IsVisible__c=true));
            lstIcons.Add(new MemberIcon_HUM__c(Name='BirthDay',DisplayText__c='Happy Birthday',PageName__c='Member Account',DocumentName__c='BirthDay',IsVisible__c=true));
            lstIcons.Add(new MemberIcon_HUM__c(Name='Deceased',DisplayText__c='Deceased',PageName__c='Member Account',DocumentName__c='Deceased',IsVisible__c=true));
            lstIcons.Add(new MemberIcon_HUM__c(Name='GroupMedicare',DisplayText__c='Group Medicare',PageName__c='Member Account,Policy Member',DocumentName__c='GroupMedicare',IsVisible__c=true));
            lstIcons.Add(new MemberIcon_HUM__c(Name='MedicareSupplement',DisplayText__c='Medicare Supplement',PageName__c='Member Account,Policy Member',DocumentName__c='MedicareSupplement',IsVisible__c=true));
            lstIcons.Add(new MemberIcon_HUM__c(Name='H1036Contract',DisplayText__c='H1036 Contract',PageName__c='Member Account,Policy Member',DocumentName__c='H1036Contract',IsVisible__c=true));
            lstIcons.Add(new MemberIcon_HUM__c(Name='RepeatCaller',DisplayText__c='Repeat Caller',DocumentName__c='RepeatCaller',IsVisible__c=true,Order__c = '9',PageName__c='Member Account'));
            lstIcons.Add(new MemberIcon_HUM__c(Name='HIDE',DisplayText__c='HIDE',DocumentName__c='HIDE',IsVisible__c=true,Order__c = '10',PageName__c='Member Account,Policy Member,Search'));
	        lstIcons.Add(new MemberIcon_HUM__c(Name='FIDE',DisplayText__c='FIDE',DocumentName__c='FIDE',IsVisible__c=true,Order__c = '11',PageName__c='Member Account,Policy Member,Search'));
            lstIcons.Add(new MemberIcon_HUM__c(Name='VeteranIcon',DisplayText__c='Veteran',DocumentName__c='VeteranIcon',IsVisible__c=true,Order__c = '4',PageName__c='Member Account,Search',PermissionSet__c='')); 
            lstIcons.Add(new MemberIcon_HUM__c(Name='creditCardExpiredIcon',DisplayText__c='Expired',DocumentName__c='CreditCardCaution_RGB_Green',IsVisible__c=true,Order__c = '14',PageName__c='Humana Pharmacy'));
            lstIcons.Add(new MemberIcon_HUM__c(Name='creditCardExpiringSoonIcon',DisplayText__c='Expiring Soon',DocumentName__c='MagneticStripCardTime_RGB_Green',IsVisible__c=true,Order__c = '15',PageName__c='Humana Pharmacy'));
            lstIcons.Add(new MemberIcon_HUM__c(Name='PharmacyAuthorization',DisplayText__c='Authorization',DocumentName__c='PharmacyAuthorization',IsVisible__c=true,Order__c = '16',PageName__c='Humana Pharmacy'));
            lstIcons.Add(new MemberIcon_HUM__c(Name='IDCardRequest',DisplayText__c='ID Card Request',DocumentName__c='IDCardRequest',IsVisible__c=true,Order__c = '18',PageName__c='Policy Member'));
            lstIcons.Add(new MemberIcon_HUM__c(Name='LINET',DisplayText__c='LI NET',DocumentName__c='LINET',IsVisible__c=true,Order__c = '15',PageName__c='Member Account,Policy Member,Search'));
			insert lstIcons;
			            
            Product2 oProd = new Product2(Name = 'Laptop X200',Segment_Type__c='MCR',ProductCode='MED', Major_LOB__c='MER');
            insert oProd;
            System.assertNotEquals(oProd.Id, NULL, 'Value not matched');
            
            Product2 oProdMA = new Product2(Name = 'MED',Segment_Type__c='MCR',ProductCode='MA', Major_LOB__c='MA', Product_Type__c='MA');
            insert oProdMA;
            System.assertNotEquals(oProdMA.Id, NULL, 'Value not matched');
            
            Product2 oProdMAPD = new Product2(Name = 'MED',Segment_Type__c='MCR',ProductCode='MAPD', Major_LOB__c='MAPD', Product_Type__c='MAPD');
            insert oProdMAPD;
            System.assertNotEquals(oProdMAPD.Id, NULL, 'Value not matched');
            
            Account objAcc = HUMTestDataHelperTest.getAccount(); 
            objAcc.FirstName = 'Test';
            objAcc.LastName = 'Account';
            objAcc.Tenant_Id__c = '00';
            objAcc.Enterprise_ID__c = '1023880950';
            objAcc.General_Account__c = true;
            ObjAcc.BirthDate__c = Date.today().month() +'/'+Date.today().day()+'/'+Date.today().year();
            objAcc.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get(System.label.HUMCOBMemberLabel).getRecordTypeId();

            insert objAcc;
            System.assertEquals(objAcc.Tenant_Id__c,'00');
            
            Account objAcc1 = HUMTestDataHelperTest.getAccount(); 
            objAcc1.FirstName = 'Test';
            objAcc1.LastName = 'AccountName2';
            objAcc1.Tenant_Id__c = '00';
            objAcc1.Enterprise_ID__c = '1023880950';
            objAcc1.General_Account__c = true;
            objAcc1.BirthDate__c = '08/27/201';
            objAcc1.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get(System.label.HUMCOBMemberLabel).getRecordTypeId();
            insert objAcc1;
            System.assertEquals(objAcc1.Tenant_Id__c,'00');
            
            Policy__c objpol = HUMTestDataHelperTest.getPolicy();
            objpol.Name='testPolicy';
            objpol.Group_Name__c = objAcc.Id;
            objpol.Major_LOB__c = 'MER';
            objpol.Source_Cust_Cov_Key__c = '1023880950';
            objpol.Product__c = oProd.Id;
            objpol.Product__r = oProd;
            objpol.Platform__c = 'LV';
            objpol.Client_Number__c = '57';
            insert objpol;
            system.assertEquals(objpol.Source_Cust_Cov_Key__c,'1023880950');
            
            
            Policy__c objpolMA = HUMTestDataHelperTest.getPolicy();
            objpolMA.Name='testPolicyMA';
            objpolMA.Group_Name__c = objAcc.Id;
            objpolMA.Major_LOB__c = 'MA';
            objpolMA.Source_Cust_Cov_Key__c = '1023880951';
            objpolMA.Product__c = oProdMA.Id;
            objpolMA.Product__r = oProdMA;
            objpolMA.Platform__c = 'LV';
            objpolMA.Client_Number__c = '57';
            insert objpolMA;
            system.assertEquals(objpolMA.Source_Cust_Cov_Key__c,'1023880951');
            
            Policy__c objpolMAPD = HUMTestDataHelperTest.getPolicy();
            objpolMAPD.Name='testPolicyMAPD';
            objpolMAPD.Group_Name__c = objAcc.Id;
            objpolMAPD.Major_LOB__c = 'MAPD';
            objpolMAPD.Source_Cust_Cov_Key__c = '1023880952';
            objpolMAPD.Product__c = oProdMAPD.Id;
            objpolMAPD.Product__r = oProdMAPD;
            objpolMAPD.Platform__c = 'LV';
            objpolMAPD.Client_Number__c = '57';
            insert objpolMAPD;
            system.assertEquals(objpolMAPD.Source_Cust_Cov_Key__c,'1023880952');
            
            Policy__c objpolMPD = HUMTestDataHelperTest.getPolicy();
            objpolMPD.Name='testPolicyMPD';
            objpolMPD.Group_Name__c = objAcc.Id;
            objpolMPD.Major_LOB__c = 'MPD';
            objpolMPD.Source_Cust_Cov_Key__c = '1023880952';
            objpolMPD.Product__c = oProdMAPD.Id;
            objpolMPD.Product__r = oProdMAPD;
            objpolMPD.Platform__c = 'LV';
            objpolMPD.Client_Number__c = '57';
            insert objpolMPD;
            system.assertEquals(objpolMPD.Source_Cust_Cov_Key__c,'1023880952');
            
            Policy_Member__c objPolMem = new Policy_Member__c();
            objPolMem.Name = 'testpolmem11';
            objPolMem.Relationship__c = 'Self';
            objPolMem.Relationship_Code__c = '00';
            objPolMem.Subscriber__c = objPolMem.Id;
            objPolMem.Effective_Date__c=Date.today().addDays(-1).format();
            objPolMem.End_Date__c=Date.today().addDays(1).format();
            objPolMem.Policy_Member_External_ID__c='00|ODS|6925004434817|12345678|34567|2013-07-01';
            objPolMem.Member__c = objAcc.Id;
            objPolMem.Policy__c = objpol.Id;
            objPolMem.Policy__r = objpol;
            insert objPolMem;
            system.assertEquals(objPolMem.Relationship__c, 'Self');
            
            Policy_Member__c objPolMemMPD = new Policy_Member__c();
            objPolMemMPD.Name = 'testpolmemMPD';
            objPolMemMPD.Relationship__c = 'Self';
            objPolMemMPD.Relationship_Code__c = '00';
            objPolMemMPD.Subscriber__c = objPolMem.Id;
            objPolMemMPD.Effective_Date__c=Date.today().addDays(-1).format();
            objPolMemMPD.End_Date__c=Date.today().addDays(1).format();
            objPolMemMPD.Policy_Member_External_ID__c='00|ODS|6925004434817|12345678|34567|2011-07-01';
            objPolMemMPD.Member__c = objAcc.Id;
            objPolMemMPD.Policy__c = objpolMPD.Id;
            //objPolMemMPD.Policy__r = objpolMPD;
            insert objPolMemMPD;
            system.assertEquals(objPolMemMPD.Relationship__c, 'Self');
            
            Policy_Member__c objTermPolMem = new Policy_Member__c();
            objTermPolMem.Name = 'testpolmem11';
            objTermPolMem.Relationship__c = 'Self';
            objTermPolMem.Relationship_Code__c = '00';
            objTermPolMem.Subscriber__c = objPolMem.Id;
            objTermPolMem.Effective_Date__c=Date.today().addDays(-10).format();
            objTermPolMem.End_Date__c=Date.today().addDays(-1).format();
            objTermPolMem.Policy_Member_External_ID__c='00|ODS|6925004434817|12345678|34569|2013-07-01';
            objTermPolMem.Member__c = objAcc.Id;
            objTermPolMem.Policy__c = objpolMA.Id;
            objTermPolMem.Policy__r = objpolMA;
            insert objTermPolMem;
            system.assertEquals(objPolMem.Relationship__c, 'Self');
        }
    }
    
    /*
    * Method Name : testMemberIcons
    * Description : This method is used to test main functionality.
    * Return type : void
    * Parameter   : NA
    */
    static testmethod void testMemberIcons() 
    {
        User oCCSUser = [Select id,UserName from User where LastName = 'test User1' LIMIT 1 ] ;
        system.assertEquals(oCCSUser.UserName,'user1user1@humcrmstest.com');
        
        System.runAs (oCCSUser) 
        { 
            Test.StartTest();
            Account oAccount = [Select Id, Name, Tenant_Id__c, RecordType.Name,Enterprise_ID__c, Veteran_Status__c  from Account where LastName = 'Account'];  
            System.assertEquals(oAccount.Tenant_Id__c, '00');
            String sAccRecType = oAccount.RecordType.Name;
            System.assertEquals(sAccRecType,'Member');
            String sBirthDate = Date.today().month() +'/'+Date.today().day()+'/'+Date.today().year();
            MemberIcons_C_HUM objClass = new MemberIcons_C_HUM();
            objClass.sRecordId = oAccount.Id;
            objClass.sPageName = 'Member Account';
            objClass.sBirthDate = sBirthDate ;
            objClass.bVeteranIconOnOffSwitch = true;
            objClass.bLINETIconOnOffSwitch = true; 
            objClass.sVeteranStatus = oAccount.Veteran_Status__c;
            objClass.getMemberIconStatus();
            System.assertEquals(objClass.objIcons.lstMemberIcons.size() ,2);
            Policy_Member__c objPolMember = [Select Id From Policy_Member__c where Name ='testpolmemMPD' Limit 1];
            objClass = new MemberIcons_C_HUM();
            objClass.sRecordId = objPolMember.Id;
            objClass.sPageName = 'Policy Member';
            objClass.sBirthDate = '';
            objClass.getMemberIconStatus();
            System.assertEquals(objClass.objIcons.lstMemberIcons.size() ,1);
            objClass = new MemberIcons_C_HUM();
            objClass.sRecordId = objPolMember.Id;
            objClass.sPageName = 'Humana Pharmacy';
            objClass.bExpiredIcon = true;
            objClass.bExpiringSoonIcon = true;
			objClass.bPharmacyAuthIcon = true;
            objClass.getMemberIconStatus();
            System.assertEquals(objClass.objIcons.lstMemberIcons.size() ,3);
            Test.stopTest();           
        }
    }   
}
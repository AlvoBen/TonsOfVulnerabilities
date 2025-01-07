/**************************************************************************************************************************************************
Apex class Name    :  MemberIcons_ST_HUM
Version            :  1.0
Function           :  This is the test class for MemberIcons_S_HUM.class

   Modification Log:
    
 *   Developer                   Code Review             Date               Description
 * -------------------------------------------------------------------------------------------------------------------------------------------------  
 *  Santhi Mandava                                       08/27/2018          Original Version
 *  Amar Gadewar                                         10/29/2019          REQ - 411303 Quality - 2020 New Member Indicator (CRM)
 *  Amar Gadewar                                         01/28/2020          USER STORY 729689 PR00091574 - MF 1 - User Interface to manage Icons display (CRM)
 *  Amit Agarwal                                         06/25/2020          User Story 1186331: PR00091574 - MF 1 - Quality - Ability to display Repeat Caller Icon on search page and Repeat Caller Information on the Member Account page (RF)
 *  Amit Agarwal                                         09/08/2020          Remove Assert Statement-TestClass Failure for BirthdayIcon
 *  Atia Uzma                                            09/18/2020          User Story 1449938: PR00091574 - MF 1 - Iconology - HIDE Icon (CRM)
 *  Raja Sekhar Reddy M V                                11/13/2020          User Story 1660597: PR00096346 - MF 1 - DEV - New Member Indicator  
 *  Samantha Tennyson                                    12/08/2021          Removing unused existing logic for Dual Eligible Icon
 *  Anjani Vaddadi                                       02/26/2021          User Story#-1260069 -T1PRJ0003372 - MF 1 - FR1.1 Veteran Member Icon
 *  Atia Uzma                                            09/27/2021          User Story 2696506: T1PRJ0001827 - MF 1 - Iconology - ID Card Request Icon (RF)
 *  Amar Gadewar                                         10/22/2021          User Story 2607022: T1PRJ0001827 - MF 1 - 2022 New Member Indicator (CRM)
 *  Atia Uzma                                            01/18/2022          User Story 2977635: T1PRJ0314829 - MF 1 - Iconology - LI NET Icon (RF)
************************************************************************************************************************************************* */
@isTest
public Class MemberIcons_ST_HUM
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

        User oHPSUser = HUMTestDataHelperTest.createTestUser('User2','Humana Pharmacy Specialist');
        insert oHPSUser;
        system.assertEquals(oHPSUser.LastName,'test User2');
        
        User oCCSpUser = HUMTestDataHelperTest.createTestUser('User3','Customer Care Supervisor');
        insert oCCSpUser;
        system.assertEquals(oCCSpUser.LastName,'test User3');
        
        List<PermissionSet> permSet = [SELECT Id, Name FROM PermissionSet where name = 'CRMS_684_Medicare_Customer_Service_Access'];                        
        PermissionSetAssignment permSetAssign = new PermissionSetAssignment();
        permSetAssign.PermissionSetId = permSet[0].ID;
        permSetAssign.AssigneeId = oCCSUser.Id;        
        insert permSetAssign;

        PermissionSetAssignment permSetAssign1 = new PermissionSetAssignment();
        permSetAssign1.PermissionSetId = permSet[0].ID;
        permSetAssign1.AssigneeId = oHPSUser.Id;        
        insert permSetAssign1;
        
        List<PermissionSet> permSet2 = [SELECT Id, Name FROM PermissionSet where name = 'CRMS_685_PCC_Customer_Service_Access'];                        
        PermissionSetAssignment permSetAssign2 = new PermissionSetAssignment();
        permSetAssign2.PermissionSetId = permSet2[0].ID;
        permSetAssign2.AssigneeId = oCCSpUser.Id;        
        insert permSetAssign2;

        List<PermissionSet> permSet3 = [SELECT Id, Name FROM PermissionSet where name = 'CRMS_205_CCSPDPPharmacyPilot'];                        
        PermissionSetAssignment permSetAssign3 = new PermissionSetAssignment();
        permSetAssign3.PermissionSetId = permSet3[0].ID;
        permSetAssign3.AssigneeId = oCCSpUser.Id;        
        insert permSetAssign3;
        
        System.runAs (oCCSUser) 
        {
            
            Product2 oProd = new Product2(Name = 'Laptop X200',Segment_Type__c='MCR',ProductCode='MED', Major_LOB__c='MER');
            insert oProd;
            System.assertNotEquals(oProd.Id, NULL, 'Value not matched');
            
            Product2 oProdMA = new Product2(Name = 'MED',Segment_Type__c='MCR',ProductCode='MA', Major_LOB__c='MA', Product_Type__c='MA');
            insert oProdMA;
            System.assertNotEquals(oProdMA.Id, NULL, 'Value not matched');
            
            Product2 oProdPDP = new Product2(Name = 'MED',Segment_Type__c='MCR',ProductCode='PDP', Major_LOB__c='PDP', Product_Type__c='PDP');
            insert oProdPDP;
            System.assertNotEquals(oProdPDP.Id, NULL, 'Value not matched');
            
            Product2 oProdMAPD = new Product2(Name = 'MED',Segment_Type__c='MCR',ProductCode='MAPD', Major_LOB__c='MAPD', Product_Type__c='MAPD');
            insert oProdMAPD;
            System.assertNotEquals(oProdMAPD.Id, NULL, 'Value not matched');
            
            Account objAcc = HUMTestDataHelperTest.getAccount(); 
            objAcc.FirstName = 'Test';
            objAcc.LastName = 'Account';
            objAcc.Tenant_Id__c = '00';
            objAcc.Enterprise_ID__c = '1023880950';
            objAcc.General_Account__c = true;
            ObjAcc.BirthDate__c = '11/15/2020';
            objAcc.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get(System.label.HUMCOBMemberLabel).getRecordTypeId();
            objAcc.Veteran_Status__c ='confirmed-veteran';    
            insert objAcc;
            System.assertEquals(objAcc.Tenant_Id__c,'00');
            
            Account objAcc1 = HUMTestDataHelperTest.getAccount(); 
            objAcc1.FirstName = 'Test';
            objAcc1.LastName = 'AccountName2';
            objAcc1.Tenant_Id__c = '00';
            objAcc1.Enterprise_ID__c = '1023880950';
            objAcc1.General_Account__c = true;
            objAcc1.BirthDate__c = '11/27/2020';
            objAcc1.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get(System.label.HUMCOBMemberLabel).getRecordTypeId();
            objAcc1.Veteran_Status__c = 'unknown';
            insert objAcc1;
            System.assertEquals(objAcc1.Tenant_Id__c,'00');

            Interaction__c oInteraction = new Interaction__c(
            Interaction_Origin__c = 'Inbound Call',
            Interacting_With__c = objAcc.id,
            Interacting_With_type__c = 'Member');
            insert oInteraction;
            System.assertNotEquals(oInteraction.Id, NULL, 'Value not matched');

            Case oCase = HUMTestDataHelperTest.getCase();
            oCase.AccountId=objAcc.id;
            insert oCase;
            System.assertNotEquals(oCase.Id, NULL, 'Value not matched');
            
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
            
            Policy__c objpolPDP = HUMTestDataHelperTest.getPolicy();
            objpolPDP.Name='testPolicyMA';
            objpolPDP.Group_Name__c = objAcc.Id;
            objpolPDP.Major_LOB__c = 'MA';
            objpolPDP.Source_Cust_Cov_Key__c = '1023880951';
            objpolPDP.Product__c = oProdPDP.Id;
            objpolPDP.Product__r = oProdPDP;
            objpolPDP.Platform__c = 'LV';
            objpolPDP.Client_Number__c = '57';
            insert objpolPDP;
            system.assertEquals(objpolPDP.Source_Cust_Cov_Key__c,'1023880951');
            
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
            objpolMPD.Product__c = oProdPDP.Id;
            objpolMPD.Product__r = oProdPDP;
            objpolMPD.Platform__c = 'LV';
            objpolMPD.Client_Number__c = '57';
            objpolMPD.Contract_Number__c = 'H1036';
            objpolMPD.PBP_Code__c = '280';
            objpolMPD.Medicare_Segment_ID__c = '000';
            objpolMPD.Issue_State__c = 'FL';
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
            objPolMemMPD.Effective_Date__c=Date.today().addDays(200).format();
            objPolMemMPD.End_Date__c=Date.today().addDays(10).format();
            objPolMemMPD.Policy_Member_External_ID__c='00|ODS|6925004434817|12345678|34567|2011-07-01';
            objPolMemMPD.Member__c = objAcc.Id;
            objPolMemMPD.Policy__c = objpolMPD.Id;
            insert objPolMemMPD;
            system.assertEquals(objPolMemMPD.Relationship__c, 'Self');
            
            Policy_Member__c objTermPolMem = new Policy_Member__c();
            objTermPolMem.Name = 'testpolmem11';
            objTermPolMem.Relationship__c = 'Self';
            objTermPolMem.Relationship_Code__c = '00';
            objTermPolMem.Subscriber__c = objPolMem.Id;
            objTermPolMem.Effective_Date__c=Date.today().addDays(-10).format();
            objTermPolMem.End_Date__c=Date.today().addDays(10).format();
            objTermPolMem.Policy_Member_External_ID__c='00|ODS|6925004434817|12345678|34569|2013-07-01';
            objTermPolMem.Member__c = objAcc.Id;
            objTermPolMem.Policy__c = objpolMAPD.Id;
            objTermPolMem.Policy__r = objpolMAPD;
            insert objTermPolMem;
            system.assertEquals(objPolMem.Relationship__c, 'Self');
            
            Policy_Member__c objTermedPolMem = new Policy_Member__c();
            objTermedPolMem.Name = 'testpolmem11';
            objTermedPolMem.Relationship__c = 'Self';
            objTermedPolMem.Relationship_Code__c = '00';
            objTermedPolMem.Subscriber__c = objPolMem.Id;
            objTermedPolMem.Effective_Date__c=Date.today().addDays(-10).format();
            objTermedPolMem.End_Date__c=Date.today().addDays(-1).format();
            objTermedPolMem.Policy_Member_External_ID__c='00|ODS|6925004434817|12345678|34569|2014-07-01';
            objTermedPolMem.Member__c = objAcc.Id;
            objTermedPolMem.Policy__c = objpolMA.Id;
            objTermedPolMem.Policy__r = objpolMA;
            insert objTermedPolMem;
            system.assertEquals(objTermedPolMem.Relationship__c, 'Self');


            Policy_Member__c objTermedPolMem1 = new Policy_Member__c();
            objTermedPolMem1.Name = 'testpolmemNewMem';
            objTermedPolMem1.Relationship__c = 'Self';
            objTermedPolMem1.Relationship_Code__c = '00';
            objTermedPolMem1.Effective_Date__c=Date.today().addMonths(-12).format();      
            objTermedPolMem1.End_Date__c=Date.today().addMonths(-6).format();            
            objTermedPolMem1.Member__c = objAcc.Id;
            objTermedPolMem1.Policy__c = objpolMAPD.Id;
            objTermedPolMem1.Policy__r = objpolMAPD;
            insert objTermedPolMem1;
            system.assertEquals(objTermedPolMem.Relationship__c, 'Self');
            
            Classification_Type__c ctRec = New Classification_Type__c();
            ctRec.name = 'calls';
            ctRec.Is_Active__c=true;
            insert ctRec;
            
            Classification__c classificationRec = New Classification__c();
            classificationRec.Name = 'Fulfillment';
            classificationRec.Is_Active__c = true;
            insert classificationRec;
            
            Intent__c intentRec = New Intent__c();
            intentRec.Name = 'ID Card/Access Card Request';
            intentRec.Is_Active__c = true;
            insert intentRec;
            
            CTCI_Junction__c ctciRec = New CTCI_Junction__c();
            ctciRec.Classification_Type__c = ctRec.id;
            ctciRec.Classification__c = classificationRec.Id;
            ctciRec.Intent__c = intentRec.id;
            ctciRec.IsActive__c = true;
            insert ctciRec;
            
            Case caseRecord = HUMTestDataHelperTest.getCase();
            caseRecord.Owner_Queue__c = 'Louisville RSO Calls Support';
            caseRecord.AccountId = objAcc.id;
            caseRecord.ownerId = oCCSUser.Id;
            caseRecord.Status = 'In Progress';
            caseRecord.Classification_Type__c = 'calls';
            caseRecord.Classification_id__c= classificationRec.Id;
            caseRecord.Intent_id__c = intentRec.id;
            caseRecord.Interacting_With__c = objAcc.id;
            caseRecord.Interacting_With_Type__c = 'Member';
            caseRecord.Policy_Member__c = objPolMem.id;
            caseRecord.Origin = 'Inbound Call';
            insert caseRecord;
            
            List<MemberIcon_HUM__c> lstIcons = new List<MemberIcon_HUM__c>();
            lstIcons.Add(new MemberIcon_HUM__c(Name='NewMember',DisplayText__c='New Member',PageName__c='Member Account',PermissionSet__c = 'CRMS_684_Medicare_Customer_Service_Access',DocumentName__c='NewMemember',IsVisible__c=true));
            lstIcons.Add(new MemberIcon_HUM__c(Name='BirthDay',DisplayText__c='Happy Birthday',PageName__c='Member Account',DocumentName__c='BirthDay',IsVisible__c=true));
            lstIcons.Add(new MemberIcon_HUM__c(Name='Deceased',DisplayText__c='Deceased',PageName__c='Member Account',DocumentName__c='Deceased',IsVisible__c=true));
            lstIcons.Add(new MemberIcon_HUM__c(Name='GroupMedicare',DisplayText__c='Group Medicare',PageName__c='Member Account,Policy Member',DocumentName__c='GroupMedicare',IsVisible__c=true));
            lstIcons.Add(new MemberIcon_HUM__c(Name='MedicareSupplement',DisplayText__c='Medicare Supplement',PageName__c='Member Account,Policy Member',DocumentName__c='MedicareSupplement',IsVisible__c=true));
            lstIcons.Add(new MemberIcon_HUM__c(Name='H1036Contract',DisplayText__c='H1036 Contract',PageName__c='Member Account,Policy Member',DocumentName__c='H1036Contract',IsVisible__c=true));
            lstIcons.Add(new MemberIcon_HUM__c(Name='RepeatCaller',DisplayText__c='Repeat Caller',DocumentName__c='RepeatCaller',IsVisible__c=true,Order__c = '9',PageName__c='Member Account',PermissionSet__c='CRMS_684_Medicare_Customer_Service_Access'));
            lstIcons.Add(new MemberIcon_HUM__c(Name='HIDE',DisplayText__c='HIDE',DocumentName__c='HIDE',IsVisible__c=true,Order__c = '10',PageName__c='Member Account,Policy Member,Search',PermissionSet__c='CRMS_684_Medicare_Customer_Service_Access,CRMS_685_PCC_Customer_Service_Access'));          
            lstIcons.Add(new MemberIcon_HUM__c(Name='FIDE',DisplayText__c='FIDE',DocumentName__c='FIDE',IsVisible__c=true,Order__c = '11',PageName__c='Member Account,Policy Member,Search',PermissionSet__c='CRMS_684_Medicare_Customer_Service_Access,CRMS_685_PCC_Customer_Service_Access')); 
            lstIcons.Add(new MemberIcon_HUM__c(Name='VeteranIcon',DisplayText__c='Veteran',DocumentName__c='VeteranIcon',IsVisible__c=true,Order__c = '4',PageName__c='Member Account,Search',PermissionSet__c='')); 
	    lstIcons.Add(new MemberIcon_HUM__c(Name='IDCardRequest',DisplayText__c='ID Card Request',DocumentName__c='IDCardRequest',IsVisible__c=true,Order__c = '18',PageName__c='Policy Member',PermissionSet__c='CRMS_684_Medicare_Customer_Service_Access,CRMS_240_GBO_Segment_Service_Access,CRMS_205_CCSPDPPharmacyPilot')); 
            insert lstIcons;

            List<RepeatCallerSettings_HUM__c> lstRepeatCaller = new List<RepeatCallerSettings_HUM__c>();
            lstRepeatCaller.Add(new RepeatCallerSettings_HUM__c(Name='GBO',AmberColorCode__c='yellow',GreenColorCode__c='green',Interacting_With_type__c='Member,Member Representative or Caregiver',Interaction_Origin__c='Outbound Call',Is14DaysIntsVisible__c=true,IsActive__c=false,IsOpenCasesVisible__c=true,IsTodaysIntsVisible__c=true,OpenCases_RedRange__c='11',OpenCases_Amber_Range__c='10',OpenCases_Green_Range__c='5',Past14DaysInts_RedRange__c='11',Past14DaysInts_Amber_Range__c='10',Past14DaysInts_Green_Range__c='5',PermissionSet__c='CRMS_240_GBO_Segment_Service_Access',RedColorCode__c='red',TodaysInts_RedRange__c='8',TodaysInts_Amber_Range__c='7',TodaysInts_Green_Range__c='3'));
            lstRepeatCaller.Add(new RepeatCallerSettings_HUM__c(Name='RSO',AmberColorCode__c='yellow',GreenColorCode__c='green',Interacting_With_type__c='Member',Interaction_Origin__c='Inbound Call',Is14DaysIntsVisible__c=true,IsActive__c=true,IsOpenCasesVisible__c=true,IsTodaysIntsVisible__c=true,OpenCases_RedRange__c='11',OpenCases_Amber_Range__c='10',OpenCases_Green_Range__c='5',Past14DaysInts_RedRange__c='11',Past14DaysInts_Amber_Range__c='10',Past14DaysInts_Green_Range__c='5',PermissionSet__c='CRMS_684_Medicare_Customer_Service_Access',RedColorCode__c='red',TodaysInts_RedRange__c='8',TodaysInts_Amber_Range__c='7',TodaysInts_Green_Range__c='3'));
            lstRepeatCaller.Add(new RepeatCallerSettings_HUM__c(Name='Pharmacy Specialist',AmberColorCode__c='yellow',GreenColorCode__c='green',Interacting_With_type__c='Member',Interaction_Origin__c='Inbound Call',Is14DaysIntsVisible__c=true,IsActive__c=true,IsOpenCasesVisible__c=true,IsTodaysIntsVisible__c=true,OpenCases_RedRange__c='11',OpenCases_Amber_Range__c='10',OpenCases_Green_Range__c='5',Past14DaysInts_RedRange__c='11',Past14DaysInts_Amber_Range__c='10',Past14DaysInts_Green_Range__c='5',PermissionSet__c='Humana Pharmacy Specialist',RedColorCode__c='red',TodaysInts_RedRange__c='8',TodaysInts_Amber_Range__c='7',TodaysInts_Green_Range__c='3'));
            lstRepeatCaller.Add(new RepeatCallerSettings_HUM__c(Name='PDPPharmacyPilot',AmberColorCode__c='yellow',GreenColorCode__c='green',Interacting_With_type__c='Member Representative or Caregiver',Interaction_Origin__c='Inbound Call,Web Chat,Outbound Call',Is14DaysIntsVisible__c=true,IsActive__c=true,IsOpenCasesVisible__c=true,IsTodaysIntsVisible__c=true,OpenCases_RedRange__c='11',OpenCases_Amber_Range__c='10',OpenCases_Green_Range__c='5',Past14DaysInts_RedRange__c='11',Past14DaysInts_Amber_Range__c='10',Past14DaysInts_Green_Range__c='5',PermissionSet__c='CRMS_205_CCSPDPPharmacyPilot',RedColorCode__c='red',TodaysInts_RedRange__c='8',TodaysInts_Amber_Range__c='7',TodaysInts_Green_Range__c='3'));
            insert lstRepeatCaller;
            
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
            Account oAccount = [Select Id,Name,Birthdate__c,Deceased_Date__c,Tenant_Id__c,RecordType.Name,Enterprise_ID__c  from Account where LastName = 'Account'];  
            Policy_Member__c objPolMem = [Select Id,Status__c from Policy_Member__c Where Name = 'testpolmemMPD' Limit 1];
            Interaction__c oInteraction = [Select Id, Name, CreatedDate,Interaction_Origin__c, Interacting_With__c FROM Interaction__c];            
            Map<String,RepeatCallerSettings_HUM__c> mapRC = RepeatCallerSettings_HUM__c.getAll();
            
            
            System.assertEquals(oAccount.Tenant_Id__c, '00');
            String sAccRecType = oAccount.RecordType.Name;
            System.assertEquals(sAccRecType,'Member');
            
            MemberIcons_S_HUM.getPageIcons('Member Account');
            Boolean bVisibility = MemberIcons_S_HUM.getNewMemberIconStatus(oAccount.Id);
            
            bVisibility = MemberIcons_S_HUM.getGroupMedicareIconStatusForAccount(oAccount.Id);
            System.assertEquals(bVisibility ,true);
            
            bVisibility = MemberIcons_S_HUM.getGroupMedicareIconStatusForPolMember(objPolMem.Id);
            System.assertEquals(bVisibility ,true);
            
            bVisibility = MemberIcons_S_HUM.getMedicareSupplementIconStatusForAccount(oAccount.Id);
            System.assertEquals(bVisibility ,false);
            
            bVisibility = MemberIcons_S_HUM.getMedicareSupplementIconStatusForPolMember(objPolMem.Id);
            System.assertEquals(bVisibility ,false);
            
            
            bVisibility = MemberIcons_S_HUM.getBirthdayIconStatus(oAccount.Birthdate__c);
            
            bVisibility = MemberIcons_S_HUM.getDeceasedIconStatus('10/31/2020');
            System.assertEquals(bVisibility ,true);
            
            
            
            bVisibility = MemberIcons_S_HUM.getH1036ContractIconForAccount(oAccount.Id);
            System.assertEquals(bVisibility ,true);
            
            bVisibility = MemberIcons_S_HUM.getH1036ContractIconForPolMember(objPolMem.Id);
            System.assertEquals(bVisibility ,true);

            bVisibility = MemberIcons_S_HUM.getRepeatCallerIconStatus(oAccount.Id,oInteraction.id);
            System.assertEquals(bVisibility ,false);
            
            MemberIcons_S_HUM.bIsRecordsRetrieved = false;
      
            bVisibility = MemberIcons_S_HUM.getFIDEIconForAccount(oAccount.Id);
            System.assertEquals(bVisibility ,true);
            system.assertEquals(MemberIcons_S_HUM.bIsRecordsRetrieved, true);
           
            
            bVisibility = MemberIcons_S_HUM.getFIDEIconForPolMember(objPolMem.Id);
            System.assertEquals(bVisibility ,true);
      
            MemberIcons_S_HUM.bIsRecordsRetrieved = false;
      
            bVisibility = MemberIcons_S_HUM.getHIDEIconForAccount(oAccount.Id);
            System.assertEquals(bVisibility ,false);
            system.assertEquals(MemberIcons_S_HUM.bIsRecordsRetrieved, true);
            
            
            bVisibility = MemberIcons_S_HUM.getHIDEIconForPolMember(objPolMem.Id);
            System.assertEquals(bVisibility ,false);

            bVisibility = MemberIcons_S_HUM.getVeteranIconStatus(oAccount.Id);
            System.assertEquals(bVisibility ,true);
            
            bVisibility = MemberIcons_S_HUM.getIDCardIconForPolMember(objPolMem.Id);
            System.assertEquals(bVisibility ,false);
            
            bVisibility = MemberIcons_S_HUM.getLINETIconForAccount(oAccount.Id);
            System.assertEquals(bVisibility ,false);
            
            bVisibility = MemberIcons_S_HUM.getLINETIconForPolMember(objPolMem.Id,objPolMem.Status__c);
            System.assertEquals(bVisibility ,false);
            MemberIcons_S_HUM.checkIDCardIconActionalble(objPolMem.Id);
            
            List<MemberIcons_DTO_HUM.Icon_DTO_HUM> lstIcons = new List<MemberIcons_DTO_HUM.Icon_DTO_HUM>();
            MemberIcons_DTO_HUM.Icon_DTO_HUM objIcon = new MemberIcons_DTO_HUM.Icon_DTO_HUM();
            objIcon.sIconName = 'NewMember';
            objIcon.sDocumentId = '';
            objIcon.bIconVisible = true;
            objIcon.sIconLabel = 'New Member';
            objIcon.sPermissionSets = 'CRMS_684_Medicare_Customer_Service_Access';
            objIcon.sPageName = 'Member Account, Search';
            objIcon.sOrder = '2';
            lstIcons.Add(objIcon);
            
            objIcon = new MemberIcons_DTO_HUM.Icon_DTO_HUM();
            objIcon.sIconName = 'BirthDay';
            objIcon.sDocumentId = '';
            objIcon.bIconVisible = true;
            objIcon.sIconLabel = 'Happy Birthday';
            objIcon.sPermissionSets = '';
            objIcon.sPageName = 'Member Account, Search';
            objIcon.sOrder = '1';
            lstIcons.Add(objIcon);
            lstIcons = MemberIcons_S_HUM.sortIcons(lstIcons);
            System.assertEquals(lstIcons[0].sOrder,'1');
            
            

            RepeatCallerSettings_HUM__c objRC= MemberIcons_S_HUM.getRepeatCallerConfigDetailsForLoggedinUSer();
            
            RepeatCallerInformation_DTO_HUM objInfo = MemberIcons_S_HUM.getRepeatCallerInformation(oAccount.Id,'',true);
            System.assertNotEquals(objInfo,null);
            RepeatCallerInformation_DTO_HUM  objInfor = MemberIcons_S_HUM.executeUIDisplayLogicForRepeatCaller(objInfo,objRC);
            System.assertNotEquals(objInfor,null);
            Test.stopTest();           
        }
    }


    /*
    * Method Name : testMemberIcons
    * Description : This method is used to test main functionality.
    * Return type : void
    * Parameter   : NA
    */
    static testmethod void testNewMemberIconStatus() 
    {
        User oCCSUser3 = [Select id,UserName from User where LastName = 'test User3' LIMIT 1 ] ;
        
    
        System.runAs (oCCSUser3) 
        { 
            Test.StartTest();

            Product2 oProd = new Product2(Name = 'Laptop X200',Segment_Type__c='MCR',ProductCode='MED', Major_LOB__c='MER');
            insert oProd;
            System.assertNotEquals(oProd.Id, NULL, 'Value not matched');
            
            Product2 oProdMA = new Product2(Name = 'MED',Segment_Type__c='MCR',ProductCode='MA', Major_LOB__c='MA', Product_Type__c='MA');
            insert oProdMA;
            System.assertNotEquals(oProdMA.Id, NULL, 'Value not matched');
            
            Product2 oProdPDP = new Product2(Name = 'MED',Segment_Type__c='MCR',ProductCode='PDP', Major_LOB__c='PDP', Product_Type__c='PDP');
            insert oProdPDP;
            System.assertNotEquals(oProdPDP.Id, NULL, 'Value not matched');
            
            Product2 oProdMAPD = new Product2(Name = 'MED',Segment_Type__c='MCR',ProductCode='MAPD', Major_LOB__c='MAPD', Product_Type__c='MAPD');
            insert oProdMAPD;
            System.assertNotEquals(oProdMAPD.Id, NULL, 'Value not matched');
            
            Account objAcc = HUMTestDataHelperTest.getAccount(); 
            objAcc.FirstName = 'Test';
            objAcc.LastName = 'Account';
            objAcc.Tenant_Id__c = '00';
            objAcc.Enterprise_ID__c = '1023880950';
            objAcc.General_Account__c = true;
            ObjAcc.BirthDate__c = '11/15/2020';
            objAcc.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get(System.label.HUMCOBMemberLabel).getRecordTypeId();
            objAcc.Veteran_Status__c ='confirmed-veteran';    
            insert objAcc;
            System.assertEquals(objAcc.Tenant_Id__c,'00');

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
            
            Policy__c objpolPDP = HUMTestDataHelperTest.getPolicy();
            objpolPDP.Name='testPolicyMA';
            objpolPDP.Group_Name__c = objAcc.Id;
            objpolPDP.Major_LOB__c = 'MA';
            objpolPDP.Source_Cust_Cov_Key__c = '1023880951';
            objpolPDP.Product__c = oProdPDP.Id;
            objpolPDP.Product__r = oProdPDP;
            objpolPDP.Platform__c = 'LV';
            objpolPDP.Client_Number__c = '57';
            insert objpolPDP;
            system.assertEquals(objpolPDP.Source_Cust_Cov_Key__c,'1023880951');
            
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
            objpolMPD.Product__c = oProdPDP.Id;
            objpolMPD.Product__r = oProdPDP;
            objpolMPD.Platform__c = 'LV';
            objpolMPD.Client_Number__c = '57';
            objpolMPD.Contract_Number__c = 'H1036';
            objpolMPD.PBP_Code__c = '280';
            objpolMPD.Medicare_Segment_ID__c = '000';
            objpolMPD.Issue_State__c = 'FL';
            insert objpolMPD;
            system.assertEquals(objpolMPD.Source_Cust_Cov_Key__c,'1023880952');
            
            Policy_Member__c objPolMem = new Policy_Member__c();
            objPolMem.Name = 'testpolmem111';
            objPolMem.Relationship__c = 'Self';
            objPolMem.Relationship_Code__c = '00';
            objPolMem.Subscriber__c = objPolMem.Id;
            objPolMem.Effective_Date__c=Date.today().addDays(-1).format();
            objPolMem.End_Date__c=Date.today().addDays(1).format();
            objPolMem.Policy_Member_External_ID__c='00|ODS|6925004434817|12345678|34567|2022-07-01';
            objPolMem.Member__c = objAcc.Id;
            objPolMem.Policy__c = objpol.Id;
            objPolMem.Policy__r = objpol;
            insert objPolMem;
            system.assertEquals(objPolMem.Relationship__c, 'Self');
            
            Policy_Member__c objPolMemMPD = new Policy_Member__c();
            objPolMemMPD.Name = 'testpolmemMPD11';
            objPolMemMPD.Relationship__c = 'Self';
            objPolMemMPD.Relationship_Code__c = '00';
            objPolMemMPD.Subscriber__c = objPolMem.Id;
            objPolMemMPD.Effective_Date__c=Date.newinstance(2022, 1, 1).format();
            objPolMemMPD.End_Date__c=Date.newinstance(2022, 12, 31).format();
            objPolMemMPD.Policy_Member_External_ID__c='00|ODS|6925004434817|12345678|34567|2025-07-01';
            objPolMemMPD.Member__c = objAcc.Id;
            objPolMemMPD.Policy__c = objpolMPD.Id;
            insert objPolMemMPD;
            system.assertEquals(objPolMemMPD.Relationship__c, 'Self');
            
            Policy_Member__c objTermPolMem = new Policy_Member__c();
            objTermPolMem.Name = 'testpolmem1112';
            objTermPolMem.Relationship__c = 'Self';
            objTermPolMem.Relationship_Code__c = '00';
            objTermPolMem.Subscriber__c = objPolMem.Id;
            objTermPolMem.Effective_Date__c=Date.today().addDays(-10).format();
            objTermPolMem.End_Date__c=Date.today().addDays(10).format();
            objTermPolMem.Policy_Member_External_ID__c='00|ODS|6925004434817|12345678|34569|2022-07-01';
            objTermPolMem.Member__c = objAcc.Id;
            objTermPolMem.Policy__c = objpolMAPD.Id;
            objTermPolMem.Policy__r = objpolMAPD;
            insert objTermPolMem;
            system.assertEquals(objPolMem.Relationship__c, 'Self');
            
            Policy_Member__c objTermedPolMem = new Policy_Member__c();
            objTermedPolMem.Name = 'testpolmem11312';
            objTermedPolMem.Relationship__c = 'Self';
            objTermedPolMem.Relationship_Code__c = '00';
            objTermedPolMem.Subscriber__c = objPolMem.Id;
            objTermedPolMem.Effective_Date__c=Date.today().addDays(-10).format();
            objTermedPolMem.End_Date__c=Date.today().addDays(-1).format();
            objTermedPolMem.Policy_Member_External_ID__c='00|ODS|6925004434817|12345678|34569|2022-01-01';
            objTermedPolMem.Member__c = objAcc.Id;
            objTermedPolMem.Policy__c = objpolMA.Id;
            objTermedPolMem.Policy__r = objpolMA;
            insert objTermedPolMem;
            system.assertEquals(objTermedPolMem.Relationship__c, 'Self');


            Policy_Member__c objTermedPolMem1 = new Policy_Member__c();
            objTermedPolMem1.Name = 'testpolmemNewMem1';
            objTermedPolMem1.Relationship__c = 'Self';
            objTermedPolMem1.Relationship_Code__c = '00';
            objTermedPolMem1.Effective_Date__c=Date.today().addMonths(-12).format();      
            objTermedPolMem1.End_Date__c=Date.today().addMonths(-6).format();            
            objTermedPolMem1.Member__c = objAcc.Id;
            objTermedPolMem1.Policy__c = objpolMAPD.Id;
            objTermedPolMem1.Policy__r = objpolMAPD;
            insert objTermedPolMem1;
            system.assertEquals(objTermedPolMem.Relationship__c, 'Self');
            
            
            

            Account oAccount = [Select Id,Name,Birthdate__c,Deceased_Date__c,Tenant_Id__c,RecordType.Name,Enterprise_ID__c  from Account where LastName = 'Account'];  
            System.debug('Test Account is ' + oAccount);


            MemberIcons_S_HUM.getPageIcons('Member Account');
            Boolean bVisibility = MemberIcons_S_HUM.getNewMemberIconStatus(oAccount.Id);



            Account objAcc1 = HUMTestDataHelperTest.getAccount(); 
            objAcc1.FirstName = 'Test';
            objAcc1.LastName = 'AccountName2';
            objAcc1.Tenant_Id__c = '00';
            objAcc1.Enterprise_ID__c = '1023880951';
            objAcc1.General_Account__c = true;
            objAcc1.BirthDate__c = '11/27/2020';
            objAcc1.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get(System.label.HUMCOBMemberLabel).getRecordTypeId();
            objAcc1.Veteran_Status__c = 'unknown';
            insert objAcc1;
            System.assertEquals(objAcc1.Tenant_Id__c,'00');

            Policy__c objpolMA12 = HUMTestDataHelperTest.getPolicy();
            objpolMA12.Name='testPolicyMA12';
            objpolMA12.Group_Name__c = objAcc1.Id;
            objpolMA12.Major_LOB__c = 'MA';
            objpolMA12.Source_Cust_Cov_Key__c = '1023880951';
            objpolMA12.Product__c = oProdMA.Id;
            objpolMA12.Product__r = oProdMA;
            objpolMA12.Platform__c = 'LV';
            objpolMA12.Client_Number__c = '57';
            insert objpolMA12;
            system.assertEquals(objpolMA12.Source_Cust_Cov_Key__c,'1023880951');

            Policy_Member__c objPolMemMA1 = new Policy_Member__c();
            objPolMemMA1.Name = 'testpolmemMPD112';
            objPolMemMA1.Relationship__c = 'Self';
            objPolMemMA1.Relationship_Code__c = '00';
            objPolMemMA1.Subscriber__c = objPolMem.Id;
            objPolMemMA1.Effective_Date__c=Date.newinstance(2022, 1, 1).format();
            objPolMemMA1.End_Date__c=Date.newinstance(2022, 12, 31).format();
            objPolMemMA1.Policy_Member_External_ID__c='00|ODS|6925004434817|24567834|34567|2022-07-01';
            objPolMemMA1.Member__c = objAcc1.Id;
            objPolMemMA1.Policy__c = objpolMA12.Id;
            insert objPolMemMA1;
            system.assertEquals(objPolMemMA1.Relationship__c, 'Self');


            Policy_Member__c objTermedPolMem12 = new Policy_Member__c();
            objTermedPolMem12.Name = 'testpolmem11312';
            objTermedPolMem12.Relationship__c = 'Self';
            objTermedPolMem12.Relationship_Code__c = '00';
            objTermedPolMem12.Subscriber__c = objPolMem.Id;
            objTermedPolMem12.Effective_Date__c=Date.newinstance(2022, 1, 1).addDays(-10).format();
            objTermedPolMem12.End_Date__c=Date.newinstance(2022, 12, 31).addDays(-10).format();
            objTermedPolMem12.Policy_Member_External_ID__c='00|ODS|6925004434817|12345678|34569|2022-31-31';
            objTermedPolMem12.Member__c = objAcc1.Id;
            objTermedPolMem12.Policy__c = objpolMA12.Id;
           
            insert objTermedPolMem12;
            system.assertEquals(objTermedPolMem12.Relationship__c, 'Self');

            Account oAccount1 = [Select Id,Name,Birthdate__c,Deceased_Date__c,Tenant_Id__c,RecordType.Name,Enterprise_ID__c  from Account where LastName = 'AccountName2'];  
            System.debug('Test Account is ' + oAccount1);


            MemberIcons_S_HUM.getPageIcons('Member Account');
            MemberIcons_S_HUM.getNewMemberIconStatus(oAccount1.Id);

            

            Test.stopTest();

        }
    }



        
       
}
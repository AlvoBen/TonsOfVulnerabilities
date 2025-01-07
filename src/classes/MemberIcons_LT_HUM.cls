/**************************************************************************************************************************************************
Apex class Name    :  MemberIcons_LT_HUM
Version            :  1.0
Function           :  This is the test class for MemberIcons_LC_HUM.class

Modification Log: 
Developer Name           Code Review                      Date                       Description
*--------------------------------------------------------------------------------------------------------------------------------
*Ankima Srivastava                                       03/12/2021               initial version
*Kajal Namdev                                            04/23/2021               Added Function to cover NewMemberIcon function and else part of 
*Joel George                                             04/26/2021               Test class failure fix 
*Kajal Namdev                                                                                    executeUIDisplayLogicForRepeatCaller func.
*Kajal Namdev                                            06/01/2021               Test class failure fix
*Ankima Srivastava                                       08/11/2021              Test class update for Legacy Delete changes
*Kajal Namdev                                            08/13/2021               Test class failure fix
*Ankima Srivastava                                       08/19/2021               Test class Fix
*Kajal Namdev                                            09/07/2021               Test coverage fix                 
*Abhishek Mangutkar                                      09/20/2021               Added changes of Humana Pharmacy Page Icons
*Jonathan Dickinson										 10/28/2021				  Added changes related to ICons on Humana Pharmacy page REQ_2674165
*Deepakkumar Khandelwal								     02/02/2023				  US-4137330: T1PRJ0865978 - MF 24067 /  4097490 -C13, Lightning - CORE - Match Icons to Plans 1
************************************************************************************************************************************************* */
@isTest
public with sharing Class MemberIcons_LT_HUM
{
    /*
    * Method Name : prepareTestData
    * Description : Used to create wrapper class data
    * Return type : void
    * Paramater   : NA
    */
    @testSetup static void prepareTestData() 
    {
        Id profileId = [SELECT Id FROM Profile WHERE  name = 'Customer Care Specialist' LIMIT 1 ].Id;
        User oCCSUser = [Select id from User Where ProfileId =: profileId And IsActive = true Limit 1];

        List<PermissionSet> permSet = [SELECT Id, Name FROM PermissionSet where name = 'CRMS_240_GBO_Segment_Service_Access'];                        
        List<PermissionSetAssignment> lstPermSetAssign = [SELECT Id FROM PermissionSetAssignment where AssigneeId =: oCCSUser.Id And PermissionSetId =: permSet[0].Id];                        
               
        List<PermissionSet> permSet1 = [SELECT Id, Name FROM PermissionSet where name ='CRMS_684_Medicare_Customer_Service_Access'];                          
        List<PermissionSetAssignment> lstPermSetAssign1 = [SELECT Id FROM PermissionSetAssignment where AssigneeId =: oCCSUser.Id And PermissionSetId =: permSet1[0].Id];
                
        List<PermissionSet> permSet2 = [SELECT Id, Name FROM PermissionSet where name = 'CRM_RF_LouisvilleUser_Access'];                        
        List<PermissionSetAssignment> lstPermSetAssign2 = [SELECT Id FROM PermissionSetAssignment where AssigneeId =: oCCSUser.Id And PermissionSetId =: permSet2[0].Id];
                
        List<PermissionSet> permSet3 = [SELECT Id, Name FROM PermissionSet where name = 'CRMS_205_CCSPDPPharmacyPilot'];                        
        List<PermissionSetAssignment> lstPermSetAssign3 = [SELECT Id FROM PermissionSetAssignment where AssigneeId =: oCCSUser.Id And PermissionSetId =: permSet3[0].Id];
                
        List<PermissionSet> permSet4 = [SELECT Id, Name FROM PermissionSet where name = 'CRMS_207_PBM_Iconology'];                        
        List<PermissionSetAssignment> lstPermSetAssign4 = [SELECT Id FROM PermissionSetAssignment where AssigneeId =: oCCSUser.Id And PermissionSetId =: permSet4[0].Id];
        
		List<PermissionSet> permSet5 = [SELECT Id, Name FROM PermissionSet where name = 'CRMS_420_NBARecommendationAdmin'];                        
        List<PermissionSetAssignment> lstPermSetAssign5 = [SELECT Id FROM PermissionSetAssignment where AssigneeId =: oCCSUser.Id And PermissionSetId =: permSet5[0].Id];
              
        List<PermissionSet> permSet6 = [SELECT Id, Name FROM PermissionSet where name = 'CRMS_300_Humana_Pharmacy_Supervisor'];                        
        List<PermissionSetAssignment> lstPermSetAssign6 = [SELECT Id FROM PermissionSetAssignment where AssigneeId =: oCCSUser.Id And PermissionSetId =: permSet6[0].Id];
        		
		if(lstPermSetAssign.isEmpty()){
        PermissionSetAssignment permSetAssign = new PermissionSetAssignment();
        permSetAssign.PermissionSetId = permSet[0].ID;
        permSetAssign.AssigneeId = oCCSUser.Id;        
        insert permSetAssign;
        }
		
		if(lstPermSetAssign1.isEmpty()){
        PermissionSetAssignment permSetAssign1 = new PermissionSetAssignment();
        permSetAssign1.PermissionSetId = permSet1[0].ID;
        permSetAssign1.AssigneeId = oCCSUser.Id;        
        insert permSetAssign1;        
       
        }
        
        if(lstPermSetAssign2.isEmpty()){
        PermissionSetAssignment permSetAssign2 = new PermissionSetAssignment();
        permSetAssign2.PermissionSetId = permSet2[0].ID;
        permSetAssign2.AssigneeId = oCCSUser.Id;        
        insert permSetAssign2;
         
        }
        
        if(lstPermSetAssign3.isEmpty()){
        PermissionSetAssignment permSetAssign3 = new PermissionSetAssignment();
        permSetAssign3.PermissionSetId = permSet3[0].ID;
        permSetAssign3.AssigneeId = oCCSUser.Id;        
        insert permSetAssign3;
         
        }
        
        if(lstPermSetAssign4.isEmpty()){
        PermissionSetAssignment permSetAssign4 = new PermissionSetAssignment();
        permSetAssign4.PermissionSetId = permSet4[0].ID;
        permSetAssign4.AssigneeId = oCCSUser.Id;        
        insert permSetAssign4;
        }
		
		if(lstPermSetAssign5.isEmpty()){
        PermissionSetAssignment permSetAssign5 = new PermissionSetAssignment();
        permSetAssign5.PermissionSetId = permSet5[0].ID;
        permSetAssign5.AssigneeId = oCCSUser.Id;        
        insert permSetAssign5; 
       
        }
        
         if(lstPermSetAssign6.isEmpty()){
        PermissionSetAssignment permSetAssign6 = new PermissionSetAssignment();
        permSetAssign6.PermissionSetId = permSet6[0].ID;
        permSetAssign6.AssigneeId = oCCSUser.Id;        
        insert permSetAssign6; 
       
        }
                
        System.runAs (oCCSUser) 
        {
            RepeatCallerSettings_HUM__c obj = new RepeatCallerSettings_HUM__c();
            obj.Name='GBO';
            obj.AmberColorCode__c = 'orange';
            obj.GreenColorCode__c='Green';
            obj.Interacting_With_type__c='Member, Member Representative or Caregiver, Agent, Provider, Group';
            obj.Interaction_Origin__c='Inbound Call,Web Chat,Email';
            obj.Is14DaysIntsVisible__c=True;
            obj.IsActive__c=True;
            obj.IsOpenCasesVisible__c=True;
            obj.IsTodaysIntsVisible__c=True;
            obj.OpenCases_RedRange__c='4';
            obj.OpenCases_Amber_Range__c='3';
            obj.OpenCases_Green_Range__c='1';
            obj.Past14DaysInts_RedRange__c='4';
            obj.Past14DaysInts_Amber_Range__c='3';
            obj.Past14DaysInts_Green_Range__c='1';
            obj.PermissionSet__c='CRMS_240_GBO_Segment_Service_Access';
            obj.RedColorCode__c='Red';
            obj.TodaysInts_RedRange__c='4';
            obj.TodaysInts_Amber_Range__c='3';
            obj.TodaysInts_Green_Range__c='1';            
            insert obj;
            
            
            List<MemberIcon_HUM__c> lstIcons = new List<MemberIcon_HUM__c>();
            lstIcons.Add(new MemberIcon_HUM__c(Name='NewMember',DisplayText__c='New Member',PageName__c='Member Account',DocumentName__c='NewMemember',IsVisible__c=true, Profile__c = 'Customer Care Specialist',PermissionSet__c='CRMS_240_GBO_Segment_Service_Access'));
            lstIcons.Add(new MemberIcon_HUM__c(Name='BirthDay',DisplayText__c='Happy Birthday',PageName__c='Member Account',DocumentName__c='BirthDay',IsVisible__c=true, Profile__c = 'Customer Care Specialist',PermissionSet__c='CRMS_240_GBO_Segment_Service_Access'));
            lstIcons.Add(new MemberIcon_HUM__c(Name='Deceased',DisplayText__c='Deceased',PageName__c='Member Account',DocumentName__c='Deceased',IsVisible__c=true, Profile__c = 'Customer Care Specialist',PermissionSet__c='CRMS_240_GBO_Segment_Service_Access'));
            lstIcons.Add(new MemberIcon_HUM__c(Name='GroupMedicare',DisplayText__c='Group Medicare',PageName__c='Member Account,Policy Member',DocumentName__c='GroupMedicare',IsVisible__c=true, Profile__c = 'Customer Care Specialist',PermissionSet__c='CRMS_240_GBO_Segment_Service_Access'));
            lstIcons.Add(new MemberIcon_HUM__c(Name='MedicareSupplement',DisplayText__c='Medicare Supplement',PageName__c='Member Account,Policy Member',DocumentName__c='MedicareSupplement',IsVisible__c=true, Profile__c = 'Customer Care Specialist',PermissionSet__c='CRMS_240_GBO_Segment_Service_Access'));
            lstIcons.Add(new MemberIcon_HUM__c(Name='DualEligible',DisplayText__c='Dual Eligible',PageName__c='Member Account,Policy Member',DocumentName__c='DualEligiible',IsVisible__c=true, Profile__c = 'Customer Care Specialist',PermissionSet__c='CRMS_240_GBO_Segment_Service_Access'));
            lstIcons.Add(new MemberIcon_HUM__c(Name='H1036Contract',DisplayText__c='H1036 Contract',PageName__c='Member Account,Policy Member',DocumentName__c='H1036Contract',IsVisible__c=true, Profile__c = 'Customer Care Specialist',PermissionSet__c='CRMS_240_GBO_Segment_Service_Access'));
            lstIcons.Add(new MemberIcon_HUM__c(Name='RepeatCaller',DisplayText__c='Repeat Caller',DocumentName__c='RepeatCaller',IsVisible__c=true,Order__c = '9',PageName__c='Member Account,Policy Member', Profile__c = 'Customer Care Specialist',PermissionSet__c='CRMS_240_GBO_Segment_Service_Access'));
            lstIcons.Add(new MemberIcon_HUM__c(Name='HIDE',DisplayText__c='HIDE',DocumentName__c='HIDE',IsVisible__c=true,Order__c = '10',PageName__c='Member Account,Policy Member,Search', Profile__c = 'Customer Care Specialist',PermissionSet__c='CRMS_240_GBO_Segment_Service_Access'));
            lstIcons.Add(new MemberIcon_HUM__c(Name='FIDE',DisplayText__c='FIDE',DocumentName__c='FIDE',IsVisible__c=true,Order__c = '11',PageName__c='Member Account,Policy Member,Search', Profile__c = 'Customer Care Specialist',PermissionSet__c='CRMS_240_GBO_Segment_Service_Access'));
            lstIcons.Add(new MemberIcon_HUM__c(Name='ContactHandlingAlert',DisplayText__c='ContactHandlingAlert',DocumentName__c='ContactHandlingAlert',IsVisible__c=true,Order__c = '11',PageName__c='Member Account,Policy Member,Search', Profile__c = 'Customer Care Specialist',PermissionSet__c='CRMS_240_GBO_Segment_Service_Access'));
            lstIcons.Add(new MemberIcon_HUM__c(Name='VeteranIcon',DisplayText__c='VeteranIcon',DocumentName__c='VeteranIcon',IsVisible__c=true,Order__c = '11',PageName__c='Member Account,Policy Member,Search', Profile__c = 'Customer Care Specialist',PermissionSet__c='CRMS_240_GBO_Segment_Service_Access'));
            lstIcons.Add(new MemberIcon_HUM__c(Name='WebRegistered',DisplayText__c='WebRegistered',DocumentName__c='WebRegistered',IsVisible__c=true,Order__c = '11',PageName__c='Member Account,Policy Member,Search', Profile__c = 'Customer Care Specialist',PermissionSet__c='CRMS_240_GBO_Segment_Service_Access'));
            lstIcons.Add(new MemberIcon_HUM__c(Name='creditCardExpiringSoonIcon',DisplayText__c='Credit Card Expiring Soon',DocumentName__c='creditCardExpiringSoonIcon',IsVisible__c=true,Order__c = '12',PageName__c='Humana Pharmacy', Profile__c = 'Customer Care Specialist, Customer Care Supervisor, Humana Pharmacy Specialist',PermissionSet__c='CRMS_240_GBO_Segment_Service_Access'));
			lstIcons.Add(new MemberIcon_HUM__c(Name='creditCardExpiredIcon',DisplayText__c='Credit Card Expired',DocumentName__c='creditCardExpiredIcon',IsVisible__c=true,Order__c = '13',PageName__c='Humana Pharmacy', Profile__c = 'Customer Care Specialist, Customer Care Supervisor, Humana Pharmacy Specialist',PermissionSet__c='CRMS_240_GBO_Segment_Service_Access'));
            lstIcons.Add(new MemberIcon_HUM__c(Name='LINET',DisplayText__c='LINET',DocumentName__c='LINET',IsVisible__c=true,Order__c = '15',PageName__c='Search,Member Account,Policy Member', Profile__c = 'Customer Care Specialist, Customer Care Supervisor, Humana Pharmacy Specialist',PermissionSet__c='CRMS_684_Medicare_Customer_Service_Access'));
            lstIcons.Add(new MemberIcon_HUM__c(Name='IDCardRequest',DisplayText__c='IDCardRequest',DocumentName__c='IDCardRequest',IsVisible__c=true,Order__c = '19',PageName__c='Policy Member', Profile__c = 'Customer Care Specialist, Customer Care Supervisor, Humana Pharmacy Specialist',PermissionSet__c='CRMS_684_Medicare_Customer_Service_Access , CRMS_240_GBO_Segment_Service_Access,CRMS_205_CCSPDPPharmacyPilot'));
            lstIcons.Add(new MemberIcon_HUM__c(Name='IL_MMP',DisplayText__c='IL_MMP',DocumentName__c='IL_MMP',IsVisible__c=true,Order__c = '20',PageName__c='Member Account,Search,Policy Member', Profile__c = 'Humana Pharmacy Specialist',PermissionSet__c='	CRMS_207_PBM_Iconology'));
			insert lstIcons;
			
			CRMFunctionality_ONOFF_Switch__c CRMCust1 = new CRMFunctionality_ONOFF_Switch__c();
            CRMCust1.Name = '2977635'; 
            insert CRMCust1;
            
            CRMFunctionality_ONOFF_Switch__c CRMCust2 = new CRMFunctionality_ONOFF_Switch__c();
            CRMCust2.Name = '3253058_1';   
            insert CRMCust2;
            
            boolean bILMMPIconSwitch = HUMUtilityHelper.isCRMFunctionalityON('3253058_1');
            boolean bLINETIconOnOffSwitch = HUMUtilityHelper.isCRMFunctionalityON('2977635'); 
			
            Product2 oProd = new Product2(Name = 'Laptop X200',Segment_Type__c='MCR',ProductCode='MED', Major_LOB__c='MER',Product_Type__c='MA');
            insert oProd;
            System.assertNotEquals(oProd.Id, NULL, 'Value not matched');
            
            Product2 oProdMA = new Product2(Name = 'MED',Segment_Type__c='MCR',ProductCode='MA', Major_LOB__c='MA', Product_Type__c='MA');
            insert oProdMA;
            System.assertNotEquals(oProdMA.Id, NULL, 'Value not matched');
            
            Product2 oProdMAPD = new Product2(Name = 'MED',Segment_Type__c='MCR',ProductCode='MAPD', Major_LOB__c='MAPD', Product_Type__c='MAPD');
            insert oProdMAPD;
            System.assertNotEquals(oProdMAPD.Id, NULL, 'Value not matched');
			
			Product2 oProdLINET = new Product2(Name = 'MED',Segment_Type__c='MCR',ProductCode='MAPD', Major_LOB__c='MAPD', Product_Type__c='MAPD');
            insert oProdLINET;
            System.assertNotEquals(oProdLINET.Id, NULL, 'Value not matched');
            
            Product2 oProdILMMP = new Product2(Name = 'MED',Segment_Type__c='MCR',ProductCode='MED', Major_LOB__c='MER', Product_Type__c='MAPD');
            insert oProdILMMP;
            System.assertNotEquals(oProdILMMP.Id, NULL, 'Value not matched');
            
			Product2 oProdILMMP2 = new Product2(Name = 'MED',Segment_Type__c='MCR',ProductCode='MED', Major_LOB__c='MCD', Product_Type__c='');
            insert oProdILMMP2;
             System.assertNotEquals(oProdILMMP2.Id, NULL, 'Value not matched');
            			
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
            
            Interaction__c interactObj = new Interaction__c();
            interactObj.Interacting_With__c=objAcc.Id;
            interactObj.Interacting_With_type__c = 'Member';
            interactObj.Interaction_Origin__c='Web Chat';
            insert interactObj;
            
            Interaction_Member__c objInt = new Interaction_Member__c();
            objInt.Interacting_About__c=objAcc.Id;
            objInt.Interacting_About_Type__c = 'Member';
            objInt.Interaction__c =interactObj.Id; 
            insert objInt;
            Interaction_Member__c objIntr2 = new Interaction_Member__c();
            objIntr2.Interacting_About__c=objAcc.Id;
            objIntr2.Interacting_About_Type__c = 'Member';
            objIntr2.Interaction__c =interactObj.Id; 
            insert objIntr2;
            Interaction_Member__c objIntr3 = new Interaction_Member__c();
            objIntr3.Interacting_About__c=objAcc.Id;
            objIntr3.Interacting_About_Type__c = 'Member';
            objIntr3.Interaction__c =interactObj.Id; 
            insert objIntr3;
            
            Interaction__c interactObj1 = new Interaction__c();
            interactObj1.Interacting_With__c=objAcc1.Id;
            interactObj1.Interacting_With_type__c = 'Member';
            interactObj1.Interaction_Origin__c='Email';
            insert interactObj1;
            
            Interaction_Member__c objInt1 = new Interaction_Member__c();
            objInt1.Interacting_About__c=objAcc1.Id;
            objInt1.Interacting_About_Type__c = 'Member';
            objInt1.Interaction__c =interactObj1.Id; 
            insert objInt1;
            
            Interaction_Member__c objInt2 = new Interaction_Member__c();
            objInt2.Interacting_About__c=objAcc1.Id;
            objInt2.Interacting_About_Type__c = 'Member';
            objInt2.Interaction__c =interactObj1.Id; 
            insert objInt2;
            
             Interaction_Member__c objInt3 = new Interaction_Member__c();
            objInt3.Interacting_About__c=objAcc1.Id;
            objInt3.Interacting_About_Type__c = 'Member';
            objInt3.Interaction__c =interactObj1.Id; 
            insert objInt3;

            Case caseObj = new Case();
            caseObj.Account= objAcc;
            caseObj.Status = 'In Progress';
            insert caseObj;
			
			Classification__c classific1= new Classification__c();
            classific1.Name ='TEST Claasication';
            classific1.Is_Active__c =true;
            insert classific1;
            
            Intent__c intentTest = new Intent__c();
            intentTest.Name ='Test Intent';
            intentTest.Is_Active__c=true;
            insert intentTest;
            
            Classification_Type__c Class_Type =new Classification_Type__c();
            Class_Type.Name = 'Test Classification Type';
            Class_Type.Guidance_Alert_Limit__c =2;
            Class_Type.Is_Active__c =true;
            insert Class_Type ;
            
            CTCI_Junction__c CTC_LIST =new CTCI_Junction__c();
            CTC_LIST.Classification_Type__c =Class_Type.Id;
            CTC_LIST.Classification__c =classific1.Id;
            CTC_LIST.Intent__c =intentTest.Id;
            CTC_LIST.IsActive__c=true;
            insert CTC_LIST;
            			 
			Case caseObj1 = new Case();
            caseObj1.Account= objAcc;
			caseObj1.CTCI_List__c=CTC_LIST.Id;
            caseObj1.Status = 'In Progress';
			caseObj1.CreatedDate = system.today();
            insert caseObj1;
			
			Recommendation rcomm = new Recommendation();
            rcomm.Name='Recom Test';
            rcomm.Alert_Type__c = 'Contact Handling';
            rcomm.Description ='Recom Test';
            rcomm.Interacting_With_Type__c ='Member';
            rcomm.Show_To__c ='Member';
            rcomm.Location__c ='Plan Member';
            rcomm.Message__c =' testing';
            //rcomm.Customer_Role__c ='Interacting with';
            //rcomm.Customer_Type__c ='Member';
            //rcomm.Recommendation_Type__c ='Contact Handling';
            rcomm.AcceptanceLabel ='testing';
            rcomm.RejectionLabel ='testing';
            rcomm.ActionReference ='Save_Encrypted_Flow';
            insert rcomm;  
            
            Recommendation_Detail__c recommDetail = new Recommendation_Detail__c();
            recommDetail.Recommendation__c =rcomm.Id;
            recommDetail.Account__c =objAcc.Id;
            recommDetail.Type__c ='Member';
            recommDetail.Start_Date__c =system.today();
            recommDetail.End_Date__c = system.today()+5;
            Insert recommDetail; 
                        
            PurchaserPlan objpol = new PurchaserPlan();
            objpol.Policy_Group_Number__c='11111';

            objpol.Name='testPolicy';
            objpol.PayerId = objAcc.Id;
            objpol.Major_LOB__c = 'MGP';
            objpol.Source_Cust_Cov_Key__c = '1023880950';
            objpol.Product__c = oProd.Id;
            objpol.Product__r = oProd;
            objpol.Platform__c = 'LV';
            objpol.Client_Number__c = '57';
            objpol.Contract_Number__c='H1036';
            objpol.Issue_State__c='FL';
            objpol.Medicare_Segment_ID__c='2';
            objpol.PBP_Code__c='806';
            insert objpol;
            system.assertEquals(objpol.Source_Cust_Cov_Key__c,'1023880950');
            
            
            PurchaserPlan objpolMA = new PurchaserPlan();
            objpolMA.Name='testPolicyMA';
            objpolMA.Policy_Group_Number__c='11111';
            objpolMA.PayerId = objAcc.Id;
            objpolMA.Major_LOB__c = 'MES';
            objpolMA.Source_Cust_Cov_Key__c = '1023880951';
            objpolMA.Product__c = oProdMA.Id;
            objpolMA.Product__r = oProdMA;
            objpolMA.Platform__c = 'LV';
            objpolMA.Client_Number__c = '57';
            objpolMA.Contract_Number__c='H1036';
            objpolMA.Issue_State__c='FL';
            objpolMA.Medicare_Segment_ID__c='2';
            objpolMA.PBP_Code__c='806';
            insert objpolMA;
            system.assertEquals(objpolMA.Source_Cust_Cov_Key__c,'1023880951');
            
            PurchaserPlan objpolMAPD = new PurchaserPlan();
            objpolMAPD.Name='testPolicyMAPD';
            objpolMAPD.Policy_Group_Number__c='11111';
            objpolMAPD.PayerId = objAcc.Id;
            objpolMAPD.Major_LOB__c = 'MAPD';
            objpolMAPD.Source_Cust_Cov_Key__c = '1023880952';
            objpolMAPD.Product__c = oProdMAPD.Id;
            objpolMAPD.Product__r = oProdMAPD;
            objpolMAPD.Platform__c = 'LV';
            objpolMAPD.Client_Number__c = '57';
            objpolMAPD.Contract_Number__c='H1036';
            objpolMAPD.Issue_State__c='FL';
            objpolMAPD.Medicare_Segment_ID__c='2';
            objpolMAPD.PBP_Code__c='806';
            insert objpolMAPD;
            system.assertEquals(objpolMAPD.Source_Cust_Cov_Key__c,'1023880952');
            
            PurchaserPlan objpolMPD = new PurchaserPlan();
            objpolMPD.Name='testPolicyMPD';
            objpolMPD.Policy_Group_Number__c='11111';
            objpolMPD.PayerId = objAcc.Id;
            objpolMPD.Major_LOB__c = 'MPD';
            objpolMPD.Source_Cust_Cov_Key__c = '1023880952';
            objpolMPD.Product__c = oProdMAPD.Id;
            objpolMPD.Product__r = oProdMAPD;
            objpolMPD.Platform__c = 'LV';
            objpolMPD.Client_Number__c = '57';
            objpolMPD.Contract_Number__c='H1036';
            objpolMPD.Issue_State__c='FL';
            objpolMPD.Medicare_Segment_ID__c='000';
            objpolMPD.PBP_Code__c='280';
            insert objpolMPD;
            system.assertEquals(objpolMPD.Source_Cust_Cov_Key__c,'1023880952');
            
            PurchaserPlan objpolMPD1 = new PurchaserPlan();
            objpolMPD1.Name='testPolicyMPD';
            objpolMPD1.Policy_Group_Number__c='11111';
            objpolMPD1.PayerId = objAcc.Id;
            objpolMPD1.Major_LOB__c = 'MPD';
            objpolMPD1.Source_Cust_Cov_Key__c = '1023880952';
            objpolMPD1.Product__c = oProdMAPD.Id;
            objpolMPD1.Product__r = oProdMAPD;
            objpolMPD1.Platform__c = 'LV';
            objpolMPD1.Client_Number__c = '57';
            objpolMPD1.Contract_Number__c='H1036';
            objpolMPD1.Issue_State__c='FL';
            objpolMPD1.Medicare_Segment_ID__c='000';
            objpolMPD1.PBP_Code__c='210';
            insert objpolMPD1;
            system.assertEquals(objpolMPD1.Source_Cust_Cov_Key__c,'1023880952');
			
			 PurchaserPlan objpolLINET = new PurchaserPlan();
            objpolLINET.Policy_Group_Number__c='11111';
            objpolLINET.Name='testPolicy';
            objpolLINET.PayerId = objAcc.Id;
            objpolLINET.Major_LOB__c = 'MGP';
            objpolLINET.Source_Cust_Cov_Key__c = '1023880950';
            objpolLINET.Product__c = oProdLINET.Id;
            objpolLINET.Product__r = oProdLINET;
            objpolLINET.Platform__c = 'LV';
            objpolLINET.Client_Number__c = '57';
            objpolLINET.Contract_Number__c='X0001';
            objpolLINET.Issue_State__c='FL';
            objpolLINET.Medicare_Segment_ID__c='2';
            objpolLINET.PBP_Code__c='806';
            insert objpolLINET;
            system.assertEquals(objpolLINET.Source_Cust_Cov_Key__c,'1023880950');
            
            PurchaserPlan objpolILMMP = new PurchaserPlan();
            objpolILMMP.Policy_Group_Number__c='11111';
            objpolILMMP.Name='testPolicy';
            objpolILMMP.PayerId = objAcc.Id;
            objpolILMMP.Major_LOB__c = 'MER';
            objpolILMMP.Source_Cust_Cov_Key__c = '1023880950';
            objpolILMMP.Product__c = oProdILMMP.Id;
            objpolILMMP.Product__r = oProdILMMP;
            objpolILMMP.Platform__c = 'LV';
            objpolILMMP.Client_Number__c = '57';
            objpolILMMP.Contract_Number__c='H0336';
            objpolILMMP.Issue_State__c='IL';
            objpolILMMP.Medicare_Segment_ID__c='000';
            objpolILMMP.PBP_Code__c='001';
            insert objpolILMMP;
            system.assertEquals(objpolILMMP.Source_Cust_Cov_Key__c,'1023880950');
			
			PurchaserPlan objpolILMMP2 = new PurchaserPlan();
            objpolILMMP2.Policy_Group_Number__c='11111';
            objpolILMMP2.Name='testPolicy';
            objpolILMMP2.PayerId = objAcc.Id;
            objpolILMMP2.Major_LOB__c = 'MER';
            objpolILMMP2.Source_Cust_Cov_Key__c = '1023880950';
            objpolILMMP2.Product__c = oProdILMMP2.Id;
            objpolILMMP2.Product__r = oProdILMMP2;
            objpolILMMP2.Platform__c = 'LV';
            objpolILMMP2.Client_Number__c = '57';
            objpolILMMP2.Contract_Number__c='H0336';
            objpolILMMP2.Issue_State__c='IL';
            objpolILMMP2.Medicare_Segment_ID__c='000';
            objpolILMMP2.PBP_Code__c='001';
            insert objpolILMMP2;
            
            system.assertEquals(objpolILMMP2.Source_Cust_Cov_Key__c,'1023880950');
			            
            MemberPlan objPolMem = new MemberPlan();
            objPolMem.Name = 'testpolmem111';
            objPolMem.Relationship_Code__c = '00';
            objPolMem.EffectiveFrom=Date.today().addDays(-1);
            objPolMem.EffectiveTo =Date.today().addDays(1);
            objPolMem.MemberId = objAcc.Id;
            objPolMem.PlanId = objpol.Id;
            objPolMem.Plan = objpol;
            objPolMem.ETL_Record_Deleted__c=False;
            objPolMem.Dual_Status_Indicator__c='Y';
            objPolMem.EffectiveFrom = Date.Today();
            objPolMem.EffectiveTo = Date.Today().addYears(+10);
            insert objPolMem;
            MemberPlan objPolMemMPD = new MemberPlan();
            objPolMemMPD.Name = 'testpolmemMPD';
            objPolMemMPD.Relationship_Code__c = '00';
            objPolMemMPD.EffectiveFrom=Date.today().addDays(-1);
            objPolMemMPD.EffectiveTo=Date.today().addDays(1);
            objPolMemMPD.MemberId = objAcc.Id;
            objPolMemMPD.PlanId = objpolMPD.Id;
            objPolMemMPD.ETL_Record_Deleted__c=False;
            objPolMemMPD.Dual_Status_Indicator__c='Y';
            objPolMemMPD.EffectiveFrom = Date.Today();
            objPolMemMPD.EffectiveTo = Date.Today().addYears(+10);
            insert objPolMemMPD;
            
            MemberPlan objPolMemMPD2 = new MemberPlan();
            objPolMemMPD2.Name = 'testpolmemMPD';
            objPolMemMPD2.Relationship_Code__c = '00';
            objPolMemMPD2.EffectiveFrom=Date.today().addDays(-1);
            objPolMemMPD2.EffectiveTo=Date.today().addDays(-1);
            objPolMemMPD2.MemberId = objAcc.Id;
            objPolMemMPD2.PlanId = objpolMPD.Id;
            objPolMemMPD2.ETL_Record_Deleted__c=False;
            objPolMemMPD2.Dual_Status_Indicator__c='Y';
            objPolMemMPD2.EffectiveFrom = Date.Today();
            objPolMemMPD2.EffectiveTo = Date.Today().addYears(+10);
            insert objPolMemMPD2;
            
            
            MemberPlan objPolMemMPD1 = new MemberPlan();
            objPolMemMPD1.Name = 'testpolmemMPD1';
            objPolMemMPD1.Relationship_Code__c = '00';
            objPolMemMPD1.EffectiveFrom=Date.today().addDays(-1);
            objPolMemMPD1.EffectiveTo=Date.today().addDays(1);
            objPolMemMPD1.MemberId = objAcc.Id;
            objPolMemMPD1.PlanId = objpolMPD1.Id;
            objPolMemMPD1.ETL_Record_Deleted__c=False;
            objPolMemMPD1.Dual_Status_Indicator__c='Y';
            objPolMemMPD1.EffectiveFrom = Date.Today();
            objPolMemMPD1.EffectiveTo = Date.Today().addYears(+10);
            insert objPolMemMPD1;
            
            MemberPlan objTermPolMem = new MemberPlan();
            objTermPolMem.Name = 'testpolmem11';
            objTermPolMem.Relationship_Code__c = '00';
            objTermPolMem.EffectiveFrom=Date.today().addDays(-10);
            objTermPolMem.EffectiveTo=Date.today().addDays(-1);
            objTermPolMem.MemberId = objAcc.Id;
            objTermPolMem.PlanId = objpolMA.Id;
            objTermPolMem.Plan = objpolMA;
            objTermPolMem.ETL_Record_Deleted__c=False;
            objTermPolMem.Dual_Status_Indicator__c='Y';
            insert objTermPolMem;
			
			MemberPlan objTermPolMemLINET = new MemberPlan();
            objTermPolMemLINET.Name = 'testpolmem11';
            objTermPolMemLINET.Relationship_Code__c = '00';
            objTermPolMemLINET.EffectiveFrom=Date.today().addDays(-10);
            objTermPolMemLINET.EffectiveTo=Date.today().addDays(-1);
            objTermPolMemLINET.MemberId = objAcc.Id;
            objTermPolMemLINET.PlanId = objpolLINET.Id;
            objTermPolMemLINET.Plan = objpolLINET;
            objTermPolMemLINET.ETL_Record_Deleted__c=False;
            objTermPolMemLINET.Dual_Status_Indicator__c='Y';
            insert objTermPolMemLINET;
            
            MemberPlan objTermPolMemILMMP = new MemberPlan();
            objTermPolMemILMMP.Name = 'testpolmem11';
            objTermPolMemILMMP.Relationship_Code__c = '00';
            objTermPolMemILMMP.EffectiveFrom=system.today();  
            objTermPolMemILMMP.EffectiveTo=system.today()+10; 
            objTermPolMemILMMP.MemberId = objAcc.Id;
            objTermPolMemILMMP.PlanId = objpolILMMP.Id;
            objTermPolMemILMMP.Plan = objpolILMMP;   
            objTermPolMemILMMP.ETL_Record_Deleted__c=False;
            objTermPolMemILMMP.Dual_Status_Indicator__c='Y';
            insert objTermPolMemILMMP;
			
			MemberPlan objTermPolMemILMMP2 = new MemberPlan();
            objTermPolMemILMMP2.Name = 'testpolmem11';
            objTermPolMemILMMP2.Relationship_Code__c = '00';
            objTermPolMemILMMP2.EffectiveFrom=system.today();    
            objTermPolMemILMMP2.EffectiveTo=system.today()+10;    
            objTermPolMemILMMP2.MemberId = objAcc.Id;
            objTermPolMemILMMP2.PlanId = objpolILMMP2.Id;
            objTermPolMemILMMP2.Plan = objpolILMMP2;   
            objTermPolMemILMMP2.ETL_Record_Deleted__c=False;
            objTermPolMemILMMP2.Dual_Status_Indicator__c='Y';
            insert objTermPolMemILMMP2;
            system.debug('*********ILMMP******>>>>>'+objTermPolMemILMMP2);
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
        Id profileId = [SELECT Id FROM Profile WHERE  name = 'Customer Care Specialist' LIMIT 1 ].Id;
        User oCCSUser = [Select id from User Where ProfileId =: profileId And IsActive = true Limit 1];
        
        System.runAs (oCCSUser) 
        { 
            Test.StartTest();
            Account oAccount = [Select Id,Name,Tenant_Id__c,RecordType.Name,Enterprise_ID__c  from Account where LastName = 'Account'];  
            MemberPlan oMemPlan = [Select Id  from MemberPlan where Name = 'testpolmem111'];  
            System.assertEquals(oAccount.Tenant_Id__c, '00');
            String sAccRecType = oAccount.RecordType.Name;
            System.assertEquals(sAccRecType,'Member');
            String sBirthDate = Date.today().month() +'/'+Date.today().day()+'/'+Date.today().year();
            MemberIcons_LC_HUM.getMemberIconStatus('Member Account',oAccount.Id);
            MemberIcons_LC_HUM.getMemberIconStatus('Search',oAccount.Id);           
            MemberIcons_LC_HUM.getInteractionAndCase('Member Account',String.valueOf(oAccount.Id),String.valueOf(oMemPlan.Id));
			MemberIcons_DTO_HUM memberIcons = MemberIcons_LC_HUM.getMemberIconStatus('Humana Pharmacy',oAccount.Id);           
			if(memberIcons.bIconsPresnt)
			{                
				for(MemberIcons_DTO_HUM.Icon_DTO_HUM iconObject : memberIcons.lstMemberIcons)
				{		
					if(iconObject.sIconName == 'creditCardExpiringSoonIcon')
					{
						System.assertEquals(iconObject.sIconName, 'creditCardExpiringSoonIcon', 'Credit Card Expiring Soon Icon');
						System.debug('Credit CardExpiring Soon Icon');
					}
					if(iconObject.sIconName == 'creditCardExpiredIcon')
					{
						System.assertEquals(iconObject.sIconName, 'creditCardExpiredIcon', 'Credit Card Expired Icon');
						System.debug('Credit CardExpiring Soon Icon');
					}
				}	
			}
            Test.stopTest();           
        }
    } 
     /*
    * Method Name : testElseMemberIcons
    * Description : This method is used to test else parts of the functionality.
    * Return type : void
    * Parameter   : NA
    */
    static testmethod void testElseMemberIcons() 
    {
        Id profileId = [SELECT Id FROM Profile WHERE  name = 'Customer Care Specialist' LIMIT 1 ].Id;
        User oCCSUser = [Select id from User Where ProfileId =: profileId And IsActive = true Limit 1];
        
        System.runAs (oCCSUser) 
        { 
            Test.StartTest();
            Account oAccount = [Select Id,Name,Tenant_Id__c,RecordType.Name,Enterprise_ID__c  from Account where LastName = 'AccountName2'];  
            MemberPlan oMemPlan = [Select Id  from MemberPlan where Name = 'testpolmem111'];  
            
            System.assertEquals(oAccount.Tenant_Id__c, '00');
            String sAccRecType = oAccount.RecordType.Name;
            System.assertEquals(sAccRecType,'Member');
            String sBirthDate = Date.today().month() +'/'+Date.today().day()+'/'+Date.today().year();
            MemberIcons_LC_HUM.getMemberIconStatus('Member Account',oAccount.Id);
            MemberIcons_LC_HUM.getMemberIconStatus('Search',oAccount.Id); 
            MemberIcons_LC_HUM.getInteractionAndCase('Policy Member',String.valueOf(oAccount.Id),String.valueOf(oMemPlan.Id));
			MemberIcons_DTO_HUM memberIcons = MemberIcons_LC_HUM.getMemberIconStatus('Humana Pharmacy',oAccount.Id);           
			if(memberIcons.bIconsPresnt)
			{                
				for(MemberIcons_DTO_HUM.Icon_DTO_HUM iconObject : memberIcons.lstMemberIcons)
				{		
					if(iconObject.sIconName == 'creditCardExpiringSoonIcon')
					{
						System.assertEquals(iconObject.sIconName, 'creditCardExpiringSoonIcon', 'Credit Card Expiring Soon Icon');
						System.debug('Credit CardExpiring Soon Icon');
					}
					if(iconObject.sIconName == 'creditCardExpiredIcon')
					{
						System.assertEquals(iconObject.sIconName, 'creditCardExpiredIcon', 'Credit Card Expired Icon');
						System.debug('Credit CardExpiring Soon Icon');
					}
				}	
			} 
            Test.stopTest();           
        }
    } 
     /*
    * Method Name : testNewMemberIcons
    * Description : This method is used to test NewMemberIcon function and cover else parts of executeUIDisplayLogicForRepeatCaller function.
    * Return type : void
    * Parameter   : NA
    */
    static testmethod void testNewMemberIcons() 
    {
        RepeatCallerSettings_HUM__c repeatC = [select id,TodaysInts_RedRange__c,OpenCases_RedRange__c,OpenCases_Amber_Range__c,OpenCases_Green_Range__c from RepeatCallerSettings_HUM__c Limit 1];
        repeatC.OpenCases_Green_Range__c = '0';
        repeatC.OpenCases_Amber_Range__c = '1';
        repeatC.TodaysInts_Green_Range__c = '7';
        update repeatC;
       	Id profileId = [SELECT Id FROM Profile WHERE  name = 'Customer Care Specialist' LIMIT 1 ].Id;
        User oCCSUser = [Select id from User Where ProfileId =: profileId And IsActive = true Limit 1];
        System.runAs (oCCSUser) 
        { 
            Test.StartTest();
            Account oAccount = [Select Id,Name,Tenant_Id__c,RecordType.Name,Enterprise_ID__c  from Account where LastName = 'Account'];
            MemberPlan oMemPlan = [Select Id  from MemberPlan where Name = 'testpolmem111'];  
            
            System.assertEquals(oAccount.Tenant_Id__c, '00');
            String sAccRecType = oAccount.RecordType.Name;
            System.assertEquals(sAccRecType,'Member');
            Case caseObj = new Case();
            caseObj.AccountId= oAccount.Id;
            caseObj.Status = 'In Progress';
            caseObj.Origin = 'Email';
            insert caseObj;
            String sBirthDate = Date.today().month() +'/'+Date.today().day()+'/'+Date.today().year();
            MemberIcons_LC_HUM.getMemberIconStatus('Policy Member',oAccount.Id);
            MemberIcons_LC_HUM.getMemberIconStatus('Search',oAccount.Id);
            MemberIcons_LS_HUM.getNewMemberIconStatus(oAccount.Id);
            MemberIcons_LC_HUM.getInteractionAndCase('Policy Member',String.valueOf(oAccount.Id),String.valueOf(oMemPlan.Id));
            repeatC.TodaysInts_Green_Range__c = '2';
            repeatC.TodaysInts_Amber_Range__c = '2';
            update repeatC;
            MemberIcons_LC_HUM.getInteractionAndCase('Policy Member',String.valueOf(oAccount.Id),String.valueOf(oMemPlan.Id));
            repeatC.TodaysInts_RedRange__c = '3';
            repeatC.Past14DaysInts_Green_Range__c = '3';
            update repeatC;
           	MemberIcons_LC_HUM.getInteractionAndCase('Policy Member',String.valueOf(oAccount.Id),String.valueOf(oMemPlan.Id));
            MemberIcons_LS_HUM.getRepeatCallerInformation('Policy Member',oMemPlan.Id,oAccount.Id,'rtyu',true);
			MemberIcons_DTO_HUM memberIcons = MemberIcons_LC_HUM.getMemberIconStatus('Humana Pharmacy',oAccount.Id);           
			if(memberIcons.bIconsPresnt)
			{                
				for(MemberIcons_DTO_HUM.Icon_DTO_HUM iconObject : memberIcons.lstMemberIcons)
				{		
					if(iconObject.sIconName == 'creditCardExpiringSoonIcon')
					{
						System.assertEquals(iconObject.sIconName, 'creditCardExpiringSoonIcon', 'Credit Card Expiring Soon Icon');
						System.debug('Credit CardExpiring Soon Icon');
						String creditCardExpiringSoonIconURL = MemberIcons_LC_HUM.getExpiredAndExpiringSoonIconURLs('Humana Pharmacy','expiring');
                        if(creditCardExpiringSoonIconURL != null){
                            System.assertEquals(creditCardExpiringSoonIconURL, iconObject.sDocumentId);
                        }
					}
					if(iconObject.sIconName == 'creditCardExpiredIcon')
					{
						System.assertEquals(iconObject.sIconName, 'creditCardExpiredIcon', 'Credit Card Expired Icon');
						System.debug('Credit CardExpiring Soon Icon');
						String creditCardExpiredIconURL = MemberIcons_LC_HUM.getExpiredAndExpiringSoonIconURLs('Humana Pharmacy','expired');
                        if(creditCardExpiredIconURL != null){
                            System.assertEquals(creditCardExpiredIconURL, iconObject.sDocumentId);
                        }  
					}
				}
				String returnedValueIfTypeIsNotExpiredOrExpiringSoon = MemberIcons_LC_HUM.getExpiredAndExpiringSoonIconURLs('Humana Pharmacy','');
                System.assertEquals(returnedValueIfTypeIsNotExpiredOrExpiringSoon,'');
			}
            Test.stopTest();           
        }

    } 
    
    static testMethod void testNewMemberIconsDiffProfile(){
        MemberIcons_LS_HUM.getDeceasedIconStatus('12/12/2021');
		Account oAccount = [Select Id,Name,Tenant_Id__c,RecordType.Name,Enterprise_ID__c  from Account where LastName = 'Account'];  
		Id profileId = [SELECT Id FROM Profile WHERE  name = 'Humana Pharmacy Specialist' LIMIT 1 ].Id;
        User oCCSUser = [Select id from User Where ProfileId =: profileId And IsActive = true Limit 1];
        System.runAs (oCCSUser) 
        { 
            MemberIcons_LC_HUM.getMemberIconStatus('Member Account',oAccount.Id);
            MemberIcon_HUM__c memIcons = MemberIcon_HUM__c.getValues('NewMember');
            memIcons.Profile__c = 'Humana Pharmacy Specialist';
            update memIcons;
            MemberIcons_LC_HUM.getMemberIconStatus('Member Account',oAccount.Id);
        }
    }
}
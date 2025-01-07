/**************************************************************************************************************************************************
Apex class Name    :  MemberIconsConfiguration_CT_HUM
Version            :  1.0
Function           :  This is the test class for MemberIconsConfiguration_C_HUM class

   Modification Log:
    
 *   Developer                   Code Review             Date               Description
 * -------------------------------------------------------------------------------------------------------------------------------------------------  
 *  Santhi Mandava                                       10/14/2018         Original Version
 *  Amit Agarwal                                         06/25/2020         User Story 1186331: PR00091574 - MF 1 - Quality - Ability to display Repeat Caller Icon on search page and Repeat Caller Information on the Member Account page (RF)
************************************************************************************************************************************************* */
@isTest
Public Class MemberIconsConfiguration_CT_HUM
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
        
        System.runAs (oCCSUser) 
        {
            List<MemberIcon_HUM__c> lstIcons = new List<MemberIcon_HUM__c>();
            lstIcons.Add(new MemberIcon_HUM__c(Name='NewMember',DisplayText__c='New Member',PermissionSet__c='CRMS_684_Medicare_Customer_Service_Access',PageName__c='Member Account',DocumentName__c='NewMemember',IsVisible__c=true));
            lstIcons.Add(new MemberIcon_HUM__c(Name='BirthDay',DisplayText__c='Happy Birthday',PageName__c='Member Account',DocumentName__c='BirthDay',IsVisible__c=true));
            lstIcons.Add(new MemberIcon_HUM__c(Name='Deceased',DisplayText__c='Deceased',PageName__c='Member Account',DocumentName__c='Deceased',IsVisible__c=true));
            lstIcons.Add(new MemberIcon_HUM__c(Name='GroupMedicare',DisplayText__c='Group Medicare',PageName__c='Member Account,Policy Member',DocumentName__c='GroupMedicare',IsVisible__c=true));
            lstIcons.Add(new MemberIcon_HUM__c(Name='MedicareSupplement',DisplayText__c='Medicare Supplement',PageName__c='Member Account,Policy Member',DocumentName__c='MedicareSupplement',IsVisible__c=true));
            lstIcons.Add(new MemberIcon_HUM__c(Name='DualEligible',DisplayText__c='Dual Eligible',PageName__c='Member Account,Policy Member',DocumentName__c='DualEligiible',IsVisible__c=true));
            lstIcons.Add(new MemberIcon_HUM__c(Name='H1036Contract',DisplayText__c='H1036 Contract',PageName__c='Member Account,Policy Member',DocumentName__c='H1036Contract',IsVisible__c=true));
            lstIcons.Add(new MemberIcon_HUM__c(Name='RepeatCaller',DisplayText__c='Repeat Caller',DocumentName__c='RepeatCaller',IsVisible__c=true,Order__c = '9',PageName__c='Member Account',PermissionSet__c='CRMS_684_Medicare_Customer_Service_Access'));
            insert lstIcons;

            List<RepeatCallerSettings_HUM__c> lstRepeatCaller = new List<RepeatCallerSettings_HUM__c>();
            lstRepeatCaller.Add(new RepeatCallerSettings_HUM__c(Name='GBO',AmberColorCode__c='yellow',GreenColorCode__c='green',Interacting_With_type__c='Member,Member Representative or Caregiver',Interaction_Origin__c='Outbound Call',Is14DaysIntsVisible__c=true,IsActive__c=false,IsOpenCasesVisible__c=true,IsTodaysIntsVisible__c=true,OpenCases_RedRange__c='11',OpenCases_Amber_Range__c='10',OpenCases_Green_Range__c='5',Past14DaysInts_RedRange__c='11',Past14DaysInts_Amber_Range__c='10',Past14DaysInts_Green_Range__c='5',PermissionSet__c='CRMS_240_GBO_Segment_Service_Access',RedColorCode__c='red',TodaysInts_RedRange__c='8',TodaysInts_Amber_Range__c='7',TodaysInts_Green_Range__c='3'));
            lstRepeatCaller.Add(new RepeatCallerSettings_HUM__c(Name='RSO',AmberColorCode__c='yellow',GreenColorCode__c='green',Interacting_With_type__c='Member',Interaction_Origin__c='Inbound Call',Is14DaysIntsVisible__c=true,IsActive__c=true,IsOpenCasesVisible__c=true,IsTodaysIntsVisible__c=true,OpenCases_RedRange__c='11',OpenCases_Amber_Range__c='10',OpenCases_Green_Range__c='5',Past14DaysInts_RedRange__c='11',Past14DaysInts_Amber_Range__c='10',Past14DaysInts_Green_Range__c='5',PermissionSet__c='CRMS_684_Medicare_Customer_Service_Access',RedColorCode__c='red',TodaysInts_RedRange__c='8',TodaysInts_Amber_Range__c='7',TodaysInts_Green_Range__c='3'));
            lstRepeatCaller.Add(new RepeatCallerSettings_HUM__c(Name='Pharmacy Specialist',AmberColorCode__c='yellow',GreenColorCode__c='green',Interacting_With_type__c='Member',Interaction_Origin__c='Inbound Call',Is14DaysIntsVisible__c=true,IsActive__c=true,IsOpenCasesVisible__c=true,IsTodaysIntsVisible__c=true,OpenCases_RedRange__c='11',OpenCases_Amber_Range__c='10',OpenCases_Green_Range__c='5',Past14DaysInts_RedRange__c='11',Past14DaysInts_Amber_Range__c='10',Past14DaysInts_Green_Range__c='5',PermissionSet__c='Humana Pharmacy Specialist',RedColorCode__c='red',TodaysInts_RedRange__c='8',TodaysInts_Amber_Range__c='7',TodaysInts_Green_Range__c='3'));
            lstRepeatCaller.Add(new RepeatCallerSettings_HUM__c(Name='PDPPharmacyPilot',AmberColorCode__c='yellow',GreenColorCode__c='green',Interacting_With_type__c='Member Representative or Caregiver',Interaction_Origin__c='Inbound Call,Web Chat,Outbound Call',Is14DaysIntsVisible__c=true,IsActive__c=true,IsOpenCasesVisible__c=true,IsTodaysIntsVisible__c=true,OpenCases_RedRange__c='11',OpenCases_Amber_Range__c='10',OpenCases_Green_Range__c='5',Past14DaysInts_RedRange__c='11',Past14DaysInts_Amber_Range__c='10',Past14DaysInts_Green_Range__c='5',PermissionSet__c='CRMS_205_CCSPDPPharmacyPilot',RedColorCode__c='red',TodaysInts_RedRange__c='8',TodaysInts_Amber_Range__c='7',TodaysInts_Green_Range__c='3'));
            insert lstRepeatCaller;
            RepeatCallerConfiguration_DTO_HUM objSelectedRCDTO = new RepeatCallerConfiguration_DTO_HUM(lstRepeatCaller[1]);
            system.assertNotEquals(objSelectedRCDTO,null);
            
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
            MemberIconsConfiguration_C_HUM objClass = new MemberIconsConfiguration_C_HUM();
            ApexPages.currentPage().getParameters().put('selectedIcon', 'NewMember');
            objClass.displayIconDetails();
            System.assertEquals(objClass.objSelectedIconDto.sName,'NewMember');
            
            ApexPages.currentPage().getParameters().put('selectedPages', 'Member Account');
            ApexPages.currentPage().getParameters().put('selectedPermissions', '');
            ApexPages.currentPage().getParameters().put('selectedProfiles', 'Customer Care Specialist');
            objClass.saveIconData();
            objClass.displayIconDetails();
            //System.assertEquals(objClass.objSelectedIconDto.sProfile,'Customer Care Specialist');

            ApexPages.currentPage().getParameters().put('selectedRepeatCaller', 'RepeatCaller');
            objClass.sSelectedInteractionOrigin = 'Inbound Call';
            objClass.sSelectedInteractingWithType = 'Member';
            
            objClass.saveRC();
            System.assertNotEquals(objClass.mapRepeatCallerDTO,null);
            System.assertEquals(objClass.bIsRCDetailsSectionVisible,false);

            objClass.cancel();
            System.assertEquals(objClass.bIsDetailsSectionVisible,false);

            objClass.cancelRC();
            System.assertEquals(objClass.bIsRCTableSectionVisible,true);
            Test.StopTest();
        }
    }
}
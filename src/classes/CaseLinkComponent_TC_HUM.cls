/*****************************************************************************************
Apex class Name    : CaseLinkComponent_TC_HUM
Version            : 
Created Date       : 11/03/2022 

Modification Log:
*  Developer                   Code Review             Date               Description
* ------------------------------------------------------------------------------------------------------------------------------   
*  Dinesh Subramaniyan 								11/04/2022			Initial Version
*********************************************************************************************************************************/
@isTest
public with sharing class CaseLinkComponent_TC_HUM {
    public CaseLinkComponent_TC_HUM() {

    }
    
        /*
     * Method Name : setup
     * Description : Create common test Data
     * Return type : void
     * Paramater   : NA
     */ 
    @testSetup static void setup()
    {
        User oUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        oUser.Current_Queue__c = 'Cincinnati CRU';
        oUser.Network_User_Id__c = 'SXS2222';
        insert oUser;
        
        User oUser1 = HUMTestDataHelperTest.createTestUser('User2','Customer Care Supervisor');
        oUser1.Current_Queue__c = 'Cincinnati Home Office Calls';
        oUser1.Network_User_Id__c = 'SXS2222';
        insert oUser1;
        System.assertEquals(oUser.LastName, 'test User1');
        
        User oUser2 = HUMTestDataHelperTest.createTestUser('User3','Humana Pharmacy Specialist');
        oUser2.Current_Queue__c = 'Cincinnati Home Office Calls';
        oUser2.Network_User_Id__c = 'SXS2222';
        insert oUser2;
               
        User oUser3 = HUMTestDataHelperTest.createTestUser('User4','Humana Pharmacy Specialist');
        oUser3.Current_Queue__c = 'Cincinnati Home Office Calls';
        oUser3.Network_User_Id__c = 'SXS2222';
        insert oUser3;
        
        PermissionSet ps = [Select Id, name from PermissionSet where name ='CRMS_300_Humana_Pharmacy_Supervisor'];
        
        PermissionSetAssignment psa = New PermissionSetAssignment();
        psa.AssigneeId = oUser3.Id;
        psa.PermissionSetId = ps.Id;
        insert psa;
        
        system.assertEquals(psa.PermissionSetId,ps.Id);
        
        System.runAs(oUser)
        {
            
            Classification_Type__c objcT = New Classification_Type__c();
            objcT.Name ='CallRSOpp';
            objcT.Is_Active__c = true;
            insert objcT;
            System.assertEquals(objcT.Name,'CallRSOpp');
            
            List<Work_Queue_Setup__c> oQSetup = New List<Work_Queue_Setup__c>();
            oQSetup.add(New Work_Queue_Setup__c(Name='Cincinnati CRU',Classification_Type__c=objcT.id));
            oQSetup.add(New Work_Queue_Setup__c(Name='Clinical Intake',Classification_Type__c=objcT.id));
            oQSetup.add(New Work_Queue_Setup__c(Name='Dental Calls',Classification_Type__c =objct.id));
            
            insert oQSetup ;
            system.assertNotEquals(Null , oQSetup[0].id);
            
            List<Account> oAcc = New List<Account>();
            oAcc.add( New Account(Group_Number__c='11111',BillingState='New York',Name = 'TestAccount1',recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Group').getRecordTypeId(),Source_Platform_Code__c = 'CA'));
            oAcc.add(New Account(Group_Number__c='13211',BillingState='Florida',Name = 'Account1',recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Group').getRecordTypeId(),Source_Platform_Code__c = 'CA'));
            oAcc.add(New Account(Group_Number__c='21181',BillingState='New York',Name = 'TestAcc',recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Group').getRecordTypeId(),Source_Platform_Code__c = 'CA'));
            insert oAcc;                        
            system.assertNotEquals(null , oAcc[0].Id, 'Value not matched'); 
           
            Policy__c oPolicy1 = New Policy__c();
            oPolicy1.Name ='test123';
            oPolicy1.Group_Name__c= oAcc[0].Id;
            oPolicy1.Policy_Group_Number__c='11111';
            oPolicy1.Funding_Type_Code__c = 'Y';
            oPolicy1.Issue_State__c = 'IL';  
            insert oPolicy1;            
            system.assertNotEquals(oPolicy1, null, 'Value not matched');
            System.assertEquals(oPolicy1.Funding_Type_Code__c, 'Y');
            
            
            List<Intent__c> oIntent= New List<Intent__c>();
            oIntent.add(New Intent__c(Name='Filling'));
            oIntent.add(New Intent__c(Name='payment to humana'));
            oIntent.add(New Intent__c(Name='Intial Build Request'));
            insert oIntent;
            system.assertNotEquals(Null , oIntent[0].id);
            System.assertEquals(oIntent.size(),3);
            
            
            
            Policy_Member__c oPolMem1 = New Policy_Member__c();
            oPolMem1.Relationship__c = 'Self';
            oPolMem1.Effective_Date__c=Date.today().addDays(-1).format();
            oPolMem1.End_Date__c=Date.today().addDays(1).format();
            oPolMem1.Name = '12345';
            oPolMem1.Member__c = oAcc[0].Id;
            oPolMem1.Policy__c = oPolicy1.Id;            
            oPolMem1.Relationship__c = 'Self';            
            insert oPolMem1;            
            system.assertNotEquals(oPolMem1, null, 'Value not matched');
            System.assertEquals(oPolMem1.Relationship__c, 'Self');
            Classification__c classificationRec = New Classification__c();
            classificationRec.Name = 'Claimskk';
            classificationRec.Is_Active__c = true;
            insert classificationRec;
            System.assertEquals(classificationRec.Name,'Claimskk');
            Intent__c intentRec = New Intent__c();
            intentRec.Name = 'Fillingkk';
            intentRec.Is_Active__c = true;
            insert intentRec;
            System.assertEquals(intentRec.Name,'Fillingkk');
           
            
            CTCI_Junction__c objCTCI = New CTCI_Junction__c();
            objCTCI.Classification__c=classificationRec.id;
            objCTCI.Classification_Type__c= objcT.id;
            objCTCI.Intent__c =intentRec.id;
            objCTCI.IsActive__c=true;
            insert objCTCI ;
            system.assertNotEquals(Null,objCTCI.id);
            
            List<Case> lstCases = New List<Case>();
            
            lstCases.Add(New Case(AccountId=oAcc[0].Id, Policy_Member__c= oPolMem1.Id ,
                             Interacting_With_Type__c = 'Group',CTCI_List__c= objCTCI.id, Interacting_With__c=oAcc[0].Id, Due_Date__c=Date.Today(),
                             Case_Owner__c=UserInfo.getUserName(), Owner_Queue__c='Cincinnati CRU', Created_By_Queue__c='Clinical Intake',
                             Service_Center__c ='Cincinnati', Department__c='CRU', status='In Progress'));
            
            lstCases.Add(New Case(AccountId=oAcc[1].Id, Policy_Member__c= oPolMem1.Id,
                             Interacting_With_Type__c = 'Group',CTCI_List__c= objCTCI.id, Interacting_With__c=oAcc[1].Id, Due_Date__c=Date.Today(),
                             Case_Owner__c=UserInfo.getUserName(), Owner_Queue__c='Cincinnati CRU',Created_By_Queue__c='Clinical Intake', 
                             Service_Center__c ='Cincinnati', Department__c='CRU', status='In Progress'));
            
            lstCases.Add(New Case(AccountId=oAcc[2].Id, Policy_Member__c= oPolMem1.Id ,
                              Interacting_With_Type__c = 'Group',CTCI_List__c= objCTCI.id,Complaint__c='No',G_A_Rights_Given__c='No', Interacting_With__c=oAcc[2].Id, Due_Date__c=Date.Today(), Case_Owner__c=UserInfo.getUserName(),
                              Created_By_Queue__c='Cincinnati CRU',Owner_Queue__c='Cincinnati CRU', Service_Center__c ='Cincinnati',Department__c='CRU', status='Closed'));
            
            lstCases.Add(New Case(AccountId=oAcc[0].Id, Policy_Member__c= oPolMem1.Id ,
                              Interacting_With_Type__c = 'Group',Complaint__c='No',G_A_Rights_Given__c='No', Interacting_With__c=oAcc[0].Id, Due_Date__c=Date.Today(), Case_Owner__c=UserInfo.getUserName(),
                              Owner_Queue__c='Cincinnati CRU',CTCI_List__c= objCTCI.id, Created_By_Queue__c='Dental Calls', Service_Center__c ='Cincinnati',Department__c='CRU', status='Pending'));
            insert lstCases;
            system.assertNotEquals(null, lstCases[0].Id);
            
            
            List<Case_Junction__c> lstCaseJun = New List<Case_Junction__c>();

            lstCaseJun.add(New Case_Junction__c(Case_Master__c = lstCases[0].Id, Case__c = lstCases[1].Id,Work_Queue__c = oQSetup[0].Id));
            lstCaseJun.add(New Case_Junction__c(Case_Master__c = lstCases[2].Id, Case__c = lstCases[0].Id,Work_Queue__c = oQSetup[1].Id));
            lstCaseJun.add(New Case_Junction__c(Case_Master__c = lstCases[0].Id, Case__c = lstCases[3].Id,Work_Queue__c = oQSetup[2].Id));
            lstCaseJun.add(New Case_Junction__c(Case_Master__c = lstCases[1].Id, Case__c = lstCases[2].Id,Work_Queue__c = oQSetup[2].Id));
            lstCaseJun.add(New Case_Junction__c(Case_Master__c = lstCases[3].Id, Case__c = lstCases[1].Id,Work_Queue__c = oQSetup[1].Id));
            lstCaseJun.add(New Case_Junction__c(Case_Master__c = lstCases[2].Id, Case__c = lstCases[3].Id,Work_Queue__c = oQSetup[0].Id));
            
            insert lstCaseJun;   
          
            system.assertNotEquals(null, lstCaseJun[0].Id);
            system.assertEquals( lstCaseJun[0].Case__c,lstCases[1].Id);        
            
            Pharmacy_Supervisor_HUM__c objPharmacySup = New Pharmacy_Supervisor_HUM__c(name ='CRMS_300_Humana_Pharmacy_Supervisor');
                insert objPharmacySup;
                system.assertEquals(objPharmacySup.Name,'CRMS_300_Humana_Pharmacy_Supervisor');  
        }
      }
      
      
    /*
     * Method Name : testFunctionalityWithSpecialist
     * Description : This method Is used To test main functionality With Specialist profile.
     * Return type : void
     * Parameter   : NA
     */
     Static testMethod  void testFunctionalityWithSpecialist() 
     {
        User oUser = [Select Id, Current_Queue__c, Network_User_Id__c  from User where LastName = 'test User1'];
        System.assertEquals('Cincinnati CRU', oUser.Current_Queue__c);
        System.runAs(oUser)
        {
            Test.StartTest();
            List<Case_Junction__c> lstCasejunction = [Select Id,Case__r.Id,Case_Master__r.Id,Case_Master__r.CTCI_List__r.Classification__c from Case_Junction__c where IsLinked__c=true  Limit 100];
            System.assert(lstCasejunction.size() >0);    
            ApexPages.StandardController controller;
            CaseLinkComponent_LC_HUM objLinkC = New CaseLinkComponent_LC_HUM();
            If (lstCasejunction!= NULL &&!lstCasejunction.isEmpty())
            {
            ApexPages.currentPage().getParameters().put('Id', lstCasejunction [0].Case__r.Id);
            system.assertNotEquals(null,lstCasejunction [0].Case__r.Id);
            CaseLinkComponent_LC_HUM.isUnlinkVisible();
                CaseLinkComponent_LC_HUM.getCaseLinkedData(lstCasejunction [0].Case__r.Id);
                CaseLinkComponent_LC_HUM.getCaseLinkedData(lstCasejunction [0].Case_Master__r.Id);
                CaseLinkComponent_LC_HUM.unLinkCaseJunction(lstCasejunction [0].Case__r.Id,lstCasejunction [0].Case_Master__r.Id);
            }
            Test.StopTest();
        }
    }
    
    

    


    

    /*
     * Method Name : testFunctionalityWithSupervisor
     * Description : This method Is used To test main functionality With Supervisor profile.
     * Return type : void
     * Parameter   : NA
     */
     Static testMethod  void testFunctionalityWithSupervisor() 
     {
        User oUser = [Select Id, Current_Queue__c, Network_User_Id__c  from User where LastName = 'test User2'];
        System.assertEquals('Cincinnati Home Office Calls', oUser.Current_Queue__c);
        System.runAs(oUser)
        {
            Test.StartTest();
            List<Case_Junction__c> lstCasejunction = [Select Id,Case__r.Id,Case_Master__r.Id from Case_Junction__c where IsLinked__c=true limit 100];
            ApexPages.StandardController controller;
            CaseLinkComponent_LC_HUM objLinkC = New CaseLinkComponent_LC_HUM();

             CaseLinkComponent_LC_HUM.isUnlinkVisible();
         
            Test.StopTest();
        }
    }
 
}
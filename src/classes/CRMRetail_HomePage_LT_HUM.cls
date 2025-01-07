/****************************************************************************************
Apex Class Name :  CRMRetail_HomePage_LT_HUM
Version: 1.0 
Created Date     : 07/16/2022
Function         : Test class to CRMRetail_HomePage_LC_HUM Apex class
Modification Log : 
*      Developer               Code Review            Date                 Description
* ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------
*      Sahil                                          07/16/2022           Original Version
*      Sahil Verma                                    12/02/2022           User Story 4012388: T1PRJ0154546 / SF/ MF9 Storefront - Ability to View First Time Visit - Current CY in Storefront
*      Vinoth L                                       01/05/2023           User Story 4107848: T1PRJ0154546 / SF / MF3 Storefront: Update Visitor ID & Type Population Logic
* 	   Vinoth L                      				  02/28/2023           User Story 4349337: T1PRJ0154546 / SF / MF9 Storefront: Deprecate Old Home Page Components
*      Vinoth L                                       03/17/2023           DEF - 7421 : Phone Search Criteria Fix
*      Mohamed Thameem      	               	      06/12/2023           User Story 4695921: T1PRJ0154546 / SF/ MF9 Storefront - Allow Search By Phone Number Only
*      Mohamed Thameem      	                      08/28/2023           User Story 5012149: T1PRJ0154546 / DP / MF9 Storefront - Update Guest Card # Logic
*      Vinoth L                      				  08/26/2023           User Story 5012060: T1PRJ0154546 / DP / MF9 Storefront - Add preferred name on Account
* 	   Mohamed Thameem      	                      09/22/2023           User Story 5132829: T1PRJ0154546 / SF / MF9 Storefront - Include Preferred Name in Search
*      Vinoth L										  10/03/2023		   User Story 5132659 Ability to Edit Mobile Location Field
****************************************************************************************************************************************************************************/
@isTest
Private class CRMRetail_HomePage_LT_HUM {
     /*
     * Method Name : prepareTestData
     * Description : Create common test Data
     * Return type : void
     * Parameter   : NA
     */
    @testSetup static void prepareTestData() 
    {
        list<User> lstOfUsers = new list<User>();
        User oCCSUser = HUMTestDataHelperTest.createTestUser('User1','CRMRetail User');
        oCCSUser.TimeZoneSidKey = 'Pacific/Pago_Pago';
        lstOfUsers.add(oCCSUser);
         
        User nonStorefrontUser = HUMTestDataHelperTest.createTestUser('User2','System Administrator');
        nonStorefrontUser.TimeZoneSidKey = 'Pacific/Pago_Pago';
        lstOfUsers.add(nonStorefrontUser);
         
        User BMEUser = HUMTestDataHelperTest.createTestUser('User3','BME User');
        BMEUser.TimeZoneSidKey = 'Pacific/Pago_Pago';
        lstOfUsers.add(BMEUser);
        
        User BMEUser1 = HUMTestDataHelperTest.createTestUser('User4','BME User');
        BMEUser1.TimeZoneSidKey = 'Pacific/Pago_Pago';
        lstOfUsers.add(BMEUser1);
        
        User ccsUser = HUMTestDataHelperTest.createTestUser('User5','Customer Care Specialist');
        ccsUser.TimeZoneSidKey = 'Pacific/Pago_Pago';
        lstOfUsers.add(ccsUser);
        
        User objCRMRetailUser = HUMTestDataHelperTest.createTestUser('UserCRMRetail','CRMRetail User');
        objCRMRetailUser.TimeZoneSidKey = 'Pacific/Pago_Pago';
        lstOfUsers.add(objCRMRetailUser);
        
        insert lstOfUsers;
        system.assertEquals(lstOfUsers.size(), 6);
        system.assertEquals(oCCSUser.LastName,'test User1');
        system.assertEquals(nonStorefrontUser.LastName,'test User2');
        system.assertEquals(BMEUser.LastName,'test User3');
        system.assertEquals(BMEUser1.LastName,'test User4');
        system.assertEquals(ccsUser.LastName,'test User5');
        system.assertEquals(objCRMRetailUser.LastName,'test UserCRMRetail');
        
        PermissionSet SuperAdmin = [SELECT Id FROM PermissionSet WHERE Name = 'CRMRetail_400_Super_Admin'];
        List<PermissionSetAssignment> psAssign = new List<PermissionSetAssignment>();
        PermissionSetAssignment supAdmin1 = new PermissionSetAssignment(AssigneeId = oCCSUser.id, PermissionSetId = SuperAdmin.Id);
        psAssign.add(supAdmin1);
        PermissionSetAssignment supAdmin2 = new PermissionSetAssignment(AssigneeId = objCRMRetailUser.id, PermissionSetId = SuperAdmin.Id);
        psAssign.add(supAdmin2);
        insert psAssign;
        
        system.assertEquals(psAssign.size(),2);
        system.assertEquals(psAssign[0].AssigneeId, oCCSUser.id);        
        system.assertEquals(psAssign[1].AssigneeId, objCRMRetailUser.id); 
        
        System.runAs (objCRMRetailUser) 
        {
            Id oMemRecType = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId();
            Id oCRMRetailVisitorType = Schema.SObjectType.Account.getRecordTypeInfosByName().get('CRMRetail Visitor').getRecordTypeId();
            
            List<Account> lstOfAccounts = new List<Account>();
            Date waiverDate = Date.today()-100;        
            Account oAcc = CRMRetail_TestDataFactory_H_HUM.createAccountInstance(null, oMemRecType,Date.newInstance(1984, 01, 01),'01/01/1984', 'Member', 'Account', 'O', '1060920019',false, 'New York', '56605', '(423) 123-1231','test1234',waiverDate,'confirmed-veteran',NULL);
            oAcc.PersonMailingPostalCode = '12345';
            oAcc.Birthdate__c = '01/02/1999';
            Account oAcc1 = CRMRetail_TestDataFactory_H_HUM.createAccountInstance('GC7654321', oCRMRetailVisitorType,Date.newInstance(1984, 01, 01),'01/01/1984', 'Member', 'Account', 'O', '1060920019',false, 'New York', '56605', '(123) 123-1231','test1234',waiverDate,'confirmed-veteran',NULL);
            oAcc1.PersonMailingPostalCode = '12345';
            oAcc1.Birthdate__c = '01/02/1999';
            Account oAcc2 = CRMRetail_TestDataFactory_H_HUM.createAccountInstance('GC7654322', oCRMRetailVisitorType,Date.newInstance(1984, 01, 01),'01/01/1984', 'Member', 'Account', 'O', '1060920019',false, 'New York', '56605', '(123) 123-1231','test1234',waiverDate,'identified-veteran',NULL);
            oAcc2.PersonMailingPostalCode = '12345';
            oAcc2.Birthdate__c = '01/02/1999';
            Account oAcc3 = CRMRetail_TestDataFactory_H_HUM.createAccountInstance('GC7654323', oCRMRetailVisitorType,Date.newInstance(1984, 01, 01),'01/01/1984', 'Member', 'Account', 'O', '1060920019',false, 'New York', '56605', '(123) 123-1231','test1234',waiverDate,'identified-veteran',NULL);
            oAcc3.PersonMailingPostalCode = '12345';
            oAcc3.Birthdate__c = '01/02/1999';
            Account oAcc4 = CRMRetail_TestDataFactory_H_HUM.createAccountInstance(NULL, oMemRecType,Date.newInstance(1984, 01, 01),'01/01/1984', 'NopolicyMember', 'Account', 'O', '1060920019',false, 'New York', '56605', '(123) 123-1231','test1234',waiverDate,'confirmed-veteran',NULL);
            oAcc4.PersonMailingPostalCode = '12345';
            oAcc4.Birthdate__c = '01/02/1999';
            Account oAcc5 =CRMRetail_TestDataFactory_H_HUM.createAccountInstance(NULL, oMemRecType,Date.newInstance(1984, 01, 01),'01/01/1984', 'Tom', 'Hill', 'O', '1060920019',false, 'New York', '56605', '(123) 123-1231','test1234',waiverDate,'confirmed-veteran',NULL);            
            oAcc5.PersonMailingPostalCode = '12345';
            oAcc5.Birthdate__c = '01/02/1999';
            Account oAcc6 =CRMRetail_TestDataFactory_H_HUM.createAccountInstance(NULL, oMemRecType,Date.newInstance(1984, 01, 01),'01/01/1984', 'Hosain', 'Truh', 'O', '1060920019',false, 'New York', '56605', '(123) 123-1231','test1234',waiverDate,'confirmed-veteran',NULL);            
            oAcc6.PersonMailingPostalCode = '12345';
            oAcc6.Birthdate__c = '01/02/1999';   
            Account notifAcc1 = CRMRetail_TestDataFactory_H_HUM.createAccountInstance(NULL, oMemRecType,Date.newInstance(1999, 12, 07),'07/12/1999', 'Storefront', 'New Member', 'O', '1060920019',false, 'Alabama', '81028', '(810) 817-8127','',NULL,'confirmed-veteran',NULL);            
            notifAcc1.Enterprise_Id__c = '10681029109';            
            Account notifAcc2 = CRMRetail_TestDataFactory_H_HUM.createAccountInstance('test1234', oCRMRetailVisitorType,Date.newInstance(1984, 03, 11),'11/03/1984', 'Storefront', 'Visitor', 'O', '' ,false, 'New York', '56605', '(671) 291-3147','GC7654321',system.today()-40,'identified-veteran',NULL);            
            lstOfAccounts.add(oAcc);
            lstOfAccounts.add(oAcc1);
            lstOfAccounts.add(oAcc2);
            lstOfAccounts.add(oAcc3);
            lstOfAccounts.add(oAcc4);
            lstOfAccounts.add(oAcc5);
            lstOfAccounts.add(oAcc6);
            lstOfAccounts.add(notifAcc1);
            lstOfAccounts.add(notifAcc2);
            insert lstOfAccounts;
            system.assertEquals(9,lstOfAccounts.size());
            
            List<Storefront_Location__C> lstOfSfLocations = new List<Storefront_Location__C>();
            Storefront_Location__c location1 = new Storefront_Location__C();
            location1 = CRMRetail_TestDataFactory_H_HUM.createLocation('Tampa','abc','xyz','12345','12345','Texas',20); 
            Storefront_Location__c notifLocation1 = CRMRetail_TestDataFactory_H_HUM.createLocation('Tulsa Testing','7719','Tulsa Testing','81029','15261','Tulsa - TA Testing',15);            
            lstOfSfLocations.add(location1);
            lstOfSfLocations.add(notifLocation1);
            insert lstOfSfLocations;
            lstOfSfLocations.clear();
            lstOfSfLocations = [select id,name from Storefront_Location__c where name='Tulsa Testing'];
            system.assertEquals(1, lstOfSfLocations.size());
            
            Storefront_I_Type__c InteractionType1 = new Storefront_I_Type__c(Name ='Community');
            insert InteractionType1;
            System.AssertEquals(InteractionType1.Name, 'Community');     
            
            List<Storefront_I_Reason__c> lstReason = new List<Storefront_I_Reason__c>();
            Storefront_I_Reason__c InteractionReason1 = CRMRetail_TestDataFactory_H_HUM.createInteractionReason('Community Practice' ,InteractionType1.Id);                        
            Storefront_I_Reason__c notifInteractionReason1 = CRMRetail_TestDataFactory_H_HUM.createInteractionReason('Visitor Check-In' ,InteractionType1.Id);   
            Storefront_I_Reason__c notifInteractionReason2 = CRMRetail_TestDataFactory_H_HUM.createInteractionReason('Aging In' , InteractionType1.Id);            
            Storefront_I_Reason__c notifInteractionReason3 = CRMRetail_TestDataFactory_H_HUM.createInteractionReason('Waiver Required' , InteractionType1.Id);            
            Storefront_I_Reason__c notifInteractionReason4 = CRMRetail_TestDataFactory_H_HUM.createInteractionReason('SDoH' , InteractionType1.Id);            
            Storefront_I_Reason__c notifInteractionReason5 = CRMRetail_TestDataFactory_H_HUM.createInteractionReason('Veteran' , InteractionType1.Id);            
            Storefront_I_Reason__c notifInteractionReason6 = CRMRetail_TestDataFactory_H_HUM.createInteractionReason('New Member' , InteractionType1.Id);
			Storefront_I_Reason__c notifInteractionReason7 = CRMRetail_TestDataFactory_H_HUM.createInteractionReason('Visitor Check-In (1st Time)' ,InteractionType1.Id);            
            lstReason.add(notifInteractionReason1);
            lstReason.add(notifInteractionReason2);
            lstReason.add(notifInteractionReason3);
            lstReason.add(notifInteractionReason4);
            lstReason.add(notifInteractionReason5);
            lstReason.add(notifInteractionReason6);
            lstReason.add(notifInteractionReason7);
            lstReason.add(InteractionReason1);
            insert lstReason;
            lstReason.clear();
            lstReason = [select id,name from Storefront_I_Reason__c];
            system.assertEquals(8,lstReason.size());
            
            List<Storefront_Interaction__c> lstOfSfInt = new List<Storefront_Interaction__c>();
            Storefront_Interaction__c sfInt1 = CRMRetail_TestDataFactory_H_HUM.createInteraction(oAcc2.Id,location1.Id,InteractionReason1.Id,False,'Non-Member',Date.Today()-1);
            Storefront_Interaction__c sfInt2 = CRMRetail_TestDataFactory_H_HUM.createInteraction(oAcc3.Id,location1.Id,InteractionReason1.Id,False,'Non-Member',Date.Today());
            Storefront_Interaction__c notifInteractions1 = CRMRetail_TestDataFactory_H_HUM.createInteraction(notifAcc1.Id,notifLocation1.Id,notifInteractionReason4.Id,true,'Member',system.today()-10);
            Storefront_Interaction__c notifInteractions2 = CRMRetail_TestDataFactory_H_HUM.createInteraction(notifAcc1.Id,notifLocation1.Id,notifInteractionReason6.Id,true,'Member',system.today()-10);
            Storefront_Interaction__c notifInteractions3 = CRMRetail_TestDataFactory_H_HUM.createInteraction(notifAcc2.Id,notifLocation1.Id,notifInteractionReason1.Id,true,'Non-Member',system.today()-10);
            Storefront_Interaction__c notifInteractions4 = CRMRetail_TestDataFactory_H_HUM.createInteraction(notifAcc2.Id,notifLocation1.Id,notifInteractionReason2.Id,true,'Non-Member',system.today()-10);
            Storefront_Interaction__c notifInteractions5 = CRMRetail_TestDataFactory_H_HUM.createInteraction(notifAcc2.Id,notifLocation1.Id,notifInteractionReason3.Id,true,'Non-Member',system.today()-10);
            Storefront_Interaction__c notifInteractions6 = CRMRetail_TestDataFactory_H_HUM.createInteraction(notifAcc2.Id,notifLocation1.Id,notifInteractionReason5.Id,true,'Non-Member',system.today()-10);
            notifInteractions2.Category__c = notifInteractions3.Category__c = notifInteractions4.Category__c = notifInteractions5.Category__c = notifInteractions6.Category__c = 'Notification';
            notifInteractions3.Category__c = GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CATEGORY_VIRTUAL;
            lstOfSfInt.add(notifInteractions1);
            lstOfSfInt.add(notifInteractions2);
            lstOfSfInt.add(notifInteractions3);
            lstOfSfInt.add(notifInteractions4);
            lstOfSfInt.add(notifInteractions5);
            lstOfSfInt.add(notifInteractions6);
            lstOfSfInt.add(sfInt1);
            lstOfSfInt.add(sfInt2);
            insert lstOfSfInt;
            lstOfSfInt.clear();
            lstOfSfInt = [select id,name from Storefront_Interaction__c];
            system.assertEquals(8,lstOfSfInt.size());
            
            List<Product2> lstOfProducts = new List<Product2>();                  
            Product2 prod2 = new Product2(ProductCode='MED', Name='MED',Product_Type__c='MA');
            lstOfProducts.add(prod2); 
            Product2 notifProd = new Product2(ProductCode='MED', Name='MED',Product_Type__c='MAPD');
            lstOfProducts.add(notifProd); 
            insert lstOfProducts;
            lstOfProducts.clear();
            lstOfProducts = [select id,name from Product2];
            system.assertEquals(2,lstOfProducts.size());
            
            List<Policy__c> lstOfPol = new List<Policy__c>();
            Policy__c oPolicy1 = CRMRetail_TestDataFactory_H_HUM.createPolicyInstance(oAcc.id,'89012',prod2.Id);
            lstOfPol.add(oPolicy1);            
            Policy__c oPolicy2 = CRMRetail_TestDataFactory_H_HUM.createPolicyInstance(oAcc.id,'89013',prod2.Id);
            lstOfPol.add(oPolicy2);                                    
            Policy__c oPolicy3 = CRMRetail_TestDataFactory_H_HUM.createPolicyInstance(oAcc6.id,'33333',prod2.Id);
            lstOfPol.add(oPolicy3);            
            Policy__c oPolicy4 = CRMRetail_TestDataFactory_H_HUM.createPolicyInstance(oAcc6.id,'44444',prod2.Id);
            lstOfPol.add(oPolicy4);            
            Policy__c oPolicy5 = CRMRetail_TestDataFactory_H_HUM.createPolicyInstance(oAcc5.id,'55555',prod2.Id);
            lstOfPol.add(oPolicy5);            
            Policy__c notifPolicy1 = CRMRetail_TestDataFactory_H_HUM.createPolicyInstance(notifAcc1.id,'89014',notifProd.Id); 
            lstOfPol.add(notifPolicy1);
            Policy__c notifPolicy2 = CRMRetail_TestDataFactory_H_HUM.createPolicyInstance(notifAcc1.id,'89015',notifProd.id);            
            lstOfPol.add(notifPolicy2); 
            insert lstOfPol;
            lstOfPol.clear();
            lstOfPol = [select id,name from Policy__c];
            system.assertEquals(7,lstOfPol.size());

            Date dToday = Date.valueOf(System.today());
            String sFormInvalidEndDate = String.valueOf(dToday.month())+'/'+String.valueOf(dToday.day())+'/'+String.valueOf(dToday.year()-1);
            String sFormValidEndDate = String.valueOf(dToday.month())+'/'+String.valueOf(dToday.day())+'/'+String.valueOf(dToday.year()+1);
            String sFormExpiredEndDate = String.valueOf(dToday.month())+'/'+String.valueOf(dToday.day())+'/'+String.valueOf(dToday.year()-2);
            
            List<Policy_Member__c> lstPlMem = new List<Policy_Member__c>();
            Policy_Member__c oPolicyMember1 = CRMRetail_TestDataFactory_H_HUM.createPolicyMemberInstance(oAcc.Id,oPolicy1.Id,'750678903201',(system.now().addYears(-2)).format('MM/dd/yyyy'),sFormInvalidEndDate,false);                                                                        
            lstPlMem.add(oPolicyMember1);            
            Policy_Member__c oPolicyMember2 = CRMRetail_TestDataFactory_H_HUM.createPolicyMemberInstance(oAcc.Id,oPolicy1.Id,'H5867344700',(system.now().addYears(-2)).format('MM/dd/yyyy'),sFormValidEndDate,false);                                                            
            lstPlMem.add(oPolicyMember2);
            Policy_Member__c oPolicyMember3 = CRMRetail_TestDataFactory_H_HUM.createPolicyMemberInstance(oAcc.Id,oPolicy1.Id,'781029110',(system.now().addYears(-3)).format('MM/dd/yyyy'),sFormExpiredEndDate,false);                                                
            lstPlMem.add(oPolicyMember3);            
            Policy_Member__c oPolicyMember5 = CRMRetail_TestDataFactory_H_HUM.createPolicyMemberInstance(oAcc5.Id,oPolicy2.Id,'H01928702900',(system.now()+10).format('MM/dd/yyyy'),'12/31/9999',false);                                    
            lstPlMem.add(oPolicyMember5);
            
            Policy_Member__c oPolicyMember6 = CRMRetail_TestDataFactory_H_HUM.createPolicyMemberInstance(oAcc6.Id,oPolicy3.Id,'H6678102900',(system.now().addYears(-3)).format('MM/dd/YYYY'),(system.now().addYears(-1)).format('MM/dd/YYYY'),false);                                    
            lstPlMem.add(oPolicyMember6);            
            Policy_Member__c oPolicyMember7 = CRMRetail_TestDataFactory_H_HUM.createPolicyMemberInstance(oAcc6.Id,oPolicy4.Id,'H5349302900',(system.now().addYears(-3)).format('MM/dd/YYYY'),(system.now().addYears(-1)).format('MM/dd/YYYY'),false);                        
            lstPlMem.add(oPolicyMember7);  
            Policy_Member__c oPolicyMember8 = CRMRetail_TestDataFactory_H_HUM.createPolicyMemberInstance(oAcc.Id,oPolicy1.Id,'H5867344701',(system.now().addYears(-2)).format('MM/dd/yyyy'),sFormValidEndDate,false);                                                            
            lstPlMem.add(oPolicyMember8);          
            Policy_Member__c notifPolicyMember1 = CRMRetail_TestDataFactory_H_HUM.createPolicyMemberInstance(notifAcc1.Id,notifPolicy1.Id,'H7598541400',(system.now()-10).format('MM/dd/YYYY'),(system.now().addYears(3)).format('MM/dd/YYYY'),false);            
            lstPlMem.add(notifPolicyMember1);
            Policy_Member__c notifPolicyMember2 = CRMRetail_TestDataFactory_H_HUM.createPolicyMemberInstance(notifAcc1.Id,notifPolicy1.Id,'H8903242400',(system.now().addYears(-3)).format('MM/dd/YYYY'),(system.now().addYears(-2)).format('MM/dd/YYYY'),false);            
            lstPlMem.add(notifPolicyMember2);
            insert lstPlMem;
            List<Policy_Member__c> pMember = [select id,name,Product__c from Policy_Member__c Where ID =: oPolicyMember5.id];
            lstPlMem.clear();
            lstPlMem = [select id,name from Policy_Member__c];
            system.assertEquals(9,lstPlMem.size());
                       
            Member_ID__c oMemberActive = new Member_ID__c();
            oMemberActive.Policy_Member__c = oPolicyMember3.ID;
            oMemberActive.Name = 'H71820112';
            oMemberActive.Member_Dependent_Code__c ='01';
            oMemberActive.Policy__c = oPolicy1.ID;
            oMemberActive.Member_Card_External_ID__c = 'M8192';
            oMemberActive.Member_Id_Value__c = 'H71820112';            
            insert oMemberActive;
            oMemberActive = new Member_ID__c();
            oMemberActive = [select id,name from Member_ID__c];
            system.assertEquals('H71820112',oMemberActive.Name);
            
            Set<String> setOfNotifFields = New Set<String>{'First Time Visitor','Aging In','New Member', 'SDoH', 'Veteran','Waiver Required'};
            List<Storefront_Notification__C> lstNotif = new List<Storefront_Notification__C>();
            for(String str: setOfNotifFields){
	       		Storefront_Notification__C sfNot1 = CRMRetail_TestDataFactory_H_HUM.createNotification(str,notifLocation1.Id,'Member','Follow',23,False,system.today()-12,10.0,50.0);
                Storefront_Notification__C sfNot2 = CRMRetail_TestDataFactory_H_HUM.createNotification(str,notifLocation1.Id,'Non-Member','Follow',23,False,system.today()-12,10.0,50.0);
                lstNotif.add(sfNot1);
                lstNotif.add(sfNot2);
            }                                  
            insert lstNotif;
            lstNotif.clear();
            lstNotif = [select id,name from Storefront_Notification__C];
            system.assertEquals(12,lstNotif.size());
            
            HUMConstants__c objMedicare = new HUMConstants__c(Name='HUMRSOMedicare',Stringvalue__c='RSO- Medicare');
            insert objMedicare;
            objMedicare = new HUMConstants__c();
            objMedicare = [select id,name from HUMConstants__c];
            system.assertEquals('HUMRSOMedicare',objMedicare.Name);
            Reference_Business_Line_Product_Grouping__c objGrouping = new Reference_Business_Line_Product_Grouping__c(ProductCode__c='',Major_LOB__c='MAPD',Business_Description__c='RSO- Medicare');
            insert objGrouping;
            objGrouping = new Reference_Business_Line_Product_Grouping__c();
            objGrouping = [select id,ProductCode__c from Reference_Business_Line_Product_Grouping__c];
        }
        System.runAs (oCCSUser)  
        {
            Id oMemRecType = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId();
            Id oCRMRetailVisitorType = Schema.SObjectType.Account.getRecordTypeInfosByName().get('CRMRetail Visitor').getRecordTypeId();            
            
            List<Account> lstOfAccounts = new List<Account>();                        
            Account oAcc =CRMRetail_TestDataFactory_H_HUM.createObjAccount(oMemRecType,'04/06/1985','Daphiner','Superman','12209','TAccount@gmail.com','','arizona',false);                                    
            oAcc.Group_Number__c = '987654321';                        
            oAcc.Enterprise_ID__c='1030693160';
            oAcc.Home_Office_Account__c = false;                        
            oAcc.GCM_Visitor_Barcode__c = '123456780';
            oAcc.PersonBirthdate = Date.newInstance(1985, 06, 04);
            oAcc.PersonHomePhone = '9812341523';                                  
            oAcc.Veteran_Status__c='confirmed-veteran';
            lstOfAccounts.add(oAcc);                        
                        
            Account oAcc2 =CRMRetail_TestDataFactory_H_HUM.createObjAccount(oMemRecType,'04/06/1985','Dean','Ambrose','12209','DEAN@gmail.com','','arizona',false);
            oAcc2.Enterprise_ID__c='17';
            oAcc2.PersonBirthdate = Date.newInstance(1985, 06, 04);
            oAcc2.PersonHomePhone = '9812341523';               
            oAcc2.GCM_Visitor_Barcode__c = '987654321';
            oAcc2.ETL_Record_Deleted__c = false;
            lstOfAccounts.add(oAcc2);                                    
            
            Account oAcc3 =CRMRetail_TestDataFactory_H_HUM.createObjAccount(oCRMRetailVisitorType,'04/06/1985','Dean','Ambrose','12209','DEAN11@gmail.com','','arizona',false);
            oAcc3.Enterprise_ID__c='17';
            oAcc3.PersonBirthdate = Date.newInstance(1985, 06, 04);
            oAcc3.PersonHomePhone = '9812341533';
            oAcc3.GCM_Visitor_Barcode__c = 'GC7654322';
            oAcc3.ETL_Record_Deleted__c = false;
            lstOfAccounts.add(oAcc3);
            
            Account oAcc4 =CRMRetail_TestDataFactory_H_HUM.createObjAccount(oCRMRetailVisitorType,'04/06/1985','Zak','Crawley','12209','ZAKC@gmail.com','','arizona',false);
            oAcc4.Enterprise_ID__c='17';
            oAcc4.GCM_Visitor_Barcode__c = '987654321';
            oAcc4.PersonBirthdate =  Date.newInstance(1985, 06, 04);
            oAcc4.GCM_Visitor_Barcode__c = '759874141';
            Date dToday1 = Date.valueOf(System.today());
            Date waiverDate = Date.newInstance(dToday1.year()-1, dToday1.month(), dToday1.day());
            oAcc4.Waiver_Date__c = waiverDate;
            lstOfAccounts.add(oAcc4);     
            
            Account oAcc5 =CRMRetail_TestDataFactory_H_HUM.createObjAccount(oCRMRetailVisitorType,'04/06/1985','Karan','Kumar','12209','Karan@gmail.com','','arizona',false);
            oAcc5.Enterprise_ID__c='17';
            oAcc5.PersonBirthdate =  Date.newInstance(1985, 06, 04);
            oAcc5.GCM_Visitor_Barcode__c = 'HNC123123123';
            oAcc5.Waiver_Date__c = waiverDate;
            lstOfAccounts.add(oAcc5);
            
            insert lstOfAccounts;
            System.AssertEquals(lstOfAccounts.size(), 5);
           
            List<Product2> lstOfProducts = new List<Product2>();                  
            Product2 prod2 = new Product2(ProductCode='MED', Name='MED',Product_Type__c='MA');
            lstOfProducts.add(prod2); 
            insert lstOfProducts;
            system.assertEquals(lstOfProducts.get(0).Name,'MED');
            
            Policy__c oPolicy1 = new Policy__c();
            oPolicy1.Group_Name__c = oAcc.id;
            oPolicy1.Policy_Group_Number__c='11111';
            oPolicy1.Product__C=prod2.Id;            
            insert oPolicy1;
            System.AssertEquals('11111',oPolicy1.Policy_Group_Number__c);
            
            List<Policy_Member__c> lstPolicyMembers = new List<Policy_Member__c>();
            Policy_Member__c oPolicyMember1 = new Policy_Member__c();
            oPolicyMember1.Member__c = oAcc.ID;
            oPolicyMember1.Policy__c = oPolicy1.ID;
            oPolicyMember1.Name = 'H68102971';
            oPolicyMember1.ETL_Record_Deleted__c = false;
            oPolicyMember1.End_Date__c ='12/31/2100';
            oPolicyMember1.Effective_Date__c = '12/31/2021';
            lstPolicyMembers.add(oPolicyMember1);            
            
            Date dToday = Date.valueOf(System.today());
            String sFormInvalidEndDate = String.valueOf(dToday.month())+'/'+String.valueOf(dToday.day())+'/'+String.valueOf(dToday.year()-1);
            
            Policy_Member__c oPolicyMember2 = new Policy_Member__c();
            oPolicyMember2.Member__c = oAcc2.ID;
            oPolicyMember2.Policy__c = oPolicy1.ID;
            oPolicyMember2.Name = 'H5H6789032';
            oPolicyMember2.ETL_Record_Deleted__c = false;
            oPolicyMember2.End_Date__c =sFormInvalidEndDate;
            oPolicyMember2.Effective_Date__c = '12/31/2021';
            lstPolicyMembers.add(oPolicyMember2);
            
            Policy_Member__c oPolicyMember3 = new Policy_Member__c();
            oPolicyMember3.Member__c = oAcc3.ID;
            oPolicyMember3.Policy__c = oPolicy1.ID;
            oPolicyMember3.Name = 'H5H67344732';
            oPolicyMember3.ETL_Record_Deleted__c = false;
            oPolicyMember3.End_Date__c =sFormInvalidEndDate;
            oPolicyMember3.Effective_Date__c = '12/31/2021';
            lstPolicyMembers.add(oPolicyMember3);            
           
            
            Policy_Member__c oPolicyMemberTermed = new Policy_Member__c();
            oPolicyMemberTermed.Member__c = oAcc3.ID;
            oPolicyMemberTermed.Policy__c = oPolicy1.ID;
            oPolicyMemberTermed.Name = 'H6172918200';
            oPolicyMemberTermed.ETL_Record_Deleted__c = false;
            oPolicyMemberTermed.End_Date__c ='12/31/2021';
            oPolicyMemberTermed.Effective_Date__c = '12/31/2021';
            lstPolicyMembers.add(oPolicyMemberTermed);
            
            Policy_Member__c oPolicyMember4 = new Policy_Member__c();
            oPolicyMember4.Member__c = oAcc.ID;
            oPolicyMember4.Policy__c = oPolicy1.ID;
            oPolicyMember4.Name = 'H68102981';
            oPolicyMember4.ETL_Record_Deleted__c = false;
            oPolicyMember4.End_Date__c ='12/31/2100';
            oPolicyMember4.Effective_Date__c = '12/31/2021';
            lstPolicyMembers.add(oPolicyMember4);                          
            
            insert lstPolicyMembers;
            System.assertEquals(lstPolicyMembers.size(), 5);
            
            HUMConstants__c objMedicare = new HUMConstants__c(Name='HUMRSOMedicare',Stringvalue__c='RSO- Medicare');
            insert objMedicare;
            Reference_Business_Line_Product_Grouping__c objGrouping = new Reference_Business_Line_Product_Grouping__c(ProductCode__c='MED',Major_LOB__c='MAPD',Business_Description__c='RSO- Medicare');
            insert objGrouping;
             
            List<Member_ID__c> listMembers = new List<Member_ID__c>();
            Member_ID__c oMember = new Member_ID__c();
            oMember.Policy_Member__c = lstPolicyMembers[0].ID;
            oMember.Name = 'H555555555';
            oMember.Member_Dependent_Code__c ='02';
            oMember.Policy__c = oPolicy1.ID;
            oMember.Member_Card_External_ID__c = 'M345';
            oMember.Member_Id_Value__c = '777777777';
            listMembers.add(oMember);
            System.AssertNotEquals(oMember, NULL);
            
            Member_ID__c oMemberInactive = new Member_ID__c();
            oMemberInactive.Policy_Member__c = lstPolicyMembers[1].ID;
            oMemberInactive.Name = 'H56789032';
            oMemberInactive.Member_Dependent_Code__c ='02';
            oMemberInactive.Policy__c = oPolicy1.ID;
            oMemberInactive.Member_Card_External_ID__c = 'MX89032';
            oMemberInactive.Member_Id_Value__c = 'H56789032';
            listMembers.add(oMemberInactive);
            System.AssertNotEquals(oMemberInactive, NULL);
            
            Member_ID__c oMemberNew = new Member_ID__c();
            oMemberNew.Policy_Member__c = lstPolicyMembers[4].ID;
            oMemberNew.Name = 'H55550000';
            oMemberNew.Member_Dependent_Code__c ='02';
            oMemberNew.Policy__c = oPolicy1.ID;
            oMemberNew.Member_Card_External_ID__c = 'M346';
            oMemberNew.Member_Id_Value__c = 'H56780000';
            listMembers.add(oMemberNew);
            insert listMembers;
            system.assertEquals(listMembers.size(),3);
            
            List<Storefront_Location__C> lstOfSfLocations = new List<Storefront_Location__C>();
            Storefront_Location__c location1 = new Storefront_Location__C();
            location1 = CRMRetail_TestDataFactory_H_HUM.createLocation();
            location1.Maximum_Occupancy__c= 10;
            lstOfSfLocations.add(location1);                        
            
            Storefront_Location__c location2= new Storefront_Location__C();
            location2 = CRMRetail_TestDataFactory_H_HUM.createVirtualLocation();             
            location2.Maximum_Occupancy__c= 10;            
            lstOfSfLocations.add(location2);
            
            Storefront_Location__c location3= new Storefront_Location__C();
            location3 = CRMRetail_TestDataFactory_H_HUM.createLocation();
            location3.Name = 'Florida';
            location3.Address_Line_1__c = '4505';
            location3.City__c='Miami';
            location3.Zip_Code__c='40004';
            location3.Org_Facility__c='86755';
            location3.State__c='Florida - FL';
            location3.Location_Phone__c='5028675309';
            location3.Inactive_indicator__c = false;
            location3.Maximum_Occupancy__c = 2;
            lstOfSfLocations.add(location3);
            
            insert lstOfSfLocations;
            System.AssertEquals(lstOfSfLocations.size(), 3);
            System.AssertEquals(lstOfSfLocations.get(0).Name, 'Virginia');
            System.AssertEquals(lstOfSfLocations.get(1).Name, 'Tulsa');
            System.AssertEquals(lstOfSfLocations.get(2).Name, 'Florida');
            
            List<Storefront_Preference__c> lstStfPref = new List<Storefront_Preference__c>();
            Storefront_Preference__c DefLocation1 = new Storefront_Preference__c(Default_Location__c = lstOfSfLocations.get(0).Id, User__c = oCCSUser.id);
            lstStfPref.add(DefLocation1);
            Storefront_Preference__c DefLocation2 = new Storefront_Preference__c( User__c = BMEUser1.id);
            lstStfPref.add(DefLocation2);
			insert lstStfPref;
            lstStfPref.clear();
            lstStfPref = [SELECT Id from Storefront_Preference__c];
            system.assertEquals(2, lstStfPref.size());
            
            list<Attendance__c> attList = new List<Attendance__c>();
            Attendance__c att = CRMRetail_TestDataFactory_H_HUM.createAttendance(lstOfSfLocations.get(0).Id, system.Today());
            attList.add(att);
            Attendance__c att1 = CRMRetail_TestDataFactory_H_HUM.createAttendance(lstOfSfLocations.get(2).Id, date.today().addDays(-2));
            attList.add(att1);
            insert attList;
            system.assertEquals(attList.size(), 2);
            
            Storefront_I_Type__c InteractionType1 = new Storefront_I_Type__c(Name ='Social');
            insert InteractionType1;
            System.AssertEquals(InteractionType1.Name, 'Social');
                         
            Set<String> setOfNotifFields = New Set<String>{'Visitor Check-In','First Time Visitor','Aging In','New Member', 'SDoH', 'Veteran','Waiver Required'};
			Storefront_Interaction__c sfInt; 
            Set<Storefront_Interaction__c> stOfInts = new set<Storefront_Interaction__c>();
            List<Storefront_Interaction__c> lstOfSfInteractions = new List<Storefront_Interaction__c>();
            List<Storefront_I_Reason__c> lstSfrsn = [SELECT Id,Name,Interaction_Type__C from Storefront_I_Reason__c where Name IN: setOfNotifFields];
            system.AssertEquals(6,lstSfrsn.size());
            for(Storefront_I_Reason__c sfRsn : [SELECT Id,Name,Interaction_Type__C from Storefront_I_Reason__c where Name IN: setOfNotifFields]){                
                if(sfRsn.Name == 'Visitor Check-In'){
                    sfInt = CRMRetail_TestDataFactory_H_HUM.createInteraction(oAcc.Id,location1.Id,sfRsn.Id,False, 'Member' ,system.today());
                    stOfInts.add(sfInt);                    
                    sfInt = CRMRetail_TestDataFactory_H_HUM.createInteraction(oAcc2.Id,location1.Id,sfRsn.Id,False, 'Member' ,system.today());                    
                    stOfInts.add(sfInt);
                    sfInt = CRMRetail_TestDataFactory_H_HUM.createInteraction(oAcc3.Id,location1.Id,sfRsn.Id,False, 'Non-Member' ,system.today());                    
                    stOfInts.add(sfInt);
                    sfInt = CRMRetail_TestDataFactory_H_HUM.createInteraction(oAcc4.Id,location1.Id,sfRsn.Id,False, 'Non-Member' ,system.today());
                    sfInt.Storefront_Vstr_Indicator__c = '{"Special Needs Plan (SNP)":"DE","Engagement Prediction":"engaged","First Time Visitor This Year":"True"}';                    
                    stOfInts.add(sfInt);
                }else if(sfRsn.Name == 'Veteran'){
                    sfInt = CRMRetail_TestDataFactory_H_HUM.createInteraction(oAcc.Id,location1.Id,sfRsn.Id,False, 'Member' ,system.today()-40);
					stOfInts.add(sfInt);                    
                }else if(sfRsn.Name == 'New Member'){
                    sfInt = CRMRetail_TestDataFactory_H_HUM.createInteraction(oAcc.Id,location1.Id,sfRsn.Id,False, 'Member' ,system.today()-11); 
                    stOfInts.add(sfInt);
                    break;
                }else if(sfRsn.Name == 'Aging In'){
                    sfInt = CRMRetail_TestDataFactory_H_HUM.createInteraction(oAcc.Id,location1.Id,sfRsn.Id,False, 'Member' ,system.today()-5);
                    stOfInts.add(sfInt);
                }                                
            }  
            lstOfSfInteractions.addAll(stOfInts);
            insert lstOfSfInteractions;
            lstOfSfInteractions.clear();
            lstOfSfInteractions = [select id from Storefront_Interaction__c];
            system.assertEquals(15,lstOfSfInteractions.size());
            
            Set<String> setOfNotifTypes = New Set<String>{'First Time Visitor','Aging In','New Member', 'SDoH', 'Veteran','Waiver Required'};
            List<Storefront_Notification__C> lstNotif = new List<Storefront_Notification__C>();
            for(String str: setOfNotifTypes){
	       		Storefront_Notification__C sfNot1 = CRMRetail_TestDataFactory_H_HUM.createNotification(str,location1.Id,'Member','Follow',23,False,system.today()-12,10.0,50.0);
                Storefront_Notification__C sfNot2 = CRMRetail_TestDataFactory_H_HUM.createNotification(str,location1.Id,'Non-Member','Follow',23,False,system.today()-12,10.0,50.0);
                lstNotif.add(sfNot1);
                lstNotif.add(sfNot2);
            }                               
            insert lstNotif;
            lstNotif.clear();
            lstNotif = [select id from Storefront_Notification__C];
            system.assertEquals(24,lstNotif.size());                        
            List<CRMFunctionality_ONOFF_Switch__c> lstOfCRMSwitches = new List<CRMFunctionality_ONOFF_Switch__c>();
            CRMFunctionality_ONOFF_Switch__c switch_Attendance = new CRMFunctionality_ONOFF_Switch__c();
            switch_Attendance.IsON__c = true;
            switch_Attendance.Name = GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_MODERNIZEDHOME_SWITCH_LIST[0];
            lstOfCRMSwitches.add(switch_Attendance);
            
            CRMFunctionality_ONOFF_Switch__c Switch_Duplicate = new CRMFunctionality_ONOFF_Switch__c();
            Switch_Duplicate.IsON__c = true;
            Switch_Duplicate.Name = '3450197';
            lstOfCRMSwitches.add(Switch_Duplicate);

            CRMFunctionality_ONOFF_Switch__c Switch_HNCCard = new CRMFunctionality_ONOFF_Switch__c();
            Switch_HNCCard.IsON__c = true;
            Switch_HNCCard.Name = '5012149';
            lstOfCRMSwitches.add(Switch_HNCCard);

            CRMFunctionality_ONOFF_Switch__c switch_4012388 = new CRMFunctionality_ONOFF_Switch__c();
            switch_4012388.IsON__c = true;
            switch_4012388.Name = '4012388';
            lstOfCRMSwitches.add(switch_4012388);
            
            CRMFunctionality_ONOFF_Switch__c switch_3316885 = new CRMFunctionality_ONOFF_Switch__c();
            switch_3316885.IsON__c = true;
            switch_3316885.Name = '3316885';
            lstOfCRMSwitches.add(switch_3316885);

            CRMFunctionality_ONOFF_Switch__c switch_5132659 = new CRMFunctionality_ONOFF_Switch__c();
            switch_5132659.IsON__c = true;
            switch_5132659.Name = '5132659';
            lstOfCRMSwitches.add(switch_5132659);

            CRMFunctionality_ONOFF_Switch__c switch_5012060 = new CRMFunctionality_ONOFF_Switch__c();
            switch_5012060.IsON__c = true;
            switch_5012060.Name = '5012060';
            lstOfCRMSwitches.add(switch_5012060);

            CRMFunctionality_ONOFF_Switch__c switch_5132829 = new CRMFunctionality_ONOFF_Switch__c();
            switch_5132829.IsON__c = true;
            switch_5132829.Name = '5132829';
            lstOfCRMSwitches.add(switch_5132829);
            
            insert lstOfCRMSwitches;
            lstOfCRMSwitches.clear();
            lstOfCRMSwitches = [select id from CRMFunctionality_ONOFF_Switch__c];

            assert.areEqual(8,lstOfCRMSwitches.size()); 
            
            List<HUMTriggerSwitch__c> triggerSwitchList = new List<HUMTriggerSwitch__c>();
            HUMTriggerSwitch__c triggerSwitch = new HUMTriggerSwitch__c();
            triggerSwitch.Exeute_Trigger__c = true;
            triggerSwitch.Name = 'Storefront_Interaction__c';
            triggerSwitchList.add(triggerSwitch);  
            insert triggerSwitchList;            
            triggerSwitchList.clear();
            triggerSwitchList = [select id from HUMTriggerSwitch__c];
            system.assertEquals(1,triggerSwitchList.size()); 
        }
        System.runAs(ccsUser) 
        {
            Id memberType = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId();  
            
            List<Account> lstOfAccounts1 = new List<Account>();                        
            Account oAcc5 =CRMRetail_TestDataFactory_H_HUM.createObjAccount(memberType,'04/06/1985','Peter','Thor','12209','thor.peter@gmail.com','','arizona',false);
            oAcc5.Enterprise_ID__c='17';
            oAcc5.PersonBirthdate = Date.newInstance(1985, 06, 04);
            oAcc5.PersonHomePhone = '9812341533';
            oAcc5.GCM_Visitor_Barcode__c = 'GC7654323';
            oAcc5.ETL_Record_Deleted__c = false;
            Date dToday1 = Date.valueOf(System.today());
            Date waiverDate = Date.newInstance(dToday1.year()-1, dToday1.month(), dToday1.day());
            oAcc5.Waiver_Date__c = waiverDate;
            lstOfAccounts1.add(oAcc5);
            insert lstOfAccounts1; 
            system.assertEquals(lstOfAccounts1.size(), 1);
            
            List<Product2> lstOfProducts = new List<Product2>();                  
            Product2 prod2 = new Product2(ProductCode='MED', Name='MED',Product_Type__c='MA');
            lstOfProducts.add(prod2); 
            insert lstOfProducts;
            system.assertEquals(lstOfProducts.size(), 1);
              
            Policy__c oPolicy1 = new Policy__c();
            oPolicy1.Group_Name__c = oAcc5.id;
            oPolicy1.Policy_Group_Number__c='1000';
            oPolicy1.Product__C=prod2.Id;
            insert oPolicy1;
            System.AssertNotEquals(oPolicy1, NULL);
            
            List<Policy_Member__c> lstPolicyMembers1 = new List<Policy_Member__c>();
            Policy_Member__c oPolicyMemberNonUser = new Policy_Member__c();
            oPolicyMemberNonUser.Member__c = oAcc5.ID;
            oPolicyMemberNonUser.Policy__c = oPolicy1.ID;
            oPolicyMemberNonUser.Name = 'H600';
            oPolicyMemberNonUser.ETL_Record_Deleted__c = false;
            oPolicyMemberNonUser.End_Date__c ='12/31/9999';
            oPolicyMemberNonUser.Effective_Date__c = system.today().format();
            lstPolicyMembers1.add(oPolicyMemberNonUser);
            insert lstPolicyMembers1;
            system.assertEquals(lstPolicyMembers1.size(), 1);
            
            List<Member_ID__c> listMembers1 = new List<Member_ID__c>();
            Member_ID__c oMemberNonuser = new Member_ID__c();
            oMemberNonuser.Policy_Member__c = lstPolicyMembers1[0].ID;
            oMemberNonuser.Name = 'H500000000';
            oMemberNonuser.Member_Dependent_Code__c ='02';
            oMemberNonuser.Policy__c = oPolicy1.ID;
            oMemberNonuser.Member_Card_External_ID__c = 'M350';
            oMemberNonuser.Member_Id_Value__c = '7000000000';
            listMembers1.add(oMemberNonuser);            
            insert listMembers1;
            System.AssertNotEquals(oMemberNonuser, NULL); 
            Storefront_I_Type__c InteractionType2 = new Storefront_I_Type__c(Name ='Query');
            insert InteractionType2;
            System.AssertEquals(InteractionType2.Name, 'Query'); 
            
            List<Storefront_I_Reason__c> listInteractionReason2 = new List<Storefront_I_Reason__c>();
            Storefront_I_Reason__c InteractionReason4= new Storefront_I_Reason__c(Name = 'First Time Visitor1' ,Interaction_Type__c = InteractionType2.Id);                        
            listInteractionReason2.add(InteractionReason4);
            insert listInteractionReason2;
            system.assertEquals(listInteractionReason2.size(), 1);
        }
    }

    @isTest static void testFetchLocation(){
        User testUser = [SELECT Id, Name,LastName FROM User WHERE LastName = 'test User1'];
        system.assertEquals(testUser.LastName, 'test User1');
        system.runAs(testUser)
        {
            test.startTest();
            Cache.Session.remove(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CURRENTLOCATION);
            try{
                CRMRetail_HomePage_LC_HUM.fetchLocationValue();
            }catch(Exception ex){
                system.assertEquals(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_SCRIPT_THROWN_EXCEPTION, ex.getMessage());
            }
            test.stopTest();
        }        
    }

    @isTest static  void testNotifChecks()
    {
        List<User> lstOfUsers = [Select id,LastName from User where LastName = 'test UserCRMRetail' LIMIT 1 ] ;
        assert.areEqual('test UserCRMRetail',lstOfUsers[0].LastName);
        List<Account> lstAcc = [SELECT Id,FirstName,Name,LastName,Enterprise_Id__c from Account where FirstName='Storefront'];
        assert.areEqual(2, lstAcc.size());                
        
        Storefront_Location__c locobj =[Select Id,Name,Location_Type__c from Storefront_Location__c where Name='Tulsa Testing' LIMIT 1];
        assert.areEqual('Tulsa Testing', locobj.Name); 
        CRMRetail_SDoH_Helper_HUM.enterpriseIdToSDohMetricAcronym.put(lstAcc[0].Enterprise_Id__c,null);
        system.runAs(lstOfUsers[0])
        {
            if(!Cache.Session.contains(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CURRENTLOCATION)){
                Cache.Session.Put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CURRENTLOCATION,locobj.Id +'-'+ locobj.Name);
            }
            if(!Cache.Session.contains(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CURRINTDATE)) {
                Datetime myDateTime = system.Now();
                String formatted = myDateTime.formatGMT('yyyy-MM-dd');
                Cache.Session.Put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CURRINTDATE, String.valueOf(Date.today()));
                assert.areEqual(String.valueOf(Date.today()),Cache.Session.get(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CURRINTDATE));
            }
            Map<String,String> mapOfAccIdName = new Map<String,String>();
            mapOfAccIdName.put(lstAcc[0].Id,lstAcc[0].Name);            
            String checkInType = 'checkin';
            Test.startTest();
            Map<String,String> returnMap = CRMRetail_HomePage_LC_HUM.initiateVisitorCheckIn(mapOfAccIdName, checkInType);
            assert.areEqual('true',returnMap.get('isSuccess'));  
            mapOfAccIdName.clear();
            mapOfAccIdName.put(lstAcc[1].Id,lstAcc[1].Name);                        
            returnMap.clear();
            returnMap = CRMRetail_HomePage_LC_HUM.initiateVisitorCheckIn(mapOfAccIdName, checkInType);
            assert.areEqual('true',returnMap.get('isSuccess'));  
            List<Storefront_Interaction__c> lstInts = [SELECT Id,Account__r.FirstName,Account__r.LastName,Account__r.CRMRetail_PreferredName__c from Storefront_Interaction__c where Account__c=:lstAcc[1].Id order by createddate desc limit 1];            
            assert.areEqual((lstInts[0].Account__r.FirstName.substring(0,1)+lstInts[0].Account__r.LastName.substring(0,1)),lstInts[0].Account__r.CRMRetail_PreferredName__c);                      
            List<Storefront_Notification__c> lstNotif = [SELECT Id,Inactive__c,Notification_Type__c from Storefront_Notification__c where Notification_Type__C = 'First Time Visitor' AND Visitor_Type__c= 'Member' LIMIT 1];
            lstNotif[0].Inactive__c = true;
            update lstNotif[0];
            assert.areEqual(true,lstNotif[0].Inactive__c);                             
            Test.stopTest();
        }
    }
    
    @isTest static void testNotificationCheck()
    {
        List<User> lstOfUsers = [Select id,LastName from User where LastName = 'test UserCRMRetail' LIMIT 1 ] ;
        system.assertEquals('test UserCRMRetail',lstOfUsers[0].LastName);
        List<Account> lstAcc = [SELECT Id,FirstName,Name,LastName,Enterprise_Id__c from Account where FirstName='Storefront'];
        system.assertEquals(2, lstAcc.size());                
        
        Storefront_Location__c locobj =[Select Id,Name,Location_Type__c from Storefront_Location__c where Name='Tulsa Testing' LIMIT 1];
        system.assertEquals('Tulsa Testing', locobj.Name); 
        CRMRetail_SDoH_Helper_HUM.enterpriseIdToSDohMetricAcronym.put(lstAcc[0].Enterprise_Id__c,null);
        system.runAs(lstOfUsers[0])
        {
            if(!Cache.Session.contains(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CURRENTLOCATION)){
                Cache.Session.Put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CURRENTLOCATION,locobj.Id +'-'+ locobj.Name);
            }
            if(!Cache.Session.contains(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CURRINTDATE)) {
                Datetime myDateTime = system.Now();
                String formatted = myDateTime.formatGMT('yyyy-MM-dd');
                Cache.Session.Put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CURRINTDATE, String.valueOf(Date.today()));
                system.assertEquals(String.valueOf(Date.today()),Cache.Session.get(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CURRINTDATE));
            }
            Map<String,String> mapOfAccIdName = new Map<String,String>();
            mapOfAccIdName.put(lstAcc[0].Id,lstAcc[0].Name);            
            String checkInType = 'checkin';
            
            Test.startTest();
            delete [SELECT Id from Storefront_Interaction__c where Account__c=:lstAcc[0].Id];
            Map<String,String> returnMap = CRMRetail_HomePage_LC_HUM.initiateVisitorCheckIn(mapOfAccIdName, checkInType);
            system.assertEquals('true',returnMap.get('isSuccess'));   
            locobj.Location_Type__c = GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CATEGORY_VIRTUAL;
            update locobj;
            system.assertEquals(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CATEGORY_VIRTUAL,locobj.Location_Type__c);
            Cache.Session.Put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CURRENTLOCATION,locobj.Id +'-'+ locobj.Name);
            mapOfAccIdName.clear();
            mapOfAccIdName.put(lstAcc[0].Id,lstAcc[0].Name);                        
            returnMap.clear();
            List<Storefront_Interaction__c> lstInts = [SELECT Id from Storefront_Interaction__c where Account__c=:lstAcc[0].Id];
            delete lstInts;
            lstInts.clear();
            lstInts = [SELECT Id from Storefront_Interaction__c where Account__c=:lstAcc[0].Id];
            system.assertEquals(0,lstInts.size());            
            returnMap = CRMRetail_HomePage_LC_HUM.initiateVisitorCheckIn(mapOfAccIdName, checkInType);
            system.assertEquals('true',returnMap.get('isSuccess')); 
            Test.stopTest();
        }
    }
    
    @isTest static void testresetDefault()
    {
        User testUser = [SELECT Id, Name, LastName FROM User WHERE LastName = 'test User1'];
        system.assertEquals(testUser.LastName, 'test User1');
        system.runAs(testUser){
            test.startTest();
            Map<string,string> resetMap = CRMRetail_HomePage_LC_HUM.resetDefault();
            system.assertEquals(resetMap.size(), 2);
            test.stopTest(); 
        }
    }
    
    @isTest static void testloadLocationsandInteractionDate()
    {
        User testUser = [SELECT Id, Name, LastName FROM User WHERE LastName = 'test User1'];
        system.assertEquals(testUser.LastName, 'test User1');
        system.runAs(testUser){
            test.startTest();
            Map<string,string> onLoadMap = CRMRetail_HomePage_LC_HUM.loadLocationsandInteractionDate();
            system.assertEquals(onLoadMap.size(), 4);
            test.stopTest(); 
        }
    }
    
    @isTest static void testSetDateInCache(){
        User testUser = [SELECT Id, Name, LastName FROM User WHERE LastName = 'test User1'];
        system.assertEquals(testUser.LastName, 'test User1');
        system.runAs(testuser){
            test.startTest();
            Cache.Session.Put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CURRINTDATE, date.today().format());
            String countValue = CRMRetail_HomePage_LC_HUM.getOrSetInteractionDateInCache(String.valueOf(Date.today().addDays(2)) , false);
            System.assertEquals(countValue, 'future');
            Cache.Session.Put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CURRINTDATE, date.today().format());
            string dateValue = CRMRetail_HomePage_LC_HUM.getOrSetInteractionDateInCache('' , false);
            system.assertEquals(dateValue, string.valueOf(Date.Parse(date.today().format())));
            string LocValue = CRMRetail_HomePage_LC_HUM.getorSetLocationInCache('Amazon' , false);
            system.assertEquals(LocValue, null);
            test.stopTest();
        }
    }
    
    @isTest static void testSetDateInCacheSwitchOff(){
        User testUser = [SELECT Id, Name, LastName FROM User WHERE LastName = 'test User1'];
        system.assertEquals(testUser.LastName, 'test User1');
        system.runAs(testuser){
            CRMFunctionality_ONOFF_Switch__c switch_Atendance = [Select Name, IsON__c FROM CRMFunctionality_ONOFF_Switch__c Where Name =: GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_MODERNIZEDHOME_SWITCH_LIST[0]];
            switch_Atendance.IsON__c = false;
            update switch_Atendance;
            system.assertEquals(switch_Atendance.IsON__c, false);
            test.startTest();
            Cache.Session.Put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CURRINTDATE, date.today().format());
            string dateValue = CRMRetail_HomePage_LC_HUM.getOrSetInteractionDateInCache(String.valueOf(Date.today().addDays(2)) , false);
            system.assertEquals(dateValue, null);
            test.stopTest();
        }
    }
    
    @isTest static void testSetLocationInCache(){
        User testUser = [SELECT Id, Name,LastName FROM User WHERE LastName = 'test User1'];
        system.assertEquals(testUser.LastName, 'test User1');
        string attValue ='';
        string LocationVal = '';
        system.runAs(testuser){
            test.startTest();
            Cache.Session.Put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CURRINTDATE, date.today().format());
            Storefront_Location__c locobj =[Select Id,Name from Storefront_Location__c WHERE Name = 'Tulsa'];
            system.assertEquals(locobj.Name, 'Tulsa');
            LocationVal = locobj.Name;
            system.assertEquals(LocationVal, 'Tulsa');
            attValue = CRMRetail_HomePage_LC_HUM.getorSetLocationInCache(String.valueOf(LocationVal) , false);
            system.assertEquals(attValue, '0');
            test.stopTest();
        }
    }
    
    @isTest static void testSetLocationInCacheSwitchOff(){
        User testUser = [SELECT Id, Name, LastName FROM User WHERE LastName = 'test User1'];
        system.assertEquals(testUser.LastName, 'test User1');
        string LocValue ='';
        string LocationVal = '';
        system.runAs(testuser){
            CRMFunctionality_ONOFF_Switch__c switch_Atendance = [Select Name, IsON__c FROM CRMFunctionality_ONOFF_Switch__c Where Name =: GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_MODERNIZEDHOME_SWITCH_LIST[0]];
            switch_Atendance.IsON__c = false;
            update switch_Atendance;
            system.assertEquals(switch_Atendance.IsON__c, false);
            test.startTest();
            Cache.Session.Put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CURRINTDATE, date.today().format());
            Storefront_Location__c locobj =[Select Id,Name from Storefront_Location__c WHERE Name = 'Tulsa'];
            system.assertEquals(locobj.Name, 'Tulsa');
            LocationVal = locobj.Name;
            system.assertEquals(LocationVal, 'Tulsa');
            LocValue = CRMRetail_HomePage_LC_HUM.getorSetLocationInCache(String.valueOf(LocationVal) , false);
            system.assertEquals(LocValue, null);
            test.stopTest();
        }
    }
    
    @isTest static void testSaveDefaultLocationRecord()
    {
        User testUser = [SELECT Id, Name,LastName FROM User WHERE LastName = 'test User1'];
        system.assertEquals(testUser.LastName, 'test User1');
        User nonStorefrontSUser = [Select id,UserName,LastName from User where LastName = 'test User2' LIMIT 1 ] ;
        system.assertEquals(nonStorefrontSUser.LastName, 'test User2');
        User BMEUser = [Select id,UserName,LastName from User where LastName = 'test User3' LIMIT 1 ] ;
        system.assertEquals(BMEUser.LastName, 'test User3');
        User BMEUser1 = [Select id,UserName,LastName from User where LastName = 'test User4' LIMIT 1 ] ;
        system.assertEquals(BMEUser1.LastName, 'test User4');
        system.runAs(testUser)
        {
            Storefront_Location__c locobj =[Select Id,Name from Storefront_Location__c WHERE Name = 'Tulsa'];            
            system.assertEquals(locobj.Name,'Tulsa');            
            Storefront_Preference__c checkMe = CRMRetail_HomePage_LC_HUM.saveDefaultLocationRecord(locobj.Name);
            system.assertEquals(locobj.Id,checkMe.Default_Location__c);
        }
        system.runAs(nonStorefrontSUser)
        {
            Storefront_Location__c locobj1 =[Select Id,Name from Storefront_Location__c WHERE Name = 'Florida'];            
            system.assertEquals(locobj1.Name,'Florida');
            Storefront_Preference__c checkMe = CRMRetail_HomePage_LC_HUM.saveDefaultLocationRecord(locobj1.Name);            
            system.assertEquals(locobj1.Id,checkMe.Default_Location__c);
        }
        system.runAs(BMEUser)
        {
            try{
                Storefront_Preference__c checkMe = CRMRetail_HomePage_LC_HUM.saveDefaultLocationRecord('Florida');
            }
            catch(Exception ex)
            {
              system.assertEquals('Script-thrown exception', ex.getMessage());
            }
        }
        system.runAs(BMEUser1)
        {
            try{
                Storefront_Preference__c checkMe = CRMRetail_HomePage_LC_HUM.saveDefaultLocationRecord('Florida');
            }
            catch(Exception ex)
            {
              system.assertEquals('Script-thrown exception', ex.getMessage());
            }
        }
    }
    
    @isTest static void testAttendanceEvaluation()
    {
        User testUser = [SELECT Id, Name,LastName FROM User WHERE LastName = 'test User1'];
        system.assertEquals(testUser.LastName, 'test User1');
        system.runAs(testUser)
        {        
            test.startTest();                        
            Date dt = system.today();
            Datetime myDateTime = system.Now();
            String formatted = myDateTime.formatGMT('yyyy-MM-dd');
            Cache.Session.Put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CURRINTDATE, formatted); 
            String dedAtt = CRMRetail_HomePage_LC_HUM.deduceAttendance();
            String response1 =  CRMRetail_HomePage_LC_HUM.changeAttendanceCount(dt,'Increase');
            system.assertEquals(response1,'2');
            
            String response2 =  CRMRetail_HomePage_LC_HUM.changeAttendanceCount(dt,'Reduce');
            system.assertEquals(response2,'1');
            Attendance__c att = [Select id, Current_Attendance__c FROM Attendance__c WHERE Current_Attendance__c = 1 LIMIT 1];
            system.assertNotEquals(att, null);
            
            dt = dt.addDays(-1);
            String response3 =  CRMRetail_HomePage_LC_HUM.changeAttendanceCount(dt,'Reduce');
            system.assertEquals(response3,'1');
            Attendance__c att1 = [Select id, Current_Attendance__c FROM Attendance__c WHERE Current_Attendance__c = 1 LIMIT 1];
            system.assertNotEquals(att1, null);  
            test.stopTest();
        }
        
    }
    
    @isTest static void testAttendanceEvaluation1()
    {
        User testUser = [SELECT Id, Name,LastName FROM User WHERE LastName = 'test User1'];
        system.assertEquals(testUser.LastName, 'test User1');
        system.runAs(testUser)
        {        
            Date dt = date.today();
            date yestDate = dt.addDays(-2);
            String strYestDate = yestDate.format();
            Cache.Session.Put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CURRINTDATE, strYestDate);
            Cache.Session.Put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CURRENTLOCATION, 'Florida');
            test.startTest();
            String dedAtt = CRMRetail_HomePage_LC_HUM.deduceAttendance();
            system.assertEquals(dedAtt, '0');
            test.stopTest();
        }
    }
    
    @isTest static void testFetchAccountDetails()
    {
        User testUser = [SELECT Id, Name,LastName FROM User WHERE LastName = 'test User1'];
        system.assertEquals(testUser.LastName, 'test User1');
        system.runAs(testUser)
        {
            List<Account> accList = new list<Account>();
            accList = [SELECT Id, FirstName, LastName FROM Account WHERE GCM_Visitor_Barcode__c = '123456780'];
            system.assertEquals(accList.size(), 1);
            test.startTest();
            Account acc = CRMRetail_HomePage_LC_HUM.fetchAccountDetails(accList[0].Id);
            system.assertEquals(acc.GCM_Visitor_Barcode__c, '123456780');
            test.stopTest();
        }
    }
    
    @isTest static void testProcessMemberIdSearch()
    {
        User testUser = [SELECT Id, Name,LastName FROM User WHERE LastName = 'test User1'];
        system.assertEquals(testUser.LastName, 'test User1');
        system.runAs(testUser)
        {
            List<Member_ID__c> memIdList = [SELECT Id, Name FROM Member_ID__c WHERE Name = 'H555555555'];
            system.assertEquals(memIdList.size(), 1);
            test.startTest();
            List<CRMRetail_SearchResults_DTO_HUM> lstOfRsultsObtained = CRMRetail_HomePage_LC_HUM.processMemberIdSearch(memIdList[0].Name);            
            System.assertNotEquals(NULL,lstOfRsultsObtained);
            test.stopTest();
        }
    }
    
    @isTest static void testFetchStateValue()
    {
        User testUser = [SELECT Id, Name,LastName FROM User WHERE LastName = 'test User1'];
        system.assertEquals(testUser.LastName, 'test User1');
        system.runAs(testUser)
        {
            test.startTest();
            List<string> stateValue = CRMRetail_HomePage_LC_HUM.fetchStateValue();
            System.assertNotEquals(stateValue, null);
            Cache.Session.put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CURRENTLOCATION, '1234567-Virginia');
            Cache.Session.put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CURRINTDATE, Date.today().format());
            Map<string,string> locationValueMap = CRMRetail_HomePage_LC_HUM.fetchLocationValue();
            system.assertEquals(locationValueMap.size(), 2);
            test.stopTest();
        }
    }
    
    @isTest Static void prepareTestDataForSearchCriteria2()
    {   
        User oCCSUser = [Select id,UserName from User where LastName = 'test User1' LIMIT 1 ];
        system.assertEquals(oCCSUser.UserName,'user1user1@humcrmstest.com'); 
        User nonSUser = [Select id,UserName from User where LastName = 'test User3' LIMIT 1 ];
        system.assertEquals(nonSUser.UserName,'user3user3@humcrmstest.com');
        Storefront_Location__c locobj =[Select Id,Name,Location_Type__c from Storefront_Location__c LIMIT 1];
        system.assertNotEquals(locobj, null);
        Storefront_I_Type__c InteractionType1 = [select Id, Name FROM Storefront_I_Type__c Limit 1];
        system.assertNotEquals(InteractionType1, null);
        Member_Id__c memberID = [select id,name,Policy_Member__r.Member__r.Recordtype.name from Member_Id__c where name = 'H555555555' limit 1];
        system.assertEquals('H555555555',memberID.Name);
        Member_Id__c memberIDNonMember = [select id,name,Policy_Member__r.Member__r.Recordtype.name from Member_Id__c where name = 'H500000000' limit 1];        
        system.assertEquals('H555555555',memberID.Name);
        if(!Cache.Session.contains(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CURRENTLOCATION)){
            Cache.Session.Put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CURRENTLOCATION,locobj.Id +'-'+ locobj.Name);
        }
        if(!Cache.Session.contains(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CURRINTDATE)) {
            Datetime myDateTime = system.Now();
            String formatted = myDateTime.formatGMT('yyyy-MM-dd');
            Cache.Session.Put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CURRINTDATE, formatted);
        }
        Id [] fixedSearchResults= new Id[2];
        fixedSearchResults[0] = memberIDNonMember.id;
        Test.setFixedSearchResults(fixedSearchResults);
        System.runAs(oCCSUser){
            Map<string,String> resultmap = CRMRetail_HomePage_LC_HUM.processCheckIn('H555555555');            
			system.assertEquals(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_NOMATCHERROR,resultmap.get('Error'));
            
            Map<string,String> resultmap1 = CRMRetail_HomePage_LC_HUM.processCheckIn('H555555555');            
            system.assertEquals(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_NOMATCHERROR,resultmap.get('Error'));
                           
        }
    }
    
    @isTest static void testInactiveMember()
    {
        User oCCSUser = [Select id,UserName from User where LastName = 'test User1' LIMIT 1 ] ;
        system.assertEquals(oCCSUser.UserName,'user1user1@humcrmstest.com');        
        Storefront_Location__c locobj =[Select Id,Name,Location_Type__c from Storefront_Location__c LIMIT 1];
        system.assertNotEquals(locobj, null);
        Storefront_I_Type__c InteractionType1 = [select Id, Name FROM Storefront_I_Type__c Limit 1];
        system.assertNotEquals(InteractionType1, null);
        Member_Id__c memberID = [select id,name,Policy_Member__r.Member__r.Recordtype.name from Member_Id__c where name = 'H56789032' limit 1];
        system.assertNotEquals(memberID, null);
        Account inactiveAccount = [select Id, Name, Birthdate__c, PersonEmail,PersonMailingState, PersonMailingPostalCode, 
                        RecordType.Name, PersonHomePhone from Account where LastName = 'Ambrose' and RecordType.Name = 'member'LIMIT 1];
        system.assertNotEquals(inactiveAccount, null);
        Account snonMemberAcc = [select Id, Name, Birthdate__c, GCM_Visitor_Barcode__c,PersonEmail,PersonMailingState, PersonMailingPostalCode, 
                        RecordType.Name, PersonHomePhone from Account where GCM_Visitor_Barcode__c = 'GC7654322' LIMIT 1];
        system.assertNotEquals(snonMemberAcc, null);
        System.runAs(oCCSUser){                        
            if(!Cache.Session.contains(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CURRENTLOCATION)){
                Cache.Session.Put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CURRENTLOCATION,locobj.Id +'-'+ locobj.Name);
            }
                                                if(!Cache.Session.contains(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CURRINTDATE)) {
                Datetime myDateTime = system.Now();
                String formatted = myDateTime.formatGMT('yyyy-MM-dd');
                Cache.Session.Put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CURRINTDATE, formatted);
            }             
            	Id [] fixedSearchResults= new Id[3];
                fixedSearchResults[0] = memberID.id;
                fixedSearchResults[1] = snonMemberAcc.id;
                Test.setFixedSearchResults(fixedSearchResults);
                Map<string,String> resultmap = CRMRetail_HomePage_LC_HUM.processCheckIn('H5H6789032');                
                system.assertNotEquals(NULL,resultmap);
                Map<string,String> resultmap1 = CRMRetail_HomePage_LC_HUM.processInactiveMemberCheckin(inactiveAccount.Id,inactiveAccount.Name,snonMemberAcc.Id,'',GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_DISPLAYDATE);                
                system.assertNotEquals(NULL,resultmap);
        }
    }
    
     @isTest Static void prepareTestDataForSearchCriteria1()
    {    
        
        User oCCSUser = [Select id,UserName from User where LastName = 'test User1' LIMIT 1 ] ;
        system.assertEquals(oCCSUser.UserName,'user1user1@humcrmstest.com');        
        User nonStorefrontSUser = [Select id,UserName from User where LastName = 'test User5' LIMIT 1 ] ;
        system.assertEquals(nonStorefrontSUser.UserName,'user5user5@humcrmstest.com');           
        Id oCRMRetailVisitorType = Schema.SObjectType.Account.getRecordTypeInfosByName().get('CRMRetail Visitor').getRecordTypeId();
        
        Account newacc = new Account(FirstName='John',LastName = 'Davis',Birthdate__c= '01/01/1980' );
        system.assertEquals(newacc.FirstName, 'John');  
        Account visAccount = [select Id, Firstname, lastname, GCM_Visitor_Barcode__c,Shipping_FIPS_Desc__c from Account where GCM_Visitor_Barcode__c = 'GC7654323'];        
        system.assertEquals('Peter', visAccount.Firstname);
        Storefront_Location__c locobj =[Select Id,Name,Location_Type__c from Storefront_Location__c LIMIT 1];
        system.assertNotEquals(locobj, null);
        Storefront_I_Type__c InteractionType1 = [select Id, Name FROM Storefront_I_Type__c Limit 1];
        system.assertNotEquals(InteractionType1, null);
        if(!Cache.Session.contains(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CURRENTLOCATION)){
                Cache.Session.Put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CURRENTLOCATION,locobj.Id +'-'+ locobj.Name);
            }
            if(!Cache.Session.contains(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CURRINTDATE)) {
                Datetime myDateTime = system.Now();
                String formatted = myDateTime.formatGMT('yyyy-MM-dd');
                Cache.Session.Put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CURRINTDATE, formatted);
            } 
        System.runAs(oCCSUser){            
            Map<string,String> resultmap = CRMRetail_HomePage_LC_HUM.processNewCheckIn('GC3456780',newacc);            
            system.assertEquals('true', resultmap.get('isSuccess'));
            Storefront_Interaction__c siObj = [Select id,SDoH__c,Account__r.Enterprise_Id__c, Visitor_Id__c, First_Time_Visitor__c, Category__c FROM Storefront_Interaction__c WHERE Visitor_Id__c = 'GC3456780'];
            system.assertEquals(siObj.First_Time_Visitor__c, true);                        
            Map<string,String> resultmap1 = CRMRetail_HomePage_LC_HUM.updateBarcodeCheckIn('GC3456780',newacc.Id);            
            system.assertEquals('false', resultmap1.get('isSuccess'));
        }
        
         System.runAs(nonStorefrontSUser){            
            try{
                Map<string,String> resultmap = CRMRetail_HomePage_LC_HUM.processNewCheckIn('GC3456780',newacc);                
                system.assertEquals('false', resultmap.get('isSuccess'));
                Storefront_Interaction__c siObj = [Select Visitor_Id__c FROM Storefront_Interaction__c WHERE Visitor_Id__c = 'GC3456780' ORDER BY CreatedDate DESC LIMIT 1];                
                System.assertEquals('GC3456780',siObj.Visitor_Id__c);
            }
            catch(exception ex){
                system.assertEquals('Script-thrown exception', ex.getMessage());
            }
           try{   
                Map<string,String> resultmap = CRMRetail_HomePage_LC_HUM.updateBarcodeCheckIn('GC3456870',visAccount.Id);              	
            }
            catch(exception ex){
                system.assertEquals('Script-thrown exception', ex.getMessage());
            }
        }
    }
    @isTest static void testVisitorAccount() 
    {
        User oCCSUser = [Select id,UserName from User where LastName = 'test User1' LIMIT 1 ] ;
        system.assertEquals(oCCSUser.UserName,'user1user1@humcrmstest.com');        
        Storefront_Location__c locobj =[Select Id,Name,Location_Type__c from Storefront_Location__c LIMIT 1];
        system.assertNotEquals(locobj, null);
        Storefront_I_Type__c InteractionType1 = [select Id, Name FROM Storefront_I_Type__c Limit 1];
        system.assertNotEquals(InteractionType1, null);
        Member_Id__c memberID = [select id,name,Policy_Member__r.Member__r.Recordtype.name from Member_Id__c where name = 'H56789032' limit 1];
                                system.assertNotEquals(memberID, null);        
        Account snonMemberAcc = [select Id, Name, Birthdate__c, GCM_Visitor_Barcode__c,PersonEmail,PersonMailingState, PersonMailingPostalCode, 
                        RecordType.Name, PersonHomePhone from Account where GCM_Visitor_Barcode__c = 'GC7654322' LIMIT 1];
        system.assertNotEquals(snonMemberAcc, null);
        System.runAs(oCCSUser){                        
            if(!Cache.Session.contains(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CURRENTLOCATION)){
                Cache.Session.Put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CURRENTLOCATION,locobj.Id +'-'+ locobj.Name);
            }
            if(!Cache.Session.contains(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CURRINTDATE)) {
                Datetime myDateTime = system.Now();
                String formatted = myDateTime.formatGMT('yyyy-MM-dd');
                Cache.Session.Put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CURRINTDATE, formatted);
            }             
            try{
                Map<string,String> resultmap = CRMRetail_HomePage_LC_HUM.processCheckIn('GC7654322');                 
                system.assertEquals('true', resultmap.get('isSuccess'));
                List<Storefront_Interaction__C> lstOfInts = [SELECT Account__c from Storefront_Interaction__C ORDER BY CreatedDate DESC LIMIT 1];
            	system.assertEquals('GC7654322',lstOfInts[0].Visitor_Id__c);
            }
            catch(exception ex){
                system.assertEquals('SObject row was retrieved via SOQL without querying the requested field: Storefront_Interaction__c.Visitor_ID__c', ex.getMessage());
            }
        }
    }
    @isTest static void testVisitorIdPopulation()
    {
        User testUser = [SELECT Id,LastName,Name FROM User WHERE LastName = 'test UserCRMRetail'];
        system.assertEquals('test UserCRMRetail',testUser.LastName);
        
        List<Account> lstOfAccs = [select Id,Name,(Select Id from Policy_Members__r where ETL_Record_Deleted__c = false),FirstName,LastName,PersonBirthdate,PersonMailingPostalCode,PersonHomePhone,PersonMailingState,RecordType.Name from Account ORDER BY CreatedDate];                
        system.assertEquals('Member',lstOfAccs.get(0).FirstName);
        system.assertEquals('Member',lstOfAccs.get(0).RecordType.Name);
        system.assertEquals('Member',lstOfAccs.get(1).FirstName);
        system.assertEquals('Member',lstOfAccs.get(2).FirstName);
        system.assertEquals('NopolicyMember',lstOfAccs.get(4).FirstName);
        system.assert(lstOfAccs.get(4).Policy_Members__r.size() == 0);
        
        Policy_Member__c pmRef = [SELECT Id,Name,Member__c from Policy_member__c where Name='H5867344700' LIMIT 1];
        system.assertEquals('H5867344700', pmRef.Name);

        //Policy_Member__c pmRef1 = [SELECT Id,Name,Member__c from Policy_member__c where Name='H5867344701' LIMIT 1];
        //system.assertEquals('H5867344700', pmRef.Name);
        
        List<Member_Id__c> lstMemIds = [SELECT Id,Name from Member_Id__c where Name='H71820112' LIMIT 1];
        system.assert(lstMemIds.size()>0);
        system.assertEquals('H71820112',lstMemIds.get(0).Name);
        
        Storefront_Location__c locobj =[Select Id,Name from Storefront_Location__c LIMIT 1];
        system.assertNotEquals(locobj, null);     
        
        Map<String,String> accountMap = new  Map<String,String>();
        
        System.runAs(testUser){
            Id [] fixedSearchResults= new Id[10];                  
            fixedSearchResults[0] = lstOfAccs[0].Id;
            fixedSearchResults[1] = lstOfAccs[1].Id;
            fixedSearchResults[2] = lstOfAccs[2].Id;
            fixedSearchResults[3] = lstMemIds.get(0).Id;
            Test.setFixedSearchResults(fixedSearchResults); 
            
            String personBirthDate1 =  String.valueOf(lstOfAccs[0].PersonBirthdate);
            String accountSearchString = '{"FirstName":"'+lstOfAccs[0].FirstName+'","LastName":"'+lstOfAccs[0].LastName+'","PersonMailingPostalCode":"'+lstOfAccs[0].PersonMailingPostalCode+'","PersonHomePhone":"'+lstOfAccs[0].PersonHomePhone+'","personmailingstate":"'+lstOfAccs[0].PersonMailingState+'","PersonBirthdate":"'+personBirthDate1+'"}';
            Test.startTest();
            if(!Cache.Session.contains(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CURRENTLOCATION)){
                Cache.Session.Put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CURRENTLOCATION,locobj.Id +'-'+ locobj.Name);
            }
            if(!Cache.Session.contains(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CURRINTDATE)) {
                Datetime myDateTime = system.Now(); 
                String formatted = myDateTime.formatGMT('yyyy-MM-dd');
                Cache.Session.Put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CURRINTDATE, formatted);
            } 
            List<CRMRetail_SearchResults_DTO_HUM> lstOfAccountsSearched = CRMRetail_HomePage_LC_HUM.runProcessAccountSearchLogic(accountSearchString,false);
            system.assert(lstOfAccountsSearched.size()>0);
            system.assertEquals('Member',lstOfAccountsSearched.get(0).FirstName);
            
            List<Policy_Member__C> lstPmRefs = [SELECT Id,Name,End_Date__C from Policy_Member__C where Name='H5867344700' LIMIT 1]; 
            system.assertEquals('H5867344700',lstPmRefs.get(0).Name);
            lstPmRefs.get(0).Name = '95867344700';
            lstPmRefs.get(0).End_Date__c= null;
            update lstPmRefs.get(0);
            system.assertEquals('95867344700',lstPmRefs.get(0).Name);
            
            lstOfAccountsSearched = CRMRetail_HomePage_LC_HUM.runProcessAccountSearchLogic(accountSearchString,false);
            system.assert(lstOfAccountsSearched.size()>0);
            system.assertEquals('Member',lstOfAccountsSearched.get(0).FirstName);
        
        	Date dToday = Date.valueOf(System.today());
            String sFormInvalidEndDate = String.valueOf(dToday.month())+'/'+String.valueOf(dToday.day())+'/'+String.valueOf(dToday.year()-1);	
        	String sFormValidEndDate = String.valueOf(dToday.month())+'/'+String.valueOf(dToday.day())+'/'+String.valueOf(dToday.year()+1);	
        	lstPmRefs.get(0).End_Date__c= sFormInvalidEndDate;
            update lstPmRefs.get(0);
            system.assertEquals(sFormInvalidEndDate,lstPmRefs.get(0).End_Date__c);
        
        	lstOfAccountsSearched = CRMRetail_HomePage_LC_HUM.runProcessAccountSearchLogic(accountSearchString,false);
            system.assert(lstOfAccountsSearched.size()>0);
            system.assertEquals('Member',lstOfAccountsSearched.get(0).FirstName);
        
        	lstPmRefs.get(0).End_Date__c= sFormValidEndDate;
            update lstPmRefs.get(0);
            system.assertEquals(sFormValidEndDate,lstPmRefs.get(0).End_Date__c);
        	
            Map<String, String> returnMap = CRMRetail_HomePage_LC_HUM.processCheckIn(lstMemIds.get(0).Name);            
            system.assertNotEquals(NULL,returnMap.get('InactiveMember'));
            
            lstMemIds[0].Policy_Member__c = pmRef.Id;
            update lstMemIds[0];
            system.assertEquals(pmRef.Id,lstMemIds[0].Policy_Member__c);
            
            returnMap = CRMRetail_HomePage_LC_HUM.processCheckIn(lstMemIds.get(0).Name);
            system.assertEquals('true',returnMap.get('isSuccess'));
            
        	accountMap.put(lstOfAccs[0].id, lstOfAccs[0].name);            
            Map<string,string> res5 = CRMRetail_HomePage_LC_HUM.initiateVisitorCheckIn(accountMap,'oCheckIn');
            system.assertEquals('true',res5.get('isSuccess'));
            List<Storefront_Interaction__C> lstOfInts = [SELECT Account__c from Storefront_Interaction__C ORDER BY CreatedDate DESC LIMIT 1];
            system.assertEquals(lstOfInts[0].Account__c,lstOfAccs[0].id);
            Map<string,string> res6 = CRMRetail_HomePage_LC_HUM.initiateVisitorCheckIn(accountMap,'vCheckIn');
            system.assertEquals('false',res6.get('isSuccess'));
        	locobj.Location_Type__c = GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CATEGORY_VIRTUAL;
        	update locobj;
        	system.assertEquals(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CATEGORY_VIRTUAL,locobj.Location_Type__c);
                    
        	accountMap.clear();
        	accountMap.put(lstOfAccs[4].id, lstOfAccs[4].name);        	
        	CRMRetail_Contacted_HUM.isCalledFromContacted = true;
        	res5 = CRMRetail_HomePage_LC_HUM.initiateVisitorCheckIn(accountMap,'vcheckin');
        	system.assertEquals('false',res5.get('isSuccess')); 
            
        	pmRef.Member__c = lstOfAccs[4].ID;
            update pmRef;
            system.assertEquals(lstOfAccs[4].ID, pmRef.Member__c);
        	
        	CRMRetail_Contacted_HUM.isCalledFromContacted = true;
            Datetime myDateTime = system.Now(); 
            String formatted = myDateTime.formatGMT('dd/MM/yyyy');
            Cache.Session.Put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CURRINTDATE, formatted);
        	res5 = CRMRetail_HomePage_LC_HUM.initiateVisitorCheckIn(accountMap,'vCheckIn');
        	system.assertEquals('false',res5.get('isSuccess'));
            
            accountMap.clear();
            CRMRetail_Contacted_HUM.isCalledFromContacted = true;
            accountMap.put(lstOfAccs[5].id, lstOfAccs[5].name);            
            Map<string,string> res7 = CRMRetail_HomePage_LC_HUM.initiateVisitorCheckIn(accountMap,'oCheckIn');
            system.assertEquals('false',res7.get('isSuccess'));
            Test.stopTest();               
        }
    }
    @isTest static void testVisitorIdFutureAndExpired()
    {
        User testUser = [SELECT Id,LastName,Name FROM User WHERE LastName = 'test UserCRMRetail'];
        system.assertEquals('test UserCRMRetail',testUser.LastName);
        
        List<Account> lstOfAccs = [select Id,Name,ETL_Record_Deleted__C,(Select Id,Product__c,ETL_Record_Deleted__C from Policy_Members__r where ETL_Record_Deleted__c = false),FirstName,LastName,PersonBirthdate,PersonMailingPostalCode,PersonHomePhone,PersonMailingState,RecordType.Name from Account ORDER BY CreatedDate];         
        system.assertEquals(15,lstOfAccs.size());          
        
        Storefront_Location__c locobj =[Select Id,Name from Storefront_Location__c LIMIT 1];
        system.assertNotEquals(locobj, null);     
        
        Map<String,String> accountMap = new  Map<String,String>();
    	System.runAs(testUser){            
            Test.startTest();
            if(!Cache.Session.contains(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CURRENTLOCATION)){
                Cache.Session.Put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CURRENTLOCATION,locobj.Id +'-'+ locobj.Name);
            }
            if(!Cache.Session.contains(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CURRINTDATE)) {
                Datetime myDateTime = system.Now(); 
                String formatted = myDateTime.formatGMT('yyyy-MM-dd');
                Cache.Session.Put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CURRINTDATE, formatted);
            } 
            CRMRetail_Contacted_HUM.isCalledFromContacted = true;
            Datetime myDateTime = system.Now(); 
            String formatted = myDateTime.formatGMT('MM/dd/yyyy');
            Cache.Session.Put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CURRINTDATE, formatted);
                
            accountMap.put(lstOfAccs[5].id, lstOfAccs[5].name);       
            
            accountMap.clear();
            accountMap.put(lstOfAccs[6].id, lstOfAccs[6].name);         
                        
            Test.stopTest();
        }
    }
    @isTest static void testProcessMemberIdSearchLogic()
    {
        User testUser = [SELECT Id,UserName,Name FROM User WHERE LastName = 'test User1'];
        system.assertEquals(testUser.UserName,'user1user1@humcrmstest.com'); 
        User testUser3 = [SELECT Id,UserName,Name,LastName FROM User WHERE LastName = 'test User5'];
        system.assertEquals(testUser3.UserName,'user5user5@humcrmstest.com');
        
        List<Account> oAccount = [select Id,Name,FirstName,LastName,PersonBirthdate,PersonMailingPostalCode,PersonHomePhone,PersonMailingState,RecordType.Name from Account ORDER BY CreatedDate];
        system.assertNotEquals(0,oAccount.size());
        
        Id crmRetailRecType = Schema.SObjectType.Account.getRecordTypeInfosByName().get('CRMRetail Visitor').getRecordTypeId();
        Id memRecType = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId();
        
        List<Member_Id__c> lstMemberIds = [Select Id,Name,ETL_Record_Deleted__C,Policy_Member__r.status__c from Member_Id__c where (Name='H555555555' OR Name='H56789032') AND Policy_Member__r.Member__r.RecordtypeId =: memRecType AND Policy_Member__r.ETL_Record_Deleted__c = false];        
        system.assertEquals(false,lstMemberIds.get(0).ETL_Record_Deleted__C);
        system.assertEquals(false,lstMemberIds.get(1).ETL_Record_Deleted__C);
        
        Storefront_Location__c locobj =[Select Id,Name from Storefront_Location__c LIMIT 1];
        system.assertNotEquals(locobj, null);
        Map<String,String> accountMap = new  Map<String,String>();
        
        system.runAs(testUser){
            Id [] fixedSearchResults= new Id[10];                  
            fixedSearchResults[0] = lstMemberIds[0].Id;
            fixedSearchResults[1] = oAccount[0].Id;
            Test.setFixedSearchResults(fixedSearchResults); 
            
            String ostring4 = '{"PolicyId":"H555555555"}';
            Test.startTest();
            if(!Cache.Session.contains(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CURRENTLOCATION)){
                Cache.Session.Put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CURRENTLOCATION,locobj.Id +'-'+ locobj.Name);
            }
            if(!Cache.Session.contains(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CURRINTDATE)) {
                Datetime myDateTime = system.Now(); 
                String formatted = myDateTime.formatGMT('yyyy-MM-dd');
                Cache.Session.Put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CURRINTDATE, formatted);
            } 
            accountMap.put(oAccount[0].id, oAccount[0].name);
            List<CRMRetail_SearchResults_DTO_HUM> res3 = CRMRetail_HomePage_LC_HUM.processMemberIdSearch(ostring4);
            system.assertEquals('04/06/1985',res3[0].Birthdate);
            system.runAs(testUser3){
                try{
                    Map<string,string> res7 = CRMRetail_HomePage_LC_HUM.initiateVisitorCheckIn(accountMap,'oCheckIn');
            		Map<string,string> res8 = CRMRetail_HomePage_LC_HUM.initiateVisitorCheckIn(accountMap,'vCheckIn');
                }
                catch(Exception ex){
                    system.assertEquals('Script-thrown exception', ex.getMessage());
                }
            }
            Test.stopTest();
        }
    }
     @isTest static void testAcknowledgeNotifications()
    {
        User testUser = [SELECT Id, Name,LastName FROM User WHERE LastName = 'test User1'];
        system.assertEquals(testUser.LastName, 'test User1');
        User nonStorefrontSUser = [Select id,UserName from User where LastName = 'test User5' LIMIT 1 ] ;
        system.assertEquals(nonStorefrontSUser.UserName,'user5user5@humcrmstest.com'); 
        system.runAs(testUser)
        {
            test.startTest();   
            List<Account> oAcc = [select Id, Waiver_Date__c,LastName from Account where LastName = 'Crawley' and Waiver_Date__c !=null]; 
            system.assertEquals(oAcc.get(0).LastName, 'Crawley');
            List<String> accIds = new List<String>();            
            String currentLocation;
            List<Storefront_Location__C> currLoc = [Select Id,Name,Location_Type__C from Storefront_Location__C where Name= 'Virginia' LIMIT 1];
            system.assertEquals(currLoc.get(0).Name,'Virginia');
            accIds.add(oAcc.get(0).Id);
            boolean flag = true;            
            String  jsonInput='[{"waiverDate":"'+ flag +'","accId":"' + oAcc.get(0).Id + '"},{"veteran":"'+ flag +'","accId":"' + oAcc.get(0).Id + '"},{"agingIn":"'+ flag +'","accId":"' + oAcc.get(0).Id + '"},{"SDoH":"'+ flag +'","accId":"' + oAcc.get(0).Id + '"},{"newMember":"'+ flag +'","accId":"' + oAcc.get(0).Id + '"}]';                                    
            Boolean result = CRMRetail_HomePage_LC_HUM.acknowledgeNotifications(jsonInput,accIds,String.valueOf(currLoc.get(0).Id));
            system.assertEquals(result, True);
            List<Storefront_Interaction__c> lstOfInts = [SELECT Id,Name,Category__c,Account__C from Storefront_Interaction__c ORDER BY CreatedDate DESC LIMIT 4];
            system.assertEquals(oAcc.get(0).Id,lstOfInts[0].Account__c);
            system.assertEquals(oAcc.get(0).Id,lstOfInts[1].Account__c);
            system.assertEquals(oAcc.get(0).Id,lstOfInts[2].Account__c);
            system.assertEquals(oAcc.get(0).Id,lstOfInts[3].Account__c);
        }
        system.runAs(nonStorefrontSUser)
        {              
            List<Account> oAcc = [select Id, Waiver_Date__c,LastName from Account where LastName = 'Thor' and Waiver_Date__c !=null];             
            system.assertEquals(oAcc.get(0).LastName, 'Thor');
            List<String> accIds = new List<String>();            
            String currentLocation;
            List<Storefront_Location__C> currLoc = [Select Id,Name,Location_Type__C from Storefront_Location__C where Name= 'Virginia' LIMIT 1];            
            system.assertEquals(currLoc.get(0).Name,'Virginia');
            accIds.add(oAcc.get(0).Id);
            boolean flag = true;            
            String  jsonInput='[{"waiverDate":"'+ flag +'","accId":"' + oAcc.get(0).Id + '"},{"veteran":"'+ flag +'","accId":"' + oAcc.get(0).Id + '"},{"agingIn":"'+ flag +'","accId":"' + oAcc.get(0).Id + '"},{"SDoH":"'+ flag +'","accId":"' + oAcc.get(0).Id + '"},{"newMember":"'+ flag +'","accId":"' + oAcc.get(0).Id + '"}]';                                    
            try{
                Boolean result = CRMRetail_HomePage_LC_HUM.acknowledgeNotifications(jsonInput,accIds,String.valueOf(currLoc.get(0).Id));
            }
            catch(Exception ex)
            {
                system.assertEquals('Script-thrown exception', ex.getMessage());
            }
            test.stopTest();               
        }
    }
    @isTest static void testgetUTCDate()
    {
        User testUser = [SELECT Id, Name,LastName FROM User WHERE LastName = 'test User1'];
        system.assertEquals(testUser.LastName, 'test User1');
        system.runAs(testUser)
        {
            test.startTest(); 
            if(!Cache.Session.contains(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CURRINTDATE)) {
                Datetime myDateTime = system.Now();
                String formatted = myDateTime.formatGMT('mm/dd/yyyy');
                Cache.Session.Put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CURRINTDATE, formatted);
            }
            DateTime dt = CRMRetail_HomePage_LC_HUM.getUTCDateTime('displayDate');
            system.assertNotEquals(dt, null);
            Datetime myDateTime = system.Now();
            String formatted = myDateTime.formatGMT('yyyy-mm-dd');
            Cache.Session.Put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CURRINTDATE, formatted);
            DateTime dt1 = CRMRetail_HomePage_LC_HUM.getUTCDateTime('displayDate');
           	system.assertNotEquals(dt1, null);
            test.stopTest();
        }  
    }
    @isTest static void testMobileCache()
    {
        User testUser = [SELECT Id, Name,LastName FROM User WHERE LastName = 'test User1'];
        system.assertEquals(testUser.LastName, 'test User1');
        system.runAs(testUser)
        {
            String formFactor = 'Small';
            CRMRetail_HomePage_LC_HUM.updateMobileCache(formFactor);
            System.assertEquals(formFactor, (String)Cache.Session.get(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CURRENTDEVICE));
        }   
    }
    
     @isTest static void testMobilePhoneSearch()
    {
        User testUser = [SELECT Id,LastName,Name FROM User WHERE LastName = 'test UserCRMRetail'];
        Assert.areEqual('test UserCRMRetail',testUser.LastName);
        
        System.runAs(testUser)
        {
            Storefront_Location__c locobj =[Select Id,Name from Storefront_Location__c LIMIT 1];
            Assert.areNotEqual(locobj, null); 
            
            Test.startTest();
            if(!Cache.Session.contains(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CURRENTLOCATION)){
                Cache.Session.Put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CURRENTLOCATION,locobj.Id +'-'+ locobj.Name);
            }
            if(!Cache.Session.contains(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CURRINTDATE)) {
                Datetime myDateTime = system.Now(); 
                String formatted = myDateTime.formatGMT('yyyy-MM-dd');
                Cache.Session.Put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CURRINTDATE, formatted);
            } 
            
            List<CRMRetail_SearchResults_DTO_HUM> searchdata = CRMRetail_HomePage_LC_HUM.runProcessAccountSearchLogic('{"'+System.label.CRMRetail_Person_HomePhone_Key+'":"4231231231"'+'}',true);
            Assert.areEqual('(423) 123-1231', searchdata[0].PersonHomePhone);
            
            searchdata = CRMRetail_HomePage_LC_HUM.runProcessAccountSearchLogic('{"'+System.label.CRMRetail_Person_HomePhone_Key+'":"0001113333"'+'}',true);
            Assert.areEqual(0, searchdata.size());
            
            searchdata = CRMRetail_HomePage_LC_HUM.runProcessAccountSearchLogic('{"'+System.label.CRMRetail_Person_HomePhone_Key+'":""'+'}',true);
            Assert.areEqual(0, searchdata.size());
	    
            searchdata = CRMRetail_HomePage_LC_HUM.runProcessAccountSearchLogic('{"'+GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_FIRSTNAME+'":"VoidData",'+'"LastName":"VoidData"'+'}',false);
            Assert.areEqual(0, searchdata.size());
            Test.stopTest();
        }
    }
    
    @isTest static void testHNCCards()
    {
        User oCCSUser = [Select id,UserName from User where LastName = 'test User1' LIMIT 1 ];
        assert.areEqual(oCCSUser.UserName,'user1user1@humcrmstest.com'); 
        
        System.runAs(oCCSUser)
        {
            Storefront_Location__c locobj =[Select Id,Name from Storefront_Location__c LIMIT 1];
            Assert.areNotEqual(locobj, null); 
            
            Test.startTest();
            if(!Cache.Session.contains(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CURRENTLOCATION)){
                Cache.Session.Put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CURRENTLOCATION,locobj.Id +'-'+ locobj.Name);
            }
            if(!Cache.Session.contains(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CURRINTDATE)) {
                Datetime myDateTime = system.Now(); 
                String formatted = myDateTime.formatGMT('yyyy-MM-dd');
                Cache.Session.Put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CURRINTDATE, formatted);
            } 
            
            Map<String, String> returnMap = CRMRetail_HomePage_LC_HUM.processCheckIn('HNC123123123');
            Assert.areEqual(returnMap.get('isSuccess'),'true');
            Test.stopTest();
        }
    }
    
    @isTest static void testPreferredNameSearch()
    {
        
        User testUser = [SELECT Id,LastName,Name FROM User WHERE LastName = 'test UserCRMRetail'];
        Assert.areEqual('test UserCRMRetail',testUser.LastName);
        
        
        System.runAs(testUser){
            
            List<Account> lstOfAccs = [select Id,Name,(Select Id from Policy_Members__r where ETL_Record_Deleted__c = false),FirstName,LastName,PersonBirthdate,PersonMailingPostalCode,PersonHomePhone,PersonMailingState,RecordType.Name from Account where FirstName= 'Hosain'];                
            
            Id [] fixedSearchResults= new Id[10];                  
            fixedSearchResults[0] = lstOfAccs[0].Id;
            Test.setFixedSearchResults(fixedSearchResults);
            
            
            Storefront_Location__c locobj =[Select Id,Name from Storefront_Location__c LIMIT 1];
            Assert.areNotEqual(locobj, null); 
            
            Test.startTest();
            if(!Cache.Session.contains(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CURRENTLOCATION)){
                Cache.Session.Put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CURRENTLOCATION,locobj.Id +'-'+ locobj.Name);
            }
            if(!Cache.Session.contains(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CURRINTDATE)) {
                Datetime myDateTime = system.Now(); 
                String formatted = myDateTime.formatGMT('yyyy-MM-dd');
                Cache.Session.Put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CURRINTDATE, formatted);
            } 
            String accountSearchString = '{"FirstName":"'+ 'Hosain' +'","LastName":"'+ 'Truh' +'","PersonMailingPostalCode":"'+ lstOfAccs[0].PersonMailingPostalCode  +'"}';
            
            List<CRMRetail_SearchResults_DTO_HUM> searchdata = CRMRetail_HomePage_LC_HUM.runProcessAccountSearchLogic(accountSearchString,false);
            Assert.areEqual(1, searchdata.size());
            
            accountSearchString = '{"FirstName":"'+ 'HT' +'","LastName":"'+ 'Truh' +'","PersonMailingPostalCode":"'+  '12345'  +'"}';
            searchdata = CRMRetail_HomePage_LC_HUM.runProcessAccountSearchLogic(accountSearchString,false);
            Assert.areEqual(1, searchdata.size());
            
            accountSearchString = '{"FirstName":"'+ 'VoidName' +'","LastName":"'+ 'Account' +'","PersonMailingPostalCode":"'+  '12345'  +'"}';
            searchdata = CRMRetail_HomePage_LC_HUM.runProcessAccountSearchLogic(accountSearchString,false);
            Assert.areEqual(0, searchdata.size());
            
            Test.stopTest();
        }
    }
    @isTest
    static void testMobileLocation()
    {
        User testUser = [SELECT Id,LastName,Name FROM User WHERE LastName = 'test UserCRMRetail'];
        system.assertEquals('test UserCRMRetail',testUser.LastName);
        
        Storefront_Location__c locobj =[Select Id,Name from Storefront_Location__c where name = 'Tampa' LIMIT 1];
        system.assertEquals(locobj.name, 'Tampa'); 
        
        System.runAs(testUser){ 
            Test.startTest();
            Cache.Session.Put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CURRENTLOCATION,locobj.Id +'-'+ locobj.Name);
            
            Storefront_Preference__c objPref = CRMRetail_HomePage_LC_HUM.saveDefaultLocationRecord('Tulsa');
            system.assertEquals(true,objPref.CRMRetail_Mob_Loc_Updated__c);
            system.assertEquals('Tulsa',objPref.Default_Location__r.Name);
            
            String newLocation = CRMRetail_HomePage_LC_HUM.getorSetLocationInCache('',false);
            
            List<Storefront_Preference__c> locationList = [SELECT Id, Default_Location__r.Id, Default_Location__r.Name, user__c,CRMRetail_Mob_Loc_Updated__c from Storefront_Preference__c where user__c = :UserInfo.getUserId() AND Default_Location__C != NULL AND Default_Location__r.Inactive_indicator__c != True LIMIT 1];
            system.assertEquals(false,locationList[0].CRMRetail_Mob_Loc_Updated__c);
            system.assertEquals(true,((String)Cache.Session.get(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CURRENTLOCATION)).contains('Tulsa'));
            Test.stopTest();
        }
    }
    
}
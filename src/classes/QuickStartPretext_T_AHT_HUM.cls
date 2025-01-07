/*******************************************************************************************************************************
Apex Class Name : QuickStartPretext_T_AHT_HUM
Version         : 1.0
Created On      : 01/24/2018
Function        : Class contains test methods QuickStartPretext_H_AHT_HUM.

Modification Log: 
 * Developer Name            Code Review                Date                       Description
 *------------------------------------------------------------------------------------------------------------------------------
* Subhamay Mandal                                       01/24/2018                Original Version
* Darshan Sharma			                            10/01/2018		            REQ - 374820: CTCI Model Update (RF+CRM)
* Subhamay Mandal            -----              		08/12/2019                  REQ - 406018 QuickStartPretext_X_AHT_HUM
 ********************************************************************************************************************************/
@isTest
public class QuickStartPretext_T_AHT_HUM {
    @testSetup static void setUpTestData() {
        User oUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oUser;
        system.assertEquals(oUser.lastname,'test User1');        
    }
    
    static testMethod void test() {            
        User oUser = [select Id,Name from User where name ='test User1' limit 1];                
        System.runAs(oUser) { 
            
            List<Classification_Type__c> lstClassType= new List<Classification_Type__c>();
            Classification_Type__c oClassType = new Classification_Type__c();
            oClassType.Name = 'Calls';
            oClassType.Is_Active__c = true;
            lstClassType.add(oClassType);
            
            insert lstClassType;
            
            Classification__c classification1 = New Classification__c(Name = 'Benefits', Is_Active__c = true);
            insert classification1;
            
            Intent__c intent1 = New Intent__c(Name = 'Accums', Is_Active__c = true);
            insert intent1;
           
            Intent__c intent2 = New Intent__c(Name = 'Verification', Is_Active__c = true);
            insert intent2;

            Intent__c intent3 = New Intent__c(Name = 'Rewards', Is_Active__c = true);
            insert intent3;
            
            List<CTCI_Junction__c> lstCTCI = new list<CTCI_Junction__c>();
            CTCI_Junction__c oCTCI1 = new CTCI_Junction__c();
            oCTCI1.Classification_Type__c = oClassType.id;
            oCTCI1.Classification__c = classification1.Id;
            oCTCI1.Intent__c = intent1.id;
            oCTCI1.IsActive__c = true;
            lstCTCI.add(oCTCI1);
            
            CTCI_Junction__c oCTCI2 = new CTCI_Junction__c();
            oCTCI2.Classification_Type__c = oClassType.id;
            oCTCI2.Classification__c = classification1.Id;
            oCTCI2.Intent__c = intent1.id;
            oCTCI2.IsActive__c = true;
            lstCTCI.add(oCTCI2);

            CTCI_Junction__c oCTCI3 = new CTCI_Junction__c();
            oCTCI3.Classification_Type__c = oClassType.id;
            oCTCI3.Classification__c = classification1.Id;
            oCTCI3.Intent__c = intent3.id;
            oCTCI3.IsActive__c = true;
            lstCTCI.add(oCTCI3);
            
            insert lstCTCI;
            
            Quick_Start_Pretext__c objQSP1 = new Quick_Start_Pretext__c(Is_Active__c = TRUE, CTCI_List_lkp__c = lstCTCI[0].Id, is_Frequently_Used__c = true);
            insert objQSP1;
            Quick_Start_Pretext__c objQSP2 = new Quick_Start_Pretext__c(Is_Active__c = TRUE, CTCI_List_lkp__c = lstCTCI[1].Id, is_Frequently_Used__c = true);
            insert objQSP2;
            
            // Create duplicate pretext
            try {
                Quick_Start_Pretext__c objQSP3 = new Quick_Start_Pretext__c(Is_Active__c = TRUE, CTCI_List_lkp__c = lstCTCI[1].Id, is_Frequently_Used__c = true);
                insert objQSP3 ;
            } catch(DMLException e) {
                System.debug('Insert Error'+ e+'---------'+e.getMessage());
                //system.assert(e.getMessage().contains(System.Label.UNIQUERECORDCTCI)); 
            }
            
            // Create duplicate pretext
            try {
                objQSP2.CTCI_List_lkp__c = lstCTCI[0].Id;
                update objQSP2 ;
            } catch(DMLException e) {
                System.debug('Update Error'+ e+'---------'+e.getMessage());
                //system.assert(e.getMessage().contains(System.Label.UNIQUERECORDCTCI)); 
            } 
            
            Quick_Start_Pretext__c objQSP = new Quick_Start_Pretext__c(Business_Groups__c='RSO');
            insert objQSP;
            ApexPages.StandardController sc = new ApexPages.StandardController(objQSP);
            QuickStartPretext_X_AHT_HUM oQSP = new QuickStartPretext_X_AHT_HUM(sc);
            
            oQSP.reset();
            oQSP.init();
            
            String sCTId = [select id from Classification_Type__c where Name='Calls' and Is_Active__c= true].Id;
            oQSP.sClassificationTypeIdSelect = sCTId +HUMConstants.HUM_PCP_DATESEPERATOR+ 'Calls';
            oQSP.getClassifactions();
            
            oQSP.sClassificationIdSelect = [select id from Classification__c where Name='Benefits' and Is_Active__c = true].Id;
            oQSP.getIntents();
            
            oQSP.sIntentIdSelect = [select id from Intent__c where Name='Rewards' and Is_Active__c = true].Id;
            
            oQSP.save();
        }
    }
}
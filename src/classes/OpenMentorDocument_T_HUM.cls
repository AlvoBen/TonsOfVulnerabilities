/**********************************************************************************************************************
Apex Class  : OpenMentorDocument_T_HUM
Description : Test class for OpenMentorDocument_C_HUM
Created By  : Siddharth Mehta
Created On  : 08/01/2017

Modification Log :
-----------------------------------------------------------------------------------------------------------------------
* Developer            Code Review         Date                    Description
* --------------------------------------------------------------------------------------------------------------------
* Siddharth Mehta                          08/01/2017              Original version
* Sai Ayyagari			164945		       01/19/2018		       Fixed test coverage REQ - 328929, store the list of exceptions captured
* Darshan Sharma                           05/02/2018              REQ - 361223 - Ability to update Mentor Links in QuickStart via Business Configuration
* Darshan Sharma			               10/03/2018		       REQ - 374820: CTCI Model Update (RF+CRM)
* Asish Behera			                   10/22/2018		       REQ - 377320: AHT - Claims Detail - Diagnosis Codes (RF)
**************************************************************************************************************************************************************/

@isTest
private class OpenMentorDocument_T_HUM {
    @testSetup static void testSetup() {
        
        List<Classification_Type__c> lstClassType= new List<Classification_Type__c>();
        Classification_Type__c oClassType = new Classification_Type__c();
        oClassType.Name = 'Calls';
        oClassType.Is_Active__c = true;
        lstClassType.add(oClassType);
        
        insert lstClassType;
        
        Classification__c classification = new Classification__c(Name = 'Benefits', Is_Active__c = True);
        Intent__c Intent = new Intent__c(Name = 'Rewards', Is_Active__c = True);
        Insert classification;
        Insert Intent;
        
        
        Intent__c Intent2 = new Intent__c(Name = 'Coordination', Is_Active__c = True);
        
        Insert Intent2;
        
        List<CTCI_Junction__c> lstCTCI = new list<CTCI_Junction__c>();
        CTCI_Junction__c oCTCI1 = new CTCI_Junction__c();
        oCTCI1.Classification_Type__c = oClassType.id;
        oCTCI1.Classification__c = classification.Id;
        oCTCI1.Intent__c = Intent.id;
        oCTCI1.IsActive__c = true;
        lstCTCI.add(oCTCI1);
        
        CTCI_Junction__c oCTCI2 = new CTCI_Junction__c();
        oCTCI2.Classification_Type__c = oClassType.id;
        oCTCI2.Classification__c = classification.Id;
        oCTCI2.Intent__c = Intent2.id;
        oCTCI2.IsActive__c = true;
        lstCTCI.add(oCTCI2);
        
        insert lstCTCI;
        
        Quick_Start_Pretext__c QSP = new Quick_Start_Pretext__c(Is_Active__c=True,CTCI_List_lkp__c=lstCTCI[0].Id);
        Insert QSP;
        
        Quick_Start_Pretext__c QSP2 = new Quick_Start_Pretext__c(Is_Active__c=True,CTCI_List_lkp__c=lstCTCI[1].Id);
        Insert QSP2;
        
        Id MentorLinkRecordTypeId = Schema.SObjectType.CustomLink__c.getRecordTypeInfosByName().get('Mentor Link').getRecordTypeId();
        
        CustomLink__c NMD1 = new CustomLink__c(Name='Medicare Authorization Calls',RecordTypeId = MentorLinkRecordTypeId,Download_MentorLink__c=false,Quick_Start_Pretext__c = QSP.Id,URL__c='http://dctm.humana.com/Mentor/xWeb/viewtopic.aspx?sChronicleID=0900092980db1698&dl=0&searchID=VI-8d446b54f5180ce&row=0&mode=Mentor');
        insert NMD1;
        
        CustomLink__c NMD2 = new CustomLink__c(Name='Humana Rewards Overview',RecordTypeId = MentorLinkRecordTypeId,Download_MentorLink__c=True,Quick_Start_Pretext__c = QSP2.Id,URL__c='http://dctm.humana.com/Mentor/xWeb/viewtopic.aspx?sChronicleID=090009298109b9f9&dl=0&searchID=VI-8d5956c0b347c82&row=0&mode=Mentor');
        insert NMD2;
        
        HUMCertificateURL__c objHssURL = new HUMCertificateURL__c(Name='HSSRedirectURL', CertificateListURL__c='https://hss.humana.com/ITSecurity/HSSOneClick/LaunchHSS.aspx?HIDDENTARGET=');
        insert objHssURL;
        
        User oUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oUser;
        System.assertEquals(oUser.lastname == 'test User1', true);
    }
    
    /**
    * This method is used to validate getMentorLinks from HumCaseActionLaunchController controller
    * @param None       
    * @return  Void
    */
    static testMethod void testGetMentorLinks() {
        CustomLink__c mentor_link = [select Id 
                                     from CustomLink__c
                                     where recordtype.developerName = 'Mentor_Link'
                                  	 and Quick_Start_Pretext__r.CTCI_list_lkp__r.Classification__r.Name = 'Benefits' 
                                  	 and Quick_Start_Pretext__r.CTCI_list_lkp__r.Intent__r.Name = 'Rewards'];
        
        User oUser = [select id, name from user where lastname = 'test User1' limit 1];
        system.assert(oUser.Id!= null);
        
        system.runAs(oUser) {
            PageReference pageRef = Page.OpenMentorDocument_VF_HUM; 
            Test.setCurrentPageReference(pageRef);        
            Apexpages.currentPage().getParameters().put('link_name', mentor_link.Id);         
            Apexpages.currentPage().getParameters().put('mentor_Code', 'abc');
            Test.startTest();
                OpenMentorDocument_C_HUM customController = new OpenMentorDocument_C_HUM();
                PageReference pageRefReturn = customController.redirectMentorDocument();
                /*System.assertNotEquals(pageRefReturn, null, 'Pass');
                List<Error_Log__c> lstLog = new List<Error_Log__c>();            
            Error_Log__c error = new Error_Log__c(Status__c = 'OpenMentorDocument');            
            lstLog.add(error);                        
            customController.listErrorLogs = lstLog;            
            System.assert(customController.listErrorLogs.size() > 0); */
            Test.stopTest();
            
        }
    }
    
    /**
    * This method is used to validate getMentorLinks from HumCaseActionLaunchController controller have download option check in custom setting
    * @param None       
    * @return  Void
    */
    static testMethod void testGetMentorLinks_download() {
        CustomLink__c mentor_link = [select Id 
                                     from CustomLink__c
                                     where recordtype.developerName = 'Mentor_Link'
                                  	 and Quick_Start_Pretext__r.CTCI_list_lkp__r.Classification__r.Name = 'Benefits' 
                                  	 and Quick_Start_Pretext__r.CTCI_list_lkp__r.Intent__r.Name = 'Coordination'];
        
        User oUser = [select id, name from user where lastname = 'test User1' limit 1];
        system.assert(oUser.Id!= null);
        
        system.runAs(oUser) {
            PageReference pageRef = Page.OpenMentorDocument_VF_HUM; 
            Test.setCurrentPageReference(pageRef);        
            Apexpages.currentPage().getParameters().put('link_name', mentor_link.Id);         
            
            Test.startTest();
                OpenMentorDocument_C_HUM customController = new OpenMentorDocument_C_HUM();
                PageReference pageRefReturn = customController.redirectMentorDocument();
               // System.assertNotEquals(pageRefReturn, null, 'Pass');
            Test.stopTest();
        }
    }
}
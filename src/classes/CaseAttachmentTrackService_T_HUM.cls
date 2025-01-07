/*******************************************************************************************************************************
Apex Class Name : CaseAttachmentTrackService_T_HUM.cls 
Version         : 1.0
Created On      : 2018
Function        : Test Class contains method for services to post Milestone information.

Modification Log:
* Developer Name            Code Review              Date                       Description
*-------------------------------------------------------------------------------------------------------------------------------
* Mohammed Noor             257266                   07/10/2018                 Original Version: REQ - 362124.
* Santhi Mandava                                     04/15/2019                 Replaced "Member_Id_Value__c" with Name
********************************************************************************************************************************/
@isTest
private class CaseAttachmentTrackService_T_HUM 
{
    /**
    * test set-up method to create test data
    *<p>
    * @param            NULL
    * @return           void
    */
    @testSetup static void prepareTestData()
    {
        User oUser = HUMTestDataHelperTest.createTestUser('UserTrack','Customer Care Specialist');
        oUser.Current_Queue__c = 'Cincinnati CRU';
        oUser.Network_User_Id__c = 'XYZ0000';
        insert oUser;                   
        system.assert(oUser.LanguageLocaleKey == 'en_US');
        
        List<PermissionSet> permSet = [SELECT Id, Name FROM PermissionSet where name = 'CRMS_630_MedicareElectionTracking_EnrollmentEdit'];
                        
        PermissionSetAssignment permSetAssign = new PermissionSetAssignment();
        permSetAssign.PermissionSetId = permSet[0].ID;
        permSetAssign.AssigneeId = oUser.Id;
        
        insert permSetAssign;
        
        User oUser1 = HUMTestDataHelperTest.createTestUser('UserTrack1','Customer Care Specialist');
        oUser1.Current_Queue__c = 'Cincinnati CRU';
        oUser1.Network_User_Id__c = 'XYZ0000';
        insert oUser1;                   
        system.assert(oUser1.LanguageLocaleKey == 'en_US');       
        
        system.runAs(oUser)
        {
            insert new HUM_Webservice_Callout__c(Name='TrackService_WriteTrack', Certificate_Name__c='Humana_GuidanceCare_MOCK', Active__c=true, End_Point_URL__c='https://int-crmservicews.humana.com/TrackService_V1.0/WriteTrack', Timeout__c='12000', Request_Type__c='POST', Content_Type__c='application/json');
            
            insert new MEDENROLLMENT_CASE_SUBTYPE_HUM__c(Name='Incomplete DE SNP', SubType__c='Incomplete DE SNP');

            
            Work_Queue_Setup__c oQSetup = new Work_Queue_Setup__c(Name='Cincinnati CRU');
            insert oQSetup ;
            system.assertNotEquals(null, oQSetup.id);
           
            String sAccRecordTypeName = 'Group';      
            Id accRecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get(sAccRecordTypeName).getRecordTypeId();            
            Account acc1 = HUMTestDataHelperTest.getAccount();
            acc1.Name = 'Test Account Cases1';
            acc1.recordTypeId = accRecordTypeId;            
            acc1.Birthdate__c='01/15/1951';
            acc1.Enterprise_ID__c = '1005447107';
            insert acc1;        
            system.assertEquals(acc1.Name == 'Test Account Cases1',true);
        
            Policy__c oPolicyIns = HUMTestDataHelperTest.getPolicy();
            oPolicyIns.Group_Name__c = acc1.Id;
            oPolicyIns.Name = 'Test Policy';
            insert oPolicyIns;
            system.assertEquals(oPolicyIns.Name,'Test Policy');
            system.assertEquals(oPolicyIns.Group_Name__c == acc1.Id,true);
        
            Policy_Member__c oPolicyMem = HUMTestDataHelperTest.getPolicyMember();
            oPolicyMem.Name='Policy Member PM1';
            oPolicyMem.Policy__c=oPolicyIns.Id;
            oPolicyMem.Member__c=acc1.Id;
            insert oPolicyMem;
            system.assertEquals(oPolicyMem.Id != Null,true);
            system.assertEquals(oPolicyMem.Name,'Policy Member PM1');
            system.assertEquals(oPolicyMem.Policy__c,oPolicyIns.Id);  

            Member_ID__c oMemID = HUMTestDataHelperTest.getmemberID();
            oMemID.Policy_Member__c = oPolicyMem.id;
            oMemID.Policy__c = oPolicyIns.ID;
            oMemID.Type__c = 'SSN';
            oMemID.Name = '123456789';
            oMemID.Member_Card_External_ID__c = '123456';
            insert oMemID;
            system.assertEquals(oMemID.Id != Null, true);
            system.assertEquals(oMemID.Type__c, 'SSN');
            
            list<Case> lstCases = new List<Case>();
            Id medicareCaseRTId = Schema.SObjectType.Case.getRecordTypeInfosByName().get('Medicare Case').getRecordTypeId();
                                    
            Case cs1 = HUMTestDataHelperTest.getCase();
      cs1.Subject = 'Case Subject Incomplete DE SNP';
      cs1.AccountId = acc1.Id;
            cs1.OwnerId = oUser.Id;
            cs1.RecordTypeId = medicareCaseRTId;
            cs1.SubType__c = 'Incomplete DE SNP';
            cs1.Doc_Type__c = 'E17';
            cs1.Origin = 'Inbound Call';
            cs1.Policy_Member__c = oPolicyMem.id;
            lstCases.add(cs1);  
            
            insert lstCases;
            System.assertequals(1, lstCases.size());                      
        }       
    }
    
    /**
    * testMethodForPositive
    *<p>
    *
    * @param            NULL
    * @return           void
    */
    static testMethod void testInvocableMethod() 
    {
        User oUser = [Select id,Current_Queue__c from user where LastName ='test UserTrack' limit 1 ];        
               
        Test.setMock(HttpCalloutMock.class, new TrackServiceMockResponse_T_HUM());       
        system.runAs (oUser) 
        {                                         
            list<Case> caseRec = [Select Id, casenumber, Medicare_Track_ID__c from Case where subtype__c = 'Incomplete DE SNP' limit 1];
            list<Attachment__c> lstAttachments = new list<Attachment__c>();
          Attachment__c attachment = new Attachment__c();
          attachment.RecordtypeID = HUMUtilityHelper.getRecordTypeID(Attachment__c.sObjectType ,'Case');
            attachment.Name = 'Test Attachment';
            attachment.Related_To_Case__c = caseRec[0].Id;
            lstAttachments.add(attachment);
            
            Test.StartTest();
                        
            insert lstAttachments;
                        
            list<Attachment__c> lstCaseAttachments = [SELECT Id,Related_To_Case__c FROM Attachment__c WHERE Related_To_Case__c =: caseRec[0].Id];
            System.assertEquals(false, lstCaseAttachments.isEmpty());
            list<Id> lstAttachmentIds = new list<Id>();
            for(Attachment__c att : lstCaseAttachments)
            {                
                lstAttachmentIds.add(att.Id);
            }
            
            //Queable apex invoking scenario when process builder is inactive
            CaseAttachmentTrackService_C_HUM.invokeTrackServiceForAttachments(lstAttachmentIds);            
            
            Test.StopTest();
                        
      List<Enrollment_Track_History__c> lstHist = [SELECT Id, Name, Case__c, Status__c, Milestone_Id__c FROM Enrollment_Track_History__c where case__c =:caseRec[0].Id];      
      System.assertEquals(false, lstHist.isEmpty());
      System.assertEquals('903', lstHist[0].Milestone_Id__c);                                                                  
        }        
    }
           
}
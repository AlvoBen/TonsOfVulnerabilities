/**********************************************************************************************************************************************************
Apex Class Name  : CaseClosedEdit_T_HUM
Version          : 1.0
Created Date     : 01/11/2018
Function         : Test class for CaseClosedEdit_C_HUM
***********************************************************************************************************************************************************

Modification Log:

* Developer Name            Review Number            Date                       Description
-----------------------------------------------------------------------------------------------------------------------------------------------------------
* Prasad Jandhyala                                  01/11/2018                   Original Version
* Mohammed Noor                                     12/16/2019                   Fix test class failure because of moving Case Workflows to Trigger.
* Mohammed Noor										05/03/2021					 Increase code coverage.
* Atia Uzma                                         07/29/2021                   User Story 2498842: T1PRJ0001827 - MF 1 - AHT - Ability to Reopen and Edit closed cases (RF)
************************************************************************************************************************************************************/
@isTest
Public with sharing Class CaseClosedEdit_T_HUM {
    /*
    * This method creates test data for test class
    * @param None
    * @return None
    */
    @testSetup static void setUpTestData()
    {
        User testUser = HUMTestDataHelperTest.createTestUser('CCEUser1','Customer Care Specialist');
        insert testUser;
        System.assertEquals(testUser.lastname =='test CCEUser1',true);
        RecordType AccountRecordType = [Select Id from RecordType where Name='Member' and sObjectType='Account' limit 1];
        System.runAs(testUser)
        { 
            //Creating Account Record
            Account accountRec = HUMTestDataHelperTest.getAccount();
            accountRec.OwnerId = testUser.id;
            accountRec.FirstName = 'Test';
            accountRec.LastName = 'Account';
            accountRec.Enterprise_Id__c = '123456';
            accountRec.recordtypeId = AccountRecordType.id;
            insert accountRec;
            system.assertEquals(accountRec.recordtypeId==AccountRecordType.id,true);
            
            Provider_functionality_switch__c settings = Provider_functionality_switch__c.getOrgDefaults();
            settings.Activate_CommunicationRecs_Service__c	= true;
			settings.Activate_LettersRecs_Service__c = true;
			settings.Activate_New_Authorization_Service__c = true;
			settings.Active__c = true;
			settings.Primary_Specialty_Switch__c = true;
			upsert settings;
            
            Case objCase = HUMTestDataHelperTest.getCase();
            objCase.AccountId = accountRec.Id;
            insert objCase;
            System.assertNotEquals(objCase, null,'Value not matched');
            objCase.Status ='Closed';
			objCase.recordtypeID = Schema.SObjectType.Case.getRecordTypeInfosByName().get('Closed Member Case').getRecordTypeId();
            update objCase;
            Case objCase1 = [Select Status from Case where id = :objCase.id];
            System.assertEquals(objCase1.Status,'Closed','Case Update failed');
        }
    }  
    
    /*
    * Method Name : testClosedCaseEdit
    * Description : This method Is used To test the Controller
    * Return type : None
    * Parameter   : None
    */
    Private Static testMethod void testClosedCaseEdit()
    {
        User testUser = [Select Id, Name from User where LastName='test CCEUser1'];
        Case caseRec = [SELECT Id FROM Case LIMIT 1];
        System.runAs(testUser)
        {
            Test.startTest();
            Test.setCurrentPage(Page.CaseClosed_Edit_VF_HUM);
            String caseId = caseRec.Id;
            CaseClosedEdit_C_HUM ccd = New CaseClosedEdit_C_HUM(New Apexpages.StandardController(caseRec));
            System.assertEquals(ccd.bClosedMemberRType, True,'Case record type has to be Member Case');
            System.assertEquals(ccd.bClosedAgentRType, False,'Case record type has to be Member Case');
            System.assertEquals(ccd.bClosedHPAgentRType, False,'Case record type has to be Member Case');
            System.assertEquals(ccd.bClosedGroupRType, False,'Case record type has to be Member Case');
            System.assertEquals(ccd.bClosedHPGroupRType, False,'Case record type has to be Member Case');
            System.assertEquals(ccd.bClosedProviderRType, False,'Case record type has to be Member Case');
            System.assertEquals(ccd.bClosedUnknownRType, False,'Case record type has to be Member Case');
            System.assertEquals(ccd.bClosedHPProviderRType, False,'Case record type has to be Member Case');
            System.assertEquals(ccd.bClosedHPUnknownRType, False,'Case record type has to be Member Case');
            System.assertEquals(ccd.bClosedHPMemberRType, False,'Case record type has to be Member Case');
            System.assertEquals(ccd.bClosedMedicareRType, False,'Case record type has to be Member Case');
            ccd.caseRec.Follow_up_Due_Date__c = Date.Today();
            ccd.caseCom.CommentBody = 'New Comment';
            Boolean bResult = ccd.getVerifyAccountType();
            AddClaimNum_CaseEdit_DTO_HUM oDTO = new AddClaimNum_CaseEdit_DTO_HUM();
            oDTO.sClaimText = 'Test Claim';
            oDTO.sClaimNumber = 'C12345';
            oDTO.sSecondCol = 'C23456';
            oDTO.sThirdCol = 'C34567';
            oDTO.sFourthCol = 'C45678';
            oDTO.sFifthCol = 'C567890';
            List<AddClaimNum_CaseEdit_DTO_HUM> lstDTO = new List<AddClaimNum_CaseEdit_DTO_HUM>();
            lstDTO.add(oDTO);
            ccd.lstClaims = lstDTO;
            ccd.bClaimNumberBtn = true;
            ccd.claimSaveClick();
            ccd.claimCancelClick();
            ccd.claimSaveClick();
            ccd.save();
            ccd.caseReopen();
            Case objCase1 = [Select Follow_up_Due_Date__c from Case where id = :caseId];
            System.assertEquals(objCase1.Follow_up_Due_Date__c, Date.Today(),'Case save failed');
            ccd.caseRec.OpenEnrollment__c = True;
            ccd.save();
            //ccd.pageRedirect();
            ccd.caseRec.Status = 'In Progress';
            ccd.save();
            objCase1 = [Select Status from Case where id = :caseId];
            System.assertNotEquals(objCase1.Status,'In Progress','Case saved even though there is no product selected when open enrollment type is checked');
            List < Apexpages.Message > pageMsgs = Apexpages.getMessages();
            System.assertEquals(pageMsgs.size(), 1,'Case saved even though there is no product selected when open enrollment type is checked');            
            Test.stopTest();

        }
    }
}
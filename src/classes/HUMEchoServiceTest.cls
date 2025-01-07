/*******************************************************************************************************************************
Apex Class Name : HUMEchoServiceTest 
Version         : 1.0
Created On      : 8/21/2014
Function        : This class contains methods to test HUMEchoService

Modification Log: 
 * Developer Name            Code Review                     Date                         Description
 *------------------------------------------------------------------------------------------------------------------------------
 * Pranali Choudhari            17465                      08/21/2014                   Original Version
 * Ranjeeth Nagishetty          20287                      01/12/2015                   REQ-95950 validation rule
 * SuryaKumari Medicherla       20579                      01/28/2015                   Code Modified to for Code failure
 * Vishal Verma                         23551                          06/02/2015                   185471.0001 - Fixing test class failure
 * Vishal Verma                 25256                      08/04/2015                   CA - 5102605 - Added TestSetup method.
 * Chaitanya Vegendla                       82924                            04/06/2017                           REQ-306329 ,Updated code as per CTCI Model
 ****************************************************************************************************************************/

@isTest
private class HUMEchoServiceTest
{
    /* Method to create test data.
     * @ param  : None
     * @ return : void
     */
    @testSetup 
    static void createCustomData() 
    {
        User oUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        oUser.Current_Queue__c='Test';
        insert oUser;
        System.assert(oUser.id != null);
        System.runas(oUser)
        {
            Schema.Sobjecttype objType = Schema.getGlobalDescribe().get('Account');
            List<Account> memAccountList = new List<Account>();
            List<Account> grpAccountList = new List<Account>();
            for(Integer i = 0 ; i < 10 ; i++)
            {
                Account oAccMember = HUMTestDataHelperTest.getAccount();
                oAccMember.FirstName = 'test';
                oAccMember.LastName = 'account';
                oAccMember.Enterprise_ID__c = '12345678'+String.valueof(i);
                oAccMember.Group_Number__c = '1234'+String.valueof(i);
                oAccMember.RecordTypeId = HUMUtilityHelper.getRecordTypeID(objType, 'Member');
                memAccountList.add(oAccMember);
            }
            insert memAccountList; 
            System.assert(memAccountList.size() == 10);
            
            for(Integer i = 0 ; i < 10 ; i++)
            {
                Account oAccGroup = HUMTestDataHelperTest.getAccount();
                oAccGroup.Name = 'test';
                oAccGroup.Enterprise_ID__c = '12345000'+String.valueof(i);
                oAccGroup.Group_Number__c = '1233'+String.valueof(i);
                oAccGroup.RecordTypeId = HUMUtilityHelper.getRecordTypeID(objType, 'Group');
                grpAccountList.add(oAccGroup);
            }
            insert grpAccountList; 
            System.assert(grpAccountList.size() == 10);
            
            Policy__c oPolicy = HUMTestDataHelperTest.getPolicy();
            oPolicy.Group_Name__c = grpAccountList[0].Id;
            insert oPolicy;
            system.assertNotEquals(oPolicy, NULL); 
            
            List<Policy_Member__c> polMemberList = new List<Policy_Member__c>();
            
            List<Interaction__c> lstInteraction = new List<Interaction__c>();
            
            for(Account oAccMember : memAccountList)
            { 
                Interaction__c oIntMember = HUMTestDataHelperTest.getInteraction();
                oIntMember.Interacting_With__c = oAccMember.Id;
                oIntMember.Interacting_With_Type__c = 'Member';
                lstInteraction.add(oIntMember);
                
                Policy_Member__c oPolMem = HUMTestDataHelperTest.getPolicyMember();
                oPolMem.Member__c = oAccMember.Id;
                oPolMem.Policy__c = oPolicy.Id;
                polMemberList.add(oPolMem);
            }
            
            insert polMemberList;
            System.assert(lstInteraction.size() == 10);
                        
            for(Account oAccGroup : grpAccountList)
            {
                Interaction__c oIntGroup = HUMTestDataHelperTest.getInteraction();
                oIntGroup.Interacting_With__c = oAccGroup.Id;
                oIntGroup.Interacting_With_type__c = 'Group';
                lstInteraction.add(oIntGroup);
            }
            
            insert lstInteraction;
            System.assert(lstInteraction.size() == 20);
            
            Classification_Type__c objcT = new Classification_Type__c();
             objcT.Name ='CallRSOtest';
             objcT.Is_Active__c = true;
             insert objcT;
             System.assertEquals(objcT.Name,'CallRSOtest');
            
            Classification__c classificationRec1 = New Classification__c();
            classificationRec1 .Name = 'Claimstest';
            classificationRec1 .Is_Active__c = true;
            insert classificationRec1 ;
            System.assertEquals(classificationRec1.Name,'Claimstest');
            
            Intent__c intentRec1 = New Intent__c();
            intentRec1.Name = 'Fillingtest';
            intentRec1.Is_Active__c = true;
            insert intentRec1 ;
            System.assertEquals(intentRec1.Name,'Fillingtest');
            
            
            CTCI_Junction__c objCTCI = new CTCI_Junction__c();
            objCTCI.Classification__c=classificationRec1.id;
            objCTCI.Classification_Type__c= objcT.id;
            objCTCI.Intent__c =intentRec1.id;
            objCTCI.IsActive__c=true;
            insert objCTCI ;
          
            
            Case oCase = HUMTestDataHelperTest.getCase();
            oCase.CTCI_List__c=objCTCI.id;
            insert oCase;
            
            system.assertNotEquals(oCase, NULL); 
            
            List<Interaction_Member__c> lstInteractionMember  = new List<Interaction_Member__c>();
            
            List<Case_Interaction__c> lstCaseInteraction  = new List<Case_Interaction__c>();
            
            for(Interaction__c intObj :lstInteraction)
            {
                Interaction_Member__c oIntMemb = HUMTestDataHelperTest.getInteractionMember();
                oIntMemb.Interaction__c =  intObj.Id;           
                oIntMemb.Interacting_About_Type__c = intObj.Interacting_With_type__c;
                oIntMemb.Interacting_About__c = intObj.Interacting_With__c;
                lstInteractionMember.add(oIntMemb);
                
                Case_Interaction__c oCaseInt = new Case_Interaction__c();
                oCaseInt.Case__c = oCase.Id;
                oCaseInt.Interaction__c = intObj.Id;
                lstCaseInteraction.add(oCaseInt);
            }
            
            insert lstInteractionMember;
            System.assert(lstInteractionMember.size() == 20);
            
            insert lstCaseInteraction;
            System.assert(lstCaseInteraction.size() == 20);
        }
    }

     
    /*  Test method to obtain interaction details for Member when sInquiredAbtID is passed 
     * @ param    : None
     * @ return   : void
     */
    static testMethod void testInquiredIDMember()
    {
        User oUser = [Select Id , Name from User where Username= 'user1user1@humcrmstest.com'  order by LastModifiedDate desc Limit 1];
        Policy_Member__c  oPolMem =  [Select Id , Name from Policy_Member__c Limit 1]; 
        system.runas(oUser)
        {
            HUMEchoRequestDTO req = new HUMEchoRequestDTO();
            HUMEchoRequestDTO echoReq = new HUMEchoRequestDTO();
            echoReq.sInquiredAbtID = oPolMem.Name;
            echoReq.sType = 'Member';
            echoReq.sInteractingWithType = 'Member';
            echoReq.sStartDate = '2010-10-10';
            echoReq.sEndDate =  String.valueof(System.today().Year()+1)+'-10-10';
            test.starttest();
                HUMEchoOutputDTO echoOut = HUMEchoService.getInteractionDetails(echoReq);
            test.stoptest();
            System.assert(echoOut.bIsSuccess != true);
        }
    }
    
     
    /*  Test method to obtain interaction details for Group when sInquiredAbtID is passed 
     * @ param    : None
     * @ return   : void
     */
    static testMethod void testInquiredIDGroup()
    {
        User oUser = [Select Id , Name from User where Username= 'user1user1@humcrmstest.com' order by LastModifiedDate desc Limit 1];
        Account objAcc = [Select Id, Group_Number__c from Account where recordType.Name  = 'Group' Limit 1]; 
        system.runas(oUser)
        {
            HUMEchoRequestDTO req = new HUMEchoRequestDTO();
            HUMEchoRequestDTO echoReq = new HUMEchoRequestDTO();
            echoReq.sInquiredAbtID = objAcc.Group_Number__c;
            echoReq.sType = 'Group';
            echoReq.sInteractingWithType = 'Group';
            test.starttest();
                HUMEchoOutputDTO echoOut = HUMEchoService.getInteractionDetails(echoReq);
            test.stoptest();
            System.assert(echoOut.bIsSuccess == true);
        }
    }
    
     /*  Test method to obtain interaction details for Group when sEnterpriseID is passed
     * @ param    : None
     * @ return   : void
     */
    static testMethod void testEnterpriseIdGroup()
    {
        User oUser = [Select Id , Name from User where Username= 'user1user1@humcrmstest.com' order by LastModifiedDate desc Limit 1];
        Account objAcc = [Select Id, Enterprise_Id__c from Account where recordType.Name  = 'Group' Limit 1]; 
        system.runas(oUser)
        {
            HUMEchoRequestDTO req = new HUMEchoRequestDTO();
            HUMEchoRequestDTO echoReq = new HUMEchoRequestDTO();
            echoReq.sEnterpriseID = objAcc.Enterprise_id__c;
            echoReq.sType = 'Group';
            echoReq.sInteractingWithType = 'Group';
            test.starttest();
                HUMEchoOutputDTO echoOut = HUMEchoService.getInteractionDetails(echoReq);
            test.stoptest();
            System.assert(echoOut.bIsSuccess == true);
        }
    }
    

      /* Test method to obtain interaction details when blank values are passed.
     * @ param    : None
     * @ return   : void
     */
    static testMethod void testBlankInput()
    {
        User oUser = [Select Id from User  where Username= 'user1user1@humcrmstest.com' order by LastModifiedDate desc Limit 1];
        system.runas(oUser)
        {
            HUMEchoRequestDTO req = new HUMEchoRequestDTO();
            HUMEchoRequestDTO echoReq = new HUMEchoRequestDTO();
            test.starttest();
                HUMEchoOutputDTO echoOut = HUMEchoService.getInteractionDetails(echoReq);
            test.stoptest();
            System.assert(echoOut.bIsSuccess == false);
        }
    }
    
     
      /* Test method to obtain interaction details for Account not found.
     * @ param    : None
     * @ return   : void
     */
    static testMethod void testAccountNotFound()
    {
        User oUser = [Select Id , Name from User  where Username= 'user1user1@humcrmstest.com' order by LastModifiedDate desc Limit 1];
        system.runas(oUser)
        {
            HUMEchoRequestDTO req = new HUMEchoRequestDTO();
            HUMEchoRequestDTO echoReq = new HUMEchoRequestDTO();
            echoReq.sEnterpriseID = '201';
            echoReq.sType = 'Group';
            echoReq.sInteractingWithType = 'Group';
            test.starttest();
                HUMEchoOutputDTO echoOut = HUMEchoService.getInteractionDetails(echoReq);
            test.stoptest();
            System.assert(echoOut.bIsSuccess == false);
        }
    }
    
     
    /* Test method to test Exceptions
     * @ param    : None
     * @ return   : void
     */
    static testMethod void testException()
    {
        User oUser = [Select Id , Name from User  where Username= 'user1user1@humcrmstest.com' order by LastModifiedDate desc Limit 1];
        Account objAcc = [Select Id, Enterprise_Id__c from Account where recordType.Name  = 'Group' Limit 1]; 
        system.runas(oUser)
        {
            HUMEchoRequestDTO req = new HUMEchoRequestDTO();
            HUMEchoRequestDTO echoReq = new HUMEchoRequestDTO();
            echoReq.sEnterpriseID = objAcc.Enterprise_id__c;
            echoReq.sType = 'Group';
            echoReq.sStartDate = '2010-99-10';
            echoReq.sInteractingWithType = 'Group';
            test.starttest();
                HUMEchoOutputDTO echoOut = HUMEchoService.getInteractionDetails(echoReq);
            test.stoptest();
            System.assert(echoOut.bIsSuccess == false);
        }
    }
    
     /* Test method to obtain No Interaction records.
     * @ param    : None
     * @ return   : void
     */
    static testMethod void testNoRecords()
    {
        User oUser = [Select Id , Name from User where Username= 'user1user1@humcrmstest.com' order by LastModifiedDate desc Limit 1];
        Account objAcc = [Select Id, Enterprise_Id__c from Account where recordType.Name  = 'Group' Limit 1]; 
        system.runas(oUser)
        {
            HUMEchoRequestDTO req = new HUMEchoRequestDTO();
            HUMEchoRequestDTO echoReq = new HUMEchoRequestDTO();
            echoReq.sEnterpriseID = objAcc.Enterprise_id__c;
            echoReq.sType = 'Group';
            echoReq.sStartDate = String.valueof(System.today().Year()+1)+'-10-10';
            echoReq.sInteractingWithType = 'Group';
            test.starttest();
                HUMEchoOutputDTO echoOut = HUMEchoService.getInteractionDetails(echoReq);
            test.stoptest();
            System.assert(echoOut.bIsSuccess == false);
        }
    }
}
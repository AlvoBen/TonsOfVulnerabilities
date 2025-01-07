/*****************************************************************************************
Apex class Name    : CaseHistoryComponent_LT_HUM
Version            : 1.0
Created Date       : 03/30/2021 

Modification Log:
*  Developer                   Code Review             Date               Description
* ------------------------------------------------------------------------------------------------------------------------------   
*  Ritik Agarwal                                      03/30/2021          initial version
*  Ankima                                             04/06/2021          increase in code coverage
*  Ashish Kumar/Ritik								  09/13/2021		  Increased Code coverage
*  Gowthami Thota                                     10/25/2021         Incresed Code Coverage
*  Gowthami Thota                                     02/03/2022         Incresed code coverage
*  Swetha Thuti                                       12/21/2021          Increased Code Coverage
*  Ankima Srivastava                                  2/17/2022          Incresed Code Coverage
*********************************************************************************************************************************/
@isTest
public with sharing class CaseHistoryComponent_LT_HUM {
    /*
* Method Name : prepareTestData
* Description : Create common test Data
* Return type : void
* Parameter   : NA
*/
    
    @testSetup static void prepareTestData() 
    {
        
            Account oacc1 = new Account();
            oacc1.firstName='firstName';
            oacc1.LastName='lastName';
            oacc1.MiddleName='middleName';
            insert oAcc1;
            System.AssertNotEquals(oAcc1, NULL);
            
            Case cs = new Case();
            cs.Status='Active';
            cs.AccountId = oAcc1.id;
            insert cs;
            System.AssertNotEquals(cs, NULL);
        
            CaseComment ccObj = new CaseComment();
            ccObj.CommentBody = '(TEST) Comment';
            ccObj.ParentId = cs.Id;
            insert ccObj;
            System.assertEquals(ccObj.CommentBody, '(TEST) Comment');
            User u = HUMTestDataHelperTest.createTestUser('CaseHistoryUser1','Customer Care Specialist');
            u.Current_Queue__c = 'Cincinnati CRU';
            u.Network_User_Id__c = 'SXS2222';
            insert u;
        	
        	Work_Queue_Setup__c oQSetup = new Work_Queue_Setup__c(Name='Cincinnati CRU');
            insert oQSetup ;
        
        	List<Case> csList = new List<Case>();
            for(Integer i=0;i<4;i++)
            {
                Case cs2 = new Case();
                cs2 = HUMTestDataHelperTest.getCase();
                cs2.Subject = 'Case Subject'+ i;
                cs2.AccountId = oAcc1.Id;
                cs2.OwnerId = u.Id;
                csList.add(cs2);
            }
            insert csList;	
            list<CaseComment> oCaseComments = new list<CaseComment>();
            for(Case oCase: csList){
                CaseComment ccObj1 = new CaseComment();
                ccObj1 = HUMTestDataHelperTest.getCaseComm();
                ccObj1.ParentId = oCase.Id;
                oCaseComments.add(ccObj1);
            }
            insert oCaseComments;
        
    }
    
/*
* Method Name : testgeneratepharmacycasehistory()
* Description : Test Search Criteria
* Return type : void
* Parameter   : NA
*/
    static testmethod void testgeneratepharmacycasehistory()
    {
        Account acc = [Select id  from account where MiddleName='middleName' LIMIT 1];
        String caseDetails = CaseHistoryComponent_LC_HUM.generatepharmacycasehistory(acc.id, 0);
        System.AssertNotEquals(caseDetails,Null);
    }
    
    /*
* Method Name : generatecasehistory()
* Description : Test Search Criteria
* Return type : void
* Parameter   : NA
*/
    static testmethod void generatecasehistory()
    {
        Account acc = [Select id  from account where MiddleName='middleName' LIMIT 1];
        String caseDetails = CaseHistoryComponent_LC_HUM.generatecasehistory(acc.id); 
        System.AssertNotEquals(caseDetails,Null);
    }
    
    /*
* Method Name : getCaseCommentDetails()
* Description : Test Search Criteria
* Return type : void
* Parameter   : NA
*/
    static testmethod void getCaseCommentDetails(){
        Account acc = [Select id  from account where MiddleName='middleName' LIMIT 1];
        Case cs = [Select id,Status from Case where AccountId=:acc.id LIMIT 1];
        CaseHistoryComponent_LC_HUM.getCaseCommentDetails(cs.id);
        CaseHistoryComponent_LC_HUM.generateCaseComments(cs.id);
        System.AssertNotEquals(cs,Null);
    }

    /*
* Method Name :  getLinkCasesList()
* Description : Test Search Criteria
* Return type : void
* Parameter   : NA
*/
    static testmethod void getLinkCasesList(){
        //List<Case> csList = New List<Case>();
        Account acc = [Select id  from account where MiddleName='middleName' LIMIT 1];
        User u = [SELECT Id FROM User  WHERE Lastname='test CaseHistoryUser1' LIMIT 1];
        List<Case> csList = [select Id,casenumber from Case where AccountId =: acc.Id ];
        List<String> sSelectedCases = new List<String>();
            For(Integer i=0;i<=3;i++)
            {
                sSelectedCases.add(csList[i].Id+'_'+'true');
            }
            //sCases = sCases.substring(1);
       CaseHistoryComponent_LC_HUM.linkSelectedCases(sSelectedCases);
        Boolean bIsSuccess = CaseLinkingLogic_LS_HUM.LinkCases(sSelectedCases );
        
    }
    static testmethod void getLinkedCaseListTest(){
        Account acc = [Select id  from account where MiddleName='middleName' LIMIT 1];
        User u = [SELECT Id FROM User  WHERE Lastname='test CaseHistoryUser1' LIMIT 1];
        List<Case> csList = [select Id,casenumber from Case where AccountId =: acc.Id LIMIT 3];

       	Case case1 = csList.get(0);
       	Case_Junction__c cj1 = new Case_Junction__c();
        cj1.case__c = case1.Id;
        cj1.Case_Master__c = csList.get(1)?.Id;
        cj1.Work_Queue__c = [Select Id from Work_Queue_Setup__c limit 1]?.Id;
        insert cj1;
        
        Case_Junction__C cj2 = new Case_Junction__C();
        cj2.Case__c = csList.get(2)?.Id;
        Cj2.Case_Master__c = case1.Id;
        //cj2.Work_Queue__c = [Select Id from Work_Queue_Setup__c limit 1]?.Id;
        insert cj2;
        
        CaseHistoryComponent_LC_HUM.getLinkedCaseList(case1.Id);
        
        
    }
    
}
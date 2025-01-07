/*******************************************************************************************************************************
Apex Class Name : InteractionController_LT_HUM 
Version         : 1.0
Created On      : 03/18/2022  
Function        : This class serves as a test class for InteractionController_LC_HUM apex class.
Modification Log: 
* Modification ID  Developer Name          Code Review               Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* 1.0              Krishna Teja Samudrala                         03/18/2022                 Original Version(T1PRJ0170850)
* 2.0              Krishna Teja Samudrala                         04/18/2022                 Added testmethods for negative and catch blocks
*******************************************************************************************************************************/
@isTest 
private class InteractionController_LT_HUM {
    @testSetup static void prepareTestData() 
    {
        List<Interaction__c> interactionList = new List<Interaction__c>();
        List<Interaction_Member__c> interactionMemberList = new List<Interaction_Member__c>();       
        Interaction_Member__c intMemRec = new Interaction_Member__c();
        Account oAcc = HUMTestDataHelperTest.getAccount();
        oAcc.FirstName = 'AccTest';
        oAcc.LastName = 'AccTest';
        oAcc.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId();
        oAcc.Account_Security_Answer__c = 'Mumbai';
        oAcc.Account_Security_Question__c = 'In Which City you are born?';
        oAcc.Account_Security_Access__c = 'jan2020#';
        oAcc.Account_Security_EndDate__c  = System.today()+5;
        insert oAcc;
        system.debug('oAcc'+oAcc);        
        for(integer i=0;i<10;i++){ 
            Interaction__c intRec  = new Interaction__c();
            intRec.Interaction_origin__c = 'Inbound Call';
            intRec.Caller_Name__c = 'Test '+i;  
            intRec.Interacting_With__c = oAcc.Id;
            intRec.Interacting_With_type__c = 'Member';
            intRec.Authenticated__c = true;
            interactionList.add(intRec);
        }        
        insert interactionList;
        
        Interaction__c intRec2  = new Interaction__c();
        intRec2.Interaction_origin__c = 'Inbound Call';
        intRec2.Caller_Name__c = 'Int Auth False';  
        intRec2.Interacting_With__c = oAcc.Id;
        intRec2.Interacting_With_type__c = 'Member';
        intRec2.Authenticated__c = false;
        insert intRec2;
        for(Interaction__c int1 : interactionList){            
            Interaction_Member__c intMemTest = new Interaction_Member__c(Interaction__c =int1.Id,
                                                                         Interacting_About__c = oAcc.Id,
                                                                         Interacting_About_Type__c ='Member',
                                                                         Authenticated__c = false);
            interactionMemberList.add(intMemTest);            
        }        
        insert interactionMemberList;
        
        Case objCase = HUMTestDataHelperTest.getCase();
        objCase.AccountId = oAcc.Id;
        insert objCase;
        
        Case_Interaction__c CIRec = new Case_Interaction__c();
        CIRec.Case__c = objCase.Id;
        CIRec.Interaction__c = intRec2.Id;
        insert CIRec;
        
        MemberPlan objMemberplan = new MemberPlan();
        objMemberplan.Name='MemberPlan1';
        objMemberplan.MemberId=oAcc.id;
        objMemberPlan.Member_Dependent_Code__c = '02';
        objMemberPlan.Member_Id_Base__c = 'HUM654332';
        objMemberPlan.GroupNumber = 'myid123';
        //objMemberPlan.PlanId= oMedicalPolicy.Id;
        insert objMemberplan;
    }
    
    static testmethod void testAllMethods() {
        string AccsearchKey = 'AccTest';
        string AccObjName = 'Account';
        string AccObjId = '';
        string extaccId = '';
        string searchKey = '';
        string ObjName = 'Interaction__c';
        string scrName = 'lookup';
        string scrName1 = '';
        string intRecNameTest = '';
        string intRecIdTest = '';
        string intRecIdTest2 = '';
        string CaseTestRecId = '';
        string callingFnName = '';
        string sInteractionAbout;
        Interaction__c intTest = [SELECT Id, Name, Caller_Name__c FROM Interaction__c WHERE 
                                  Caller_Name__c = 'Test 0' ORDER BY CreatedDate DESC LIMIT 1];
        Interaction__c intTest2 = [SELECT Id, Name, Caller_Name__c FROM Interaction__c WHERE 
                                   Caller_Name__c = 'Int Auth False' ORDER BY CreatedDate DESC LIMIT 1];
        searchKey = intTest.Name;
        intRecIdTest = intTest.Id;
        intRecIdTest2 = intTest2.Id;
        Account accTestRec = [SELECT Id, Name,account_external_id__c FROM Account WHERE 
                              FirstName =:AccsearchKey ORDER BY CreatedDate DESC LIMIT 1];
        AccObjId = accTestRec.Id;
        extaccId = accTestRec.account_external_id__c;
        
        Case CaseTestRec =  [SELECT Id, AccountId FROM Case WHERE 
                             AccountId =:AccObjId ORDER BY CreatedDate DESC LIMIT 1];
        CaseTestRecId = CaseTestRec.Id;
        
        Case_Interaction__c CITestRec =  [SELECT ID, NAME FROM Case_Interaction__c where Case__c =:CaseTestRecId 
                                          AND Interaction__c=:intRecIdTest2 LIMIT 1];
        Interaction_Member__c intMemTest = [SELECT ID, Authenticated__c,Interacting_About__c,Interacting_About_Type__C,Interaction__c FROM Interaction_Member__c WHERE Interaction__c = :intRecIdTest LIMIT 1];
        sInteractionAbout= intMemTest.Interacting_About__c;
        
        test.startTest();
        List<Interaction__c> intListTest = InteractionController_LC_HUM.findInteractionRecords(searchKey, ObjName, scrName);
        List<Interaction__c> intListTest1 = InteractionController_LC_HUM.findInteractionRecords(searchKey, ObjName, scrName1);
        List<Interaction__c> intListTest2 = InteractionController_LC_HUM.findInteractionRecords('', ObjName, 'lookup');
        List<Account> accListTest = InteractionController_LC_HUM.findRecords(AccsearchKey,AccObjName); 
        Interaction__c intTestRec = InteractionController_LC_HUM.getInteractionList(intRecNameTest); 
        Interaction__c intTestRec2 = InteractionController_LC_HUM.getInteractionList(intRecIdTest);
        List<Interaction_Member__c> intmEMTestRecList = InteractionController_LC_HUM.getInteractionMemberList(intRecNameTest, callingFnName); 
        List<Interaction_Member__c> intMemTestRecList2 = InteractionController_LC_HUM.getInteractionMemberList(intRecIdTest, callingFnName);
        List<Interaction_Member__c> intMemTestRecList3 = InteractionController_LC_HUM.getInteractionMemberList(intRecIdTest, 'getInteractionMemberListFn');
        InteractionController_LC_HUM.authCheckHandler(intRecIdTest);
        InteractionController_LC_HUM.authCheckHandler(intRecIdTest2);
        InteractionController_LC_HUM.initializeVariables();
        InteractionController_LC_HUM.showAckPopup(AccObjId);
        string accRecIntType = InteractionController_LC_HUM.getIntWithRecType(AccObjId);
        boolean caseIntExists = InteractionController_LC_HUM.getCaseInteraction(CaseTestRecId, intRecIdTest2);
        system.assertEquals(true,caseIntExists);
        // string appname = InteractionController_LC_HUM.getAppName();
        string accId = InteractionController_LC_HUM.getAccountId(extaccId);
        List<Account> accListTest1 = InteractionController_LC_HUM.findIntwithLookupRecords(AccObjId, 'Account','Name');
        List<Account> accListTest2 = InteractionController_LC_HUM.findIntwithLookupRecords('', 'Account','ALL');
        Interaction_Member__c intMemTestRecListTest = InteractionController_LC_HUM.getSelectedIntMemberDetails(intRecIdTest, sInteractionAbout);
        
    
        test.stopTest();
    }
    
    static testmethod void testCatchBlock() {
        string nonsense = 'nonsense';
        test.startTest();
        List<Interaction__c> intListTest = InteractionController_LC_HUM.findInteractionRecords(nonsense, nonsense, nonsense);
        List<Account> accListTest = InteractionController_LC_HUM.findRecords(nonsense,nonsense); 
        Interaction__c intTestRec = InteractionController_LC_HUM.getInteractionList('');
        List<Interaction_Member__c> intmEMTestRecList = InteractionController_LC_HUM.getInteractionMemberList('', nonsense); 
        InteractionController_LC_HUM.authCheckHandler('');
        InteractionController_LC_HUM.initializeVariables();
        InteractionController_LC_HUM.showAckPopup('');
        string accRecIntType = InteractionController_LC_HUM.getIntWithRecType('');
        boolean caseIntExists = InteractionController_LC_HUM.getCaseInteraction('', nonsense);
        system.assertEquals(false,caseIntExists);
        // string appname = InteractionController_LC_HUM.getAppName();
        // string accId = InteractionController_LC_HUM.getAccountId('');
        List<Account> accListTest1 = InteractionController_LC_HUM.findIntwithLookupRecords(nonsense, nonsense,'NAME');
        test.stopTest();
    }
    
    @isTest static void testSaveDataForCallTransfer(){
        Account acc = [select Id,FirstName,LastName from Account where LastName='AccTest' limit 1];
        Interaction__c inter = [select id,Interacting_With__c,Interacting_With_type__c,Interaction_Origin__c from Interaction__c where Interacting_With__c = :acc.Id limit 1];
        MemberPlan memPlan = [select id,Member_Id_Base__c,Member_Dependent_Code__c,GroupNumber,Policy_Platform__c from MemberPlan where Name='MemberPlan1' limit 1];
        
        test.startTest();
        Boolean saveDataVal = InteractionController_LC_HUM.saveDataForCallTransfer(acc.id,memPlan.id,inter.id);
        system.assertEquals(false,saveDataVal);
        test.stopTest();
    }
    
    @isTest static void testDynamicQuerymethod(){
        Account acc = [select Id,FirstName,LastName from Account where LastName='AccTest' limit 1];
        test.startTest();
        sObject dynamicQueryAcc = InteractionController_LC_HUM.DynamicQuerymethod(acc, 'Account');
        system.assertNotEquals(null,dynamicQueryAcc);
        test.stopTest();
    }
}
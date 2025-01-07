/****************************************************************************
Apex Class Name  : HUMUpdateCaseMapToTaskHelperTest 
Version          : 1.0 
Created On       : 7/15/2014
Function         : Test class to HUMUpdateCaseMapToTaskHelper Apex class
Modification Log :
* Developer Name          Code Review                     Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Mrudula Jaddu           17093                         07/15/2014                   Original Version
* Mrudula Jaddu           17878                         09/12/2014                   Modified fields as part of defect 154260  
* Asha Deepika Koyyana    20572                         01/28/2015                   Fixed test class failure for Defect#164139
* Santhi Mandava          21093                         02/19/2015                   Fixed test class failure issue because of req-81852.  
* Santhi Mandava                                        03/24/2015                   We are using HUMConstants custom setting in original apex class so included that in'overallTestMethod' method. 
* Ranjeeth Nagishetty     22606                         04/24/2015                   Test Class Failure for Defect 179884.0001 
* Prachi Bhasin                                         07/18/2016                   Changes made as part of REQ- 270349
* Harshith Mandya                                       02/20/2016                   Implmented REQ - 300084
* Pallavi R               81595                         04/03/2017                   Implemented REQ 306356 - as per new ctci model
* Santhi Mandava                                        03/12/2019                   Removed "DpaaSWebServiceMockImpl" usage.
* Vamshi Krishna Pemberthi						  06/08/2021 		Code Coverage Improvements
* Jasmeen Shangari									    07/31/2023					 Fixed Test Class Failure
***********************************************************************************************************************************/

@isTest
private class HUMUpdateCaseMapToTaskHelperTest
{
    /**
    * Test method to setup common test data for all test methods 
    *
    * @param None       
    * @return  Void
    */               
     @testSetup static void testSetup() 
     {
        User oUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        oUser.Current_Queue__c = 'Cincinnati CRU';
        insert oUser;
		
		User oUser1 = HUMTestDataHelperTest.createTestUser('User3','Customer Care Specialist');
        insert oUser1;
        system.assert(oUser.Current_Queue__c == 'Cincinnati CRU');  
        
        system.runAs(oUser)
        {   
            RecordType PersonAccountRecordType = [Select Id from RecordType where Name =: Label.HumMemberRecordType and sObjectType=:Label.HUM_Object_Account];
            
            HUMConstants__c obj = new HUMConstants__c(Name='HUMLISTSIZEZERO',IntegerValue__c=0);
            insert obj ;
            System.assert(obj.Name == 'HUMLISTSIZEZERO');
            HUMConstants__c obj1 = new HUMConstants__c(Name='HUMWorkTask',StringValue__c='Work Task');
            insert obj1 ;
            System.assert(obj1.Name == 'HUMWorkTask');
            HUMConstants__c obj2 = new HUMConstants__c(Name='HUMClosed',StringValue__c='Closed');
            insert obj2;
            System.assert(obj2.Name == 'HUMClosed');
            
            Account oAcc = HUMTestDataHelperTest.getAccount(); 
            oAcc.FirstName ='James';
            oAcc.LastName ='VM';
            oAcc.Source_Platform_Code__c = 'EM';
            oAcc.RecordTypeId=PersonAccountRecordType.Id;
            insert oAcc;            
            System.assert(oAcc.Source_Platform_Code__c == 'EM');
                
            Product2 oProd = HUMTestDataHelperTest.getProduct();
            oProd.Name = 'Mediclaim';
            // Added field product type as part of defect 154260                  
            oProd.Product_Type__c = 'Mediclaim';
            insert oProd;           
            System.assert(oProd.Product_Type__c == 'Mediclaim');
            
            Policy__c oPolicy1 = HUMTestDataHelperTest.getPolicy();
            oPolicy1.Name = 'test123';
            oPolicy1.Group_Name__c = oAcc.Id;
			oPolicy1.Platform__c = 'EM';
            oPolicy1.Business_Segment__c = 'Medicare';
            oPolicy1.Funding_Type_Code__c = 'Y';
			oPolicy1.ASO__c = 'N';
            oPolicy1.Original_Major_LOB__c  = 'MCD'; 
            oPolicy1.Product__c = oProd.Id;
            oPolicy1.Issue_State__c = 'IL';         
            insert oPolicy1;            
            System.assert(oPolicy1.Name == 'test123');
                
            Policy_Member__c oPolicyMem1 = HUMTestDataHelperTest.getPolicyMember();
            oPolicyMem1.Name = '12345';
            oPolicyMem1.Member__c = oAcc.Id;
            oPolicyMem1.Policy__c = oPolicy1.Id;            
            oPolicyMem1.Relationship__c = 'Self';           
            insert oPolicyMem1;         
            System.assert( oPolicyMem1.Name == '12345');           
            
        List<Classification_Type__c> ctList= new list<Classification_Type__c>();
        Classification_Type__c ctRec = new Classification_Type__c();
        ctRec.name = 'calls';
        ctRec.Is_Active__c=true;
        ctList.add(ctRec);
        insert ctList;
        system.assertEquals(ctList.size(),1);
        
        List<Classification__c> lstClassification = new List<Classification__c>();
        Classification__c classificationRec = new Classification__c();
        classificationRec.Name = 'Benefits';
        classificationRec.Is_Active__c = true;
        lstClassification.add(classificationRec);
        
        Classification__c classificationRecDec = new Classification__c();
        classificationRecDec.Name = 'Humana Pharmacy';
        classificationRecDec.Is_Active__c = true;
        lstClassification.add(classificationRecDec);
        insert lstClassification;
        system.assertEquals(lstClassification.size(),2);
        
        List<Intent__c> lstIntent = new List<Intent__c>();
        Intent__c intentRec = new Intent__c();
        intentRec.Name = 'Rewards';
        intentRec.Is_Active__c = true;
        lstIntent.add(intentRec);
        
        Intent__c intentRecDec = new Intent__c();
        intentRecDec.Name = 'Estimate';
        intentRecDec.Is_Active__c = true;
        lstIntent.add(intentRecDec);
        insert lstIntent;
        system.assertEquals(lstIntent.size(),2);
        
        List<CTCI_Junction__c> ctciList = new list<CTCI_Junction__c>();
        CTCI_Junction__c ctciRec = new CTCI_Junction__c();
        ctciRec.Classification_Type__c = ctRec.id;
        ctciRec.Classification__c = classificationRec.Id;
        ctciRec.Intent__c = intentRec.id;
        ctciRec.IsActive__c = true;
        ctciList.add(ctciRec);
        
        CTCI_Junction__c ctciRec2 = new CTCI_Junction__c();
        ctciRec2.Classification_Type__c = ctRec.id;
        ctciRec2.Classification__c = classificationRecDec.Id;
        ctciRec2.Intent__c = intentRecDec.id;
        ctciRec2.IsActive__c = true;
        ctciList.add(ctciRec2);
        insert ctciList;
        system.assertEquals(ctciList.size(),2);
        
        CTCI_Junction__c ctciOld = [select id from CTCI_Junction__c where Classification__r.name = 'Benefits' limit 1 ];
        system.assert(ctciOld!=null);
        
            Case oCase1 = new Case(AccountId = oAcc.Id,Policy__c= oPolicy1.Id, 
                                 Policy_Member__c = oPolicyMem1.Id, Interacting_With_Type__c = 'Member', Interacting_With__c = oAcc.Id,
                                 Due_Date__c = Date.Today(), Case_Owner__c = UserInfo.getUserName(), Owner_Queue__c = UserInfo.getUserName(),
                                 Work_Queue_View_Name__c = 'Cincinati CRU',IsEntitlementApplicable__c= false, Service_Center__c = 'Cincinnati', Department__c = 'CRU', 
                                 status ='In Progress',ctci_list__c=ctciOld.id, Origin = 'DEEA',Interacting_About_Type__c = 'Member');
            Case oCase2 = new Case(AccountId = oAcc.Id, Policy__c= oPolicy1.Id, 
                                 Policy_Member__c = oPolicyMem1.Id, Interacting_With_Type__c = 'Member', Interacting_With__c = oAcc.Id,
                                 Due_Date__c = Date.Today(), Case_Owner__c = UserInfo.getUserName(), Owner_Queue__c = UserInfo.getUserName(),
                                 Work_Queue_View_Name__c = 'Cincinati CRU',IsEntitlementApplicable__c=false, Service_Center__c = 'Cincinnati', Department__c = 'CRU',
                                 status='In Progress',ctci_list__c=ctciOld.id,Interacting_About_Type__c = 'Member');         
            insert oCase1;            
            System.assert(oCase1.Work_Queue_View_Name__c == 'Cincinati CRU');           
            insert oCase2;                        
            System.assert(oCase2.Work_Queue_View_Name__c == 'Cincinati CRU');
            
            Task oTask1 = new Task(WhatId = oCase1.id,Type='Work task');
            Task oTask2 = new Task(WhatId = oCase2.id,Type='Work task');
            
            insert oTask1;
            System.assert(oTask1.Type == 'Work task');            
            insert oTask2;
            System.assert(oTask2.Type == 'Work task');
        }
     }
    
    /*
     * Test method is used  for  population of case data on task record functionality coverage
     * @ Param : None
     * @ Return : None
     */    
    private  static testMethod void overallTestMethod()
    {
        User testUser = [Select Id,Name from User where LastName='test User1'];
        System.runAs (testUser) 
        { 
            CTCI_Junction__c ctciNew = [select id from CTCI_Junction__c where Classification__r.name = 'Humana Pharmacy' limit 1 ];
            system.assert(ctciNew!=null);
            List<case> lstOfCases = new List<case>();
            List<case> lstcase = new List<case>();
            Map<Id,Case> MapOldCases = new Map<Id,Case>([SELECT Id,Interacting_With_Type__c,Complaint_Type__c,Plan_Issue_State__c,ctci_list__c,Plan_Segment_Size__c,Policy_Platform__c,Funding_Type__c,Origin, Case_Owner__c, Owner_Queue__c, status FROM case]);
            Map<Id,Case> mapNewCases = new Map<Id,Case>();
            for(Case c:[ Select Id ,Interacting_With_Type__c,Complaint_Type__c,ctci_list__c,Origin,Funding_Type__c,Plan_Issue_State__c,Plan_Segment_Size__c,Policy_Platform__c, Case_Owner__c, Owner_Queue__c, status from Case limit 2])
            {
                lstcase.add(c);
            }
            update lstcase;
            HUMUpdateCaseMapToTaskHelper objHelper11 =new HUMUpdateCaseMapToTaskHelper();
            objHelper11.populateData(lstCase,mapOldCases,mapNewCases);

            for(Case c:[ Select Id ,Interacting_With_Type__c,Complaint_Type__c,Funding_Type__c,ctci_list__c,Origin,Plan_Issue_State__c,Plan_Segment_Size__c,Policy_Platform__c, Case_Owner__c, Owner_Queue__c, status from Case limit 2])
            {
                c.ctci_list__c = ctciNew.id;
                //c.Policy__r.ASO__c = 'Y';
                c.status= 'Pending - Response';
                mapNewCases.put(c.Id,c);
                lstOfCases.add(c);
            }
            update lstOfCases;
			
            test.startTest();
			HUMUpdateCaseMapToTaskHelper objHelper1 =new HUMUpdateCaseMapToTaskHelper();
            objHelper1.populateData(lstOfCases,null,null);
                if(!lstOfCases.isEmpty())
                {
                    //update lstOfCases;
                    HUMUpdateCaseMapToTaskHelper objHelper =new HUMUpdateCaseMapToTaskHelper();
                    objHelper.populateData(lstOfCases,mapOldCases,mapNewCases);
                    HUMUpdateCaseMapToTaskHelper.updateCaseMilestoneNonMIRUser(lstOfCases,mapOldCases);
                }
            test.stopTest();
        }
    }
	
	private  static testMethod void testTransferOwner()
    {
        User newUser = [Select Id,Name from User where LastName='test User3'];
        User testUser = [Select Id,Name from User where LastName='test User1'];
        System.runAs (testUser) 
        { 
            CTCI_Junction__c ctciNew = [select id from CTCI_Junction__c where Classification__r.name = 'Humana Pharmacy' limit 1 ];
            system.assert(ctciNew!=null);
            List<case> lstOfCases = new List<case>();
            Map<Id,Case> MapOldCases = new Map<Id,Case>([SELECT Id,OwnerId,Funding_Type__c,Plan_Issue_State__c,Plan_Segment_Size__c,Policy_Platform__c,IsEntitlementApplicable__c,Complaint_Type__c,ctci_list__c,Origin, Case_Owner__c, Owner_Queue__c, status FROM case]);
            Map<Id,Case> mapNewCases = new Map<Id,Case>();
            for(Case c:[ Select Id ,OwnerId,IsEntitlementApplicable__c,Funding_Type__c,Complaint_Type__c,ctci_list__c,Origin, Case_Owner__c,Plan_Issue_State__c,Plan_Segment_Size__c,Policy_Platform__c, Owner_Queue__c, status from Case limit 2])
            {
                c.ctci_list__c = ctciNew.id;
                c.status= 'Cancelled';
                c.IsEntitlementApplicable__c=true;
                System.runAs(newUser){
                    c.OwnerId = UserInfo.getUserId();
                }
                mapNewCases.put(c.Id,c);
                lstOfCases.add(c);
            }
            update lstOfCases;

            test.startTest();
                if(!lstOfCases.isEmpty())
                {
                    //update lstOfCases;
                    HUMUpdateCaseMapToTaskHelper objHelper =new HUMUpdateCaseMapToTaskHelper();
                    objHelper.populateData(lstOfCases,mapOldCases,mapNewCases);
                    HUMUpdateCaseMapToTaskHelper.updateCaseMilestoneNonMIRUser(lstOfCases,mapOldCases);
                }
            test.stopTest();
        }
    }
}
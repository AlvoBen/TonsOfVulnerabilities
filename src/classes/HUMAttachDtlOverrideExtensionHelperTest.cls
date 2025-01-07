/*****************************************************************************************
Apex class Name    : HUMAttachDtlOverrideExtensionHelperTest
Version            : 1.0
Function           : This is test class for HUMAttachDtlOverrideExtensionHelper
Created Date       : 25 Mar 2015

Modification Log:
*   Developer                    Code Review         Date               Description
* ------------------------------------------------------------------------------------------------------------------------------  
* Tanushree Hazari                21906            03/25/2015         Original version
* Tanushree Hazari                22388            04/16/2015         Code modified(line 30) to fix test class failure Defect#179884.0001
* Avinash Choubey                 33390            07/11/2016         Changes made as part of REQ- 255151
* Santhi Mandava                                   03/12/2019         Removed "DpaaSWebServiceMockImpl" usage.
*****************************************************************************************************************************/
@isTest
private class HUMAttachDtlOverrideExtensionHelperTest
{

    /*
     * This method is used to insert the data only once and called automicticaly in all methods
     * @param    NA
     * @return   void
     */
    @testSetup static void setup() {
    
        User oUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oUser;
        system.assertEquals(oUser.lastname=='test User1',true);

        
        System.runAs (oUser) 
        { 
            HUM_Webservice_Callout__c vfs = new HUM_Webservice_Callout__c(Name = 'EhubService', Certificate_Name__c = 'TestCertificate', Timeout__c = '3000', End_Point_URL__c = 'SampleURL', Active__c=true);
            insert vfs;
            System.assert(vfs.name == 'EhubService');
        
            List<HUMConstants__c> lstConstants = new List<HUMConstants__c>();
            lstConstants.add(new HUMConstants__c(Name='TaskHistoryZero',StringValue__c='0'));
            lstConstants.add(new HUMConstants__c(Name='TaskHistoryConstantOne',StringValue__c='1'));
            lstConstants.add(new HUMConstants__c(Name='HUMConstantMinusOne',StringValue__c='-1'));
            lstConstants.add(new HUMConstants__c(Name='HUMLISTSIZEONE',IntegerValue__c=1));
            lstConstants.add(new HUMConstants__c(Name='HUMAttachment',StringValue__c='Attachment'));
            lstConstants.add(new HUMConstants__c(Name='HUMNote',StringValue__c='Note'));
            lstConstants.add(new HUMConstants__c(Name='HUMDateFormat',StringValue__c='MM/dd/YYYY'));
            lstConstants.add(new HUMConstants__c(Name='HUMTimeFormat',StringValue__c='hh:mm'));
            lstConstants.add(new HUMConstants__c(Name='HUMTime',StringValue__c='a'));
            insert lstConstants;
            System.assert(lstConstants[0].name  =='TaskHistoryZero');
          
            Account oAcc = HUMTestDataHelperTest.getAccount(); 
            oAcc.Name='TestAccounoTask';
            oAcc.Source_Platform_Code__c = 'CA';
            oAcc.Tenant_Id__c = '00';
            oAcc.General_Account__c = true;
            insert oAcc;                
            system.assert( oAcc.Name =='TestAccounoTask');

            Case oCase = new Case(AccountId = oAcc.Id,  General_Case__c = true, 
            Interacting_With_Type__c = 'Member', Interacting_With__c = oAcc.Id, Due_Date__c = Date.Today(),
            Case_Owner__c = UserInfo.getUserName(), Owner_Queue__c = 'Cincinnati CRU', Work_Queue_View_Name__c = 'Cincinati CRU', 
            Service_Center__c = 'Cincinnati', Department__c = 'CRU', status = 'In Progress');
            insert oCase;               
            system.assert(oCase.status == 'In Progress');             

            Task oTask = new Task(WhatId = oCase.id, Task_Owner__c = UserInfo.getUserName(), Owner_Queue__c = 'Cincinnati CRU',
            Work_Queue_View_Name__c = 'Cincinati CRU', Service_Center__c = 'Cincinnati',Department__c = 'CRU', status = 'In Progress',Type='Work Task', ActivityDate = Date.Today());
            insert oTask; 
            system.assert(oTask.Type == 'Work Task');

            ApexPages.currentPage().getParameters().put('id', oTask.id);

            Attachment__c objAttach = HUMTestDataHelperTest.getCustomAttachment();
            objAttach.Name='Attachment1';
            objAttach.Related_To_Task__c= oTask.Id;
            objAttach.Related_To_Case__c=oCase.Id;
            insert objAttach;
            system.assert(objAttach.Name =='Attachment1');

            Note objNote = new Note();
            objNote.Title='abc';
            objNote.ParentId=objAttach.Id;
            insert objNote;
            system.assert(objNote.Title =='abc');
            
            Attachment oAttach = new Attachment();
            oAttach.Name='Attachment2';
            oAttach.ParentId=objAttach.Id;
            oAttach.Body=Blob.valueOf('Unit Test Attachment Body');
            insert oAttach;
            system.assert(oAttach.Name =='Attachment2');            
        }
    }    

    /*
    * Used to create wrapper class data
    * @param    NA
    * @return   void
    */
    static testMethod void prepareTestData() 
    {
       User oUser = [SELECT Id ,lastname FROM User  WHERE Lastname='test User1' LIMIT 1];
       system.assertEquals(oUser.lastname=='test User1',true); 
        
        System.runAs (oUser) 
        { 
            Test.loadData(HUMConstants__c.sObjectType,'HUMConsTestData');
            Test.setMock(WebServiceMock.class, new HUMEhubImagingWebserviceMockTest());
            HUMAttachDtlOverrideExtensionHelper oExtensionHelper = new HUMAttachDtlOverrideExtensionHelper();
            Attachment oAttach = [select id, name,ParentId,Body,LastModifiedDate,ownerid, owner.name from Attachment where name = 'Attachment2' limit 1  ];
            system.assert(oAttach.Name =='Attachment2');
            Note objNote = [select id, title,ParentId,ownerid, owner.name from Note where title = 'abc' limit 1  ];
            system.assert(objNote.Title =='abc');
            oExtensionHelper.sLastModifiedDate=String.valueOf(oAttach.LastModifiedDate);

            Test.StartTest();
            List<HUMAttachDtlOverrideExtensionHelper> lstAttach = new List<HUMAttachDtlOverrideExtensionHelper>();
            lstAttach.add(new HUMAttachDtlOverrideExtensionHelper(NULL, oAttach));
            lstAttach.add(new HUMAttachDtlOverrideExtensionHelper(objNote, NULL));
            oExtensionHelper.compareTo(oExtensionHelper);
            Test.StopTest();
        }
    }  
}
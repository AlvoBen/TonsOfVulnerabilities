/*****************************************************************************************
Apex class Name    : HUMCaseTaskHelperTest
Version            : 1.0
Created On         : June 9 2014
Function           : This is test class for HUMCaseTaskHelper

Modification Log:
*   Developer                      Code Review              Date               Description
* ------------------------------------------------------------------------------------------------------------------------------                
*   Manish Kumar                     17492                   06/09/2014          Original Version
*   Mrudula Jaddu                    18811                   10/29/2014          Modified according to code review : 18811
*   Manish Kumar                     19218                   11/12/2014          Code fix for defect#160822, #160818
*   SuryaKumari Medicherla           20579                   01/28/2015          Code modified to fix test class failure
*   Manish Kumar                                             03/12/2015          Code modified to fix test class failure in TEST env.
                                                                                 Code Added/modified line#47 to 71
*   Manish Kumar                                             02/05/2015          Code fix as part of Case/Task optimization. Splitted constructors for Case and Task. Removed few more variables
*   Santhi Mandava                                           03/12/2019          Removed "DpaaSWebServiceMockImpl" usage.
***********************************************************************************************************************************/
@isTest
private class HUMCaseTaskHelperTest
{
    /*
    * Method Name   :    overallTestMethod
    * Description   :    This method is used for overall testing for HUMCaseTaskHelper.
    * Return Type   :    void
    * Parameters    :    NA
    */
    static testMethod void overallTestMethod()
    {
        User oCCSUser = HUMTestDataHelperTest.createTestUser('User1', 'Customer Care Specialist');
    insert oCCSUser;
    system.assertNotEquals(oCCSUser, NULL, 'Value not matched');
        
        System.runAs(oCCSUser)
        {
            List<HUMConstants__c> lstHUMConstants = new List<HUMConstants__c>();
    lstHUMConstants.add(new HUMConstants__c(Name= 'HUMCaseTaskPageSize', StringValue__c= '20'));
            lstHUMConstants.add(new HUMConstants__c(Name= 'HUMWorkQueueSetupLimit', StringValue__c= '200'));
            lstHUMConstants.add(new HUMConstants__c(Name= 'HUMNoAccessToQueue', StringValue__c= 'Err'));
            lstHUMConstants.add(new HUMConstants__c(Name= 'HUMTaskRoutingSelectValue', StringValue__c= 'Err'));
            lstHUMConstants.add(new HUMConstants__c(Name= 'HUMTaskRoutingChangeOwner', StringValue__c= 'Err'));
            lstHUMConstants.add(new HUMConstants__c(Name= 'HUM_QUEUE', StringValue__c= 'queue'));
            lstHUMConstants.add(new HUMConstants__c(Name= 'HUMInProgress', StringValue__c= 'In Progress'));
            lstHUMConstants.add(new HUMConstants__c(Name= 'HUMPending', StringValue__c= 'Pending'));
            lstHUMConstants.add(new HUMConstants__c(Name= 'HUMPendingTask', StringValue__c= 'Pending - Task'));
            lstHUMConstants.add(new HUMConstants__c(Name= 'HUMPendingResponse', StringValue__c= 'Pending - Response'));
            lstHUMConstants.add(new HUMConstants__c(Name= 'HUMAllOtherView', StringValue__c= 'Other'));
            lstHUMConstants.add(new HUMConstants__c(Name= 'HUMAge', StringValue__c= 'Age'));
            lstHUMConstants.add(new HUMConstants__c(Name= 'HUMDueDate', StringValue__c= 'Due Date'));
            lstHUMConstants.add(new HUMConstants__c(Name= 'HUMId', StringValue__c= 'Id'));
            lstHUMConstants.add(new HUMConstants__c(Name= 'HUMPriority', StringValue__c= 'Priority'));
            lstHUMConstants.add(new HUMConstants__c(Name= 'HUMPriorityAndAge', StringValue__c= 'Priotity & Age'));
            lstHUMConstants.add(new HUMConstants__c(Name= 'HUMUnAssigned', StringValue__c= 'Un-Assigned'));
            lstHUMConstants.add(new HUMConstants__c(Name= 'HUMAssigned', StringValue__c= 'Assigned'));
            lstHUMConstants.add(new HUMConstants__c(Name= 'HUMBoth', StringValue__c= 'Both'));
            lstHUMConstants.add(new HUMConstants__c(Name= 'HUMCases', StringValue__c= 'Case'));
            lstHUMConstants.add(new HUMConstants__c(Name= 'HUMTasks', StringValue__c= 'Task'));
            lstHUMConstants.add(new HUMConstants__c(Name= 'HUMREGULAR', StringValue__c= 'Regular'));
            
            lstHUMConstants.add(new HUMConstants__c(Name= 'Priority&Age', StringValue__c= 'Priority&Age'));
            lstHUMConstants.add(new HUMConstants__c(Name= 'HUM_DueDate', StringValue__c= 'DueDate'));
            lstHUMConstants.add(new HUMConstants__c(Name= 'HUM_Critical', StringValue__c= 'Critical'));
            lstHUMConstants.add(new HUMConstants__c(Name= 'HUM_High', StringValue__c= 'High'));
            lstHUMConstants.add(new HUMConstants__c(Name= 'HUM_Normal', StringValue__c= 'Normal'));
            lstHUMConstants.add(new HUMConstants__c(Name= 'CaseTaskQuery_Limit', StringValue__c= '3500'));
            lstHUMConstants.add(new HUMConstants__c(Name= 'HUMLISTSIZEZERO', IntegerValue__c= 0));
            lstHUMConstants.add(new HUMConstants__c(Name= 'HUMConstantOne', IntegerValue__c= 1));
            
            insert lstHUMConstants;
    System.assertEquals(lstHUMConstants.size(), 30);
            
            Account oAcc = new Account(Name = 'TestAccount1');
    insert oAcc;
    system.assertNotEquals(oAcc,null,'Value not matched');
            
            Case oCase = new Case(AccountId = oAcc.Id, Interacting_With__c = oAcc.Id, Due_Date__c = Date.Today(), Case_Owner__c = UserInfo.getUserName(), Owner_Queue__c = UserInfo.getUserName(), Work_Queue_View_Name__c = 'Cincinati CRU', status = 'In Progress');
    insert oCase;
    system.assertNotEquals(oCase, NULL, 'Value not matched');
            
            Task oTask = new Task(WhatId = oCase.Id, OwnerId = UserInfo.getUserId(), ActivityDate = Date.Today(), Task_Owner__c = UserInfo.getUserName(), Owner_Queue__c = UserInfo.getUserName(), Status = 'In Progress', Priority = 'Low');
    insert oTask;
    system.assertNotEquals(oTask, NULL, 'Value not matched');
            
            HUMTaskListViewController oTaskLV = new HUMTaskListViewController();
    system.assertNotEquals(oTaskLV, NULL, 'Value not matched');
            
            List<HUMCaseTaskHelper> lstHUMHelper = new List<HUMCaseTaskHelper>();

    lstHUMHelper.add(new HUMCaseTaskHelper(oTask, oAcc.Name, oAcc.Name, oAcc.Id, NULL, true, 'Id',false,false));
            lstHUMHelper.add(new HUMCaseTaskHelper(oCase, oAcc.Name, true, 'Id',false,false));
            lstHUMHelper.add(new HUMCaseTaskHelper(oTask, oAcc.Name, 'Name1', oAcc.Id, NULL, true, 'DueDate',false,false));
            lstHUMHelper.add(new HUMCaseTaskHelper(oCase, 'Name1', true, 'DueDate',false,false));
            lstHUMHelper.add(new HUMCaseTaskHelper(oTask, oAcc.Name, 'Name2', oAcc.Id, NULL, true, 'Priority&Age',false,false));
            lstHUMHelper.add(new HUMCaseTaskHelper(oCase, 'Name2', true, 'Priority&Age',false,false));
            lstHUMHelper.add(new HUMCaseTaskHelper(oTask, oAcc.Name, 'Name3', oAcc.Id, NULL, true, 'Age',false,false));
            lstHUMHelper.add(new HUMCaseTaskHelper(oCase, 'Name3', true, 'Age',false,false));
            lstHUMHelper.add(new HUMCaseTaskHelper(oTask, oAcc.Name, 'Name4', oAcc.Id, NULL, true, 'Priority',false,false));
            lstHUMHelper.add(new HUMCaseTaskHelper(oCase, 'Name4', true, 'Priority',false,false));
            System.assertEquals(lstHUMHelper.size(), 10);
            lstHUMHelper.sort();
            
            HUMCaseTaskHelper oCaseTask3 = new HUMCaseTaskHelper();
    system.assertNotEquals(oCaseTask3, NULL, 'Value not matched');
        }
    }
}
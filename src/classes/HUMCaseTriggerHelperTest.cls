/****************************************************************************
Apex Class Name  : HUMCaseTriggerHelperTest
Version          : 1.0 
Created Date     : JUNE 09 2014
Function         : Test class to HUMCaseTriggerHelper Apex class.
Modification Log :
 * ModificationID           Developer Name          Code Review                Date                       Description
 *------------------------------------------------------------------------------------------------------------------------------
 * 1.0                      Ninad Patil             16484                   09/06/2014                Original Version
 * 1.1                      Ninad Patil             16484                   10/07/2014                Modified for negative test cases.
 * 1.2                      Ninad Patil             17100                   05/08/2014                Resubmitting after Coding standard correction.
 * 1.3                      Ninad Patil             19758                   12/08/2014                populating caller name field in interaction
 * 1.4                      Reeena Rajan                                    01/12/2015                Added attribute value for Interaction Origin since the valu is not defaulted any more.
 * 1.5                      SuryaKumari Medicherla   20579                  28/01/2015                Added method to call Mock method to fix test failure
 * 1.5                      SuryaKumari Medicherla                          10/04/2015                Code Modified to fix test class failure
 * 1.5                      Avinash Choubey                                 06/04/2016                Code Modified to fix test class failure
 * 1.6                      Santhi Mandava                                  03/12/2019                Removed "DpaaSWebServiceMockImpl" usage.
 * Vamshi Krishna Pemberthi						  06/08/2021 		Code Coverage Improvements
 *******************************************************************************************************************************/
@isTest

private class HUMCaseTriggerHelperTest 
{
    /*
     * Method Name : testCaseTriggerHandler
     * Description : This method tests creation of case.
     * Return type : Void
     * Paramater   : None
     */
    static testMethod void testCaseTriggerHandler() 
    {    
    User testUser = HUMTestDataHelperTest.createTestUser('Customer_Case_specialist_User', 'Customer Care Specialist');
    testUser.Current_Queue__c = 'Cincinnati CRU';
    insert testUser;
    System.assert(testUser.id != null); 

    system.runAs(testUser)
    {          
        Work_Queue_Setup__c oQSetup = new Work_Queue_Setup__c(Name='Cincinnati CRU',Service_Center__c='Cincinnati',Department__c='CRU');
        insert oQSetup;
        System.assert(oQSetup.id != null);         

        //Inserting custom setting data.      
        HUMQueueViewBatchSettings__c obj = new HUMQueueViewBatchSettings__c(Name='QueueViewBatchSetup',TriggerExecutionRequired__c=true);
        insert obj ;     
        System.assert(obj.name == 'QueueViewBatchSetup');  
        HUMTriggerSwitch__c caseTriggerSwith = new HUMTriggerSwitch__c (name='Case', Exeute_Trigger__c=true);
        System.assert(caseTriggerSwith.name == 'Case'); 
        HUMCaseUpdateAllowedProfies__c objAllProf = new HUMCaseUpdateAllowedProfies__c (name='Customer Care Specialist');    
        System.assert(objAllProf.name == 'Customer Care Specialist');             
        List<HUMConstants__c> lstConstanst=new List<HUMConstants__c>();            
        lstConstanst.Add(new HUMConstants__c(Name='HUMConstantOne',IntegerValue__c=1,StringValue__c='1'));            
        lstConstanst.Add(new HUMConstants__c(Name='HUMLISTSIZEZERO',IntegerValue__c=0,StringValue__c='0'));            
        lstConstanst.Add(new HUMConstants__c(Name='HUMViewCriteriaLimit',IntegerValue__c=22,StringValue__c=''));            
        lstConstanst.Add(new HUMConstants__c(Name='HUMDefaultBatchLimit',IntegerValue__c=5,StringValue__c=''));            
        lstConstanst.Add(new HUMConstants__c(Name='HUMAllOtherView',StringValue__c='Other'));            
        lstConstanst.Add(new HUMConstants__c(Name='HUMCompleted',StringValue__c='Completed'));            
        lstConstanst.Add(new HUMConstants__c(Name='HUMClosed',StringValue__c='Closed'));            
        lstConstanst.Add(new HUMConstants__c(Name='HUMStatusCancelled',StringValue__c='Cancelled'));   
        lstConstanst.add(new HUMConstants__c(Name='HUMSingleSlash',StringValue__c='/'));
        lstConstanst.add(new HUMConstants__c(Name='HUMCaseCloseReturnURL',StringValue__c='/s?retURL='));
        lstConstanst.add(new HUMConstants__c(Name='HUMEnt',StringValue__c='ent'));
        lstConstanst.add(new HUMConstants__c(Name='HUMNoOverrideEqualsOne',StringValue__c='&nooverride=1'));
        lstConstanst.add(new HUMConstants__c(Name='HUMWorkTask',StringValue__c='Work Task'));
        lstConstanst.add(new HUMConstants__c(Name='HUMNotificationTask',StringValue__c='Notification Task'));        
        insert lstConstanst;
        System.assert(lstConstanst.size() > 0);
        
        //Preparing Test Data : Interaction, Case and ACcount records.
        Interaction__C oInt = HUMTestDataHelperTest.getInteraction();
        oInt.Caller_Name__c = 'test';     
        insert oInt ;
        System.assert(oInt != null);
        Case oCase = HUMTestDataHelperTest.getCase();        
        insert(oCase);
        System.assert(oCase != null);
        Case_interaction__C oCaseInteraction = new Case_interaction__C(interaction__C = oInt.ID, Case__C  = oCase.id);
        insert(oCaseInteraction);  
        System.assert(oCaseInteraction != null);                                                    
        Account oAcc = HUMTestDataHelperTest.getAccount();
        oAcc.Name = 'test acc';
        insert oAcc; 
        System.assert(oAcc != null);                                                   
        //Creating list of Cases so as to pass it to the trigger.
        List<Case> csList = new List<Case>();
        for(Integer i=0;i<200;i++)       
        {         
            Case cs = HUMTestDataHelperTest.getCase(); 
            cs.AccountId = oAcc.Id;
            cs.Interaction_ID__c = oInt.ID;     
            csList.add(cs);                     
        }
        
        Test.startTest();
        Interaction__C oIntNew = new Interaction__C(Caller_name__C = 'Test Caller1', Interaction_Origin__c = 'Inbound Call');       
        insert oIntNew ;
        System.assert(oIntNew != null); 
        Case oCaseNew = new Case(Interaction_id__C = oIntNew.id);
        List<Case> lstCaseNew = new List<Case>{oCaseNew,null};

        //Inserting Record of case created above.
        HUMCaseTriggerHelper.createCaseInteractionRecord(lstCaseNew,true,true);

        //Fetching Case Interaction records for the Interaction created previously.
        List<Case_Interaction__C> lstCaseInteraction = [SELECT ID FROM
                                                        Case_Interaction__C WHERE Interaction__C = :oIntNew.ID];
        //As Case has not been saved, hence above list must be empty.                                               
        System.assert(lstCaseInteraction.size() == 0);

        //Inserting list of Cases list created.
        insert csList;
		HUMCaseTriggerHelper.onAfterInsert(csList,true,true);
        Test.stopTest();
                         
        //Fetching Case Interaction records created by trigger.
        lstCaseInteraction = [SELECT ID FROM Case_Interaction__C WHERE Interaction__C = :oInt.ID];  
                                   
            
    }    
    }
}
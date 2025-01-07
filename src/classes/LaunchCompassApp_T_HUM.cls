/************************************************************************************************************************************************************************************       
Apex Class Name  : LaunchCompassApp_T_HUM.cls 
Created Date     : August 08, 2019
Function         : Test class for LaunchCompassApp_C_HUM
Test Class       : 

**Modification Log
* Developer Name          Code Review #         Date                       Description
* -----------------------------------------------------------------------------------------------------------------------------
* Rakesh Patni									08/08/2019					Original Version
************************************************************************************************************************************************************************************/ 
@isTest
private class LaunchCompassApp_T_HUM
{
	/*
    * Method Name: setup
    * Description: This is created test records.
    * @param N/A
    * @return N/A
    */
	 @testSetup static void setup()
    {
        Group oNewGroup = null;
        oNewGroup = new Group(Name ='Test Business Process and Testing', Type = 'Queue');
        insert oNewGroup;
        system.assertEquals(oNewGroup.Type,'Queue');

        QueueSObject oNewQueue = new QueueSObject(QueueId = oNewGroup.Id, SObjectType = 'Work_Queue_Setup__c');
        insert oNewQueue;
        system.assertEquals(oNewQueue.SObjectType,'Work_Queue_Setup__c');

        User oCCSUser = HUMTestDataHelperTest.createTestUser('QuickStartSpecialist','Customer Care Specialist');
        oCCSUser.Current_Queue__c = 'Test Business Process and Testing';
        oCCSUser.Mentor_Sub_Function__c='Go365';
        insert oCCSUser;
        system.assertEquals(oCCSUser.Mentor_Sub_Function__c,'Go365');
        
        
        system.runAs(oCCSUser)
        {            
            //CLASSIFICATION TYPE
            List<Classification_Type__c> lstClassType= new List<Classification_Type__c>();
            
            Classification_Type__c oClassType = new Classification_Type__c();
            oClassType.Name = 'Calls';
            oClassType.Is_Active__c = true;
            oClassType.Mentor_Function__c ='Contact Center GBO';
            lstClassType.add(oClassType);
            
            insert lstClassType;
            system.assertEquals(lstClassType[0].Is_Active__c,true);
            
            Work_Queue_Setup__c oWorkQueue = new Work_Queue_Setup__c(Name = 'Test Business Process and Testing', OwnerId = oNewGroup.Id, Service_Center__c = 'Business', Department__c = 'Process and Testing',Classification_Type__c = oClassType.id);
            insert oWorkQueue; 
            system.assertEquals(oWorkQueue.Service_Center__c,'Business');
        }
    }
	
	/*
    * Method Name: testConstructor
    * Description: This is to test Launch Compass app Constructor logic
    * @param N/A
    * @return N/A
    */
	@isTest
    static void testConstructor()
    {
    	User oCCSUser = [SELECT Id, Name, UserName, Current_Queue__c 
                         FROM User 
                         WHERE LastName = 'test QuickStartSpecialist'];
        system.assert(oCCSUser.Current_Queue__c=='Test Business Process and Testing');
        system.runAs(oCCSUser)
        {	
            test.starttest();
            LaunchCompassApp_C_HUM cont = new LaunchCompassApp_C_HUM();
            cont.redirectToMentorPage();
            test.stoptest();
        }
    }
}
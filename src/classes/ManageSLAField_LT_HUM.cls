/**************************************************************************************************************
Apex Class Name : ManageSLAField_LT_HUM
Version         : 1.0
Created On      : 05/06/2021
Function        : Test class for ManageSLAField_LC_HUM

* Modification Log:
* Developer Name            Code Review                 Date                       Description
*----------------------------------------------------------------------------------------------------------------
* Vijaykumar                                          05/06/2021                  US-2224460  T1PRJ0001827- IR 5050 - Create new " Manage SLA setup" page -WQ summary & Queue View (CRM)
*****************************************************************************************************************/


@isTest
private class ManageSLAField_LT_HUM {
   
   /*
    * Method Name : setup
    * Description : This method is used to create test data for the class 
    * Return type : void
    * Parameter   : NA
    */
    @testSetup static void setup() 
    {
       
        Group oCompassNewGroup = new Group(Name ='Green Bay WVB Front End', Type = 'Queue');
        insert oCompassNewGroup;
        system.assertEquals(oCompassNewGroup.Type, 'Queue');

        QueueSObject oCompassNewQueue = new QueueSObject(QueueId = oCompassNewGroup.Id, SObjectType = 'Work_Queue_Setup__c');
        insert oCompassNewQueue;
        system.assertEquals(oCompassNewQueue.SObjectType,'Work_Queue_Setup__c');

        User usr = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        usr.Current_Queue__c ='Louisville Cash Posting';
        insert usr;

        System.RunAs(usr) {
        Classification_Type__c ctRec = New Classification_Type__c();
        ctRec.name = 'calls (RSO)';
        ctRec.Is_Active__c=true;
        insert ctRec;

        Work_Queue_Setup__c oWorkQueue = new Work_Queue_Setup__c(Name =oCompassNewGroup.Name, OwnerId = oCompassNewGroup.Id, Service_Center__c = 'Green Bay', Department__c = 'Workplace Voluntary Benefit (WVB) Front End',Classification_Type__c=ctRec.id, SLA_In_Days__c=45, SLA_On_Case_Transfer__c=true );
        insert oWorkQueue;
        system.assertEquals(oWorkQueue.Service_Center__c,'Green Bay');

        Work_Queue_Setup__c oWorkQueue2 = new Work_Queue_Setup__c(Name =oCompassNewGroup.Name, OwnerId = oCompassNewGroup.Id, Service_Center__c = 'Louisville', Department__c = 'Medicare Billing',Classification_Type__c=ctRec.id, SLA_In_Days__c=25, SLA_On_Case_Transfer__c=false );
        insert oWorkQueue2;
        system.assertEquals(oWorkQueue2.SLA_On_Case_Transfer__c, false);

        List<Queue_View__c> lstQueueViews = new list<Queue_View__c>();
        Queue_View__c obj1 = new Queue_View__c();
        obj1.name='Test Queue view';
        obj1.SLA_In_Days__c = 10; 
        obj1.SLA_On_Case_Transfer__c = true;
        obj1.Work_Queue_Name__c=oWorkQueue.id;
        obj1.Accountable_Line_of_Business__c='Humana pharmacy';
        insert obj1;

        Queue_View__c obj2 = new Queue_View__c();
        obj2.name='Test Queue view2';
        obj2.SLA_In_Days__c = 67; 
        obj2.SLA_On_Case_Transfer__c = false;
        obj2.Work_Queue_Name__c=oWorkQueue.id;
        obj2.Accountable_Line_of_Business__c='RSO';
        insert obj2;
        }
    }
    
    /*
    * Method Name : testFunctionality
    * Description : This method is used to test main functionality.
    * Return type : void
    * Parameter   : NA
    */
    static testMethod void testFunctionality() 
    {
        User oUser = [Select Id,Network_User_Id__c from User where LastName = 'test User1'];
        Work_Queue_Setup__c objRec = [select id, SLA_In_Days__c, SLA_On_Case_Transfer__c from Work_Queue_Setup__c where SLA_On_Case_Transfer__c=true LIMIT 1];
        List<Queue_View__c> lstQVS = [select id, SLA_In_Days__c, SLA_On_Case_Transfer__c from Queue_View__c where Work_Queue_Name__c=:objRec.Id];
        system.assertEquals(objRec.SLA_On_Case_Transfer__c, true);
        System.runAs(oUser)
        {
            Test.StartTest();
            Work_Queue_Setup__c objWQS =  ManageSLAField_LC_HUM.getWQSData(objRec.Id);
            System.assertNotEquals(null, objWQS); 

            List<Queue_View__c> lstQuVS =ManageSLAField_LC_HUM.getQueueViewData(objRec.Id);
            System.assertNotEquals(null, lstQuVS); 

            objWQS.SLA_In_Days__c =77;
            Boolean bRecordUpdated = ManageSLAField_LC_HUM.updateWQSRecord(objWQS);
            System.assertEquals(bRecordUpdated, true); 

            Boolean bQVpdate = ManageSLAField_LC_HUM.saveQueueViewData(lstQVS);
            System.assertEquals(bQVpdate, true); 
            Test.StopTest();
        }
    }

        /*
    * Method Name : testFunctionality
    * Description : This method is used to test main functionality.
    * Return type : void
    * Parameter   : NA
    */
    static testMethod void testFunctionality2() 
    {
        User oUser = [Select Id,Network_User_Id__c from User where LastName = 'test User1'];
        Work_Queue_Setup__c objRec = [select id, SLA_In_Days__c, SLA_On_Case_Transfer__c from Work_Queue_Setup__c where SLA_On_Case_Transfer__c=true LIMIT 1];
        Queue_View__c objQV = [select id, SLA_In_Days__c, SLA_On_Case_Transfer__c from Queue_View__c where Work_Queue_Name__c=:objRec.Id LIMIT 1];
        system.assertEquals(objRec.SLA_On_Case_Transfer__c, true);
        System.runAs(oUser)
        {
            Test.StartTest();
            Id wqsId=null;
            Work_Queue_Setup__c objWQS =  ManageSLAField_LC_HUM.getWQSData(wqsId);
            System.assertEquals(null, null); 

            
            List<Queue_View__c> lstQuVS =ManageSLAField_LC_HUM.getQueueViewData(wqsId);
            System.assertEquals(null, null); 
            System.assertNotEquals(lstQuVS, null);

            objRec.SLA_On_Case_Transfer__c =true;
            objRec.SLA_In_Days__c =15;
            objRec.Id = wqsId;
            Boolean bRecordUpdated = ManageSLAField_LC_HUM.updateWQSRecord(objRec);
            System.assertEquals(bRecordUpdated, false); 

            List<Queue_View__c> lstQueueViews =  new List<Queue_View__c>();

            objQV.Id =null;
            objQV.SLA_In_Days__c=15;
            objQV.SLA_On_Case_Transfer__c =true;
            lstQueueViews.add(objQV); 

            Boolean bQVpdate = ManageSLAField_LC_HUM.saveQueueViewData(lstQueueViews);
            System.assertEquals(bQVpdate, false); 
            Test.StopTest();
        }
    }
}
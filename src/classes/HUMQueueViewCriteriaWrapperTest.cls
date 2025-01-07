/****************************************************************************
Apex Class Name  : HUMQueueViewCriteriaWrapperTest 
Version          : 1.0 
Created On       : 08/12/2015
Function         : Test class to HUMQueueViewCriteriaWrapper apex class

Modification Log :
* Developer Name          Code Review                   Date                       Description
*-----------------------------------------------------------------------------------------------------------------------
* Amit Sinha                                      08/12/2015                 Original Version
* Pradeepkumar Dani			98235				  06/06/2017                 Classification_Type_Old__c Clean up
*************************************************************************************************************************/

@isTest                            
private class HUMQueueViewCriteriaWrapperTest 
{
    /*
     * Method Name : createCustomData
     * Description : @testSetup method to create test data.
     * Return type : void
     * Paramater   : NA
     */
    @testSetup 
    static void createCustomData() 
    {
        User oUser = HUMTestDataHelperTest.createTestUser('User1','System Administrator');
        insert oUser;
        system.assertNotEquals(oUser,null,'Value not matched'); 
        Group grp = new Group(Name = 'Test PGQ' ,  developerName = 'test' , Type='Regular');
        Group oGrp;
        
        System.runas(oUser)
        {
            insert grp;
            List<Work_Queue_Setup__c> lstWQS = new List<Work_Queue_Setup__c>();
            lstWQS.Add(new Work_Queue_Setup__c(Name='Cincinnati CRU',Service_Center__c='Cincinnati',Department__c='CRU',IsActive__c=true));
            lstWQS.Add(new Work_Queue_Setup__c(Name='Cincinnati Calls',Service_Center__c='Cincinnati',Department__c='Calls',IsActive__c=true));
            insert lstWQS ;
            Work_Queue_Setup__c oQSetup = lstWQS[0];
                        
            oUser.Current_Queue__c  = oQSetup.Id;
            update oUser;
            
            List<Queue_View__c> lstViews = HUMTestDataHelperTest.insertTestDataForViews(oQSetup);
            HUMTestDataHelperTest.insertTestDataForViewsItems(lstViews);    
        }   
    }
    
    /*
     * Method Name : overAllMainTestMethod
     * Description : This is the main method which is used for queue view assignment 
     * Return type : Void
     * Paramater   : NA
     */  
    static testmethod void overAllMainTestMethod() 
    {
        Work_Queue_Setup__c  oWQS =  [Select Id , Name, Service_Center__c, Department__c, Classification_Type__r.Name, IsActive__c, Public_Group_Queue_Id__c,Batch_Update_Required__c  from Work_Queue_Setup__c Limit 1];
        User oUser = [Select Id , Name, current_queue__c from User where Username= 'user1user1@humcrmstest.com' order by LastModifiedDate desc Limit 1];
        Queue_View__c oQV = [Select id, Name , Work_Queue_Name__c, IsActive__c, Criteria_Updated__c from Queue_View__c where Work_Queue_Name__c =: oWQS.Id limit 1];
        Queue_View_Item__c oQVI = [Select id, Name, API_Name__c, Field_Operator__c, Field_Value__c, Queue_View__c from Queue_View_Item__c where Queue_View__c =: oQV.id limit 1];
        
        system.runAs(oUser)
        {
           HUMTestDataHelperTest.getHUMConstantsData();
           Test.startTest();
           List<HUMQueueViewCriteriaWrapper> wrapperList = new List<HUMQueueViewCriteriaWrapper>();
           wrapperList.add(new HUMQueueViewCriteriaWrapper('Test4', oQV.Name, oQVI, 4));
           wrapperList.add(new HUMQueueViewCriteriaWrapper('Test2', oQV.Name, oQVI, 2));
           wrapperList.add(new HUMQueueViewCriteriaWrapper('Test1', oQV.Name, oQVI, 1));
           wrapperList.add(new HUMQueueViewCriteriaWrapper('Test3', oQV.Name, oQVI, 3));
           wrapperList.sort();
           Test.stopTest();
           system.assertEquals(4,wrapperList.size());
        }
    }
}
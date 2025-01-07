/***************************************************************************************************
 @Author Prasanthi Kandula
 @Name CheckDuplicatesOnAction_T_HUM
 @CreateDate 11/15/2016
 @Description Class contains test methods for CheckDuplicatesOnAction_H_HUM controller
 @Version 1.0
 @Reference NA
   
 Modification Log :
 * Developer              Code Review           Date                      Description
 * -----------------------------------------------------------------------------------------------                 
 * Prasanthi Kandula        48595             11/15/2016                Original Version
***************************************************************************************************/

@isTest
private Class CheckDuplicatesOnAction_T_HUM
{

   /**
    *  This method creates test data for test class
    *  @name setUpTestData
    *  @param NA
    *  @return NA
    *  @throws NA
    *  @see NA
    */
    @testSetup static void setUpTestData()
    {
        User oUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oUser;
        system.assertEquals(oUser.lastname,'test User1');
        
        HUMTriggerSwitch__c oTriggerSwitch = new HUMTriggerSwitch__c ();
        oTriggerSwitch.name ='MET_Action__c';
        oTriggerSwitch.Exeute_Trigger__c =true;
        insert oTriggerSwitch;
        system.assertEquals(oTriggerSwitch.name,'MET_Action__c');
    }
    
    /**
    *  This method tests the insert logic on Action object 
    *  @name testInsertOnAction
    *  @param NA
    *  @return NA
    *  @throws exception - Checks for all Actions when inserting a record and throws an exception if Action name already exists.
    *  @see NA
    */
    static testMethod void testInsertOnAction()
    {            
                 
        User oUser = [select Id,Name from User where name = 'test User1' limit 1]; 
        system.runAs(oUser)
        {
            MET_Action__c oAction = new MET_Action__c();
            oAction.Name = 'Test Action';
            oAction.Is_Active__c = true;
            insert oAction;
            system.assertEquals(oAction.Name,'Test Action');
            
            MET_Action__c oActionDuplicate = new MET_Action__c();
            oActionDuplicate.Name = 'Test Action';
            oActionDuplicate.Is_Active__c = true;
            try 
            {
                insert oActionDuplicate;
            }
            catch(DMLException e) 
            {
                system.assert(e.getMessage().contains(System.Label.UNIQUERECORD_ACTION_HUM));
            }
        }
    }
    
    /**
    *  This method tests the bulk insert on Action object. Also Throws an exception if Action name already exists.
    *  @name testBulkInsertOnAction
    *  @param NA
    *  @return NA
    *  @throws exception - Checks for all Actions when inserting a record and throws an exception if Action name already exists.
    *  @see NA
    */
    static testMethod void testBulkInsertOnAction()
    {            
        User oUser = [select Id,Name from User where name = 'test User1' limit 1];                
        system.runAs(oUser)
        {
            List<MET_Action__c> lstAction = new List<MET_Action__c>();
            for(integer i=0; i<202;i++)
            {
                MET_Action__c oAction = new MET_Action__c();
                oAction.Name = 'Test Action'+i;
                lstAction.add(oAction);
            }
            insert lstAction;
            system.assertEquals(lstAction.size(),202);
            List<MET_Action__c> lstActionDuplicate = new List<MET_Action__c>();
            for(integer i=0; i<4;i++) 
            {
                MET_Action__c oAction = new MET_Action__c();
                oAction.Name = 'Test Action'+i;
                lstActionDuplicate.add(oAction);
            }
            try 
            {
                insert lstActionDuplicate;
            }
            catch(DMLException e) 
            {
                system.assert(e.getMessage().contains(System.Label.UNIQUERECORD_ACTION_HUM));
            }
        }
    }
    
    /**
    *  This method tests the update on Action object. Also Throws an exception if Action name already exists.
    *  @name testUpdateOnAction
    *  @param NA
    *  @return NA
    *  @throws exception - Checks for all Actions when updating a record and throws an exception if Action name already exists.
    *  @see NA
    */
    static testMethod void testUpdateOnAction()
    {            
        User oUser = [select Id,Name from User where name = 'test User1' limit 1];                
        system.runAs(oUser)
        {
            MET_Action__c oAction = new MET_Action__c();
            oAction.Name = 'Test Action';
            oAction.Is_Active__c = true;
            insert oAction;
            system.assertEquals(oAction.Name,'Test Action');
            MET_Action__c oAction1 = new MET_Action__c();
            oAction1.Name = 'Test Action1';
            oAction1.Is_Active__c = true;
            insert oAction1;
            
            oAction.Name = 'New Test Action';
            update oAction;
            system.assertEquals(oAction.Name,'New Test Action');
            oAction.Name = 'Test Action1';
            try 
            {
                update oAction;
            }
            catch(DMLException e) 
            {
                system.assert(e.getMessage().contains(System.Label.UNIQUERECORD_ACTION_HUM));
            }
            
        }
    }
    
    /**
    *  This method tests the update on Action object. Also Throws an exception if Action name already exists.
    *  @name testBulkUpdateOnAction
    *  @param NA
    *  @return NA
    *  @throws exception - Checks for all Actions when updating a record and throws an exception if Action name already exists.
    *  @see 
    */
    static testMethod void testBulkUpdateOnAction()
    {            
        User oUser = [select Id,Name from User where name = 'test User1' limit 1];                
        system.runAs(oUser)
        {
            List<MET_Action__c> lstAction = new List<MET_Action__c>();
            for(integer i=0; i<5; i++)
            {
                MET_Action__c oAction = new MET_Action__c(); 
                oAction.Name = 'Test Action'+i;
                lstAction.add(oAction);
            }
            insert lstAction;
            system.assertEquals(lstAction[0].Name,'Test Action0');
            MET_Action__c oAction = new MET_Action__c(Id=lstAction[0].id,Name='Test Action6'); 
            MET_Action__c oAction1 = new MET_Action__c(Id=lstAction[1].id,Name='Test Action3'); 
            MET_Action__c oAction2 = new MET_Action__c(Id=lstAction[2].id,Name='Test Action4'); 
            List<MET_Action__c> lstMETActionDuplicate =  new MET_Action__c [] {oAction, oAction1, oAction2};
            try 
            {
                update lstMETActionDuplicate;
            }
            catch(DMLException e) 
            {
                system.assert(e.getMessage().contains(System.Label.UNIQUERECORD_ACTION_HUM));
            }
            
        }
    }
}
/***************************************************************************************************
 @Author AnupamaTavva 
 @Name CheckDuplicatesOnSource_T_HUM
 @CreateDate 11/15/2016
 @Description Class contains test methods for CheckDuplicatesOnSource_H_HUM controller
 @Version 1.0
 @Reference NA
   
 Modification Log :
 * Developer              Code Review           Date                      Description
 * -----------------------------------------------------------------------------------------------                 
 * Anupama Tavva            49268             11/25/2016                REQ - 290277-Displays an error message if the source name already exists.
***************************************************************************************************/

@isTest
private Class CheckDuplicatesOnSource_T_HUM
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
        oTriggerSwitch.name ='MET_Source__c';
        oTriggerSwitch.Exeute_Trigger__c =true;
        insert oTriggerSwitch;
        system.assertEquals(oTriggerSwitch.name,'MET_Source__c');
    }
    
    /**
    *  This method tests the insert logic on Source object 
    *  @name testInsertOnSource
    *  @param NA
    *  @return NA
    *  @throws exception - Checks for all Sources when inserting a record and throws an exception if Source name already exists.
    *  @see NA
    */
    static testMethod void testInsertOnSource()
    {            
                 
        User oUser = [select Id,Name from User where name = 'test User1' limit 1]; 
        system.runAs(oUser)
        {
            MET_Source__c oSource = new MET_Source__c();
            oSource.Name = 'Test Source';
            oSource.Is_Active__c = true;
            insert oSource;
            system.assertEquals(oSource.Name,'Test Source');
            
            MET_Source__c oSourceDuplicate = new MET_Source__c();
            oSourceDuplicate.Name = 'Test Source';
            oSourceDuplicate.Is_Active__c = true;
            try 
            {
                insert oSourceDuplicate;
            }
            catch(DMLException e) 
            {
                system.assert(e.getMessage().contains(Label.UNIQUERECORD_SOURCE_HUM));
            }
        }
    }
    
    /**
    *  This method tests the bulk insert on Source object. Also Throws an exception if source name already exists.
    *  @name testBulkInsertOnSource
    *  @param NA
    *  @return NA
    *  @throws exception - Checks for all sources when inserting a record and throws an exception if Source name already exists.
    *  @see NA
    */
    static testMethod void testBulkInsertOnSource()
    {            
        User oUser = [select Id,Name from User where name = 'test User1' limit 1];                
        system.runAs(oUser)
        {
            List<MET_Source__c> lstSource = new List<MET_Source__c>();
            for(integer i=0; i<202;i++)
            {
                MET_Source__c oSource = new MET_Source__c();
                oSource.Name = 'Test Source'+i;
                lstSource.add(oSource);
            }
            insert lstSource;
            system.assertEquals(lstSource.size(),202);
            List<MET_Source__c> lstSourceDuplicate = new List<MET_Source__c>();
            for(integer i=0; i<4;i++) 
            {
                MET_Source__c oSource = new MET_Source__c();
                oSource.Name = 'Test Source'+i;
                lstSourceDuplicate.add(oSource);
            }
            try 
            {
                insert lstSourceDuplicate;
            }
            catch(DMLException e) 
            {
                system.assert(e.getMessage().contains(Label.UNIQUERECORD_SOURCE_HUM));
            }
        }
    }
    
    /**
    *  This method tests the update on Source object. Also Throws an exception if Source name already exists.
    *  @name testUpdateOnSource 
    *  @param NA
    *  @return NA
    *  @throws exception - Checks for all Sources when updating a record and throws an exception if Source name already exists.
    *  @see NA
    */
    static testMethod void testUpdateOnSource()
    {            
        User oUser = [select Id,Name from User where name = 'test User1' limit 1];                
        system.runAs(oUser)
        {
            MET_Source__c oSource = new MET_Source__c ();
            oSource.Name = 'Test Source';
            oSource.Is_Active__c = true;
            insert oSource;
            system.assertEquals(oSource.Name,'Test Source');
            MET_Source__c oSource1 = new MET_Source__c();
            oSource1.Name = 'Test Source1';
            oSource1.Is_Active__c = true;
            insert oSource1;
            
            oSource.Name = 'New Test Source';
            update oSource;
            system.assertEquals(oSource.Name,'New Test Source');
            oSource.Name = 'Test Source1';
            try 
            {
                update oSource;
            }
            catch(DMLException e) 
            {
                system.assert(e.getMessage().contains(Label.UNIQUERECORD_SOURCE_HUM));
            }
            
        }
    }
    
    /**
    *  This method tests the update on Source object. Also Throws an exception if Source name already exists.
    *  @name testBulkUpdateOnSource
    *  @param NA
    *  @return NA
    *  @throws exception - Checks for all Source when updating a record and throws an exception if Source name already exists.
    *  @see 
    */
    static testMethod void testBulkUpdateOnSource()
    {            
        User oUser = [select Id,Name from User where name = 'test User1' limit 1];                
        system.runAs(oUser)
        {
            List<MET_Source__c> lstSource = new List<MET_Source__c>();
            for(integer i=0; i<5; i++)
            {
                MET_Source__c oSource = new MET_Source__c(); 
                oSource.Name = 'Test Source'+i;
                lstSource.add(oSource);
            }
            insert lstSource;
            system.assertEquals(lstSource[0].Name,'Test Source0');
            MET_Source__c oSource = new MET_Source__c(Id=lstSource[0].id,Name='Test Source6'); 
            MET_Source__c oSource1 = new MET_Source__c(Id=lstSource[1].id,Name='Test Source3'); 
            MET_Source__c oSource2 = new MET_Source__c(Id=lstSource[2].id,Name='Test Source4'); 
            List<MET_Source__c> lstMETSourceDuplicate =  new MET_Source__c [] {oSource, oSource1, oSource2};
            try 
            {
                update lstMETSourceDuplicate;
            }
            catch(DMLException e) 
            {
                system.assert(e.getMessage().contains(Label.UNIQUERECORD_SOURCE_HUM));
            }
            
        }
    }
}
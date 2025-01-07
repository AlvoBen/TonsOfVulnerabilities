/***************************************************************************************************
Apex Class Name  : DuplicateUserOnPreferences_T_HUM
Version          : 1.0
Created Date     : 06/27/2017
Function         : Test Class for DuplicateActionOnUserHelper_H_HUM
**************************************************************************************************
Modification Log:

* Developer Name       Review                       Date                        Description
*--------------------------------------------------------------------------------------------------
*  Santhosh Ganji                               06/27/2019                  Original Version. 
***************************************************************************************************/
@isTest
private Class DuplicateUserOnPreferences_T_HUM
{

   /**
    *  This method creates test data for test class
    *  @name setUpTestData
    *  @param NA
    *  @return NA
    *  @throws NA
    *  @see 
    */
    @testSetup static void setUpTestData()
    {
        User oUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oUser;
        system.assertEquals(oUser.lastname,'test User1');
        
        User oUser1 = HUMTestDataHelperTest.createTestUser('User2','Customer Care Specialist');
        insert oUser1;
        system.assertEquals(oUser1.lastname,'test User2');
         
        User oUser2 = HUMTestDataHelperTest.createTestUser('User3','Customer Care Specialist');
        insert oUser2;
        system.assertEquals(oUser2.lastname,'test User3');
            
        HUMTriggerSwitch__c objTrigswitch = new HUMTriggerSwitch__c ();
        objTrigswitch.name ='Default_Location__c';
        objTrigswitch.Exeute_Trigger__c =true;
        insert objTrigswitch;
        system.assertEquals(objTrigswitch.name,'Default_Location__c');
    }
    
    /**
    *  This method tests the insert logic on Preferences  object 
    *  @name testInsertOnPreferences
    *  @param NA
    *  @return NA
    *  @throws exception - Checks for all Preferences when inserting a record and throws an exception if associated user record is already exist in the object.
    *  @see 
    */
    static testMethod void testInsertOnPreferences()
    {            
     User oUser = [select Id,Name from User where name = 'test User1' limit 1];                
        system.runAs(oUser)
        {  
            Storefront_Location__c olocation = new Storefront_Location__c(Name = 'Virginia',Address_Line_1__c = '9005',City__c='Virginia',Zip_Code__c='40222',Org_Facility__c='12445',State__c='Virginia - VA');
            insert olocation;
            system.assertEquals(olocation.Name,'Virginia');
            
            Storefront_Preference__c oPreference = new Storefront_Preference__c();
            oPreference.Default_Location__c = olocation.Id;
            oPreference.User__c  = oUser.Id;
            insert oPreference;
            system.assertEquals(oPreference.Default_Location__c,olocation.Id);
                    
            Storefront_Preference__c oPreferenceDup = new Storefront_Preference__c();
            oPreferenceDup.Default_Location__c = olocation.Id;
            oPreferenceDup.User__c  = oUser.Id;
            try 
            {
              insert oPreferenceDup;
            }
            
            catch(DMLException e) 
            {
                system.assert(e.getMessage().contains(System.Label.UNIQUERECORD_PREFERENCES_HUM)); 
            }   
        }
    }
      
         
    /**
    *  This method tests the update on Preferences object. Also Throws an exception if associated Preferences already exists for the same user.
    *  @name testUpdateOnPreferences
    *  @param NA
    *  @return NA
    *  @throws exception - Checks for all added Preferences when updating a record and throws an exception if associated Preferences already exists for the same user.
    *  @see 
    */
    static testMethod void testUpdateOnPreferences()
    {            
        User oUser = [select Id,Name from User where name = 'test User1' limit 1];                
        system.runAs(oUser)
        {      
            User oUser1 = [select Id,Name from User where name = 'test User2' limit 1];                
            User oUser2 = [select Id,Name from User where name = 'test User3' limit 1]; 
                           
            Storefront_Location__c olocation = new Storefront_Location__c(Name = 'Virginia',Address_Line_1__c = '9005',City__c='Virginia',Zip_Code__c='40222',Org_Facility__c='12445',State__c='Virginia - VA');
            insert olocation;
            system.assertEquals(olocation.Name,'Virginia');
            
            Storefront_Preference__c oPreference = new Storefront_Preference__c();
            oPreference.Default_Location__c = olocation.Id;
            oPreference.User__c  = oUser.Id;
            insert oPreference;
            system.assertEquals(oPreference.Default_Location__c,olocation.Id);
                    
            Storefront_Preference__c oPreference1 = new Storefront_Preference__c();
            oPreference1.Default_Location__c = olocation.Id;
            oPreference1.User__c  = oUser1.Id;
            insert oPreference1;
            
            oPreference.User__c  = oUser2.Id;
            update oPreference;
            oPreference1.User__c  = oUser2.Id;
            try 
            {
                 update oPreference1;
            }
            catch(DMLException e)
            {
                system.assert(e.getMessage().contains(System.Label.UNIQUERECORD_PREFERENCES_HUM));
            }
                  
        }
    }
    
  }
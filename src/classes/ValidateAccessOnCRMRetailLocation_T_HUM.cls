/***************************************************************************************************
Apex Class Name  : ValidateAccessOnCRMRetailLocation_T_HUM
Version          : 1.0
Created Date     : 08/12/2019
Function         : Test Class for ValidateAccessOnCRMRetailLocation_H_HUM
**************************************************************************************************
Modification Log:

* Developer Name       Review                       Date                        Description
*--------------------------------------------------------------------------------------------------
*  Santhosh Ganji                               08/12/2019                    Original Version.
*  Abhishek Maurya                              07/03/2021                    USer Story 2146105 -CRM Storefront: Delete Calendar Event 
***************************************************************************************************/
@isTest
private class ValidateAccessOnCRMRetailLocation_T_HUM {
    
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
        
        HUMTriggerSwitch__c objTrigswitch = new HUMTriggerSwitch__c ();
        objTrigswitch.name ='Storefront_Location__c';
        objTrigswitch.Exeute_Trigger__c =true;
        insert objTrigswitch;
        system.assertEquals(objTrigswitch.name,'Storefront_Location__c');
    }
    
    /**
    *  This method tests the delete logic on Location object 
    *  @name testCheckForLocationAccessOnDelete
    *  @param NA
    *  @return NA
    *  @throws exception - Checks for user permissionset when deleting a record and throws an exception if user is not BusinessAdmin.
    *  @see 
    */
    static testMethod void testCheckForLocationAccessOnDelete()
    {
        User oUser = [select Id,Name from User where name = 'test User1' limit 1];  
        system.assertEquals(oUser.Name,'test User1');
        system.runAs(oUser)
        { 
            List<PermissionSet> crmretailBusinessAdmin = [Select id , Name from PermissionSet where name =: GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_BUSINESSADMIN];
            if(crmretailBusinessAdmin != null  )
            {
               PermissionSetAssignment psAssingment= new PermissionSetAssignment(assigneeId = oUser.id , PermissionSetId =crmretailBusinessAdmin[0].id);
               insert psAssingment;
               system.assertEquals(psAssingment.assigneeId, oUser.Id);
            }
            Storefront_Location__c location = new Storefront_Location__c(Name = 'Virginia',Address_Line_1__c = '9005',City__c='Virginia',Zip_Code__c='40222',Org_Facility__c='12445',State__c='Virginia - VA',Inactive_indicator__c = true);
            insert location;
            system.assertEquals(location.Name, 'Virginia');
            
            try 
            {
               delete location;
            }
            catch(DMLException e) 
            {
                system.assert(e.getMessage().contains(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_LOCATION_DEL_MSG)); 
            } 
        }
    }
	
	/**
    *  This method tests the delete logic on Location object 
    *  @name testActiveLocationDelete
    *  @param NA
    *  @return NA
    *  @throws exception - Checks if the location is in active before business admin deleting.
    *  @see 
    */
    static testMethod void testActiveLocationDelete()
    {
        User oUser = [select Id,Name from User where name = 'test User1' limit 1]; 
        system.assertEquals(oUser.Name,'test User1');
        system.runAs(oUser)
        { 
            List<PermissionSet> crmretailadmin = [Select id , Name from PermissionSet where name =: GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_400SUPERADMIN];
            if(crmretailadmin != null  )
            {
               PermissionSetAssignment psAssingment= new PermissionSetAssignment(assigneeId = oUser.id , PermissionSetId =crmretailadmin[0].id);
               insert psAssingment;
               system.assertEquals(psAssingment.assigneeId, oUser.Id);
            }
            Storefront_Location__c location = new Storefront_Location__c(Name = 'Virginia',Address_Line_1__c = '9005',City__c='Virginia',Zip_Code__c='40222',Org_Facility__c='12445',State__c='Virginia - VA',Inactive_indicator__c = false);
            insert location;
            system.assertEquals(location.Name, 'Virginia');
            
            try 
            {
               delete location;
            }
            catch(DMLException e) 
            {
                system.assert(e.getMessage().contains(System.Label.CRMRetail_Active_Location)); 
            } 
        }
    }
    /**
    *  This method tests the update logic on Location object 
    *  @name testActiveLocationUpdate
    *  @param NA
    *  @return NA
    *  @throws exception - Checks for user permissionset when deleting a record and throws an exception if user is not BusinessAdmin.
    *  @see 
    */
    static testMethod void testActiveLocationUpdate()
    {
        User oUser = [select Id,Name from User where name = 'test User1' limit 1];
        system.assertEquals(oUser.Name,'test User1');
        system.runAs(oUser)
        { 
            List<PermissionSet> crmretailBusinessAdmin = [Select id , Name from PermissionSet where name =: GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_BUSINESSADMIN];
            if(crmretailBusinessAdmin != null  )
            {
               PermissionSetAssignment psAssingment= new PermissionSetAssignment(assigneeId = oUser.id , PermissionSetId =crmretailBusinessAdmin[0].id);
               insert psAssingment;
               system.assertEquals(psAssingment.assigneeId, oUser.Id);
            }
            Storefront_Location__c location = new Storefront_Location__c(Name = 'Virginia',Address_Line_1__c = '9005',City__c='Virginia',Zip_Code__c='40222',Org_Facility__c='12445',State__c='Virginia - VA',Inactive_indicator__c = true);
            insert location;
            system.assertEquals(location.Name, 'Virginia');
            
            try 
            {
                update location;
            }
            catch(DMLException e) 
            {
                system.assert(e.getMessage().contains(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_LOCATION_EDIT_MSG)); 
            }     
            
        }
    }
}
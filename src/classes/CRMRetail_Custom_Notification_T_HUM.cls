/********************************************************************************************************************************************************
Apex Class Name  : CRMRetail_Custom_Notification_T_HUM
Version          : 1.0 
Created Date     : APR 27 2022
Function         :Test class for CRMRetail_Custom_Notification_C_HUM
Modification Log :       
* Developer                         Date                 Description
* -------------------------------------------------------------------------------------------------------------------------------------------------------               
* Vivek Sharma                   04/27/2022              User Story 3316820: T1PRJ0154546 / SF / MF9 Storefront - SNP: Change Notifications Section to Custom
*******************************************************************************************************************************************************/
@isTest
private class CRMRetail_Custom_Notification_T_HUM {
    /*
    * Method Name : prepareTestData
    * Description : Create common test Data
    * Return type : void
    * Parameter   : NA
    */
    @testSetup static void prepareTestData() 
    {
        User oCCSUser = HUMTestDataHelperTest.createTestUser('User1','System Administrator');
        insert oCCSUser;
        system.assertEquals(oCCSUser.LastName,'test User1');
        
        PermissionSet SuperAdmin = [SELECT Id FROM PermissionSet WHERE Name =: GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_400SUPERADMIN];
        PermissionSetAssignment supAdmin = new PermissionSetAssignment(AssigneeId = oCCSUser.id, PermissionSetId = SuperAdmin.Id);
        insert supAdmin;
        system.assertEquals(supAdmin.AssigneeId, oCCSUser.id);        		
        
        System.runAs (oCCSUser) 
        {
            Id oCRMRetailVisitorType =  Schema.SObjectType.Account.getRecordTypeInfosByName().get(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_RETAIL_VISITOR).getRecordTypeId();
            Date waiverDate = Date.newInstance(2021, 03, 16);
            
            Account oAcc =CRMRetail_TestDataFactory_H_HUM.createAccount('987654321', oCRMRetailVisitorType,Date.newInstance(1984, 01, 01),'01/01/1984', 'Daphiner', 'Superman', 'J', '1060920019',false, 'New York', '56605', '(123) 123-1231','test1234',waiverDate,'confirmed-veteran',NULL);
            System.AssertEquals(oAcc.Waiver_Date__c, waiverDate);
            
            Storefront_Location__c location = CRMRetail_TestDataFactory_H_HUM.createVirtualLocation();
            insert location;
            System.AssertEquals(location.Name, 'Tulsa');
            
            Storefront_I_Type__c InteractionType1 = CRMRetail_TestDataFactory_H_HUM.createInteractionType('Community');
            insert InteractionType1;
            System.AssertEquals(InteractionType1.Name, 'Community');
            
            Storefront_I_Reason__c InteractionReason = CRMRetail_TestDataFactory_H_HUM.createInteractionReason('Visitor Check-In (1st Time)' ,InteractionType1.Id);
            insert InteractionReason;
            system.assertEquals(InteractionReason.Interaction_Type__c, InteractionType1.id);
            
            Storefront_Interaction__c sfInt1 = CRMRetail_TestDataFactory_H_HUM.createInteraction(oAcc.Id,location.Id,InteractionReason.Id,False,'Member',Date.Today());
            sfInt1.Storefront_Vstr_Indicator__c = '{"SNP":"CC"}';
            insert sfInt1;
            system.assertEquals(sfInt1.Reason__c, InteractionReason.id);
        }
    }
    /*
    * Method Name : testMethod1
    * Description : Test method to cover  
    * Return Type : NA
    * Parameters : NA
    */
    @isTest static void testMethod1()
    {
        User oCCSUser = [Select id,UserName from User where LastName = 'test User1' LIMIT 1 ] ;
        system.assertEquals(oCCSUser.UserName,'user1user1@humcrmstest.com');   
        
        system.runAs(oCCSUser)
        {
            List<Storefront_Interaction__c> intList = [SELECT Id, Storefront_Vstr_Indicator__c FROM Storefront_Interaction__c];
            Test.startTest();
            String SNPIndicator = CRMRetail_Custom_Notification_C_HUM.getVisitorIndicators(intList[0].Id);
            system.assertEquals(intList[0].Storefront_Vstr_Indicator__c, SNPIndicator);
            Try{
                CRMRetail_Custom_Notification_C_HUM.getVisitorIndicators('abc');
            }catch(Exception e){
                
            }
            Boolean validate = CRMRetail_Custom_Notification_C_HUM.validateAccess();
            system.assertEquals(True, validate);
            Test.stopTest();
        }
    }
}
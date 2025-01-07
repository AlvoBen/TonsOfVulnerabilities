/*******************************************************************************************************************************
Apex Class Name : HUMTaskDeleteExtensionTest
Version         : 1.0
Created On      : 09/30/2021 
Function        : Test class for HUMTaskDeleteExtension
Test Class      : HUMTaskEditButtonOverrideExtensionTest
                  
Modification Log: 
* Developer Name               Code Review                         Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
  Ananya Singh                                                      27/30/2021                 Original Verison User story : 2561838
*************************************************************************************************************************/
@isTest
public class HUMTaskDeleteExtensionTest {
    /**
    * This method is used to insert the data only once and called automatically in all methods 
    *
    * @param  None      
    * @return  Void
    */
    @testSetup static void setup()
    {
        User oUser  = HUMTestDataHelperTest.createTestUser('User1','CRMRetail User');
        insert oUser;
        System.assertNotEquals(oUser, null);
        System.runAs(oUser) 
        {
            List<Task> lstTask = new List<Task>();
            Task objTask = new Task();
            objTask.RecordtypeId = Schema.SObjectType.Task.getRecordTypeInfosByName().get(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRM_RETAIL_TASK).getRecordTypeId();
            objTask.Subject = 'Test 1';
            objTask.Priority = 'Medium';
            objTask.Status = 'Not Started';
            objTask.CRMRetail_Automated__c = true;
            lstTask.add(objTask);

            Task objTsk = new Task();
            objTsk.RecordtypeId = Schema.SObjectType.Task.getRecordTypeInfosByName().get('CSI').getRecordTypeId();
            objTsk.Subject = 'Test 2';
            objTsk.Priority = 'Medium';
            objTsk.Status = 'Not Started';
            objTask.CRMRetail_Automated__c = true;
            lstTask.add(objTsk);

            insert lstTask;
            System.assertEquals(lstTask.size(), 2);
            
            CRMRetail_Edit_Delete_Task_Bypass__c cs = new CRMRetail_Edit_Delete_Task_Bypass__c();
            cs.Name = GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRM_RETAIL_TASK;
            insert cs;
            System.assertEquals(cs.Name,GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRM_RETAIL_TASK);
            
            CRMFunctionality_ONOFF_Switch__c switch_2561838 = new CRMFunctionality_ONOFF_Switch__c(Name = '2561838',IsON__c=true);
            insert switch_2561838;
            System.assertEquals(switch_2561838.IsON__c, true);
        }
    }
    
    /**
    * This method is used test to delete the task if the recordtype of the task is selected as CRMretail Task 
    *
    * @param  None      
    * @return  Void
    */
    @isTest static void testbyPassValidationToDelete()
    {
        User oUser = [select Id,Name from User where name = 'test User1' limit 1]; 
        System.assertEquals(oUser.Name,'test User1');
        system.runAs(oUser)
        {
            List<Task> lstTsk = [Select RecordType.Name FROM Task WHERE Subject = 'Test 1' LIMIT 1];
            System.assertEquals(lstTsk.size(), 1);
            
            Test.startTest();
            ApexPages.StandardController oStdController = new ApexPages.StandardController(lstTsk[0]);
            HUMTaskDeleteExtension oExtension = new  HUMTaskDeleteExtension(oStdController);
            oExtension.urlRedirect();
            String redirectNextPage = oExtension.urlRedirect().getURL();
            System.assert(redirectNextPage != null);
            Test.stopTest();
        }
        
    }
    
    /**
    * This method is used test to not delete task if the recordType is not CRMretail task
    *
    * @param  None      
    * @return  Void
    */
    @isTest static void testbyShowingErrorMsg()
    {
        User oUser = [select Id,Name from User where name = 'test User1' limit 1]; 
        System.assertEquals(oUser.Name,'test User1');
        system.runAs(oUser)
        {
            List<Task> lstTsk = [Select Recordtype.Name FROM task WHERE Subject = 'Test 2' LIMIT 1];
            System.assertEquals(lstTsk.size(),1);
            
            Test.startTest();
            ApexPages.StandardController oStdController = new ApexPages.StandardController(lstTsk[0]);
            HUMTaskDeleteExtension oExtension = new  HUMTaskDeleteExtension(oStdController);
            oExtension.urlRedirect();
            System.assert(oExtension.urlRedirect() == null);
            Test.stopTest();
        }
    }
}
/****************************************************************************
Apex Class Name  : HUMAttachmentEditExtensionTest
Version          : 1.0 
Created Date     : JUNE 09 2014
Function         : Test class to HUMAttachementEditExtension Apex class
Modification Log :
 *   Modification ID     Developer          Code Review             Date                    Description
 * -----------------------------------------------------------------------------------------------------------------------------------------------------------               
 *           1.0         Sagar Tapadia       17248                  06/09/2014             Original Version  
 *           1.1         Chaitanya Kumar     17248                  07/03/2014             Added relevant Assertions
 *           1.2         Santhi Mandava      20576                  01/28/2015             Code Modified to fix test class failure Defect # 164139 
 *           1.3         Tanushree Hazari    21906                  03/24/2015             Modified line 43-56 as part of REQ-82518 to increase code coverage
 *           1.4         Tanushree Hazari    22388                  04/16/2015             Code modified for adding oTask.Type value to fix 
                                                                                           test class failure Defect#179884.0001
* Santhi Mandava                                                    03/12/2019             Removed "DpaaSWebServiceMockImpl" usage.
 *************************************************************************************************************************************************************/
@isTest
private class HUMAttachmentEditExtensionTest 
{  
    /*
     * Method Name : createNewTaskAttachment
     * Description : Emulate creation of Custom Attachment related to Task
     * Return type : void
     * Paramater   : NA
     */
    static testMethod void createNewTaskAttachment() 
    {       
        //Load Constants from Custom Setting
        HUMTestDataHelperTest.getHUMConstantsData();

        Case objCase = HUMTestDataHelperTest.getCase();
        insert objCase;
        system.assertNotEquals(objCase,null,'Value not matched');

        Task objTask = HUMTestDataHelperTest.getTaskRecord();
        objTask.WhatId = objCase.Id;
        objTask.Type='Work Task';
        objTask.Status='Closed';
        insert objTask;
        system.assertNotEquals(objTask,null,'Value not matched');

        PageReference pg = Page.HUMAttachmentEdit;
        Test.setCurrentPageReference(pg);

        Apexpages.currentPage().getParameters().put('taskID',objTask.Id);
        ApexPages.Standardcontroller stdController = new ApexPages.Standardcontroller(new Attachment__c());
        
         List<HUMConstants__c> lstConstants = new List<HUMConstants__c>();
         lstConstants.add(new HUMConstants__c(Name='HUMTask',StringValue__c='Task'));
         lstConstants.add(new HUMConstants__c(Name='HUMClosed',StringValue__c='Closed'));         
         insert lstConstants;
         System.assertEquals(lstConstants.size(), 2);
         
        Test.startTest();

        HUMAttachmentEditExtension attachService = new HUMAttachmentEditExtension(stdController);
        attachService.attachment.Name = 'New Attachment';
        aTTachService.attachment.Related_To_Task__c = objTask.Id;
        attachService.saveAttachment();
        attachService.onCancel();

        Test.stopTest();

        //Assert if a new Attachment is created and Proper Values are populated
        List<Attachment__c> getAttach = [Select 
                                         Name ,id,Related_To_Task__c,Related_To_Case__c,Related_To__c 
                                         from Attachment__c 
                                         where Related_To_Case__c=:objCase.id and Related_To_Task__c=:objTask.id];

        system.assertEquals(getAttach.size(),1);
        system.assertEquals(getAttach[0].Name,'New Attachment');
    }

    /*
     * Method Name : constructorException
     * Description : Emulate constructor Exception
     * Return type : void
     * Paramater   : NA
     */
    static testMethod void constructorException() 
    {
        //Load Constants from Custom Setting
        HUMTestDataHelperTest.getHUMConstantsData();

        Case objCase = HUMTestDataHelperTest.getCase();
        insert objCase;
        system.assertNotEquals(objCase,null,'Value not matched');

        Task objTask = HUMTestDataHelperTest.getTaskRecord();
        objTask.WhatId = objCase.Id;
        objTask.Type='Work Task';
        objTask.Status='Closed';
        insert objTask;
        system.assertNotEquals(objTask,null,'Value not matched');

        PageReference pg = Page.HUMAttachmentEdit;
        Test.setCurrentPageReference(pg);

        //Include Task ID Parameters 
        Apexpages.currentPage().getParameters().put('taskID','Dummy');
        ApexPages.Standardcontroller stdController = new ApexPages.Standardcontroller(new Attachment__c());
      
        HUMAttachmentEditExtension attachService;

        Test.startTest();

        attachService = new HUMAttachmentEditExtension(stdController);
        attachService.attachment.Name = 'New Attachment';
        attachService.saveAttachment();

        Test.stopTest();

        //In case of error , log objects are populated
        System.assertEquals(attachService.listErrorLogs.size()>0,true);
    }

    /*
     * Method Name : ediTaskAttachment
     * Description : Emulate update of Custom Attachment related to Task
     * Return type : void
     * Paramater   : NA
     */
    static testMethod void editTaskAttachment() 
    {
        //Load Constants from Custom Setting
        HUMTestDataHelperTest.getHUMConstantsData();

        Case objCase = HUMTestDataHelperTest.getCase();
        insert objCase;
        system.assertNotEquals(objCase,null,'Value not matched');

        Task objTask = HUMTestDataHelperTest.getTaskRecord();
        objTask.WhatId = objCase.Id;
        objTask.Type='Work Task';
        objTask.Status='In Progress';
        insert objTask;
        system.assertNotEquals(objTask,null,'Value not matched');

        Attachment__c customAttachment =HUMTestDataHelperTest.getCustomAttachment();
        customAttachment.Related_to_Case__c = objCase.id;
        customAttachment.Related_to_Task__c = objtask.id;
        insert customAttachment;
        system.assertNotEquals(customAttachment,null,'Value not matched');

        PageReference pg = Page.HUMAttachmentEdit;
        Test.setCurrentPageReference(pg);

        //In Case of new Attachment record ,instantiate a standard controller
        ApexPages.Standardcontroller stdController = new ApexPages.Standardcontroller(customAttachment);

        Test.startTest();

        HUMAttachmentEditExtension attachService = new HUMAttachmentEditExtension(stdController);
        attachService.attachment.Name = 'Edit Attachment';
        attachService.saveAttachment();

        Test.stopTest();

        //Assert if a new Attachment is created and Proper Values are populated
        List<Attachment__c> getAttach = [Select 
                                         Name ,id,Related_To_Task__c,Related_To_Case__c
                                         from Attachment__c 
                                         where Related_To_Case__c=:objCase.id and Related_To_Task__c=:objTask.id];

        system.assertEquals(getAttach.size(),1);       
        //Assert the Changed Name
        system.assertEquals(getAttach[0].Name,'Edit Attachment');
    }

    /*
     * Method Name : createNewCaseAttachment
     * Description : Emulate creation of Custom Attachment related to Case
     * Return type : void
     * Paramater   : NA
     */
    static testMethod void createNewCaseAttachment() 
    {
        //Load Constants from Custom Setting
        HUMTestDataHelperTest.getHUMConstantsData();

        Case objCase = HUMTestDataHelperTest.getCase();
        insert objCase;
        system.assertNotEquals(objCase,null,'Value not matched');

        PageReference pg = Page.HUMAttachmentEdit;
        Test.setCurrentPageReference(pg);

        //Include CaseID Parameters 
        Apexpages.currentPage().getParameters().put('caseID',objCase.Id);
        //In Case of new Attachment record ,instantiate a standard controller
        ApexPages.Standardcontroller stdController = new ApexPages.Standardcontroller(new Attachment__c());

        Test.startTest();

        HUMAttachmentEditExtension attachService = new HUMAttachmentEditExtension(stdController);
        attachService.attachment.Name = 'New Attachment';
        attachService.saveAttachment();

        Test.stopTest();

        //Assert if a new Attachment is created and Proper Values are populated
        List<Attachment__c> getAttach = [Select 
                                         Name ,id,Related_To_Task__c,Related_To_Case__c
                                         from Attachment__c 
                                         where Related_To_Case__c=:objCase.id];

        system.assertEquals(getAttach.size(),1);
        system.assertEquals(getAttach[0].Related_To_Task__c,null);
        system.assertEquals(getAttach[0].Related_To_Case__c,objCase.id);       
    } 

    /*
     * Method Name : editCaseAttachment
     * Description : Emulate update of Custom Attachment related to Case
     * Return type : void
     * Paramater   : NA
     */
    static testMethod void editCaseAttachment() 
    {
        //Load Constants from Custom Setting
        HUMTestDataHelperTest.getHUMConstantsData();

        Case objCase = HUMTestDataHelperTest.getCase();
        insert objCase;
        system.assertNotEquals(objCase,null,'Value not matched');

        Attachment__c customAttachment =HUMTestDataHelperTest.getCustomAttachment();
        customAttachment.Related_to_Case__c = objCase.id;
        insert customAttachment;
        system.assertNotEquals(customAttachment,null,'Value not matched');

        PageReference pg = Page.HUMAttachmentEdit;
        Test.setCurrentPageReference(pg);
        
        ApexPages.Standardcontroller stdController = new ApexPages.Standardcontroller(customAttachment);

        Test.startTest();

        HUMAttachmentEditExtension attachService = new HUMAttachmentEditExtension(stdController);
        attachService.attachment.Name = 'Edit Attachment';
        attachService.saveAttachment();

        Test.stopTest();

        //Assert if a new Attachment is created and Proper Values are populated
        List<Attachment__c> getAttach = [Select 
                                         Name ,id,Related_To_Task__c 
                                         from Attachment__c 
                                         where Related_To_Case__c=:objCase.id];
        system.assertEquals(getAttach.size(),1);

        //Assert the Changed Name
        system.assertEquals(getAttach[0].Name,'Edit Attachment');
    } 
}
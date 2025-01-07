/****************************************************************************
Apex Class Name  : HUMAttachmentRelatedListHelperTest
Version          : 1.0 
Created Date     : JUNE 09 2014
Function         : Test class to HUMAttachmentRelatedListHelper Apex class
Modification Log :
 *   Modification ID     Developer        Code Review         Date                 Description
 * ------------------------------------------------------------------------------------------------------------------------------                
 *           1.0         Sagar Tapadia     17248              09/06/2014           Original Version  
 *           1.1         Santhi Mandava    20576              01/28/2015           Code Modified to fix test class failure Defect # 164139 
 *           1.2         Tanushree Hazari  21906              03/20/2015           Added line 102 to increase code coverage for REQ-82518 
 *           1.3         Tanushree Hazari  22388              04/21/2015           Code modified for PROD defect 179884.0001
 *           1.4         Mithra Bharadwaj  345536             03/22/2019           REQ-  383827 Added code to download the attachments from task
  *          1.5         Santhi Mandava                       03/12/2019           Removed "DpaaSWebServiceMockImpl" usage.
 **********************************************************************************************************************************/
@isTest
private class HUMAttachmentRelatedListHelperTest 
{
    /*
     * Method Name : getTaskAttachmentsPositiveCase
     * Description : Test method to get task related attachments
     * Return type : void
     * Paramater   : NA
     */
    static testMethod void getTaskAttachmentsPositiveCase() 
    {
        //Load Constants from Custom Setting
        HUMTestDataHelperTest.getHUMConstantsData();

        Case objCase = HUMTestDataHelperTest.getCase();
        insert objCase;
        system.assertNotEquals(objCase,null,'Value not matched');

        Task objTask = HUMTestDataHelperTest.getTaskRecord();
        objTask.WhatId = objCase.Id;
        objTask.Type='Work Task';
        insert objTask;
        system.assertNotEquals(objTask,null,'Value not matched');

        Attachment__c objAttach = HUMTestDataHelperTest.getCustomAttachment();
        objAttach.Related_To_Task__c = (ID)(objTask.Id);
        objAttach.Related_To_Case__c = objCase.Id;
        insert objAttach;
        system.assertNotEquals(objAttach,null,'Value not matched');

        // Add id parameter to URL
        ApexPages.currentPage().getParameters().put('id',objTask.Id);
        Test.startTest();

        HUMAttachmentRelatedListHelper attachRelated = new HUMAttachmentRelatedListHelper();

        Test.stopTest();
        //Only one record should return for assert
        system.assertEquals(attachRelated.lstAttachments.size(),1);
    }

    /*
     * Method Name : getTaskAttachmentsNegativeCase
     * Description : Test method to get task related attachments . As no ID is passed , no results will be returned
     * Return type : void
     * Paramater   : NA
     */
    static testMethod void getTaskAttachmentsNegativeCase()
    {
        //Load Constants from Custom Setting
        HUMTestDataHelperTest.getHUMConstantsData();

        Case objCase = HUMTestDataHelperTest.getCase();
        insert objCase;
        system.assertNotEquals(objCase,null,'Value not matched');
        
        Task objTask = HUMTestDataHelperTest.getTaskRecord();
        objTask.WhatId = objCase.Id;
        objTask.Type='Work Task';
        insert objTask;
        system.assertNotEquals(objTask,null,'Value not matched');

        //Create Custom Attachemnt Record
        Attachment__c objAttach = HUMTestDataHelperTest.getCustomAttachment();
        objAttach.Related_To_Task__c = (ID)(objTask.Id);
        objAttach.Related_To_Case__c = objCase.Id;
        insert objAttach;
        system.assertNotEquals(objAttach,null,'Value not matched');

        Test.startTest();
        
        HUMAttachmentRelatedListHelper attachRelated = new HUMAttachmentRelatedListHelper();

        Test.stopTest();
        //List should be empty
        system.assertEquals(attachRelated.lstAttachments.size(),0);
    }

    /*
     * Method Name : constructorException
     * Description : Test for constructor exception
     * Return type : void
     * Paramater   : NA
     */
    static testMethod void constructorException() 
    {
        //Load Constants from Custom Setting
        HUMTestDataHelperTest.getHUMConstantsData();
        HUMAttachmentRelatedListHelper attachRelated ;
        // Add id parameter to URL
        ApexPages.currentPage().getParameters().put('id','Dummy');

        Test.startTest();

            attachRelated = new HUMAttachmentRelatedListHelper();
            attachRelated.queryAttachments();
        Test.stopTest();
        //Error logs are populated
        system.assertEquals(attachRelated.listErrorLogs.size()>0,true);
    }
}
/******************************************************************************************************************************
Apex Class Name  : HUMExceptionHelperTest
Version          : 1.0 
Created Date     : July 13 2014
Function         : Test class for HUMExceptionHelper

Modification Log :
 * Developer                  Code Review         Date                 Description
 * ------------------------------------------------------------------------------------------------------------------------------                
 * Chaitanya Kumar             17152             07/13/2014          Original Version                                                                                
 * SuryaKumari Medicherla      20576             01/28/2015          Code Modified to fix test class failure Defect # 164139   
 * Chaitanya                                     01/30/2015          Code Modified to fix test class failure
 * Santhi Mandava                                03/12/2019          Removed "DpaaSWebServiceMockImpl" usage.
 *********************************************************************************************************************************/
@isTest
private class HUMExceptionHelperTest 
{

    /*
     * Method Name   : throwPositiveCustomException
     * Description   : Throw an error message to display on the Page for information purpose
     * Return Type   : void
     * Parameters    : NA
     */
    static testMethod void throwPositiveCustomException() 
    {     
        //Load Constants from Custom Setting
        HUMTestDataHelperTest.getHUMConstantsData();
        //Set the Current Page Reference
        PageReference pg =Page.HUMAttachmentEdit;
        Test.setCurrentPageReference(pg); 
                      
        Test.startTest();       
        try 
        {
            throw new HUMCustomException('Show Custom Error Message, Please enter 3 digit number');
        }
        Catch(Exception e) 
        {
            //Log the errors
            HUMExceptionHelper.logErrors(e,'HUMExceptionHelperTest','throwPositiveCustomException');
        }       
        Test.stopTest();        
                
        System.assert(ApexPages.hasMessages());     
        //Assert no Error Log objects in database. This is a valid Error Message
        System.assertEquals(0,[Select id from Error_log__c].size());        
    }

    /*
     * Method Name   : throwIntegrationException
     * Description   : Log a Integration error into Error Log table
     * Return Type   : void
     * Parameters    : NA
     */
    static testMethod void throwIntegrationException() 
    {        
        //Load Constants from Custom Setting
        HUMTestDataHelperTest.getHUMConstantsData();
        //Set the Current Page Reference
        PageReference pg =Page.HUMAttachmentEdit;
        Test.setCurrentPageReference(pg);   
                    
        //Insert Error code look up object
        Error_Code_Lookup__c errorLookUp = HUMTestDataHelperTest.getErrorLookUpRecord();
        errorLookup.error_code__c='101';
        errorLookup.Description__c='Integration Error';
        errorLookup.User_Friendly_Message__c='Integration Error';
        insert errorLookUp;          
           
        Test.startTest();       
        try
        {
            //Paramters - Errorcode , Error Message , End point URL , Type of Integration
            throw new HUMCustomException(new HUMCustomExceptionHelper('101','Failied Integration','End Point','Integration Error'));
        }
        Catch(Exception e) 
        {
            //Log the errors
            HUMExceptionHelper.logErrors(e,'HUMExceptionHelperTest','throwIntegrationException');
        }   
        Test.stopTest();    
            
        System.assert(ApexPages.hasMessages());     
        List<Error_Log__c> errorLogs=[Select id,Type__c from Error_Log__c];     
        //Assert Error type
        System.assertEquals(errorLogs[0].type__c,'Integration Error');      
    }
    
    /*
     * Method Name   : throwDMLUpsertExceptionNegative
     * Description   : Log DML Upsert Exception into error code look up table
     * Return Type   : void
     * Parameters    : NA
     */
    static testMethod void throwDMLUpsertExceptionNegative() 
    {      
        //Load Constants from Custom Setting
        HUMTestDataHelperTest.getHUMConstantsData();        
        List<Attachment> listAttachments = new List<Attachment>();      
        Attachment standardAttachment = HUMTestDataHelperTest.getAttachmentRecord();
        listAttachments.add(standardAttachment);   
         
        //Set the Current Page Reference
        PageReference pg =Page.HUMAttachmentEdit;
        Test.setCurrentPageReference(pg);    
                   
        Test.startTest();
            List<Database.UpsertREsult> upsertResults = Database.upsert(listAttachments,false);
        Test.stopTest();                
        
        // True is returned, as update resulted in DML Exception
        boolean isError= HUMExceptionHelper.processUpsertResults(upsertResults ,listAttachments ,'DummyExceptionTestingClass','upsertRecords','Attachment');        
        System.assert(isError);     
        List<Error_Log__c> errorLogs=[Select id,Type__c,Error_code__c from Error_Log__c];       
        System.assertEquals(errorLogs[0].Type__c,'Application Error');
    }
            
    /*
     * Method Name   : throwDMLsaveExceptionPositive
     * Description   : Show Error message on page in case of DML Exception due to validation Error
     * Return Type   : void
     * Parameters    : NA
     */
    static testMethod void throwDMLSaveExceptionPositive() 
    {    
        //Load Constants from Custom Setting
        HUMTestDataHelperTest.getHUMConstantsData();        
        //Create Case
        Case objCase = HUMTestDataHelperTest.getCase();
        insert objCase;
        //Create Task
        Task objTask = HUMTestDataHelperTest.getTaskRecord();
        objTask.WhatId = objCase.Id;
        insert objTask;     
        
        Attachment__c customAttachment =HUMTestDataHelperTest.getCustomAttachment();
        customAttachment.Related_to_Case__c=objCase.id;
        insert customAttachment;        
        List<Attachment> listAttachments = new List<Attachment>();      
        Attachment standardAttachment = HUMTestDataHelperTest.getAttachmentRecord();
        standardAttachment.parentID=customAttachment.id;
        listAttachments.add(standardAttachment);
        insert listAttachments;
        
        //Set the Current Page Reference
        PageReference pg =Page.HUMAttachmentEdit;
        Test.setCurrentPageReference(pg);       
        
        Test.startTest();           
            listAttachments.clear();
            Attachment standardAttachmentDuplicate= HUMTestDataHelperTest.getAttachmentRecord();
            standardAttachmentDuplicate.parentID=customAttachment.id;
            listAttachments.add(standardAttachmentDuplicate);
            List<Database.saveREsult> saveResults = Database.insert(listAttachments,false);
        Test.stopTest();        
        
        // False is returned, as update resulted in NO Valid DML Exception
        boolean isError= HUMExceptionHelper.processsaveResults(saveResults ,listAttachments ,'DummyExceptionTestingClass','throwDMLSaveExceptionPositive','Attachment');
        System.assert(!isError);        
        //Assert no Error Log objects in database
        System.assertEquals(0,[Select id,Type__c,Error_code__c from Error_Log__c where object_type__c='Attachment'].size());    
    }
    
    /*
     * Method Name   : throwDMLSaveExceptionNegative
     * Description   : Show Error message on page in case of DML Exception 
     * Return Type   : void
     * Parameters    : NA
     */
    static testMethod void throwDMLSaveExceptionNegative() 
    {        
        //Load Constants from Custom Setting
        HUMTestDataHelperTest.getHUMConstantsData();        
        List<Account> lstAccounts = new List<Account>{new Account()};
        //Set the Current Page Reference
        PageReference pg =Page.HUMAttachmentEdit;
        Test.setCurrentPageReference(pg);       
        
        Test.startTest();           
            List<Database.saveREsult> saveResults = Database.insert(lstAccounts,false);
        Test.stopTest();        
        
        // True is returned, as update resulted in Valid DML Exception
        boolean isError= HUMExceptionHelper.processsaveResults(saveResults ,lstAccounts ,'DummyExceptionTestingClass','throwDMLSaveExceptionNegative','Account');       
        System.assert(isError);     
        //Assert  Log objects in database
        System.assertEquals(1,[Select id from Error_log__c].size());    
    }
    
    /*
     * Method Name   : throwCallOutException
     * Description   : Log a Call out Exception into Error code look up table
     * Return Type   : void
     * Parameters    : NA
     */
    static testMethod void throwCallOutException() 
    {        
        //Load Constants from Custom Setting
        HUMTestDataHelperTest.getHUMConstantsData();        
        //Insert Error code look up object
        Error_Code_Lookup__c errorLookUp = HUMTestDataHelperTest.getErrorLookUpRecord();
        errorLookup.error_code__c='System.CallOutException';
        errorLookup.Description__c='Integration Error';
        errorLookup.User_Friendly_Message__c='Integration Error';
        insert errorLookUp;
        
        //Set the Current Page Reference
        PageReference pg =Page.HUMAttachmentEdit;
        Test.setCurrentPageReference(pg);   
        Http h = new Http();
        HttpRequest req = new HttpRequest();
        req.setEndPoint('DummyURL');
        req.setMethod('GET');   
        
        Test.startTest();       
        try 
        {           
            //This will lead to Call out Exception
            HTTPResponse response =h.send(req);
        }
        Catch(Exception e) 
        {            
            //Log the errors
            HUMExceptionHelper.logErrors(e,'HUMExceptionHelperTest','throwCallOutException');           
            //Log Call out Exception
            HUMExceptionHelper.logCallOutExceptions(e,'HUMExceptionHelperTest','throwCallOutException');
        }       
        Test.stopTest();        
        
        System.assert(ApexPages.hasMessages());     
        List<Error_Log__c> errorLogs=[Select id,Type__c,Error_Code__c from Error_Log__c];       
        //Assert Error Type
        System.assertEquals(errorLogs[0].type__c,'Application Error');
    }
    

    /*
     * Method Name   : throwDMLDeleteExceptionNegative
     * Description   : Show Error message on page in case of DML Exception due to validation Error
     * Return Type   : void
     * Parameters    : NA
     */
    static testMethod void throwDMLDeleteExceptionNegative() 
    {    
        //Load Constants from Custom Setting
        HUMTestDataHelperTest.getHUMConstantsData();    
        //Create Case
        Case objCase = HUMTestDataHelperTest.getCase();
        insert objCase;
        //Create Task
        Task objTask = HUMTestDataHelperTest.getTaskRecord();
        objTask.WhatId = objCase.Id;
        insert objTask;     
        
        Attachment__c customAttachment =HUMTestDataHelperTest.getCustomAttachment();
        customAttachment.Related_to_Case__c=objCase.id;
        insert customAttachment;        
        List<Attachment> listAttachments = new List<Attachment>();      
        Attachment standardAttachment = HUMTestDataHelperTest.getAttachmentRecord();
        standardAttachment.parentID=customAttachment.id;
        listAttachments.add(standardAttachment);
        insert listAttachments;
        
        //Set the Current Page Reference
        PageReference pg =Page.HUMAttachmentEdit;
        Test.setCurrentPageReference(pg);    
           
        Test.startTest();
            List<Database.deleteResult> deleteResults = Database.Delete(listAttachments,false);
        Test.stopTest();        
        
        // Code throws validation error  , Which need not be logged into error look up table
        boolean isError= HUMExceptionHelper.processDeleteResults(deleteResults ,listAttachments ,'DummyExceptionTestingClass','upsertRecords','Attachment');        
        System.assert(!isError);        
        List<Error_Log__c> errorLogs=[Select id,Type__c,Error_code__c from Error_Log__c where object_type__c='Attachment'];       
        //Assert Error records
        System.assertEquals(0,errorLogs.size());
    }
    
    /*
     * Method Name   : throwDMLDeleteExceptionPositive
     * Description   : No Error is returned in case of DML Exception
     * Return Type   : void
     * Parameters    : NA
     */
    static testMethod void throwDMLDeleteExceptionPositive() 
    {    
        //Load Constants from Custom Setting
        HUMTestDataHelperTest.getHUMConstantsData();        
        //Create Case
        Case objCase = HUMTestDataHelperTest.getCase();
        insert objCase; 
        List<Attachment> listAttachments = new List<Attachment>();  
        Attachment standardAttachment = HUMTestDataHelperTest.getAttachmentRecord();
        standardAttachment.parentID=objCase.id;
        listAttachments.add(standardAttachment);
        insert listAttachments;     
        delete listAttachments; 
        
        //Set the Current Page Reference
        PageReference pg =Page.HUMAttachmentEdit;
        Test.setCurrentPageReference(pg);    
           
        Test.startTest();
            List<Database.deleteResult> deleteResults = Database.Delete(listAttachments,false);
        Test.stopTest();    
        
        // True is returned, as deleted resulted in DML exception
        boolean isError= HUMExceptionHelper.processDeleteResults(deleteResults ,listAttachments ,'DummyExceptionTestingClass','deleteResults','Attachment');    
        //Assert the Message , true
        System.assert(isError);     
        List<Error_Log__c> errorLogs=[Select id,Type__c,Error_code__c from Error_Log__c];       
        //Assert One error records created
        System.assertEquals(1,errorLogs.size());
    }
    
    /*
     * Method Name   : logErrors
     * Description   : Log the records into error log table
     * Return Type   : void
     * Parameters    : NA
     */
     static testMethod void logErrors() 
     {       
        HUMExceptionHelper exceptionLogger = new HUMExceptionHelper();
        exceptionLogger.listErrorLogs = new List<Error_Log__c>{new Error_Log__c(Error_code__c='101', Error_Message__C='TEst Error')};
        
        Test.startTest();
            exceptionLogger.saveLog();
        Test.stopTest();        
        
        //Assert one record in table
        List<Error_Log__c> errorLogs=[Select id,Type__c,Error_code__c from Error_Log__c];       
        System.assertEquals(1,errorLogs.size());
     }

}
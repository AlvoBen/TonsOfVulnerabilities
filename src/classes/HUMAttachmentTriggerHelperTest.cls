/****************************************************************************
Apex Class Name  : HUMAttachmentTriggerHelperTest 
Version          : 1.0 
Created Date     : JUNE 09 2014
Function         : Test class to HUMAttachmentTriggerHelper  Apex class
Modification Log :
 *   Modification ID     Developer          Code Review      Date                 Description
 * ------------------------------------------------------------------------------------------------------------------------------                
 *           1.0         Chaitanya Kumar    17248           06/09/2014          Original Version  
 *           1.1         Chaitanya Kumar    17248           07/03/2014          Updated assertions
 *           1.2         Santhi Mandava     20576           01/28/2015          Code Modified to fix test class failure Defect # 164139 
 *           1.3         Vishal Verma       21654           03/20/2015          Added custom settings record to increase the coverage. 
 *           1.4         Vishal Verma                       05/12/2015          Added createCustomSetting method with testSetup annotation to create test data.
 *           1.5         Shruthi Karanth    30921           04/11/2016          REQ - 262439:Interchanged the setting of mock classes to resolve Collection store exception.
 *           1.6         Srivinas Damera                    10/27/2018          REQ - 377478 - eHub DSS 2.0 - Deployment - for AddDcoumentservice
 *           1.7         Santhi Mandava                     03/12/2019          Removed "DpaaSWebServiceMockImpl" usage.
 *			 1.8		 Pooja Kumbhar						04/29/2020			Checkmarx test method without assert error fix.
 *			 1.9		 Pooja Kumbhar                      09/10/2020			Code changes for test coverage.
 **********************************************************************************************************************************/

@IsTest
private class HUMAttachmentTriggerHelperTest 
{
    /*
     * Method Name : createCustomSetting 
     * Description : Method is used to create custom setting data ifor Test class
     * Return type : void
     * Return type : NA
     */
    @testSetup 
    static void createCustomSetting() 
    {
        insert (new HUM_Webservice_Callout__c (name='EhubAddDocumentService',Certificate_Name__c='cert_dev_name_in_salesforce',Request_Type__c='POST',Content_Type__c='application/json',Timeout__c='120000',End_Point_URL__c='SampleUrl',Active__c=true));
        insert (new HUMConstants__c(IntegerValue__c=1,StringValue__c='1',Name=' HUMLISTSIZEONE'));
        HUMCipherCloudSettings__c cipherCloudSetting = new HUMCipherCloudSettings__c(Name = 'APPLICATION_NAME' , Value__c = 'test');
        insert cipherCloudSetting;
        system.assert(cipherCloudSetting.id != null);
        //Load Constants from Custom Setting
        HUMTestDataHelperTest.getHUMConstantsData();
    }
    
    /*
     * Method Name : sendRequestToEhubTest 
     * Description : For testing send bytes data through eHub service
     * Return type : void
     * Return type : NA
     */
    static testMethod void sendRequestToEhubTest() 
    {
         Test.setMock(HttpCalloutMock.class, new EhubDocumentInsertResponseGenerator());
       
        Case objCase = HUMTestDataHelperTest.getCase();
        insert objCase;
        Attachment__c attObj = new Attachment__c();//Select Id, Version_Id__c,DocId__c from 
        attObj.Related_To_Case__c = objCase.id;
        insert attObj;
        
        Blob b = Blob.valueOf('Test Data');
        Attachment attachmentObj = new Attachment();
        attachmentObj.ParentId = attObj.ID;
        attachmentObj.Name = 'Test Attachment for Parent';
        attachmentObj.Body = b;
        insert attachmentObj;
		System.assertEquals(attachmentObj.Name, 'Test Attachment for Parent');
        
        String sUser = [Select Network_User_Id__c from User where id = : UserInfo.getUserId()].Network_User_Id__c;
     
        EhubAttachmentResponse_DTO_HUM ehubattachresponse = new EhubAttachmentResponse_DTO_HUM(); 
        EhubAttachmentResponse_DTO_HUM.AddDocumentResultDTO adddocResDTO = new EhubAttachmentResponse_DTO_HUM.AddDocumentResultDTO();
        EhubAttachmentResponse_DTO_HUM.AddDocument addDoc = new EhubAttachmentResponse_DTO_HUM.AddDocument();
        EhubAttachmentResponse_DTO_HUM.AddDocumentResponse addDocRes = new EhubAttachmentResponse_DTO_HUM.AddDocumentResponse();
        ehubattachresponse.AddDocument = addDoc;
        
        adddocResDTO.DocId = '999999';
        adddocResDTO.Status = 'Success';
        adddocResDTO.Version = '1';
        addDocRes.AddDocumentResultDTO = adddocResDTO;
        addDoc.AddDocumentResponse = addDocRes;
       
            
        Test.startTest();
            HUMAttachmentTriggerHelper.sendRequestToEhub(attachmentObj.Id,attachmentObj.parentId , EncodingUtil.base64Encode(attachmentObj.body) , attachmentObj.Name,sUser);
           
        Test.StopTest();
      
    }
    
    /*
     * Method Name : onBeforeInsertEventsPositive 
     * Description : For testing On before Insert Trigger Events Positive use cases of Standard Attachment object attached to Custom Attachment object
     * Return type : void
     * Return type : NA
     */
    static testMethod void onBeforeInsertEventsPositive() 
    {
        Test.setMock(WebServiceMock.class, new HUMEhubImagingWebserviceMockTest());
       
        Case objCase = HUMTestDataHelperTest.getCase();
        insert objCase;
        system.assert(objCase.id != null);
        Task objTask = HUMTestDataHelperTest.getTaskRecord();
        objTask.WhatId = objCase.Id;
        insert objTask;
        system.assert(objTask.id != null);

        //Create a new Custom Attachments
        List<Attachment__c> lstCustomAttachments = new List<Attachment__c>();

        for(Integer i=0;i<=1;i ++) 
        {            
            Attachment__c customAttachment =HUMTestDataHelperTest.getCustomAttachment();
            customAttachment.Related_to_Case__c=objCase.id;
            customAttachment.Related_to_Task__c=objtask.id;
            lstCustomAttachments.add(customAttachment);
        }     

        insert lstcustomAttachments;   
        system.assert(lstcustomAttachments != null);     
        //Store the list of Attachments corresponding to different parentIDs
        List<Attachment> lstStandardAttachments = new List<Attachment>();

        Attachment standardAttachment = HUMTestDataHelperTest.getAttachmentRecord();
        standardAttachment.parentID=lstCustomAttachments[0].id;
        lstStandardAttachments.add(standardAttachment);

        for(Integer i=0;i<=200;i++) 
        {
            Attachment stdAttachment = HUMTestDataHelperTest.getAttachmentRecord();
            stdAttachment.parentID=objCase.id;
            lstStandardAttachments.add(stdAttachment);
        } 

        Test.startTest();
            insert lstStandardAttachments;
        Test.StopTest();

        //Query for Assertion 
        List<Attachment> lstAttachments = [Select id from Attachment where ParentID =:lstCustomAttachments[0].id or ParentID=:objCase.id ] ;
        //Successful creation of Attachment
        System.assertEquals(lstAttachments.size(),202);
    }

    /*
     * Method Name : onBeforeInsertEventsNegative 
     * Description : For testing On before Insert Trigger Events Negative use cases of Standard Attachment object attached to Custom Attachment object
     * Return type : void
     * Return type : NA
     */
    static testMethod void onBeforeInsertEventsNegative() 
    {
        Test.setMock(WebServiceMock.class, new HUMEhubImagingWebserviceMockTest());
        
        Case objCase = HUMTestDataHelperTest.getCase();
        insert objCase;
        system.assert(objCase.id != null);  
        Task objTask = HUMTestDataHelperTest.getTaskRecord();
        objTask.WhatId = objCase.Id;
        insert objTask;
        system.assert(objTask.id != null);  
        //Create a new Custom Attachments
        List<Attachment__c> lstCustomAttachments = new List<Attachment__c>();

        for(Integer i=0;i<=1;i ++) 
        {            
            Attachment__c customAttachment =HUMTestDataHelperTest.getCustomAttachment();
            customAttachment.Related_to_Case__c=objCase.id;
            customAttachment.Related_to_Task__c=objtask.id;
            lstCustomAttachments.add(customAttachment);
        }                      
        insert lstCustomAttachments;
        system.assert(lstCustomAttachments != null); 
        //Store the list of Attachments corresponding to different parentIDs
        List<Attachment> lstStandardAttachments = new List<Attachment>();

        Attachment standardAttachment = HUMTestDataHelperTest.getAttachmentRecord();
        standardAttachment.parentID=lstCustomAttachments[0].id;
        lstStandardAttachments.add(standardAttachment);

        for(Integer i=0;i<=200;i++) 
        {
            Attachment stdAttachment = HUMTestDataHelperTest.getAttachmentRecord();
            stdAttachment.parentID=lstCustomAttachments[1].id;
            lstStandardAttachments.add(stdAttachment);
        } 

        Test.startTest();
            Database.insert(lstStandardAttachments,false);
        Test.StopTest();

        //Query for Assertion 
        List<Attachment> lstAttachments = [Select id from Attachment where ParentID =:lstCustomAttachments[0].id or ParentID=:lstCustomAttachments[1].id ] ;
        //Only one record is created
        System.assertEquals(lstAttachments.size(),1);
    }

    /*
     * Method Name : onBeforeDeleteEventsNegative 
     * Description : For testing On before Delete Trigger Events negative use cases of Standard attachment object attached to custom Attachment object
     * Return type : void
     * Return type : NA
     */
    static testMethod void onBeforeDeleteEventsNegative() 
    {
        Test.setMock(WebServiceMock.class, new HUMEhubImagingWebserviceMockTest());
        
        Case objCase = HUMTestDataHelperTest.getCase();
        insert objCase;
        system.assert(objCase.id != null); 

        Task objTask = HUMTestDataHelperTest.getTaskRecord();
        objTask.WhatId = objCase.Id;
        insert objTask;
        system.assert(objTask.id != null); 

        //Create a new Custom Attachments
        List<Attachment__c> lstCustomAttachments = new List<Attachment__c>();

        for(Integer i=0;i<=1;i ++) 
        {            
            Attachment__c customAttachment =HUMTestDataHelperTest.getCustomAttachment();
            customAttachment.Related_to_Case__c=objCase.id;
            customAttachment.Related_to_Task__c=objtask.id;
            lstCustomAttachments.add(customAttachment);
        }                      
        insert lstCustomAttachments;
        system.assert(lstCustomAttachments != null); 
        //Store the list of Attachments corresponding to different parentIDs
        List<Attachment> lstStandardAttachments = new List<Attachment>();

        Attachment standardAttachment = HUMTestDataHelperTest.getAttachmentRecord();
        standardAttachment.parentID=lstCustomAttachments[0].id;
        lstStandardAttachments.add(standardAttachment);

        for(Integer i=0;i<=200;i++) 
        {
            Attachment stdAttachment = HUMTestDataHelperTest.getAttachmentRecord();
            stdAttachment.parentID=objCase.id;
            lstStandardAttachments.add(stdAttachment);
        } 
        insert lstStandardAttachments;

        Test.startTest();
        Database.Delete(lstStandardAttachments,false);
        Test.StopTest();

        //Query for Assertion 
        List<Attachment> lstAttachments = [Select id from Attachment where ParentID =:lstCustomAttachments[0].id or ParentID=:objCase.id ] ;
        //One Failed deletion
        System.assertEquals(lstAttachments.size(),1);

    }

    /*
     * Method Name : onBeforeDeleteEventsPositive
     * Description : For testing On before Delete Trigger Events Positive use cases of Standard attachment object attached to custom Attachment object
     * Return type : void
     * Return type : NA
     */
    static testMethod void onBeforeDeleteEventsPositive() 
    {
        Test.setMock(WebServiceMock.class, new HUMEhubImagingWebserviceMockTest());
        
        Case objCase = HUMTestDataHelperTest.getCase();
        insert objCase;
        system.assert(objCase.id != null); 

        //Store the list of Attachments corresponding to different parentIDs
        List<Attachment> lstStandardAttachments = new List<Attachment>();

        for(Integer i=0;i<=200;i++) 
        {
            Attachment stdAttachment = HUMTestDataHelperTest.getAttachmentRecord();
            stdAttachment.parentID=objCase.id;
            lstStandardAttachments.add(stdAttachment);
        } 

        insert lstStandardAttachments;
        system.assert(lstStandardAttachments != null); 
        Test.startTest();
            Database.delete(lstStandardAttachments,false);
        Test.StopTest();

        //Query for Assertion 
        List<Attachment> lstAttachments = [Select id from Attachment where ParentID =:objCase.id ] ;
        //Successful Deletion of Attachment 
        System.assertEquals(lstAttachments.size(),0);
    }


    /*
     * Method Name : onBeforeUpdateEventsNegative 
     * Description : For testing On before Update Trigger Events negative use cases of Standard attachment object attached to custom Attachment object
     * Return type : void
     * Paramater   : NA
     */
    static testMethod void onBeforeUpdateEventsPositive() 
    {
        Test.setMock(WebServiceMock.class, new HUMEhubImagingWebserviceMockTest());
        
        Case objCase = HUMTestDataHelperTest.getCase();
        insert objCase;
        system.assert(objCase.id != null); 

        Task objTask = HUMTestDataHelperTest.getTaskRecord();
        objTask.WhatId = objCase.Id;
        insert objTask;
        system.assert(objTask.id != null); 

        //Create a new Custom Attachments
        List<Attachment__c> lstCustomAttachments = new List<Attachment__c>();

        for(Integer i=0;i<=1;i ++) 
        {            
            Attachment__c customAttachment =HUMTestDataHelperTest.getCustomAttachment();
            customAttachment.Related_to_Case__c=objCase.id;
            customAttachment.Related_to_Task__c=objtask.id;
            lstCustomAttachments.add(customAttachment);
        }     
        insert lstCustomAttachments;    
        system.assert(lstCustomAttachments != null);   

        //Store the list of Attachments corresponding to different parentIDs
        List<Attachment> lstStandardAttachments = new List<Attachment>();

        for(Integer i=0;i<=200;i++) 
        {
            Attachment stdAttachment = HUMTestDataHelperTest.getAttachmentRecord();
            stdAttachment.parentID=objCase.id;
            stdAttachment.Name= 'Update' + String.valueOf(Math.random());
            lstStandardAttachments.add(stdAttachment);
        } 

        insert lstStandardAttachments;

        Test.startTest();

        Database.update(lstStandardAttachments,false);
        Test.StopTest();

        //Query for Assertion 
        List<Attachment> lstAttachments = [Select id from Attachment where Name like 'Update%' ] ;
        //Successful Update of Attachment
        System.assertEquals(lstAttachments.size(),201);
    }
    
    /*
     * Method Name : onBeforeUpdateEventsNegative
     * Description : For testing On before Update Trigger Events Negative use cases of Standard attachment object attached to custom Attachment object
     * Return type : void
     * Return type : NA
     */
    static testMethod void onBeforeUpdateEventsNegative() 
    {
        Test.setMock(WebServiceMock.class, new HUMEhubImagingWebserviceMockTest());
        
        Case objCase = HUMTestDataHelperTest.getCase();
        insert objCase;
        system.assert(objCase.id != null); 

        Task objTask = HUMTestDataHelperTest.getTaskRecord();
        objTask.WhatId = objCase.Id;
        insert objTask;
        system.assert(objTask.id != null); 

        //Create a new Custom Attachments
        List<Attachment__c> lstCustomAttachments = new List<Attachment__c>();

        for(Integer i=0;i<=1;i ++) 
        {            
            Attachment__c customAttachment = HUMTestDataHelperTest.getCustomAttachment();
            customAttachment.Related_to_Case__c=objCase.id;
            customAttachment.Related_to_Task__c=objtask.id;
            lstCustomAttachments.add(customAttachment);
        }                      
        insert lstCustomAttachments;
        system.assert(lstCustomAttachments != null);

        //Store the list of Attachments corresponding to different parentIDs
        List<Attachment> lstStandardAttachments = new List<Attachment>();

        Attachment standardAttachment = HUMTestDataHelperTest.getAttachmentRecord();
        standardAttachment.parentID=lstCustomAttachments[0].id;
        lstStandardAttachments.add(standardAttachment);

        for(Integer i=0;i<=200;i++) 
        {
            Attachment stdAttachment = HUMTestDataHelperTest.getAttachmentRecord();
            stdAttachment.parentID=objCase.id;
            lstStandardAttachments.add(stdAttachment);
        } 

        insert lstStandardAttachments;

        //Update the records
        for(Attachment attachment : lstStandardAttachments) 
        {               
            attachment.Name ='Update' + String.valueOf(Math.random());
        }

        Test.startTest();
        Database.update(lstStandardAttachments,false);
        Test.StopTest();

        //Query for Assertion 
        List<Attachment> lstAttachments = [Select id from Attachment where ParentID =:objCase.id and Name like 'Update%' ] ;
        //Successful update of Attachment - One record failed
        System.assertEquals(lstAttachments.size(),201);
    }
}
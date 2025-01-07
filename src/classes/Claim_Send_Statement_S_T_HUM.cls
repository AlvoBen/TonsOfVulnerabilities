/**************************************************************************** 
Apex Class Name  : Claim_Send_Statement_S_T_HUM
Version          : 1.0  
Created Date     : September 21 2018
Function         : Test class for Claim_Send_Statement_S_HUM
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Vijaya Koriprolu                                 09/24/2018          origina Version
***************************************************************************************************/
@isTest
private Class Claim_Send_Statement_S_T_HUM
{
     /*
     * Method Name : prepareTestData
     * Description : Used to create wrapper class data
     * Return type : void
     * Parameter   : NA
     */
    @testSetup static void prepareTestData() 
    {
        User oCCSUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oCCSUser;
        system.assertEquals(oCCSUser.LastName,'test User1');
        
        System.runAs (oCCSUser) 
        {
            HUM_Webservice_Callout__c oHUMTaskHierarchyService = new HUM_Webservice_Callout__c();
            oHUMTaskHierarchyService.name='Claim_Send';
            oHUMTaskHierarchyService.Active__c=true;
            oHUMTaskHierarchyService.Certificate_Name__c='testts';
            oHUMTaskHierarchyService.End_Point_URL__c='https://testst-cmservicews.hum.com';
            oHUMTaskHierarchyService.Timeout__c='120000';
            oHUMTaskHierarchyService.Content_Type__c='application/json';
            oHUMTaskHierarchyService.Request_Type__c='POST';
            insert oHUMTaskHierarchyService;
            system.assert(oHUMTaskHierarchyService.id!=null);
            system.assert(oHUMTaskHierarchyService.name == 'Claim_Send');
            Account objAcc = HUMTestDataHelperTest.getAccount(); 
            objAcc.FirstName = 'Test';
            objAcc.LastName = 'Account';
            objAcc.Tenant_Id__c = '00';
            objAcc.Enterprise_ID__c = '1005577525';
            objAcc.General_Account__c = true;
            objAcc.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get(System.label.HUMCOBMemberLabel).getRecordTypeId();
            insert objAcc;
            System.assertEquals(objAcc.Tenant_Id__c,'00');            
                        
            Product2 oProd = new Product2(Name = 'Laptop X200',Segment_Type__c='MCR',ProductCode='MED', Major_LOB__c='MER');
            insert oProd;
            System.assertNotEquals(oProd.Id, NULL, 'Value not matched');        
            
            Policy__c objpol = HUMTestDataHelperTest.getPolicy();
            objpol.Name='testPolicy';
            objpol.Group_Name__c = objAcc.Id;
            objpol.Major_LOB__c = 'MER';
            objpol.Source_Cust_Cov_Key__c = '123456789';
            objpol.Product__c = oProd.Id;
            objpol.Product__r = oProd;
            objpol.Platform__c = 'LV';
            objpol.Client_Number__c = '57';
            insert objpol;
            system.assertEquals(objpol.Source_Cust_Cov_Key__c,'123456789');
            
            Policy_Member__c objPolMem = new Policy_Member__c();
            objPolMem.Name = 'testpolmem11';
            objPolMem.Relationship__c = 'Self';
            objPolMem.Subscriber__c = objPolMem.Id;
            objPolMem.Effective_Date__c=Date.today().addDays(-1).format();
            objPolMem.End_Date__c=Date.today().addDays(1).format();
            objPolMem.Policy_Member_External_ID__c='00|ODS|6925004434817|12345678|34567|2013-07-01';
            objPolMem.Member__c = objAcc.Id;
            objPolMem.Policy__c = objpol.Id;
            objPolMem.Policy__r = objpol;
            objPolMem.Relationship_Code__c = '00';
            insert objPolMem;
            system.assertEquals(objPolMem.Relationship__c, 'Self');
            
            PageReference pageRef = Page.Claim_Send_Statement_VF_HUM;
            Test.setCurrentPage(pageRef);
            ApexPages.currentPage().getParameters().put('pid',objPolMem.Id);
         }
       
}


/*
*  This method is used to run positive scenarios data
*  @name   runPositiveMedicalData
*  @param  NA
*  @return NA
*  @throws NA
*/  
    static testMethod void runPositiveData()
    {    
        User oCCSUser = [Select id,UserName from User where LastName = 'test User1' LIMIT 1 ] ;
        system.assertEquals(oCCSUser.UserName,'user1user1@humcrmstest.com');
        
        System.runAs (oCCSUser) 
        { 
            HUM_Webservice_Callout__c oServiceCallout = [select Name, Certificate_Name__c, End_Point_URL__c, Timeout__c, Content_Type__c, Request_Type__c from HUM_Webservice_Callout__c where name='Claim_Send' limit 1];   
            System.assertEquals(oServiceCallout.Certificate_Name__c,'testts');
            
            String sAddressLine1='8900 shelbyville road';
            String sAddressLine2='apt 5';
             String sStateCode='louisville"';
             String sCity ='ky'; 
             String sZipCode ='41105';
            
            Test.StartTest();
            Claim_Send_Statement_S_HUM oCaseDetail = new Claim_Send_Statement_S_HUM();
            oCaseDetail.buildRequest(sAddressLine1,sAddressLine2,sStateCode,sCity,sZipCode);
            HttpRequest oRequest = new HttpRequest();
            oCaseDetail.setupRequest(oRequest,oServiceCallout,sAddressLine1,sAddressLine2,sStateCode,sCity,sZipCode);
            Claim_Send_Statement_Response_H_HUM objRestResp = new Claim_Send_Statement_Response_H_HUM();
            Claim_Send_Statement_Response_H_HUM.StandardizeAddressResponse oStandaredAddResults  = objRestResp.StandardizeAddressResponse;
          
            
            Test.stopTest();  
        }
    }  
    
    
      /*
*  This method is used to run positive scenarios data
*  @name   runTaskHierResponse
*  @param  NA
*  @return NA
*  @throws NA
*/  
    static testMethod void runTaskHierResponse()
    {    
        User oCCSUser = [Select id,UserName from User where LastName = 'test User1' LIMIT 1 ] ;
        system.assertEquals(oCCSUser.UserName,'user1user1@humcrmstest.com');
        
        System.runAs (oCCSUser) 
        { 
            HUM_Webservice_Callout__c oServiceCallout = [select Name, Certificate_Name__c, End_Point_URL__c, Timeout__c, Content_Type__c, Request_Type__c from HUM_Webservice_Callout__c where name='Claim_Send' limit 1];   
            System.assertEquals(oServiceCallout.Certificate_Name__c,'testts');
            
           Claim_Send_Statement_S_HUM oCaseDetail1 = new Claim_Send_Statement_S_HUM();
            HttpResponse oResponse = New HttpResponse();
            oResponse.setHeader('Content-Type', 'application/json');
            oResponse.setHeader('SOAPAction', '');    
            oResponse.setHeader('Connection','keep-alive');
            Test.StartTest();
            String sJson = '{ "StandardizeAddressRequest": { "AddressLine1": "8900 shelbyville road", "AddressLine2": "apt 5", "City": "louisville", "StateCode": "ky", "ZipCode": "41105" } } ';
            oResponse.setBody(sJson );
            oResponse.setStatusCode(200); 
            oCaseDetail1.processResponse(sJson );
            Test.stopTest();  
        }
    }   
     
    
    
 
         
      }
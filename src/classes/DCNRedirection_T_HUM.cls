/**************************************************************************** 
Apex Class Name  : DCNRedirection_T_HUM
Version          : 1.0  
Created Date     : 07/26/2017
Function         : DCNRedirection_C_HUM
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Chaitanya Vegendla                           07/26/2017                Original Version
* Vandana Chaudhari                            05/07/2018                REQ - 362020 
* Vandana Chaudhari                            05/15/2018                REQ - 362020 - Test Class Coverage
* Santhi Mandava                               03/12/2019                Replaced "Endpoint_Url__c" with Endpoint_Url_HUM__c.
* Vamshi Krishna Pemberthi						  06/08/2021 		Code Coverage Improvements
***************************************************************************************************/
@isTest
private Class DCNRedirection_T_HUM
{

   /**
    * <p>
    * Test Setup Method 
    * @ Param     ApexPages.StandardController Standardcontroller reference
    * @ Return     NA
    */
    @testSetup 
    static void testSetup()
    {        
        User usr = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert usr;
        
        HUMDCNApplicationUrl__c dcnLink= new HUMDCNApplicationUrl__c (DCNImageURL__c= 'https://dcnlink.com' );
        insert dcnLink;
        
        Endpoint_Url_HUM__c ssoLink= new Endpoint_Url_HUM__c(Name='SSO_LOGINURL' ,Remote_Url__c= 'https://ssolink.com');
        insert ssoLink;
        
        String orgId = UserInfo.getOrganizationId();
        String strEndpointURL = '';
                //TESTFULL
                If(orgId == '00D4C0000000TunUAE')
                {
                    strEndpointURL = 'http://test-associates.humana.com/SPIDAR/SpidarViewer/BasicViewer?ViewFlag=True&DCN=';
                }
                //QA
                Else If(orgId == '00Dc0000003uIioEAE')
                {
                    strEndpointURL = 'https://QA-associates.humana.com/SPIDAR/SpidarViewer/BasicViewer?ViewFlag=True&DCN=';

                }
                //QAS
                Else If(orgId == '00DL0000005rQrAMAU')
                {
                    strEndpointURL = 'https://QA-associates.humana.com/SPIDAR/SpidarViewer/BasicViewer?ViewFlag=True&DCN=';
                }
                //PROD
                Else If(orgId == '00DF00000005DjnMAE')
                {
                    strEndpointURL = 'https://associates.humana.com/SPIDAR/SpidarViewer/BasicViewer?ViewFlag=True&DCN=';
                }
                else
                {
                    strEndpointURL = 'http://test-associates.humana.com/SPIDAR/SpidarViewer/BasicViewer?ViewFlag=True&DCN=';
                }
                
        Endpoint_Url_HUM__c DCNSpider2Link= new Endpoint_Url_HUM__c(Name='DCNSpider2URL' ,Remote_Url__c= strEndpointURL);
        insert DCNSpider2Link;
        
        RecordType AccountRecordType = [Select Id from RecordType where Name='Member' and sObjectType='Account'];
        
        Account accountRec = HUMTestDataHelperTest.getAccount();
        accountRec.OwnerId = usr.id;
        accountRec.FirstName = 'Test';
        accountRec.LastName = 'Account';
        accountRec.Enterprise_Id__c = '123456';
        accountRec.recordtypeId = AccountRecordType.id;
        insert accountRec;
        
        Case caseRecord = new Case();
        caseRecord.Owner_Queue__c = 'Louisville RSO Calls Support';
        caseRecord.Status = 'In Progress';
        caseRecord.AccountId = accountRec.id;
        caseRecord.ownerId = usr.Id;
        caseRecord.Interacting_With__c = accountRec.id;
        caseRecord.Interacting_With_Type__c = 'Member';
        caseRecord.Interacting_About_Type__c = 'Member';
        caseRecord.Policy_Member__c = null;
        caseRecord.dcn__c= '23432';
        insert caseRecord;
        
        Case caseRecord1 = new Case();
        caseRecord1.Owner_Queue__c = 'Louisville RSO Calls Support';
        caseRecord1.Status = 'In Progress';
        caseRecord1.AccountId = accountRec.id;
        caseRecord1.ownerId = usr.Id;
        caseRecord1.Interacting_With__c = accountRec.id;
        caseRecord1.Interacting_With_Type__c = 'Member';
        caseRecord1.Interacting_About_Type__c = 'Member';
        caseRecord1.Policy_Member__c = null;
        caseRecord1.dcn__c= 'AC23432';
        insert caseRecord1;
        
        Case caseRecord2 = new Case();
        caseRecord2.Owner_Queue__c = 'Louisville RSO Calls Support';
        caseRecord2.Status = 'In Progress';
        caseRecord2.AccountId = accountRec.id;
        caseRecord2.ownerId = usr.Id;
        caseRecord2.Interacting_With__c = accountRec.id;
        caseRecord2.Interacting_With_Type__c = 'Member';
        caseRecord2.Interacting_About_Type__c = 'Member';
        caseRecord2.Policy_Member__c = null;
        caseRecord2.dcn__c= 'DC23432';
        caseRecord2.dcn_type__c= 'claim';
        insert caseRecord2;
    }
    
    /**
    * <p>
    * Test Method to test sso link
    * @ Param      NA
    * @ Return     NA
    */
    static testMethod void testAccumsServiceRequest()
    {            
        User oUser = [select Id,Name from User where name ='test User1' limit 1];                
        system.runAs(oUser)
        {   
            Test.StartTest();
            case caseRecord = [Select id,DCN__c from case limit 1];
            AccumsServiceRequest_H_HUM oRequest = new AccumsServiceRequest_H_HUM();
            ApexPages.StandardController scon = new ApexPages.StandardController(caseRecord);
            DCNRedirection_C_HUM dcnController = new DCNRedirection_C_HUM(scon);
            
            PageReference pg = dcnController.redirectTODCN();           
            system.assert(pg!=null);
   
            Test.StopTest();
        }
    }
    
    /**
    * <p>
    * Test Method to test DCN link
    * @ Param      NA
    * @ Return     NA
    */
    static testMethod void spider2DCNURLTest()
    {            
        User oUser = [select Id,Name from User where name ='test User1' limit 1];                
        system.runAs(oUser)
        {   
            case caseRecord = [SELECT Id,DCN__c FROM Case WHERE dcn__c= 'AC23432'];   
            Test.StartTest();
                     
            ApexPages.StandardController scon = new ApexPages.StandardController(caseRecord);
            ApexPages.currentPage().getParameters().put('dcnNumber', 'AC23432'); 
            DCNRedirection_C_HUM dcnController = new DCNRedirection_C_HUM(scon);            
            PageReference pg = dcnController.redirectTODCN();
            
            String orgId = UserInfo.getOrganizationId();
            String strDCNURL = '';
                //TESTFULL
                If(orgId == '00D4C0000000TunUAE')
                {
                    strDCNURL = 'http://test-associates.humana.com/SPIDAR/SpidarViewer/BasicViewer?ViewFlag=True&DCN=';
                }
                //QA
                Else If(orgId == '00Dc0000003uIioEAE')
                {
                    strDCNURL = 'https://QA-associates.humana.com/SPIDAR/SpidarViewer/BasicViewer?ViewFlag=True&DCN=';

                }
                //QAS
                Else If(orgId == '00DL0000005rQrAMAU')
                {
                    strDCNURL = 'https://QA-associates.humana.com/SPIDAR/SpidarViewer/BasicViewer?ViewFlag=True&DCN=';
                }
                //PROD
                Else If(orgId == '00DF00000005DjnMAE')
                {
                    strDCNURL = 'https://associates.humana.com/SPIDAR/SpidarViewer/BasicViewer?ViewFlag=True&DCN=';
                }
                else
                {
                    strDCNURL = 'http://test-associates.humana.com/SPIDAR/SpidarViewer/BasicViewer?ViewFlag=True&DCN=';
                }           
            System.assertEquals(dcnController.dcnURL+caseRecord.dcn__c,strDCNURL +'AC23432');   
            Test.StopTest(); 
        }
    }
    /**
    * <p>
    * Test Method to test DCN link
    * Added to cover redirectToDCN method for Claim
    * @ Param      NA
    * @ Return     NA
    */
    Static testMethod void spider2DCNURLTest2()
    {            
        User oUser = [select Id, Name from User where name ='test User1' limit 1];                
        system.runAs(oUser)
        {   
            Case caseRecord = [SELECT Id,DCN__c, dcn_type__c,dcn_Url__c FROM Case WHERE dcn__c= 'DC23432'];   
            Test.StartTest();
                     
                       
            String orgId = UserInfo.getOrganizationId();
            String strDCNURL = '';
                // TESTFULL
                If (orgId == '00D4C0000000TunUAE')
                {
                    strDCNURL = 'http://test-associates.humana.com/SPIDAR/SpidarViewer/BasicViewer?ViewFlag=True&DCN=';
                }
                //QA
                Else If (orgId == '00Dc0000003uIioEAE')
                {
                    strDCNURL = 'https://QA-associates.humana.com/SPIDAR/SpidarViewer/BasicViewer?ViewFlag=True&DCN=';

                }
                //QAS
                Else If (orgId == '00DL0000005rQrAMAU')
                {
                    strDCNURL = 'https://QA-associates.humana.com/SPIDAR/SpidarViewer/BasicViewer?ViewFlag=True&DCN=';
                }
                //PROD
                else if (orgId == '00DF00000005DjnMAE')
                {
                    strDCNURL = 'https://associates.humana.com/SPIDAR/SpidarViewer/BasicViewer?ViewFlag=True&DCN=';
                }
                else
                {
                    strDCNURL = 'http://test-associates.humana.com/SPIDAR/SpidarViewer/BasicViewer?ViewFlag=True&DCN=';
                }   
            caseRecord.dcn_Url__c = strDCNURL;
            update caseRecord;
            
            ApexPages.StandardController scon = New ApexPages.StandardController(caseRecord);
            ApexPages.currentPage().getParameters().put('dcnNumber', 'DC23432'); 
            DCNRedirection_C_HUM dcnController = New DCNRedirection_C_HUM(scon);            
            PageReference pg = dcnController.redirectTODCN();
                    
            //System.assertEquals(dcnController.dcnURL+caseRecord.dcn__c,strDCNURL +'DC23432');   
            Test.StopTest(); 
        }
    }
    
   }
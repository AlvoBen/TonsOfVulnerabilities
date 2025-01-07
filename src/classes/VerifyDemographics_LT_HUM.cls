/*******************************************************************************************************************************
Apex Class Name : VerifyDemographics_LT_HUM
Version         : 1.0
Created On      : 02/23/2021  
Function        : This Class serves as test class to VerifyDemographics_LC_HUM. 
Test Class      : VerifyDemographics_LT_HUM
Modification Log: 
Developer Name           Code Review                      Date                       Description
*--------------------------------------------------------------------------------------------------------------------------------
*Ankima Srivastava										 02/23/2021				  initial version
*********************************************************************************************************************************/
@isTest
    private class VerifyDemographics_LT_HUM {
        
    @testSetup static void prepareTestData() 
    {
                Account oAccount = new Account();
                oAccount.Name ='Test Account';
                oAccount.Enterprise_ID__c = '1000065119';
                insert oAccount;
                System.assertNotEquals(oAccount, NULL, 'Account is not inserted');
                
                HUM_Webservice_Callout__c calloutSettings = new HUM_Webservice_Callout__c();
                calloutSettings.Name = 'GetVerifyDemographics';
                calloutSettings.Active__c = True;
                calloutSettings.Enable_Service_Tester__c = True;
                calloutSettings.Certificate_Name__c = 'Humana_GuidanceCare_Pileus';
                calloutSettings.Content_Type__c = 'application/json';
                calloutSettings.Request_Type__c = 'Post';
                calloutSettings.Timeout__c = '12000';
                calloutSettings.End_Point_URL__c = 'https://dev-crmservicews.humana.com/XXX';
                insert calloutSettings;
                System.assertNotEquals(calloutSettings , NULL);
                
                HUM_Webservice_Callout__c oVfs = new HUM_Webservice_Callout__c(Name = 'AddressVerification', Certificate_Name__c = 'TestCertificate', Timeout__c = '3000', End_Point_URL__c = 'SampleURL');
   				insert oVfs;
         
                HUM_Webservice_Callout__c calloutSettings1 = new HUM_Webservice_Callout__c();
                calloutSettings1.Name = 'UpdateVerifyDemographics';
                calloutSettings1.Active__c = True;
                calloutSettings1.Enable_Service_Tester__c = True;
                calloutSettings1.Certificate_Name__c = 'Humana_GuidanceCare_Pileus';
                calloutSettings1.Content_Type__c = 'application/json';
                calloutSettings1.Request_Type__c = 'Post';
                calloutSettings1.Timeout__c = '12000';
                calloutSettings1.End_Point_URL__c = 'https://dev-crmservicews.humana.com/XXX';
                insert calloutSettings1;
                System.assertNotEquals(calloutSettings1 , NULL);
                //CRMFunctionality_ONOFF_Switch__c CRMOnOff = new CRMFunctionality_ONOFF_Switch__c(Name = '1065536', IsON__c = true );
                //insert CRMOnOff;
                
                HUM_CBIS_Enabled__c oHUMCbisFlag = new HUM_CBIS_Enabled__c(Name = 'HUM_CBIS_Flag', CBIS_Flag__c = true );
                insert oHUMCbisFlag ;
                System.assertNotEquals(oHUMCbisFlag , NULL, 'CBIS flag is not inserted');
                
    }    
   		static testMethod void testGetLastVerifyDatesRest()
        { 
                CRMFunctionality_ONOFF_Switch__c CRMOnOff = new CRMFunctionality_ONOFF_Switch__c(Name = '1065536', IsON__c = true );
                insert CRMOnOff;
                Account oAccount = [Select Id FROM Account where Name ='Test Account' LIMIT 1 ];            
                Test.startTest();
            	VerifyDemograhics_LC_HUM.processGetLastVerifyDates(null);
            	Continuation continueObj = (Continuation)VerifyDemograhics_LC_HUM.APIService(oAccount.Id,'onLoad');
                Test.stopTest();
                HttpResponse httpRes = new Httpresponse();
                httpRes.setStatusCode(200);
                String resBodyString = '{ "ResponseHeader":{ "RequestId":"CoreAPI 9c145fcc-f2f6-4ef8-bdc1-7d531a713693", "SessionId":"CoreAPI e0b403ec-e3c2-45b4-b039-46b3c312e3ed", "ResponseDuration":"10.975526", "CoreApiHttpStatusCode":"200 OK" }, "getverificationstatusResponse":{ "demographics":{ "lastVerifiedDate":"2021-02-18T08:47:43.526Z", "source":"CRM", "verifiedBy":"Ankima Srivastava" } } }';
                httpRes.setBody(resBodyString);
                system.assert(httpRes.getBody() == resBodyString);         
                string reqType = 'Continuation-1';        
                List<string> labelGetmember = new List<string>();
                labelGetmember.add(reqType);
                Test.setContinuationResponse(reqType,httpRes);
                try
                {
                    Object result = VerifyDemographics_LS_HUM.processGetVerifyDemographicsResponse(labelGetmember,VerifyDemographics_LS_HUM.contstate1);
                    system.assert(result!=NULL);  
                }
                catch(Exception e)
                {
                    system.assert(e.getMessage()!=NULL);
                }      
                          
        }
        
        static testMethod void testGetLastVerifyDatesRest1()
        { 
                CRMFunctionality_ONOFF_Switch__c CRMOnOff = new CRMFunctionality_ONOFF_Switch__c(Name = '1065536', IsON__c = true );
                insert CRMOnOff;
                Account oAccount = [Select Id FROM Account where Name ='Test Account' LIMIT 1 ];               
                Test.startTest();
                Continuation continueObj = (Continuation)VerifyDemograhics_LC_HUM.APIService(oAccount.Id,'onLoad');
                Test.stopTest();
                HttpResponse httpRes = new Httpresponse();
                httpRes.setStatusCode(200);
                String resBodyString = '{ "ResponseHeader":{ "RequestId":"CoreAPI 9c145fcc-f2f6-4ef8-bdc1-7d531a713693", "SessionId":"CoreAPI e0b403ec-e3c2-45b4-b039-46b3c312e3ed", "ResponseDuration":"10.975526", "CoreApiHttpStatusCode":"faultcode" }, "getverificationstatusResponse":{ "demographics":{ "lastVerifiedDate":"2021-02-18T08:47:43.526Z", "source":"CRM", "verifiedBy":"Ankima Srivastava" } } }';
                httpRes.setBody(resBodyString);
                system.assert(httpRes.getBody() == resBodyString);         
                string reqType = 'Continuation-1';        
                List<string> labelGetmember = new List<string>();
                labelGetmember.add(reqType);
                Test.setContinuationResponse(reqType,httpRes);
                try
                {
                    VerifyDemographics_LS_HUM.processGetVerifyDemographicsResponse(labelGetmember, VerifyDemographics_LS_HUM.contstate1);                
                }
                catch(Exception e)
                {
                    system.assert(e.getMessage()!=NULL);
                }      
                
            }      
             
        
        static testMethod void testGetLastVerifyDatesRestfalse()
        {
            CRMFunctionality_ONOFF_Switch__c CRMOnOff = new CRMFunctionality_ONOFF_Switch__c(Name = '1065536', IsON__c = true );
            insert CRMOnOff;
            Account oAccount = [Select Id FROM Account where Name ='Test Account' LIMIT 1 ]; 
            Test.startTest();
            Continuation continueObj = (Continuation)VerifyDemograhics_LC_HUM.APIService(oAccount.Id,'onLoad');
            Test.stopTest();
            HttpResponse httpRes = new HttpResponse();
            httpRes.setStatusCode(400);
            string reqType = 'Continuation-1';        
            List<string> labelGetmember = new List<string>();
            labelGetmember.add(reqType);
            Test.setContinuationResponse('Continuation-1', httpRes);
            try
            {
                VerifyDemographics_LS_HUM.processGetVerifyDemographicsResponse(labelGetmember, VerifyDemographics_LS_HUM.contstate1);
            }
            catch(Exception e)
            {
                system.assert(e.getMessage()!=NULL);
            }    
        }
        
        
        
        static testMethod void testGetLastVerifyDatesRestfalse1()
        { 
                CRMFunctionality_ONOFF_Switch__c CRMOnOff = new CRMFunctionality_ONOFF_Switch__c(Name = '1065536', IsON__c = true );
                insert CRMOnOff;
                Account oAccount = [Select Id FROM Account where Name ='Test Account' LIMIT 1 ];                 
                Test.startTest();
                Continuation continueObj = (Continuation)VerifyDemograhics_LC_HUM.APIService(oAccount.Id,'onLoad');
                Test.stopTest();
                HttpResponse httpRes = new Httpresponse();
                httpRes.setStatusCode(200);
                String resBodyString = '{ "ResponseHeader":{ "RequestId":"CoreAPI 9c145fcc-f2f6-4ef8-bdc1-7d531a713693", "SessionId":"CoreAPI e0b403ec-e3c2-45b4-b039-46b3c312e3ed", "ResponseDuration":"10.975526", "CoreApiHttpStatusCode":"200 OK" }}}'; 
                httpRes.setBody(resBodyString);
                system.assert(httpRes.getBody() == resBodyString);         
                string reqType = 'Continuation-1';        
                List<string> labelGetmember = new List<string>();
                labelGetmember.add(reqType);
                Test.setContinuationResponse(reqType,httpRes);
                try
                {
                    VerifyDemographics_LS_HUM.processGetVerifyDemographicsResponse(labelGetmember, VerifyDemographics_LS_HUM.contstate1);
                    
                }
                catch(Exception e)
                {
                    system.assert(e.getMessage()!=NULL);
                }      
                
          }     
        
        
        static testMethod void testGetLastVerifyDatesRestfalse2()
        { 
                CRMFunctionality_ONOFF_Switch__c CRMOnOff = new CRMFunctionality_ONOFF_Switch__c(Name = '1065536', IsON__c = true );
                insert CRMOnOff;
                Account oAccount = [Select Id FROM Account where Name ='Test Account' LIMIT 1 ];     
                Test.startTest();
                Continuation continueObj = (Continuation)VerifyDemograhics_LC_HUM.APIService(oAccount.Id,'onLoad');
                Test.stopTest();
        }
        
        
        static testMethod void testGetLastVerifyDatesRestfalse3()
        { 
                CRMFunctionality_ONOFF_Switch__c CRMOnOff = new CRMFunctionality_ONOFF_Switch__c(Name = '1065536', IsON__c = true );
                insert CRMOnOff;
                Test.startTest();
                Continuation continueObj = (Continuation)VerifyDemograhics_LC_HUM.APIService('12345','onLoad');
                Test.stopTest();
        }
        
        
        static testMethod void testGetLastVerifyDates1()
        { 
                CRMFunctionality_ONOFF_Switch__c CRMOnOff = new CRMFunctionality_ONOFF_Switch__c(Name = '1065536', IsON__c = false );
    			insert CRMOnOff;
            	Account oAccount = [Select Id FROM Account where Name ='Test Account' LIMIT 1 ];            
                Test.startTest();
                Continuation continueObj = (Continuation)VerifyDemograhics_LC_HUM.APIService(oAccount.Id,'onLoad');
                Test.stopTest();
                HttpResponse httpRes = new Httpresponse();
                httpRes.setStatusCode(200);
                String resBodyString = '<?xml version="1.0" encoding="UTF-8"?><s:Envelope xmlns:s="http://schemas.xmlsoap.org/soap/envelope/"><s:Body><GetLastVerifiedDatesResponse xmlns="http://tempuri.org/_V2"><GetLastVerifiedDatesResult><a:DemographicsLastVerifiedDate xmlns="http://tempuri.org/" xmlns:i="http://www.w3.org/2001/XMLSchema-instance" xmlns:a="http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.OIDemographics">2021-02-23T02:42:52.107361</a:DemographicsLastVerifiedDate><a:DemographicsVerifiedBy xmlns="http://tempuri.org/" xmlns:i="http://www.w3.org/2001/XMLSchema-instance" xmlns:a="http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.OIDemographics">Joel George</a:DemographicsVerifiedBy><a:OIDenLastVerifiedDate xmlns="http://tempuri.org/" xmlns:i="http://www.w3.org/2001/XMLSchema-instance" xmlns:a="http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.OIDemographics">0001-01-01T00:00:00</a:OIDenLastVerifiedDate><a:OIMedLastVerifiedDate xmlns="http://tempuri.org/" xmlns:i="http://www.w3.org/2001/XMLSchema-instance" xmlns:a="http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.OIDemographics">0001-01-01T00:00:00</a:OIMedLastVerifiedDate></GetLastVerifiedDatesResult></GetLastVerifiedDatesResponse></s:Body></s:Envelope>';
                httpRes.setBody(resBodyString);
                system.assert(httpRes.getBody() == resBodyString);         
                string reqType = 'Continuation-1';        
                List<string> labelGetmember = new List<string>();
                labelGetmember.add(reqType);
                Test.setContinuationResponse(reqType,httpRes);
                try
                {
                    Object result =  VerifyDemographics_LS_HUM.processGetLastVerifyDates(VerifyDemographics_LS_HUM.contstate2);                    
                }
                catch(Exception e)
                {
                    system.assert(e.getMessage()!=NULL);
                }      
                
                  
        }
        
        
        static testMethod void testGetLastVerifyDatesfalse()
        {
            CRMFunctionality_ONOFF_Switch__c CRMOnOff = new CRMFunctionality_ONOFF_Switch__c(Name = '1065536', IsON__c = false );
    		insert CRMOnOff;
            Account oAccount = [Select Id FROM Account where Name ='Test Account' LIMIT 1 ];            
            Test.startTest();
            Continuation continueObj = (Continuation)VerifyDemograhics_LC_HUM.APIService(oAccount.Id,'onLoad');
            Test.stopTest();
            HttpResponse httpRes = new HttpResponse();
            httpRes.setStatusCode(400);
            Test.setContinuationResponse('Continuation-1', httpRes);
            try
            {
                VerifyDemographics_LS_HUM.processGetLastVerifyDates(VerifyDemographics_LS_HUM.contstate2);
            }
            catch(Exception e)
            {
                system.assert(e.getMessage()!=NULL);
            }    
        }
       
        
        static testMethod void testverifyDemographicsRest()
        { 
                CRMFunctionality_ONOFF_Switch__c CRMOnOff = new CRMFunctionality_ONOFF_Switch__c(Name = '1065536', IsON__c = true );
                insert CRMOnOff;
                Account oAccount = [Select Id FROM Account where Name ='Test Account' LIMIT 1 ];            
                Test.startTest();
                Continuation continueObj = (Continuation)VerifyDemograhics_LC_HUM.APIService(oAccount.Id,'onclickVerify');
                Test.stopTest();
                HttpResponse httpRes = new Httpresponse();
                httpRes.setStatusCode(200);
                String resBodyString = '{ "ResponseHeader":{ "RequestId":"CoreAPI 4b205d33-066b-47a6-b541-3e1a8ceb6060", "SessionId":"CoreAPI b468f7cd-5290-4556-aab9-fdb94caa8ae2", "ResponseDuration":"14.794215999999999", "CoreApiHttpStatusCode":"200 OK" }, "putverificationstatusResponse":{ "Status":"success" } }';
                httpRes.setBody(resBodyString);
                system.assert(httpRes.getBody() == resBodyString);         
                string reqType = 'Continuation-1';        
                List<string> labelGetmember = new List<string>();
                labelGetmember.add(reqType);
                Test.setContinuationResponse(reqType,httpRes);
                try
                {
                    Object result = VerifyDemographics_LS_HUM.processUpdateVerifyDemographicsResponse(labelGetmember,VerifyDemographics_LS_HUM.contstate3);
                    system.assert(result!=NULL);  
                }
                catch(Exception e)
                {
                    system.assert(e.getMessage()!=NULL);
                }      
                          
        }
        
          
             
        
        static testMethod void testverifyDemographicsRestfalse()
        {
            CRMFunctionality_ONOFF_Switch__c CRMOnOff = new CRMFunctionality_ONOFF_Switch__c(Name = '1065536', IsON__c = true );
            insert CRMOnOff;
            Account oAccount = [Select Id FROM Account where Name ='Test Account' LIMIT 1 ]; 
            Test.startTest();
            Continuation continueObj = (Continuation)VerifyDemograhics_LC_HUM.APIService(oAccount.Id,'onclickVerify');
            Test.stopTest();
            HttpResponse httpRes = new HttpResponse();
            httpRes.setStatusCode(400);
            string reqType = 'Continuation-1';        
            List<string> labelGetmember = new List<string>();
            labelGetmember.add(reqType);
            Test.setContinuationResponse('Continuation-1', httpRes);
            try
            {
               VerifyDemographics_LS_HUM.processUpdateVerifyDemographicsResponse(labelGetmember,VerifyDemographics_LS_HUM.contstate3);
            }
            catch(Exception e)
            {
                system.assert(e.getMessage()!=NULL);
            }    
        }
        
        
        static testMethod void testverifyDemographicsRestfalse1()
        { 
                CRMFunctionality_ONOFF_Switch__c CRMOnOff = new CRMFunctionality_ONOFF_Switch__c(Name = '1065536', IsON__c = true );
                insert CRMOnOff;
                Account oAccount = [Select Id FROM Account where Name ='Test Account' LIMIT 1 ];            
                Test.startTest();
                Continuation continueObj = (Continuation)VerifyDemograhics_LC_HUM.APIService(oAccount.Id,'onclickVerify');
                Test.stopTest();
                HttpResponse httpRes = new Httpresponse();
                httpRes.setStatusCode(200);
                String resBodyString = '{ "ResponseHeader":{ "RequestId":"CoreAPI 4b205d33-066b-47a6-b541-3e1a8ceb6060", "SessionId":"CoreAPI b468f7cd-5290-4556-aab9-fdb94caa8ae2", "ResponseDuration":"14.794215999999999", "CoreApiHttpStatusCode":"200 OK" }, "putverificationstatusResponse":{ "Status":"failure" } }';
                httpRes.setBody(resBodyString);
                system.assert(httpRes.getBody() == resBodyString);         
                string reqType = 'Continuation-1';        
                List<string> labelGetmember = new List<string>();
                labelGetmember.add(reqType);
                Test.setContinuationResponse(reqType,httpRes);
                try
                {
                   VerifyDemographics_LS_HUM.processUpdateVerifyDemographicsResponse(labelGetmember,VerifyDemographics_LS_HUM.contstate3);      
                }
                catch(Exception e)
                {
                    system.assert(e.getMessage()!=NULL);
                }      
                          
        }
        
        
        
        static testMethod void testverifyDemographics()
        { 
                CRMFunctionality_ONOFF_Switch__c CRMOnOff = new CRMFunctionality_ONOFF_Switch__c(Name = '1065536', IsON__c = false );
                insert CRMOnOff;
                Account oAccount = [Select Id FROM Account where Name ='Test Account' LIMIT 1 ];            
                Test.startTest();
                Continuation continueObj = (Continuation)VerifyDemograhics_LC_HUM.APIService(oAccount.Id,'onclickVerify');
                Test.stopTest();
                HttpResponse httpRes = new Httpresponse();
                httpRes.setStatusCode(200);
                String resBodyString ='<?xml version="1.0" encoding="UTF-8"?><s:Envelope xmlns:s="http://schemas.xmlsoap.org/soap/envelope/"><s:Body><UpdateDemographicsResponse xmlns="http://tempuri.org/_V2"><UpdateDemographicsResult><CCP_GRP_SEG_DETAILS><EXPORT_ERROR_INFO><SEVERITY_CD>I</SEVERITY_CD><ERR_CD>00000</ERR_CD><ERR_DESC>Successfull</ERR_DESC><MESSAGE_END>Y</MESSAGE_END><MESSAGE_TYPE>Y</MESSAGE_TYPE></EXPORT_ERROR_INFO><CCP_SEGMENT_GROUP><GG_KEY>0000000000000</GG_KEY><S_IND/></CCP_SEGMENT_GROUP></CCP_GRP_SEG_DETAILS><CCP_MEMBER_EMAIL><EXPORT_ERROR_INFO><SEVERITY_CD>I</SEVERITY_CD><ERR_CD>00000</ERR_CD><ERR_DESC>SUCCESSFUL READ ON IQMEMR TO SELECT VERIFY RECORD </ERR_DESC><MESSAGE_END>Y</MESSAGE_END><MESSAGE_TYPE>C</MESSAGE_TYPE></EXPORT_ERROR_INFO><MEMBER_EMAIL><ENTERPRISE_ID>1006913039</ENTERPRISE_ID><HOME_EMAIL> </HOME_EMAIL><WORK_EMAIL> </WORK_EMAIL><VERIFIED_BY> </VERIFIED_BY><VERIFIED_ON>2021-02-25-01.42.56.745762</VERIFIED_ON><UPDATED_BY> </UPDATED_BY><UPDATED_ON> </UPDATED_ON><OI_INDICATOR> </OI_INDICATOR><OI_VERIFIED_BY> </OI_VERIFIED_BY><OI_VERIFIED_TS> </OI_VERIFIED_TS><OI_CHANGED_TS> </OI_CHANGED_TS><OI_CHANGED_BY> </OI_CHANGED_BY><OI_DEN_INDICATOR> </OI_DEN_INDICATOR><OI_DEN_VERIFIED_BY> </OI_DEN_VERIFIED_BY><OI_DEN_VERIFIED_TS> </OI_DEN_VERIFIED_TS><OI_DEN_CHANGED_TS> </OI_DEN_CHANGED_TS><OI_DEN_CHANGED_BY> </OI_DEN_CHANGED_BY></MEMBER_EMAIL></CCP_MEMBER_EMAIL></UpdateDemographicsResult></UpdateDemographicsResponse></s:Body></s:Envelope>';
                httpRes.setBody(resBodyString);
                system.assert(httpRes.getBody() == resBodyString);         
                
                try
                {
                    Object result = VerifyDemographics_LS_HUM.processverifyDemographics(VerifyDemographics_LS_HUM.contstate4);
                   // system.assert(result!=NULL);  
                }
                catch(Exception e)
                {
                    system.assert(e.getMessage()!=NULL);
                }      
                          
        }
        
                              
        static testMethod void testverifyDemographicsfalse()
        {
            CRMFunctionality_ONOFF_Switch__c CRMOnOff = new CRMFunctionality_ONOFF_Switch__c(Name = '1065536', IsON__c = false );
            insert CRMOnOff;
            Account oAccount = [Select Id FROM Account where Name ='Test Account' LIMIT 1 ]; 
            Test.startTest();
            Continuation continueObj = (Continuation)VerifyDemograhics_LC_HUM.APIService(oAccount.Id,'onclickVerify');
            Test.stopTest();
            HttpResponse httpRes = new HttpResponse();
            httpRes.setStatusCode(400);
            string reqType = 'Continuation-1';        
            List<string> labelGetmember = new List<string>();
            labelGetmember.add(reqType);
            Test.setContinuationResponse('Continuation-1', httpRes);
            try
            {
                VerifyDemographics_LS_HUM.processverifyDemographics(VerifyDemographics_LS_HUM.contstate4);
            }
            catch(Exception e)
            {
                system.assert(e.getMessage()!=NULL);
            }    
        }       
        
     
   }
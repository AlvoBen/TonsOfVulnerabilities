/**
   @Author Gargey Sharma
   @name ProviderSearch_T_HUM
   @CreateDate 10/30/2016
   @Description Test Class for ProviderSearch_H_HUM
   @Version 1.0
   @reference REQ - 270080 : EPSS conversion SOAP to REST
 */ 
  /**********************************************************************************************************
Modification Log:

* Developer Name            Review Number            Date                       Description
-------------------------------------------------------------------------------------------------------------------------------------------------
* Santhi Mandava            71723                    03/03/2017                 Included address,city parameters in EPSS request criteria.               
*************************************************************************************************************************************************/
@isTest
private class ProviderSearch_T_HUM 
{
    /**
    * Method to setup test data
    * 
    * @param    None
    * @return   Void 
    */
    @testSetup static void setup() 
    {  
        //Load all Constants data        
        HUMTestDataHelperTest.getHUMConstantsData();        
        User oUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');        
        insert oUser;
        system.assertEquals(oUser.lastname=='test User1',true);
        
        HUM_Webservice_Callout__c oEPSServiceDetails = new HUM_Webservice_Callout__c();
        oEPSServiceDetails.Name = 'EPSServiceREST_V1';
        oEPSServiceDetails.Certificate_Name__c = 'Test_Certificate';
        oEPSServiceDetails.Active__c = true;
        oEPSServiceDetails.End_Point_URL__c ='https://int-crmservicews.humana.com/EPSServiceREST_V1.0/EnterpriseProviderSearch';
        oEPSServiceDetails.Timeout__c = '12000';
        oEPSServiceDetails.Request_Type__c = 'POST';
        oEPSServiceDetails.Content_Type__c ='application/json'; 
        insert oEPSServiceDetails;
        system.assert(HUM_Webservice_Callout__c.getValues('EPSServiceREST_V1').Certificate_Name__c == 'Test_Certificate');
    }
    
    /**
    * Method to test request construction methods
    * 
    * @param    None
    * @return   Void 
    */
    static testMethod void testConstructRequest()
    {
        User oUser = [SELECT Id FROM User WHERE Lastname='test User1' LIMIT 1];
        system.assert(ouser.id!=null);
        System.runAs (ouser)
        {  
            ProviderSearch_H_HUM oProvSearch = new ProviderSearch_H_HUM();
            Map<string,string> mapRequestDto = new Map<string, string>();
            mapRequestDto.put('sstate','Alasaka');
            mapRequestDto.put('sTaxID','123458');
            mapRequestDto.put('sspeciality','ANL');
            mapRequestDto.put('sFirstName','ttestfname');
            mapRequestDto.put('slastname','testlastname');
            mapRequestDto.put('sFacilityName','testlastname');
            mapRequestDto.put('sPostalCode','12355');
            HttpRequest oRequest = new HttpRequest();
            test.startTest();
            oRequest = oProvSearch.getEPSSReq(mapRequestDto);
            system.assertNotEquals(oRequest.getBody(), null);
            test.stopTest();
        }
    }
    
    /**
    * Method to test response parsing methods
    * 
    * @param    None
    * @return   Void 
    */
    static testMethod void testResponseParsing()
    {
        User oUser = [SELECT Id FROM User WHERE Lastname='test User1' LIMIT 1];
        system.assert(ouser.id!=null);
        System.runAs (ouser)
        {  
            ProviderSearch_H_HUM oProvSearch = new ProviderSearch_H_HUM();
            string sEPSSResponse = '{ "SearchProviderResponse":{ "EnterpriseProviders":[ { "EnterpriseID":"11060211", "UniqueID":{ "Source":"PIM" }, "ProviderName"'+
                                   ':{ "Last":"Artis", "First":"Krystal", "Degree":"PA" }, "DbaName":"Artis, Krystal N PA", "Classification":"", "Demographics":{ "Addresses":[ '+
                                   '{ "AddressType":"Billing", "Line1":"PO BOX 37215", "Line2":"", "City":"Baltimore", "State":"MD", "Zip":"212755001", "Phones":[ '+
                                   '{ "PhoneType":"Phone", "PhoneNumber":"4016029262", "Extension":"" }, { "PhoneType":"Phone", "PhoneNumber":"4239902470", "Extension":"" }, '+
                                   '{ "PhoneType":"Phone", "PhoneNumber":"4103285946", "Extension":"" } ] }, { "AddressType":"Service", "Line1":"111 Michigan Ave NW", "Line2":"", '+
                                   '"City":"Washington", "State":"DC", "Zip":"200102978", "Phones":[ { "PhoneType":"Phone", "PhoneNumber":"2024763032", "Extension":"" }, '+
                                   '{ "PhoneType":"Fax", "PhoneNumber":"2024764928", "Extension":"" } ] } ], "DateOfBirth":"0001-01-01", "Gender":"Female" }, "Identifiers":[ '+
                                   '{ "IdentifierType":"NpiID", "Identifiers":[ { "ID":"1457502858" } ] } ], "Specialties":[ { "Description":"Physician Assistant [PIMS] / '+
                                   'Psychoanalysis [ARC,AMA]" } ], "SourceProviders":[ { "Source":"CAS", "ProviderID":"000079599UD" }, { "Source":"MTV", "ProviderID":"165809451278" }'+
                                   ', { "Source":"NPI", "ProviderID":"1457502858" }, { "Source":"PIM", "ProviderID":"1641194" } ], "Taxonomy":[ { "Code":"363A00000X" }, '+
                                   '{ "Code":"363AM0700X" } ] }, { "EnterpriseID":"4366474", "UniqueID":{ "Source":"DEA" }, "ProviderName":{ "Last":"ARTIS", "First":"KRYSTAL", '+
                                   '"Degree":"PA-C" }, "DbaName":"ARTIS,KRYSTAL NYA PA-C", "Classification":"", "Demographics":{ "Addresses":[ ], "DateOfBirth":"", "Gender":"" }, '+
                                   '"Identifiers":[ { "IdentifierType":"DeaID", "Identifiers":[ { "ID":"MA1917410" } ] } ], "Specialties":[ { "Description":"Family and General '+
                                   'Practice [DEA,NPI]" } ], "SourceProviders":[ { "Source":"DEA", "ProviderID":"MA1917410" } ], "Taxonomy":[ ] } ] } }';
            ProviderSearch_H_HUM oParseRespProvSearch = new ProviderSearch_H_HUM();
            HttpResponse oResponse = new HttpResponse();
            oResponse.setBody(sEPSSResponse);
            test.startTest();
            list<HUMCustomSearchHelper> lstEPSSResults = oProvSearch.parseServiceResponse(oResponse,true);
            system.assertEquals(lstEPSSResults[0].sEnterpriseID,'11060211');
            test.stopTest();
        }
    }
    
    /**
    * Method to test negative scenarios
    * 
    * @param    None
    * @return   Void 
    */
    static testMethod void testNegativeScenarios()
    {
        User oUser = [SELECT Id FROM User WHERE Lastname='test User1' LIMIT 1];
        system.assert(ouser.id!=null);
        System.runAs (ouser)
        {
            //testing invalid request map
            ProviderSearch_H_HUM oProvSearch = new ProviderSearch_H_HUM();
            Map<string,string> mapRequestDto = new Map<string, string>();
            mapRequestDto.put('','');
            HttpRequest oRequest = new HttpRequest();
            string sEPSSResponse = '{}';
            HttpResponse oResponse = new HttpResponse();
            oResponse.setBody(sEPSSResponse);
            test.startTest();
            oRequest = oProvSearch.getEPSSReq(mapRequestDto);
            system.assertNotEquals(oRequest.getbody(), null);
            list<HUMCustomSearchHelper> lstEPSSResults = oProvSearch.parseServiceResponse(oResponse,true);
            system.assertEquals(lstEPSSResults.size(), 0);
            test.stopTest();  
        }
    }
}
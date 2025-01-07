/*****************************************************************************************
Apex class Name    : HUMSearchEPSSServiceAsyncTest
Version            : 1.0
Function           : Test class for HUMSearchEPSSService
Created Date       : 5 Aug 2014

Modification Log:
*   Developer                   Code Review      Date               Description
* ------------------------------------------------------------------------------------------------------------------------------                
*    Ranjeeth Nagishetty        25261           08/05/2014          Original Version

******************************************************************************************/
@isTest
private class HUMSearchEPSSServiceAsyncTest
{
 /*
    * Method Name : setup
    * Description : This method is used to insert the data only once and called automicticaly in all methods
    * Return type : Void
    * Paramater   : None
    */
    
     @testSetup static void setup() {  
                 
        //Load all Constants data        
        HUMTestDataHelperTest.getHUMConstantsData();        
        User oUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');        
        insert oUser;        
        system.assertNotEquals(oUser,null,'​Value not matched');
        
            HUM_Webservice_Callout__c oHumEPSSservice = new HUM_Webservice_Callout__c();
            oHumEPSSservice.name='ProviderSearchService';
            oHumEPSSservice.Active__c=true;
            oHumEPSSservice.Certificate_Name__c='Certificate';
            oHumEPSSservice.End_Point_URL__c='';
            oHumEPSSservice.Timeout__c='5550';
            insert oHumEPSSservice ;
            system.assert(oHumEPSSservice.id!=null);
        
        }
        

    /*
    * Method Name : prepareTestDataForSearchCriteria
    * Description : Test Method to prepare test data for searches
    * Return type : void
    * Paramater   : NA
    */
    static testMethod void prepareTestDataForSearchCriteria()
    {
        User oUser = [SELECT Id FROM User  WHERE Lastname='test User1' LIMIT 1];
        system.assert(ouser.id!=null);
        System.runAs (ouser)
        {  
           
            
            test.startTest();
            HUMSearchEPSSServiceAsync oHumservice= new HUMSearchEPSSServiceAsync();
            System.assertNotEquals(oHumservice, NULL);
            Map<string,string> mapRequestDto = new Map<string, string>();
            mapRequestDto.put('sstate','Alasaka');
            mapRequestDto.put('sTaxID','123458');
            mapRequestDto.put('sspeciality','ANL');
            mapRequestDto.put('sFirstName','ttestfname');
            mapRequestDto.put('slastname','testlastname');
            mapRequestDto.put('sFacilityName','testlastname');
            mapRequestDto.put('sPostalCode','12355');
            Test.setMock(WebServiceMock.class, new HUMProvider_esHumanaComEPSSMockImpl());
            Continuation con ;
            oHumservice.SearchEPSS(con ,mapRequestDto );
            
            HUMProvider_esHumanaComEPSS.SearchResponseDto oSearchResponseDto =new HUMProvider_esHumanaComEPSS.SearchResponseDto();
            System.assertNotEquals(oSearchResponseDto , NULL);
            HUMProvider_esHumanaComEPSS.SearchRequestDto oSearchRequestDto = oHumservice.HUMSearchInputs(mapRequestDto );
            System.assertNotEquals(oSearchRequestDto , NULL);
            HUMProvider_esHumanaComEPSS.EnterpriseProviderSearchServicePort oEPSSProvider = new HUMProvider_esHumanaComEPSS.EnterpriseProviderSearchServicePort();
            System.assertNotEquals(oEPSSProvider , NULL);
            oSearchResponseDto = oEPSSProvider.SearchProvider(oSearchRequestDto);
            System.assertNotEquals(oSearchResponseDto , NULL);
            
            oHumservice.HUMServiceResponse(oSearchResponseDto ); 
            test.stopTest();
        }
    }
    
     /*
    * Method Name : prepareTestDataForSearchCriteriaforNPI
    * Description : Test Method to prepare test data for searches
    * Return type : void
    * Paramater   : NA
    */
    static testMethod void prepareTestDataForSearchCriteriaforNPI()
    {
        User oUser = [SELECT Id FROM User  WHERE Lastname='test User1' LIMIT 1];
        system.assert(ouser.id!=null);
        System.runAs (ouser)
        {   
            test.startTest();
            HUMSearchEPSSServiceAsync oHumservice= new HUMSearchEPSSServiceAsync();
            System.assertNotEquals(oHumservice, NULL);
            Map<string,string> mapRequestDto = new Map<string, string>();
            mapRequestDto.put('sNPI','12345');
            Test.setMock(WebServiceMock.class, new HUMProvider_esHumanaComEPSSMockImpl());
            Continuation con ;
            oHumservice.SearchEPSS(con ,mapRequestDto );
            Map<string,string> mapRequestDtoID = new Map<string, string>();
            Test.setMock(WebServiceMock.class, new HUMProvider_esHumanaComEPSSMockImpl());
            oHumservice.SearchEPSS(con ,mapRequestDtoID );
            HUMProvider_esHumanaComEPSS.SearchResponseDto oSearchResponseDto =new HUMProvider_esHumanaComEPSS.SearchResponseDto();
            System.assertNotEquals(oSearchResponseDto , NULL);
            HUMProvider_esHumanaComEPSS.SearchRequestDto oSearchRequestDto = oHumservice.HUMSearchInputs(mapRequestDto );
            System.assertNotEquals(oSearchRequestDto , NULL);
            HUMProvider_esHumanaComEPSS.EnterpriseProviderSearchServicePort oEPSSProvider = new HUMProvider_esHumanaComEPSS.EnterpriseProviderSearchServicePort();
            System.assertNotEquals(oEPSSProvider , NULL);
            oSearchResponseDto = oEPSSProvider.SearchProvider(oSearchRequestDto);
            System.assertNotEquals(oSearchResponseDto , NULL);
            oHumservice.HUMServiceResponse(oSearchResponseDto );
            test.stopTest();
        }
    }
}
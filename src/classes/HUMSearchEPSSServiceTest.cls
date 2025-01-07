/*****************************************************************************************
Apex class Name    : HUMSearchEPSSServiceTest
Version            : 1.0
Function           : Test class for HUMSearchEPSSService
Created Date       : 5 Aug 2014

Modification Log:
*   Developer                   Code Review      Date               Description
* ------------------------------------------------------------------------------------------------------------------------------                
*    Ranjeeth Nagishetty        17123            08/05/2014          Original Version
*    Ranjeeth Nagishetty        17534            08/27/2014          Test Class updated
*    Ranjeeth Nagishetty        17722            09/04/2014          Test Class updated
*    Ranjeeth Nagishetty        18042            09/11/2014          Test Class updated
*    Ranjeeth Nagishetty        18381            10/08/2014          Test class updated 
*    Ranjeeth Nagishetty        18987            11/06/2014          Test Class updated
******************************************************************************************/
@isTest
private class HUMSearchEPSSServiceTest
{
    /*
    * Method Name : prepareTestDataForSearchCriteria
    * Description : Test Method to prepare test data for searches
    * Return type : void
    * Paramater   : NA
    */
    static testMethod void prepareTestDataForSearchCriteria()
    {
        User ouser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert ouser ;
        system.assert(ouser.id!=null);
        System.runAs (ouser)
        {  
            HUM_Webservice_Callout__c oHumEPSSservice = new HUM_Webservice_Callout__c();
            oHumEPSSservice.name='ProviderSearchService';
            oHumEPSSservice.Active__c=true;
            oHumEPSSservice.Certificate_Name__c='Certificate';
            oHumEPSSservice.End_Point_URL__c='';
            oHumEPSSservice.Timeout__c='5550';
            insert oHumEPSSservice ;
            system.assert(oHumEPSSservice.id!=null);
            
            test.startTest();
            HUMSearchEPSSService oHumservice= new HUMSearchEPSSService();
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
            oHumservice.SearchEPSS(mapRequestDto );
            
            HUMProvider_esHumanaComEPSS.SearchResponseDto oSearchResponseDto =new HUMProvider_esHumanaComEPSS.SearchResponseDto();
            System.assertNotEquals(oSearchResponseDto , NULL);
            HUMProvider_esHumanaComEPSS.SearchRequestDto oSearchRequestDto = oHumservice.HUMSearchInputs(mapRequestDto );
            System.assertNotEquals(oSearchRequestDto , NULL);
            HUMProvider_esHumanaComEPSS.EnterpriseProviderSearchServicePort oEPSSProvider = new HUMProvider_esHumanaComEPSS.EnterpriseProviderSearchServicePort();
            System.assertNotEquals(oEPSSProvider , NULL);
            oSearchResponseDto = oEPSSProvider.SearchProvider(oSearchRequestDto);
            System.assertNotEquals(oSearchResponseDto , NULL);
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
        User ouser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert ouser ;
        system.assert(ouser.id!=null);
        System.runAs (ouser)
        {  
            HUM_Webservice_Callout__c oHumEPSSservice = new HUM_Webservice_Callout__c();
            oHumEPSSservice.name='ProviderSearchService';
            oHumEPSSservice.Active__c=true;
            oHumEPSSservice.Certificate_Name__c='Certificate';
            oHumEPSSservice.End_Point_URL__c='';
            oHumEPSSservice.Timeout__c='5550';
            insert oHumEPSSservice ;
            system.assert(oHumEPSSservice.id!=null);
            
            test.startTest();
            HUMSearchEPSSService oHumservice= new HUMSearchEPSSService();
            System.assertNotEquals(oHumservice, NULL);
            Map<string,string> mapRequestDto = new Map<string, string>();
            mapRequestDto.put('sNPI','12345');
            Test.setMock(WebServiceMock.class, new HUMProvider_esHumanaComEPSSMockImpl());
            oHumservice.SearchEPSS(mapRequestDto );
            Map<string,string> mapRequestDtoID = new Map<string, string>();
            Test.setMock(WebServiceMock.class, new HUMProvider_esHumanaComEPSSMockImpl());
            oHumservice.SearchEPSS(mapRequestDtoID );
            HUMProvider_esHumanaComEPSS.SearchResponseDto oSearchResponseDto =new HUMProvider_esHumanaComEPSS.SearchResponseDto();
            System.assertNotEquals(oSearchResponseDto , NULL);
            HUMProvider_esHumanaComEPSS.SearchRequestDto oSearchRequestDto = oHumservice.HUMSearchInputs(mapRequestDto );
            System.assertNotEquals(oSearchRequestDto , NULL);
            HUMProvider_esHumanaComEPSS.EnterpriseProviderSearchServicePort oEPSSProvider = new HUMProvider_esHumanaComEPSS.EnterpriseProviderSearchServicePort();
            System.assertNotEquals(oEPSSProvider , NULL);
            oSearchResponseDto = oEPSSProvider.SearchProvider(oSearchRequestDto);
            System.assertNotEquals(oSearchResponseDto , NULL);
            test.stopTest();
        }
    }
}
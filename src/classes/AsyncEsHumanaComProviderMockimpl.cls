/*****************************************************************************************
Apex class Name    : AsyncEsHumanaComProviderEnterpriseMockImpl
Version            : 1.0
Function           : Test class to generate fake response 
Created Date       : 5 Aug 2014

Modification Log:
*   Developer                   Code Review     Date                Description
* ------------------------------------------------------------------------------------------------------------------------------                
*   Manish Kumar                  17123         08/05/2014          Original Version
*   Ranjeeth Nagishetty           18042         09/19/2014          Updated Error message of test class
*   Ranjeeth Nagishetty           19484         11/24/2014          updated Address type  for test class
******************************************************************************************/

@isTest
public with sharing class AsyncEsHumanaComProviderMockimpl implements WebServiceMock 
{
    /*
    * Method Name   :    doInvoke
    * Description   :    This method is used to create mock service response
    * Return Type   :    Void
    * Parameters    :    Object,Object ,Map<String, Object>,String,String,String,String,String,String
    */
    public void doInvoke(Object stub,Object request,Map<String, Object> response,String endpoint,String soapAction,String requestName,
                            String responseNS,String responseName,String responseType)
    {     
        if(responseType == 'AsyncEsHumanaComProviderEnterprise.beginSearchProvider')
        {
            HUMProvider_esHumanaComEPSS.SearchProviderResponse_element  onewResponse= new  HUMProvider_esHumanaComEPSS.SearchProviderResponse_element();
            System.assertNotEquals(onewResponse, NULL);
            HUMProvider_esHumanaComEPSS.ResponseMessageDto oResponseMessageDto = new HUMProvider_esHumanaComEPSS.ResponseMessageDto();
            System.assertNotEquals(oResponseMessageDto , NULL);
            HUMProvider_esHumanaComEPSS.ResultInfoDto oResultInfoDto = new HUMProvider_esHumanaComEPSS.ResultInfoDto();
            System.assertNotEquals(oResultInfoDto  , NULL);
            
            HUMProvider_esHumanaComEPSS.SearchResponseDto oSearchResponseDto = new HUMProvider_esHumanaComEPSS.SearchResponseDto();
            System.assertNotEquals(oSearchResponseDto , NULL);
            
            HUMProvider_esHumanaComEPSS.EnterpriseProviderDto oentProv = new HUMProvider_esHumanaComEPSS.EnterpriseProviderDto();
            List<HUMProvider_esHumanaComEPSS.EnterpriseProviderDto> olstentProv = new List<HUMProvider_esHumanaComEPSS.EnterpriseProviderDto>();
            oentProv=prepareSpecialtiesAndTaxonomy(oentProv);
            oentProv.Identifiers = prepareIdentifiers() ;
            
            oentProv=prepareProviders(oentProv);
            oentProv.Classification = 'ABCD';
            olstentProv.add(oentProv);
            oSearchResponseDto.EnterpriseProviders = olstentProv;
            oSearchResponseDto.Message = prepareResponseMessageDto();
            HUMProvider_esHumanaComEPSS.SearchProviderResponse_element ovfResponse = new HUMProvider_esHumanaComEPSS.SearchProviderResponse_element();
            ovfResponse.SearchResponse = oSearchResponseDto;
            // Add response element to the response parameter, as follows:
            response.put('response_x', ovfResponse); 
        }
    }
    
    /*
    * Method Name   :    prepareResponseMessageDto
    * Description   :    This method is used to create mock Response of the callout
    * Return Type   :    HUMProvider_esHumanaComEPSS.ResponseMessageDto
    * Parameters    :    NA
    */
    private HUMProvider_esHumanaComEPSS.ResponseMessageDto prepareResponseMessageDto()
    {
        HUMProvider_esHumanaComEPSS.ResponseMessageDto oHUMRespMessageDTO = new HUMProvider_esHumanaComEPSS.ResponseMessageDto();
        oHUMRespMessageDTO.isError = false;
        oHUMRespMessageDTO.IsWarning = false;
        List<HUMProvider_esHumanaComEPSS.WarningMessageDto> olstmesssges=new List<HUMProvider_esHumanaComEPSS.WarningMessageDto>();
        HUMProvider_esHumanaComEPSS.WarningMessageDto owarningMessage=new HUMProvider_esHumanaComEPSS.WarningMessageDto();
        owarningMessage.Description = 'Results limited due to overly common criteria';
        olstmesssges.Add(owarningMessage);
        oHUMRespMessageDTO.Warnings = olstmesssges;
        return oHUMRespMessageDTO;
    }
    
    /*
    * Method Name   :    prepareSpecialtiesAndTaxonomy
    * Description   :    This method is used to create mock Specialties and Taxonomy for service response.
    * Return Type   :    HUMProvider_esHumanaComEPSS.EnterpriseProviderDto
    * Parameters    :    HUMProvider_esHumanaComEPSS.EnterpriseProviderDto
    */
    private HUMProvider_esHumanaComEPSS.EnterpriseProviderDto prepareSpecialtiesAndTaxonomy(HUMProvider_esHumanaComEPSS.EnterpriseProviderDto oentProv)
    {
        HUMProvider_esHumanaComEPSS.SpecialtyDto ospecialties = new HUMProvider_esHumanaComEPSS.SpecialtyDto();
        list<HUMProvider_esHumanaComEPSS.SpecialtyDto> lstallSpecialties = new list<HUMProvider_esHumanaComEPSS.SpecialtyDto>();
        ospecialties.Description ='Ambulances';
        lstallSpecialties.add(ospecialties);
        list<HUMProvider_esHumanaComEPSS.ProviderTaxonomyDto> lstTaxonomy = new  list<HUMProvider_esHumanaComEPSS.ProviderTaxonomyDto>();
        HUMProvider_esHumanaComEPSS.ProviderTaxonomyDto oTaxonomytype = new  HUMProvider_esHumanaComEPSS.ProviderTaxonomyDto();
        oTaxonomytype.Code='anannan';
        oTaxonomytype.Code='1234';
        lstTaxonomy .add(oTaxonomytype);
        oentProv.Taxonomy = lstTaxonomy ;
        oentProv.Specialties = lstallSpecialties;
        return oentProv;
    }
    
    /*
    * Method Name   :    prepareIdentifiers
    * Description   :    This method is used to create mock identifiers for service response.
    * Return Type   :    list<HUMProvider_esHumanaComEPSS.IdentifierListDto>
    * Parameters    :    NA
    */
    private list<HUMProvider_esHumanaComEPSS.IdentifierListDto> prepareIdentifiers()
    {
        HUMProvider_esHumanaComEPSS.IdentifierListDto oIdty = new HUMProvider_esHumanaComEPSS.IdentifierListDto();
        list<HUMProvider_esHumanaComEPSS.IdentifierListDto> lstIdentify = new  list<HUMProvider_esHumanaComEPSS.IdentifierListDto>();
            
        list<HUMProvider_esHumanaComEPSS.IdentifierDto> lstIdentifiers = new list<HUMProvider_esHumanaComEPSS.IdentifierDto>();
        HUMProvider_esHumanaComEPSS.IdentifierDto oIdentifyId = new HUMProvider_esHumanaComEPSS.IdentifierDto();
        oIdentifyId.Id ='123456789';
        oIdentifyId.Id ='123456781';
        lstIdentifiers.add(oIdentifyId );
        oIdty.Identifiers = lstIdentifiers;
        oIdty.IdentifierType = 'TaxID';
        oIdty.IdentifierType = 'NpID';
        lstIdentify.add(oIdty);
        return lstIdentify;
    }
    
    /*
    * Method Name   :    prepareProviders
    * Description   :    This method is used to create mock providers for service response.
    * Return Type   :    HUMProvider_esHumanaComEPSS.EnterpriseProviderDto
    * Parameters    :    HUMProvider_esHumanaComEPSS.EnterpriseProviderDto
    */
    private HUMProvider_esHumanaComEPSS.EnterpriseProviderDto prepareProviders(HUMProvider_esHumanaComEPSS.EnterpriseProviderDto oentProv)
    {
        HUMProvider_esHumanaComEPSS.ProviderDemographicsDto odemograph = new HUMProvider_esHumanaComEPSS.ProviderDemographicsDto();
        odemograph.Degree = 'ABCD';
        System.AssertEquals( odemograph.Degree , 'ABCD');
        
        odemograph.Gender = 'Male';
        System.AssertEquals( odemograph.Gender , 'Male');
        
        HUMProvider_esHumanaComEPSS.AddressDto oaddr = new  HUMProvider_esHumanaComEPSS.AddressDto();
        List<HUMProvider_esHumanaComEPSS.AddressDto> lstAddr = new List<HUMProvider_esHumanaComEPSS.AddressDto>();
       
        oaddr.AddressType = Label.HumServiceAddresstype;
        oaddr.Line1 = 'Test';
        System.AssertEquals( oaddr.Line1, 'Test');
        
        oaddr.City = 'Test';
        lstAddr.add(oaddr);
        oaddr.AddressType =  Label.HUMBillingAddresstype;
        oaddr.City = 'Test';
        lstAddr.add(oaddr);
        HUMProvider_esHumanaComEPSS.NameDto oPrName = new HUMProvider_esHumanaComEPSS.NameDto();
        oPrName.first='test';
        oprName.last='last';
        odemograph.Addresses = lstAddr;
        oentProv.providername = oprName;
        oentProv.Demographics = odemograph;
        oentProv.EnterpriseID =125480;
        return oentProv;
    }
}
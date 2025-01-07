/*****************************************************************************************
Apex class Name    : HUMProvider_esHumanaComEPSSTest
Version            : 1.0
Function           : This is controller for HUMProvider_esHumanaComEPSS
Created Date       : 5 Aug 2014

Modification Log:
*   Developer                   Code Review     Date               Description
* ------------------------------------------------------------------------------------------------------------------------------                
*   Manish Kumar                 17123         08/05/2014          Original Version
******************************************************************************************/
@isTest
private class HUMProvider_esHumanaComEPSSTest
{
    /*
    * Method Name   :    overallTestMethod
    * Description   :    This method is used for overall testing for HUMProvider_esHumanaComEPSS.
    * Return Type   :    void
    * Parameters    :    NA
    */
    static testMethod void overallTestMethod(){
        User thisUser = [ select Id from User where Id =: UserInfo.getUserId() ];
        
        System.runAs ( thisUser )
        {
            // This causes a fake response to be generated
            Test.setMock(WebServiceMock.class, new HUMProvider_esHumanaComEPSSMockImpl());
            
            test.startTest();
            
            HUMProvider_esHumanaComEPSS HUMProvider = new HUMProvider_esHumanaComEPSS();
            System.assertNotEquals(HUMProvider , NULL);
            HUMProvider_esHumanaComEPSS.MemberDetailsDto MemberDetailsDto = new HUMProvider_esHumanaComEPSS.MemberDetailsDto();
            System.assertNotEquals(MemberDetailsDto , NULL);
            HUMProvider_esHumanaComEPSS.TaxonomiesResponseDto TaxonomiesResponseDto = new HUMProvider_esHumanaComEPSS.TaxonomiesResponseDto();
            System.assertNotEquals(TaxonomiesResponseDto , NULL);
            HUMProvider_esHumanaComEPSS.ResultInfoDto ResultInfoDto = new HUMProvider_esHumanaComEPSS.ResultInfoDto();
            System.assertNotEquals(TaxonomiesResponseDto , NULL);
            HUMProvider_esHumanaComEPSS.AddressCriteriaDto AddressCriteriaDto = new HUMProvider_esHumanaComEPSS.AddressCriteriaDto();
            System.assertNotEquals(AddressCriteriaDto, NULL);
            HUMProvider_esHumanaComEPSS.EnterpriseProviderDto EnterpriseProviderDto = new HUMProvider_esHumanaComEPSS.EnterpriseProviderDto();
            System.assertNotEquals(EnterpriseProviderDto , NULL);
            HUMProvider_esHumanaComEPSS.MemberContextDto MemberContextDto = new HUMProvider_esHumanaComEPSS.MemberContextDto();
            System.assertNotEquals(MemberContextDto , NULL);
            HUMProvider_esHumanaComEPSS.FaultExceptionResponse_element FaultExceptionResponse_element = new HUMProvider_esHumanaComEPSS.FaultExceptionResponse_element();
            System.assertNotEquals(FaultExceptionResponse_element , NULL);
            HUMProvider_esHumanaComEPSS.ResultPagingDto ResultPagingDto = new HUMProvider_esHumanaComEPSS.ResultPagingDto();
            System.assertNotEquals(ResultPagingDto , NULL);
            HUMProvider_esHumanaComEPSS.RetrieveCriteriaDto RetrieveCriteriaDto = new HUMProvider_esHumanaComEPSS.RetrieveCriteriaDto();
            System.assertNotEquals(RetrieveCriteriaDto , NULL);
            HUMProvider_esHumanaComEPSS.PreferenceDto PreferenceDto = new HUMProvider_esHumanaComEPSS.PreferenceDto();
            System.assertNotEquals(PreferenceDto , NULL);
            HUMProvider_esHumanaComEPSS.AttributeCriteriaDto AttributeCriteriaDto = new HUMProvider_esHumanaComEPSS.AttributeCriteriaDto();
            System.assertNotEquals(AttributeCriteriaDto , NULL);
            HUMProvider_esHumanaComEPSS.IdentifierListDto IdentifierListDto = new HUMProvider_esHumanaComEPSS.IdentifierListDto();
            System.assertNotEquals(IdentifierListDto , NULL);
            HUMProvider_esHumanaComEPSS.SpecialtyDto SpecialtyDto = new HUMProvider_esHumanaComEPSS.SpecialtyDto();
            System.assertNotEquals(SpecialtyDto , NULL);
            HUMProvider_esHumanaComEPSS.ScoredSourceProviderDto ScoredSourceProviderDto = new HUMProvider_esHumanaComEPSS.ScoredSourceProviderDto();
            System.assertNotEquals(ScoredSourceProviderDto , NULL);
            HUMProvider_esHumanaComEPSS.SearchProviderRequest_element SearchProviderRequest_element = new HUMProvider_esHumanaComEPSS.SearchProviderRequest_element();
            System.assertNotEquals(SearchProviderRequest_element , NULL);
            HUMProvider_esHumanaComEPSS.SourceStatusDto SourceStatusDto = new HUMProvider_esHumanaComEPSS.SourceStatusDto();
            System.assertNotEquals(SourceStatusDto, NULL);
            HUMProvider_esHumanaComEPSS.NameDto NameDto = new HUMProvider_esHumanaComEPSS.NameDto();
            System.assertNotEquals(NameDto, NULL);
            HUMProvider_esHumanaComEPSS.PcpRequestDto PcpRequestDto = new HUMProvider_esHumanaComEPSS.PcpRequestDto();
            System.assertNotEquals(PcpRequestDto , NULL);
            HUMProvider_esHumanaComEPSS.PcpInfoDto PcpInfoDto = new HUMProvider_esHumanaComEPSS.PcpInfoDto();
            System.assertNotEquals(PcpInfoDto, NULL);
            HUMProvider_esHumanaComEPSS.ParInfoDto ParInfoDto = new HUMProvider_esHumanaComEPSS.ParInfoDto();
             System.assertNotEquals(ParInfoDto , NULL);
            HUMProvider_esHumanaComEPSS.ResponseMessageDto ResponseMessageDto = new HUMProvider_esHumanaComEPSS.ResponseMessageDto();
            System.assertNotEquals(ResponseMessageDto , NULL);
            HUMProvider_esHumanaComEPSS.TaxonomyDetailDto TaxonomyDetailDto = new HUMProvider_esHumanaComEPSS.TaxonomyDetailDto();
            System.assertNotEquals(TaxonomyDetailDto, NULL);
            HUMProvider_esHumanaComEPSS.ErrorMessageDto ErrorMessageDto = new HUMProvider_esHumanaComEPSS.ErrorMessageDto();
            System.assertNotEquals(ErrorMessageDto , NULL);
            HUMProvider_esHumanaComEPSS.MasterSpecialtyDto MasterSpecialtyDto = new HUMProvider_esHumanaComEPSS.MasterSpecialtyDto();
            System.assertNotEquals(MasterSpecialtyDto , NULL);
            HUMProvider_esHumanaComEPSS.FaultMessageDto FaultMessageDto = new HUMProvider_esHumanaComEPSS.FaultMessageDto(); 
            System.assertNotEquals(FaultMessageDto , NULL);
            HUMProvider_esHumanaComEPSS.NameValueDto NameValueDto = new HUMProvider_esHumanaComEPSS.NameValueDto();
            System.assertNotEquals(NameValueDto , NULL);
            HUMProvider_esHumanaComEPSS.PreferredCommunicationDto PreferredCommunicationDto = new HUMProvider_esHumanaComEPSS.PreferredCommunicationDto();
            System.assertNotEquals(PreferredCommunicationDto , NULL);
            HUMProvider_esHumanaComEPSS.BillingEntityDto BillingEntityDto = new HUMProvider_esHumanaComEPSS.BillingEntityDto();
            System.assertNotEquals(BillingEntityDto, NULL);
            HUMProvider_esHumanaComEPSS.SearchResponseDto SearchResponseDto = new HUMProvider_esHumanaComEPSS.SearchResponseDto();
            System.assertNotEquals(SearchResponseDto , NULL);
            HUMProvider_esHumanaComEPSS.TaxonomiesRequestDto TaxonomiesRequestDto = new HUMProvider_esHumanaComEPSS.TaxonomiesRequestDto();
            System.assertNotEquals(TaxonomiesRequestDto , NULL);
            HUMProvider_esHumanaComEPSS.SearchProviderResponse_element SearchProviderResponse_element = new HUMProvider_esHumanaComEPSS.SearchProviderResponse_element();
            System.assertNotEquals(SearchProviderResponse_element , NULL);
            HUMProvider_esHumanaComEPSS.SearchOptionDto SearchOptionDto = new HUMProvider_esHumanaComEPSS.SearchOptionDto();
            System.assertNotEquals(SearchOptionDto , NULL);
            HUMProvider_esHumanaComEPSS.ProviderDemographicsDto ProviderDemographicsDto = new HUMProvider_esHumanaComEPSS.ProviderDemographicsDto();
            System.assertNotEquals(ProviderDemographicsDto , NULL);
            HUMProvider_esHumanaComEPSS.CoverageDetailsDto CoverageDetailsDto = new HUMProvider_esHumanaComEPSS.CoverageDetailsDto();
            System.assertNotEquals(CoverageDetailsDto , NULL);
            HUMProvider_esHumanaComEPSS.TaxonomyDto TaxonomyDto = new HUMProvider_esHumanaComEPSS.TaxonomyDto();
            System.assertNotEquals(TaxonomyDto , NULL);
            HUMProvider_esHumanaComEPSS.ProviderTaxonomyDto ProviderTaxonomyDto = new HUMProvider_esHumanaComEPSS.ProviderTaxonomyDto(); 
            System.assertNotEquals(ProviderTaxonomyDto , NULL);
            HUMProvider_esHumanaComEPSS.WarningMessageDto WarningMessageDto = new HUMProvider_esHumanaComEPSS.WarningMessageDto();
            System.assertNotEquals(WarningMessageDto , NULL);
            HUMProvider_esHumanaComEPSS.AddressDto AddressDto = new HUMProvider_esHumanaComEPSS.AddressDto();
            System.assertNotEquals(AddressDto , NULL);
            HUMProvider_esHumanaComEPSS.MasterSpecialtiesResponseDto MasterSpecialtiesResponseDto = new HUMProvider_esHumanaComEPSS.MasterSpecialtiesResponseDto();
            System.assertNotEquals(MasterSpecialtiesResponseDto , NULL);
            HUMProvider_esHumanaComEPSS.FiltersDto FiltersDto = new HUMProvider_esHumanaComEPSS.FiltersDto();
            System.assertNotEquals(FiltersDto , NULL);
            HUMProvider_esHumanaComEPSS.SpecialtyAttributesDto SpecialtyAttributesDto = new HUMProvider_esHumanaComEPSS.SpecialtyAttributesDto();
            System.assertNotEquals(SpecialtyAttributesDto , NULL);
            HUMProvider_esHumanaComEPSS.SearchRequestDto SearchRequestDto = new HUMProvider_esHumanaComEPSS.SearchRequestDto();
            System.assertNotEquals(SearchRequestDto , NULL);
            HUMProvider_esHumanaComEPSS.RetrieveProviderRequestDto RetrieveProviderRequestDto = new HUMProvider_esHumanaComEPSS.RetrieveProviderRequestDto();
            System.assertNotEquals(RetrieveProviderRequestDto , NULL);
            HUMProvider_esHumanaComEPSS.SearchCriteriaDto SearchCriteriaDto = new HUMProvider_esHumanaComEPSS.SearchCriteriaDto();
            System.assertNotEquals(SearchCriteriaDto , NULL);
            HUMProvider_esHumanaComEPSS.IdentifierDto IdentifierDto = new HUMProvider_esHumanaComEPSS.IdentifierDto();
            System.assertNotEquals(IdentifierDto , NULL);
            HUMProvider_esHumanaComEPSS.MasterSpecialtiesRequestDto MasterSpecialtiesRequestDto = new HUMProvider_esHumanaComEPSS.MasterSpecialtiesRequestDto();
            System.assertNotEquals(MasterSpecialtiesRequestDto , NULL);
            HUMProvider_esHumanaComEPSS.ThresholdDto ThresholdDto = new HUMProvider_esHumanaComEPSS.ThresholdDto();
            System.assertNotEquals(ThresholdDto , NULL);
            HUMProvider_esHumanaComEPSS.TaxonomyConvertDto TaxonomyConvertDto = new HUMProvider_esHumanaComEPSS.TaxonomyConvertDto();
            System.assertNotEquals(TaxonomyConvertDto , NULL);
            HUMProvider_esHumanaComEPSS.SourceProviderDto SourceProviderDto = new HUMProvider_esHumanaComEPSS.SourceProviderDto();
            System.assertNotEquals(SourceProviderDto , NULL);
            HUMProvider_esHumanaComEPSS.PreferenceDetailsDto PreferenceDetailsDto = new HUMProvider_esHumanaComEPSS.PreferenceDetailsDto();
            System.assertNotEquals(PreferenceDetailsDto , NULL);
            HUMProvider_esHumanaComEPSS.TelephoneDto TelephoneDto = new HUMProvider_esHumanaComEPSS.TelephoneDto();
            System.assertNotEquals(TelephoneDto , NULL);
            HUMProvider_esHumanaComEPSS.EnterpriseProviderSearchServicePort EnterpriseProviderSearchServicePort = new HUMProvider_esHumanaComEPSS.EnterpriseProviderSearchServicePort();
            EnterpriseProviderSearchServicePort.SearchProvider(SearchRequestDto);
             System.assertNotEquals(EnterpriseProviderSearchServicePort , NULL);
            test.stopTest();
        }
    }
}
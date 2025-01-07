/*****************************************************************************************
Apex class Name    : HUMVerifyFamilyDatacontractTest
Version            : 1.0
Function           : Test class for HUMVerifyFamilyDatacontract
Created Date       : 5 Aug 2014

Modification Log:
*   Developer                   Code Review      Date               Description
* ------------------------------------------------------------------------------------------------------------------------------                
*    Manish Kumar                 17076               08/05/2014          Original Version
******************************************************************************************/
@isTest
private class HUMVerifyFamilyDatacontractTest
{
    /*
    * Method Name   :    overallTestMethod
    * Description   :    This method is used for overall testing for HUMVerifyFamilyDatacontract.
    * Return Type   :    void
    * Parameters    :    NA
    */
    static testMethod void overallTestMethod(){
        
        User thisUser = [ select Id from User where Id =: UserInfo.getUserId() ];
        
        System.runAs ( thisUser )
        {
             // This causes a fake response to be generated
            Test.setMock(WebServiceMock.class, new HUMVerifyFamilyWSDLMockImplHelper());
            
            test.startTest();
            
            HUMVerifyFamilyDatacontract verifyFamilyContract = new HUMVerifyFamilyDatacontract();
            
            HUMVerifyFamilyDatacontract.LongTermCareDTO longtrmCare = new HUMVerifyFamilyDatacontract.LongTermCareDTO();
            
            HUMVerifyFamilyDatacontract.ArrayOfMemberCoverageDTO arrOfMember = new HUMVerifyFamilyDatacontract.ArrayOfMemberCoverageDTO();
            
            HUMVerifyFamilyDatacontract.ArrayOfClinicalCaseDTO ArrayOfClinicalCaseDTO = new  HUMVerifyFamilyDatacontract.ArrayOfClinicalCaseDTO();
            
            HUMVerifyFamilyDatacontract.PlanDTO PlanDTO = new  HUMVerifyFamilyDatacontract.PlanDTO();
            
            HUMVerifyFamilyDatacontract.ProviderDTO ProviderDTO = new  HUMVerifyFamilyDatacontract.ProviderDTO();
            
            HUMVerifyFamilyDatacontract.SupplementalCoverageDTO SupplementalCoverageDTO = new  HUMVerifyFamilyDatacontract.SupplementalCoverageDTO();
            
            HUMVerifyFamilyDatacontract.MemberCoveragePropertyDTO MemberCoveragePropertyDTO = new  HUMVerifyFamilyDatacontract.MemberCoveragePropertyDTO();
            
            HUMVerifyFamilyDatacontract.ArrayOfLongTermCareDTO ArrayOfLongTermCareDTO = new  HUMVerifyFamilyDatacontract.ArrayOfLongTermCareDTO();
            
            HUMVerifyFamilyDatacontract.MemberCoverageDTO MemberCoverageDTO = new  HUMVerifyFamilyDatacontract.MemberCoverageDTO();
            
            HUMVerifyFamilyDatacontract.PartnerDetailDTO PartnerDetailDTO = new  HUMVerifyFamilyDatacontract.PartnerDetailDTO();
            
            HUMVerifyFamilyDatacontract.ArrayOfMemberCoverageSourceDTO ArrayOfMemberCoverageSourceDTO = new  HUMVerifyFamilyDatacontract.ArrayOfMemberCoverageSourceDTO();
            
            HUMVerifyFamilyDatacontract.MemberCoverageSourceDTO MemberCoverageSourceDTO = new  HUMVerifyFamilyDatacontract.MemberCoverageSourceDTO();
            
            HUMVerifyFamilyDatacontract.ArrayOfMemberCoveragePropertyDTO ArrayOfMemberCoveragePropertyDTO = new  HUMVerifyFamilyDatacontract.ArrayOfMemberCoveragePropertyDTO();
            
            HUMVerifyFamilyDatacontract.COBDTO COBDTO = new  HUMVerifyFamilyDatacontract.COBDTO();
            
            HUMVerifyFamilyDatacontract.ClinicalCaseDTO ClinicalCaseDTO = new  HUMVerifyFamilyDatacontract.ClinicalCaseDTO();
            
            HUMVerifyFamilyDatacontract.MemberPropertiesDTO MemberPropertiesDTO = new  HUMVerifyFamilyDatacontract.MemberPropertiesDTO();
            
            HUMVerifyFamilyDatacontract.ArrayOfMemberDTO ArrayOfMemberDTO = new  HUMVerifyFamilyDatacontract.ArrayOfMemberDTO();
            
            HUMVerifyFamilyDatacontract.AddressDTO AddressDTO = new  HUMVerifyFamilyDatacontract.AddressDTO();
            
            HUMVerifyFamilyDatacontract.NetworkDTO NetworkDTO = new  HUMVerifyFamilyDatacontract.NetworkDTO();
            
            HUMVerifyFamilyDatacontract.ArrayOfCOBDTO ArrayOfCOBDTO = new  HUMVerifyFamilyDatacontract.ArrayOfCOBDTO();
            
            HUMVerifyFamilyDatacontract.MemberDTO MemberDTO = new  HUMVerifyFamilyDatacontract.MemberDTO();
            
            HUMVerifyFamilyDatacontract.SupplementCoverageKeysDTO SupplementCoverageKeysDTO = new  HUMVerifyFamilyDatacontract.SupplementCoverageKeysDTO();
            
            HUMVerifyFamilyDatacontract.ArrayOfSupplementalCoverageDTO ArrayOfSupplementalCoverageDTO = new  HUMVerifyFamilyDatacontract.ArrayOfSupplementalCoverageDTO();
            
            HUMVerifyFamilyDatacontract.BaseMemberDTO BaseMemberDTO = new  HUMVerifyFamilyDatacontract.BaseMemberDTO();
            
            HUMVerifyFamilyDatacontract.ArrayOfPartnerDetailDTO ArrayOfPartnerDetailDTO = new  HUMVerifyFamilyDatacontract.ArrayOfPartnerDetailDTO();
            
            HUMVerifyFamilyDatacontract.APTCDTO APTCDTO = new  HUMVerifyFamilyDatacontract.APTCDTO();
            
            HUMVerifyFamilyDatacontract.UpdatedMemberDemographicsDetailsDTO UpdatedMemberDemographicsDetailsDTO = new  HUMVerifyFamilyDatacontract.UpdatedMemberDemographicsDetailsDTO();
            
            HUMVerifyFamilyDatacontract.GCSValidationErrors GCSValidationErrors = new  HUMVerifyFamilyDatacontract.GCSValidationErrors();
            
            HUMVerifyFamilyDatacontract.ArrayOfAPTCDTO ArrayOfAPTCDTO = new  HUMVerifyFamilyDatacontract.ArrayOfAPTCDTO();
            
            HUMVerifyFamilyDatacontract.ArrayOfGCSValidationErrors ArrayOfGCSValidationErrors = new  HUMVerifyFamilyDatacontract.ArrayOfGCSValidationErrors();
            
            HUMVerifyFamilyDatacontract.PersonCoverageInfoDTO PersonCoverageInfoDTO = new  HUMVerifyFamilyDatacontract.PersonCoverageInfoDTO();
            
            HUMVerifyFamilyDatacontract.UpdatedMemberResidentialAddressDTO UpdatedMemberResidentialAddressDTO = new  HUMVerifyFamilyDatacontract.UpdatedMemberResidentialAddressDTO();
            
            HUMVerifyFamilyDatacontract.ArrayOfUpdatedMemberCoverageDetailsDTO ArrayOfUpdatedMemberCoverageDetailsDTO = new  HUMVerifyFamilyDatacontract.ArrayOfUpdatedMemberCoverageDetailsDTO();
            
            HUMVerifyFamilyDatacontract.ResidentialAddressDTO ResidentialAddressDTO = new  HUMVerifyFamilyDatacontract.ResidentialAddressDTO();
            
            HUMVerifyFamilyDatacontract.UpdatedMemberAddressDetailsDTO UpdatedMemberAddressDetailsDTO = new  HUMVerifyFamilyDatacontract.UpdatedMemberAddressDetailsDTO();
            
            HUMVerifyFamilyDatacontract.UpdatedMemberCoverageDetailsDTO UpdatedMemberCoverageDetailsDTO = new  HUMVerifyFamilyDatacontract.UpdatedMemberCoverageDetailsDTO();
            
            HUMVerifyFamilyDatacontract.PersonDemographicsLastVerifiedInfoDTO PersonDemographicsLastVerifiedInfoDTO = new  HUMVerifyFamilyDatacontract.PersonDemographicsLastVerifiedInfoDTO();
            
            HUMVerifyFamilyDatacontract.ArrayOfUpdatedMemberAddressDetailsDTO ArrayOfUpdatedMemberAddressDetailsDTO = new  HUMVerifyFamilyDatacontract.ArrayOfUpdatedMemberAddressDetailsDTO();
            
            HUMVerifyFamilyDatacontract.ArrayOfPersonMemberInfoDTO ArrayOfPersonMemberInfoDTO = new  HUMVerifyFamilyDatacontract.ArrayOfPersonMemberInfoDTO();
            
            HUMVerifyFamilyDatacontract.ArrayOfPersonCoverageInfoDTO ArrayOfPersonCoverageInfoDTO = new  HUMVerifyFamilyDatacontract.ArrayOfPersonCoverageInfoDTO();
            
            HUMVerifyFamilyDatacontract.PersonMemberInfoDTO PersonMemberInfoDTO = new  HUMVerifyFamilyDatacontract.PersonMemberInfoDTO();
            
            HUMVerifyFamilyDatacontract.GCServiceFaultBase GCServiceFaultBase = new  HUMVerifyFamilyDatacontract.GCServiceFaultBase();
            
            HUMVerifyFamilyDatacontract.GCSPersonServiceFault GCSPersonServiceFault = new  HUMVerifyFamilyDatacontract.GCSPersonServiceFault();
            
            HUMVerifyFamilyDatacontract.ArrayOfAlternateValue ArrayOfAlternateValue = new  HUMVerifyFamilyDatacontract.ArrayOfAlternateValue();
            
            HUMVerifyFamilyDatacontract.AlternateCoverageIDList AlternateCoverageIDList = new  HUMVerifyFamilyDatacontract.AlternateCoverageIDList();
            
            HUMVerifyFamilyDatacontract.AlternateValue AlternateValue = new  HUMVerifyFamilyDatacontract.AlternateValue();
            
            HUMVerifyFamilyDatacontract.OIDemographicsOutputDTO OIDemographicsOutputDTO = new  HUMVerifyFamilyDatacontract.OIDemographicsOutputDTO();
            
            HUMVerifyFamilyDatacontract.OIDemographicsInputDTO OIDemographicsInputDTO = new  HUMVerifyFamilyDatacontract.OIDemographicsInputDTO();
            
            test.stopTest();
         }
    } 
 }
/*****************************************************************************************
Apex class Name    : HUMVerifyFamilyCOBServiceTest
Version            : 1.0
Function           : Test class for HUMVerifyFamilyCOBService: Person Service
Created Date       : 02/16/2015

Modification Log:
*   Developer                   Code Review     Date                Description
* ------------------------------------------------------------------------------------------------------------------------------                
*   Pradeep Veeravali           20976           02/16/2015          Original Version
* Saurabh Kaple              162071                 01/10/2018		   REQ - 333069 - Genkey conversion- updates to person service request
* Rajesh CG                  176180                 01/25/2018		   REQ - 350788 - Convert Address Verification on HumVerifyFamilyServiceController_R1 to Asynch																																				
******************************************************************************************/
@isTest
private class HUMVerifyFamilyCOBServiceTest
{
  /*
    * Method Name   :    COBServiceTestMethod
    * Description   :    This method is to test & have code coverage for HUMVerifyFamilyCOBService.
    * Return Type   :    void
    * Parameters    :    NA
    */
    static testMethod void COBServiceTestMethod()
    {
        HUMTestDataHelperTest.getHUMConstantsData();
        //Cipher Cloud Custom Setting Data
        insert new HUMCipherCloudSettings__c(Name = 'APPLICATION_NAME',Value__c = 'CRMS TESTING');
        //Create User
        User oUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        oUser.Current_Queue__c = 'Market HO Quality Operations Compliance';
        insert oUser;
        System.runAs(oUser)
        {
            // This causes a fake response to be generated
            Test.setMock(WebServiceMock.class, new HUMVerifyFamilyCOBServiceMockTest());
            Long memberGenKey = 1;
			String enterpriseID = '1';
            Continuation cont = new Continuation(60);
            test.startTest();
            
            HUMVerifyFamilyCOBService verifyFamily = new HUMVerifyFamilyCOBService();
            HUMVerifyFamilyCOBService.GetLastVerifiedDates_element getLastVerified = new HUMVerifyFamilyCOBService.GetLastVerifiedDates_element();
            HUMVerifyFamilyCOBService.GetLastVerifiedDatesResponse_element getKLastVerifiedResponse = new HUMVerifyFamilyCOBService.GetLastVerifiedDatesResponse_element();
            HUMVerifyFamilyCOBService.GetMemberInformation_element getMemberInfo = new HUMVerifyFamilyCOBService.GetMemberInformation_element();
            HUMVerifyFamilyCOBService.GetMemberInformationResponse_element memberInfo = new HUMVerifyFamilyCOBService.GetMemberInformationResponse_element();
            HUMVerifyFamilyCOBService.UpdateDemographics_element updateDemographics = new HUMVerifyFamilyCOBService.UpdateDemographics_element();
            HUMVerifyFamilyCOBService.UpdateDemographicsResponse_element updateDemographicsRes = new HUMVerifyFamilyCOBService.UpdateDemographicsResponse_element();
            HUMVerifyFamilyCOBService.UpdateDemographicsResponse_elementFuture updateAsyncDemographicsRes = new HUMVerifyFamilyCOBService.UpdateDemographicsResponse_elementFuture();
			
            HUMVerifyFamilyCOBService.GCSPersonServiceFault GCSPerFault = new HUMVerifyFamilyCOBService.GCSPersonServiceFault();
            HUMVerifyFamilyCOBService.GCServiceFaultBase GCSSerFaultBase = new HUMVerifyFamilyCOBService.GCServiceFaultBase(); 
            HUMVerifyFamilyCOBService.PersonMemberInfoDTO PerMemInfoDTO = new HUMVerifyFamilyCOBService.PersonMemberInfoDTO();
            HUMVerifyFamilyCOBService.ArrayOfPersonCoverageInfoDTO ArrofPerCovInfoDTO = new HUMVerifyFamilyCOBService.ArrayOfPersonCoverageInfoDTO();
            HUMVerifyFamilyCOBService.ArrayOfPersonMemberInfoDTO ArrPerMemInfoDTO = new HUMVerifyFamilyCOBService.ArrayOfPersonMemberInfoDTO();
            HUMVerifyFamilyCOBService.UpdatedMemberCoverageDetailsDTO UpMemCovDetailsDTO = new HUMVerifyFamilyCOBService.UpdatedMemberCoverageDetailsDTO();
            HUMVerifyFamilyCOBService.UpdatedMemberAddressDetailsDTO UpMemAddDetailsDTO = new HUMVerifyFamilyCOBService.UpdatedMemberAddressDetailsDTO();
            HUMVerifyFamilyCOBService.GCSValidationErrors GSCValErrors = new HUMVerifyFamilyCOBService.GCSValidationErrors();
            HUMVerifyFamilyCOBService.ArrayOfGCSValidationErrors ArrofGCSValErrors = new HUMVerifyFamilyCOBService.ArrayOfGCSValidationErrors();
            HUMVerifyFamilyCOBService.PersonCoverageInfoDTO PerCovInfoDTO = new HUMVerifyFamilyCOBService.PersonCoverageInfoDTO();
            HUMVerifyFamilyCOBService.UpdatedMemberResidentialAddressDTO UpMemResAddDTO = new HUMVerifyFamilyCOBService.UpdatedMemberResidentialAddressDTO(); 
            HUMVerifyFamilyCOBService.ArrayOfUpdatedMemberCoverageDetailsDTO ArrofUpMemCovDetailsDTO = new HUMVerifyFamilyCOBService.ArrayOfUpdatedMemberCoverageDetailsDTO();
            HUMVerifyFamilyCOBService.ArrayOfUpdatedMemberAddressDetailsDTO ArrofUpMemAddDetailsDTO = new HUMVerifyFamilyCOBService.ArrayOfUpdatedMemberAddressDetailsDTO();
            
            HUMVerifyFamilyCOBService.soap soap = new HUMVerifyFamilyCOBService.soap();
            HUMVerifyFamilyCOBService.PersonDemographicsLastVerifiedInfoDTO personDemographics = new HUMVerifyFamilyCOBService.PersonDemographicsLastVerifiedInfoDTO();
            HUMVerifyFamilyCOBService.UpdatedMemberDemographicsDetailsDTO demographicsInputDTO = new HUMVerifyFamilyCOBService.UpdatedMemberDemographicsDetailsDTO();
            String demographicsUpdated = soap.UpdateDemographics('', personDemographics, '', '', demographicsInputDTO);
            system.assert(demographicsUpdated != null);
            
            HUMVerifyFamilyCOBService.OIDemographicsInputDTO oiDemographicDatesDTO = new HUMVerifyFamilyCOBService.OIDemographicsInputDTO();
            HUMVerifyFamilyCOBService.OIDemographicsOutputDTO oiDemOutput = soap.GetLastVerifiedDates(oiDemographicDatesDTO);
            system.assert(oiDemOutput != null);
            
			HUMVerifyFamilyCOBService.ArrayOfMemberDTO arrMemDTO = soap.GetMemberInformation(enterpriseID,memberGenKey);
            system.assert(arrMemDTO != null);
            //Asynchronous callout
            HUMVerifyFamilyCOBService.OIDemographicsInputDTO oiAsyncDemographicDatesDTO = new HUMVerifyFamilyCOBService.OIDemographicsInputDTO();            
            cont.continuationMethod = 'HUMVerifyFamilyServiceController_R1.processGetLastVerifyDates';
            HUMVerifyFamilyCOBService.GetLastVerifiedDatesResponse_elementFuture oiAsyncDemOutput = soap.beginGetLastVerifiedDates(cont,oiAsyncDemographicDatesDTO);
            system.assert(oiAsyncDemOutput != null);
            cont.continuationMethod = 'HUMVerifyFamilyServiceController_R1.processverifyDemographics';
            HUMVerifyFamilyCOBService.UpdateDemographicsResponse_elementFuture updateAsyncDemographicsResonse = soap.beginUpdateDemographics(cont,'', personDemographics, '', '', demographicsInputDTO);
            system.assert(updateAsyncDemographicsResonse != null);  
            String updateDemographicsResult;
            try
            {
                updateDemographicsResult = updateAsyncDemographicsRes.getValue();
            }
            catch(Exception ex)
            {               
                System.assert(updateDemographicsResult == null);    
            }
            test.stopTest();
        }
    }  
}
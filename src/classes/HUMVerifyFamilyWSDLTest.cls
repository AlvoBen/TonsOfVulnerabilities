/*****************************************************************************************
Apex class Name    : HUMVerifyFamilyWSDLTest
Version            : 1.0
Function           : Controller for Task List View page to display list of Case/Task
Created Date       : 08/05/2014

Modification Log:
*   Developer                   Code Review     Date               Description
* ------------------------------------------------------------------------------------------------------------------------------                
*   Manish Kumar                 17076               08/05/2014          Original Version
******************************************************************************************/
@isTest
private class HUMVerifyFamilyWSDLTest
{
    /*
    * Method Name   :    overallTestMethod
    * Description   :    This method is used for overall testing for HUMTaskListViewController.
    * Return Type   :    void
    * Parameters    :    NA
    */
    static testMethod void overallTestMethod(){
        User thisUser = [ select Id from User where Id =: UserInfo.getUserId() ];
        
        System.runAs ( thisUser )
        {
            // This causes a fake response to be generated
            Test.setMock(WebServiceMock.class, new HUMVerifyFamilyWSDLMockImplHelper());
            
            Long memberGenKey = 1;
            
            test.startTest();
            
            HUMVerifyFamilyWSDL verifyFamily = new HUMVerifyFamilyWSDL();
            HUMVerifyFamilyWSDL.GetLastVerifiedDates_element getLastVerified = new HUMVerifyFamilyWSDL.GetLastVerifiedDates_element();
            HUMVerifyFamilyWSDL.GetLastVerifiedDatesResponse_element getKLastVerifiedResponse = new HUMVerifyFamilyWSDL.GetLastVerifiedDatesResponse_element();
            HUMVerifyFamilyWSDL.GetMemberInformation_element getMemberInfo = new HUMVerifyFamilyWSDL.GetMemberInformation_element();
            HUMVerifyFamilyWSDL.GetMemberInformationResponse_element memberInfo = new HUMVerifyFamilyWSDL.GetMemberInformationResponse_element();
            HUMVerifyFamilyWSDL.UpdateDemographics_element updateDemographics = new HUMVerifyFamilyWSDL.UpdateDemographics_element();
            HUMVerifyFamilyWSDL.UpdateDemographicsResponse_element updateDemographicsRes = new HUMVerifyFamilyWSDL.UpdateDemographicsResponse_element();
            
            HUMVerifyFamilyWSDL.soap soap = new HUMVerifyFamilyWSDL.soap();
            
            HUMVerifyFamilyDatacontract.PersonDemographicsLastVerifiedInfoDTO personDemographics = new HUMVerifyFamilyDatacontract.PersonDemographicsLastVerifiedInfoDTO();
            HUMVerifyFamilyDatacontract.UpdatedMemberDemographicsDetailsDTO demographicsInputDTO = new HUMVerifyFamilyDatacontract.UpdatedMemberDemographicsDetailsDTO();
            
            soap.UpdateDemographics('', personDemographics, '', '', demographicsInputDTO);
            
            HUMVerifyFamilyDatacontract.OIDemographicsInputDTO oiDemographicDatesDTO = new HUMVerifyFamilyDatacontract.OIDemographicsInputDTO();
            
            soap.GetLastVerifiedDates(oiDemographicDatesDTO);
            
            soap.GetMemberInformation(memberGenKey);
            
            test.stopTest();
        }
    }
}
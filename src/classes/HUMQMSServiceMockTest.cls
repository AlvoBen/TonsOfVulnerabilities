/****************************************************************************
Apex Class Name  : HUMQMSServiceMockTest 
Version          : 1.0 
Created On       : 08/14/2014
Function         : Mock Test Class for HUMChangeCaseOwnerControllerTest Class. It Generates mock response for QMS Service.
                   Covered in HUMChangeCaseOwnerControllerTest 

Modification Log :
* Developer Name          Code Review                        Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Sagar Tapadia                                              08/14/2014                  Original Version
***********************************************************************************************************************************/
@isTest
global class HUMQMSServiceMockTest implements WebServiceMock
{
    /*
     * Method Name : doInvoke
     * Description : Test method to create QMS Service Response
     * Return type : Void
     * Paramater   : NA
     */
    global void doInvoke(
           Object stub,
           Object request,
           Map<String, Object> response,
           String endpoint,
           String soapAction,
           String requestName,
           String responseNS,
           String responseName,
           String responseType) 
           {
               HUMIntQMSService.SaveReferralCRMSResponse_element respElement = new HUMIntQMSService.SaveReferralCRMSResponse_element();
               HUMIntQMSService.QualityReferralCRMResponseDataContract respContract = new HUMIntQMSService.QualityReferralCRMResponseDataContract();
               respContract.Guid='5d764bae-2a09-44f9-96a1-605a12a5e600';
               respContract.Message='Referral already exists. Additional information updated in QMS.';
               respContract.ReferralID=Integer.valueOf('18640');
               respContract.ErrorCode='';
               respContract.Status=true;
               respElement.SaveReferralCRMSResult = respContract;
               response.put('response_x', respElement); 
         }
}
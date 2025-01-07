/*******************************************************************************************************************************
Apex Class Name : PharmacyBenefits_D_HUM.cls
Version         : 1.0
Created On      : 20th June 2017
Function        : Data Class for PharmacyBenefits_C_HUM.
TestClass       : PharmacyBenefits_T_HUM

Modification Log:
* Developer Name            Code Review                Date                       Description
*----------------------------------------------------------------------------------------------------------------------------------------
* Joel George                                       06/20/2017                  Original Version (REQ 305017)
* Joel George               125710                  09/05/2017                  Changes for Req 322640
* Melkisan Selvaraj         224067                  04/24/2018                  REQ-335128 Added Event Type And Outcome Verbiage
* Lakshmi Madduri									10/30/2019					Pharmacy Mail Order section
* Riya Agarwal									    30/04/2020				    REQ - 729137 - MF 23 - Humana Pharmacy InComm- adding the hyperlink for the OTC 'Benefit Type’ field when it equals ‘RxNova’
* Sayali Nemade										   4/29/2020					REQ - 842472 - Pharmacy InComm Service V2
* Pinky Vijur                                  05/11/2021                  REQ 2230065: T1PRJ0002606 -2021 MF1- SF - Humana Constants Cleanup Humana Pharmacy
* Pinky Vijur                                        01/23/2023                User Story 3578587: T1PRJ0870026 Solutran Integrations - CRM FR5.01 - TECH - Remove CRM Service coding related to InComm OTC Balance
* Tharun Madishetti                                 03/31/2023                US-4250871:T1PRJ0891742- CRM IOP-2022-6037203-- Pharmacy Authorization Details: add Department,Coverage Start Date & Coverage End Date(Classic)
*****************************************************************************************************************************************/

public with sharing class PharmacyBenefits_D_HUM 
{
    Public Static final Integer NUMBER_CONSTANT_ONE_HUM = 1; 
    Public Static final String Humana_Member_Id = 'HumanaMemberId';
   
    
    /**
    * Returns Policy_Member__C object that can then be utilized for showing Policy member Information.  
    * Exception Is handled form the calling method  
    * This method returns SOQL result of policy member.
    *
    * @param    sPolMemId       policy member ID.
    * @return  Policy_Member__c    return the policy member object
    */
    public Policy_Member__C getPolicyMemberData(String sPolMemId)
    {
        return[Select Id,  Name, Member__r.Enterprise_Id__c , Member__r.Name, Member__r.Id, Member__r.birthdate__c, Policy_Member_External_ID__c,Product__c, Policy_Platform__c, Product_Type_Code__c from  Policy_Member__c Where Id = :sPolMemId];
    }
    
    /**
     * Returns PriorAuthHubRequest_DTO_HUM  object that can then be utilized for sending the webservice request.
     * This method is to create the request Structure for GetMemberResponseservice .
     *
     * @param String Enterprise ID   
     * @return  PriorAuthHubRequest_DTO_HUM  request structure of the wrapper class PriorAuthHubRequest_DTO_HUM 
     */
    public static PriorAuthHubRequest_DTO_HUM RequestStructureGetpAuthResponse(String sMemID)
    {
        PriorAuthHubRequest_DTO_HUM oRequest = new PriorAuthHubRequest_DTO_HUM();
        PriorAuthHubRequest_DTO_HUM.GetPriorAuthStatusHistoryRequest pAuthRequest = new PriorAuthHubRequest_DTO_HUM.GetPriorAuthStatusHistoryRequest();
        pAuthRequest.MemberIdentity = sMemID;
        pAuthRequest.MemberIdentifier = NUMBER_CONSTANT_ONE_HUM;

        oRequest.GetPriorAuthStatusHistoryRequest = pAuthRequest;
        
        return oRequest;
    }
    

    /**
     * Returns list of PriorAuthHubWrapper for populating authorization details
     * This method is to populate authorization details from PAHub service
     *
     * @param   PriorAuthHubResponse_DTO_HUM DTO Response 
     * @return  PriorAuthHubWrapper request structure of the wrapper class PriorAuthHubRequest_DTO_HUM 
     */
    public static List<PriorAuthHubWrapper> preparePriorAuthDetails(PriorAuthHubResponse_DTO_HUM oResponseGetMember)
    {
        List<PriorAuthHubWrapper> lstResponseWrapper = new List<PriorAuthHubWrapper>();
        PriorAuthHubResponse_DTO_HUM.GetPriorAuthStatusHistoryResponse oResponse = new PriorAuthHubResponse_DTO_HUM.GetPriorAuthStatusHistoryResponse(); 
        if(oResponseGetMember != NULL && oResponseGetMember.GetPriorAuthStatusHistoryResponse != NULL)
        {
            if(oResponseGetMember.GetPriorAuthStatusHistoryResponse.PriorAuthDetails != NULL)
            {
                for(Integer i=0; i<oResponseGetMember.GetPriorAuthStatusHistoryResponse.PriorAuthDetails.size();i++)
                {
                    if(oResponseGetMember.GetPriorAuthStatusHistoryResponse.PriorAuthDetails[i].AgadiaAuthDetails != NULL) 
                    {
                        for(Integer k=0; k < oResponseGetMember.GetPriorAuthStatusHistoryResponse.PriorAuthDetails[i].AgadiaAuthDetails.size(); k++)
                        {
                            lstResponseWrapper.add(new PriorAuthHubWrapper(oResponseGetMember.GetPriorAuthStatusHistoryResponse.PriorAuthDetails[i].AgadiaAuthDetails[k].AuthDescription,oResponseGetMember.GetPriorAuthStatusHistoryResponse.PriorAuthDetails[i].AgadiaAuthDetails[k].EOCCreationDate,
                            oResponseGetMember.GetPriorAuthStatusHistoryResponse.PriorAuthDetails[i].AgadiaAuthDetails[k].EOCDecisionDate,oResponseGetMember.GetPriorAuthStatusHistoryResponse.PriorAuthDetails[i].AgadiaAuthDetails[k].DrugName,
                            oResponseGetMember.GetPriorAuthStatusHistoryResponse.PriorAuthDetails[i].AgadiaAuthDetails[k].HCSEventType,oResponseGetMember.GetPriorAuthStatusHistoryResponse.PriorAuthDetails[i].AgadiaAuthDetails[k].DenialLanguage,
                            oResponseGetMember.GetPriorAuthStatusHistoryResponse.PriorAuthDetails[i].AgadiaAuthDetails[k].DecisionRoute,oResponseGetMember.GetPriorAuthStatusHistoryResponse.PriorAuthDetails[i].AgadiaAuthDetails[k].PrescriberFirstName,
	                        oResponseGetMember.GetPriorAuthStatusHistoryResponse.PriorAuthDetails[i].AgadiaAuthDetails[k].PrescriberLastName,oResponseGetMember.GetPriorAuthStatusHistoryResponse.PriorAuthDetails[i].AgadiaAuthDetails[k].PrescriberFaxNumber,
                            oResponseGetMember.GetPriorAuthStatusHistoryResponse.PriorAuthDetails[i].AgadiaAuthDetails[k].Urgency,oResponseGetMember.GetPriorAuthStatusHistoryResponse.PriorAuthDetails[i].AgadiaAuthDetails[k].LineOfBusiness,
                            oResponseGetMember.GetPriorAuthStatusHistoryResponse.PriorAuthDetails[i].AgadiaAuthDetails[k].DrugDetails,oResponseGetMember.GetPriorAuthStatusHistoryResponse.PriorAuthDetails[i].AgadiaAuthDetails[k].Attachment));
                        }
                    }
                }
            }
        }
        return lstResponseWrapper;
    }

}
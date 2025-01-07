/*******************************************************************************************************************************
Apex Class Name : PharmacyBenefits_LD_HUM.cls
Version         : 1.0
Created On      : 10/14/2021
Function        : Class contains methods for services to fetch Benefits Information.
TestClass       : PharmacyBenefits_LT_HUM

Modification Log:
* Developer Name            Code Review                Date                       Description
*-------------------------------------------------------------------------------------------------------------------------------
* Nirmal Garg											10/14/2021				  Initial Version - US26489489 - Adding for Pharmacy lightning
* Jonathan Dickinson                                    10/20/2021                Added methods from non-lightning classes 
*                                                                                 And modified them to work with the New lightning classes
* M K Manoj                                             11/04/2022               US-3578373 And US-3578548
* Pinky Vijur                                           01/23/2023                User Story 3578587: T1PRJ0870026 Solutran Integrations - CRM FR5.01 - TECH - Remove CRM Service coding related to InComm OTC Balance
* Pinky Vijur                                           02/03/2023                User Story 4222287: T1PRJ0870026 - INC2137452 Solutran Integrations - TECH - Request with Partial Plan Information
* Pinky Vijur						                    04/03/2023				  User Story 4401068: C04; Authorization/Referral Verification; LIGHTNING--T1PRJ0891742-CRM IOP-2022-6037203—Pharmacy Authorization Details: add ‘Department’, ‘Coverage Start Date’ & ‘Coverage End Date’
* Monali Jagtap											07/17/2023 				  USER STORY 4812246 T1PRJ0891415 Platform Management-  SF - TECH - Critical- Lightning – Pharmacy
*****************************************************************************************************************************************/
public with sharing class PharmacyBenefits_LD_HUM 
{
	Public Static final Integer NUMBER_CONSTANT_ONE_HUM = 1; 
    Public Static final String Humana_Member_Id = 'HumanaMemberId';
	Public static Integer limitValue = Integer.valueOf(system.label.iPaginator);
    public static Boolean Generic_Security_Switch = HUMUtilityHelper.isCRMFunctionalityON('Generic_Security_Switch');
    public static List<PriorAuthHubWrapper> lstResponseWrapper;
    public static PriorAuthHubResponse_DTO_HUM.GetPriorAuthStatusHistoryResponse oResponse;
    
    /**
     * Returns PriorAuthHubRequest_DTO_HUM  object that can then be utilized for sending the webservice request.
     * This method is to create the request Structure for GetMemberResponseservice .
     *
     * @param String Enterprise ID   
     * @return  PriorAuthHubRequest_DTO_HUM  request structure of the wrapper class PriorAuthHubRequest_DTO_HUM 
     */
    @AuraEnabled(continuation=true cacheable=true)
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
        lstResponseWrapper=prepareAuthDetails_Sec(oResponseGetMember);
	    return lstResponseWrapper;
    }
   
    public static List<PriorAuthHubWrapper> prepareAuthDetails_Sec(PriorAuthHubResponse_DTO_HUM oResponseGetMember)
    {
        lstResponseWrapper = new List<PriorAuthHubWrapper>();
        oResponseGetMember=(oResponseGetMember != NULL)?oResponseGetMember:null;
        oResponse = new PriorAuthHubResponse_DTO_HUM.GetPriorAuthStatusHistoryResponse(); 
        oResponse=(oResponseGetMember.GetPriorAuthStatusHistoryResponse != NULL)?oResponseGetMember.GetPriorAuthStatusHistoryResponse:null;
  		oResponse.PriorAuthDetails= (oResponseGetMember.GetPriorAuthStatusHistoryResponse.PriorAuthDetails != NULL)?oResponse.PriorAuthDetails:null;   
        
        for(Integer i=0; i<oResponseGetMember.GetPriorAuthStatusHistoryResponse.PriorAuthDetails.size();i++)
        {
            oResponse.PriorAuthDetails[i].AgadiaAuthDetails=(oResponse.PriorAuthDetails[i].AgadiaAuthDetails!=null)?oResponse.PriorAuthDetails[i].AgadiaAuthDetails:null;
                 for(Integer k=0; k < oResponseGetMember.GetPriorAuthStatusHistoryResponse.PriorAuthDetails[i].AgadiaAuthDetails.size(); k++)
                {
                         lstResponseWrapper.add(new PriorAuthHubWrapper(oResponseGetMember.GetPriorAuthStatusHistoryResponse.PriorAuthDetails[i].AgadiaAuthDetails[k].AuthDescription,
                                                                           oResponseGetMember.GetPriorAuthStatusHistoryResponse.PriorAuthDetails[i].AgadiaAuthDetails[k].EOCCreationDate,
                            											   oResponseGetMember.GetPriorAuthStatusHistoryResponse.PriorAuthDetails[i].AgadiaAuthDetails[k].EOCDecisionDate,
                                                                           oResponseGetMember.GetPriorAuthStatusHistoryResponse.PriorAuthDetails[i].AgadiaAuthDetails[k].DrugName,
                                                                            oResponseGetMember.GetPriorAuthStatusHistoryResponse.PriorAuthDetails[i].AgadiaAuthDetails[k].HCSEventType,
                                                                            oResponseGetMember.GetPriorAuthStatusHistoryResponse.PriorAuthDetails[i].AgadiaAuthDetails[k].DenialLanguage,
                                                                            oResponseGetMember.GetPriorAuthStatusHistoryResponse.PriorAuthDetails[i].AgadiaAuthDetails[k].DecisionRoute,
                                                                            oResponseGetMember.GetPriorAuthStatusHistoryResponse.PriorAuthDetails[i].AgadiaAuthDetails[k].PrescriberFirstName,
                                                                            oResponseGetMember.GetPriorAuthStatusHistoryResponse.PriorAuthDetails[i].AgadiaAuthDetails[k].PrescriberLastName,
                                                                            oResponseGetMember.GetPriorAuthStatusHistoryResponse.PriorAuthDetails[i].AgadiaAuthDetails[k].PrescriberFaxNumber,
                                                                            oResponseGetMember.GetPriorAuthStatusHistoryResponse.PriorAuthDetails[i].AgadiaAuthDetails[k].Urgency,
                                                                            oResponseGetMember.GetPriorAuthStatusHistoryResponse.PriorAuthDetails[i].AgadiaAuthDetails[k].LineOfBusiness,
                                                                            oResponseGetMember.GetPriorAuthStatusHistoryResponse.PriorAuthDetails[i].AgadiaAuthDetails[k].DrugDetails,
                                                                            oResponseGetMember.GetPriorAuthStatusHistoryResponse.PriorAuthDetails[i].AgadiaAuthDetails[k].Attachment));

                        }
                    }
        return lstResponseWrapper;
    }
    
    public static List<MemberPlan> getPolicyMemberData(string sAccId){
        return [SELECT id, Name, Plan.Name,Plan.iab_description__c, Policy_Platform__c, Product__c, OSB_Indicator__c, GroupNumber,
        Product_Type__c, EffectiveFrom, EffectiveTo , ASO__c, Display_Group_Name__c, EHB_Term_Date__c, Member_Coverage_Status__c,
        Member.MedicareId__c,Member_Dependent_Code__c, Member_Id_Base__c, Product_Type_Code__c,ETL_Record_Deleted__c,
        Policy__r.PBP_Code__c, Policy__r.Contract_Number__c, Policy__r.Product_Description__c, Member.Birthdate__c
        FROM MemberPlan WHERE MemberId = :sAccId order by Member_Coverage_Status__c asc,Product__c asc LIMIT: limitValue];
    }
	
	/**
     * Returns PharmacyBenefitsSoltrnRequest_H_HUM  object that can then be utilized for sending the webservice request.
     * This method is to create the request Structure for PharmacyBenefits_SolutranOTC_V3.
     *
     * @param String sMemberid  ,sPBPCode,sContractNumber,sProductDescription
     * @return  PharmacyBenefitsSoltrnRequest_H_HUM request structure of the wrapper class PharmacyBenefitsSoltrnRequest_H_HUM
     */
    
    public static PharmacyBenefitsSoltrnRequest_H_HUM requestStructureGetSoltrnRequestV3(String sMemberid, String sPBPCode, string sContractNumber, string sProductDescription)
    { 
        PharmacyBenefitsSoltrnRequest_H_HUM oRequest = new PharmacyBenefitsSoltrnRequest_H_HUM();
        oRequest.MemberIdentityQualifier = Humana_Member_Id;
        oRequest.MemberIdentity = sMemberid;
        if(string.isNotBlank(sPBPCode) && string.isNotBlank(sContractNumber) && string.isNotBlank(sProductDescription))
        {
            PharmacyBenefitsSoltrnRequest_H_HUM.PlanInformation oPlanInformation = new PharmacyBenefitsSoltrnRequest_H_HUM.PlanInformation();
            oPlanInformation.ContractId = sContractNumber;
            oPlanInformation.PlanNumber = sPBPCode;
            oPlanInformation.PlanOptionNumber =sProductDescription;
            oRequest.PlanInformation = new list<PharmacyBenefitsSoltrnRequest_H_HUM.PlanInformation>();
            oRequest.PlanInformation.add(oPlanInformation);
        }
        return oRequest;
    }
    

}
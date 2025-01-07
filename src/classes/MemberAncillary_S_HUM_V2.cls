/*******************************************************************************************************************************************************
Apex Class Name  : MemberAncillary_S_HUM_V2
Version          : 1.0
Created Date     : 11/04/2019
Function         : This class is Service class for Member Ancilliary
Test Class       : MemberAncillary_S_T_HUM_V2
Modification Log :

* Developer Name            Review Number           Date                       Description
-----------------------------------------------------------------------------------------------------------------------------------------------------
*  Manikanta                356372                 05/31/2019             Original Version - REQ - 379006 Abitlity to Display Medicaid information from CI in CRM Service
*  Manikanta                                       06/07/2019             REQ - 379006 Made code changes to align with the DP changes
*  Manikanta                                       06/26/2019             REQ - 379006 Defect Fix 424421.0001
*  Yogesh Gupta                                    11/04/2019             V2 Version - REQ -403709 MemberAncillary_S_HUM_V2 class for Ancillary Service
*  Yogesh Gupta								       11/27/2019			  Defect - 450407 Response structure changes for State KY 
*  Keerti Durbha                                   08/20/2020             US-1114331- PR00094035 - MF 02 � Adding three New fields for Medicaid Policy Details section for �SC�
*  Subrahmanyam N                                  27/01/2021              REQ - 1896703  CALL_MF05_FR01: KY MMC 2021_The CRM system must display PriorMedicaidID & PriorMedicaidBeginDate
*  Ajay Chakradhar                                 05/05/2021	          REQ - 2184294 - Replacing constants with Consumer Management Constants
*  Akash Surwase                                    04/27/2022                 User Story 2444097: T1PRJ0054938 MF09 - PLT - CRM Must Display New Fields in Medicaid Details Section  for OH MCD
*  Akash Surwase                                    05/23/2022                 User Story 3085055: T1PRJ0054938 - FR09.07 CRM Service must display the following fields on the Medicaid policy detail page for OH Medicaid
*  Akash Surwase                                    06/06/2022              DF-4947  fix for US-3085055
*  Akash Surwase                                    02/16/2023            User Story 4042809: T1PRJ0881713- FR09.07 v2 - TECH - CRM Service must display the OH Rise Indicator from the MAS Team on the Medicaid policy detail page for OH Medicaid (Classic)
************************************************************************************************************************************************/
public with sharing class MemberAncillary_S_HUM_V2
{

    public  MemberAncillaryResponse_DTO_HUM_V2  ancillaryDetails;
    Boolean bFirstName, bAddressLine1, bState;

    /*
    *  This method is used to build request body for Member Ancillary Service
    *  @name  assignValuesToAncillaryReq 
    *  @param IEntityTypeList - List of entity types, oPolMem - Policy member details, sMemIdBase - Member id, sDependentCode - Member dependent code
        IEntityTypeList - Removed from Parameter because no more used - REQ - 403709
    *  @return MemberAncillaryRequest_H_HUM_V2 - wrapper class for building request    
    */
  
    public MemberAncillaryRequest_H_HUM_V2 assignValuesToAncillaryReq( Policy_member__c oPolMem, String sMemIdBase, String sDependentCode)
    {    
        String sLVPlatForm =  ConsumerManagementConstants_HUM.VOB_PLATFORM_LV_HUM;
        String sEMPlatForm =  ConsumerManagementConstants_HUM.VOB_PLATFORM_EM_HUM;
        
        try
        {     
            MemberAncillaryRequest_H_HUM_V2 oAncillaryReq= new MemberAncillaryRequest_H_HUM_V2();
            MemberAncillaryRequest_H_HUM_V2.getMemberAncillary ogetmember = new MemberAncillaryRequest_H_HUM_V2.getMemberAncillary();
            MemberAncillaryRequest_H_HUM_V2.includes oIncludes = new MemberAncillaryRequest_H_HUM_V2.includes();
                        
            MemberAncillaryRequest_H_HUM_V2.ancillaryRequest oancillaryrequest = new MemberAncillaryRequest_H_HUM_V2.ancillaryRequest();
            MemberAncillaryRequest_H_HUM_V2.memberIdentifier oCoverageIdentifier = new MemberAncillaryRequest_H_HUM_V2.memberIdentifier();
            List<MemberAncillaryRequest_H_HUM_V2.includes> listOfIncludes = new List<MemberAncillaryRequest_H_HUM_V2.includes>();
            MemberAncillaryRequest_H_HUM_V2.Criteria oCriteria = new MemberAncillaryRequest_H_HUM_V2.Criteria();
            
            //Request check for KY
            if(oPolMem.Issue_State__c  == label.Ancillary_State_Code_KY)
            {
                oincludes.ICostShareInformation = true;
                oincludes.IMedicaidManagedCareDetails = true;
                oincludes.ICommunityEngagementExemptCode = true;
                oincludes.IDeductibleAmount = true;
                oincludes.ITypeOfAssistance = true;
                oincludes.IPremium = true;
                oincludes.IMedicallyFrail = true;
                oincludes.IRewards = true;
                oincludes.IJuvenilleJustice = true;
                oincludes.IPenaltyReasonIndicator = true;
                oincludes.ISuspensionReasonIndicator = true;
                oincludes.IHospiceIndicator = true;
                oincludes.IFosterCareIndicator = true;
                oincludes.IGuardianshipIndicator = true;
                oincludes.IHeadOfHousehold = true;
                oincludes.ILTSS = true;
            
               oincludes.IBehavioralHealthCode = oincludes.IBehavioralHealthCode != null ? oincludes.IBehavioralHealthCode : false;
               oincludes.ICapitationCode = oincludes.ICapitationCode != null ? oincludes.ICapitationCode : false;
               oincludes.ICaseTypeCode = oincludes.ICaseTypeCode != null ? oincludes.ICaseTypeCode : false;
               oincludes.IChishlmCase = oincludes.IChishlmCase != null ? oincludes.IChishlmCase : false;
               oincludes.IChoiceCode = oincludes.IChoiceCode != null ? oincludes.IChoiceCode : false;
               oincludes.IClosureCode = oincludes.IClosureCode != null ? oincludes.IClosureCode : false;
               oincludes.ICoverageEndDate = oincludes.ICoverageEndDate != null ? oincludes.ICoverageEndDate : false;
               oincludes.IDepartmentOfCorrection = oincludes.IDepartmentOfCorrection != null ? oincludes.IDepartmentOfCorrection : false;
               oincludes.IHighRiskPregancyCode = oincludes.IHighRiskPregancyCode != null ? oincludes.IHighRiskPregancyCode : false;
               oincludes.IMedicalAssistanceCategoryCode = oincludes.IMedicalAssistanceCategoryCode != null ? oincludes.IMedicalAssistanceCategoryCode : false;
               oincludes.IMultipleBirthIndicator = oincludes.IMultipleBirthIndicator != null ? oincludes.IMultipleBirthIndicator : false;
               oincludes.INewBornMothersReferenceId = oincludes.INewBornMothersReferenceId != null ? oincludes.INewBornMothersReferenceId : false;
               oincludes.IRenewalCode = oincludes.IRenewalCode != null ? oincludes.IRenewalCode : false;
               oincludes.IRenewalDate = oincludes.IRenewalDate != null ? oincludes.IRenewalDate : false;
               oincludes.ICoordinatedSystemOfCare = oincludes.ICoordinatedSystemOfCare != null ? oincludes.ICoordinatedSystemOfCare : false;
               oincludes.IHandicapIndicator = oincludes.IHandicapIndicator != null ? oincludes.IHandicapIndicator : false;
               oincludes.IMaintenanceReason = oincludes.IMaintenanceReason != null ? oincludes.IMaintenanceReason : false; 
               oincludes.IReviewDetails = oincludes.IReviewDetails != null ? oincludes.IReviewDetails : false;
       		   oincludes.IChoiseReasonCd = oincludes.IChoiseReasonCd != null ? oincludes.IChoiseReasonCd : false;
               oincludes.IParentInfoIdentifier = oincludes.IParentInfoIdentifier != null ? oincludes.IParentInfoIdentifier : false;
            }
            //Request check for LA
            else if(oPolMem.Issue_State__c == label.Ancillary_State_Code_LA)
            {
                oincludes.IBehavioralHealthCode = true;
                oincludes.ICapitationCode = true;
                oincludes.ICaseTypeCode = true;
                oincludes.IChishlmCase = true;
                oincludes.IChoiceCode = true;
                oincludes.IClosureCode = true;
                oincludes.ICoverageEndDate = true;
                oincludes.IDepartmentOfCorrection = true;
                oincludes.IHighRiskPregancyCode = true;
                oincludes.IMedicalAssistanceCategoryCode = true;
                oincludes.IMultipleBirthIndicator= true;
                oincludes.INewBornMothersReferenceId = true;
                oincludes.IRenewalCode = true;
                oincludes.IRenewalDate = true;
                oincludes.ICoordinatedSystemOfCare = true;
                oincludes.IMaintenanceReason = true;
                oincludes.IHandicapIndicator = true;
            
                oincludes.ICostShareInformation = oincludes.ICostShareInformation != null ? oincludes.ICostShareInformation : false;
                oincludes.ICommunityEngagementExemptCode = oincludes.ICommunityEngagementExemptCode != null ? oincludes.ICommunityEngagementExemptCode : false;
                oincludes.IDeductibleAmount = oincludes.IDeductibleAmount != null ? oincludes.IDeductibleAmount : false;   
                oincludes.ITypeOfAssistance = oincludes.ITypeOfAssistance != null ? oincludes.ITypeOfAssistance : false;   
                oincludes.IPremium = oincludes.IPremium != null ? oincludes.IPremium : false;   
                oincludes.IMedicallyFrail = oincludes.IMedicallyFrail != null ? oincludes.IMedicallyFrail : false;   
                oincludes.IRewards = oincludes.IRewards != null ? oincludes.IRewards : false;   
                oincludes.IJuvenilleJustice = oincludes.IJuvenilleJustice != null ? oincludes.IJuvenilleJustice : false;   
                oincludes.IPenaltyReasonIndicator = oincludes.IPenaltyReasonIndicator != null ? oincludes.IPenaltyReasonIndicator : false;   
                oincludes.ISuspensionReasonIndicator = oincludes.ISuspensionReasonIndicator != null ? oincludes.ISuspensionReasonIndicator : false;   
                oincludes.IHospiceIndicator = oincludes.IHospiceIndicator != null ? oincludes.IHospiceIndicator : false;   
                oincludes.IFosterCareIndicator = oincludes.IFosterCareIndicator != null ? oincludes.IFosterCareIndicator : false;   
                oincludes.IGuardianshipIndicator = oincludes.IGuardianshipIndicator != null ? oincludes.IGuardianshipIndicator : false;   
                oincludes.IHeadOfHousehold = oincludes.IHeadOfHousehold != null ? oincludes.IHeadOfHousehold : false;   
                oincludes.ILTSS = oincludes.ILTSS != null ? oincludes.ILTSS : false;   
                oincludes.IReviewDetails = oincludes.IReviewDetails != null ? oincludes.IReviewDetails : false;
       		    oincludes.IChoiseReasonCd = oincludes.IChoiseReasonCd != null ? oincludes.IChoiseReasonCd : false;
                oincludes.IParentInfoIdentifier = oincludes.IParentInfoIdentifier != null ? oincludes.IParentInfoIdentifier : false;
                oincludes.IMedicaidManagedCareDetails = oincludes.IMedicaidManagedCareDetails != null ? oincludes.IMedicaidManagedCareDetails : false;
            }
            //Request check for SC
            else if(oPolMem.Issue_State__c == label.Ancillary_State_Code_SC)
            {
                oincludes.IReviewDetails = true;
                oincludes.IChoiseReasonCd = true;
                oincludes.IParentInfoIdentifier = true;
                
               oincludes.IBehavioralHealthCode = oincludes.IBehavioralHealthCode != null ? oincludes.IBehavioralHealthCode : false;
               oincludes.ICapitationCode = oincludes.ICapitationCode != null ? oincludes.ICapitationCode : false;
               oincludes.ICaseTypeCode = oincludes.ICaseTypeCode != null ? oincludes.ICaseTypeCode : false;
               oincludes.IChishlmCase = oincludes.IChishlmCase != null ? oincludes.IChishlmCase : false;
               oincludes.IChoiceCode = oincludes.IChoiceCode != null ? oincludes.IChoiceCode : false;
               oincludes.IClosureCode = oincludes.IClosureCode != null ? oincludes.IClosureCode : false;
               oincludes.ICoverageEndDate = oincludes.ICoverageEndDate != null ? oincludes.ICoverageEndDate : false;
               oincludes.IDepartmentOfCorrection = oincludes.IDepartmentOfCorrection != null ? oincludes.IDepartmentOfCorrection : false;
               oincludes.IHighRiskPregancyCode = oincludes.IHighRiskPregancyCode != null ? oincludes.IHighRiskPregancyCode : false;
               oincludes.IMedicalAssistanceCategoryCode = oincludes.IMedicalAssistanceCategoryCode != null ? oincludes.IMedicalAssistanceCategoryCode : false;
               oincludes.IMultipleBirthIndicator = oincludes.IMultipleBirthIndicator != null ? oincludes.IMultipleBirthIndicator : false;
               oincludes.INewBornMothersReferenceId = oincludes.INewBornMothersReferenceId != null ? oincludes.INewBornMothersReferenceId : false;
               oincludes.IRenewalCode = oincludes.IRenewalCode != null ? oincludes.IRenewalCode : false;
               oincludes.IRenewalDate = oincludes.IRenewalDate != null ? oincludes.IRenewalDate : false;
               oincludes.ICoordinatedSystemOfCare = oincludes.ICoordinatedSystemOfCare != null ? oincludes.ICoordinatedSystemOfCare : false;
               oincludes.IHandicapIndicator = oincludes.IHandicapIndicator != null ? oincludes.IHandicapIndicator : false;
               oincludes.IMaintenanceReason = oincludes.IMaintenanceReason != null ? oincludes.IMaintenanceReason : false;
               oincludes.ICostShareInformation = oincludes.ICostShareInformation != null ? oincludes.ICostShareInformation : false;
               oincludes.ICommunityEngagementExemptCode = oincludes.ICommunityEngagementExemptCode != null ? oincludes.ICommunityEngagementExemptCode : false;
               oincludes.IDeductibleAmount = oincludes.IDeductibleAmount != null ? oincludes.IDeductibleAmount : false;   
               oincludes.ITypeOfAssistance = oincludes.ITypeOfAssistance != null ? oincludes.ITypeOfAssistance : false;   
               oincludes.IPremium = oincludes.IPremium != null ? oincludes.IPremium : false;   
               oincludes.IMedicallyFrail = oincludes.IMedicallyFrail != null ? oincludes.IMedicallyFrail : false;   
               oincludes.IRewards = oincludes.IRewards != null ? oincludes.IRewards : false;   
               oincludes.IJuvenilleJustice = oincludes.IJuvenilleJustice != null ? oincludes.IJuvenilleJustice : false;   
               oincludes.IPenaltyReasonIndicator = oincludes.IPenaltyReasonIndicator != null ? oincludes.IPenaltyReasonIndicator : false;   
               oincludes.ISuspensionReasonIndicator = oincludes.ISuspensionReasonIndicator != null ? oincludes.ISuspensionReasonIndicator : false;   
               oincludes.IHospiceIndicator = oincludes.IHospiceIndicator != null ? oincludes.IHospiceIndicator : false;   
               oincludes.IFosterCareIndicator = oincludes.IFosterCareIndicator != null ? oincludes.IFosterCareIndicator : false;   
               oincludes.IGuardianshipIndicator = oincludes.IGuardianshipIndicator != null ? oincludes.IGuardianshipIndicator : false;   
               oincludes.IHeadOfHousehold = oincludes.IHeadOfHousehold != null ? oincludes.IHeadOfHousehold : false;   
               oincludes.ILTSS = oincludes.ILTSS != null ? oincludes.ILTSS : false;
            }
            //Request check for OH
            else if(oPolMem.Issue_State__c == label.Ancillary_State_Code_OH && HUMUtilityHelper.isCRMFunctionalityON('2444097'))
            {
                oincludes.IRenewalDate = true;
                oincludes.IParentInfoIdentifier = true;
                oincludes.IRiseDetails=true;  //change relted to US-3085055(OhioRise)
            }
            listOfIncludes.add(oincludes);
            oCriteria.sequenceId = 1;
            if(sMemIdBase != null && String.isNotBlank(sMemIdBase)) oCoverageIdentifier.MemberIdBase =sMemIdBase;
            if(sDependentCode != null && String.isNotBlank(sDependentCode)) oCoverageIdentifier.DependentCode = sDependentCode;
            if(oPolMem.Policy__r.Group_Name__r.Source_Customer_Key__c!=null) oCoverageIdentifier.DivisionId = oPolMem.Policy__r.Group_Name__r.Source_Customer_Key__c;
            if(oPolMem.Policy_Platform__c!=null) oCoverageIdentifier.PlatformCode = oPolMem.Policy_Platform__c;
            if(oPolMem.Policy__r.Group_Name__r.Source_Customer_Key__c!=null) oCoverageIdentifier.GroupId = oPolMem.Policy__r.Group_Name__r.Source_Customer_Key__c;
            if(oPolMem.Effective_Date__c!=null) oCoverageIdentifier.CoverageStartDate = oPolMem.Effective_Date__c;
            if(oPolMem.Policy__r.Product__r.Sold_Product_Key_Value__c!=null) oCoverageIdentifier.ProductId = oPolMem.Policy__r.Product__r.Sold_Product_Key_Value__c;
         
            oCriteria.memberIdentifier = oCoverageIdentifier;
            Ancillary_State_Code__c ancillaryStateCode= Ancillary_State_Code__c.getValues(oPolMem.Issue_State__c);
            if (ancillaryStateCode != null){
                 oCriteria.stateCode = ancillaryStateCode.name;
            }
            oancillaryrequest.includes = listOfIncludes;
            oancillaryrequest.Criteria = oCriteria;
            ogetmember.ancillaryRequest = oancillaryrequest;
            oAncillaryReq.getMemberAncillary = ogetmember;
            return oAncillaryReq; 
         }
         catch(Exception ex)
         {            
            HUMExceptionHelper.logErrors(ex, 'MemberAncillary_S_HUM_V2', 'assignValuesToAncillaryReq');
            return null;
         }      
    }
  
        
    /*
    *  This method is used to generate the request
    *  @name  setupAncillaryRequest
    *  @param oRequest - Empty service request, oSvcAncillary - Ancillary service details, sJsonRequest - Json service request
    *  @return HttpRequest
    */
    
    public HttpRequest setupAncillaryRequest(HttpRequest oRequest, HUM_Webservice_Callout__c oSvcAncillary, String sJsonRequest)
    {  
        try
        {            
            Integer iTIMEOUT_INT_SECS = Integer.valueof(oSvcAncillary.Timeout__c)/1000;
            oRequest.setMethod(oSvcAncillary.Request_Type__c); 
            oRequest.setEndpoint(oSvcAncillary.End_Point_URL__c);
            oRequest.setHeader(GlobalCommonConstants_HUM.CONTENT_TYPE_HUM, oSvcAncillary.Content_Type__c);   
            oRequest.setBody(sJsonRequest);             
            if(!Test.isRunningTest()) oRequest.setClientCertificateName(oSvcAncillary.Certificate_Name__c);
            oRequest.setTimeout(iTIMEOUT_INT_SECS);
            return oRequest;
         }
         catch(Exception ex)
         {            
            HUMExceptionHelper.logErrors(ex, 'MemberAncillary_S_HUM_V2', 'setupAncillaryRequest');
            return null;
         }  
    }
    
   
    /*
    *  This method is used to parse the Member Ancillary service reponse
    *  @name  processResponse
    *  @param response - Ancillary service response
    *  @return MemberAncillaryResponse_DTO_HUM_V2 - wrapper class to store data from service
    */
   
    public MemberAncillaryResponse_DTO_HUM_V2 processResponse(string response)  
    {
        try
        {  
            MemberAncillaryResponse_H_HUM_V2  objRestResp = new MemberAncillaryResponse_H_HUM_V2 ();
            //Deserialize the response from MemberAncillaryResponse_H_HUM_V2 class to map fields
            objRestResp = (MemberAncillaryResponse_H_HUM_V2)System.JSON.deserialize(response, MemberAncillaryResponse_H_HUM_V2.class); 
            //When we dont reponse from servce we get node like "getMemberAncillaryResponse=getMemberAncillaryResponse:[AncillaryEntityList=null]" , So we have put a null check to avoid the error log entry
            if(objRestResp.getMemberAncillaryResponse != null)
            {
                MemberAncillaryResponse_H_HUM_V2.getMemberAncillaryResponse oAncillaryResponse = objRestResp.getMemberAncillaryResponse;
                MemberAncillaryResponse_H_HUM_V2.postalAddress oPostalAddress;
                if(oAncillaryResponse.AncillaryEntityList !=null) {
                    List<MemberAncillaryResponse_H_HUM_V2.AncillaryEntityList> oAncillaryEntityList  = oAncillaryResponse.AncillaryEntityList;
					if(oAncillaryEntityList != null && oAncillaryEntityList.size() > 0) {
					
						//Defect - 450407 Response structure changes for State KY 
						MemberAncillaryResponse_H_HUM_V2.PenaltyReasonIndicator oPenaltyReasonIndicator = oAncillaryEntityList[0].PenaltyReasonIndicator;
						MemberAncillaryResponse_H_HUM_V2.Premium oPremium = oAncillaryEntityList[0].Premium; 
						
						MemberAncillaryResponse_H_HUM_V2.HeadofHousehold oHeadofHousehold = oAncillaryEntityList[0].HeadofHousehold;
						MemberAncillaryResponse_H_HUM_V2.LTSS oLTSS = oAncillaryEntityList[0].LTSS;
                        MemberAncillaryResponse_H_HUM_V2.MedicaidManagedCareDetails oMedicaidManagedCareDetails = oAncillaryEntityList[0].MedicaidManagedCareDetails;
						MemberAncillaryResponse_H_HUM_V2.CoordinatedSystemOfCare oCoordinatedSystemOfCare  = oAncillaryEntityList[0].CoordinatedSystemOfCare;
						MemberAncillaryResponse_H_HUM_V2.ChishlmCase oChishlmCase  = oAncillaryEntityList[0].ChishlmCase;
						MemberAncillaryResponse_H_HUM_V2.DepartmentOfCorrection oDepartmentOfCorrection= oAncillaryEntityList[0].DepartmentOfCorrection;
                        MemberAncillaryResponse_H_HUM_V2.ReviewDetails oReviewDetails= oAncillaryEntityList[0].ReviewDetails;
                        MemberAncillaryResponse_H_HUM_V2.ChoiseReasonCd oChoiseReasonCd= oAncillaryEntityList[0].ChoiseReasonCd;
                        MemberAncillaryResponse_H_HUM_V2.ParentInfoIdentifier oParentInfoIdentifier= oAncillaryEntityList[0].ParentInfoIdentifier;
                        MemberAncillaryResponse_H_HUM_V2.RiseDetails oOhioRiseobj= oAncillaryEntityList[0].RiseDetails; //This change is related with US-3085055
						if(oHeadofHousehold != null) oPostalAddress = oHeadofHousehold.postalAddress;
	                    ancillaryDetails = buildAncillaryWrapperList(oHeadofHousehold, oPostalAddress, oChishlmCase, oLTSS, oCoordinatedSystemOfCare , oDepartmentOfCorrection , oAncillaryEntityList, oPenaltyReasonIndicator, oPremium, oReviewDetails, oChoiseReasonCd, oParentInfoIdentifier, oMedicaidManagedCareDetails,oOhioRiseobj);// change related to US-3085055(OhioRise)
                    }
                }
             }
             return ancillaryDetails;
         }
         catch(Exception ex)
         {     
             HUMExceptionHelper.logErrors(ex, 'MemberAncillary_S_HUM_V2', 'processResponse');
             return null;
         }        
     }
    
    /*
    *  This method is used to parse the Member Ancillary service reponse
    *  @name  buildAncillaryWrapperList
    *  @param MemberAncillaryResponse_H_HUM_V2.memberIdentifier oCoverageIdentifier, MemberAncillaryResponse_H_HUM_V2.HeadofHousehold oHeadofHousehold, MemberAncillaryResponse_H_HUM_V2.postalAddress oPostalAddress, MemberAncillaryResponse_H_HUM_V2.LTSS oLTSS, List<MemberAncillaryResponse_H_HUM_V2.EntityTypeList> oEntityTypeList, List<MemberAncillaryResponse_H_HUM_V2.AncillaryEntityList> oAncillaryEntityList
        
    *  @return MemberAncillaryResponse_DTO_HUM_V2 - wrapper class to store data from service
    */
  
    @testvisible private MemberAncillaryResponse_DTO_HUM_V2 buildAncillaryWrapperList(MemberAncillaryResponse_H_HUM_V2.HeadofHousehold oHeadofHousehold, MemberAncillaryResponse_H_HUM_V2.postalAddress oPostalAddress, MemberAncillaryResponse_H_HUM_V2.ChishlmCase oChishlmCase, MemberAncillaryResponse_H_HUM_V2.LTSS oLTSS, MemberAncillaryResponse_H_HUM_V2.CoordinatedSystemOfCare oCoordinatedSystemOfCare,MemberAncillaryResponse_H_HUM_V2.DepartmentOfCorrection oDepartmentOfCorrection , List<MemberAncillaryResponse_H_HUM_V2.AncillaryEntityList> oAncillaryEntityList, MemberAncillaryResponse_H_HUM_V2.PenaltyReasonIndicator oPenaltyReasonIndicator, MemberAncillaryResponse_H_HUM_V2.Premium oPremium,MemberAncillaryResponse_H_HUM_V2.ReviewDetails oReviewDetails, MemberAncillaryResponse_H_HUM_V2.ChoiseReasonCd oChoiseReasonCd, MemberAncillaryResponse_H_HUM_V2.ParentInfoIdentifier oParentInfoIdentifier, MemberAncillaryResponse_H_HUM_V2.MedicaidManagedCareDetails oMedicaidManagedCareDetails,MemberAncillaryResponse_H_HUM_V2.RiseDetails  oOhioRiseobj)//change related to US-3085055(OhioRise)
    {
        try
        {
            MemberAncillaryResponse_DTO_HUM_V2 objWrapperMediacid = new MemberAncillaryResponse_DTO_HUM_V2 ();
            if(oAncillaryEntityList[0].CostShareInformation!= null)  objWrapperMediacid.CostShareInformation= oAncillaryEntityList[0].CostShareInformation;
            if(oAncillaryEntityList[0].CommunityEngagementExemptCode!= null)  objWrapperMediacid.CommunityEngagementExemptCode= oAncillaryEntityList[0].CommunityEngagementExemptCode;
            if(oAncillaryEntityList[0].TypeOfAssistance!= null)  objWrapperMediacid.TypeOfAssistance= oAncillaryEntityList[0].TypeOfAssistance;
            if(oAncillaryEntityList[0].DeductibleAmount!= null)  objWrapperMediacid.DeductibleAmount= oAncillaryEntityList[0].DeductibleAmount;
            
            //REQ-403709 Assigning variable in wrapper list
            if(oAncillaryEntityList[0].MedicallyFrail!= null)  objWrapperMediacid.MedicallyFrail= oAncillaryEntityList[0].MedicallyFrail;
            if(oAncillaryEntityList[0].Rewards!= null)  objWrapperMediacid.Rewards = oAncillaryEntityList[0].Rewards;
            if(oAncillaryEntityList[0].JuvenilleJustice!= null)  objWrapperMediacid.JuvenilleJustice= oAncillaryEntityList[0].JuvenilleJustice;
            if(oAncillaryEntityList[0].SuspensionReasonIndicator!= null)  objWrapperMediacid.SuspensionReasonIndicator = oAncillaryEntityList[0].SuspensionReasonIndicator;
            if(oAncillaryEntityList[0].HospiceIndicator!= null)  objWrapperMediacid.HospiceIndicator = oAncillaryEntityList[0].HospiceIndicator ;
            if(oAncillaryEntityList[0].FosterCareIndicator!= null)  objWrapperMediacid.ForesterCareIndicator = oAncillaryEntityList[0].FosterCareIndicator;
            if(oAncillaryEntityList[0].GuardianshipIndicator!= null)  objWrapperMediacid.GuardianshipIndicator= oAncillaryEntityList[0].GuardianshipIndicator;
            if(oAncillaryEntityList[0].CoverageEndDate!= null)  objWrapperMediacid.CoverageEndDate = oAncillaryEntityList[0].CoverageEndDate;
            if(oAncillaryEntityList[0].CapitationCode!= null)  objWrapperMediacid.CapitationCode= oAncillaryEntityList[0].CapitationCode;
            if(oAncillaryEntityList[0].ChoiceCode!= null)  objWrapperMediacid.ChoiceCode = oAncillaryEntityList[0].ChoiceCode;
            if(oAncillaryEntityList[0].HighRiskPregancyCode!= null)  objWrapperMediacid.HighRiskPregancyCode= oAncillaryEntityList[0].HighRiskPregancyCode;
            if(oAncillaryEntityList[0].BehavioralHealthCode!= null)  objWrapperMediacid.BehavioralHealthCode= oAncillaryEntityList[0].BehavioralHealthCode;
            if(oAncillaryEntityList[0].ClosureCode!= null)  objWrapperMediacid.ClosureCode = oAncillaryEntityList[0].ClosureCode;
            if(oAncillaryEntityList[0].RenewalDate!= null)  objWrapperMediacid.RenewalDate = oAncillaryEntityList[0].RenewalDate;
            if(oAncillaryEntityList[0].RenewalCode!= null)  objWrapperMediacid.RenewalCode= oAncillaryEntityList[0].RenewalCode;
            if(oAncillaryEntityList[0].MultipleBirthIndicator!= null)  objWrapperMediacid.MultipleBirthIndicator= oAncillaryEntityList[0].MultipleBirthIndicator;
            if(oAncillaryEntityList[0].MedicalAssistanceCategoryCode!= null)  objWrapperMediacid.MedicalAssistanceCategoryCode= oAncillaryEntityList[0].MedicalAssistanceCategoryCode;
            if(oAncillaryEntityList[0].NewBornMothersReferenceId!= null)  objWrapperMediacid.NewBornMothersReferenceId = oAncillaryEntityList[0].NewBornMothersReferenceId;
            if(oAncillaryEntityList[0].CaseTypeCode!= null)  objWrapperMediacid.CaseTypeCode= oAncillaryEntityList[0].CaseTypeCode;
            if(oAncillaryEntityList[0].MaintenanceReason!= null)  objWrapperMediacid.MaintenanceReason= oAncillaryEntityList[0].MaintenanceReason;
            if(oAncillaryEntityList[0].HandicapIndicator!= null)  objWrapperMediacid.HandicapIndicator= oAncillaryEntityList[0].HandicapIndicator;
            if(oHeadofHousehold!= null) {
                if(oHeadofHousehold.FirstName!= null && String.isNotBlank(oHeadofHousehold.FirstName))  objWrapperMediacid.FirstName = oHeadofHousehold.FirstName;
                if(oHeadofHousehold.LastName!= null && String.isNotBlank(oHeadofHousehold.LastName))  objWrapperMediacid.LastName= oHeadofHousehold.LastName;
                if(objWrapperMediacid.FirstName!= null && String.isNotBlank(objWrapperMediacid.FirstName))
                {
                    bFirstName = True;
                    objWrapperMediacid.fullName = objWrapperMediacid.FirstName;
                }
                if(objWrapperMediacid.LastName!= null && String.isNotBlank(objWrapperMediacid.LastName) && bFirstName)
                {
                    objWrapperMediacid.fullName = objWrapperMediacid.FirstName + ' ' + objWrapperMediacid.LastName;
                }
                else if(objWrapperMediacid.LastName!= null && String.isNotBlank(objWrapperMediacid.LastName))
                {
                    objWrapperMediacid.fullName = objWrapperMediacid.LastName;
                }
            }
            
            if(oHeadofHousehold!= null) {
                if(oPostalAddress.AddressLine1!= null && String.isNotBlank(oPostalAddress.AddressLine1)) objWrapperMediacid.AddressLine1= oPostalAddress.AddressLine1;
                if(oPostalAddress.AddressLine2!= null && String.isNotBlank(oPostalAddress.AddressLine2)) objWrapperMediacid.AddressLine2 = oPostalAddress.AddressLine2;
                if(oPostalAddress.City!= null && String.isNotBlank(oPostalAddress.City)) objWrapperMediacid.City = oPostalAddress.City;
                if(oPostalAddress.State!= null && String.isNotBlank(oPostalAddress.State)) objWrapperMediacid.State = oPostalAddress.State;
                if(oPostalAddress.Zip!= null && String.isNotBlank(oPostalAddress.Zip)) objWrapperMediacid.Zip = oPostalAddress.Zip; 
                if(objWrapperMediacid.AddressLine1!= null && String.isNotBlank(objWrapperMediacid.AddressLine1)) 
                {
                    bAddressLine1 = True;
                    objWrapperMediacid.fullAddress = objWrapperMediacid.AddressLine1;
                }
                if(objWrapperMediacid.AddressLine2!= null && String.isNotBlank(objWrapperMediacid.AddressLine2) && bAddressLine1)
                {
                    objWrapperMediacid.fullAddress = objWrapperMediacid.AddressLine1 + ' ' + objWrapperMediacid.AddressLine2;
                }
                else if(objWrapperMediacid.AddressLine2!= null && String.isNotBlank(objWrapperMediacid.AddressLine2))
                {
                    objWrapperMediacid.fullAddress = objWrapperMediacid.AddressLine2;
                }
                if(objWrapperMediacid.State!= null && String.isNotBlank(objWrapperMediacid.State)) 
                {
                    bState = True;
                    objWrapperMediacid.fullAddress = objWrapperMediacid.fullAddress + ',' + ' ' + objWrapperMediacid.State;
                }            
                if(objWrapperMediacid.Zip!= null && String.isNotBlank(objWrapperMediacid.Zip) && bState)
                {
                    objWrapperMediacid.fullAddress = objWrapperMediacid.fullAddress + ' ' +objWrapperMediacid.Zip;
                }
                else if(objWrapperMediacid.Zip!= null && String.isNotBlank(objWrapperMediacid.Zip))
                {
                    objWrapperMediacid.fullAddress = objWrapperMediacid.fullAddress + ',' + ' ' + objWrapperMediacid.Zip;
                }
            }
            
            if(oLTSS != null) {
                if(oLTSS.SpecialCond!= null && String.isNotBlank(oLTSS.SpecialCond))  objWrapperMediacid.SpecialCond= oLTSS.SpecialCond;
                if(oLTSS.ProdPlanId!= null && String.isNotBlank(oLTSS.ProdPlanId))  objWrapperMediacid.ProdPlanId= oLTSS.ProdPlanId;
                if(oLTSS.LevelOfCareDate!= null)  objWrapperMediacid.LevelOfCareDate= oLTSS.LevelOfCareDate;
                if(oLTSS.LevelOfCareCode!= null && String.isNotBlank(oLTSS.LevelOfCareCode))  objWrapperMediacid.LevelOfCareCode= oLTSS.LevelOfCareCode;
                if(oLTSS.SpecialNeedsNote!= null && String.isNotBlank(oLTSS.SpecialNeedsNote))  objWrapperMediacid.SpecialNeedsNote= oLTSS.SpecialNeedsNote;
                if(oLTSS.MedicaidAidCategoryCode!= null && String.isNotBlank(oLTSS.MedicaidAidCategoryCode))  objWrapperMediacid.MedicaidAidCategoryCode= oLTSS.MedicaidAidCategoryCode;
                if(oLTSS.MedicaidBeginDate!= null)  objWrapperMediacid.MedicaidBeginDate= oLTSS.MedicaidBeginDate;
                if(oLTSS.MedicaidEndDate!= null)  objWrapperMediacid.MedicaidEndDate= oLTSS.MedicaidEndDate;
                if(oLTSS.ACWM!= null && String.isNotBlank(oLTSS.ACWM))  objWrapperMediacid.ACWM= oLTSS.ACWM;
                if(oLTSS.otherHealthIndicators!= null && String.isNotBlank(oLTSS.otherHealthIndicators))  objWrapperMediacid.OtherHealthIndicators = oLTSS.otherHealthIndicators;
                if(oLTSS.ltcProgramRecommendation!= null && String.isNotBlank(oLTSS.ltcProgramRecommendation))  objWrapperMediacid.LtcProgramRecommendation = oLTSS.ltcProgramRecommendation;
                if(oLTSS.providerCode!= null && String.isNotBlank(oLTSS.providerCode))  objWrapperMediacid.ProviderCode= oLTSS.providerCode;
            }
            if(oMedicaidManagedCareDetails != null)
            {
            if(oMedicaidManagedCareDetails.priorMedicaidid!= null && String.isNotBlank(oMedicaidManagedCareDetails.priorMedicaidid))  objWrapperMediacid.priorMedicaidid= oMedicaidManagedCareDetails.priorMedicaidid;
            if(oMedicaidManagedCareDetails.priorMedicaidBeginDate!= null && String.isNotBlank(oMedicaidManagedCareDetails.priorMedicaidBeginDate))  objWrapperMediacid.priorMedicaidBeginDate= oMedicaidManagedCareDetails.priorMedicaidBeginDate;   
            }
            
            if(oChishlmCase != null) {
                if(oChishlmCase.startDate!= null && String.isNotBlank(oChishlmCase.startDate))  objWrapperMediacid.StartDate= oChishlmCase.startDate;
                if(oChishlmCase.endDate!= null && String.isNotBlank(oChishlmCase.endDate))  objWrapperMediacid.EndDate= oChishlmCase.endDate;
                if(oChishlmCase.manager!= null && String.isNotBlank(oChishlmCase.manager))  objWrapperMediacid.Manager= oChishlmCase.manager;
            }
            
            if(oCoordinatedSystemOfCare!= null) {
                if(oCoordinatedSystemOfCare.AdmitDate!= null && String.isNotBlank(oCoordinatedSystemOfCare.AdmitDate)) objWrapperMediacid.AdmitDate= oCoordinatedSystemOfCare.AdmitDate;
                if(oCoordinatedSystemOfCare.dischargeDate!= null && String.isNotBlank(oCoordinatedSystemOfCare.dischargeDate)) objWrapperMediacid.dischargeDate= oCoordinatedSystemOfCare.dischargeDate;
            } 
            
            if(oDepartmentOfCorrection!= null) {
            
                if(oDepartmentOfCorrection.type!= null && String.isNotBlank(oDepartmentOfCorrection.type))  objWrapperMediacid.Doctype= oDepartmentOfCorrection.type;
                if(oDepartmentOfCorrection.startDate!= null && String.isNotBlank(oDepartmentOfCorrection.startDate))  objWrapperMediacid.DocstartDate= oDepartmentOfCorrection.startDate;
                if(oDepartmentOfCorrection.endDate!= null && String.isNotBlank(oDepartmentOfCorrection.endDate))  objWrapperMediacid.DocendDate= oDepartmentOfCorrection.endDate;
                if(oDepartmentOfCorrection.preReleaseDate!= null && String.isNotBlank(oDepartmentOfCorrection.preReleaseDate))  objWrapperMediacid.DocpreReleaseDate= oDepartmentOfCorrection.preReleaseDate;
                if(oDepartmentOfCorrection.locationIndicator!= null && String.isNotBlank(oDepartmentOfCorrection.locationIndicator))  objWrapperMediacid.DoclocationIndicator= oDepartmentOfCorrection.locationIndicator;
            }
			//Defect - 450407 Response structure changes for State KY so assigning values to display on UI
			if(oPenaltyReasonIndicator != null){
            	 if(oPenaltyReasonIndicator.penaltyValue != null && String.isNotBlank(oPenaltyReasonIndicator.penaltyValue))  
            	 	objWrapperMediacid.penaltyValue= oPenaltyReasonIndicator.penaltyValue;
            	 if(oPenaltyReasonIndicator.penaltyStartDate != null && String.isNotBlank(oPenaltyReasonIndicator.penaltyStartDate))  
            	 	objWrapperMediacid.penaltyStartDate= oPenaltyReasonIndicator.penaltyStartDate;
            	 if(oPenaltyReasonIndicator.penaltyEndDate != null && String.isNotBlank(oPenaltyReasonIndicator.penaltyEndDate))  
            	 	objWrapperMediacid.penaltyEndDate= oPenaltyReasonIndicator.penaltyEndDate;
            }
            if(oPremium != null){
            	 if(oPremium.premiumValue != null && String.isNotBlank(oPremium.premiumValue))  
            	 	objWrapperMediacid.premiumValue= oPremium.premiumValue;
            	 if(oPremium.premiumStartDate != null && String.isNotBlank(oPremium.premiumStartDate))  
            	 	objWrapperMediacid.premiumStartDate= oPremium.premiumStartDate;
            	 if(oPremium.premiumEndDate != null && String.isNotBlank(oPremium.premiumEndDate))  
            	 	objWrapperMediacid.premiumEndDate= oPremium.premiumEndDate;
            }
             if(oReviewDetails != null){
                if(oReviewDetails.ReviewDate != null && String.isNotBlank(oReviewDetails.ReviewDate))  
            	 	objWrapperMediacid.ReviewDate= oReviewDetails.ReviewDate;
            }
            if(oChoiseReasonCd != null){
                if(oChoiseReasonCd.ChoiseValue != null && String.isNotBlank(oChoiseReasonCd.ChoiseValue))  
            	 	objWrapperMediacid.ChoiseValue= oChoiseReasonCd.ChoiseValue;
            }
            if(oParentInfoIdentifier != null){
                if(oParentInfoIdentifier.MotherMedicaidID != null && String.isNotBlank(oParentInfoIdentifier.MotherMedicaidID))  
            	 	objWrapperMediacid.MotherMedicaidID= oParentInfoIdentifier.MotherMedicaidID;
            }
           if(oOhioRiseobj != null  && HUMUtilityHelper.isCRMFunctionalityON('3085055'))  //Changes related to US-3085055(OhioRise) starts
			{               
                objWrapperMediacid.OhioRISEBeginDate = (oOhioRiseobj.BeginDate != null && String.isNotBlank(oOhioRiseobj.BeginDate)) ? oOhioRiseobj.BeginDate : '' ; 
                objWrapperMediacid.OhioRISEEndDate = (oOhioRiseobj.EndDate != null && String.isNotBlank(oOhioRiseobj.EndDate)) ? oOhioRiseobj.EndDate : '' ;                   
                
                if(HUMUtilityHelper.isCRMFunctionalityON('4042809'))
                {
                    objWrapperMediacid.Indvalue=(oOhioRiseobj.indicator != null && String.isNotBlank(oOhioRiseobj.indicator)) ? oOhioRiseobj.indicator : 'N' ;
                }
                else
                {
                    	objWrapperMediacid.Indvalue = 'N';
                 	 	if(oOhioRiseobj.BeginDate != null && String.isNotBlank(oOhioRiseobj.BeginDate))  
                		{
	                     	 if((date.today() == date.parse(oOhioRiseobj.BeginDate)))  
								objWrapperMediacid.Indvalue='Y';
			    		}
               			 if((oOhioRiseobj.EndDate != null && String.isNotBlank(oOhioRiseobj.EndDate)))
                		{
                        	 if((date.today() == date.parse(oOhioRiseobj.EndDate)))
								objWrapperMediacid.Indvalue='Y';
                		}
               			if(oOhioRiseobj.BeginDate != null && String.isNotBlank(oOhioRiseobj.BeginDate) && (oOhioRiseobj.EndDate != null && String.isNotBlank(oOhioRiseobj.EndDate)))
               			{
                    		if(date.today() >= date.parse(oOhioRiseobj.BeginDate) && date.today()<= date.parse(oOhioRiseobj.EndDate))
								objWrapperMediacid.Indvalue='Y';
                		}  
                }

            }    //Changes related to US-3085055 ends here
            bFirstName = False;
            bAddressLine1 = False;
            bState = False;
            return objWrapperMediacid ; 
        }
        catch(Exception ex)
        {            
            HUMExceptionHelper.logErrors(ex, 'MemberAncillary_S_HUM_V2', 'buildAncillaryWrapperList');
            return null;
        }    
    }
}
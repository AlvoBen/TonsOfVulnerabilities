/*******************************************************************************************************************************
Apex Class Name : MemberPlanAncillary_LS_HUM
Version         : 1.0
Created On      : 05/01/2021
Function        : This is the service layer class for Medicaid Plan Details section on the Policy Detail page
Test Class      : MemberPlanDetail_LT_HUM

Modification Log: 
* Version Number             Developer Name              Code Review                Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
*     1.0 					 Ashish Kumar 											05/01/2021
*     2.0 					 Visweswararao J 								        20/06/2023                US-4358477 T1PRJ0865978 - MF25266 - C13; Plan Management: OHRise indicator on Medicaid Plan Member Page for OH Medicaid
*  	  3.0					 Tharun Madishetti								        02/28/2024			      US-5479030 & US-5480355 : T1PRJ1132745 - MF 28387 - C13; Consumer - Oklahoma MCD Lightning - Display fields on the Medicaid Policy Details Section
******************************************************************************************************************************************************************************/

public with sharing class MemberPlanAncillary_LS_HUM
{

    public static MemberAncillaryResponse_DTO_HUM_V2  ancillaryDetails;
    static Boolean bFirstName, bAddressLine1, bState;

    /*
    *  This method is used to build request body for Member Ancillary Service
    *  @name  assignValuesToAncillaryReq 
    *  @param IEntityTypeList - List of entity types, oPolMem - Policy member details, sMemIdBase - Member id, sDependentCode - Member dependent code
        IEntityTypeList - Removed from Parameter because no more used - REQ - 403709
    *  @return MemberAncillaryRequest_H_HUM_V2 - wrapper class for building request    
    */
  
    public static MemberAncillaryRequest_H_HUM_V2 assignValuesToAncillaryReq(MemberPlan oPolMem, String sMemIdBase, String sDependentCode)
    {    
        String sLVPlatForm =  GLOBAL_MEMBERPLAN_CONSTANT_LH_HUM.VOB_PLATFORM_LV_HUM;
        String sEMPlatForm =  GLOBAL_MEMBERPLAN_CONSTANT_LH_HUM.VOB_PLATFORM_EM_HUM;
        
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
            else if(oPolMem.Issue_State__c == label.Ancillary_State_Code_OH )
            {
                oincludes.IRenewalDate = true;
                oincludes.IParentInfoIdentifier = true;
                oincludes.IRiseDetails=true;  //change relted to (OhioRise)
            }
            else if(oPolMem.Issue_State__c == label.Ancillary_State_Code_OK ) //changes for OK State
            {
                oincludes.IRenewalDate = true;
                oincludes.IAIANIndicator = true;
                oincludes.ICaseHead = true;
                oincludes.IPregnancyInfo = true;
                oincludes.ICustodyDetails = true;
            } 
            listOfIncludes.add(oincludes);
            oCriteria.sequenceId = 1;
            
            if(sMemIdBase != null && String.isNotBlank(sMemIdBase)) oCoverageIdentifier.MemberIdBase =sMemIdBase;
            if(sDependentCode != null && String.isNotBlank(sDependentCode)) oCoverageIdentifier.DependentCode = sDependentCode;
            if(oPolMem.Plan.Payer.Source_Customer_Key__c!=null) oCoverageIdentifier.DivisionId = oPolMem.Plan.Payer.Source_Customer_Key__c;
            if(oPolMem.Policy_Platform__c!=null) oCoverageIdentifier.PlatformCode = oPolMem.Policy_Platform__c;
            if(oPolMem.Plan.Payer.Source_Customer_Key__c!=null) oCoverageIdentifier.GroupId = oPolMem.Plan.Payer.Source_Customer_Key__c;
           	
            //Changed to effectivefrom(MemberPlan) from Previous value Effective_Date__c (Policy member)
            if(oPolMem.effectiveFrom !=null) oCoverageIdentifier.CoverageStartDate = UserAssociatedInformation_LD_HUM.setDateToString(oPolMem.effectiveFrom);
            if(oPolMem.Plan.Product__r.Sold_Product_Key_Value__c!=null) oCoverageIdentifier.ProductId = oPolMem.Plan.Product__r.Sold_Product_Key_Value__c;
             
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
            HUMExceptionHelper.logErrors(ex, 'MemberPlanAncillary_LS_HUM', 'assignValuesToAncillaryReq');
            return null;
         }      
    }
  
        
    /*
    *  This method is used to generate the request
    *  @name  setupAncillaryRequest
    *  @param oRequest - Empty service request, oSvcAncillary - Ancillary service details, sJsonRequest - Json service request
    *  @return HttpRequest
    */
    
    public static HttpRequest setupAncillaryRequest(HttpRequest oRequest, HUM_Webservice_Callout__c oSvcAncillary, String sJsonRequest)
    {  
        try
        {            
            Integer iTIMEOUT_INT_SECS = Integer.valueof(oSvcAncillary.Timeout__c)/1000;
            oRequest.setMethod(oSvcAncillary.Request_Type__c); 
            oRequest.setEndpoint(oSvcAncillary.End_Point_URL__c);
            oRequest.setHeader(GLOBAL_MEMBERPLAN_CONSTANT_LH_HUM.CONTENT_TYPE_HUM, oSvcAncillary.Content_Type__c);   
            oRequest.setBody(sJsonRequest);             
            if(!Test.isRunningTest()) oRequest.setClientCertificateName(oSvcAncillary.Certificate_Name__c);
            oRequest.setTimeout(iTIMEOUT_INT_SECS);
            return oRequest;
         }
         catch(Exception ex)
         {            
            HUMExceptionHelper.logErrors(ex, 'MemberPlanAncillary_LS_HUM', 'setupAncillaryRequest');
            return null;
         }  
    }
    
   
    /*
    *  This method is used to parse the Member Ancillary service reponse
    *  @name  processResponse
    *  @param response - Ancillary service response
    *  @return MemberAncillaryResponse_DTO_HUM_V2 - wrapper class to store data from service
    */
   
    public static MemberAncillaryResponse_DTO_HUM_V2 processResponse(string response)  
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
                        MemberAncillaryResponse_H_HUM_V2.RiseDetails oRiseDetails=oAncillaryEntityList[0].RiseDetails;
                        MemberAncillaryResponse_H_HUM_V2.PregnancyInfo PregnancyInfo=oAncillaryEntityList[0].PregnancyInfo;
                        MemberAncillaryResponse_H_HUM_V2.CaseHead CaseHead=oAncillaryEntityList[0].CaseHead;
                        MemberAncillaryResponse_H_HUM_V2.CustodyDetails CustodyDetails=oAncillaryEntityList[0].CustodyDetails;
						if(oHeadofHousehold != null) oPostalAddress = oHeadofHousehold.postalAddress;
	                    ancillaryDetails = buildAncillaryWrapperList(oHeadofHousehold, oPostalAddress, oChishlmCase, oLTSS, oCoordinatedSystemOfCare , oDepartmentOfCorrection , oAncillaryEntityList, oPenaltyReasonIndicator, oPremium, oReviewDetails, oChoiseReasonCd, oParentInfoIdentifier, oMedicaidManagedCareDetails,oRiseDetails,PregnancyInfo,CaseHead,CustodyDetails);
                    }
                }
             }
             return ancillaryDetails;
         }
         catch(Exception ex)
         {     
             HUMExceptionHelper.logErrors(ex, 'MemberPlanAncillary_LS_HUM', 'processResponse');
             return null;
         }        
     }
    
    /*
    *  This method is used to parse the Member Ancillary service reponse
    *  @name  buildAncillaryWrapperList
    *  @param MemberAncillaryResponse_H_HUM_V2.memberIdentifier oCoverageIdentifier, MemberAncillaryResponse_H_HUM_V2.HeadofHousehold oHeadofHousehold, MemberAncillaryResponse_H_HUM_V2.postalAddress oPostalAddress, MemberAncillaryResponse_H_HUM_V2.LTSS oLTSS, List<MemberAncillaryResponse_H_HUM_V2.EntityTypeList> oEntityTypeList, List<MemberAncillaryResponse_H_HUM_V2.AncillaryEntityList> oAncillaryEntityList
        
    *  @return MemberAncillaryResponse_DTO_HUM_V2 - wrapper class to store data from service
    */
  
    @testvisible private static MemberAncillaryResponse_DTO_HUM_V2 buildAncillaryWrapperList(MemberAncillaryResponse_H_HUM_V2.HeadofHousehold oHeadofHousehold, MemberAncillaryResponse_H_HUM_V2.postalAddress oPostalAddress, MemberAncillaryResponse_H_HUM_V2.ChishlmCase oChishlmCase, MemberAncillaryResponse_H_HUM_V2.LTSS oLTSS, MemberAncillaryResponse_H_HUM_V2.CoordinatedSystemOfCare oCoordinatedSystemOfCare,MemberAncillaryResponse_H_HUM_V2.DepartmentOfCorrection oDepartmentOfCorrection , List<MemberAncillaryResponse_H_HUM_V2.AncillaryEntityList> oAncillaryEntityList, MemberAncillaryResponse_H_HUM_V2.PenaltyReasonIndicator oPenaltyReasonIndicator, MemberAncillaryResponse_H_HUM_V2.Premium oPremium,MemberAncillaryResponse_H_HUM_V2.ReviewDetails oReviewDetails, MemberAncillaryResponse_H_HUM_V2.ChoiseReasonCd oChoiseReasonCd, MemberAncillaryResponse_H_HUM_V2.ParentInfoIdentifier oParentInfoIdentifier, MemberAncillaryResponse_H_HUM_V2.MedicaidManagedCareDetails oMedicaidManagedCareDetails,MemberAncillaryResponse_H_HUM_V2.RiseDetails oRiseDetails,MemberAncillaryResponse_H_HUM_V2.PregnancyInfo PregnancyInfo,MemberAncillaryResponse_H_HUM_V2.CaseHead CaseHead,MemberAncillaryResponse_H_HUM_V2.CustodyDetails CustodyDetails)
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
            if(oAncillaryEntityList[0].AIANIndicator != null) objWrapperMediacid.AIANIndicatorValue = oAncillaryEntityList[0].AIANIndicator;
            if(oAncillaryEntityList[0].RenewalDate != null) objWrapperMediacid.ReviewDate_OK = oAncillaryEntityList[0].RenewalDate;
            
            
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
	    if(oRiseDetails != null )  //Changes related to US-4358477(OhioRise) starts
			{               
                objWrapperMediacid.OhioRISEBeginDate = (oRiseDetails.BeginDate != null && String.isNotBlank(oRiseDetails.BeginDate)) ? oRiseDetails.BeginDate : '' ; 
                objWrapperMediacid.OhioRISEEndDate = (oRiseDetails.EndDate != null && String.isNotBlank(oRiseDetails.EndDate)) ? oRiseDetails.EndDate : '' ;                   
                
                
                    	objWrapperMediacid.Indvalue = 'N';
                 	 	if(oRiseDetails.BeginDate != null && String.isNotBlank(oRiseDetails.BeginDate))  
                		{
	                     	 if((date.today() == date.parse(oRiseDetails.BeginDate)))  
								objWrapperMediacid.Indvalue='Y';
			    		}
               			 if((oRiseDetails.EndDate != null && String.isNotBlank(oRiseDetails.EndDate)))
                		{
                        	 if((date.today() == date.parse(oRiseDetails.EndDate)))
								objWrapperMediacid.Indvalue='Y';
                		}
               			if(oRiseDetails.BeginDate != null && String.isNotBlank(oRiseDetails.BeginDate) && (oRiseDetails.EndDate != null && String.isNotBlank(oRiseDetails.EndDate)))
               			{
                    		if(date.today() >= date.parse(oRiseDetails.BeginDate) && date.today()<= date.parse(oRiseDetails.EndDate))
								objWrapperMediacid.Indvalue='Y';
                		}  
                
            }
            if(PregnancyInfo != null){
                objWrapperMediacid.pregnancyDueDate = PregnancyInfo.pregnancyDueDate;
            }
            if(CaseHead != null){
                objWrapperMediacid.CaseHeadFirstName = CaseHead.firstName;
                objWrapperMediacid.CaseHeadLastName = CaseHead.lastName;
            }
            if(CustodyDetails != null){
                objWrapperMediacid.CustodyCode = CustodyDetails.code;
                objWrapperMediacid.CustodyDescription = CustodyDetails.description;
            }
            
            bFirstName = False;
            bAddressLine1 = False;
            bState = False;
            return objWrapperMediacid ; 
        }
        catch(Exception ex)
        {            
            HUMExceptionHelper.logErrors(ex, 'MemberPlanAncillary_LS_HUM', 'buildAncillaryWrapperList');
            return null;
        }    
    }
}
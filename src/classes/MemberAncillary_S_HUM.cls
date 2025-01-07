/*******************************************************************************************************************************************************
Apex Class Name  : MemberAncillary_S_HUM
Version          : 1.0
Created Date     : 05/31/2019
Function         : This class is Service class for Member Ancilliary
Test Class       : MemberAncillary_S_T_HUM
Modification Log :

* Developer Name            Review Number           Date                       Description
-----------------------------------------------------------------------------------------------------------------------------------------------------
*  Manikanta                356372                 05/31/2019             Original Version - REQ - 379006 Abitlity to Display Medicaid information from CI in CRM Service
*  Manikanta                                       06/07/2019             REQ - 379006 Made code changes to align with the DP changes
*  Manikanta                                       06/26/2019             REQ - 379006 Defect Fix 424421.0001
*  Satyam Malviya								   10/04/2019			  REQ - 408298- CRM Service Error Trending (SFDC)
************************************************************************************************************************************************/
public with sharing class MemberAncillary_S_HUM
{

    public  MemberAncillaryResponse_DTO_HUM  ancillaryDetails;
    Boolean bFirstName, bAddressLine1, bState;

    /*
    *  This method is used to build request body for Member Ancillary Service
    *  @name  assignValuesToAncillaryReq
    *  @param IEntityTypeList - List of entity types, oPolMem - Policy member details, sMemIdBase - Member id, sDependentCode - Member dependent code
    *  @return MemberAncillaryRequest_H_HUM - wrapper class for building request    
    */
  
    public MemberAncillaryRequest_H_HUM assignValuesToAncillaryReq(List<String> IEntityTypeList, Policy_member__c oPolMem, String sMemIdBase, String sDependentCode)
    {    
        String sLVPlatForm =  GLOBAL_CONSTANT_HUM.VOB_PLATFORM_LV_HUM;
        String sEMPlatForm =  GLOBAL_CONSTANT_HUM.VOB_PLATFORM_EM_HUM;
        
        try
        {     
            MemberAncillaryRequest_H_HUM oAncillaryReq= new MemberAncillaryRequest_H_HUM();
            MemberAncillaryRequest_H_HUM.getMemberAncillary ogetmember = new MemberAncillaryRequest_H_HUM.getMemberAncillary();                             
            MemberAncillaryRequest_H_HUM.includes oIncludes = new MemberAncillaryRequest_H_HUM.includes();
            MemberAncillaryRequest_H_HUM.IAncillaryEntity oIAncillaryEntity   = new  MemberAncillaryRequest_H_HUM.IAncillaryEntity();            
            
            MemberAncillaryRequest_H_HUM.ancillaryRequest ancillaryRequest = new MemberAncillaryRequest_H_HUM.ancillaryRequest();
            List<MemberAncillaryRequest_H_HUM.ancillaryRequest> ancillaryReqList = new  List<MemberAncillaryRequest_H_HUM.ancillaryRequest>();
            MemberAncillaryRequest_H_HUM.CoverageIdentifier CoverageIdentifier = new MemberAncillaryRequest_H_HUM.CoverageIdentifier();               
            if(sMemIdBase != null && String.isNotBlank(sMemIdBase)) CoverageIdentifier.MemberIdBase = sMemIdBase;
            if(sDependentCode != null && String.isNotBlank(sDependentCode)) CoverageIdentifier.DependentCode = sDependentCode;
            if(oPolMem.Policy__r.Group_Name__r.Source_Customer_Key__c!=null) CoverageIdentifier.DivisionId = oPolMem.Policy__r.Group_Name__r.Source_Customer_Key__c;
            if(oPolMem.Policy_Platform__c!=null) CoverageIdentifier.PlatformCode = oPolMem.Policy_Platform__c;
            if(oPolMem.Policy__r.Group_Name__r.Source_Customer_Key__c!=null) CoverageIdentifier.GroupId = oPolMem.Policy__r.Group_Name__r.Source_Customer_Key__c;
            if(oPolMem.Effective_Date__c!=null) CoverageIdentifier.CoverageStartDate = oPolMem.Effective_Date__c;
            if(oPolMem.Policy__r.Product__r.Sold_Product_Key_Value__c!=null) CoverageIdentifier.ProductId = oPolMem.Policy__r.Product__r.Sold_Product_Key_Value__c;
            ancillaryRequest.SequenceId = 1;
            ancillaryRequest.CoverageIdentifier = CoverageIdentifier; 
            ancillaryReqList.add(ancillaryRequest);
            
            oIAncillaryEntity.ICoverageIdentifier = true;
            oIAncillaryEntity.ICostShareInformation = true;
            oIAncillaryEntity.ICommunityEngagementExemptCode = true;
            oIAncillaryEntity.ITypeOfAssistance = true;
            oIAncillaryEntity.IDeductibleAmount = true;
            oIAncillaryEntity.IEntityTypeList = IEntityTypeList;
            oIAncillaryEntity.IHeadofHousehold = true;
            oIAncillaryEntity.ILTSS = true;
            
            oIncludes.IAncillaryEntity = oIAncillaryEntity;                    
            ogetmember.ancillaryRequest = AncillaryReqList;
            ogetmember.includes = oIncludes ;
            oAncillaryReq.getMemberAncillary = ogetmember;            
            return oAncillaryReq; 
         }
         catch(Exception ex)
         {            
            HUMExceptionHelper.logErrors(ex, 'MemberAncillary_S_HUM', 'assignValuesToAncillaryReq');
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
            integer iTIMEOUT_INT_SECS = Integer.valueof(oSvcAncillary.Timeout__c)/1000;
            oRequest.setMethod(oSvcAncillary.Request_Type__c); 
            oRequest.setEndpoint(oSvcAncillary.End_Point_URL__c);
            oRequest.setHeader(GLOBAL_CONSTANT_HUM.CONTENT_TYPE_HUM, oSvcAncillary.Content_Type__c);   
            oRequest.setBody(sJsonRequest);             
            If(!Test.isRunningTest()) oRequest.setClientCertificateName(oSvcAncillary.Certificate_Name__c);
            oRequest.setTimeout(iTIMEOUT_INT_SECS);
            return oRequest;
         }
         catch(Exception ex)
         {            
            HUMExceptionHelper.logErrors(ex, 'MemberAncillary_S_HUM', 'setupAncillaryRequest');
            return null;
         }  
    }
    
    /*
    *  This method is used to parse the Member Ancillary service reponse
    *  @name  processResponse
    *  @param response - Ancillary service response
    *  @return MemberAncillaryResponse_DTO_HUM - wrapper class to store data from service
    */
   
    public MemberAncillaryResponse_DTO_HUM processResponse(string response) 
    {
        try
        {    
            MemberAncillaryResponse_H_HUM  objRestResp = new MemberAncillaryResponse_H_HUM ();
            objRestResp = MemberAncillaryResponse_H_HUM.parse(response);            
            //When we dont reponse from servce we get node like "getMemberAncillaryResponse=getMemberAncillaryResponse:[AncillaryEntityList=null]" , So we have put a null check to avoid the error log entry
            if(objRestResp.getMemberAncillaryResponse != null)
            {
                MemberAncillaryResponse_H_HUM.getMemberAncillaryResponse oAncillaryResponse = objRestResp.getMemberAncillaryResponse;
                MemberAncillaryResponse_H_HUM.PostalAddress oPostalAddress;
                if(oAncillaryResponse.AncillaryEntityList !=null) {
	                List<MemberAncillaryResponse_H_HUM.AncillaryEntityList> oAncillaryEntityList  = oAncillaryResponse.AncillaryEntityList;
	                List<MemberAncillaryResponse_H_HUM.EntityTypeList> oEntityTypeList = oAncillaryEntityList[0].EntityTypeList;
	                MemberAncillaryResponse_H_HUM.HeadofHousehold oHeadofHousehold = oAncillaryEntityList[0].HeadofHousehold;
	                MemberAncillaryResponse_H_HUM.LTSS oLTSS = oAncillaryEntityList[0].LTSS;
	                MemberAncillaryResponse_H_HUM.CoverageIdentifier oCoverageIdentifier = oAncillaryEntityList[0].CoverageIdentifier;
	                if(oHeadofHousehold != null) oPostalAddress = oHeadofHousehold.PostalAddress;
	                ancillaryDetails = buildAncillaryWrapperList(oCoverageIdentifier, oHeadofHousehold, oPostalAddress, oLTSS, oEntityTypeList, oAncillaryEntityList);
                }
             }
             return ancillaryDetails;
         }
         catch(Exception ex)
         {         
             HUMExceptionHelper.logErrors(ex, 'MemberAncillary_S_HUM', 'processResponse');
             return null;
         }        
     }
    
    /*
    *  This method is used to parse the Member Ancillary service reponse
    *  @name  buildAncillaryWrapperList
    *  @param MemberAncillaryResponse_H_HUM.CoverageIdentifier oCoverageIdentifier, MemberAncillaryResponse_H_HUM.HeadofHousehold oHeadofHousehold, MemberAncillaryResponse_H_HUM.PostalAddress oPostalAddress, MemberAncillaryResponse_H_HUM.LTSS oLTSS, List<MemberAncillaryResponse_H_HUM.EntityTypeList> oEntityTypeList, List<MemberAncillaryResponse_H_HUM.AncillaryEntityList> oAncillaryEntityList
    *  @return MemberAncillaryResponse_DTO_HUM - wrapper class to store data from service
    */
  
    @testvisible private MemberAncillaryResponse_DTO_HUM buildAncillaryWrapperList(MemberAncillaryResponse_H_HUM.CoverageIdentifier oCoverageIdentifier, MemberAncillaryResponse_H_HUM.HeadofHousehold oHeadofHousehold, MemberAncillaryResponse_H_HUM.PostalAddress oPostalAddress, MemberAncillaryResponse_H_HUM.LTSS oLTSS, List<MemberAncillaryResponse_H_HUM.EntityTypeList> oEntityTypeList, List<MemberAncillaryResponse_H_HUM.AncillaryEntityList> oAncillaryEntityList)
    {
        try
        {
            MemberAncillaryResponse_DTO_HUM objWrapperMediacid = new MemberAncillaryResponse_DTO_HUM ();
            if(oCoverageIdentifier.MemberIdBase!= null && String.isNotBlank(oCoverageIdentifier.MemberIdBase))  objWrapperMediacid.MemberIdBase= oCoverageIdentifier.MemberIdBase;
            if(oCoverageIdentifier.DependentCode!= null && String.isNotBlank(oCoverageIdentifier.DependentCode))  objWrapperMediacid.DependentCode= oCoverageIdentifier.DependentCode;
            if(oCoverageIdentifier.PlatformCode!= null && String.isNotBlank(oCoverageIdentifier.PlatformCode))  objWrapperMediacid.PlatformCode= oCoverageIdentifier.PlatformCode;
            if(oCoverageIdentifier.CoverageStartDate!= null)  objWrapperMediacid.CoverageStartDate= oCoverageIdentifier.CoverageStartDate;
            if(oCoverageIdentifier.GroupId!= null && String.isNotBlank(oCoverageIdentifier.GroupId))  objWrapperMediacid.GroupId= oCoverageIdentifier.GroupId;
            if(oCoverageIdentifier.DivisionId!= null && String.isNotBlank(oCoverageIdentifier.DivisionId))  objWrapperMediacid.DivisionId= oCoverageIdentifier.DivisionId;
            if(oCoverageIdentifier.ProductId!= null && String.isNotBlank(oCoverageIdentifier.ProductId))  objWrapperMediacid.ProductId= oCoverageIdentifier.ProductId;
            if(oAncillaryEntityList[0].CostShareInformation!= null)  objWrapperMediacid.CostShareInformation= oAncillaryEntityList[0].CostShareInformation;
            if(oAncillaryEntityList[0].CommunityEngagementExemptCode!= null)  objWrapperMediacid.CommunityEngagementExemptCode= oAncillaryEntityList[0].CommunityEngagementExemptCode;
            if(oAncillaryEntityList[0].TypeOfAssistance!= null)  objWrapperMediacid.TypeOfAssistance= oAncillaryEntityList[0].TypeOfAssistance;
            if(oAncillaryEntityList[0].DeductibleAmount!= null)  objWrapperMediacid.DeductibleAmount= oAncillaryEntityList[0].DeductibleAmount;
            
            for(MemberAncillaryResponse_H_HUM.EntityTypeList obj : oEntityTypeList)
            {
                if(obj.Type == 'Premium')
                {
                    if(obj.Value!= null) objWrapperMediacid.premiumValue = obj.Value;
                    if(obj.StartDate!= null) objWrapperMediacid.premiumStartDate = obj.StartDate.formatgmt('MM/dd/yyyy');
                    if(obj.EndDate!= null) objWrapperMediacid.premiumEndDate = obj.EndDate.formatgmt('MM/dd/yyyy');
                }
                else if(obj.Type == 'PenaltyReasonIndicator')
                {
                    if(obj.Value!= null) objWrapperMediacid.penaltyValue = obj.Value;
                    if(obj.StartDate!= null) objWrapperMediacid.penaltyStartDate = obj.StartDate.formatgmt('MM/dd/yyyy');
                    if(obj.EndDate!= null) objWrapperMediacid.penaltyEndDate = obj.EndDate.formatgmt('MM/dd/yyyy');
                }
                else if(obj.Type == 'SuspensionReasonIndicator')
                {
                    if(obj.Value!= null) objWrapperMediacid.suspensionValue = obj.Value;
                }
                else if(obj.Type == 'Rewards')
                {
                    if(obj.Value!= null) objWrapperMediacid.rewardsValue = obj.Value;
                }
            }
            
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
            if(oLTSS.OtherHealthIndicators!= null && String.isNotBlank(oLTSS.OtherHealthIndicators))  objWrapperMediacid.OtherHealthIndicators= oLTSS.OtherHealthIndicators;
            if(oLTSS.SpecialCond!= null && String.isNotBlank(oLTSS.SpecialCond))  objWrapperMediacid.SpecialCond= oLTSS.SpecialCond;
            if(oLTSS.LtcProgramRecommendation!= null && String.isNotBlank(oLTSS.LtcProgramRecommendation))  objWrapperMediacid.LtcProgramRecommendation= oLTSS.LtcProgramRecommendation;
            if(oLTSS.ProdPlanId!= null && String.isNotBlank(oLTSS.ProdPlanId))  objWrapperMediacid.ProdPlanId= oLTSS.ProdPlanId;
            if(oLTSS.ProviderCode!= null && String.isNotBlank(oLTSS.ProviderCode))  objWrapperMediacid.ProviderCode= oLTSS.ProviderCode;
            if(oLTSS.LevelOfCareDate!= null)  objWrapperMediacid.LevelOfCareDate= oLTSS.LevelOfCareDate;
            if(oLTSS.LevelOfCareCode!= null && String.isNotBlank(oLTSS.LevelOfCareCode))  objWrapperMediacid.LevelOfCareCode= oLTSS.LevelOfCareCode;
            if(oLTSS.SpecialNeedsNote!= null && String.isNotBlank(oLTSS.SpecialNeedsNote))  objWrapperMediacid.SpecialNeedsNote= oLTSS.SpecialNeedsNote;
            if(oLTSS.MedicaidAidCategoryCode!= null && String.isNotBlank(oLTSS.MedicaidAidCategoryCode))  objWrapperMediacid.MedicaidAidCategoryCode= oLTSS.MedicaidAidCategoryCode;
            if(oLTSS.MedicaidBeginDate!= null)  objWrapperMediacid.MedicaidBeginDate= oLTSS.MedicaidBeginDate;
            if(oLTSS.MedicaidEndDate!= null)  objWrapperMediacid.MedicaidEndDate= oLTSS.MedicaidEndDate;
            if(oLTSS.ACWM!= null && String.isNotBlank(oLTSS.ACWM))  objWrapperMediacid.ACWM= oLTSS.ACWM;
            bFirstName = False;
            bAddressLine1 = False;
            bState = False;
            return objWrapperMediacid ; 
        }
        catch(Exception ex)
        {            
            HUMExceptionHelper.logErrors(ex, 'MemberAncillary_S_HUM', 'buildAncillaryWrapperList');
            return null;
        }    
    }
}
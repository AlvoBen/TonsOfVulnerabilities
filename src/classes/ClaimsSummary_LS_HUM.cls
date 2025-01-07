/**************************************************************************************************************************************************
Apex class Name    :  ClaimsSummary_LS_HUM
Version            :  1.0
Function           :  This is Helper class to ClaimsSummary_LC_Hum.It forms request and parse response.
Test Class         :  ClaimsSummary_LT_HUM
   Modification Log:
    
 *   Developer                   Code Review             Date               Description
 * -------------------------------------------------------------------------------------------------------------------------------------------------  
 *   Suraj Patil                                      	 06/10/2022         Original Version 
 *   Anuradha Gajbhe                                     08/10/2022         US#3587828 - Claims--Lightning--Additional Capabilities on Claims Line Items: informational Messages Claim Summary 3
************************************************************************************************************************************************* */
public with sharing class ClaimsSummary_LS_HUM
{
    Public Static final String Content_type_HUM = 'content-type';
    Public Static final String SOAPAction_HUM = 'SOAPAction';
    Public Static final String Connection_HUM = 'Connection';    
    Public Static final String Keep_alive_HUM = 'keep-alive';
    Public Static final String HYPHEN_HUM = '-';
    Public Static final String BACKSLASH_HUM = '/';
    Public Static final Integer NUMBER_CONSTANT_TWO_HUM = 2;

    private Integer pageNum = NULL;
    public  Boolean bViewResults = true ;
    public Integer iTotalRecordsFromService;
    public Integer iEndRecordFromService;
    @TestVisible private String sBody ='' ;
    @TestVisible private String sBodyGo ='' ;
    @TestVisible private List<String> lstSplitData = NULL;
    @TestVisible private String sDependentCode = '';
    private List<ClaimsSummary_Wrapper_LDTO_HUM> lstClaimRefferal = new List<ClaimsSummary_Wrapper_LDTO_HUM>();
    
   /*
    *  This is the Constructor for the class ClaimsSummary_L_S_HUM
    *  @name   ClaimsSummary_LS_HUM
    *  @param  NA
    *  @return NA
    *  @throws NA
    */ 
    public ClaimsSummary_LS_HUM()
    {
        pageNum = 1;
        bViewResults = true;
    }
    
   /*
    *  This method is used to form http request for claims summary service
    *  @name   setupRequest
    *  @param  HttpRequest - http Request for Claims summary service
               HUM_Webservice_Callout__c - custom setting which has values for web service callouts 
               string sRecId - SalesforceID of the Policy Member
               Policy_Member__C - policy member object 
    *  @return HttpRequest - request for claims summary service
    *  @throws setupRequest
    */
    public HttpRequest setupRequest(HttpRequest request,HUM_Webservice_Callout__c oSvcHeaderClaims, string sRecId ,String sStartCount,Set<String> setPolicyMembers )
    {
       HttpRequest orequest = new HttpRequest();    
        try
        { 
            sBody = buildRequest(sRecId,sStartCount,setPolicyMembers);
            if(String.isNotBlank(sBody) && sBody != null)
            {
                request.setMethod(oSvcHeaderClaims.Request_Type__c); 
                request.setHeader(CONTENT_TYPE_HUM, oSvcHeaderClaims.Content_Type__c);  
                request.setHeader(SOAPACTION_HUM, '');    
                request.setHeader(CONNECTION_HUM,KEEP_ALIVE_HUM);
                request.setBody(sBody);
                if(!Test.isRunningTest())    request.setClientCertificateName(oSvcHeaderClaims.Certificate_Name__c);    
                request.setTimeout(Integer.valueof(oSvcHeaderClaims.Timeout__c));
                request.setEndpoint(oSvcHeaderClaims.End_Point_URL__c);  
                orequest = request;
                if(Test.isRunningTest()) throw new HUMCustomException('Error');
            }
            else    orequest = null;
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'ClaimsSummary_LS_HUM', 'setupRequest');
            orequest = null;
        }
      return orequest;  
    }
    
    
    /*
    *  This method is used to form HTTP Request for Claims Summary Service Go Button
    *  @name   setupGoButtonRequest
    *  @param  HttpRequest - http Request for Claims Summary Service Go Button
               HUM_Webservice_Callout__c - Custom Setting which has values for Web Service Callouts 
               string sClaimNbr - Claim Number to search
    *  @return HttpRequest - Request for Claims Summary Service Go Button
    */
    public HttpRequest setupGoButtonRequest(HttpRequest requestGo,HUM_Webservice_Callout__c oSvcHeaderGoClaims, String sClaimNbr)
    {
       HttpRequest orequestGo = new HttpRequest();    
        try
        { 
            sBodyGo = buildRequestGoButton(sClaimNbr);
            if(String.isNotBlank(sBodyGo))
            {
                requestGo.setMethod(oSvcHeaderGoClaims.Request_Type__c); 
                requestGo.setHeader(CONTENT_TYPE_HUM, oSvcHeaderGoClaims.Content_Type__c);  
                requestGo.setHeader(SOAPACTION_HUM, '');    
                requestGo.setHeader(CONNECTION_HUM,KEEP_ALIVE_HUM);
                requestGo.setBody(sBodyGo);
                if(!Test.isRunningTest())    requestGo.setClientCertificateName(oSvcHeaderGoClaims.Certificate_Name__c);    
                requestGo.setTimeout(Integer.valueof(oSvcHeaderGoClaims.Timeout__c));
                requestGo.setEndpoint(oSvcHeaderGoClaims.End_Point_URL__c);  
                orequestGo = requestGo;
                if(Test.isRunningTest()) throw new HUMCustomException('Error');
            }
            else    orequestGo = null;
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'ClaimsSummary_LS_HUM', 'setupGoButtonRequest');
            orequestGo = null;
        }
      return orequestGo;  
    }
    /*
    *  This method is used to build the required inputs needed to hit the Claims Service Number
    *  @name   buildRequestGoButton
    *  @param  String sClaimNbr -  Claim Number to be searched
    *  @return String - Returns the Request body in Serialized form
    */
    @testvisible private string buildRequestGoButton(String sClaimNbr)
    {    
        string sJsonBodyGo = '';
        try
        {
            if(String.isNotBlank(sClaimNbr))
            { 
                ClaimsServiceSummaryRequest_LH_HUM oRequestGo = new ClaimsServiceSummaryRequest_LH_HUM();
                ClaimsServiceSummaryRequest_LH_HUM.GetClaimsSummary getClaimSumGo = new ClaimsServiceSummaryRequest_LH_HUM.GetClaimsSummary();
                List<ClaimsServiceSummaryRequest_LH_HUM.MemberRequests> lstMemberRequests = new List<ClaimsServiceSummaryRequest_LH_HUM.MemberRequests>();
                getClaimSumGo.ClaimNumber = sClaimNbr;
                getClaimSumGo.StartCount = '1';
                getClaimSumGo.MemberRequests = lstMemberRequests;    
                oRequestGo.GetClaimsSummary = getClaimSumGo;
                sJsonBodyGo = JSON.serialize(oRequestGo);
            }
            else    sJsonBodyGo =  null;
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'ClaimsSummary_LS_HUM', 'buildRequestGoButton');
            sJsonBodyGo  =  NULL;
        }
        return sJsonBodyGo ;
    }
    
   /*
    *  This method is used to build the required inputs needed to hit the Claims service
    *  @name   buildRequest
    *  @param  string sRecId -  It is the policy member id for which claims are to be displayed
               Policy_Member__c - Policy Member object 
    *  @return string - returns the request body in serialized form
    *  @throws buildRequest
    */
    @testvisible private string buildRequest(string sRecId ,String sStartCount,Set<String> setPolicyMembers )
    {    
        string sJsonbody = '';
        try
        {
            if(setPolicyMembers != null && !setPolicyMembers.isEmpty())
            { 
                ClaimsServiceSummaryRequest_LH_HUM oRequest = new ClaimsServiceSummaryRequest_LH_HUM();
                ClaimsServiceSummaryRequest_LH_HUM.GetClaimsSummary oGetClaimSummary = new ClaimsServiceSummaryRequest_LH_HUM.GetClaimsSummary();
                if(String.isNotBlank(sStartCount))
                {
                    oGetClaimSummary.StartCount = sStartCount;
                }
                else
                oGetClaimSummary.StartCount = '1';  
                List<ClaimsServiceSummaryRequest_LH_HUM.MemberRequests> lstMemberRequests = new List<ClaimsServiceSummaryRequest_LH_HUM.MemberRequests>();
                for(String oPolMem : setPolicyMembers) 
                {
                    ClaimsServiceSummaryRequest_LH_HUM.MemberRequests oMemberReq = new  ClaimsServiceSummaryRequest_LH_HUM.MemberRequests();
                    String sPolicyMemberName = oPolMem ;
                    oMemberReq.MemberId = sPolicyMemberName.substring(0,sPolicyMemberName.length()-2);
                    oMemberReq.MemberDependentCode = sPolicyMemberName.substring(sPolicyMemberName.length()-2,sPolicyMemberName.length());
                    lstMemberRequests.add(oMemberReq);
                }
                oGetClaimSummary.MemberRequests = lstMemberRequests;
                oRequest.GetClaimsSummary = oGetClaimSummary;
                sJsonbody = JSON.serialize(oRequest);
            }
            else    sJsonbody =  null;
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'ClaimsSummary_LS_HUM', 'buildRequest');
            sJsonbody =  NULL;
        }
        return sJsonbody;
    }
    
   /*
    *  This method is used to Process the Json response from service to Wrapper List
    *  @name   processResponse
    *  @param  string(Json response) - Body of the response in JSON format
    *  @return List<ClaimsSummary_Wrapper_LDTO_HUM> - WrapperList containing the response details
    *  @throws NA
    */ 
    public List<ClaimsSummary_Wrapper_LDTO_HUM> processResponse(string response) 
    {  
        ClaimsServiceSummaryResponse_LH_HUM  objresp = new ClaimsServiceSummaryResponse_LH_HUM();
        List<ClaimsSummary_Wrapper_LDTO_HUM> listClaimsResp = new List<ClaimsSummary_Wrapper_LDTO_HUM>();
        objresp = (ClaimsServiceSummaryResponse_LH_HUM )System.JSON.deserialize(response,ClaimsServiceSummaryResponse_LH_HUM.class);
        if(objresp.ClaimSummaryReponse != Null)
        {
            listClaimsResp = parseResponse(objresp);
            return listClaimsResp;
        } 
        else
        {
            return listClaimsResp;
        }
    } 
    
    
    /*
    *  This method is used to return number of  duplicate claim numbers.
    *  @name   noOnClickGO
    *  @param  NA
    *  @return Map<String,Boolean> 
    *  @throws NA
    */  
    public Integer noOnClickGO(String sResponseGo) 
    {
        ClaimsServiceSummaryResponse_LH_HUM objrespGo = new ClaimsServiceSummaryResponse_LH_HUM();
        objrespGo = (ClaimsServiceSummaryResponse_LH_HUM)System.JSON.deserialize(sResponseGo,ClaimsServiceSummaryResponse_LH_HUM.class);  
        if(objrespGo.ClaimSummaryReponse != Null)
        {
            ClaimsServiceSummaryResponse_LH_HUM.ClaimSummaryReponse oClaimResponseGo = objrespGo.ClaimSummaryReponse;
            return Integer.valueof(oClaimResponseGo.TotalRecordNumber);
        }
        else    return 0;
    }
    
   /*
    *  This method is used to assign the response to variables in wrapper Class
    *  @name   parseResponse
    *  @param  ClaimsServiceSummaryResponse_LH_HUM response - obbject of ClaimsServiceSummaryResponse_LH_HUM with response details
    *  @return List<ClaimsSummary_Wrapper_LDTO_HUM> - list of wrapper class objects with the reponse values 
    *  @throws NA
    */ 
    Private List<ClaimsSummary_Wrapper_LDTO_HUM> parseResponse(ClaimsServiceSummaryResponse_LH_HUM response)
    {   
        ClaimsServiceSummaryResponse_LH_HUM.ClaimSummaryReponse  oClaimSummaryResponse = response.ClaimSummaryReponse ;
        ClaimsServiceSummaryResponse_LH_HUM.ClaimSummaryResults  oClaimsumlist = oClaimSummaryResponse.ClaimSummaryResults;
        List<ClaimsServiceSummaryResponse_LH_HUM.ClaimSummaryResult> lstClaim = oClaimsumlist.ClaimSummaryResult;
        iTotalRecordsFromService = Integer.valueOf(oClaimSummaryResponse.TotalRecordNumber);
	iEndRecordFromService = Integer.valueOf(oClaimSummaryResponse.EndRecordNumber);
        lstClaimRefferal = buildWrapperList(lstClaim, iEndRecordFromService, iTotalRecordsFromService);

        return lstClaimRefferal;
    }
    
   /*
    *  This method is used to assign the response to variables in wrapper Class
    *  @name   buildWrapperList
    *  @param  ClaimsServiceSummaryResponse_LH_HUM response - obbject of ClaimsServiceSummaryResponse_LH_HUM with response details
    *  @return List<ClaimsSummary_Wrapper_LDTO_HUM> - list of wrapper class objects with the reponse values stored in it
    *  @throws NA
    */  
    private List<ClaimsSummary_Wrapper_LDTO_HUM> buildWrapperList(List<ClaimsServiceSummaryResponse_LH_HUM.ClaimSummaryResult> lstClaim, integer EndRecordFromService , integer TotalRecordsFromService)
    {
        lstClaimRefferal = new List<ClaimsSummary_Wrapper_LDTO_HUM>();
        List<ClaimsSummary_Wrapper_LDTO_HUM> lstClaimsummary = new List<ClaimsSummary_Wrapper_LDTO_HUM>();
        List<ClaimsSummary_Wrapper_LDTO_HUM> lstDentalClaimRefferal = new List<ClaimsSummary_Wrapper_LDTO_HUM>();
        Set<String> setStr =new Set<String>();
        for(ClaimsServiceSummaryResponse_LH_HUM.ClaimSummaryResult obj:lstClaim )
        {
            if(string.isBlank(obj.ClaimTypeCode))
            {
                obj.ClaimTypeCode = obj.ClaimType;                
            }
            ClaimsSummary_Wrapper_LDTO_HUM objectwrapper = new ClaimsSummary_Wrapper_LDTO_HUM();
            
            objectWrapper.sAdjustInd = obj.AdjustmentIndicator;
            objectWrapper.sClaimNbr = obj.ClaimNumber;           
            objectWrapper.sClaimType = obj.ClaimType;                    
            objectWrapper.sClaimTypeCd = obj.ClaimTypeCode; 
            objectWrapper.sClmGenKey = obj.Id;
            objectWrapper.sClmReceiptDate = obj.ClaimReceiptDate;
            objectWrapper.sChargeAmt = '$' + obj.ChargeAmount;
            objectWrapper.sDispGrpID = obj.DisplayGroupId;
            objectWrapper.sLOBCd = obj.LineOfBusinessCode;
            objectWrapper.sClmDetailLink = obj.ClaimDetailLink;
            objectWrapper.sLastProcessDate = obj.LastProcessDate;
            objectWrapper.sMbrRespAmt = '$' + obj.MemberResponsibilityAmount;
            objectWrapper.sPaidAmt = '$' + obj.PaidAmount;
            objectWrapper.sPlatformCd = obj.PlatformCode;
            objectWrapper.sPreDeterminationIndicator = obj.PreDeterminationIndicator;
            objectWrapper.sPreDeterminationNumber = obj.PreDeterminationNumber;
            objectWrapper.sProviderName = obj.ProviderName; 
            objectWrapper.sProviderID = obj.ProviderTaxId;
            objectWrapper.sSRCNPIID = obj.SourceNationalProviderId;
            objectwrapper.sServiceEndDate = obj.ServiceEndDate;
            objectWrapper.sServiceStartDate = obj.ServiceStartDate;
            objectWrapper.sStatusDesc = obj.PaidStatusDescription;
            objectWrapper.sDeductAmt= obj.DeductibleAmount;
            objectWrapper.sCopayAmt = obj.CopaymentAmount;
            objectWrapper.sCoInsAmt= obj.CoInsuranceAmount;
            objectWrapper.sMemberId = obj.MemberId;            
	    objectWrapper.sEndRecordNumber= EndRecordFromService;
            objectWrapper.sTotalRecordNumber = TotalRecordsFromService; 
            lstDentalClaimRefferal.add(objectwrapper);
            lstSplitData =splitMemberId(objectWrapper.sMemberId);
            setStr.add(lstSplitData[0]);   
        }
        if(((lstDentalClaimRefferal!=null)&&(!(lstDentalClaimRefferal.isEmpty()))) && ((setStr!=null)&&(!(setStr.isEmpty()))))
         {
           lstDentalClaimRefferal = updateDentalClaimsList(lstDentalClaimRefferal,setStr);
           lstClaimRefferal.addAll(lstDentalClaimRefferal);
         }
      return lstClaimRefferal;
    }
    
   /*
    * This method returns wrapper object record for the claim number that the user enters as input in the UI
    *  @name   summaryGoButton
    *  @param  List<ClaimsSummary_Wrapper_LDTO_HUM> - list of wrapper class object with details of all claims
               String sClaimNbr - String which stores value of claim number entered by user
    *  @return ClaimsSummary_Wrapper_LDTO_HUM -  wrapper class object with the details for the claim user enters
    *  @throws NA
    */  
    public ClaimsSummary_Wrapper_LDTO_HUM  summaryGoButton(List<ClaimsSummary_Wrapper_LDTO_HUM> lstClaimSummary,String sClaimNbr)
    {
	if((lstClaimSummary!=null) && (!(lstClaimSummary.isEmpty())))
            {
                lstClaimSummary = updateClaimDetailLinkURL(lstClaimSummary);
            }
        ClaimsSummary_Wrapper_LDTO_HUM oclaimSummaryDTO;
        for(ClaimsSummary_Wrapper_LDTO_HUM oClaimResp  :lstClaimSummary)
        {
            if(oClaimResp.sClaimNbr.equalsIgnoreCase(sClaimNbr))
            {
                oclaimSummaryDTO = new ClaimsSummary_Wrapper_LDTO_HUM();
                oclaimSummaryDTO.sClaimType = oClaimResp.sClaimType;
                oclaimSummaryDTO.sClaimTypeCd = oClaimResp.sClaimTypeCd;
                oclaimSummaryDTO.sClaimNbr = oClaimResp.sClaimNbr;
                oclaimSummaryDTO.sClmGenKey = oClaimResp.sClmGenKey;
                oclaimSummaryDTO.sServiceStartDate = oClaimResp.sServiceStartDate;
                oclaimSummaryDTO.sServiceEndDate = oClaimResp.sServiceEndDate;
                oclaimSummaryDTO.sClmReceiptDate = oClaimResp.sClmReceiptDate;
                oclaimSummaryDTO.sLastProcessDate = oClaimResp.sLastProcessDate;
                oclaimSummaryDTO.sAdjustInd = oClaimResp.sAdjustInd;
                oclaimSummaryDTO.sPlatformCd = oClaimResp.sPlatformCd;
                oclaimSummaryDTO.sProviderName  = oClaimResp.sProviderName;
                oclaimSummaryDTO.sProviderID = oClaimResp.sProviderID;
                oclaimSummaryDTO.sSRCNPIID = oClaimResp.sSRCNPIID;
                oclaimSummaryDTO.sStatusDesc = oClaimResp.sStatusDesc;
                oclaimSummaryDTO.sMemberId = oClaimResp.sMemberId;
                oclaimSummaryDTO.sDeductAmt = oClaimResp.sDeductAmt;
                oclaimSummaryDTO.sCopayAmt = oClaimResp.sCopayAmt;
                oclaimSummaryDTO.sCoInsAmt = oClaimResp.sCoInsAmt;
                oclaimSummaryDTO.sPreDeterminationNumber = oClaimResp.sPreDeterminationNumber;
                oclaimSummaryDTO.sPreDeterminationIndicator = oClaimResp.sPreDeterminationIndicator;
                oclaimSummaryDTO.sMbrRespAmt = oClaimResp.sMbrRespAmt;
                oclaimSummaryDTO.sPaidAmt = oClaimResp.sPaidAmt;
                oclaimSummaryDTO.sClmDetailLink  = oClaimResp.sClmDetailLink;
                oclaimSummaryDTO.sEndRecordNumber  = oClaimResp.sEndRecordNumber;
                oclaimSummaryDTO.sTotalRecordNumber   = oClaimResp.sTotalRecordNumber ;
            }  
        }  
        return oclaimSummaryDTO;  
    }
            
   /*
    *  This method returns updated list of dental claims data for each MemberId of the claim of type Dental
    *  @name   updateDentalClaimsList
    *  @param  Set<string> - Set of Member Ids 
               List<ClaimsSummary_Wrapper_LDTO_HUM>- List of wrapper class objects containing claim details
    *  @return List<ClaimsSummary_Wrapper_LDTO_HUM> - list of wrapper class objects with the claim summary details
    *  @throws updateDentalClaimsList
    */   
    private List<ClaimsSummary_Wrapper_LDTO_HUM> updateDentalClaimsList(List<ClaimsSummary_Wrapper_LDTO_HUM> lstwrapper,Set<string> setMemberId)
    {
        try
        {
            List<MemberPlan> lstreturnedresults = new List<MemberPlan>();
            List<String> encryptList =new List<String>();
            List<String> lstPlainMemId = new List<String>(setMemberId);
            if((lstPlainMemId!=null)&&(!(lstPlainMemId.isEmpty())))
            {
                lstreturnedresults=ClaimsSummary_LD_HUM.getMemberParameters(lstPlainMemId); 
            }
            if((lstreturnedresults!=null) && (!(lstreturnedresults.isEmpty())) && (lstwrapper!=null) && (!(lstwrapper.isEmpty())))
            {
                lstwrapper = updateGroupNumber(lstreturnedresults,lstwrapper);
            }
            if((lstwrapper!=null) && (!(lstwrapper.isEmpty())))
            {
                lstwrapper = updateClaimDetailLinkURL(lstwrapper);
            }
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'ClaimsSummary_LS_HUM', 'updateDentalClaimsList');
        }            
        return lstwrapper;
    }
               
   /*
    *  This method coverts a string into date Format
    *  @name   convertStringToDate
    *  @param String sDate -  Date in String format
    *  @return Date - Date object after conversion from String type
    *  @throws NA
    */    
    @testVisible private Date convertStringToDate(String sDate)
    {
        Date dDate = null;
        if(sDate!=null)
        {
            try
            {
                 String[] sDate1 = sDate.split(' ');
                 String[] sDate2 = sDate1[0].split(BACKSLASH_HUM);
                 dDate = date.newinstance(Integer.valueOf(sDate2[2]), Integer.valueOf(sDate2[0]), Integer.valueOf(sDate2[1]));
            }
            catch(Exception ex)
            {
                HUMExceptionHelper.logErrors(ex, 'ClaimsSummary_LS_HUM', 'convertStringToDate');
            }
        }
        return dDate;
    }
    
    /*
    *  This method splits member id and dependent code
    *  @name   splitMemberId
    *  @param  String sMemberId -  Member Id with dependent code
    *  @return List<string> 
    *  @throws NA
    */    
    private List<String> splitMemberId(String sMemberId)
    {
        List<String> lstSplitMemId =new List<String>();
        Try
        {
            if(sMemberId.contains(HYPHEN_HUM)) 
            {  
                lstSplitMemId = sMemberId.split(HYPHEN_HUM); 
            }
            else
            {
                lstSplitMemId.add(sMemberId);
            }
            
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'ClaimsSummary_LS_HUM', 'splitMemberId');
        }
        return lstSplitMemId ;
    } 

    /*
    *  This method updates dental claims group number and product type based on member id
    *  @name   updateClaimDetailLinkURL
    *  @param  List<ClaimsSummary_Wrapper_LDTO_HUM> lstWrapperClaims
    *  @return List<ClaimsSummary_Wrapper_LDTO_HUM> 
    *  @throws NA
    */
    @testVisible private List<ClaimsSummary_Wrapper_LDTO_HUM> updateClaimDetailLinkURL(List<ClaimsSummary_Wrapper_LDTO_HUM> lstWrapperClaims)
    {
        try
        {
            For(ClaimsSummary_Wrapper_LDTO_HUM lstclaims:lstWrapperClaims)
            {
                lstclaims.sClmDetailLink = 
                '/lightning/n/Claim_Details_LWC?' +
                '&ClaimType=' + lstclaims.sClaimType + 
                '&ClaimTypeCd=' + lstclaims.sClaimTypeCd + 
                '&ClaimNbr=' + lstclaims.sClaimNbr + 
                '&ClaimGenKey=' + lstclaims.sClmGenKey + 
                '&StartDate=' + lstclaims.sServiceStartDate + 
                '&EndDate=' + lstclaims.sServiceEndDate + 
                '&ReceiptDate=' + lstclaims.sClmReceiptDate + 
                '&ProcessDate=' + lstclaims.sLastProcessDate + 
                '&AdjustInd=' + lstclaims.sAdjustInd + 
                '&PlatformCd=' + lstclaims.sPlatformCd + 
                '&ProviderName=' + lstclaims.sProviderName + 
                '&ProviderId=' + lstclaims.sProviderId + 
                '&Npi=' + lstclaims.sSRCNPIID + 
                '&Status=' + lstclaims.sStatusDesc + 
                '&MemberId=' + lstclaims.sMemberId + 
                '&Deduct=' + lstclaims.sDeductAmt + 
                '&Copay=' + lstclaims.sCopayAmt + 
                '&CoIns=' + lstclaims.sCoinsAmt + 
                '&PreDeterminationIndicator=' + lstclaims.sPreDeterminationIndicator + 
                '&type=Claims&subtype=Claim: ' + lstclaims.sClaimNbr + 
                '&MbrRespAmt=' + lstclaims.sMbrRespAmt + 
                '&PaidAmt=' + lstclaims.sPaidAmt + 
                '&ProductType=' + lstclaims.sLOBCd +
                '&AccountId=' + lstclaims.sAccountId;        
            }
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'ClaimsSummary_LS_HUM', 'updateClaimDetailLinkURL');
        }
            
        return lstWrapperClaims;
    }
   /*
    *  This method updates dental claims group number and product type based on member id
    *  @name   updateGroupNumber
    *  @param  List<MemberPlan> lstMemParameters,List<ClaimsSummary_Wrapper_LDTO_HUM> lstWrapperDentalClaims
    *  @return List<ClaimsSummary_Wrapper_LDTO_HUM> 
    *  @throws NA
    */
    @testVisible private List<ClaimsSummary_Wrapper_LDTO_HUM> updateGroupNumber(List<MemberPlan> lstMemParameters,List<ClaimsSummary_Wrapper_LDTO_HUM> lstWrapperDentalClaims)
    {
        try
        {
            For(ClaimsSummary_Wrapper_LDTO_HUM lstdentalclaims:lstWrapperDentalClaims)
            {
                Date dServiceStartDate = convertStringToDate(lstdentalclaims.sServiceStartDate);
                Date dServiceEndDate = convertStringToDate(lstdentalclaims.sServiceEndDate);
                string sdependentcode ='';
                string sEncryptedMemId ='';
                string sMemIdPlusDependentCd ='';
                lstSplitData =splitMemberId(lstdentalclaims.sMemberId);
                if(NUMBER_CONSTANT_TWO_HUM==lstSplitData.size())
                {
                    sdependentcode =lstSplitData[1];
                }   
                sEncryptedMemId =lstSplitData[0];

                sMemIdPlusDependentCd = sEncryptedMemId + sdependentcode;
                for(MemberPlan oMemId :lstMemParameters)
                {
                    Date dEffectiveDate = oMemId.EffectiveFrom;
                    if(oMemId.EffectiveTo == null)
                    {  
                            if(dEffectiveDate != null && dServiceStartDate != null && (dEffectiveDate <=dServiceStartDate) && sMemIdPlusDependentCd.equals(oMemId.Name )) 
                            {    
                                 lstdentalclaims.sDispGrpID = oMemId.GroupNumber;
                                 lstdentalclaims.sLOBCd = oMemId.Product_Type__c;
                                 break;
                            }
                        
                    
                    }
                    else
                    {
                        Date dEndDate = oMemId.EffectiveTo;
                        if(dEffectiveDate!= null && dServiceStartDate!= null && dEndDate!= null && dServiceEndDate!= null && (dEffectiveDate <= dServiceStartDate && dEndDate >= dServiceEndDate) && sMemIdPlusDependentCd.equals(oMemId.Name)) 
                        {   
                            lstdentalclaims.sDispGrpID = oMemId.GroupNumber;
                            lstdentalclaims.sLOBCd = oMemId.Product_Type__c;
                            break;
                        }  
                    }
                    String AccountId = oMemId.MemberId;
                    lstdentalclaims.sAccountId = AccountId!= null ? AccountId :'';
                }
            }
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'ClaimsSummary_LS_HUM', 'updateGroupNumber');
        }
        return lstWrapperDentalClaims;
    }
}
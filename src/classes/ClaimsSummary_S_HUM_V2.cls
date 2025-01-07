/**************************************************************************************************************************************************
Apex class Name    :  ClaimsSummary_S_HUM_V2
Version            :  1.0
Function           :  This is Helper class to ClaimsSummary_C_Hum.It forms request and parse response.
Test Class         :  ClaimsSummary_Service_T_HUM_V2
   Modification Log:
    
 *   Developer                   Code Review             Date               Description
 * -------------------------------------------------------------------------------------------------------------------------------------------------  
 *   Kritika Popat                 24466               02/25/2016             Original Version 
 *   Prasanthi Kandula             24441               04/18/2016             Modified to assign the Policy Member
 *   Samridh Manucha               27178               05/10/2016             Defect fix #222777.0002 - Including'Dental' value to claim type column 
 *   Naveenkanth B                 27208               05/11/2016             For the defect fix: 223672.0001.
 *   Samridh Manucha               27605               05/16/2016             Defect 225119 -Changing mapping for ProdType
 *   Prasanthi Kandula             27605               05/17/2016             Defect Fix #226305 Redirect to Claim Detail of click of GO button
 *   Santhi Mandava                29879               06/11/2016             Implemented standard map changes for claim logging
 *   Prasanthi Kandula             33038               07/15/2016             Defect Fix #232538.0001 Changed the assignment for SubscriberPersonId from Enterprise_ID__c to blank
 *   Harika Devi Kalidindi         34240               08/03/2016             Defect Fix # 231003.0001 Added sClaimTypeCd field in  SummaryGoButton method
 *   Harika Devi Kalidindi         35220               08/11/2016             Defect Fix 227069.0001 -Added getClaimMemberID method
 *   Harika Devi Kalidindi         38517               09/02/2016             Defect  Fix 239780.0001- Added ComparingDates and  convertStringToDate Methods
 *   Pallavi Ravishankar           48220               11/14/2016             Defect Fix - 247701.0001 - updated buildWrapperList to include null check
 *   Harika Devi Kalidindi         48220               11/11/2016             Defect Fix - 247701.0001 - added updateDentalClaimsList Method, splitMemberId
 *   Prasanthi Kandula             96248               05/24/2017             REQ-318369 Fixed Null pointer exception on convertStringToDate() method
 *   Prasanthi Kandula             212620              03/06/2018             REQ - 290133 - Updated logic as part of claims service v3 changes to display 500 claims at a time on button click
 *   Kritika Popat                 212620              03/06/2018             REQ - 355813 - Claims V3 Integration of Summary method
 *   Suresh Gottipati                                  06/15/2018             REQ - 366902 - Claim Summary section in Claim Summary page 
 *   Sunil Bukya                                       01/24/2019             Removed Platform switch
 *   Santhi Mandava                                    04/15/2019             Replaced "Member_Id_Value__c" with Name.
 *   Praveen Kumar Parimi                              07/30/2019             REQ 403782 - Requirement to update PreDeterminationNumber to PreDeterminationIndicator. 
 *   Anjani Vaddadi                                    06/03/2020             REQ - 1083200 -CA Incident Wrong Product Type on Claim Details page And claim summary
 *   Anjani Vaddadi                                    06/26/2020             RollBack REQ 1083200
 *   Samruddhi Rathi                                   05/07/2021             REQ - 2230063 Humana Constant CleanUp
 *   Prasanthi Kandula                                 07/02/2021             US - 2356070 Claims Summary performance improvement 
************************************************************************************************************************************************* */
public with sharing class ClaimsSummary_S_HUM_V2
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
    @TestVisible private String sBody ='' ;
    @TestVisible private String sBodyGo ='' ;
    @TestVisible private List<String> lstSplitData = NULL;
    @TestVisible private String sDependentCode = '';
    private List<ClaimsSummary_Wrapper_DTO_HUM> lstClaimRefferal = new List<ClaimsSummary_Wrapper_DTO_HUM>();
    
   /*
    *  This is the Constructor for the class ClaimsSummary_S_HUM_V2
    *  @name   ClaimsSummary_S_HUM_V2
    *  @param  NA
    *  @return NA
    *  @throws NA
    */ 
    public ClaimsSummary_S_HUM_V2()
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
            HUMExceptionHelper.logErrors(ex, 'ClaimsSummary_S_HUM_V2', 'setupRequest');
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
            HUMExceptionHelper.logErrors(ex, 'ClaimsSummary_S_HUM_V2', 'setupGoButtonRequest');
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
                ClaimsServiceSummaryRequest_H_HUM_V2 oRequestGo = new ClaimsServiceSummaryRequest_H_HUM_V2();
                ClaimsServiceSummaryRequest_H_HUM_V2.GetClaimsSummary getClaimSumGo = new ClaimsServiceSummaryRequest_H_HUM_V2.GetClaimsSummary();
                List<ClaimsServiceSummaryRequest_H_HUM_V2.MemberRequests> lstMemberRequests = new List<ClaimsServiceSummaryRequest_H_HUM_V2.MemberRequests>();
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
            HUMExceptionHelper.logErrors(ex, 'ClaimsSummary_S_HUM_V2', 'buildRequestGoButton');
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
                ClaimsServiceSummaryRequest_H_HUM_V2 oRequest = new ClaimsServiceSummaryRequest_H_HUM_V2();
                ClaimsServiceSummaryRequest_H_HUM_V2.GetClaimsSummary oGetClaimSummary = new ClaimsServiceSummaryRequest_H_HUM_V2.GetClaimsSummary();
                if(String.isNotBlank(sStartCount))
                {
                    oGetClaimSummary.StartCount = sStartCount;
                }
                else
                oGetClaimSummary.StartCount = '1';  
                List<ClaimsServiceSummaryRequest_H_HUM_V2.MemberRequests> lstMemberRequests = new List<ClaimsServiceSummaryRequest_H_HUM_V2.MemberRequests>();
                for(String oPolMem : setPolicyMembers) 
                {
                    ClaimsServiceSummaryRequest_H_HUM_V2.MemberRequests oMemberReq = new  ClaimsServiceSummaryRequest_H_HUM_V2.MemberRequests();
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
            HUMExceptionHelper.logErrors(ex, 'ClaimsSummary_S_HUM_V2', 'buildRequest');
            sJsonbody =  NULL;
        }
        return sJsonbody;
    }
    
   /*
    *  This method is used to Process the Json response from service to Wrapper List
    *  @name   processResponse
    *  @param  string(Json response) - Body of the response in JSON format
    *  @return List<ClaimsSummary_Wrapper_DTO_HUM> - WrapperList containing the response details
    *  @throws NA
    */ 
    public List<ClaimsSummary_Wrapper_DTO_HUM> processResponse(string response) 
    {  
        ClaimsServiceSummaryResponse_H_HUM_V2  objresp = new ClaimsServiceSummaryResponse_H_HUM_V2();
        List<ClaimsSummary_Wrapper_DTO_HUM> listClaimsResp = new List<ClaimsSummary_Wrapper_DTO_HUM>();
        objresp = (ClaimsServiceSummaryResponse_H_HUM_V2 )System.JSON.deserialize(response,ClaimsServiceSummaryResponse_H_HUM_V2.class);
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
        ClaimsServiceSummaryResponse_H_HUM_V2 objrespGo = new ClaimsServiceSummaryResponse_H_HUM_V2();
        objrespGo = (ClaimsServiceSummaryResponse_H_HUM_V2)System.JSON.deserialize(sResponseGo,ClaimsServiceSummaryResponse_H_HUM_V2.class);  
        if(objrespGo.ClaimSummaryReponse != Null)
        {
            ClaimsServiceSummaryResponse_H_HUM_V2.ClaimSummaryReponse oClaimResponseGo = objrespGo.ClaimSummaryReponse;
            return Integer.valueof(oClaimResponseGo.TotalRecordNumber);
        }
        else    return 0;
    }
    
   /*
    *  This method is used to assign the response to variables in wrapper Class
    *  @name   parseResponse
    *  @param  ClaimsServiceSummaryResponse_H_HUM_V2 response - obbject of ClaimsServiceSummaryResponse_H_HUM_V2 with response details
    *  @return List<ClaimsSummary_Wrapper_DTO_HUM> - list of wrapper class objects with the reponse values 
    *  @throws NA
    */ 
    Private List<ClaimsSummary_Wrapper_DTO_HUM> parseResponse(ClaimsServiceSummaryResponse_H_HUM_V2 response)
    {   
        
        ClaimsServiceSummaryResponse_H_HUM_V2.ClaimSummaryReponse  oClaimSummaryResponse = response.ClaimSummaryReponse ;
        ClaimsServiceSummaryResponse_H_HUM_V2.ClaimSummaryResults  oClaimsumlist = oClaimSummaryResponse.ClaimSummaryResults;
        List<ClaimsServiceSummaryResponse_H_HUM_V2.ClaimSummaryResult> lstClaim = oClaimsumlist.ClaimSummaryResult;
        iTotalRecordsFromService = Integer.valueOf(oClaimSummaryResponse.TotalRecordNumber);
        lstClaimRefferal = buildWrapperList(lstClaim);
        return lstClaimRefferal;
    }
    
   /*
    *  This method is used to assign the response to variables in wrapper Class
    *  @name   buildWrapperList
    *  @param  ClaimsServiceSummaryResponse_H_HUM_V2 response - obbject of ClaimsServiceSummaryResponse_H_HUM_V2 with response details
    *  @return List<ClaimsSummary_Wrapper_DTO_HUM> - list of wrapper class objects with the reponse values stored in it
    *  @throws NA
    */  
    private List<ClaimsSummary_Wrapper_DTO_HUM> buildWrapperList(List<ClaimsServiceSummaryResponse_H_HUM_V2.ClaimSummaryResult> lstClaim)
    {
        lstClaimRefferal = new List<ClaimsSummary_Wrapper_DTO_HUM>();
        List<ClaimsSummary_Wrapper_DTO_HUM> lstClaimsummary = new List<ClaimsSummary_Wrapper_DTO_HUM>();
        List<ClaimsSummary_Wrapper_DTO_HUM> lstDentalClaimRefferal = new List<ClaimsSummary_Wrapper_DTO_HUM>();
        Set<String> setStr =new Set<String>();
        for(ClaimsServiceSummaryResponse_H_HUM_V2.ClaimSummaryResult obj:lstClaim )
        {
            if(string.isBlank(obj.ClaimTypeCode))
            {
                obj.ClaimTypeCode = obj.ClaimType;                
            }
            ClaimsSummary_Wrapper_DTO_HUM objectwrapper = new ClaimsSummary_Wrapper_DTO_HUM();
            
            objectWrapper.sAdjustInd = obj.AdjustmentIndicator;
            objectWrapper.sClaimNbr = obj.ClaimNumber;           
            objectWrapper.sClaimType = obj.ClaimType;                    
            objectWrapper.sClaimTypeCd = obj.ClaimTypeCode; 
            objectWrapper.sClmGenKey = obj.Id;
            objectWrapper.sClmReceiptDate = obj.ClaimReceiptDate;
            objectWrapper.sChargeAmt = obj.ChargeAmount;
            objectWrapper.sDispGrpID = obj.DisplayGroupId;
            objectWrapper.sLOBCd = obj.LineOfBusinessCode;
            objectWrapper.sLastProcessDate = obj.LastProcessDate;
            objectWrapper.sMbrRespAmt = obj.MemberResponsibilityAmount;
            objectWrapper.sPaidAmt = obj.PaidAmount;
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
    *  @param  List<ClaimsSummary_Wrapper_DTO_HUM> - list of wrapper class object with details of all claims
               String sClaimNbr - String which stores value of claim number entered by user
    *  @return ClaimsSummary_Wrapper_DTO_HUM -  wrapper class object with the details for the claim user enters
    *  @throws NA
    */  
    public ClaimsSummary_Wrapper_DTO_HUM  summaryGoButton(List<ClaimsSummary_Wrapper_DTO_HUM> lstClaimSummary,String sClaimNbr)
    {
        ClaimsSummary_Wrapper_DTO_HUM oclaimSummaryDTO;
        for(ClaimsSummary_Wrapper_DTO_HUM oClaimResp  :lstClaimSummary)
        {
            if(oClaimResp.sClaimNbr.equalsIgnoreCase(sClaimNbr))
            {
                oclaimSummaryDTO = new ClaimsSummary_Wrapper_DTO_HUM();
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
                
            }  
        }  
        return oclaimSummaryDTO;  
    }
            
   /*
    *  This method returns updated list of dental claims data for each MemberId of the claim of type Dental
    *  @name   updateDentalClaimsList
    *  @param  Set<string> - Set of Member Ids 
               List<ClaimsSummary_Wrapper_DTO_HUM>- List of wrapper class objects containing claim details
    *  @return List<ClaimsSummary_Wrapper_DTO_HUM> - list of wrapper class objects with the claim summary details
    *  @throws updateDentalClaimsList
    */   
    private List<ClaimsSummary_Wrapper_DTO_HUM> updateDentalClaimsList(List<ClaimsSummary_Wrapper_DTO_HUM> lstwrapper,Set<string> setMemberId)
    {
        try
        {
            List<Member_ID__c> lstreturnedresults = new List<Member_ID__c>();
            List<String> encryptList =new List<String>();
            List<String> lstPlainMemId = new List<String>(setMemberId);
            if((lstPlainMemId!=null)&&(!(lstPlainMemId.isEmpty())))
            {
                lstreturnedresults=ClaimsSummary_D_HUM_V2.getMemberParameters(lstPlainMemId); 
            }
            if((lstreturnedresults!=null) && (!(lstreturnedresults.isEmpty())) && (lstwrapper!=null) && (!(lstwrapper.isEmpty())))
            {
                lstwrapper = updateGroupNumber(lstreturnedresults,lstwrapper);
            }
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'ClaimsSummary_S_HUM_V2', 'updateDentalClaimsList');
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
                HUMExceptionHelper.logErrors(ex, 'ClaimsSummary_S_HUM_V2', 'convertStringToDate');
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
            HUMExceptionHelper.logErrors(ex, 'ClaimsSummary_S_HUM_V2', 'splitMemberId');
        }
        return lstSplitMemId ;
    } 
    
   /*
    *  This method updates dental claims group number and product type based on member id
    *  @name   updateGroupNumber
    *  @param  List<Member_ID__c> lstMemParameters,List<ClaimsSummary_Wrapper_DTO_HUM> lstWrapperDentalClaims
    *  @return List<ClaimsSummary_Wrapper_DTO_HUM> 
    *  @throws NA
    */
    @testVisible private List<ClaimsSummary_Wrapper_DTO_HUM> updateGroupNumber(List<Member_ID__c> lstMemParameters,List<ClaimsSummary_Wrapper_DTO_HUM> lstWrapperDentalClaims)
    {
        try
        {
            For(ClaimsSummary_Wrapper_DTO_HUM lstdentalclaims:lstWrapperDentalClaims)
            {
                Date dServiceStartDate = convertStringToDate(lstdentalclaims.sServiceStartDate);
                Date dServiceEndDate = convertStringToDate(lstdentalclaims.sServiceEndDate);
                string sdependentcode ='';
                string sEncryptedMemId ='';
                lstSplitData =splitMemberId(lstdentalclaims.sMemberId);
                if(NUMBER_CONSTANT_TWO_HUM==lstSplitData.size())
                {
                    sdependentcode =lstSplitData[1];
                }   
                sEncryptedMemId =lstSplitData[0];
                for(Member_Id__c oMemId :lstMemParameters)
                {
                    Date dEffectiveDate = convertStringToDate(oMemId.Policy_Member__r.Effective_Date__c);
                    if(string.isBlank(oMemId.Policy_Member__r.End_Date__c))
                    {  
                           if(dEffectiveDate != null && dServiceStartDate != null && (dEffectiveDate <=dServiceStartDate) && sdependentcode.equals(oMemId.Member_Dependent_Code__c) && sEncryptedMemId.equals(oMemId.Name ))
                            {    
                                 lstdentalclaims.sDispGrpID = oMemId.Policy_Member__r.Group_Number__c;
                                 lstdentalclaims.sLOBCd = oMemId.Policy_Member__r.Product_Type__c;
                                 break;
                            }
                        
                    
                    }
                    else
                    {
                        Date dEndDate = convertStringToDate(oMemId.Policy_Member__r.End_Date__c) ;
                        if(dEffectiveDate!= null && dServiceStartDate!= null && dEndDate!= null && dServiceEndDate!= null && (dEffectiveDate <= dServiceStartDate && dEndDate >= dServiceEndDate) && sdependentcode.equals(oMemId.Member_Dependent_Code__c) && sEncryptedMemId.equals(oMemId.Name)) 
                        {   
                            lstdentalclaims.sDispGrpID = oMemId.Policy_Member__r.Group_Number__c;
                            lstdentalclaims.sLOBCd = oMemId.Policy_Member__r.Product_Type__c;
                            break;
                        }  
                    }
                }
            }
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'ClaimsSummary_S_HUM_V2', 'updateGroupNumber');
        }
            
        return lstWrapperDentalClaims;
    }
}
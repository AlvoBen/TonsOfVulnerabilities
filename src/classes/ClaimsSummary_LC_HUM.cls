/*****************************************************************************************
Apex class Name    : ClaimsSummary_LC_HUM
Version            : 1.0
Function           : Controller to Claim summary LWC component.This class is used to display Claims Summary on the lightning compoennt.
Created Date       : 
Test Class         : ClaimsSummary_LT_HUM

        Modification Log:
 *   Developer                   Code Review             Date               Description
 * ------------------------------------------------------------------------------------------------------------------------------   
 *   Suraj Patil                                         06/10/2022         Original Version Claim summary
 *   Anuradha Gajbhe                                     08/10/2022         US#3587828 - Claims--Lightning--Additional Capabilities on Claims Line Items: informational Messages Claim Summary 3
 *   Sagar G                                    	 	 09/16/2022         DF-6177
 *   Anuradha Gajbhe                                     11/02/2022         US#3786555 - Claims System Integration: Claims: Medical Claims: Dental Claims: Security Home Office Differentiation
 *   Anuradha Gajbhe                                     02/29/2024         User Story 5289600: Claims Not Visible - INC2619433 and INC2621261
 *********************************************************************************************************************************/
public with sharing class ClaimsSummary_LC_HUM
{   
    Public Static final Integer HTTP_OK_HUM = 200;
    Public Static final Integer HTTP_ACCEPTED_HUM = 202; 
    Public Static final String ID_HUM = 'id';
    Public Static final String CASAppName = 'CASLegacy';
    public Static boolean bCoveoUser {get;set;}
    Public Static boolean bClaimsReq {get;set;}
    Public Static final String ChromeBrowser = 'Chrome/';
    Public Static final String EdgeBrowser = 'Edg';

    @TestVisible Public Static ClaimsSummary_LS_HUM oServiceClaim = new ClaimsSummary_LS_HUM();
    @TestVisible public static ClaimsSummaryHOCheck_LC_HUM oHoClaimCheck = new ClaimsSummaryHOCheck_LC_HUM();
    @TestVisible public static String sRequest ='';
    @TestVisible Public Static String sGoRequest =''; 
    @TestVisible Public Static List<MemberPlan> lstPolMember = new List<MemberPlan>(); 
    @TestVisible public static ID curentUserID;
    @TestVisible Public Static Boolean bIsMemberAHomeOfficeAcct;
    @TestVisible Public Static Boolean bIsNonHOUser;
    @TestVisible Public Static Boolean bisHomeOfficeAllUser;
    @TestVisible Public Static Boolean bisHomeOfficeMedicalUser;
    @TestVisible Public Static Boolean bisHomeOfficeDentalUser;

       
    public Static string AccountId {get;set;} 
    public Static string vMemberAccountID {get;set;} 
    public Static String sMemName{get;set;}     
    public Static String sRecId {get;set;}
    public Static Boolean bHomeOfcMedDen;
    public String sClaimSearch{get;set;}
    public Static Boolean bINFO;
    public Static Boolean bShowResults;
    public Static Boolean bClaimExists{get;set;}
    public Static String sMsgOnGo{get;set;}
    public Static String sWarningMsg{get;set;}
    public Static String sErrorMsg{get;set;}
    public Static String sInfoMsg{get;set;}
    public Static String sSeverityOnGo{get;set;}
    public Static Boolean bDuplicate{get;set;}
    public Static Boolean bShowError{get;set;}
    public Static Boolean bShowServiceError{get;set;}
    public Static Boolean bShowBADropdown;
    public Static List<ClaimsSummary_Wrapper_LDTO_HUM> lstClaimResponse;
    public Static Set<String> setBA {get;set;}
    public Static Boolean bHomeOficeMessage{get;set;}
    public Map<String,String> filteredClaimsMap=new Map<String,String>();
    public Static MemberPlan objPMMain {get; set;}
    public Static String sClaimNbr{get;set;}
    public Static ClaimsSummary_Wrapper_LDTO_HUM claimgotoResponse {get;set;} 
    public String sGCPURL{get;set;}
    public String sCASWEBUrl{get;set;}
    public String sClientNumber{get;set;}
    public String sFirstName{get;set;}
    public String sMemId{get;set;}
    public String sRelation{get;set;}
    public String sApplName{get;set;}
    public String sClmNbrCAS{get;set;}
    public Static String sAttachKey{get;set;}
    public Static Integer iTotalRecords {get;set;}
    public Static String sStartCount1 {get;set;}
    public Static Set<String> setPolicyMembers;
    public Static String sClaimsSummary {get;set;}
    public Static String sClaimsGoSummary {get;set;}
    public Static String sClaimSummaryRspStr {get;set;}
    public Static Boolean bdisableButton {get; set;}
    public Static String sEnvironment{get;set;}
    
    /*
     * Method Name   :  Initialize()
     * Description   :  This is like a constructor for class ClaimsSummary_LC_HUM
     * Return Type   :  NA
     * Parameters    :  NA
     */
    public Static Void Initialize(ID sRecordId)
    {
        bINFO = true;
        bShowServiceError = false;        
        sRecId = sRecordId;
        sMemName = getMemberName(sRecordId);
        sAttachKey = (lstPolMember!= null && lstPolMember.size() > 0) ? lstPolMember[0].Member.Enterprise_ID__c: '';
        AccountId = (lstPolMember!= null && lstPolMember.size() > 0) ? lstPolMember[0].Memberid: '';
        curentUserID = UserInfo.getUserId();
        objPMMain = new MemberPlan();
        setPolicyMembers = ClaimsSummary_LD_HUM.getPolicyMembersFromAccount(AccountId);
        bClaimsReq = HUMUtilityHelper.isCRMFunctionalityON('3038833');
        sEnvironment = HUMConstants__c.getInstance('APPLAUNCHER_ENVIRONMENT').StringValue__c;
    }

    /*
     * Method Name   :  searchClaim
     * Description   :  This method is used for searching claims
     * Return Type   :  NA
     * Parameters    :  NA
     */
    @AuraEnabled(continuation=true)
    public Static object searchClaim(list<string> sMemberIds, list<string> sClaimNumber)  
    {
        try
        { 
            sClaimNbr = sClaimNumber[0];
            String sGoRequest = '';
            bDuplicate = false;
            bClaimExists = false;
            bShowServiceError = false;
            bShowError = false;

            
            
            Map<String, list<String>> SearchData = new Map<String, list<String>>();
            SearchData.put('sMemberIds',sMemberIds);
            SearchData.put('sClaimNumber',sClaimNumber);
            if(String.isNotBlank(sClaimNbr))
            {
                HUM_Webservice_Callout__c oSvcHeaderGoClaims = HUM_Webservice_Callout__c.getValues('ClaimsService_GetClaimsSummary_V3'); 
                Integer iTIMEOUT_INT_SECS = Integer.valueof(oSvcHeaderGoClaims.Timeout__c)/1000;
                HttpRequest requestGo = new HttpRequest();
                requestGo = oServiceClaim.setupGoButtonRequest(requestGo, oSvcHeaderGoClaims, sClaimNbr);
                Continuation conGo = new Continuation(iTIMEOUT_INT_SECS );
                sGoRequest = conGo.addHttpRequest(requestGo);
				conGo.state = SearchData;
                conGo.continuationMethod = 'processGoButtonResponse';
                return conGo;
            }
            else return Null;
        }
        catch(Exception ex)
        {
            bClaimExists = true; 
            sMsgOnGo = Label.ClaimsSummary_Service_Error;
            sSeverityOnGo = 'Error';
            HUMExceptionHelper.logErrors(ex, 'ClaimsSummary_LC_HUM', 'searchClaim');
            return NULL;
        }   
    }
    
    /*
    * Method Name   :  processGoButtonResponse
    * Description   :  This method is used to get response from service.
    * Return Type   :  void 
    * Parameters    :  NA
    */
    @AuraEnabled
     public static String processGoButtonResponse(List<string> labels,Object state)
    {
        try 
        {
	    sClaimSummaryRspStr = ''; 
            HttpResponse responseGo = Continuation.getResponse(labels[0]);
            sClaimSummaryRspStr = responseparseGoButton(responseGO,state);
            return sClaimSummaryRspStr;
        }
        catch(Exception ex)
        {
            bClaimExists = true; 
            sMsgOnGo = Label.ClaimsSummary_Service_Error;
            sSeverityOnGo = 'Error';
            HUMExceptionHelper.logErrors(ex, 'ClaimsSummary_LC_HUM', 'processGoButtonResponse');
	    return sMsgOnGo;
        }
        
    }
    
    /*
     * Method Name   :  responseparseGoButton
     * Description   :  This method is used to check response
     * Return Type   :  ClaimsSummary_Wrapper_LDTO_HUM 
     * Parameters    :  HttpResponse obj
     */
    @testvisible
    public Static String responseparseGoButton(HttpResponse sResponseGo,Object state )
    {
        Integer statusCode = sResponseGo.getStatusCode();
        if (statusCode == HTTP_OK_HUM || statusCode == HTTP_ACCEPTED_HUM)
        {
            List<ClaimsSummary_Wrapper_LDTO_HUM> templstClaimResponse = oServiceClaim.processResponse(sResponseGo.getbody());
            List<ClaimsSummary_Wrapper_LDTO_HUM> templstClaimResponseMember = new List<ClaimsSummary_Wrapper_LDTO_HUM>();
	        object ClmData = state;	    
            Map<String, Object> ClmDataObj = (Map<String, Object>) JSON.deserializeUntyped(JSON.serialize(ClmData));
            string sMemberIds = string.valueOf(ClmDataObj.get('sMemberIds'));
	    	String sClaimNbr = string.valueOf(ClmDataObj.get('sClaimNumber'));
            String ResponseMemId;

            if (templstClaimResponse.size() > 1)
            {
                for(ClaimsSummary_Wrapper_LDTO_HUM tmpClaimResp : templstClaimResponse){
                    if ((tmpClaimResp.sMemberId != '' && tmpClaimResp.sMemberId != null) && (sMemberIds.contains(tmpClaimResp.sMemberId))){
                        ResponseMemId = tmpClaimResp.sMemberId;
                    }
                    if(ResponseMemId != '' && ResponseMemId != null){
                            break;
                    }
                }
            }else if (templstClaimResponse.size() == 1)
                {
                    ResponseMemId = (templstClaimResponse.size() > 0) ? templstClaimResponse[0].sMemberId : '';
                }

            if (ResponseMemId != '' && ResponseMemId != null){
            
	    	boolean MemIdAccessible = sMemberIds.contains(ResponseMemId);	    
            if (MemIdAccessible) {
                if(templstClaimResponse.size() > 0){
                    templstClaimResponseMember = templstClaimResponse ;
                    List<ClaimsSummary_Wrapper_LDTO_HUM> lstClaimResponseGoButton = new List<ClaimsSummary_Wrapper_LDTO_HUM>();
                    lstClaimResponseGoButton = templstClaimResponseMember;
                    if(lstClaimResponseGoButton.size() > 0)
                    {
                        ClaimsSummary_LS_HUM oClaimService = new ClaimsSummary_LS_HUM();
                        String TempsClaimNbr = sClaimNbr.remove('(');
                        String TemsClaimNbr = TempsClaimNbr.remove(')');
                        claimgotoResponse = oClaimService.SummaryGoButton(lstClaimResponseGoButton,TemsClaimNbr);
                        sClaimsGoSummary = JSON.serialize(claimgotoResponse);
                        sClaimsGoSummary = '{"data":'+sClaimsGoSummary+'}';
                        return sClaimsGoSummary;
                    }
                    if((templstClaimResponseMember.size()>lstClaimResponseGoButton.size()) && (lstClaimResponseGoButton.size() == 0))            
                    {
                        bClaimExists = true;  
                        sWarningMsg = Label.Claimssummary_HomeOffice_Message;
                        sSeverityOnGo  = 'Warning';
                        return sWarningMsg;
                    }
                }else{ 
                    bClaimExists = true; 
                    sMsgOnGo = Label.ClaimSummary_ClmNbr_NotFound;
                    sSeverityOnGo = 'Warning';
                    return sMsgOnGo;
                }
            }else{ 
                    bClaimExists = true; 
                    sMsgOnGo = Label.Claimssummary_HomeOffice_Message;
                    sSeverityOnGo = 'Warning';
                    return sMsgOnGo;
            }  
        }else{ 
            bClaimExists = true; 
            sMsgOnGo = Label.ClaimSummary_ClmNbr_NotFound;
            sSeverityOnGo = 'Warning';
            return sMsgOnGo;
            }
        }
        else
        {
            bClaimExists = true;
            sWarningMsg = Label.ClaimsSummary_Service_Error;
            sSeverityOnGo  = 'Error';
            return sWarningMsg;
        }
        return null;
    }
    /*
    * Method Name   :  formDropdown
    * Description   :  This method is used to form Dropdown
    * Return Type   :  NA
    * Parameters    :  List<ClaimsSummary_Wrapper_LDTO_HUM>
    */
    public Static void formDropdown(List<ClaimsSummary_Wrapper_LDTO_HUM> lstClaimResponse1)
    {
        setBA = new Set<String>();
        for (Integer i = 0; i < lstClaimResponse1.size(); i++)
        {
            if(lstClaimResponse1[i].sDeductAmt != '0.00')    setBA.add('Deductible');
            if(lstClaimResponse1[i].sCopayAmt != '0.00')    setBA.add('Co-Pay');
            if(lstClaimResponse1[i].sCoInsAmt != '0.00')    setBA.add('Co-Insurance');
        }
        if(!((setBA).isEmpty()))    bShowBADropdown = true;
    }
    /*
    * Method Name   :  infoSet
    * Description   :  This method is used to fetch parameters to be sent to CASLegacy link and set Apex page message.
    * Return Type   :  NA
    * Parameters    :  NA
    */
    public void infoSet()
    {
        HUM_Webservice_Callout__c cstCASWEB =  HUM_Webservice_Callout__c.getInstance(System.Label.HUMLaunchCASWEBUI);
        if(cstCASWEB!=null)  sCASWEBUrl = cstCASWEB.End_Point_URL__c;
        if(!lstPolMember.isEmpty() && lstPolMember!=null)
        {
        if(lstPolMember[0].Policy__r.Client_Number__c!=null)sClientNumber = lstPolMember[0].Policy__r.Client_Number__c;
        if(lstPolMember[0].Member.FirstName!=null)sFirstName = lstPolMember[0].Member.FirstName;
        if(lstPolMember[0].Relationship_Code__c!=null)
        {
            list<LVRelationShipCodeToCAS__c> lstlvcas = ClaimsSummary_LD_HUM.getLVCASTranslationDetails(lstPolMember[0].Relationship_Code__c);
            if((!lstlvcas.isEmpty()) && lstlvcas[0].CASTranslation__c!=null)sRelation = lstlvcas[0].CASTranslation__c;
        }
        }
        list<MemberPlan> lstmemIdObj= ClaimsSummary_LD_HUM.getMemberIDObjDetails(apexpages.currentpage().getparameters().get(ID_HUM).escapeHTML4());
        if(!lstmemIdObj.isEmpty() && lstmemIdObj!=null && lstmemIdObj[0].Name !=null)
            sMemId = lstmemIdObj[0].Name ;
        sApplName = CASAppName;
        sClmNbrCAS = '';
        ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.Info, Label.ClaimSummary_Info_V4_Msg));       
        
    }
         
    /*
    * Method Name   :  initiateRequest
    * Description   :  This method is used to call the Service while loading the page
    * Return Type   :  continuation
    * Parameters    :  NA
    */
    @AuraEnabled(continuation=true cacheable=true)
    public static object initiateRequest(ID sRecordId, string sStartCount)
    {
        try
        { 
            bClaimsReq = HUMUtilityHelper.isCRMFunctionalityON('3038833');
            sClaimsSummary = '';
            string sStartCount1 = sStartCount;
            sRequest = '';
            sRecId = sRecordId;
            if(String.isNotBlank(sRecId) && bClaimsReq)
            {
                Initialize(sRecordId);
		if (lstPolMember!= null && lstPolMember.size() > 0){
		    objPMMain =  lstPolMember[0];
                }

                HUM_Webservice_Callout__c oSvcHeaderClaims = HUM_Webservice_Callout__c.getValues('ClaimsService_GetClaimsSummary_V3'); 
                Integer iTIMEOUT_INT_SECS = Integer.valueof(oSvcHeaderClaims.Timeout__c)/1000;
                HttpRequest request = new HttpRequest();
                oServiceClaim.setupRequest(request,oSvcHeaderClaims, sRecId,sStartCount1,setPolicyMembers);
                Continuation con = new Continuation(iTIMEOUT_INT_SECS );
                sRequest = con.addHttpRequest(request);
                con.continuationMethod='processResponse';
	    	con.state = '{' + sRecId + '}';
                if(Test.isRunningTest()) throw new HUMCustomException('Error');
                    return con;
            }
            else    return Null;
        }
        catch(Exception ex)
        {
            bShowServiceError = true;
            HUMExceptionHelper.logErrors(ex, 'ClaimsSummary_LC_HUM', 'initiaterequest');
            return NULL;
        }
    }
    
    /*
    * Method Name   :  processResponse
    * Description   :  This method is used to get response from service.
    * Return Type   :  String 
    * Parameters    :  NA
    */
    @AuraEnabled (cacheable=true)
    public Static String processResponse(List<string> labels,Object state)
    {
        try 
        {
            HttpResponse response = Continuation.getResponse(labels[0]);
            String claimLinkResponse = responseparse(response,state);
            if (claimLinkResponse != ''){
                return claimLinkResponse;
            }
            else{ 
                return null;
            }
        }
        catch(Exception ex)
        {
            bShowServiceError = true;
            HUMExceptionHelper.logErrors(ex, 'ClaimsSummary_LC_HUM', 'processResponse');
        }
        return null;
    }

   /*
     * Method Name   :  responseparse
     * Description   :  This method is used to check response
     * Return Type   :  String
     * Parameters    :  HttpResponse obj
     */
    @testvisible 
    public Static String  responseParse(HttpResponse sResponse,Object state)
    {
        sClaimsSummary = '';
        Integer statusCode = sResponse.getStatusCode();
        if (statusCode == HTTP_OK_HUM || statusCode == HTTP_ACCEPTED_HUM)
        {
            string sTmpRecId = string.valueof(state);
            String sRecId = (sTmpRecId != Null || sTmpRecId != '') ? sTmpRecId.replace('{','').replace('}',''): ''; 
            bHomeOficeMessage = false;
            bShowError = false;
            bShowServiceError = false;
            setBA = new Set<String>();
            list<MemberPlan> lstMemPlan = ClaimsSummary_LD_HUM.getPolicyMemberDetails(sRecId);
            sRecId = lstMemPlan[0].Id;
            AccountId = lstMemPlan[0].Memberid;
            curentUserID = UserInfo.getUserId();
            ID vMemberAccountID = lstMemPlan[0].Memberid; 
            bIsMemberAHomeOfficeAcct = oHoClaimCheck.isMemberHaveHomeOfficePolicies(AccountId);
            bisHomeOfficeAllUser = oHoClaimCheck.isHomeOfficeAllUser(curentUserID);
            bisHomeOfficeMedicalUser = oHoClaimCheck.isHomeOfficeMedicalUser(curentUserID);
            bisHomeOfficeDentalUser = oHoClaimCheck.isHomeOfficeDentalUser(curentUserID);
            bIsNonHOUser = (bisHomeOfficeAllUser || bisHomeOfficeMedicalUser || bisHomeOfficeDentalUser)?false:true;
            if(bIsMemberAHomeOfficeAcct && bIsNonHOUser) bHomeOficeMessage = true;
            bShowBADropdown = false;
            bShowResults = true;
            objPMMain = new MemberPlan();
	    List<ClaimsSummary_Wrapper_LDTO_HUM> templstClaimResponse = oServiceClaim.processResponse(sResponse.getbody());
            
            ClaimsSecurity_LH_HUM cHomeOfficeSecurity = new ClaimsSecurity_LH_HUM();
            lstClaimResponse = new List<ClaimsSummary_Wrapper_LDTO_HUM>();
            bDuplicate = false;
            bClaimExists = false;
	    if(templstClaimResponse != null && !(templstClaimResponse).isEmpty()) {
                lstClaimResponse = cHomeOfficeSecurity.getFilteredClaims(templstClaimResponse,vMemberAccountID,bIsMemberAHomeOfficeAcct,bisHomeOfficeAllUser,bisHomeOfficeMedicalUser,bisHomeOfficeDentalUser);
                sClaimsSummary = JSON.serialize(lstClaimResponse);
                sClaimsSummary = '{"data":'+sClaimsSummary+'}';
            }
            
            if(templstClaimResponse.size()>lstClaimResponse.size() || bHomeOficeMessage )            
            {
               bHomeOficeMessage = true; 
	       sWarningMsg = Label.Claimssummary_HomeOffice_Message;
	       if (lstClaimResponse.size() > 0){
                  return '{"filteredResponse":' + sClaimsSummary + ',' + '"HOMsg":' + sWarningMsg + '}';
               }
		else {
               	    return sWarningMsg;
		}
            }
            else if(lstClaimResponse!= null && !(lstClaimResponse).isEmpty())
            {
                bShowResults = oServiceClaim.bviewresults;
                iTotalRecords = oServiceClaim.iTotalRecordsFromService;
                formDropdown(lstClaimResponse);
                return  sClaimsSummary;
            }
            else if(((lstClaimResponse).isEmpty()) || (lstClaimResponse == null))
            {
                bShowError = true;
                bShowResults = false;
				sWarningMsg = Label.Claimssummary_No_Records;
                return sWarningMsg;
            }
        }
        else
        {
            bShowServiceError = true;
            bShowResults = false;
	    sErrorMsg = Label.ClaimsSummary_Service_Error;
            return sErrorMsg;
        }
        return  sClaimsSummary;
    }
  
    
    /*
     * Method Name   :  getMemberName
     * Description   :  This method is return membername
     * Return Type   :  String 
     * Parameters    :  ID sRecId
     */
    @testvisible
    private Static String getMemberName(ID sRecId )
    {
       lstPolMember = ClaimsSummary_LD_HUM.getPolicyMemberDetails(sRecId);
       if(NULL != lstPolMember && !lstPolMember.isEmpty())    
       {
           bdisableButton = false;
           bdisableButton = lstPolMember[0].ETL_Record_Deleted__c;
           return lstPolMember[0].Member.Name;
       }
       return NULL;
    }
}
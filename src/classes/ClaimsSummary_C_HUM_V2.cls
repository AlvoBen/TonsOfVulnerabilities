/*****************************************************************************************
Apex class Name    : ClaimsSummary_C_HUM_V2
Version            : 1.0
Function           : Controller to VF Page ClaimSummary_VF_HUM.This class is used to display Claims Summary Page.
Created Date       : 06 Mar 2018
Test Class         : ClaimsSummary_T_HUM_V2

        Modification Log:
 *   Developer                   Code Review             Date               Description
 * ------------------------------------------------------------------------------------------------------------------------------   
 *   Kritika Popat                 24466              12/15/2015          Original Version Claim summary
 *   Rajaram Korivi                25239              04/15/2016           Added security related changes
 *   Harikrishna                   25239              04/15/2016           Added security related changes
 *   HarikaDevi Kalidindi          26321              05/02/2016           For the defect fix: 224498.0001   One member's Claims Summary link is not working when you click it
 *   Manish Kumar                  23769              05/03/2016           For the defect fix: 224903.0001. Fixed DML exception
 *   Naveenkanth B                 27208              05/11/2016           For the defect fix: 223672.0001. 
 *   Prasanthi Kandula             27605              05/17/2016           Defect Fix #226305 Redirect to Claim Detail of click of GO button
 *   Gargey Sharma                 28937              05/27/2016           REQ - 269631 Ability to link to CAS legacy from the Claim Summary Page
 *   Pallavi Ravishankar           30143              06/15/2016           Defect fix : 229874.0001  
 *   Pallavi Ravishankar           30916              06/23/2016           Removed logic to check for leading 0 in client number in infoSet()
 *   Santhi Mandava                40488              09/20/2016           Added logic to retrieve account enterprise id.
 *   Anurag Shah                   201051             03/13/2018           REQ - 354520:: SF - USER - Research/Attachments - MA Post Claims Summary.
 *   Anurag Shah                   209697             04/04/2018           Defect-353259:: REQ-354520_Inconsistent behavior in associating Claims to a Case from Claim Summary Page
 *   Prasanthi Kandula             212620             03/06/2018           REQ - 290133 - Updated logic as part of claims service v3 changes to display 500 claims at a time on button click
 *   Kritika Popat                 212620             03/06/2018           REQ - 355813 - Claims V3 Integration of Summary method.
 *   Praveen Kumar Parimi          230338             05/17/2018           Added the new CAS WEB UI references as part of REQ - 324821.
 *   Anurag Shah                   235263             05/23/2018           Reverting the Resolution changes.
 *   Suresh Gottipati                                 06/15/2018           REQ - 366902 - Claim Summary section in Claim Summary page 
 *   Suresh Sunkari                331209             01/24/2019           REQ - 383744 - Claimssummary_HomeOffice_Message 
 *   Santhi Mandava                                   02/05/2019           Removed dpaas call
 *   Prudhvi Pamarthi                                 02/18/2019           REQ - 361155 - Not allowing case creation on Legacy Deleted policies
 *   Santhi Mandava                                   04/15/2019           Replaced "Member_Id_Value__c" with Name
 *   Yogesh Gupta                                     09/18/2019           REQ - 408299 - Create a CRM Application Launcher - CAS (batch 5 links)
 *   Kapil Arora                                      04/17/2020           US#1021296 MF 1 - AHT - Ability to launch multiple line items from Claim Summary and Auth/Referral Summary Pages (RF)
 *   Sayali Nemade                                    04/07/2020           REQ - 844876  MF 6 - Renaming new links, removing old links 
 *   Sunil Kumar Reddy Y                              04/30/2020           USER STORY 1062501 - User Story 1062501: PR00091574 - MF 1 - Quality - Display informational message at the top of Claim and Auth/Referral Summary pages to provide guidance about CTRL+Click feature (RF)
 *   Sayali Nemade                                    04/07/2020           REQ - 844876  MF 6 - Removed Code
 *   Amar Gadewar                                     06/02/2020           User Story 1145819: PR00091574 - MF 1 - Quality - Display informational message at the top of Claim and Auth/Referral Summary pages to provide guidance about CTRL+Click feature (CRM)
 *   Amar Gadewar                                     06/03/2020           User Story 1145807: PR00091574 - MF 1 - AHT - Ability to launch multiple line items from Claim Summary and Auth/Referral Summary Pages (CRM)
 *   Sayali Nemade				      06/22/2020           REQ - 844876  MF 6 - Renaming new links, removing old links(Applauncher Full Launch)
 *   Suresh Kokkeragadda                              06/24/2020           User Story 1203671: PR00091574 - MF 1 - SF - TECH - On/Off Switch Cleanup 07/07 and 07/17 releases (CRM)
 *   Prasanthi Kandula                                07/22/2020           US - 1031530 Implemented Switch to toggle between claim details V2 and V3
 *   Priyanka Puranik								  1/28/2021				REQ - 1759875 PR00094167 - MF 4 - TECH - SF- CRMS – Legacy App Launcher code clean up MTV, CAS
 *   Priyanka Puranik								  1/29/2021				REQ - 1759875 Removing check for permission set
  *   Prasanthi Kandula                                02/01/2021            US-1669846 SonarQube critical security fixes  
 *   Suraj Patil								   	  04/09/2021			REQ - 1905147 - PR00094226 - CR AEP readiness - Low Security Issues 
*   Samruddhi Rathi                                  05/07/2021            REQ - 2230063 Humana Constant CleanUp 
 *   Prasanthi Kandula                                07/02/2021            US - 2356070 Claims Summary performance improvement 
 * Nirmal Garg										09/16/2021					 Added browser logic for coveo
 * 	 Nirmal Garg									  11/11/2021			Added CRMS switch for coveo
 *********************************************************************************************************************************/
public with sharing class ClaimsSummary_C_HUM_V2
{   
    Public Static final Integer HTTP_OK_HUM = 200;
    Public Static final Integer HTTP_ACCEPTED_HUM = 202; 
    Public Static final String ID_HUM = 'id';
    Public Static final String CASAppName = 'CASLegacy';
	public boolean bCoveoUser {get;set;}
	Public Static final String ChromeBrowser = 'Chrome/';
	Public Static final String EdgeBrowser = 'Edg';

    @TestVisible private ClaimsSummary_S_HUM_V2 oServiceClaim = new ClaimsSummary_S_HUM_V2();
    @TestVisible private ClaimsSummaryHOCheck_C_HUM_V2 oHoClaimCheck = new ClaimsSummaryHOCheck_C_HUM_V2();
    @TestVisible private String sRequest ='';
    @TestVisible private String sGoRequest =''; 
    @TestVisible private List<Policy_Member__c> lstPolMember = new List<Policy_Member__c>(); 
    @TestVisible private ID curentUserID;
    @TestVisible private Boolean bIsMemberAHomeOfficeAcct;
    @TestVisible private Boolean bIsNonHOUser;
    @TestVisible private Boolean bisHomeOfficeAllUser;
    @TestVisible private Boolean bisHomeOfficeMedicalUser;
    @TestVisible private Boolean bisHomeOfficeDentalUser;
    /*
     * Method Name   :  ClaimsSummary_C_HUM_V2
     * Description   :  This is a constructor for class ClaimsSummary_C_HUM_V2
     * Return Type   :  NA
     * Parameters    :  NA
     */
    public ClaimsSummary_C_HUM_V2()
    {
        bHomeOficeMessage = false;
        bShowError = false;
        bINFO = true;
        bShowServiceError = false;
        lstClaimResponse = new List<ClaimsSummary_Wrapper_DTO_HUM>();
        setBA = new Set<String>();
        sRecId = apexpages.currentpage().getparameters().get(ID_HUM);
        sMemName = getMemberName(apexpages.currentpage().getparameters().get(ID_HUM).escapeHtml4());
        sAttachKey = lstPolMember[0].Member__r.Enterprise_ID__c;
        AccountId = lstPolMember[0].Member__c;
        curentUserID = UserInfo.getUserId();
        bIsMemberAHomeOfficeAcct = oHoClaimCheck.isMemberHaveHomeOfficePolicies(AccountId);
        bisHomeOfficeAllUser = oHoClaimCheck.isHomeOfficeAllUser(curentUserID);
        bisHomeOfficeMedicalUser = oHoClaimCheck.isHomeOfficeMedicalUser(curentUserID);
        bisHomeOfficeDentalUser = oHoClaimCheck.isHomeOfficeDentalUser(curentUserID);
        bIsNonHOUser = (bisHomeOfficeAllUser || bisHomeOfficeMedicalUser || bisHomeOfficeDentalUser)?false:true;
        if(bIsMemberAHomeOfficeAcct && bIsNonHOUser) bHomeOficeMessage = true;
        bShowBADropdown = false;
        bShowResults = true;
        objPMMain = new Policy_Member__c();
        setPolicyMembers = ClaimsSummary_D_HUM_V2.getPolicyMembersFromAccount(AccountId);
        sEnvironment = HUMConstants__c.getInstance('APPLAUNCHER_ENVIRONMENT').StringValue__c;
		string sBrowserContext = ApexPages.currentPage().getHeaders().get('USER-AGENT');
		if(string.isNotBlank(sBrowserContext)){
			if(sBrowserContext.contains(ChromeBrowser) || sBrowserContext.contains(EdgeBrowser)){
				 bCoveoUser = HUMUtilityHelper.isCRMFunctionalityON('CRMSUnifiedSearchSwitch') && UnifiedSearchUtility_H_HUM.isUnifiedSearchEligible();
			}
		}
    }
    
    public string AccountId {get;set;} 
    public String sMemName{get;set;}     
    public String sRecId {get;set;}
    public Boolean bHomeOfcMedDen;
    public String sClaimSearch{get;set;}
    public Boolean bINFO;
    public Boolean bShowResults;
    public Boolean bClaimExists{get;set;}
    public String sMsgOnGo{get;set;}
    public String sSeverityOnGo{get;set;}
    public Boolean bDuplicate{get;set;}
    public Boolean bShowError{get;set;}
    public Boolean bShowServiceError{get;set;}
    public Boolean bShowBADropdown;
    public Transient List<ClaimsSummary_Wrapper_DTO_HUM> lstClaimResponse;
    public Set<String> setBA {get;set;}
    public Boolean bHomeOficeMessage{get;set;}
    public Map<String,String> filteredClaimsMap=new Map<String,String>();
    public Policy_Member__c objPMMain {get; set;}
    public String sClaimNbr{get;set;}
    public ClaimsSummary_Wrapper_DTO_HUM claimgotoResponse {get;set;} 
    public String sGCPURL{get;set;}
    public String sCASWEBUrl{get;set;}
    public String sClientNumber{get;set;}
    public String sFirstName{get;set;}
    public String sMemId{get;set;}
    public String sRelation{get;set;}
    public String sApplName{get;set;}
    public String sClmNbrCAS{get;set;}
    public String sAttachKey{get;set;}
    public Integer iTotalRecords {get;set;}
    public String sStartCount1 {get;set;}
    public Set<String> setPolicyMembers;
    public String sClaimsSummary {get;set;}
    public Boolean bdisableButton {get; set;}
    public String sEnvironment{get;set;}
    
    /*
     * Method Name   :  searchClaim
     * Description   :  This method is used for searching claims
     * Return Type   :  NA
     * Parameters    :  NA
     */
    public object searchClaim() 
    {
        try
        { 
            sGoRequest = '';
            bDuplicate = false;
            bClaimExists = false;
            bShowServiceError = false;
            bShowError = false;
            if(String.isNotBlank(sClaimNbr))
            {
                HUM_Webservice_Callout__c oSvcHeaderGoClaims = HUM_Webservice_Callout__c.getValues('ClaimsService_GetClaimsSummary_V3'); 
                Integer iTIMEOUT_INT_SECS = Integer.valueof(oSvcHeaderGoClaims.Timeout__c)/1000;
                HttpRequest requestGo = new HttpRequest();
                oServiceClaim.setupGoButtonRequest(requestGo, oSvcHeaderGoClaims, sClaimNbr);
                Continuation conGo = new Continuation(iTIMEOUT_INT_SECS );
                sGoRequest = conGo.addHttpRequest(requestGo);
                conGo.continuationMethod = 'processGoButtonResponse';
                if(Test.isRunningTest()) throw new HUMCustomException('Error');
                return conGo;
            }
            else    return Null;
        }
        catch(Exception ex)
        {
            bClaimExists = true; 
            sMsgOnGo = Label.ClaimsSummary_Service_Error;
            sSeverityOnGo = 'Error';
            HUMExceptionHelper.logErrors(ex, 'ClaimsSummary_C_HUM_V2', 'searchClaim');
            return NULL;
        }   
    }
    
    /*
    * Method Name   :  processGoButtonResponse
    * Description   :  This method is used to get response from service.
    * Return Type   :  void 
    * Parameters    :  NA
    */
    public void processGoButtonResponse()
    {
        try 
        {
            HttpResponse responseGo = Continuation.getResponse(sGoRequest);
            responseparseGoButton(responseGO);
        }
        catch(Exception ex)
        {
            bClaimExists = true; 
            sMsgOnGo = Label.ClaimsSummary_Service_Error;
            sSeverityOnGo = 'Error';
            HUMExceptionHelper.logErrors(ex, 'ClaimsSummary_C_HUM_V2', 'processGoButtonResponse');
        }
    }
    
    /*
     * Method Name   :  responseparseGoButton
     * Description   :  This method is used to check response
     * Return Type   :  void 
     * Parameters    :  HttpResponse obj
     */
    @testvisible
    private void  responseparseGoButton(HttpResponse sResponseGo)
    {
        Integer statusCode = sResponseGo.getStatusCode();
        if (statusCode == HTTP_OK_HUM || statusCode == HTTP_ACCEPTED_HUM)
        {
            Integer nNumberOfRecords = oServiceClaim.noOnClickGO(sResponseGo.getbody());
            List<ClaimsSummary_Wrapper_DTO_HUM> templstClaimResponse = oServiceClaim.processResponse(sResponseGo.getbody());
            List<ClaimsSummary_Wrapper_DTO_HUM> templstClaimResponseMember = new List<ClaimsSummary_Wrapper_DTO_HUM>();
            for(ClaimsSummary_Wrapper_DTO_HUM objDTo : templstClaimResponse)
            {
                for(String sPolMem : setPolicyMembers)
                {
                    if(sPolMem.equalsIgnoreCase(objDTo.sMemberId.remove('-')))
                    {
                        templstClaimResponseMember.add(objDTo);
                        break;
                    }
                }
            }
            if(templstClaimResponseMember.size() >= 1)
            {
                List<ClaimsSummary_Wrapper_DTO_HUM> lstClaimResponseGoButton = new List<ClaimsSummary_Wrapper_DTO_HUM>();
                ClaimsSecurity_H_HUM cHomeOfficeSecurity = new ClaimsSecurity_H_HUM();
                ID vMemberAccountID = lstPolMember[0].Member__c;
                lstClaimResponseGoButton = cHomeOfficeSecurity.getFilteredClaims(templstClaimResponseMember,vMemberAccountID,bIsMemberAHomeOfficeAcct,bisHomeOfficeAllUser,bisHomeOfficeMedicalUser,bisHomeOfficeDentalUser);
                if(lstClaimResponseGoButton.size() == 1)
                {
                    ClaimsSummary_S_HUM_V2 oClaimService = new ClaimsSummary_S_HUM_V2();
                    claimgotoResponse = oClaimService.SummaryGoButton(lstClaimResponseGoButton,sClaimNbr);
                }
                if((templstClaimResponseMember.size()>lstClaimResponseGoButton.size()) && (lstClaimResponseGoButton.size() == 0))            
                {
                    bClaimExists = true;  
                    sMsgOnGo = Label.Claimssummary_HomeOffice_Message;
                    sSeverityOnGo  = 'Warning';
                }
                if(lstClaimResponseGoButton.size() > 1)
                {
                    sClaimsSummary = '';
                    bDuplicate = true;
                    if(lstClaimResponse!= null && !(lstClaimResponse).isEmpty())    lstClaimResponse.clear();
                    else lstClaimResponse = new List<ClaimsSummary_Wrapper_DTO_HUM>();
                    lstClaimResponse.addAll(lstClaimResponseGoButton);
                    sClaimsSummary = JSON.serialize(lstClaimResponse);
                    sClaimsSummary = '{"data":'+sClaimsSummary+'}';
                    if(lstClaimResponse!= null && !(lstClaimResponse).isEmpty())
                    {
                        iTotalRecords = lstClaimResponse.size();
                        formDropdown(lstClaimResponse);
                    }
                }
            }    
            else
            {   
                bClaimExists = true; 
                sMsgOnGo = Label.ClaimSummary_ClmNbr_NotFound;
                sSeverityOnGo = 'Warning';
            }
        }
        else
        {
            bClaimExists = true;
            sMsgOnGo = Label.ClaimsSummary_Service_Error;
            sSeverityOnGo  = 'Error';
        }
    }
    /*
    * Method Name   :  formDropdown
    * Description   :  This method is used to form Dropdown
    * Return Type   :  NA
    * Parameters    :  List<ClaimsSummary_Wrapper_DTO_HUM>
    */
    public void formDropdown(List<ClaimsSummary_Wrapper_DTO_HUM> lstClaimResponse1)
    {
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
        if(lstPolMember[0].Member__r.FirstName!=null)sFirstName = lstPolMember[0].Member__r.FirstName;
        if(lstPolMember[0].Relationship_Code__c!=null)
        {
            list<LVRelationShipCodeToCAS__c> lstlvcas = ClaimsSummary_D_HUM_V2.getLVCASTranslationDetails(lstPolMember[0].Relationship_Code__c);
            if((!lstlvcas.isEmpty()) && lstlvcas[0].CASTranslation__c!=null)sRelation = lstlvcas[0].CASTranslation__c;
        }
        }
        list<Member_ID__c> lstmemIdObj= ClaimsSummary_D_HUM_V2.getMemberIDObjDetails(apexpages.currentpage().getparameters().get(ID_HUM).escapeHTML4());
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
    public object initiateRequest()
    {
        try
        { 
            sClaimsSummary = '';
            sRequest = '';
            sRecId = apexpages.currentpage().getparameters().get(ID_HUM);
            if(String.isNotBlank(sRecId))
            {
                objPMMain = lstPolMember[0]; 
                HUM_Webservice_Callout__c oSvcHeaderClaims = HUM_Webservice_Callout__c.getValues('ClaimsService_GetClaimsSummary_V3'); 
                Integer iTIMEOUT_INT_SECS = Integer.valueof(oSvcHeaderClaims.Timeout__c)/1000;
                HttpRequest request = new HttpRequest();
                oServiceClaim.setupRequest(request,oSvcHeaderClaims, sRecId.escapeHtml4(),sStartCount1,setPolicyMembers);
                Continuation con = new Continuation(iTIMEOUT_INT_SECS );
                sRequest = con.addHttpRequest(request);
                con.continuationMethod='processResponse';
                if(Test.isRunningTest()) throw new HUMCustomException('Error');
                return con;
            }
            else    return Null;
        }
        catch(Exception ex)
        {
            bShowServiceError = true;
            ApexPages.Message myMsg = new ApexPages.Message(ApexPages.Severity.Error,Label.ClaimsSummary_Service_Error);
            ApexPages.addMessage(myMsg);
            HUMExceptionHelper.logErrors(ex, 'ClaimsSummary_C_HUM_V2', 'initiaterequest');
            return NULL;
        }
    }
    
    /*
    * Method Name   :  processResponse
    * Description   :  This method is used to get response from service.
    * Return Type   :  void 
    * Parameters    :  NA
    */
    public void processResponse()
    {
        try 
        {
            HttpResponse response = Continuation.getResponse(sRequest);
            responseparse(response);
        }
        catch(Exception ex)
        {
            bShowServiceError = true;
            ApexPages.Message myMsg = new ApexPages.Message(ApexPages.Severity.Error,Label.ClaimsSummary_Service_Error);
            ApexPages.addMessage(myMsg);
            HUMExceptionHelper.logErrors(ex, 'ClaimsSummary_C_HUM_V2', 'processResponse');
        }
    }
    
   /*
     * Method Name   :  responseparse
     * Description   :  This method is used to check response
     * Return Type   :  void 
     * Parameters    :  HttpResponse obj
     */
    @testvisible
    private void  responseParse(HttpResponse sResponse)
    {
        sClaimsSummary = '';
        Integer statusCode = sResponse.getStatusCode();
        if (statusCode == HTTP_OK_HUM || statusCode == HTTP_ACCEPTED_HUM)
        {
            List<ClaimsSummary_Wrapper_DTO_HUM> templstClaimResponse = oServiceClaim.processResponse(sResponse.getbody());
            bDuplicate = false;
            bClaimExists = false;
            ClaimsSecurity_H_HUM cHomeOfficeSecurity=new ClaimsSecurity_H_HUM();
            ID vMemberAccountID = lstPolMember[0].Member__c; 
            lstClaimResponse = cHomeOfficeSecurity.getFilteredClaims(templstClaimResponse,vMemberAccountID,bIsMemberAHomeOfficeAcct,bisHomeOfficeAllUser,bisHomeOfficeMedicalUser,bisHomeOfficeDentalUser);
            sClaimsSummary = JSON.serialize(lstClaimResponse);
            sClaimsSummary = '{"data":'+sClaimsSummary+'}';
            if(templstClaimResponse.size()>lstClaimResponse.size() || bHomeOficeMessage )            
            {
               bHomeOficeMessage = true; 
               ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.Warning, Label.Claimssummary_HomeOffice_Message ));
            }
            if(lstClaimResponse!= null && !(lstClaimResponse).isEmpty())
            {
                bShowResults = oServiceClaim.bviewresults;
                ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.Info,Label.Claim_Summary_guidance_HUM));
                iTotalRecords = oServiceClaim.iTotalRecordsFromService;
                formDropdown(lstClaimResponse);
            }
            if(((lstClaimResponse).isEmpty()) || (lstClaimResponse == null))
            {
                bShowError = true;
                bShowResults = false;
                ApexPages.Message myMsg = new ApexPages.Message(ApexPages.Severity.Warning,Label.Claimssummary_No_Records);
                ApexPages.addMessage(myMsg);
            }
        }
        else
        {
            bShowServiceError = true;
            bShowResults = false;
            ApexPages.Message myMsg = new ApexPages.Message(ApexPages.Severity.Error,Label.ClaimsSummary_Service_Error);
            ApexPages.addMessage(myMsg);
        }
    }
  
    
    /*
     * Method Name   :  getMemberName
     * Description   :  This method is return membername
     * Return Type   :  String 
     * Parameters    :  ID sRecId
     */
    @testvisible
   private String getMemberName(ID sRecId )
    {
       lstPolMember = ClaimsSummary_D_HUM_V2.getPolicyMemberDetails(sRecId);
       if(NULL != lstPolMember && !lstPolMember.isEmpty())    
       {
           bdisableButton = false;
           bdisableButton = lstPolMember[0].ETL_Record_Deleted__c;
           return lstPolMember[0].Member__r.Name;
       }
       return NULL;
    }
}
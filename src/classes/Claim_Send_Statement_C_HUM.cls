/*****************************************************************************************
Apex class Name    : Claim_Send_Statement_C_HUM
Version            : 1.0
Function           : This is the controller class for claim send statment
Created Date       : 10/04/2018 
Test Class         : Claim_Send_Statement_C_T_HUM

Modification Log:
*   Developer                   Code Review             Date               Description
* ------------------------------------------------------------------------------------------------------------------------------   
*  Vijaya Koriprolu                                    10/04/2018          origina Version
*  Kritika Popat                295053                 09/28/2018          Added Submit Request Functionality
* Shivam Anand                                            11/05/2018            REQ - 371426- Logging for Member Claims
* Vijaya K                                                11/05/2018            REQ - 371427- Logging for Provide claims
*  Kritika Popat                                       11/20/2018          Fax Recipient Name Break Fix
*  Suresh Sunkari                                      07/12/2019          REQ - 388215 -  Send Claim Statement service fix 
*  Suresh Sunkari                                      07/31/2019          Rollback REQ - 388215 -  Send Claim Statement service fix 
*  Suresh Sunkari                                      08/08/2019          REQ - 406050 - Statement Type PROD issues
*  Vijaya Koriprolu                                    10/24/2019          REQ – 403631 implementation
*  Prasanthi Kandula                                   04/16/2020          US 1003374 - To display send print page when redirected from RTI     
*  Prasanthi Kandula                                   04/28/2020          Defect Fix - 585 to disable mail address fields on click of Adress Verification button          
*  Prasanthi Kandula                                   05/06/2020          Modified providerAddress method to handle DML exception  
* Trina  Ghosh                                         07/27/2020          US# 965352
* Prasanthi Kandula                                    02/01/2021          US-1669846 SonarQube critical security fixes  
* Rahul Chaurasia                                      03/05/2021          REQ - 1905086 - T1PRJ0002606- SF- Mobius Services old version Decommission
* Ranadheer                                            04/19/2021          US 2187946, Convert the Memeber Gen Key to match the backend
* Satyanarayana Chenna        2077440                  04/27/2021          US-T1PRJ0002736 - 2021 MF6  - CR AEP readiness - Low Security Issues   
* Ranadheer                                            05/06/2021          XSS vulnerabilities removal

*********************************************************************************************************************************/
public with sharing class Claim_Send_Statement_C_HUM{
    
    @TestVisible private Claim_Send_Statement_S_HUM oServiceClaimSend = new Claim_Send_Statement_S_HUM();
    @TestVisible private String sRequest ='';
    @TestVisible private ClaimSendStmt_S_HUM oServiceSendStmtDtl = new ClaimSendStmt_S_HUM();
    @TestVisible private String sServiceRequest ='';
    @TestVisible private String sProvID = '';
    @TestVisible private String sProvIDType = '';
    
    public Boolean bShowError {get;set;}
    public Boolean bMessageInfo{get;set;}
    public Boolean bDisAddressVef{get;set;}
    public Boolean bInputParams{get;set;}
    public Boolean isFullAddress{get;set;}
    public string sClaimNumber {get; set;}
    public id sPolMemID {get; set;}
    public string sStatementType {get; set;}
    public string sDateSent {get; set;}
    public string sStatementPeriod {get; set;}
    public string sRemitId {get; set;}
    public string shidepopup{get;set;}
    public string sEndStatementPeriod {get; set;}
    public string sRecipientType {
        get; 
        set {
            sRecipientType = ESAPI_C_HUM.encoder().SFDC_HTMLENCODE(value);
        }
    }
    public string sCallRedirect{get; private set;}
    Public Account accID;
    public String selectedValue {
        get; 
        set {
            selectedValue = ESAPI_C_HUM.encoder().SFDC_HTMLENCODE(value);
        }
    }
    public string faxNumber {
        get; 
        set {
            faxNumber = ESAPI_C_HUM.encoder().SFDC_HTMLENCODE(value);
        }
    }
    Boolean bDataerror{get;set;}
    public String repName {
        get; 
        set {
            repName = ESAPI_C_HUM.encoder().SFDC_HTMLENCODE(value);
        }
    }
    public String sMailrepName {
        get; 
        set {
            sMailrepName = ESAPI_C_HUM.encoder().SFDC_HTMLENCODE(value);
        }
    }
    public Boolean bChangeIndicator {get;set;}
    public Boolean isAddress {get;set;}
    Public Boolean isFaxNull {get;set;}
    public Boolean bShowServiceError {get;set;}
    public Boolean SuccessFlag{get; set;}
    public String callinitiReq{get;set;}
    public String sDocumentKey{get;set;}
    public Boolean displayPopup {get;set;}
    public String sStatementTypeMobValue{get;set;}
    Public String sRefAddressLine1 {
        get; 
        set {
            sRefAddressLine1 = ESAPI_C_HUM.encoder().SFDC_HTMLENCODE(value);
        }
    }
    Public String sRefAddressline2 {
        get; 
        set {
            sRefAddressline2 = ESAPI_C_HUM.encoder().SFDC_HTMLENCODE(value);
        }
    }
    Public String sRefCity {
        get; 
        set {
            sRefCity = ESAPI_C_HUM.encoder().SFDC_HTMLENCODE(value);
        }
    }
    Public String sRefStatecode{
        get; 
        set {
            sRefStatecode = ESAPI_C_HUM.encoder().SFDC_HTMLENCODE(value);
        }
    }
    Public String sRefZipCode {
        get; 
        set {
            sRefZipCode = ESAPI_C_HUM.encoder().SFDC_HTMLENCODE(value);
        }
    }
    public transient Claim_Send_Statement_DTO_HUM oClaimSendWrap {get; private set;}
    public Boolean bIsErrorMail{get; private set;}
    public Boolean isMobile{get; private set;}
    public Boolean bProviderInt{get; private set;}
    public Boolean bError{get; private set;}
    public Boolean bWarning{get; private set;}
    public Id SSubmissionID {get;set;}
    public List<account> accIDTochk;
    public String sObjFirstName {
        get; 
        set {
            sObjFirstName = ESAPI_C_HUM.encoder().SFDC_HTMLENCODE(value);
        }
    }
    public String sObjLastName {
        get; 
        set {
            sObjLastName = ESAPI_C_HUM.encoder().SFDC_HTMLENCODE(value);
        }
    }
    public String sJsFirstName {
        get; 
        set {
            sJsFirstName = ESAPI_C_HUM.encoder().SFDC_HTMLENCODE(value);
        }
    }
    public String sJsLastName {
        get; 
        set {
            sJsLastName = ESAPI_C_HUM.encoder().SFDC_HTMLENCODE(value);
        }
    }
    public String sObjName {
        get; 
        set {
            sObjName = ESAPI_C_HUM.encoder().SFDC_HTMLENCODE(value);
        }
    }
    public String sJsName {
        get; 
        set {
            sJsName = ESAPI_C_HUM.encoder().SFDC_HTMLENCODE(value);
        }
    }
    public string sApplicationKey{get;set;}
    public Id sTemplateOwnerId {get;set;}
    public boolean bIsPageFromRTI {get; set;}
    public string smemberName {get; set;}
    public string sCategoryDesc {get; set;}
    public string sMessageName {get; set;}
    public string sRTIPageId {get; set;}
    public string sSourceCode {get; set;}
    
    Public List<Error_Log__c> lstLogErrors
        
    {
        get
        {
            if(lstLogErrors == Null)
            {
                lstLogErrors = new List<Error_Log__c>();
            } 
            return lstLogErrors;
        }
        set;
    }
    private boolean bIsDataAvailable;
    public Claim_Send_Statement_C_HUM()
    {
        selectedValue = 'Mail';
        isMobile =false;
        bDataerror = false;
        bMessageInfo = true;
        bIsErrorMail = false;
        bChangeIndicator = False;
        isAddress = false;
        bDisAddressVef = False;
        isFaxNull = True;
        SuccessFlag = false;
        isFullAddress =false;
        bError = false;
        bWarning = false;
        bProviderInt = false;
        sSourceCode = '';
        bIsPageFromRTI = getHTMLEncodedPageParameter('fromPage') == 'RTIDetail';
        sMemberName = getHTMLEncodedPageParameter('accName');
        sCategoryDesc = getHTMLEncodedPageParameter('catDesc');
        sMessageName = getHTMLEncodedPageParameter('msgName');
        sRTIPageId = getHTMLEncodedPageParameter('rtiPageId');
        sStatementType = getHTMLEncodedPageParameter('statementType');
        sPolMemID = getHTMLEncodedPageParameter('pid'); 
        sDateSent = getHTMLEncodedPageParameter('DateSent');
        sStatementPeriod = getHTMLEncodedPageParameter('StatementPeriod');
        sSourceCode = getHTMLEncodedPageParameter('sSourceCode');
        if(sStatementPeriod == null || sStatementPeriod=='')
        {
            sStatementPeriod = '';
        }
        sRemitId = getHTMLEncodedPageParameter('RemitId');
        sEndStatementPeriod = getHTMLEncodedPageParameter('EndPeriod');
        if(sEndStatementPeriod == null || sEndStatementPeriod=='')
        {
            sEndStatementPeriod = '';
        }
        sClaimNumber = getHTMLEncodedPageParameter('ClaimNumber');
        sDocumentKey = getHTMLEncodedPageParameter('DocKey');
        sApplicationKey = GLOBAL_CONSTANT_HUM.MOBIUS_GETDOCUMENT_APPLICATIONKEY;
        sStatementTypeMobValue = getHTMLEncodedPageParameter('StatementTypeSerValue');
        if(sStatementTypeMobValue == null || sStatementTypeMobValue=='')
        {
            sStatementTypeMobValue = 'Traditional EOB';
        }
        if(string.isNotBlank(sRTIPageId))
        {
            accIDTochk = getLstAccountDetails(getHTMLEncodedPageParameter('rtiPageId'));
        }
        if(sPolMemID != null)
        {
            accIDTochk = [Select id,name,Fax, FirstName,LastName,IsPersonAccount,PersonMailingStreet,PersonMailingState,PersonMailingCity,PersonMailingStateCode,PersonMailingPostalCode, PersonMobilePhone, PersonHomePhone, PersonOtherPhone from account where id IN (Select member__c from Policy_Member__c where id=:getHTMLEncodedPageParameter('pid'))];
        }
        if((bIsPageFromRTI && string.isNotBlank(sRTIPageId)) || (sStatementType == 'Member' && sPolMemID != null))
        {
            bProviderInt = true; 
            memberAddress();   
        }
        else if(sStatementType == 'Provider' && sPolMemID != null)
        {
            providerAddress();
        }
        
    }
    
    /*
* Method Name   :  getString
* Description   :  This method is used get selected value
* Return Type   :  void 
* Parameters    :  NA
*/
    
    public String getString() {
        return selectedValue;
    }
    
    /*
* Method Name   :  setString
* Description   :  This method is used set the selected value to string
* Return Type   :  void 
* Parameters    :  NA
*/
    
    public void setString(String selectedValue) {
        this.selectedValue = selectedValue;
    }
    
    /*
* Method Name   :  memberAddress
* Description   :  This method is used get member details
* Return Type   :  void 
* Parameters    :  NA
*/
    public void memberAddress()
    {
        if(string.isNotBlank(sRTIPageId))
            accID = getAccountDetails(getHTMLEncodedPageParameter('rtiPageId'));
        else
            accID = getAccountDetails(getHTMLEncodedPageParameter('pid'));
        sRefAddressLine1 =  accId.PersonMailingStreet;
        sRefAddressline2 = sRefAddressline2;
        sRefStatecode = accID.PersonMailingStateCode;
        sRefCity = accID.PersonMailingCity;
        sRefZipcode = accID.PersonMailingPostalCode;
        sObjFirstName = accID.FirstName;
        sObjLastName = accID.LastName;
    }
    
    /*
* Method Name   :  providerAddress
* Description   :  This method is used get provider details
* Return Type   :  void 
* Parameters    :  NA
*/
    
    public void providerAddress()
    {    
        Policy_Member__c objPolMember = [Select Member__r.Id from Policy_Member__c where Id =:getHTMLEncodedPageParameter('pid')];
        DateTime currentLogin = [SELECT LastLoginDate FROM User WHERE Id =:UserInfo.getUserId()].LastLoginDate;
        if(objPolMember!= null && currentLogin != null)
        {
            List<Interaction__c> lstInteraction  = [Select Id, CreatedbyID, CreatedDate,interacting_with__c,LastModifiedDate,Interacting_With_type__c from Interaction__c where CreatedbyId =:String.escapeSingleQuotes( UserInfo.getUserId()) and LastModifiedDate >= :currentLogin order by LastModifiedDate desc limit 1];
            if(!lstInteraction.isEmpty() && lstInteraction[0] != null && lstInteraction[0].Interacting_With_type__c != null)
            {
                if(lstInteraction[0].Interacting_With_type__c.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.AUTHDETAIL_PROVIDER_HUM))
                {
                    List<Interaction_Member__c> lstIntrcMember = [Select Id, Interacting_About__r.ShippingPostalCode, Interacting_About__r.ShippingCity, Interacting_About__r.ShippingStateCode, Interacting_About__r.ShippingStreet, CreatedbyID, CreatedDate, Interaction__c, Interacting_About__c,LastModifiedDate from Interaction_Member__c  where CreatedbyId =: UserInfo.getUserId() and Interaction__c =: lstInteraction[0].Id and Interacting_About__c =: objPolMember.Member__r.Id order by LastModifiedDate desc limit 1];
                    if(lstIntrcMember != null && !lstIntrcMember.isEmpty())    
                    {
                        bProviderInt = true;
                        accID = [SELECT Id,Name,Enterprise_ID__c,Source_Platform_Code__c,ShippingStreet, ShippingCity,ShippingStateCode,ShippingPostalCode, ShippingState, ShippingCountry , ShippingCountryCode FROM Account where id =: lstInteraction[0].interacting_with__c limit 1];
                        sRefAddressLine1 =  accId.ShippingStreet;
                        sRefAddressline2 = sRefAddressline2;
                        sRefStatecode = accID.ShippingStateCode;
                        sRefCity = accID.ShippingCity;
                        sRefZipcode = accID.ShippingPostalCode;
                        sObjName = accID.Name;
                        sProvIDType = accID.Source_Platform_Code__c;
                        sProvID = accID.Enterprise_ID__c;
                    } 
                    else
                    {
                        bWarning = true;
                    }   
                }
                else
                {
                    bWarning = true;
                }
            }
            else
            {
                bError = true;
            }
        }
    }
    
    
    /*
* Method Name   :  ValidateFax
* Description   :  This method is used validate conditions
* Return Type   :  void 
* Parameters    :  NA
*/
    
    public void ValidateFax(){
        Integer result = faxNumber.length();
        if(String.isBlank(repName))
        {
            isFaxNull = True;
            ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.FATAL, 'Recipient Name: You must enter a value'));
        }
        else if(result < 12){
            if(result > 0){
                isFaxNull = True;
                
                ApexPages.addMessage(New ApexPages.Message(ApexPages.Severity.FATAL, 'Fax number must be 10 digits'));  
            }
        }
        else {
            isFaxNull = False;
            if(sStatementType == 'Provider')
            {
                if(accIDTochk[0].PersonHomePhone != null)
                {
                    string faxReplace = accIDTochk[0].PersonHomePhone.replace('-', '');
                    string faxReplacet = faxReplace.replace(') ', '');
                    string faxReplaceth = faxReplacet.replace('(', '');
                    string faxNumberFull  = faxNumber.replace('-', '');
                    
                    if(faxNumberFull.equals(faxReplaceth))
                        
                    {
                        isMobile =true;
                    }
                }
            }
            
        }
    }
    
    
    /*
* Method Name   :  Validate
* Description   :  This method is used validate conditions
* Return Type   :  void 
* Parameters    :  NA
*/
    Public void Validate()
    {}
    
    /*
* Method Name   :  initiateRequest
* Description   :  This method is used to call the Service when clicked on Address verification
* Return Type   :  continuation
* Parameters    :  NA
*/       
    public Object initiateRequest()
    { 
        try
        {
            isFullAddress = false;
            bDataerror = false;
            bIsErrorMail = false;
            if(String.isBlank(sMailrepName))
            {
                sCallRedirect = '';
                oClaimSendWrap=null;
                callinitiReq='';
                ApexPages.Message myMsg = new ApexPages.Message(ApexPages.Severity.FATAL,'Recipient Name: You must enter a value');
                ApexPages.addMessage(myMsg);
                return null;
                
            }else if(String.isBlank(sRefAddressLine1))
            {
                sRequest= null;
                callinitiReq='';
                ApexPages.Message myMsg = new ApexPages.Message(ApexPages.Severity.FATAL,'Address Line 1: You must enter a value');
                ApexPages.addMessage(myMsg);
                return null;
                
            }else if(String.isBlank(sRefCity))
            {
                callinitiReq='';
                bIsErrorMail = true;
                sRequest= null;
                oClaimSendWrap=null;
                ApexPages.Message myMsg = new ApexPages.Message(ApexPages.Severity.FATAL,'City: You must enter a value');
                ApexPages.addMessage(myMsg);
                return null;
                
            }else if(String.isBlank(sRefStateCode))
            {
                bIsErrorMail =true;
                sRequest= null;
                callinitiReq='';
                oClaimSendWrap=null;
                ApexPages.Message myMsg = new ApexPages.Message(ApexPages.Severity.FATAL,'State: You must enter a value');
                ApexPages.addMessage(myMsg);
                return null;
                
            }else if(String.isBlank(sRefZipcode))
            {
                bIsErrorMail = true;
                callinitiReq='';
                sRequest= null;
                oClaimSendWrap=null;
                ApexPages.Message myMsg = new ApexPages.Message(ApexPages.Severity.FATAL,'Zip Code: You must enter a value');
                ApexPages.addMessage(myMsg);
                return null;
                
            }
            else if(!String.isBlank(sRefZipcode) && !Pattern.matches('^[0-9]{5,5}+$', sRefZipcode))
            {
                bDataerror = true;
                bIsErrorMail = true;
                callinitiReq='';
                sRequest= null;
                oClaimSendWrap=null;
                ApexPages.Message myMsg = new ApexPages.Message(ApexPages.Severity.FATAL,'Please enter 5 digit Zip Code Value');
                ApexPages.addMessage(myMsg);
                return null;
            }
            else if(!String.isBlank(sRefStatecode) && !Pattern.matches('^[a-zA-Z]{2,2}+$', sRefStatecode))
            {
                bDataerror = true;
                bIsErrorMail = true;
                callinitiReq='';
                sRequest= null;
                oClaimSendWrap=null;
                ApexPages.Message myMsg = new ApexPages.Message(ApexPages.Severity.FATAL,'Please enter correct state code value');
                ApexPages.addMessage(myMsg);
                return null;
            }
            
            else if(sRefAddressLine1.equalsIgnoreCase(accIDTochk[0].PersonMailingStreet) &&  sRefStatecode.equalsIgnoreCase(accIDTochk[0].PersonMailingStateCode) &&  sRefCity.equalsIgnoreCase(accIDTochk[0].PersonMailingCity) && sStatementType == 'Provider')
            {
                isFullAddress = true;
                return null;
            }
            else if(String.isNotBlank(sMailrepName) && String.isNotBlank(sRefAddressLine1) && String.isNotBlank(sRefStatecode) && String.isNotBlank(sRefCity) && String.isNotBlank(sRefZipcode) && Pattern.matches('^[a-zA-Z]{2,2}+$', sRefStatecode) && Pattern.matches('^[0-9]{5,5}+$', sRefZipcode) && !bIsErrorMail && !isFullAddress && !bDataerror )
            {  
                HUM_Webservice_Callout__c oSvcAddress = HUM_Webservice_Callout__c.getValues('Claims_AddressStandardization'); 
                Integer iTIMEOUT_INT_SECS = Integer.valueof(oSvcAddress.Timeout__c)/1000;
                HttpRequest request = new HttpRequest();
                
                
                oServiceClaimSend.setupRequest(request, oSvcAddress,sRefAddressLine1,sRefAddressLine2,sRefCity,sRefStatecode,sRefZipcode);
                Continuation con = new Continuation(iTIMEOUT_INT_SECS);
                sRequest = con.addHttpRequest(request);
                con.continuationMethod='processResponse';
                return con;
            }
            
            
            
            
            else
            {
                bShowServiceError = true;
                isFullAddress = false;
                isMobile = false;
                String sError = System.Label.EncryptRestService_No_records;
                ApexPages.Message myMsg = new ApexPages.Message(ApexPages.Severity.FATAL,'Section Information did not load');
                ApexPages.addMessage(myMsg);
                return Null;            
            }
        }  
        
        
        
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'Claim_Send_Statement_C_HUM', 'initiateRequest');
            return NULL;          
        }
    }
    
    /*
* Method Name   :  processResponse
* Description   :  This process response method is used to call the Service when clicked on Address verification
* Return Type   :  Void
* Parameters    :  NA
*/    
    
    public void processResponse()
    {     
        try 
        { 
            HttpResponse response = Continuation.getResponse(sRequest);
            if(response != null)
                responseparse(response);
        }
        catch(Exception ex)
        {  
            bShowServiceError = true;
            String sError = System.Label.EncrytRest_Service_Error;
            HUMExceptionHelper.bRestrictLog = true;
            lstLogErrors = HUMExceptionHelper.logErrors(ex,'Claim_Send_Statement_C_HUM','processResponse');   
            
        }
    }
    
    /*
* Method Name   :  responseParse
* Description   :  This method is used to check response
* Return Type   :  void 
* Parameters    :  HttpResponse obj
*/
    
    @testvisible public void responseParse(HttpResponse sResponse)
    {
        
        Integer iStatusCode = sResponse.getStatusCode();
        
        if (iStatusCode == GLOBAL_CONSTANT_HUM.HTTP_OK_HUM || iStatusCode == GLOBAL_CONSTANT_HUM.HTTP_ACCEPTED_HUM)
        { 
            oServiceClaimSend.processResponse(sResponse.getbody());
            bInputParams = false;
            oClaimSendWrap = oServiceClaimSend.clmSendWrapObj;
            if(oClaimSendWrap.sValid != null)
            {
                if((oClaimSendWrap.sValid == 'true' && (oClaimSendWrap.sChangeIndicator =='GOOD' || oClaimSendWrap.sChangeIndicator =='Good') && !isAddress) && !isFullAddress)
                {
                    bInputParams = bIsPageFromRTI;
                    bMessageInfo = false;
                    bDisAddressVef = true;
                    SuccessFlag = true;
                    sRefAddressLine1 =  oClaimSendWrap.sAddressLine1;
                    sRefStatecode = oClaimSendWrap.sStateCode;
                    sRefCity = oClaimSendWrap.sCity;
                    sRefZipcode = oClaimSendWrap.sZipCode;
                    if(sRefAddressline2 != null)
                    {
                        sRefAddressline2 = '';
                    }
                }
                else if((oClaimSendWrap.sValid == 'true' && (oClaimSendWrap.sChangeIndicator =='Change' || oClaimSendWrap.sChangeIndicator =='CHANGE')) && !isFullAddress)
                {
                    bInputParams = bIsPageFromRTI;
                    bMessageInfo = true;
                    bDisAddressVef = true;
                    bChangeIndicator = true;
                    SuccessFlag = true;
                    sRefAddressLine1 =  oClaimSendWrap.sAddressLine1;
                    sRefStatecode = oClaimSendWrap.sStateCode;
                    sRefCity = oClaimSendWrap.sCity;
                    sRefZipcode = oClaimSendWrap.sZipCode;
                    if(sRefAddressline2 != null)
                    {
                        sRefAddressline2 = '';
                    }
                }
                else if(oClaimSendWrap.sValid == 'false')
                {
                    bMessageInfo = true;
                    SuccessFlag = true;
                    sCallRedirect = '<script>SendStatement.onNewClick(); </script>';
                }
            }
        }   
    }
    
    /*
* Method Name   :  closePopup
* Description   :  This method is used to close the popup
* Return Type   :  void 
* Parameters    :  NA
*/
    public void closePopup()
    {
        Successflag= true;
        bInputParams = bIsPageFromRTI;
        bDisAddressVef = bIsPageFromRTI;
        sCallRedirect = '';
    }
    
    /*
* Method Name   :  redirectPopup
* Description   :  This method is used to redirect the popup yes seclection
* Return Type   :  PageReference 
* Parameters    :  NA
*/
    
    public PageReference redirectPopup()
    {
        displayPopup = false;
        PageReference pageRef = new PageReference(System.currentPageReference().getURL());
        pageRef.setRedirect(true);
        return pageRef;
    } 
    
    /*
* Method Name   :  checkSelected
* Description   :  This method is used to checkSelected value
* Return Type   :  void
* Parameters    :  NA
*/
    public void checkSelected(){
        
    }  
    
    
    /*
* Method Name   :  initiateSendStmtRequest
* Description   :  This method is used to call the Service on click of Submit Request
* Return Type   :  continuation
* Parameters    :  NA
*/
    public object initiateSendStmtRequest()
    {
        try
        { 
            
            String sMemberGenKey = '';
            String sRFN = '';
            String sRLN = '';
            String sSDRPersonId = '';
            String sMbrId = '';
            String sDptCode = '';
            String sName = '';
            String sType = '';
            String sMsgFulfillmentId = '';
            String sROrgName = '';
            String sPlatformCode = '';
            String sAttachmentCategory = '';
            String sReprintSystemOfRecord ='';
            
            String sDistributionChnl = ((selectedValue != null || String.isNotEmpty(selectedValue)) ? selectedValue : '');
            String sRAdrLine1 = ((sRefAddressLine1 != null || String.isNotEmpty(sRefAddressLine1)) ? sRefAddressLine1 : '');
            String sRAdrLine2 = ((sRefAddressLine2 != null || String.isNotEmpty(sRefAddressLine2)) ? sRefAddressLine2 : '');
            String sRCity = ((sRefCity != null || String.isNotEmpty(sRefCity)) ? sRefCity : '');
            String sRState = ((sRefStatecode != null || String.isNotEmpty(sRefStatecode)) ? sRefStatecode : '');
            String sRZip = ((sRefZipcode != null || String.isNotEmpty(sRefZipcode)) ? sRefZipcode : '');
            String sRFaxNbr = ((faxNumber != null || String.isNotEmpty(faxNumber)) ? faxNumber : '');
            
            
            if(selectedValue != null || String.isNotEmpty(selectedValue))
            {
                if(selectedValue == 'Mail')    
                {
                    sDistributionChnl = 'Print';
                    if(sMailrepName != null || String.isNotEmpty(sMailrepName))    sROrgName = sMailrepName;
                }
                else    
                {
                    sDistributionChnl = selectedValue;
                    if(repName != null || String.isNotEmpty(repName))    sROrgName = repName;    
                }
            }
            
            
            sRFN = sJsFirstName;
            sRLN = sJsLastName;
            sName = sJsName;
            if(sStatementType == 'rtiMember')
            {
                sMsgFulfillmentId = getHTMLEncodedPageParameter('fulfilId');
                sMbrId = getHTMLEncodedPageParameter('memberId');
                sDptCode =  getHTMLEncodedPageParameter('dependentCd');
                sMemberGenKey = getHTMLEncodedPageParameter('personGenkey');
                sSDRPersonId = getHTMLEncodedPageParameter('sdrId');
                sPlatformCode = getHTMLEncodedPageParameter('platformCd');
                if(sSourceCode.equalsIgnoreCase('EMCON'))
                    sReprintSystemOfRecord = 'History';
                else if(sSourceCode.equalsIgnoreCase('CP') || sSourceCode.equalsIgnoreCase('Control Point') )
                    sReprintSystemOfRecord = 'CP';
                else if(sSourceCode.equalsIgnoreCase('EMME'))
                    sReprintSystemOfRecord = sSourceCode;                
            }
            else
            {
                String sMsgFulfillment = getHTMLEncodedPageParameter('DocKey');
                if(sMsgFulfillment != null || String.isNotEmpty(sMsgFulfillment))    sMsgFulfillmentId = sMsgFulfillment.escapeHtml4().substringAfter('DocumentKey');
                String sPolicyMemberId = sPolMemID;//getHTMLEncodedPageParameter('pid');
                String sMemberId = getHTMLEncodedPageParameter('mid');
                sPlatformCode = getHTMLEncodedPageParameter('pCode');
                List<POLICY_Member__C> lstPolicyMem = getAccDetails(getHTMLEncodedPageParameter('pid'));
                if(NULL != lstPolicyMem && !lstPolicyMem.isEmpty())    
                {
                    sMemberGenKey = getHTMLEncodedPageParameter('personGenkey');
                    sSDRPersonId = lstPolicyMem[0].Member__r.Enterprise_ID__c;
                }
                if(sMemberId.escapeHtml4() != null || String.isNotEmpty(sMemberId.escapeHtml4()))
                {
                    sMbrId = sMemberId.substringBefore('-');
                    sDptCode = sMemberId.substringAfter('-'); 
                }
            }
            
            if(sStatementType.escapeHtml4() != null || String.isNotEmpty(sStatementType.escapeHtml4()))
            {
                if(sStatementType == 'Member' || sStatementType == 'rtiMember')
                {
                    if(sRecipientType == 'Member')    sType = 'Member';
                    if(sRecipientType == 'Member Representative or Caregiver')    sType = 'Member';
                    if(sRecipientType == 'Other')    sType = 'Member';
                    
                    if(sStatementType == 'rtiMember'){                        
                        if(sCategoryDesc.equalsIgnoreCase('EOB') || sCategoryDesc.equalsIgnoreCase('Id card')||sCategoryDesc.equalsIgnoreCase('Letter') ||sCategoryDesc.equalsIgnoreCase('Renewal Packet' )|| sCategoryDesc.equalsIgnoreCase('Smart Summary'))
                            sAttachmentCategory = sCategoryDesc;
                        else if(sCategoryDesc.equalsIgnoreCase('EXPLANATION OF BENEFITS'))
                            sAttachmentCategory = 'EOB' ;
                        else
                            sAttachmentCategory = 'Other';
                    }else if(sStatementType == 'Member'){
                        List<String> smartSummaryCodeValues = new List<String> {'COM','PDP','SSA','FAM','H1F','H1I','MAPD','XBSI','YBSI','ZBSI'};
                            List<String> eobCodeValues = new List<String>{'EBSF','EBSI','XBSF','EOBSTMT'};
                                if(eobCodeValues.contains(sStatementTypeMobValue))
                                sAttachmentCategory = 'EOB';
                        else if(smartSummaryCodeValues.contains(sStatementTypeMobValue))
                            sAttachmentCategory = 'Smart Summary';
                        else if(sStatementTypeMobValue.equalsIgnoreCase('Traditional EOB'))
                            sAttachmentCategory = 'EOB';
                        else
                            sAttachmentCategory = 'Other';
                        
                        sReprintSystemOfRecord = 'Mobius';
                    }                   
                }
                if(sStatementType == 'Provider')
                {
                    sRFN = '';
                    sRLN = sName;
                    if(sRecipientType == 'Provider')    sType = 'Provider';
                    if(sRecipientType == 'Other')    sType = 'Provider';
                    sAttachmentCategory = 'Other';
                    sReprintSystemOfRecord = 'Mobius';                    
                }
            }   
            
            sServiceRequest = '';
            HUM_Webservice_Callout__c oSvcHeaderSendStmt = new HUM_Webservice_Callout__c ();
            oSvcHeaderSendStmt = HUM_Webservice_Callout__c.getValues('CommunicationReprint_V3');//Call New VERSION               
            Integer iTIMEOUT_INT_SECS = Integer.valueof(oSvcHeaderSendStmt.Timeout__c)/1000;
            HttpRequest request = new HttpRequest();
            oServiceSendStmtDtl.setupRequest(request, oSvcHeaderSendStmt, sROrgName, sDistributionChnl, sMsgFulfillmentId, sRFN, sRLN, sRAdrLine1, sRAdrLine2, sRCity, sRState, sRZip, sRFaxNbr, sMbrId, sDptCode, sPlatformCode, sSDRPersonId, sMemberGenKey, sProvID, sProvIDType, sType,sAttachmentCategory,sReprintSystemOfRecord);
            Continuation con = new Continuation(iTIMEOUT_INT_SECS );
            sServiceRequest = con.addHttpRequest(request);
            con.continuationMethod = 'processSendStmtResponse';
            if(Test.isRunningTest()) throw new HUMCustomException('Error');
            return con;
        }
        catch(Exception ex)
        {
            showErrorMsg();HUMExceptionHelper.logErrors(ex, 'Claim_Send_Statement_C_HUM', 'initiateSendStmtRequest');
            return NULL;
        }
    }
    
    /*
* Method Name   :  processSendStmtResponse
* Description   :  This method is used to get response from service.
* Return Type   :  void 
* Parameters    :  NA
*/
    public void processSendStmtResponse()
    {
        try 
        {
            HttpResponse response = Continuation.getResponse(sServiceRequest);
            if(response != null)    responseParseSendStmt(response);
        }
        catch(Exception ex)
        {
            showErrorMsg();HUMExceptionHelper.logErrors(ex, 'Claim_Send_Statement_C_HUM', 'processSendStmtResponse');
        }
    }
    
    /*
* Method Name   :  responseParseSendStmt
* Description   :  This method is used to check response
* Return Type   :  void 
* Parameters    :  HttpResponse obj
*/
    @testvisible
    private void  responseParseSendStmt(HttpResponse sResponse)
    {
        Integer statusCode = sResponse.getStatusCode();
        if (statusCode == GLOBAL_CONSTANT_HUM.HTTP_OK_HUM || statusCode == GLOBAL_CONSTANT_HUM.HTTP_ACCEPTED_HUM)
        {
            ClaimSendStmt_Mbr_ServiceResponse_H_HUM oResponse = new ClaimSendStmt_Mbr_ServiceResponse_H_HUM();
            oResponse = (ClaimSendStmt_Mbr_ServiceResponse_H_HUM)JSON.deserialize(sResponse.getBody(), ClaimSendStmt_Mbr_ServiceResponse_H_HUM.class);
            if(oResponse.ReprintResponse != null)
            {
                ClaimSendStmt_Mbr_ServiceResponse_H_HUM.ReprintResponse oDetailsResponse = oResponse.ReprintResponse;
                ClaimSendStmt_Mbr_ServiceResponse_H_HUM.ReprintResult oDetailsResult = oDetailsResponse.ReprintResult;
                if(oDetailsResult != null)    
                {
                    if(oDetailsResult.Status != null && (oDetailsResult.Status == 'Printed' || oDetailsResult.Status == 'Success'))
                    {
                        ApexPages.Message myMsg;
                        bShowError = true;
                        if(bIsPageFromRTI)
                        {
                            createPrintItemtemplateObjects();
                            myMsg = new ApexPages.Message(ApexPages.Severity.Info,'Send Print Item Request successfully submitted.');
                        }
                        else
                        {
                            createtemplateObjects();
                            myMsg = new ApexPages.Message(ApexPages.Severity.Info,'Send Claim Statement Request successfully submitted.');
                        }
                        ApexPages.addMessage(myMsg);
                    }
                    else    showErrorMsg();
                }
                else    showErrorMsg();
            }
            else    showErrorMsg();
        }
        else    showErrorMsg();
    }
    
    public void createPrintItemtemplateObjects()
    {
        sTemplateOwnerId = createTemplateOwnerRTI();
        sTemplateOwnerId = createTemplateSubmissionRTI(sTemplateOwnerId);
        createTemplateSubmissionDataRTI(sTemplateOwnerId);
    }
    
    public id createTemplateOwnerRTI()
    {
        template_submission_owner__c oTemplateSubmissionOwner = new template_submission_owner__c();
        oTemplateSubmissionOwner.Object_Owner_Type__c = 'Case';
        oTemplateSubmissionOwner.Object_Owner_ID__c = sRTIPageId.escapeHtml4();
        insert oTemplateSubmissionOwner;
        return oTemplateSubmissionOwner.id;
    }
    
    public id createTemplateSubmissionRTI(Id sOwnerId)
    {
        template_submission__c oTemplateSubmission = new template_submission__c();
        oTemplateSubmission.Submission_Owner__c = sOwnerId;
        List<Template__c > lstTempMem = [SELECT Id FROM Template__c where  Description__c='Send Print Item: Member' limit 1];
        oTemplateSubmission.Template__c = lstTempMem [0].id;
        insert oTemplateSubmission;
        return oTemplateSubmission.id;
    } 
    
    public void createTemplateSubmissionDataRTI(Id idSubmissionId){
        List<Template_Field__c> OTempField;
        OTempField =[SELECT id,template__r.Description__c,Name FROM Template_Field__c where template__r.Description__c ='Send Print Item: Member'];
        List<template_submission_data__c> lstSubIdToupdate = new List<template_submission_data__c>() ;
        for(Integer I=0;I<OTempField.size();I++)
        {
            template_submission_data__c oTemplateSubmissionData = new template_submission_data__c();
            oTemplateSubmissionData.template_field__c = OTempField[I].id;
            oTemplateSubmissionData.template_submission__c = idSubmissionId;
            if(OTempField[I].Name == 'Data')
            {
                String sViewLinkData = EncodingUtil.urlDecode(getHTMLEncodedPageParameter('viewlink').escapeHtml4(),'UTF-8');                   
                oTemplateSubmissionData.value__c =  '  <FONT FACE="Arial" STYLE="font-size:16px"><u><b>Summary</b></u><br><br> <table style="font-family: Arial, Helvetica, sans-serif; border-collapse: collapse;width: 1200px;font-size:12px;text-align: left;"><thead><tr> <th style="border: 1px solid #ddd;background-color: #D3D3D3;padding: 8px;">Action</th> <th style="border: 1px solid #ddd;background-color: #D3D3D3;padding: 8px;">Message Name</b> </td><th style="border: 1px solid #ddd;background-color: #D3D3D3;padding: 8px;"> Category Type</b></td><th style="border: 1px solid #ddd;background-color: #D3D3D3;padding:  8px;">Date Type</b></td>  <th style="border: 1px solid #ddd;background-color: #D3D3D3;padding: 8px;">Delivery Method</b>  </td></tr></thead><tr><td style="border: 1px solid #ddd;padding: 8px;"> <a style="color:blue" href='+sViewLinkData+' target="_blank" >View</a></td>  <td style="border: 1px solid #ddd;padding: 8px;">'+sMessageName.escapeHtml4()+' </td>  <td style="border: 1px solid #ddd;padding: 8px;">'+sCategoryDesc+'</td>  <td style="border: 1px solid #ddd;padding: 8px;">'+sDateSent+'</td>  <td style="border: 1px solid #ddd;padding: 8px;">Mail</td></tr> </table>  </FONT> <br/><br/> <br/>   <FONT FACE="Arial" STYLE="font-size:14px"><b>Recipient Name: </b>'+sMailrepName+'</FONT> <br/><br/><FONT FACE="Arial" STYLE="font-size:14px"><b>Recipient Type: </b>'+sRecipientType +'</FONT> <br/><br/>    <FONT FACE="Arial" STYLE="font-size:14px"><b>Address Line 1: </b>'+sRefAddressLine1+'</FONT> <br/><br/>   <FONT FACE="Arial" STYLE="font-size:14px"><b>Address Line 2: </b></FONT> <br/><br/>   <FONT FACE="Arial" STYLE="font-size:14px"><b>City: </b>'+sRefCity+'</FONT> <br/><br/>     <FONT FACE="Arial" STYLE="font-size:14px"><b>State: </b>'+sRefStatecode+'</FONT> <br/><br/>   <FONT FACE="Arial" STYLE="font-size:14px"><b>Zip Code : </b>'+sRefZipcode+'</FONT>';
            }
            
            else if(OTempField[I].Name == 'RTIPrintDeliveryMethod')
            {
                oTemplateSubmissionData.value__c = selectedValue;
            }
            else if(OTempField[I].Name == 'RTIPrintRecipientName' && selectedValue == 'Mail')
            {
                oTemplateSubmissionData.value__c = sMailrepName;
            } 
            else if(OTempField[I].Name == 'RTIPrintRecipientType')
            {
                oTemplateSubmissionData.value__c = sRecipientType ;
            }
            else if(OTempField[I].Name == 'RTIPrintAddressLine1' && selectedValue == 'Mail')
            {
                oTemplateSubmissionData.value__c = sRefAddressLine1;
            }
            else if(OTempField[I].Name == 'RTIPrintAddressLine2' && selectedValue == 'Mail')
            {
                oTemplateSubmissionData.value__c = sRefAddressline2;
            }
            else if(OTempField[I].Name == 'RTIPrintCity' && selectedValue == 'Mail')
            {
                oTemplateSubmissionData.value__c = sRefCity;
            }
            else if(OTempField[I].Name == 'RTIPrintState' && selectedValue == 'Mail')
            {
                oTemplateSubmissionData.value__c = sRefStatecode;
            }
            else if(OTempField[I].Name == 'RTIPrintZip' && selectedValue == 'Mail')
            {
                oTemplateSubmissionData.value__c = sRefZipcode;
            }
            
            else if(OTempField[I].Name == 'RTIPrintWorkQueueName')
            {    
                string usersCurrentQueue;
                usersCurrentQueue = LoggingDataLogic_D_HUM.getUserQueue(UserInfo.getUserId());
                oTemplateSubmissionData.value__c = usersCurrentQueue;
            }
            else if(OTempField[I].Name == 'RTIPrintCategoryType')
            {
                oTemplateSubmissionData.value__c = sCategoryDesc.escapeHtml4();
            }
            else if(OTempField[I].Name == 'RTIPrintMessageName')
            {
                oTemplateSubmissionData.value__c = sMessageName.escapeHtml4();
            }
            lstSubIdToupdate.add(oTemplateSubmissionData);
            
        }
        Database.SaveResult[] lstResultTemplateData = Database.insert(lstSubIdToupdate, false);
        HUMExceptionHelper.processSaveResults(lstResultTemplateData, lstSubIdToupdate, 'createTemplateSubmissionDataRTI', 'Claim_Send_Statement_C_HUM', 'template_submission_data__c');                
    }
    public void createtemplateObjects(){
        SSubmissionID = createtemplateOwner();
        SSubmissionID = createTemplateSubmission(SSubmissionID);
        createTemplateSubmissionData(SSubmissionID);
    }
    
    public id createtemplateOwner(){
        template_submission_owner__c oTemplateSubmissionOwner = new template_submission_owner__c();
        oTemplateSubmissionOwner.Object_Owner_Type__c = 'Case';
        oTemplateSubmissionOwner.Object_Owner_ID__c = sPolMemID;
        insert oTemplateSubmissionOwner;
        return oTemplateSubmissionOwner.id;
    }
    
    public id createTemplateSubmission(Id sOwnerId){
        template_submission__c oTemplateSubmission = new template_submission__c();
        oTemplateSubmission.Submission_Owner__c = sOwnerId;
        if(sStatementType == 'Member')
        {
            List<Template__c > lstTempMem = [SELECT Id FROM Template__c where  Description__c='Send Claim Statement: Member' limit 1];
            oTemplateSubmission.Template__c = lstTempMem [0].id;
        }
        else if(sStatementType == 'Provider')
        {
            List<Template__c > lstTempPro = [SELECT Id FROM Template__c where  Description__c='Send Claim Statement: Provider' limit 1];
            oTemplateSubmission.Template__c = lstTempPro[0].id;
        }
        insert oTemplateSubmission;
        return oTemplateSubmission.id;
    } 
    
    public void createTemplateSubmissionData(Id idSubmissionId){
        List<Template_Field__c> OTempField;
        if(sStatementType == 'Member')
        {
            OTempField =[SELECT id,template__r.Description__c,Name FROM Template_Field__c where template__r.Description__c ='Send Claim Statement: Member'];
        }
        else if(sStatementType == 'Provider')
        {
            OTempField =[SELECT id,template__r.Description__c,Name FROM Template_Field__c where template__r.Description__c ='Send Claim Statement: Provider'];
        }
        List<template_submission_data__c> lstSubIdToupdate = new List<template_submission_data__c>() ;
        
        for(Integer I=0;I<OTempField.size();I++)
        {
            template_submission_data__c oTemplateSubmissionData = new template_submission_data__c();
            oTemplateSubmissionData.template_field__c = OTempField[I].id;
            oTemplateSubmissionData.template_submission__c = idSubmissionId;
            if(OTempField[I].Name == 'Data')
            {
                oTemplateSubmissionData.value__c = populateTemplatedata();
            }
            else if(OTempField[I].Name == 'EndStatementPeriod')
            {
                oTemplateSubmissionData.value__c = sEndStatementPeriod.escapeHtml4();
            }
            else if(OTempField[I].Name == 'ClaimNumber')
            {
                oTemplateSubmissionData.value__c = sClaimNumber.escapeHtml4();
            }
            else if(OTempField[I].Name == 'RemittanceID')
            {
                oTemplateSubmissionData.value__c = sRemitId;
            }
            else if(OTempField[I].Name == 'DeliveryMethod')
            {
                oTemplateSubmissionData.value__c = selectedValue;
            }
            else if(OTempField[I].Name == 'RecipientName' && selectedValue == 'Mail')
            {
                oTemplateSubmissionData.value__c = sMailrepName;
            } 
            else if(OTempField[I].Name == 'RecipientName' && selectedValue == 'Fax')
            {
                oTemplateSubmissionData.value__c = repName;
            } 
            else if(OTempField[I].Name == 'RecipientType')
            {
                oTemplateSubmissionData.value__c = sRecipientType ;
            }
            else if(OTempField[I].Name == 'AddressLine1' && selectedValue == 'Mail')
            {
                oTemplateSubmissionData.value__c = sRefAddressLine1;
            }
            else if(OTempField[I].Name == 'AddressLine2' && selectedValue == 'Mail')
            {
                oTemplateSubmissionData.value__c = sRefAddressline2;
            }
            else if(OTempField[I].Name == 'City' && selectedValue == 'Mail')
            {
                oTemplateSubmissionData.value__c = sRefCity;
            }
            else if(OTempField[I].Name == 'State' && selectedValue == 'Mail')
            {
                oTemplateSubmissionData.value__c = sRefStatecode;
            }
            else if(OTempField[I].Name == 'Zip' && selectedValue == 'Mail')
            {
                oTemplateSubmissionData.value__c = sRefZipcode;
            }
            else if(OTempField[I].Name == 'FaxNumber')
            {
                oTemplateSubmissionData.value__c = faxNumber;
            }
            else if(OTempField[I].Name == 'WorkQueueName')
            {    
                string usersCurrentQueue;
                usersCurrentQueue = LoggingDataLogic_D_HUM.getUserQueue(UserInfo.getUserId());
                oTemplateSubmissionData.value__c = usersCurrentQueue;
            }
            else if(OTempField[I].Name == 'DocumentKey')
            {
                string DocumentKeyOnly = sDocumentKey.substringAfter('DocumentKey=');
                oTemplateSubmissionData.value__c = DocumentKeyOnly;
            }
            else if(OTempField[I].Name == 'ApplicationKey')
            {
                oTemplateSubmissionData.value__c = sApplicationKey;
            }
            else if(OTempField[I].Name == 'StatementType')
            {
                oTemplateSubmissionData.value__c = sStatementTypeMobValue.escapeHtml4();
            }
            else if(OTempField[I].Name == 'BeginStatementPeriod')
            {
                oTemplateSubmissionData.value__c = sStatementPeriod.escapeHtml4();
            }
            
            lstSubIdToupdate.add(oTemplateSubmissionData);
            
        }
        Database.SaveResult[] lstResultTemplateData = Database.insert(lstSubIdToupdate, false);
        HUMExceptionHelper.processSaveResults(lstResultTemplateData, lstSubIdToupdate, 'createTemplateSubmissionData', 'Claim_Send_Statement_C_HUM', 'template_submission_data__c');                
    }
    
    public String populateTemplatedata() {
        String sData;
        string sDocKeyEncode = sDocumentKey.replaceAll( ' ', '%20');
        if(selectedValue == 'Mail' && sStatementType == 'Member')  { 
            sData = '<FONT FACE="Arial" STYLE="font-size:16px"><u><b>Summary</b></u><br><br>Claim Number:' +sClaimNumber+'<br><br> <table border="1Px"> <tr> <td><b>Action</b></td> <td><b>Statement Type</b></td> <td><b>Begin Statement period</b></td> <td><b>End Statement Period</b></td> </tr> <tr><td> <a href='+sDocKeyEncode+' target="_blank" >View</a></td> <td>'+sStatementTypeMobValue+'</td> <td>'+sStatementPeriod+'</td> <td>'+sEndStatementPeriod+'</td> </tr> </table> </FONT> <br><br><FONT FACE="Arial" STYLE="font-size:14px"><b>Delivery Method : </b>'+selectedValue+'</FONT> <br> <FONT FACE="Arial" STYLE="font-size:14px"><b>Recipient Name: </b>'+sMailrepName+'</FONT> <br> <FONT FACE="Arial" STYLE="font-size:14px"><b>Recipient Type: </b>'+sRecipientType +'</FONT> <br> <FONT FACE="Arial" STYLE="font-size:14px"><b>Address Line 1: </b>'+sRefAddressLine1+'</FONT> <br> <FONT FACE="Arial" STYLE="font-size:14px"><b>Address Line 2: </b></FONT> <br> <FONT FACE="Arial" STYLE="font-size:14px"><b>City: </b>'+sRefCity+'</FONT> <br> <FONT FACE="Arial" STYLE="font-size:14px"><b>State: </b>'+sRefStatecode+'</FONT> <br> <FONT FACE="Arial" STYLE="font-size:14px"><b>Zip Code : </b>'+sRefZipcode+'</FONT>';
        }
        else if(selectedValue == 'Fax' && sStatementType == 'Member')  { 
            sData ='<FONT FACE="Arial" STYLE="font-size:16px"><u><b>Summary</b></u><br><br>Claim Number:' +sClaimNumber+'<br><br> <table border="1Px"> <tr> <td><b>Action</b></td> <td><b>Statement Type</b></td> <td><b>Begin Statement period</b></td> <td><b>End Statement Period</b></td> </tr> <tr><td> <a href='+sDocKeyEncode+' target="_blank" >View</a></td> <td>'+sStatementTypeMobValue+'</td> <td>'+sStatementPeriod+'</td> <td>'+sEndStatementPeriod+'</td> </tr> </table> </FONT> <br><br><FONT FACE="Arial" STYLE="font-size:14px"><b>Delivery Method : </b>'+selectedValue+'</FONT> <br> <FONT FACE="Arial" STYLE="font-size:14px"><b>Recipient Name: </b>'+repName+'</FONT> <br> <FONT FACE="Arial" STYLE="font-size:14px"><b>Recipient Type: </b>'+sRecipientType +'</FONT> <br> <FONT FACE="Arial" STYLE="font-size:14px"><b>Fax number: </b>'+faxNumber+'</FONT>';
        }
        
        else if(selectedValue == 'Mail' && sStatementType == 'Provider')  {
            sData = '<FONT FACE="Arial" STYLE="font-size:16px"><u><b>Summary</b></u><br><br>Claim Number:' +sClaimNumber+'<br><br> <table border="1Px"> <tr> <td><b>Action</b></td> <td><b>Remittance ID</b></td> </tr> <tr><td> <a href='+sDocKeyEncode+' target="_blank" >View</a></td> <td>'+sRemitId+'</td> </tr> </table> </FONT> <br><br><FONT FACE="Arial" STYLE="font-size:14px"><b>Delivery Method : </b>'+selectedValue+'</FONT> <br> <FONT FACE="Arial" STYLE="font-size:14px"><b>Recipient Name: </b>'+sMailrepName+'</FONT> <br> <FONT FACE="Arial" STYLE="font-size:14px"><b>Recipient Type: </b>'+sRecipientType +'</FONT> <br> <FONT FACE="Arial" STYLE="font-size:14px"><b>Address Line 1: </b>'+sRefAddressLine1+'</FONT> <br> <FONT FACE="Arial" STYLE="font-size:14px"><b>Address Line 2: </b></FONT> <br> <FONT FACE="Arial" STYLE="font-size:14px"><b>City: </b>'+sRefCity+'</FONT> <br> <FONT FACE="Arial" STYLE="font-size:14px"><b>State: </b>'+sRefStatecode+'</FONT> <br> <FONT FACE="Arial" STYLE="font-size:14px"><b>Zip Code : </b>'+sRefZipcode+'</FONT>';
        }
        else if(selectedValue == 'Fax' && sStatementType == 'Provider')  { 
            sData = '<FONT FACE="Arial" STYLE="font-size:16px"><u><b>Summary</b></u><br><br>Claim Number:' +sClaimNumber+'<br><br> <table border="1Px"> <tr> <td><b>Action</b></td> <td><b>Remittance ID</b></td> </tr> <tr><td> <a href='+sDocKeyEncode+' target="_blank" >View</a></td> <td>'+sRemitId+'</td> </tr> </table> </FONT> <br><br><FONT FACE="Arial" STYLE="font-size:14px"><b>Delivery Method : </b>'+selectedValue+'</FONT> <br> <FONT FACE="Arial" STYLE="font-size:14px"><b>Recipient Name: </b>'+repName+'</FONT> <br> <FONT FACE="Arial" STYLE="font-size:14px"><b>Recipient Type: </b>'+sRecipientType +'</FONT> <br> <FONT FACE="Arial" STYLE="font-size:14px"><b>Fax number: </b>'+faxNumber+'</FONT>';
        }
        return sData;
    }
    
    
    
    
    
    /*
* Method Name   :  getAccDetails
* Description   :  This method is used for getting details from Account associated to Policy Member
* Return Type   :  List<POLICY_Member__C>
* Parameters    :  ID sRecId
*/
    
    public List<POLICY_Member__C> getAccDetails(ID sRecId)
    {
        try
        {
            return (List<POLICY_Member__C>)[SELECT Id, Member__r.Name, Member__r.FirstName, Member__r.LastName, Member__r.Enterprise_ID__c, Member__r.Mbr_Gen_Key__c,Member__r.Source_Platform_Code__c FROM POLICY_Member__C WHERE ID =: sRecId];
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'Claim_Send_Statement_C_HUM', 'getAccDetails');
            return null;
        } 
    } 
    /*
* Method Name   :  showErrorMsg
* Description   :  This method is used for showing error
* Return Type   :  NA
* Parameters    :  NA
*/
    public void showErrorMsg()
    {
        bShowError = true;
        ApexPages.Message myMsg;
        if(bIsPageFromRTI)
            myMsg = new ApexPages.Message(ApexPages.Severity.Error,'Send Print Item Request failed - Please try again. If the issue persists contact Help Desk.');
        else
            myMsg = new ApexPages.Message(ApexPages.Severity.Error,'Send Claim Statement Request failed - Please try again. If the issue persists contact Help Desk.');
        ApexPages.addMessage(myMsg);
    }   
    public void disButton()
    {     
        if (selectedValue =='Mail')
        {
            shidepopup ='<script> SendStatement.hidepop();</script>';
        }
    }
    
    public static List<Account> getlstAccountDetails(String sRecId)
    {
        List<Account> lstAccount = new List<Account>();
        try
        { 
            if(String.valueof(sRecId).startswith(Schema.SObjectType.Account.keyprefix))
            {
                
                lstAccount = [Select id,name,Fax, FirstName,LastName,IsPersonAccount,PersonMailingStreet,PersonMailingState,PersonMailingCity,PersonMailingStateCode,PersonMailingPostalCode, PersonMobilePhone, PersonHomePhone, PersonOtherPhone from account where id = :sRecId];
            }
            else
            {
                lstAccount = [Select id,name,Fax, FirstName,LastName,IsPersonAccount,PersonMailingStreet,PersonMailingState,PersonMailingCity,PersonMailingStateCode,PersonMailingPostalCode, PersonMobilePhone, PersonHomePhone, PersonOtherPhone from account where id IN (Select member__c from Policy_Member__c where id=:sRecId)];
            }
            if(Test.isRunningTest()) throw new HUMCustomException('Error');       
        }   
        catch(exception ex)
        {
            HUMExceptionHelper.logErrors(ex,'Claim_Send_Statement_C_HUM', 'getlstAccountDetails');
            lstAccount = new List<Account>();
        } 
        return lstAccount;   
    }
    
    public static Account getAccountDetails(String sRecId)
    {
        Account objAcc = new Account();
        try
        { 
            if(String.valueof(sRecId).startswith(Schema.SObjectType.Account.keyprefix))
            {
                
                List<Account> lstAccount = [Select id,name,Fax, FirstName,LastName,IsPersonAccount,PersonMailingStreet,PersonMailingState,PersonMailingCity,PersonMailingStateCode,PersonMailingPostalCode, PersonMobilePhone, PersonHomePhone, PersonOtherPhone from account where id = :sRecId];
                if(!lstAccount.isEmpty())
                {
                    objAcc = lstAccount[0];       
                }
            }
            else
            {
                
                List<Policy_Member__c> lstPolMember = [Select Member__r.Id from Policy_Member__c where Id =:sRecId];
                if(!lstPolMember.isEmpty())
                {
                    objAcc  = [Select id,name,Fax, FirstName,LastName,IsPersonAccount,PersonMailingStreet,PersonMailingState,PersonMailingCity,PersonMailingStateCode,PersonMailingPostalCode, PersonMobilePhone, PersonHomePhone, PersonOtherPhone from account where id IN (Select member__c from Policy_Member__c where id=:sRecId)];
                    
                }
            }
            if(Test.isRunningTest()) throw new HUMCustomException('Error');       
        }   
        catch(exception ex)
        {
            HUMExceptionHelper.logErrors(ex,'Claim_Send_Statement_C_HUM', 'getAccountDetails');
            objAcc = new Account();
        } 
        return objAcc;   
    }   
    /*
* Method Name   :  getHTMLEncodedPageParameter
* Description   :  This method is used to get Encoded Page Parameter.
* Return Type   :  String 
* Parameters    :  String
*/
    private String getHTMLEncodedPageParameter(String param) {
        String paramValue = ApexPages.currentPage().getParameters().get(param);
        return ESAPI_C_HUM.encoder().SFDC_HTMLENCODE(paramValue);
    }
}
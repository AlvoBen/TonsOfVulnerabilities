/*****************************************************************************************
Apex class Name    : DualEligibleDetails_C_HUM
Version            : 1.0
Function           : This is the Controller class for GetDualInfo Service 
Created Date       : 05/18/2019 
Test Class         : DualEligibleDetails_C_T_HUM

Modification Log:
*   Developer                   Code Review             Date                Description
* ------------------------------------------------------------------------------------------------------------------------------   
*  Pavan Karnam                                    05/18/2019          Controller Class for Dual/Moop Project.   
*  Vardhaman                                       06/07/2021         User Story#-1914200 -T1PRJ0002736 - MF 6 - PLT - Sonar Cube remediation/Consumer Management (URL Parameters should be Escaped/Sanitized)
*  Pinky Vijur										08/09/2021				USER STORY 1801161 T1PRJ0002472 - MVP MF 6 - Dual Eligible Details page - Strides
*  Nirmal Garg										   09/16/2021					Added browser logic for coveo
*  Pinky Vijur                                       09/21/2021         DF-3769 Fix
* 	 Nirmal Garg									  11/11/2021			Added CRMS switch for coveo
*********************************************************************************************************************************/    
    
    public with sharing class DualEligibleDetails_C_HUM{
    public String sPersonId{get;set;}
    public Boolean bShowServiceError{get;set;}
    public Boolean bShowServiceErrorDuals{get;set;}
    public Boolean bShowServiceErrorDeeming{get;set;}
    public String sRequest ='';
    public boolean bIsDataAvailable {get;set;}
    public Boolean bIsError{get; private set;}
    public DualEligibleDetails_S_HUM oServiceObj = new DualEligibleDetails_S_HUM();
    public DualEligibleDetails_DTO_HUM objDualDeemingDetails {get;set;}
    public List<DualEligibleDetails_DTO_HUM> lstDualEligibleStatusVals{get;set;}
    public List<DualEligibleDetails_DTO_HUM> lstDualEligibleDeemingVals{get;set;}
    private boolean bESAPILibSecurityfixSwitch {Get;Set;} 
    Public string sPolicyMemberId{Get;Set;}
	public boolean bCoveoUser {get;set;}
	Public Static final String ChromeBrowser = 'Chrome/';
	Public Static final String EdgeBrowser = 'Edg';
    /*
    * Method Name   :  DualEligibleDetails_C_HUM
    * Description   :  This is a constructor for class DualEligibleDetails_C_HUM
    * Return Type   :  NA
    * Parameters    :  NA
    */
    public DualEligibleDetails_C_HUM(){
        bESAPILibSecurityfixSwitch = HUMUtilityHelper.isCRMFunctionalityON('1914200');
        sPersonId =  ApexPages.currentPage().getParameters().get('Person ID');
        sPersonId = bESAPILibSecurityfixSwitch ? ESAPI.encoder().SFDC_HTMLENCODE(sPersonId) : sPersonId;
        sPolicyMemberId = ApexPages.currentPage().getParameters().get('Id');
        sPolicyMemberId = string.isNotBlank(sPolicyMemberId) ? sPolicyMemberId : 'ent-'+sPersonId;
        bShowServiceErrorDuals = False;
        bShowServiceErrorDeeming = false;
        bIsDataAvailable = true;
        bIsError = false;
		string sBrowserContext = ApexPages.currentPage().getHeaders().get('USER-AGENT');
        if(string.isNotBlank(sBrowserContext)){
			if(sBrowserContext.contains(ChromeBrowser) || sBrowserContext.contains(EdgeBrowser)){
				 bCoveoUser = HUMUtilityHelper.isCRMFunctionalityON('CRMSUnifiedSearchSwitch') && UnifiedSearchUtility_H_HUM.isUnifiedSearchEligible();
			}
		}
    }
    
    /*
    * Method Name   :  initiateRequest
    * Description   :  This method is used to call the Service on load of DualEligibleDetails_VF_HUM Page
    * Return Type   :  continuation
    * Parameters    :  NA
    */       
    public Object initiateRequest()
    { 
        try
        { 
           if(String.isNotBlank(sPersonId))
            {
                HUM_Webservice_Callout__c oSvcEncrypt = HUM_Webservice_Callout__c.getValues('MBEPLUSServiceREST'); 
                Integer iTIMEOUT_INT_SECS = Integer.valueof(oSvcEncrypt.Timeout__c)/1000;
                HttpRequest request = new HttpRequest();
                oServiceObj.setupRequest(request, oSvcEncrypt,sPersonId);
                Continuation con = new Continuation(iTIMEOUT_INT_SECS );
                sRequest = con.addHttpRequest(request);
                con.continuationMethod='processResponse';
                return con;
            }
           else
            {
                return null;          
            }
        }
        catch(Exception ex)
        {
            bShowServiceError = true;
            ApexPages.Message myMsg = new ApexPages.Message(ApexPages.Severity.Error,'Service Error');
            ApexPages.addMessage(myMsg);
            HUMExceptionHelper.logErrors(ex, 'DualEligibleDetails_C_HUM', 'initiateRequest');
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
            if(response != null)
            responseparse(response);
        }
        catch(Exception ex)
        {   
            bShowServiceError = true;
            ApexPages.Message myMsg = new ApexPages.Message(ApexPages.Severity.Error,'Service Error');
            ApexPages.addMessage(myMsg);
            HUMExceptionHelper.logErrors(ex, 'DualEligibleDetails_C_HUM', 'processResponse');  
        }
    }
    
    /*
    * Method Name   :  responseParse
    * Description   :  This method is used to parse response.
    * Return Type   :  NA 
    * Parameters    :  HttpResponse obj
    */
    @testvisible public void  responseParse(HttpResponse sResponse)
    { 
        objDualDeemingDetails = new DualEligibleDetails_DTO_HUM();
        lstDualEligibleStatusVals = new List<DualEligibleDetails_DTO_HUM>();
        lstDualEligibleDeemingVals = new List<DualEligibleDetails_DTO_HUM>();
        Integer iStatusCode = sResponse.getStatusCode();
        if(sResponse != null && (sResponse.getStatusCode() == GLOBAL_CONSTANT_HUM.HTTP_OK_HUM || sResponse.getStatusCode() == GLOBAL_CONSTANT_HUM.HTTP_ACCEPTED_HUM && !sResponse.getbody().contains(GLOBAL_CONSTANT_HUM.FAULTCODE_ALERTS_HUM)))
        { 
            objDualDeemingDetails = oServiceObj.processResponse(sResponse.getbody());
            lstDualEligibleStatusVals = oServiceObj.DualStatus();
            lstDualEligibleDeemingVals = oServiceObj.DeemingPeriod();
            
            
            if(lstDualEligibleStatusVals ==null || lstDualEligibleStatusVals.isEmpty())
            { 
                bShowServiceErrorDuals = true;
                ApexPages.Message myMsg = new ApexPages.Message(ApexPages.Severity.Info,Label.ClaimDetails_NoRecords_Msg);
                ApexPages.addMessage(myMsg); 
            }
            else
            { 
                bShowServiceErrorDuals =false;
            }
            
            if(lstDualEligibleDeemingVals ==null || lstDualEligibleDeemingVals.isEmpty())
            {      
                bShowServiceErrorDeeming = true;
                ApexPages.Message myMsg = new ApexPages.Message(ApexPages.Severity.Info,Label.ClaimDetails_NoRecords_Msg);
                ApexPages.addMessage(myMsg); 
            }
            else
            {
                bShowServiceErrorDeeming =false;
            }
        }    
            
    
        else
        {
            bShowServiceError = true;
            ApexPages.Message myMsg = new ApexPages.Message(ApexPages.Severity.Error,Label.PharmacyAuth_Error_ServiceCallout);
            ApexPages.addMessage(myMsg);
        }            
    }    
}
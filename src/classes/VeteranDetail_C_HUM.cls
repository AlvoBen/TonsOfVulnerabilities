/*****************************************************************************************
Apex class Name    : VeteranDetail_C_HUM
Version            : 1.0
Function           : Controller to VF Component: VeteranDetail_CMP_HUM
This class is used to display the Veteran Details
Created Date       : 03/04/2021
Test Class         : VeteranDetail_T_HUM

Modification Log
*   Developer                   Code Review             Date               Description
* ------------------------------------------------------------------------------------------------------------------------------  
*   Anjani Vadadi                                      03/04/2021          Original Version
* Ajay Chakradhar                           		   05/05/2021	       REQ- 2184294 - Replacing constants with Consumer Management Constants
*   Vardhaman Jain                                     09/28/2021          User Story-2615671-Veterans/Update Option in Dropdown
****************************************************************************************************************************/

public with sharing class VeteranDetail_C_HUM
{
    public Veteran_DTO_HUM oVeteranDTO {get;set;}
    public Boolean bVAhealthYes {get;set;}
    public Boolean bVAhealthNo {get;set;}    
    @testVisible private VeteranDetail_S_HUM oVetService = new VeteranDetail_S_HUM();
    @testVisible private string sResponse;
    @testVisible private string sRequest;
    
    Private String sINFO = 'Info';
    Private string sCASEMGMT_ERROR = 'Error';
    Private string sConfirmedVeteran = 'confirmed-veteran';
    Private string sIdentifiedVeteran = 'identified-veteran';
    Private String sCALLBACK ='processVeteranResponse';
    Private string sVALIDATION_ERROR_MESSAGE = 'Cannot Save Veteran Information as validation rule failed';  
    Private String sUPDATE_SUCCESSDELAY_MESSAGE ='Veteran information saved successfully in the Veteran system. It will take 24-48 hours to reflect in the CRM system';
    Private string sVETERANSECTION_MESSAGE ='Note: Any changes to Veteran information will take 24-48 hours to reflect in CRM system.';
    Private String sVETERAN_SAVE_SERVICE = 'VeteranSaveWebServiceCallout';
    Private string sUPDATE_SUCCESSCRM_MESSAGE ='Veteran Information saved successfully'; 
    Private string sUPDATE_FAILCRMMESSAGE ='Failed to update Veteran Information in CRM system';
    Private string sUPDATE_FAIL_ERROR ='Fail to update Veteran information';
    Private string sUPDATE_FAILVETERANMESSAGE = 'Failed to update Veteran Information in Veteran system'; 
    Private string sUPDATE_ERROR = 'Fail to update information in Veteran System'; 
    //US_2615671_Veterans/Update Option in Dropdown-Start
    Private string sNonVeteran = 'non-veteran';
    Private string sUnknown = 'unknown';
    Private string nonVetValue = 'Non Veteran';
    Private string vetValue = 'Veteran';
    Private string unValue = 'Unknown';
	private boolean bVeteranstatusSwitch;
	//US_2615671_Veterans/Update Option in Dropdown-End  

/* Method Name  :  getVeteranStatusList
* Description   :  This method is used to assign values to the veteran Status picklist on the Veteran update section
* Return Type   :  object
* Parameters    :NA
*/  
	//US_2615671_Veterans/Update Option in Dropdown-Start
    public List<SelectOption> getVeteranStatusList(){
		 try {
            if (Test.isRunningTest())
            {bVeteranstatusSwitch = true;}
            else bVeteranstatusSwitch = HUMUtilityHelper.isCRMFunctionalityOn('2615671');
            
            if(bVeteranstatusSwitch){
                
                List<SelectOption> selVetStatusOptions = new List<SelectOption>();
                selVetStatusOptions.add(new SelectOption(sConfirmedVeteran, vetValue)); 
                selVetStatusOptions.add(new SelectOption(sNonVeteran,nonVetValue)); 
                selVetStatusOptions.add(new SelectOption(sUnknown,unValue)); 
                return selVetStatusOptions;    
                
            }
            else{
                
                List<String> vetStatusPickList= new List<String>();
                List<SelectOption> selVetStatusOptions = new List<SelectOption>();
                Schema.DescribeFieldResult vetStatusfieldResult = Account.Veteran_Status__c.getDescribe();
                List<Schema.PicklistEntry> vetStatusPickListValues = vetStatusfieldResult.getPicklistValues();
                for( Schema.PicklistEntry pickListVal : vetStatusPickListValues){
                    selVetStatusOptions.add(new SelectOption(pickListVal.getValue(),pickListVal.getLabel()));
                }
                return selVetStatusOptions;
                
            }
        }
        catch(Exception ex)
        {
         HUMExceptionHelper.logErrors(ex, 'VeteranDetail_C_HUM', 'getVeteranStatusList');
         return null;
        } 
    }
    //US_2615671_Veterans/Update Option in Dropdown-End

/* Method Name  :  getVeteranSectionMessage
* Description   :  This method is used to show Veteran Message in the Veteran update section
* Return Type   :  string
* Parameters    :  NA
*/	
	public String getVeteranSectionMessage(){    
        try {
                return sVETERANSECTION_MESSAGE;
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'VeteranDetail_C_HUM', 'getVeteranStaticMessage');
            return null;
        } 
    }
	
    
 /* Method Name  :  saveVeteranStatus
* Description   :  This is used to frame the request for VeteransAPI getInfo service
* Return Type   :  object
* Parameters    :NA
*/   
    public Continuation saveVeteranStatus()
    {
        try
        {
            oVeteranDTO.veteranMessageType = null;
            String sVAHealth = (bVAhealthYes) ? GlobalCommonConstants_HUM.STRING_TRUE: (bVAhealthNo) ? GlobalCommonConstants_HUM.STRING_FALSE : null;
            if (oVeteranDTO.sVeteranStatus != sConfirmedVeteran && oVeteranDTO.sVeteranStatus != sIdentifiedVeteran && (sVAHealth != null))
            {
                oVeteranDTO.veteranMessageType = sCASEMGMT_ERROR;
                oVeteranDTO.veteranMessage = sVALIDATION_ERROR_MESSAGE;
                return null;
            } 
            oVeteranDTO.sVAHealthEnrollee = sVAHealth;
            VeteranSaveRequest_DTO_HUM oVeteranSaveRequest =  oVetService.buildRequest(oVeteranDTO);
            if (oVeteranSaveRequest != null) {             
                HUM_Webservice_Callout__c oVetSaveService = HUM_Webservice_Callout__c.getvalues(sVETERAN_SAVE_SERVICE);      
                Integer iTIMEOUT_INT_SECS = Integer.valueof(oVetSaveService.Timeout__c) / 1000;
                Continuation con = new Continuation(iTIMEOUT_INT_SECS);
                con.ContinuationMethod = sCALLBACK;
                String sjsonRequest = JSON.Serialize(oVeteranSaveRequest);
                HttpRequest oRequest = new HttpRequest(); 
                oRequest = oVetService.setHttpRequest(oRequest,oVetSaveService,sjsonRequest); 
                sRequest = con.addHttpRequest(oRequest);
                return con;
            }
            else {
                    oVeteranDTO.veteranMessageType = sCASEMGMT_ERROR;
                    oVeteranDTO.veteranMessage = sUPDATE_FAIL_ERROR;
                    return null;
                } 
        } 
        catch(Exception ex)
        {
            oVeteranDTO.veteranMessageType = sCASEMGMT_ERROR;
            oVeteranDTO.veteranMessage = sUPDATE_FAIL_ERROR;
            HUMExceptionHelper.logErrors(ex, 'VeteranDetail_C_HUM', 'saveVeteranStatus');
            return null;
        } 
        
    }
/* Method Name  :  processVeteranResponse
* Description   :  This is the callback method for VeteransAPI SaveInfo service
* Return Type   :  object
* Parameters    :NA
*/    
    public object processVeteranResponse() {
        
        try{     
            HttpResponse oResponse = new HttpResponse();
            oResponse = Continuation.getResponse(sRequest);
             if(oResponse != null){
                 parseResponse(oResponse);
             }    
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'VeteranDetail_C_HUM', 'getVeteranStatusList');
            return null;
        } 
        return null;        
    }  

	
/* Method Name  :  parseResponse
* Description   :  This method is to parse the response of VeteransAPI  service
* Return Type   :  void
* Parameters    :  NA
*/ 
    @testvisible private void parseResponse(HttpResponse objResponse){
        try{            
        sResponse = objResponse.getBody();
        Integer sStatusCode  = objResponse.getStatusCode();
            if (sStatusCode == GlobalCommonConstants_HUM.HTTP_OK_HUM || sStatusCode == GlobalCommonConstants_HUM.HTTP_ACCEPTED_HUM)
            {
                VeteranSaveResponse_DTO_HUM oVetResponse = new VeteranSaveResponse_DTO_HUM();
                oVetResponse = (VeteranSaveResponse_DTO_HUM )System.JSON.deserialize(sResponse,VeteranSaveResponse_DTO_HUM.class);                
                if (oVetResponse != null)
                {
                     switch on oVetResponse.statusCode {
                        when '201', '204'  {
                            oVeteranDTO.veteranMessageType = sINFO;
                            oVeteranDTO.veteranMessage = sUPDATE_SUCCESSDELAY_MESSAGE;
                        }
                        when else {
                            oVeteranDTO.veteranMessageType = sCASEMGMT_ERROR;
                            oVeteranDTO.veteranMessage = sUPDATE_FAILVETERANMESSAGE;
                        }
                    }
                }
                else 
                {
                    oVeteranDTO.veteranMessageType = sCASEMGMT_ERROR;
                    oVeteranDTO.veteranMessage = sUPDATE_ERROR;
                }
            }
            
        }
    
    	catch(Exception ex){
            oVeteranDTO.veteranMessageType = sCASEMGMT_ERROR;
            oVeteranDTO.veteranMessage = sUPDATE_ERROR;
            HUMExceptionHelper.logErrors(ex, 'VeteranDetail_C_HUM', 'parseResponse');
            
    	}
    } 
    
 }
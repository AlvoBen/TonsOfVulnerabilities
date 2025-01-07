/*****************************************************************************************
Apex class Name    : VeteranDetail_LC_HUM
Version            : 1.0
Function           : Controller to Veteraninformation LWC Component: Veteraninformation
This class is used to display the Veteran Details
Created Date       : 02/21/2023
Test Class         : VeteranDetail_LT_HUM

Modification Log
*   Developer                   Code Review             Date               Description
* ------------------------------------------------------------------------------------------------------------------------------  
* Visweswara rao j                                   02/21/2023           US 3731797: T1PRJ0865978 - MF 19972 Lightning- Veteran's Update Section and fields (Jaguars)
****************************************************************************************************************************/
public with sharing class VeteranDetail_LC_HUM {

    public Veteran_DTO_HUM oVeteranDTO {get;set;}
    public static  Veteran_DTO_HUM vtnDTO = new Veteran_DTO_HUM();
    public static Boolean bVAhealthYes {get;set;}
    public static Boolean bVAhealthNo {get;set;}    
    @testVisible private static VeteranDetail_LS_HUM oVetService = new VeteranDetail_LS_HUM();
    @testVisible private static string sResponse;
    @testVisible private static string sRequest;
    
    Private String sINFO = 'Info';
    Private static string sCASEMGMT_ERROR = 'Error';
    Private static string sConfirmedVeteran = 'confirmed-veteran';
    Private static string sIdentifiedVeteran = 'identified-veteran';
    Private static String sCALLBACK ='getprocessVeteranResponse';
    Private static string sVALIDATION_ERROR_MESSAGE = 'Cannot Save Veteran Information as validation rule failed';  
    Private static String sUPDATE_SUCCESSDELAY_MESSAGE ='Veteran information saved successfully in the Veteran system. It will take 24-48 hours to reflect in the CRM system';
    Private string sVETERANSECTION_MESSAGE ='Note: Any changes to Veteran information will take 24-48 hours to reflect in CRM system.';
    Private static String sVETERAN_SAVE_SERVICE = 'VeteranSaveWebServiceCallout';
    Private string sUPDATE_SUCCESSCRM_MESSAGE ='Veteran Information saved successfully'; 
    Private string sUPDATE_FAILCRMMESSAGE ='Failed to update Veteran Information in CRM system';
    Private static string sUPDATE_FAIL_ERROR ='Fail to update Veteran information';
    Private static string sUPDATE_FAILVETERANMESSAGE = 'Failed to update Veteran Information in Veteran system'; 
    Private static string sUPDATE_ERROR = 'Fail to update information in Veteran System'; 
    //US_2615671_Veterans/Update Option in Dropdown-Start
    Private static string sNonVeteran = 'non-veteran';
    Private static string sUnknown = 'unknown';
    Private static string nonVetValue = 'Non Veteran';
    Private static string vetValue = 'Veteran';
    Private static string unValue = 'Unknown';
	private static boolean bVeteranstatusSwitch;
    @TestVisible private static String continuationLabel;
    @TestVisible private static String continuationState;
	//US_2615671_Veterans/Update Option in Dropdown-End  


    
 /* Method Name  :  saveVeteranStatus
* Description   :  This is used to frame the request for VeteransAPI getInfo service
* Return Type   :  object
* Parameters    :NA
*/   
    @AuraEnabled(continuation=true) 
    public static Continuation saveVeteranStatus(String requestParam)
    {
        if(String.isNotBlank(requestParam)){
            vtnDTO = (Veteran_DTO_HUM) JSON.deserialize(requestParam, Veteran_DTO_HUM.class);
        }
   
        try
        {    
            vtnDTO.veteranMessageType = null;
            VeteranSaveRequest_DTO_HUM  vtnSaveReq = new VeteranSaveRequest_DTO_HUM();
             vtnSaveReq =  oVetService.buildRequest(vtnDTO);
              if (vtnSaveReq != null) {             
                HUM_Webservice_Callout__c oVetSaveService = HUM_Webservice_Callout__c.getvalues(sVETERAN_SAVE_SERVICE);      
                Integer iTIMEOUT_INT_SECS = Integer.valueof(oVetSaveService.Timeout__c) / 1000;
                Continuation con = new Continuation(iTIMEOUT_INT_SECS );
                con.continuationMethod='processResponse'; // changed by viswa
                String sjsonRequest = JSON.Serialize(vtnSaveReq);
                HttpRequest oRequest = new HttpRequest(); 
                oRequest = oVetService.setHttpRequest(oRequest,oVetSaveService,sjsonRequest);
                continuationLabel = con.addHttpRequest(oRequest);
                continuationState = 'data';
                con.state = continuationState;
                return con;
            } 
            else { 
                    return null;
                } 
        } 
        catch(Exception ex)
        {    
            HUMExceptionHelper.logErrors(ex, 'VeteranDetail_LC_HUM', 'saveVeteranStatus');
            return null;
        } 
        
    }
    @AuraEnabled 
        public static  Object processResponse(List<string> labels, Object State)// added by viswa
        {
            try 
            {
                HttpResponse response = Continuation.getResponse(labels[0]);
                String result = response.getBody();
                return parseResponse(response,State);
            }
            catch(Exception ex)
            {
                HUMExceptionHelper.logErrors(ex, 'VeteranDetail_LC_HUM', 'processResponse');
                return null;
            }
        }

	
/* Method Name  :  parseResponse
* Description   :  This method is to parse the response of VeteransAPI  service
* Return Type   :  void
* Parameters    :  NA
*/ 
    public static Object parseResponse(HttpResponse objResponse,Object state){
         VeteranSaveResponse_DTO_HUM oVetResponse = new VeteranSaveResponse_DTO_HUM();
        try{            
        sResponse = objResponse.getBody();
        return sResponse;   
        }
    
    	catch(Exception ex){
            HUMExceptionHelper.logErrors(ex, 'VeteranDetail_LC_HUM', 'parseResponse');
            return null;   
    	}    
    }
    }
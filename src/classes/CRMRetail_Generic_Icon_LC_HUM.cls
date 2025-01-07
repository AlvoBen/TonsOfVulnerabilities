/*******************************************************************************************************************************
Apex Class Name     : CRMRetail_Generic_Icon_LC_HUM
Version             : 1.0
Created Date        : 04/25/2023
Function            : This serves the logic on showing icons on Account detail page.
Test class			: CRMRetail_Generic_Icon_LT_HUM

Modification Log: 
* Developer Name           Code Reveiw                     Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Sahil Verma                                          	   01/25/2022            	  User Story 4231796: T1PRJ0154546 / DP / MF9 Storefront - Ability to View Deceased Information on Member Account
* Vinoth L                                         	   	   04/26/2023                 User Story 4566290: T1PRJ0154546 / SF / MF3 Storefront: Display MyHumana Enrollment & Go365 Enrollment - Backend Logic
*******************************************************************************************************************************/
public class CRMRetail_Generic_Icon_LC_HUM 
{
    public static map<string,map<string,string>> showIconMap= new map<string,map<string,string>>();
    public static List<Error_Log__c> listOfErrorLog = new List<Error_Log__c>(); 
    public static final string CUSTOM_EXCEPTION = HUMConstants__c.getInstance('CUSTOM_EXCEPTION') != null ? HUMConstants__c.getInstance('CUSTOM_EXCEPTION').StringValue__c : '';
    public static final string ERROR_INTEGRATION = HUMConstants__c.getInstance('ERROR_INTEGRATION') != null ? HUMConstants__c.getInstance('ERROR_INTEGRATION').StringValue__c : '';

    /*
    * Method Name   :    showIcons
    * Description   :    creates a map for icons to show on the Account detail page
    * Return Type   :    map<string,map<string,string>>  
    */
    @AuraEnabled
    public static CRMRetail_Result_DTO_HUM showIcons(string recId)
    {
        CRMRetail_Result_DTO_HUM oResWrapper = new CRMRetail_Result_DTO_HUM();
        try
        {
            List<Account> accList = [SELECT Id, Enterprise_Id__c,Deceased_Date__c FROM Account WHERE id =:recId AND RecordType.Name=: GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_MEMBER];
            if(!accList.isEmpty())
            {
                isMemberDeceased(accList[0]);
                if(HUMUtilityHelper.isCRMFunctionalityON('4566290')){
                	getMHG365Eligibility(accList[0]);                                                
                }                
            }            
            if(showIconMap != NULL)
            {
                oResWrapper.sResult = json.serialize(showIconMap);
                oResWrapper.IsError = false; 
            }            
        }
        catch(Exception ex)
        {
            listOfErrorLog.add(GenericHelper_CRMRetail_H_HUM.createErrorLog('','CRMRetail_Generic_Icon_LC_HUM','showIcons',ex.getMessage(),CUSTOM_EXCEPTION));
            oResWrapper.sResult = ex.getMessage();
            oResWrapper.IsError = true;
        }
        if(!listOfErrorLog.isEmpty())
        {
            insert listOfErrorLog;
        }
        return oResWrapper;
    }

    /*
    * Method Name   :    isMemberDeceased
    * Description   :    set the deceased flag in the map based on  the deceased date value from account
    * Return Type   :    void
    */
    @testVisible 
    private static void isMemberDeceased(Account acc)
    { 
        try
        {
            showIconMap.put(System.Label.CRMRetail_Deceased_Label, iconAttribute(string.isNotBlank(acc.Deceased_Date__c) ? 'true' : 'false',GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_REDCOLOR,GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_DECEASEDICONNAME,'','false','','1'));                   
        }  
        catch(Exception ex)
        {
            captureExceptions(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_MEMBERACTIVITY_ERROR_NODE,system.label.CRMRetail_Error_Loading_Deceased_Icon,ex.getMessage(),'isMemberDeceased',CUSTOM_EXCEPTION,false);
        }
    }        

    /*
    * Method Name   :    getMHG365Eligibility
    * Description   :    makes the service callout to memberWebActivity service to get MyHumana and Go365 details
    * Return Type   :    void
    */
    private static void getMHG365Eligibility(Account acc)
    { 
        CRMRetail_ServicesReq_DTO_HUM.GetMHG365Info objMHG365Info = new CRMRetail_ServicesReq_DTO_HUM.GetMHG365Info();
        objMHG365Info.IDValue = acc.Enterprise_id__c;
        objMHG365Info.IDType = GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_MHG365_PERSONID;
        objMHG365Info.VitalityFlag = GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_MHG365_VITALITY_Y;
        try
        {       
            HUM_Webservice_Callout__c getMHGo365CstSetting = HUM_Webservice_Callout__c.getValues(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_MHG365_CUST_SET);
            String sBody = JSON.serialize(objMHG365Info);
            Http http = new Http(); 
            HttpRequest request = new HttpRequest();
            request.setMethod(getMHGo365CstSetting.Request_Type__c);  
            if(!Test.isRunningTest())
            {
                request.setClientCertificateName(getMHGo365CstSetting.Certificate_Name__c);
            }
            request.setHeader(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_CONTENTTYPE, GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_APPLICATION_JSON);				
            request.setEndpoint(getMHGo365CstSetting.End_Point_URL__c);
            request.setTimeout(Integer.valueOf(getMHGo365CstSetting.Timeout__c));                
            request.setBody(sBody); 
            HttpResponse mHGo365response = http.send(request);
            if(mHGo365response != NULL)
            {
                parseMyHumGo365Response(acc.Id,mHGo365response);                
            }  
        }
        catch(Exception ex)
        {
            captureExceptions(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_MEMBERACTIVITY_ERROR_NODE,GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_MHG365_ERROR_FETCHING_DETAILS,ex.getMessage(),'getMHG365Eligibility',CUSTOM_EXCEPTION,false);
        }
    }

    /*
    * Method Name   :    parseMyHumGo365Response
    * Description   :    parse the WebActivity response to fetch MyHumana and Go365 values
    * Return Type   :    void
    */
    private static void parseMyHumGo365Response(String accId,HttpResponse mHGo365response)
    {
        Map<String,Object> mapOfResponse = new Map<String,Object>();
        if(mHGo365response.getStatus() == GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_OK_TEXT &&  mHGo365response.getStatusCode() == 200)
        {             
            mapOfResponse = (mHGo365response.getBody() != NULL) ? (Map<String,Object>)Json.deserializeUntyped(mHGo365response.getBody()) : NULL;                                                      
            if(mapOfResponse!= NULL && !mapOfResponse.containsKey(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_MHG365_FAULT_KEY) && String.isBlank(String.valueOf(mapOfResponse.get(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_MHG365_ERROR))))
            {                                                            
                try
                {    
                    if(mapOfResponse.containsKey(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_MHG365_ISWEBREGISTERED_KEY) && (mapOfResponse.get(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_MHG365_ISWEBREGISTERED_KEY) == 'true') || mapOfResponse.get(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_MHG365_ISWEBREGISTERED_KEY) == true)
                    {                   
                        showIconMap.put(System.Label.CRMRetail_MyHumanaKey,iconAttribute('true',GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_BLACKCOLOR,GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_CHECKICON,'','false','','3'));
                    }
                }
                catch(Exception ex)
                {
                    captureExceptions(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_MYHUMANA_ERROR_NODE,GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_MYHUMANA_ERROR_FETCHING_DETAILS,ex.getMessage(),'parseMyHumGo365Response',CUSTOM_EXCEPTION,false);
                }
                try
                {    
                    if(mapOfResponse.containsKey(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_MHG365_LASTLOGINDATETIME_KEY) && mapOfResponse.get(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_MHG365_LASTLOGINDATETIME_KEY) != '')
                    {
                        showIconMap.put(System.Label.CRMRetail_LastLogin_DateTime,iconAttribute('true',GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_BLACKCOLOR,'',String.valueOf(mapOfResponse.get(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_MHG365_LASTLOGINDATETIME_KEY)),'false','','4'));                    
                    }
                }
                catch(Exception ex)
                {
                    captureExceptions(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_LASTLOGIN_ERROR_NODE,GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_LSTLGN_DTTME_ERROR_FETCHING_DETAILS,ex.getMessage(),'parseMyHumGo365Response',CUSTOM_EXCEPTION,false);
                }
                try
                {    
                    if(mapOfResponse.containsKey(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_MHG365_VitalityEntity) && String.isNotBlank(String.valueOf(mapOfResponse.get(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_MHG365_VitalityEntity))))
                    { 
                        showIconMap.put(System.Label.CRMRetail_Go365_Eligibility,iconAttribute('true',GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_BLACKCOLOR,GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_CHECKICON,'','false','','2'));
                    }
                    else if(mapOfResponse.get(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_MHG365_VitalityEntity) == NULL || mapOfResponse.get(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_MHG365_VitalityEntity) == '')
                    {                             
                        List<Member_Id__c> lstMemIds = [SELECT Id,Name from Member_Id__c where Type__c=:GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_MHG365_VITALITY_ENTITY_TYPE AND Policy_Member__r.Member__c=: accId AND Policy_Member__r.Policy_Platform__c=:GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_MHG365_NI_PLATFORM_KEY];
                        if(!lstMemIds.isEmpty())
                        {
                            showIconMap.put(System.Label.CRMRetail_Go365_Eligibility,iconAttribute('true',GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_BLACKCOLOR,GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_CHECKICON,'','false','','2')); 
                        }                        
                    } 
                }
                catch(Exception ex)
                {                    
                    captureExceptions(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_GO365_ERROR_NODE,GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_GO365_ERROR_FETCHING_DETAILS,ex.getMessage(),'parseMyHumGo365Response',CUSTOM_EXCEPTION,false);
                }
            }                
            else if(!mapOfResponse.containsKey(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_MHG365_FAULT_KEY) && String.isNotBlank(String.valueOf(mapOfResponse.get(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_MHG365_ERROR))))
            {
                String sMHG365Error = JSON.serialize(mapOfResponse.get(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_MHG365_ERROR));                
                if(String.isNotBlank(sMHG365Error))
                {
                    captureExceptions(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_MEMBERACTIVITY_ERROR_NODE,GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_MHG365_ERROR_FETCHING_DETAILS,sMHG365Error,'parseMyHumGo365Response',CUSTOM_EXCEPTION,false);
                }                
            }
            else if(mapOfResponse.containsKey(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_MHG365_FAULT_KEY) && mapOfResponse.get(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_MHG365_FAULT_KEY) != NULL)
            {                                
                String sMHG365Error = JSON.serialize(mapOfResponse);
                if(String.isNotBlank(sMHG365Error) && sMHG365Error.containsIgnoreCase(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_VOB_FAULT_CODE))
                {
                    captureExceptions(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_MEMBERACTIVITY_ERROR_NODE,GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_MHG365_ERROR_FETCHING_DETAILS,sMHG365Error,'parseMyHumGo365Response', GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_ERROR,true);
                }                 
            }                
        } 
    }

    /*
    * Method Name   :    iconAttribute
    * Description   :    creates a map for icon attributes
    * Return Type   :    map<string,string>  
    */
    private static map<string,string> iconAttribute(String show, string color, string icon, String value, String isError, String erroMsg,String order)
    {
        map<string,string> iconAttrMap = new map<string,string>();
        iconAttrMap.put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_SHOW,show);
        iconAttrMap.put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_COLOR, color);
        iconAttrMap.put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_ICON, icon);
        iconAttrMap.put(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_VALUE, value);
        iconAttrMap.put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_MHG365_ORDER, order);
        iconAttrMap.put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_MHG365_ISERROR_KEY, isError);
        iconAttrMap.put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_MHG365_ERRORMSG_KEY, erroMsg);
        return iconAttrMap;
    }
    
    /*
    * Method Name   :    captureExceptions
    * Description   :    captures and logs the various kinds of exceptions
    * Return Type   :    void
    */
    private static void captureExceptions(String errorNode,String errorMessage,String customErrorMessage,String methodName,String exceptionType,Boolean invokeLogFaultError)
    {
        String loggingErrorMessage;
        showIconMap.put(errorNode,iconAttribute('','','','','true',errorMessage,null));        
        loggingErrorMessage = String.isNotBlank(customErrorMessage) ? customErrorMessage : errorMessage;
        if(invokeLogFaultError)
        {
            listOfErrorLog.add(GenericHelper_CRMRetail_H_HUM.logFaultError(loggingErrorMessage,'CRMRetail_Generic_Icon_LC_HUM', methodName, '', '', GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_ERROR));                        
        }
        else
        {
            listOfErrorLog.add(GenericHelper_CRMRetail_H_HUM.createErrorLog('','CRMRetail_Generic_Icon_LC_HUM',methodName,loggingErrorMessage,exceptionType));           
        }        
    }
}
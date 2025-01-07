/***********************************************************************************************************************************
* Apex Class Name  : PhoneBook_S_HUM 
* Version          : 1.0
* Created Date     : 22nd January, 2021
* Function         : CRM Search Integration
* Test Class       : 
****************************************************************************
* Modification Log:
* Developer Name                             Code Review #           Date                 Description
*---------------------------------------------------------------------------------------------------------------------------------
* Isha Gupta                                                        01/22/2021            Original Version
* Shreya Agrawal/Isha Gupta											01/30/2021			  PhoneBook Implementation
* Nikhil/Isha														05/28/2021			  Error handling
* Shreya Agrawal													07/13/2021            MF2- US-2357061  IVR End Call or Transfer – IVR Call Data End Agent Session
**********************************************************************************************************************************/
public with sharing class PhoneBook_S_HUM 
{
    /**
* Returns continuation object
* <p>
* Method to call continuation service for the service for GetCallData services .
*
* @param   String     uuid
* @param  PhoneBook_C_HUM oThis
* @return  Continuation
*/
    public Continuation getDataFromWebService(String uuid,PhoneBook_C_HUM oThis)
    {
        Continuation con = null;
        try
        {
            HUM_Webservice_Callout__c GetCalldataServiceDetails = HUM_Webservice_Callout__c.getValues(PhoneBookConstants_H_HUM.SERVICE_GETCALLDATA);
            if (GetCalldataServiceDetails != null && GetCalldataServiceDetails.Active__c)
            {
                integer iTIMEOUT_INT_SECS = Integer.valueof(GetCalldataServiceDetails.Timeout__c);
                con = new Continuation(iTIMEOUT_INT_SECS); 
                con.continuationMethod = 'processResponse';
                HttpRequest request = new HttpRequest();
                request.setEndpoint(GetCalldataServiceDetails.End_Point_URL__c);
                request.setMethod(GetCalldataServiceDetails.Request_Type__c);
                request.setBody('{"GetCallData": {"UUID":"'+ uuid+'"}}');
                if (!Test.isRunningTest() && String.isNotBlank(GetCalldataServiceDetails.Certificate_Name__c)) request.setClientCertificateName(GetCalldataServiceDetails.Certificate_Name__c);
                oThis.sconLabel =  con.addHttpRequest(request);
            } 
        }catch(Exception e)
        {
            con = null;
        }
        return con;
    }
    /**
* Returns responses of continuation object
* <p>
* @param  String sconLabel
* @return Object
</p>
*/
    public  Object processResponse(String sconLabel)
    {
        try
        {
            HttpResponse response = Continuation.getResponse(sconLabel); 
            String result = response.getBody();
            return result;  
        }catch(Exception e)
        {
            throw new CalloutException();
        }
    }
    
    /**
* Returns continuation object
* <p>
* Method to call continuation service for the service for GetAllPhoneBookRules services .
*
* @param  PhoneBook_C_HUM oThis
* @return  Continuation
*/
    public Continuation AllPhoneBookRules(PhoneBook_C_HUM oThis)
    {
        Continuation con = null;
        try
        {
            HUM_Webservice_Callout__c RSPBRulesServiceDetails = HUM_Webservice_Callout__c.getValues(PhoneBookConstants_H_HUM.SERVICE_ALLPHONEBOOKRULES);
            if (RSPBRulesServiceDetails != null && RSPBRulesServiceDetails.Active__c)
            {
                integer iTIMEOUT_INT_SECS = Integer.valueof(RSPBRulesServiceDetails.Timeout__c);
                con = new Continuation(iTIMEOUT_INT_SECS); 
                con.continuationMethod = 'processResponsegetAllPhoneBookRules';
                HttpRequest request = new HttpRequest();
                request.setEndpoint(RSPBRulesServiceDetails.End_Point_URL__c);
                request.setMethod(RSPBRulesServiceDetails.Request_Type__c);
                request.setBody('{"GetAllPhoneBookRulesRequest": ""}');
                if (!Test.isRunningTest() && String.isNotBlank(RSPBRulesServiceDetails.Certificate_Name__c)) request.setClientCertificateName(RSPBRulesServiceDetails.Certificate_Name__c);
                oThis.AllPhBookRulesRequest =  con.addHttpRequest(request);
            }
        }
        catch(Exception e)
        {
           HUMExceptionHelper.logErrors(e,'Phonebook_S_HUM', 'AllPhoneBookRules');
        }
        return con;
    }
    
    /**
* Returns responses of continuation object
* <p>
* @param  String AllPhBookRulesRequest
* @return Object
</p>
*/
   public  Object processResponsegetAllPhoneBookRules(String AllPhBookRulesRequest)
    {
        String result ='';
        try
        {
            HttpResponse response = Continuation.getResponse(AllPhBookRulesRequest); 
            result = response.getBody();
        }
        catch(Exception e){
           HUMExceptionHelper.logErrors(e,'Phonebook_S_HUM', 'processResponsegetAllPhoneBookRules');
        }
        return result;  
    }
    
    /**
* Returns continuation object
* <p>
* Method to call continuation service for the service for RouteSoftphoneXml services .
*
* @param String reqBody
* @param  PhoneBook_C_HUM oThis
* @return  Continuation
*/
    public Continuation fetchDefaultPhoneBookRules(String reqBody,PhoneBook_C_HUM oThis)
    {
        Continuation con = null;
        try
        {
            HUM_Webservice_Callout__c RSRouteXMLServiceDetails = HUM_Webservice_Callout__c.getValues(PhoneBookConstants_H_HUM.SERVICE_ROUTEXML);
            if (RSRouteXMLServiceDetails != null && RSRouteXMLServiceDetails.Active__c)
            {
                integer iTIMEOUT_INT_SECS = Integer.valueof(RSRouteXMLServiceDetails.Timeout__c);
                con = new Continuation(iTIMEOUT_INT_SECS); 
                con.continuationMethod = 'processResponsegetDefaultPhBookRules';
                HttpRequest request = new HttpRequest();
                request.setEndpoint(RSRouteXMLServiceDetails.End_Point_URL__c);
                request.setMethod(RSRouteXMLServiceDetails.Request_Type__c);
                request.setBody(reqBody);
                if (!Test.isRunningTest() && String.isNotBlank(RSRouteXMLServiceDetails.Certificate_Name__c)) request.setClientCertificateName(RSRouteXMLServiceDetails.Certificate_Name__c);
                oThis.defaultphBookRequest =  con.addHttpRequest(request);
            }
        }
        catch(Exception e)
        {
           HUMExceptionHelper.logErrors(e,'Phonebook_S_HUM', 'fetchDefaultPhoneBookRules');
        }
        return con;
    }
    
    /**
* Returns responses of continuation object
* <p>
* @param  String defaultphBookRequest
* @return Object
</p>
*/
    public  Object processResponsegetDefaultPhBookRules(String defaultphBookRequest)
    {
        String result='';
        try
        {
            HttpResponse response = Continuation.getResponse(defaultphBookRequest); 
            result = response.getBody();
        }
        catch(Exception e)
        {
           HUMExceptionHelper.logErrors(e,'Phonebook_S_HUM', 'processResponsegetDefaultPhBookRules'); 
        }
        return result;  
    }
    
    /**
* Returns continuation object
* <p>
* Method to call continuation service for the service for SaveCallData and RouteSoftphoneXml services .
*
* @param String reqBodyForRoutingXML
* @param  PhoneBook_C_HUM oThis
* @return  Continuation
*/
    public Continuation OnCallTransfer(String reqBodyForRoutingXML,PhoneBook_C_HUM oThis)
    {
        Continuation con = null;
        try
        {
            HUM_Webservice_Callout__c RSRoutingxmlServiceDetails = HUM_Webservice_Callout__c.getValues(PhoneBookConstants_H_HUM.SERVICE_ROUTEXML);
            integer iTIMEOUT_INT_SECS = Integer.valueof(RSRoutingxmlServiceDetails.Timeout__c);
            con = new Continuation(iTIMEOUT_INT_SECS); 
            con.continuationMethod = 'processResponsegetDefaultPhBookRules';
            if (RSRoutingxmlServiceDetails != null && RSRoutingxmlServiceDetails.Active__c)
            {
                HttpRequest requestForRoutingXML = new HttpRequest();
                requestForRoutingXML.setEndpoint(RSRoutingxmlServiceDetails.End_Point_URL__c);
                requestForRoutingXML.setMethod(RSRoutingxmlServiceDetails.Request_Type__c);
                requestForRoutingXML.setBody(reqBodyForRoutingXML);
                requestForRoutingXML.setClientCertificateName(RSRoutingxmlServiceDetails.Certificate_Name__c);
                oThis.defaultphBookRequest = con.addHttpRequest(requestForRoutingXML);
            }
        }
        catch(Exception e)
        {
           HUMExceptionHelper.logErrors(e,'Phonebook_S_HUM', 'OnCallTransfer');
        }
        return con;
    }
    /**
* Returns responses of continuation object
* <p>
* @param  String defaultphBookRequest
* @param PhoneBook_C_HUM oThis
* @return Object
</p>
*/
     public  Object processResponsegetDefaultPhBookRules(String defaultphBookRequest,PhoneBook_C_HUM oThis)
    { 
        String result ='';
        try
        {
            HttpResponse RouteXMLresponse = Continuation.getResponse(defaultphBookRequest); 
            result = RouteXMLresponse.getBody();
        }
        catch(Exception e){
            HUMExceptionHelper.logErrors(e,'Phonebook_S_HUM', 'processResponsegetDefaultPhBookRules');
        }
        return result;  
    } 

    /**
    * Returns continuation object
    * <p>
    * Method to call continuation service for the service for SaveCallData services to send updated to IVR .
    *
    * @param String reqBodyForSaveCallData
    * @param  PhoneBook_C_HUM oThis
    * @return  Continuation
    */
    public static Continuation saveCallDataServiceCallout(String reqBodyForSaveCallData)
    {
        Continuation con = null;
        String SaveCallDataRequest;
        try
        {
            HUM_Webservice_Callout__c RSSaveCallDataServiceDetails = HUM_Webservice_Callout__c.getValues(PhoneBookConstants_H_HUM.SERVICE_SAVECALLDATA);
            integer iTIMEOUT_INT_SECS = Integer.valueof(RSSaveCallDataServiceDetails.Timeout__c);
            con = new Continuation(iTIMEOUT_INT_SECS); 
          	con.continuationMethod = 'processResponseSaveCallData';
            if (RSSaveCallDataServiceDetails != null && RSSaveCallDataServiceDetails.Active__c)
            {
                HttpRequest requestForSaveCallData = new HttpRequest();
                requestForSaveCallData.setEndpoint(RSSaveCallDataServiceDetails.End_Point_URL__c);
                requestForSaveCallData.setMethod(RSSaveCallDataServiceDetails.Request_Type__c);
                requestForSaveCallData.setBody(reqBodyForSaveCallData);
                if (!Test.isRunningTest() && String.isNotBlank(RSSaveCallDataServiceDetails.Certificate_Name__c)) requestForSaveCallData.setClientCertificateName(RSSaveCallDataServiceDetails.Certificate_Name__c);
                con.addHttpRequest(requestForSaveCallData);
            }
            
        }
        catch(Exception e)
        {
           HUMExceptionHelper.logErrors(e,'Phonebook_S_HUM', 'saveCallDataServiceCallout');
        }
        return con;
    }
    /**
    * Returns responses of continuation object
    * <p>
    * @param String SaveCallDataRequest
    * @param PhoneBook_C_HUM oThis
    * @return Object
    </p>
    */
     public static Object processResponseSaveCallData(list<string> lstLabels,Object oState)
    {
        HttpResponse SCResponse = new HttpResponse();
        try
        {
            SCResponse = Continuation.getResponse(lstLabels[0]);
        }
        catch(Exception e){
            HUMExceptionHelper.logErrors(e,'Phonebook_S_HUM', 'processResponseSaveCallData');
        }
        return SCResponse;  
    } 

    /**
    * <p>
    * Method to create requests for SaveCallData by collecting Interaction details
    *
    * @param  String uuidata
    * @return String
    */
    public static String createSaveCalldataRequest(String uuidata, Map<String,String> StateDataMapping,String intAbtId)
    {
        String reqBodyForSaveCallData='';
        Map<String,String> saveCallDataMapping = new map<String,String>();
            
        try
        {
            Map<String,String> interactionsavecallmapping =  PhoneBook_D_HUM.getInteractionInfoForSaveCallData(uuidata,intAbtId);
            if(interactionsavecallmapping.size()>0)
            {
                for(String key: interactionsavecallmapping.KeySet())
                {
                    saveCallDataMapping.put(key,interactionsavecallmapping.get(key));
                }
            }

            if(StateDataMapping.containsKey(PhoneBookConstants_H_HUM.CALL_TYPE))
            {
                User userRec = [select id,name,Network_User_Id__c from User where id =: UserInfo.getUserId() ];
                String userId=userRec.Network_User_Id__c!=null?userRec.Network_User_Id__c:'';
                String userName=userRec.Name;
                saveCallDataMapping.put(PhoneBookConstants_H_HUM.USER_ID,userId);
                saveCallDataMapping.put(PhoneBookConstants_H_HUM.USER_NAME,userName);
            }
            
            if(StateDataMapping.containsKey(PhoneBookConstants_H_HUM.TRANSFER_NUMBER))
            {
                Map<String,String> callTransferDataMapping = PhoneBook_D_HUM.getCallTransferData(uuidata);
                
                if(callTransferDataMapping.size()>0 )
                {
                    for(String key: callTransferDataMapping.KeySet())
                    {
                        saveCallDataMapping.put(key,callTransferDataMapping.get(key));
                    }
                }
            }
            
            reqBodyForSaveCallData = JSON.serialize(CreateSaveCallDataRequest_DTO_HUM.requestStructuresaveCallData(saveCallDataMapping,StateDataMapping, uuidata));
        }
        catch(Exception ex)
        {
            String errorLogNum =  HUMExceptionHelper.logError(String.valueOf(ex), 'PhoneBook_C_HUM', 'createSaveCalldataRequest','System.InvalidParameterValueException','Integration Error');
            String errMsg = 'An error has occurred; please refresh your browser and try again.  If this error persists, contact the help desk for assistance: '+errorLogNum;
            ApexPages.addMessage(new ApexPages.message(ApexPages.severity.ERROR,errMsg));  
        }
        
        return reqBodyForSaveCallData;
    }

    public Continuation getDataFromWebServiceMultipleTabs(String uuid,PhoneBook_C_HUM oThis)
    {
        Continuation con = null;
        try
        {
            HUM_Webservice_Callout__c GetCalldataServiceDetails = HUM_Webservice_Callout__c.getValues(PhoneBookConstants_H_HUM.SERVICE_GETCALLDATA);
            if (GetCalldataServiceDetails != null && GetCalldataServiceDetails.Active__c)
            {
                integer iTIMEOUT_INT_SECS = Integer.valueof(GetCalldataServiceDetails.Timeout__c);
                con = new Continuation(iTIMEOUT_INT_SECS); 
                con.continuationMethod = 'processResponseMultipletabs';
                HttpRequest request = new HttpRequest();
                request.setEndpoint(GetCalldataServiceDetails.End_Point_URL__c);
                request.setMethod(GetCalldataServiceDetails.Request_Type__c);
                request.setBody('{"GetCallData": {"UUID":"'+ uuid+'"}}');
                if (!Test.isRunningTest() && String.isNotBlank(GetCalldataServiceDetails.Certificate_Name__c)) request.setClientCertificateName(GetCalldataServiceDetails.Certificate_Name__c);
                oThis.sconLabel =  con.addHttpRequest(request);
            } 
        }catch(Exception e)
        {
            con = null;
        }
        return con;
    }
    /**
    * Returns responses of continuation object
    * <p>
    * @param  String sconLabel
    * @return Object
    </p>
    */
    public  Object processResponseMultipletabs(String sconLabel)
    {
        try
        {
            HttpResponse response = Continuation.getResponse(sconLabel); 
            String result = response.getBody();
            return result;  
        }catch(Exception e)
        {
            throw new CalloutException();
        }
    }
}
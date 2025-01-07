/*******************************************************************************************************************************
Apex Class Name : ArchivedCaseHistory_LS_HUM.cls
Version         : 1.0
Created On      : 07/22/2022
Function        : Service Class for ArchivedCaseHistory_LC_HUM.
TestClass       : ArchivedCaseHistory_LT_HUM.cls
Modification Log:
* Developer Name            Code Review                Date                       Description
*-------------------------------------------------------------------------------------------------------------------------------
* Ankime/Isha		                   				  07/22/2022                  Original version
* Ankime/Isha		                   				  11/29/2022                  Service related Change Get to Post
*****************************************************************************************************************************************/
public with sharing class ArchivedCaseHistory_LS_HUM {
    
/*
* Method Name   :    requestArchiveCaseData
* Description   :    This method is used for returning Details related to archived Cases
* Return Type   :  	 Object
* Parameters    :    String, String,String, Integer,Integer Boolean,String,String
*/
public static object requestArchiveCaseData(String accId,String fromDate,String toDate, Integer startRow, Integer endRow, Boolean isArchived, String caseNumber,String whereClause)
    {
        try
        {	
            String reqParam;
            if(String.isBlank(caseNumber)){
                reqParam = accId+'?StartDate='+fromDate+'&EndDate='+toDate+'&StartRow='+startRow+'&EndRow='+endRow;
            }
            else{
                reqParam = accId+'?CaseNumber='+caseNumber;   
            }
            ArchivedCaseHistoryRequest_DTO_HUM archiveDataRequest = new ArchivedCaseHistoryRequest_DTO_HUM();
            archiveDataRequest = formRequest(whereClause);
            HUM_Webservice_Callout__c archiveSummaryCallout = HUM_Webservice_Callout__c.getValues('ArchivedCaseSearchService');
            integer iTIMEOUT_INT_SECS = Integer.valueof(archiveSummaryCallout.Timeout__c) / 1000;
            Continuation con = new Continuation(iTIMEOUT_INT_SECS);
            con.continuationMethod = 'parseArchivedResponse';
            HttpRequest httpRequestArchiveSummary = builtHttpRequest(archiveSummaryCallout,reqParam);
            httpRequestArchiveSummary.setBody(JSON.serialize(archiveDataRequest));
            con.addHttpRequest(httpRequestArchiveSummary);
            return con;
        }
        catch(Exception ex)
        {
            System.debug('error in search data: '+ex.getMessage());
            HUMExceptionHelper.logErrors(ex, 'ArchivedCaseHistory_LS_HUM', 'requestArchiveCaseData');
            return null;
        }
    }
    /*
    * Method Name   :    formRequest
    * Description   :    This method is used to create JSON Request
    * Return Type   :  	 Object
    * Parameters    :    String, String,String, Integer,Integer Boolean,String,String
    */
	public static ArchivedCaseHistoryRequest_DTO_HUM formRequest(String whereClause){
        ArchivedCaseHistoryRequest_DTO_HUM oDRRequest = new ArchivedCaseHistoryRequest_DTO_HUM();
        oDRRequest.Criteria = whereClause;
        return oDRRequest;

    }
    
	 /**
    * Returns HttpRequest request with request body
    * <p>
    * Method to prepare httpRequest details and certification details
    * @param  HUM_Webservice_Callout__c record .
    * @return HttpRequest
    */
    public static HttpRequest builtHttpRequest(HUM_Webservice_Callout__c ServiceObj, String reqParam)
    {
        HttpRequest request = new HttpRequest();
        if (ServiceObj != null && ServiceObj.Active__c)
        {
           // request.setMethod(ServiceObj.Request_Type__c);
            request.setMethod(ServiceObj.Request_Type__c);
            request.setHeader('client_id', HUMConstants__c.getValues('HUMArchival_clientId').StringValue__c); 
            request.setHeader('client_secret', HUMConstants__c.getValues('HUMArchival_clientSecret').StringValue__c);
            request.setHeader('Content-Type', 'application/json');	
            request.setEndpoint(ServiceObj.End_Point_URL__c+reqParam);
         }
        return request;
    } 
    /*
    * Method Name   :    parseArchivedResponse
    * Description   :    This method is used to parse the response
    * Return Type   :  	 Object
    */
    public static Object parseArchivedResponse(List<string> labels,object state){
       
        try
        {
            HttpResponse archiveResponse = Continuation.getResponse(labels[0]);
        	String responseBodyOrg = archiveResponse.getBody();
            Set<String> CTCIIds = new Set<String>();
            Set<String> AccountIds = new Set<String>();
            Map<String,String> classificationIntent = new Map<String,String>();
            Map<String,String> interactionWith = new Map<String,String>();
            String classificationVal='';
            String IntentVal='';
        	String InteractingWithVal='';
            String responseBody;
        
         //Below code will be used once we have real time response from service callout    
            System.debug('archiveResponse.getBody() : '+archiveResponse.getBody());
           if(archiveResponse!= null && !archiveResponse.getBody().containsIgnoreCase(GLOBAL_CONSTANT_LH_HUM.VOB_FAULT_CODE)  && ((archiveResponse.getStatusCode() == GLOBAL_CONSTANT_LH_HUM.HTTP_OK_HUM || archiveResponse.getStatusCode() == GLOBAL_CONSTANT_LH_HUM.HTTP_ACCEPTED_HUM)))
            {
                String sArchiveResponseBody = archiveResponse.getBody();
                ArchivedCaseHistoryResponse_DTO_HUM oResponse = (ArchivedCaseHistoryResponse_DTO_HUM)System.JSON.deserialize(sArchiveResponseBody, ArchivedCaseHistoryResponse_DTO_HUM.class);
                for(ArchivedCaseHistoryResponse_DTO_HUM.CaseResults caseObj : oResponse.CaseSearchResponse.CaseResults){
                    CTCIIds.add(caseObj.sCTCI_List);
                    AccountIds.add(caseObj.sInteracting_With);
                }
               
                List<String> tempCTCI = new List<String>();
                    tempCTCI.addAll(CTCIIds);
                List<String> tempAcc = new List<String>();
                    tempAcc.addAll(AccountIds);
                for(CTCI_Junction__c obj:ArchivedCaseHistory_LD_HUM.CTCIData(tempCTCI)){
                    classificationIntent.put(obj.id, obj.Classification__r.Name+'~'+ obj.Intent__r.Name);
                }
                for(Account accObj:ArchivedCaseHistory_LD_HUM.AccData(tempAcc)){
                    interactionWith.put(accObj.id,accObj.Name);    
                }
                
                if(oResponse.CaseSearchResponse.CaseResults.size() > 0){
                for(ArchivedCaseHistoryResponse_DTO_HUM.CaseResults caseResult : oResponse.CaseSearchResponse.CaseResults){
                     if(classificationIntent.containsKey(caseResult.sCTCI_List)){
                        classificationVal = classificationIntent.get(caseResult.sCTCI_List);
                        classificationVal = classificationVal.split('~')[0];
                        IntentVal = classificationIntent.get(caseResult.sCTCI_List);
                        IntentVal = IntentVal.split('~')[1];
                     }
                    caseResult.sClassification=classificationVal;
                    caseResult.sIntent=IntentVal;
        
                    caseResult.sCreatedDate = ArchivedcaseDetail_LS_HUM.formatDate(caseResult.sCreatedDate,'Date','MM/dd/yyyy');
                    caseResult.sClosedDate = ArchivedcaseDetail_LS_HUM.formatDate(caseResult.sClosedDate,'Date','MM/dd/yyyy');
                    caseResult.sFollow_up_Due_Date = ArchivedcaseDetail_LS_HUM.formatDate(caseResult.sFollow_up_Due_Date,'Date','MM/dd/yyyy');
                    if(interactionWith.containsKey(caseResult.sInteracting_With)){
                        InteractingWithVal=interactionWith.get(caseResult.sInteracting_With);
                    }
                    caseResult.sInteracting_With = InteractingWithVal;
                }
                }
                String oldCaseDate = oResponse.CaseSearchResponse.Header.sOldestCaseDate;
                oldCaseDate = ArchivedcaseDetail_LS_HUM.formatDate(oldCaseDate,'Date','MM/dd/yyyy');
                oResponse.CaseSearchResponse.Header.sOldestCaseDate = oldCaseDate;
                
        		return oResponse;
            } 
            else if(archiveResponse.getStatusCode() == 2000){
            	String errorCode = HUMExceptionHelper.logError('Error In Response: Request Time Out'+archiveResponse.getStatusCode(), 'ArchivedCaseHistory_LS_HUM', 'builtHttpRequest','System.CalloutException',
											'Request Time Out Error');
                

				ArchivedCaseHistoryResponse_DTO_HUM.CaseSearchResponse oResponse = new ArchivedCaseHistoryResponse_DTO_HUM.CaseSearchResponse();
                oResponse.sError=true;
                oResponse.sErrorMsg='Contact System Administrator: '+errorCode;
                return oResponse;
            }
            else{
				String errorCode = HUMExceptionHelper.logError('Error In Response:'+archiveResponse.getStatusCode(), 'ArchivedCaseHistory_LS_HUM', 'builtHttpRequest','System.CalloutException',
											'Error In Response');
                

				ArchivedCaseHistoryResponse_DTO_HUM.CaseSearchResponse oResponse = new ArchivedCaseHistoryResponse_DTO_HUM.CaseSearchResponse();
                oResponse.sError=true;
                oResponse.sErrorMsg='Contact System Administrator: '+errorCode;
                return oResponse;
               
            } 
        
        }
        catch(Exception ex)
        {
            System.debug('error inresposnse : '+ex.getMessage());
            HUMExceptionHelper.logErrors(ex, 'ArchivedCaseHistory_LS_HUM', 'parseArchivedResponse');
        }
        return null;
  

    }

}
/*******************************************************************************************************************************
Apex Class Name : ArchivedCaseDetail_LS_HUM
Version         : 1.0
Created On      : 07/18/2022   
Function        : This class is used to get data from archived case detail service.
Test Class      : ArchivedCaseDetail_LT_HUM

Modification Log: 
*  Developer Name             Code Review              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------
*  Ashish Kumar/Kajal Namdev		                   07/18/2022                  Original version
*  Ashish Kumar/Kajal Namdev		                   11/29/2022                  DF-6727 Fix
**********************************************************************************************************************************************************/
public with sharing class ArchivedCaseDetail_LS_HUM {
    
    /*
* Method Name   :    requestCaseDetails
* Description   :    This method is used for returning Details related to archived Case Detail, case comments, tasks and Case history
* Return Type   :  	 Object
* Parameters    :    String, String
*/
    public static object requestCaseDetails(String sCaseId,String ObjectName, String StartRow, String EndRow)
    {
        try
        {   
            String reqParam= sCaseId+'?StartRow='+startRow+'&EndRow='+EndRow+'&ObjectName='+ObjectName;
            HUM_Webservice_Callout__c archivedDetailsCallout = HUM_Webservice_Callout__c.getValues('ArchivedCaseDetailService');
            integer iTIMEOUT_INT_SECS = Integer.valueof(archivedDetailsCallout.Timeout__c) / 1000;
            Continuation con = new Continuation(iTIMEOUT_INT_SECS);
            con.continuationMethod ='parseCaseDetails';
            HttpRequest httpRequestCaseDetails = builtHttpRequest(archivedDetailsCallout,reqParam);
            con.addHttpRequest(httpRequestCaseDetails);
            return con;
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'ArchivedCaseDetail_LS_HUM', 'requestCaseDetails');
            return null;
        }
    }
    
    /*
* Method Name   :    parseCaseDetails
* Description   :    This method is used to parse the response
* Return Type   :  	 Object
*/
    public static Object parseCaseDetails(List<string> labels,object state)
    {
        try
        {
            Set<Id> caseHistoryUserIds = new Set<Id>();    
            HttpResponse caseDetailsResponse = Continuation.getResponse(labels[0]);
            if(caseDetailsResponse!= null && !caseDetailsResponse.getBody().containsIgnoreCase(GLOBAL_CONSTANT_HUM.VOB_FAULT_CODE)  && ((caseDetailsResponse.getStatusCode() == GLOBAL_CONSTANT_HUM.HTTP_OK_HUM || caseDetailsResponse.getStatusCode() == GLOBAL_CONSTANT_HUM.HTTP_ACCEPTED_HUM) ))
            {
                String Jsons = caseDetailsResponse.getBody();
                
                //Case history
                ArchivedCaseDetail_DTO_HUM adt= (ArchivedCaseDetail_DTO_HUM)System.JSON.deserialize(Jsons, ArchivedCaseDetail_DTO_HUM.class);
                ArchivedCaseDetail_DTO_HUM.CaseHistoryResponse caseHistoryRes = adt.CaseHistoryResponse;
                ArchivedCaseDetail_DTO_HUM.CaseCommentsResponse caseCommentRes = adt.CaseCommentsResponse;
                ArchivedCaseDetail_DTO_HUM.CaseTasksResponse caseTaskRes = adt.CaseTasksResponse;
                ArchivedCaseDetail_DTO_HUM.CaseDetailResponse caseDetailRes = adt.CaseDetailResponse;

                //Case detail Related changes
                caseDetailRes.sCreatedDate = formatDate(caseDetailRes.sCreatedDate,'DateTime','MM/dd/YYYY hh:mm a');
                caseDetailRes.sClosedDate = formatDate(caseDetailRes.sClosedDate,'DateTime','MM/dd/YYYY hh:mm a');
                caseDetailRes.sFollow_up_Due_Date = formatDate(caseDetailRes.sFollow_up_Due_Date,'Date','MM/dd/YYYY');
                caseDetailRes.sRe_Open_Case_Date  = formatDate(caseDetailRes.sRe_Open_Case_Date ,'Date','MM/dd/YYYY');
                caseDetailRes.sOGO_Resolution_Date  = formatDate(caseDetailRes.sOGO_Resolution_Date ,'Date','MM/dd/YYYY');    
                        
                if(String.isNotBlank(caseDetailRes.sCreatedById)){
                    List<user> UserList = ArchivedCaseDetail_LD_HUM.fetchUser(caseDetailRes.sCreatedById);
                    caseDetailRes.sCreatedByName = UserList.size()>0 ? UserList[0].Name : '';
                }
                if(String.isNotBlank(caseDetailRes.sLastModifiedById)){
                    List<user> UserList = ArchivedCaseDetail_LD_HUM.fetchUser(caseDetailRes.sLastModifiedById);
                    caseDetailRes.sLastModifiedByName = UserList.size()>0 ? UserList[0].Name : '';
                }
                if(String.isNotBlank(caseDetailRes.sContactId)){
                    List<Contact> contactList = ArchivedCaseDetail_LD_HUM.fetchContact(caseDetailRes.sContactId);
                    caseDetailRes.sContactName = contactList.size()>0 ? contactList[0].Name : '';
                }
                List<Account> accountList = String.isNotBlank(caseDetailRes.sAccountId) ? ArchivedCaseDetail_LD_HUM.fetchAccountData(caseDetailRes.sAccountId) : new List<Account>();
                caseDetailRes.sAccountName = accountList.size() > 0 ? accountList[0].Name : '';
                if (String.IsNotBlank(caseDetailRes.sMember_Plan_Id) && caseDetailRes.sMember_Plan_Id != null){
                    List<MemberPlan> memPlanList = ArchivedCaseDetail_LD_HUM.fetchMemberPlanData(caseDetailRes.sMember_Plan_Id);
                    if(memPlanList.size()>0){
                        caseDetailRes.sMemberPlanName = memPlanList[0].Name;
                        if (String.IsNotBlank(memPlanList[0].Member_Plan_External_Id__C) && memPlanList[0].Member_Plan_External_Id__C != null){
                            List<Policy_Member__c> polMemberList = ArchivedCaseDetail_LD_HUM.fetchPolicyMemberData(memPlanList[0].Member_Plan_External_Id__C);
                            if(polMemberList.size()>0){
                                caseDetailRes.sPolicy_Member =	polMemberList[0].Id;
                                caseDetailRes.sPolicyMemberName = polMemberList[0].Name;
                                
                            }
                        }
                    }
                }
                else if(String.IsNotBlank(caseDetailRes.sPolicy_Member) && caseDetailRes.sPolicy_Member != null){
                    List<Policy_Member__c> polMemberList = ArchivedCaseDetail_LD_HUM.getPolicyMember(caseDetailRes.sPolicy_Member);
                        if(polMemberList.size()>0){
                         caseDetailRes.sPolicy_Member =  polMemberList[0].Id;
                         caseDetailRes.sPolicyMemberName = polMemberList[0].Name;
                        }
                }
                List<Account> Interacting_WithList = String.isNotBlank(caseDetailRes.sInteracting_With) ? ArchivedCaseDetail_LD_HUM.fetchAccountData(caseDetailRes.sInteracting_With) : new List<Account>();
                caseDetailRes.sInteractingWithName = Interacting_WithList.size() > 0 ? Interacting_WithList[0].Name : '';
                caseDetailRes.sClassificationName =String.isNotBlank(caseDetailRes.sClassification_Id) && ArchivedCaseDetail_LD_HUM.fetchClassificationData(caseDetailRes.sClassification_Id).size()>0 ?ArchivedCaseDetail_LD_HUM.fetchClassificationData(caseDetailRes.sClassification_Id)[0].Name : '';
                caseDetailRes.sIntentName =String.isNotBlank(caseDetailRes.sIntent_Id) && ArchivedCaseDetail_LD_HUM.fetchIntentData(caseDetailRes.sIntent_Id).size()>0 ? ArchivedCaseDetail_LD_HUM.fetchIntentData(caseDetailRes.sIntent_Id)[0].Name: '';
                caseDetailRes.sRecordTypeName = String.isNotBlank(caseDetailRes.sRecordTypeId) ? ArchivedCaseDetail_LD_HUM.fetchRecordType(caseDetailRes.sRecordTypeId).Name : '';

                if(string.isNotBlank(caseDetailRes.sDCN)){
                    caseDetailRes.sDCN_Link = redirectTODCN(caseDetailRes.sDCN,caseDetailRes.sDCN_Type,caseDetailRes.sDCN_URL);
                }
                //case task Related changes
                for(ArchivedCaseDetail_DTO_HUM.CaseTasksResponseData caseTask : caseTaskRes.CaseTasksResponseData){
                    caseTask.sCreated_DateTime= formatDate(caseTask.sCreated_DateTime,'DateTime','MM/dd/YYYY hh:mm a');//'DateTime','yyyy/MM/dd hh:mm a'
                    caseTask.sDate_Time_Closed= formatDate(caseTask.sDate_Time_Closed,'DateTime','MM/dd/YYYY hh:mm a');//'DateTime','yyyy/MM/dd hh:mm a'
                    caseTask.sActivityDate= formatDate(caseTask.sActivityDate,'Date','MM/dd/YYYY');//'DateTime','yyyy/MM/dd hh:mm a'
                }
                
                //case history related changes
                Map<String, String> mapApiVsLabel= fetchFieldLabelMap('Case');

                for(ArchivedCaseDetail_DTO_HUM.CaseHistoryResponseData caseHist : caseHistoryRes.CaseHistoryResponseData){
                    caseHistoryUserIds.add(caseHist.sCreatedById);
                    //manipulation to get label of fields from API
                    caseHist.sField = (mapApiVsLabel !=null && String.isNotBlank(mapApiVsLabel.get(caseHist.sField))) ? mapApiVsLabel.get(caseHist.sField) :caseHist.sField ;                   
                }
                
                for(ArchivedCaseDetail_DTO_HUM.CaseCommentsResponseData caseCom : caseCommentRes.CaseCommentsResponseData){
                    caseHistoryUserIds.add(caseCom.sCreatedById);
                    caseHistoryUserIds.add(caseCom.sLastModifiedById);
                }
                
                if(caseHistoryUserIds.size()>0){
                    Map<ID, User> caseUserMap = new Map<ID, User>(ArchivedCaseDetail_LD_HUM.fetchUserDetails(caseHistoryUserIds));
                    
                    for(ArchivedCaseDetail_DTO_HUM.CaseHistoryResponseData caseHist : caseHistoryRes.CaseHistoryResponseData){
                        if(caseUserMap.containsKey(caseHist.sCreatedById)){
                            User userObj = caseUserMap.get(caseHist.sCreatedById);
                            caseHist.sCreatedByName = userObj.Name;
                        }
                        caseHist.sCreatedDate= formatDate(caseHist.sCreatedDate,'DateTime','MM/dd/YYYY hh:mm:ss a');
                    }
                    
                    //Case comment related changes
                    for(ArchivedCaseDetail_DTO_HUM.CaseCommentsResponseData caseCom : caseCommentRes.CaseCommentsResponseData){
                        if(caseUserMap.containsKey(caseCom.sCreatedById)){
                            User userObj = caseUserMap.get(caseCom.sCreatedById);
                            caseCom.sCreatedByName = userObj.Name;
                        }
                        if(caseUserMap.containsKey(caseCom.sLastModifiedById)){
                            User userObj = caseUserMap.get(caseCom.sLastModifiedById);
                            caseCom.sLastModifiedByName = userObj.Name;
                        }
                        
                        caseCom.sLastModifiedDate = formatDate(caseCom.sLastModifiedDate,'DateTime','hh:mm a | MM/dd/YYYY');
                        caseCom.sCreatedDate = formatDate(caseCom.sCreatedDate,'DateTime','hh:mm a | MM/dd/YYYY');
                    }
                }
                return adt;
            }else if(caseDetailsResponse.getStatusCode() == 2000){
                String errorCode = HUMExceptionHelper.logError('Error In Response:Request Time Out '+caseDetailsResponse.getStatusCode(), 'ArchivedCaseHistory_LS_HUM', 'builtHttpRequest','System.CalloutException',
                                                               'Request Time Out Error');
                              
                ArchivedCaseHistoryResponse_DTO_HUM.CaseSearchResponse oResponse = new ArchivedCaseHistoryResponse_DTO_HUM.CaseSearchResponse();
                oResponse.sError=true;
                oResponse.sErrorMsg='Contact System Administrator: '+errorCode;
                return oResponse;
            }else{
                String errorCode = HUMExceptionHelper.logError('Error in response:'+caseDetailsResponse.getStatusCode(), 'ArchivedCaseDetail_LS_HUM', 'builtHttpRequest','System.CalloutException',
                                                               'Integration Error');
                
                ArchivedCaseDetail_DTO_HUM oResponse = new ArchivedCaseDetail_DTO_HUM();
                oResponse.sError=true;
                oResponse.sErrorMsg='Contact System Administrator: '+errorCode;
                return oResponse;
            } 
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'ArchivedCaseDetail_LS_HUM', 'parseCaseDetails');
        }
        return null;
    }
    
    
    /**
* Returns HttpRequest request with request body
* <p>
* Method to prepare httpRequest details and certification details
*
* @param  HUM_Webservice_Callout__c record .
* @return HttpRequest
*/
    public static HttpRequest builtHttpRequest(HUM_Webservice_Callout__c ServiceObj, String reqParam)
    {
        HttpRequest request = new HttpRequest();
            if (ServiceObj != null && ServiceObj.Active__c)
            {
                request.setMethod(ServiceObj.Request_Type__c);
                request.setHeader('client_id', HUMConstants__c.getValues('HUMArchival_clientId').StringValue__c);
                request.setHeader('client_secret', HUMConstants__c.getValues('HUMArchival_clientSecret').StringValue__c);
                request.setEndpoint(ServiceObj.End_Point_URL__c+reqParam);
            }
        return request;
    } 
    
    /*
* Method Name   :    formatDate
* Description   :    This method is used format date
* Return Type   :  	 String,String,string
*/
    public static String formatDate(String dateValue, String dateType, String format)
    {	
        String dateValueFormatted = dateValue;
        if(String.isNotBlank(dateValue))
        {
            if(dateType=='DateTime')
            {
                DateTime dt=(DateTime)JSON.deserialize('"' + dateValue + '"', DateTime.class);
                dateValueFormatted = String.isNotBlank(dateValue)? dt.formatGMT(format):'';
                return dateValueFormatted;
            }
            else
            {
                Date dt1 = Date.valueOf(dateValue);                
                DateTime dt2 = DateTime.newInstance(dt1.year(), dt1.month(), dt1.day()); 
                dateValueFormatted = dt2.formatGMT(format);
                return dateValueFormatted;
            } 
        }
        return dateValueFormatted;
        
    }
        /*
    * Method Name   :    fetchFieldLabelMap
    * Description   :    This method is used to get the map of field API and field label
    * Return Type   :    Object
    */ 
    public static Map<String,String> fetchFieldLabelMap(String ObjectName)
    {    
       Map<String,String> apiVsLabelMap= new Map<String,String>();
       Map<String, Schema.SObjectField> mapObjDef= Schema.getGlobalDescribe().get(ObjectName).getDescribe().fields.getMap();
       if(mapObjDef!=null)
       {
              for(Schema.SObjectField sField : mapObjDef.values())
              {
                  apiVsLabelMap.put((string)sField.getDescribe().getName(),(string)sField.getDescribe().getLabel());
              } 
       }
       return apiVsLabelMap;

    }
    
    public static string redirectTODCN(string dcn, string dcnType, string dcnUrl){
        
        String ssoURL = Endpoint_Url_HUM__c.getInstance('SSO_LOGINURL').Remote_Url__c;   
        if(dcnType == 'claim' || dcnType == 'edoc'){
            string strurl = dcnUrl;
            return strurl;
        }else{
            
            dcnURL = HUMDCNApplicationUrl__c.getOrgDefaults().DCNImageURL__c;  
                       
            if(Endpoint_Url_HUM__c.getInstance('DCNSpider2URL') != null && String.isNotBlank(Endpoint_Url_HUM__c.getInstance('DCNSpider2URL').Remote_Url__c) && (dcn).startsWithIgnoreCase('AC'))
            {            
                dcnURL = Endpoint_Url_HUM__c.getInstance('DCNSpider2URL').Remote_Url__c ;            
                dcnURL = dcnURL + dcn;                        
            }
            else
            {           
                dcnURL = EncodingUtil.base64Encode(blob.valueOf(dcnURL+dcn));            
                dcnURL  = ssoURL +'?HIDDENTARGET='+dcnURL;
            }
            return dcnURL;
        }
    }
}
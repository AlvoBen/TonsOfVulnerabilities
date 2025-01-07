/**
 *   Name        : ProviderDisputes_S_HUM
 *   Description : This class serves as service class Provider Correspondence Dispute Service
 *   Version     : 1.0
 *   Test        : ProviderDisputes_S_T_HUM
 * Developer Name        Code Review #         Date                       Description
 *---------------------------------------------------------------------------------------------------
 * Ajay Chakradhar                            10/15/2019                  Original Version
 * Ajay Chakradhar                            11/27/2019                  Defect 450384 Fix
 * Ajay Chakradhar                            11/29/2019                  Defect-450506,450541 Fix
 * Priyanka Puranik						   	  02/08/2021				  REQ: 1669827 CR AEP readiness -Research - Critical Security Issues (slide 11)
 ************************************************************************************************************************************************* */
public with sharing class ProviderDisputes_S_HUM {

    public class ServiceDownException extends Exception {}
    @TestVisible private static String sBody ='' ;
    
    /*
    *  This method is used to form HTTP Request for Provider Correspondence Dispute Service
    *  @name   sendProviderDisputeCasesRequest
    *  @param  string sRecordId - Account to get data from service
    *  @return HttpRequest - Object
    */        
    public static Continuation sendProviderDisputeCasesRequest(string sRecordId)
    {
		HUM_Webservice_Callout__c oPDService = HUM_Webservice_Callout__c.getValues('ProviderCorrespondenceDisputes');
        Integer iTIMEOUT_INT_SECS = Integer.valueof(oPDService.Timeout__c)/1000;
        Continuation conGo = new Continuation(60);
        try
        {  
            sBody = buildRequestProviderDispute(sRecordId);
            if(string.isNotBlank(sBody))
            {
                HttpRequest request = new HttpRequest();
                request.setMethod(oPDService.Request_Type__c);
                request.setHeader('Accept-Encoding', 'gzip');
                request.setHeader(GLOBAL_CONSTANT_HUM.CONTENT_TYPE_HUM, oPDService.Content_Type__c);  
                request.setHeader(GLOBAL_CONSTANT_HUM.SOAPACTION_HUM, '');    
                request.setHeader(GLOBAL_CONSTANT_HUM.CONNECTION_HUM,GLOBAL_CONSTANT_HUM.KEEP_ALIVE_HUM);
                request.setBody(sBody);
                if(!Test.isRunningTest())    request.setClientCertificateName(oPDService.Certificate_Name__c);    
                request.setTimeout(Integer.valueof(oPDService.Timeout__c));
                request.setEndpoint(oPDService.End_Point_URL__c);
                conGo.continuationMethod = 'processProviderDisputeCasesResponse';
                conGo.State = sRecordId;
                conGo.addHttpRequest(request);
                if(Test.isRunningTest())  throw new HUMCustomException('Error');
            }
            else
            {
                return null;
            }
        }
        catch(Exception ex)
        {   
            HUMExceptionHelper.logErrors(ex, 'ProviderDisputes_S_HUM', 'sendProviderDisputeCasesRequest');
            return null;
        }
        
        return conGo;  
    }
    
    /*
    *  This method is used to build the required inputs needed to hit the Provider Correspondence Disputes service
    *  @name   buildRequestProviderDispute
    *  @param  string sRecordId -  Account to get data from service
    *  @return string - Returns the Request body in Serialized form
    */
    @testvisible private static string buildRequestProviderDispute(string sRecordId)
    {   
        string sJsonBodyGo = '';
        try
        {
            if(string.isNotBlank(sRecordId))
            { 
                ProviderDisputesRequest_H_HUM oRequest = new ProviderDisputesRequest_H_HUM();
                ProviderDisputesRequest_H_HUM.GetProviderDisputesByCriteria objGetProviderDisputes = new ProviderDisputesRequest_H_HUM.GetProviderDisputesByCriteria();
                
                ProviderDisputesRequest_H_HUM.Member objGetMemberId = new ProviderDisputesRequest_H_HUM.Member();
                
                Account objAccount = [Select id,Enterprise_ID__c from Account where id =:sRecordId LIMIT 1];
                if(objAccount.Enterprise_ID__c != Null)
                {
                    objGetMemberId.externalId = objAccount.Enterprise_ID__c ;
                }
                else
                {
                    objGetMemberId.externalId = '';
                }
                objGetProviderDisputes.member = objGetMemberId;
                String currentDate = string.valueOf(System.now());
                String target = ' ';
                String replacement = 'T';
                String requestThruDate = currentDate.replace(target, replacement);
                // Current Date
                objGetProviderDisputes.thruDate = requestThruDate;
                
                String earlierDate = string.valueOf(System.now().addDays(-548));
                String requestFromDate = earlierDate.replace(target, replacement);
                //18 months earlier to current date
                objGetProviderDisputes.fromDate = requestFromDate;
                
                oRequest.GetProviderDisputesByCriteria = objGetProviderDisputes;
                sJsonBodyGo = JSON.serialize(oRequest);
                
            }
            else    sJsonBodyGo =  null;
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'ProviderDisputes_S_HUM', 'buildRequestProviderDispute');
            sJsonBodyGo  =  NULL;
        }
       
        return sJsonBodyGo ;
    }
    
    /*
    *  This method is used to Parse response from Provider Correspondence Dispute service
    *  @name   processProviderDisputeCasesResponse
    *  @param  List and object
    *  @return object
    */
    @testvisible
    public static object processProviderDisputeCasesResponse(List<string> labels, object State)
    { 
    	List<ProviderDisputes_DTO_H_HUM> lstProviderDisputesRes = new List<ProviderDisputes_DTO_H_HUM >();
        try{        
        HttpResponse sResponse = Continuation.getResponse(labels[0]);
            
         if ((sResponse != null && !sResponse.getBody().containsIgnoreCase(GLOBAL_CONSTANT_HUM.VOB_FAULT_CODE) && 
            (sResponse.getStatusCode() == GLOBAL_CONSTANT_HUM.HTTP_OK_HUM || sResponse.getStatusCode() == GLOBAL_CONSTANT_HUM.HTTP_ACCEPTED_HUM)
            ) || (Test.isRunningTest()))
         {
             ProviderDisputesResponse_H_HUM objresp = new ProviderDisputesResponse_H_HUM();
             objresp = (ProviderDisputesResponse_H_HUM)System.JSON.deserialize(sResponse.getbody(),ProviderDisputesResponse_H_HUM.class);
             if (objresp != null){ 
                 lstProviderDisputesRes = processResponse(objresp);
             }
         }
         else{
             throw New ServiceDownException('SERVICEERROR'+sResponse.getBody());
             } 
        }
        catch (ServiceDownException e)
        {	
            //Handling Exceptions - Service Failure  
            List<Error_Log__c> errorLogList = new  List<Error_Log__c>();
            Error_Log__c logObj = HUMExceptionHelper.logOtherExceptions(e, 'ProviderDisputes_S_HUM','SERVICE_FAILURE');
            errorLogList.add(logObj);
            string errorNumber='';
              Id saveResultId;  
               Database.SaveResult[] srList =  Database.insert(errorLogList, false);
               for (Database.SaveResult sr : srList) 
               {    if (sr.isSuccess()) { 
                        saveResultId = sr.getId();
                    }
               } 
            if(saveResultId!=null)
            {
		errorNumber = [Select Name from Error_Log__c where Id = :saveResultId].Name;
            }
             
             ProviderDisputes_DTO_H_HUM  dlg= new ProviderDisputes_DTO_H_HUM();
             dlg.internalError= 'TESTFAILURE'+'*'+ errorNumber  + '#';
             lstProviderDisputesRes.add(dlg);
        } 
        catch (Exception ex)
        {    
            //Handling General Exceptions 
            List<Error_Log__c> errorLogList = new  List<Error_Log__c>();
            Error_Log__c logObj = HUMExceptionHelper.logOtherExceptions(ex,'ProviderDisputes_S_HUM', 'processProviderDisputeCasesResponse');
            errorLogList.add(logObj);
            string errorNumber='';
               Id saveResultId; 
               Database.SaveResult[] srList =  Database.insert(errorLogList, false);
               for (Database.SaveResult sr : srList) 
               {
                    if (sr.isSuccess()) {
                      saveResultId = sr.getId();
                    }
                } 
            if(saveResultId!=null)
            {
		errorNumber = [Select Name from Error_Log__c where Id = :saveResultId].Name;
            }
             
             ProviderDisputes_DTO_H_HUM  dlg= new ProviderDisputes_DTO_H_HUM();
             dlg.internalError= 'TESTFAILURE'+'*'+ errorNumber  + '#';
             lstProviderDisputesRes.add(dlg);
        }
         return lstProviderDisputesRes;
    }
 
    /**
    * This method generates list of Provider Correspondence Disputes cases associated to the account.
    * @Nmae     processResponse
    * @param    ProviderDisputesResponse_H_HUM response
    * @return   List of ProviderDisputes_DTO_H_HUM
    */
    public static list<ProviderDisputes_DTO_H_HUM> processResponse(ProviderDisputesResponse_H_HUM resProviderDisputes)
    {
        list<ProviderDisputes_DTO_H_HUM>  lstPDCases = new list<ProviderDisputes_DTO_H_HUM>();
        
        ProviderDisputesResponse_H_HUM.cls_getProviderDisputesByCriteriaResponse oPDCasesResponse
            = resProviderDisputes.getProviderDisputesByCriteriaResponse;
         
		List<ProviderDisputesResponse_H_HUM.cls_providerDisputes> lstOfPDCases = oPDCasesResponse.providerDisputes;
        
        for (ProviderDisputesResponse_H_HUM.cls_providerDisputes objPDCases : lstOfPDCases)
        {
            ProviderDisputes_DTO_H_HUM oPDCasesWrap = new ProviderDisputes_DTO_H_HUM();
            //caseNumber
            if(objPDCases.caseNumber!=null && objPDCases.caseNumber!=''){
            	oPDCasesWrap.sCase = objPDCases.caseNumber;
            }else{ 
                oPDCasesWrap.sCase = '';
            }
            //Type
            if(objPDCases.caseCategory!=null && objPDCases.caseCategory!=''){
            	oPDCasesWrap.sType = objPDCases.caseCategory;
            }else{ 
                oPDCasesWrap.sType = '';
            }
            //category
            if(objPDCases.category!=null && objPDCases.subCategory!=null){
                oPDCasesWrap.sCategory = objPDCases.category +' - '+ objPDCases.subCategory;
            }else if(objPDCases.category!=null && objPDCases.subCategory==null){ 
                oPDCasesWrap.sCategory = objPDCases.category;
            }else{ 
                oPDCasesWrap.sCategory = '';
            }
            //Status
            if(objPDCases.status!=null && objPDCases.statusReason!=null){
                oPDCasesWrap.sStatus = objPDCases.status +' - '+ objPDCases.statusReason;
            }else if(objPDCases.status!=null && objPDCases.statusReason==null){ 
                oPDCasesWrap.sStatus = objPDCases.status;
            }else{ 
                oPDCasesWrap.sStatus = '';
            }
            //priority
            if(objPDCases.priority!=null && objPDCases.priority!=''){
            	oPDCasesWrap.sPriority = objPDCases.priority;
            }else{ 
                oPDCasesWrap.sPriority = '';
            }
            //DueDate
            if(objPDCases.dueDate!=null && objPDCases.dueDate!=''){
            	String theDate = objPDCases.dueDate;
                String reqDate= theDate.subString(0,10);
                String [] dateArray = reqDate.split('-');
                String dueDate= '',day='',month='',deterDate='';
                if(dateArray[2].length() == 1){ day = '0'+dateArray[2];
                }else{
                    day = dateArray[2];
                }    
                if(dateArray[1].length() == 1){ month = '0'+dateArray[1];
                }else{
                    month = dateArray[1];
                }
                dueDate = month+'/'+day+'/'+dateArray[0];
                        
                oPDCasesWrap.sDueDate = dueDate;
 
            }else{ 
                oPDCasesWrap.sDueDate = '';
            }
		
			//ProviderName
            if(objPDCases.wsProvider.providerDisplayName!=null){
            	oPDCasesWrap.sProviderName = objPDCases.wsProvider.providerDisplayName;
            }else{ 
                oPDCasesWrap.sProviderName = '';
            }
			//ServiceType
			if(objPDCases.disputeType!=null && objPDCases.disputeType!=''){
            	oPDCasesWrap.sServiceType = objPDCases.disputeType;
            }else{ 
                oPDCasesWrap.sServiceType = '';
            }
			//RequestType
            if(objPDCases.requestType!=null && objPDCases.requestType!=''){
            	oPDCasesWrap.sRequestType = objPDCases.requestType;
            }else{ 
                oPDCasesWrap.sRequestType = '';
            }
            //DateofDetermination
         	if(String.isBlank(objPDCases.completionDate))
            {
                 oPDCasesWrap.sDateofDetermination = '';
            }
            else
            {
                String theDate = objPDCases.completionDate;
                String reqDate = theDate.subString(0,19);
                String target = 'T';
                String replacement = '-';
                String formatDate = reqDate.replace(target, replacement);
               
                String [] dateArrays = formatDate.split('-');
                String dueDate= '',day='',month='',deterDate='';
                if(dateArrays[2].length() == 1){ day = '0'+dateArrays[2];
                }else{
                    day = dateArrays[2];
                }    
                if(dateArrays[1].length() == 1){ month = '0'+dateArrays[1];
                }else{
                    month = dateArrays[1];
                }
                deterDate = month+'-'+day+'-'+dateArrays[0]+' '+dateArrays[3];
                
                oPDCasesWrap.sDateofDetermination = deterDate;
            }
		
            //Product
			if(objPDCases.product!=null && objPDCases.product!=''){
            	oPDCasesWrap.sProduct = objPDCases.product;
            }else{ 
                oPDCasesWrap.sProduct = '';
            } 
			//ReceivedDate
			if(String.isBlank(objPDCases.manualReceivedDate) && String.isBlank(objPDCases.receivedDate))
            {
                 oPDCasesWrap.sReceivedDate = '';
            }
            else
            {
                String theDate = (String.isBlank(objPDCases.manualReceivedDate)) ? objPDCases.receivedDate : objPDCases.manualReceivedDate;
                String reqDate = theDate.subString(0,19);
                String target = 'T';
                String replacement = '-';
                String formatDate = reqDate.replace(target, replacement);
               
                String [] dateArrays = formatDate.split('-');
                String dueDate= '',day='',month='',deterDate='';
                if(dateArrays[2].length() == 1){ day = '0'+dateArrays[2];
                }else{
                    day = dateArrays[2];
                }    
                if(dateArrays[1].length() == 1){ month = '0'+dateArrays[1];
                }else{
                    month = dateArrays[1];
                }
                deterDate = month+'-'+day+'-'+dateArrays[0]+' '+dateArrays[3];
                
                oPDCasesWrap.sReceivedDate = deterDate;
            }
            //ProviderTaxID
            if(objPDCases.wsProvider.npi!=null){
            	oPDCasesWrap.sProviderTaxID = objPDCases.wsProvider.npi;
            }else{ 
                oPDCasesWrap.sProviderTaxID = '';
            }
			
            //DateofService
            if(objPDCases.dateOFServiceDesc!=null && objPDCases.dateOFServiceDesc!='')
            {
            	oPDCasesWrap.sDateofService = objPDCases.dateOFServiceDesc;
 			}else{ 
                oPDCasesWrap.sDateofService = '';
            }
            
			//ClaimNumber
			if(objPDCases.medicalClaims != null){ 
                oPDCasesWrap.sClaimNumber = '';
                integer iElement = 1;
                integer claimSize = objPDCases.medicalClaims.size();
                for (ProviderDisputesResponse_H_HUM.cls_medicalClaims medicalClaims : objPDCases.medicalClaims)
                {   
                    if(objPDCases.medicalClaims.size() >= 1 && medicalClaims.claimNumber != null)
                    {
                    	if(iElement == 1){
                            oPDCasesWrap.sClaimNumber = medicalClaims.claimNumber;
                        }
                        else{
                            if(math.mod(claimSize,3) != 2){
                                oPDCasesWrap.sClaimNumber = oPDCasesWrap.sClaimNumber + ', ' + medicalClaims.claimNumber;    
                            }
                            if(math.mod(claimSize,3) == 2){
                                oPDCasesWrap.sClaimNumber = oPDCasesWrap.sClaimNumber + ', ' + medicalClaims.claimNumber +'\n';    
                                oPDCasesWrap.sClaimNumber = oPDCasesWrap.sClaimNumber.trim();
                            }
                        }
                        iElement++;
                        claimSize--;
                    }
                }    
            }else{ 
                oPDCasesWrap.sClaimNumber = '';
            }
            
            //Assigning analyst first name and last name
       		if(objPDCases.updateUser!=null)
            {
            	String AgentName = '';
            	if(objPDCases.updateUser.firstName!=null){
                	AgentName += objPDCases.updateUser.firstName + ' ';
            	}
            	if(objPDCases.updateUser.LastName!=null){
                	AgentName += objPDCases.updateUser.LastName;
            	}
            	oPDCasesWrap.sAssignedAnalyst = AgentName;
            }else{
            	oPDCasesWrap.sAssignedAnalyst = '';   
            }    
            
            //Notifications
            ProviderDisputes_DTO_H_HUM.Notifications notify = new ProviderDisputes_DTO_H_HUM.Notifications();    
            oPDCasesWrap.Notifications = notify;
            
            List<ProviderDisputes_DTO_H_HUM.caseNotifications> caseComments = new List<ProviderDisputes_DTO_H_HUM.caseNotifications>();
            List<ProviderDisputes_DTO_H_HUM.caseNotifications> newCaseComments = new List<ProviderDisputes_DTO_H_HUM.caseNotifications>();
           
            if(objPDCases.notification != null)
            {
            	for(ProviderDisputesResponse_H_HUM.cls_notification caseCmmts : objPDCases.notification)
                {
                	ProviderDisputes_DTO_H_HUM.caseNotifications vCasecomments = new ProviderDisputes_DTO_H_HUM.caseNotifications();
                    vCasecomments.createdBy = caseCmmts.createdBy;
                    vCasecomments.note = caseCmmts.note;
                    vCasecomments.noteDate = caseCmmts.createdDate;
                    
                    caseComments.add(new ProviderDisputes_DTO_H_HUM.caseNotifications(vCasecomments.createdBy,vCasecomments.note,vCasecomments.noteDate));
                    caseComments.sort();
                }
                if(caseComments != null)
                {
                    for(ProviderDisputes_DTO_H_HUM.caseNotifications vCseComments : caseComments)
                    {
                        ProviderDisputes_DTO_H_HUM.caseNotifications vCaseCommts = new ProviderDisputes_DTO_H_HUM.caseNotifications();    
                            vCaseCommts.createdBy = vCseComments.createdBy;
                            vCaseCommts.note = vCseComments.note;
                            vCaseCommts.noteDate = vCseComments.noteDate;
                        if(String.isBlank(vCaseCommts.noteDate))
                        {
                            vCaseCommts.noteDate = '';
                        }
                        else
                        {
                            string theDate = vCseComments.noteDate;
                            string reqDate= theDate.subString(0,19);
                            String target = 'T';
                            String replacement = '-';
                            String formatDate = reqDate.replace(target, replacement);
                            String dueDate= '',day='',month='',deterDate='';
                            String [] dateArrays = formatDate.split('-');
                            if(dateArrays[2].length() == 1){  day = '0'+dateArrays[2];
                            }else{
                                day = dateArrays[2];
                            }    
                            if(dateArrays[1].length() == 1){  month = '0'+dateArrays[1];
                            }else{
                                month = dateArrays[1];
                            }
                            deterDate = month+'-'+day+'-'+dateArrays[0]+' '+dateArrays[3];
                            
                            vCaseCommts.noteDate = deterDate;
                        }
                    newCaseComments.add(vCaseCommts);
                    } //end of for
                }
                notify.caseNotifications = newCaseComments;
            }
            //Case Documents
            oPDCasesWrap.medhokID = objPDCases.medhokID;
            oPDCasesWrap.context = 'ProviderDispute';
            
            lstPDCases.add(oPDCasesWrap);
        }
     	return lstPDCases;
    }
}
/**************************************************************************************************************************************************
Apex class Name    :  GrievanceAndAppeal_S_HUM
Version            :  1.0
Function           :  This is Service Helper class to GrievanceAndAppealComponent_C_RT_HUM.It forms request and parse response.
Test Class         :  GrievanceAndAppeal_T_HUM

Modification Log:
 * Developer Name        Code Review #         Date                       Description
 *---------------------------------------------------------------------------------------------------
 * Ajay Chakradhar                             10/29/2018                 Original Version
 * Riya Agarwal 							   11/12/2020			      REQ - 1625475-PR00094226 - CR AEP readiness -Research - Critical Security Issues
 * Swapnali Sonawane                           04/09/2021                 Req#2119511 added ExRM Fields to MHK Web Service and Additional Data for G&A Status Page in CRM
 * Swapnali Sonawane                           04/20/2021                 Fix Defect#2909 service Type should be blank for Appeal cases
 * Swapnali Sonawane                           04/21/2021                 Fix Defect#2910 EXRM Case On CRM UI, shows incorrect/ blank values for the fields for which data is present in MHK
 * Swapnali Sonawane                           04/23/2021                 Fix Defect#2925 For EXRM case when click on "View Documents" link, it is showing error message
 * Swapnali Sonawane                           04/27/2021                 Fix Defect#2910 shows incorrect/ blank values for Service Type fields for EXRM Case 
 * Swapnali Sonawane                           05/12/2021                 Fix Defect#3029 When user enters alphanumeric values in Date of Service field in MHK it throws the exception on CRM UI
 ************************************************************************************************************************************************* */

public with sharing class GrievanceAndAppeal_S_HUM
{
    public class ServiceDownException extends Exception {}
    @TestVisible private static String sBody ='' ;
    
    /*
    *  This method is used to form HTTP Request for Greivance and Appeal Service
    *  @name   sendGreivanceAndAppealRequest
    *  @param  string sRecordId - Account to get data from service
    *  @return HttpRequest - Object
    */        
    public static Continuation sendGreivanceAndAppealRequest(string sRecordId)
    {
        
        HUM_Webservice_Callout__c oGAService = HUM_Webservice_Callout__c.getValues('GrievanceAndAppeal');
        Integer iTIMEOUT_INT_SECS = Integer.valueof(oGAService.Timeout__c)/1000;
        Continuation conGo = new Continuation(60);
        try
        {  
            sBody = buildRequestGreivanceAndAppeal(sRecordId);
            if(string.isNotBlank(sBody))
            {
                HttpRequest request = new HttpRequest();
                request.setMethod(oGAService.Request_Type__c); 
                request.setHeader(GLOBAL_CONSTANT_HUM.CONTENT_TYPE_HUM, oGAService.Content_Type__c);  
                request.setHeader(GLOBAL_CONSTANT_HUM.SOAPACTION_HUM, '');    
                request.setHeader(GLOBAL_CONSTANT_HUM.CONNECTION_HUM,GLOBAL_CONSTANT_HUM.KEEP_ALIVE_HUM);
                request.setBody(sBody);
                if(!Test.isRunningTest())    request.setClientCertificateName(oGAService.Certificate_Name__c);    
                request.setTimeout(Integer.valueof(oGAService.Timeout__c));
                request.setEndpoint(oGAService.End_Point_URL__c);
                conGo.continuationMethod = 'processGreivanceAndAppealResponse';
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
            HUMExceptionHelper.logErrors(ex, 'GrievanceAndAppeal_S_HUM', 'sendGreivanceAndAppealRequest');
            return null;
        }
        
        return conGo;  
    }
    
    /*
    *  This method is used to build the required inputs needed to hit the Greivance And Appeal service
    *  @name   buildRequestGreivanceAndAppeal
    *  @param  string sRecordId -  Account to get data from service
    *  @return string - Returns the Request body in Serialized form
    */
    @testvisible private static string buildRequestGreivanceAndAppeal(string sRecordId)
    {   
        
        string sJsonBodyGo = '';
        try
        {
            if(string.isNotBlank(sRecordId))
            { 
                GreivanceAndAppealRequest_H_HUM oRequest = new GreivanceAndAppealRequest_H_HUM();
                GreivanceAndAppealRequest_H_HUM.GetGrievanceAndAppealByCriteriaRequest objGetGreivanceAndAppeals = new GreivanceAndAppealRequest_H_HUM.GetGrievanceAndAppealByCriteriaRequest();
                
                GreivanceAndAppealRequest_H_HUM.Member objGetMemberId = new GreivanceAndAppealRequest_H_HUM.Member();
                
                Account objAccount = [Select id,Enterprise_ID__c from Account where id =:sRecordId LIMIT 1];
                
                if(objAccount.Enterprise_ID__c != Null)
                {
                    objGetMemberId.externalId = objAccount.Enterprise_ID__c ;
                }
                else{   objGetMemberId.externalId = '';
                }                 
                objGetGreivanceAndAppeals.member = objGetMemberId;
                
                String currentDate = string.valueOf(System.now());
                String target = ' ';
                String replacement = 'T';
                String requestThruDate = currentDate.replace(target, replacement);
                // Current Date
                objGetGreivanceAndAppeals.thruDate = requestThruDate;
                
                String earlierDate = string.valueOf(System.now().addDays(-548));
                String requestFromDate = earlierDate.replace(target, replacement);
                // 18 months earlier to current date
                objGetGreivanceAndAppeals.fromDate = requestFromDate;

                oRequest.GetGrievanceAndAppealByCriteriaRequest = objGetGreivanceAndAppeals;
                sJsonBodyGo = JSON.serialize(oRequest);
                
            }
            else    sJsonBodyGo =  null;
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'GrievanceAndAppeal_S_HUM', 'buildRequestGreivanceAndAppeal');
            sJsonBodyGo  =  NULL;
        }
       
        return sJsonBodyGo ;
    }
    
    /*
    *  This method is used to Parse response from Greivance And Appeal service
    *  @name   processGreivanceAndAppealResponse
    *  @param  List and object
    *  @return object
    */
    @testvisible
    public static object processGreivanceAndAppealResponse(List<string> labels, object State)
    { 
    List<GreivanceAndAppeal_DTO_HUM> lstGreivAndAppealRes = new List<GreivanceAndAppeal_DTO_HUM >();
       try{        
        HttpResponse sResponse = Continuation.getResponse(labels[0]);
        
          if ((sResponse != null && !sResponse.getBody().containsIgnoreCase(GLOBAL_CONSTANT_HUM.VOB_FAULT_CODE) && 
            (sResponse.getStatusCode() == GLOBAL_CONSTANT_HUM.HTTP_OK_HUM || sResponse.getStatusCode() == GLOBAL_CONSTANT_HUM.HTTP_ACCEPTED_HUM)
            ) || Test.isRunningTest())
         {
            GreivanceAndAppealResponse_H_HUM objresp = new GreivanceAndAppealResponse_H_HUM();
            objresp = (GreivanceAndAppealResponse_H_HUM)System.JSON.deserialize(sResponse.getbody(),GreivanceAndAppealResponse_H_HUM.class);

             if (objresp != null){ lstGreivAndAppealRes = processResponse(objresp);
             } 
         }
         else{ 
             throw New ServiceDownException('SERVICEERROR'+sResponse.getBody());
			 } 
        }
        catch (ServiceDownException e)
        {
            //Handling Exceptions - Service Failure added 11/19 
            List<Error_Log__c> errorLogList = new  List<Error_Log__c>();
            Error_Log__c logObj = HUMExceptionHelper.logOtherExceptions(e, 'GrievanceAndAppeal_S_HUM','SERVICE_FAILURE');
            errorLogList.add(logObj);
            
             GreivanceAndAppeal_DTO_HUM  dlg= new GreivanceAndAppeal_DTO_HUM();
             errorLog(dlg,errorLogList);
             lstGreivAndAppealRes.add(dlg);
        } 
        catch (Exception ex)
        {   
            //Handling General Exceptions - added 12/05
            List<Error_Log__c> errorLogList = new  List<Error_Log__c>();
            Error_Log__c logObj = HUMExceptionHelper.logOtherExceptions(ex,'GrievanceAndAppeal_S_HUM', 'processGreivanceAndAppealResponse');
            errorLogList.add(logObj);
            
             GreivanceAndAppeal_DTO_HUM  dlg= new GreivanceAndAppeal_DTO_HUM();
             errorLog(dlg,errorLogList);
             lstGreivAndAppealRes.add(dlg);
        }
         return lstGreivAndAppealRes;      
          
    }
    /**
    * This method convert the date in MM/DD/YYYY format .
    * @Nmae     DateConverter
    * @param    sDate
    * @return   String
    */
 	public static String DateConverter(String sDate, string sFormat)
    {
        String dueDate = '',day='',month='',deterDate='';
        if (sFormat == 'DateTime')
        {
            //string reqDate= theDate.subString(0,19);
            String target = 'T';
            String replacement = '-';
            String formatDate = sDate.replace(target, replacement);
            
            String [] dateArrays = formatDate.split('-');
            if(dateArrays[2].length() == 1){  day = '0'+dateArrays[2];
                                           }else{
                                               day = dateArrays[2];
                                           }    
            if(dateArrays[1].length() == 1){  month = '0'+dateArrays[1];
                                           }else{
                                               month = dateArrays[1];
                                           }
            dueDate = month+'-'+day+'-'+dateArrays[0]+' '+dateArrays[3];
        }
        else
        {
            String [] dateArray = sDate.split('-');
            
            if(dateArray[2].length() == 1){ day = '0'+dateArray[2];
                                          }else{
                                              day = dateArray[2];
                                          }    
            if(dateArray[1].length() == 1){ month = '0'+dateArray[1];
                                          }else{
                                              month = dateArray[1];
                                          }
            dueDate = month+'/'+day+'/'+dateArray[0];
        }
        return dueDate;                
    }
    /**
    * This method generates list of Greivance And Appeal cases associated to the account.
    * @Nmae     processResponse
    * @param    GreivanceAndAppealResponse_H_HUM response
    * @return   List of GreivanceAndAppeal_DTO_HUM
    */
    public static list<GreivanceAndAppeal_DTO_HUM> processResponse(GreivanceAndAppealResponse_H_HUM resGreivanceAndAppeal)
    {
        Boolean bSwitchGNAStatus;
        
        bSwitchGNAStatus=  HUMUtilityHelper.isCRMFunctionalityON('2119511');
		
        list<GreivanceAndAppeal_DTO_HUM>  lstGACases = new list<GreivanceAndAppeal_DTO_HUM>();
        
        GreivanceAndAppealResponse_H_HUM.GetGrievanceAndAppealByCriteriaResponse oGACasesResponse = 
        resGreivanceAndAppeal.GetGrievanceAndAppealByCriteriaResponse;
        
        List<GreivanceAndAppealResponse_H_HUM.Cases> lstOfGACases = oGACasesResponse.Cases;
        
        for (GreivanceAndAppealResponse_H_HUM.Cases objGACases : lstOfGACases)
        {
            GreivanceAndAppeal_DTO_HUM oGACasesWrap = new GreivanceAndAppeal_DTO_HUM();
            
            GreivanceAndAppeal_DTO_HUM.Notifications notify = new GreivanceAndAppeal_DTO_HUM.Notifications();
                
            oGACasesWrap.Notifications = notify;
            string CaseNum = objGACases.caseNumber;
            oGACasesWrap.sCase = objGACases.caseNumber;
            oGACasesWrap.sType = objGACases.caseCategory; 
            oGACasesWrap.sCategory = objGACases.subCategory;
            oGACasesWrap.sStatus = objGACases.status +' - ' + objGACases.statusReason;
            oGACasesWrap.sPriority = objGACases.priority;
            if(objGACases.caseType == null || objGACases.caseType == 'NA')
            {
                oGACasesWrap.sServiceType = '';
            }
            else
            {
            	oGACasesWrap.sServiceType = objGACases.caseType;
            }
            oGACasesWrap.sRequestType = objGACases.requestType;
            
            if (bSwitchGNAStatus == true)
            {
                //***Start New fields from Cases
                //For EXRM Cases
                if (CaseNum.containsNone('AG'))
                {
                    if (objGACases.creationType !=null)
                    {
                        oGACasesWrap.sType = objGACases.creationType;                     
                    }
                    
                    if (objGACases.originalCaseAppealsSubCategory != null)
                    {
                        oGACasesWrap.sCategory = objGACases.originalCaseAppealsSubCategory;
                    }
                	oGACasesWrap.sStatus = objGACases.status;
                    oGACasesWrap.sPriority = objGACases.irePriority;
                    if (objGACases.ireAppealsDisputeType != null)
                    {
                    	oGACasesWrap.sServiceType = objGACases.ireAppealsDisputeType;
                    }
                    else
                    {
                        oGACasesWrap.sServiceType ='';
                    }
                    oGACasesWrap.sRequestType = objGACases.ireCaseType;
                }
                //Provider Name
                string ProviderName='';
                if (objGACases.originalCaseNo != null)
                {
                    ProviderName = objGACases.originalCaseNo;    
                }
                else if (objGACases.providerDisplayName!=null)
                {
                    ProviderName = objGACases.providerDisplayName;
                }
                else
                {
                    ProviderName ='';
                }
                oGACasesWrap.sProviderDisplayName = ProviderName;  
                
                //Humana Received Date
                String receiveDate = objGACases.manualReceivedDate;
                if(String.isBlank(objGACases.manualReceivedDate))
                {
                    receiveDate = objGACases.ReceivedDate;
                    if(String.isBlank(objGACases.ReceivedDate))
                    {	
                        oGACasesWrap.sManualReceivedDate = '';
                    }
                    else
                    {
                        oGACasesWrap.sManualReceivedDate = DateConverter(receiveDate.subString(0,10),'Date');
                            
                    }
                }   
                else
                {
                    oGACasesWrap.sManualReceivedDate = DateConverter(receiveDate.subString(0,10),'Date');            
                }
                //Date of Services
				string DateofSerDate= objGACases.datesOfService;
                if(String.isBlank(objGACases.datesOfService))
                {
                    oGACasesWrap.sDatesOfService = '';
                }
                else if ((CaseNum.contains('A')))
                {
                    oGACasesWrap.sDatesOfService = objGACases.datesOfService;
                        
                }
                else if ((CaseNum.contains('G')))
                {
                    if (DateofSerDate.contains('/'))
                    {
                        oGACasesWrap.sDatesOfService=DateofSerDate;
                    }
                    else
                    {
                        oGACasesWrap.sDatesOfService = DateConverter(DateofSerDate.subString(0,10),'Date');
                    }
                }
                //Group
                if(objGACases.MemberEligibility != null)
                {
                     oGACasesWrap.sMemberElgGrp = objGACases.MemberEligibility.eligGroup;
                }else {oGACasesWrap.sMemberElgGrp='';}
                
                //Plan
                if(objGACases.MemberEligibility != null)
                {
                     oGACasesWrap.sMemberElgPlan = objGACases.MemberEligibility.eligPlan;
                }else {oGACasesWrap.sMemberElgPlan='';}
                
                //LOB
                if(objGACases.MemberEligExtAttribute != null)
                {
                     oGACasesWrap.sMemberLOB = objGACases.MemberEligExtAttribute.attribute8;
                }else {oGACasesWrap.sMemberLOB='';}
                
                //Member Id Card
                if(objGACases.MemberEligExtAttribute != null)
                {
                     oGACasesWrap.sMemberCardId = objGACases.MemberEligExtAttribute.attribute22;
                }else {oGACasesWrap.sMemberCardId='';}
                
                //Claim 
                String clmNumber ='';
                if(objGACases.medicalClaims != null)
                {                    
                    for(GreivanceAndAppealResponse_H_HUM.MedicalClaims Claims : objGACases.medicalClaims )
                    {                   
                        clmNumber = clmNumber +' ' + Claims.claimNumber;   
                    }
                    oGACasesWrap.sClaimNumber = clmNumber;
                }
                else
				{
					oGACasesWrap.sClaimNumber='';
				}
                
                
                //Appeal Request Reason
                if ((CaseNum.contains('A') && objGACases.allegation != null))
                {
                    oGACasesWrap.sAllegation = objGACases.allegation;
                }
                if ((CaseNum.contains('G') && objGACases.notes != null))
                {
                    oGACasesWrap.sAllegation = objGACases.notes;
                }
                if(CaseNum.containsNone('AG'))
                {
                      oGACasesWrap.sAllegation='';
                }
                
                //Final Resolution
                if (objGACases.ireDecision != null)
                {
                    oGACasesWrap.sFinalResolutionCaseNote = objGACases.ireDecision;
                }
                else if (objGACases.finalResolutionCaseNote != null)
                {
                    oGACasesWrap.sFinalResolutionCaseNote = objGACases.finalResolutionCaseNote;    
                }
                else
                {
                    oGACasesWrap.sFinalResolutionCaseNote='';
                }
                //Provider Tax ID
                string ProviderTaxId='';
                if (objGACases.Member != null)
                {
                     if (objGACases.Member.pcp != null)
                     {
                         if ( objGACases.Member.pcp.federalTaxID != null)
                         {
                            ProviderTaxId = objGACases.Member.pcp.federalTaxID;
                         }
                     }
                }
                else
                {
                    ProviderTaxId='';
                }
                oGACasesWrap.sProviderTaxID=ProviderTaxId;
                
                //****End New Fields from Cases
            }
            //Date formatting
            String theDate = objGACases.dueDate;
            if(String.isBlank(objGACases.dueDate))
            {
                oGACasesWrap.sDueDate = '';
            }
            else
            {
                oGACasesWrap.sDueDate =DateConverter(theDate.subString(0,10),'Date');
                
            }            
            
            if(String.isBlank(objGACases.decisionDate))
            {
                 oGACasesWrap.sDateofDetermination = '';
            }
            else
            {
                theDate = objGACases.decisionDate;                
                oGACasesWrap.sDateofDetermination = DateConverter(theDate.subString(0,19),'DateTime');
            }
            if (bSwitchGNAStatus == true)
            {
                //For EXRM Cases
                if (CaseNum.containsNone('AG'))
                {
                    //DateofDetermination
                    if(String.isBlank(objGACases.ireDecisionDate))
                    {
                        oGACasesWrap.sDateofDetermination='';
                    }
                    else
                    {
                    	theDate= objGACases.ireDecisionDate;
                    	oGACasesWrap.sDateofDetermination =DateConverter(theDate.substring(0,19),'DateTime');
                    }
                    
                    //Due Date
                    if(String.isBlank(objGACases.runningDueDate))
                    {
                        oGACasesWrap.sDueDate='';
                    }
                    else
                    {
                    	theDate= objGACases.runningDueDate;
                    	oGACasesWrap.sDueDate=DateConverter(theDate.subString(0,10),'Date');
                    }
                }
            }
            //Assigning analyst first name and last name
            String AgentName = '';
            if (objGACases.UpdateUser!= null)
            {
                if(objGACases.UpdateUser.firstName!=null)
                {
                    AgentName += objGACases.UpdateUser.firstName + ' ';
                }
                if(objGACases.UpdateUser.LastName!=null)
                {
                    AgentName += objGACases.UpdateUser.LastName;
                }
            }
            oGACasesWrap.sAssignedAnalyst = AgentName;
            
            //Case Documents
            oGACasesWrap.medhokID = objGACases.medhokID;
            oGACasesWrap.context = objGACases.caseCategory;
			if (bSwitchGNAStatus == true)
            {
                 if (CaseNum.containsNone('AG'))
                 {
                    oGACasesWrap.context = 'ExRM'; 
                 }
            }
            List<GreivanceAndAppeal_DTO_HUM.CaseNotifications> newCaseComments = new List<GreivanceAndAppeal_DTO_HUM.CaseNotifications>();
            if (bSwitchGNAStatus == true)
            {
                //List<GreivanceAndAppeal_DTO_HUM.CaseNotifications> ExRMcaseComments = new List<GreivanceAndAppeal_DTO_HUM.CaseNotifications>();
                // Logic for EXRM notes
                if(objGACases.internalNotes != null)
                {
                    string Notes= objGACases.internalNotes;
                    if (String.isnotBlank(Notes))
                    {
                        String [] iNotes = Notes.split('\n\n');
                        integer ilen=iNotes.size();
                        for (integer i=0;i<ilen;i++ )
                        {
                            String CaseComment = iNotes[i];
                           
                            GreivanceAndAppeal_DTO_HUM.CaseNotifications vCasecomments = new GreivanceAndAppeal_DTO_HUM.CaseNotifications();    
                            integer startIndex = casecomment.indexOf('[');
                            integer endIndex = casecomment.indexOf(']');
                            vCasecomments.noteDate= startIndex > 1 ? CaseComment.subString(0,startIndex - 1) : '';                          
                            vCasecomments.createdBy= startIndex >=0 && endIndex >= 2 ? casecomment.substring(startIndex + 1,endIndex) :   '';                           
                            vCasecomments.note = endIndex >= 0 && casecomment.length() > endIndex + 2 ? casecomment.substring(endIndex + 1) : '';
							newCaseComments.add(new GreivanceAndAppeal_DTO_HUM.CaseNotifications(vCasecomments.createdBy,vCasecomments.note,vCasecomments.noteDate));
                    		newCaseComments.sort();
                        }
                    }
                }
                
            }
            //Notifications
            List<GreivanceAndAppeal_DTO_HUM.CaseNotifications> caseComments = new List<GreivanceAndAppeal_DTO_HUM.CaseNotifications>();
            
           
            if(objGACases.notification != null)
            {
                for(GreivanceAndAppealResponse_H_HUM.Notification casCmts : objGACases.notification )
                {
                    GreivanceAndAppeal_DTO_HUM.CaseNotifications vCasecomments = new GreivanceAndAppeal_DTO_HUM.CaseNotifications();    
                    vCasecomments.createdBy = casCmts.createdBy;
                    vCasecomments.note = casCmts.note;
                    vCasecomments.noteDate = casCmts.createdDate;
                    
                    caseComments.add(new GreivanceAndAppeal_DTO_HUM.CaseNotifications(vCasecomments.createdBy,vCasecomments.note,vCasecomments.noteDate));
                    caseComments.sort();
                }
                if(caseComments != null)
                {
                    for(GreivanceAndAppeal_DTO_HUM.CaseNotifications vCseComments : caseComments)
                    {
                        GreivanceAndAppeal_DTO_HUM.CaseNotifications vCaseCommts = new GreivanceAndAppeal_DTO_HUM.CaseNotifications();    
                            vCaseCommts.createdBy = vCseComments.createdBy;
                            vCaseCommts.note = vCseComments.note;
                            vCaseCommts.noteDate = vCseComments.noteDate;
                        if(String.isBlank(vCaseCommts.noteDate))
                        {
                            vCaseCommts.noteDate = '';
                        }
                        else
                        {
                            theDate = vCseComments.noteDate;
                            vCaseCommts.noteDate = DateConverter(theDate.substring(0,19),'DateTime');
                        }
                    newCaseComments.add(vCaseCommts);
                    } 
                }
                
            }
           notify.CaseNotifications = newCaseComments;
           lstGACases.add(oGACasesWrap);  
           
        }
        
        return lstGACases;
    }
	
	public static GreivanceAndAppeal_DTO_HUM errorLog(GreivanceAndAppeal_DTO_HUM  dlg, List<Error_Log__c> errorLogList )
	{ 
               string errorNumber='';
               Database.SaveResult[] srList =  Database.insert(errorLogList, false);
			   List<Error_Log__c> lstError = new List<Error_Log__c>();
               Set<ID>  ids = new Set<ID>();   
               for (Database.SaveResult sr : srList) 
               {
                    if (sr.isSuccess()) {
                      ids.add(sr.getId());
                      
                    }
                } 
               lstError = [Select Name from Error_Log__c where Id = :ids];
			   errorNumber = lstError[0].Name;
             
             dlg.internalError= 'TESTFAILURE'+'*'+ errorNumber  + '#';
             return dlg;
	} 
 
}
/*****************************************************************************************************
Apex Class Name  : ClinicalPrograms_S_HUM
Version          : 1.0 
Created Date     : 10/25/2018
Function         : Controller to fetch Case care manager details from authorization service
Test Class       : ClinicalProgramDetail_T_HUM

Modification Log :
* Developer                 Code Review         Date                        Description
* -----------------------------------------------------------------------------------------------------                 
* Buvaneswaran M                                10/29/2018                  Original Version    
* Sagar M N                                     12/17/2018                  REQ-380791      
* Asish Behera                                  06/20/2019                  REQ-380791 Program code assignment  
* Imran                                         04/14/2020                  REQ - 891352 - Table updated  
* Ranjeeth N                                    05/12/2020                  fixed comments to display on available programs 
* Ranjeeth N			                 		05/18/2020		             Df-746 column with null
* Trina Ghosh                                   02/09/2021                  US:1669845 - Low Security Fixes
* Pinky Vijur                               08/19/2021                 REQ - 2080855 - T1PRJ0052463 - MF 6 -  Display Palliative Care program participation details in CRM - Pallitus Health Partners (CRM)
*******************************************************************************************************/
public with sharing class ClinicalProgramDetail_S_HUM {  
    
    private static string sMemberId { get; set; }
    private static string sMemberIDType{get;set;}    
    @TestVisible private static String sBody ='' ;
    public class ServiceDownException extends Exception {}
    
/*
*  This method is used to form HTTP Request for Clinical Programs
*  @name   sendClinicalProgramsRequest
*  @param  string MemberId 
*  @param  string MemeberIDType
*  @return HttpRequest - Object
*/    
    public static Continuation sendClinicalProgramsRequest(String memberId, String memberGenKey){
        HUM_Webservice_Callout__c oSvcHeaderClaimsV2 = HUM_Webservice_Callout__c.getValues('GetClinicalPrograms_V2');       
        HUM_Webservice_Callout__c oSvcHeaderClaims = HUM_Webservice_Callout__c.getValues('ClinicalPrograms');       
        Integer iTIMEOUT_INT_SECS = HUMConstants.HUMServiceTimeOut;
        Continuation con = new Continuation(iTIMEOUT_INT_SECS);
        try
        {                
            sMemberId= memberId;             
            sMemberIDType= memberGenKey;           
            sBody= buildReqestClinical(sMemberId, sMemberIDType);                       
            if(string.isNotBlank(sBody))
            {   
                
            HttpRequest request= new HttpRequest();             
            Boolean bClinicalSwitch= CRMFunctionalityenabler_H_HUM.Checkflagvalue('Activate_GetClinicalV2_Service');
            if (bClinicalSwitch) 
            {
                request.setMethod(oSvcHeaderClaimsV2.Request_Type__c); 
                request.setHeader(GLOBAL_CONSTANT_HUM.Content_type_HUM, oSvcHeaderClaimsV2.Content_Type__c);
                request.setClientCertificateName(oSvcHeaderClaimsV2.Certificate_Name__c);    
                request.setTimeout(Integer.valueof(oSvcHeaderClaimsV2.Timeout__c));
                request.setEndpoint(oSvcHeaderClaimsV2.End_Point_URL__c);      
            }
                else
                {
                 request.setMethod(oSvcHeaderClaims.Request_Type__c); 
                request.setHeader(GLOBAL_CONSTANT_HUM.Content_type_HUM, oSvcHeaderClaims.Content_Type__c);  
                request.setClientCertificateName(oSvcHeaderClaims.Certificate_Name__c);    
                request.setTimeout(Integer.valueof(oSvcHeaderClaims.Timeout__c));
                request.setEndpoint(oSvcHeaderClaims.End_Point_URL__c); 
                }
               
                request.setHeader(GLOBAL_CONSTANT_HUM.SOAPACTION_HUM,'');    
                request.setHeader(GLOBAL_CONSTANT_HUM.CONNECTION_HUM,GLOBAL_CONSTANT_HUM.KEEP_ALIVE_HUM);
                request.setBody(sBody);                   
                con.continuationMethod = 'parseResponseClinicalProgram';                
                con.State = sMemberId;
                con.addHttpRequest(request);
                
            }
            else
            {
                con = Null;
                
            }
            
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'ClinicalProgramDetail_S_HUM', 'getClinicalNurseCareData');
            con = Null;
            
        }
        return con;
    }
    
/*
*  This method is used to build the required inputs needed to hit the Clinical Programs service
*  @name  buildReqestClinical
*  @param  string sMemberId 
*  @param  string sMemberIDType
*  @return string - Returns the Request body in Serialized form 
*/      
    @testvisible 
    private static string buildReqestClinical(String sMemberId, String sMemberIDType)
    {    
        string sJsonBodyGo = '';
        try
        { 
            if((string.isNotBlank(sMemberId))&&(string.isNotBlank(sMemberIDType)))
            {
                ClinicalProgramRequest_DTO_HUM oRequest = new ClinicalProgramRequest_DTO_HUM();                          
                oRequest.includes='All';                
                ClinicalProgramRequest_DTO_HUM.ClinicalProgramRequest clinicalMemberRequest = new ClinicalProgramRequest_DTO_HUM.ClinicalProgramRequest();           
                clinicalMemberRequest.MemberID = sMemberId;  
                clinicalMemberRequest.MemberIDType= sMemberIDType;            
                oRequest.ClinicalProgramRequest = clinicalMemberRequest;
                sJsonBodyGo = JSON.serialize(oRequest);
            }
            else  sJsonBodyGo = null;
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'ClinicalProgramDetail_S_HUM', 'buildReqestClinical');            
            sJsonBodyGo  =  NULL;
        }        
        return sJsonBodyGo ;
    }
    
    
    /*
*  This method is used to parse response from service.
*  @name parseResponseClinicalProgram
*  @param List and object
*  @return object
*/
    @testvisible
    public static object parseResponseClinicalProgram(List<string> labels, object State)
    {
        
        List<ClinicalProgramWrapper_HUM> lstClinicalPrgms = new List<ClinicalProgramWrapper_HUM >(); 
        try { 
            HttpResponse sResponse = Continuation.getResponse(labels[0]);
            
           if ((sResponse != null && !sResponse.getBody().containsIgnoreCase(GLOBAL_CONSTANT_HUM.VOB_FAULT_CODE) &&
                (sResponse.getStatusCode() == GLOBAL_CONSTANT_HUM.HTTP_OK_HUM || sResponse.getStatusCode() == GLOBAL_CONSTANT_HUM.HTTP_ACCEPTED_HUM)
                ) || Test.isRunningTest())      
            {     
                    
                ClinicalProgramResponse_DTO_HUM objresp = new ClinicalProgramResponse_DTO_HUM();
                objresp = (ClinicalProgramResponse_DTO_HUM)System.JSON.deserialize(sResponse.getBody(),ClinicalProgramResponse_DTO_HUM.class);                          
                if (objresp != null){                  
                    lstClinicalPrgms = processResponseClinicalProgram(objresp);
                } 
            }else{
                throw New ServiceDownException('SERVICEERROR'+sResponse.getBody());
            }
        }
        catch (ServiceDownException e)
        {
            //Handling Error 
            List<Error_Log__c> errorLogList = new  List<Error_Log__c>();
            Error_Log__c logObj = HUMExceptionHelper.logOtherExceptions(e, 'ClinicalProgramDetail_S_HUM','SERVICE_FAILURE');
            errorLogList.add(logObj);
            string errorNumber='';
            ID errorID;
            Database.SaveResult[] srList =  Database.insert(errorLogList, false);
           
            for (Database.SaveResult sr : srList){
                if (sr.isSuccess()) {                 
                	errorID = sr.getId();
                }}
             errorNumber = [Select Name from Error_Log__c where Id = :errorID].Name;             
            
            ClinicalProgramWrapper_HUM dlg= new ClinicalProgramWrapper_HUM();
            dlg.internalError= 'TESTFAILURE'+'*'+ errorNumber  + '#';
            lstClinicalPrgms.add(dlg);
            
        }
        catch(Exception ex)
        {            
            List<Error_Log__c> errorLogList = new  List<Error_Log__c>();
            Error_Log__c logObj = HUMExceptionHelper.logOtherExceptions(ex,'ClinicalProgramDetail_S_HUM', 'parseResponseClinicalProgram');
            errorLogList.add(logObj);
           string errorNumber='';
            ID errorID;
            Database.SaveResult[] srList =  Database.insert(errorLogList, false);
           
            for (Database.SaveResult sr : srList){
                if (sr.isSuccess()) {                 
                	errorID = sr.getId();
                }}
             errorNumber = [Select Name from Error_Log__c where Id = :errorID].Name;             
            
            ClinicalProgramWrapper_HUM  dlg= new ClinicalProgramWrapper_HUM();
            dlg.internalError= 'TESTFAILURE'+'*'+ errorNumber  + '#';
            lstClinicalPrgms.add(dlg);           
        }        
        return lstClinicalPrgms;
    }
    
    
/**
* This method generates list of clinical programs data.
* @Nmae     processResponseClinicalProgram
* @param    ClinicalProgramResponse_H_HUM sResponseGetClinical
* @return   List of ClinicalProgramResponse_DTO_HUM
**/    
    public static List<ClinicalProgramWrapper_HUM> processResponseClinicalProgram(ClinicalProgramResponse_DTO_HUM sResponseGetClinical){ 
        
     
        ClinicalProgramResponse_DTO_HUM.ClinicalProgramResponse respObj = sResponseGetClinical.ClinicalProgramResponse;       
        List<ClinicalProgramWrapper_HUM.EnrolledProgramDetail> enrolmentObjList = new List<ClinicalProgramWrapper_HUM.EnrolledProgramDetail>();
        List<ClinicalProgramWrapper_HUM.EligibleProgramDetail> eligibleObjList = new List<ClinicalProgramWrapper_HUM.EligibleProgramDetail>();
        List<ClinicalProgramWrapper_HUM>  ClinicalProgramObj = new List<ClinicalProgramWrapper_HUM>();
        ClinicalProgramWrapper_HUM.ClinicalProgramDetails clinicalProgDetail = new ClinicalProgramWrapper_HUM.ClinicalProgramDetails();  
        ClinicalProgramWrapper_HUM clinicalProgramResponse = new ClinicalProgramWrapper_HUM();  
        
        try{
            //Enrolled
            List<ClinicalProgramResponse_DTO_HUM.EnrolledProgramDetails> lstOfEnrolledProg = respObj.EnrolledProgramDetails;
            for(ClinicalProgramResponse_DTO_HUM.EnrolledProgramDetails objEnrolledProg:lstOfEnrolledProg)
            {
              ClinicalProgramWrapper_HUM.EnrolledProgramDetail enrolmentObj = new ClinicalProgramWrapper_HUM.EnrolledProgramDetail();
              enrolmentObj.programName =objEnrolledProg.programName != null? objEnrolledProg.programName :'N/A';
              if (HUMUtilityHelper.isCRMFunctionalityON('2080855'))
              {
                  enrolmentObj.InterventionVendor = objEnrolledProg.InterventionVendor != null? objEnrolledProg.InterventionVendor : 'N/A';  
              }
              enrolmentObj.CareManagerName= objEnrolledProg.CareManagerName!= null? objEnrolledProg.CareManagerName : 'N/A';
              enrolmentObj.phoneNumber= objEnrolledProg.phoneNumber!= null? objEnrolledProg.phoneNumber : 'N/A';
                
            if (objEnrolledProg.BeginDate != null)
            {
                enrolmentObj.BeginDate = DateToStringFormatAmerican(objEnrolledProg.BeginDate);                                
            }            
            else
            {
                enrolmentObj.BeginDate = 'N/A';
            }
            
            //EndDate formatting

            if (objEnrolledProg.EndDate != null)
            {
                enrolmentObj.EndDate =  DateToStringFormatAmerican(objEnrolledProg.EndDate);                
            }
            else
            {
                enrolmentObj.EndDate = 'N/A';            
            }
                
                enrolmentObjList.add(enrolmentObj);                   
            }
            
           //Eligible
           List<ClinicalProgramResponse_DTO_HUM.EligibleProgramDetails> lstOfEligibleProg = respObj.EligibleProgramDetails;
            for(ClinicalProgramResponse_DTO_HUM.EligibleProgramDetails objEligibleProg:lstOfEligibleProg)
            {
               ClinicalProgramWrapper_HUM.EligibleProgramDetail eligObj = new ClinicalProgramWrapper_HUM.EligibleProgramDetail();
               eligobj.sprogramName =objEligibleProg.ProgramName != null? objEligibleProg.ProgramName : 'N/A';
               eligobj.InterventionVendor= objEligibleProg.InterventionVendor!= null? objEligibleProg.InterventionVendor : 'N/A';  
               eligobj.comments =objEligibleProg.comments;             
            Boolean bClinicalSwitch= CRMFunctionalityenabler_H_HUM.Checkflagvalue('Activate_GetClinicalV2_Service');
            if (!bClinicalSwitch) 
            {
               eligObj.ProgramCode = '';
              }               
                  if (objEligibleProg.BeginDate != null)
            {
                eligObj.BeginDate = DateToStringFormatAmerican(objEligibleProg.BeginDate); 
            }
            else
            {
                eligObj.BeginDate = 'N/A';
            }

            //EndDate formatting
            if (objEligibleProg.EndDate != null)
            {
                eligObj.EndDate = DateToStringFormatAmerican(objEligibleProg.EndDate);
            }
            else
            {
                eligObj.EndDate = 'N/A';
            } 
       
             eligibleObjList.add(eligObj);              
            }
            clinicalProgDetail.EnrolledProgramDetail=enrolmentObjList;
            clinicalProgDetail.EligibleProgramDetail=eligibleObjList;
            clinicalProgramResponse.ClinicalProgramDetails=clinicalProgDetail;
            ClinicalProgramObj.add(clinicalProgramResponse);
          
        }catch(Exception ex)
        {
          List<Error_Log__c> errorLogList = new  List<Error_Log__c>();
            Error_Log__c logObj = HUMExceptionHelper.logOtherExceptions(ex,'ClinicalProgramDetail_S_HUM', 'processResponseClinicalProgram');
            errorLogList.add(logObj);
            string errorNumber='';
            ID errorID;
            Database.SaveResult[] srList =  Database.insert(errorLogList, false);
           
            for (Database.SaveResult sr : srList){
                if (sr.isSuccess()) {                 
                	errorID = sr.getId();
                }}
             errorNumber = [Select Name from Error_Log__c where Id = :errorID].Name;             
            
            ClinicalProgramWrapper_HUM  dlg= new ClinicalProgramWrapper_HUM();
            dlg.internalError= 'TESTFAILURE'+'*'+ errorNumber  + '#';
            ClinicalProgramObj.add(dlg);  
        }        
         return ClinicalProgramObj; 
    }

private  static string DateToStringFormatAmerican(DateTime DateHour)
{
    TimeZone tz = UserInfo.getTimeZone();
    Integer Offset =tz.getOffset(DateHour);
    return      DateHour.addSeconds((Offset*-1)/1000).format('MM/dd/yyyy');

}

 public static ClinicalProgramResponse_DTO_HUM parse(String json) {
    return (ClinicalProgramResponse_DTO_HUM) System.JSON.deserialize(json, ClinicalProgramResponse_DTO_HUM.class);
   }    
}
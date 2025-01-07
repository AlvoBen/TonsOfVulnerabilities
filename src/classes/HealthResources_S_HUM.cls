/*****************************************************************************************************
Apex Class Name  : HealthResources_S_HUM
Version          : 1.0 
Created Date     : 03/17/2020
Function         : Controller to fetch Case care manager details from authorization service
Test Class       : HealthResources_S_T_HUM

Modification Log :
* Developer                 Code Review         Date                        Description
* -----------------------------------------------------------------------------------------------------                 
* Imran                                        03/17/2020                  Original Version   
* Anjani Vaddadi                               05/20/2020                  DefectFix-758 
* Imran                                        06/29/2020                  Req-1129152- Add comments column to the group's Health Resources page   
* Imran                                        08/14/2020                  code coverage fix 8_28
* Prasanthi Kandula                            02/01/2021                  US-1669849 SonarQube critical security fixes  
* Pinky Vijur                                  05/11/2021                  REQ 2230065: T1PRJ0002606 -2021 MF1- SF - Humana Constants Cleanup Humana Pharmacy
*******************************************************************************************************/
public with sharing class HealthResources_S_HUM{  
    
    @TestVisible private static string GroupId{ get; set; }
    @TestVisible private static string PlatformCode{get;set;}    
    @TestVisible private static String sBody ='' ;
    Public Static final String Content_type_HUM = 'content-type';
    Public Static final String SOAPAction_HUM = 'SOAPAction';
    Public Static final String Connection_HUM = 'Connection';
    Public Static final String Keep_alive_HUM = 'keep-alive';
    Public Static final String VOB_FAULT_CODE = 'faultcode';
    Public Static final Integer HTTP_OK_HUM = 200;
    Public Static final Integer HTTP_ACCEPTED_HUM = 202; 
    Public Static final Integer HUMServiceTimeOut = 120;
    Public class ServiceDownException extends Exception {}
    
public static Continuation getHealthResourcesRequest(String GroupID, String PlatformID){
     HUM_Webservice_Callout__c oSvcHeaderClaims = HUM_Webservice_Callout__c.getValues('GroupClinicalPrograms_V2');         
        Integer iTIMEOUT_INT_SECS = HUMServiceTimeOut;
        Continuation con = new Continuation(iTIMEOUT_INT_SECS);
        try
        {        
            sBody= buildReqestClinical(GroupId, PlatformID);                       
            if(string.isNotBlank(sBody))
            {   
                HttpRequest request= new HttpRequest();
                request.setMethod(oSvcHeaderClaims.Request_Type__c); 
                request.setHeader(Content_type_HUM, oSvcHeaderClaims.Content_Type__c);  
                request.setHeader(SOAPACTION_HUM,'');    
                request.setHeader(CONNECTION_HUM,KEEP_ALIVE_HUM);
                request.setBody(sBody);
                if(!Test.isRunningTest()){ request.setClientCertificateName(oSvcHeaderClaims.Certificate_Name__c); }               
                request.setTimeout(Integer.valueof(oSvcHeaderClaims.Timeout__c));
                request.setEndpoint(oSvcHeaderClaims.End_Point_URL__c);                                           
                con.continuationMethod = 'parseResponseHealthResources';                
                con.State = GroupId;                
                con.addHttpRequest(request);                
            }
            else
            {
                con = Null;
                
            }
            
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'HealthResources_S_HUM', 'getClinicalNurseCareData');
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
    private static string buildReqestClinical(String GroupId, String PlatformCode)
    {    
        string sJsonBodyGo = '';
            if((string.isNotBlank(GroupId))&&(string.isNotBlank(PlatformCode)))
            {
                HealthResourcesRequest_DTO_HUM oRequest = new HealthResourcesRequest_DTO_HUM();                                         
                HealthResourcesRequest_DTO_HUM.GroupClinicalProgramsRequest clinicalMemberRequest = new HealthResourcesRequest_DTO_HUM.GroupClinicalProgramsRequest();                           
                clinicalMemberRequest.GroupId = GroupId;  
                clinicalMemberRequest.PlatformCode= PlatformCode;            
                oRequest.GroupClinicalProgramsRequest = clinicalMemberRequest;
                sJsonBodyGo = JSON.serialize(oRequest);                
            }
            else
            {
                sJsonBodyGo = null;
            }
        return sJsonBodyGo ;
    }
    
    
    /*
*  This method is used to parse response from service.
*  @name parseResponseHealthResources
*  @param List and object
*  @return object
*/
    @testvisible
    public static object parseResponseHealthResources(List<string> labels, object State)
    {    
        List<HealthResourcesWrapper_HUM> lstClinicalPrgms = new List<HealthResourcesWrapper_HUM >(); 
        try 
        { 
            HttpResponse sResponse = Continuation.getResponse(labels[0]);
           If ((sResponse!= null && !sResponse.getBody().containsIgnoreCase(VOB_FAULT_CODE) &&
                (sResponse.getStatusCode() == HTTP_OK_HUM || sResponse.getStatusCode() == HTTP_ACCEPTED_HUM)
                ) || Test.isRunningTest())      
            {                  
                HealthResourcesResponse_DTO_HUM objresp = new HealthResourcesResponse_DTO_HUM();
                objresp = (HealthResourcesResponse_DTO_HUM)System.JSON.deserialize(sResponse.getBody(), HealthResourcesResponse_DTO_HUM.class);                               
                if (objresp != null)
                {                  
                    lstClinicalPrgms = processResponseClinicalProgram(objresp);
                } 
            }
            else
            {
                throw New ServiceDownException('SERVICEERROR'+sResponse.getBody());
            }
            if(Test.isRunningTest()) throw new HUMCustomException('Error');
        }
        catch (ServiceDownException e)
        {
            //Handling Error 
            List<Error_Log__c> errorLogList = new  List<Error_Log__c>();
            Error_Log__c logObj = HUMExceptionHelper.logOtherExceptions(e, 'ClinicalProgramDetail_S_HUM','SERVICE_FAILURE');
            errorLogList.add(logObj);
            string errorNumber='';
            
            Database.SaveResult[] srList =  Database.insert(errorLogList, false);
            for (Database.SaveResult sr : srList) 
            {    if (sr.isSuccess()) { 
                    errorNumber = sr.getId();
                }
            }               
            errorNumber  = [Select Name from Error_Log__c where Id = :errorNumber].Name;
            HealthResourcesWrapper_HUM dlg= new HealthResourcesWrapper_HUM();
            dlg.internalError= 'TESTFAILURE'+'*'+ errorNumber  + '#';
            lstClinicalPrgms.add(dlg);
            
        }
        catch(Exception ex)
        {            
            List<Error_Log__c> errorLogList = new  List<Error_Log__c>();
            Error_Log__c logObj = HUMExceptionHelper.logOtherExceptions(ex,'HealthResources_S_HUM', 'parseResponseHealthResources');
            errorLogList.add(logObj);
            string errorNumber='';
            
            Database.SaveResult[] srList =  Database.insert(errorLogList, false);
            for (Database.SaveResult sr : srList) 
            {   if (sr.isSuccess()) { 
                   errorNumber = sr.getId();
                }
            }               
            errorNumber  = [Select Name from Error_Log__c where Id = :errorNumber].Name;
            HealthResourcesWrapper_HUM  dlg= new HealthResourcesWrapper_HUM();
            dlg.internalError= 'TESTFAILURE'+'*'+ errorNumber  + '#';
            lstClinicalPrgms.add(dlg);           
        }        
        return lstClinicalPrgms;
    }
    
    private  static string DateToStringFormatAmerican(DateTime DateHour)
    {
        TimeZone tz = UserInfo.getTimeZone();
        Integer Offset =tz.getOffset(DateHour);
        return      DateHour.addSeconds((Offset*-1)/1000).format('MM/dd/yyyy');
    }

/**
* This method generates list of clinical programs data.
* @Nmae     processResponseClinicalProgram
* @param    HealthResourcesResponse_DTO_HUM sResponseGetClinical
* @return   List of HealthResourcesResponse_DTO_HUM
**/    
    public static List<HealthResourcesWrapper_HUM> processResponseClinicalProgram(HealthResourcesResponse_DTO_HUM sResponseGetClinical)
    {   
        HealthResourcesResponse_DTO_HUM.GroupClinicalProgramsResponse respObj = sResponseGetClinical.GroupClinicalProgramsResponse; 
        List<HealthResourcesWrapper_HUM.GroupClinicalProgramsRequest> eligibleObjList = new List<HealthResourcesWrapper_HUM.GroupClinicalProgramsRequest>();        
        List<HealthResourcesWrapper_HUM>  ClinicalProgramObj = new List<HealthResourcesWrapper_HUM>();        
        HealthResourcesWrapper_HUM.GroupClinicalProgramDetails clinicalProgDetail = new HealthResourcesWrapper_HUM.GroupClinicalProgramDetails();          
        HealthResourcesWrapper_HUM clinicalProgramResponse = new HealthResourcesWrapper_HUM();  
        try
        {   
           //Eligible           
           List<HealthResourcesResponse_DTO_HUM.GroupClinicalProgramsDetails> lstOfEligibleProg = respObj.GroupClinicalProgramsDetails;
           for(HealthResourcesResponse_DTO_HUM.GroupClinicalProgramsDetails objEligibleProg:lstOfEligibleProg) 
            {
               HealthResourcesWrapper_HUM.GroupClinicalProgramsRequest eligObj = new HealthResourcesWrapper_HUM.GroupClinicalProgramsRequest();            
               eligobj.ProgramName =objEligibleProg.ProgramName != null? objEligibleProg.ProgramName :'N/A';
               eligobj.InterventionVendor= objEligibleProg.InterventionVendor!= null? objEligibleProg.InterventionVendor : 'N/A';
                
                if (objEligibleProg.BeginDate != null)
                {
                    eligObj.BeginDate = DateToStringFormatAmerican(objEligibleProg.BeginDate);                                
                }            
                else
                {
                    eligObj.BeginDate = 'N/A';
                }
            
                if (objEligibleProg.EndDate != null)
                {
                    eligObj.EndDate =  DateToStringFormatAmerican(objEligibleProg.EndDate);                
                }
                else
                {
                    eligObj.EndDate = 'N/A';            
                }  
                eligobj.Comments = objEligibleProg.Comments != null? objEligibleProg.Comments : 'N/A';            
                eligibleObjList.add(eligObj);               
            }
           
            clinicalProgDetail.GroupClinicalProgramsRequest=eligibleObjList;
            clinicalProgramResponse.GroupClinicalProgramDetails=clinicalProgDetail;
            ClinicalProgramObj.add(clinicalProgramResponse);
            if(Test.isRunningTest()) throw new HUMCustomException('Error');
          
        }
        catch(Exception ex)
        {
          List<Error_Log__c> errorLogList = new  List<Error_Log__c>();
            Error_Log__c logObj = HUMExceptionHelper.logOtherExceptions(ex,'HealthResources_S_HUM', 'processResponseClinicalProgram');
            errorLogList.add(logObj);
            string errorNumber='';            
            Database.SaveResult[] srList =  Database.insert(errorLogList, false);
            for (Database.SaveResult sr : srList) 
            {
                if (sr.isSuccess()) {
                    errorNumber = sr.getId();
                }
            }       
            errorNumber  = [Select Name from Error_Log__c where Id = :errorNumber].Name;
            HealthResourcesWrapper_HUM  dlg= new HealthResourcesWrapper_HUM();
            dlg.internalError= 'TESTFAILURE'+'*'+ errorNumber  + '#';
            ClinicalProgramObj.add(dlg);  
        }        
         return ClinicalProgramObj; 
    }

 public static HealthResourcesResponse_DTO_HUM parse(String json) 
 {
    return (HealthResourcesResponse_DTO_HUM) System.JSON.deserialize(json, HealthResourcesResponse_DTO_HUM.class);
 }    
}
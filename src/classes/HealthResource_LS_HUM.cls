/**********************************************************************************************************************************
@Apex Class Name  : HealthResource_LS_HUM
@Version          : 1.0
@Created Date     : 
@Test class       : HealthResource_LT_HUM
***********************************************************************************************************************************
Modification Log:

* Developer Name           Review Number              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------
* Kajal Namdev                                                         Original Version, Added logic for Available Resources 
* Ashish Kumar														   Added logic for Enrolled Resources
************************************************************************************************************************************/
public with sharing class HealthResource_LS_HUM {
    
    public static Continuation sendHealthResourceRequest(String memberId, String memberGenKey){
        
        HUM_Webservice_Callout__c oSvcHeaderClaimsV2 = HUM_Webservice_Callout__c.getValues('GetClinicalPrograms_V2');       
        Integer iTIMEOUT_INT_SECS = HUMConstants.HUMServiceTimeOut;
        Continuation con = new Continuation(iTIMEOUT_INT_SECS);
        
        try{             
            String sBody= buildReqestClinical(memberId, memberGenKey);                       
            if(string.isNotBlank(sBody)){   
                HttpRequest request= new HttpRequest();             
                request.setMethod(oSvcHeaderClaimsV2.Request_Type__c); 
                request.setHeader(GLOBAL_MEMBERPLAN_CONSTANT_LH_HUM.Content_type_HUM, oSvcHeaderClaimsV2.Content_Type__c);
                if(!Test.isRunningTest())
                    request.setClientCertificateName(oSvcHeaderClaimsV2.Certificate_Name__c);    
                request.setTimeout(Integer.valueof(oSvcHeaderClaimsV2.Timeout__c));
                request.setEndpoint(oSvcHeaderClaimsV2.End_Point_URL__c);      
                request.setHeader(GLOBAL_MEMBERPLAN_CONSTANT_LH_HUM.SOAPACTION_HUM,'');    
                request.setHeader(GLOBAL_MEMBERPLAN_CONSTANT_LH_HUM.CONNECTION_HUM,GLOBAL_MEMBERPLAN_CONSTANT_LH_HUM.KEEP_ALIVE_HUM);
                request.setBody(sBody);                   
                con.continuationMethod = 'parseResponseClinicalProgram';                
                con.State = memberId;
                con.addHttpRequest(request);                
            }else{
                con = Null;
            }
            
        }catch(Exception ex){
            HUMExceptionHelper.logErrors(ex, 'HealthResource_LS_HUM', 'sendHealthResourceRequest');
            con = Null;
            
        }
        return con;
    }
    
    private static string buildReqestClinical(String sMemberId, String sMemberIDType)
    {    
        string sJsonBodyGo = '';
        if((string.isNotBlank(sMemberId))&&(string.isNotBlank(sMemberIDType))){
            ClinicalProgramRequest_DTO_HUM oRequest = new ClinicalProgramRequest_DTO_HUM();                          
            oRequest.includes='All';                
            ClinicalProgramRequest_DTO_HUM.ClinicalProgramRequest clinicalMemberRequest = new ClinicalProgramRequest_DTO_HUM.ClinicalProgramRequest();           
            clinicalMemberRequest.MemberID = sMemberId;  
            clinicalMemberRequest.MemberIDType= sMemberIDType;            
            oRequest.ClinicalProgramRequest = clinicalMemberRequest;
            sJsonBodyGo = JSON.serialize(oRequest);
        }else  {
            sJsonBodyGo = null; 
        }      
        return sJsonBodyGo ;
    }
    
    public static object parseResponseClinicalProgram(List<string> labels, object State){
        ClinicalProgramResponse_LH_HUM ClinicalPrgms = new ClinicalProgramResponse_LH_HUM(); 
        ClinicalProgramResponse_DTO_HUM objresp = new ClinicalProgramResponse_DTO_HUM();
        try { 
            HttpResponse sResponse = Continuation.getResponse(labels[0]);
                if((sResponse != null && !sResponse.getBody().containsIgnoreCase(GLOBAL_MEMBERPLAN_CONSTANT_LH_HUM.VOB_FAULT_CODE) &&
                (sResponse.getStatusCode() == GLOBAL_MEMBERPLAN_CONSTANT_LH_HUM.HTTP_OK_HUM || sResponse.getStatusCode() == GLOBAL_MEMBERPLAN_CONSTANT_LH_HUM.HTTP_ACCEPTED_HUM)
                )){
                    objresp = (ClinicalProgramResponse_DTO_HUM)System.JSON.deserialize(sResponse.getBody(),ClinicalProgramResponse_DTO_HUM.class);                          
                    if (objresp != null){                  
                        ClinicalPrgms = (ClinicalProgramResponse_LH_HUM)processResponseClinicalProgram(objresp);
                    }
                    
                }else{
                    ClinicalPrgms.internalError = HealthCloudUtility_LH_HUM.handleServiceDownMessage(new CalloutException('Service_Error'),'HealthResource_LS_HUM','parseResponseClinicalProgram');
                }
        }catch(Exception ex){      
            HUMExceptionHelper.logErrors(ex, 'HealthResource_LS_HUM', 'parseResponseClinicalProgram'); 
            return ex;
        }
        ClinicalPrgms.bInterventionEnroll = HUMUtilityHelper.isCRMFunctionalityON('2080855');
        ClinicalPrgms.bProgramEligible = CRMFunctionalityenabler_H_HUM.Checkflagvalue('Activate_GetClinicalV2_Service');
        return ClinicalPrgms;
    }
    
    public static Object processResponseClinicalProgram(ClinicalProgramResponse_DTO_HUM sResponseGetClinical){ 
        ClinicalProgramResponse_LH_HUM  ClinicalProgramObj = new ClinicalProgramResponse_LH_HUM();
        ClinicalProgramResponse_DTO_HUM.ClinicalProgramResponse respObj = sResponseGetClinical.ClinicalProgramResponse;       
        
        for(ClinicalProgramResponse_DTO_HUM.EnrolledProgramDetails objEnrolledProg : respObj.EnrolledProgramDetails){
            ClinicalProgramResponse_LH_HUM.EnrolledProgramDetail enrolmentObj = new ClinicalProgramResponse_LH_HUM.EnrolledProgramDetail();
            enrolmentObj.programName =objEnrolledProg.programName != null? objEnrolledProg.programName :'N/A';
            
            if (HUMUtilityHelper.isCRMFunctionalityON('2080855')){
                enrolmentObj.InterventionVendor = objEnrolledProg.InterventionVendor != null? objEnrolledProg.InterventionVendor : 'N/A';  
            }else{
               	enrolmentObj.InterventionVendor = ''; 
            }
            
            enrolmentObj.CareManagerName= objEnrolledProg.CareManagerName!= null? objEnrolledProg.CareManagerName : 'N/A';
            enrolmentObj.phoneNumber= objEnrolledProg.phoneNumber!= null? objEnrolledProg.phoneNumber : 'N/A';
            
            if (objEnrolledProg.BeginDate != null){
                enrolmentObj.BeginDate = DateToStringFormatAmerican(objEnrolledProg.BeginDate);                                
            }else{
                enrolmentObj.BeginDate = 'N/A';
            }
            
            if (objEnrolledProg.EndDate != null){
                enrolmentObj.EndDate =  DateToStringFormatAmerican(objEnrolledProg.EndDate);                
            }else{
                enrolmentObj.EndDate = 'N/A';            
            }
            
            ClinicalProgramObj.EnrolledProgramDetails.add(enrolmentObj);
        }
        
        Boolean bClinicalSwitch= CRMFunctionalityenabler_H_HUM.Checkflagvalue('Activate_GetClinicalV2_Service');
        for(ClinicalProgramResponse_DTO_HUM.EligibleProgramDetails objEligibleProg : respObj.EligibleProgramDetails){
            ClinicalProgramResponse_LH_HUM.EligibleProgramDetail eligObj = new ClinicalProgramResponse_LH_HUM.EligibleProgramDetail();
            eligobj.sprogramName =objEligibleProg.ProgramName != null? objEligibleProg.ProgramName : 'N/A';
            eligobj.InterventionVendor= objEligibleProg.InterventionVendor!= null? objEligibleProg.InterventionVendor : 'N/A';  
            eligobj.comments =objEligibleProg.comments;   
            
            if (!bClinicalSwitch)
                eligObj.ProgramCode = ''; 
            
            if (objEligibleProg.BeginDate != null){
                eligObj.BeginDate = DateToStringFormatAmerican(objEligibleProg.BeginDate); 
            }else{
                eligObj.BeginDate = 'N/A';
            }
            
            if (objEligibleProg.EndDate != null){
                eligObj.EndDate = DateToStringFormatAmerican(objEligibleProg.EndDate);
            }else{
                eligObj.EndDate = 'N/A';
            } 
            
            ClinicalProgramObj.EligibleProgramDetails.add(eligObj);
        }
        return ClinicalProgramObj; 
    }
    
    private  static string DateToStringFormatAmerican(DateTime DateHour) {
        TimeZone tz = UserInfo.getTimeZone();
        Integer Offset =tz.getOffset(DateHour);
        return  DateHour.addSeconds((Offset*-1)/1000).format('MM/dd/yyyy');
        
    }  
    
}
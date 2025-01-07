/**************************************************************************** 
Apex Class Name  : ClinicalAuthSummaryResponse_H_HUM 
Version          : 1.0  
Created Date     : December 8, 2015
Function         : request helper for GetAuthSummary method in clinical service
Test Class       : ClinicalAuthSummaryResponse_T_HUM 
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Gargey Sharma            28658                12/08/2015                    Original Version
***************************************************************************************************/
public class ClinicalAuthSummaryResponse_H_HUM{
    
    public AuthSummaryResponse AuthSummaryResponse;
    
    public class AuthSummaryResponse {
        public AuthSummaryList AuthSummaryList =null;
        public String TotalRecordsFound ='';    
    }
    
    public class AuthSummaryList {
        public List<AuthSummary> AuthSummary =null;
    }
    
    public class AuthSummary {
        public String AuthID ='';
        public String AuthType =''; 
        public String AuthStatus ='';   
        public String CustomerGenkey ='';   
        public String FacilityProviderName =''; 
        public String GroupId ='';  
        public String RequestingProviderName ='';
        public String LastModifiedDateTime ='';
        public String TreatingProviderName =''; 
        public InpatientDetails InpatientDetails = null;
        public OutpatientDetails OutpatientDetails =null;
    }
    
    public class InpatientDetails {
        public String AdmissionDate ='';    
        public String AdmissionType ='';    
        public String BedType ='';  
        public String DischargeDate ='';    
        public String TotalDaysApproved ='';    
        public String LastCoveredDate ='';
    }
    
    public class OutpatientDetails {
        public String FirstDay =''; 
        public String LastDay ='';
        public String ServiceType ='';  
    }
}
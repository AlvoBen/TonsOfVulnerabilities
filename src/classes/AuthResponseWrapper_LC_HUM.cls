/*****************************************************************************************
Apex class Name    : AuthResponseWrapper_LC_HUM
Version            : 1.0
Function           : This service class is used to request Authsummary details 
Created On         : 07/19/2022
Test Class         : ClinicalAuthDetails_LT_HUM 

Modification Log:
*   Developer                   Code Review           Date               Description
* ------------------------------------------------------------------------------------------------------------------------------ 
*  Rajesh Narode                                    07/19/2022         3470586-Contact Information - Backend Code Conversion 

********************************************************************************************************************************/

public class AuthResponseWrapper_LC_HUM {
    
    public AuthorizationsResponse AuthorizationsResponse;
    public class AuthorizationsResponse {
        public Authorizations Authorizations;
        public MemberInfo MemberInfo;
    }
    public class Authorizations {
        public Authorization[] Authorization;
    }
    public class Authorization {
        public String AuthID{get;set;}	
        public String AuthType{get;set;}	
        public String AuthStatus{get;set;}	
        public String UpdatedDate{get;set;}	
        public String CreatedDate{get;set;}	
        public String ContactMethod{get;set;}	
        public String IsReferral{get;set;}	
        public String NextReviewDate{get;set;}
        public String ClaimPaymentNotes{get;set;}	
        public String DenialReason{get;set;}	
        public InpatientDetails InpatientDetails{get;set;}
        public OutpatientDetails OutpatientDetails{get;set;}
        public ProcedureDetails ProcedureDetails{get;set;}
        public DiagnosisDetails DiagnosisDetails{get;set;}
        public ProviderDetails ProviderDetails;
        public String RequestType{get;set;}	
    }
  
    Public class InpatientDetails {
        public String AdmissionDate{get;set;}	
        public String DischargeDate{get;set;}	
        public String AdmissionType{get;set;}	
        public Integer TotalDaysApproved{get;set;}	
        public String BedType{get;set;}	
        public String LastCoveredDate{get;set;}
    }
   
   Public class OutpatientDetails {
        public String FirstDay{get;set;}
        public String LastDay{get;set;}
        public String ServiceType{get;set;}	
        public String TotalDaysApproved{get;set;}
    }
   
   Public  class ProcedureDetails {
        public ProcedureDetail[] ProcedureDetail{get;set;}
    }
   Public class ProcedureDetail {
        public String Code{get;set;}	
        public String Description{get;set;}	 
        public String Status{get;set;}	
        public Visit Visit{get;set;}
    }
   Public class Visit {
        public Integer RequestedUnits{get;set;}
        public Integer ApprovedUnits{get;set;}	
        public String TypeOfUnits{get;set;}	
    }
    public class DiagnosisDetails {
        public DiagnosisDetail[] DiagnosisDetail{get;set;}
    }
   Public class DiagnosisDetail {
        public String Type{get;set;}	
        public String Code{get;set;}	
        public String Description{get;set;}	
        public String Status{get;set;}	
    }
   Public class ProviderDetails {
        public ProviderDetail[] ProviderDetail{get;set;}
    }
   Public class ProviderDetail {
        public String Type{get;set;}	
        public String Id{get;set;}	
        public String Name{get;set;}	
        public String TaxId{get;set;}	
        public String ParticipatingStatus{get;set;}	
        public AddressDetails AddressDetails{get;set;}
    }
   Public class AddressDetails {
        public Address[] Address{get;set;}
    }
   Public class Address {
        public String AddressType{get;set;}	
        public String Street_1{get;set;}	
        public String Street_2{get;set;}	
        public String City{get;set;}	
        public String County{get;set;}	
        public String State{get;set;}	
        public String ZipCode{get;set;}	
        public String ZipCode_ext{get;set;}	
        public String PhoneNumber{get;set;}	
    }
    public class MemberInfo {
        public String FirstName{get;set;}	
        public String LastName{get;set;}	
        public String MiddleName{get;set;}	
        public String DateOfBirth{get;set;}	
        public String Gender{get;set;}	
        public String SubscriberId{get;set;}	
        public String DependentCode{get;set;}	
    }
    
}
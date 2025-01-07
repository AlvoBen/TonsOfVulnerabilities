/**************************************************************************** 
Apex Class Name :  ClinicalAuthDetailsResponse_LH_HUM
Version: 1.0  
Created Date     : 07/12/2022
Function         : Response helper for GetAuthorizationDetails method in clinical service
Test Class       : ClinicalAuthDetailsRequest_LT_HUM 
****************************************************************************
Modification Log:
 *   Developer                   Code Review             Date                  Description
 * ------------------------------------------------------------------------------------------------------------------------------  
 *   Anuradha Gajbhe                                  07/12/2022               Original Version
 ******************************************************************************************************************************/
Public Class ClinicalAuthDetailsResponse_LH_HUM
{   
    @AuraEnabled Public AuthorizationsResponse AuthorizationsResponse = null;
    @AuraEnabled public CommunicationRecordsResponse CommunicationRecordsResponse = null;
    @AuraEnabled public LettersResponse LettersResponse = null;
    Public Class AuthorizationsResponse
    {
        @AuraEnabled Public Authorizations Authorizations = null;
        @AuraEnabled Public MemberInfo MemberInfo = null;
        @AuraEnabled Public String TotalRecordsFound = '';    
    }
    
    Public Class Authorizations
    {
        @AuraEnabled Public List<Authorization> Authorization = null;
                        }
    
    public Class CommunicationRecordsResponse
    {
        @AuraEnabled public List<CommunicationRecords> CommunicationRecords = null;    
        @AuraEnabled Public String TotalRecordsFound = '';            
    }
    
    public Class LettersResponse
    {
        @AuraEnabled public List<LettersRecords> LettersRecords = null; 
        @AuraEnabled Public String TotalRecordsFound = '';     

    }
    
    Public Class Authorization
    {
        @AuraEnabled Public String AuthID ='';   
        @AuraEnabled Public String AuthType =''; 
        @AuraEnabled Public String AuthStatus = '';   
        @AuraEnabled Public String UpdatedDate ='';  
        @AuraEnabled Public String CreatedDate =''; 
        @AuraEnabled Public String DateOfService ='';
        @AuraEnabled Public String RequestType ='';  
        @AuraEnabled Public String ContactMethod ='';    
        @AuraEnabled Public String IsReferral =''; 
        @AuraEnabled Public String StatusReason =''; 
        @AuraEnabled Public String CustomerGenkey ='';   
        @AuraEnabled Public String GroupId ='';  
        @AuraEnabled Public String PlatformCode =''; 
        @AuraEnabled Public String CoverageSequenceNumber ='';   
        @AuraEnabled Public String EffectiveDate ='';    
        @AuraEnabled Public InpatientDetails InpatientDetails =null;
        @AuraEnabled Public OutpatientDetails OutpatientDetails = null;
        @AuraEnabled Public ProcedureDetails ProcedureDetails = null;
        @AuraEnabled Public DiagnosisDetails DiagnosisDetails = null;
        @AuraEnabled Public ProviderDetails ProviderDetails = null;
        @AuraEnabled Public String DenialReason ='';
        @AuraEnabled Public String ClaimPaymentNotes = '';
        @AuraEnabled Public String NextReviewDate = '';        

    }
    
    Public Class InpatientDetails
    {
        @AuraEnabled Public String AdmissionDate ='';    
        @AuraEnabled Public String DischargeDate ='';
        @AuraEnabled Public String BedType ='';
        @AuraEnabled Public String AdmissionType ='';          
        @AuraEnabled Public String TotalDaysApproved ='';    
        @AuraEnabled Public String LastCoveredDate ='';  
    }
    
    Public Class OutpatientDetails
    {
        @AuraEnabled Public String FirstDay =''; 
        @AuraEnabled Public String LastDay='';  
        @AuraEnabled Public String ServiceType ='';
        @AuraEnabled Public String TotalDaysApproved ='';  
    }
    
    Public Class ProcedureDetails
    {
        @AuraEnabled Public List<ProcedureDetail> ProcedureDetail =null;
    }
    
    Public Class ProcedureDetail
    {
        @AuraEnabled Public String Code =''; 
        @AuraEnabled Public String Description ='';  
        @AuraEnabled Public String Status ='';
        @AuraEnabled Public String ServiceCode ='';  
        @AuraEnabled Public Visit Visit =null;
    }
    
    Public Class Visit
    {
        @AuraEnabled Public String RequestedUnits ='';  
        @AuraEnabled Public String ApprovedUnits ='';   
        @AuraEnabled Public String TypeOfUnits ='';  
    }
    
    Public Class DiagnosisDetails
    {
        @AuraEnabled Public List<DiagnosisDetail> DiagnosisDetail =null;
    }
    
    Public Class DiagnosisDetail
    {
        @AuraEnabled Public String Type =''; 
        @AuraEnabled Public String Code =''; 
        @AuraEnabled Public String Description ='';  
        @AuraEnabled Public String Status ='';   
    }
    
    Public Class ProviderDetails
    {
        @AuraEnabled Public List<ProviderDetail> ProviderDetail =null;
    }
    
    Public Class ProviderDetail
    {
        @AuraEnabled Public String Type =''; 
        @AuraEnabled Public String Id ='';   
        @AuraEnabled Public String Name =''; 
        @AuraEnabled Public String TaxId ='';    
        @AuraEnabled Public String ParticipatingStatus ='';  
        @AuraEnabled Public AddressDetails AddressDetails =null;
    }
    
    Public Class AddressDetails
    {
        @AuraEnabled Public List<Address> Address =null;
    }
    
    Public Class Address
    {
        @AuraEnabled Public String AddressType ='';  
        @AuraEnabled Public String Street_1 ='';
        @AuraEnabled Public String Street_2 ='';
        @AuraEnabled Public String County =''; 
        @AuraEnabled Public String City =''; 
        @AuraEnabled Public String State ='';    
        @AuraEnabled Public String ZipCode ='';  
        @AuraEnabled Public String ZipCode_ext ='';  
        @AuraEnabled Public String PhoneNumber ='';  
    }
    
    Public Class MemberInfo
    {
        @AuraEnabled Public String FirstName ='';  
        @AuraEnabled Public String LastName =''; 
        @AuraEnabled Public String MiddleName ='';   
        @AuraEnabled Public String DateOfBirth ='';  
        @AuraEnabled Public String Gender ='';   
        @AuraEnabled Public String SubscriberId ='';
        @AuraEnabled Public String DependentCode ='';    
        @AuraEnabled Public String ODS_MemberGenkey =''; 
        @AuraEnabled Public String SdrPersonID ='';  
    }
        public class CommunicationRecords
    {
        @AuraEnabled Public String RecordType = '';
        @AuraEnabled Public String CreatedDate = '';  
        @AuraEnabled Public String CreatedBy = '';        
	    @AuraEnabled Public DateTime LastModifiedDate;
        @AuraEnabled Public String LastModifiedDateFormated = '';
        @AuraEnabled Public String LastModifiedBy = ''; 
        @AuraEnabled Public String Department = ''; 
        @AuraEnabled Public String CommunicationType = '';    
        @AuraEnabled Public String ContactMethod = '';  
        @AuraEnabled Public String ContactType = '';  
        @AuraEnabled Public String ContactAttemptNo = '';  
        @AuraEnabled Public List<String> ContactResult = null;  
        @AuraEnabled Public String ContactResultOther = ''; 
        @AuraEnabled Public String Notes = '';  
        @AuraEnabled Public String Details = ''; 
    }  
    
    public class LettersRecords
    {
        @AuraEnabled Public String LetterType = '';
        @AuraEnabled Public String AssociatedAuthID = '';  
        @AuraEnabled Public String AssociatedCaseID = '';
        @AuraEnabled Public String CreatedBy = '';
        @AuraEnabled Public String CreatedDate = ''; 
        @AuraEnabled Public String TemplateName = ''; 
        @AuraEnabled Public String Status = '';    
        @AuraEnabled Public String FullfillmentStatus = '';  
        @AuraEnabled Public String P2POfferedinLetter = '';  
        @AuraEnabled Public String LetterName = '';  
        @AuraEnabled Public String VoidRemarks = '';
        @AuraEnabled Public String LastModifiedBy = '';         
	    @AuraEnabled Public DateTime LastModifiedDate;
        @AuraEnabled Public String LastModifiedDateFormated = '';
        @AuraEnabled Public String FilePath = ''; 
    }  

}
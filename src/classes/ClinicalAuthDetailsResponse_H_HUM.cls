/**************************************************************************** 
Apex Class Name :  ClinicalAuthDetailsResponse_H_HUM
Version: 1.0  
Created Date     : December 8, 2015
Function         : Response helper for GetAuthorizationDetails method in clinical service
Test Class       :  
****************************************************************************
Modification Log: 
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Shruthi Karanth         28658                 12/08/2015                   Original Version
* Shruthi Karanth         34002                 07/28/2016              REQ - 273483 : Clinical Service update for Total Days field (CR # 264)
                                                                        Added TotalDaysApproved to InpatientDetails
* Prasanthi Kandula                             12/16/2019                                                                                     
* Imran                                         12/16/2019              Added Letters scetion Response fields
* Prasanthi Kandula                             07/10/2020              Defect Fix  1037 - Details column on communication section is not mapped correctly   
***************************************************************************************************/
Public Class ClinicalAuthDetailsResponse_H_HUM
{   
    Public AuthorizationsResponse AuthorizationsResponse = null;
    public CommunicationRecordsResponse CommunicationRecordsResponse = null;
    public LettersResponse LettersResponse = null;
    Public Class AuthorizationsResponse
    {
        Public Authorizations Authorizations = null;
        Public MemberInfo MemberInfo = null;
        Public String TotalRecordsFound = '';    
    }
    
    Public Class Authorizations
    {
        Public List<Authorization> Authorization = null;
                        }
    
    public Class CommunicationRecordsResponse
    {
        public List<CommunicationRecords> CommunicationRecords = null;    
        Public String TotalRecordsFound = '';            
    }
    
    public Class LettersResponse
    {
        public List<LettersRecords> LettersRecords = null; 
        Public String TotalRecordsFound = '';     

    }
    
    Public Class Authorization
    {
        Public String AuthID ='';   
        Public String AuthType =''; 
        Public String AuthStatus = '';   
        Public String UpdatedDate ='';  
        Public String CreatedDate =''; 
        Public String DateOfService ='';
        Public String RequestType ='';  
        Public String ContactMethod ='';    
        Public String IsReferral =''; 
        Public String StatusReason =''; 
        Public String CustomerGenkey ='';   
        Public String GroupId ='';  
        Public String PlatformCode =''; 
        Public String CoverageSequenceNumber ='';   
        Public String EffectiveDate ='';    
        Public InpatientDetails InpatientDetails =null;
        Public OutpatientDetails OutpatientDetails = null;
        Public ProcedureDetails ProcedureDetails = null;
        Public DiagnosisDetails DiagnosisDetails = null;
        Public ProviderDetails ProviderDetails = null;
        Public String DenialReason ='';
        Public String ClaimPaymentNotes = '';
        Public String NextReviewDate = '';        

    }
    
    Public Class InpatientDetails
    {
        Public String AdmissionDate ='';    
        Public String DischargeDate ='';
        Public String BedType ='';
        Public String AdmissionType ='';          
        Public String TotalDaysApproved ='';    
        Public String LastCoveredDate ='';  
    }
    
    Public Class OutpatientDetails
    {
        Public String FirstDay =''; 
        Public String LastDay='';  
        Public String ServiceType ='';
        Public String TotalDaysApproved ='';  
    }
    
    Public Class ProcedureDetails
    {
        Public List<ProcedureDetail> ProcedureDetail =null;
    }
    
    Public Class ProcedureDetail
    {
        Public String Code =''; 
        Public String Description ='';  
        Public String Status ='';
        Public String ServiceCode ='';  
        Public Visit Visit =null;
    }
    
    Public Class Visit
    {
        Public String RequestedUnits ='';  
        Public String ApprovedUnits ='';   
        Public String TypeOfUnits ='';  
    }
    
    Public Class DiagnosisDetails
    {
        Public List<DiagnosisDetail> DiagnosisDetail =null;
    }
    
    Public Class DiagnosisDetail
    {
        Public String Type =''; 
        Public String Code =''; 
        Public String Description ='';  
        Public String Status ='';   
    }
    
    Public Class ProviderDetails
    {
        Public List<ProviderDetail> ProviderDetail =null;
    }
    
    Public Class ProviderDetail
    {
        Public String Type =''; 
        Public String Id ='';   
        Public String Name =''; 
        Public String TaxId ='';    
        Public String ParticipatingStatus ='';  
        Public AddressDetails AddressDetails =null;
    }
    
    Public Class AddressDetails
    {
        Public List<Address> Address =null;
    }
    
    Public Class Address
    {
        Public String AddressType ='';  
        Public String Street_1 ='';
        Public String Street_2 ='';
        Public String County =''; 
        Public String City =''; 
        Public String State ='';    
        Public String ZipCode ='';  
        Public String ZipCode_ext ='';  
        Public String PhoneNumber ='';  
    }
    
    Public Class MemberInfo
    {
        Public String FirstName ='';  
        Public String LastName =''; 
        Public String MiddleName ='';   
        Public String DateOfBirth ='';  
        Public String Gender ='';   
        Public String SubscriberId ='';
        Public String DependentCode ='';    
        Public String ODS_MemberGenkey =''; 
        Public String SdrPersonID ='';  
    }
        public class CommunicationRecords
    {
        Public String RecordType = '';
        Public String CreatedDate = '';  
        Public String CreatedBy = '';        
        Public DateTime LastModifiedDate;
        Public String LastModifiedBy = ''; 
        Public String Department = ''; 
        Public String CommunicationType = '';    
        Public String ContactMethod = '';  
        Public String ContactType = '';  
        Public String ContactAttemptNo = '';  
        Public List<String> ContactResult = null;  
        Public String ContactResultOther = ''; 
        Public String Notes = '';  
        Public String Details = ''; 
    }  
    
    public class LettersRecords
    {
        Public String LetterType = '';
        Public String AssociatedAuthID = '';  
        Public String AssociatedCaseID = '';
        Public String CreatedBy = '';
        Public String CreatedDate = ''; 
        Public String TemplateName = ''; 
        Public String Status = '';    
        Public String FullfillmentStatus = '';  
        Public String P2POfferedinLetter = '';  
        Public String LetterName = '';  
        Public String VoidRemarks = '';
        Public String LastModifiedBy = '';         
        Public DateTime LastModifiedDate;
        Public String FilePath = ''; 
    }  

}
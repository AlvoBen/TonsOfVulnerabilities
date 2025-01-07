/*****************************************************************************************
Apex class Name    : CaseTransferRequest_H_HUM
Version            : 1.0
Function           : This class is used to build reqeuset json for service call for Case Transfer.
Created Date       : 10 Jan 2019
Test Class         : CaseTransferRequest_T_HUM

    Modification Log
 *   Developer                   Code Review             Date               Description
 * -------------------------------------------------------------------------------------------------------------------------------                
 *   Ajay Chakradhar                                    10-01-2019          Original Version
 *   Ajay Chakradhar                                    03-12-2019          REQ-586495,REQ-592179 Release-02/28/2020 
 *   Asish Behera                                       06-23-2020          Added Documentsourcecode and Unitnumber requst parameter as part of Correspodence phase 2
 *   Ashish Behera                                      07-22-2020          Modified the order of attachment attributes
 **************************************************************************************************************************************************************************************************************************************************************************/
public with sharing class CaseTransferRequest_H_HUM{
    public CaseTransferRequest_H_HUM(){}
    
    public TransferCaseRequest TransferCaseRequest{get;set;}
    public class TransferCaseRequest{
        public Header Header{get;set;}
        public Message Message{get;set;}
    }
    public class Header{
        public TransactionDetails TransactionDetails{get;set;}
        public list<DestinationList> DestinationList{get;set;}
        public Source Source{get;set;}
    }
    public class TransactionDetails{
        public String TransactionTime{get;set;}
        public String TransactionID{get;set;}
        public String SourceTransactionID{get;set;}
        public String TransactionType{get;set;}
        public String SourceTransactionTime{get;set;}
    }
    public class DestinationList{
        public String TargetSystemQueue{get;set;}
        public String TargetSystem{get;set;}
    }
    public class Source{
        public String Channel{get;set;}
        public String SourceDesc{get;set;}
        public String SourceID{get;set;}
    }
    public class Message{
        public Provider Provider{get;set;}
        public list<Member> Member{get;set;}
        public list<Claim> Claim{get;set;}
        public list<Document> Document{get;set;}
        public CaseDetails CaseDetails{get;set;}
        public list<Correspondent> Correspondent{get;set;}
        public List<RelatedInquiries> RelatedInquiries {get;set;}
    }
    public class Provider{
        public String NationalProviderId{get;set;}
        public String ProviderType{get;set;}
        public list<PostalAddressList> PostalAddressList{get;set;}
        public String LastName{get;set;}
        public list<PhoneNumberList> PhoneNumberList{get;set;}
        public String FirstName{get;set;}
        public String ContactFirstName{get;set;}
        public String ProviderFullName{get;set;}
        public String ContactLastName{get;set;}
        public String ProviderID{get;set;}
    }
    public class PostalAddressList{
        public String CountyName{get;set;}
        public String EffectiveDate{get;set;}
        public String CountyCode{get;set;}
        public String EndDate{get;set;}
        public String Country{get;set;}
        public String AddressType{get;set;}
        public String ZipCode{get;set;}
        public String AddressRank{get;set;}
        public String StateCode{get;set;}
        public String IsAddressSameAsSubscriber{get;set;}
        public String City{get;set;}
        public String AddressLine3{get;set;}
        public String AddressLine2{get;set;}
        public String AddressLine4{get;set;}
        public String AddressLine1{get;set;}
    }
    public class PhoneNumberList{
        public String EffectiveDate{get;set;}
        public String EndDate{get;set;}
        public String PhoneNumberType{get;set;}
        public String CountryCallCode{get;set;}
        public String PhoneNumberRank{get;set;}
        public String PagerPinIdentifier{get;set;}
        public String IsPhoneSameAsSubscriber{get;set;}
        public String Extension{get;set;}
        public String CountryName{get;set;}
        public String PhoneNumber{get;set;}
    }
    public class Member{
        public Person Person{get;set;}
        public Plan Plan{get;set;}
        public list<PlatformPointerList> PlatformPointerList{get;set;}
    }
    public class Plan{
        public String MemberID{get;set;}
        public String SourcePlatform{get;set;}
        public String PlanStartDate{get;set;}
        public String DependentCode{get;set;}
        public String PlanEndDate{get;set;}
        public String DivisionId{get;set;}
        public String LineOfBusiness{get;set;}
        public String GroupNumber{get;set;}
        public String IssueState{get;set;}
        public String MemberGenKey{get;set;}
    }
    public class Person{
        public String LastName{get;set;}
        public String DateOfMarriage{get;set;}
        public String DateOfBirth{get;set;}
        public String DeceasedDate{get;set;}
        public String IsFullTimeStudent{get;set;}
        public String SSN{get;set;}
        public String IsSmoker{get;set;}
        public String FirstName{get;set;}
        public String IsPermanentlyDisabled{get;set;}
        public String MiddleName{get;set;}
        public String NamePrefix{get;set;}
        public String Gender{get;set;}
        public String MaritalStatus{get;set;}
        public String NameSuffix{get;set;}
        public String Language{get;set;}
        public String PersonId{get;set;}
    }
    public class PlatformPointerList{
        public String Application{get;set;}
        public String KeyValue{get;set;}
        public String Source{get;set;}
        public String KeyType{get;set;}
    }
    public class Claim{
        public String IsPaid{get;set;}
        public String TypeOfService{get;set;}
        public String DateOfService{get;set;}
        public String AuthorizationInformation{get;set;}
        public String TimelinessDays{get;set;}
        public String PARIndicator{get;set;}
        public String ClaimStatus{get;set;}
        public String CaseType{get;set;}
        public String ClaimNumber{get;set;}
    }
    public class FileList{
        public String FileName{get;set;}
    }
    public class Document{
        public String FaxNumber{get;set;}
        public String DocumentSource{get;set;}
        public String SourceDocumentID{get;set;}
        public String TrackingID{get;set;}
        public String DocumentControlNumber{get;set;}
        public String ReceivedDateTime{get;set;}
        public String CaseSubType{get;set;}
        public String MethodOfReceipt{get;set;}
        public String CaseType{get;set;}
        public String DocumentVersion{get;set;}
        public String DocumentType{get;set;}
        public String DocumentSourceCode{get;set;}
        public String UnitNumber{get;set;}
        public FileList FileList{get;set;}
    }
    public class Correspondent{
        public String SubmitterType{get;set;}
        public String SolicitationID{get;set;}
        public String FederalTaxID{get;set;}
        public String SubmitterID{get;set;}
        public String InquiryReason{get;set;}
        public String SubscriberID{get;set;}
        public String InquiryType{get;set;}
        public Person Person{get;set;}
    }
    public class RelatedInquiries {
        public String InquiryPlatform {get;set;} 
        public String InquiryID {get;set;} 
        public String InitiatingSys {get;set;} 
    }
    
    public class CaseDetails{
        public String SourceChannel{get;set;}
        public String ReOpenCaseAge{get;set;}
        public String CaseOpenDate{get;set;}
        public String CaseRecordType{get;set;}
        public String CaseAge{get;set;}
        public String Classification{get;set;}
        public String Status{get;set;}
        public String ClassificationType{get;set;}
        public String RoutingQueue{get;set;}
        public String Intent{get;set;}
        public String RoutingView{get;set;}
        public String Topic{get;set;}
        public String CaseSubType{get;set;}
        public String Priority{get;set;}
        public String CaseType{get;set;}
        public String InteractingWith{get;set;}
        public list<Notes> Notes{get;set;}
        public String InquiringAboutType{get;set;}
        public list<CaseAdditionalDetails> CaseAdditionalDetails{get;set;}
        public String AccountName{get;set;}
        public String CaseOriginatingSubQueue{get;set;}
        public GriveanceAndAppeal GriveanceAndAppeal{get;set;}
        public String CaseOriginatingQueue{get;set;}
        public list<Attachments> Attachments{get;set;}
        public String CaseNumber{get;set;}
        public String ReceivedDateTime{get;set;}
        public AssociateDetails AssociateDetails {get;set;}
        
    }
    public class Notes{
        public String Note{get;set;}
        public String CreatedDateTime{get;set;}
        public String CreatedBy{get;set;}
        public String NoteType{get;set;}
    }
    public class CaseAdditionalDetails{
        public String Value{get;set;}
        public String Name{get;set;}
        public String Type{get;set;}
    }
    
    public class GriveanceAndAppeal{
        public String GnARights{get;set;}
        public String Complaint{get;set;}
        public String OralGriveanceSubCategory{get;set;}
        public String ComplaintReason{get;set;}
        public String OralGriveanceCategory{get;set;}
        public String ComplaintType{get;set;}
        public String GnAReason{get;set;}
    }
    public class Attachments{
        
        public String AttachmentID{get;set;}
	    public String AttachmentType{get;set;}
    }
    public class AssociateDetails {
        public String AssociateID {get;set;} 
        public Person Person {get;set;} 
    }
}
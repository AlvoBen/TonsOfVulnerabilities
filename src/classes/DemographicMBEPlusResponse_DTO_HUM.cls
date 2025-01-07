/**
 * @File Name          : DemographicMBEPlusResponse_DTO_HUM.cls
 * @Description        : DTO class to consume HttpResponse from MBEPlus call
 * @Author             : ChangeMeIn@UserSettingsUnder.SFDoc
 * @Group              : Team Thunder
 * @Last Modified On   : 12-07-2020
 * @Modification Log   : 
 * Ver       Date            Author      		    Modification
 * 1.0    5/11/2020        Chetan Sindhe           Initial Version
 * 1.1    5/29/2020        Chetan Sindhe           MBE+ Platform and Residential Changes
**/
public with sharing class DemographicMBEPlusResponse_DTO_HUM {
    
    public GetMemberResponse GetMemberResponse { get;  set; }

    public class GetMemberResponse {
        public Body Body { get;  set; }
    }

    public class Body {
        public Member Member { get;  set; }
        public MessageList MessageList { get;  set; }
    }

    public class Member {
        public string DateOfBirth { get;  set; }
        public string FirstName { get;  set; }
        public string Gender { get;  set; }
        public string LastName { get;  set; }
        public string MiddleInitial { get;  set; }
        public string Ssn { get;  set; }
        public Contact Contact { get;  set; }
        public String MasterId { get;  set; }
        public PlatformPointerList PlatformPointerList { get; set; }
        public SoldProductList SoldProductList { get; set; }
    }

    public class Contact {
        public ContactProfile ContactProfile { get;  set; }
    }

    public class ContactProfile {
        public EmailList EmailList { get;  set; }
        public PhoneList PhoneList { get;  set; }
        public AddressList AddressList { get;  set; }
    }

    public class EmailList {
        public List<PersonEmail> PersonEmail { get;  set; }
    }

    public class PersonEmail {
        public string Address { get;  set; }
        public string Type { get;  set; }
    }

    public class PhoneList {
        public List<PersonPhone> PersonPhone { get;  set; }
    }

    public class PersonPhone {
        public string PhoneNumber { get;  set; }
        public string Type { get;  set; }
    }

    public class AddressList {
        public List<PersonAddress> PersonAddress { get;  set; }
    }

    public class PersonAddress {
        public string AddressLine1 { get;  set; }
        public string AddressLine2 { get;  set; }
        public string City { get;  set; }
        public string StateCode { get;  set; }
        public string County { get;  set; }
        public string ZipCode { get;  set; }
        public string Type { get;  set; }
        public string CountyCode { get;  set; }
        public string DeliveryCode { get; set; }
        public string ForeignPostalCode { get; set; }
        public string FIPSStateCode { get; set; }
        public string FIPSCountryCode { get; set; }
        public string EndDate { get; set; }
        public string StartDate { get; set; }
        public PersonIdentifier PersonIdentifier { get; set; }
    }

    public class PersonIdentifier {
        public PersonIdentifierKey PersonIdentifierKey { get; set; }
    }

    public class PersonIdentifierKey {
        public string Application { get; set; }
        public string KeyType { get; set; }
        public string KeyValue { get; set; }
        public string Source { get; set; }
    }

    public class MessageList {
        public List<Message> Message { get;  set; }
    }

    public class Message {
        public string Type { get;  set; }
        public string Code { get;  set; }
        public string Source { get;  set; }
        public string Description { get;  set; }
    }

    public class PlatformPointerList {
        public List<PlatformProxyKey> PlatformProxyKey { get; set; }
    }

    public class PlatformProxyKey {
        public string Application { get; set; }
        public string KeyType { get; set; }
        public string KeyValue { get; set; }
        public string Source { get; set; }
    }

    public class SoldProductList {
        public List<SoldProduct> SoldProduct { get; set; }
    }

    public class SoldProduct {
        public InsuranceProduct InsuranceProduct { get; set; }
        public NonInsuranceProduct NonInsuranceProduct { get; set; }
    }

    public class InsuranceProduct {
        public string MajorLineOfBusinessCode { get; set; }
        public string Source { get; set; }
        public string ProductLineCode { get; set; }
        public string SegmentType { get; set; }
        public string ProductTypeCode { get; set; }
        public SoldProductDetailList_IP SoldProductDetailList { get; set; }
    }

    public class NonInsuranceProduct {
        public string MajorLineOfBusinessCode { get; set; }
        public string Source { get; set; }
        public string ProductLineCode { get; set; }
        public string SegmentType { get; set; }
        public string ProductTypeCode { get; set; }
        public SoldProductDetailList_NIP SoldProductDetailList { get; set; }
    }

    public class SoldProductDetailList_IP {
        public List<SoldProductDetail_IP> SoldProductDetail { get; set; }
    }

    public class SoldProductDetailList_NIP {
        public List<SoldProductDetail_NIP> SoldProductDetail { get; set; }
    }

    public class SoldProductDetail_IP {
        public InsuranceCoverage InsuranceCoverage { get; set; }
    }

    public class SoldProductDetail_NIP {
        public NonInsuranceCoverage NonInsuranceCoverage { get; set; }
    }

    public class InsuranceCoverage {
        public CoveredRoleList CoveredRoleList { get; set; }
        public string PolicyType { get; set; }
        public string CoverageType { get; set; }
    }

    public class NonInsuranceCoverage {
        public CoveredRoleList_NIC CoveredRoleList { get; set; }
        public string PolicyType { get; set; }
        public string CoverageType { get; set; }
    }

    public class CoveredRoleList {
        public List<CoveredRole> CoveredRole { get; set; }
    }

    public class CoveredRole {
        public string MemberMasterId { get; set; }
        public string RelationshipToSubscriber { get; set; }
        public string Hipaa { get; set; }
        public string CoverageKey { get; set; }
        public CoveredRolePlatformPointerList CoveredRolePlatformPointerList { get; set; }
    }

    public class CoveredRolePlatformPointerList {
        public List<PlatformProxyKey_C> PlatformProxyKey { get; set; }
    }

    public class PlatformProxyKey_C {
        public string Application { get; set; }
        public string KeyType { get; set; }
        public string KeyValue { get; set; }
        public string Source { get; set; }
    }

    public class CoveredRoleList_NIC {
        public List<CoveredRole_NIC> CoveredRole { get; set; }
    }

    public class CoveredRole_NIC {
        public string MemberMasterId { get; set; }
        public string RelationshipToSubscriber { get; set; }
        public string Hipaa { get; set; }
        public string CoverageKey { get; set; }
        public CoveredRolePlatformPointerList_NIC CoveredRolePlatformPointerList { get; set; }
    }

    public class CoveredRolePlatformPointerList_NIC {
        public List<PlatformProxyKey_NIC> PlatformProxyKey { get; set; }
    }

    public class PlatformProxyKey_NIC {
        public string Application { get; set; }
        public string KeyType { get; set; }
        public string KeyValue { get; set; }
        public string Source { get; set; }
    }
}
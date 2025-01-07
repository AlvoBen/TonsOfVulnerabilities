/**
 * @File Name          : DemographicMBEPlusRequest_DTO_HUM.cls
 * @Description        : DTO class to generate HttpRequest for MBEPlus call
 * @Author             : Chetan Sindhe
 * @Group              : Team Thunder
 * @Last Modified On   : 7/2/2020, 2:14:13 PM
 * @Modification Log   : 
 * Ver       Date            Author      		    Modification
 * 1.0    5/11/2020        Chetan Sindhe           Initial Version
 * 1.1    5/29/2020        Chetan Sindhe           MBE+ Platform and Residential Changes
**/
public with sharing class DemographicMBEPlusRequest_DTO_HUM {
    
    public GetMember GetMember;

    public class GetMember {
        public Member Member;
    }

    public class Member {
        public Contact Contact;
        public PlatformPointerList PlatformPointerList;
        public SoldProductList SoldProductList;
        public GetCriteria GetCriteria;
    }

    public class Contact {
        public ContactProfile ContactProfile;
    }

    public class ContactProfile
    {
        public PhoneList PhoneList;
        public EmailList EmailList;
        public AddressList AddressList;
    }

    public class PhoneList
    {
        public String PersonPhone =''; 
    }
    
    public class EmailList
    {
        public String PersonEmail = ''; 
    }
    
    public class AddressList
    {
        public String PersonAddress = '';
        public string PlatformAddress = '';
        public string ResidentialAddress = '';
    }

    public class PlatformPointerList
    {
        public String PlatformProxyKey = '';
    }

    public class GetCriteria {
        public PersonIdCriteria PersonIdCriteria;
    }

    public class PersonIdCriteria {
        public string AsOfDate = '';
        public Period PeriodList;
        public string PersonId = '';
    }

    public class Period {
        public List<string> Period;
    }

    public class SoldProductList {
        public List<SoldProduct> SoldProduct;
    }

    public class SoldProduct {
        public InsuranceProduct InsuranceProduct;
        public NonInsuranceProduct NonInsuranceProduct;
    }

    public class InsuranceProduct {
        public SoldProductDetailList_IP SoldProductDetailList;
    }

    public class NonInsuranceProduct {
        public SoldProductDetailList_NIP SoldProductDetailList; 
    }

    public class SoldProductDetailList_IP {
        public SoldProductDetail_IP SoldProductDetail;
    }

    public class SoldProductDetailList_NIP {
        public SoldProductDetail_NIP SoldProductDetail;
    }

    public class SoldProductDetail_IP {
        public InsuranceCoverage InsuranceCoverage;
    }

    public class SoldProductDetail_NIP {
        public NonInsuranceCoverage NonInsuranceCoverage;
    }

    public class InsuranceCoverage {
        public CoveredRoleList CoveredRoleList;
        public MemberPlatformPointerList MemberPlatformPointerList;
        public GroupPlatformPointerList GroupPlatformPointerList;
    }

    public class NonInsuranceCoverage {
        public CoveredRoleList CoveredRoleList;
        public MemberPlatformPointerList MemberPlatformPointerList;
        public GroupPlatformPointerList GroupPlatformPointerList;
    }

    public class CoveredRoleList {
        public CoveredRole CoveredRole;
    }

    public class CoveredRole {
        public CoveredRolePlatformPointerList CoveredRolePlatformPointerList;
    }

    public class CoveredRolePlatformPointerList {
        public string PlatformProxyKey;
    }

    public class MemberPlatformPointerList {
        public string PlatformProxyKey;
    }

    public class GroupPlatformPointerList {
        public string PlatformProxyKey;
    }
}